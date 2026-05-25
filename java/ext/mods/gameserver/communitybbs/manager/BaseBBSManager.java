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
package ext.mods.gameserver.communitybbs.manager;

import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.logging.CLogger;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.ShowBoard;

public abstract class BaseBBSManager
{
	protected static final CLogger LOGGER = new CLogger(BaseBBSManager.class.getName());
	
	protected static final String CB_PATH = "html/CommunityBoard/";
	
	public void parseCmd(String command, Player player)
	{
		separateAndSend("<html><body><br><br><center>The command: " + command + " isn't implemented.</center></body></html>", player);
	}
	
	public void parseWrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player player)
	{
		separateAndSend("<html><body><br><br><center>The command: " + ar1 + " isn't implemented.</center></body></html>", player);
	}
	
	public static void separateAndSend(String html, Player player)
	{
		if (html == null || player == null)
			return;
		
		if (html.length() < 4090)
		{
			player.sendPacket(new ShowBoard(html, "101"));
			player.sendPacket(ShowBoard.STATIC_SHOWBOARD_102);
			player.sendPacket(ShowBoard.STATIC_SHOWBOARD_103);
		}
		else if (html.length() < 8180)
		{
			player.sendPacket(new ShowBoard(html.substring(0, 4090), "101"));
			player.sendPacket(new ShowBoard(html.substring(4090, html.length()), "102"));
			player.sendPacket(ShowBoard.STATIC_SHOWBOARD_103);
		}
		else if (html.length() < 12270)
		{
			player.sendPacket(new ShowBoard(html.substring(0, 4090), "101"));
			player.sendPacket(new ShowBoard(html.substring(4090, 8180), "102"));
			player.sendPacket(new ShowBoard(html.substring(8180, html.length()), "103"));
		}
	}
	
	protected static void send1001(String html, Player player)
	{
		if (html.length() < 8180)
			player.sendPacket(new ShowBoard(html, "1001"));
	}
	
	protected static void send1002(Player player)
	{
		send1002(player, " ", " ", "0");
	}
	
	protected static void send1002(Player player, String string, String string2, String string3)
	{
		final List<String> params = new ArrayList<>();
		params.add("0");
		params.add("0");
		params.add("0");
		params.add("0");
		params.add("0");
		params.add("0");
		params.add(player.getName());
		params.add(Integer.toString(player.getObjectId()));
		params.add(player.getAccountName());
		params.add("9");
		params.add(string2);
		params.add(string2);
		params.add(string);
		params.add(string3);
		params.add(string3);
		params.add("0");
		params.add("0");
		
		player.sendPacket(new ShowBoard(params));
	}
	
	/**
	 * Loads an HTM located in the default CB path.
	 * @param file : the file to load.
	 * @param player : the requester.
	 */
	protected void loadStaticHtm(String file, Player player)
	{
		separateAndSend(HTMLData.getInstance().getHtm(player, CB_PATH + getFolder() + file), player);
	}
	
	/**
	 * That method is overidden in every board type. It allows to switch of folders following the board.
	 * @return the folder.
	 */
	protected String getFolder()
	{
		return "";
	}
}