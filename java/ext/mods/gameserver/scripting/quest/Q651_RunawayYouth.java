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

public class Q651_RunawayYouth extends Quest
{
	private static final String QUEST_NAME = "Q651_RunawayYouth";
	
	private static final int IVAN = 32014;
	private static final int BATIDAE = 31989;
	
	private static final int SCROLL_OF_ESCAPE = 736;
	
	private static final SpawnLocation[] SPAWNS =
	{
		new SpawnLocation(118600, -161235, -1119, 0),
		new SpawnLocation(108380, -150268, -2376, 0),
		new SpawnLocation(123254, -148126, -3425, 0)
	};
	
	private int _currentPosition = 0;
	
	public Q651_RunawayYouth()
	{
		super(651, "Runaway Youth");
		
		addQuestStart(IVAN);
		addTalkId(IVAN, BATIDAE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32014-04.htm"))
		{
			if (player.getInventory().hasItems(SCROLL_OF_ESCAPE))
			{
				htmltext = "32014-03.htm";
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
				takeItems(player, SCROLL_OF_ESCAPE, 1);
				
				startQuestTimer("65101", npc, null, 3000);
			}
			else
				st.exitQuest(true);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("65101"))
		{
			int chance = Rnd.get(3);
			
			while (chance == _currentPosition)
				chance = Rnd.get(3);
			
			_currentPosition = chance;
			
			npc.deleteMe();
			addSpawn(IVAN, SPAWNS[chance], false, 0, false);
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
				htmltext = (player.getStatus().getLevel() < 26) ? "32014-01.htm" : "32014-02.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case BATIDAE:
						htmltext = "31989-01.htm";
						rewardItems(player, 57, 2883);
						playSound(player, SOUND_FINISH);
						st.exitQuest(true);
						break;
					
					case IVAN:
						htmltext = "32014-04a.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
}