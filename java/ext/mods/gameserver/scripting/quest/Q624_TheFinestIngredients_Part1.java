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

public class Q624_TheFinestIngredients_Part1 extends Quest
{
	private static final String QUEST_NAME = "Q624_TheFinestIngredients_Part1";
	
	private static final int NEPENTHES = 21319;
	private static final int ATROX = 21321;
	private static final int ATROXSPAWN = 21317;
	private static final int BANDERSNATCH = 21314;
	
	private static final int TRUNK_OF_NEPENTHES = 7202;
	private static final int FOOT_OF_BANDERSNATCHLING = 7203;
	private static final int SECRET_SPICE = 7204;
	
	private static final int ICE_CRYSTAL = 7080;
	private static final int SOY_SAUCE_JAR = 7205;
	
	public Q624_TheFinestIngredients_Part1()
	{
		super(624, "The Finest Ingredients - Part 1");
		
		setItemsIds(TRUNK_OF_NEPENTHES, FOOT_OF_BANDERSNATCHLING, SECRET_SPICE);
		
		addQuestStart(31521);
		addTalkId(31521);
		
		addMyDying(NEPENTHES, ATROX, ATROXSPAWN, BANDERSNATCH);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31521-02.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31521-05.htm"))
		{
			if (player.getInventory().getItemCount(TRUNK_OF_NEPENTHES) >= 50 && player.getInventory().getItemCount(FOOT_OF_BANDERSNATCHLING) >= 50 && player.getInventory().getItemCount(SECRET_SPICE) >= 50)
			{
				takeItems(player, TRUNK_OF_NEPENTHES, -1);
				takeItems(player, FOOT_OF_BANDERSNATCHLING, -1);
				takeItems(player, SECRET_SPICE, -1);
				giveItems(player, ICE_CRYSTAL, 1);
				giveItems(player, SOY_SAUCE_JAR, 1);
				playSound(player, SOUND_FINISH);
				st.exitQuest(true);
			}
			else
			{
				st.setCond(1);
				htmltext = "31521-07.htm";
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
				htmltext = (player.getStatus().getLevel() < 73) ? "31521-03.htm" : "31521-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				if (cond == 1)
					htmltext = "31521-06.htm";
				else if (cond == 2)
				{
					if (player.getInventory().getItemCount(TRUNK_OF_NEPENTHES) >= 50 && player.getInventory().getItemCount(FOOT_OF_BANDERSNATCHLING) >= 50 && player.getInventory().getItemCount(SECRET_SPICE) >= 50)
						htmltext = "31521-04.htm";
					else
						htmltext = "31521-07.htm";
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		Player player = killer.getActingPlayer();
		
		final QuestState st = getRandomPartyMember(player, npc, 1);
		if (st == null)
			return;
		
		player = st.getPlayer();
		switch (npc.getNpcId())
		{
			case NEPENTHES:
				if (dropItemsAlways(player, TRUNK_OF_NEPENTHES, 1, 50) && player.getInventory().getItemCount(FOOT_OF_BANDERSNATCHLING) >= 50 && player.getInventory().getItemCount(SECRET_SPICE) >= 50)
					st.setCond(2);
				break;
			
			case ATROX, ATROXSPAWN:
				if (dropItemsAlways(player, SECRET_SPICE, 1, 50) && player.getInventory().getItemCount(TRUNK_OF_NEPENTHES) >= 50 && player.getInventory().getItemCount(FOOT_OF_BANDERSNATCHLING) >= 50)
					st.setCond(2);
				break;
			
			case BANDERSNATCH:
				if (dropItemsAlways(player, FOOT_OF_BANDERSNATCHLING, 1, 50) && player.getInventory().getItemCount(TRUNK_OF_NEPENTHES) >= 50 && player.getInventory().getItemCount(SECRET_SPICE) >= 50)
					st.setCond(2);
				break;
		}
	}
}