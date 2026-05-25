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

public class Q653_WildMaiden extends Quest
{
	private static final String QUEST_NAME = "Q653_WildMaiden";
	
	private static final int SUKI = 32013;
	private static final int GALIBREDO = 30181;
	
	private static final int SCROLL_OF_ESCAPE = 736;
	
	private static final SpawnLocation[] SPAWNS =
	{
		new SpawnLocation(66578, 72351, -3731, 0),
		new SpawnLocation(77189, 73610, -3708, 2555),
		new SpawnLocation(71809, 67377, -3675, 29130),
		new SpawnLocation(69166, 88825, -3447, 43886)
	};
	
	private int _currentPosition = 0;
	
	public Q653_WildMaiden()
	{
		super(653, "Wild Maiden");
		
		addQuestStart(SUKI);
		addTalkId(SUKI, GALIBREDO);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32013-03.htm"))
		{
			if (player.getInventory().hasItems(SCROLL_OF_ESCAPE))
			{
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
				takeItems(player, SCROLL_OF_ESCAPE, 1);
				
				startQuestTimer("65301", npc, null, 3000);
			}
			else
			{
				htmltext = "32013-03a.htm";
				st.exitQuest(true);
			}
		}
		
		return htmltext;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("65301"))
		{
			int chance = Rnd.get(4);
			
			while (chance == _currentPosition)
				chance = Rnd.get(4);
			
			_currentPosition = chance;
			
			npc.deleteMe();
			addSpawn(SUKI, SPAWNS[chance], false, 0, false);
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
				htmltext = (player.getStatus().getLevel() < 36) ? "32013-01.htm" : "32013-02.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case GALIBREDO:
						htmltext = "30181-01.htm";
						rewardItems(player, 57, 2883);
						playSound(player, SOUND_FINISH);
						st.exitQuest(true);
						break;
					
					case SUKI:
						htmltext = "32013-04a.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
}