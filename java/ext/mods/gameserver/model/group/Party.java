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
package ext.mods.gameserver.model.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.DuelManager;
import ext.mods.gameserver.data.manager.FestivalOfDarknessManager;
import ext.mods.gameserver.data.manager.PartyMatchRoomManager;
import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.enums.LootRule;
import ext.mods.gameserver.enums.MessageType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.container.npc.RewardInfo;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.instance.Servitor;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.network.serverpackets.ExCloseMPCC;
import ext.mods.gameserver.network.serverpackets.ExOpenMPCC;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.PartyMemberPosition;
import ext.mods.gameserver.network.serverpackets.PartySmallWindowAdd;
import ext.mods.gameserver.network.serverpackets.PartySmallWindowAll;
import ext.mods.gameserver.network.serverpackets.PartySmallWindowDelete;
import ext.mods.gameserver.network.serverpackets.PartySmallWindowDeleteAll;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class Party extends AbstractGroup
{
	private static final double[] BONUS_EXP_SP =
	{
		1,
		1,
		1.30,
		1.39,
		1.50,
		1.54,
		1.58,
		1.63,
		1.67,
		1.71
	};
	
	private static final int PARTY_POSITION_BROADCAST = 12000;
	
	private final List<Player> _members = new CopyOnWriteArrayList<>();
	private final LootRule _lootRule;
	
	private boolean _pendingInvitation;
	private long _pendingInviteTimeout;
	private int _itemLastLoot;
	
	private CommandChannel _commandChannel;
	
	private Future<?> _positionBroadcastTask;
	protected PartyMemberPosition _positionPacket;
	
	public Party(Player leader, Player target, LootRule lootRule)
	{
		super(leader);
		
		_members.add(leader);
		_members.add(target);
		
		leader.setParty(this);
		target.setParty(this);
		
		_lootRule = lootRule;
		
		recalculateLevel();
		
		target.sendPacket(new PartySmallWindowAll(target, this));
		leader.sendPacket(new PartySmallWindowAdd(target, this));
		
		target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_JOINED_S1_PARTY).addCharName(leader));
		leader.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_JOINED_PARTY).addCharName(target));
		
		for (Player member : _members)
		{
			member.updateEffectIcons(true);
			member.broadcastUserInfo();
		}
		
		_positionPacket = new PartyMemberPosition(this);
		_positionBroadcastTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			_positionPacket.reuse(this);
			
			broadcastPacket(_positionPacket);
		}, PARTY_POSITION_BROADCAST / 2, PARTY_POSITION_BROADCAST);
	}
	
	@Override
	public final List<Player> getMembers()
	{
		return _members;
	}
	
	@Override
	public int getMembersCount()
	{
		return _members.size();
	}
	
	@Override
	public boolean containsPlayer(WorldObject player)
	{
		return _members.contains(player);
	}
	
	@Override
	public void broadcastPacket(final L2GameServerPacket packet)
	{
		for (Player member : _members)
			member.sendPacket(packet);
	}
	
	@Override
	public void broadcastCreatureSay(final CreatureSay msg, final Player broadcaster)
	{
		for (Player member : _members)
		{
			if (!RelationManager.getInstance().isInBlockList(member, broadcaster))
				member.sendPacket(msg);
		}
	}
	
	@Override
	public void broadcastOnScreen(int time, NpcStringId npcStringId)
	{
		broadcastPacket(new ExShowScreenMessage(npcStringId.getMessage(), time));
	}
	
	@Override
	public void broadcastOnScreen(int time, NpcStringId npcStringId, Object... params)
	{
		broadcastPacket(new ExShowScreenMessage(npcStringId.getMessage(params), time));
	}
	
	@Override
	public void recalculateLevel()
	{
		int newLevel = 0;
		for (Player member : _members)
		{
			if (member.getStatus().getLevel() > newLevel)
				newLevel = member.getStatus().getLevel();
		}
		setLevel(newLevel);
	}
	
	@Override
	public void disband()
	{
		DuelManager.getInstance().onPartyEdit(getLeader());
		
		if (_commandChannel != null)
		{
			broadcastPacket(ExCloseMPCC.STATIC_PACKET);
			
			if (_commandChannel.isLeader(getLeader()))
				_commandChannel.disband();
			else
				_commandChannel.removeParty(this);
		}
		
		for (Player member : _members)
		{
			member.setParty(null);
			member.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
			
			if (member.isFestivalParticipant())
				FestivalOfDarknessManager.getInstance().updateParticipants(member, this);
			
			if (member.getFusionSkill() != null)
				member.getCast().stop();
			
			member.forEachKnownType(Creature.class, creature -> creature.getFusionSkill() != null && creature.getFusionSkill().getTarget() == member, creature -> creature.getCast().stop());
			
			member.sendPacket(SystemMessageId.PARTY_DISPERSED);
		}
		_members.clear();
		
		if (_positionBroadcastTask != null)
		{
			_positionBroadcastTask.cancel(false);
			_positionBroadcastTask = null;
		}
	}
	
	/**
	 * @return True if this {@link Party} waits for invitation respond, false otherwise.
	 */
	public boolean getPendingInvitation()
	{
		return _pendingInvitation;
	}
	
	/**
	 * Set invitation process flag and store time for expiration happens when a {@link Player} joins or declines to join.
	 * @param val : Set the invitation process flag to that value.
	 */
	public void setPendingInvitation(boolean val)
	{
		_pendingInvitation = val;
		_pendingInviteTimeout = System.currentTimeMillis() + Player.REQUEST_TIMEOUT * 1000;
	}
	
	/**
	 * @return True if the invitation request time expired, false otherwise.
	 */
	public boolean isInvitationRequestExpired()
	{
		return _pendingInviteTimeout <= System.currentTimeMillis();
	}
	
	/**
	 * @param itemId : The ID of the item for which the member must have inventory space.
	 * @param target : The {@link Creature} of which the member must be within a certain range (must not be null).
	 * @return A random valid {@link Player} looter from this {@link Party}, or null if none of the members match conditions.
	 */
	private Player getRandomValidLooter(int itemId, Creature target)
	{
		final List<Player> validMembers = new ArrayList<>();
		for (Player member : _members)
		{
			if (!member.isDead() && member.getInventory().validateCapacityByItemId(itemId, 1) && member.isInStrictRadius(target, Config.PARTY_RANGE))
				validMembers.add(member);
		}
		return (validMembers.isEmpty()) ? null : Rnd.get(validMembers);
	}
	
	/**
	 * @param itemId : The ID of the item for which the member must have inventory space.
	 * @param target : The {@link Creature} of which the member must be within a certain range (must not be null).
	 * @return The next valid {@link Player} looter from this {@link Party}, or null if none of the members match conditions.
	 */
	private Player getNextValidLooter(int itemId, Creature target)
	{
		for (int i = 0; i < getMembersCount(); i++)
		{
			if (++_itemLastLoot >= getMembersCount())
				_itemLastLoot = 0;
			
			final Player member = _members.get(_itemLastLoot);
			if (!member.isDead() && member.getInventory().validateCapacityByItemId(itemId, 1) && member.isInStrictRadius(target, Config.PARTY_RANGE))
				return member;
		}
		return null;
	}
	
	/**
	 * @param player : The {@link Player} used as reference looter.
	 * @param itemId : The ID of the item for which the member must have inventory space.
	 * @param isSpoil : True if the item comes from a spoil process, false otherwise.
	 * @param target : The {@link Creature} of which the member must be within a certain range (must not be null).
	 * @return A valid {@link Player} looter based on this {@link Party}'s {@link LootRule}.
	 */
	private Player getValidLooter(Player player, int itemId, boolean isSpoil, Creature target)
	{
		Player looter = player;
		
		switch (_lootRule)
		{
			case ITEM_RANDOM:
				if (!isSpoil)
					looter = getRandomValidLooter(itemId, target);
				break;
			
			case ITEM_RANDOM_SPOIL:
				looter = getRandomValidLooter(itemId, target);
				break;
			
			case ITEM_ORDER:
				if (!isSpoil)
					looter = getNextValidLooter(itemId, target);
				break;
			
			case ITEM_ORDER_SPOIL:
				looter = getNextValidLooter(itemId, target);
				break;
		}
		
		return (looter == null) ? player : looter;
	}
	
	public void broadcastNewLeaderStatus()
	{
		final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BECOME_A_PARTY_LEADER).addCharName(getLeader());
		for (Player member : _members)
		{
			member.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
			member.sendPacket(new PartySmallWindowAll(member, this));
			member.broadcastUserInfo();
			member.sendPacket(sm);
		}
	}
	
	/**
	 * Send a {@link L2GameServerPacket} to all {@link Player}s of this {@link Party}, except the {@link Player} set as parameter.
	 * @param player : This {@link Player} won't receive the {@link L2GameServerPacket}.
	 * @param gsp : The {@link L2GameServerPacket} to send.
	 */
	public void broadcastToPartyMembers(Player player, L2GameServerPacket gsp)
	{
		_members.stream().filter(m -> m != player).forEach(m -> m.sendPacket(gsp));
	}
	
	/**
	 * Add a {@link Player} to this {@link Party}.
	 * @param player : The {@link Player} to add to this {@link Party}.
	 */
	public void addPartyMember(Player player)
	{
		if (player == null || _members.contains(player))
			return;
		
		player.sendPacket(new PartySmallWindowAll(player, this));
		broadcastPacket(new PartySmallWindowAdd(player, this));
		
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_JOINED_S1_PARTY).addCharName(getLeader()));
		broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_JOINED_PARTY).addCharName(player));
		
		DuelManager.getInstance().onPartyEdit(getLeader());
		
		_members.add(player);
		
		player.setParty(this);
		
		if (player.getStatus().getLevel() > getLevel())
			setLevel(player.getStatus().getLevel());
		
		for (Player member : _members)
		{
			member.updateEffectIcons(true);
			member.broadcastUserInfo();
		}
		
		if (_commandChannel != null)
			player.sendPacket(ExOpenMPCC.STATIC_PACKET);
	}
	
	/**
	 * Remove a {@link Party} member using his name.
	 * @param name : The {@link Player} name to remove from the {@link Party}.
	 * @param type : The {@link MessageType} sent as removal information.
	 */
	public void removePartyMember(String name, MessageType type)
	{
		removePartyMember(getPlayerByName(name), type);
	}
	
	/**
	 * Remove a {@link Party} member instance.
	 * @param player : The {@link Player} to remove from the {@link Party}.
	 * @param type : The {@link MessageType} sent as removal information.
	 */
	public void removePartyMember(Player player, MessageType type)
	{
		if (player == null || !_members.contains(player))
			return;
		
		final boolean isLeader = isLeader(player);
		
		if (_members.size() == 2 || (type != MessageType.DISCONNECTED && isLeader))
			disband();
		else
		{
			if (isLeader)
			{
				for (Player member : _members)
				{
					if (member != player)
					{
						changePartyLeader(member);
						break;
					}
				}
			}
			
			DuelManager.getInstance().onPartyEdit(getLeader());
			
			_members.remove(player);
			recalculateLevel();
			
			if (player.isFestivalParticipant())
				FestivalOfDarknessManager.getInstance().updateParticipants(player, this);
			
			if (player.getFusionSkill() != null)
				player.getCast().stop();
			
			player.forEachKnownType(Creature.class, creature -> creature.getFusionSkill() != null && creature.getFusionSkill().getTarget() == player, creature -> creature.getCast().stop());
			
			if (type == MessageType.EXPELLED)
			{
				player.sendPacket(SystemMessageId.HAVE_BEEN_EXPELLED_FROM_PARTY);
				broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_WAS_EXPELLED_FROM_PARTY).addCharName(player));
			}
			else if (type == MessageType.LEFT || type == MessageType.DISCONNECTED)
			{
				player.sendPacket(SystemMessageId.YOU_LEFT_PARTY);
				broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_LEFT_PARTY).addCharName(player));
			}
			
			player.setParty(null);
			player.sendPacket(PartySmallWindowDeleteAll.STATIC_PACKET);
			
			broadcastPacket(new PartySmallWindowDelete(player));
			
			if (_commandChannel != null)
				player.sendPacket(ExCloseMPCC.STATIC_PACKET);
		}
	}
	
	/**
	 * Change the {@link Party} leader. If CommandChannel leader was the previous leader, change it too.
	 * @param name : The name of the {@link Player} newly promoted to leader.
	 */
	public void changePartyLeader(String name)
	{
		changePartyLeader(getPlayerByName(name));
	}
	
	/**
	 * Change the {@link Party} leader. If CommandChannel leader was the previous leader, change it too.
	 * @param player : The {@link Player} newly promoted to leader.
	 */
	public void changePartyLeader(Player player)
	{
		if (player == null || player.isInDuel())
			return;
		
		if (!_members.contains(player))
		{
			player.sendPacket(SystemMessageId.YOU_CAN_TRANSFER_RIGHTS_ONLY_TO_ANOTHER_PARTY_MEMBER);
			return;
		}
		
		if (isLeader(player))
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFER_RIGHTS_TO_YOURSELF);
			return;
		}
		
		if (_commandChannel != null && _commandChannel.isLeader(getLeader()))
		{
			_commandChannel.setLeader(player);
			_commandChannel.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.COMMAND_CHANNEL_LEADER_NOW_S1).addCharName(player));
		}
		
		setLeader(player);
		broadcastNewLeaderStatus();
		
		if (player.isInPartyMatchRoom())
		{
			final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
			if (room != null)
				room.changeLeader(player);
		}
	}
	
	/**
	 * @param name : The name of the {@link Player} to search.
	 * @return A {@link Party} member by his name.
	 */
	private Player getPlayerByName(String name)
	{
		for (Player member : _members)
		{
			if (member.getName().equalsIgnoreCase(name))
				return member;
		}
		return null;
	}
	
	/**
	 * Distribute item(s) to one {@link Party} member, based on {@link Party}'s {@link LootRule}.
	 * @param player : The initial {@link Player} looter.
	 * @param item : The {@link ItemInstance} used as looted item to distribute.
	 * @param summon : If different than null, use the {@link Pet} as benefactor.
	 */
	public void distributeItem(Player player, ItemInstance item, Summon summon)
	{
		if (item.getItemId() == 57)
		{
			distributeAdena(player, item.getCount(), player);
			item.destroyMe();
			return;
		}
		
		final Player target = getValidLooter(player, item.getItemId(), false, player);
		if (target == null)
			return;
		
		if (summon instanceof Pet && target == summon.getOwner())
			summon.addItem(item, true);
		else
		{
			if (item.getCount() > 1)
				broadcastToPartyMembers(target, SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S3_S2).addCharName(target).addItemName(item).addItemNumber(item.getCount()));
			else if (item.getEnchantLevel() > 0)
				broadcastToPartyMembers(target, SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S2_S3).addCharName(target).addNumber(item.getEnchantLevel()).addItemName(item));
			else
				broadcastToPartyMembers(target, SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S2).addCharName(target).addItemName(item));
			
			target.addItem(item, true);
		}
	}
	
	/**
	 * Distribute item(s) to one {@link Party} member, based on {@link Party}'s {@link LootRule}.
	 * @param player : The initial {@link Player} looter.
	 * @param item : The {@link IntIntHolder} used as looted item to distribute.
	 * @param isSpoil : True if the item comes from a spoil process, false otherwise.
	 * @param target : The {@link Attackable} used as looted target.
	 */
	public void distributeItem(Player player, IntIntHolder item, boolean isSpoil, Attackable target)
	{
		if (item == null)
			return;
		
		if (item.getId() == 57)
		{
			distributeAdena(player, item.getValue(), target);
			return;
		}
		
		final Player looter = getValidLooter(player, item.getId(), isSpoil, target);
		if (looter == null)
			return;
		
		if (isSpoil)
			looter.addEarnedItem(item.getId(), item.getValue(), true);
		else
			looter.addItem(item.getId(), item.getValue(), true);
		
		SystemMessage msg;
		if (item.getValue() > 1)
		{
			msg = (isSpoil) ? SystemMessage.getSystemMessage(SystemMessageId.S1_SWEEPED_UP_S3_S2) : SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S3_S2);
			msg.addCharName(looter);
			msg.addItemName(item.getId());
			msg.addItemNumber(item.getValue());
		}
		else
		{
			msg = (isSpoil) ? SystemMessage.getSystemMessage(SystemMessageId.S1_SWEEPED_UP_S2) : SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S2);
			msg.addCharName(looter);
			msg.addItemName(item.getId());
		}
		broadcastToPartyMembers(looter, msg);
	}
	
	/**
	 * Distribute adena to {@link Party} members.
	 * @param player : The {@link Player} picker.
	 * @param adena : The Adena amount.
	 * @param target : The {@link Creature} of which the member must be within a certain range (must not be null).
	 */
	public void distributeAdena(Player player, int adena, Creature target)
	{
		List<Player> toReward = new ArrayList<>(_members.size());
		for (Player member : _members)
		{
			if (member.getAdena() == Integer.MAX_VALUE || !member.isInStrictRadius(target, Config.PARTY_RANGE))
				continue;
			
			toReward.add(member);
		}
		
		if (toReward.isEmpty())
			return;
		
		final int count = adena / toReward.size();
		for (Player member : toReward)
			member.addAdena(count, true);
	}
	
	/**
	 * Distribute Experience and SP rewards to {@link Party} members in the known area of the last attacker.<BR>
	 * <BR>
	 * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T GIVE rewards to Pet</B></FONT><BR>
	 * <BR>
	 * Exception are Pets that leech from the owner's XP; they get the exp indirectly, via the owner's exp gain.<BR>
	 * @param xp_dynam
	 * @param xpReward_pr
	 * @param spReward_pr
	 * @param xpReward : The Experience reward to distribute.
	 * @param spReward : The SP reward to distribute.
	 * @param rewardedMembers : The {@link Player}s' {@link List} to reward.
	 * @param topLvl : The maximum level.
	 * @param rewards : The {@link Map} of {@link Creature}s and {@link RewardInfo}.
	 */
	public void distributeXpAndSp(long xp_dynam, long xpReward_pr, int spReward_pr, long xpReward, int spReward, List<Player> rewardedMembers, int topLvl, Map<Creature, RewardInfo> rewards)
	{
		final List<Player> validMembers = new ArrayList<>();
		
		if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("level"))
		{
			for (Player member : rewardedMembers)
			{
				if (topLvl - member.getStatus().getLevel() <= Config.PARTY_XP_CUTOFF_LEVEL)
					validMembers.add(member);
			}
		}
		else if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("percentage"))
		{
			int sqLevelSum = 0;
			for (Player member : rewardedMembers)
				sqLevelSum += (member.getStatus().getLevel() * member.getStatus().getLevel());
			
			for (Player member : rewardedMembers)
			{
				int sqLevel = member.getStatus().getLevel() * member.getStatus().getLevel();
				if (sqLevel * 100 >= sqLevelSum * Config.PARTY_XP_CUTOFF_PERCENT)
					validMembers.add(member);
			}
		}
		else if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("auto"))
		{
			int sqLevelSum = 0;
			for (Player member : rewardedMembers)
				sqLevelSum += (member.getStatus().getLevel() * member.getStatus().getLevel());
			
			final int partySize = Math.clamp(rewardedMembers.size(), 1, 9);
			
			for (Player member : rewardedMembers)
			{
				int sqLevel = member.getStatus().getLevel() * member.getStatus().getLevel();
				if (sqLevel >= sqLevelSum * (1 - 1 / (1 + BONUS_EXP_SP[partySize] - BONUS_EXP_SP[partySize - 1])))
					validMembers.add(member);
			}
		}
		else if (Config.PARTY_XP_CUTOFF_METHOD.equalsIgnoreCase("none"))
			validMembers.addAll(rewardedMembers);
		
		final double partyRate = BONUS_EXP_SP[Math.min(validMembers.size(), 9)];
		
		xpReward *= partyRate * Config.RATE_PARTY_XP;
		spReward *= partyRate * Config.RATE_PARTY_SP;
		xpReward_pr *= partyRate * Config.RATE_PARTY_XP;
		spReward_pr *= partyRate * Config.RATE_PARTY_SP;
		xp_dynam *= partyRate * Config.RATE_PARTY_XP;
		
		long xpRew = 0;
		int spRew = 0;
		int sqLevelSum = 0;
		for (Player member : validMembers)
			sqLevelSum += member.getStatus().getLevel() * member.getStatus().getLevel();
		
		for (Player member : rewardedMembers)
		{
			if (member.isDead())
				continue;
			
			if (Config.DYNAMIC_XP)
			{
				xpRew = xp_dynam;
				spRew = spReward;
			}
			else if (member.getPremiumService() == 1)
			{
				xpRew = xpReward_pr;
				spRew = spReward_pr;
			}
			else
			{
				xpRew = xpReward;
				spRew = spReward;
			}
			
			if (validMembers.contains(member))
			{
				final float penalty = member.hasServitor() ? ((Servitor) member.getSummon()).getExpPenalty() : 0;
				
				final double sqLevel = member.getStatus().getLevel() * member.getStatus().getLevel();
				final double preCalculation = (sqLevel / sqLevelSum) * (1 - penalty);
				
				long xp = Math.round(xpRew * preCalculation);
				int sp = (int) (spRew * preCalculation);
				
				member.updateKarmaLoss(xp);
				
				member.addExpAndSp(xp, sp, rewards);
			}
			else
				member.addExpAndSp(0, 0);
		}
	}
	
	public LootRule getLootRule()
	{
		return _lootRule;
	}
	
	public boolean isInCommandChannel()
	{
		return _commandChannel != null;
	}
	
	public CommandChannel getCommandChannel()
	{
		return _commandChannel;
	}
	
	public void setCommandChannel(CommandChannel channel)
	{
		_commandChannel = channel;
	}
	
	/**
	 * @return True if the entire party is currently dead, false otherwise.
	 */
	public boolean wipedOut()
	{
		return _members.stream().allMatch(Player::isDead);
	}
	
	/**
	 * Check whether the leader of this {@link Party} is the same as the leader of the specified {@link Party} (which essentially means they're the same group).
	 * @param party : The other {@link Party} to check against.
	 * @return True if this {@link Party} equals the specified {@link Party}, false otherwise.
	 */
	public boolean equals(Party party)
	{
		return party != null && getLeaderObjectId() == party.getLeaderObjectId();
	}
	
	/**
	 * Reset the duel state of each {@link Party} member.
	 */
	public void resetDuelState()
	{
		_members.forEach(Player::resetDuelState);
	}
	
	/**
	 * Reset the fight state of each {@link Party} member.
	 */
	public void stopToFight()
	{
		_members.forEach(Player::stopToFight);
	}
}