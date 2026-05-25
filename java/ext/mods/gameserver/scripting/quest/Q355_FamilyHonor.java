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

public class Q355_FamilyHonor extends Quest
{
	private static final String QUEST_NAME = "Q355_FamilyHonor";
	
	private static final int GALIBREDO = 30181;
	private static final int PATRIN = 30929;
	
	private static final int TIMAK_ORC_TROOP_LEADER = 20767;
	private static final int TIMAK_ORC_TROOP_SHAMAN = 20768;
	private static final int TIMAK_ORC_TROOP_WARRIOR = 20769;
	private static final int TIMAK_ORC_TROOP_ARCHER = 20770;
	
	private static final int GALIBREDO_BUST = 4252;
	private static final int WORK_OF_BERONA = 4350;
	private static final int STATUE_PROTOTYPE = 4351;
	private static final int STATUE_ORIGINAL = 4352;
	private static final int STATUE_REPLICA = 4353;
	private static final int STATUE_FORGERY = 4354;
	
	private static final Map<Integer, int[]> CHANCES = HashMap.newHashMap(4);
	
	public Q355_FamilyHonor()
	{
		super(355, "Family Honor");
		
		CHANCES.put(TIMAK_ORC_TROOP_LEADER, new int[]
		{
			44,
			54
		});
		CHANCES.put(TIMAK_ORC_TROOP_SHAMAN, new int[]
		{
			36,
			45
		});
		CHANCES.put(TIMAK_ORC_TROOP_WARRIOR, new int[]
		{
			35,
			43
		});
		CHANCES.put(TIMAK_ORC_TROOP_ARCHER, new int[]
		{
			32,
			42
		});
		
		setItemsIds(GALIBREDO_BUST);
		
		addQuestStart(GALIBREDO);
		addTalkId(GALIBREDO, PATRIN);
		
		addMyDying(TIMAK_ORC_TROOP_LEADER, TIMAK_ORC_TROOP_SHAMAN, TIMAK_ORC_TROOP_WARRIOR, TIMAK_ORC_TROOP_ARCHER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30181-2.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30181-4b.htm"))
		{
			final int count = player.getInventory().getItemCount(GALIBREDO_BUST);
			if (count > 0)
			{
				htmltext = "30181-4.htm";
				
				int reward = 2800 + (count * 120);
				if (count >= 100)
				{
					htmltext = "30181-4a.htm";
					reward += 5000;
				}
				
				takeItems(player, GALIBREDO_BUST, count);
				rewardItems(player, 57, reward);
			}
		}
		else if (event.equalsIgnoreCase("30929-7.htm"))
		{
			if (player.getInventory().hasItems(WORK_OF_BERONA))
			{
				takeItems(player, WORK_OF_BERONA, 1);
				
				final int appraising = Rnd.get(100);
				if (appraising < 20)
					htmltext = "30929-2.htm";
				else if (appraising < 40)
				{
					htmltext = "30929-3.htm";
					giveItems(player, STATUE_REPLICA, 1);
				}
				else if (appraising < 60)
				{
					htmltext = "30929-4.htm";
					giveItems(player, STATUE_ORIGINAL, 1);
				}
				else if (appraising < 80)
				{
					htmltext = "30929-5.htm";
					giveItems(player, STATUE_FORGERY, 1);
				}
				else
				{
					htmltext = "30929-6.htm";
					giveItems(player, STATUE_PROTOTYPE, 1);
				}
			}
		}
		else if (event.equalsIgnoreCase("30181-6.htm"))
		{
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
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
				htmltext = (player.getStatus().getLevel() < 36) ? "30181-0a.htm" : "30181-0.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case GALIBREDO:
						htmltext = (player.getInventory().hasItems(GALIBREDO_BUST)) ? "30181-3a.htm" : "30181-3.htm";
						break;
					
					case PATRIN:
						htmltext = "30929-0.htm";
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
		
		final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		final int[] chances = CHANCES.get(npc.getNpcId());
		final int random = Rnd.get(100);
		
		if (random < chances[1])
			dropItemsAlways(st.getPlayer(), (random < chances[0]) ? GALIBREDO_BUST : WORK_OF_BERONA, 1, 0);
	}
}