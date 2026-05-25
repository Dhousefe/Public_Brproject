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

import java.util.StringTokenizer;

import ext.mods.gameserver.data.xml.ScriptData;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.event.Events;

public class AdminEvent implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_event_start",
		"admin_event_stop"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		String eventName = "";
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (st.hasMoreTokens())
			eventName = st.nextToken();
		
		if (command.startsWith("admin_event_start"))
		{
			sendFile(player, "events/default.htm");
			try
			{
				if (eventName != null)
				{
					Events event = (Events) ScriptData.getInstance().getQuest(eventName);
					if (event != null)
					{
						if (event.eventStart(1))
						{
							player.sendMessage(player.getSysString(10_116, eventName));
							return;
						}
						
						player.sendMessage(player.getSysString(10_117, eventName));
						return;
					}
				}
			}
			catch (Exception e)
			{
				player.sendMessage(player.getSysString(10_120, ": //event_start <eventname>"));
				sendFile(player, "events/default.htm");
			}
		}
		else if (command.startsWith("admin_event_stop"))
		{
			try
			{
				sendFile(player, "events/default.htm");
				if (eventName != null)
				{
					Events event = (Events) ScriptData.getInstance().getQuest(eventName);
					if (event != null)
					{
						if (event.eventStop())
						{
							player.sendMessage(player.getSysString(10_118, eventName));
							return;
						}
						player.sendMessage(player.getSysString(10_119, eventName));
						return;
					}
				}
			}
			catch (Exception e)
			{
				player.sendMessage(player.getSysString(10_120, ": //event_start <eventname>"));
				sendFile(player, "events/default.htm");
			}
		}
		
		return;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}