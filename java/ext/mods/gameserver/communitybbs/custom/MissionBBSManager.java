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

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.custom.data.MissionData;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.Mission;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;

public class MissionBBSManager extends BaseBBSManager
{
	public MissionBBSManager()
	{
	}
	
	@Override
	protected String getFolder()
	{
		return "custom/mission/";
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.startsWith("_cbmission;"))
		{
			if (Config.ENABLE_MISSION)
			{
				StringTokenizer st = new StringTokenizer(command, ";");
				st.nextToken();
				
				int page = Integer.parseInt(st.nextToken());
				
				String content = HTMLData.getInstance().getHtm(player, CB_PATH + getFolder() + "index.htm");
				separateAndSend(content, player);
				if (content != null)
					content = content.replaceAll("%list%", String.valueOf(getList(player, page)));
				
				BaseBBSManager.separateAndSend(content, player);
			}
			else
			{
				String content = HTMLData.getInstance().getHtm(player, CB_PATH + getFolder() + "disabled.htm");
				separateAndSend(content, player);
				BaseBBSManager.separateAndSend(content, player);
			}
		}
		else
			super.parseCmd(command, player);
	}
	
	protected String getList(Player player, int page)
	{
		final StringBuilder sb = new StringBuilder();
		final Pagination<MissionType> list = new Pagination<>(player.getMissions().getAvailableTypes().stream(), page, 8);
		for (MissionType type : list)
		{
			final IntIntHolder mission = player.getMissions().getMission(type);
			final Mission data = MissionData.getInstance().getMissionByLevel(type, mission.getId() + 1);
			if (data == null)
				continue;
			
			final boolean completed = data.getLevel() == mission.getId();
			sb.append("<table width=623 bgcolor=000000><tr><td width=610 align=center>" + generateBar(606, 4, completed ? data.getRequired() : mission.getValue(), data.getRequired()) + "</td></tr></table><table width=623 bgcolor=000000><tr>");
			sb.append("<td width=40 height=40 align=right><button width=32 height=32 back=" + data.getIcon() + " fore=" + data.getIcon() + "></td>");
			sb.append("<td width=580><font color=LEVEL>Lv " + data.getLevel() + "</font> " + data.getName() + " " + (completed ? "<font color=00FF00>Done</font>" : "") + "<br1>");
			sb.append("<font color=B09878>" + (completed ? "[" + data.getName() + " achievement complete]" : data.getDescription().replaceAll("%remain%", StringUtil.formatNumber(data.getRequired() - mission.getValue())).replaceAll("%remaindefault%", StringUtil.formatNumber(data.getRequired()))) + "</font></td></tr></table><img src=L2UI.SquareGray width=620 height=1>");

		}
		
		list.generateSpace(22);
		list.generatePagesMedium("bypass _cbmission;%page%", 680);
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
	
	public static MissionBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MissionBBSManager INSTANCE = new MissionBBSManager();
	}
}