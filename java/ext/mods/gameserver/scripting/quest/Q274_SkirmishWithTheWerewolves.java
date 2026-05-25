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

public class Q274_SkirmishWithTheWerewolves extends Quest
{
	private static final String QUEST_NAME = "Q274_SkirmishWithTheWerewolves";
	
	private static final int NECKLACE_OF_VALOR = 1507;
	private static final int NECKLACE_OF_COURAGE = 1506;
	
	private static final int MARAKU_WEREWOLF_HEAD = 1477;
	private static final int MARAKU_WOLFMEN_TOTEM = 1501;
	
	public Q274_SkirmishWithTheWerewolves()
	{
		super(274, "Skirmish with the Werewolves");
		
		setItemsIds(MARAKU_WEREWOLF_HEAD, MARAKU_WOLFMEN_TOTEM);
		
		addQuestStart(30569);
		addTalkId(30569);
		
		addMyDying(20363, 20364);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		String htmltext = event;
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30569-03.htm"))
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
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() != ClassRace.ORC)
					htmltext = "30569-00.htm";
				else if (player.getStatus().getLevel() < 9)
					htmltext = "30569-01.htm";
				else if (player.getInventory().hasAtLeastOneItem(NECKLACE_OF_COURAGE, NECKLACE_OF_VALOR))
					htmltext = "30569-02.htm";
				else
					htmltext = "30569-07.htm";
				break;
			
			case STARTED:
				if (st.getCond() == 1)
					htmltext = "30569-04.htm";
				else
				{
					htmltext = "30569-05.htm";
					
					int amount = 3500 + player.getInventory().getItemCount(MARAKU_WOLFMEN_TOTEM) * 600;
					
					takeItems(player, MARAKU_WEREWOLF_HEAD, -1);
					takeItems(player, MARAKU_WOLFMEN_TOTEM, -1);
					rewardItems(player, 57, amount);
					
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
		
		if (dropItemsAlways(player, MARAKU_WEREWOLF_HEAD, 1, 40))
			st.setCond(2);
		
		if (Rnd.get(100) < 6)
			giveItems(player, MARAKU_WOLFMEN_TOTEM, 1);
	}
}