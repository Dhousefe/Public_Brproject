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

public class Q109_InSearchOfTheNest extends Quest
{
	private static final String QUEST_NAME = "Q109_InSearchOfTheNest";
	
	private static final int PIERCE = 31553;
	private static final int KAHMAN = 31554;
	private static final int SCOUT_CORPSE = 32015;
	
	private static final int SCOUT_MEMO = 8083;
	private static final int RECRUIT_BADGE = 7246;
	private static final int SOLDIER_BADGE = 7247;
	
	public Q109_InSearchOfTheNest()
	{
		super(109, "In Search of the Nest");
		
		setItemsIds(SCOUT_MEMO);
		
		addQuestStart(PIERCE);
		addTalkId(PIERCE, SCOUT_CORPSE, KAHMAN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31553-01.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32015-02.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
			giveItems(player, SCOUT_MEMO, 1);
		}
		else if (event.equalsIgnoreCase("31553-03.htm"))
		{
			st.setCond(3);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, SCOUT_MEMO, 1);
		}
		else if (event.equalsIgnoreCase("31554-02.htm"))
		{
			rewardItems(player, 57, 5168);
			playSound(player, SOUND_FINISH);
			st.exitQuest(false);
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
				if (player.getStatus().getLevel() >= 66 && player.getInventory().hasAtLeastOneItem(RECRUIT_BADGE, SOLDIER_BADGE))
					htmltext = "31553-00.htm";
				else
					htmltext = "31553-00a.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case PIERCE:
						if (cond == 1)
							htmltext = "31553-01a.htm";
						else if (cond == 2)
							htmltext = "31553-02.htm";
						else if (cond == 3)
							htmltext = "31553-03.htm";
						break;
					
					case SCOUT_CORPSE:
						if (cond == 1)
							htmltext = "32015-01.htm";
						else if (cond == 2)
							htmltext = "32015-02.htm";
						break;
					
					case KAHMAN:
						if (cond == 3)
							htmltext = "31554-01.htm";
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
}