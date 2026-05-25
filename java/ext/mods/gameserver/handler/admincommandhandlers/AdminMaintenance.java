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

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;

import ext.mods.commons.network.ServerType;
import ext.mods.commons.util.SysUtil;

import ext.mods.Config;
import ext.mods.gameserver.GameServer;
import ext.mods.gameserver.LoginServerThread;
import ext.mods.gameserver.Shutdown;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class AdminMaintenance implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_server"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (!st.hasMoreTokens())
		{
			sendHtmlForm(player);
			return;
		}
		
		try
		{
			switch (st.nextToken())
			{
				case "shutdown":
					Shutdown.getInstance().startShutdown(player, Integer.parseInt(st.nextToken()), false);
					break;
				
				case "restart":
					Shutdown.getInstance().startShutdown(player, Integer.parseInt(st.nextToken()), true);
					break;
				
				case "abort":
					Shutdown.getInstance().abort(player);
					break;
				
				case "gmonly":
					LoginServerThread.getInstance().setServerType(ServerType.GM_ONLY);
					Config.SERVER_GMONLY = true;
					
					player.sendMessage("Server is now set as GMonly.");
					break;
				
				case "all":
					LoginServerThread.getInstance().setServerType(ServerType.AUTO);
					Config.SERVER_GMONLY = false;
					
					player.sendMessage("Server isn't set as GMonly anymore.");
					break;
				
				case "max":
					final int number = Integer.parseInt(st.nextToken());
					
					LoginServerThread.getInstance().setMaxPlayer(number);
					player.sendMessage("Server maximum player amount is set to " + number + ".");
					break;
			}
		}
		catch (Exception e)
		{
			player.sendMessage("Usage: //server <shutdown|restart|abort|gmonly|all|max> time in seconds.");
		}
		sendHtmlForm(player);
	}
	
	private static void sendHtmlForm(Player player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/maintenance.htm");
		
		html.replace("%count%", World.getInstance().getPlayers().size());
		html.replace("%used%", SysUtil.getUsedMemory());
		html.replace("%server_name%", LoginServerThread.getInstance().getServerName());
		html.replace("%status%", LoginServerThread.getInstance().getServerType().getName());
		html.replace("%max_players%", LoginServerThread.getInstance().getMaxPlayers());
		html.replace("%time%", GameTimeTaskManager.getInstance().getGameTimeFormated());
		
		Instant serverStartTime = Instant.ofEpochMilli(GameServer.getInstance().getServerStartTime());
		Duration uptime = Duration.between(serverStartTime, Instant.now());
		
		html.replace("%server_start_time%", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(serverStartTime));
		html.replace("%uptime%", String.format("%d hours, %d minutes, %d seconds", uptime.toHours(), uptime.toMinutes() % 60, uptime.toSeconds() % 60));
		
		player.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}