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

import java.util.List;

import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

import ext.mods.fakeplayer.FakePlayer;
import ext.mods.fakeplayer.FakePlayerManager;
import ext.mods.fakeplayer.FakePlayerTaskManager;

public class AdminCommand implements IAdminCommandHandler
{
	private final String fakesFolder = "fakeplayers/";
	private static final int PAGE_SIZE = 10;
	private static final int MAX_VISIBLE_PAGES = 3;
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_fakes",
		"admin_spawnrandom",
		"admin_spawnrandomgrupe",
		"admin_deletefake",
		"admin_findfake"
	
	};
	
	private void showFakeDashboard(Player admin, int page)
	{
		List<FakePlayer> allFakes = FakePlayerManager.getInstance().getFakePlayers();
		int total = allFakes.size();
		
		if (total == 0)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(admin.getLocale(), "html/admin/" + fakesFolder + "index.htm");
			html.replace("%fakecount%", "0");
			html.replace("%taskcount%", FakePlayerTaskManager.getInstance().getTaskCount());
			html.replace("%list%", "");
			html.replace("%pages%", "");
			admin.sendPacket(html);
			return;
		}
		
		int totalPages = (int) Math.ceil(total / (double) PAGE_SIZE);
		page = Math.max(1, Math.min(page, totalPages));
		int start = (page - 1) * PAGE_SIZE;
		int end = Math.min(start + PAGE_SIZE, total);
		
		StringBuilder list = new StringBuilder("<table width=300>");
		for (int i = start; i < end; i++)
		{
			FakePlayer fake = allFakes.get(i);
			list.append("<tr><td align=left width=300><font color=\"LEVEL\">" + fake.getName() + "</font></td></tr>");
		}
		list.append("</table>");
		
		StringBuilder pages = new StringBuilder();
		pages.append("<table width=150><tr>");
		
		int half = MAX_VISIBLE_PAGES / 2;
		int startPage = Math.max(1, page - half);
		int endPage = Math.min(totalPages, startPage + MAX_VISIBLE_PAGES - 1);
		if (endPage - startPage < MAX_VISIBLE_PAGES - 1)
			startPage = Math.max(1, endPage - MAX_VISIBLE_PAGES + 1);
		
		if (startPage > 1)
		{
			pages.append("<td><button value=\"1\" action=\"bypass -h admin_fakes 1\" width=32 height=16 back=\"sek.cbui94\" fore=\"sek.cbui92\"></button></td>");
		}
		
		for (int i = startPage; i <= endPage; i++)
		{
			if (i == page)
			{
				pages.append("<td><button value=\"" + i + "\" action=\"\" width=32 height=16 back=\"sek.cbui92\" fore=\"sek.cbui94\"></button></td>");
			}
			else
			{
				pages.append("<td><button value=\"" + i + "\" action=\"bypass -h admin_fakes " + i + "\" width=32 height=16 back=\"sek.cbui94\" fore=\"sek.cbui92\"></button></td>");
			}
		}
		
		if (endPage < totalPages)
		{
			pages.append("<td><button value=\"" + totalPages + "\" action=\"bypass -h admin_fakes " + totalPages + "\" width=32 height=16 back=\"sek.cbui94\" fore=\"sek.cbui92\"></button></td>");
		}
		
		pages.append("</tr></table>");
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(admin.getLocale(), "html/admin/" + fakesFolder + "index.htm");
		html.replace("%fakecount%", String.valueOf(total));
		html.replace("%taskcount%", FakePlayerTaskManager.getInstance().getTaskCount());
		html.replace("%list%", list.toString());
		html.replace("%pages%", pages.toString());
		admin.sendPacket(html);
	}
	
	@Override
	public void useAdminCommand(String command, Player admin)
	{
		if (command.startsWith("admin_fakes"))
		{
			try
			{
				String[] parts = command.split(" ");
				
				if (parts.length < 2)
				{
					showFakeDashboard(admin, 1);
				}
				else
				{
					int page = Integer.parseInt(parts[1]);
					showFakeDashboard(admin, page);
				}
				
			}
			catch (Exception e)
			{
				showFakeDashboard(admin, 1);
			}
			
		}
		
		if (command.startsWith("admin_deletefake"))
		{
			if (admin.getTarget() != null && admin.getTarget() instanceof FakePlayer)
			{
				FakePlayer fakePlayer = (FakePlayer) admin.getTarget();
				fakePlayer.despawnPlayer();
			}
			showFakeDashboard(admin, 1);
		}
		
		if (command.startsWith("admin_spawnrandom"))
		{
			FakePlayerManager.getInstance().spawnPlayer(admin.getX(), admin.getY(), admin.getZ());
			
			showFakeDashboard(admin, 1);
		}
		
		if (command.startsWith("admin_spawnrandomgrupe"))
		{
			FakePlayerManager.getInstance().spawnPlayerGroup(admin.getX(), admin.getY(), admin.getZ(), 8);
			showFakeDashboard(admin, 1);
		}
		
		if (command.startsWith("admin_findfake"))
		{
			String[] params = command.split(" ");
			if (params.length < 2)
			{
				admin.sendMessage("Uso correto: //admin_findfake <nome>");
			}
			else
			{
				String fakeName = params[1];
				FakePlayer targetFake = null;
				
				for (FakePlayer fake : FakePlayerManager.getInstance().getFakePlayers())
				{
					if (fake.getName().equalsIgnoreCase(fakeName))
					{
						targetFake = fake;
						break;
					}
				}
				
				if (targetFake != null)
				{
					admin.teleportTo(targetFake.getX(), targetFake.getY(), targetFake.getZ(), 75);
					admin.sendMessage("Teletransportado para FakePlayer: " + fakeName);
				}
				else
				{
					admin.sendMessage("FakePlayer " + fakeName + " não encontrado.");
				}
			}
			showFakeDashboard(admin, 1);
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
}
