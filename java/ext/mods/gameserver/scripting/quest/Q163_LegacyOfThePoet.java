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
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q163_LegacyOfThePoet extends Quest
{
	private static final String QUEST_NAME = "Q163_LegacyOfThePoet";
	
	private static final int STARDEN = 30220;
	
	private static final int[] RUMIELS_POEMS =
	{
		1038,
		1039,
		1040,
		1041
	};
	
	private static final int[][] DROPLIST =
	{
		{
			RUMIELS_POEMS[0],
			1,
			1,
			100000
		},
		{
			RUMIELS_POEMS[1],
			1,
			1,
			200000
		},
		{
			RUMIELS_POEMS[2],
			1,
			1,
			200000
		},
		{
			RUMIELS_POEMS[3],
			1,
			1,
			400000
		}
	};
	
	public Q163_LegacyOfThePoet()
	{
		super(163, "Legacy of the Poet");
		
		setItemsIds(RUMIELS_POEMS);
		
		addQuestStart(STARDEN);
		addTalkId(STARDEN);
		
		addMyDying(20372, 20373);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30220-07.htm"))
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
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() == ClassRace.DARK_ELF)
					htmltext = "30220-00.htm";
				else if (player.getStatus().getLevel() < 11)
					htmltext = "30220-02.htm";
				else
					htmltext = "30220-03.htm";
				break;
			
			case STARTED:
				if (st.getCond() == 2)
				{
					htmltext = "30220-09.htm";
					
					for (int poem : RUMIELS_POEMS)
						takeItems(player, poem, -1);
					
					rewardItems(player, 57, 13890);
					playSound(player, SOUND_FINISH);
					st.exitQuest(false);
				}
				else
					htmltext = "30220-08.htm";
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerCondition(player, npc, 1);
		if (st == null)
			return;
		
		if (dropMultipleItems(player, DROPLIST))
			st.setCond(2);
	}
}