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
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q411_PathToAnAssassin extends Quest
{
	private static final String QUEST_NAME = "Q411_PathToAnAssassin";
	
	private static final int SHILEN_CALL = 1245;
	private static final int ARKENIA_LETTER = 1246;
	private static final int LEIKAN_NOTE = 1247;
	private static final int MOONSTONE_BEAST_MOLAR = 1248;
	private static final int SHILEN_TEARS = 1250;
	private static final int ARKENIA_RECOMMENDATION = 1251;
	private static final int IRON_HEART = 1252;
	
	private static final int TRISKEL = 30416;
	private static final int ARKENIA = 30419;
	private static final int LEIKAN = 30382;
	
	private static final int MOONSTONE_BEAST = 20369;
	private static final int CALPICO = 27036;
	
	public Q411_PathToAnAssassin()
	{
		super(411, "Path to an Assassin");
		
		setItemsIds(SHILEN_CALL, ARKENIA_LETTER, LEIKAN_NOTE, MOONSTONE_BEAST_MOLAR, SHILEN_TEARS, ARKENIA_RECOMMENDATION);
		
		addQuestStart(TRISKEL);
		addTalkId(TRISKEL, ARKENIA, LEIKAN);
		
		addMyDying(CALPICO, MOONSTONE_BEAST);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30416-05.htm"))
		{
			if (player.getClassId() != ClassId.DARK_FIGHTER)
				htmltext = (player.getClassId() == ClassId.ASSASSIN) ? "30416-02a.htm" : "30416-02.htm";
			else if (player.getStatus().getLevel() < 19)
				htmltext = "30416-03.htm";
			else if (player.getInventory().hasItems(IRON_HEART))
				htmltext = "30416-04.htm";
			else
			{
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
				giveItems(player, SHILEN_CALL, 1);
			}
		}
		else if (event.equalsIgnoreCase("30419-05.htm"))
		{
			st.setCond(2);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, SHILEN_CALL, 1);
			giveItems(player, ARKENIA_LETTER, 1);
		}
		else if (event.equalsIgnoreCase("30382-03.htm"))
		{
			st.setCond(3);
			playSound(player, SOUND_MIDDLE);
			takeItems(player, ARKENIA_LETTER, 1);
			giveItems(player, LEIKAN_NOTE, 1);
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
				htmltext = "30416-01.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case TRISKEL:
						if (cond == 1)
							htmltext = "30416-11.htm";
						else if (cond == 2)
							htmltext = "30416-07.htm";
						else if (cond == 3 || cond == 4)
							htmltext = "30416-08.htm";
						else if (cond == 5)
							htmltext = "30416-09.htm";
						else if (cond == 6)
							htmltext = "30416-10.htm";
						else if (cond == 7)
						{
							htmltext = "30416-06.htm";
							takeItems(player, ARKENIA_RECOMMENDATION, 1);
							giveItems(player, IRON_HEART, 1);
							rewardExpAndSp(player, 3200, 3930);
							player.broadcastPacket(new SocialAction(player, 3));
							playSound(player, SOUND_FINISH);
							st.exitQuest(true);
						}
						break;
					
					case ARKENIA:
						if (cond == 1)
							htmltext = "30419-01.htm";
						else if (cond == 2)
							htmltext = "30419-07.htm";
						else if (cond == 3 || cond == 4)
							htmltext = "30419-10.htm";
						else if (cond == 5)
							htmltext = "30419-11.htm";
						else if (cond == 6)
						{
							htmltext = "30419-08.htm";
							st.setCond(7);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, SHILEN_TEARS, -1);
							giveItems(player, ARKENIA_RECOMMENDATION, 1);
						}
						else if (cond == 7)
							htmltext = "30419-09.htm";
						break;
					
					case LEIKAN:
						if (cond == 2)
							htmltext = "30382-01.htm";
						else if (cond == 3)
							htmltext = (!player.getInventory().hasItems(MOONSTONE_BEAST_MOLAR)) ? "30382-05.htm" : "30382-06.htm";
						else if (cond == 4)
						{
							htmltext = "30382-07.htm";
							st.setCond(5);
							playSound(player, SOUND_MIDDLE);
							takeItems(player, MOONSTONE_BEAST_MOLAR, -1);
							takeItems(player, LEIKAN_NOTE, -1);
						}
						else if (cond == 5)
							htmltext = "30382-09.htm";
						else if (cond > 5)
							htmltext = "30382-08.htm";
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		if (npc.getNpcId() == MOONSTONE_BEAST)
		{
			if (st.getCond() == 3 && dropItemsAlways(player, MOONSTONE_BEAST_MOLAR, 1, 10))
				st.setCond(4);
		}
		else if (st.getCond() == 5)
		{
			st.setCond(6);
			playSound(player, SOUND_MIDDLE);
			giveItems(player, SHILEN_TEARS, 1);
		}
	}
}