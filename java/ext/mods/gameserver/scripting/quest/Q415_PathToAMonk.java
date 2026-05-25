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
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q415_PathToAMonk extends Quest
{
	private static final String QUEST_NAME = "Q415_PathToAMonk";
	
	private static final int POMEGRANATE = 1593;
	private static final int LEATHER_POUCH_1 = 1594;
	private static final int LEATHER_POUCH_2 = 1595;
	private static final int LEATHER_POUCH_3 = 1596;
	private static final int LEATHER_POUCH_FULL_1 = 1597;
	private static final int LEATHER_POUCH_FULL_2 = 1598;
	private static final int LEATHER_POUCH_FULL_3 = 1599;
	private static final int KASHA_BEAR_CLAW = 1600;
	private static final int KASHA_BLADE_SPIDER_TALON = 1601;
	private static final int SCARLET_SALAMANDER_SCALE = 1602;
	private static final int FIERY_SPIRIT_SCROLL = 1603;
	private static final int ROSHEEK_LETTER = 1604;
	private static final int GANTAKI_LETTER_OF_RECOMMENDATION = 1605;
	private static final int FIG = 1606;
	private static final int LEATHER_POUCH_4 = 1607;
	private static final int LEATHER_POUCH_FULL_4 = 1608;
	private static final int VUKU_ORC_TUSK = 1609;
	private static final int RATMAN_FANG = 1610;
	private static final int LANG_KLIZARDMAN_TOOTH = 1611;
	private static final int FELIM_LIZARDMAN_TOOTH = 1612;
	private static final int IRON_WILL_SCROLL = 1613;
	private static final int TORUKU_LETTER = 1614;
	private static final int KHAVATARI_TOTEM = 1615;
	private static final int KASHA_SPIDER_TOOTH = 8545;
	private static final int HORN_OF_BAAR_DRE_VANUL = 8546;
	
	private static final int GANTAKI = 30587;
	private static final int ROSHEEK = 30590;
	private static final int KASMAN = 30501;
	private static final int TORUKU = 30591;
	private static final int AREN = 32056;
	private static final int MOIRA = 31979;
	
	private static final int FELIM_LIZARDMAN_WARRIOR = 20014;
	private static final int VUKU_ORC_FIGHTER = 20017;
	private static final int LANGK_LIZZARDMAN_WARRIOR = 20024;
	private static final int RATMAN_WARRIOR = 20359;
	private static final int SCARLET_SALAMANDER = 20415;
	private static final int KASHA_FANG_SPIDER = 20476;
	private static final int KASHA_BLADE_SPIDER = 20478;
	private static final int KASHA_BEAR = 20479;
	private static final int BAAR_DRE_VANUL = 21118;
	
	public Q415_PathToAMonk()
	{
		super(415, "Path to a Monk");
		
		setItemsIds(POMEGRANATE, LEATHER_POUCH_1, LEATHER_POUCH_2, LEATHER_POUCH_3, LEATHER_POUCH_FULL_1, LEATHER_POUCH_FULL_2, LEATHER_POUCH_FULL_3, KASHA_BEAR_CLAW, KASHA_BLADE_SPIDER_TALON, SCARLET_SALAMANDER_SCALE, FIERY_SPIRIT_SCROLL, ROSHEEK_LETTER, GANTAKI_LETTER_OF_RECOMMENDATION, FIG, LEATHER_POUCH_4, LEATHER_POUCH_FULL_4, VUKU_ORC_TUSK, RATMAN_FANG, LANG_KLIZARDMAN_TOOTH, FELIM_LIZARDMAN_TOOTH, IRON_WILL_SCROLL, TORUKU_LETTER, KASHA_SPIDER_TOOTH, HORN_OF_BAAR_DRE_VANUL);
		
		addQuestStart(GANTAKI);
		addTalkId(GANTAKI, ROSHEEK, KASMAN, TORUKU, AREN, MOIRA);
		
		addMyDying(FELIM_LIZARDMAN_WARRIOR, VUKU_ORC_FIGHTER, LANGK_LIZZARDMAN_WARRIOR, RATMAN_WARRIOR, SCARLET_SALAMANDER, KASHA_FANG_SPIDER, KASHA_BLADE_SPIDER, KASHA_BEAR, BAAR_DRE_VANUL);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30587-05.htm"))
		{
			if (player.getClassId() != ClassId.ORC_FIGHTER)
				htmltext = (player.getClassId() == ClassId.MONK) ? "30587-02a.htm" : "30587-02.htm";
			else if (player.getStatus().getLevel() < 19)
				htmltext = "30587-03.htm";
			else if (player.getInventory().hasItems(KHAVATARI_TOTEM))
				htmltext = "30587-04.htm";
		}
		else if (event.equalsIgnoreCase("30587-06.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, POMEGRANATE, 1);
		}
		else if (event.equalsIgnoreCase("30587-09a.htm"))
		{
			st.setCond(9);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, ROSHEEK_LETTER, 1);
			giveItems(player, GANTAKI_LETTER_OF_RECOMMENDATION, 1);
		}
		else if (event.equalsIgnoreCase("30587-09b.htm"))
		{
			st.setCond(14);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, ROSHEEK_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("32056-03.htm"))
		{
			st.setCond(15);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("32056-08.htm"))
		{
			st.setCond(20);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("31979-03.htm"))
		{
			takeItems(player, FIERY_SPIRIT_SCROLL, 1);
			giveItems(player, KHAVATARI_TOTEM, 1);
			rewardExpAndSp(player, 3200, 4230);
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
				htmltext = "30587-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case GANTAKI:
						if (cond == 1)
							htmltext = "30587-07.htm";
						else if (cond > 1 && cond < 8)
							htmltext = "30587-08.htm";
						else if (cond == 8)
							htmltext = "30587-09.htm";
						else if (cond == 9)
							htmltext = "30587-10.htm";
						else if (cond > 9)
							htmltext = "30587-11.htm";
						break;
					
					case ROSHEEK:
						if (cond == 1)
						{
							htmltext = "30590-01.htm";
							st.setCond(2);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, POMEGRANATE, 1);
							giveItems(player, LEATHER_POUCH_1, 1);
						}
						else if (cond == 2)
							htmltext = "30590-02.htm";
						else if (cond == 3)
						{
							htmltext = "30590-03.htm";
							st.setCond(4);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, LEATHER_POUCH_FULL_1, 1);
							giveItems(player, LEATHER_POUCH_2, 1);
						}
						else if (cond == 4)
							htmltext = "30590-04.htm";
						else if (cond == 5)
						{
							htmltext = "30590-05.htm";
							st.setCond(6);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, LEATHER_POUCH_FULL_2, 1);
							giveItems(player, LEATHER_POUCH_3, 1);
						}
						else if (cond == 6)
							htmltext = "30590-06.htm";
						else if (cond == 7)
						{
							htmltext = "30590-07.htm";
							st.setCond(8);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, LEATHER_POUCH_FULL_3, 1);
							giveItems(player, FIERY_SPIRIT_SCROLL, 1);
							giveItems(player, ROSHEEK_LETTER, 1);
						}
						else if (cond == 8)
							htmltext = "30590-08.htm";
						else if (cond > 8)
							htmltext = "30590-09.htm";
						break;
					
					case KASMAN:
						if (cond == 9)
						{
							htmltext = "30501-01.htm";
							st.setCond(10);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, GANTAKI_LETTER_OF_RECOMMENDATION, 1);
							giveItems(player, FIG, 1);
						}
						else if (cond == 10)
							htmltext = "30501-02.htm";
						else if (cond == 11 || cond == 12)
							htmltext = "30501-03.htm";
						else if (cond == 13)
						{
							htmltext = "30501-04.htm";
							takeItems(player, FIERY_SPIRIT_SCROLL, 1);
							takeItems(player, IRON_WILL_SCROLL, 1);
							takeItems(player, TORUKU_LETTER, 1);
							giveItems(player, KHAVATARI_TOTEM, 1);
							rewardExpAndSp(player, 3200, 1500);
							player.broadcastPacket(new SocialAction(player, 3));
							playSound(player, SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case TORUKU:
						if (cond == 10)
						{
							htmltext = "30591-01.htm";
							st.setCond(11);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, FIG, 1);
							giveItems(player, LEATHER_POUCH_4, 1);
						}
						else if (cond == 11)
							htmltext = "30591-02.htm";
						else if (cond == 12)
						{
							htmltext = "30591-03.htm";
							st.setCond(13);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, LEATHER_POUCH_FULL_4, 1);
							giveItems(player, IRON_WILL_SCROLL, 1);
							giveItems(player, TORUKU_LETTER, 1);
						}
						else if (cond == 13)
							htmltext = "30591-04.htm";
						break;
					
					case AREN:
						if (cond == 14)
							htmltext = "32056-01.htm";
						else if (cond == 15)
							htmltext = "32056-04.htm";
						else if (cond == 16)
						{
							htmltext = "32056-05.htm";
							st.setCond(17);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, KASHA_SPIDER_TOOTH, -1);
						}
						else if (cond == 17)
							htmltext = "32056-06.htm";
						else if (cond == 18)
						{
							htmltext = "32056-07.htm";
							st.setCond(19);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, HORN_OF_BAAR_DRE_VANUL, -1);
						}
						else if (cond == 20)
							htmltext = "32056-09.htm";
						break;
					
					case MOIRA:
						if (cond == 20)
							htmltext = "31979-01.htm";
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
		
		final WeaponType weapon = player.getAttackType();
		if (weapon != WeaponType.DUALFIST && weapon != WeaponType.FIST)
		{
			playSound(player, SOUND_GIVEUP);
			st.exitQuest(true);
			return;
		}
		
		switch (npc.getNpcId())
		{
			case KASHA_BEAR:
				if (st.getCond() == 2 && dropItemsAlways(player, KASHA_BEAR_CLAW, 1, 5))
				{
					st.setCond(3);
					takeItems(player, KASHA_BEAR_CLAW, -1);
					takeItems(player, LEATHER_POUCH_1, 1);
					giveItems(player, LEATHER_POUCH_FULL_1, 1);
				}
				break;
			
			case KASHA_BLADE_SPIDER:
				if (st.getCond() == 4 && dropItemsAlways(player, KASHA_BLADE_SPIDER_TALON, 1, 5))
				{
					st.setCond(5);
					takeItems(player, KASHA_BLADE_SPIDER_TALON, -1);
					takeItems(player, LEATHER_POUCH_2, 1);
					giveItems(player, LEATHER_POUCH_FULL_2, 1);
				}
				else if (st.getCond() == 15 && dropItems(player, KASHA_SPIDER_TOOTH, 1, 6, 500000))
					st.setCond(16);
				break;
			
			case KASHA_FANG_SPIDER:
				if (st.getCond() == 15 && dropItems(player, KASHA_SPIDER_TOOTH, 1, 6, 500000))
					st.setCond(16);
				break;
			
			case SCARLET_SALAMANDER:
				if (st.getCond() == 6 && dropItemsAlways(player, SCARLET_SALAMANDER_SCALE, 1, 5))
				{
					st.setCond(7);
					takeItems(player, SCARLET_SALAMANDER_SCALE, -1);
					takeItems(player, LEATHER_POUCH_3, 1);
					giveItems(player, LEATHER_POUCH_FULL_3, 1);
				}
				break;
			
			case FELIM_LIZARDMAN_WARRIOR:
				if (st.getCond() == 11 && dropItemsAlways(player, FELIM_LIZARDMAN_TOOTH, 1, 3))
				{
					if (player.getInventory().getItemCount(RATMAN_FANG) == 3 && player.getInventory().getItemCount(LANG_KLIZARDMAN_TOOTH) == 3 && player.getInventory().getItemCount(VUKU_ORC_TUSK) == 3)
					{
						st.setCond(12);
						takeItems(player, VUKU_ORC_TUSK, -1);
						takeItems(player, RATMAN_FANG, -1);
						takeItems(player, LANG_KLIZARDMAN_TOOTH, -1);
						takeItems(player, FELIM_LIZARDMAN_TOOTH, -1);
						takeItems(player, LEATHER_POUCH_4, 1);
						giveItems(player, LEATHER_POUCH_FULL_4, 1);
					}
				}
				break;
			
			case VUKU_ORC_FIGHTER:
				if (st.getCond() == 11 && dropItemsAlways(player, VUKU_ORC_TUSK, 1, 3))
				{
					if (player.getInventory().getItemCount(RATMAN_FANG) == 3 && player.getInventory().getItemCount(LANG_KLIZARDMAN_TOOTH) == 3 && player.getInventory().getItemCount(FELIM_LIZARDMAN_TOOTH) == 3)
					{
						st.setCond(12);
						takeItems(player, VUKU_ORC_TUSK, -1);
						takeItems(player, RATMAN_FANG, -1);
						takeItems(player, LANG_KLIZARDMAN_TOOTH, -1);
						takeItems(player, FELIM_LIZARDMAN_TOOTH, -1);
						takeItems(player, LEATHER_POUCH_4, 1);
						giveItems(player, LEATHER_POUCH_FULL_4, 1);
					}
				}
				break;
			
			case LANGK_LIZZARDMAN_WARRIOR:
				if (st.getCond() == 11 && dropItemsAlways(player, LANG_KLIZARDMAN_TOOTH, 1, 3))
				{
					if (player.getInventory().getItemCount(RATMAN_FANG) == 3 && player.getInventory().getItemCount(FELIM_LIZARDMAN_TOOTH) == 3 && player.getInventory().getItemCount(VUKU_ORC_TUSK) == 3)
					{
						st.setCond(12);
						takeItems(player, VUKU_ORC_TUSK, -1);
						takeItems(player, RATMAN_FANG, -1);
						takeItems(player, LANG_KLIZARDMAN_TOOTH, -1);
						takeItems(player, FELIM_LIZARDMAN_TOOTH, -1);
						takeItems(player, LEATHER_POUCH_4, 1);
						giveItems(player, LEATHER_POUCH_FULL_4, 1);
					}
				}
				break;
			
			case RATMAN_WARRIOR:
				if (st.getCond() == 11 && dropItemsAlways(player, RATMAN_FANG, 1, 3))
				{
					if (player.getInventory().getItemCount(LANG_KLIZARDMAN_TOOTH) == 3 && player.getInventory().getItemCount(FELIM_LIZARDMAN_TOOTH) == 3 && player.getInventory().getItemCount(VUKU_ORC_TUSK) == 3)
					{
						st.setCond(12);
						takeItems(player, VUKU_ORC_TUSK, -1);
						takeItems(player, RATMAN_FANG, -1);
						takeItems(player, LANG_KLIZARDMAN_TOOTH, -1);
						takeItems(player, FELIM_LIZARDMAN_TOOTH, -1);
						takeItems(player, LEATHER_POUCH_4, 1);
						giveItems(player, LEATHER_POUCH_FULL_4, 1);
					}
				}
				break;
			
			case BAAR_DRE_VANUL:
				if (st.getCond() == 17)
				{
					st.setCond(18);
					playSound(player, SOUND_MIDDLE);
					giveItems(player, HORN_OF_BAAR_DRE_VANUL, 1);
				}
				break;
		}
	}
}