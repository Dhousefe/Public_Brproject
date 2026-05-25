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

import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q376_ExplorationOfTheGiantsCave_Part1 extends Quest
{
	private static final String QUEST_NAME = "Q376_ExplorationOfTheGiantsCave_Part1";
	
	private static final int SOBLING = 31147;
	private static final int CLIFF = 30182;
	
	private static final int ANCIENT_PARCHMENT = 5944;
	private static final int MYSTERIOUS_BOOK = 5890;
	private static final int ANCIENT_DICTIONARY_BASIC_LEVEL = 5891;
	private static final int ANCIENT_DICTIONARY_INTERMEDIATE_LEVEL = 5892;
	private static final int[][] BOOKS =
	{
		{
			5937,
			5938,
			5939,
			5940,
			5941
		},
		{
			5932,
			5933,
			5934,
			5935,
			5936
		},
		{
			5922,
			5923,
			5924,
			5925,
			5926
		},
		{
			5927,
			5928,
			5929,
			5930,
			5931
		}
	};
	
	private static final int[][] RECIPES =
	{
		{
			5346,
			5354
		},
		{
			5332,
			5334
		},
		{
			5416,
			5418
		},
		{
			5424,
			5340
		}
	};
	
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(20647, 26000);
		CHANCES.put(20648, 28000);
		CHANCES.put(20649, 30000);
		CHANCES.put(20650, 32000);
	}
	
	public Q376_ExplorationOfTheGiantsCave_Part1()
	{
		super(376, "Exploration of the Giants' Cave, Part 1");
		
		setItemsIds(ANCIENT_DICTIONARY_BASIC_LEVEL, MYSTERIOUS_BOOK);
		
		addQuestStart(SOBLING);
		addTalkId(SOBLING, CLIFF);
		
		for (int npcId : CHANCES.keySet())
			addMyDying(npcId);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31147-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, ANCIENT_DICTIONARY_BASIC_LEVEL, 1);
		}
		else if (event.equalsIgnoreCase("31147-04.htm"))
		{
			htmltext = checkItems(player, st);
		}
		else if (event.equalsIgnoreCase("31147-09.htm"))
		{
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30182-02.htm"))
		{
			st.setCond(3);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, MYSTERIOUS_BOOK, -1);
			giveItems(player, ANCIENT_DICTIONARY_INTERMEDIATE_LEVEL, 1);
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
				htmltext = (player.getStatus().getLevel() < 51) ? "31147-01.htm" : "31147-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case SOBLING:
						htmltext = checkItems(player, st);
						break;
					
					case CLIFF:
						if (cond == 2 && player.getInventory().hasItems(MYSTERIOUS_BOOK))
							htmltext = "30182-01.htm";
						else if (cond == 3)
							htmltext = "30182-03.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		if (!st.getPlayer().getInventory().hasAtLeastOneItem(MYSTERIOUS_BOOK, ANCIENT_DICTIONARY_INTERMEDIATE_LEVEL))
			dropItems(st.getPlayer(), MYSTERIOUS_BOOK, 1, 1, 2000);
		
		dropItems(st.getPlayer(), ANCIENT_PARCHMENT, 1, 0, CHANCES.get(npc.getNpcId()));
	}
	
	private static String checkItems(Player player, QuestState st)
	{
		if (player.getInventory().hasItems(MYSTERIOUS_BOOK))
		{
			if (st.getCond() == 1)
			{
				st.setCond(2);
				playSound(player, SOUND_MIDDLE);
				return "31147-07.htm";
			}
			return "31147-08.htm";
		}
		
		for (int type = 0; type < BOOKS.length; type++)
		{
			boolean complete = true;
			for (int book : BOOKS[type])
			{
				if (!player.getInventory().hasItems(book))
					complete = false;
			}
			
			if (complete)
			{
				for (int book : BOOKS[type])
					takeItems(player, book, 1);
				
				giveItems(player, Rnd.get(RECIPES[type]), 1);
				return "31147-04.htm";
			}
		}
		return "31147-05.htm";
	}
}