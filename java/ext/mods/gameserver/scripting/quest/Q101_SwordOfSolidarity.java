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
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q101_SwordOfSolidarity extends Quest
{
	private static final String QUEST_NAME = "Q101_SwordOfSolidarity";
	
	private static final int ROIEN = 30008;
	private static final int ALTRAN = 30283;
	
	private static final int BROKEN_SWORD_HANDLE = 739;
	private static final int BROKEN_BLADE_BOTTOM = 740;
	private static final int BROKEN_BLADE_TOP = 741;
	private static final int ROIEN_LETTER = 796;
	private static final int DIRECTIONS_TO_RUINS = 937;
	private static final int ALTRAN_NOTE = 742;
	
	private static final int SWORD_OF_SOLIDARITY = 738;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	public Q101_SwordOfSolidarity()
	{
		super(101, "Sword of Solidarity");
		
		setItemsIds(BROKEN_SWORD_HANDLE, BROKEN_BLADE_BOTTOM, BROKEN_BLADE_TOP, ROIEN_LETTER, DIRECTIONS_TO_RUINS, ALTRAN_NOTE);
		
		addQuestStart(ROIEN);
		addTalkId(ROIEN, ALTRAN);
		
		addMyDying(20361, 20362);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30008-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, ROIEN_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("30283-02.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, ROIEN_LETTER, 1);
			giveItems(player, DIRECTIONS_TO_RUINS, 1);
		}
		else if (event.equalsIgnoreCase("30283-06.htm"))
		{
			takeItems(player, BROKEN_SWORD_HANDLE, 1);
			giveItems(player, SWORD_OF_SOLIDARITY, 1);
			
			rewardNewbieShots(player, 7000, 0);
			rewardItems(player, LESSER_HEALING_POT, 100);
			rewardItems(player, ECHO_BATTLE, 10);
			rewardItems(player, ECHO_LOVE, 10);
			rewardItems(player, ECHO_SOLITUDE, 10);
			rewardItems(player, ECHO_FEAST, 10);
			rewardItems(player, ECHO_CELEBRATION, 10);
			
			player.broadcastPacket(new SocialAction(player, 3));
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
				if (player.getRace() != ClassRace.HUMAN)
					htmltext = "30008-01a.htm";
				else if (player.getStatus().getLevel() < 9)
					htmltext = "30008-01.htm";
				else
					htmltext = "30008-02.htm";
				break;
			
			case STARTED:
				int cond = (st.getCond());
				switch (npc.getNpcId())
				{
					case ROIEN:
						if (cond == 1)
							htmltext = "30008-04.htm";
						else if (cond == 2)
							htmltext = "30008-03a.htm";
						else if (cond == 3)
							htmltext = "30008-06.htm";
						else if (cond == 4)
						{
							htmltext = "30008-05.htm";
							st.setCond(5);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, ALTRAN_NOTE, 1);
							giveItems(player, BROKEN_SWORD_HANDLE, 1);
						}
						else if (cond == 5)
							htmltext = "30008-05a.htm";
						break;
					
					case ALTRAN:
						if (cond == 1)
							htmltext = "30283-01.htm";
						else if (cond == 2)
							htmltext = "30283-03.htm";
						else if (cond == 3)
						{
							htmltext = "30283-04.htm";
							st.setCond(4);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, DIRECTIONS_TO_RUINS, 1);
							takeItems(player, BROKEN_BLADE_TOP, 1);
							takeItems(player, BROKEN_BLADE_BOTTOM, 1);
							giveItems(player, ALTRAN_NOTE, 1);
						}
						else if (cond == 4)
							htmltext = "30283-04a.htm";
						else if (cond == 5)
							htmltext = "30283-05.htm";
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
		
		if (!player.getInventory().hasItems(BROKEN_BLADE_TOP))
			dropItems(player, BROKEN_BLADE_TOP, 1, 1, 200000);
		else if (dropItems(player, BROKEN_BLADE_BOTTOM, 1, 1, 200000))
			st.setCond(3);
	}
}