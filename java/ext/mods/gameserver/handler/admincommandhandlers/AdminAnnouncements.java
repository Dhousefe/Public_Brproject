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

import ext.mods.gameserver.data.xml.AdminData;
import ext.mods.gameserver.data.xml.AnnouncementData;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.CreatureSay;

public class AdminAnnouncements implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_announce",
		"admin_ann",
		"admin_say",
		"admin_gmchat"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_announce"))
		{
			try
			{
				final String[] tokens = command.split(" ", 3);
				switch (tokens[1])
				{
					case "list":
						AnnouncementData.getInstance().listAnnouncements(player);
						break;
					
					case "all", "all_auto":
						final boolean isAuto = tokens[1].equalsIgnoreCase("all_auto");
						World.getInstance().getPlayers().forEach(p -> AnnouncementData.getInstance().showAnnouncements(p, isAuto));
						
						AnnouncementData.getInstance().listAnnouncements(player);
						break;
					
					case "add":
						String[] split = tokens[2].split(" ", 2);
						boolean crit = Boolean.parseBoolean(split[0]);
						
						if (!AnnouncementData.getInstance().addAnnouncement(split[1], crit, false, -1, -1, -1))
							player.sendMessage("Invalid //announce message content ; can't be null or empty.");
						
						AnnouncementData.getInstance().listAnnouncements(player);
						break;
					
					case "add_auto":
						split = tokens[2].split(" ", 6);
						crit = Boolean.parseBoolean(split[0]);
						final boolean auto = Boolean.parseBoolean(split[1]);
						final int idelay = Integer.parseInt(split[2]);
						final int delay = Integer.parseInt(split[3]);
						final int limit = Integer.parseInt(split[4]);
						final String msg = split[5];
						
						if (!AnnouncementData.getInstance().addAnnouncement(msg, crit, auto, idelay, delay, limit))
							player.sendMessage("Invalid //announce message content ; can't be null or empty.");
						
						AnnouncementData.getInstance().listAnnouncements(player);
						break;
					
					case "del":
						AnnouncementData.getInstance().delAnnouncement(Integer.parseInt(tokens[2]));
						AnnouncementData.getInstance().listAnnouncements(player);
						break;
					
					default:
						player.sendMessage("Possible //announce parameters : <list|all|add|add_auto|del>");
						break;
				}
			}
			catch (Exception e)
			{
				sendFile(player, "announce.htm");
			}
		}
		else if (command.startsWith("admin_ann") || command.startsWith("admin_say"))
			AnnouncementData.getInstance().handleAnnounce(command, 10, command.startsWith("admin_say"));
		else if (command.startsWith("admin_gmchat"))
		{
			try
			{
				AdminData.getInstance().broadcastToGMs(new CreatureSay(player, SayType.ALLIANCE, command.substring(13)));
			}
			catch (Exception e)
			{
				player.sendMessage("Invalid //gmchat message content ; can't be null or empty.");
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}