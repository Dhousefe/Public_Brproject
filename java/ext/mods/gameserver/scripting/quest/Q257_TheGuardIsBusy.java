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

public class Q257_TheGuardIsBusy extends Quest
{
	private static final String QUEST_NAME = "Q257_TheGuardIsBusy";
	
	private static final int GLUDIO_LORD_MARK = 1084;
	private static final int ORC_AMULET = 752;
	private static final int ORC_NECKLACE = 1085;
	private static final int WEREWOLF_FANG = 1086;
	
	public Q257_TheGuardIsBusy()
	{
		super(257, "The Guard Is Busy");
		
		setItemsIds(ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG, GLUDIO_LORD_MARK);
		
		addQuestStart(30039);
		addTalkId(30039);
		
		addMyDying(20006, 20093, 20096, 20098, 20130, 20131, 20132, 20342, 20343);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30039-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, GLUDIO_LORD_MARK, 1);
		}
		else if (event.equalsIgnoreCase("30039-05.htm"))
		{
			takeItems(player, GLUDIO_LORD_MARK, 1);
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
				if (player.getStatus().getLevel() < 6)
					htmltext = "30039-01.htm";
				else
					htmltext = "30039-02.htm";
				break;
			
			case STARTED:
				final int amulets = player.getInventory().getItemCount(ORC_AMULET);
				final int necklaces = player.getInventory().getItemCount(ORC_NECKLACE);
				final int fangs = player.getInventory().getItemCount(WEREWOLF_FANG);
				
				if (amulets + necklaces + fangs == 0)
					htmltext = "30039-04.htm";
				else
				{
					htmltext = "30039-07.htm";
					
					takeItems(player, ORC_AMULET, -1);
					takeItems(player, ORC_NECKLACE, -1);
					takeItems(player, WEREWOLF_FANG, -1);
					
					int reward = (10 * amulets) + 20 * (necklaces + fangs);
					if (amulets + necklaces + fangs >= 10)
						reward += 1000;
					
					rewardItems(player, 57, reward);
					rewardNewbieShots(player, 6000, 3000);
				}
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
			case 20006, 20130, 20131:
				dropItems(player, ORC_AMULET, 1, 0, 500000);
				break;
			
			case 20093, 20096, 20098:
				dropItems(player, ORC_NECKLACE, 1, 0, 500000);
				break;
			
			case 20342:
				dropItems(player, WEREWOLF_FANG, 1, 0, 200000);
				break;
			
			case 20343:
				dropItems(player, WEREWOLF_FANG, 1, 0, 400000);
				break;
			
			case 20132:
				dropItems(player, WEREWOLF_FANG, 1, 0, 500000);
				break;
		}
	}
}