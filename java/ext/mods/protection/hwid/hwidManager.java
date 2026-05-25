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
package ext.mods.protection.hwid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.GameClient;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class hwidManager
{
	private static final CLogger LOGGER = new CLogger(hwidManager.class.getName());
	
	public hwidManager()
	{
	}
	
	private static boolean multiboxKickTask(final Player activeChar, final Integer numberBox, final Collection<Player> world)
	{
		final Map<String, List<Player>> hwidMap = new HashMap<>();
		for (final Player player : world)
		{
			if (player.getClient() != null)
			{
				if (player.getClient().isDetached())
					continue;
				
				final String hwid = activeChar.getHWid();
				final String playerHwid = player.getHWid();
				if (!hwid.equals(playerHwid))
					continue;
				
				if (hwidMap.get(hwid) == null)
					hwidMap.put(hwid, new ArrayList<>());
				
				hwidMap.get(hwid).add(player);
				if (hwidMap.get(hwid).size() >= numberBox)
					return true;
				
				continue;
			}
		}
		return false;
	}
	
	public boolean validBox(final Player activeChar, final Integer numberBox, final Collection<Player> world, final Boolean forcedLogOut)
	{
		if (multiboxKickTask(activeChar, numberBox, world))
		{
			if (forcedLogOut)
			{
				final GameClient client = activeChar.getClient();
				if (Config.ENABLE_CONSOLE_LOG)
					LOGGER.warn("Dualbox Protection: " + client.getHWID() + " was trying to use over " + numberBox + " clients!");
				activeChar.sendMessage("SYS: You have exceeded the PC connection limit = " + Config.PROTECT_WINDOWS_COUNT + " box per PC.");
				activeChar.sendMessage("SYS: You will be disconnected in 30 seconds.");
				activeChar.setIsImmobilized(true);
				activeChar.setInvul(true);
				activeChar.disableAllSkills();
				showChatWindow(activeChar, 0);
				waitSecs(30);
				activeChar.getClient().closeNow();
			}
			return true;
		}
		return false;
	}
	
	public void showChatWindow(final Player player, final int val)
	{
		final NpcHtmlMessage msg = new NpcHtmlMessage(5);
		msg.setHtml(ExcedLimit(player));
		player.sendPacket(msg);
	}
	
	private static String ExcedLimit(final Player player)
	{
		final StringBuilder tb = new StringBuilder();
		tb.append("<html><body><center>");
		tb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>HWID<font color=LEVEL> Dual Box </font>'- Manager");
		tb.append("<br><table><tr><td height=7><img src=\"L2UI.SquareGray\" width=220 height=1></td></tr></table>");
		tb.append("<img src=\"L2UI.SquareGray\" width=295 height=1><table width=295 border=0 bgcolor=000000><tr><td align=center>");
		tb.append("<br>You have exceeded the PC connection limit.<br1>Server have limit to <font color=LEVEL>" + Config.PROTECT_WINDOWS_COUNT + "</font> per PC.<br><br>You will be disconnected in '<font color=LEVEL>30 seconds</font>'.<br1>" + player.getName() + ", Thanks for following the server rules.<br1>Thanks.<br>");
		tb.append("<br><img src=\"l2ui.squarewhite\" width=\"150\" height=\"1\"><br>");
		tb.append("<br></td></tr></table><img src=\"L2UI.SquareGray\" width=295 height=1>");
		tb.append("<table><tr><td height=7><img src=\"L2UI.SquareGray\" width=220 height=1></td></tr></table><br>");
		tb.append("<br><br><font color=000000>Respect the rules</font>");
		return tb.toString();
	}
	
	public static void waitSecs(final int i)
	{
		try
		{
			Thread.sleep(i * 1000);
		}
		catch (InterruptedException ie)
		{
			ie.printStackTrace();
		}
	}
	
	/**
	 * Verifica se dois players sao da mesma maquina (mesmo HWID).
	 * Usado pela Olympiad para impedir duelo entre dual box (farm de pontos).
	 * @param a Primeiro player.
	 * @param b Segundo player.
	 * @return true se ambos tem mesmo HWID valido (nao vazio).
	 */
	public static boolean hasSameHwid(final Player a, final Player b)
	{
		if (a == null || b == null)
			return false;
		final String h1 = a.getHWid();
		final String h2 = b.getHWid();
		return h1 != null && !h1.isEmpty() && h1.equals(h2);
	}
	
	public static final hwidManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final hwidManager INSTANCE = new hwidManager();
	}
}
