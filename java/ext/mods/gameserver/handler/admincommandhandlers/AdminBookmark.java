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

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.sql.BookmarkTable;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.Bookmark;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminBookmark implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_bk",
		"admin_delbk"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		int page = 1;
		
		if (command.startsWith("admin_bk"))
		{
			if (st.hasMoreTokens())
			{
				final String param = st.nextToken();
				if (StringUtil.isDigit(param))
					page = Integer.parseInt(param);
				else
				{
					if (param.length() > 15)
					{
						player.sendMessage("The bookmark name is too long.");
						return;
					}
					
					if (BookmarkTable.getInstance().isExisting(param, player.getObjectId()))
					{
						player.sendMessage("The bookmark name already exists.");
						return;
					}
					
					BookmarkTable.getInstance().saveBookmark(param, player);
				}
			}
		}
		else if (command.startsWith("admin_delbk"))
		{
			if (!st.hasMoreTokens())
			{
				player.sendMessage("The command delbk must be followed by a valid name.");
				return;
			}
			
			final String param = st.nextToken();
			
			if (!BookmarkTable.getInstance().isExisting(param, player.getObjectId()))
			{
				player.sendMessage("That bookmark doesn't exist.");
				return;
			}
			
			BookmarkTable.getInstance().deleteBookmark(param, player.getObjectId());
		}
		showBookmarks(player, page);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	/**
	 * Show the basic HTM fed with generated data.
	 * @param player : The {@link Player} to test.
	 * @param page : The page id to show.
	 */
	private static void showBookmarks(Player player, int page)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/bk.htm");
		
		int row = 0;
		
		final Pagination<Bookmark> list = new Pagination<>(BookmarkTable.getInstance().getBookmarks(player.getObjectId()).stream(), page, PAGE_LIMIT_15);
		for (Bookmark bk : list)
		{
			list.append(((row % 2) == 0 ? "<table width=280 bgcolor=000000><tr>" : "<table width=280><tr>"));
			list.append("<td width=230><a action=\"bypass -h admin_teleport ", bk.x(), " ", bk.y(), " ", bk.z(), "\">", bk.name(), " (", bk.x(), " ", bk.y(), " ", bk.z(), ")", "</a></td><td width=50><a action=\"bypass -h admin_delbk ", bk.name(), "\">Remove</a></td>");
			list.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
			
			row++;
		}
		
		list.generateSpace(20);
		list.generatePages("bypass admin_bk %page%");
		
		html.replace("%content%", list.getContent());
		player.sendPacket(html);
	}
}