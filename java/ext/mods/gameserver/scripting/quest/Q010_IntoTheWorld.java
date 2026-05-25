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
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q010_IntoTheWorld extends Quest
{
	private static final String QUEST_NAME = "Q010_IntoTheWorld";
	
	private static final int REED = 30520;
	private static final int BALANKI = 30533;
	private static final int GERALD = 30650;
	
	private static final int VERY_EXPENSIVE_NECKLACE = 7574;
	
	private static final int SOE_GIRAN = 7559;
	private static final int MARK_TRAVELER = 7570;
	
	public Q010_IntoTheWorld()
	{
		super(10, "Into the World");
		
		setItemsIds(VERY_EXPENSIVE_NECKLACE);
		
		addQuestStart(BALANKI);
		addTalkId(BALANKI, REED, GERALD);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30533-02.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30520-02.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
			giveItems(player, VERY_EXPENSIVE_NECKLACE, 1);
		}
		else if (event.equalsIgnoreCase("30650-02.htm"))
		{
			st.setCond(3);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, VERY_EXPENSIVE_NECKLACE, 1);
		}
		else if (event.equalsIgnoreCase("30520-04.htm"))
		{
			st.setCond(4);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30533-05.htm"))
		{
			giveItems(player, SOE_GIRAN, 1);
			rewardItems(player, MARK_TRAVELER, 1);
			playSound(player, SOUND_FINISH);
			st.exitQuest(false);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getStatus().getLevel() >= 3 && player.getRace() == ClassRace.DWARF)
					htmltext = "30533-01.htm";
				else
					htmltext = "30533-01a.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case BALANKI:
						if (cond < 4)
							htmltext = "30533-03.htm";
						else if (cond == 4)
							htmltext = "30533-04.htm";
						break;
					
					case REED:
						if (cond == 1)
							htmltext = "30520-01.htm";
						else if (cond == 2)
							htmltext = "30520-02a.htm";
						else if (cond == 3)
							htmltext = "30520-03.htm";
						else if (cond == 4)
							htmltext = "30520-04a.htm";
						break;
					
					case GERALD:
						if (cond == 2)
							htmltext = "30650-01.htm";
						else if (cond > 2)
							htmltext = "30650-04.htm";
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