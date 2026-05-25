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

public class Q276_TotemOfTheHestui extends Quest
{
	private static final String QUEST_NAME = "Q276_TotemOfTheHestui";
	
	private static final int KASHA_PARASITE = 1480;
	private static final int KASHA_CRYSTAL = 1481;
	
	private static final int HESTUI_TOTEM = 1500;
	private static final int LEATHER_PANTS = 29;
	
	public Q276_TotemOfTheHestui()
	{
		super(276, "Totem of the Hestui");
		
		setItemsIds(KASHA_PARASITE, KASHA_CRYSTAL);
		
		addQuestStart(30571);
		addTalkId(30571);
		
		addMyDying(20479, 27044);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30571-03.htm"))
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
				if (player.getRace() != ClassRace.ORC)
					htmltext = "30571-00.htm";
				else if (player.getStatus().getLevel() < 15)
					htmltext = "30571-01.htm";
				else
					htmltext = "30571-02.htm";
				break;
			
			case STARTED:
				if (st.getCond() == 1)
					htmltext = "30571-04.htm";
				else
				{
					htmltext = "30571-05.htm";
					takeItems(player, KASHA_CRYSTAL, -1);
					takeItems(player, KASHA_PARASITE, -1);
					giveItems(player, HESTUI_TOTEM, 1);
					giveItems(player, LEATHER_PANTS, 1);
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
		
		if (!player.getInventory().hasItems(KASHA_CRYSTAL))
		{
			switch (npc.getNpcId())
			{
				case 20479:
					final int count = player.getInventory().getItemCount(KASHA_PARASITE);
					final int random = Rnd.get(100);
					
					if (count >= 79 || (count >= 69 && random <= 20) || (count >= 59 && random <= 15) || (count >= 49 && random <= 10) || (count >= 39 && random < 2))
					{
						addSpawn(27044, npc, true, 0, true);
						takeItems(player, KASHA_PARASITE, count);
					}
					else
						dropItemsAlways(player, KASHA_PARASITE, 1, 0);
					break;
				
				case 27044:
					st.setCond(2);
					playSound(player, SOUND_MIDDLE);
					giveItems(player, KASHA_CRYSTAL, 1);
					break;
			}
		}
	}
}