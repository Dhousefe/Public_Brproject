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
package ext.mods.gameserver.model.residence.castle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.util.ArraysUtil;
import ext.mods.extensions.listener.manager.SiegeListenerManager;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.HeroManager;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.SiegeStatus;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.TowerSpawnLocation;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanMember;
import ext.mods.gameserver.model.pledge.ItemInfo;
import ext.mods.gameserver.model.records.custom.SiegeInfo;
import ext.mods.gameserver.model.residence.Siegable;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.UserInfo;

public class Siege implements Siegable
{
	private static final CLogger LOGGER = new CLogger(Siege.class.getName());
	
	private static final String LOAD_SIEGE_CLAN = "SELECT clan_id,type FROM siege_clans WHERE castle_id=?";
	private static final String CLEAR_SIEGE_CLANS = "DELETE FROM siege_clans WHERE castle_id=?";
	private static final String CLEAR_PENDING_CLANS = "DELETE FROM siege_clans WHERE castle_id=? AND type='PENDING'";
	private static final String CLEAR_SIEGE_CLAN = "DELETE FROM siege_clans WHERE castle_id=? AND clan_id=?";
	private static final String UPDATE_SIEGE_INFOS = "UPDATE castle SET siegeDate=?, regTimeOver=? WHERE id=?";
	private static final String ADD_OR_UPDATE_SIEGE_CLAN = "INSERT INTO siege_clans (clan_id,castle_id,type) VALUES (?,?,?) ON DUPLICATE KEY UPDATE type=VALUES(type)";
	
	private final Castle _castle;
	
	private final Map<Clan, SiegeSide> _registeredClans = new ConcurrentHashMap<>();
	
	protected Calendar _siegeEndDate;
	protected ScheduledFuture<?> _siegeTask;
	
	private Clan _formerOwner;
	private SiegeStatus _siegeStatus = SiegeStatus.REGISTRATION_OPENED;
	
	private List<NpcMaker> _makerEvents = Collections.emptyList();
	
	public Siege(Castle castle)
	{
		_castle = castle;
		
		if (_castle.getOwnerId() > 0)
		{
			final Clan clan = ClanTable.getInstance().getClan(_castle.getOwnerId());
			if (clan != null)
				_registeredClans.put(clan, SiegeSide.OWNER);
		}
		
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(LOAD_SIEGE_CLAN))
			{
				ps.setInt(1, _castle.getId());
				
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						final Clan clan = ClanTable.getInstance().getClan(rs.getInt("clan_id"));
						if (clan != null)
							_registeredClans.put(clan, Enum.valueOf(SiegeSide.class, rs.getString("type")));
					}
				}
				
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load siege registered clans.", e);
		}
		
		startAutoTask();
	}
	
	@Override
	public void startSiege()
	{
		if (isInProgress())
			return;
		
		if (getAttackerClans().isEmpty())
		{
			final SystemMessage sm = SystemMessage.getSystemMessage((_castle.getOwnerId() <= 0) ? SystemMessageId.SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST : SystemMessageId.S1_SIEGE_WAS_CANCELED_BECAUSE_NO_CLANS_PARTICIPATED);
			sm.addFortId(_castle.getId());
			
			World.toAllOnlinePlayers(sm);
			saveCastleSiege(true);
			return;
		}
		
		_formerOwner = ClanTable.getInstance().getClan(_castle.getOwnerId());
		
		changeStatus(SiegeStatus.IN_PROGRESS);
		
		_castle.getSiegeZone().banishForeigners(_castle.getOwnerId());
		_castle.getSiegeZone().setActive(true);
		
		updatePlayerSiegeStateFlags(false);
		
		_castle.getControlTowers().forEach(TowerSpawnLocation::polymorph);
		
		_castle.closeDoors();
		_castle.spawnSiegeGuardsOrMercenaries();
		
		World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.SIEGE_OF_S1_HAS_STARTED).addFortId(_castle.getId()));
		World.toAllOnlinePlayers(new PlaySound("systemmsg_e.17"));
		
		announce(SystemMessageId.TEMPORARY_ALLIANCE, SiegeSide.ATTACKER);
		
		_siegeEndDate = Calendar.getInstance();
		_siegeEndDate.add(Calendar.MINUTE, Config.SIEGE_LENGTH);
		
		processSiegeTimer();
		SiegeListenerManager.getInstance().notifySiegeStart(this);
		
	}
	
	@Override
	public void endSiege()
	{
		if (!isInProgress())
			return;
		
		World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.SIEGE_OF_S1_HAS_ENDED).addFortId(_castle.getId()));
		World.toAllOnlinePlayers(new PlaySound("systemmsg_e.18"));
		
		if (_castle.getOwnerId() > 0)
		{
			Clan clan = ClanTable.getInstance().getClan(_castle.getOwnerId());
			World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_VICTORIOUS_OVER_S2_S_SIEGE).addString(clan.getName()).addFortId(_castle.getId()));
			
			for (Player player : clan.getOnlineMembers())
			{
				final Map<Integer, ItemInfo> item = player.isClanLeader() ? _castle.getItemsLeader() : _castle.getItemsMember();
				
				item.forEach((itemId, itemCount) ->
				{
					player.addItem(itemId, itemCount.getCount(), true).setEnchantLevel(itemCount.getEnchant(), null);
				});
			}
			
			if (_formerOwner != null && clan != _formerOwner)
			{
				_castle.checkItemsForClan(_formerOwner);
				
				for (ClanMember member : clan.getMembers())
				{
					final Player player = member.getPlayerInstance();
					if (player != null && player.isNoble())
						HeroManager.getInstance().setCastleTaken(player.getObjectId(), _castle.getId());
				}
			}
		}
		else
			World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.SIEGE_S1_DRAW).addFortId(_castle.getId()));
		
		for (Clan clan : _registeredClans.keySet())
		{
			clan.setSiegeKills(0);
			clan.setSiegeDeaths(0);
			clan.setFlag(null);
		}
		
		updateClansReputation();
		
		_castle.getSiegeZone().banishForeigners(_castle.getOwnerId());
		
		updatePlayerSiegeStateFlags(true);
		
		saveCastleSiege(true);
		
		clearAllClans();
		
		_castle.getControlTowers().forEach(TowerSpawnLocation::unpolymorph);
		
		_castle.despawnSiegeGuardsOrMercenaries();
		
		_castle.spawnDoors(false);
		
		_castle.getSiegeZone().setActive(false);
		
		Clan owner = ClanTable.getInstance().getClan(_castle.getOwnerId());
		if (owner != null)
		{
			for (Player player : owner.getOnlineMembers())
			{
				final Map<Integer, Integer> skill = player.isClanLeader() ? _castle.getSkillsLeader() : _castle.getSkillsMember();
				skill.forEach((skillId, skillLvl) ->
				{
					player.addSkill(SkillTable.getInstance().getInfo(skillId, skillLvl), true);
				});
			}
		}
		
		if (_formerOwner != null)
		{
			Clan formerOwner = ClanTable.getInstance().getClan(_formerOwner.getClanId());
			for (Player player : formerOwner.getOnlineMembers())
			{
				final Map<Integer, Integer> skill = player.isClanLeader() ? _castle.getSkillsLeader() : _castle.getSkillsMember();
				skill.forEach((skillId, skillLvl) ->
				{
					player.removeSkill(skillId, true);
				});
			}
		}
		SiegeListenerManager.getInstance().notifySiegeEnd(this);
	}
	
	@Override
	public final List<Clan> getAttackerClans()
	{
		return _registeredClans.entrySet().stream().filter(e -> e.getValue() == SiegeSide.ATTACKER).map(Map.Entry::getKey).toList();
	}
	
	@Override
	public final List<Clan> getDefenderClans()
	{
		return _registeredClans.entrySet().stream().filter(e -> e.getValue() == SiegeSide.DEFENDER || e.getValue() == SiegeSide.OWNER).map(Map.Entry::getKey).toList();
	}
	
	@Override
	public boolean checkSide(Clan clan, SiegeSide type)
	{
		return clan != null && _registeredClans.get(clan) == type;
	}
	
	@Override
	public boolean checkSides(Clan clan, SiegeSide... types)
	{
		return clan != null && ArraysUtil.contains(types, _registeredClans.get(clan));
	}
	
	@Override
	public boolean checkSides(Clan clan)
	{
		return clan != null && _registeredClans.containsKey(clan);
	}
	
	@Override
	public Npc getFlag(Clan clan)
	{
		return (checkSide(clan, SiegeSide.ATTACKER)) ? clan.getFlag() : null;
	}
	
	@Override
	public final Calendar getSiegeDate()
	{
		return _castle.getSiegeDate();
	}
	
	public Map<Clan, SiegeSide> getRegisteredClans()
	{
		return _registeredClans;
	}
	
	public final List<Clan> getPendingClans()
	{
		return _registeredClans.entrySet().stream().filter(e -> e.getValue() == SiegeSide.PENDING).map(Map.Entry::getKey).toList();
	}
	
	/**
	 * Update clan reputation points over siege end, as following :
	 * <ul>
	 * <li>The former clan failed to defend the castle : 1000 points for new owner, -1000 for former clan.</li>
	 * <li>The former clan successfully defended the castle, ending in a draw : 500 points for former clan.</li>
	 * <li>No former clan, which means players successfully attacked over NPCs : 1000 points for new owner.</li>
	 * </ul>
	 */
	public void updateClansReputation()
	{
		final Clan owner = ClanTable.getInstance().getClan(_castle.getOwnerId());
		if (_formerOwner != null)
		{
			if (_formerOwner != owner)
			{
				_formerOwner.takeReputationScore(2000);
				_formerOwner.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_WAS_DEFEATED_IN_SIEGE_AND_LOST_S1_REPUTATION_POINTS).addNumber(1000));
				
				if (owner != null)
				{
					owner.addReputationScore(1000);
					owner.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_VICTORIOUS_IN_SIEGE_AND_GAINED_S1_REPUTATION_POINTS).addNumber(1000));
				}
			}
			else
			{
				_formerOwner.addReputationScore(500);
				_formerOwner.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_VICTORIOUS_IN_SIEGE_AND_GAINED_S1_REPUTATION_POINTS).addNumber(500));
			}
		}
		else if (owner != null)
		{
			owner.addReputationScore(1000);
			owner.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_VICTORIOUS_IN_SIEGE_AND_GAINED_S1_REPUTATION_POINTS).addNumber(1000));
		}
	}
	
	/**
	 * This method is used to switch a specific {@link Clan} {@link SiegeSide} to another {@link SiegeSide}.
	 * @param clan : The {@link Clan} to edit state.
	 * @param newState : The new {@link SiegeSide} to set.
	 */
	private void switchSide(Clan clan, SiegeSide newState)
	{
		_registeredClans.put(clan, newState);
	}
	
	/**
	 * This method is used to switch all {@link SiegeSide}s to another {@link SiegeSide}.
	 * @param newState : The new {@link SiegeSide} to set.
	 * @param previousStates : The {@link SiegeSide}s to replace.
	 */
	private void switchSides(SiegeSide newState, SiegeSide... previousStates)
	{
		for (Map.Entry<Clan, SiegeSide> entry : _registeredClans.entrySet())
		{
			if (ArraysUtil.contains(previousStates, entry.getValue()))
				entry.setValue(newState);
		}
	}
	
	public SiegeSide getSide(Clan clan)
	{
		return _registeredClans.get(clan);
	}
	
	/**
	 * Check if both {@link Clan}s set as parameters are registered as opponents.
	 * @param formerClan : The first {@link Clan} to check.
	 * @param targetClan : The second {@link Clan} to check.
	 * @return True if one side is attacker/defender and other side is defender/attacker, and false if one of clan isn't registered or if previous statement didn't match.
	 */
	public boolean isOnOppositeSide(Clan formerClan, Clan targetClan)
	{
		if (formerClan == null || targetClan == null)
			return false;
		
		final SiegeSide formerSide = _registeredClans.get(formerClan);
		final SiegeSide targetSide = _registeredClans.get(targetClan);
		
		if (formerSide == null || targetSide == null)
			return false;
		
		switch (formerSide)
		{
			case OWNER, DEFENDER, PENDING:
				return targetSide == SiegeSide.ATTACKER;
			
			case ATTACKER:
				return targetSide == SiegeSide.OWNER || targetSide == SiegeSide.DEFENDER || targetSide == SiegeSide.PENDING;
		}
		return false;
	}
	
	/**
	 * When control of castle changed during siege.
	 */
	public void midVictory()
	{
		if (!isInProgress())
			return;
		
		_castle.despawnSiegeGuardsOrMercenaries();
		
		_castle.spawnSiegeGuardsOrMercenaries();
		
		if (_castle.getOwnerId() <= 0)
			return;
		
		final List<Clan> attackers = getAttackerClans();
		
		final Clan castleOwner = ClanTable.getInstance().getClan(_castle.getOwnerId());
		final int allyId = castleOwner.getAllyId();
		
		announce(SystemMessageId.TEMPORARY_ALLIANCE_DISSOLVED, SiegeSide.ATTACKER);
		
		switchSides(SiegeSide.ATTACKER, SiegeSide.DEFENDER, SiegeSide.OWNER);
		
		switchSide(castleOwner, SiegeSide.OWNER);
		
		if (allyId != 0)
		{
			for (Clan clan : attackers)
			{
				if (clan.getAllyId() == allyId)
					switchSide(clan, SiegeSide.DEFENDER);
			}
		}
		_castle.getSiegeZone().banishForeigners(_castle.getOwnerId());
		
		for (Clan clan : attackers)
			clan.setFlag(null);
		
		_castle.removeDoorUpgrade();
		
		_castle.removeTrapUpgrade();
		
		_castle.spawnDoors(true);
		
		_castle.getControlTowers().forEach(TowerSpawnLocation::midVictory);
		
		updatePlayerSiegeStateFlags(false);
	}
	
	/**
	 * Broadcast a {@link SystemMessage} to given {@link SiegeSide}s.
	 * @param sm : The {@link SystemMessage} to send to {@link Clan}s members.
	 * @param sides : The {@link SiegeSide}s to inform. Only ATTACKER and DEFENDER actually react to this method.
	 */
	public void announce(SystemMessage sm, SiegeSide... sides)
	{
		for (SiegeSide side : sides)
		{
			if (side == SiegeSide.ATTACKER)
				getAttackerClans().forEach(c -> c.broadcastToMembers(sm));
			else if (side == SiegeSide.DEFENDER)
				getDefenderClans().forEach(c -> c.broadcastToMembers(sm));
		}
	}
	
	/**
	 * Broadcast a static {@link SystemMessageId} to given {@link SiegeSide}s.
	 * @param smId : The {@link SystemMessageId} to send to {@link Clan}s members.
	 * @param sides : The {@link SiegeSide}s to inform. Only ATTACKER and DEFENDER actually react to this method.
	 * @see #announce(SystemMessage, SiegeSide...)
	 */
	public void announce(SystemMessageId smId, SiegeSide... sides)
	{
		announce(SystemMessage.getSystemMessage(smId), sides);
	}
	
	private void updatePlayerSiegeStateFlags(boolean clear)
	{
		for (Clan clan : getAttackerClans())
		{
			for (Player member : clan.getOnlineMembers())
			{
				member.setSiegeState((clear) ? 0 : 1);
				member.sendPacket(new UserInfo(member));
				member.broadcastRelationsChanges();
			}
		}
		
		for (Clan clan : getDefenderClans())
		{
			for (Player member : clan.getOnlineMembers())
			{
				member.setSiegeState((clear) ? 0 : 2);
				member.sendPacket(new UserInfo(member));
				member.broadcastRelationsChanges();
			}
		}
	}
	
	/** Clear all registered siege clans from database for castle */
	public void clearAllClans()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(CLEAR_SIEGE_CLANS))
		{
			ps.setInt(1, _castle.getId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't clear siege registered clans.", e);
		}
		
		_registeredClans.clear();
		
		if (_castle.getOwnerId() > 0)
		{
			final Clan clan = ClanTable.getInstance().getClan(_castle.getOwnerId());
			if (clan != null)
				_registeredClans.put(clan, SiegeSide.OWNER);
		}
	}
	
	/** Clear all siege clans waiting for approval from database for castle */
	protected void clearPendingClans()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(CLEAR_PENDING_CLANS))
		{
			ps.setInt(1, _castle.getId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't clear siege pending clans.", e);
		}
		
		_registeredClans.entrySet().removeIf(e -> e.getValue() == SiegeSide.PENDING);
	}
	
	/**
	 * Register clan as attacker
	 * @param player : The player trying to register
	 */
	public void registerAttacker(Player player)
	{
		if (player.getClan() == null)
			return;
		
		int allyId = 0;
		if (_castle.getOwnerId() != 0)
			allyId = ClanTable.getInstance().getClan(_castle.getOwnerId()).getAllyId();
		
		if (allyId != 0 && player.getClan().getAllyId() == allyId)
		{
			player.sendPacket(SystemMessageId.CANNOT_ATTACK_ALLIANCE_CASTLE);
			return;
		}
		
		if (allyIsRegisteredOnOppositeSide(player.getClan(), true))
			player.sendPacket(SystemMessageId.CANT_ACCEPT_ALLY_ENEMY_FOR_SIEGE);
		else if (checkIfCanRegister(player, SiegeSide.ATTACKER))
		{
			registerClan(player.getClan(), SiegeSide.ATTACKER);
			SiegeListenerManager.getInstance().notifyRegisterAttacker(this, player);
		}
	}
	
	/**
	 * Register clan as defender.
	 * @param player : The player trying to register
	 */
	public void registerDefender(Player player)
	{
		if (player.getClan() == null)
			return;
		
		if (_castle.getOwnerId() <= 0)
			player.sendPacket(SystemMessageId.DEFENDER_SIDE_FULL);
		else if (allyIsRegisteredOnOppositeSide(player.getClan(), false))
			player.sendPacket(SystemMessageId.CANT_ACCEPT_ALLY_ENEMY_FOR_SIEGE);
		else if (checkIfCanRegister(player, SiegeSide.PENDING))
		{
			registerClan(player.getClan(), SiegeSide.PENDING);
			SiegeListenerManager.getInstance().notifyRegisterDefender(this, player);
		}
	}
	
	/**
	 * Verify if allies are registered on different list than the actual player's choice. Let's say clan A and clan B are in same alliance. If clan A wants to attack a castle, clan B mustn't be on defenders' list. The contrary is right too : you can't defend if one ally is on attackers' list.
	 * @param clan : The clan used for alliance existence checks.
	 * @param attacker : A boolean used to know if this check is used for attackers or defenders.
	 * @return true if one clan of the alliance is registered in other side.
	 */
	private boolean allyIsRegisteredOnOppositeSide(Clan clan, boolean attacker)
	{
		final int allyId = clan.getAllyId();
		if (allyId != 0)
		{
			for (Clan alliedClan : ClanTable.getInstance().getClans())
			{
				if (alliedClan.getAllyId() == allyId)
				{
					if (alliedClan.getClanId() == clan.getClanId())
						continue;
					
					if (attacker)
					{
						if (checkSides(alliedClan, SiegeSide.DEFENDER, SiegeSide.OWNER, SiegeSide.PENDING))
							return true;
					}
					else
					{
						if (checkSides(alliedClan, SiegeSide.ATTACKER))
							return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Remove clan from siege. Drop it from _registeredClans and database. Castle owner can't be dropped.
	 * @param clan : The clan to check.
	 */
	public void unregisterClan(Clan clan)
	{
		if (clan == null || clan.getCastleId() == _castle.getId() || _registeredClans.remove(clan) == null)
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(CLEAR_SIEGE_CLAN))
		{
			ps.setInt(1, _castle.getId());
			ps.setInt(2, clan.getClanId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't unregister clan on siege.", e);
		}
	}
	
	/**
	 * This method allows to :
	 * <ul>
	 * <li>Check if the siege time is deprecated, and recalculate otherwise.</li>
	 * <li>Schedule start siege (it's in an else because saveCastleSiege() already affect it).</li>
	 * </ul>
	 */
	private void startAutoTask()
	{
		if (_castle.getSiegeDate().getTimeInMillis() < Calendar.getInstance().getTimeInMillis())
			saveCastleSiege(false);
		else
		{
			if (_siegeTask != null)
				_siegeTask.cancel(false);
			
			_siegeTask = ThreadPool.schedule(this::siegeStart, 1000);
		}
	}
	
	/**
	 * @param player : The player trying to register.
	 * @param type : The SiegeSide to test.
	 * @return true if the player can register.
	 */
	private boolean checkIfCanRegister(Player player, SiegeSide type)
	{
		SystemMessage sm;
		
		if (isRegistrationOver())
			sm = SystemMessage.getSystemMessage(SystemMessageId.DEADLINE_FOR_SIEGE_S1_PASSED).addFortId(_castle.getId());
		else if (isInProgress())
			sm = SystemMessage.getSystemMessage(SystemMessageId.NOT_SIEGE_REGISTRATION_TIME2);
		else if (player.getClan() == null || player.getClan().getLevel() < Config.MINIMUM_CLAN_LEVEL)
			sm = SystemMessage.getSystemMessage(SystemMessageId.ONLY_CLAN_LEVEL_4_ABOVE_MAY_SIEGE);
		else if (player.getClan().hasCastle())
			sm = (player.getClan().getClanId() == _castle.getOwnerId()) ? SystemMessage.getSystemMessage(SystemMessageId.CLAN_THAT_OWNS_CASTLE_IS_AUTOMATICALLY_REGISTERED_DEFENDING) : SystemMessage.getSystemMessage(SystemMessageId.CLAN_THAT_OWNS_CASTLE_CANNOT_PARTICIPATE_OTHER_SIEGE);
		else if (player.getClan().isRegisteredOnSiege())
			sm = SystemMessage.getSystemMessage(SystemMessageId.ALREADY_REQUESTED_SIEGE_BATTLE);
		else if (checkIfAlreadyRegisteredForSameDay(player.getClan()))
			sm = SystemMessage.getSystemMessage(SystemMessageId.APPLICATION_DENIED_BECAUSE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_SIEGE_BATTLE);
		else if (type == SiegeSide.ATTACKER && getAttackerClans().size() >= Config.MAX_ATTACKERS_NUMBER)
			sm = SystemMessage.getSystemMessage(SystemMessageId.ATTACKER_SIDE_FULL);
		else if ((type == SiegeSide.DEFENDER || type == SiegeSide.PENDING || type == SiegeSide.OWNER) && (getDefenderClans().size() + getPendingClans().size() >= Config.MAX_DEFENDERS_NUMBER))
			sm = SystemMessage.getSystemMessage(SystemMessageId.DEFENDER_SIDE_FULL);
		else
			return true;
		
		player.sendPacket(sm);
		return false;
	}
	
	/**
	 * @param clan The L2Clan of the player trying to register
	 * @return true if the clan has already registered to a siege for the same day.
	 */
	public boolean checkIfAlreadyRegisteredForSameDay(Clan clan)
	{
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			final Siege siege = castle.getSiege();
			if (siege == this)
				continue;
			
			if (siege.getSiegeDate().get(Calendar.DAY_OF_WEEK) == getSiegeDate().get(Calendar.DAY_OF_WEEK) && siege.checkSides(clan))
				return true;
		}
		return false;
	}
	
	/**
	 * Save castle siege related to database.
	 * @param launchTask : if true, launch the start siege task.
	 */
	private void saveCastleSiege(boolean launchTask)
	{
		if (_castle.getSieges().size() == 1)
			customSetNextSiegeData();
		else
			setNextSiegeDate();
		
		_castle.setTimeRegistrationOver(false);
		
		saveSiegeDate();
		
		if (launchTask)
			startAutoTask();
		
		LOGGER.info("New date for {} siege: {}.", _castle.getName(), _castle.getSiegeDate().getTime());
	}
	
	/**
	 * Save siege date to database.
	 */
	private void saveSiegeDate()
	{
		if (_siegeTask != null)
		{
			_siegeTask.cancel(false);
			_siegeTask = ThreadPool.schedule(this::siegeStart, 1000);
		}
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_SIEGE_INFOS))
		{
			ps.setLong(1, getSiegeDate().getTimeInMillis());
			ps.setString(2, String.valueOf(isTimeRegistrationOver()));
			ps.setInt(3, _castle.getId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save siege date.", e);
		}
	}
	
	/**
	 * Save registration to database.
	 * @param clan : The L2Clan of player.
	 * @param type
	 */
	public void registerClan(Clan clan, SiegeSide type)
	{
		if (clan.hasCastle())
			return;
		
		switch (type)
		{
			case DEFENDER, PENDING, OWNER:
				if (getDefenderClans().size() + getPendingClans().size() >= Config.MAX_DEFENDERS_NUMBER)
					return;
				break;
			
			default:
				if (getAttackerClans().size() >= Config.MAX_ATTACKERS_NUMBER)
					return;
				break;
		}
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(ADD_OR_UPDATE_SIEGE_CLAN))
		{
			ps.setInt(1, clan.getClanId());
			ps.setInt(2, _castle.getId());
			ps.setString(3, type.toString());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't register clan on siege.", e);
		}
		
		_registeredClans.put(clan, type);
	}
	
	/**
	 * Set the date for the next siege.
	 */
	private void setNextSiegeDate()
	{
		final Calendar siegeDate = _castle.getSiegeDate();
		if (siegeDate.getTimeInMillis() < System.currentTimeMillis())
			siegeDate.setTimeInMillis(System.currentTimeMillis());
		
		switch (_castle.getId())
		{
			case 3, 4, 6, 7:
				siegeDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				break;
			
			default:
				siegeDate.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				break;
		}
		
		siegeDate.add(Calendar.WEEK_OF_YEAR, 2);
		
		siegeDate.set(Calendar.HOUR_OF_DAY, 18);
		siegeDate.set(Calendar.MINUTE, 0);
		siegeDate.set(Calendar.SECOND, 0);
		siegeDate.set(Calendar.MILLISECOND, 0);
		
		World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.S1_ANNOUNCED_SIEGE_TIME).addFortId(_castle.getId()));
		changeStatus(SiegeStatus.REGISTRATION_OPENED);
	}
	
	private void customSetNextSiegeData()
	{
		final Calendar siegeDate = _castle.getSiegeDate();
		if (siegeDate.getTimeInMillis() < System.currentTimeMillis())
			siegeDate.setTimeInMillis(System.currentTimeMillis());
		
		Map<Integer, SiegeInfo> sieges = _castle.getSieges();
		
		for (Map.Entry<Integer, SiegeInfo> entry : sieges.entrySet())
		{
			var week = entry.getKey();
			var siegeInfo = entry.getValue();
			
			siegeDate.add(Calendar.DAY_OF_MONTH, siegeInfo.day());
			siegeDate.add(Calendar.WEEK_OF_MONTH, week);
			
			siegeDate.set(Calendar.HOUR_OF_DAY, siegeInfo.hour());
			siegeDate.set(Calendar.MINUTE, siegeInfo.minute());
			siegeDate.set(Calendar.SECOND, 0);
			siegeDate.set(Calendar.MILLISECOND, 0);
		}
		
		World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.S1_ANNOUNCED_SIEGE_TIME).addFortId(_castle.getId()));
		changeStatus(SiegeStatus.REGISTRATION_OPENED);
	}
	
	public final Castle getCastle()
	{
		return _castle;
	}
	
	public final boolean isInProgress()
	{
		return _siegeStatus == SiegeStatus.IN_PROGRESS;
	}
	
	public final boolean isRegistrationOver()
	{
		return _siegeStatus != SiegeStatus.REGISTRATION_OPENED;
	}
	
	public final boolean isTimeRegistrationOver()
	{
		return _castle.isTimeRegistrationOver();
	}
	
	/**
	 * @return siege registration end date, which always equals siege date minus one day.
	 */
	public final long getSiegeRegistrationEndDate()
	{
		return _castle.getSiegeDate().getTimeInMillis() - 86400000;
	}
	
	public void endTimeRegistration(boolean automatic)
	{
		_castle.setTimeRegistrationOver(true);
		if (!automatic)
			saveSiegeDate();
	}
	
	public void addMakerEvent(NpcMaker quest)
	{
		if (_makerEvents.isEmpty())
			_makerEvents = new ArrayList<>(3);
		
		_makerEvents.add(quest);
	}
	
	public String getStatusTranslation(Player player, SiegeStatus status)
	{
		switch (status)
		{
			case REGISTRATION_OPENED:
				return player.getSysString(10_166);
			case REGISTRATION_OVER:
				return player.getSysString(10_167);
			case IN_PROGRESS:
				return player.getSysString(10_168);
			default:
				return player.getSysString(10_169);
		}
	}
	
	public SiegeStatus getStatus()
	{
		return _siegeStatus;
	}
	
	protected void changeStatus(SiegeStatus status)
	{
		_siegeStatus = status;
		
		for (NpcMaker maker : _makerEvents)
			maker.getMaker().onSiegeEvent(this, maker);
	}
	
	private void siegeStart()
	{
		_siegeTask.cancel(false);
		
		if (isInProgress())
			return;
		
		if (!isTimeRegistrationOver())
		{
			final long regTimeRemaining = getSiegeRegistrationEndDate() - Calendar.getInstance().getTimeInMillis();
			if (regTimeRemaining > 0)
			{
				_siegeTask = ThreadPool.schedule(this::siegeStart, regTimeRemaining);
				return;
			}
			
			endTimeRegistration(true);
		}
		
		final long timeRemaining = getSiegeDate().getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		
		if (timeRemaining > 86400000)
			_siegeTask = ThreadPool.schedule(this::siegeStart, timeRemaining - 86400000);
		else if (timeRemaining <= 86400000 && timeRemaining > 13600000)
		{
			World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.REGISTRATION_TERM_FOR_S1_ENDED).addFortId(_castle.getId()));
			changeStatus(SiegeStatus.REGISTRATION_OVER);
			clearPendingClans();
			_siegeTask = ThreadPool.schedule(this::siegeStart, timeRemaining - 13600000);
		}
		else if (timeRemaining <= 13600000 && timeRemaining > 600000)
			_siegeTask = ThreadPool.schedule(this::siegeStart, timeRemaining - 600000);
		else if (timeRemaining <= 600000 && timeRemaining > 300000)
			_siegeTask = ThreadPool.schedule(this::siegeStart, timeRemaining - 300000);
		else if (timeRemaining <= 300000 && timeRemaining > 10000)
			_siegeTask = ThreadPool.schedule(this::siegeStart, timeRemaining - 10000);
		else if (timeRemaining <= 10000 && timeRemaining > 0)
			_siegeTask = ThreadPool.schedule(this::siegeStart, timeRemaining);
		else
			startSiege();
	}
	
	private void processSiegeTimer()
	{
		if (!isInProgress())
			return;
		
		final long timeRemaining = _siegeEndDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		
		if (timeRemaining > 3600000)
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 3600000);
		else if (timeRemaining > 1800000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.S1_HOURS_UNTIL_SIEGE_CONCLUSION).addNumber(1), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 1800000);
		}
		else if (timeRemaining > 600000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTES_UNTIL_SIEGE_CONCLUSION).addNumber(30), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 600000);
		}
		else if (timeRemaining > 300000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTES_UNTIL_SIEGE_CONCLUSION).addNumber(10), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 300000);
		}
		else if (timeRemaining > 60000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTES_UNTIL_SIEGE_CONCLUSION).addNumber(5), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 60000);
		}
		else if (timeRemaining > 10000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.S1_MINUTES_UNTIL_SIEGE_CONCLUSION).addNumber(1), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 10000);
		}
		else if (timeRemaining > 9000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(10), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 9000);
		}
		else if (timeRemaining > 8000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(9), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 8000);
		}
		else if (timeRemaining > 7000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(8), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 7000);
		}
		else if (timeRemaining > 6000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(7), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 6000);
		}
		else if (timeRemaining > 5000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(6), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 5000);
		}
		else if (timeRemaining > 4000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(5), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 4000);
		}
		else if (timeRemaining > 3000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(4), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 3000);
		}
		else if (timeRemaining > 2000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(3), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 2000);
		}
		else if (timeRemaining > 1000)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(2), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining - 1000);
		}
		else if (timeRemaining > 0)
		{
			announce(SystemMessage.getSystemMessage(SystemMessageId.CASTLE_SIEGE_S1_SECONDS_LEFT).addNumber(1), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
			ThreadPool.schedule(this::processSiegeTimer, timeRemaining);
		}
		else
			endSiege();
	}
}