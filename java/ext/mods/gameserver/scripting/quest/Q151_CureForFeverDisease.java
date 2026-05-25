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
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q151_CureForFeverDisease extends Quest
{
	private static final String QUEST_NAME = "Q151_CureForFeverDisease";
	
	private static final int POISON_SAC = 703;
	private static final int FEVER_MEDICINE = 704;
	
	private static final int ELIAS = 30050;
	private static final int YOHANES = 30032;
	
	public Q151_CureForFeverDisease()
	{
		super(151, "Cure for Fever Disease");
		
		setItemsIds(FEVER_MEDICINE, POISON_SAC);
		
		addQuestStart(ELIAS);
		addTalkId(ELIAS, YOHANES);
		
		addMyDying(20103, 20106, 20108);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30050-03.htm"))
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
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				htmltext = (player.getStatus().getLevel() < 15) ? "30050-01.htm" : "30050-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case ELIAS:
						if (cond == 1)
							htmltext = "30050-04.htm";
						else if (cond == 2)
							htmltext = "30050-05.htm";
						else if (cond == 3)
						{
							htmltext = "30050-06.htm";
							takeItems(player, FEVER_MEDICINE, 1);
							giveItems(player, 102, 1);
							playSound(player, SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case YOHANES:
						if (cond == 2)
						{
							htmltext = "30032-01.htm";
							st.setCond(3);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, POISON_SAC, 1);
							giveItems(player, FEVER_MEDICINE, 1);
						}
						else if (cond == 3)
							htmltext = "30032-02.htm";
						break;
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
		
		if (dropItems(player, POISON_SAC, 1, 1, 200000))
			st.setCond(2);
	}
}