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

public class Q259_RanchersPlea extends Quest
{
	private static final String QUEST_NAME = "Q259_RanchersPlea";
	
	private static final int EDMOND = 30497;
	private static final int MARIUS = 30405;
	
	private static final int GIANT_SPIDER = 20103;
	private static final int TALON_SPIDER = 20106;
	private static final int BLADE_SPIDER = 20108;
	
	private static final int GIANT_SPIDER_SKIN = 1495;
	
	private static final int ADENA = 57;
	private static final int HEALING_POTION = 1061;
	private static final int WOODEN_ARROW = 17;
	
	public Q259_RanchersPlea()
	{
		super(259, "Rancher's Plea");
		
		setItemsIds(GIANT_SPIDER_SKIN);
		
		addQuestStart(EDMOND);
		addTalkId(EDMOND, MARIUS);
		
		addMyDying(GIANT_SPIDER, TALON_SPIDER, BLADE_SPIDER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30497-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("30497-06.htm"))
		{
			playSound(player, SOUND_FINISH);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("30405-04.htm"))
		{
			if (player.getInventory().getItemCount(GIANT_SPIDER_SKIN) >= 10)
			{
				takeItems(player, GIANT_SPIDER_SKIN, 10);
				rewardItems(player, HEALING_POTION, 1);
			}
			else
				htmltext = "<html><body>Incorrect item count</body></html>";
		}
		else if (event.equalsIgnoreCase("30405-05.htm"))
		{
			if (player.getInventory().getItemCount(GIANT_SPIDER_SKIN) >= 10)
			{
				takeItems(player, GIANT_SPIDER_SKIN, 10);
				rewardItems(player, WOODEN_ARROW, 50);
			}
			else
				htmltext = "<html><body>Incorrect item count</body></html>";
		}
		else if (event.equalsIgnoreCase("30405-07.htm"))
		{
			if (player.getInventory().getItemCount(GIANT_SPIDER_SKIN) >= 10)
				htmltext = "30405-06.htm";
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
				htmltext = (player.getStatus().getLevel() < 15) ? "30497-01.htm" : "30497-02.htm";
				break;
			
			case STARTED:
				final int count = player.getInventory().getItemCount(GIANT_SPIDER_SKIN);
				switch (npc.getNpcId())
				{
					case EDMOND:
						if (count == 0)
							htmltext = "30497-04.htm";
						else
						{
							htmltext = "30497-05.htm";
							takeItems(player, GIANT_SPIDER_SKIN, -1);
							rewardItems(player, ADENA, ((count >= 10) ? 250 : 0) + count * 25);
						}
						break;
					
					case MARIUS:
						htmltext = (count < 10) ? "30405-01.htm" : "30405-02.htm";
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
		
		dropItemsAlways(player, GIANT_SPIDER_SKIN, 1, 0);
	}
}