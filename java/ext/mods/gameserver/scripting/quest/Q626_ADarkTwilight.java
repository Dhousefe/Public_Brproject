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

public class Q626_ADarkTwilight extends Quest
{
	private static final String QUEST_NAME = "Q626_ADarkTwilight";
	
	private static final int BLOOD_OF_SAINT = 7169;
	
	private static final int HIERARCH = 31517;
	
	private static final Map<Integer, Integer> CHANCES = HashMap.newHashMap(14);
	
	public Q626_ADarkTwilight()
	{
		super(626, "A Dark Twilight");
		
		CHANCES.put(21520, 533000);
		CHANCES.put(21523, 566000);
		CHANCES.put(21524, 603000);
		CHANCES.put(21525, 603000);
		CHANCES.put(21526, 587000);
		CHANCES.put(21529, 606000);
		CHANCES.put(21530, 560000);
		CHANCES.put(21531, 669000);
		CHANCES.put(21532, 651000);
		CHANCES.put(21535, 672000);
		CHANCES.put(21536, 597000);
		CHANCES.put(21539, 739000);
		CHANCES.put(21540, 739000);
		CHANCES.put(21658, 669000);
		
		setItemsIds(BLOOD_OF_SAINT);
		
		addQuestStart(HIERARCH);
		addTalkId(HIERARCH);
		
		for (int npcId : CHANCES.keySet())
			addMyDying(npcId);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31517-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("reward1"))
		{
			if (player.getInventory().getItemCount(BLOOD_OF_SAINT) == 300)
			{
				htmltext = "31517-07.htm";
				takeItems(player, BLOOD_OF_SAINT, 300);
				rewardExpAndSp(player, 162773, 12500);
				playSound(player, SOUND_FINISH);
				st.exitQuest(true);
			}
			else
				htmltext = "31517-08.htm";
		}
		else if (event.equalsIgnoreCase("reward2"))
		{
			if (player.getInventory().getItemCount(BLOOD_OF_SAINT) == 300)
			{
				htmltext = "31517-07.htm";
				takeItems(player, BLOOD_OF_SAINT, 300);
				rewardItems(player, 57, 100000);
				playSound(player, SOUND_FINISH);
				st.exitQuest(true);
			}
			else
				htmltext = "31517-08.htm";
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
				htmltext = (player.getStatus().getLevel() < 60) ? "31517-02.htm" : "31517-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				if (cond == 1)
					htmltext = "31517-05.htm";
				else
					htmltext = "31517-04.htm";
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
		
		final QuestState st = getRandomPartyMember(player, npc, 1);
		if (st == null)
			return;
		
		if (dropItems(st.getPlayer(), BLOOD_OF_SAINT, 1, 300, CHANCES.get(npc.getNpcId())))
			st.setCond(2);
	}
}