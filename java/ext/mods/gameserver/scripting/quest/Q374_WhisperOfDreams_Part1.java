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

public class Q374_WhisperOfDreams_Part1 extends Quest
{
	private static final String QUEST_NAME = "Q374_WhisperOfDreams_Part1";
	
	private static final int MANAKIA = 30515;
	private static final int TORAI = 30557;
	
	private static final int CAVE_BEAST = 20620;
	private static final int DEATH_WAVE = 20621;
	
	private static final int CAVE_BEAST_TOOTH = 5884;
	private static final int DEATH_WAVE_LIGHT = 5885;
	private static final int SEALED_MYSTERIOUS_STONE = 5886;
	private static final int MYSTERIOUS_STONE = 5887;
	
	private static final int[][] REWARDS =
	{
		{
			5486,
			3,
			2950
		},
		{
			5487,
			2,
			18050
		},
		{
			5488,
			2,
			18050
		},
		{
			5485,
			4,
			10450
		},
		{
			5489,
			6,
			15550
		}
	};
	
	public Q374_WhisperOfDreams_Part1()
	{
		super(374, "Whisper of Dreams, Part 1");
		
		setItemsIds(DEATH_WAVE_LIGHT, CAVE_BEAST_TOOTH, SEALED_MYSTERIOUS_STONE, MYSTERIOUS_STONE);
		
		addQuestStart(MANAKIA);
		addTalkId(MANAKIA, TORAI);
		
		addMyDying(CAVE_BEAST, DEATH_WAVE);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30515-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			st.set("condStone", 1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.startsWith("30515-06-"))
		{
			if (player.getInventory().getItemCount(CAVE_BEAST_TOOTH) >= 65 && player.getInventory().getItemCount(DEATH_WAVE_LIGHT) >= 65)
			{
				htmltext = "30515-06.htm";
				playSound(player, SOUND_MIDDLE);
				
				int[] reward = REWARDS[Integer.parseInt(event.substring(9, 10))];
				
				takeItems(player, CAVE_BEAST_TOOTH, -1);
				takeItems(player, DEATH_WAVE_LIGHT, -1);
				
				rewardItems(player, 57, reward[2]);
				giveItems(player, reward[0], reward[1]);
			}
			else
				htmltext = "30515-07.htm";
		}
		else if (event.equalsIgnoreCase("30515-08.htm"))
		{
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30557-02.htm"))
		{
			if (st.getCond() == 2 && player.getInventory().hasItems(SEALED_MYSTERIOUS_STONE))
			{
				st.setCond(3);
				takeItems(player, SEALED_MYSTERIOUS_STONE, -1);
				giveItems(player, MYSTERIOUS_STONE, 1);
				playSound(player, SOUND_MIDDLE);
			}
			else
				htmltext = "30557-03.htm";
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
				htmltext = (player.getStatus().getLevel() < 56) ? "30515-01.htm" : "30515-02.htm";
				break;
			
			case STARTED:
				final int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case MANAKIA:
						if (!player.getInventory().hasItems(SEALED_MYSTERIOUS_STONE))
						{
							if (player.getInventory().getItemCount(CAVE_BEAST_TOOTH) >= 65 && player.getInventory().getItemCount(DEATH_WAVE_LIGHT) >= 65)
								htmltext = "30515-05.htm";
							else
								htmltext = "30515-04.htm";
						}
						else
						{
							if (cond == 1)
							{
								htmltext = "30515-09.htm";
								st.setCond(2);
								playSound(player, SOUND_MIDDLE);
							}
							else
								htmltext = "30515-10.htm";
						}
						break;
					
					case TORAI:
						if (cond == 2 && player.getInventory().hasItems(SEALED_MYSTERIOUS_STONE))
							htmltext = "30557-01.htm";
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
		
		QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		dropItems(st.getPlayer(), (npc.getNpcId() == CAVE_BEAST) ? CAVE_BEAST_TOOTH : DEATH_WAVE_LIGHT, 1, 65, 500000);
		
		st = getRandomPartyMember(player, npc, "condStone", "1");
		if (st != null && dropItems(st.getPlayer(), SEALED_MYSTERIOUS_STONE, 1, 1, 1000))
			st.unset("condStone");
	}
}