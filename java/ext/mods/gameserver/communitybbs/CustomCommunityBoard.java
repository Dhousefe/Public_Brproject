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
package ext.mods.gameserver.communitybbs;

import java.util.StringTokenizer;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.communitybbs.custom.AuctionBBSManager;
import ext.mods.gameserver.communitybbs.custom.BuffBBSManager;
import ext.mods.gameserver.communitybbs.custom.ClassMasterBBSManager;
import ext.mods.gameserver.communitybbs.custom.GateKeeperBBSManager;
import ext.mods.gameserver.communitybbs.custom.IndexCBManager;
import ext.mods.gameserver.communitybbs.custom.MissionBBSManager;
import ext.mods.gameserver.communitybbs.custom.RankingBBSManager;
import ext.mods.gameserver.communitybbs.custom.ServiceBBSManager;
import ext.mods.gameserver.communitybbs.custom.ShopBBSManager;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.GameClient;
import ext.mods.gameserver.network.SystemMessageId;

public class CustomCommunityBoard
{
	private static final CLogger LOGGER = new CLogger(CustomCommunityBoard.class.getName());
	
	protected CustomCommunityBoard()
	{
		if (!Config.ENABLE_CUSTOM_BBS)
			return;
		
		LOGGER.info("Loaded custom community board.");
	}
	
	public void handleCommands(GameClient client, String command)
	{
		final Player player = client.getPlayer();
		if (player == null)
			return;
		
		if (!Config.ENABLE_CUSTOM_BBS)
		{
			player.sendPacket(SystemMessageId.CB_OFFLINE);
			return;
		}
		
		if (!player.isGM() && (player.getCast().isCastingNow() || player.isInJail() || player.isInCombat() || player.isInDuel() || player.isInOlympiadMode() || player.isInsideZone(ZoneId.SIEGE) || player.isInsideZone(ZoneId.PVP) || player.getPvpFlag() > 0 || player.getKarma() > 0 || player.isAlikeDead()))
		{
			player.sendMessage("You can't use the Community Board right now.");
			return;
		}
		
		if (command.startsWith("_bbsgetfav_add"))
			ServiceBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbshome"))
			IndexCBManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsgetfav"))
			GateKeeperBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsloc"))
			BuffBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsclan"))
			RankingBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmemo"))
			ClassMasterBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_maillist_0_1_0_"))
			ShopBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmultisell;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			ShopBBSManager.getInstance().parseCmd("_maillist_0_1_0_;" + st.nextToken(), player);
			MultisellData.getInstance().separateAndSendCb("" + st.nextToken(), player, false);
		}
		else if (command.startsWith("_bbsexcmultisell;"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			ShopBBSManager.getInstance().parseCmd("_maillist_0_1_0_;" + st.nextToken(), player);
			MultisellData.getInstance().separateAndSendCb("" + st.nextToken(), player, true);
		}
		else if (command.startsWith("_friend"))
			AuctionBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_cbmission"))
			MissionBBSManager.getInstance().parseCmd(command, player);
		else
			BaseBBSManager.separateAndSend("<html><body><br><br><center>The command: " + command + " isn't implemented.</center></body></html>", player);
	}
	
	public static CustomCommunityBoard getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CustomCommunityBoard INSTANCE = new CustomCommunityBoard();
	}
}