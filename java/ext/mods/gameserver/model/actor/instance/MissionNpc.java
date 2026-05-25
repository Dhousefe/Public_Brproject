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
package ext.mods.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.custom.data.MissionData;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.Mission;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class MissionNpc extends Folk
{
	public MissionNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "html/mods/mission/" + filename + ".htm";
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("mission"))
		{
			if (Config.ENABLE_MISSION)
			{
				StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				
				int page = 1;
				if (st.hasMoreTokens())
				{
					try
					{
						page = Integer.parseInt(st.nextToken());
					}
					catch (NumberFormatException e)
					{
						page = 1;
					}
				}
				
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 0));
				html.replace("%list%", String.valueOf(getList(player, page)));
				player.sendPacket(html);
			}
		}
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), val));
		html.replace("%list%", String.valueOf(getList(player, val)));
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	protected String getList(Player player, int page)
	{
		page = Math.max(1, page);
		
		final StringBuilder sb = new StringBuilder();
		final Pagination<MissionType> list = new Pagination<>(player.getMissions().getAvailableTypes().stream(), page, 6);
		for (MissionType type : list)
		{
			final IntIntHolder mission = player.getMissions().getMission(type);
			final Mission data = MissionData.getInstance().getMissionByLevel(type, mission.getId() + 1);
			if (data == null)
				continue;
			
			final boolean completed = data.getLevel() == mission.getId();
			sb.append("<table width=278 bgcolor=000000><tr><td width=278 align=center>" + generateBar(273, 4, completed ? data.getRequired() : mission.getValue(), data.getRequired()) + "</td></tr></table><table width=278 bgcolor=000000><tr>");
			sb.append("<td width=40 height=40 align=right><button width=32 height=32 back=" + data.getIcon() + " fore=" + data.getIcon() + "></td>");
			sb.append("<td width=278><font color=LEVEL>Lv " + data.getLevel() + "</font> " + data.getName() + " " + (completed ? "<font color=00FF00>Done</font>" : "") + "<br1>");
			sb.append("<font color=B09878>" + (completed ? "[" + data.getName() + " achievement complete]" : data.getDescription().replaceAll("%remain%", StringUtil.formatNumber(data.getRequired() - mission.getValue())).replaceAll("%remaindefault%", StringUtil.formatNumber(data.getRequired()))) + "</font></td></tr></table><img src=L2UI.SquareGray width=278 height=1>");
			
		}
		
		list.generateSpace(22);
		list.generatePages("bypass npc_" + getObjectId() + "_mission %page%");
		sb.append(list.getContent());
		return sb.toString();
	}
	
	public String generateBar(int width, int height, int current, int max)
	{
		final StringBuilder sb = new StringBuilder();
		current = current > max ? max : current;
		int bar = Math.max((width * (current * 100 / max) / 100), 0);
		sb.append("<table width=" + width + " cellspacing=0 cellpadding=0><tr><td width=" + bar + " align=center><img src=L2UI_CH3.BR_BAR1_CP width=" + bar + " height=" + height + "/></td>");
		sb.append("<td width=" + (width - bar) + " align=center><img src=L2UI_CH3.BR_BAR1_HP1 width=" + (width - bar) + " height=" + height + "/></td></tr></table>");
		return sb.toString();
	}
}