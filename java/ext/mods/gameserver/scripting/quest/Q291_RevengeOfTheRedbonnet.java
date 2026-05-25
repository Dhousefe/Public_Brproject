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

public class Q291_RevengeOfTheRedbonnet extends Quest
{
	private static final String QUEST_NAME = "Q291_RevengeOfTheRedbonnet";
	
	private static final int BLACK_WOLF_PELT = 1482;
	
	private static final int SCROLL_OF_ESCAPE = 736;
	private static final int GRANDMA_PEARL = 1502;
	private static final int GRANDMA_MIRROR = 1503;
	private static final int GRANDMA_NECKLACE = 1504;
	private static final int GRANDMA_HAIRPIN = 1505;
	
	public Q291_RevengeOfTheRedbonnet()
	{
		super(291, "Revenge of the Redbonnet");
		
		setItemsIds(BLACK_WOLF_PELT);
		
		addQuestStart(30553);
		addTalkId(30553);
		
		addMyDying(20317);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30553-03.htm"))
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
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getStatus().getLevel() < 4) ? "30553-01.htm" : "30553-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				if (cond == 1)
					htmltext = "30553-04.htm";
				else if (cond == 2)
				{
					htmltext = "30553-05.htm";
					takeItems(player, BLACK_WOLF_PELT, -1);
					
					int random = Rnd.get(100);
					if (random < 3)
						rewardItems(player, GRANDMA_PEARL, 1);
					else if (random < 21)
						rewardItems(player, GRANDMA_MIRROR, 1);
					else if (random < 46)
						rewardItems(player, GRANDMA_NECKLACE, 1);
					else
					{
						rewardItems(player, SCROLL_OF_ESCAPE, 1);
						rewardItems(player, GRANDMA_HAIRPIN, 1);
					}
					
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
		
		if (dropItemsAlways(player, BLACK_WOLF_PELT, 1, 40))
			st.setCond(2);
	}
}