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

import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.QueryParser;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q663_SeductiveWhispers extends Quest
{
	private static final String QUEST_NAME = "Q663_SeductiveWhispers";
	
	private static final int WILBERT = 30846;
	
	public static final int q_orb_of_spirit = 8766;
	
	public static final int adena = 57;
	public static final int scrl_of_ench_wp_a = 729;
	public static final int scrl_of_ench_am_a = 730;
	
	public static final int scrl_of_ench_wp_d = 955;
	public static final int scrl_of_ench_wp_c = 951;
	
	public static final int scrl_of_ench_wp_b = 947;
	public static final int scrl_of_ench_am_b = 948;
	
	private static final int rp_great_sword_i = 4963;
	private static final int rp_heavy_war_axe_i = 4964;
	private static final int rp_sprites_staff_i = 4965;
	private static final int rp_kshanberk_i = 4966;
	private static final int rp_sword_of_valhalla_i = 4967;
	private static final int rp_kris_i = 4968;
	private static final int rp_hell_knife_i = 4969;
	private static final int rp_arthro_nail_i = 4970;
	private static final int rp_dark_elven_long_bow_i = 4971;
	private static final int rp_great_axe_i = 4972;
	private static final int rp_sword_of_damascus_i = 5000;
	private static final int rp_lancia_i = 5001;
	private static final int rp_deadmans_glory_i = 5002;
	private static final int rp_art_of_battle_axe_i = 5003;
	private static final int rp_staff_of_evil_sprit_i = 5004;
	private static final int rp_demons_sword_i = 5005;
	private static final int rp_bellion_cestus_i = 5006;
	private static final int rp_hazard_bow_i = 5007;
	
	private static final int great_sword_blade = 4104;
	private static final int great_axe_head = 4105;
	private static final int dark_elven_long_bow_shaft = 4112;
	private static final int sword_of_valhalla_blade = 4108;
	private static final int arthro_nail_blade = 4111;
	private static final int sprites_staff_head = 4106;
	private static final int kris_edge = 4109;
	private static final int kshanberk_blade = 4107;
	private static final int heavy_war_axe_head = 4105;
	private static final int hell_knife_edge = 4110;
	private static final int sword_of_damascus_blade = 4114;
	private static final int lancia_blade = 4115;
	private static final int bellion_cestus_edge = 4120;
	private static final int staff_of_evil_sprit_head = 4118;
	private static final int deadmans_glory_stone = 4116;
	private static final int art_of_battle_axe_blade = 4117;
	private static final int demons_sword_edge = 4119;
	private static final int hazard_bow_shaft = 4121;
	
	private static final Map<Integer, NpcStringId> CARDS = HashMap.newHashMap(11);
	
	private static final Map<Integer, Integer> CHANCES = HashMap.newHashMap(27);
	
	public Q663_SeductiveWhispers()
	{
		super(663, "Seductive Whispers");
		
		CARDS.put(0, NpcStringId.ID_66300);
		CARDS.put(11, NpcStringId.ID_66311);
		CARDS.put(12, NpcStringId.ID_66312);
		CARDS.put(13, NpcStringId.ID_66313);
		CARDS.put(14, NpcStringId.ID_66314);
		CARDS.put(15, NpcStringId.ID_66315);
		CARDS.put(21, NpcStringId.ID_66321);
		CARDS.put(22, NpcStringId.ID_66322);
		CARDS.put(23, NpcStringId.ID_66323);
		CARDS.put(24, NpcStringId.ID_66324);
		CARDS.put(25, NpcStringId.ID_66325);
		
		CHANCES.put(20674, 807000);
		CHANCES.put(20678, 372000);
		CHANCES.put(20954, 460000);
		CHANCES.put(20955, 537000);
		CHANCES.put(20956, 540000);
		CHANCES.put(20957, 565000);
		CHANCES.put(20958, 425000);
		CHANCES.put(20959, 682000);
		CHANCES.put(20960, 372000);
		CHANCES.put(20961, 547000);
		CHANCES.put(20962, 522000);
		CHANCES.put(20963, 498000);
		CHANCES.put(20974, 1000000);
		CHANCES.put(20975, 975000);
		CHANCES.put(20976, 825000);
		CHANCES.put(20996, 385000);
		CHANCES.put(20997, 342000);
		CHANCES.put(20998, 377000);
		CHANCES.put(20999, 450000);
		CHANCES.put(21000, 395000);
		CHANCES.put(21001, 535000);
		CHANCES.put(21002, 472000);
		CHANCES.put(21006, 502000);
		CHANCES.put(21007, 540000);
		CHANCES.put(21008, 692000);
		CHANCES.put(21009, 740000);
		CHANCES.put(21010, 595000);
		
		setItemsIds(q_orb_of_spirit);
		
		addQuestStart(WILBERT);
		addTalkId(WILBERT);
		
		for (int npcId : CHANCES.keySet())
			addMyDying(npcId);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player talker)
	{
		final Map<String, String> props = QueryParser.parse(event);
		final int ask = Integer.parseInt(props.getOrDefault("ask", "0"));
		final int reply = Integer.parseInt(props.getOrDefault("reply", "0"));
		final QuestState st = talker.getQuestList().getQuestState(QUEST_NAME);
		
		if (st == null)
			return event;
		
		if (props.getOrDefault("$name", "").equals("link"))
			return props.getOrDefault("htm", "");
			
		if (props.getOrDefault("$name", "").equals("quest_accept"))
		{
			setMemo(st);
			st.setCond(1);
			setMemoState(st, 1);
			playSound(talker, SOUND_ACCEPT);
			return "blacksmith_wilbert_q0663_03.htm";
		}
		
		if (props.getOrDefault("$name", "").equals("menu_select") && ask == 663)
		{
			if (reply == 1 && !hasMemo(st) && talker.getStatus().getLevel() >= 50)
				return "blacksmith_wilbert_q0663_01a.htm";
			
			if (reply == 4 && hasMemo(st) && getMemoState(st) % 10 <= 4)
			{
				if (hasMemo(st) && getMemoState(st) / 10 < 1)
				{
					if (ownItemCount(talker, q_orb_of_spirit) >= 50)
					{
						deleteItem1(talker, q_orb_of_spirit, 50);
						setMemoState(st, 5);
						setMemoStateEx(st, 0);
						return "blacksmith_wilbert_q0663_09.htm";
					}
					else
						return "blacksmith_wilbert_q0663_10.htm";
				}
				else
				{
					int i0 = getMemoState(st) / 10;
					int i1 = i0 * 10 + 5;
					setMemoState(st, i1);
					setMemoStateEx(st, 0);
					return "blacksmith_wilbert_q0663_09a.htm";
				}
			}
			
			if (reply == 5 && hasMemo(st) && getMemoState(st) % 10 == 5 && getMemoState(st) / 1000 == 0)
			{
				int card1pic = getMemoStateEx(st);
				if (card1pic < 0)
					card1pic = 0;
				
				int card1num = card1pic % 10;
				int card1kind = (card1pic - card1num) / 10;
				int card2num = Rnd.get(5) + 1;
				int card2kind = Rnd.get(2) + 1;
				int card2pic = card2kind * 10 + card2num;
				int wincount = getMemoState(st) / 10;
				if (card1kind == card2kind)
				{
					int sum = card1num + card2num;
					if (sum % 5 == 0 && sum != 10)
					{
						if ((getMemoState(st) % 100) / 10 >= 7)
						{
							giveItem1(talker, adena, 2384000);
							giveItem1(talker, scrl_of_ench_wp_a, 1);
							giveItem1(talker, scrl_of_ench_am_a, 2);
							setMemoState(st, 4);
							return showHTML(talker, "blacksmith_wilbert_q0663_14.htm", card1pic, card2pic);
						}
						else
						{
							setMemoState(st, (getMemoState(st) / 10) * 10 + 7);
							return showHTML(talker, "blacksmith_wilbert_q0663_13.htm", card1pic, card2pic, wincount + 1);
						}
					}
					else
					{
						setMemoStateEx(st, card2pic);
						setMemoState(st, (getMemoState(st) / 10) * 10 + 6);
						return showHTML(talker, "blacksmith_wilbert_q0663_12.htm", card1pic, card2pic);
					}
				}
				else
				{
					if (card1num == 5 || card2num == 5)
					{
						if ((getMemoState(st) % 100) / 10 >= 7)
						{
							giveItem1(talker, adena, 2384000);
							giveItem1(talker, scrl_of_ench_wp_a, 1);
							giveItem1(talker, scrl_of_ench_am_a, 2);
							setMemoState(st, 4);
							return showHTML(talker, "blacksmith_wilbert_q0663_14.htm", card1pic, card2pic);
						}
						else
						{
							setMemoState(st, (getMemoState(st) / 10) * 10 + 7);
							return showHTML(talker, "blacksmith_wilbert_q0663_13.htm", card1pic, card2pic, wincount + 1);
						}
					}
					else
					{
						setMemoStateEx(st, card2pic);
						setMemoState(st, (getMemoState(st) / 10) * 10 + 6);
						return showHTML(talker, "blacksmith_wilbert_q0663_12.htm", card1pic, card2pic);
					}
				}
			}
			
			if (reply == 6 && hasMemo(st) && getMemoState(st) % 10 == 6 && getMemoState(st) / 1000 == 0)
			{
				int card1pic = getMemoStateEx(st);
				if (card1pic < 0)
					card1pic = 0;
				
				int card1num = card1pic % 10;
				int card1kind = (card1pic - card1num) / 10;
				int card2num = Rnd.get(5) + 1;
				int card2kind = Rnd.get(2) + 1;
				int card2pic = card2kind * 10 + card2num;
				if (card1kind == card2kind)
				{
					int sum = card1num + card2num;
					if (sum % 5 == 0 && sum != 10)
					{
						setMemoState(st, 1);
						setMemoStateEx(st, 0);
						return showHTML(talker, "blacksmith_wilbert_q0663_19.htm", card1pic, card2pic);
					}
					else
					{
						setMemoState(st, (getMemoState(st) / 10) * 10 + 5);
						setMemoStateEx(st, card2pic);
						return showHTML(talker, "blacksmith_wilbert_q0663_18.htm", card1pic, card2pic);
					}
				}
				else
				{
					if (card1num == 5 || card2num == 5)
					{
						setMemoState(st, 1);
						return showHTML(talker, "blacksmith_wilbert_q0663_19.htm", card1pic, card2pic);
					}
					else
					{
						setMemoState(st, (getMemoState(st) / 10) * 10 + 5);
						setMemoStateEx(st, card2pic);
						return showHTML(talker, "blacksmith_wilbert_q0663_18.htm", card1pic, card2pic);
					}
				}
			}
			
			if (reply == 8 && hasMemo(st) && getMemoState(st) % 10 == 7 && getMemoState(st) / 1000 == 0)
			{
				setMemoState(st, (getMemoState(st) / 10 + 1) * 10 + 4);
				setMemoStateEx(st, 0);
				return "blacksmith_wilbert_q0663_20.htm";
			}
			
			if (reply == 9 && hasMemo(st) && getMemoState(st) % 10 == 7 && getMemoState(st) / 1000 == 0)
			{
				int wincount = getMemoState(st) / 10;
				switch (wincount)
				{
					case 0 -> giveItem1(talker, adena, 40000);
					case 1 -> giveItem1(talker, adena, 80000);
					case 2 -> {
						giveItem1(talker, adena, 110000);
						giveItem1(talker, scrl_of_ench_wp_d, 1);
					}
					case 3 -> {
						giveItem1(talker, adena, 199000);
						giveItem1(talker, scrl_of_ench_wp_c, 1);
					}
					case 4 -> {
						giveItem1(talker, adena, 388000);
						switch (Rnd.get(18))
						{
							case 0 -> giveItem1(talker, rp_great_sword_i, 1);
							case 1 -> giveItem1(talker, rp_heavy_war_axe_i, 1);
							case 2 -> giveItem1(talker, rp_sprites_staff_i, 1);
							case 3 -> giveItem1(talker, rp_kshanberk_i, 1);
							case 4 -> giveItem1(talker, rp_sword_of_valhalla_i, 1);
							case 5 -> giveItem1(talker, rp_kris_i, 1);
							case 6 -> giveItem1(talker, rp_hell_knife_i, 1);
							case 7 -> giveItem1(talker, rp_arthro_nail_i, 1);
							case 8 -> giveItem1(talker, rp_dark_elven_long_bow_i, 1);
							case 9 -> giveItem1(talker, rp_great_axe_i, 1);
							case 10 -> giveItem1(talker, rp_sword_of_damascus_i, 1);
							case 11 -> giveItem1(talker, rp_lancia_i, 1);
							case 12 -> giveItem1(talker, rp_deadmans_glory_i, 1);
							case 13 -> giveItem1(talker, rp_art_of_battle_axe_i, 1);
							case 14 -> giveItem1(talker, rp_staff_of_evil_sprit_i, 1);
							case 15 -> giveItem1(talker, rp_demons_sword_i, 1);
							case 16 -> giveItem1(talker, rp_bellion_cestus_i, 1);
							case 17 -> giveItem1(talker, rp_hazard_bow_i, 1);
						}
					}
					case 5 -> {
						giveItem1(talker, adena, 675000);
						switch (Rnd.get(18))
						{
							case 0 -> giveItem1(talker, great_sword_blade, 12);
							case 1 -> giveItem1(talker, great_axe_head, 12);
							case 2 -> giveItem1(talker, dark_elven_long_bow_shaft, 12);
							case 3 -> giveItem1(talker, sword_of_valhalla_blade, 12);
							case 4 -> giveItem1(talker, arthro_nail_blade, 12);
							case 5 -> giveItem1(talker, sprites_staff_head, 12);
							case 6 -> giveItem1(talker, kris_edge, 12);
							case 7 -> giveItem1(talker, kshanberk_blade, 12);
							case 8 -> giveItem1(talker, heavy_war_axe_head, 12);
							case 9 -> giveItem1(talker, hell_knife_edge, 12);
							case 10 -> giveItem1(talker, sword_of_damascus_blade, 13);
							case 11 -> giveItem1(talker, lancia_blade, 13);
							case 12 -> giveItem1(talker, bellion_cestus_edge, 13);
							case 13 -> giveItem1(talker, staff_of_evil_sprit_head, 13);
							case 14 -> giveItem1(talker, deadmans_glory_stone, 13);
							case 15 -> giveItem1(talker, art_of_battle_axe_blade, 13);
							case 16 -> giveItem1(talker, demons_sword_edge, 13);
							case 17 -> giveItem1(talker, hazard_bow_shaft, 13);
						}
					}
					case 6 -> {
						giveItem1(talker, adena, 1284000);
						giveItem1(talker, scrl_of_ench_wp_b, 2);
						giveItem1(talker, scrl_of_ench_am_b, 2);
					}
				}
				setMemoState(st, 1);
				setMemoStateEx(st, 0);
				return "blacksmith_wilbert_q0663_21.htm";
			}
			
			if (reply == 10 && hasMemo(st) && getMemoState(st) == 1 && getMemoState(st) / 1000 == 0)
				return "blacksmith_wilbert_q0663_21a.htm";
			
			if (reply == 14 && hasMemo(st) && getMemoState(st) % 10 == 1)
			{
				if (ownItemCount(talker, q_orb_of_spirit) >= 1)
				{
					deleteItem1(talker, q_orb_of_spirit, 1);
					setMemoState(st, 1005);
					return "blacksmith_wilbert_q0663_22.htm";
				}
				else
					return "blacksmith_wilbert_q0663_22a.htm";
			}
			
			if (reply == 15 && hasMemo(st) && getMemoState(st) == 1005)
			{
				int card1pic = getMemoStateEx(st);
				if (card1pic < 0)
					card1pic = 0;
				
				int card1num = card1pic % 10;
				int card1type = (card1pic - card1num) / 10;
				int card2num = Rnd.get(5) + 1;
				int card2type = Rnd.get(2) + 1;
				int card2pic = card2type * 10 + card2num;
				if (card2type == card1type)
				{
					if ((card1num + card2num) % 5 == 0 && (card1num + card2num) != 10)
					{
						setMemoState(st, 1);
						setMemoStateEx(st, 0);
						giveItem1(talker, adena, 800);
						return showHTML(talker, "blacksmith_wilbert_q0663_25.htm", card1pic, card2pic);
					}
					else
					{
						setMemoState(st, 1006);
						setMemoStateEx(st, card2pic);
						return showHTML(talker, "blacksmith_wilbert_q0663_24.htm", card1pic, card2pic);
					}
				}
				else
				{
					if (card1num == 5 || card2num == 5)
					{
						setMemoState(st, 1);
						setMemoStateEx(st, 0);
						giveItem1(talker, adena, 800);
						return showHTML(talker, "blacksmith_wilbert_q0663_25.htm", card1pic, card2pic);
						
					}
					else
					{
						setMemoState(st, 1006);
						setMemoStateEx(st, card2pic);
						return showHTML(talker, "blacksmith_wilbert_q0663_24.htm", card1pic, card2pic);
					}
				}
			}
			
			if (reply == 16 && hasMemo(st) && getMemoState(st) == 1006)
			{
				int card1pic = getMemoStateEx(st);
				if (card1pic < 0)
					card1pic = 0;
				
				int card1num = card1pic % 10;
				int card1type = (card1pic - card1num) / 10;
				int card2num = Rnd.get(5) + 1;
				int card2type = Rnd.get(2) + 1;
				int card2pic = card2type * 10 + card2num;
				if (card2type == card1type)
				{
					if ((card1num + card2num) % 5 == 0 && (card1num + card2num) != 10)
					{
						setMemoState(st, 1);
						setMemoStateEx(st, 0);
						return showHTML(talker, "blacksmith_wilbert_q0663_29.htm", card1pic, card2pic);
					}
					else
					{
						setMemoState(st, 1005);
						setMemoStateEx(st, card2pic);
						return showHTML(talker, "blacksmith_wilbert_q0663_28.htm", card1pic, card2pic);
					}
				}
				else
				{
					if (card1num == 5 || card2num == 5)
					{
						setMemoState(st, 1);
						setMemoStateEx(st, 0);
						return showHTML(talker, "blacksmith_wilbert_q0663_29.htm", card1pic, card2pic);
						
					}
					else
					{
						setMemoState(st, 1005);
						setMemoStateEx(st, card2pic);
						return showHTML(talker, "blacksmith_wilbert_q0663_28.htm", card1pic, card2pic);
					}
				}
			}
			if (reply == 20 && hasMemo(st))
			{
				removeMemo(st);
				playSound(talker, SOUND_FINISH);
				return "blacksmith_wilbert_q0663_30.htm";
			}
			
			if (reply == 21 && hasMemo(st))
				return "blacksmith_wilbert_q0663_31.htm";
			
			if (reply == 22 && hasMemo(st))
				return "blacksmith_wilbert_q0663_32.htm";
		}
		
		return event;
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		QuestState st = talker.getQuestList().getQuestState(QUEST_NAME);
		
		if (st == null)
			return getNoQuestMsg();
		
		if (!hasMemo(st) && talker.getStatus().getLevel() >= 50)
			return "blacksmith_wilbert_q0663_01.htm";
		
		if (!hasMemo(st) && talker.getStatus().getLevel() < 50)
			return "blacksmith_wilbert_q0663_02.htm";
		
		if (hasMemo(st) && getMemoState(st) < 4 && getMemoState(st) >= 1 && ownItemCount(talker, q_orb_of_spirit) == 0)
			return "blacksmith_wilbert_q0663_04.htm";
		
		if (hasMemo(st) && getMemoState(st) < 4 && getMemoState(st) >= 1 && ownItemCount(talker, q_orb_of_spirit) > 0)
			return "blacksmith_wilbert_q0663_05.htm";
		
		if (hasMemo(st) && getMemoState(st) == 1005)
			return "blacksmith_wilbert_q0663_23.htm";
		
		if (hasMemo(st) && getMemoState(st) == 1006)
			return "blacksmith_wilbert_q0663_26.htm";
		
		if (hasMemo(st) && getMemoState(st) % 10 == 4 && !isPractice(st))
			return "blacksmith_wilbert_q0663_05a.htm";
		
		if (hasMemo(st) && getMemoState(st) % 10 == 5 && !isPractice(st))
			return "blacksmith_wilbert_q0663_11.htm";
		
		if (hasMemo(st) && getMemoState(st) % 10 == 6 && !isPractice(st))
			return "blacksmith_wilbert_q0663_15.htm";
		
		if (hasMemo(st) && getMemoState(st) % 10 == 7 && !isPractice(st))
		{
			if (winCount(st) >= 7)
			{
				setMemoState(st, 1);
				giveItem1(talker, adena, 2384000);
				giveItem1(talker, scrl_of_ench_wp_a, 1);
				giveItem1(talker, scrl_of_ench_am_a, 2);
				return "blacksmith_wilbert_q0663_17.htm";
			}
			else
				return showHTML(talker, "blacksmith_wilbert_q0663_16.htm", 0, 0, (getMemoState(st) / 10 + 1));
		}
		
		return getNoQuestMsg();
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		dropItems(st.getPlayer(), q_orb_of_spirit, 1, 0, CHANCES.get(npc.getNpcId()));
	}
	
	public int winCount(QuestState st)
	{
		return (getMemoState(st) % 100) / 10;
	}
	
	public boolean isPractice(QuestState st)
	{
		return getMemoState(st) / 1000 == 1;
	}
	
	public String showHTML(Player talker, String html, int card1pic, int card2pic)
	{
		return showHTML(talker, html, card1pic, card2pic, 0);
	}
	
	public String showHTML(Player talker, String html, int card1pic, int card2pic, int wincount)
	{
		return getHtmlText(html, talker).replace("<?card1pic?>", CARDS.get(card1pic).getMessage()).replace("<?card2pic?>", CARDS.get(card2pic).getMessage()).replace("<?name?>", talker.getName()).replace("<?wincount?>", "" + wincount);
	}
}