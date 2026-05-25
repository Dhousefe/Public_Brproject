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
package ext.mods.gameserver.scripting.quest;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q124_MeetingTheElroki extends Quest
{
	public static final String QUEST_NAME = "Q124_MeetingTheElroki";
	
	private static final int MARQUEZ = 32113;
	private static final int MUSHIKA = 32114;
	private static final int ASAMAH = 32115;
	private static final int KARAKAWEI = 32117;
	private static final int MANTARASA = 32118;
	
	public Q124_MeetingTheElroki()
	{
		super(124, "Meeting the Elroki");
		
		addQuestStart(MARQUEZ);
		addTalkId(MARQUEZ, MUSHIKA, ASAMAH, KARAKAWEI, MANTARASA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32113-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32113-04.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32114-02.htm"))
		{
			st.setCond(3);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32115-04.htm"))
		{
			st.setCond(4);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32117-02.htm"))
		{
			if (st.getCond() == 4)
				st.set("progress", 1);
		}
		else if (event.equalsIgnoreCase("32117-03.htm"))
		{
			st.setCond(5);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32118-02.htm"))
		{
			st.setCond(6);
			playSound(player, SOUND_MIDDLE);
			giveItems(player, 8778, 1);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getStatus().getLevel() < 75) ? "32113-01a.htm" : "32113-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case MARQUEZ:
						if (cond == 1)
							htmltext = "32113-03.htm";
						else if (cond > 1)
							htmltext = "32113-04a.htm";
						break;
					
					case MUSHIKA:
						if (cond == 2)
							htmltext = "32114-01.htm";
						else if (cond > 2)
							htmltext = "32114-03.htm";
						break;
					
					case ASAMAH:
						if (cond == 3)
							htmltext = "32115-01.htm";
						else if (cond == 6)
						{
							htmltext = "32115-05.htm";
							takeItems(player, 8778, -1);
							rewardItems(player, 57, 71318);
							playSound(player, SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case KARAKAWEI:
						if (cond == 4)
						{
							htmltext = "32117-01.htm";
							if (st.getInteger("progress") == 1)
								htmltext = "32117-02.htm";
						}
						else if (cond > 4)
							htmltext = "32117-04.htm";
						break;
					
					case MANTARASA:
						if (cond == 5)
							htmltext = "32118-01.htm";
						else if (cond > 5)
							htmltext = "32118-03.htm";
						break;
				}
				break;
			
			case COMPLETED:
				if (npc.getNpcId() == ASAMAH)
					htmltext = "32115-06.htm";
				else
					htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
}