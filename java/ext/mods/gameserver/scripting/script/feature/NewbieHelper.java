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
package ext.mods.gameserver.scripting.script.feature;

import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.data.SkillTable.FrequentSkill;
import ext.mods.gameserver.data.xml.NewbieBuffData;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.enums.TeleportType;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.records.NewbieBuff;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class NewbieHelper extends Quest
{
	private static final String QUEST_NAME = "NewbieHelper";
	private static final String QUEST_NAME_TUTORIAL = "Tutorial";
	
	private static final int RECOMMENDATION_01 = 1067;
	private static final int RECOMMENDATION_02 = 1068;
	private static final int LEAF_OF_MOTHERTREE = 1069;
	private static final int BLOOD_OF_JUNDIN = 1070;
	private static final int LICENSE_OF_MINER = 1498;
	private static final int VOUCHER_OF_FLAME = 1496;
	
	private static final int SOULSHOT_NO_GRADE_FOR_BEGINNERS = 5789;
	private static final int SPIRITSHOT_NO_GRADE_FOR_BEGINNERS = 5790;
	private static final int BLUE_GEMSTONE = 6353;
	private static final int NEWBIE_TRAVEL_TOKEN = 8542;
	
	private static final Map<String, Location> TELEPORT_LOCS = new HashMap<>();
	
	static
	{
		TELEPORT_LOCS.put("30598", new Location(-84053, 243343, -3729));
		TELEPORT_LOCS.put("30599", new Location(45470, 48328, -3059));
		TELEPORT_LOCS.put("30600", new Location(12160, 16554, -4583));
		TELEPORT_LOCS.put("30601", new Location(115594, -177993, -912));
		TELEPORT_LOCS.put("30602", new Location(-45067, -113563, -199));
	}
	
	private static final Map<Integer, Location> NEWBIE_GUIDE_LOCS = new HashMap<>();
	
	static
	{
		NEWBIE_GUIDE_LOCS.put(30008, new Location(-84058, 243239, -3730));
		NEWBIE_GUIDE_LOCS.put(30017, new Location(-84058, 243239, -3730));
		NEWBIE_GUIDE_LOCS.put(30129, new Location(12116, 16666, -4610));
		NEWBIE_GUIDE_LOCS.put(30370, new Location(45491, 48359, -3086));
		NEWBIE_GUIDE_LOCS.put(30528, new Location(115632, -177996, -912));
		NEWBIE_GUIDE_LOCS.put(30573, new Location(-45067, -113549, -235));
	}
	
	private static final int[] RADARS =
	{
		30006,
		30039,
		30040,
		30041,
		30042,
		30043,
		30044,
		30045,
		30046,
		30283,
		30003,
		30004,
		30001,
		30002,
		30031,
		30033,
		30035,
		30032,
		30036,
		30026,
		30027,
		30029,
		30028,
		30054,
		30055,
		30005,
		30048,
		30312,
		30368,
		30049,
		30047,
		30497,
		30050,
		30311,
		30051,
		
		30134,
		30224,
		30348,
		30355,
		30347,
		30432,
		30356,
		30349,
		30346,
		30433,
		30357,
		30431,
		30430,
		30307,
		30138,
		30137,
		30135,
		30136,
		30143,
		30360,
		30145,
		30135,
		30144,
		30358,
		30359,
		30141,
		30139,
		30140,
		30350,
		30421,
		30419,
		30130,
		30351,
		30353,
		30354,
		
		30146,
		30285,
		30284,
		30221,
		30217,
		30219,
		30220,
		30218,
		30216,
		30363,
		30149,
		30150,
		30148,
		30147,
		30155,
		30156,
		30157,
		30158,
		30154,
		30153,
		30152,
		30151,
		30423,
		30414,
		30361,
		31853,
		30223,
		30362,
		30222,
		30371,
		31852,
		
		30540,
		30541,
		30542,
		30543,
		30544,
		30545,
		30546,
		30547,
		30548,
		30531,
		30532,
		30533,
		30534,
		30535,
		30536,
		30525,
		30526,
		30527,
		30518,
		30519,
		30516,
		30517,
		30520,
		30521,
		30522,
		30523,
		30524,
		30537,
		30650,
		30538,
		30539,
		30671,
		30651,
		30550,
		30554,
		30553,
		
		30576,
		30577,
		30578,
		30579,
		30580,
		30581,
		30582,
		30583,
		30584,
		30569,
		30570,
		30571,
		30572,
		30564,
		30560,
		30561,
		30558,
		30559,
		30562,
		30563,
		30565,
		30566,
		30567,
		30568,
		30585,
		30587,
	};
	
	public NewbieHelper()
	{
		super(-1, "feature");
		
		addTalkId(30009, 30019, 30131, 30400, 30530, 30575, 30008, 30017, 30129, 30370, 30528, 30573, 30598, 30599, 30600, 30601, 30602, 31076, 31077);
		addFirstTalkId(30009, 30019, 30131, 30400, 30530, 30575, 30008, 30017, 30129, 30370, 30528, 30573, 30598, 30599, 30600, 30601, 30602, 31076, 31077);
		
		addMyDying(18342);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		QuestState qs = player.getQuestList().getQuestState(QUEST_NAME_TUTORIAL);
		if (st == null || qs == null)
			return null;
		
		String htmltext = name;
		
		if (name.equalsIgnoreCase("TimerEx_NewbieHelper"))
		{
			final int ex = qs.getInteger("Ex");
			if (ex == 0)
			{
				switch (player.getClassId())
				{
					case HUMAN_FIGHTER, ELVEN_FIGHTER, DARK_FIGHTER, ORC_FIGHTER, DWARVEN_FIGHTER:
						playTutorialVoice(player, "tutorial_voice_009a");
						break;
					
					case HUMAN_MYSTIC, ELVEN_MYSTIC, DARK_MYSTIC:
						playTutorialVoice(player, "tutorial_voice_009b");
						break;
					
					case ORC_MYSTIC:
						playTutorialVoice(player, "tutorial_voice_009c");
						break;
				}
				qs.set("Ex", 1);
			}
			else if (ex == 3)
			{
				switch (player.getClassId())
				{
					case HUMAN_FIGHTER:
						playTutorialVoice(player, "tutorial_voice_010a");
						break;
					
					case HUMAN_MYSTIC:
						playTutorialVoice(player, "tutorial_voice_010b");
						break;
					
					case ELVEN_FIGHTER, ELVEN_MYSTIC:
						playTutorialVoice(player, "tutorial_voice_010c");
						break;
					
					case DARK_FIGHTER, DARK_MYSTIC:
						playTutorialVoice(player, "tutorial_voice_010d");
						break;
					
					case ORC_FIGHTER, ORC_MYSTIC:
						playTutorialVoice(player, "tutorial_voice_010e");
						break;
					
					case DWARVEN_FIGHTER:
						playTutorialVoice(player, "tutorial_voice_010f");
						break;
				}
				qs.set("Ex", 4);
			}
			return null;
		}
		else if (name.equalsIgnoreCase("TimerEx_GrandMaster"))
		{
			if (qs.getInteger("Ex") >= 4)
			{
				showQuestionMark(player, 7);
				playSound(player, SOUND_TUTORIAL);
				playTutorialVoice(player, "tutorial_voice_025");
			}
			return null;
		}
		
		return (htmltext.isEmpty()) ? null : htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		QuestState qs = player.getQuestList().getQuestState(QUEST_NAME_TUTORIAL);
		if (st == null || qs == null)
			return null;
		
		String htmltext = event;
		
		if (event.equalsIgnoreCase("30008-03.htm") || event.equalsIgnoreCase("30017-03.htm") || event.equalsIgnoreCase("30129-03.htm") || event.equalsIgnoreCase("30370-03.htm") || event.equalsIgnoreCase("30528-03.htm") || event.equalsIgnoreCase("30573-03.htm"))
		{
			player.getRadarList().addMarker(NEWBIE_GUIDE_LOCS.get(npc.getNpcId()));
			
			final int itemId = getItemId(npc.getNpcId());
			if (player.getInventory().hasItems(itemId) && st.getInteger("onlyone") == 0)
			{
				takeItems(player, itemId, 1);
				rewardExpAndSp(player, 0, 50);
				
				startQuestTimer("TimerEx_GrandMaster", null, player, 60000);
				
				if (qs.getInteger("Ex") <= 3)
					qs.set("Ex", 4);
				
				if (player.isMageClass() && player.getClassId() != ClassId.ORC_MYSTIC)
				{
					playTutorialVoice(player, "tutorial_voice_027");
					giveItems(player, SPIRITSHOT_NO_GRADE_FOR_BEGINNERS, 100);
				}
				else
				{
					playTutorialVoice(player, "tutorial_voice_026");
					giveItems(player, SOULSHOT_NO_GRADE_FOR_BEGINNERS, 200);
				}
				
				st.unset("step");
				st.set("onlyone", 1);
			}
		}
		else if (event.startsWith("AskAdvice"))
		{
			switch (npc.getTemplate().getRace())
			{
				case HUMAN:
					if (player.getRace() != ClassRace.HUMAN)
						return "human/guide_human_cnacelot003.htm";
					
					htmltext = "human/guide_human_cnacelot";
					break;
				
				case ELVE:
					if (player.getRace() != ClassRace.ELF)
						return "elf/guide_elf_roios003.htm";
					
					htmltext = "elf/guide_elf_roios";
					break;
				
				case DARKELVE:
					if (player.getRace() != ClassRace.DARK_ELF)
						return "darkelf/guide_delf_frankia003.htm";
					
					htmltext = "darkelf/guide_delf_frankia";
					break;
				
				case ORC:
					if (player.getRace() != ClassRace.ORC)
						return "orc/guide_orc_tanai003.htm";
					
					htmltext = "orc/guide_orc_tanai";
					break;
				
				case DWARVE:
					if (player.getRace() != ClassRace.DWARF)
						return "dwarf/guide_dwarf_gullin003.htm";
					
					htmltext = "dwarf/guide_dwarf_gullin";
					break;
			}
			
			final int level = player.getStatus().getLevel();
			
			if (level >= 20 || player.getClassId().getLevel() != 0)
				htmltext += "002.htm";
			else if (!player.isMageClass())
			{
				if (level <= 5)
					htmltext += "_f05.htm";
				else if (level <= 10)
					htmltext += "_f10.htm";
				else if (level <= 15)
					htmltext += "_f15.htm";
				else
					htmltext += "_f20.htm";
			}
			else if (level <= 7)
				htmltext += "_m07.htm";
			else if (level <= 14)
				htmltext += "_m14.htm";
			else
				htmltext += "_m20.htm";
		}
		else if (event.startsWith("SupportMagic"))
		{
			if (player.isCursedWeaponEquipped())
				return null;
			
			final boolean isMage = player.isMageClass() && player.getClassId() != ClassId.ORC_MYSTIC && player.getClassId() != ClassId.ORC_SHAMAN;
			
			final int playerLevel = player.getStatus().getLevel();
			
			if (playerLevel < NewbieBuffData.getInstance().getLowestBuffLevel(isMage))
				htmltext = "guide_for_newbie002.htm";
			else if (!player.isNewbie(false))
				htmltext = "guide_for_newbie003.htm";
			else
			{
				int i = 0;
				for (NewbieBuff buff : NewbieBuffData.getInstance().getValidBuffs(isMage, playerLevel))
					ThreadPool.schedule(() -> callSkill(player, player, buff.getSkill()), 1000L * i++);
				
				return null;
			}
		}
		else if (event.equals("NewbieToken"))
		{
			if (!player.isNewbie(false))
				htmltext = getInvalidHtm(npc);
			else
			{
				npc.showTeleportWindow(player, TeleportType.NEWBIE_TOKEN);
				return null;
			}
		}
		else if (event.startsWith("NewbieToken"))
		{
			if (!player.getInventory().hasItems(NEWBIE_TRAVEL_TOKEN))
				htmltext = "newbie_guide_no_token.htm";
			else
			{
				final Location loc = TELEPORT_LOCS.get(event);
				if (loc != null)
				{
					takeItems(player, NEWBIE_TRAVEL_TOKEN, 1);
					player.teleportTo(loc, 0);
				}
				return null;
			}
		}
		else if (event.startsWith("GiveBlessing"))
		{
			if (player.getStatus().getLevel() > 39 || player.getClassId().getLevel() >= 2)
				htmltext = getInvalidHtm(npc);
			else
			{
				ThreadPool.schedule(() -> callSkill(player, player, FrequentSkill.BLESSING_OF_PROTECTION.getSkill()), 1000);
				return null;
			}
		}
		else if (event.startsWith("NpcLocationInfo"))
		{
			final int npcId = Integer.parseInt(event.substring(16));
			
			if (!ArraysUtil.contains(RADARS, npcId))
				return null;
			
			npc = World.getInstance().getNpc(npcId);
			if (npc != null)
				player.getRadarList().addMarker(npc.getSpawnLocation());
			
			htmltext = "newbie_guide_move_to_loc.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		final QuestState qs = player.getQuestList().getQuestState(QUEST_NAME_TUTORIAL);
		if (qs == null)
			return null;
		
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			st = newQuestState(player);
		
		final int npcId = npc.getNpcId();
		
		if (npcId == 31076 || npcId == 31077)
			return npcId + ".htm";
		
		if (npcId >= 30598 && npcId <= 30602)
		{
			if (!st.isCompleted())
			{
				if (player.isMageClass())
				{
					playTutorialVoice(player, "tutorial_voice_027");
					giveItems(player, SPIRITSHOT_NO_GRADE_FOR_BEGINNERS, 100);
				}
				else
				{
					playTutorialVoice(player, "tutorial_voice_026");
					giveItems(player, SOULSHOT_NO_GRADE_FOR_BEGINNERS, 200);
				}
				giveItems(player, NEWBIE_TRAVEL_TOKEN, 12);
				
				st.setState(QuestStatus.COMPLETED);
			}
			return npcId + ".htm";
		}
		
		if (npcId == 30008 || npcId == 30017 || npcId == 30129 || npcId == 30370 || npcId == 30528 || npcId == 30573)
		{
			if (st.isCompleted())
				return npcId + "-04.htm";
			
			final int step = st.getInteger("step");
			if (step == 0)
			{
				npc.showChatWindow(player);
				return null;
			}
			
			if (step == 1)
				return npcId + "-01.htm";
			
			if (step == 2)
				return npcId + "-02.htm";
			
			if (step == 3 || qs.getInteger("ucMemo") >= 3)
				return npcId + "-04.htm";
		}
		
		if (npcId == 30009 || npcId == 30019 || npcId == 30131 || npcId == 30400 || npcId == 30530 || npcId == 30575)
		{
			final int level = player.getStatus().getLevel();
			if (level >= 10 || st.getInteger("onlyone") == 1)
				return "newbiehelper_03.htm";
			
			String htmltext = "newbiehelper_fig_01.htm";
			
			final int step = st.getInteger("step");
			if (step == 0)
			{
				qs.set("Ex", 0);
				
				st.set("step", 1);
				st.setState(QuestStatus.STARTED);
				
				startQuestTimer("TimerEx_NewbieHelper", null, player, 30000);
				
				if (player.isMageClass())
					htmltext = (player.getClassId() == ClassId.ORC_MYSTIC) ? "newbiehelper_mage_02.htm" : "newbiehelper_mage_01.htm";
			}
			else if (step == 1 && qs.getInteger("Ex") <= 2)
			{
				if (player.getInventory().hasAtLeastOneItem(BLUE_GEMSTONE))
				{
					qs.set("Ex", 3);
					qs.set("ucMemo", 3);
					
					st.set("step", 2);
					takeItems(player, BLUE_GEMSTONE, -1);
					giveItems(player, getItemId(npcId), 1);
					
					startQuestTimer("TimerEx_NewbieHelper", null, player, 30000);
					
					if (player.isMageClass() && player.getClassId() != ClassId.ORC_MYSTIC)
					{
						htmltext = npcId + ((npcId == 30009 || npcId == 30530) ? "-03.htm" : "-03a.htm");
						
						giveItems(player, SPIRITSHOT_NO_GRADE_FOR_BEGINNERS, 100);
						playTutorialVoice(player, "tutorial_voice_027");
					}
					else
					{
						htmltext = npcId + "-03.htm";
						giveItems(player, SOULSHOT_NO_GRADE_FOR_BEGINNERS, 200);
						playTutorialVoice(player, "tutorial_voice_026");
					}
				}
				else if (player.isMageClass())
					htmltext = (player.getClassId() == ClassId.ORC_MYSTIC) ? "newbiehelper_mage_02a.htm" : "newbiehelper_mage_01a.htm";
				else
					htmltext = "newbiehelper_fig_01a.htm";
			}
			else if (step == 2)
				htmltext = npcId + "-04.htm";
			
			return htmltext;
		}
		return null;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		final QuestState qs = player.getQuestList().getQuestState(QUEST_NAME_TUTORIAL);
		if (qs == null)
			return;
		
		final int ex = qs.getInteger("Ex");
		if (ex <= 1)
		{
			qs.set("Ex", 2);
			showQuestionMark(player, 3);
			playTutorialVoice(player, "tutorial_voice_011");
		}
		
		if (ex <= 2 && qs.getInteger("Gemstone") == 0 && Rnd.get(100) < 25)
		{
			((Monster) npc).dropItem(new IntIntHolder(BLUE_GEMSTONE, 1), player);
			playSound(player, SOUND_TUTORIAL);
		}
	}
	
	private static String getInvalidHtm(Npc npc)
	{
		switch (npc.getTemplate().getRace())
		{
			case HUMAN:
				return "human/guide_human_cnacelot002.htm";
			
			case ELVE:
				return "elf/guide_elf_roios002.htm";
			
			case DARKELVE:
				return "darkelf/guide_delf_frankia002.htm";
			
			case ORC:
				return "orc/guide_orc_tanai002.htm";
			
			case DWARVE:
				return "dwarf/guide_dwarf_gullin002.htm";
		}
		return null;
	}
	
	private static int getItemId(int npcId)
	{
		if (npcId == 30008 || npcId == 30009)
			return RECOMMENDATION_01;
		
		if (npcId == 30017 || npcId == 30019)
			return RECOMMENDATION_02;
		
		if (npcId == 30129 || npcId == 30131)
			return BLOOD_OF_JUNDIN;
		
		if (npcId == 30370 || npcId == 30400)
			return LEAF_OF_MOTHERTREE;
		
		if (npcId == 30528 || npcId == 30530)
			return LICENSE_OF_MINER;
		
		if (npcId == 30573 || npcId == 30575)
			return VOUCHER_OF_FLAME;
		
		return 0;
	}
}