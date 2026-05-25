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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;

public class RegionBBSManager extends BaseBBSManager
{
	protected RegionBBSManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.equals("_bbsloc"))
			showRegionsList(player);
		else if (command.startsWith("_bbsloc"))
		{
			final StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			
			showRegion(player, Integer.parseInt(st.nextToken()));
		}
		else
			super.parseCmd(command, player);
	}
	
	@Override
	protected String getFolder()
	{
		return "region/";
	}
	
	private static void showRegionsList(Player player)
	{
		final String content = HTMLData.getInstance().getHtm(player, CB_PATH + "region/castlelist.htm");
		
		final StringBuilder sb = new StringBuilder(500);
		for (Castle castle : CastleManager.getInstance().getCastles())
		{
			final Clan owner = ClanTable.getInstance().getClan(castle.getOwnerId());
			
			StringUtil.append(sb, "<table><tr><td width=5></td><td width=160><a action=\"bypass _bbsloc;", castle.getId(), "\">", castle.getName(), "</a></td><td width=160>", ((owner != null) ? "<a action=\"bypass _bbsclan;home;" + owner.getClanId() + "\">" + owner.getName() + "</a>" : player.getSysString(10_174)), "</td><td width=160>", ((owner != null && owner.getAllyId() > 0) ? owner.getAllyName() : player.getSysString(10_174)), "</td><td width=120>", ((owner != null) ? castle.getCurrentTaxPercent() : "0"), "</td><td width=5></td></tr></table><br1><img src=\"L2UI.Squaregray\" width=605 height=1><br1>");
		}
		separateAndSend(content.replace("%castleList%", sb.toString()), player);
	}
	
	private static void showRegion(Player player, int castleId)
	{
		final Castle castle = CastleManager.getInstance().getCastleById(castleId);
		final Clan owner = ClanTable.getInstance().getClan(castle.getOwnerId());
		
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "region/castle.htm");
		
		content = content.replace("%castleName%", castle.getName());
		content = content.replace("%tax%", Integer.toString(castle.getCurrentTaxPercent()));
		content = content.replace("%lord%", ((owner != null) ? owner.getLeaderName() : player.getSysString(10_174)));
		content = content.replace("%clanName%", ((owner != null) ? "<a action=\"bypass _bbsclan;home;" + owner.getClanId() + "\">" + owner.getName() + "</a>" : player.getSysString(10_174)));
		content = content.replace("%allyName%", ((owner != null && owner.getAllyId() > 0) ? owner.getAllyName() : player.getSysString(10_174)));
		content = content.replace("%siegeDate%", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(castle.getSiegeDate().getTimeInMillis()));
		
		final StringBuilder sb = new StringBuilder(200);
		
		final List<ClanHall> clanHalls = ClanHallManager.getInstance().getClanHallsByLocation(castle.getName());
		if (clanHalls != null && !clanHalls.isEmpty())
		{
			sb.append("<br><br><table width=610 bgcolor=A7A19A><tr><td width=5></td><td width=200>Clan Hall Name</td><td width=200>Owning Clan</td><td width=200>Clan Leader Name</td><td width=5></td></tr></table><br1>");
			
			for (ClanHall ch : clanHalls)
			{
				final Clan chOwner = ClanTable.getInstance().getClan(ch.getOwnerId());
				
				StringUtil.append(sb, "<table><tr><td width=5></td><td width=200>", ch.getName(), "</td><td width=200>", ((chOwner != null) ? "<a action=\"bypass _bbsclan;home;" + chOwner.getClanId() + "\">" + chOwner.getName() + "</a>" : player.getSysString(10_174)), "</td><td width=200>", ((chOwner != null) ? chOwner.getLeaderName() : player.getSysString(10_174)), "</td><td width=5></td></tr></table><br1><img src=\"L2UI.Squaregray\" width=605 height=1><br1>");
			}
		}
		separateAndSend(content.replace("%hallsList%", sb.toString()), player);
	}
	
	public static RegionBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RegionBBSManager INSTANCE = new RegionBBSManager();
	}
}