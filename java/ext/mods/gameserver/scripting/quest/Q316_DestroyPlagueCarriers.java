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
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;

public class Q316_DestroyPlagueCarriers extends Quest
{
	private static final String QUEST_NAME = "Q316_DestroyPlagueCarriers";
	
	private static final int WERERAT_FANG = 1042;
	private static final int VAROOL_FOULCLAW_FANG = 1043;
	
	private static final int SUKAR_WERERAT = 20040;
	private static final int SUKAR_WERERAT_LEADER = 20047;
	private static final int VAROOL_FOULCLAW = 27020;
	
	public Q316_DestroyPlagueCarriers()
	{
		super(316, "Destroy Plague Carriers");
		
		setItemsIds(WERERAT_FANG, VAROOL_FOULCLAW_FANG);
		
		addQuestStart(30155);
		addTalkId(30155);
		
		addAttacked(VAROOL_FOULCLAW);
		addMyDying(SUKAR_WERERAT, SUKAR_WERERAT_LEADER, VAROOL_FOULCLAW);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30155-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30155-08.htm"))
		{
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
				if (player.getRace() != ClassRace.ELF)
					htmltext = "30155-00.htm";
				else if (player.getStatus().getLevel() < 18)
					htmltext = "30155-02.htm";
				else
					htmltext = "30155-03.htm";
				break;
			
			case STARTED:
				final int ratFangs = player.getInventory().getItemCount(WERERAT_FANG);
				final int varoolFangs = player.getInventory().getItemCount(VAROOL_FOULCLAW_FANG);
				
				if (ratFangs + varoolFangs == 0)
					htmltext = "30155-05.htm";
				else
				{
					htmltext = "30155-07.htm";
					takeItems(player, WERERAT_FANG, -1);
					takeItems(player, VAROOL_FOULCLAW_FANG, -1);
					rewardItems(player, 57, ratFangs * 30 + varoolFangs * 10000 + ((ratFangs > 10) ? 5000 : 0));
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.getScriptValue() == 0)
		{
			npc.broadcastNpcSay(NpcStringId.ID_31603);
			npc.setScriptValue(1);
		}
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
			case SUKAR_WERERAT, SUKAR_WERERAT_LEADER:
				dropItems(player, WERERAT_FANG, 1, 0, 400000);
				break;
			
			case VAROOL_FOULCLAW:
				dropItems(player, VAROOL_FOULCLAW_FANG, 1, 1, 200000);
				break;
		}
	}
}