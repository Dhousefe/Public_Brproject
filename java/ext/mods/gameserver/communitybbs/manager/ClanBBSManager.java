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

import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanMember;
import ext.mods.gameserver.network.SystemMessageId;

public class ClanBBSManager extends BaseBBSManager
{
	protected ClanBBSManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.equalsIgnoreCase("_bbsclan"))
		{
			if (player.getClan() == null)
				sendClanList(player, 1);
			else
				sendClanDetails(player, player.getClan().getClanId());
		}
		else if (command.startsWith("_bbsclan"))
		{
			final StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			
			final String clanCommand = st.nextToken();
			if (clanCommand.equalsIgnoreCase("clan"))
				sendClanList(player, Integer.parseInt(st.nextToken()));
			else if (clanCommand.equalsIgnoreCase("home"))
				sendClanDetails(player, Integer.parseInt(st.nextToken()));
			else if (clanCommand.equalsIgnoreCase("mail"))
				sendClanMail(player, Integer.parseInt(st.nextToken()));
			else if (clanCommand.equalsIgnoreCase("management"))
				sendClanManagement(player, Integer.parseInt(st.nextToken()));
			else if (clanCommand.equalsIgnoreCase("notice"))
			{
				if (st.hasMoreTokens())
				{
					final String noticeCommand = st.nextToken();
					if (!noticeCommand.isEmpty() && player.getClan() != null)
						player.getClan().setNotice(Boolean.parseBoolean(noticeCommand));
				}
				sendClanNotice(player, player.getClanId());
			}
		}
		else
			super.parseCmd(command, player);
	}
	
	@Override
	public void parseWrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player player)
	{
		if (ar1.equalsIgnoreCase("intro"))
		{
			if (Integer.valueOf(ar2) != player.getClanId())
				return;
			
			final Clan clan = ClanTable.getInstance().getClan(player.getClanId());
			if (clan == null)
				return;
			
			clan.setIntroduction(ar3, true);
			sendClanManagement(player, Integer.valueOf(ar2));
		}
		else if (ar1.equals("notice"))
		{
			final Clan clan = player.getClan();
			if (clan != null)
			{
				clan.setNotice(ar4);
				sendClanNotice(player, player.getClanId());
			}
		}
		else if (ar1.equalsIgnoreCase("mail"))
		{
			if (Integer.valueOf(ar2) != player.getClanId())
				return;
			
			final Clan clan = ClanTable.getInstance().getClan(player.getClanId());
			if (clan == null)
				return;
			
			final StringBuilder members = new StringBuilder();
			
			for (ClanMember member : clan.getMembers())
			{
				if (members.length() > 0)
					members.append(";");
				
				members.append(member.getName());
			}
			MailBBSManager.getInstance().sendMail(members.toString(), ar4, ar5, player);
			sendClanDetails(player, player.getClanId());
		}
		else
			super.parseWrite(ar1, ar2, ar3, ar4, ar5, player);
	}
	
	@Override
	protected String getFolder()
	{
		return "clan/";
	}
	
	private static void sendClanMail(Player player, int clanId)
	{
		final Clan clan = ClanTable.getInstance().getClan(clanId);
		if (clan == null)
			return;
		
		if (player.getClanId() != clanId || !player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			sendClanList(player, 1);
			return;
		}
		
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "clan/clanhome-mail.htm");
		content = content.replace("%clanid%", Integer.toString(clanId));
		content = content.replace("%clanName%", clan.getName());
		separateAndSend(content, player);
	}
	
	private static void sendClanManagement(Player player, int clanId)
	{
		final Clan clan = ClanTable.getInstance().getClan(clanId);
		if (clan == null)
			return;
		
		if (player.getClanId() != clanId || !player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_IS_ENABLED);
			sendClanList(player, 1);
			return;
		}
		
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "clan/clanhome-management.htm");
		content = content.replace("%clanid%", Integer.toString(clan.getClanId()));
		content = content.replace("%curAnnNonPer%", clan.getAnnBoard().getAccess().getDesc());
		content = content.replace("%curAnnMemPer%", clan.getAnnBoard().getAccess().getDesc());
		content = content.replace("%curCbbNonPer%", clan.getCbbBoard().getAccess().getDesc());
		content = content.replace("%curCbbMemPer%", clan.getCbbBoard().getAccess().getDesc());
		
		send1001(content, player);
		send1002(player, clan.getIntroduction(), "", "");
	}
	
	private static void sendClanNotice(Player player, int clanId)
	{
		final Clan clan = ClanTable.getInstance().getClan(clanId);
		if (clan == null || player.getClanId() != clanId)
			return;
		
		if (clan.getLevel() < 2)
		{
			player.sendPacket(SystemMessageId.NO_CB_IN_MY_CLAN);
			sendClanList(player, 1);
			return;
		}
		
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "clan/clanhome-notice.htm");
		content = content.replace("%clanid%", Integer.toString(clan.getClanId()));
		content = content.replace("%enabled%", "[" + clan.isNoticeEnabled() + "]");
		content = content.replace("%flag%", String.valueOf(!clan.isNoticeEnabled()));
		send1001(content, player);
		send1002(player, clan.getNotice(), "", "");
	}
	
	private static void sendClanList(Player player, int index)
	{
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "clan/clanlist.htm");
		
		final StringBuilder sb = new StringBuilder();
		
		final Clan clan = player.getClan();
		if (clan != null)
			StringUtil.append(sb, "<table width=610 bgcolor=A7A19A><tr><td width=5></td><td width=605><a action=\"bypass _bbsclan;home;", clan.getClanId(), "\">[GO TO MY CLAN]</a></td></tr></table>");
		
		content = content.replace("%homebar%", sb.toString());
		
		if (index < 1)
			index = 1;
		
		sb.setLength(0);
		
		int i = 0;
		for (Clan cl : ClanTable.getInstance().getClans())
		{
			if (i > (index + 1) * 7)
				break;
			
			if (i++ >= (index - 1) * 7)
				StringUtil.append(sb, "<table width=610><tr><td width=5></td><td width=150 align=center><a action=\"bypass _bbsclan;home;", cl.getClanId(), "\">", cl.getName(), "</a></td><td width=150 align=center>", cl.getLeaderName(), "</td><td width=100 align=center>", cl.getLevel(), "</td><td width=200 align=center>", cl.getMembersCount(), "</td><td width=5></td></tr></table><br1><img src=\"L2UI.Squaregray\" width=605 height=1><br1>");
		}
		sb.append("<table><tr>");
		
		if (index == 1)
			sb.append("<td><button action=\"\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16></td>");
		else
			StringUtil.append(sb, "<td><button action=\"_bbsclan;clan;", index - 1, "\" back=\"l2ui_ch3.prev1_down\" fore=\"l2ui_ch3.prev1\" width=16 height=16 ></td>");
		
		int pageNumber = ClanTable.getInstance().getClans().size() / 8;
		if (pageNumber * 8 != ClanTable.getInstance().getClans().size())
			pageNumber++;
		
		for (i = 1; i <= pageNumber; i++)
		{
			if (i == index)
				StringUtil.append(sb, "<td> ", i, " </td>");
			else
				StringUtil.append(sb, "<td><a action=\"bypass _bbsclan;clan;", i, "\"> ", i, " </a></td>");
		}
		
		if (index == pageNumber)
			sb.append("<td><button action=\"\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16></td>");
		else
			StringUtil.append(sb, "<td><button action=\"bypass _bbsclan;clan;", index + 1, "\" back=\"l2ui_ch3.next1_down\" fore=\"l2ui_ch3.next1\" width=16 height=16 ></td>");
		
		sb.append("</tr></table>");
		
		content = content.replace("%clanlist%", sb.toString());
		separateAndSend(content, player);
	}
	
	private static void sendClanDetails(Player player, int clanId)
	{
		final Clan clan = ClanTable.getInstance().getClan(clanId);
		if (clan == null)
			return;
		
		if (clan.getLevel() < 2)
		{
			player.sendPacket(SystemMessageId.NO_CB_IN_MY_CLAN);
			sendClanList(player, 1);
			return;
		}
		
		String content;
		if (player.getClanId() != clanId)
			content = HTMLData.getInstance().getHtm(player, CB_PATH + "clan/clanhome.htm");
		else if (player.isClanLeader())
			content = HTMLData.getInstance().getHtm(player, CB_PATH + "clan/clanhome-leader.htm");
		else
			content = HTMLData.getInstance().getHtm(player, CB_PATH + "clan/clanhome-member.htm");
		
		content = content.replace("%clanid%", Integer.toString(clan.getClanId()));
		content = content.replace("%clanIntro%", clan.getIntroduction());
		content = content.replace("%clanName%", clan.getName());
		content = content.replace("%clanLvL%", Integer.toString(clan.getLevel()));
		content = content.replace("%clanMembers%", Integer.toString(clan.getMembersCount()));
		content = content.replace("%clanLeader%", clan.getLeaderName());
		content = content.replace("%allyName%", (clan.getAllyId() > 0) ? clan.getAllyName() : "");
		separateAndSend(content, player);
	}
	
	public static ClanBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanBBSManager INSTANCE = new ClanBBSManager();
	}
}