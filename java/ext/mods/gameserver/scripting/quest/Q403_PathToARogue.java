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

import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;

public class Q403_PathToARogue extends Quest
{
	private static final String QUEST_NAME = "Q403_PathToARogue";
	
	private static final int BEZIQUE_LETTER = 1180;
	private static final int NETI_BOW = 1181;
	private static final int NETI_DAGGER = 1182;
	private static final int SPARTOI_BONES = 1183;
	private static final int HORSESHOE_OF_LIGHT = 1184;
	private static final int MOST_WANTED_LIST = 1185;
	private static final int STOLEN_JEWELRY = 1186;
	private static final int STOLEN_TOMES = 1187;
	private static final int STOLEN_RING = 1188;
	private static final int STOLEN_NECKLACE = 1189;
	private static final int BEZIQUE_RECOMMENDATION = 1190;
	
	private static final int BEZIQUE = 30379;
	private static final int NETI = 30425;
	
	private static final int TRACKER_SKELETON = 20035;
	private static final int TRACKER_SKELETON_LEADER = 20042;
	private static final int SCOUT_SKELETON = 20045;
	private static final int SNIPER_SKELETON = 20051;
	private static final int RUIN_SPARTOI = 20054;
	private static final int RAGING_SPARTOI = 20060;
	private static final int CATSEYE_BANDIT = 27038;
	
	public Q403_PathToARogue()
	{
		super(403, "Path to a Rogue");
		
		setItemsIds(BEZIQUE_LETTER, NETI_BOW, NETI_DAGGER, SPARTOI_BONES, HORSESHOE_OF_LIGHT, MOST_WANTED_LIST, STOLEN_JEWELRY, STOLEN_TOMES, STOLEN_RING, STOLEN_NECKLACE);
		
		addQuestStart(BEZIQUE);
		addTalkId(BEZIQUE, NETI);
		
		addAttacked(TRACKER_SKELETON, TRACKER_SKELETON_LEADER, SCOUT_SKELETON, SNIPER_SKELETON, RUIN_SPARTOI, RAGING_SPARTOI, CATSEYE_BANDIT);
		addMyDying(TRACKER_SKELETON, TRACKER_SKELETON_LEADER, SCOUT_SKELETON, SNIPER_SKELETON, RUIN_SPARTOI, RAGING_SPARTOI, CATSEYE_BANDIT);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30379-05.htm"))
		{
			if (player.getClassId() != ClassId.HUMAN_FIGHTER)
				htmltext = (player.getClassId() == ClassId.ROGUE) ? "30379-02a.htm" : "30379-02.htm";
			else if (player.getStatus().getLevel() < 19)
				htmltext = "30379-02.htm";
			else if (player.getInventory().hasItems(BEZIQUE_RECOMMENDATION))
				htmltext = "30379-04.htm";
		}
		else if (event.equalsIgnoreCase("30379-06.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, BEZIQUE_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("30425-05.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, BEZIQUE_LETTER, 1);
			giveItems(player, NETI_BOW, 1);
			giveItems(player, NETI_DAGGER, 1);
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
				htmltext = "30379-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case BEZIQUE:
						if (cond == 1)
							htmltext = "30379-07.htm";
						else if (cond == 2 || cond == 3)
							htmltext = "30379-10.htm";
						else if (cond == 4)
						{
							htmltext = "30379-08.htm";
							st.setCond(5);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, HORSESHOE_OF_LIGHT, 1);
							giveItems(player, MOST_WANTED_LIST, 1);
						}
						else if (cond == 5)
							htmltext = "30379-11.htm";
						else if (cond == 6)
						{
							htmltext = "30379-09.htm";
							takeItems(player, NETI_BOW, 1);
							takeItems(player, NETI_DAGGER, 1);
							takeItems(player, STOLEN_JEWELRY, 1);
							takeItems(player, STOLEN_NECKLACE, 1);
							takeItems(player, STOLEN_RING, 1);
							takeItems(player, STOLEN_TOMES, 1);
							giveItems(player, BEZIQUE_RECOMMENDATION, 1);
							rewardExpAndSp(player, 3200, 1500);
							player.broadcastPacket(new SocialAction(player, 3));
							playSound(player, SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case NETI:
						if (cond == 1)
							htmltext = "30425-01.htm";
						else if (cond == 2)
							htmltext = "30425-06.htm";
						else if (cond == 3)
						{
							htmltext = "30425-07.htm";
							st.setCond(4);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, SPARTOI_BONES, 10);
							giveItems(player, HORSESHOE_OF_LIGHT, 1);
						}
						else if (cond > 3)
							htmltext = "30425-08.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		final int condition = npc.getScriptValue();
		if (condition < 0)
			return;
		
		final Player player = attacker.getActingPlayer();
		if (player == null || checkPlayerState(player, npc, QuestStatus.STARTED) == null)
			return;
		
		final int equippedItemId = player.getInventory().getItemIdFrom(Paperdoll.RHAND);
		if (equippedItemId != NETI_BOW && equippedItemId != NETI_DAGGER)
		{
			npc.setScriptValue(-1);
			return;
		}
		
		if (condition == 0)
		{
			if (npc.getNpcId() == CATSEYE_BANDIT)
				npc.broadcastNpcSay(NpcStringId.ID_40306);
			
			npc.setScriptValue(player.getObjectId());
		}
		else
		{
			if (condition != player.getObjectId())
				npc.setScriptValue(-1);
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null || npc.getScriptValue() < 0)
			return;
		
		switch (npc.getNpcId())
		{
			case TRACKER_SKELETON, SCOUT_SKELETON, SNIPER_SKELETON:
				if (st.getCond() == 2 && dropItems(player, SPARTOI_BONES, 1, 10, 200000))
					st.setCond(3);
				break;
			
			case TRACKER_SKELETON_LEADER:
				if (st.getCond() == 2 && dropItems(player, SPARTOI_BONES, 1, 10, 300000))
					st.setCond(3);
				break;
			
			case RUIN_SPARTOI, RAGING_SPARTOI:
				if (st.getCond() == 2 && dropItems(player, SPARTOI_BONES, 1, 10, 800000))
					st.setCond(3);
				break;
			
			case CATSEYE_BANDIT:
				npc.broadcastNpcSay(NpcStringId.ID_40307);
				
				if (st.getCond() == 5)
				{
					final int randomItem = Rnd.get(STOLEN_JEWELRY, STOLEN_NECKLACE);
					
					if (!player.getInventory().hasItems(randomItem))
					{
						giveItems(player, randomItem, 1);
						
						if (player.getInventory().hasItems(STOLEN_JEWELRY, STOLEN_TOMES, STOLEN_RING, STOLEN_NECKLACE))
						{
							st.setCond(6);
							playSound(player, SOUND_MIDDLE);
						}
						else
							playSound(player, SOUND_ITEMGET);
					}
				}
				break;
		}
	}
}