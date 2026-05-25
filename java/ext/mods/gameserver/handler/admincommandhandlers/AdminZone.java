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

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.restart.RestartPoint;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminZone implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_zone"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (!st.hasMoreTokens())
		{
			showHtml(player);
			return;
		}
		
		switch (st.nextToken().toLowerCase())
		{
			case "show":
				try
				{
					final ExServerPrimitive debug = player.getDebugPacket("ZONE");
					debug.reset();
					
					final String param = st.nextToken().toLowerCase();
					switch (param)
					{
						case "all":
							for (ZoneType zone : player.getZones(false))
								zone.visualizeZone(debug);
							
							debug.sendTo(player);
							
							showHtml(player);
							break;
						
						case "clear":
							debug.sendTo(player);
							
							showHtml(player);
							break;
						
						default:
							ZoneManager.getInstance().getZoneById(Integer.parseInt(param)).visualizeZone(debug);
							
							debug.sendTo(player);
							break;
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Invalid parameter for //zone show.");
				}
				break;
			
			default:
				showHtml(player);
				break;
		}
	}
	
	private static void showHtml(Player player)
	{
		int x = player.getX();
		int y = player.getY();
		int rx = (x - World.WORLD_X_MIN) / World.TILE_SIZE + World.TILE_X_MIN;
		int ry = (y - World.WORLD_Y_MIN) / World.TILE_SIZE + World.TILE_Y_MIN;
		
		final RestartPoint currentRp = RestartPointData.getInstance().getRestartPoint(player);
		final RestartPoint currentCrp = RestartPointData.getInstance().getCalculatedRestartPoint(player);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/zone.htm");
		html.replace("%GEOREGION%", rx + "_" + ry);
		html.replace("%RA%", RestartPointData.getInstance().getRestartArea(player) != null);
		html.replace("%CALCULATED_RP%", (currentCrp == null) ? "N/A" : currentCrp.getName());
		html.replace("%CURRENT_RP%", (currentRp == null) ? "N/A" : currentRp.getName());
		html.replace("%CURRENTLOC%", x + ", " + y + ", " + player.getZ());
		
		final StringBuilder sb = new StringBuilder(100);
		
		for (ZoneId zoneId : ZoneId.VALUES)
		{
			if (player.isInsideZone(zoneId))
				StringUtil.append(sb, zoneId, "<br1>");
		}
		html.replace("%ZONES%", sb.toString());
		
		sb.setLength(0);
		
		for (ZoneType zoneType : World.getInstance().getRegion(x, y).getZones())
		{
			if (zoneType.isInZone(player))
				StringUtil.append(sb, zoneType.getId(), " ");
		}
		html.replace("%ZLIST%", sb.toString());
		player.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}