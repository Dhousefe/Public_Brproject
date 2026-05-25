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
package ext.mods.dungeon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.model.actor.Player;

import ext.mods.dungeon.data.DungeonData;
import ext.mods.dungeon.enums.DungeonType;
import ext.mods.util.Tokenizer;

public class DungeonManager
{
	private static Logger log = Logger.getLogger(DungeonManager.class.getName());
	
	private final List<Dungeon> running = new CopyOnWriteArrayList<>();
	
	public void handleEnterDungeonId(Player player, Tokenizer tokenizer)
	{
		int dungeonId = tokenizer.getAsInteger(2, 0);
		if (isPlayerEligibleForDungeon(player, dungeonId))
		{
			enterDungeon(dungeonId, player);
		}
		else
		{
			int playerId = player.getObjectId();
			
			long nextJoin = getNextJoinTime(dungeonId, playerId);
			
			if (nextJoin > 0)
			{
				long currentTime = System.currentTimeMillis();
				if (currentTime < nextJoin)
				{
					long remainingTime = nextJoin - currentTime;
					long minutes = (remainingTime / (60 * 1000)) % 60;
					long seconds = (remainingTime / 1000) % 60;
					player.sendMessage(String.format("You cannot enter this dungeon yet. Remaining time: %d min %d seg", minutes, seconds));
					return;
				}
			}
			
		}
	}
	
	private void enterDungeon(int dungeonId, Player player)
	{
		DungeonTemplate template = DungeonData.getInstance().getDungeon(dungeonId);
		if (template == null)
		{
			player.sendMessage("Invalid dungeon ID.");
			return;
		}
		
		List<Player> party = new CopyOnWriteArrayList<>();
		
		if (template.type == DungeonType.PARTY)
		{
			if (!player.isInParty())
			{
				player.sendMessage("This dungeon requires a party.");
				return;
			}
			
			if (!player.getParty().isLeader(player))
			{
				player.sendMessage("Only the party leader can enter this dungeon.");
				return;
			}
			
			for (Player member : player.getParty().getMembers())
			{
				if (!isPlayerEligibleForDungeon(member, dungeonId))
				{
					player.sendMessage(member.getName() + " is not eligible for the dungeon.");
					return;
				}
				party.add(member);
			}
		}
		else
		{
			if (player.isInParty())
			{
				player.sendMessage("This dungeon is solo. Leave your party to enter.");
				return;
			}
			party.add(player);
		}
		
		int playerId = player.getObjectId();
		long currentTime = System.currentTimeMillis();
		long nextJoin = getNextJoinTime(template.id, playerId);
		
		if (nextJoin > currentTime)
		{
			long remainingTime = nextJoin - currentTime;
			long minutes = (remainingTime / (60 * 1000)) % 60;
			long seconds = (remainingTime / 1000) % 60;
			player.sendMessage(String.format("You cannot enter this dungeon yet. Remaining time: %d min %d seg", minutes, seconds));
			return;
		}
		
		Dungeon dungeon = new Dungeon(template, party);
		running.add(dungeon);
		
		for (Player member : party)
		{
			if (member == null)
				continue;
			
			savePlayerCooldown(template.id, member.getObjectId(), currentTime, template.cooldown, member.getClient().getConnection().getInetAddress().getHostAddress(), 1);
		}
		
	}
	
	private static long getNextJoinTime(int dungeonId, int playerId)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT next_join FROM dungeon_cooldowns WHERE dungeon_id = ? AND player_id = ?"))
		{
			
			ps.setInt(1, dungeonId);
			ps.setInt(2, playerId);
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					return rs.getLong("next_join");
				}
			}
		}
		catch (Exception e)
		{
			log.severe("DungeonManager: Error retrieving next join time: " + e.getMessage());
			e.printStackTrace();
		}
		return 0;
	}
	
	public synchronized void removeDungeon(Dungeon dungeon)
	{
		running.remove(dungeon);
		
	}
	
	private boolean isPlayerEligibleForDungeon(Player player, int dungeonId)
	{
		if (isInDungeon(player) || player.isInOlympiadMode())
			return false;
		
		int savedStage = getPlayerSavedStage(dungeonId, player.getObjectId());
		DungeonTemplate template = DungeonData.getInstance().getDungeon(dungeonId);
		
		if (savedStage >= template.stages.size())
		{
			long nextJoin = getNextJoinTime(dungeonId, player.getObjectId());
			return System.currentTimeMillis() >= nextJoin;
		}
		
		return true;
	}
	
	public boolean isInDungeon(Player player)
	{
		return running.stream().anyMatch(dungeon -> dungeon.getPlayers().contains(player));
	}
	
	public void updateStage(int dungeonId, int playerId, int stage)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE dungeon_cooldowns SET stage = ? WHERE dungeon_id = ? AND player_id = ?"))
		{
			ps.setInt(1, stage);
			ps.setInt(2, dungeonId);
			ps.setInt(3, playerId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			log.severe("DungeonManager: Failed to update stage: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void savePlayerCooldown(int dungeonId, int playerId, long lastJoin, long cooldownMillis, String ipAddress, int stage)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO dungeon_cooldowns (dungeon_id, player_id, last_join, next_join, ip_address, stage) " + "VALUES (?, ?, ?, ?, ?, ?) " + "ON DUPLICATE KEY UPDATE last_join = VALUES(last_join), next_join = VALUES(next_join), ip_address = VALUES(ip_address), stage = VALUES(stage)"))
		{
			
			long nextJoin = lastJoin + cooldownMillis;
			ps.setInt(1, dungeonId);
			ps.setInt(2, playerId);
			ps.setLong(3, lastJoin);
			ps.setLong(4, nextJoin);
			ps.setString(5, ipAddress);
			ps.setInt(6, stage);
			
			ps.executeUpdate();
			
		}
		catch (Exception e)
		{
			log.severe("SystemDungeonManager: Error saving cooldown: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public int getPlayerSavedStage(int dungeonId, int playerId)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT stage FROM dungeon_cooldowns WHERE dungeon_id = ? AND player_id = ?"))
		{
			ps.setInt(1, dungeonId);
			ps.setInt(2, playerId);
			
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					return rs.getInt("stage");
				}
			}
		}
		catch (Exception e)
		{
			log.severe("DungeonManager: Error retrieving saved stage: " + e.getMessage());
			e.printStackTrace();
		}
		return 1;
	}
	
	public static DungeonManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DungeonManager instance = new DungeonManager();
	}
}