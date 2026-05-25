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

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q162_CurseOfTheUndergroundFortress extends Quest
{
	private static final String QUEST_NAME = "Q162_CurseOfTheUndergroundFortress";
	
	private static final int SHADE_HORROR = 20033;
	private static final int DARK_TERROR = 20345;
	private static final int MIST_TERROR = 20371;
	private static final int DUNGEON_SKELETON_ARCHER = 20463;
	private static final int DUNGEON_SKELETON = 20464;
	private static final int DREAD_SOLDIER = 20504;
	
	private static final int BONE_FRAGMENT = 1158;
	private static final int ELF_SKULL = 1159;
	
	private static final int BONE_SHIELD = 625;
	
	private static final Map<Integer, Integer> CHANCES = HashMap.newHashMap(6);
	
	public Q162_CurseOfTheUndergroundFortress()
	{
		super(162, "Curse of the Underground Fortress");
		
		CHANCES.put(SHADE_HORROR, 250000);
		CHANCES.put(DARK_TERROR, 260000);
		CHANCES.put(MIST_TERROR, 230000);
		CHANCES.put(DUNGEON_SKELETON_ARCHER, 250000);
		CHANCES.put(DUNGEON_SKELETON, 230000);
		CHANCES.put(DREAD_SOLDIER, 260000);
		
		setItemsIds(BONE_FRAGMENT, ELF_SKULL);
		
		addQuestStart(30147);
		addTalkId(30147);
		
		addMyDying(SHADE_HORROR, DARK_TERROR, MIST_TERROR, DUNGEON_SKELETON_ARCHER, DUNGEON_SKELETON, DREAD_SOLDIER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30147-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		String htmltext = getNoQuestMsg();
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getRace() == ClassRace.DARK_ELF)
					htmltext = "30147-00.htm";
				else if (player.getStatus().getLevel() < 12)
					htmltext = "30147-01.htm";
				else
					htmltext = "30147-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				if (cond == 1)
					htmltext = "30147-05.htm";
				else if (cond == 2)
				{
					htmltext = "30147-06.htm";
					takeItems(player, ELF_SKULL, -1);
					takeItems(player, BONE_FRAGMENT, -1);
					giveItems(player, BONE_SHIELD, 1);
					rewardItems(player, 57, 24000);
					playSound(player, SOUND_FINISH);
					st.exitQuest(false);
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
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
		
		final int npcId = npc.getNpcId();
		
		switch (npcId)
		{
			case DUNGEON_SKELETON, DUNGEON_SKELETON_ARCHER, DREAD_SOLDIER:
				if (dropItems(player, BONE_FRAGMENT, 1, 10, CHANCES.get(npcId)) && player.getInventory().getItemCount(ELF_SKULL) >= 3)
					st.setCond(2);
				break;
			
			case SHADE_HORROR, DARK_TERROR, MIST_TERROR:
				if (dropItems(player, ELF_SKULL, 1, 3, CHANCES.get(npcId)) && player.getInventory().getItemCount(BONE_FRAGMENT) >= 10)
					st.setCond(2);
				break;
		}
	}
}