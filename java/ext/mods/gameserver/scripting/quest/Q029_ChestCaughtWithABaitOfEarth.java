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

public class Q029_ChestCaughtWithABaitOfEarth extends Quest
{
	private static final String QUEST_NAME = "Q029_ChestCaughtWithABaitOfEarth";
	
	private static final int WILLIE = 31574;
	private static final int ANABEL = 30909;
	
	private static final int SMALL_PURPLE_TREASURE_CHEST = 6507;
	private static final int SMALL_GLASS_BOX = 7627;
	private static final int PLATED_LEATHER_GLOVES = 2455;
	
	public Q029_ChestCaughtWithABaitOfEarth()
	{
		super(29, "Chest caught with a bait of earth");
		
		setItemsIds(SMALL_GLASS_BOX);
		
		addQuestStart(WILLIE);
		addTalkId(WILLIE, ANABEL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31574-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31574-07.htm"))
		{
			if (player.getInventory().hasItems(SMALL_PURPLE_TREASURE_CHEST))
			{
				st.setCond(2);
				takeItems(player, SMALL_PURPLE_TREASURE_CHEST, 1);
				giveItems(player, SMALL_GLASS_BOX, 1);
			}
			else
				htmltext = "31574-08.htm";
		}
		else if (event.equalsIgnoreCase("30909-02.htm"))
		{
			if (player.getInventory().hasItems(SMALL_GLASS_BOX))
			{
				htmltext = "30909-02.htm";
				takeItems(player, SMALL_GLASS_BOX, 1);
				giveItems(player, PLATED_LEATHER_GLOVES, 1);
				playSound(player, SOUND_FINISH);
				st.exitQuest(false);
			}
			else
				htmltext = "30909-03.htm";
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
				if (player.getStatus().getLevel() < 48)
					htmltext = "31574-02.htm";
				else
				{
					QuestState st2 = player.getQuestList().getQuestState("Q052_WilliesSpecialBait");
					if (st2 != null && st2.isCompleted())
						htmltext = "31574-01.htm";
					else
						htmltext = "31574-03.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case WILLIE:
						if (cond == 1)
							htmltext = (!player.getInventory().hasItems(SMALL_PURPLE_TREASURE_CHEST)) ? "31574-06.htm" : "31574-05.htm";
						else if (cond == 2)
							htmltext = "31574-09.htm";
						break;
					
					case ANABEL:
						if (cond == 2)
							htmltext = "30909-01.htm";
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