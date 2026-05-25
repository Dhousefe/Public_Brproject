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

public class Q042_HelpTheUncle extends Quest
{
	private static final String QUEST_NAME = "Q042_HelpTheUncle";
	
	private static final int WATERS = 30828;
	private static final int SOPHYA = 30735;
	
	private static final int TRIDENT = 291;
	private static final int MAP_PIECE = 7548;
	private static final int MAP = 7549;
	private static final int PET_TICKET = 7583;
	
	private static final int MONSTER_EYE_DESTROYER = 20068;
	private static final int MONSTER_EYE_GAZER = 20266;
	
	public Q042_HelpTheUncle()
	{
		super(42, "Help the Uncle!");
		
		setItemsIds(MAP_PIECE, MAP);
		
		addQuestStart(WATERS);
		addTalkId(WATERS, SOPHYA);
		
		addMyDying(MONSTER_EYE_DESTROYER, MONSTER_EYE_GAZER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30828-01.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30828-03.htm") && player.getInventory().hasItems(TRIDENT))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, TRIDENT, 1);
		}
		else if (event.equalsIgnoreCase("30828-05.htm"))
		{
			st.setCond(4);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, MAP_PIECE, 30);
			giveItems(player, MAP, 1);
		}
		else if (event.equalsIgnoreCase("30735-06.htm"))
		{
			st.setCond(5);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, MAP, 1);
		}
		else if (event.equalsIgnoreCase("30828-07.htm"))
		{
			giveItems(player, PET_TICKET, 1);
			playSound(player, SOUND_FINISH);
			st.exitQuest(false);
		}
		
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
				htmltext = (player.getStatus().getLevel() < 25) ? "30828-00a.htm" : "30828-00.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case WATERS:
						if (cond == 1)
							htmltext = (!player.getInventory().hasItems(TRIDENT)) ? "30828-01a.htm" : "30828-02.htm";
						else if (cond == 2)
							htmltext = "30828-03a.htm";
						else if (cond == 3)
							htmltext = "30828-04.htm";
						else if (cond == 4)
							htmltext = "30828-05a.htm";
						else if (cond == 5)
							htmltext = "30828-06.htm";
						break;
					
					case SOPHYA:
						if (cond == 4)
							htmltext = "30735-05.htm";
						else if (cond == 5)
							htmltext = "30735-06a.htm";
						break;
				}
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
		
		final QuestState st = checkPlayerCondition(player, npc, 2);
		if (st == null)
			return;
		
		if (dropItemsAlways(player, MAP_PIECE, 1, 30))
			st.setCond(3);
	}
}