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

public class Q030_ChestCaughtWithABaitOfFire extends Quest
{
	private static final String QUEST_NAME = "Q030_ChestCaughtWithABaitOfFire";
	
	private static final int LINNAEUS = 31577;
	private static final int RUKAL = 30629;
	
	private static final int RED_TREASURE_BOX = 6511;
	private static final int MUSICAL_SCORE = 7628;
	private static final int NECKLACE_OF_PROTECTION = 916;
	
	public Q030_ChestCaughtWithABaitOfFire()
	{
		super(30, "Chest caught with a bait of fire");
		
		setItemsIds(MUSICAL_SCORE);
		
		addQuestStart(LINNAEUS);
		addTalkId(LINNAEUS, RUKAL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31577-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31577-07.htm"))
		{
			if (player.getInventory().hasItems(RED_TREASURE_BOX))
			{
				st.setCond(2);
				takeItems(player, RED_TREASURE_BOX, 1);
				giveItems(player, MUSICAL_SCORE, 1);
			}
			else
				htmltext = "31577-08.htm";
		}
		else if (event.equalsIgnoreCase("30629-02.htm"))
		{
			if (player.getInventory().hasItems(MUSICAL_SCORE))
			{
				htmltext = "30629-02.htm";
				takeItems(player, MUSICAL_SCORE, 1);
				giveItems(player, NECKLACE_OF_PROTECTION, 1);
				playSound(player, SOUND_FINISH);
				st.exitQuest(false);
			}
			else
				htmltext = "30629-03.htm";
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
				if (player.getStatus().getLevel() < 60)
					htmltext = "31577-02.htm";
				else
				{
					QuestState st2 = player.getQuestList().getQuestState("Q053_LinnaeusSpecialBait");
					if (st2 != null && st2.isCompleted())
						htmltext = "31577-01.htm";
					else
						htmltext = "31577-03.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case LINNAEUS:
						if (cond == 1)
							htmltext = (!player.getInventory().hasItems(RED_TREASURE_BOX)) ? "31577-06.htm" : "31577-05.htm";
						else if (cond == 2)
							htmltext = "31577-09.htm";
						break;
					
					case RUKAL:
						if (cond == 2)
							htmltext = "30629-01.htm";
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