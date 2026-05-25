/*
* Copyleft © 2024-2026 L2Brproject
* * This file is part of L2Brproject derived from aCis409/RusaCis3.8
* * L2Brproject is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the
* Free Software Foundation, either version 3 of the License.
* * L2Brproject is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
* * You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.WeightPenalty;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.handler.ItemHandler;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.status.PetStatus;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.actor.template.PetTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.itemcontainer.PetInventory;
import ext.mods.gameserver.model.records.PetDataEntry;
import ext.mods.gameserver.model.records.Timestamp;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.PetInventoryUpdate;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.DecayTaskManager;

/**
 * A pet is a instance extending {@link Summon}, linked to a {@link Player}. A pet is different than a Servitor in multiple ways:
 * <ul>
 * <li>It got its own inventory</li>
 * <li>It can earn xp and levels</li>
 * <li>Their lifetime isn't limited (but they got a food gauge)</li>
 * </ul>
 * It can be mountable, like Wyverns or Striders. A children class of Pet, {@link BabyPet} can also buff their owner. Finally a last type of pet is the Sin Eater, a creature used to remove PK kills.
 */
public class Pet extends Summon
{
	private static final String LOAD_PET = "SELECT name, level, curHp, curMp, exp, sp, fed FROM pets WHERE item_obj_id=?";
	private static final String STORE_PET = "INSERT INTO pets (name,level,curHp,curMp,exp,sp,fed,item_obj_id) VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE name=VALUES(name),level=VALUES(level),curHp=VALUES(curHp),curMp=VALUES(curMp),exp=VALUES(exp),sp=VALUES(sp),fed=VALUES(fed)";
	private static final String DELETE_PET = "DELETE FROM pets WHERE item_obj_id=?";
	
	private final Map<Integer, Timestamp> _reuseTimeStamps = new ConcurrentHashMap<>();
	
	private final PetInventory _inventory = new PetInventory(this);
	private final PetInventoryUpdate _iu = new PetInventoryUpdate(this);
	
	private final int _controlItemId;
	private final boolean _isMountable;
	
	private int _curFed;
	private WeightPenalty _weightPenalty = WeightPenalty.NONE;
	
	private long _expBeforeDeath = 0;
	
	private Future<?> _feedTask;
	
	private PetDataEntry _petData;
	
	public Pet(int objectId, NpcTemplate template, Player owner, ItemInstance control)
	{
		super(objectId, template, owner);
		
		_controlItemId = control.getObjectId();
		_isMountable = template.getNpcId() == 12526 || template.getNpcId() == 12527 || template.getNpcId() == 12528 || template.getNpcId() == 12621;
	}
	
	@Override
	public PetStatus getStatus()
	{
		return (PetStatus) _status;
	}
	
	@Override
	public void setStatus()
	{
		_status = new PetStatus(this);
	}
	
	@Override
	public PetTemplate getTemplate()
	{
		return (PetTemplate) super.getTemplate();
	}
	
	@Override
	public PetInventory getInventory()
	{
		return _inventory;
	}
	
	@Override
	public int getControlItemId()
	{
		return _controlItemId;
	}
	
	@Override
	public boolean isMountable()
	{
		return _isMountable;
	}
	
	@Override
	public int getSummonType()
	{
		return 2;
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (player.getObjectId() == getOwner().getObjectId() && player != getOwner())
			setOwner(player);
		
		super.onAction(player, isCtrlPressed, isShiftPressed);
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return _inventory.getItemFrom(Paperdoll.RHAND);
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		final ItemInstance weapon = getActiveWeaponInstance();
		return (weapon == null) ? null : (Weapon) weapon.getItem();
	}
	
	@Override
	public void addItem(ItemInstance item, boolean sendMessage)
	{
		if (item.getCount() < 1)
			return;
		
		if (sendMessage)
		{
			if (item.getItemId() == 57)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S1_ADENA).addItemNumber(item.getCount()));
			else if (item.getEnchantLevel() > 0)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
			else if (item.getCount() > 1)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S2_S1_S).addItemName(item.getItemId()).addItemNumber(item.getCount()));
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S1).addItemName(item.getItemId()));
		}
		
		_inventory.addItem(item);
	}
	
	@Override
	public ItemInstance addItem(int itemId, int count, boolean sendMessage)
	{
		if (count < 1)
			return null;
		
		if (sendMessage)
		{
			if (itemId == 57)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S1_ADENA).addItemNumber(count));
			else if (count > 1)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S2_S1_S).addItemName(itemId).addItemNumber(count));
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_PICKED_S1).addItemName(itemId));
		}
		
		return _inventory.addItem(itemId, count);
	}
	
	@Override
	public boolean destroyItem(int objectId, int count, boolean sendMessage)
	{
		final ItemInstance item = _inventory.destroyItem(objectId, count);
		if (item == null)
		{
			if (sendMessage)
				sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			
			return false;
		}
		
		if (sendMessage)
		{
			if (count > 1)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(item.getItemId()).addItemNumber(count));
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(item.getItemId()));
		}
		return true;
	}
	
	@Override
	public boolean destroyItemByItemId(int itemId, int count, boolean sendMessage)
	{
		final ItemInstance item = _inventory.destroyItemByItemId(itemId, count);
		if (item == null)
		{
			if (sendMessage)
				sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			
			return false;
		}
		
		if (sendMessage)
		{
			if (count > 1)
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(item.getItemId()).addItemNumber(count));
			else
				sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(item.getItemId()));
		}
		return true;
	}
	
	@Override
	public void deleteMe(Player owner)
	{
		getInventory().deleteMe();
		super.deleteMe(owner);
		destroyControlItem(owner);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		stopFeed();
		
		sendPacket(SystemMessageId.MAKE_SURE_YOU_RESSURECT_YOUR_PET_WITHIN_20_MINUTES);
		
		DecayTaskManager.getInstance().add(this, 1200);
		
		final Player owner = getOwner();
		if (owner != null && !owner.isInDuel() && (!isInsideZone(ZoneId.PVP) || isInsideZone(ZoneId.SIEGE)))
			deathPenalty();
		
		return true;
	}
	
	@Override
	public void doRevive()
	{
		getOwner().removeReviving();
		
		super.doRevive();
		
		DecayTaskManager.getInstance().cancel(this);
		
		startFeed();
		
		if (!checkHungryState())
			forceRunStance();
		
		getAI().tryToIdle();
	}
	
	@Override
	public void doRevive(double revivePower)
	{
		restoreExp(revivePower);
		doRevive();
	}
	
	@Override
	public final int getWeapon()
	{
		final ItemInstance item = getActiveWeaponInstance();
		return (item == null) ? 0 : item.getItemId();
	}
	
	@Override
	public final int getArmor()
	{
		final ItemInstance item = getInventory().getItemFrom(Paperdoll.CHEST);
		return (item == null) ? 0 : item.getItemId();
	}
	
	@Override
	public void store()
	{
		if (_controlItemId == 0)
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(STORE_PET))
		{
			ps.setString(1, getName());
			ps.setInt(2, getStatus().getLevel());
			ps.setDouble(3, getStatus().getHp());
			ps.setDouble(4, getStatus().getMp());
			ps.setLong(5, getStatus().getExp());
			ps.setInt(6, getStatus().getSp());
			ps.setInt(7, getCurrentFed());
			ps.setInt(8, _controlItemId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't store pet data for {}.", e, getObjectId());
		}
		
		final ItemInstance itemInst = getControlItem();
		if (itemInst != null && itemInst.getEnchantLevel() != getStatus().getLevel())
			itemInst.setEnchantLevel(getStatus().getLevel(), getOwner());
	}
	
	@Override
	public synchronized void unSummon(Player owner)
	{
		stopFeed();
		
		if (!isDead() && getInventory() != null)
			getInventory().deleteMe();
		
		super.unSummon(owner);
		
		if (!isDead())
			World.getInstance().removePet(owner.getObjectId());
	}
	
	@Override
	public void addExpAndSp(long addToExp, int addToSp)
	{
		getStatus().addExpAndSp(Math.round(addToExp * ((getNpcId() == 12564) ? Config.SINEATER_XP_RATE : Config.PET_XP_RATE)), addToSp);
	}
	
	@Override
	public int getSkillLevel(int skillId)
	{
		if (getSkill(skillId) == null)
			return 0;
		
		int level = getStatus().getLevel();
		if (level < 70)
			level = 1 + level / 10;
		else
			level = 8 + (level - 70) / 5;
		
		return Math.clamp(level, 1, SkillTable.getInstance().getMaxLevel(skillId));
	}
	
	/**
	 * Note: Base weight limit value is 34500 (half of player's value).
	 */
	@Override
	public final int getWeightLimit()
	{
		return (int) getStatus().calcStat(Stats.WEIGHT_LIMIT, 34500 * Formulas.CON_BONUS[getStatus().getCON()] * Config.WEIGHT_LIMIT, this, null);
	}
	
	public final WeightPenalty getWeightPenalty()
	{
		return _weightPenalty;
	}
	
	@Override
	public int getSoulShotsPerHit()
	{
		return getPetData().ssCount();
	}
	
	@Override
	public int getSpiritShotsPerHit()
	{
		return getPetData().spsCount();
	}
	
	@Override
	public void updateAndBroadcastStatus(int val)
	{
		refreshWeightPenalty();
		super.updateAndBroadcastStatus(val);
	}
	
	@Override
	public void addTimeStamp(L2Skill skill, long reuse)
	{
		_reuseTimeStamps.put(skill.getReuseHashCode(), new Timestamp(skill, reuse));
	}
	
	public Collection<Timestamp> getReuseTimeStamps()
	{
		return _reuseTimeStamps.values();
	}
	
	public Map<Integer, Timestamp> getReuseTimeStamp()
	{
		return _reuseTimeStamps;
	}
	
	public PetDataEntry getPetData()
	{
		return _petData;
	}
	
	public void setPetData(int level)
	{
		_petData = getTemplate().getPetDataEntry(level);
	}
	
	public ItemInstance getControlItem()
	{
		return getOwner().getInventory().getItemByObjectId(_controlItemId);
	}
	
	public int getCurrentFed()
	{
		return _curFed;
	}
	
	public void setCurrentFed(int num)
	{
		_curFed = Math.min(num, getPetData().maxMeal());
	}
	
	@Override
	public ItemInstance transferItem(int objectId, int count, Playable target)
	{
		final ItemInstance oldItem = checkItemManipulation(objectId, count);
		if (oldItem == null)
			return null;
		
		final boolean wasWorn = oldItem.isPetItem() && oldItem.isEquipped();
		
		final ItemInstance newItem = super.transferItem(objectId, count, target);
		if (newItem == null)
			return null;
		
		if (wasWorn)
			sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_TOOK_OFF_S1).addItemName(newItem));
		
		return newItem;
	}
	
	@Override
	public ItemInstance checkItemManipulation(int objectId, int count)
	{
		final ItemInstance item = getInventory().getItemByObjectId(objectId);
		if (item == null)
			return null;
		
		if (count < 1 || (count > 1 && !item.isStackable()))
			return null;
		
		if (count > item.getCount())
			return null;
		
		return item;
	}
	
	/**
	 * Remove the {@link Pet} reference from {@link World}, then the control item from the {@link Player} owner inventory. Finally, delete the pet from database.
	 * @param owner : The owner from whose inventory we should delete the item.
	 */
	public void destroyControlItem(Player owner)
	{
		World.getInstance().removePet(owner.getObjectId());
		
		owner.destroyItem(_controlItemId, 1, false);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_PET))
		{
			ps.setInt(1, _controlItemId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete pet data for {}.", e, getObjectId());
		}
	}
	
	public static Pet restore(ItemInstance control, NpcTemplate template, Player owner)
	{
		Pet pet;
		if (template.isType("BabyPet"))
			pet = new BabyPet(IdFactory.getInstance().getNextId(), template, owner, control);
		else
			pet = new Pet(IdFactory.getInstance().getNextId(), template, owner, control);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_PET))
		{
			ps.setInt(1, control.getObjectId());
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					pet.setName(rs.getString("name"));
					
					pet.getStatus().setLevel(rs.getByte("level"));
					pet.getStatus().setExp(rs.getLong("exp"));
					pet.getStatus().setSp(rs.getInt("sp"));
					
					pet.getStatus().setHpMp(rs.getDouble("curHp"), rs.getDouble("curMp"));
					
					if (rs.getDouble("curHp") < 0.5)
					{
						pet.setIsDead(true);
						pet.getStatus().stopHpMpRegeneration();
					}
					
					pet.setCurrentFed(rs.getInt("fed"));
				}
				else
				{
					pet.getStatus().setLevel((template.getNpcId() == 12564) ? (byte) pet.getOwner().getStatus().getLevel() : template.getLevel());
					pet.getStatus().setExp(pet.getStatus().getExpForThisLevel());
					pet.getStatus().setMaxHpMp();
					pet.setCurrentFed(pet.getPetData().maxMeal());
					pet.store();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore pet data for {}.", e, owner.getName());
			return null;
		}
		return pet;
	}
	
	public synchronized void stopFeed()
	{
		if (_feedTask != null)
		{
			_feedTask.cancel(false);
			_feedTask = null;
		}
	}
	
	public synchronized void startFeed()
	{
		stopFeed();
		
		if (!isDead() && getOwner().getSummon() == this)
			_feedTask = ThreadPool.scheduleAtFixedRate(new FeedTask(), 10000, 10000);
	}
	
	/**
	 * Restore the specified % of experience this {@link Pet} has lost.
	 * @param restorePercent : The percent of experience to restore.
	 */
	public void restoreExp(double restorePercent)
	{
		if (_expBeforeDeath > 0)
		{
			getStatus().addExp(Math.round((_expBeforeDeath - getStatus().getExp()) * restorePercent / 100));
			
			_expBeforeDeath = 0;
		}
	}
	
	private void deathPenalty()
	{
		int lvl = getStatus().getLevel();
		double percentLost = -0.07 * lvl + 6.5;
		
		long lostExp = Math.round((getStatus().getExpForLevel(lvl + 1) - getStatus().getExpForLevel(lvl)) * percentLost / 100);
		
		_expBeforeDeath = getStatus().getExp();
		
		getStatus().addExp(-lostExp);
	}
	
	public int getCurrentWeight()
	{
		return _inventory.getTotalWeight();
	}
	
	public int getInventoryLimit()
	{
		return Config.INVENTORY_MAXIMUM_PET;
	}
	
	public void refreshWeightPenalty()
	{
		final int weightLimit = getWeightLimit();
		if (weightLimit <= 0)
			return;
		
		final double ratio = (getCurrentWeight() - getStatus().calcStat(Stats.WEIGHT_PENALTY, 0, this, null)) / weightLimit;
		
		final WeightPenalty newWeightPenalty;
		if (ratio < 0.5)
			newWeightPenalty = WeightPenalty.NONE;
		else if (ratio < 0.666)
			newWeightPenalty = WeightPenalty.LEVEL_1;
		else if (ratio < 0.8)
			newWeightPenalty = WeightPenalty.LEVEL_2;
		else if (ratio < 1)
			newWeightPenalty = WeightPenalty.LEVEL_3;
		else
			newWeightPenalty = WeightPenalty.LEVEL_4;
		
		if (_weightPenalty != newWeightPenalty)
		{
			_weightPenalty = newWeightPenalty;
			
			getStatus().broadcastStatusUpdate();
		}
	}
	
	/**
	 * @return true if the auto feed limit is reached, false otherwise or if there is no need to feed.
	 */
	public boolean checkAutoFeedState()
	{
		return getCurrentFed() < (_petData.maxMeal() * getTemplate().getAutoFeedLimit());
	}
	
	/**
	 * @return true if the hungry limit is reached, false otherwise or if there is no need to feed.
	 */
	public boolean checkHungryState()
	{
		return getCurrentFed() < (_petData.maxMeal() * getTemplate().getHungryLimit());
	}
	
	/**
	 * @return true if the unsummon limit is reached, false otherwise or if there is no need to feed.
	 */
	public boolean checkUnsummonState()
	{
		return getCurrentFed() < (_petData.maxMeal() * getTemplate().getUnsummonLimit());
	}
	
	/**
	 * Manage {@link Pet} feeding task.
	 * <ul>
	 * <li>Feed or kill the pet depending on hunger level.</li>
	 * <li>If pet has food in inventory and feed level drops below 55% then consume food from inventory.</li>
	 * <li>Send a broadcastStatusUpdate packet for this pet.</li>
	 * </ul>
	 */
	protected class FeedTask implements Runnable
	{
		@Override
		public void run()
		{
			if (getOwner() == null || getOwner().getSummon() == null || getOwner().getSummon().getObjectId() != getObjectId())
			{
				stopFeed();
				return;
			}
			
			setCurrentFed((getCurrentFed() > getFeedConsume()) ? getCurrentFed() - getFeedConsume() : 0);
			
			ItemInstance food = getInventory().getItemByItemId(getTemplate().getFood1());
			if (food == null)
				food = getInventory().getItemByItemId(getTemplate().getFood2());
			
			if (food != null && checkAutoFeedState())
			{
				final IItemHandler handler = ItemHandler.getInstance().getHandler(food.getEtcItem());
				if (handler != null)
				{
					sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_TOOK_S1_BECAUSE_HE_WAS_HUNGRY).addItemName(food));
					handler.useItem(Pet.this, food, false);
				}
			}
			else if (getCurrentFed() == 0)
			{
				sendPacket(SystemMessageId.YOUR_PET_IS_VERY_HUNGRY);
				if (Rnd.get(100) < 30)
				{
					stopFeed();
					sendPacket(SystemMessageId.STARVING_GRUMPY_AND_FED_UP_YOUR_PET_HAS_LEFT);
					deleteMe(getOwner());
					return;
				}
			}
			else if (getCurrentFed() < (0.10 * getPetData().maxMeal()))
			{
				sendPacket(SystemMessageId.YOUR_PET_IS_VERY_HUNGRY_PLEASE_BE_CAREFUL);
				if (Rnd.get(100) < 3)
				{
					stopFeed();
					sendPacket(SystemMessageId.STARVING_GRUMPY_AND_FED_UP_YOUR_PET_HAS_LEFT);
					deleteMe(getOwner());
					return;
				}
			}
			
			if (checkHungryState())
				forceWalkStance();
			else
				forceRunStance();
			
			getStatus().broadcastStatusUpdate();
		}
		
		private int getFeedConsume()
		{
			return (isInCombat()) ? getPetData().mealInBattle() : getPetData().mealInNormal();
		}
	}
	
	@Override
	public void sendIU()
	{
		sendPacket(_iu);
	}
}