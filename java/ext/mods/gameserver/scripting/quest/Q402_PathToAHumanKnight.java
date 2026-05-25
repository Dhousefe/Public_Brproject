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
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q402_PathToAHumanKnight extends Quest
{
	private static final String QUEST_NAME = "Q402_PathToAHumanKnight";
	
	private static final int SWORD_OF_RITUAL = 1161;
	private static final int COIN_OF_LORDS_1 = 1162;
	private static final int COIN_OF_LORDS_2 = 1163;
	private static final int COIN_OF_LORDS_3 = 1164;
	private static final int COIN_OF_LORDS_4 = 1165;
	private static final int COIN_OF_LORDS_5 = 1166;
	private static final int COIN_OF_LORDS_6 = 1167;
	private static final int GLUDIO_GUARD_MARK_1 = 1168;
	private static final int BUGBEAR_NECKLACE = 1169;
	private static final int EINHASAD_CHURCH_MARK_1 = 1170;
	private static final int EINHASAD_CRUCIFIX = 1171;
	private static final int GLUDIO_GUARD_MARK_2 = 1172;
	private static final int SPIDER_LEG = 1173;
	private static final int EINHASAD_CHURCH_MARK_2 = 1174;
	private static final int LIZARDMAN_TOTEM = 1175;
	private static final int GLUDIO_GUARD_MARK_3 = 1176;
	private static final int GIANT_SPIDER_HUSK = 1177;
	private static final int EINHASAD_CHURCH_MARK_3 = 1178;
	private static final int HORRIBLE_SKULL = 1179;
	private static final int MARK_OF_ESQUIRE = 1271;
	
	private static final int SIR_KLAUS_VASPER = 30417;
	private static final int BATHIS = 30332;
	private static final int RAYMOND = 30289;
	private static final int BEZIQUE = 30379;
	private static final int LEVIAN = 30037;
	private static final int GILBERT = 30039;
	private static final int BIOTIN = 30031;
	private static final int SIR_AARON_TANFORD = 30653;
	private static final int SIR_COLLIN_WINDAWOOD = 30311;
	
	private static final int BUGBEAR_RAIDER = 20775;
	private static final int UNDEAD_PRIEST = 27024;
	private static final int VENOMOUS_SPIDER = 20038;
	private static final int ARACHNID_TRACKER = 20043;
	private static final int ARACHNID_PREDATOR = 20050;
	private static final int LANGK_LIZARDMAN = 20030;
	private static final int LANGK_LIZARDMAN_SCOUT = 20027;
	private static final int LANGK_LIZARDMAN_WARRIOR = 20024;
	private static final int GIANT_SPIDER = 20103;
	private static final int TALON_SPIDER = 20106;
	private static final int BLADE_SPIDER = 20108;
	private static final int SILENT_HORROR = 20404;
	
	public Q402_PathToAHumanKnight()
	{
		super(402, "Path to a Human Knight");
		
		setItemsIds(MARK_OF_ESQUIRE, COIN_OF_LORDS_1, COIN_OF_LORDS_2, COIN_OF_LORDS_3, COIN_OF_LORDS_4, COIN_OF_LORDS_5, COIN_OF_LORDS_6, GLUDIO_GUARD_MARK_1, BUGBEAR_NECKLACE, EINHASAD_CHURCH_MARK_1, EINHASAD_CRUCIFIX, GLUDIO_GUARD_MARK_2, SPIDER_LEG, EINHASAD_CHURCH_MARK_2, LIZARDMAN_TOTEM, GLUDIO_GUARD_MARK_3, GIANT_SPIDER_HUSK, EINHASAD_CHURCH_MARK_3, LIZARDMAN_TOTEM, GLUDIO_GUARD_MARK_3, GIANT_SPIDER_HUSK, EINHASAD_CHURCH_MARK_3, HORRIBLE_SKULL);
		
		addQuestStart(SIR_KLAUS_VASPER);
		addTalkId(SIR_KLAUS_VASPER, BATHIS, RAYMOND, BEZIQUE, LEVIAN, GILBERT, BIOTIN, SIR_AARON_TANFORD, SIR_COLLIN_WINDAWOOD);
		
		addMyDying(BUGBEAR_RAIDER, UNDEAD_PRIEST, VENOMOUS_SPIDER, ARACHNID_TRACKER, ARACHNID_PREDATOR, LANGK_LIZARDMAN, LANGK_LIZARDMAN_SCOUT, LANGK_LIZARDMAN_WARRIOR, GIANT_SPIDER, TALON_SPIDER, BLADE_SPIDER, SILENT_HORROR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30417-05.htm"))
		{
			if (player.getClassId() != ClassId.HUMAN_FIGHTER)
				htmltext = (player.getClassId() == ClassId.KNIGHT) ? "30417-02a.htm" : "30417-03.htm";
			else if (player.getStatus().getLevel() < 19)
				htmltext = "30417-02.htm";
			else if (player.getInventory().hasItems(SWORD_OF_RITUAL))
				htmltext = "30417-04.htm";
		}
		else if (event.equalsIgnoreCase("30417-08.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, MARK_OF_ESQUIRE, 1);
		}
		else if (event.equalsIgnoreCase("30332-02.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, GLUDIO_GUARD_MARK_1, 1);
		}
		else if (event.equalsIgnoreCase("30289-03.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, EINHASAD_CHURCH_MARK_1, 1);
		}
		else if (event.equalsIgnoreCase("30379-02.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, GLUDIO_GUARD_MARK_2, 1);
		}
		else if (event.equalsIgnoreCase("30037-02.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, EINHASAD_CHURCH_MARK_2, 1);
		}
		else if (event.equalsIgnoreCase("30039-02.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, GLUDIO_GUARD_MARK_3, 1);
		}
		else if (event.equalsIgnoreCase("30031-02.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, EINHASAD_CHURCH_MARK_3, 1);
		}
		else if (event.equalsIgnoreCase("30417-13.htm") || event.equalsIgnoreCase("30417-14.htm"))
		{
			final int coinCount = player.getInventory().getItemCount(COIN_OF_LORDS_1) + player.getInventory().getItemCount(COIN_OF_LORDS_2) + player.getInventory().getItemCount(COIN_OF_LORDS_3) + player.getInventory().getItemCount(COIN_OF_LORDS_4) + player.getInventory().getItemCount(COIN_OF_LORDS_5) + player.getInventory().getItemCount(COIN_OF_LORDS_6);
			
			takeItems(player, COIN_OF_LORDS_1, -1);
			takeItems(player, COIN_OF_LORDS_2, -1);
			takeItems(player, COIN_OF_LORDS_3, -1);
			takeItems(player, COIN_OF_LORDS_4, -1);
			takeItems(player, COIN_OF_LORDS_5, -1);
			takeItems(player, COIN_OF_LORDS_6, -1);
			takeItems(player, MARK_OF_ESQUIRE, 1);
			giveItems(player, SWORD_OF_RITUAL, 1);
			rewardExpAndSp(player, 3200, 1500 + (1920 * (coinCount - 3)));
			player.broadcastPacket(new SocialAction(player, 3));
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
				htmltext = "30417-01.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case SIR_KLAUS_VASPER:
						final int coins = player.getInventory().getItemCount(COIN_OF_LORDS_1) + player.getInventory().getItemCount(COIN_OF_LORDS_2) + player.getInventory().getItemCount(COIN_OF_LORDS_3) + player.getInventory().getItemCount(COIN_OF_LORDS_4) + player.getInventory().getItemCount(COIN_OF_LORDS_5) + player.getInventory().getItemCount(COIN_OF_LORDS_6);
						if (coins < 3)
							htmltext = "30417-09.htm";
						else if (coins == 3)
							htmltext = "30417-10.htm";
						else if (coins > 3 && coins < 6)
							htmltext = "30417-11.htm";
						else if (coins == 6)
						{
							htmltext = "30417-12.htm";
							takeItems(player, COIN_OF_LORDS_1, -1);
							takeItems(player, COIN_OF_LORDS_2, -1);
							takeItems(player, COIN_OF_LORDS_3, -1);
							takeItems(player, COIN_OF_LORDS_4, -1);
							takeItems(player, COIN_OF_LORDS_5, -1);
							takeItems(player, COIN_OF_LORDS_6, -1);
							takeItems(player, MARK_OF_ESQUIRE, 1);
							giveItems(player, SWORD_OF_RITUAL, 1);
							rewardExpAndSp(player, 3200, 7260);
							player.broadcastPacket(new SocialAction(player, 3));
							playSound(player, SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case BATHIS:
						if (player.getInventory().hasItems(COIN_OF_LORDS_1))
							htmltext = "30332-05.htm";
						else if (player.getInventory().hasItems(GLUDIO_GUARD_MARK_1))
						{
							if (player.getInventory().getItemCount(BUGBEAR_NECKLACE) < 10)
								htmltext = "30332-03.htm";
							else
							{
								htmltext = "30332-04.htm";
								playSound(player, SOUND_MIDDLE);
								takeItems(player, BUGBEAR_NECKLACE, -1);
								takeItems(player, GLUDIO_GUARD_MARK_1, 1);
								giveItems(player, COIN_OF_LORDS_1, 1);
							}
						}
						else
							htmltext = "30332-01.htm";
						break;
					
					case RAYMOND:
						if (player.getInventory().hasItems(COIN_OF_LORDS_2))
							htmltext = "30289-06.htm";
						else if (player.getInventory().hasItems(EINHASAD_CHURCH_MARK_1))
						{
							if (player.getInventory().getItemCount(EINHASAD_CRUCIFIX) < 12)
								htmltext = "30289-04.htm";
							else
							{
								htmltext = "30289-05.htm";
								playSound(player, SOUND_MIDDLE);
								takeItems(player, EINHASAD_CRUCIFIX, -1);
								takeItems(player, EINHASAD_CHURCH_MARK_1, 1);
								giveItems(player, COIN_OF_LORDS_2, 1);
							}
						}
						else
							htmltext = "30289-01.htm";
						break;
					
					case BEZIQUE:
						if (player.getInventory().hasItems(COIN_OF_LORDS_3))
							htmltext = "30379-05.htm";
						else if (player.getInventory().hasItems(GLUDIO_GUARD_MARK_2))
						{
							if (player.getInventory().getItemCount(SPIDER_LEG) < 20)
								htmltext = "30379-03.htm";
							else
							{
								htmltext = "30379-04.htm";
								playSound(player, SOUND_MIDDLE);
								takeItems(player, SPIDER_LEG, -1);
								takeItems(player, GLUDIO_GUARD_MARK_2, 1);
								giveItems(player, COIN_OF_LORDS_3, 1);
							}
						}
						else
							htmltext = "30379-01.htm";
						break;
					
					case LEVIAN:
						if (player.getInventory().hasItems(COIN_OF_LORDS_4))
							htmltext = "30037-05.htm";
						else if (player.getInventory().hasItems(EINHASAD_CHURCH_MARK_2))
						{
							if (player.getInventory().getItemCount(LIZARDMAN_TOTEM) < 20)
								htmltext = "30037-03.htm";
							else
							{
								htmltext = "30037-04.htm";
								playSound(player, SOUND_MIDDLE);
								takeItems(player, LIZARDMAN_TOTEM, -1);
								takeItems(player, EINHASAD_CHURCH_MARK_2, 1);
								giveItems(player, COIN_OF_LORDS_4, 1);
							}
						}
						else
							htmltext = "30037-01.htm";
						break;
					
					case GILBERT:
						if (player.getInventory().hasItems(COIN_OF_LORDS_5))
							htmltext = "30039-05.htm";
						else if (player.getInventory().hasItems(GLUDIO_GUARD_MARK_3))
						{
							if (player.getInventory().getItemCount(GIANT_SPIDER_HUSK) < 20)
								htmltext = "30039-03.htm";
							else
							{
								htmltext = "30039-04.htm";
								playSound(player, SOUND_MIDDLE);
								takeItems(player, GIANT_SPIDER_HUSK, -1);
								takeItems(player, GLUDIO_GUARD_MARK_3, 1);
								giveItems(player, COIN_OF_LORDS_5, 1);
							}
						}
						else
							htmltext = "30039-01.htm";
						break;
					
					case BIOTIN:
						if (player.getInventory().hasItems(COIN_OF_LORDS_6))
							htmltext = "30031-05.htm";
						else if (player.getInventory().hasItems(EINHASAD_CHURCH_MARK_3))
						{
							if (player.getInventory().getItemCount(HORRIBLE_SKULL) < 10)
								htmltext = "30031-03.htm";
							else
							{
								htmltext = "30031-04.htm";
								playSound(player, SOUND_MIDDLE);
								takeItems(player, HORRIBLE_SKULL, -1);
								takeItems(player, EINHASAD_CHURCH_MARK_3, 1);
								giveItems(player, COIN_OF_LORDS_6, 1);
							}
						}
						else
							htmltext = "30031-01.htm";
						break;
					
					case SIR_AARON_TANFORD:
						htmltext = "30653-01.htm";
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
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		switch (npc.getNpcId())
		{
			case BUGBEAR_RAIDER:
				if (player.getInventory().hasItems(GLUDIO_GUARD_MARK_1))
					dropItemsAlways(player, BUGBEAR_NECKLACE, 1, 10);
				break;
			
			case UNDEAD_PRIEST:
				if (player.getInventory().hasItems(EINHASAD_CHURCH_MARK_1))
					dropItems(player, EINHASAD_CRUCIFIX, 1, 12, 500000);
				break;
			
			case VENOMOUS_SPIDER:
			case ARACHNID_TRACKER:
			case ARACHNID_PREDATOR:
				if (player.getInventory().hasItems(GLUDIO_GUARD_MARK_2))
					dropItemsAlways(player, SPIDER_LEG, 1, 20);
				break;
			
			case LANGK_LIZARDMAN:
			case LANGK_LIZARDMAN_SCOUT:
			case LANGK_LIZARDMAN_WARRIOR:
				if (player.getInventory().hasItems(EINHASAD_CHURCH_MARK_2))
					dropItems(player, LIZARDMAN_TOTEM, 1, 20, 500000);
				break;
			
			case GIANT_SPIDER:
			case TALON_SPIDER:
			case BLADE_SPIDER:
				if (player.getInventory().hasItems(GLUDIO_GUARD_MARK_3))
					dropItems(player, GIANT_SPIDER_HUSK, 1, 20, 400000);
				break;
			
			case SILENT_HORROR:
				if (player.getInventory().hasItems(EINHASAD_CHURCH_MARK_3))
					dropItems(player, HORRIBLE_SKULL, 1, 10, 400000);
				break;
		}
	}
}