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

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q004_LongliveThePaagrioLord extends Quest
{
	private static final String QUEST_NAME = "Q004_LongliveThePaagrioLord";
	
	private static final Map<Integer, Integer> NPC_GIFTS = HashMap.newHashMap(6);
	
	public Q004_LongliveThePaagrioLord()
	{
		super(4, "Long live the Pa'agrio Lord!");
		
		NPC_GIFTS.put(30585, 1542);
		NPC_GIFTS.put(30566, 1541);
		NPC_GIFTS.put(30562, 1543);
		NPC_GIFTS.put(30560, 1544);
		NPC_GIFTS.put(30559, 1545);
		NPC_GIFTS.put(30587, 1546);
		
		setItemsIds(1541, 1542, 1543, 1544, 1545, 1546);
		
		addQuestStart(30578);
		addTalkId(30578, 30585, 30566, 30562, 30560, 30559, 30587);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30578-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
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
				if (player.getRace() != ClassRace.ORC)
					htmltext = "30578-00.htm";
				else if (player.getStatus().getLevel() < 2)
					htmltext = "30578-01.htm";
				else
					htmltext = "30578-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				int npcId = npc.getNpcId();
				
				if (npcId == 30578)
				{
					if (cond == 1)
						htmltext = "30578-04.htm";
					else if (cond == 2)
					{
						htmltext = "30578-06.htm";
						giveItems(player, 4, 1);
						for (int item : NPC_GIFTS.values())
							takeItems(player, item, -1);
						
						playSound(player, SOUND_FINISH);
						st.exitQuest(false);
					}
				}
				else
				{
					int i = NPC_GIFTS.get(npcId);
					if (player.getInventory().hasItems(i))
						htmltext = npcId + "-02.htm";
					else
					{
						giveItems(player, i, 1);
						htmltext = npcId + "-01.htm";
						
						int count = 0;
						for (int item : NPC_GIFTS.values())
							count += player.getInventory().getItemCount(item);
						
						if (count == 6)
						{
							st.setCond(2);
							playSound(player, SOUND_MIDDLE);
						}
						else
							playSound(player, SOUND_ITEMGET);
					}
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
}