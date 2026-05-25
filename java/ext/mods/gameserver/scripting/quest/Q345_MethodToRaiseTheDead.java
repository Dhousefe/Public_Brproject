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
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q345_MethodToRaiseTheDead extends Quest
{
	private static final String QUEST_NAME = "Q345_MethodToRaiseTheDead";
	
	private static final int VICTIM_ARM_BONE = 4274;
	private static final int VICTIM_THIGH_BONE = 4275;
	private static final int VICTIM_SKULL = 4276;
	private static final int VICTIM_RIB_BONE = 4277;
	private static final int VICTIM_SPINE = 4278;
	private static final int USELESS_BONE_PIECES = 4280;
	private static final int POWDER_TO_SUMMON_DEAD_SOULS = 4281;
	
	private static final int XENOVIA = 30912;
	private static final int DOROTHY = 30970;
	private static final int ORPHEUS = 30971;
	private static final int MEDIUM_JAR = 30973;
	
	private static final int BILL_OF_IASON_HEINE = 4310;
	private static final int IMPERIAL_DIAMOND = 3456;
	
	public Q345_MethodToRaiseTheDead()
	{
		super(345, "Method to Raise the Dead");
		
		setItemsIds(VICTIM_ARM_BONE, VICTIM_THIGH_BONE, VICTIM_SKULL, VICTIM_RIB_BONE, VICTIM_SPINE, POWDER_TO_SUMMON_DEAD_SOULS, USELESS_BONE_PIECES);
		
		addQuestStart(DOROTHY);
		addTalkId(DOROTHY, XENOVIA, MEDIUM_JAR, ORPHEUS);
		
		addMyDying(20789, 20791);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30970-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30970-06.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30912-04.htm"))
		{
			if (player.getAdena() >= 1000)
			{
				htmltext = "30912-03.htm";
				st.setCond(3);
				playSound(player, SOUND_MIDDLE);
				takeItems(player, 57, 1000);
				giveItems(player, POWDER_TO_SUMMON_DEAD_SOULS, 1);
			}
		}
		else if (event.equalsIgnoreCase("30973-04.htm"))
		{
			if (st.getCond() == 3)
			{
				final int chance = Rnd.get(3);
				if (chance == 0)
				{
					st.setCond(6);
					htmltext = "30973-02a.htm";
				}
				else if (chance == 1)
				{
					st.setCond(6);
					htmltext = "30973-02b.htm";
				}
				else
				{
					st.setCond(7);
					htmltext = "30973-02c.htm";
				}
				
				takeItems(player, POWDER_TO_SUMMON_DEAD_SOULS, -1);
				takeItems(player, VICTIM_ARM_BONE, -1);
				takeItems(player, VICTIM_THIGH_BONE, -1);
				takeItems(player, VICTIM_SKULL, -1);
				takeItems(player, VICTIM_RIB_BONE, -1);
				takeItems(player, VICTIM_SPINE, -1);
				
				playSound(player, SOUND_MIDDLE);
			}
		}
		else if (event.equalsIgnoreCase("30971-02a.htm"))
		{
			if (player.getInventory().hasItems(USELESS_BONE_PIECES))
				htmltext = "30971-02.htm";
		}
		else if (event.equalsIgnoreCase("30971-03.htm"))
		{
			if (player.getInventory().hasItems(USELESS_BONE_PIECES))
			{
				int amount = player.getInventory().getItemCount(USELESS_BONE_PIECES) * 104;
				takeItems(player, USELESS_BONE_PIECES, -1);
				rewardItems(player, 57, amount);
			}
			else
				htmltext = "30971-02a.htm";
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
				htmltext = (player.getStatus().getLevel() < 35) ? "30970-00.htm" : "30970-01.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case DOROTHY:
						if (cond == 1)
							htmltext = (!player.getInventory().hasItems(VICTIM_ARM_BONE, VICTIM_THIGH_BONE, VICTIM_SKULL, VICTIM_RIB_BONE, VICTIM_SPINE)) ? "30970-04.htm" : "30970-05.htm";
						else if (cond == 2)
							htmltext = "30970-07.htm";
						else if (cond > 2 && cond < 6)
							htmltext = "30970-08.htm";
						else
						{
							int amount = player.getInventory().getItemCount(USELESS_BONE_PIECES) * 70;
							takeItems(player, USELESS_BONE_PIECES, -1);
							
							if (cond == 7)
							{
								htmltext = "30970-10.htm";
								rewardItems(player, 57, 3040 + amount);
								
								if (Rnd.get(100) < 10)
									giveItems(player, IMPERIAL_DIAMOND, 1);
								else
									giveItems(player, BILL_OF_IASON_HEINE, 5);
							}
							else
							{
								htmltext = "30970-09.htm";
								rewardItems(player, 57, 5390 + amount);
								giveItems(player, BILL_OF_IASON_HEINE, 3);
							}
							playSound(player, SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case XENOVIA:
						if (cond == 2)
							htmltext = "30912-01.htm";
						else if (cond > 2)
							htmltext = "30912-06.htm";
						break;
					
					case MEDIUM_JAR:
						htmltext = "30973-01.htm";
						break;
					
					case ORPHEUS:
						htmltext = "30971-01.htm";
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
		
		final QuestState st = checkPlayerCondition(player, npc, 1);
		if (st == null)
			return;
		
		if (Rnd.get(4) == 0)
		{
			final int randomPart = Rnd.get(VICTIM_ARM_BONE, VICTIM_SPINE);
			if (!player.getInventory().hasItems(randomPart))
			{
				playSound(player, SOUND_ITEMGET);
				giveItems(player, randomPart, 1);
				return;
			}
		}
		dropItemsAlways(player, USELESS_BONE_PIECES, 1, 0);
	}
}