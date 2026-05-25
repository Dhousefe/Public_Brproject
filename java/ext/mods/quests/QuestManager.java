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
package ext.mods.quests;

import java.util.List;

import ext.mods.commons.lang.StringUtil;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.quests.holder.QuestHolder;
import ext.mods.quests.holder.QuestObjective;
import ext.mods.quests.holder.QuestReward;

public class QuestManager
{
	public void showMenuQuest(Player player, int page)
	{
		if (player == null)
			return;
		int playerClassId = player.getClassId().getId();
		
		for (int questId : player.getActiveQuestIds())
		{
			QuestHolder quest = QuestData.getInstance().getQuest(questId);
			if (quest == null)
				continue;
			
			boolean isClassValid = quest.getRequiredClasses().isEmpty() || quest.getRequiredClasses().contains(player.getClassId().getId());
			if (!isClassValid)
			{
				player.sendMessage("You cannot access this quest because your class does not match.");
				continue;
			}
			
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			StringBuilder sb = new StringBuilder();
			
			sb.append("<html><body>");
			sb.append("<center><font color=\"LEVEL\">").append(quest.getName()).append("</font></center><br1>");
			
			if (!quest.getDesc().isEmpty())
			{
				sb.append("<center>").append(quest.getDesc()).append("</center><br1>");
			}
			
			if (!quest.getObjectivesForClass(playerClassId).isEmpty())
			{
				sb.append("<center><font color=\"LEVEL\">Quest Objectives</font></center><br1>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br1>");
				
				for (QuestObjective obj : quest.getObjectivesForClass(playerClassId))
				{
					int npcId = obj.getNpcId();
					int requiredCount = obj.getCount();
					NpcTemplate npc = NpcData.getInstance().getTemplate(npcId);
					if (npc == null)
						continue;
					
					long currentCount = player.getQuestKillCount(questId, npcId);
					
					sb.append("<table width=300><tr>");
					
					sb.append("<td width=60 height=65 align=center>");
					sb.append("<img src=\"").append(quest.getIcon()).append("\" width=32 height=32>");
					sb.append("</td>");
					
					sb.append("<td width=200>");
					String npcName = npc.getName().length() > 32 ? npc.getName().substring(0, 32) : npc.getName();
					sb.append("<font color=LEVEL>").append(npcName).append("</font><br1>");
					
					if (player.isQuestCompleted(questId))
					{
						sb.append("<font color=00FF00>Completed</font><br1>");
					}
					else
					{
						sb.append("<font color=B09878>Progress: ").append(StringUtil.formatNumber(currentCount)).append(" / ").append(StringUtil.formatNumber(requiredCount)).append("</font><br1>");
						
						sb.append(generateBarProgress(200, 4, (int) currentCount, requiredCount));
					}
					
					sb.append("</td></tr></table>");
					sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br>");
				}
			}
			
			List<QuestReward> rewards = quest.getRewardsForClass(player.getClassId().getId());
			if (!rewards.isEmpty())
			{
				sb.append("<center><font color=\"LEVEL\">Rewards</font></center><br1>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br>");
				
				int totalRewards = rewards.size();
				int totalPages = (int) Math.ceil((double) totalRewards / MAX_ITEM_PAGE);
				
				if (page < 1)
					page = 1;
				if (page > totalPages)
					page = totalPages;
				
				int startIndex = (page - 1) * MAX_ITEM_PAGE;
				int endIndex = Math.min(startIndex + MAX_ITEM_PAGE, totalRewards);
				
				for (int i = startIndex; i < endIndex; i++)
				{
					QuestReward reward = rewards.get(i);
					Item template = ItemData.getInstance().getTemplate(reward.getItemId());
					if (template == null)
						continue;
					
					sb.append("<table width=300 bgcolor=000000><tr>");
					sb.append("<td width=40 height=40 align=center>");
					sb.append("<img src=\"").append(template.getIcon()).append("\" width=32 height=32>");
					sb.append("</td>");
					sb.append("<td width=240><font color=LEVEL>").append(template.getName()).append("</font><br1>x").append(StringUtil.formatNumber(reward.getCount())).append("</td>");
					sb.append("</tr></table>");
					sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br1>");
				}
				
				if (totalRewards > MAX_ITEM_PAGE)
				{
					sb.append("<center>");
					sb.append("<table width=\"280\" height=\"15\"><tr>");
					
					if (page > 1)
						sb.append("<td align=left width=70><a action=\"bypass -h questnav ").append(quest.getId()).append(" ").append(page - 1).append("\">Previous</a></td>");
					else
						sb.append("<td align=left width=70>Previous</td>");
					
					sb.append("<td align=center width=90> Page: ").append(page).append("</td>");
					
					if (page < totalPages)
						sb.append("<td align=right width=70><a action=\"bypass -h questnav ").append(quest.getId()).append(" ").append(page + 1).append("\">Next</a></td>");
					else
						sb.append("<td align=right width=70>Next</td>");
					
					sb.append("</tr></table>");
					sb.append("</center><br>");
				}
				
			}
			sb.append("<center><br>");
			sb.append("<font color=B09878>Notificações:</font>");
			sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br>");
			sb.append("<table width=200>");
			
			sb.append("<tr>");
			sb.append("<td width=120 align=left>Notificação HTML</td>");
			sb.append("<td width=16 align=center>");
			sb.append("<button action=\"bypass -h questnotify html ").append(player.isQuestNotifyHtml() ? "off" : "on").append("\" ").append("width=16 height=16 back=\"").append(player.isQuestNotifyHtml() ? "L2UI.CheckBox_checked" : "L2UI.CheckBox").append("\" ").append("fore=\"").append(player.isQuestNotifyHtml() ? "L2UI.CheckBox_checked" : "L2UI.CheckBox").append("\">");
			sb.append("</td>");
			sb.append("</tr>");
			
			sb.append("<tr>");
			sb.append("<td width=120 align=left>Notificação Chat</td>");
			sb.append("<td width=16 align=center>");
			sb.append("<button action=\"bypass -h questnotify chat ").append(player.isQuestNotifyChat() ? "off" : "on").append("\" ").append("width=16 height=16 back=\"").append(player.isQuestNotifyChat() ? "L2UI.CheckBox_checked" : "L2UI.CheckBox").append("\" ").append("fore=\"").append(player.isQuestNotifyChat() ? "L2UI.CheckBox_checked" : "L2UI.CheckBox").append("\">");
			sb.append("</td>");
			sb.append("</tr>");
			
			sb.append("</table></center><br1>");
			sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br>");
			sb.append("</body></html>");
			
			html.setHtml(sb.toString());
			player.sendPacket(html);
			
			if (!quest.getSound().isEmpty())
			{
				player.sendPacket(new PlaySound(quest.getSound()));
			}
		}
	}
	
	public String generateBarProgress(int width, int height, int current, int max)
	{
		final StringBuilder sb = new StringBuilder();
		
		current = current > max ? max : current;
		
		int barHP = Math.max((width * (current * 100 / max) / 100), 0);
		int barCP = width - barHP;
		sb.append("<table width=" + width + "><tr>");
		sb.append("<td width=" + barHP + " align=center><br><img src=\"L2UI_CH3.BR_BAR1_HP\" width=" + barHP + " height=" + height + "/></td>");
		sb.append("<td width=" + barCP + " align=center><br><img src=\"L2UI_CH3.BR_BAR1_CP\" width=" + barCP + " height=" + height + "/></td>");
		sb.append("</tr></table>");
		return sb.toString();
	}
	
	private static int MAX_ITEM_PAGE = 2;
	
	public void showCompleteQuest(Player player, QuestHolder quest, int page)
	{
		if (player == null)
			return;
		int playerClassId = player.getClassId().getId();
		
		quest = QuestData.getInstance().getQuest(quest.getId());
		if (quest == null)
			return;
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><body>");
		sb.append("<center><font color=\"LEVEL\">").append(quest.getName()).append("</font></center><br1>");
		
		if (!quest.getDesc().isEmpty())
		{
			sb.append("<center>").append(quest.getDesc()).append("</center><br1>");
		}
		
		if (!quest.getObjectivesForClass(playerClassId).isEmpty())
		{
			sb.append("<center><font color=\"LEVEL\">Quest Objectives</font></center><br1>");
			sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br1>");
			
			for (QuestObjective obj : quest.getObjectivesForClass(playerClassId))
			{
				int npcId = obj.getNpcId();
				
				NpcTemplate npc = NpcData.getInstance().getTemplate(npcId);
				if (npc == null)
					continue;
				
				sb.append("<table width=300><tr>");
				
				sb.append("<td width=40 height=40 align=center>");
				sb.append("<img src=\"").append(quest.getIcon()).append("\" width=32 height=32>");
				sb.append("</td>");
				
				sb.append("<td width=200>");
				String npcName = npc.getName().length() > 32 ? npc.getName().substring(0, 32) : npc.getName();
				sb.append("<font color=LEVEL>").append(npcName).append("</font><br1>");
				
				if (player.isQuestCompleted(quest.getId()))
				{
					sb.append("<font color=00FF00>Completed</font>");
				}
				
				sb.append("</td></tr></table>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br>");
			}
		}
		
		List<QuestReward> rewards = quest.getRewardsForClass(player.getClassId().getId());
		if (!rewards.isEmpty())
		{
			sb.append("<center><font color=\"LEVEL\">Rewards</font></center><br1>");
			sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br>");
			
			int totalRewards = rewards.size();
			int totalPages = (int) Math.ceil((double) totalRewards / MAX_ITEM_PAGE);
			
			if (page < 1)
				page = 1;
			if (page > totalPages)
				page = totalPages;
			
			int startIndex = (page - 1) * MAX_ITEM_PAGE;
			int endIndex = Math.min(startIndex + MAX_ITEM_PAGE, totalRewards);
			
			for (int i = startIndex; i < endIndex; i++)
			{
				QuestReward reward = rewards.get(i);
				Item template = ItemData.getInstance().getTemplate(reward.getItemId());
				if (template == null)
					continue;
				
				sb.append("<table width=300 bgcolor=000000><tr>");
				sb.append("<td width=40 height=40 align=center>");
				sb.append("<img src=\"").append(template.getIcon()).append("\" width=32 height=32>");
				sb.append("</td>");
				sb.append("<td width=240><font color=LEVEL>").append(template.getName()).append("</font><br1>x").append(StringUtil.formatNumber(reward.getCount())).append(" <font color=B09878>received!</font></td>");
				sb.append("</tr></table>");
				sb.append("<img src=\"L2UI.SquareGray\" width=300 height=1><br1>");
			}
			
			if (totalRewards > MAX_ITEM_PAGE)
			{
				sb.append("<center>");
				sb.append("<table width=\"280\" height=\"15\"><tr>");
				
				if (page > 1)
					sb.append("<td align=left width=70><a action=\"bypass -h questnav2 ").append(quest.getId()).append(" ").append(page - 1).append("\">Previous</a></td>");
				else
					sb.append("<td align=left width=70>Previous</td>");
				
				sb.append("<td align=center width=90> Page: ").append(page).append("</td>");
				
				if (page < totalPages)
					sb.append("<td align=right width=70><a action=\"bypass -h questnav2 ").append(quest.getId()).append(" ").append(page + 1).append("\">Next</a></td>");
				else
					sb.append("<td align=right width=70>Next</td>");
				
				sb.append("</tr></table>");
				sb.append("</center><br>");
			}
			
		}
		
		if (!quest.isRepeatable())
		{
			
			sb.append("<br><center>");
			sb.append("<font color=LEVEL>Congratulations!</font><br1>");
			sb.append("You have completed this quest. " + quest.getName() + "<br1>");
			sb.append("</center><br>");
			QuestHolder questCheck = QuestData.getInstance().getQuest(quest.getId() + 1);
			if (questCheck != null)
				sb.append("<center>Do you want go to the next one?<br1><button value=\"Next\" action=\"bypass -h setquest ").append(quest.getId() + 1).append("\" width=65 height=16 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></center>");
			
		}
		else
		{
			
			sb.append("<br><center>");
			sb.append("<font color=LEVEL>Congratulations!</font><br1>");
			sb.append("You have completed this quest. " + quest.getName() + "<br1>");
			sb.append("</center>");
			
			sb.append("<br><center><button value=\"Repeat\" action=\"bypass -h setquest ").append(quest.getId()).append("\" width=65 height=16 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></center>");
			
			QuestHolder questCheck = QuestData.getInstance().getQuest(quest.getId() + 1);
			if (questCheck != null)
				sb.append("<center>Do you want to repeat or go to the next one?<br1><button value=\"Next\" action=\"bypass -h setquest ").append(quest.getId() + 1).append("\" width=65 height=16 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></center>");
			
		}
		
		sb.append("</body></html>");
		
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	public static QuestManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final QuestManager INSTANCE = new QuestManager();
	}
}
