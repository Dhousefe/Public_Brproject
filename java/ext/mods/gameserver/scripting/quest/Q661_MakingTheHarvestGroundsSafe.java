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

public class Q661_MakingTheHarvestGroundsSafe extends Quest
{
	private static final String QUEST_NAME = "Q661_MakingTheHarvestGroundsSafe";
	
	private static final int NORMAN = 30210;
	
	private static final int STING_OF_GIANT_POISON_BEE = 8283;
	private static final int CLOUDY_GEM = 8284;
	private static final int TALON_OF_YOUNG_ARANEID = 8285;
	
	private static final int ADENA = 57;
	
	private static final int GIANT_POISON_BEE = 21095;
	private static final int CLOUDY_BEAST = 21096;
	private static final int YOUNG_ARANEID = 21097;
	
	public Q661_MakingTheHarvestGroundsSafe()
	{
		super(661, "Making the Harvest Grounds Safe");
		
		setItemsIds(STING_OF_GIANT_POISON_BEE, CLOUDY_GEM, TALON_OF_YOUNG_ARANEID);
		
		addQuestStart(NORMAN);
		addTalkId(NORMAN);
		
		addMyDying(GIANT_POISON_BEE, CLOUDY_BEAST, YOUNG_ARANEID);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30210-02.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30210-04.htm"))
		{
			int item1 = player.getInventory().getItemCount(STING_OF_GIANT_POISON_BEE);
			int item2 = player.getInventory().getItemCount(CLOUDY_GEM);
			int item3 = player.getInventory().getItemCount(TALON_OF_YOUNG_ARANEID);
			
			deleteItem1(player, STING_OF_GIANT_POISON_BEE, item1);
			deleteItem1(player, CLOUDY_GEM, item2);
			deleteItem1(player, TALON_OF_YOUNG_ARANEID, item3);
			
			if ((ownItemCount(player, item1) + ownItemCount(player, item2) + item3) >= 10)
				giveItem1(player, ADENA, 5773 + (57 * item1) + 56 * (item2 + 60) * item3);
			else
				giveItem1(player, ADENA, (57 * item1) + (56 * item2) + (60 * item3));
		}
		else if (event.equalsIgnoreCase("30210-06.htm"))
			st.exitQuest(true);
		
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
				htmltext = (player.getStatus().getLevel() < 21) ? "30210-01a.htm" : "30210-01.htm";
				break;
			
			case STARTED:
				htmltext = (player.getInventory().hasAtLeastOneItem(STING_OF_GIANT_POISON_BEE, CLOUDY_GEM, TALON_OF_YOUNG_ARANEID)) ? "30210-03.htm" : "30210-05.htm";
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
		
		dropItems(player, npc.getNpcId() - 12812, 1, 0, 500000);
	}
}