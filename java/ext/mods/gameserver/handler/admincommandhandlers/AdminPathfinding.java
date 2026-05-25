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
package ext.mods.gameserver.handler.admincommandhandlers;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.geoengine.PathfindingCache;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;

/**
 * Comandos admin para monitorar e controlar o sistema de pathfinding.
 */
public class AdminPathfinding implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_pathfinding",
		"admin_pf_stats",
		"admin_pf_clear"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.equals("admin_pathfinding") || command.equals("admin_pf_stats"))
		{
			showPathfindingStats(player);
		}
		else if (command.equals("admin_pf_clear"))
		{
			PathfindingCache.clearCache();
			player.sendMessage("✅ Cache de pathfinding limpo com sucesso!");
			showPathfindingStats(player);
		}
	}
	
	private void showPathfindingStats(Player player)
	{
		player.sendMessage("=== PATHFINDING STATS ===");
		player.sendMessage(" ");
		
		player.sendMessage("📊 " + PathfindingCache.getStats());
		player.sendMessage("   └─ Hit Rate: " + String.format("%.1f%%", PathfindingCache.getHitRate() * 100));
		player.sendMessage(" ");
		
		player.sendMessage("🔧 Pathfinding ThreadPool:");
		player.sendMessage("   ├─ Threads Ativas: " + ThreadPool.getPathfindingActiveCount());
		player.sendMessage("   └─ Fila de Espera: " + ThreadPool.getPathfindingQueueSize());
		player.sendMessage(" ");
		
		double hitRate = PathfindingCache.getHitRate();
		if (hitRate < 0.3)
		{
			player.sendMessage("⚠️ AVISO: Hit rate baixo (<30%)");
			player.sendMessage("   Considere aumentar PathfindingCacheSize ou PathfindingCacheExpiration");
		}
		else if (hitRate > 0.7)
		{
			player.sendMessage("✅ Cache funcionando bem (>70% hit rate)");
		}
		
		int queueSize = ThreadPool.getPathfindingQueueSize();
		if (queueSize > 50)
		{
			player.sendMessage("⚠️ AVISO: Fila de pathfinding alta (" + queueSize + ")");
			player.sendMessage("   Considere aumentar PathfindingThreads em geoengine.properties");
		}
		
		player.sendMessage(" ");
		player.sendMessage("Use //pf_clear para limpar o cache");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}

