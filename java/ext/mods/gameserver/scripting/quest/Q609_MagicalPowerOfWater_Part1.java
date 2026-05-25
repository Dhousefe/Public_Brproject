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
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q609_MagicalPowerOfWater_Part1 extends Quest
{
	private static final String QUEST_NAME = "Q609_MagicalPowerOfWater_Part1";
	
	private static final int WAHKAN = 31371;
	private static final int ASEFA = 31372;
	private static final int UDAN_BOX = 31561;
	private static final int UDAN_EYE = 31684;
	
	private static final int THIEF_KEY = 1661;
	private static final int STOLEN_GREEN_TOTEM = 7237;
	private static final int GREEN_TOTEM = 7238;
	private static final int DIVINE_STONE = 7081;
	
	public Q609_MagicalPowerOfWater_Part1()
	{
		super(609, "Magical Power of Water - Part 1");
		
		setItemsIds(STOLEN_GREEN_TOTEM);
		
		addQuestStart(WAHKAN);
		addTalkId(WAHKAN, ASEFA, UDAN_BOX);
		
		addMyDying(UDAN_EYE);
		addSeeCreature(21350, 21351, 21353, 21354, 21355, 21357, 21358, 21360, 21361, 21362, 21369, 21370, 21364, 21365, 21366, 21368, 21371, 21372, 21373, 21374, 21375);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31371-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			st.set("spawned", 0);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31561-03.htm"))
		{
			if (st.getInteger("spawned") == 1)
				htmltext = "31561-04.htm";
			else if (!player.getInventory().hasItems(THIEF_KEY))
				htmltext = "31561-02.htm";
			else
			{
				st.setCond(3);
				playSound(player, SOUND_ITEMGET);
				takeItems(player, THIEF_KEY, 1);
				giveItems(player, STOLEN_GREEN_TOTEM, 1);
			}
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
				htmltext = (player.getStatus().getLevel() >= 74 && player.getAllianceWithVarkaKetra() >= 2) ? "31371-01.htm" : "31371-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case WAHKAN:
						htmltext = "31371-04.htm";
						break;
					
					case ASEFA:
						if (cond == 1)
						{
							htmltext = "31372-01.htm";
							st.setCond(2);
							playSound(player, SOUND_MIDDLE);
						}
						else if (cond == 2)
						{
							if (st.getInteger("spawned") == 0)
								htmltext = "31372-02.htm";
							else
							{
								htmltext = "31372-03.htm";
								st.set("spawned", 0);
								playSound(player, SOUND_MIDDLE);
							}
						}
						else if (cond == 3 && player.getInventory().hasItems(STOLEN_GREEN_TOTEM))
						{
							htmltext = "31372-04.htm";
							
							takeItems(player, STOLEN_GREEN_TOTEM, 1);
							giveItems(player, GREEN_TOTEM, 1);
							giveItems(player, DIVINE_STONE, 1);
							
							st.unset("spawned");
							playSound(player, SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case UDAN_BOX:
						if (cond == 2)
							htmltext = "31561-01.htm";
						else if (cond == 3)
							htmltext = "31561-05.htm";
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
		
		final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		npc.broadcastNpcSay(NpcStringId.ID_60904);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player player)
		{
			final QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
			if (st == null)
				return;
			
			if (st.getInteger("spawned") == 0 && st.getCond() == 2)
			{
				st.set("spawned", 1);
				
				final Npc udanEye = addSpawn(UDAN_EYE, player, true, 10000, true);
				if (udanEye != null)
				{
					udanEye.broadcastNpcSay(NpcStringId.ID_60903);
					playSound(player, SOUND_GIVEUP);
				}
			}
		}
	}
}