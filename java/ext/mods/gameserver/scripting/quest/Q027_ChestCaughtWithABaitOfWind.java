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

public class Q027_ChestCaughtWithABaitOfWind extends Quest
{
	private static final String QUEST_NAME = "Q027_ChestCaughtWithABaitOfWind";
	
	private static final int LANOSCO = 31570;
	private static final int SHALING = 31434;
	
	private static final int LARGE_BLUE_TREASURE_CHEST = 6500;
	private static final int STRANGE_BLUEPRINT = 7625;
	private static final int BLACK_PEARL_RING = 880;
	
	public Q027_ChestCaughtWithABaitOfWind()
	{
		super(27, "Chest caught with a bait of wind");
		
		setItemsIds(STRANGE_BLUEPRINT);
		
		addQuestStart(LANOSCO);
		addTalkId(LANOSCO, SHALING);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31570-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31570-07.htm"))
		{
			if (player.getInventory().hasItems(LARGE_BLUE_TREASURE_CHEST))
			{
				st.setCond(2);
				takeItems(player, LARGE_BLUE_TREASURE_CHEST, 1);
				giveItems(player, STRANGE_BLUEPRINT, 1);
			}
			else
				htmltext = "31570-08.htm";
		}
		else if (event.equalsIgnoreCase("31434-02.htm"))
		{
			if (player.getInventory().hasItems(STRANGE_BLUEPRINT))
			{
				htmltext = "31434-02.htm";
				takeItems(player, STRANGE_BLUEPRINT, 1);
				giveItems(player, BLACK_PEARL_RING, 1);
				playSound(player, SOUND_FINISH);
				st.exitQuest(false);
			}
			else
				htmltext = "31434-03.htm";
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
				if (player.getStatus().getLevel() < 27)
					htmltext = "31570-02.htm";
				else
				{
					QuestState st2 = player.getQuestList().getQuestState("Q050_LanoscosSpecialBait");
					if (st2 != null && st2.isCompleted())
						htmltext = "31570-01.htm";
					else
						htmltext = "31570-03.htm";
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case LANOSCO:
						if (cond == 1)
							htmltext = (!player.getInventory().hasItems(LARGE_BLUE_TREASURE_CHEST)) ? "31570-06.htm" : "31570-05.htm";
						else if (cond == 2)
							htmltext = "31570-09.htm";
						break;
					
					case SHALING:
						if (cond == 2)
							htmltext = "31434-01.htm";
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