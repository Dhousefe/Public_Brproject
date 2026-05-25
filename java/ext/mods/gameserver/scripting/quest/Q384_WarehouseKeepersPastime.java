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
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q384_WarehouseKeepersPastime extends Quest
{
	private static final String QUEST_NAME = "Q384_WarehouseKeepersPastime";
	
	private static final int CLIFF = 30182;
	private static final int BAXT = 30685;
	
	private static final int MEDAL = 5964;
	
	private static final Map<Integer, Integer> CHANCES = HashMap.newHashMap(26);
	
	private static final IntIntHolder[] REWARDS_10_WIN =
	{
		new IntIntHolder(16, 1888),
		new IntIntHolder(32, 1887),
		new IntIntHolder(50, 1894),
		new IntIntHolder(80, 952),
		new IntIntHolder(89, 1890),
		new IntIntHolder(98, 1893),
		new IntIntHolder(100, 951)
	};
	
	private static final IntIntHolder[] REWARDS_10_LOSE =
	{
		new IntIntHolder(50, 4041),
		new IntIntHolder(80, 952),
		new IntIntHolder(98, 1892),
		new IntIntHolder(100, 917)
	};
	
	private static final IntIntHolder[] REWARDS_100_WIN =
	{
		new IntIntHolder(50, 883),
		new IntIntHolder(80, 951),
		new IntIntHolder(98, 852),
		new IntIntHolder(100, 401)
	};
	
	private static final IntIntHolder[] REWARDS_100_LOSE =
	{
		new IntIntHolder(50, 951),
		new IntIntHolder(80, 500),
		new IntIntHolder(98, 2437),
		new IntIntHolder(100, 135)
	};
	
	public Q384_WarehouseKeepersPastime()
	{
		super(384, "Warehouse Keeper's Pastime");
		
		CHANCES.put(20947, 160000);
		CHANCES.put(20948, 180000);
		CHANCES.put(20945, 120000);
		CHANCES.put(20946, 150000);
		CHANCES.put(20635, 150000);
		CHANCES.put(20773, 610000);
		CHANCES.put(20774, 600000);
		CHANCES.put(20760, 240000);
		CHANCES.put(20758, 240000);
		CHANCES.put(20759, 230000);
		CHANCES.put(20242, 220000);
		CHANCES.put(20281, 220000);
		CHANCES.put(20556, 140000);
		CHANCES.put(20668, 210000);
		CHANCES.put(20241, 220000);
		CHANCES.put(20286, 220000);
		CHANCES.put(20949, 190000);
		CHANCES.put(20950, 200000);
		CHANCES.put(20942, 90000);
		CHANCES.put(20943, 120000);
		CHANCES.put(20944, 110000);
		CHANCES.put(20559, 140000);
		CHANCES.put(20243, 210000);
		CHANCES.put(20282, 210000);
		CHANCES.put(20677, 340000);
		CHANCES.put(20605, 150000);
		
		addQuestStart(CLIFF);
		addTalkId(CLIFF, BAXT);
		
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
		
		final int npcId = npc.getNpcId();
		if (event.equalsIgnoreCase("30182-05.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase(npcId + "-08.htm"))
		{
			playSound(player, SOUND_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase(npcId + "-11.htm"))
		{
			if (player.getInventory().getItemCount(MEDAL) < 10)
				htmltext = npcId + "-12.htm";
			else
			{
				st.set("bet", 10);
				st.set("board", StringUtil.scrambleString("123456789"));
				takeItems(player, MEDAL, 10);
			}
		}
		else if (event.equalsIgnoreCase(npcId + "-13.htm"))
		{
			if (player.getInventory().getItemCount(MEDAL) < 100)
				htmltext = npcId + "-12.htm";
			else
			{
				st.set("bet", 100);
				st.set("board", StringUtil.scrambleString("123456789"));
				takeItems(player, MEDAL, 100);
			}
		}
		else if (event.startsWith("select_1-"))
		{
			st.set("playerArray", event.substring(9));
			
			htmltext = fillBoard(st, getHtmlText(npcId + "-14.htm", player));
		}
		else if (event.startsWith("select_2-"))
		{
			String number = event.substring(9);
			
			String playerArray = st.get("playerArray");
			
			if (playerArray.contains(number))
				htmltext = fillBoard(st, getHtmlText(npcId + "-" + (14 + 2 * playerArray.length()) + ".htm", player));
			else
			{
				st.set("playerArray", playerArray.concat(number));
				
				htmltext = fillBoard(st, getHtmlText(npcId + "-" + (13 + 2 * playerArray.length()) + ".htm", player));
			}
		}
		else if (event.startsWith("select_3-"))
		{
			String number = event.substring(9);
			
			String playerArray = st.get("playerArray");
			
			if (playerArray.contains(number))
				htmltext = fillBoard(st, getHtmlText(npcId + "-26.htm", player));
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
						htmltext = getHtmlText(npcId + "-23.htm", player);
						
						int chance = Rnd.get(100);
						for (IntIntHolder reward : ((st.getInteger("bet") == 10) ? REWARDS_10_WIN : REWARDS_100_WIN))
						{
							if (chance < reward.getId())
							{
								giveItems(player, reward.getValue(), 1);
								if (reward.getValue() == 2437)
									giveItems(player, 2463, 1);
								
								break;
							}
						}
						break;
						
					case 0:
						htmltext = getHtmlText(npcId + "-25.htm", player);
						
						chance = Rnd.get(100);
						for (IntIntHolder reward : ((st.getInteger("bet") == 10) ? REWARDS_10_LOSE : REWARDS_100_LOSE))
						{
							if (chance < reward.getId())
							{
								giveItems(player, reward.getValue(), 1);
								break;
							}
						}
						break;
					
					default:
						htmltext = getHtmlText(npcId + "-24.htm", player);
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
				htmltext = (player.getStatus().getLevel() < 40) ? "30182-04.htm" : "30182-01.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case CLIFF:
						htmltext = (player.getInventory().getItemCount(MEDAL) < 10) ? "30182-06.htm" : "30182-07.htm";
						break;
					
					case BAXT:
						htmltext = (player.getInventory().getItemCount(MEDAL) < 10) ? "30685-01.htm" : "30685-02.htm";
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
		
		final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		dropItems(st.getPlayer(), MEDAL, 1, 0, CHANCES.get(npc.getNpcId()));
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