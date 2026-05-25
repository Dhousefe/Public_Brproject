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
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;

public class ThirdClassQuest extends Quest
{
	private final int _classId;
	private final ClassId _prevClassId;
	
	private static final int ICE_CRYSTAL = 7080;
	private static final int DIVINE_STONE_OF_WISDOM = 7081;
	private static final int BOOK_OF_GIANTS = 6622;
	/** Main quest item, identifying the quest. */
	protected int _itemMain = 0;
	/** Optional item (fish) to be delivered with Ice Crystal. */
	protected int _itemOptional = 0;
	/** Reward for delivering Ice Crystal (with optional item). */
	protected int _itemReward = 0;
	/** Resonance Amulet - 1 to resonate with Tablet of Vision. */
	protected int _itemAmulet1st = 0;
	/** Resonance Amulet - 2 to resonate with Tablet of Vision. */
	protected int _itemAmulet2nd = 0;
	/** Resonance Amulet - 3 to resonate with Tablet of Vision. */
	protected int _itemAmulet3rd = 0;
	/** Resonance Amulet - 4 to resonate with Tablet of Vision. */
	protected int _itemAmulet4th = 0;
	/** Halisha's Mark being dropped by monsters in Shrine of Loyalty. */
	protected int _itemHalishaMark = 0;
	/** Resonance Amulet - 5 to resonate with Tablet of Vision. */
	protected int _itemAmulet5th = 0;
	/** Resonance Amulet - 6 to resonate with Tablet of Vision. */
	protected int _itemAmulet6th = 0;
	
	/** Main quest NPC. */
	protected int _npcMain = 0;
	/** The NPC trading reward item for the Resonance Amulet - 1. */
	protected int _npc1st = 0;
	/** The NPC trading Ice Crystal (with optional item) for reward item. */
	protected int _npc2nd = 0;
	/** 1st Tablet of Vision. */
	protected int _npcTablet1st = 0;
	/** 2nd Tablet of Vision. */
	protected int _npcTablet2nd = 0;
	/** 3rd Tablet of Vision. */
	protected int _npcTablet3rd = 0;
	/** The NPC trading Divine Stone of Wisdom for the Resonance Amulet - 4. */
	protected int _npc3rd = 0;
	/** 4th Tablet of Vision. */
	protected int _npcTablet4th = 0;
	/** The NPC guiding to hunt Halisha to obtain the Resonance Amulet - 5. */
	protected int _npc4th = 0;
	/** 5th Tablet of Vision. */
	protected int _npcTablet5th = 0;
	/** 6th Tablet of Vision. */
	protected int _npcTablet6th = 0;
	/** The defending NPC of the combat near 6th Tablet of Vision. */
	protected int _npcDefender = 0;
	
	/** Monsters in Shrine of Loyalty giving Halisha's Mark. */
	private static final int[] SHRINE_OF_LOYALTY =
	{
		21646,
		21647,
		21648,
		21649,
		21650,
		21651
	};
	/** Archons of Halisha spawned in Four Sepulchers giving Resonance Amulet - 5. */
	private static final int[] ARCHON_OF_HALISHA_FOUR_SEPULCHERS =
	{
		18212,
		18213,
		18214,
		18215,
		18216,
		18217,
		18218,
		18219
	};
	/** The Guardian of Forbidden Knowledge monster giving Resonance Amulet - 2. */
	protected int _mobGuardian = 0;
	/** The corrupted monster giving Resonance Amulet - 3. */
	protected int _mobCorrupted = 0;
	/** The Archon of Halisha spawned in Shrine of Loyalty giving Resonance Amulet - 5. */
	protected int _mobHalisha = 0;
	/** The attacking monster of the combat near 6th Tablet of Vision. */
	protected int _mobAttacker = 0;
	
	/** The location to spawn corrupted monster. */
	protected SpawnLocation _locCorrupted = null;
	/** The location to spawn attacking monster of the combat near 6th Tablet of Vision. */
	protected SpawnLocation _locAttacker = null;
	/** The location to spawn defending NPC of the combat near 6th Tablet of Vision. */
	protected SpawnLocation _locDefender = null;
	
	protected NpcStringId _msgCorruptedSpawn = null;
	protected NpcStringId _msgCorruptedDespawn = null;
	protected NpcStringId _msgCorruptedKill = null;
	protected NpcStringId _msgHalishaSpawn = null;
	protected NpcStringId _msgHalishaDespawn = null;
	protected NpcStringId _msgHalishaKill = null;
	protected NpcStringId _msgHalishaKillOther = null;
	protected NpcStringId _msgAttackerSpawn = null;
	protected NpcStringId _msgAttackerDespawn = null;
	protected NpcStringId _msgAttackerAttack1 = null;
	protected NpcStringId _msgAttackerAttack16 = null;
	protected NpcStringId _msgDefenderSpawn = null;
	protected NpcStringId _msgDefenderDespawnWon = null;
	protected NpcStringId _msgDefenderDespawnLost = null;
	protected NpcStringId _msgDefenderCombat = null;
	protected NpcStringId _msgDefenderCombatIdle1 = null;
	protected NpcStringId _msgDefenderCombatIdle2 = null;
	protected NpcStringId _msgDefenderReward = null;
	
	private static final Map<ClassId, ThirdClassQuest> _quests = HashMap.newHashMap(31);
	private static final Map<Npc, Npc> _npcBusy = new ConcurrentHashMap<>();
	private static final Map<Npc, Attackable> _npcSpawns = new ConcurrentHashMap<>();
	
	/**
	 * Implicit constructor for third class quest core. The third class quest core is used for shared functionalities between all third class quests ({@link Q070_SagaOfThePhoenixKnight} up to {@link Q100_SagaOfTheMaestro}).
	 */
	public ThirdClassQuest()
	{
		super(-1, "Third Class Quest");
		
		_classId = 0;
		_prevClassId = null;
		
		addMyDying(SHRINE_OF_LOYALTY);
	}
	
	/**
	 * Superclass constructor, used by particular third class quest ({@link Q070_SagaOfThePhoenixKnight} up to {@link Q100_SagaOfTheMaestro}).
	 * @param id : ID of the third class quest.
	 * @param descr : Name of the third class quest.
	 * @param classId : The target third class of the quest.
	 */
	public ThirdClassQuest(int id, String descr, ClassId classId)
	{
		super(id, descr);
		
		_classId = classId.getId();
		_prevClassId = classId.getParent();
		_quests.put(_prevClassId, this);
		
		setItemsNpcsMobsLocs();
		setItemsIds(_itemMain, _itemReward, _itemAmulet1st, _itemAmulet2nd, _itemAmulet3rd, _itemAmulet4th, _itemHalishaMark, _itemAmulet5th, _itemAmulet6th);
		addQuestStart(_npcMain);
		addFirstTalkId(_npcDefender);
		addTalkId(_npcMain, _npc1st, _npc2nd, _npcTablet1st, _npcTablet2nd, _npcTablet3rd, _npc3rd, _npcTablet4th, _npc4th, _npcTablet5th, _npcDefender, _npcTablet6th);
		
		addAttacked(_mobCorrupted, _mobAttacker, _npcDefender);
		addDecayed(_mobCorrupted, _mobHalisha, _npcDefender, _mobAttacker);
		addMyDying(_mobGuardian, _mobCorrupted, _mobHalisha, _mobAttacker);
		addMyDying(ARCHON_OF_HALISHA_FOUR_SEPULCHERS);
		addSeeSpell(_mobCorrupted);
	}
	
	/**
	 * Set third class quest specific items, NPCs, monsters and spawn locations.
	 */
	protected void setItemsNpcsMobsLocs()
	{
	}
	
	private static void cast(Npc npc, Creature target, int skillId, int level)
	{
		target.broadcastPacket(new MagicSkillUse(target, target, skillId, level, 6000, 1));
		target.broadcastPacket(new MagicSkillUse(npc, npc, skillId, level, 6000, 1));
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(getName());
		if (st == null)
			return null;
		
		if (event.equalsIgnoreCase("0-1"))
		{
			if (player.getStatus().getLevel() < 76)
				htmltext = "0-02.htm";
			else
				htmltext = "0-05.htm";
		}
		else if (event.equalsIgnoreCase("accept"))
		{
			htmltext = "0-03.htm";
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, _itemMain, 1);
		}
		else if (event.equalsIgnoreCase("0-2"))
		{
			if (player.getStatus().getLevel() >= 76)
			{
				htmltext = "0-07.htm";
				giveItems(player, 57, 5000000);
				giveItems(player, BOOK_OF_GIANTS, 1);
				rewardExpAndSp(player, 2299404, 0);
				playSound(player, SOUND_FINISH);
				st.exitQuest(false);
				
				player.setClassId(_classId);
				if (!player.isSubClassActive() && player.getBaseClass() == _prevClassId.getId())
					player.setBaseClass(_classId);
				player.broadcastUserInfo();
				
				cast(npc, player, 4339, 1);
			}
			else
			{
				htmltext = "0-08.htm";
				st.setCond(20);
				playSound(player, SOUND_MIDDLE);
				takeItems(player, _itemMain, -1);
			}
		}
		else if (event.equalsIgnoreCase("2-1"))
		{
			htmltext = "2-05.htm";
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("2-2"))
		{
			htmltext = "2-06.htm";
			st.setCond(5);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, _itemReward, 1);
			giveItems(player, _itemAmulet1st, 1);
		}
		else if (event.equalsIgnoreCase("1-3"))
		{
			htmltext = "1-05.htm";
			st.setCond(3);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("1-4"))
		{
			htmltext = "1-06.htm";
			st.setCond(4);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, ICE_CRYSTAL, 1);
			if (_itemOptional != 0)
				takeItems(player, _itemOptional, 1);
			giveItems(player, _itemReward, 1);
		}
		else if (event.equalsIgnoreCase("5-1"))
		{
			htmltext = "5-02.htm";
			st.setCond(6);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, _itemAmulet1st, 1);
			
			cast(npc, player, 4546, 1);
		}
		else if (event.equalsIgnoreCase("6-1"))
		{
			htmltext = "6-03.htm";
			st.setCond(8);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, _itemAmulet2nd, 1);
			
			cast(npc, player, 4546, 1);
		}
		else if (event.equalsIgnoreCase("7-1"))
		{
			Npc corrupted = _npcBusy.get(npc);
			if (corrupted == null)
			{
				
				corrupted = createOnePrivateEx(npc, _mobCorrupted, _locCorrupted.getX(), _locCorrupted.getY(), _locCorrupted.getZ(), _locCorrupted.getHeading(), 0, true, player.getObjectId(), player.getObjectId(), npc.getObjectId());
				corrupted.setScriptValue(player.getObjectId());
				
				_npcBusy.put(npc, corrupted);
				startQuestTimer("corrupted", corrupted, player, 500);
				htmltext = "7-02.htm";
			}
			else
			{
				
				if (corrupted.getScriptValue() == player.getObjectId())
					htmltext = "7-03.htm";
				else
					htmltext = "7-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("7-2"))
		{
			htmltext = "7-06.htm";
			st.setCond(10);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, _itemAmulet3rd, 1);
			
			cast(npc, player, 4546, 1);
		}
		else if (event.equalsIgnoreCase("3-5"))
		{
			htmltext = "3-07.htm";
		}
		else if (event.equalsIgnoreCase("3-6"))
		{
			htmltext = "3-02.htm";
			st.setCond(11);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("3-7"))
		{
			htmltext = "3-03.htm";
			st.setCond(12);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("3-8"))
		{
			htmltext = "3-08.htm";
			st.setCond(13);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, DIVINE_STONE_OF_WISDOM, 1);
			giveItems(player, _itemAmulet4th, 1);
		}
		else if (event.equalsIgnoreCase("8-1"))
		{
			htmltext = "8-02.htm";
			st.setCond(14);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, _itemAmulet4th, 1);
			
			cast(npc, player, 4546, 1);
		}
		else if (event.equalsIgnoreCase("11-9"))
		{
			htmltext = "11-03.htm";
			st.setCond(15);
			playSound(player, SOUND_MIDDLE);
		}
		else if (event.equalsIgnoreCase("9-1"))
		{
			htmltext = "9-03.htm";
			st.setCond(17);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, _itemAmulet5th, 1);
			
			cast(npc, player, 4546, 1);
		}
		else if (event.equalsIgnoreCase("10-1"))
		{
			Npc defender = _npcBusy.get(npc);
			if (defender == null)
			{
				
				st.unset("attacks");
				
				defender = addSpawn(_npcDefender, _locDefender, false, 60000, true);
				defender.setScriptValue(player.getObjectId());
				
				Attackable attacker = (Attackable) addSpawn(_mobAttacker, _locAttacker, false, 59000, true);
				attacker.setScriptValue(player.getObjectId());
				attacker.getAI().addAttackDesire(defender, 200);
				
				_npcBusy.put(npc, defender);
				_npcSpawns.put(defender, attacker);
				
				startQuestTimer("defender", defender, player, 500);
				startQuestTimer("attacker", attacker, player, 500);
				
				htmltext = "10-02.htm";
			}
			else
			{
				
				if (defender.getScriptValue() == player.getObjectId())
					htmltext = "10-03.htm";
				else
					htmltext = "10-04.htm";
			}
		}
		else if (event.equalsIgnoreCase("10-2"))
		{
			htmltext = "10-06.htm";
			st.setCond(19);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, _itemAmulet6th, 1);
			
			cast(npc, player, 4546, 1);
		}
		else if (event.equalsIgnoreCase("4-1"))
		{
			htmltext = "4-010.htm";
		}
		else if (event.equalsIgnoreCase("4-2"))
		{
			htmltext = "4-011.htm";
			st.unset("attacks");
			st.setCond(18);
			playSound(player, SOUND_MIDDLE);
			giveItems(player, _itemAmulet6th, 1);
		}
		else if (event.equalsIgnoreCase("4-3"))
		{
			htmltext = null;
			st.unset("attacks");
			st.setCond(18);
			playSound(player, SOUND_MIDDLE);
			giveItems(player, _itemAmulet6th, 1);
			
			npc.broadcastNpcSay(_msgDefenderReward);
			cancelQuestTimers("defender2", npc);
			cancelQuestTimers("defender3", npc);
			npc.deleteMe();
		}
		
		return htmltext;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("corrupted"))
		{
			npc.broadcastNpcSay(_msgCorruptedSpawn, player.getName());
			return null;
		}
		
		if (name.equalsIgnoreCase("defender"))
		{
			npc.broadcastNpcSay(_msgDefenderSpawn);
			startQuestTimer("defender2", npc, player, 1500);
			return null;
		}
		else if (name.equalsIgnoreCase("defender2"))
		{
			npc.broadcastNpcSay(_msgDefenderCombat, player.getName());
			startQuestTimer("defender3", npc, player, 10000);
			return null;
		}
		else if (name.equalsIgnoreCase("defender3"))
		{
			QuestState st = checkPlayerCondition(player, npc, 17);
			if (st == null)
				return null;
			
			if (_npcSpawns.containsKey(npc))
			{
				npc.broadcastNpcSay(Rnd.nextBoolean() ? _msgDefenderCombatIdle1 : _msgDefenderCombatIdle2, player.getName());
				startQuestTimer("defender3", npc, player, 10000);
			}
			return null;
		}
		
		if (name.equalsIgnoreCase("attacker"))
		{
			npc.broadcastNpcSay(_msgAttackerSpawn, player.getName());
			return null;
		}
		
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(getName());
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (npc.getNpcId() == _npcMain)
				{
					if (player.getClassId() == _prevClassId)
						htmltext = "0-01.htm";
					else
						htmltext = "0-02.htm";
				}
				break;
			
			case STARTED:
				if (player.getClassId() != _prevClassId)
					return htmltext;
				
				final int npcId = npc.getNpcId();
				switch (st.getCond())
				{
					case 1:
						if (npcId == _npcMain)
							htmltext = "0-04.htm";
						else if (npcId == _npc1st)
							htmltext = "2-01.htm";
						break;
					
					case 2:
						if (npcId == _npc1st)
							htmltext = "2-02.htm";
						else if (npcId == _npc2nd)
							htmltext = "1-01.htm";
						break;
					
					case 3:
						if (npcId == _npc1st)
							htmltext = "2-02.htm";
						else if (npcId == _npc2nd)
						{
							if (player.getInventory().hasItems(ICE_CRYSTAL) && (_itemOptional == 0 || player.getInventory().hasItems(_itemOptional)))
								htmltext = "1-03.htm";
							else
								htmltext = "1-02.htm";
						}
						break;
					
					case 4:
						if (npcId == _npc2nd)
							htmltext = "1-04.htm";
						else if (npcId == _npc1st)
							htmltext = "2-03.htm";
						break;
					
					case 5:
						if (npcId == _npc1st)
							htmltext = "2-04.htm";
						else if (npcId == _npcTablet1st)
							htmltext = "5-01.htm";
						break;
					
					case 6:
						if (npcId == _npcTablet1st)
							htmltext = "5-03.htm";
						else if (npcId == _npcTablet2nd)
							htmltext = "6-01.htm";
						break;
					
					case 7:
						if (npcId == _npcTablet2nd)
							htmltext = "6-02.htm";
						break;
					
					case 8:
						if (npcId == _npcTablet2nd)
							htmltext = "6-04.htm";
						else if (npcId == _npcTablet3rd)
							htmltext = "7-01.htm";
						break;
					
					case 9:
						if (npcId == _npcTablet3rd)
							htmltext = "7-05.htm";
						break;
					
					case 10:
						if (npcId == _npcTablet3rd)
							htmltext = "7-07.htm";
						else if (npcId == _npc3rd)
							htmltext = "3-01.htm";
						break;
					
					case 11, 12:
						if (npcId == _npc3rd)
						{
							if (player.getInventory().hasItems(DIVINE_STONE_OF_WISDOM))
								htmltext = "3-05.htm";
							else
								htmltext = "3-04.htm";
						}
						break;
					
					case 13:
						if (npcId == _npc3rd)
							htmltext = "3-06.htm";
						else if (npcId == _npcTablet4th)
							htmltext = "8-01.htm";
						break;
					
					case 14:
						if (npcId == _npcTablet4th)
							htmltext = "8-03.htm";
						else if (npcId == _npc4th)
							htmltext = "11-01.htm";
						break;
					
					case 15:
						if (npcId == _npc4th)
							htmltext = "11-02.htm";
						else if (npcId == _npcTablet5th)
							htmltext = "9-01.htm";
						break;
					
					case 16:
						if (npcId == _npcTablet5th)
							htmltext = "9-02.htm";
						break;
					
					case 17:
						if (npcId == _npcTablet5th)
							htmltext = "9-04.htm";
						else if (npcId == _npcTablet6th)
							htmltext = "10-01.htm";
						break;
					
					case 18:
						if (npcId == _npcTablet6th)
							htmltext = "10-05.htm";
						break;
					
					case 19:
						if (npcId == _npcTablet6th)
							htmltext = "10-07.htm";
						else if (npcId == _npcMain)
							htmltext = "0-06.htm";
						break;
					
					case 20:
						if (npcId == _npcMain)
						{
							if (player.getStatus().getLevel() >= 76)
							{
								htmltext = "0-09.htm";
								giveItems(player, 57, 5000000);
								giveItems(player, BOOK_OF_GIANTS, 1);
								rewardExpAndSp(player, 2299404, 0);
								playSound(player, SOUND_FINISH);
								st.exitQuest(false);
								
								player.setClassId(_classId);
								if (!player.isSubClassActive() && player.getBaseClass() == _prevClassId.getId())
									player.setBaseClass(_classId);
								player.broadcastUserInfo();
								
								cast(npc, player, 4339, 1);
							}
							else
								htmltext = "0-010.htm";
						}
						break;
				}
				break;
			
			case COMPLETED:
				if (npc.getNpcId() == _npcMain)
					htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		
		QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return htmltext;
		
		int cond = st.getCond();
		if (cond == 17)
		{
			if (_npcSpawns.containsKey(npc))
			{
				if (npc.getScriptValue() == player.getObjectId())
				{
					if (st.getInteger("attacks") < 16)
						htmltext = "4-01.htm";
					else
						htmltext = "4-04.htm";
				}
				else
				{
					if (st.getInteger("attacks") < 16)
						htmltext = "4-02.htm";
					else
						htmltext = "4-05.htm";
				}
			}
			else
			{
				
				if (st.getInteger("attacks") < 16)
					htmltext = "4-03.htm";
				else if (npc.getScriptValue() == player.getObjectId())
					htmltext = "4-06.htm";
				else
					htmltext = "4-07.htm";
			}
		}
		else if (cond == 18)
		{
			if (_npcSpawns.containsKey(npc))
				htmltext = "4-08.htm";
			else
				htmltext = "4-09.htm";
		}
		
		player.getQuestList().setLastQuestNpcObjectId(npc.getObjectId());
		return htmltext;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Player player = attacker.getActingPlayer();
		if (player == null)
			return;
		
		final int npcId = npc.getNpcId();
		
		if (npcId == _mobCorrupted)
		{
			if (player.getObjectId() != npc.getScriptValue() || player.getClassId() != _prevClassId)
			{
				npc.setScriptValue(0);
				npc.deleteMe();
			}
		}
		else if (npcId == _mobAttacker)
		{
			if (player.getObjectId() != npc.getScriptValue())
				return;
			
			if (player.getClassId() != _prevClassId)
				return;
			
			QuestState st = checkPlayerCondition(player, npc, 17);
			if (st == null)
				return;
			
			int attacks = st.getInteger("attacks");
			attacks++;
			st.set("attacks", attacks);
			
			if (attacks == 1)
			{
				npc.broadcastNpcSay(_msgAttackerAttack1, player.getName());
			}
			else if (attacks == 16)
			{
				npc.broadcastNpcSay(_msgAttackerAttack16, player.getName());
				
				npc.setScriptValue(0);
				npc.deleteMe();
			}
		}
		else if (npcId == _npcDefender && !(attacker instanceof Playable))
			npc.getAI().addAttackDesire(attacker, 2000);
	}
	
	@Override
	public void onDecayed(Npc npc)
	{
		final int npcId = npc.getNpcId();
		
		if (npcId == _mobCorrupted)
		{
			_npcBusy.values().remove(npc);
			
			if (!npc.isDead() && npc.getScriptValue() > 0)
				npc.broadcastNpcSay(_msgCorruptedDespawn);
		}
		else if (npcId == _mobHalisha)
		{
			if (!npc.isDead())
				npc.broadcastNpcSay(_msgHalishaDespawn);
		}
		else if (npcId == _mobAttacker)
		{
			_npcSpawns.values().remove(npc);
			
			if (!npc.isDead() && npc.getScriptValue() > 0)
				npc.broadcastNpcSay(_msgAttackerDespawn);
		}
		else if (npcId == _npcDefender)
		{
			_npcBusy.values().remove(npc);
			_npcSpawns.remove(npc);
			
			Player p = World.getInstance().getPlayer(npc.getScriptValue());
			if (p == null)
			{
				npc.broadcastNpcSay(_msgDefenderDespawnLost);
				return;
			}
			
			QuestState st = checkPlayerCondition(p, npc, 17);
			if (st != null)
			{
				if (st.getInteger("attacks") > 15)
					npc.broadcastNpcSay(_msgDefenderDespawnWon);
				else
					npc.broadcastNpcSay(_msgDefenderDespawnLost);
			}
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		if (player == null)
			return;
		
		final int npcId = npc.getNpcId();
		
		if (npcId == _mobGuardian)
		{
			if (player.getClassId() != _prevClassId)
				return;
			
			QuestState st = checkPlayerCondition(player, npc, 6);
			if (st == null)
				return;
			
			int kills = st.getInteger("kills") + 1;
			if (kills < 10)
				st.set("kills", kills);
			else
			{
				st.unset("kills");
				st.setCond(7);
				playSound(player, SOUND_MIDDLE);
				giveItems(player, _itemAmulet2nd, 1);
			}
			return;
		}
		
		if (npcId == _mobCorrupted)
		{
			if (player.getObjectId() != npc.getScriptValue())
				return;
			
			if (player.getClassId() != _prevClassId)
				return;
			
			QuestState st = checkPlayerCondition(player, npc, 8);
			if (st == null)
				return;
			
			st.setCond(9);
			playSound(player, SOUND_MIDDLE);
			giveItems(player, _itemAmulet3rd, 1);
			
			npc.broadcastNpcSay(_msgCorruptedKill);
			return;
		}
		
		if (ArraysUtil.contains(SHRINE_OF_LOYALTY, npcId))
		{
			ThirdClassQuest tcq;
			QuestState st;
			
			if (player.isInParty())
			{
				List<QuestState> valid = new ArrayList<>();
				for (Player pm : player.getParty().getMembers())
				{
					tcq = _quests.get(pm.getClassId());
					if (tcq == null)
						continue;
					
					st = tcq.checkPlayerCondition(pm, npc, 15);
					if (st == null)
						continue;
					
					valid.add(st);
				}
				
				st = Rnd.get(valid);
				if (st == null)
					return;
				
				tcq = (ThirdClassQuest) st.getQuest();
			}
			else
			{
				tcq = _quests.get(player.getClassId());
				if (tcq == null)
					return;
				
				st = tcq.checkPlayerCondition(player, npc, 15);
				if (st == null)
					return;
			}
			
			Player p = st.getPlayer();
			if (p.getInventory().getItemCount(tcq._itemHalishaMark) < 700)
			{
				dropItemsAlways(p, tcq._itemHalishaMark, 1, 700);
			}
			else
			{
				takeItems(p, tcq._itemHalishaMark, 20);
				
				Attackable archon = (Attackable) addSpawn(tcq._mobHalisha, npc, false, 600000, true);
				archon.setScriptValue(p.getObjectId());
				
				archon.getAI().addAttackDesire(p, 200);
				
				archon.broadcastNpcSay(tcq._msgHalishaSpawn, p.getName());
			}
			return;
		}
		
		if (npcId == _mobHalisha)
		{
			if (player.getObjectId() != npc.getScriptValue())
			{
				npc.broadcastNpcSay(_msgHalishaKillOther);
				return;
			}
			
			if (player.getClassId() != _prevClassId)
				return;
			
			QuestState st = checkPlayerCondition(player, npc, 15);
			if (st == null)
				return;
			
			st.setCond(16);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, _itemHalishaMark, -1);
			giveItems(player, _itemAmulet5th, 1);
			
			npc.broadcastNpcSay(_msgHalishaKill);
			return;
		}
		
		if (ArraysUtil.contains(ARCHON_OF_HALISHA_FOUR_SEPULCHERS, npcId))
		{
			if (player.isInParty())
			{
				for (Player pm : player.getParty().getMembers())
				{
					if (pm.getClassId() != _prevClassId)
						continue;
					
					QuestState st = checkPlayerCondition(pm, npc, 15);
					if (st == null)
						continue;
					
					st.setCond(16);
					playSound(pm, SOUND_MIDDLE);
					takeItems(pm, _itemHalishaMark, -1);
					giveItems(pm, _itemAmulet5th, 1);
				}
			}
			else
			{
				if (player.getClassId() != _prevClassId)
					return;
				
				QuestState st = checkPlayerCondition(player, npc, 15);
				if (st == null)
					return;
				
				st.setCond(16);
				playSound(player, SOUND_MIDDLE);
				takeItems(player, _itemHalishaMark, -1);
				giveItems(player, _itemAmulet5th, 1);
			}
			return;
		}
		
		if (npcId == _mobAttacker)
			npc.setScriptValue(0);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player player, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (player == null || !ArraysUtil.contains(targets, npc))
			return;
		
		if (_mobCorrupted == npc.getNpcId() && (player.getObjectId() != npc.getScriptValue() || player.getClassId() != _prevClassId))
		{
			npc.setScriptValue(0);
			npc.deleteMe();
		}
	}
}