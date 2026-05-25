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

public class Q688_DefeatTheElrokianRaiders extends Quest
{
	private static final String QUEST_NAME = "Q688_DefeatTheElrokianRaiders";
	
	private static final int DINOSAUR_FANG_NECKLACE = 8785;
	
	private static final int DINN = 32105;
	
	private static final int ELROKI = 22214;
	
	public Q688_DefeatTheElrokianRaiders()
	{
		super(688, "Defeat the Elrokian Raiders!");
		
		setItemsIds(DINOSAUR_FANG_NECKLACE);
		
		addQuestStart(DINN);
		addTalkId(DINN);
		
		addMyDying(ELROKI);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("32105-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("32105-08.htm"))
		{
			final int count = player.getInventory().getItemCount(DINOSAUR_FANG_NECKLACE);
			if (count > 0)
			{
				takeItems(player, DINOSAUR_FANG_NECKLACE, -1);
				rewardItems(player, 57, count * 3000);
			}
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("32105-06.htm"))
		{
			final int count = player.getInventory().getItemCount(DINOSAUR_FANG_NECKLACE);
			
			takeItems(player, DINOSAUR_FANG_NECKLACE, -1);
			rewardItems(player, 57, count * 3000);
		}
		else if (event.equalsIgnoreCase("32105-07.htm"))
		{
			final int count = player.getInventory().getItemCount(DINOSAUR_FANG_NECKLACE);
			if (count >= 100)
			{
				takeItems(player, DINOSAUR_FANG_NECKLACE, 100);
				rewardItems(player, 57, 450000);
			}
			else
				htmltext = "32105-04.htm";
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
				htmltext = (player.getStatus().getLevel() < 75) ? "32105-00.htm" : "32105-01.htm";
				break;
			
			case STARTED:
				htmltext = (!player.getInventory().hasItems(DINOSAUR_FANG_NECKLACE)) ? "32105-04.htm" : "32105-05.htm";
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
		
		dropItems(st.getPlayer(), DINOSAUR_FANG_NECKLACE, 1, 0, 500000);
	}
}