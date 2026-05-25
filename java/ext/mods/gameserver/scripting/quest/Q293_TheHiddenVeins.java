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
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q293_TheHiddenVeins extends Quest
{
	private static final String QUEST_NAME = "Q293_TheHiddenVeins";
	
	private static final int CHRYSOLITE_ORE = 1488;
	private static final int TORN_MAP_FRAGMENT = 1489;
	private static final int HIDDEN_VEIN_MAP = 1490;
	
	private static final int FILAUR = 30535;
	private static final int CHINCHIRIN = 30539;
	
	private static final int UTUKU_ORC = 20446;
	private static final int UTUKU_ARCHER = 20447;
	private static final int UTUKU_GRUNT = 20448;
	
	public Q293_TheHiddenVeins()
	{
		super(293, "The Hidden Veins");
		
		setItemsIds(CHRYSOLITE_ORE, TORN_MAP_FRAGMENT, HIDDEN_VEIN_MAP);
		
		addQuestStart(FILAUR);
		addTalkId(FILAUR, CHINCHIRIN);
		
		addMyDying(UTUKU_ORC, UTUKU_ARCHER, UTUKU_GRUNT);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30535-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30535-06.htm"))
		{
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30539-02.htm"))
		{
			if (player.getInventory().getItemCount(TORN_MAP_FRAGMENT) >= 4)
			{
				htmltext = "30539-03.htm";
				playSound(player, SOUND_ITEMGET);
				takeItems(player, TORN_MAP_FRAGMENT, 4);
				giveItems(player, HIDDEN_VEIN_MAP, 1);
			}
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
				if (player.getRace() != ClassRace.DWARF)
					htmltext = "30535-00.htm";
				else if (player.getStatus().getLevel() < 6)
					htmltext = "30535-01.htm";
				else
					htmltext = "30535-02.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case FILAUR:
						final int ores = player.getInventory().getItemCount(CHRYSOLITE_ORE);
						final int maps = player.getInventory().getItemCount(HIDDEN_VEIN_MAP);
						
						if (ores + maps == 0)
							htmltext = "30535-04.htm";
						else
						{
							htmltext = (maps > 0) ? ((ores > 0) ? "30535-09.htm" : "30535-08.htm") : "30535-05.htm";
							takeItems(player, CHRYSOLITE_ORE, -1);
							takeItems(player, HIDDEN_VEIN_MAP, -1);
							
							int reward = (ores * 5) + (maps * 500);
							if (ores >= 10)
								reward += 2000;
							
							rewardItems(player, 57, reward);
							rewardNewbieShots(player, 6000, 0);
						}
						break;
					
					case CHINCHIRIN:
						htmltext = "30539-01.htm";
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
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		final int chance = Rnd.get(100);
		
		if (chance > 50)
			dropItemsAlways(player, CHRYSOLITE_ORE, 1, 0);
		else if (chance < 5)
			dropItemsAlways(player, TORN_MAP_FRAGMENT, 1, 0);
	}
}