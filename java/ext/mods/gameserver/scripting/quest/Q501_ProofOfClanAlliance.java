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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q501_ProofOfClanAlliance extends Quest
{
	private static final String QUEST_NAME = "Q501_ProofOfClanAlliance";
	
	private static final int POTION_OF_RECOVERY = 3889;
	
	private static final int HERB_OF_HARIT = 3832;
	private static final int HERB_OF_VANOR = 3833;
	private static final int HERB_OF_OEL_MAHUM = 3834;
	private static final int BLOOD_OF_EVA = 3835;
	private static final int SYMBOL_OF_LOYALTY = 3837;
	private static final int VOUCHER_OF_FAITH = 3873;
	private static final int ANTIDOTE_RECIPE_LIST = 3872;
	
	private static final int ALLIANCE_MANIFESTO = 3874;
	
	private static final int SIR_KRISTOF_RODEMAI = 30756;
	private static final int STATUE_OF_OFFERING = 30757;
	private static final int ATHREA = 30758;
	private static final int KALIS = 30759;
	
	private static final int VANOR_SILENOS_SHAMAN = 20685;
	private static final int HARIT_LIZARDMAN_SHAMAN = 20644;
	private static final int OEL_MAHUM_WITCH_DOCTOR = 20576;
	private static final int[] BOXES_OF_ATHREA = new int[]
	{
		27173,
		27174,
		27175,
		27176,
		27177,
	};
	
	private static final Map<Integer, Integer> HERB_DROPLIST = new HashMap<>();
	static
	{
		HERB_DROPLIST.put(VANOR_SILENOS_SHAMAN, HERB_OF_VANOR);
		HERB_DROPLIST.put(HARIT_LIZARDMAN_SHAMAN, HERB_OF_HARIT);
		HERB_DROPLIST.put(OEL_MAHUM_WITCH_DOCTOR, HERB_OF_OEL_MAHUM);
	}
	
	private static final SpawnLocation[] BOXES_OF_ATHREA_SPAWNLIST =
	{
		new SpawnLocation(102273, 103433, -3512, 0),
		new SpawnLocation(102190, 103379, -3524, 0),
		new SpawnLocation(102107, 103325, -3533, 0),
		new SpawnLocation(102024, 103271, -3500, 0),
		new SpawnLocation(102327, 103350, -3511, 0),
		new SpawnLocation(102244, 103296, -3518, 0),
		new SpawnLocation(102161, 103242, -3529, 0),
		new SpawnLocation(102078, 103188, -3500, 0),
		new SpawnLocation(102381, 103267, -3538, 0),
		new SpawnLocation(102298, 103213, -3532, 0),
		new SpawnLocation(102215, 103159, -3520, 0),
		new SpawnLocation(102132, 103105, -3513, 0),
		new SpawnLocation(102435, 103184, -3515, 0),
		new SpawnLocation(102352, 103130, -3522, 0),
		new SpawnLocation(102269, 103076, -3533, 0),
		new SpawnLocation(102186, 103022, -3541, 0)
	};
	
	private final Set<Npc> _boxesOfAthrea = ConcurrentHashMap.newKeySet();
	
	public Q501_ProofOfClanAlliance()
	{
		super(501, "Proof Of Clan Alliance");
		
		setItemsIds(SYMBOL_OF_LOYALTY, HERB_OF_HARIT, HERB_OF_VANOR, HERB_OF_OEL_MAHUM, BLOOD_OF_EVA, VOUCHER_OF_FAITH, ANTIDOTE_RECIPE_LIST);
		
		addQuestStart(SIR_KRISTOF_RODEMAI, KALIS, STATUE_OF_OFFERING, ATHREA);
		addTalkId(SIR_KRISTOF_RODEMAI, KALIS, STATUE_OF_OFFERING, ATHREA);
		
		addDecayed(BOXES_OF_ATHREA);
		addMyDying(BOXES_OF_ATHREA);
		addMyDying(VANOR_SILENOS_SHAMAN, HARIT_LIZARDMAN_SHAMAN, OEL_MAHUM_WITCH_DOCTOR);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30756-07.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30759-03.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("30759-07.htm"))
		{
			st.set("3832", true);
			st.set("3833", true);
			st.set("3834", true);
			st.set("3835", true);
			
			st.setCond(3);
			st.unset("sacrificed");
			takeItems(player, SYMBOL_OF_LOYALTY, -1);
			giveItems(player, ANTIDOTE_RECIPE_LIST, 1);
			playSound(player, SOUND_MIDDLE);
			
			SkillTable.getInstance().getInfo(4082, 1).getEffects(npc, player);
		}
		else if (event.equalsIgnoreCase("30757-03.htm"))
		{
			final QuestState lst = checkClanLeaderCondition(player, null, 2);
			if (lst == null)
				return null;
			
			if (Rnd.get(100) < 40)
			{
				htmltext = "30757-04.htm";
				giveItems(player, SYMBOL_OF_LOYALTY, 1);
				playSound(player, SOUND_ITEMGET);
				
				lst.set("sacrificed", lst.getInteger("sacrificed") + 1);
				lst.set(String.valueOf(player.getObjectId()), true);
			}
			else
			{
				npc.getAI().addCastDesire(player, 4083, 1, 100000);
				startQuestTimer("die", null, player, 4000);
			}
		}
		else if (event.equalsIgnoreCase("30758-03.htm"))
		{
			if (_boxesOfAthrea.isEmpty())
			{
				final QuestState lst = checkClanLeaderVariable(player, null, "3835", "true");
				if (lst == null)
					return null;
				
				lst.set("spawn", true);
				lst.set("boxes", 0);
				
				SpawnLocation loc1;
				SpawnLocation loc2;
				SpawnLocation loc3;
				SpawnLocation loc4;
				
				do
				{
					loc1 = Rnd.get(BOXES_OF_ATHREA_SPAWNLIST);
					loc2 = Rnd.get(BOXES_OF_ATHREA_SPAWNLIST);
					loc3 = Rnd.get(BOXES_OF_ATHREA_SPAWNLIST);
					loc4 = Rnd.get(BOXES_OF_ATHREA_SPAWNLIST);
				}
				while (loc1 == loc2 || loc1 == loc3 || loc1 == loc4 || loc2 == loc3 || loc2 == loc4 || loc3 == loc4);
				
				final int clanId = lst.getPlayer().getClanId();
				for (SpawnLocation loc : BOXES_OF_ATHREA_SPAWNLIST)
				{
					Npc box = addSpawn(Rnd.get(BOXES_OF_ATHREA), loc, false, 300000, false);
					_boxesOfAthrea.add(box);
					
					if (loc == loc1 || loc == loc2 || loc == loc3 || loc == loc4)
						box.setScriptValue(clanId);
				}
			}
			else
				htmltext = "30758-03a.htm";
		}
		else if (event.equalsIgnoreCase("30758-07.htm"))
		{
			if (_boxesOfAthrea.isEmpty())
			{
				if (player.getAdena() < 10000)
					htmltext = "30758-06.htm";
				else
					takeItems(player, 57, 10000);
			}
			else
				htmltext = "30758-03a.htm";
		}
		
		return htmltext;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equals("die") && player.isDead())
		{
			final QuestState lst = checkClanLeaderCondition(player, null, 2);
			if (lst == null)
				return null;
			
			giveItems(player, SYMBOL_OF_LOYALTY, 1);
			playSound(player, SOUND_ITEMGET);
			
			lst.set("sacrificed", lst.getInteger("sacrificed") + 1);
			lst.set(String.valueOf(player.getObjectId()), true);
		}
		
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		final QuestState lst = getClanLeaderQuestState(player, null);
		if (lst == null)
			return htmltext;
		
		switch (lst.getState())
		{
			case CREATED:
				switch (npc.getNpcId())
				{
					case SIR_KRISTOF_RODEMAI:
						if (player.isClanLeader())
						{
							final int level = player.getClan().getLevel();
							if (level < 3)
								htmltext = "30756-01.htm";
							else if (level == 3)
								htmltext = player.getInventory().hasItems(ALLIANCE_MANIFESTO) ? "30756-03.htm" : "30756-04.htm";
							else
								htmltext = "30756-02.htm";
						}
						else
							htmltext = "30756-05.htm";
						break;
				}
				break;
			
			case STARTED:
				final int cond = lst.getCond();
				switch (npc.getNpcId())
				{
					case SIR_KRISTOF_RODEMAI:
						if (player.isClanLeader())
						{
							if (cond == 4)
							{
								rewardExpAndSp(player, 0, 120000);
								takeItems(player, VOUCHER_OF_FAITH, -1);
								giveItems(player, ALLIANCE_MANIFESTO, 1);
								playSound(player, SOUND_FINISH);
								lst.exitQuest(true);
								htmltext = "30756-09.htm";
							}
							else
								htmltext = "30756-10.htm";
						}
						break;
					
					case KALIS:
						if (player.isClanLeader())
						{
							if (cond == 1)
								htmltext = "30759-01.htm";
							else if (cond == 2)
							{
								if (player.getInventory().getItemCount(SYMBOL_OF_LOYALTY) < 3)
									htmltext = "30759-05.htm";
								else
									htmltext = "30759-06.htm";
							}
							else if (cond > 3 && cond < 6 && lst.getInteger("sacrificed") >= 3)
							{
								lst.setCond(1);
								takeItems(player, ANTIDOTE_RECIPE_LIST, -1);
								htmltext = "30759-09.htm";
							}
							else if (cond == 3)
							{
								if (player.getFirstEffect(4082) == null)
								{
									resetKalis(lst);
									htmltext = "30759-09.htm";
								}
								else if (player.getInventory().hasItems(HERB_OF_HARIT, HERB_OF_VANOR, HERB_OF_OEL_MAHUM, BLOOD_OF_EVA))
								{
									lst.setCond(4);
									takeItems(player, ANTIDOTE_RECIPE_LIST, -1);
									takeItems(player, HERB_OF_HARIT, -1);
									takeItems(player, HERB_OF_VANOR, -1);
									takeItems(player, HERB_OF_OEL_MAHUM, -1);
									takeItems(player, BLOOD_OF_EVA, -1);
									giveItems(player, VOUCHER_OF_FAITH, 1);
									giveItems(player, POTION_OF_RECOVERY, 1);
									playSound(player, SOUND_MIDDLE);
									htmltext = "30759-08.htm";
								}
								else
									htmltext = "30759-10.htm";
							}
							else if (cond == 4)
								htmltext = "30759-11.htm";
						}
						else
						{
							if (cond >= 1 && cond <= 3)
								htmltext = "30759-12.htm";
						}
						break;
					
					case STATUE_OF_OFFERING:
						if (player.isClanLeader())
						{
							if (cond == 2)
								htmltext = "30757-01a.htm";
						}
						else
						{
							if (cond != 2)
								htmltext = "30757-06.htm";
							else if (player.getStatus().getLevel() < 40)
								htmltext = "30757-02.htm";
							else if (lst.getBool(String.valueOf(player.getObjectId())))
								htmltext = "30757-01b.htm";
							else
								htmltext = "30757-01.htm";
						}
						break;
					
					case ATHREA:
						if (!player.isClanLeader() && cond == 3)
						{
							if (!lst.getBool("3835"))
								htmltext = "30758-09.htm";
							else if (!lst.getBool("spawn"))
								htmltext = "30758-01.htm";
							else if (lst.getInteger("boxes") < 4)
								htmltext = "30758-05.htm";
							else
							{
								lst.unset("3835");
								lst.unset("boxes");
								lst.unset("spawn");
								
								giveItems(player, BLOOD_OF_EVA, 1);
								playSound(player, SOUND_ITEMGET);
								htmltext = "30758-08.htm";
							}
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onDecayed(Npc npc)
	{
		_boxesOfAthrea.remove(npc);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		if (player == null)
			return;
		
		final int npcId = npc.getNpcId();
		if (HERB_DROPLIST.containsKey(npcId))
		{
			final int herbId = HERB_DROPLIST.get(npcId);
			final String herbFlag = String.valueOf(herbId);
			
			Result result = getRandomPartyMemberClanLeaderQuestState(player, npc, herbFlag, "true");
			if (result == null)
				return;
			
			if (dropItems(result.player, herbId, 1, 1, 100000))
				result.lst.unset(herbFlag);
		}
		else if (ArraysUtil.contains(BOXES_OF_ATHREA, npcId))
		{
			Result result = getRandomPartyMemberClanLeaderQuestState(player, npc, "3835", "true");
			if (result == null)
				return;
			
			final QuestState lst = result.lst;
			
			if (npc.getScriptValue() == lst.getPlayer().getClanId())
			{
				lst.set("boxes", lst.getInteger("boxes") + 1);
				
				npc.broadcastNpcSay(NpcStringId.ID_50110);
			}
		}
	}
	
	/**
	 * Resets Clan leader's {@link QuestState} right before Kalis's sacrifice request (cond 1).
	 * @param lst : Clan leader's {@link QuestState}.
	 */
	private void resetKalis(QuestState lst)
	{
		lst.exitQuest(true);
		
		lst = newQuestState(lst.getPlayer());
		lst.setState(QuestStatus.STARTED);
		lst.setCond(1);
		playSound(lst.getPlayer(), SOUND_MIDDLE);
	}
	
	/**
	 * {@link Player} and his {@link Clan} leader's {@link QuestState}.
	 */
	private static class Result
	{
		private final Player player;
		private final QuestState lst;
		
		private Result(Player member, QuestState clState)
		{
			player = member;
			lst = clState;
		}
	}
	
	/**
	 * @param player : The {@link Player} requesting clan leader's {@link QuestState}.
	 * @param npc : The {@link Npc} used to compare distance to check player/party member, not clan leader.
	 * @param var : A tuple specifying a quest condition that must be satisfied.
	 * @param value : A tuple specifying a quest condition that must be satisfied.
	 * @return The {@link QuestState} of random party member's {@link Clan} leader, if existing and matching conditions - otherwise, null.
	 */
	private Result getRandomPartyMemberClanLeaderQuestState(Player player, Npc npc, String var, String value)
	{
		final Party party = player.getParty();
		if (party == null)
		{
			if (!player.isIn3DRadius(npc, Config.PARTY_RANGE))
				return null;
			
			final QuestState lst = checkClanLeaderVariable(player, null, var, value);
			if (lst == null)
				return null;
			
			return new Result(player, lst);
		}
		
		final List<Result> members = new ArrayList<>();
		for (Player member : party.getMembers())
		{
			if (!member.isIn3DRadius(npc, Config.PARTY_RANGE))
				continue;
			
			final QuestState lst = checkClanLeaderVariable(member, null, var, value);
			if (lst == null)
				continue;
			
			members.add(new Result(member, lst));
		}
		
		return Rnd.get(members);
	}
}