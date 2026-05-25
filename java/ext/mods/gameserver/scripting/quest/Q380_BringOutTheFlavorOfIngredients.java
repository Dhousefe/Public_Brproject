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
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q380_BringOutTheFlavorOfIngredients extends Quest
{
	private static final String QUEST_NAME = "Q380_BringOutTheFlavorOfIngredients";
	
	private static final int DIRE_WOLF = 20205;
	private static final int KADIF_WEREWOLF = 20206;
	private static final int GIANT_MIST_LEECH = 20225;
	
	private static final int RITRON_FRUIT = 5895;
	private static final int MOON_FACE_FLOWER = 5896;
	private static final int LEECH_FLUIDS = 5897;
	private static final int ANTIDOTE = 1831;
	
	private static final int RITRON_JELLY = 5960;
	private static final int JELLY_RECIPE = 5959;
	
	public Q380_BringOutTheFlavorOfIngredients()
	{
		super(380, "Bring Out the Flavor of Ingredients!");
		
		setItemsIds(RITRON_FRUIT, MOON_FACE_FLOWER, LEECH_FLUIDS);
		
		addQuestStart(30069);
		addTalkId(30069);
		
		addMyDying(DIRE_WOLF, KADIF_WEREWOLF, GIANT_MIST_LEECH);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30069-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30069-12.htm"))
		{
			giveItems(player, JELLY_RECIPE, 1);
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
				htmltext = (player.getStatus().getLevel() < 24) ? "30069-00.htm" : "30069-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				if (cond == 1)
					htmltext = "30069-06.htm";
				else if (cond == 2)
				{
					if (player.getInventory().getItemCount(ANTIDOTE) >= 2)
					{
						htmltext = "30069-07.htm";
						st.setCond(3);
						playSound(player, SOUND_MIDDLE);
						takeItems(player, RITRON_FRUIT, -1);
						takeItems(player, MOON_FACE_FLOWER, -1);
						takeItems(player, LEECH_FLUIDS, -1);
						takeItems(player, ANTIDOTE, 2);
					}
					else
						htmltext = "30069-06.htm";
				}
				else if (cond == 3)
				{
					htmltext = "30069-08.htm";
					st.setCond(4);
					playSound(player, SOUND_MIDDLE);
				}
				else if (cond == 4)
				{
					htmltext = "30069-09.htm";
					st.setCond(5);
					playSound(player, SOUND_MIDDLE);
				}
				else if (cond == 5)
				{
					htmltext = "30069-10.htm";
					st.setCond(6);
					playSound(player, SOUND_MIDDLE);
				}
				else if (cond == 6)
				{
					giveItems(player, RITRON_JELLY, 1);
					if (Rnd.get(100) < 55)
						htmltext = "30069-11.htm";
					else
					{
						htmltext = "30069-13.htm";
						playSound(player, SOUND_FINISH);
						st.exitQuest(true);
					}
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
			case DIRE_WOLF:
				if (dropItems(player, RITRON_FRUIT, 1, 4, 100000))
					if (player.getInventory().getItemCount(MOON_FACE_FLOWER) == 20 && player.getInventory().getItemCount(LEECH_FLUIDS) == 10)
						st.setCond(2);
				break;
			
			case KADIF_WEREWOLF:
				if (dropItems(player, MOON_FACE_FLOWER, 1, 20, 500000))
					if (player.getInventory().getItemCount(RITRON_FRUIT) == 4 && player.getInventory().getItemCount(LEECH_FLUIDS) == 10)
						st.setCond(2);
				break;
			
			case GIANT_MIST_LEECH:
				if (dropItems(player, LEECH_FLUIDS, 1, 10, 500000))
					if (player.getInventory().getItemCount(RITRON_FRUIT) == 4 && player.getInventory().getItemCount(MOON_FACE_FLOWER) == 20)
						st.setCond(2);
				break;
		}
	}
}