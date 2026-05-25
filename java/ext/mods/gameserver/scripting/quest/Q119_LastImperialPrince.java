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

public class Q119_LastImperialPrince extends Quest
{
	private static final String QUEST_NAME = "Q119_LastImperialPrince";
	
	private static final int NAMELESS_SPIRIT = 31453;
	private static final int DEVORIN = 32009;
	
	private static final int ANTIQUE_BROOCH = 7262;
	
	public Q119_LastImperialPrince()
	{
		super(119, "Last Imperial Prince");
		
		setItemsIds(ANTIQUE_BROOCH);
		
		addQuestStart(NAMELESS_SPIRIT);
		addTalkId(NAMELESS_SPIRIT, DEVORIN);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31453-04.htm"))
		{
			if (player.getInventory().hasItems(ANTIQUE_BROOCH))
			{
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
			}
			else
			{
				htmltext = "31453-04b.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("32009-02.htm"))
		{
			if (!player.getInventory().hasItems(ANTIQUE_BROOCH))
			{
				htmltext = "31453-02a.htm";
				st.exitQuest(true);
			}
		}
		else if (event.equalsIgnoreCase("32009-03.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31453-07.htm"))
		{
			rewardItems(player, 57, 68787);
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
				htmltext = (!player.getInventory().hasItems(ANTIQUE_BROOCH) || player.getStatus().getLevel() < 74) ? "31453-00a.htm" : "31453-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case NAMELESS_SPIRIT:
						if (cond == 1)
							htmltext = "31453-04a.htm";
						else if (cond == 2)
							htmltext = "31453-05.htm";
						break;
					
					case DEVORIN:
						if (cond == 1)
							htmltext = "32009-01.htm";
						else if (cond == 2)
							htmltext = "32009-04.htm";
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = "31453-00b.htm";
				break;
		}
		
		return htmltext;
	}
}