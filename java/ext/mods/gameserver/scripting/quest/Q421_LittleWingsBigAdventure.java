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

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.itemcontainer.Inventory;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;

/**
 * The script uses internal iCond variable, used because cond isn't developed on that quest (only 3 states):
 * <ul>
 * <li>1-3: Initial Mimyu behavior.</li>
 * <li>4-63: Leave acquisition (tree flags: 4, 8, 16, 32 = 60 overall).</li>
 * <li>100: Mimyu acknowledge conditions are fulfilled.</li>
 * </ul>
 */
public class Q421_LittleWingsBigAdventure extends Quest
{
	private static final String QUEST_NAME = "Q421_LittleWingsBigAdventure";
	
	private static final int DRAGONFLUTE_OF_WIND = 3500;
	private static final int DRAGONFLUTE_OF_STAR = 3501;
	private static final int DRAGONFLUTE_OF_TWILIGHT = 3502;
	private static final int FAIRY_LEAF = 4325;
	
	private static final int CRONOS = 30610;
	private static final int MIMYU = 30747;
	
	private static final int FAIRY_TREE_OF_WIND = 27185;
	private static final int FAIRY_TREE_OF_STAR = 27186;
	private static final int FAIRY_TREE_OF_TWILIGHT = 27187;
	private static final int FAIRY_TREE_OF_ABYSS = 27188;
	private static final int SOUL_OF_TREE_GUARDIAN = 27189;
	
	private static final NpcStringId[] GUARDIAN_MESSAGES = new NpcStringId[]
	{
		NpcStringId.ID_42118,
		NpcStringId.ID_42119,
		NpcStringId.ID_42120,
	};
	
	private static final Map<Integer, TreeData> TREES_DATA = HashMap.newHashMap(4);
	
	public Q421_LittleWingsBigAdventure()
	{
		super(421, "Little Wing's Big Adventure");
		
		TREES_DATA.put(FAIRY_TREE_OF_WIND, new TreeData(4, 270, 3, NpcStringId.ID_42112, NpcStringId.ID_42114, NpcStringId.ID_42113));
		TREES_DATA.put(FAIRY_TREE_OF_STAR, new TreeData(8, 400, 2, NpcStringId.ID_42112, NpcStringId.ID_42114, NpcStringId.ID_42115));
		TREES_DATA.put(FAIRY_TREE_OF_TWILIGHT, new TreeData(16, 150, 2, NpcStringId.ID_42112, NpcStringId.ID_42114, NpcStringId.ID_42116));
		TREES_DATA.put(FAIRY_TREE_OF_ABYSS, new TreeData(32, 270, 2, NpcStringId.ID_42112, NpcStringId.ID_42114, NpcStringId.ID_42117));
		
		setItemsIds(FAIRY_LEAF);
		
		addQuestStart(CRONOS);
		addTalkId(CRONOS, MIMYU);
		
		addAttacked(FAIRY_TREE_OF_WIND, FAIRY_TREE_OF_STAR, FAIRY_TREE_OF_TWILIGHT, FAIRY_TREE_OF_ABYSS);
		addCreated(FAIRY_TREE_OF_WIND, FAIRY_TREE_OF_STAR, FAIRY_TREE_OF_TWILIGHT, FAIRY_TREE_OF_ABYSS, SOUL_OF_TREE_GUARDIAN);
		addMyDying(FAIRY_TREE_OF_WIND, FAIRY_TREE_OF_STAR, FAIRY_TREE_OF_TWILIGHT, FAIRY_TREE_OF_ABYSS);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30610-06.htm"))
		{
			if (getDragonfluteCount(player) == 1)
			{
				for (int i = DRAGONFLUTE_OF_WIND; i <= DRAGONFLUTE_OF_TWILIGHT; i++)
				{
					final ItemInstance item = player.getInventory().getItemByItemId(i);
					if (item != null && item.getEnchantLevel() >= 55)
					{
						st.setState(QuestStatus.STARTED);
						st.setCond(1);
						st.set("iCond", 1);
						st.set("summonOid", item.getObjectId());
						playSound(player, SOUND_ACCEPT);
						return "30610-05.htm";
					}
				}
			}
		}
		else if (event.equalsIgnoreCase("30747-02.htm"))
		{
			final Summon summon = player.getSummon();
			if (summon != null)
				htmltext = (summon.getControlItemId() == st.getInteger("summonOid")) ? "30747-04.htm" : "30747-03.htm";
		}
		else if (event.equalsIgnoreCase("30747-05.htm"))
		{
			final Summon summon = player.getSummon();
			if (summon == null || summon.getControlItemId() != st.getInteger("summonOid"))
				htmltext = "30747-06.htm";
			else
			{
				st.setCond(2);
				st.set("iCond", 3);
				playSound(player, SOUND_MIDDLE);
				giveItems(player, FAIRY_LEAF, 4);
			}
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
				if (player.getStatus().getLevel() < 45)
					htmltext = "30610-01.htm";
				else if (getDragonfluteCount(player) != 1)
					htmltext = "30610-02.htm";
				else
				{
					for (int i = DRAGONFLUTE_OF_WIND; i <= DRAGONFLUTE_OF_TWILIGHT; i++)
					{
						final ItemInstance item = player.getInventory().getItemByItemId(i);
						if (item != null && item.getEnchantLevel() >= 55)
							return "30610-04.htm";
					}
					
					htmltext = "30610-03.htm";
				}
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case CRONOS:
						htmltext = "30610-07.htm";
						break;
					
					case MIMYU:
						final int id = st.getInteger("iCond");
						if (id == 1)
						{
							htmltext = "30747-01.htm";
							st.set("iCond", 2);
						}
						else if (id == 2)
						{
							final Summon summon = player.getSummon();
							if (summon == null)
								htmltext = "30747-02.htm";
							else if (summon.getControlItemId() != st.getInteger("summonOid"))
								htmltext = "30747-03.htm";
							else
								htmltext = "30747-04.htm";
						}
						else if (id == 3)
							htmltext = "30747-07.htm";
						else if (id > 3 && id < 63)
							htmltext = "30747-11.htm";
						else if (id == 63)
						{
							final Summon summon = player.getSummon();
							if (summon == null)
								return "30747-12.htm";
							
							if (summon.getControlItemId() != st.getInteger("summonOid"))
								return "30747-14.htm";
							
							htmltext = "30747-13.htm";
							st.set("iCond", 100);
						}
						else if (id == 100)
						{
							final Summon summon = player.getSummon();
							if (summon != null && summon.getControlItemId() == st.getInteger("summonOid"))
								return "30747-15.htm";
							
							if (getDragonfluteCount(player) > 1)
								return "30747-17.htm";
							
							for (int i = DRAGONFLUTE_OF_WIND; i <= DRAGONFLUTE_OF_TWILIGHT; i++)
							{
								final ItemInstance item = player.getInventory().getItemByItemId(i);
								if (item != null && item.getObjectId() == st.getInteger("summonOid"))
								{
									takeItems(player, i, 1);
									giveItems(player, i + 922, 1, item.getEnchantLevel());
									playSound(player, SOUND_FINISH);
									st.exitQuest(true);
									return "30747-16.htm";
								}
							}
							
							htmltext = "30747-18.htm";
							npc.getAI().addCastDesire(player, 4167, 1, 1000000);
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final Player player = attacker.getActingPlayer();
		if (player == null)
			return;
		
		final QuestState st = checkPlayerCondition(player, npc, 2);
		if (st == null)
		{
			if (npc.getStatus().getHpRatio() < 0.67 && Rnd.get(100) < 30)
				npc.getAI().addCastDesire(attacker, 4243, 1, 1000000);
		}
		else if (attacker instanceof Pet attackerPet)
		{
			final int npcId = npc.getNpcId();
			final TreeData td = TREES_DATA.get(npcId);
			final int condition = st.getInteger("iCond");
			final int mask = td._mask;
			
			if ((mask & condition) == 0)
			{
				if (attackerPet.getControlItemId() == st.getInteger("summonOid"))
				{
					int attack = st.getInteger("attack") + 1;
					if (attack > td._attacks)
					{
						if (Rnd.get(100) < td._chance && player.getInventory().hasItems(FAIRY_LEAF))
						{
							st.set("iCond", condition | mask);
							st.set("attack", 0);
							
							npc.broadcastNpcSay(NpcStringId.ID_42111);
							takeItems(player, FAIRY_LEAF, 1);
							
							if (st.getInteger("iCond") == 63)
							{
								st.setCond(3);
								playSound(player, SOUND_MIDDLE);
							}
							else
								playSound(player, SOUND_ITEMGET);
						}
					}
					else
					{
						st.set("attack", attack);
						
						if (npcId == FAIRY_TREE_OF_ABYSS && Rnd.get(100) < 2)
							npc.getAI().addCastDesire(attackerPet, 1201, 33, 1000000);
					}
				}
			}
			else
			{
				npc.broadcastNpcSay(Rnd.get(td._messages));
			}
		}
		else
		{
			if (Rnd.get(100) < 30)
				npc.getAI().addCastDesire(attacker, 4243, 1, 1000000);
		}
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (npc.getNpcId() == SOUL_OF_TREE_GUARDIAN)
			npc.broadcastNpcSay(Rnd.get(GUARDIAN_MESSAGES));
		else
		{
			npc.disableCoreAi(true);
			npc.setShowSummonAnimation(true);
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		for (int i = 0; i < 20; i++)
		{
			final Npc ghost = createOnePrivate(npc, SOUL_OF_TREE_GUARDIAN, 300000, true);
			if (ghost != null)
			{
				if (i == 0)
					ghost.getAI().addCastDesire(killer, 4243, 1, 1000000);
				
				ghost.getAI().addAttackDesire(killer, 2000);
			}
		}
	}
	
	private static int getDragonfluteCount(Player player)
	{
		final Inventory i = player.getInventory();
		return i.getItemCount(DRAGONFLUTE_OF_WIND) + i.getItemCount(DRAGONFLUTE_OF_STAR) + i.getItemCount(DRAGONFLUTE_OF_TWILIGHT);
	}
	
	/**
	 * Supporting class containing data of a tree.
	 */
	private class TreeData
	{
		private final int _mask;
		private final int _attacks;
		private final int _chance;
		private final NpcStringId[] _messages;
		
		private TreeData(int mask, int attacks, int chance, NpcStringId... messages)
		{
			_mask = mask;
			_attacks = attacks;
			_chance = chance;
			_messages = messages;
		}
	}
}