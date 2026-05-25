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

public class Q408_PathToAnElvenWizard extends Quest
{
	private static final String QUEST_NAME = "Q408_PathToAnElvenWizard";
	
	private static final int ROSELLA_LETTER = 1218;
	private static final int RED_DOWN = 1219;
	private static final int MAGICAL_POWERS_RUBY = 1220;
	private static final int PURE_AQUAMARINE = 1221;
	private static final int APPETIZING_APPLE = 1222;
	private static final int GOLD_LEAVES = 1223;
	private static final int IMMORTAL_LOVE = 1224;
	private static final int AMETHYST = 1225;
	private static final int NOBILITY_AMETHYST = 1226;
	private static final int FERTILITY_PERIDOT = 1229;
	private static final int ETERNITY_DIAMOND = 1230;
	private static final int CHARM_OF_GRAIN = 1272;
	private static final int SAP_OF_THE_MOTHER_TREE = 1273;
	private static final int LUCKY_POTPOURRI = 1274;
	
	private static final int ROSELLA = 30414;
	private static final int GREENIS = 30157;
	private static final int THALIA = 30371;
	private static final int NORTHWIND = 30423;
	
	private static final int DRYAD_ELDER = 20019;
	private static final int SUKAR_WERERAT_LEADER = 20047;
	private static final int PINCER_SPIDER = 20466;
	
	public Q408_PathToAnElvenWizard()
	{
		super(408, "Path to an Elven Wizard");
		
		setItemsIds(ROSELLA_LETTER, RED_DOWN, MAGICAL_POWERS_RUBY, PURE_AQUAMARINE, APPETIZING_APPLE, GOLD_LEAVES, IMMORTAL_LOVE, AMETHYST, NOBILITY_AMETHYST, FERTILITY_PERIDOT, CHARM_OF_GRAIN, SAP_OF_THE_MOTHER_TREE, LUCKY_POTPOURRI);
		
		addQuestStart(ROSELLA);
		addTalkId(ROSELLA, GREENIS, THALIA, NORTHWIND);
		
		addMyDying(SUKAR_WERERAT_LEADER, DRYAD_ELDER, PINCER_SPIDER);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30414-06.htm"))
		{
			if (player.getClassId() != ClassId.ELVEN_MYSTIC)
				htmltext = (player.getClassId() == ClassId.ELVEN_WIZARD) ? "30414-02a.htm" : "30414-03.htm";
			else if (player.getStatus().getLevel() < 19)
				htmltext = "30414-04.htm";
			else if (player.getInventory().hasItems(ETERNITY_DIAMOND))
				htmltext = "30414-05.htm";
			else
			{
				st.setState(QuestStatus.STARTED);
				st.setCond(1);
				playSound(player, SOUND_ACCEPT);
				giveItems(player, FERTILITY_PERIDOT, 1);
			}
		}
		else if (event.equalsIgnoreCase("30414-07.htm"))
		{
			if (!player.getInventory().hasItems(MAGICAL_POWERS_RUBY))
			{
				playSound(player, SOUND_MIDDLE);
				giveItems(player, ROSELLA_LETTER, 1);
			}
			else
				htmltext = "30414-10.htm";
		}
		else if (event.equalsIgnoreCase("30414-14.htm"))
		{
			if (!player.getInventory().hasItems(PURE_AQUAMARINE))
			{
				playSound(player, SOUND_MIDDLE);
				giveItems(player, APPETIZING_APPLE, 1);
			}
			else
				htmltext = "30414-13.htm";
		}
		else if (event.equalsIgnoreCase("30414-18.htm"))
		{
			if (!player.getInventory().hasItems(NOBILITY_AMETHYST))
			{
				playSound(player, SOUND_MIDDLE);
				giveItems(player, IMMORTAL_LOVE, 1);
			}
			else
				htmltext = "30414-17.htm";
		}
		else if (event.equalsIgnoreCase("30157-02.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			takeItems(player, ROSELLA_LETTER, 1);
			giveItems(player, CHARM_OF_GRAIN, 1);
		}
		else if (event.equalsIgnoreCase("30371-02.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			takeItems(player, APPETIZING_APPLE, 1);
			giveItems(player, SAP_OF_THE_MOTHER_TREE, 1);
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
				htmltext = "30414-01.htm";
				break;
			
			case STARTED:
				switch (npc.getNpcId())
				{
					case ROSELLA:
						if (player.getInventory().hasItems(MAGICAL_POWERS_RUBY, NOBILITY_AMETHYST, PURE_AQUAMARINE))
						{
							htmltext = "30414-24.htm";
							takeItems(player, FERTILITY_PERIDOT, 1);
							takeItems(player, MAGICAL_POWERS_RUBY, 1);
							takeItems(player, NOBILITY_AMETHYST, 1);
							takeItems(player, PURE_AQUAMARINE, 1);
							giveItems(player, ETERNITY_DIAMOND, 1);
							rewardExpAndSp(player, 3200, 1890);
							player.broadcastPacket(new SocialAction(player, 3));
							playSound(player, SOUND_FINISH);
							st.exitQuest(true);
						}
						else if (player.getInventory().hasItems(ROSELLA_LETTER))
							htmltext = "30414-08.htm";
						else if (player.getInventory().hasItems(CHARM_OF_GRAIN))
						{
							if (player.getInventory().getItemCount(RED_DOWN) == 5)
								htmltext = "30414-25.htm";
							else
								htmltext = "30414-09.htm";
						}
						else if (player.getInventory().hasItems(APPETIZING_APPLE))
							htmltext = "30414-15.htm";
						else if (player.getInventory().hasItems(SAP_OF_THE_MOTHER_TREE))
						{
							if (player.getInventory().getItemCount(GOLD_LEAVES) == 5)
								htmltext = "30414-26.htm";
							else
								htmltext = "30414-16.htm";
						}
						else if (player.getInventory().hasItems(IMMORTAL_LOVE))
							htmltext = "30414-19.htm";
						else if (player.getInventory().hasItems(LUCKY_POTPOURRI))
						{
							if (player.getInventory().getItemCount(AMETHYST) == 2)
								htmltext = "30414-27.htm";
							else
								htmltext = "30414-20.htm";
						}
						else
							htmltext = "30414-11.htm";
						break;
					
					case GREENIS:
						if (player.getInventory().hasItems(ROSELLA_LETTER))
							htmltext = "30157-01.htm";
						else if (player.getInventory().getItemCount(RED_DOWN) == 5)
						{
							htmltext = "30157-04.htm";
							playSound(player, SOUND_MIDDLE);
							takeItems(player, CHARM_OF_GRAIN, 1);
							takeItems(player, RED_DOWN, -1);
							giveItems(player, MAGICAL_POWERS_RUBY, 1);
						}
						else if (player.getInventory().hasItems(CHARM_OF_GRAIN))
							htmltext = "30157-03.htm";
						break;
					
					case THALIA:
						if (player.getInventory().hasItems(APPETIZING_APPLE))
							htmltext = "30371-01.htm";
						else if (player.getInventory().getItemCount(GOLD_LEAVES) == 5)
						{
							htmltext = "30371-04.htm";
							playSound(player, SOUND_MIDDLE);
							takeItems(player, GOLD_LEAVES, -1);
							takeItems(player, SAP_OF_THE_MOTHER_TREE, 1);
							giveItems(player, PURE_AQUAMARINE, 1);
						}
						else if (player.getInventory().hasItems(SAP_OF_THE_MOTHER_TREE))
							htmltext = "30371-03.htm";
						break;
					
					case NORTHWIND:
						if (player.getInventory().hasItems(IMMORTAL_LOVE))
						{
							htmltext = "30423-01.htm";
							playSound(player, SOUND_MIDDLE);
							takeItems(player, IMMORTAL_LOVE, 1);
							giveItems(player, LUCKY_POTPOURRI, 1);
						}
						else if (player.getInventory().getItemCount(AMETHYST) == 2)
						{
							htmltext = "30423-03.htm";
							playSound(player, SOUND_MIDDLE);
							takeItems(player, AMETHYST, -1);
							takeItems(player, LUCKY_POTPOURRI, 1);
							giveItems(player, NOBILITY_AMETHYST, 1);
						}
						else if (player.getInventory().hasItems(LUCKY_POTPOURRI))
							htmltext = "30423-02.htm";
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
		
		switch (npc.getNpcId())
		{
			case DRYAD_ELDER:
				if (player.getInventory().hasItems(SAP_OF_THE_MOTHER_TREE))
					dropItems(player, GOLD_LEAVES, 1, 5, 400000);
				break;
			
			case SUKAR_WERERAT_LEADER:
				if (player.getInventory().hasItems(LUCKY_POTPOURRI))
					dropItems(player, AMETHYST, 1, 2, 400000);
				break;
			
			case PINCER_SPIDER:
				if (player.getInventory().hasItems(CHARM_OF_GRAIN))
					dropItems(player, RED_DOWN, 1, 5, 700000);
				break;
		}
	}
}