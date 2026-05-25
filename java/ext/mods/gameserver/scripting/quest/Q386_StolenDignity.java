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

import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.math.MathUtil;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q386_StolenDignity extends Quest
{
	private static final String QUEST_NAME = "Q386_StolenDignity";
	
	private static final int ROMP = 30843;
	
	private static final int STOLEN_INFERNIUM_ORE = 6363;
	
	private static final Map<Integer, Integer> CHANCES = HashMap.newHashMap(26);
	
	private static final int[] REWARDS =
	{
		5529,
		5532,
		5533,
		5534,
		5535,
		5536,
		5537,
		5538,
		5539,
		5541,
		5542,
		5543,
		5544,
		5545,
		5546,
		5547,
		5548,
		8331,
		8341,
		8342,
		8349,
		8346
	};
	
	public Q386_StolenDignity()
	{
		super(386, "Stolen Dignity");
		
		CHANCES.put(20970, 208000);
		CHANCES.put(20971, 299000);
		CHANCES.put(20958, 170000);
		CHANCES.put(20960, 149000);
		CHANCES.put(20963, 199000);
		CHANCES.put(20670, 202000);
		CHANCES.put(21114, 352000);
		CHANCES.put(20959, 273000);
		CHANCES.put(21020, 478000);
		CHANCES.put(21258, 487000);
		CHANCES.put(21003, 173000);
		CHANCES.put(20969, 205000);
		CHANCES.put(21108, 245000);
		CHANCES.put(21005, 211000);
		CHANCES.put(21116, 487000);
		CHANCES.put(21113, 370000);
		CHANCES.put(20954, 184000);
		CHANCES.put(20671, 211000);
		CHANCES.put(21110, 260000);
		CHANCES.put(20967, 257000);
		CHANCES.put(20956, 216000);
		CHANCES.put(21021, 234000);
		CHANCES.put(21259, 487000);
		CHANCES.put(20974, 440000);
		CHANCES.put(20975, 390000);
		CHANCES.put(21001, 214000);
		
		addQuestStart(ROMP);
		addTalkId(ROMP);
		
		for (int npcId : CHANCES.keySet())
			addMyDying(npcId);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30843-05.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30843-08.htm"))
		{
			playSound(player, SOUND_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30843-12.htm"))
		{
			if (player.getInventory().getItemCount(STOLEN_INFERNIUM_ORE) < 100)
				htmltext = "30843-11.htm";
			else
			{
				st.set("board", StringUtil.scrambleString("123456789"));
				takeItems(player, STOLEN_INFERNIUM_ORE, 100);
			}
		}
		else if (event.startsWith("select_1-"))
		{
			st.set("playerArray", event.substring(9));
			
			htmltext = fillBoard(st, getHtmlText("30843-13.htm", player));
		}
		else if (event.startsWith("select_2-"))
		{
			String number = event.substring(9);
			
			String playerArray = st.get("playerArray");
			
			if (playerArray.contains(number))
				htmltext = fillBoard(st, getHtmlText("30843-" + (13 + 2 * playerArray.length()) + ".htm", player));
			else
			{
				st.set("playerArray", playerArray.concat(number));
				
				htmltext = fillBoard(st, getHtmlText("30843-" + (12 + 2 * playerArray.length()) + ".htm", player));
			}
		}
		else if (event.startsWith("select_3-"))
		{
			String number = event.substring(9);
			
			String playerArray = st.get("playerArray");
			
			if (playerArray.contains(number))
				htmltext = fillBoard(st, getHtmlText("30843-25.htm", player));
			else
			{
				final String playerChoice = playerArray.concat(number);
				
				final String[] board = st.get("board").split("");
				
				int winningLines = 0;
				
				for (int[] map : MathUtil.MATRICE_3X3_LINES)
				{
					boolean won = true;
					for (int index : map)
						won &= playerChoice.contains(board[index - 1]);
					
					if (won)
						winningLines++;
				}
				
				switch (winningLines)
				{
					case 3:
						htmltext = getHtmlText("30843-22.htm", player);
						giveItems(player, Rnd.get(REWARDS), 4);
						break;
					
					case 0:
						htmltext = getHtmlText("30843-24.htm", player);
						giveItems(player, Rnd.get(REWARDS), 10);
						break;
					
					default:
						htmltext = getHtmlText("30843-23.htm", player);
						break;
				}
				
				for (int i = 1; i < 10; i++)
				{
					htmltext = htmltext.replace("<?Cell" + i + "?>", board[i - 1]);
					htmltext = htmltext.replace("<?FontColor" + i + "?>", (playerChoice.contains(board[i - 1])) ? "ff0000" : "ffffff");
				}
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
				htmltext = (player.getStatus().getLevel() < 58) ? "30843-04.htm" : "30843-01.htm";
				break;
			
			case STARTED:
				htmltext = (player.getInventory().getItemCount(STOLEN_INFERNIUM_ORE) < 100) ? "30843-06.htm" : "30843-07.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		
		final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		dropItems(st.getPlayer(), STOLEN_INFERNIUM_ORE, 1, 0, CHANCES.get(npc.getNpcId()));
	}
	
	private static final String fillBoard(QuestState st, String htmltext)
	{
		final String playerArray = st.get("playerArray");
		final String[] board = st.get("board").split("");
		
		for (int i = 1; i < 10; i++)
			htmltext = htmltext.replace("<?Cell" + i + "?>", (playerArray.contains(board[i - 1])) ? board[i - 1] : "?");
		
		return htmltext;
	}
}