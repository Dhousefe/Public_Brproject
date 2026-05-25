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

public class Q610_MagicalPowerOfWater_Part2 extends Quest
{
	private static final String QUEST_NAME = "Q610_MagicalPowerOfWater_Part2";
	
	private static final int SOUL_OF_WATER_ASHUTAR = 25316;
	
	private static final int ASEFA = 31372;
	private static final int VARKAS_HOLY_ALTAR = 31560;
	
	private static final int GREEN_TOTEM = 7238;
	private static final int ICE_HEART_OF_ASHUTAR = 7239;
	
	private Npc _npc;
	private Npc _raid;
	
	public Q610_MagicalPowerOfWater_Part2()
	{
		super(610, "Magical Power of Water - Part 2");
		
		setItemsIds(ICE_HEART_OF_ASHUTAR);
		
		addQuestStart(ASEFA);
		addTalkId(ASEFA, VARKAS_HOLY_ALTAR);
		
		addDecayed(SOUL_OF_WATER_ASHUTAR);
		addMyDying(SOUL_OF_WATER_ASHUTAR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31372-04.htm"))
		{
			if (player.getInventory().hasItems(GREEN_TOTEM))
			{
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
			}
			else
				htmltext = "31372-02.htm";
		}
		else if (event.equalsIgnoreCase("31372-07.htm"))
		{
			if (player.getInventory().hasItems(ICE_HEART_OF_ASHUTAR))
			{
				takeItems(player, ICE_HEART_OF_ASHUTAR, 1);
				rewardExpAndSp(player, 10000, 0);
				playSound(player, SOUND_FINISH);
				st.exitQuest(true);
			}
			else
				htmltext = "31372-08.htm";
		}
		else if (event.equalsIgnoreCase("31560-02.htm"))
		{
			if (player.getInventory().hasItems(GREEN_TOTEM))
			{
				if (_raid == null)
				{
					_raid = addSpawn(SOUL_OF_WATER_ASHUTAR, 104825, -36926, -1136, Rnd.get(65536), false, 1200000, false);
					_raid.broadcastNpcSay(NpcStringId.ID_61050);
					
					_npc = npc;
					_npc.deleteMe();
					
					st.setCond(2);
					playSound(player, SOUND_MIDDLE);
					takeItems(player, GREEN_TOTEM, 1);
				}
				else
					htmltext = "31560-04.htm";
			}
			else
				htmltext = "31560-03.htm";
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
				if (!player.getInventory().hasItems(GREEN_TOTEM))
					htmltext = "31372-02.htm";
				else if (player.getStatus().getLevel() < 75 && player.getAllianceWithVarkaKetra() < 2)
					htmltext = "31372-03.htm";
				else
					htmltext = "31372-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case ASEFA:
						htmltext = (cond < 3) ? "31372-05.htm" : "31372-06.htm";
						break;
					
					case VARKAS_HOLY_ALTAR:
						if (cond == 1)
							htmltext = "31560-01.htm";
						else if (cond == 2)
							htmltext = "31560-05.htm";
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
				
				_raid.broadcastNpcSay(NpcStringId.ID_61051);
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
		giveItems(st.getPlayer(), ICE_HEART_OF_ASHUTAR, 1);
	}
}