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

public class Q306_CrystalsOfFireAndIce extends Quest
{
	private static final String QUEST_NAME = "Q306_CrystalsOfFireAndIce";
	
	private static final int FLAME_SHARD = 1020;
	private static final int ICE_SHARD = 1021;
	
	private static final int[][] DROPLIST =
	{
		{
			20109,
			FLAME_SHARD,
			300000
		},
		{
			20110,
			ICE_SHARD,
			300000
		},
		{
			20112,
			FLAME_SHARD,
			400000
		},
		{
			20113,
			ICE_SHARD,
			400000
		},
		{
			20114,
			FLAME_SHARD,
			500000
		},
		{
			20115,
			ICE_SHARD,
			500000
		}
	};
	
	public Q306_CrystalsOfFireAndIce()
	{
		super(306, "Crystals of Fire and Ice");
		
		setItemsIds(FLAME_SHARD, ICE_SHARD);
		
		addQuestStart(30004);
		addTalkId(30004);
		
		addMyDying(20109, 20110, 20112, 20113, 20114, 20115);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30004-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30004-06.htm"))
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
				htmltext = (player.getStatus().getLevel() < 17) ? "30004-01.htm" : "30004-02.htm";
				break;
			
			case STARTED:
				final int totalItems = player.getInventory().getItemCount(FLAME_SHARD) + player.getInventory().getItemCount(ICE_SHARD);
				if (totalItems == 0)
					htmltext = "30004-04.htm";
				else
				{
					htmltext = "30004-05.htm";
					takeItems(player, FLAME_SHARD, -1);
					takeItems(player, ICE_SHARD, -1);
					rewardItems(player, 57, 30 * totalItems + ((totalItems > 10) ? 5000 : 0));
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
		
		for (int[] drop : DROPLIST)
		{
			if (npc.getNpcId() == drop[0])
			{
				dropItems(player, drop[1], 1, 0, drop[2]);
				break;
			}
		}
	}
}