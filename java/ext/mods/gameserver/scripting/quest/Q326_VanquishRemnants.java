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

public class Q326_VanquishRemnants extends Quest
{
	private static final String QUEST_NAME = "Q326_VanquishRemnants";
	
	private static final int RED_CROSS_BADGE = 1359;
	private static final int BLUE_CROSS_BADGE = 1360;
	private static final int BLACK_CROSS_BADGE = 1361;
	
	private static final int BLACK_LION_MARK = 1369;
	
	public Q326_VanquishRemnants()
	{
		super(326, "Vanquish Remnants");
		
		setItemsIds(RED_CROSS_BADGE, BLUE_CROSS_BADGE, BLACK_CROSS_BADGE);
		
		addQuestStart(30435);
		addTalkId(30435);
		
		addMyDying(20053, 20437, 20058, 20436, 20061, 20439, 20063, 20066, 20438);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30435-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30435-07.htm"))
		{
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
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
				htmltext = (player.getStatus().getLevel() < 21) ? "30435-01.htm" : "30435-02.htm";
				break;
			
			case STARTED:
				final int redBadges = player.getInventory().getItemCount(RED_CROSS_BADGE);
				final int blueBadges = player.getInventory().getItemCount(BLUE_CROSS_BADGE);
				final int blackBadges = player.getInventory().getItemCount(BLACK_CROSS_BADGE);
				
				final int badgesSum = redBadges + blueBadges + blackBadges;
				
				if (badgesSum > 0)
				{
					takeItems(player, RED_CROSS_BADGE, -1);
					takeItems(player, BLUE_CROSS_BADGE, -1);
					takeItems(player, BLACK_CROSS_BADGE, -1);
					rewardItems(player, 57, ((redBadges * 46) + (blueBadges * 52) + (blackBadges * 58) + ((badgesSum >= 10) ? 4320 : 0)));
					
					if (badgesSum >= 100)
					{
						if (!player.getInventory().hasItems(BLACK_LION_MARK))
						{
							htmltext = "30435-06.htm";
							giveItems(player, BLACK_LION_MARK, 1);
							playSound(player, SOUND_ITEMGET);
						}
						else
							htmltext = "30435-09.htm";
					}
					else
						htmltext = "30435-05.htm";
				}
				else
					htmltext = "30435-04.htm";
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
		
		switch (npc.getNpcId())
		{
			case 20053, 20437, 20058:
				dropItems(player, RED_CROSS_BADGE, 1, 0, 330000);
				break;
			
			case 20436, 20061, 20439, 20063:
				dropItems(player, BLUE_CROSS_BADGE, 1, 0, 160000);
				break;
			
			case 20066, 20438:
				dropItems(player, BLACK_CROSS_BADGE, 1, 0, 120000);
				break;
		}
	}
}