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
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q156_MillenniumLove extends Quest
{
	private static final String QUEST_NAME = "Q156_MillenniumLove";
	
	private static final int LILITH_LETTER = 1022;
	private static final int THEON_DIARY = 1023;
	
	private static final int LILITH = 30368;
	private static final int BAENEDES = 30369;
	
	public Q156_MillenniumLove()
	{
		super(156, "Millennium Love");
		
		setItemsIds(LILITH_LETTER, THEON_DIARY);
		
		addQuestStart(LILITH);
		addTalkId(LILITH, BAENEDES);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30368-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, LILITH_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("30369-02.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, LILITH_LETTER, 1);
			giveItems(player, THEON_DIARY, 1);
		}
		else if (event.equalsIgnoreCase("30369-03.htm"))
		{
			takeItems(player, LILITH_LETTER, 1);
			rewardExpAndSp(player, 3000, 0);
			playSound(player, SOUND_FINISH);
			st.exitQuest(false);
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
				htmltext = (player.getStatus().getLevel() < 15) ? "30368-00.htm" : "30368-01.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case LILITH:
						if (player.getInventory().hasItems(LILITH_LETTER))
							htmltext = "30368-05.htm";
						else if (player.getInventory().hasItems(THEON_DIARY))
						{
							htmltext = "30368-06.htm";
							takeItems(player, THEON_DIARY, 1);
							giveItems(player, 5250, 1);
							rewardExpAndSp(player, 3000, 0);
							playSound(player, SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case BAENEDES:
						if (player.getInventory().hasItems(LILITH_LETTER))
							htmltext = "30369-01.htm";
						else if (player.getInventory().hasItems(THEON_DIARY))
							htmltext = "30369-04.htm";
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
}