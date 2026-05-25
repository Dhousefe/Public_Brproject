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

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q652_AnAgedExAdventurer extends Quest
{
	private static final String QUEST_NAME = "Q652_AnAgedExAdventurer";
	
	private static final int TANTAN = 32012;
	private static final int SARA = 30180;
	
	private static final int SOULSHOT_C = 1464;
	
	private static final int ENCHANT_ARMOR_D = 956;
	
	private static final SpawnLocation[] SPAWNS =
	{
		new SpawnLocation(78355, -1325, -3659, 0),
		new SpawnLocation(79890, -6132, -2922, 0),
		new SpawnLocation(90012, -7217, -3085, 0),
		new SpawnLocation(94500, -10129, -3290, 0),
		new SpawnLocation(96534, -1237, -3677, 0)
	};
	
	private int _currentPosition = 0;
	
	public Q652_AnAgedExAdventurer()
	{
		super(652, "An Aged Ex-Adventurer");
		
		addQuestStart(TANTAN);
		addTalkId(TANTAN, SARA);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32012-02.htm"))
		{
			if (player.getInventory().getItemCount(SOULSHOT_C) >= 100)
			{
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
				takeItems(player, SOULSHOT_C, 100);
				
				startQuestTimer("65201", npc, null, 3000);
			}
			else
			{
				htmltext = "32012-02a.htm";
				st.exitQuest(true);
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("65201"))
		{
			int chance = Rnd.get(5);
			
			while (chance == _currentPosition)
				chance = Rnd.get(5);
			
			_currentPosition = chance;
			
			npc.deleteMe();
			addSpawn(TANTAN, SPAWNS[chance], false, 0, false);
		}
		
		return null;
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
				htmltext = (player.getStatus().getLevel() < 46) ? "32012-00.htm" : "32012-01.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case SARA:
						if (Rnd.get(100) < 50)
						{
							htmltext = "30180-01.htm";
							rewardItems(player, 57, 5026);
							giveItems(player, ENCHANT_ARMOR_D, 1);
						}
						else
						{
							htmltext = "30180-02.htm";
							rewardItems(player, 57, 10000);
						}
						playSound(player, SOUND_FINISH);
						st.exitQuest(true);
						break;
					
					case TANTAN:
						htmltext = "32012-04a.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
}