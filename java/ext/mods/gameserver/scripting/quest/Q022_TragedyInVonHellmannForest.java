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
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;

public class Q022_TragedyInVonHellmannForest extends Quest
{
	private static final String QUEST_NAME = "Q022_TragedyInVonHellmannForest";
	
	private static final int WELL = 31527;
	private static final int TIFAREN = 31334;
	private static final int INNOCENTIN = 31328;
	private static final int GHOST_OF_PRIEST = 31528;
	private static final int GHOST_OF_ADVENTURER = 31529;
	
	private static final int CROSS_OF_EINHASAD = 7141;
	private static final int LOST_SKULL_OF_ELF = 7142;
	private static final int LETTER_OF_INNOCENTIN = 7143;
	private static final int GREEN_JEWEL_OF_ADVENTURER = 7144;
	private static final int RED_JEWEL_OF_ADVENTURER = 7145;
	private static final int SEALED_REPORT_BOX = 7146;
	private static final int REPORT_BOX = 7147;
	
	private static final int SOUL_OF_WELL = 27217;
	private static final int OPPRESSED_ONE = 21553;
	private static final int OPPRESSED_ONE_A = 21554;
	private static final int AGENT_OF_SLAUGHTER = 21555;
	private static final int AGENT_OF_SLAUGHTER_A = 21556;
	private static final int SACRIFICED_ONE = 21561;
	
	private Npc _ghostOfPriest;
	private Npc _soulOfWell;
	
	public Q022_TragedyInVonHellmannForest()
	{
		super(22, "Tragedy in von Hellmann Forest");
		
		setItemsIds(LOST_SKULL_OF_ELF, REPORT_BOX, SEALED_REPORT_BOX, LETTER_OF_INNOCENTIN, RED_JEWEL_OF_ADVENTURER, GREEN_JEWEL_OF_ADVENTURER);
		
		addQuestStart(TIFAREN, INNOCENTIN);
		addTalkId(INNOCENTIN, TIFAREN, GHOST_OF_PRIEST, GHOST_OF_ADVENTURER, WELL);
		
		addAttacked(SOUL_OF_WELL);
		addDecayed(GHOST_OF_PRIEST);
		addMyDying(SOUL_OF_WELL, OPPRESSED_ONE, OPPRESSED_ONE_A, AGENT_OF_SLAUGHTER, AGENT_OF_SLAUGHTER_A, SACRIFICED_ONE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("31334-03.htm"))
		{
			QuestState st2 = player.getQuestList().getQuestState("Q021_HiddenTruth");
			if (st2 != null && st2.isCompleted() && player.getStatus().getLevel() >= 63)
				htmltext = "31334-02.htm";
		}
		else if (event.equalsIgnoreCase("31334-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31334-07.htm"))
		{
			if (!player.getInventory().hasItems(CROSS_OF_EINHASAD))
				st.setCond(2);
			else
				htmltext = "31334-06.htm";
		}
		else if (event.equalsIgnoreCase("31334-08.htm"))
		{
			if (player.getInventory().hasItems(CROSS_OF_EINHASAD))
			{
				st.setCond(4);
				playSound(player, SOUND_MIDDLE);
				takeItems(player, CROSS_OF_EINHASAD, 1);
			}
			else
			{
				st.setCond(2);
				htmltext = "31334-07.htm";
			}
		}
		else if (event.equalsIgnoreCase("31334-13.htm"))
		{
			if (_ghostOfPriest != null)
			{
				st.setCond(6);
				htmltext = "31334-14.htm";
			}
			else
			{
				st.setCond(7);
				playSound(player, SOUND_MIDDLE);
				takeItems(player, LOST_SKULL_OF_ELF, 1);
				
				_ghostOfPriest = addSpawn(GHOST_OF_PRIEST, 38418, -49894, -1104, 0, false, 120000, true);
				_ghostOfPriest.broadcastNpcSay(NpcStringId.ID_2250, player.getName());
			}
		}
		else if (event.equalsIgnoreCase("31528-08.htm"))
		{
			st.setCond(8);
			playSound(player, SOUND_MIDDLE);
			
			if (_ghostOfPriest != null)
				startQuestTimer("ghost_delete", _ghostOfPriest, null, 3000);
		}
		else if (event.equalsIgnoreCase("31328-10.htm"))
		{
			st.setCond(9);
			playSound(player, SOUND_MIDDLE);
			giveItems(player, LETTER_OF_INNOCENTIN, 1);
		}
		else if (event.equalsIgnoreCase("31529-12.htm"))
		{
			st.setCond(10);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, LETTER_OF_INNOCENTIN, 1);
			giveItems(player, GREEN_JEWEL_OF_ADVENTURER, 1);
		}
		else if (event.equalsIgnoreCase("31527-02.htm"))
		{
			if (_soulOfWell == null)
			{
				_soulOfWell = addSpawn(SOUL_OF_WELL, 34860, -54542, -2048, 0, false, 0, true);
				_soulOfWell.getAI().addAttackDesire(player, 2000);
			}
		}
		else if (event.equalsIgnoreCase("31328-13.htm"))
		{
			st.setCond(15);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, REPORT_BOX, 1);
		}
		else if (event.equalsIgnoreCase("31328-21.htm"))
		{
			st.setCond(16);
			playSound(player, SOUND_MIDDLE);
		}
		return htmltext;
	}
	
	@Override
	public String onTimer(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("ghost_delete"))
		{
			_ghostOfPriest.deleteMe();
		}
		else if (event.equalsIgnoreCase("attack_timer"))
		{
			QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
			if (st != null)
			{
				st.setCond(11);
				playSound(player, SOUND_MIDDLE);
				takeItems(player, GREEN_JEWEL_OF_ADVENTURER, 1);
				giveItems(player, RED_JEWEL_OF_ADVENTURER, 1);
			}
		}
		
		return null;
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
				switch (npc.getNpcId())
				{
					case INNOCENTIN:
						QuestState st2 = player.getQuestList().getQuestState("Q021_HiddenTruth");
						if (st2 != null && st2.isCompleted())
						{
							if (!player.getInventory().hasItems(CROSS_OF_EINHASAD))
							{
								htmltext = "31328-01.htm";
								giveItems(player, CROSS_OF_EINHASAD, 1);
								playSound(player, SOUND_ITEMGET);
							}
							else
								htmltext = "31328-01b.htm";
						}
						break;
					
					case TIFAREN:
						htmltext = "31334-01.htm";
						break;
				}
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case TIFAREN:
						if (cond == 1 || cond == 2 || cond == 3)
							htmltext = "31334-05.htm";
						else if (cond == 4)
							htmltext = "31334-09.htm";
						else if (cond == 5 || cond == 6)
						{
							if (player.getInventory().hasItems(LOST_SKULL_OF_ELF))
								htmltext = (_ghostOfPriest == null) ? "31334-10.htm" : "31334-11.htm";
							else
							{
								htmltext = "31334-09.htm";
								st.setCond(4);
							}
						}
						else if (cond == 7)
							htmltext = (_ghostOfPriest != null) ? "31334-15.htm" : "31334-17.htm";
						else if (cond > 7)
							htmltext = "31334-18.htm";
						break;
					
					case INNOCENTIN:
						if (cond < 3)
						{
							if (!player.getInventory().hasItems(CROSS_OF_EINHASAD))
							{
								htmltext = "31328-01.htm";
								st.setCond(3);
								playSound(player, SOUND_ITEMGET);
								giveItems(player, CROSS_OF_EINHASAD, 1);
							}
							else
								htmltext = "31328-01b.htm";
						}
						else if (cond == 3)
							htmltext = "31328-02.htm";
						else if (cond == 8)
							htmltext = "31328-03.htm";
						else if (cond == 9)
							htmltext = "31328-11.htm";
						else if (cond == 14)
						{
							if (player.getInventory().hasItems(REPORT_BOX))
								htmltext = "31328-12.htm";
							else
								st.setCond(13);
						}
						else if (cond == 15)
							htmltext = "31328-14.htm";
						else if (cond == 16)
						{
							htmltext = (player.getStatus().getLevel() < 64) ? "31328-23.htm" : "31328-22.htm";
							st.exitQuest(false);
							playSound(player, SOUND_FINISH);
						}
						break;
					
					case GHOST_OF_PRIEST:
						if (cond == 7)
							htmltext = "31528-01.htm";
						else if (cond == 8)
							htmltext = "31528-08.htm";
						break;
					
					case GHOST_OF_ADVENTURER:
						if (cond == 9)
						{
							if (player.getInventory().hasItems(LETTER_OF_INNOCENTIN))
								htmltext = "31529-01.htm";
							else
							{
								htmltext = "31529-10.htm";
								st.setCond(8);
							}
						}
						else if (cond == 10)
							htmltext = "31529-16.htm";
						else if (cond == 11)
						{
							if (player.getInventory().hasItems(RED_JEWEL_OF_ADVENTURER))
							{
								htmltext = "31529-17.htm";
								st.setCond(12);
								playSound(player, SOUND_MIDDLE);
								takeItems(player, RED_JEWEL_OF_ADVENTURER, 1);
							}
							else
							{
								htmltext = "31529-09.htm";
								st.setCond(10);
							}
						}
						else if (cond == 12)
							htmltext = "31529-17.htm";
						else if (cond == 13)
						{
							if (player.getInventory().hasItems(SEALED_REPORT_BOX))
							{
								htmltext = "31529-18.htm";
								st.setCond(14);
								playSound(player, SOUND_MIDDLE);
								takeItems(player, SEALED_REPORT_BOX, 1);
								giveItems(player, REPORT_BOX, 1);
							}
							else
							{
								htmltext = "31529-10.htm";
								st.setCond(12);
							}
						}
						else if (cond > 13)
							htmltext = "31529-19.htm";
						break;
					
					case WELL:
						if (cond == 10)
							htmltext = "31527-01.htm";
						else if (cond == 11)
							htmltext = "31527-03.htm";
						else if (cond == 12)
						{
							htmltext = "31527-04.htm";
							st.setCond(13);
							playSound(player, SOUND_MIDDLE);
							giveItems(player, SEALED_REPORT_BOX, 1);
						}
						else if (cond > 12)
							htmltext = "31527-05.htm";
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
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Player player = attacker.getActingPlayer();
		
		final QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null || !st.isStarted())
			return;
		
		if (attacker instanceof Summon || npc != _soulOfWell)
			return;
		
		if (st.getCond() == 10)
			startQuestTimer("attack_timer", npc, player, 20000);
	}
	
	@Override
	public void onDecayed(Npc npc)
	{
		if (npc == _ghostOfPriest)
		{
			_ghostOfPriest.broadcastNpcSay(NpcStringId.ID_2251);
			
			cancelQuestTimers(_ghostOfPriest);
			_ghostOfPriest = null;
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		if (npc.getNpcId() != SOUL_OF_WELL)
		{
			if (st.getCond() == 4 && dropItems(player, LOST_SKULL_OF_ELF, 1, 1, 100000))
				st.setCond(5);
		}
		else
		{
			cancelQuestTimers("attack_timer", _soulOfWell);
			
			_soulOfWell = null;
		}
	}
}