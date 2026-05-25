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

public class Q275_DarkWingedSpies extends Quest
{
	private static final String QUEST_NAME = "Q275_DarkWingedSpies";
	
	private static final int DARKWING_BAT = 20316;
	private static final int VARANGKA_TRACKER = 27043;
	
	private static final int DARKWING_BAT_FANG = 1478;
	private static final int VARANGKA_PARASITE = 1479;
	
	public Q275_DarkWingedSpies()
	{
		super(275, "Dark Winged Spies");
		
		setItemsIds(DARKWING_BAT_FANG, VARANGKA_PARASITE);
		
		addQuestStart(30567);
		addTalkId(30567);
		
		addMyDying(DARKWING_BAT, VARANGKA_TRACKER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30567-03.htm"))
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
				if (player.getRace() != ClassRace.ORC)
					htmltext = "30567-00.htm";
				else if (player.getStatus().getLevel() < 11)
					htmltext = "30567-01.htm";
				else
					htmltext = "30567-02.htm";
				break;
			
			case STARTED:
				if (st.getCond() == 1)
					htmltext = "30567-04.htm";
				else
				{
					htmltext = "30567-05.htm";
					takeItems(player, DARKWING_BAT_FANG, -1);
					takeItems(player, VARANGKA_PARASITE, -1);
					rewardItems(player, 57, 4200);
					playSound(player, SOUND_FINISH);
					st.exitQuest(true);
				}
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
		
		switch (npc.getNpcId())
		{
			case DARKWING_BAT:
				if (dropItemsAlways(player, DARKWING_BAT_FANG, 1, 70))
					st.setCond(2);
				else if (Rnd.get(100) < 10 && player.getInventory().getItemCount(DARKWING_BAT_FANG) > 10 && player.getInventory().getItemCount(DARKWING_BAT_FANG) < 66)
				{
					addSpawn(VARANGKA_TRACKER, npc, true, 0, true);
					
					giveItems(player, VARANGKA_PARASITE, 1);
				}
				break;
			
			case VARANGKA_TRACKER:
				if (player.getInventory().hasItems(VARANGKA_PARASITE))
				{
					takeItems(player, VARANGKA_PARASITE, -1);
					
					if (dropItemsAlways(player, DARKWING_BAT_FANG, 5, 70))
						st.setCond(2);
				}
				break;
		}
	}
}