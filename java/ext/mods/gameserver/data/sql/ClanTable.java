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
package ext.mods.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanMember;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.clanhall.SiegableHall;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import ext.mods.gameserver.network.serverpackets.PledgeShowMemberListAll;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.UserInfo;

public class ClanTable
{
	private static final CLogger LOGGER = new CLogger(ClanTable.class.getName());
	
	private static final String LOAD_CLANS = "SELECT * FROM clan_data";
	
	private static final String DELETE_CLAN = "DELETE FROM clan_data WHERE clan_id=?";
	private static final String DELETE_CLAN_PRIVS = "DELETE FROM clan_privs WHERE clan_id=?";
	private static final String DELETE_CLAN_SKILLS = "DELETE FROM clan_skills WHERE clan_id=?";
	private static final String DELETE_CLAN_SUBPLEDGES = "DELETE FROM clan_subpledges WHERE clan_id=?";
	private static final String DELETE_CLAN_WARS = "DELETE FROM clan_wars WHERE clan1=? OR clan2=?";
	private static final String DELETE_CLAN_SIEGES = "DELETE FROM siege_clans WHERE clan_id=?";
	private static final String RESET_CASTLE_TAX = "UPDATE castle SET currentTaxPercent=0, nextTaxPercent=0 WHERE id=?";
	
	private static final String INSERT_WAR = "REPLACE INTO clan_wars (clan1, clan2) VALUES(?,?)";
	private static final String UPDATE_WAR_TIME = "UPDATE clan_wars SET expiry_time=? WHERE clan1=? AND clan2=?";
	private static final String DELETE_WAR = "DELETE FROM clan_wars WHERE clan1=? AND clan2=?";
	
	private static final String DELETE_OLD_WARS = "DELETE FROM clan_wars WHERE expiry_time > 0 AND expiry_time <= ?";
	private static final String LOAD_WARS = "SELECT * FROM clan_wars";
	
	private static final String LOAD_RANK = "SELECT clan_id FROM clan_data ORDER BY reputation_score DESC LIMIT 99";
	private static final String CLEAR_GRADUATES = "UPDATE clan_data SET graduates=NULL";
	
	private final Map<Integer, Clan> _clans = new ConcurrentHashMap<>();
	
	protected ClanTable()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_CLANS);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final int clanId = rs.getInt("clan_id");
				final Clan clan = new Clan(clanId, rs.getInt("leader_id"));
				
				_clans.put(clanId, clan);
				
				clan.setName(rs.getString("clan_name"));
				clan.setLevel(rs.getInt("clan_level"));
				clan.setCastle(rs.getInt("hasCastle"));
				clan.setAllyId(rs.getInt("ally_id"));
				clan.setAllyName(rs.getString("ally_name"));
				
				final long allyExpireTime = rs.getLong("ally_penalty_expiry_time");
				if (allyExpireTime > System.currentTimeMillis())
					clan.setAllyPenaltyExpiryTime(allyExpireTime, rs.getInt("ally_penalty_type"));
				
				final long charExpireTime = rs.getLong("char_penalty_expiry_time");
				if (charExpireTime + Config.CLAN_JOIN_DAYS * 86400000L > System.currentTimeMillis())
					clan.setCharPenaltyExpiryTime(charExpireTime);
				
				clan.setDissolvingExpiryTime(rs.getLong("dissolving_expiry_time"));
				
				clan.setCrestId(rs.getInt("crest_id"));
				clan.setCrestLargeId(rs.getInt("crest_large_id"));
				clan.setAllyCrestId(rs.getInt("ally_crest_id"));
				
				clan.addReputationScore(rs.getInt("reputation_score"));
				clan.setAuctionBiddedAt(rs.getInt("auction_bid_at"));
				clan.setNewLeaderId(rs.getInt("new_leader_id"), false);
				
				if (clan.getDissolvingExpiryTime() != 0)
					scheduleRemoveClan(clan);
				
				clan.setNotice(rs.getString("notice"), rs.getBoolean("enabled"), false);
				clan.setIntroduction(rs.getString("introduction"), false);
				clan.setGraduates(rs.getString("graduates"));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore clans.", e);
		}
		LOGGER.info("Loaded {} clans.", _clans.size());
		
		allianceCheck();
		
		restoreWars();
		
		refreshClansLadder(false);
	}
	
	/**
	 * @return the {@link Collection} of all stored {@link Clan}s.
	 */
	public Collection<Clan> getClans()
	{
		return _clans.values();
	}
	
	/**
	 * @param clanId : The id of the Clan to retrieve.
	 * @return the {@link Clan} object based on id.
	 */
	public Clan getClan(int clanId)
	{
		return _clans.get(clanId);
	}
	
	/**
	 * @param clanName : The name of the Clan to retrieve.
	 * @return the {@link Clan} object based on name.
	 */
	public Clan getClanByName(String clanName)
	{
		return _clans.values().stream().filter(c -> c.getName().equalsIgnoreCase(clanName)).findAny().orElse(null);
	}
	
	/**
	 * Creates a new {@link Clan} and store clan info to database.
	 * @param player : The player who requested the clan creation.
	 * @param clanName : The name of the clan player wants.
	 * @return null if checks fail or the new Clan if successful.
	 */
	public Clan createClan(Player player, String clanName)
	{
		if (player == null)
			return null;
		
		if (player.getStatus().getLevel() < 10)
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN);
			return null;
		}
		
		if (player.getClanId() != 0)
		{
			player.sendPacket(SystemMessageId.FAILED_TO_CREATE_CLAN);
			return null;
		}
		
		if (System.currentTimeMillis() < player.getClanCreateExpiryTime())
		{
			player.sendPacket(SystemMessageId.YOU_MUST_WAIT_XX_DAYS_BEFORE_CREATING_A_NEW_CLAN);
			return null;
		}
		
		if (!StringUtil.isAlphaNumeric(clanName))
		{
			player.sendMessage("Clan name is incorrect.");
			return null;
		}
		
		if (clanName.length() < 2 || clanName.length() > 16)
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_LENGTH_INCORRECT);
			return null;
		}
		
		if (getClanByName(clanName) != null)
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS).addString(clanName));
			return null;
		}
		
		final Clan clan = new Clan(IdFactory.getInstance().getNextId(), clanName);
		final ClanMember leader = new ClanMember(clan, player);
		final NpcHtmlMessage html = new NpcHtmlMessage(leader.getObjectId());
		
		clan.setLeader(leader);
		leader.setPlayerInstance(player);
		clan.store();
		player.setClan(clan);
		player.setPledgeClass(ClanMember.calculatePledgeClass(player));
		
		_clans.put(clan.getClanId(), clan);
		
		html.setFile(player.getLocale(), "html/script/feature/Clan/9000-02-success.htm");
		player.sendPacket(new PledgeShowMemberListAll(clan, 0));
		player.sendPacket(new UserInfo(player));
		player.sendPacket(html);
		return clan;
	}
	
	/**
	 * Instantly delete related {@link Clan}. Run different clanId related queries, remove clan from _clans, inform Clan members, delete Clans from pending sieges.
	 * @param clan : The Clan to delete.
	 */
	public void destroyClan(Clan clan)
	{
		if (!_clans.containsKey(clan.getClanId()))
			return;
		
		clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.CLAN_HAS_DISPERSED));
		
		for (Castle castle : CastleManager.getInstance().getCastles())
			castle.getSiege().getRegisteredClans().keySet().removeIf(c -> c.getClanId() == clan.getClanId());
		
		final int hallId = clan.getClanHallId();
		if (hallId == 0)
		{
			for (SiegableHall sh : ClanHallManager.getInstance().getSiegableHalls())
				sh.removeAttacker(clan);
		}
		else
		{
			final SiegableHall hall = ClanHallManager.getInstance().getSiegableHall(hallId);
			if (hall != null && hall.getOwnerId() == clan.getClanId())
				hall.free();
		}
		
		for (int clanId : clan.getAttackerList())
		{
			final Clan attackerClan = _clans.get(clanId);
			
			attackerClan.deleteAttackerClan(clan.getClanId());
			attackerClan.deleteEnemyClan(clan.getClanId());
		}
		
		clan.getWarehouse().destroyAllItems();
		
		for (ClanMember member : clan.getMembers())
			clan.removeClanMember(member.getObjectId(), 0);
		
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(DELETE_CLAN))
			{
				ps.setInt(1, clan.getClanId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CLAN_PRIVS))
			{
				ps.setInt(1, clan.getClanId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CLAN_SKILLS))
			{
				ps.setInt(1, clan.getClanId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CLAN_SUBPLEDGES))
			{
				ps.setInt(1, clan.getClanId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CLAN_WARS))
			{
				ps.setInt(1, clan.getClanId());
				ps.setInt(2, clan.getClanId());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CLAN_SIEGES))
			{
				ps.setInt(1, clan.getClanId());
				ps.executeUpdate();
			}
			
			if (clan.getCastleId() != 0)
			{
				try (PreparedStatement ps = con.prepareStatement(RESET_CASTLE_TAX))
				{
					ps.setInt(1, clan.getCastleId());
					ps.executeUpdate();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete clan.", e);
		}
		
		IdFactory.getInstance().releaseId(clan.getClanId());
		
		_clans.remove(clan.getClanId());
	}
	
	/**
	 * Set up the {@link Clan} dissolve task.
	 * @param clan : The Clan to delete.
	 */
	public void scheduleRemoveClan(final Clan clan)
	{
		if (clan == null)
			return;
		
		ThreadPool.schedule(() ->
		{
			if (clan.getDissolvingExpiryTime() != 0)
				destroyClan(clan);
		}, Math.max(clan.getDissolvingExpiryTime() - System.currentTimeMillis(), 60000));
	}
	
	/**
	 * @param allyName : The ally name to test.
	 * @return true if the {@link Clan} is bound to this ally name.
	 */
	public boolean isAllyExists(String allyName)
	{
		for (Clan clan : _clans.values())
		{
			if (clan.getAllyName() != null && clan.getAllyName().equalsIgnoreCase(allyName))
				return true;
		}
		return false;
	}
	
	/**
	 * Start a {@link Clan} war. Broadcast messages towards both sides, edit database and containers.
	 * @param clanId1 : The attacker Clan id.
	 * @param clanId2 : The victim Clan id.
	 */
	public void storeClansWars(int clanId1, int clanId2)
	{
		final Clan clan1 = _clans.get(clanId1);
		final Clan clan2 = _clans.get(clanId2);
		
		clan1.setEnemyClan(clanId2);
		clan1.broadcastToMembers(new PledgeShowInfoUpdate(clan1), SystemMessage.getSystemMessage(SystemMessageId.CLAN_WAR_DECLARED_AGAINST_S1_IF_KILLED_LOSE_LOW_EXP).addString(clan2.getName()));
		
		clan2.setAttackerClan(clanId1);
		clan2.broadcastToMembers(new PledgeShowInfoUpdate(clan2), SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_DECLARED_WAR).addString(clan1.getName()));
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_WAR))
		{
			ps.setInt(1, clanId1);
			ps.setInt(2, clanId2);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't store clans wars.", e);
		}
	}
	
	/**
	 * Stop a {@link Clan} war. Broadcast messages towards both sides, edit database and containers.
	 * @param clanId1 : The surrendering Clan id.
	 * @param clanId2 : The enemy Clan id.
	 */
	public void deleteClansWars(int clanId1, int clanId2)
	{
		final Clan clan1 = _clans.get(clanId1);
		final Clan clan2 = _clans.get(clanId2);
		
		clan1.deleteEnemyClan(clanId2);
		clan1.broadcastToMembers(new PledgeShowInfoUpdate(clan1), SystemMessage.getSystemMessage(SystemMessageId.WAR_AGAINST_S1_HAS_STOPPED).addString(clan2.getName()));
		
		clan2.deleteAttackerClan(clanId1);
		clan2.broadcastToMembers(new PledgeShowInfoUpdate(clan2), SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_HAS_DECIDED_TO_STOP).addString(clan1.getName()));
		
		try (Connection con = ConnectionPool.getConnection())
		{
			if (Config.CLAN_WAR_PENALTY_WHEN_ENDED > 0)
			{
				final long penaltyExpiryTime = System.currentTimeMillis() + Config.CLAN_WAR_PENALTY_WHEN_ENDED * 86400000L;
				
				clan1.addWarPenaltyTime(clanId2, penaltyExpiryTime);
				
				try (PreparedStatement ps = con.prepareStatement(UPDATE_WAR_TIME))
				{
					ps.setLong(1, penaltyExpiryTime);
					ps.setInt(2, clanId1);
					ps.setInt(3, clanId2);
					ps.executeUpdate();
				}
			}
			else
			{
				try (PreparedStatement ps = con.prepareStatement(DELETE_WAR))
				{
					ps.setInt(1, clanId1);
					ps.setInt(2, clanId2);
					ps.executeUpdate();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete clans wars.", e);
		}
	}
	
	/**
	 * Check surrender state for a given {@link Clan}.
	 * @param clan1 : The surrendering Clan.
	 * @param clan2 : The enemy Clan.
	 */
	public void checkSurrender(Clan clan1, Clan clan2)
	{
		int count = 0;
		for (ClanMember player : clan1.getMembers())
		{
			if (player != null && player.getPlayerInstance().wantsPeace())
				count++;
		}
		
		if (count == clan1.getMembersCount() - 1)
		{
			clan1.deleteEnemyClan(clan2.getClanId());
			clan2.deleteEnemyClan(clan1.getClanId());
			deleteClansWars(clan1.getClanId(), clan2.getClanId());
		}
	}
	
	/**
	 * Restore wars and check penalties for all {@link Clan}s.
	 */
	private void restoreWars()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(DELETE_OLD_WARS))
			{
				ps.setLong(1, System.currentTimeMillis());
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement(LOAD_WARS);
				ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final int clan1 = rs.getInt("clan1");
					final int clan2 = rs.getInt("clan2");
					final long expiryTime = rs.getLong("expiry_time");
					
					if (expiryTime > 0)
						_clans.get(clan1).addWarPenaltyTime(clan2, expiryTime);
					else
					{
						_clans.get(clan1).setEnemyClan(clan2);
						_clans.get(clan2).setAttackerClan(clan1);
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore clans wars.", e);
		}
	}
	
	/**
	 * Used to check data integrity. If this {@link Clan} isn't allied, edit given properties and save it on database.
	 */
	private void allianceCheck()
	{
		_clans.values().stream().filter(c -> c.getAllyId() != 0 && c.getClanId() != c.getAllyId() && !_clans.containsKey(c.getAllyId())).forEach(c ->
		{
			c.setAllyId(0);
			c.setAllyName(null);
			c.changeAllyCrest(0, true);
			c.updateClanInDB();
		});
	}
	
	/**
	 * @param allianceId : The alliance id to check.
	 * @return a {@link List} of allied {@link Clan}s based on a alliance id, or Collections#emptyList() if empty.
	 */
	public List<Clan> getClanAllies(int allianceId)
	{
		if (allianceId == 0)
			return Collections.emptyList();
		
		return _clans.values().stream().filter(c -> c.getAllyId() == allianceId).toList();
	}
	
	/**
	 * Refresh clans ladder, picking up the 99 first best {@link Clan}s, and allocating their ranks accordingly.
	 * @param cleanupRank : If true, cleanup ranks. Used for the task, useless for startup.
	 */
	public void refreshClansLadder(boolean cleanupRank)
	{
		if (cleanupRank)
		{
			for (Clan clan : _clans.values())
				if (clan != null && clan.getRank() != 0)
					clan.setRank(0);
		}
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_RANK);
			ResultSet rs = ps.executeQuery())
		{
			int rank = 1;
			
			while (rs.next())
			{
				final Clan clan = _clans.get(rs.getInt("clan_id"));
				if (clan != null && clan.getReputationScore() > 0)
					clan.setRank(rank++);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't refresh clans ladder.", e);
		}
	}
	
	/**
	 * Clear graduates from all {@link Clan}s.
	 */
	public void clearGraduates()
	{
		_clans.values().forEach(c -> c.getGraduates().clear());
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(CLEAR_GRADUATES))
		{
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't clear graduates.", e);
		}
	}
	
	public static ClanTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanTable INSTANCE = new ClanTable();
	}
}