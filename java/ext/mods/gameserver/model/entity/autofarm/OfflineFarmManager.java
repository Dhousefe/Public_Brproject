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
package ext.mods.gameserver.model.entity.autofarm;

import java.util.concurrent.ConcurrentHashMap;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Player;

/**
 * Offline Farm Manager - manages offline farming for players
 * Handles player state, routine management, and system lifecycle
 */
public class OfflineFarmManager
{
	private final ConcurrentHashMap<Player, OfflineFarmRoutine> _offlinePlayers = new ConcurrentHashMap<>();
	
	public boolean startOfflineFarm(Player player)
	{
		if (player == null || player.isOfflineFarm())
			return false;
		
		final OfflineFarmRoutine routine = new OfflineFarmRoutine(player);
		_offlinePlayers.put(player, routine);
		player.startOfflineFarm();
		routine.start();
		
		return true;
	}
	
	public void stopOfflineFarm(Player player)
	{
		if (player == null)
			return;
		
		final OfflineFarmRoutine routine = _offlinePlayers.remove(player);
		if (routine != null)
			routine.stop();
		
		player.stopOfflineFarm();
	}
	
	public void onPlayerLogout(Player player)
	{
		if (player != null && player.isOfflineFarm())
			stopOfflineFarm(player);
	}
	
	public void onPlayerDeath(Player player)
	{
		if (player != null && player.isOfflineFarm())
		{
			final AutoFarmProfile profile = AutoFarmManager.getInstance().getPlayer(player.getObjectId());
			if (profile != null && profile.isEnabled() && profile.isDeathReturnEnabled())
				return;
			
			final OfflineFarmRoutine routine = _offlinePlayers.get(player);
			if (routine != null)
			{
				routine.respawnAndRestoreBuffs();
			}
			
			if (Config.OFFLINE_FARM_LOGOUT_ON_DEATH)
			{
			}
		}
	}
	
	public static OfflineFarmManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OfflineFarmManager INSTANCE = new OfflineFarmManager();
	}
}