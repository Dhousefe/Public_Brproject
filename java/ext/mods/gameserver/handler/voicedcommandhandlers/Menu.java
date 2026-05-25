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
package ext.mods.gameserver.handler.voicedcommandhandlers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import ext.mods.Config;
import ext.mods.gameserver.GameServer;
import ext.mods.gameserver.data.sql.OfflineTradersTable;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class Menu implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"menu",
		"exp",
		"trade",
		"autoloot",
		"offline",
		"buffprotect",
		"lang"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (!Config.ENABLE_MENU)
		{
			player.sendMessage(player.getSysString(10_200));
			return false;
		}
		
		if (command.equals("menu"))
			showHtm(player);
		else if (command.startsWith("exp"))
		{
			player.setStopExp(!player.getStopExp());
			player.sendMessage(player.getSysString(player.getStopExp() ? 10_000 : 10_001));
		}
		else if (command.startsWith("trade"))
		{
			player.setTradeRefusal(!player.getTradeRefusal());
			player.sendMessage(player.getSysString(player.getTradeRefusal() ? 10_002 : 10_003));
		}
		else if (command.startsWith("autoloot"))
		{
			player.setAutoLoot(!player.getAutoLoot());
			player.sendMessage(player.getSysString(player.getAutoLoot() ? 10_004 : 10_005));
		}
		else if (command.startsWith("buffprotect"))
		{
			player.setBuffProtected(!player.isBuffProtected());
			player.sendMessage(player.getSysString(player.isBuffProtected() ? 10186 : 10187));
		}
		else if (command.startsWith("offline"))
		{
			if (!OfflineTradersTable.offlineMode(player))
			{
				player.sendMessage(player.getSysString(10_006));
				return false;
			}
			
			if ((player.isInStoreMode() && Config.OFFLINE_TRADE_ENABLE) || (player.isCrafting() && Config.OFFLINE_CRAFT_ENABLE))
			{
				player.sendMessage(player.getSysString(10_007));
				player.logout(false);
				return true;
			}
			
			OfflineTradersTable.getInstance().saveOfflineTraders(player);
		}
		else if (command.startsWith("lang") && command.length() > 5)
			player.setLocale(Locale.forLanguageTag(command.substring(5).trim()));
		
		showHtm(player);
		return true;
	}
	
	private void showHtm(Player player)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile(player.getLocale(), "html/mods/menu/menu.htm");
		
		final String ACTIVATED = "<font color=00FF00>" + player.getSysString(10_008) + "</font>";
		final String DEACTIVATED = "<font color=FF0000>" + player.getSysString(10_009) + "</font>";
		
		TimeZone timeZone = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar currentTime = Calendar.getInstance(timeZone);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(Config.DATE_FORMAT, player.getLocale());
		dateFormat.setTimeZone(timeZone);
		String formattedTime = dateFormat.format(currentTime.getTime());
		String lastRestart = dateFormat.format(GameServer.getInstance().getServerStartTime());
		
		htm.replace("%online%", player.isInStoreMode() ? 0 : World.getInstance().getOnlinePlayerCount() * Config.FAKE_ONLINE_AMOUNT);
		htm.replace("%gainexp%", player.getStopExp() ? ACTIVATED : DEACTIVATED);
		htm.replace("%trade%", player.getTradeRefusal() ? ACTIVATED : DEACTIVATED);
		htm.replace("%autoloot%", player.getAutoLoot() ? ACTIVATED : DEACTIVATED);
		htm.replace("%buffprotect%", player.isBuffProtected() ? ACTIVATED : DEACTIVATED);
		htm.replace("%button%", player.getStopExp() ? "value=" + player.getSysString(10_009) + " action=\"bypass voiced_exp\"" : "value=" + player.getSysString(10_008) + " action=\"bypass voiced_exp\"");
		htm.replace("%button1%", player.getTradeRefusal() ? "value=" + player.getSysString(10_009) + " action=\"bypass voiced_trade\"" : "value=" + player.getSysString(10_008) + " action=\"bypass voiced_trade\"");
		htm.replace("%button2%", player.getAutoLoot() ? "value=" + player.getSysString(10_009) + " action=\"bypass voiced_autoloot\"" : "value=" + player.getSysString(10_008) + " action=\"bypass voiced_autoloot\"");
		htm.replace("%button3%", player.isBuffProtected() ? "value=" + player.getSysString(10_009) + " action=\"bypass voiced_buffprotect\"" : "value=" + player.getSysString(10_008) + " action=\"bypass voiced_buffprotect\"");
		htm.replace("%serverTime%", formattedTime);
		htm.replace("%lastRestart%", lastRestart);
		htm.replace("%trader%", World.getInstance().getTraderCount());
		htm.replace("%maxOnline%", World.getInstance().getMaxOnline());
		
		player.sendPacket(htm);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}