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

public class Q347_GoGetTheCalculator extends Quest
{
	private static final String QUEST_NAME = "Q347_GoGetTheCalculator";
	
	private static final int BRUNON = 30526;
	private static final int SILVERA = 30527;
	private static final int SPIRON = 30532;
	private static final int BALANKI = 30533;
	
	private static final int GEMSTONE_BEAST_CRYSTAL = 4286;
	private static final int CALCULATOR_QUEST = 4285;
	private static final int CALCULATOR_REAL = 4393;
	
	public Q347_GoGetTheCalculator()
	{
		super(347, "Go Get the Calculator");
		
		setItemsIds(GEMSTONE_BEAST_CRYSTAL, CALCULATOR_QUEST);
		
		addQuestStart(BRUNON);
		addTalkId(BRUNON, SILVERA, SPIRON, BALANKI);
		
		addMyDying(20540);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30526-05.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30533-03.htm"))
		{
			if (player.getInventory().getItemCount(57) >= 100)
			{
				htmltext = "30533-02.htm";
				takeItems(player, 57, 100);
				
				if (st.getCond() == 3)
					st.setCond(4);
				else
					st.setCond(2);
				
				playSound(player, SOUND_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("30532-02.htm"))
		{
			if (st.getCond() == 2)
				st.setCond(4);
			else
				st.setCond(3);
			
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30526-08.htm"))
		{
			takeItems(player, CALCULATOR_QUEST, -1);
			giveItems(player, CALCULATOR_REAL, 1);
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30526-09.htm"))
		{
			takeItems(player, CALCULATOR_QUEST, -1);
			rewardItems(player, 57, 1000);
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
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
				htmltext = (player.getStatus().getLevel() < 12) ? "30526-00.htm" : "30526-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case BRUNON:
						htmltext = (!player.getInventory().hasItems(CALCULATOR_QUEST)) ? "30526-06.htm" : "30526-07.htm";
						break;
					
					case SPIRON:
						htmltext = (cond < 4) ? "30532-01.htm" : "30532-05.htm";
						break;
					
					case BALANKI:
						htmltext = (cond < 4) ? "30533-01.htm" : "30533-04.htm";
						break;
					
					case SILVERA:
						if (cond < 4)
							htmltext = "30527-00.htm";
						else if (cond == 4)
						{
							htmltext = "30527-01.htm";
							st.setCond(5);
							playSound(player, SOUND_MIDDLE);
						}
						else if (cond == 5)
						{
							if (player.getInventory().getItemCount(GEMSTONE_BEAST_CRYSTAL) < 10)
								htmltext = "30527-02.htm";
							else
							{
								htmltext = "30527-03.htm";
								st.setCond(6);
								takeItems(player, GEMSTONE_BEAST_CRYSTAL, -1);
								giveItems(player, CALCULATOR_QUEST, 1);
								playSound(player, SOUND_MIDDLE);
							}
						}
						else if (cond == 6)
							htmltext = "30527-04.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerCondition(player, npc, 5);
		if (st == null)
			return;
		
		dropItems(player, GEMSTONE_BEAST_CRYSTAL, 1, 10, 500000);
	}
}