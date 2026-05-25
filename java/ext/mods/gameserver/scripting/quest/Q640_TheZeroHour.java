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

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q640_TheZeroHour extends Quest
{
	private static final String QUEST_NAME = "Q640_TheZeroHour";
	
	private static final int KAHMAN = 31554;
	
	private static final int FANG_OF_STAKATO = 8085;
	
	private static final int[][] REWARDS =
	{
		{
			12,
			4042,
			1
		},
		{
			6,
			4043,
			1
		},
		{
			6,
			4044,
			1
		},
		{
			81,
			1887,
			10
		},
		{
			33,
			1888,
			5
		},
		{
			30,
			1889,
			10
		},
		{
			150,
			5550,
			10
		},
		{
			131,
			1890,
			10
		},
		{
			123,
			1893,
			5
		}
	};
	
	public Q640_TheZeroHour()
	{
		super(640, "The Zero Hour");
		
		setItemsIds(FANG_OF_STAKATO);
		
		addQuestStart(KAHMAN);
		addTalkId(KAHMAN);
		
		addMyDying(22105, 22106, 22107, 22108, 22109, 22110, 22111, 22113, 22114, 22115, 22116, 22117, 22118, 22119, 22121);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31554-02.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31554-05.htm"))
		{
			if (!player.getInventory().hasItems(FANG_OF_STAKATO))
				htmltext = "31554-06.htm";
		}
		else if (event.equalsIgnoreCase("31554-08.htm"))
		{
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (StringUtil.isDigit(event))
		{
			final int[] reward = REWARDS[Integer.parseInt(event)];
			
			if (player.getInventory().getItemCount(FANG_OF_STAKATO) >= reward[0])
			{
				htmltext = "31554-09.htm";
				takeItems(player, FANG_OF_STAKATO, reward[0]);
				rewardItems(player, reward[1], reward[2]);
			}
			else
				htmltext = "31554-06.htm";
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
				if (player.getStatus().getLevel() < 66)
					htmltext = "31554-00.htm";
				else
				{
					QuestState st2 = player.getQuestList().getQuestState("Q109_InSearchOfTheNest");
					htmltext = (st2 != null && st2.isCompleted()) ? "31554-01.htm" : "31554-10.htm";
				}
				break;
			
			case STARTED:
				htmltext = (player.getInventory().hasItems(FANG_OF_STAKATO)) ? "31554-04.htm" : "31554-03.htm";
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
		
		dropItemsAlways(st.getPlayer(), FANG_OF_STAKATO, 1, 0);
	}
}