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

import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q617_GatherTheFlames extends Quest
{
	private static final String QUEST_NAME = "Q617_GatherTheFlames";
	
	private static final int HILDA = 31271;
	private static final int VULCAN = 31539;
	private static final int ROONEY = 32049;
	
	private static final int TORCH = 7264;
	
	private static final Map<Integer, Integer> CHANCES = new HashMap<>();
	static
	{
		CHANCES.put(21381, 510000);
		CHANCES.put(21653, 510000);
		CHANCES.put(21387, 530000);
		CHANCES.put(21655, 530000);
		CHANCES.put(21390, 560000);
		CHANCES.put(21656, 690000);
		CHANCES.put(21389, 550000);
		CHANCES.put(21388, 530000);
		CHANCES.put(21383, 510000);
		CHANCES.put(21392, 560000);
		CHANCES.put(21382, 600000);
		CHANCES.put(21654, 520000);
		CHANCES.put(21384, 640000);
		CHANCES.put(21394, 510000);
		CHANCES.put(21395, 560000);
		CHANCES.put(21385, 520000);
		CHANCES.put(21391, 550000);
		CHANCES.put(21393, 580000);
		CHANCES.put(21657, 570000);
		CHANCES.put(21386, 520000);
		CHANCES.put(21652, 490000);
		CHANCES.put(21378, 490000);
		CHANCES.put(21376, 480000);
		CHANCES.put(21377, 480000);
		CHANCES.put(21379, 590000);
		CHANCES.put(21380, 490000);
	}
	
	private static final int[] REWARDS =
	{
		6881,
		6883,
		6885,
		6887,
		6891,
		6893,
		6895,
		6897,
		6899,
		7580
	};
	
	public Q617_GatherTheFlames()
	{
		super(617, "Gather the Flames");
		
		setItemsIds(TORCH);
		
		addQuestStart(VULCAN, HILDA);
		addTalkId(VULCAN, HILDA, ROONEY);
		
		for (int mobs : CHANCES.keySet())
			addMyDying(mobs);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31539-03.htm") || event.equalsIgnoreCase("31271-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31539-05.htm"))
		{
			if (player.getInventory().getItemCount(TORCH) >= 1000)
			{
				htmltext = "31539-07.htm";
				takeItems(player, TORCH, 1000);
				giveItems(player, Rnd.get(REWARDS), 1);
			}
		}
		else if (event.equalsIgnoreCase("31539-08.htm"))
		{
			takeItems(player, TORCH, -1);
			st.exitQuest(true);
		}
		else if (StringUtil.isDigit(event))
		{
			if (player.getInventory().getItemCount(TORCH) >= 1200)
			{
				htmltext = "32049-03.htm";
				takeItems(player, TORCH, 1200);
				giveItems(player, Integer.valueOf(event), 1);
			}
			else
				htmltext = "32049-02.htm";
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
				htmltext = npc.getNpcId() + ((player.getStatus().getLevel() >= 74) ? "-01.htm" : "-02.htm");
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case VULCAN:
						htmltext = (player.getInventory().getItemCount(TORCH) >= 1000) ? "31539-04.htm" : "31539-05.htm";
						break;
					
					case HILDA:
						htmltext = "31271-04.htm";
						break;
					
					case ROONEY:
						htmltext = (player.getInventory().getItemCount(TORCH) >= 1200) ? "32049-01.htm" : "32049-02.htm";
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
		
		dropItems(st.getPlayer(), TORCH, 1, 0, CHANCES.get(npc.getNpcId()));
	}
}