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

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q633_InTheForgottenVillage extends Quest
{
	private static final String QUEST_NAME = "Q633_InTheForgottenVillage";
	
	private static final int MINA = 31388;
	
	private static final int RIB_BONE = 7544;
	private static final int ZOMBIE_LIVER = 7545;
	
	private static final Map<Integer, Integer> MOBS = HashMap.newHashMap(16);
	
	private static final Map<Integer, Integer> UNDEADS = HashMap.newHashMap(10);
	
	public Q633_InTheForgottenVillage()
	{
		super(633, "In the Forgotten Village");
		
		MOBS.put(21557, 328000);
		MOBS.put(21558, 328000);
		MOBS.put(21559, 337000);
		MOBS.put(21560, 337000);
		MOBS.put(21563, 342000);
		MOBS.put(21564, 348000);
		MOBS.put(21565, 351000);
		MOBS.put(21566, 359000);
		MOBS.put(21567, 359000);
		MOBS.put(21572, 365000);
		MOBS.put(21574, 383000);
		MOBS.put(21575, 383000);
		MOBS.put(21580, 385000);
		MOBS.put(21581, 395000);
		MOBS.put(21583, 397000);
		MOBS.put(21584, 401000);
		
		UNDEADS.put(21553, 347000);
		UNDEADS.put(21554, 347000);
		UNDEADS.put(21561, 450000);
		UNDEADS.put(21578, 501000);
		UNDEADS.put(21596, 359000);
		UNDEADS.put(21597, 370000);
		UNDEADS.put(21598, 441000);
		UNDEADS.put(21599, 395000);
		UNDEADS.put(21600, 408000);
		UNDEADS.put(21601, 411000);
		
		setItemsIds(RIB_BONE, ZOMBIE_LIVER);
		
		addQuestStart(MINA);
		addTalkId(MINA);
		
		for (int i : MOBS.keySet())
			addMyDying(i);
		
		for (int i : UNDEADS.keySet())
			addMyDying(i);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31388-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31388-10.htm"))
		{
			takeItems(player, RIB_BONE, -1);
			playSound(player, SOUND_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("31388-09.htm"))
		{
			if (player.getInventory().getItemCount(RIB_BONE) >= 200)
			{
				htmltext = "31388-08.htm";
				takeItems(player, RIB_BONE, 200);
				rewardItems(player, 57, 25000);
				rewardExpAndSp(player, 305235, 0);
				playSound(player, SOUND_FINISH);
			}
			st.setCond(1);
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
				htmltext = (player.getStatus().getLevel() < 65) ? "31388-03.htm" : "31388-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				if (cond == 1)
					htmltext = "31388-06.htm";
				else if (cond == 2)
					htmltext = "31388-05.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		final int npcId = npc.getNpcId();
		
		if (UNDEADS.containsKey(npcId))
		{
			final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
			if (st == null)
				return;
			
			dropItems(st.getPlayer(), ZOMBIE_LIVER, 1, 0, UNDEADS.get(npcId));
		}
		else if (MOBS.containsKey(npcId))
		{
			final QuestState st = getRandomPartyMember(player, npc, 1);
			if (st == null)
				return;
			
			if (dropItems(st.getPlayer(), RIB_BONE, 1, 200, MOBS.get(npcId)))
				st.setCond(2);
		}
	}
}