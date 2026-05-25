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
package ext.mods.gameserver.communitybbs.custom;

import java.util.StringTokenizer;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.custom.data.DonateData;
import ext.mods.gameserver.custom.data.DonateData.Donate;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.gameserver.model.actor.Player;

public class ServiceBBSManager extends BaseBBSManager
{
	@Override
	public void parseCmd(String command, Player player)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String action = st.nextToken();
		switch (action)
		{
			case "_bbsgetfav_add" -> {
				showPage("index", player);
			}
			
			case "_bbsgetfav_add;page" -> {
				String page = st.nextToken();
				showPage(page, player);
			}
			
			case "_bbsgetfav_add;nobles" -> {
				showPage("character", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setNobless(player, service);
			}
			
			case "_bbsgetfav_add;hero" -> {
				showPage("character", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setHero(player, service);
			}
			
			case "_bbsgetfav_add;setnamecolor" -> {
				showPage("colorname", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				if (!st.hasMoreTokens())
					return;
				
				DonateData.setNameColor(player, service, st.nextToken());
			}
			
			case "_bbsgetfav_add;settitlecolor" -> {
				showPage("colortitle", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				if (!st.hasMoreTokens())
					return;
				
				DonateData.setTitleColor(player, service, st.nextToken());
			}
			
			case "_bbsgetfav_add;setname" -> {
				showPage("name", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				if (!st.hasMoreTokens())
					return;
				
				DonateData.setName(player, service, st.nextToken());
			}
			
			case "_bbsgetfav_add;premium" -> {
				showPage("premium", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setPremium(player, service);
			}
			
			case "_bbsgetfav_add;gender" -> {
				showPage("character", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setGender(player, service);
			}
			
			case "_bbsgetfav_add;nullpk" -> {
				showPage("character", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.clearPK(player, service);
			}
			
			case "_bbsgetfav_add;clanlvl" -> {
				showPage("clan", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.setClanLevel(player, service);
			}
			
			case "_bbsgetfav_add;clanskill" -> {
				showPage("clan", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.addClanSkill(player, service);
			}
			case "_bbsgetfav_add;clanrep" -> {
				showPage("clan", player);
				final var service = scanService(st);
				if (!checkService(service, player, command))
					return;
				
				DonateData.addClanRep(player, service);
			}
			
			case "_bbsgetfav_add;multisell" -> {
				String[] args = command.split(" ");
				if (args.length < 2)
					return;
				
				MultisellData.getInstance().separateAndSendCb(args[1], player, false);
			}
		}
	}
	
	private void showPage(String page, Player player)
	{
		String content = HTMLData.getInstance().getHtm(player.getLocale(), CB_PATH + getFolder() + page + ".htm");
		content = content.replace("%name%", player.getName());
		separateAndSend(content, player);
	}
	
	public static Donate scanService(StringTokenizer st)
	{
		return DonateData.getInstance().getDonate(Integer.parseInt(st.nextToken()));
	}
	
	public static boolean checkService(Donate donate, Player pc, String command)
	{
		if (donate != null)
			return true;
		
		LOGGER.info("pc[{}] use missing service[{}]", pc.getName(), command);
		return false;
	}
	
	@Override
	protected String getFolder()
	{
		return "custom/donate/";
	}
	
	public static ServiceBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ServiceBBSManager INSTANCE = new ServiceBBSManager();
	}
}