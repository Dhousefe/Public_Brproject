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

public class Q264_KeenClaws extends Quest
{
	private static final String QUEST_NAME = "Q264_KeenClaws";
	
	private static final int WOLF_CLAW = 1367;
	
	private static final int LEATHER_SANDALS = 36;
	private static final int WOODEN_HELMET = 43;
	private static final int STOCKINGS = 462;
	private static final int HEALING_POTION = 1061;
	private static final int SHORT_GLOVES = 48;
	private static final int CLOTH_SHOES = 35;
	
	public Q264_KeenClaws()
	{
		super(264, "Keen Claws");
		
		setItemsIds(WOLF_CLAW);
		
		addQuestStart(30136);
		addTalkId(30136);
		
		addMyDying(20003, 20456);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30136-03.htm"))
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
				htmltext = (player.getStatus().getLevel() < 3) ? "30136-01.htm" : "30136-02.htm";
				break;
			
			case STARTED:
				final int count = player.getInventory().getItemCount(WOLF_CLAW);
				if (count < 50)
					htmltext = "30136-04.htm";
				else
				{
					htmltext = "30136-05.htm";
					takeItems(player, WOLF_CLAW, -1);
					
					final int n = Rnd.get(17);
					if (n == 0)
					{
						giveItems(player, WOODEN_HELMET, 1);
						playSound(player, SOUND_JACKPOT);
					}
					else if (n < 2)
						giveItems(player, 57, 1000);
					else if (n < 5)
						giveItems(player, LEATHER_SANDALS, 1);
					else if (n < 8)
					{
						giveItems(player, STOCKINGS, 1);
						giveItems(player, 57, 50);
					}
					else if (n < 11)
						giveItems(player, HEALING_POTION, 1);
					else if (n < 14)
						giveItems(player, SHORT_GLOVES, 1);
					else
						giveItems(player, CLOTH_SHOES, 1);
					
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
		
		if (npc.getNpcId() == 20003)
		{
			if (dropItems(player, WOLF_CLAW, Rnd.nextBoolean() ? 2 : 4, 50, 500000))
				st.setCond(2);
		}
		else if (dropItemsAlways(player, WOLF_CLAW, (Rnd.get(5) < 4) ? 1 : 2, 50))
			st.setCond(2);
	}
}