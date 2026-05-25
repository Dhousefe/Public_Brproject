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
package ext.mods.gameserver.model.zone.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ConnectionPool;

import ext.mods.Config;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A Boss zone, extending {@link ZoneType}. It holds a {@link List} and a {@link Map} of allowed {@link Player}s.<br>
 * <br>
 * The Map is used for Players disconnections, while the List is used for Players to re-enter the zone after server downtime/restart.
 */
public class BossZone extends ZoneType
{
	private static final String SELECT_GRAND_BOSS_LIST = "SELECT * FROM grandboss_list WHERE zone = ?";
	
	private final Map<Integer, Long> _allowedPlayersEntryTime = new ConcurrentHashMap<>();
	
	private final Set<Integer> _allowedPlayers = ConcurrentHashMap.newKeySet();
	
	private final int[] _oustLoc = new int[3];
	
	private int _invadeTime;
	
	public BossZone(int id)
	{
		super(id);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_GRAND_BOSS_LIST))
		{
			ps.setInt(1, id);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
					allowPlayerEntry(rs.getInt("player_id"));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load players for {}.", e, toString());
		}
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("InvadeTime"))
			_invadeTime = Integer.parseInt(value);
		else if (name.equals("oustX"))
			_oustLoc[0] = Integer.parseInt(value);
		else if (name.equals("oustY"))
			_oustLoc[1] = Integer.parseInt(value);
		else if (name.equals("oustZ"))
			_oustLoc[2] = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		creature.setInsideZone(ZoneId.BOSS, true);
		
		if (creature instanceof Player player)
		{
			player.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
			
			if (player.isGM() || _invadeTime == 0 || Config.ALLOW_GRAND_BOSSES_TELEPORT)
				return;
			
			final int id = player.getObjectId();
			
			if (_allowedPlayers.contains(id))
			{
				final long entryTime = _allowedPlayersEntryTime.remove(id);
				if (entryTime > System.currentTimeMillis())
					return;
				
				_allowedPlayers.remove(Integer.valueOf(id));
			}
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		creature.setInsideZone(ZoneId.BOSS, false);
		
		if (creature instanceof Playable)
		{
			if (creature instanceof Player player)
			{
				player.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
				
				if (player.isGM() || _invadeTime == 0)
					return;
				
				final int id = player.getObjectId();
				
				if (_allowedPlayers.contains(id))
				{
					if (!player.isOnline())
					{
						_allowedPlayersEntryTime.put(id, System.currentTimeMillis() + _invadeTime);
					}
					else
					{
						if (_allowedPlayersEntryTime.containsKey(id))
							return;
						
						_allowedPlayers.remove(Integer.valueOf(id));
					}
				}
			}
			
			if (!_creatures.isEmpty())
			{
				if (!getKnownTypeInside(Playable.class).isEmpty())
					return;
				
				for (Attackable raid : getKnownTypeInside(Attackable.class))
				{
					if (!raid.isRaidRelated())
						continue;
					
					raid.returnHome();
				}
			}
		}
		else if (creature instanceof Attackable attackable && attackable.isRaidRelated())
			attackable.returnHome();
	}
	
	/**
	 * Enables the entry of a {@link Player} to this {@link BossZone} for next "duration" seconds. If the Player tries to enter the zone after this period, he will be teleported out.
	 * @param player : The allowed player to entry.
	 * @param duration : The entry permission period (in seconds).
	 */
	public void allowPlayerEntry(Player player, int duration)
	{
		final int playerId = player.getObjectId();
		
		if (!_allowedPlayers.contains(playerId))
			_allowedPlayers.add(playerId);
		
		_allowedPlayersEntryTime.put(playerId, System.currentTimeMillis() + duration * 1000);
	}
	
	/**
	 * Enables the entry of a {@link Player} to this {@link BossZone} after server shutdown/restart. The time limit is specified by each zone via "InvadeTime" parameter. If the player tries to enter the zone after this period, he will be teleported out.
	 * @param playerId : The objectid of the allowed player to entry.
	 */
	public void allowPlayerEntry(int playerId)
	{
		if (!_allowedPlayers.contains(playerId))
			_allowedPlayers.add(playerId);
		
		_allowedPlayersEntryTime.put(playerId, System.currentTimeMillis() + _invadeTime);
	}
	
	/**
	 * Removes the {@link Player} from allowed list and cancel the entry permition.
	 * @param player : Player to remove from the zone.
	 */
	public void removePlayer(Player player)
	{
		final int id = player.getObjectId();
		
		_allowedPlayers.remove(Integer.valueOf(id));
		
		_allowedPlayersEntryTime.remove(id);
	}
	
	/**
	 * @return the {@link Set} of all allowed {@link Player}s objectIds.
	 */
	public Set<Integer> getAllowedPlayers()
	{
		return _allowedPlayers;
	}
	
	/**
	 * Teleport all {@link Player}s located in this {@link BossZone} to a specific location, as listed on {@link #_oustLoc}. Clear both containers holding {@link Player}s informations.
	 * @return the {@link List} of all {@link Player}s who have been forced to teleport.
	 */
	public List<Player> oustAllPlayers()
	{
		final List<Player> players = getKnownTypeInside(Player.class);
		if (players.isEmpty())
			return players;
		
		for (Player player : players)
		{
			if (player.isOnline())
			{
				if (_oustLoc[0] != 0 && _oustLoc[1] != 0 && _oustLoc[2] != 0)
					player.teleportTo(_oustLoc[0], _oustLoc[1], _oustLoc[2], 0);
				else
					player.teleportTo(RestartType.TOWN);
			}
		}
		_allowedPlayersEntryTime.clear();
		_allowedPlayers.clear();
		
		return players;
	}
}