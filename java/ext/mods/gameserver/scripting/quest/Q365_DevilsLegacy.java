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

public class Q365_DevilsLegacy extends Quest
{
	private static final String QUEST_NAME = "Q365_DevilsLegacy";
	
	private static final int RANDOLF = 30095;
	private static final int COLLOB = 30092;
	
	protected static final int PIRATE_ZOMBIE = 20836;
	protected static final int PIRATE_ZOMBIE_CAPTAIN_1 = 20845;
	protected static final int PIRATE_ZOMBIE_CAPTAIN_2 = 21629;
	protected static final int PIRATE_ZOMBIE_CAPTAIN_3 = 21630;
	
	private static final int PIRATE_TREASURE_CHEST = 5873;
	
	public Q365_DevilsLegacy()
	{
		super(365, "Devil's Legacy");
		
		setItemsIds(PIRATE_TREASURE_CHEST);
		
		addQuestStart(RANDOLF);
		addTalkId(RANDOLF, COLLOB);
		
		addMyDying(PIRATE_ZOMBIE, PIRATE_ZOMBIE_CAPTAIN_1, PIRATE_ZOMBIE_CAPTAIN_2, PIRATE_ZOMBIE_CAPTAIN_3);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30095-02.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30095-06.htm"))
		{
			playSound(player, SOUND_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30092-05.htm"))
		{
			if (!player.getInventory().hasItems(PIRATE_TREASURE_CHEST))
				htmltext = "30092-02.htm";
			else if (player.getInventory().getItemCount(57) < 600)
				htmltext = "30092-03.htm";
			else if (st.getInteger("memoState") == 1)
				htmltext = "30092-04.htm";
			else
			{
				takeItems(player, PIRATE_TREASURE_CHEST, 1);
				takeItems(player, 57, 600);
				
				int i0;
				if (Rnd.get(100) < 80)
				{
					i0 = Rnd.get(100);
					if (i0 < 1)
						giveItems(player, 955, 1);
					else if (i0 < 4)
						giveItems(player, 956, 1);
					else if (i0 < 36)
						giveItems(player, 1868, 1);
					else if (i0 < 68)
						giveItems(player, 1884, 1);
					else
						giveItems(player, 1872, 1);
					
					htmltext = "30092-05.htm";
				}
				else
				{
					i0 = Rnd.get(1000);
					if (i0 < 10)
						giveItems(player, 951, 1);
					else if (i0 < 40)
						giveItems(player, 952, 1);
					else if (i0 < 60)
						giveItems(player, 955, 1);
					else if (i0 < 260)
						giveItems(player, 956, 1);
					else if (i0 < 445)
						giveItems(player, 1879, 1);
					else if (i0 < 630)
						giveItems(player, 1880, 1);
					else if (i0 < 815)
						giveItems(player, 1882, 1);
					else
						giveItems(player, 1881, 1);
					
					htmltext = "30092-06.htm";
					
					st.set("memoState", 1);
					npc.getAI().addCastDesire(player, 4035, 2, 1000000);
				}
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
				htmltext = (player.getStatus().getLevel() < 39) ? "30095-00.htm" : "30095-01.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case RANDOLF:
						if (!player.getInventory().hasItems(PIRATE_TREASURE_CHEST))
							htmltext = "30095-03.htm";
						else
						{
							htmltext = "30095-05.htm";
							
							int reward = player.getInventory().getItemCount(PIRATE_TREASURE_CHEST);
							
							takeItems(player, PIRATE_TREASURE_CHEST, -1);
							rewardItems(player, 57, (reward * 400) + 19800);
						}
						break;
					
					case COLLOB:
						htmltext = st.getCond() != 1 ? "30092-04.htm" : "30092-01.htm";
						break;
				}
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
		
		dropItems(st.getPlayer(), PIRATE_TREASURE_CHEST, 1, 0, (npc.getNpcId() == PIRATE_ZOMBIE) ? 360000 : 520000);
	}
}