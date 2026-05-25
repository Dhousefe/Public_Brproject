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

public class Q331_ArrowOfVengeance extends Quest
{
	private static final String QUEST_NAME = "Q331_ArrowOfVengeance";
	
	private static final int HARPY_FEATHER = 1452;
	private static final int MEDUSA_VENOM = 1453;
	private static final int WYRM_TOOTH = 1454;
	
	public Q331_ArrowOfVengeance()
	{
		super(331, "Arrow of Vengeance");
		
		setItemsIds(HARPY_FEATHER, MEDUSA_VENOM, WYRM_TOOTH);
		
		addQuestStart(30125);
		addTalkId(30125);
		
		addMyDying(20145, 20158, 20176);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30125-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30125-06.htm"))
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
				htmltext = (player.getStatus().getLevel() < 32) ? "30125-01.htm" : "30125-02.htm";
				break;
			
			case STARTED:
				final int harpyFeather = player.getInventory().getItemCount(HARPY_FEATHER);
				final int medusaVenom = player.getInventory().getItemCount(MEDUSA_VENOM);
				final int wyrmTooth = player.getInventory().getItemCount(WYRM_TOOTH);
				
				if (harpyFeather + medusaVenom + wyrmTooth > 0)
				{
					htmltext = "30125-05.htm";
					takeItems(player, HARPY_FEATHER, -1);
					takeItems(player, MEDUSA_VENOM, -1);
					takeItems(player, WYRM_TOOTH, -1);
					
					int reward = harpyFeather * 78 + medusaVenom * 88 + wyrmTooth * 92;
					if (harpyFeather + medusaVenom + wyrmTooth > 10)
						reward += 3100;
					
					rewardItems(player, 57, reward);
				}
				else
					htmltext = "30125-04.htm";
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
		
		switch (npc.getNpcId())
		{
			case 20145:
				dropItems(player, HARPY_FEATHER, 1, 0, 500000);
				break;
			
			case 20158:
				dropItems(player, MEDUSA_VENOM, 1, 0, 500000);
				break;
			
			case 20176:
				dropItems(player, WYRM_TOOTH, 1, 0, 500000);
				break;
		}
	}
}