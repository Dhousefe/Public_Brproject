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
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q604_DaimonTheWhiteEyed_Part2 extends Quest
{
	private static final String QUEST_NAME = "Q604_DaimonTheWhiteEyed_Part2";
	
	private static final int DAIMON_THE_WHITE_EYED = 25290;
	
	private static final int EYE_OF_ARGOS = 31683;
	private static final int DAIMON_ALTAR = 31541;
	
	private static final int UNFINISHED_SUMMON_CRYSTAL = 7192;
	private static final int SUMMON_CRYSTAL = 7193;
	private static final int ESSENCE_OF_DAIMON = 7194;
	private static final int[] REWARDS =
	{
		4595,
		4596,
		4597,
		4598,
		4599,
		4600
	};
	
	private Npc _npc;
	private Npc _raid;
	
	public Q604_DaimonTheWhiteEyed_Part2()
	{
		super(604, "Daimon The White-Eyed - Part 2");
		
		setItemsIds(SUMMON_CRYSTAL, ESSENCE_OF_DAIMON);
		
		addQuestStart(EYE_OF_ARGOS);
		addTalkId(EYE_OF_ARGOS, DAIMON_ALTAR);
		
		addDecayed(DAIMON_THE_WHITE_EYED);
		addMyDying(DAIMON_THE_WHITE_EYED);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31683-03.htm"))
		{
			if (player.getInventory().hasItems(UNFINISHED_SUMMON_CRYSTAL))
			{
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
				takeItems(player, UNFINISHED_SUMMON_CRYSTAL, 1);
				giveItems(player, SUMMON_CRYSTAL, 1);
			}
			else
				htmltext = "31683-04.htm";
		}
		else if (event.equalsIgnoreCase("31683-08.htm"))
		{
			if (player.getInventory().hasItems(ESSENCE_OF_DAIMON))
			{
				takeItems(player, ESSENCE_OF_DAIMON, 1);
				rewardItems(player, Rnd.get(REWARDS), 5);
				playSound(player, SOUND_FINISH);
				st.exitQuest(true);
			}
			else
				htmltext = "31683-09.htm";
		}
		else if (event.equalsIgnoreCase("31541-02.htm"))
		{
			if (player.getInventory().hasItems(SUMMON_CRYSTAL))
			{
				if (_raid == null)
				{
					_raid = addSpawn(DAIMON_THE_WHITE_EYED, 186320, -43904, -3175, Rnd.get(65536), false, 1200000, false);
					_raid.broadcastNpcSay(NpcStringId.ID_60403);
					
					_npc = npc;
					_npc.deleteMe();
					
					st.setCond(2);
					playSound(player, SOUND_MIDDLE);
					takeItems(player, SUMMON_CRYSTAL, 1);
				}
				else
					htmltext = "31541-04.htm";
			}
			else
				htmltext = "31541-03.htm";
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
				if (player.getStatus().getLevel() < 73)
				{
					htmltext = "31683-02.htm";
					st.exitQuest(true);
				}
				else
					htmltext = "31683-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case EYE_OF_ARGOS:
						if (cond == 1)
							htmltext = "31683-05.htm";
						else if (cond == 2)
							htmltext = "31683-06.htm";
						else
							htmltext = "31683-07.htm";
						break;
					
					case DAIMON_ALTAR:
						if (cond == 1)
							htmltext = "31541-01.htm";
						else if (cond == 2)
							htmltext = "31541-05.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onDecayed(Npc npc)
	{
		if (npc == _raid)
		{
			if (!_raid.isDead())
			{
				_npc.getSpawn().doRespawn(_npc);
				
				_raid.broadcastNpcSay(NpcStringId.ID_60404);
			}
			
			_npc = null;
			_raid = null;
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = getRandomPartyMember(player, npc, 2);
		if (st == null)
			return;
		
		st.setCond(3);
		playSound(st.getPlayer(), SOUND_MIDDLE);
		giveItems(st.getPlayer(), ESSENCE_OF_DAIMON, 1);
	}
}