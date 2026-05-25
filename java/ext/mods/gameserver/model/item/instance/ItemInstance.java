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
package ext.mods.gameserver.model.item.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.items.EtcItemType;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.enums.items.ItemState;
import ext.mods.gameserver.enums.items.ItemType;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.Augmentation;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.MercenaryTicket;
import ext.mods.gameserver.model.item.kind.Armor;
import ext.mods.gameserver.model.item.kind.EtcItem;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.DropItem;
import ext.mods.gameserver.network.serverpackets.GetItem;
import ext.mods.gameserver.network.serverpackets.SpawnItem;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.basefuncs.Func;
import ext.mods.gameserver.taskmanager.ItemInstanceTaskManager;
import ext.mods.gameserver.taskmanager.ItemsOnGroundTaskManager;

/**
 * This class manages items.
 */
public final class ItemInstance extends WorldObject implements Runnable, Comparable<ItemInstance>
{
	private static final String RESTORE_AUGMENTATION = "SELECT attributes, skill_id, skill_level FROM augmentations WHERE item_oid = ?";
	
	private static final long REGULAR_LOOT_PROTECTION_TIME = 15000;
	private static final long RAID_LOOT_PROTECTION_TIME = 300000;
	
	private final Item _item;
	
	private int _ownerId;
	private int _dropperObjectId;
	private int _count;
	private int _enchantLevel;
	private int _manaLeft;
	private int _type1;
	private int _type2;
	private int _shotsMask;
	
	private ItemLocation _loc;
	private int _locationSlot;
	
	private long _time;
	
	private Augmentation _augmentation;
	
	private boolean _destroyProtected;
	
	private ScheduledFuture<?> _dropProtection;
	
	public ItemInstance(int objectId, int itemId)
	{
		this(objectId, ItemData.getInstance().getTemplate(itemId));
	}
	
	public ItemInstance(int objectId, Item item)
	{
		super(objectId);
		
		_item = item;
		_loc = ItemLocation.VOID;
		_manaLeft = (isShadowItem()) ? _item.getDuration() * 60 : -1;
		
		setName(item.getName());
		setCount(1);
	}
	
	public ItemInstance(int objectId, int itemId, int count, int enchantLevel)
	{
		super(objectId);
		
		_item = ItemData.getInstance().getTemplate(itemId);
		_count = count;
		_enchantLevel = enchantLevel;
		_loc = ItemLocation.VOID;
		_manaLeft = (isShadowItem()) ? _item.getDuration() * 60 : -1;
		
		setName(_item.getName());
	}
	
	public ItemInstance(ResultSet rs) throws SQLException
	{
		super(rs.getInt("object_id"));
		
		_item = ItemData.getInstance().getTemplate(rs.getInt("item_id"));
		_count = rs.getInt("count");
		_enchantLevel = rs.getInt("enchant_level");
		_ownerId = rs.getInt("owner_id");
		_type1 = rs.getInt("custom_type1");
		_type2 = rs.getInt("custom_type2");
		_loc = ItemLocation.valueOf(rs.getString("loc"));
		_locationSlot = rs.getInt("loc_data");
		_manaLeft = rs.getInt("mana_left");
		_time = rs.getLong("time");
		
		setName(_item.getName());
	}
	
	@Override
	public int compareTo(ItemInstance item)
	{
		final int time = Long.compare(item.getTime(), _time);
		if (time != 0)
			return time;
		
		return Integer.compare(item.getObjectId(), getObjectId());
	}
	
	@Override
	public void decayMe()
	{
		ItemsOnGroundTaskManager.getInstance().remove(this);
		
		super.decayMe();
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (player.isFlying())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (_item.getItemType() == EtcItemType.CASTLE_GUARD)
		{
			if (player.isInParty())
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Castle castle = CastleManager.getInstance().getCastle(player);
			if (castle == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final MercenaryTicket ticket = castle.getTicket(_item.getItemId());
			if (ticket == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (!player.isCastleLord(castle.getId()))
			{
				player.sendPacket(SystemMessageId.THIS_IS_NOT_A_MERCENARY_OF_A_CASTLE_THAT_YOU_OWN_AND_SO_CANNOT_CANCEL_POSITIONING);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
		}
		
		if (player.isInParty())
		{
			boolean hasFreeSlot = false;
			
			for (Player partyMember : player.getParty().getMembers())
			{
				if (partyMember.getInventory().validateCapacity(1))
				{
					player.getAI().tryToPickUp(getObjectId(), isShiftPressed);
					hasFreeSlot = true;
					break;
				}
			}
			
			if (!hasFreeSlot)
			{
				player.sendPacket(SystemMessageId.SLOTS_FULL);
				return;
			}
		}
		else
			player.getAI().tryToPickUp(getObjectId(), isShiftPressed);
	}
	
	@Override
	public synchronized void run()
	{
		_ownerId = 0;
		_dropProtection = null;
	}
	
	@Override
	public void sendInfo(Player player)
	{
		if (_dropperObjectId != 0)
			player.sendPacket(new DropItem(this, _dropperObjectId));
		else
			player.sendPacket(new SpawnItem(this));
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (_shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (charged)
			_shotsMask |= type.getMask();
		else
			_shotsMask &= ~type.getMask();
	}
	
	/**
	 * Sets the ownerID of the item
	 * @param ownerId : int designating the ID of the owner
	 */
	public void setOwnerId(int ownerId)
	{
		if (ownerId == _ownerId)
			return;
		
		_ownerId = ownerId;
		
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	/**
	 * Returns the ownerID of the item
	 * @return int : ownerID of the item
	 */
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	/**
	 * Sets the location of the item
	 * @param loc : ItemLocation (enumeration)
	 */
	public void setLocation(ItemLocation loc)
	{
		setLocation(loc, 0);
	}
	
	/**
	 * Sets the location of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param loc : ItemLocation (enumeration)
	 * @param locData : int designating the slot where the item is stored or the village for freights
	 */
	public void setLocation(ItemLocation loc, int locData)
	{
		if (loc == _loc && locData == _locationSlot)
			return;
		
		_loc = loc;
		_locationSlot = locData;
		
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	public ItemLocation getLocation()
	{
		return _loc;
	}
	
	/**
	 * Sets the quantity of the item.<BR>
	 * <BR>
	 * @param count the new count to set
	 */
	public void setCount(int count)
	{
		if (_count == count)
			return;
		
		_count = Math.max(0, count);
		
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	/**
	 * Returns the quantity of item
	 * @return int
	 */
	public int getCount()
	{
		return _count;
	}
	
	/**
	 * Sets the quantity of the item.<BR>
	 * <BR>
	 * <U><I>Remark :</I></U> If loc and loc_data different from database, say datas not up-to-date
	 * @param count : int
	 * @param creator : Player Player requesting the item creation
	 */
	public void changeCount(int count, Playable creator)
	{
		if (count == 0)
			return;
		
		if (count > 0 && getCount() > Integer.MAX_VALUE - count)
			setCount(Integer.MAX_VALUE);
		else
			setCount(getCount() + count);
		
		updateState(creator, ItemState.MODIFIED);
	}
	
	/**
	 * Returns if item is equipable
	 * @return boolean
	 */
	public boolean isEquipable()
	{
		return !(_item.getBodyPart() == 0 || _item.getItemType() == EtcItemType.ARROW || _item.getItemType() == EtcItemType.LURE);
	}
	
	/**
	 * Returns if item is equipped
	 * @return boolean
	 */
	public boolean isEquipped()
	{
		return _loc == ItemLocation.PAPERDOLL || _loc == ItemLocation.PET_EQUIP;
	}
	
	/**
	 * @return The slot where the item is stored (paperdoll slot or freight town id).
	 */
	public int getLocationSlot()
	{
		return _locationSlot;
	}
	
	/**
	 * @return The {@link Item} associated to that {@link ItemInstance}.
	 */
	public Item getItem()
	{
		return _item;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public void setCustomType1(int type)
	{
		_type1 = type;
		
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public void setCustomType2(int type)
	{
		_type2 = type;
		
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	public boolean isOlyRestrictedItem()
	{
		return getItem().isOlyRestrictedItem();
	}
	
	/**
	 * Returns the type of item
	 * @return Enum
	 */
	public ItemType getItemType()
	{
		return _item.getItemType();
	}
	
	/**
	 * Returns the ID of the item
	 * @return int
	 */
	public int getItemId()
	{
		return _item.getItemId();
	}
	
	/**
	 * Returns true if item is an EtcItem
	 * @return boolean
	 */
	public boolean isEtcItem()
	{
		return _item instanceof EtcItem;
	}
	
	/**
	 * @return True if this {@link ItemInstance} is an {@link Weapon}, or false otherwise.
	 */
	public boolean isWeapon()
	{
		return _item instanceof Weapon;
	}
	
	/**
	 * @return True if this {@link ItemInstance} is an {@link Armor}, or false otherwise.
	 */
	public boolean isArmor()
	{
		return _item instanceof Armor;
	}
	
	/**
	 * @return This {@link ItemInstance} casted as a {@link EtcItem}, or null if it isn't the good instance type.
	 */
	public EtcItem getEtcItem()
	{
		return (_item instanceof EtcItem etcItem) ? etcItem : null;
	}
	
	/**
	 * @return This {@link ItemInstance} casted as a {@link Weapon}, or null if it isn't the good instance type.
	 */
	public Weapon getWeaponItem()
	{
		return (_item instanceof Weapon weapon) ? weapon : null;
	}
	
	/**
	 * @return This {@link ItemInstance} casted as an {@link Armor}, or null if it isn't the good instance type.
	 */
	public Armor getArmorItem()
	{
		return (_item instanceof Armor armor) ? armor : null;
	}
	
	/**
	 * Returns the quantity of crystals for crystallization
	 * @return int
	 */
	public final int getCrystalCount()
	{
		return _item.getCrystalCount(_enchantLevel);
	}
	
	/**
	 * @return the reference price of the item.
	 */
	public int getReferencePrice()
	{
		return _item.getReferencePrice();
	}
	
	/**
	 * @return the name of the item.
	 */
	public String getItemName()
	{
		return _item.getName();
	}
	
	/**
	 * Add an {@link ItemState} update for the Inventory of the {@link Creature} set as parameter.
	 * @param creature : The {@link Creature} owning the item.
	 * @param state : The {@link ItemState} to send as update.
	 */
	public void updateState(Creature creature, ItemState state)
	{
		if (creature == null || (getLocation() != ItemLocation.PET_EQUIP && getLocation() != ItemLocation.PET && getLocation() != ItemLocation.INVENTORY && getLocation() != ItemLocation.PAPERDOLL))
			return;
		
		creature.getInventory().addUpdate(this, state);
	}
	
	/**
	 * @return True if this {@link ItemInstance} is stackable, or false otherwise.
	 */
	public boolean isStackable()
	{
		return _item.isStackable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is dropable, or false otherwise.
	 */
	public boolean isDropable()
	{
		return !isAugmented() && _item.isDropable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is dropable, or false otherwise.
	 */
	public boolean isDestroyable()
	{
		return _item.isDestroyable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is tradable, or false otherwise.
	 */
	public boolean isTradable()
	{
		return !isAugmented() && _item.isTradable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is sellable, or false otherwise.
	 */
	public boolean isSellable()
	{
		if (_item.getType2() == Item.TYPE2_QUEST)
			return false;
		
		return !isAugmented() && _item.isSellable();
	}
	
	/**
	 * @param isPrivateWarehouse : make additionals checks on tradable / shadow items.
	 * @return True if this {@link ItemInstance} can be deposited in warehouse or freight, or false otherwise.
	 */
	public boolean isDepositable(boolean isPrivateWarehouse)
	{
		if (isEquipped() || !_item.isDepositable())
			return false;
		
		if (!isPrivateWarehouse)
		{
			if (!isTradable() || isShadowItem())
				return false;
		}
		return true;
	}
	
	/**
	 * @return if item is enchantable
	 */
	public boolean isEnchantable()
	{
		return _item.isEnchantable();
	}
	
	/**
	 * @return True if this {@link ItemInstance} is a consumable, or false otherwise.
	 */
	public boolean isConsumable()
	{
		return _item.isConsumable();
	}
	
	/**
	 * @param player : The {@link Player} to check.
	 * @param allowAdena : If true, count Adena.
	 * @param allowNonTradable : If true, count non tradable items.
	 * @param allowStoreBuy : If true, count store buy items.
	 * @return True if this {@link ItemInstance} is available for manipulation, or false otherwise.
	 */
	public boolean isAvailable(Player player, boolean allowAdena, boolean allowNonTradable, boolean allowStoreBuy)
	{
		return ((!isEquipped() || allowStoreBuy)
			&& (getItem().getType2() != Item.TYPE2_QUEST)
			&& (getItem().getType2() != Item.TYPE2_MONEY || getItem().getType1() != Item.TYPE1_SHIELD_ARMOR)
			&& (player.getSummon() == null || getObjectId() != player.getSummon().getControlItemId())
			&& (player.getActiveEnchantItem() != this)
			&& (allowAdena || getItemId() != 57)
			&& (player.getAI().getCurrentIntention().getSkill() == null || player.getAI().getCurrentIntention().getSkill().getItemConsumeId() != getItemId()) && (allowNonTradable || isTradable()));
	}
	
	/**
	 * @return The level of enchantment of this {@link ItemInstance}.
	 */
	public int getEnchantLevel()
	{
		return _enchantLevel;
	}
	
	/**
	 * Set the level of enchantment of this {@link ItemInstance}.
	 * @param enchantLevel : number to apply.
	 * @param playable : The {@link Playable} to send inventory update.
	 */
	public void setEnchantLevel(int enchantLevel, Playable playable)
	{
		if (_enchantLevel == enchantLevel)
			return;
		
		_enchantLevel = enchantLevel;
		
		ItemInstanceTaskManager.getInstance().add(this);
		
		updateState(playable, ItemState.MODIFIED);
	}
	
	/**
	 * @return True if this {@link ItemInstance} is augmented, or false otherwise.
	 */
	public boolean isAugmented()
	{
		return _augmentation != null;
	}
	
	/**
	 * @return the augmentation object for this item.
	 */
	public Augmentation getAugmentation()
	{
		return _augmentation;
	}
	
	/**
	 * Set a new {@link Augmentation} to this {@link ItemInstance}.
	 * @param augmentation : The {@link Augmentation} to apply.
	 * @param player : The {@link Player} to refresh inventory from.
	 * @return True if the operation is successful, or false otherwise.
	 */
	public boolean setAugmentation(Augmentation augmentation, Player player)
	{
		if (_augmentation != null)
			return false;
		
		_augmentation = augmentation;
		
		ItemInstanceTaskManager.getInstance().add(this);
		
		updateState(player, ItemState.MODIFIED);
		
		return true;
	}
	
	/**
	 * Remove the augmentation associated to this {@link ItemInstance}.
	 * @param player : The {@link Player} to refresh inventory from.
	 */
	public void removeAugmentation(Player player)
	{
		if (_augmentation == null)
			return;
		
		_augmentation = null;
		
		ItemInstanceTaskManager.getInstance().add(this);
		
		updateState(player, ItemState.MODIFIED);
	}
	
	private void restoreAttributes()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_AUGMENTATION))
		{
			ps.setInt(1, getObjectId());
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
					_augmentation = new Augmentation(rs.getInt("attributes"), rs.getInt("skill_id"), rs.getInt("skill_level"));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore augmentation for {}.", e, toString());
		}
	}
	
	/**
	 * @return True if this {@link ItemInstance} is a shadow item. Shadow items have a limited life-time.
	 */
	public boolean isShadowItem()
	{
		return _item.getDuration() > -1;
	}
	
	/**
	 * Decrease the mana for this {@link ItemInstance}.
	 * @param amount : The amount to decrease out of manaLeft variable.
	 */
	public void decreaseMana(int amount)
	{
		_manaLeft -= Math.min(_manaLeft, amount);
		
		ItemInstanceTaskManager.getInstance().add(this);
	}
	
	/**
	 * @return The remaining mana of this {@link ItemInstance} in seconds.
	 */
	public int getManaLeft()
	{
		return isShadowItem() ? _manaLeft : -1;
	}
	
	/**
	 * @return The remaining mana of this {@link ItemInstance} for display purpose (as a minute).
	 */
	public int getDisplayedManaLeft()
	{
		return isShadowItem() ? _manaLeft / 60 : -1;
	}
	
	/**
	 * @param creature : The {@link Creature} used as parameter.
	 * @return An array of {@link Func}s based on this {@link ItemInstance}'s {@link Item} template and the {@link Creature} set as parameter.
	 */
	public List<Func> getStatFuncs(Creature creature)
	{
		return getItem().getStatFuncs(this, creature);
	}
	
	/**
	 * @param rs : The {@link ResultSet} of the item.
	 * @return A new {@link ItemInstance} from database using a {@link ResultSet} content.
	 */
	public static ItemInstance restoreFromDb(ResultSet rs)
	{
		try
		{
			final ItemInstance item = new ItemInstance(rs);
			
			if (item.isEquipable())
				item.restoreAttributes();
			
			return item;
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore an owned item.", e);
			return null;
		}
	}
	
	/**
	 * Validate intended dropping location, set it and spawn this {@link ItemInstance} to the world.
	 * @param dropper : The {@link Creature} dropper.
	 * @param x : The X coordinate of intended location.
	 * @param y : The Y coordinate of intended location.
	 * @param z : The Z coordinate of intended location.
	 */
	public final void dropMe(Creature dropper, int x, int y, int z)
	{
		ThreadPool.execute(() ->
		{
			setDropperObjectId(dropper.getObjectId());
			
			World.getInstance().removeObject(this);
			
			spawnMe(GeoEngine.getInstance().getValidLocation(dropper, x, y, z));
			ItemsOnGroundTaskManager.getInstance().add(this, dropper);
			
			setDropperObjectId(0);
		});
	}
	
	/**
	 * Calculate dropping location from {@link Creature} location and offset, validate it, set it and spawn this {@link ItemInstance} to the world.
	 * @param dropper : The {@link Creature} dropper.
	 */
	public final void dropMe(Creature dropper)
	{
		final Location loc = dropper.getPosition().clone();
		loc.addRandomOffsetBetween(30, 45);
		
		ThreadPool.execute(() ->
		{
			setDropperObjectId(dropper.getObjectId());
			
			World.getInstance().removeObject(this);
			
			spawnMe(GeoEngine.getInstance().getValidLocation(dropper, loc));
			ItemsOnGroundTaskManager.getInstance().add(this, dropper);
			
			setDropperObjectId(0);
		});
	}
	
	/**
	 * Remove this {@link ItemInstance} from the visible world and broadcast GetItem packet.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T REMOVE the object from _objects of World.</B></FONT><BR>
	 * <BR>
	 * @param creature : The {@link Creature} that pick up the item.
	 */
	public final void pickupMe(Creature creature)
	{
		creature.broadcastPacket(new GetItem(this, creature.getObjectId()));
		
		final Castle castle = CastleManager.getInstance().getCastle(this);
		if (castle != null && castle.getTicket(getItemId()) != null)
			castle.removeDroppedTicket(this);
		
		if (getItemId() == 57 || getItemId() == 6353)
		{
			final Player actor = creature.getActingPlayer();
			if (actor != null)
			{
				final QuestState qs = actor.getQuestList().getQuestState("Tutorial");
				if (qs != null)
					qs.getQuest().notifyEvent("CE" + getItemId() + "", null, actor);
			}
		}
		
		setIsVisible(false);
	}
	
	public synchronized boolean hasDropProtection()
	{
		return _dropProtection != null;
	}
	
	public synchronized void setDropProtection(int ownerId, boolean isRaidParty)
	{
		_ownerId = ownerId;
		_dropProtection = ThreadPool.schedule(this, (isRaidParty) ? RAID_LOOT_PROTECTION_TIME : REGULAR_LOOT_PROTECTION_TIME);
	}
	
	public synchronized void removeDropProtection()
	{
		if (_dropProtection != null)
		{
			_dropProtection.cancel(true);
			_dropProtection = null;
		}
		
		_ownerId = 0;
	}
	
	public void setDestroyProtected(boolean destroyProtected)
	{
		_destroyProtected = destroyProtected;
	}
	
	public boolean isDestroyProtected()
	{
		return _destroyProtected;
	}
	
	public long getTime()
	{
		return _time;
	}
	
	public void actualizeTime()
	{
		_time = System.currentTimeMillis();
	}
	
	public boolean isPetItem()
	{
		return getItem().isPetItem();
	}
	
	public boolean isPotion()
	{
		return getItem().isPotion();
	}
	
	public boolean isElixir()
	{
		return getItem().isElixir();
	}
	
	public boolean isHerb()
	{
		return getItem().getItemType() == EtcItemType.HERB;
	}
	
	public boolean isSummonItem()
	{
		return getItem().getItemType() == EtcItemType.PET_COLLAR;
	}
	
	public boolean isHeroItem()
	{
		return getItem().isHeroItem();
	}
	
	public boolean isQuestItem()
	{
		return getItem().isQuestItem();
	}
	
	/**
	 * Create an {@link ItemInstance} corresponding to the itemId and count, add it to the server and logs the activity.
	 * @param itemId : The itemId of the item to be created.
	 * @param count : The quantity of items to be created for stackable items.
	 * @return a new ItemInstance corresponding to the itemId and count.
	 */
	public static ItemInstance create(int itemId, int count)
	{
		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		
		World.getInstance().addObject(item);
		
		if (item.isStackable() && count > 1)
			item.setCount(count);
		
		return item;
	}
	
	/**
	 * Destroy this {@link ItemInstance} from server, and release its objectId.
	 */
	public void destroyMe()
	{
		setCount(0);
		setOwnerId(0);
		setLocation(ItemLocation.VOID);
		
		World.getInstance().removeObject(this);
		IdFactory.getInstance().releaseId(getObjectId());
	}
	
	public void setDropperObjectId(int id)
	{
		_dropperObjectId = id;
	}
	
	public List<Quest> getQuestEvents()
	{
		return _item.getQuestEvents();
	}
	
	public void unChargeAllShots()
	{
		_shotsMask = 0;
	}
	
	/**
	 * @return The weight of the {@link Item}'s weight multiplied by count.
	 */
	public int getWeight()
	{
		return _item.getWeight() * _count;
	}

}