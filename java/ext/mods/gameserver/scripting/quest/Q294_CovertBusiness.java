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
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q294_CovertBusiness extends Quest
{
	private static final String QUEST_NAME = "Q294_CovertBusiness";
	
	private static final int BAT_FANG = 1491;
	
	private static final int RING_OF_RACCOON = 1508;
	
	public Q294_CovertBusiness()
	{
		super(294, "Covert Business");
		
		setItemsIds(BAT_FANG);
		
		addQuestStart(30534);
		addTalkId(30534);
		
		addMyDying(20370, 20480);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30534-03.htm"))
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
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() != ClassRace.DWARF)
					htmltext = "30534-00.htm";
				else if (player.getStatus().getLevel() < 10)
					htmltext = "30534-01.htm";
				else
					htmltext = "30534-02.htm";
				break;
			
			case STARTED:
				if (st.getCond() == 1)
					htmltext = "30534-04.htm";
				else
				{
					if (!player.getInventory().hasItems(RING_OF_RACCOON))
					{
						htmltext = "30534-05.htm";
						takeItems(player, BAT_FANG, -1);
						giveItems(player, RING_OF_RACCOON, 1);
					}
					else
					{
						htmltext = "30534-06.htm";
						takeItems(player, BAT_FANG, -1);
						rewardItems(player, 57, 2400);
					}
					rewardExpAndSp(player, 0, 600);
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
		
		int count = 1;
		final int chance = Rnd.get(10);
		final boolean isBarded = (npc.getNpcId() == 20370);
		
		if (chance < 3)
			count++;
		else if (chance < ((isBarded) ? 5 : 6))
			count += 2;
		else if (isBarded && chance < 7)
			count += 3;
		
		if (dropItemsAlways(player, BAT_FANG, count, 100))
			st.setCond(2);
	}
}