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

public class Q044_HelpTheSon extends Quest
{
	private static final String QUEST_NAME = "Q044_HelpTheSon";
	
	private static final int LUNDY = 30827;
	private static final int DRIKUS = 30505;
	
	private static final int WORK_HAMMER = 168;
	private static final int GEMSTONE_FRAGMENT = 7552;
	private static final int GEMSTONE = 7553;
	private static final int PET_TICKET = 7585;
	
	private static final int MAILLE = 20919;
	private static final int MAILLE_SCOUT = 20920;
	private static final int MAILLE_GUARD = 20921;
	
	public Q044_HelpTheSon()
	{
		super(44, "Help the Son!");
		
		setItemsIds(GEMSTONE_FRAGMENT, GEMSTONE);
		
		addQuestStart(LUNDY);
		addTalkId(LUNDY, DRIKUS);
		
		addMyDying(MAILLE, MAILLE_SCOUT, MAILLE_GUARD);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30827-01.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30827-03.htm") && player.getInventory().hasItems(WORK_HAMMER))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, WORK_HAMMER, 1);
		}
		else if (event.equalsIgnoreCase("30827-05.htm"))
		{
			st.setCond(4);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, GEMSTONE_FRAGMENT, 30);
			giveItems(player, GEMSTONE, 1);
		}
		else if (event.equalsIgnoreCase("30505-06.htm"))
		{
			st.setCond(5);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, GEMSTONE, 1);
		}
		else if (event.equalsIgnoreCase("30827-07.htm"))
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
				htmltext = (player.getStatus().getLevel() < 24) ? "30827-00a.htm" : "30827-00.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case LUNDY:
						if (cond == 1)
							htmltext = (!player.getInventory().hasItems(WORK_HAMMER)) ? "30827-01a.htm" : "30827-02.htm";
						else if (cond == 2)
							htmltext = "30827-03a.htm";
						else if (cond == 3)
							htmltext = "30827-04.htm";
						else if (cond == 4)
							htmltext = "30827-05a.htm";
						else if (cond == 5)
							htmltext = "30827-06.htm";
						break;
					
					case DRIKUS:
						if (cond == 4)
							htmltext = "30505-05.htm";
						else if (cond == 5)
							htmltext = "30505-06a.htm";
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
		
		if (dropItemsAlways(player, GEMSTONE_FRAGMENT, 1, 30))
			st.setCond(3);
	}
}