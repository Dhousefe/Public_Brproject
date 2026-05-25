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

import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q104_SpiritOfMirrors extends Quest
{
	private static final String QUEST_NAME = "Q104_SpiritOfMirrors";
	
	private static final int GALLINS_OAK_WAND = 748;
	private static final int WAND_SPIRITBOUND_1 = 1135;
	private static final int WAND_SPIRITBOUND_2 = 1136;
	private static final int WAND_SPIRITBOUND_3 = 1137;
	
	private static final int WAND_OF_ADEPT = 747;
	private static final int LESSER_HEALING_POT = 1060;
	private static final int SOULSHOT_NO_GRADE = 1835;
	private static final int SPIRITSHOT_NO_GRADE = 2509;
	private static final int ECHO_BATTLE = 4412;
	private static final int ECHO_LOVE = 4413;
	private static final int ECHO_SOLITUDE = 4414;
	private static final int ECHO_FEAST = 4415;
	private static final int ECHO_CELEBRATION = 4416;
	
	private static final int GALLINT = 30017;
	private static final int ARNOLD = 30041;
	private static final int JOHNSTONE = 30043;
	private static final int KENYOS = 30045;
	
	public Q104_SpiritOfMirrors()
	{
		super(104, "Spirit of Mirrors");
		
		setItemsIds(GALLINS_OAK_WAND, WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3);
		
		addQuestStart(GALLINT);
		addTalkId(GALLINT, ARNOLD, JOHNSTONE, KENYOS);
		
		addMyDying(27003, 27004, 27005);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.equalsIgnoreCase("30017-03.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
			giveItems(player, GALLINS_OAK_WAND, 1);
			giveItems(player, GALLINS_OAK_WAND, 1);
			giveItems(player, GALLINS_OAK_WAND, 1);
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
				if (player.getRace() != ClassRace.HUMAN)
					htmltext = "30017-00.htm";
				else if (player.getStatus().getLevel() < 10)
					htmltext = "30017-01.htm";
				else
					htmltext = "30017-02.htm";
				break;
			
			case STARTED:
				int cond = st.getCond();
				switch (npc.getNpcId())
				{
					case GALLINT:
						if (cond == 1 || cond == 2)
							htmltext = "30017-04.htm";
						else if (cond == 3)
						{
							htmltext = "30017-05.htm";
							
							takeItems(player, WAND_SPIRITBOUND_1, -1);
							takeItems(player, WAND_SPIRITBOUND_2, -1);
							takeItems(player, WAND_SPIRITBOUND_3, -1);
							
							giveItems(player, WAND_OF_ADEPT, 1);
							
							if (player.isMageClass())
								rewardItems(player, SPIRITSHOT_NO_GRADE, 500);
							else
								rewardItems(player, SOULSHOT_NO_GRADE, 1000);
							
							rewardNewbieShots(player, 0, 3000);
							rewardItems(player, LESSER_HEALING_POT, 100);
							rewardItems(player, ECHO_BATTLE, 10);
							rewardItems(player, ECHO_LOVE, 10);
							rewardItems(player, ECHO_SOLITUDE, 10);
							rewardItems(player, ECHO_FEAST, 10);
							rewardItems(player, ECHO_CELEBRATION, 10);
							
							player.broadcastPacket(new SocialAction(player, 3));
							playSound(player, SOUND_FINISH);
							st.exitQuest(false);
						}
						break;
					
					case KENYOS, JOHNSTONE, ARNOLD:
						htmltext = npc.getNpcId() + "-01.htm";
						if (cond == 1)
						{
							st.setCond(2);
							playSound(player, SOUND_MIDDLE);
						}
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
		
		final QuestState st = checkPlayerState(player, npc, QuestStatus.STARTED);
		if (st == null)
			return;
		
		if (player.getInventory().getItemIdFrom(Paperdoll.RHAND) == GALLINS_OAK_WAND)
		{
			switch (npc.getNpcId())
			{
				case 27003:
					if (!player.getInventory().hasItems(WAND_SPIRITBOUND_1))
					{
						takeItems(player, GALLINS_OAK_WAND, 1);
						giveItems(player, WAND_SPIRITBOUND_1, 1);
						
						if (player.getInventory().hasItems(WAND_SPIRITBOUND_2, WAND_SPIRITBOUND_3))
						{
							st.setCond(3);
							playSound(player, SOUND_MIDDLE);
						}
						else
							playSound(player, SOUND_ITEMGET);
					}
					break;
				
				case 27004:
					if (!player.getInventory().hasItems(WAND_SPIRITBOUND_2))
					{
						takeItems(player, GALLINS_OAK_WAND, 1);
						giveItems(player, WAND_SPIRITBOUND_2, 1);
						
						if (player.getInventory().hasItems(WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_3))
						{
							st.setCond(3);
							playSound(player, SOUND_MIDDLE);
						}
						else
							playSound(player, SOUND_ITEMGET);
					}
					break;
				
				case 27005:
					if (!player.getInventory().hasItems(WAND_SPIRITBOUND_3))
					{
						takeItems(player, GALLINS_OAK_WAND, 1);
						giveItems(player, WAND_SPIRITBOUND_3, 1);
						
						if (player.getInventory().hasItems(WAND_SPIRITBOUND_1, WAND_SPIRITBOUND_2))
						{
							st.setCond(3);
							playSound(player, SOUND_MIDDLE);
						}
						else
							playSound(player, SOUND_ITEMGET);
					}
					break;
			}
		}
	}
}