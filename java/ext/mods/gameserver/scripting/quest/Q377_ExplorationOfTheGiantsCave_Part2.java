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

public class Q377_ExplorationOfTheGiantsCave_Part2 extends Quest
{
	private static final String QUEST_NAME = "Q377_ExplorationOfTheGiantsCave_Part2";
	
	private static final int ANCIENT_TITAN_BOOK = 5955;
	private static final int ANCIENT_DICTIONARY_INTERMEDIATE_LEVEL = 5892;
	
	private static final int[][] BOOKS =
	{
		{
			5945,
			5946,
			5947,
			5948,
			5949
		},
		{
			5950,
			5951,
			5952,
			5953,
			5954
		}
	};
	
	private static final int[][] RECIPES =
	{
		{
			5338,
			5336
		},
		{
			5420,
			5422
		}
	};
	
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(20654, 25000);
		CHANCES.put(20656, 22000);
		CHANCES.put(20657, 16000);
		CHANCES.put(20658, 15000);
	}
	
	public Q377_ExplorationOfTheGiantsCave_Part2()
	{
		super(377, "Exploration of the Giants' Cave, Part 2");
		
		addQuestStart(31147);
		addTalkId(31147);
		
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
		}
		else if (event.equalsIgnoreCase("31147-04.htm"))
		{
			htmltext = checkItems(player);
		}
		else if (event.equalsIgnoreCase("31147-07.htm"))
		{
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
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
				htmltext = (player.getStatus().getLevel() < 57 || !player.getInventory().hasItems(ANCIENT_DICTIONARY_INTERMEDIATE_LEVEL)) ? "31147-01.htm" : "31147-02.htm";
				break;
			
			case STARTED:
				htmltext = checkItems(player);
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		dropItems(st.getPlayer(), ANCIENT_TITAN_BOOK, 1, 0, CHANCES.get(npc.getNpcId()));
	}
	
	private static String checkItems(Player player)
	{
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