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

import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q619_RelicsOfTheOldEmpire extends Quest
{
	private static final String QUEST_NAME = "Q619_RelicsOfTheOldEmpire";
	
	private static final int GHOST_OF_ADVENTURER = 31538;
	
	private static final int BROKEN_RELIC_PART = 7254;
	private static final int ENTRANCE_PASS_TO_THE_SEPULCHER = 7075;
	
	private static final int[] REWARDS = new int[]
	{
		6881,
		6883,
		6885,
		6887,
		6891,
		6893,
		6895,
		6897,
		6899,
		7580
	};
	
	private static final Map<Integer, Integer> FOUR_SEPULCHERS_DROPLIST = HashMap.newHashMap(79);
	private static final Map<Integer, Integer> IMPERIAL_TOMB_DROPLIST = HashMap.newHashMap(45);
	
	public Q619_RelicsOfTheOldEmpire()
	{
		super(619, "Relics of the Old Empire");
		
		FOUR_SEPULCHERS_DROPLIST.put(18120, 1280000);
		FOUR_SEPULCHERS_DROPLIST.put(18121, 1210000);
		FOUR_SEPULCHERS_DROPLIST.put(18122, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18123, 1280000);
		FOUR_SEPULCHERS_DROPLIST.put(18124, 1210000);
		FOUR_SEPULCHERS_DROPLIST.put(18125, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18126, 1280000);
		FOUR_SEPULCHERS_DROPLIST.put(18127, 1210000);
		FOUR_SEPULCHERS_DROPLIST.put(18128, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18129, 1280000);
		FOUR_SEPULCHERS_DROPLIST.put(18130, 1210000);
		FOUR_SEPULCHERS_DROPLIST.put(18131, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18132, 1300000);
		FOUR_SEPULCHERS_DROPLIST.put(18133, 1200000);
		FOUR_SEPULCHERS_DROPLIST.put(18134, 900000);
		FOUR_SEPULCHERS_DROPLIST.put(18135, 1200000);
		FOUR_SEPULCHERS_DROPLIST.put(18136, 1200000);
		FOUR_SEPULCHERS_DROPLIST.put(18137, 890000);
		FOUR_SEPULCHERS_DROPLIST.put(18138, 1190000);
		FOUR_SEPULCHERS_DROPLIST.put(18139, 1170000);
		FOUR_SEPULCHERS_DROPLIST.put(18140, 1190000);
		FOUR_SEPULCHERS_DROPLIST.put(18141, 760000);
		FOUR_SEPULCHERS_DROPLIST.put(18142, 760000);
		FOUR_SEPULCHERS_DROPLIST.put(18143, 760000);
		FOUR_SEPULCHERS_DROPLIST.put(18144, 760000);
		FOUR_SEPULCHERS_DROPLIST.put(18145, 650000);
		FOUR_SEPULCHERS_DROPLIST.put(18146, 660000);
		FOUR_SEPULCHERS_DROPLIST.put(18147, 620000);
		FOUR_SEPULCHERS_DROPLIST.put(18148, 720000);
		FOUR_SEPULCHERS_DROPLIST.put(18149, 630000);
		FOUR_SEPULCHERS_DROPLIST.put(18166, 920000);
		FOUR_SEPULCHERS_DROPLIST.put(18167, 900000);
		FOUR_SEPULCHERS_DROPLIST.put(18168, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18169, 900000);
		FOUR_SEPULCHERS_DROPLIST.put(18170, 900000);
		FOUR_SEPULCHERS_DROPLIST.put(18171, 940000);
		FOUR_SEPULCHERS_DROPLIST.put(18172, 890000);
		FOUR_SEPULCHERS_DROPLIST.put(18173, 990000);
		FOUR_SEPULCHERS_DROPLIST.put(18174, 1220000);
		FOUR_SEPULCHERS_DROPLIST.put(18175, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18176, 990000);
		FOUR_SEPULCHERS_DROPLIST.put(18177, 1220000);
		FOUR_SEPULCHERS_DROPLIST.put(18178, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18179, 990000);
		FOUR_SEPULCHERS_DROPLIST.put(18180, 1220000);
		FOUR_SEPULCHERS_DROPLIST.put(18181, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18182, 990000);
		FOUR_SEPULCHERS_DROPLIST.put(18183, 1220000);
		FOUR_SEPULCHERS_DROPLIST.put(18184, 930000);
		FOUR_SEPULCHERS_DROPLIST.put(18185, 1230000);
		FOUR_SEPULCHERS_DROPLIST.put(18186, 1240000);
		FOUR_SEPULCHERS_DROPLIST.put(18187, 1200000);
		FOUR_SEPULCHERS_DROPLIST.put(18188, 900000);
		FOUR_SEPULCHERS_DROPLIST.put(18189, 1200000);
		FOUR_SEPULCHERS_DROPLIST.put(18190, 1200000);
		FOUR_SEPULCHERS_DROPLIST.put(18191, 890000);
		FOUR_SEPULCHERS_DROPLIST.put(18192, 1190000);
		FOUR_SEPULCHERS_DROPLIST.put(18193, 1170000);
		FOUR_SEPULCHERS_DROPLIST.put(18194, 1200000);
		FOUR_SEPULCHERS_DROPLIST.put(18195, 910000);
		FOUR_SEPULCHERS_DROPLIST.put(18212, 3790000);
		FOUR_SEPULCHERS_DROPLIST.put(18213, 3790000);
		FOUR_SEPULCHERS_DROPLIST.put(18214, 3790000);
		FOUR_SEPULCHERS_DROPLIST.put(18215, 3790000);
		FOUR_SEPULCHERS_DROPLIST.put(18216, 3790000);
		FOUR_SEPULCHERS_DROPLIST.put(18217, 3790000);
		FOUR_SEPULCHERS_DROPLIST.put(18218, 3790000);
		FOUR_SEPULCHERS_DROPLIST.put(18219, 3790000);
		FOUR_SEPULCHERS_DROPLIST.put(18220, 1240000);
		FOUR_SEPULCHERS_DROPLIST.put(18221, 1270000);
		FOUR_SEPULCHERS_DROPLIST.put(18222, 1210000);
		FOUR_SEPULCHERS_DROPLIST.put(18223, 900000);
		FOUR_SEPULCHERS_DROPLIST.put(18224, 1220000);
		FOUR_SEPULCHERS_DROPLIST.put(18225, 1210000);
		FOUR_SEPULCHERS_DROPLIST.put(18226, 890000);
		FOUR_SEPULCHERS_DROPLIST.put(18227, 1530000);
		FOUR_SEPULCHERS_DROPLIST.put(18228, 1150000);
		FOUR_SEPULCHERS_DROPLIST.put(18229, 1190000);
		FOUR_SEPULCHERS_DROPLIST.put(18230, 1490000);
		
		IMPERIAL_TOMB_DROPLIST.put(21396, 1510000);
		IMPERIAL_TOMB_DROPLIST.put(21397, 500000);
		IMPERIAL_TOMB_DROPLIST.put(21398, 950000);
		IMPERIAL_TOMB_DROPLIST.put(21399, 840000);
		IMPERIAL_TOMB_DROPLIST.put(21400, 760000);
		IMPERIAL_TOMB_DROPLIST.put(21401, 670000);
		IMPERIAL_TOMB_DROPLIST.put(21402, 690000);
		IMPERIAL_TOMB_DROPLIST.put(21403, 800000);
		IMPERIAL_TOMB_DROPLIST.put(21404, 900000);
		IMPERIAL_TOMB_DROPLIST.put(21405, 640000);
		IMPERIAL_TOMB_DROPLIST.put(21406, 870000);
		IMPERIAL_TOMB_DROPLIST.put(21407, 560000);
		IMPERIAL_TOMB_DROPLIST.put(21408, 820000);
		IMPERIAL_TOMB_DROPLIST.put(21409, 920000);
		IMPERIAL_TOMB_DROPLIST.put(21410, 810000);
		IMPERIAL_TOMB_DROPLIST.put(21411, 660000);
		IMPERIAL_TOMB_DROPLIST.put(21412, 1060000);
		IMPERIAL_TOMB_DROPLIST.put(21413, 810000);
		IMPERIAL_TOMB_DROPLIST.put(21414, 1790000);
		IMPERIAL_TOMB_DROPLIST.put(21415, 800000);
		IMPERIAL_TOMB_DROPLIST.put(21416, 820000);
		IMPERIAL_TOMB_DROPLIST.put(21417, 1270000);
		IMPERIAL_TOMB_DROPLIST.put(21418, 660000);
		IMPERIAL_TOMB_DROPLIST.put(21419, 670000);
		IMPERIAL_TOMB_DROPLIST.put(21420, 820000);
		IMPERIAL_TOMB_DROPLIST.put(21421, 770000);
		IMPERIAL_TOMB_DROPLIST.put(21422, 880000);
		IMPERIAL_TOMB_DROPLIST.put(21423, 940000);
		IMPERIAL_TOMB_DROPLIST.put(21424, 1190000);
		IMPERIAL_TOMB_DROPLIST.put(21425, 1210000);
		IMPERIAL_TOMB_DROPLIST.put(21426, 1080000);
		IMPERIAL_TOMB_DROPLIST.put(21427, 740000);
		IMPERIAL_TOMB_DROPLIST.put(21428, 760000);
		IMPERIAL_TOMB_DROPLIST.put(21429, 800000);
		IMPERIAL_TOMB_DROPLIST.put(21430, 1100000);
		IMPERIAL_TOMB_DROPLIST.put(21431, 940000);
		IMPERIAL_TOMB_DROPLIST.put(21432, 1340000);
		IMPERIAL_TOMB_DROPLIST.put(21433, 1340000);
		IMPERIAL_TOMB_DROPLIST.put(21434, 1900000);
		IMPERIAL_TOMB_DROPLIST.put(21435, 1600000);
		IMPERIAL_TOMB_DROPLIST.put(21436, 1660000);
		IMPERIAL_TOMB_DROPLIST.put(21437, 690000);
		IMPERIAL_TOMB_DROPLIST.put(21798, 330000);
		IMPERIAL_TOMB_DROPLIST.put(21799, 610000);
		IMPERIAL_TOMB_DROPLIST.put(21800, 310000);
		
		setItemsIds(BROKEN_RELIC_PART);
		
		addQuestStart(GHOST_OF_ADVENTURER);
		addTalkId(GHOST_OF_ADVENTURER);
		
		addEventIds(IMPERIAL_TOMB_DROPLIST.keySet(), EventHandler.MY_DYING);
		addEventIds(FOUR_SEPULCHERS_DROPLIST.keySet(), EventHandler.MY_DYING);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31538-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31538-09.htm"))
		{
			if (player.getInventory().getItemCount(BROKEN_RELIC_PART) >= 1000)
			{
				htmltext = "31538-09.htm";
				takeItems(player, BROKEN_RELIC_PART, 1000);
				giveItems(player, Rnd.get(REWARDS), 1);
			}
			else
				htmltext = "31538-06.htm";
		}
		else if (event.equalsIgnoreCase("31538-10.htm"))
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
				htmltext = (player.getStatus().getLevel() < 74) ? "31538-02.htm" : "31538-01.htm";
				break;
			
			case STARTED:
				if (player.getInventory().getItemCount(BROKEN_RELIC_PART) >= 1000)
					htmltext = "31538-04.htm";
				else if (player.getInventory().hasItems(ENTRANCE_PASS_TO_THE_SEPULCHER))
					htmltext = "31538-06.htm";
				else
					htmltext = "31538-07.htm";
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
		
		final int npcId = npc.getNpcId();
		
		if (IMPERIAL_TOMB_DROPLIST.containsKey(npcId))
		{
			dropItems(st.getPlayer(), BROKEN_RELIC_PART, 1, 0, IMPERIAL_TOMB_DROPLIST.get(npcId));
			
			dropItems(st.getPlayer(), ENTRANCE_PASS_TO_THE_SEPULCHER, 1, 0, 33333);
		}
		else
			dropItems(st.getPlayer(), BROKEN_RELIC_PART, 1, 0, FOUR_SEPULCHERS_DROPLIST.get(npcId));
	}
}