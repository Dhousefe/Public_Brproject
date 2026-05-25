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
package ext.mods.gameserver.taskmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.RandomZone;
import ext.mods.Crypta.RandomManager;

/**
 * Update and clear PvP flag of {@link Player}s after specified time.
 */
public final class PvpFlagTaskManager implements Runnable
{
	private final Map<Player, Long> _players = new ConcurrentHashMap<>();
	
	protected PvpFlagTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public final void run()
	{
		if (_players.isEmpty())
			return;
		
		final long currentTime = System.currentTimeMillis();
		
		for (Map.Entry<Player, Long> entry : _players.entrySet())
		{
			final Player player = entry.getKey();
			final long timeLeft = entry.getValue();
			
			final RandomZone zone = ZoneManager.getInstance().getZone(player.getX(), player.getY(), RandomZone.class);
			if (zone != null)
			{
				Object randomManager = RandomManager.getInstance();
				if (randomManager != null)
				{
					ext.mods.FarmEventRandom.holder.RandomZoneData zoneData = (ext.mods.FarmEventRandom.holder.RandomZoneData) RandomManager.getInstance().getZoneDataForZone(zone);
					if (zoneData != null && zoneData.isEnchanterZone())
					{
						if (player.isInsideZone(ZoneId.RANDOM))
						{
							if (player.getPvpFlag() == 0)
							{
								player.updatePvPFlag(1);
							}
							continue;
						}
					}
				}
				
				if (zone.isFlegZone())
				{
					if (player.isInsideZone(ZoneId.RANDOM))
					{
						continue;
					}
				}
			}
			
			if (player.isInTournament())
				continue;
			
			if (currentTime > timeLeft)
				remove(player, true);
			else if (currentTime > (timeLeft - 5000))
				player.updatePvPFlag(2);
			else
				player.updatePvPFlag(1);
		}
	}
	
	/**
	 * Add the {@link Player} set as parameter to the {@link PvpFlagTaskManager}.
	 * @param player : The {@link Player} to add.
	 * @param time : The time in ms, after which the PvP flag is removed.
	 */
	public final void add(Player player, long time)
	{
		if (player != null)
		{
			try
			{
				final RandomZone zone = ZoneManager.getInstance().getZone(player.getX(), player.getY(), RandomZone.class);
				if (zone != null && zone.isActive())
				{
					ext.mods.FarmEventRandom.holder.RandomZoneData zoneData = (ext.mods.FarmEventRandom.holder.RandomZoneData) RandomManager.getInstance().getZoneDataForZone(zone);
					if (zoneData != null && zoneData.isEnchanterZone() && player.isInsideZone(ZoneId.RANDOM))
					{
						return;
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		
		_players.put(player, System.currentTimeMillis() + time);
	}
	
	/**
	 * Remove the {@link Player} set as parameter from the {@link PvpFlagTaskManager}.
	 * @param player : The {@link Player} to remove.
	 * @param resetFlag : If true, the PvP flag is reset.
	 */
	public final void remove(Player player, boolean resetFlag)
	{
		_players.remove(player);
		
		if (resetFlag)
			player.updatePvPFlag(0);
	}
	
	public static final PvpFlagTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PvpFlagTaskManager INSTANCE = new PvpFlagTaskManager();
	}
}