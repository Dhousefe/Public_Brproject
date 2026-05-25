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
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q360_PlunderTheirSupplies extends Quest
{
	private static final String QUEST_NAME = "Q360_PlunderTheirSupplies";
	
	private static final int SUPPLY_ITEM = 5872;
	private static final int SUSPICIOUS_DOCUMENT = 5871;
	private static final int RECIPE_OF_SUPPLY = 5870;
	
	private static final int[][][] DROPLIST =
	{
		{
			{
				SUSPICIOUS_DOCUMENT,
				1,
				0,
				50000
			},
			{
				SUPPLY_ITEM,
				1,
				0,
				500000
			}
		},
		{
			{
				SUSPICIOUS_DOCUMENT,
				1,
				0,
				50000
			},
			{
				SUPPLY_ITEM,
				1,
				0,
				660000
			}
		}
	};
	
	public Q360_PlunderTheirSupplies()
	{
		super(360, "Plunder Their Supplies");
		
		setItemsIds(RECIPE_OF_SUPPLY, SUPPLY_ITEM, SUSPICIOUS_DOCUMENT);
		
		addQuestStart(30873);
		addTalkId(30873);
		
		addMyDying(20666, 20669);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30873-2.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30873-6.htm"))
		{
			takeItems(player, SUPPLY_ITEM, -1);
			takeItems(player, SUSPICIOUS_DOCUMENT, -1);
			takeItems(player, RECIPE_OF_SUPPLY, -1);
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
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
				htmltext = (player.getStatus().getLevel() < 52) ? "30873-0a.htm" : "30873-0.htm";
				break;
			
			case STARTED:
				final int supplyItems = player.getInventory().getItemCount(SUPPLY_ITEM);
				if (supplyItems == 0)
					htmltext = "30873-3.htm";
				else
				{
					final int reward = 6000 + (supplyItems * 100) + (player.getInventory().getItemCount(RECIPE_OF_SUPPLY) * 6000);
					
					htmltext = "30873-5.htm";
					takeItems(player, SUPPLY_ITEM, -1);
					takeItems(player, RECIPE_OF_SUPPLY, -1);
					rewardItems(player, 57, reward);
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
		
		dropMultipleItems(player, DROPLIST[(npc.getNpcId() == 20666) ? 0 : 1]);
		
		if (player.getInventory().getItemCount(SUSPICIOUS_DOCUMENT) == 5)
		{
			takeItems(player, SUSPICIOUS_DOCUMENT, 5);
			giveItems(player, RECIPE_OF_SUPPLY, 1);
		}
	}
}