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
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q166_MassOfDarkness extends Quest
{
	private static final String QUEST_NAME = "Q166_MassOfDarkness";
	
	private static final int UNDRIAS = 30130;
	private static final int IRIA = 30135;
	private static final int DORANKUS = 30139;
	private static final int TRUDY = 30143;
	
	private static final int UNDRIAS_LETTER = 1088;
	private static final int CEREMONIAL_DAGGER = 1089;
	private static final int DREVIANT_WINE = 1090;
	private static final int GARMIEL_SCRIPTURE = 1091;
	
	public Q166_MassOfDarkness()
	{
		super(166, "Mass of Darkness");
		
		setItemsIds(UNDRIAS_LETTER, CEREMONIAL_DAGGER, DREVIANT_WINE, GARMIEL_SCRIPTURE);
		
		addQuestStart(UNDRIAS);
		addTalkId(UNDRIAS, IRIA, DORANKUS, TRUDY);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30130-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, UNDRIAS_LETTER, 1);
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
				if (player.getRace() != ClassRace.DARK_ELF)
					htmltext = "30130-00.htm";
				else if (player.getStatus().getLevel() < 2)
					htmltext = "30130-02.htm";
				else
					htmltext = "30130-03.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case UNDRIAS:
						if (cond == 1)
							htmltext = "30130-05.htm";
						else if (cond == 2)
						{
							htmltext = "30130-06.htm";
							takeItems(player, CEREMONIAL_DAGGER, 1);
							takeItems(player, DREVIANT_WINE, 1);
							takeItems(player, GARMIEL_SCRIPTURE, 1);
							takeItems(player, UNDRIAS_LETTER, 1);
							rewardItems(player, 57, 500);
							rewardExpAndSp(player, 500, 0);
							playSound(player, SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case IRIA:
						if (cond == 1 && !player.getInventory().hasItems(CEREMONIAL_DAGGER))
						{
							htmltext = "30135-01.htm";
							giveItems(player, CEREMONIAL_DAGGER, 1);
							
							if (player.getInventory().hasItems(DREVIANT_WINE, GARMIEL_SCRIPTURE))
							{
								st.setCond(2);
								playSound(player, SOUND_MIDDLE);
							}
							else
								playSound(player, SOUND_ITEMGET);
						}
						else if (cond == 2)
							htmltext = "30135-02.htm";
						break;
					
					case DORANKUS:
						if (cond == 1 && !player.getInventory().hasItems(DREVIANT_WINE))
						{
							htmltext = "30139-01.htm";
							giveItems(player, DREVIANT_WINE, 1);
							
							if (player.getInventory().hasItems(CEREMONIAL_DAGGER, GARMIEL_SCRIPTURE))
							{
								st.setCond(2);
								playSound(player, SOUND_MIDDLE);
							}
							else
								playSound(player, SOUND_ITEMGET);
						}
						else if (cond == 2)
							htmltext = "30139-02.htm";
						break;
					
					case TRUDY:
						if (cond == 1 && !player.getInventory().hasItems(GARMIEL_SCRIPTURE))
						{
							htmltext = "30143-01.htm";
							giveItems(player, GARMIEL_SCRIPTURE, 1);
							
							if (player.getInventory().hasItems(CEREMONIAL_DAGGER, DREVIANT_WINE))
							{
								st.setCond(2);
								playSound(player, SOUND_MIDDLE);
							}
							else
								playSound(player, SOUND_ITEMGET);
						}
						else if (cond == 2)
							htmltext = "30143-02.htm";
						break;
				}
				break;
			
			case COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		
		return htmltext;
	}
}