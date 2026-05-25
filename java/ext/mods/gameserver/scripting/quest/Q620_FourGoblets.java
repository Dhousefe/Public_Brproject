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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.zone.type.BossZone;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q620_FourGoblets extends Quest
{
	private static final String QUEST_NAME = "Q620_FourGoblets";
	
	private static final int GHOST_OF_WIGOTH_1 = 31452;
	private static final int NAMELESS_SPIRIT = 31453;
	private static final int GHOST_OF_WIGOTH_2 = 31454;
	
	private static final int GHOST_CHAMBERLAIN_OF_ELMOREDEN = 31919;
	
	private static final int BROKEN_RELIC_PART = 7254;
	private static final int SEALED_BOX = 7255;
	
	private static final int GOBLET_OF_ALECTIA = 7256;
	private static final int GOBLET_OF_TISHAS = 7257;
	private static final int GOBLET_OF_MEKARA = 7258;
	private static final int GOBLET_OF_MORIGUL = 7259;
	
	private static final int CHAPEL_KEY = 7260;
	
	private static final int ENTRANCE_PASS_TO_THE_SEPULCHER = 7075;
	private static final int USED_GRAVE_PASS = 7261;
	
	private static final int ANTIQUE_BROOCH = 7262;
	private static final int[] RCP_REWARDS = new int[]
	{
		6881,
		6883,
		6885,
		6887,
		6891,
		6893,
		6895,
		6897,
		6899,
		7580,
	};
	
	private static final Map<Integer, Location> SEPULCHER_MANAGERS = HashMap.newHashMap(4);
	
	private static final Map<Integer, Integer> HALL_GATEKEEPER_DOORS = HashMap.newHashMap(20);
	
	private static final Map<Integer, Integer> SEALED_BOX_DROPLIST = HashMap.newHashMap(79);
	
	private static final Map<Integer, Integer> HALISHA_GOBLETS = HashMap.newHashMap(4);
	
	public Q620_FourGoblets()
	{
		super(620, "Four Goblets");
		SEPULCHER_MANAGERS.put(31921, new Location(181528, -85583, -7216));
		SEPULCHER_MANAGERS.put(31922, new Location(179849, -88990, -7216));
		SEPULCHER_MANAGERS.put(31923, new Location(173216, -86195, -7216));
		SEPULCHER_MANAGERS.put(31924, new Location(175615, -82365, -7216));
		HALL_GATEKEEPER_DOORS.put(31925, 25150012);
		HALL_GATEKEEPER_DOORS.put(31926, 25150013);
		HALL_GATEKEEPER_DOORS.put(31927, 25150014);
		HALL_GATEKEEPER_DOORS.put(31928, 25150015);
		HALL_GATEKEEPER_DOORS.put(31929, 25150016);
		HALL_GATEKEEPER_DOORS.put(31930, 25150002);
		HALL_GATEKEEPER_DOORS.put(31931, 25150003);
		HALL_GATEKEEPER_DOORS.put(31932, 25150004);
		HALL_GATEKEEPER_DOORS.put(31933, 25150005);
		HALL_GATEKEEPER_DOORS.put(31934, 25150006);
		HALL_GATEKEEPER_DOORS.put(31935, 25150032);
		HALL_GATEKEEPER_DOORS.put(31936, 25150033);
		HALL_GATEKEEPER_DOORS.put(31937, 25150034);
		HALL_GATEKEEPER_DOORS.put(31938, 25150035);
		HALL_GATEKEEPER_DOORS.put(31939, 25150036);
		HALL_GATEKEEPER_DOORS.put(31940, 25150022);
		HALL_GATEKEEPER_DOORS.put(31941, 25150023);
		HALL_GATEKEEPER_DOORS.put(31942, 25150024);
		HALL_GATEKEEPER_DOORS.put(31943, 25150025);
		HALL_GATEKEEPER_DOORS.put(31944, 25150026);
		SEALED_BOX_DROPLIST.put(18120, 1510000);
		SEALED_BOX_DROPLIST.put(18121, 1440000);
		SEALED_BOX_DROPLIST.put(18122, 1100000);
		SEALED_BOX_DROPLIST.put(18123, 1510000);
		SEALED_BOX_DROPLIST.put(18124, 1440000);
		SEALED_BOX_DROPLIST.put(18125, 1100000);
		SEALED_BOX_DROPLIST.put(18126, 1510000);
		SEALED_BOX_DROPLIST.put(18127, 1440000);
		SEALED_BOX_DROPLIST.put(18128, 1100000);
		SEALED_BOX_DROPLIST.put(18129, 1510000);
		SEALED_BOX_DROPLIST.put(18130, 1440000);
		SEALED_BOX_DROPLIST.put(18131, 1100000);
		SEALED_BOX_DROPLIST.put(18132, 1540000);
		SEALED_BOX_DROPLIST.put(18133, 1420000);
		SEALED_BOX_DROPLIST.put(18134, 1070000);
		SEALED_BOX_DROPLIST.put(18135, 1420000);
		SEALED_BOX_DROPLIST.put(18136, 1420000);
		SEALED_BOX_DROPLIST.put(18137, 1060000);
		SEALED_BOX_DROPLIST.put(18138, 1410000);
		SEALED_BOX_DROPLIST.put(18139, 1390000);
		SEALED_BOX_DROPLIST.put(18140, 1410000);
		SEALED_BOX_DROPLIST.put(18141, 900000);
		SEALED_BOX_DROPLIST.put(18142, 900000);
		SEALED_BOX_DROPLIST.put(18143, 900000);
		SEALED_BOX_DROPLIST.put(18144, 900000);
		SEALED_BOX_DROPLIST.put(18145, 760000);
		SEALED_BOX_DROPLIST.put(18146, 780000);
		SEALED_BOX_DROPLIST.put(18147, 730000);
		SEALED_BOX_DROPLIST.put(18148, 850000);
		SEALED_BOX_DROPLIST.put(18149, 750000);
		SEALED_BOX_DROPLIST.put(18166, 1080000);
		SEALED_BOX_DROPLIST.put(18167, 1070000);
		SEALED_BOX_DROPLIST.put(18168, 1100000);
		SEALED_BOX_DROPLIST.put(18169, 1060000);
		SEALED_BOX_DROPLIST.put(18170, 1070000);
		SEALED_BOX_DROPLIST.put(18171, 1110000);
		SEALED_BOX_DROPLIST.put(18172, 1060000);
		SEALED_BOX_DROPLIST.put(18173, 1170000);
		SEALED_BOX_DROPLIST.put(18174, 1450000);
		SEALED_BOX_DROPLIST.put(18175, 1100000);
		SEALED_BOX_DROPLIST.put(18176, 1170000);
		SEALED_BOX_DROPLIST.put(18177, 1450000);
		SEALED_BOX_DROPLIST.put(18178, 1100000);
		SEALED_BOX_DROPLIST.put(18179, 1170000);
		SEALED_BOX_DROPLIST.put(18180, 1450000);
		SEALED_BOX_DROPLIST.put(18181, 1100000);
		SEALED_BOX_DROPLIST.put(18182, 1170000);
		SEALED_BOX_DROPLIST.put(18183, 1450000);
		SEALED_BOX_DROPLIST.put(18184, 1100000);
		SEALED_BOX_DROPLIST.put(18185, 1460000);
		SEALED_BOX_DROPLIST.put(18186, 1470000);
		SEALED_BOX_DROPLIST.put(18187, 1420000);
		SEALED_BOX_DROPLIST.put(18188, 1070000);
		SEALED_BOX_DROPLIST.put(18189, 1420000);
		SEALED_BOX_DROPLIST.put(18190, 1420000);
		SEALED_BOX_DROPLIST.put(18191, 1060000);
		SEALED_BOX_DROPLIST.put(18192, 1410000);
		SEALED_BOX_DROPLIST.put(18193, 1390000);
		SEALED_BOX_DROPLIST.put(18194, 1420000);
		SEALED_BOX_DROPLIST.put(18195, 1080000);
		SEALED_BOX_DROPLIST.put(18212, 4500000);
		SEALED_BOX_DROPLIST.put(18213, 4500000);
		SEALED_BOX_DROPLIST.put(18214, 4500000);
		SEALED_BOX_DROPLIST.put(18215, 4500000);
		SEALED_BOX_DROPLIST.put(18216, 4500000);
		SEALED_BOX_DROPLIST.put(18217, 4500000);
		SEALED_BOX_DROPLIST.put(18218, 4500000);
		SEALED_BOX_DROPLIST.put(18219, 4500000);
		SEALED_BOX_DROPLIST.put(18220, 1470000);
		SEALED_BOX_DROPLIST.put(18221, 1510000);
		SEALED_BOX_DROPLIST.put(18222, 1430000);
		SEALED_BOX_DROPLIST.put(18223, 1070000);
		SEALED_BOX_DROPLIST.put(18224, 1440000);
		SEALED_BOX_DROPLIST.put(18225, 1430000);
		SEALED_BOX_DROPLIST.put(18226, 1060000);
		SEALED_BOX_DROPLIST.put(18227, 1820000);
		SEALED_BOX_DROPLIST.put(18228, 1360000);
		SEALED_BOX_DROPLIST.put(18229, 1410000);
		SEALED_BOX_DROPLIST.put(18230, 1580000);
		
		HALISHA_GOBLETS.put(25339, GOBLET_OF_ALECTIA);
		HALISHA_GOBLETS.put(25342, GOBLET_OF_TISHAS);
		HALISHA_GOBLETS.put(25346, GOBLET_OF_MEKARA);
		HALISHA_GOBLETS.put(25349, GOBLET_OF_MORIGUL);
		
		setItemsIds(SEALED_BOX, CHAPEL_KEY, USED_GRAVE_PASS);
		
		addQuestStart(NAMELESS_SPIRIT);
		addTalkId(NAMELESS_SPIRIT, GHOST_OF_WIGOTH_1, GHOST_OF_WIGOTH_2, GHOST_CHAMBERLAIN_OF_ELMOREDEN);
		addCreated(HALL_GATEKEEPER_DOORS.keySet());
		addCreated(SEPULCHER_MANAGERS.keySet());
		addCreated(GHOST_OF_WIGOTH_1);
		addMyDying(SEALED_BOX_DROPLIST.keySet());
		addMyDying(HALISHA_GOBLETS.keySet());
		addTalkId(SEPULCHER_MANAGERS.keySet());
		addTalkId(HALL_GATEKEEPER_DOORS.keySet());
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (npc.getNpcId() >= 31925 && npc.getNpcId() <= 31944)
		{
			npc._i_ai0 = 0;
			final Door door = DoorData.getInstance().getDoor(HALL_GATEKEEPER_DOORS.get(npc.getNpcId()));
			door.closeMe();
			
			startQuestTimerAtFixedRate("3000", npc, null, 1000, 1000);
		}
		
		if (npc.getNpcId() == GHOST_OF_WIGOTH_1)
			startQuestTimerAtFixedRate("62001", npc, null, 1000, 1000);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		final int npcId = npc.getNpcId();
		
		if (event.equalsIgnoreCase("31453-13.htm"))
		{
			st.setState(QuestStatus.STARTED);
			if (player.getInventory().hasItems(ANTIQUE_BROOCH))
			{
				st.setCond(2);
				htmltext = "31453-19.htm";
			}
			else
				st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.equalsIgnoreCase("31453-16.htm"))
		{
			if (player.getInventory().hasItems(GOBLET_OF_ALECTIA, GOBLET_OF_TISHAS, GOBLET_OF_MEKARA, GOBLET_OF_MORIGUL))
			{
				st.setCond(2);
				playSound(player, SOUND_MIDDLE);
				takeItems(player, GOBLET_OF_ALECTIA, -1);
				takeItems(player, GOBLET_OF_TISHAS, -1);
				takeItems(player, GOBLET_OF_MEKARA, -1);
				takeItems(player, GOBLET_OF_MORIGUL, -1);
				giveItems(player, ANTIQUE_BROOCH, 1);
			}
			else
				return null;
		}
		else if (event.equalsIgnoreCase("31453-18.htm"))
		{
			playSound(player, SOUND_GIVEUP);
			st.exitQuest(true);
		}
		else if (event.equalsIgnoreCase("31452-06.htm"))
		{
			takeItems(player, CHAPEL_KEY, -1);
			player.teleportTo(169592, -91006, -2912, 0);
		}
		else if (event.equalsIgnoreCase("31454-14.htm"))
		{
			if (player.getInventory().hasItems(SEALED_BOX))
			{
				takeItems(player, SEALED_BOX, 1);
				
				if (openSealedBox(player))
					htmltext = "31454-13.htm";
			}
			else
				return null;
		}
		else if (StringUtil.isDigit(event))
		{
			final int id = Integer.parseInt(event);
			if (ArraysUtil.contains(RCP_REWARDS, id) && player.getInventory().getItemCount(BROKEN_RELIC_PART) >= 1000)
			{
				takeItems(player, BROKEN_RELIC_PART, 1000);
				giveItems(player, id, 1);
				htmltext = "31454-17.htm";
			}
			else
				return null;
		}
		else if (event.equalsIgnoreCase("31919-06.htm"))
		{
			if (player.getInventory().hasItems(SEALED_BOX))
			{
				takeItems(player, SEALED_BOX, 1);
				
				if (Rnd.nextBoolean())
					htmltext = "31919-05.htm";
				else if (openSealedBox(player))
					htmltext = "31919-03.htm";
				else
					htmltext = "31919-04.htm";
			}
		}
		else if (SEPULCHER_MANAGERS.containsKey(npcId) && event.equalsIgnoreCase("Enter"))
		{
			synchronized (this)
			{
				final int i0 = Calendar.getInstance().get(Calendar.MINUTE);
				if (i0 >= 0 && i0 < 55)
					return npcId + "-02.htm";
				
				final Party party = player.getParty();
				if (party == null || party.getMembersCount() < Config.FS_PARTY_MEMBER_COUNT)
					return npcId + "-04.htm";
				
				if (!party.isLeader(player))
					return npcId + "-03.htm";
				
				for (Player member : party.getMembers())
				{
					if (!member.getInventory().hasItems(ENTRANCE_PASS_TO_THE_SEPULCHER))
						return getHtmlText(npcId + "-05.htm", player).replace("%member%", member.getName());
					
					QuestState mst = member.getQuestList().getQuestState(QUEST_NAME);
					if (mst == null || !mst.isStarted())
						return getHtmlText(npcId + "-06.htm", player).replace("%member%", member.getName());
				}
				
				if (npc._av_quest0.compareAndExchange(0, 1) == 1)
					return npcId + "-07.htm";
				
				for (Player member : party.getMembers())
				{
					if (!member.isInStrictRadius(npc, 1000))
						continue;
					
					QuestState mst = member.getQuestList().getQuestState(QUEST_NAME);
					if (mst == null)
						continue;
					
					mst.set("completed", false);
					
					takeItems(member, ENTRANCE_PASS_TO_THE_SEPULCHER, 1);
					if (!member.getInventory().hasItems(ANTIQUE_BROOCH))
						giveItems(member, USED_GRAVE_PASS, 1);
					takeItems(member, CHAPEL_KEY, -1);
					
					final Location loc = SEPULCHER_MANAGERS.get(npcId);
					ZoneManager.getInstance().getZone(loc.getX(), loc.getY(), loc.getZ(), BossZone.class).allowPlayerEntry(member, 30);
					member.teleportTo(loc, 80);
				}
				
				return npcId + "-08.htm";
			}
		}
		
		return htmltext;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final int npcId = npc.getNpcId();
		final Player player = killer.getActingPlayer();
		
		if (SEALED_BOX_DROPLIST.containsKey(npcId))
		{
			final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
			if (st != null)
				dropItems(st.getPlayer(), SEALED_BOX, 1, 0, SEALED_BOX_DROPLIST.get(npcId));
		}
		
		if (HALISHA_GOBLETS.containsKey(npcId))
		{
			final int goblet = HALISHA_GOBLETS.get(npcId);
			for (QuestState mst : getPartyMembersState(player, npc, QuestStatus.STARTED))
			{
				mst.set("completed", true);
				
				final Player member = mst.getPlayer();
				if (mst.getCond() == 2 || member.getInventory().hasItems(goblet))
					continue;
				
				giveItems(member, goblet, 1);
				playSound(member, SOUND_ITEMGET);
			}
			
			final Npc wigoth = addSpawn(GHOST_OF_WIGOTH_1, npc, false, 0, false);
			wigoth.scheduleDespawn(Math.max((54 - Calendar.getInstance().get(Calendar.MINUTE)), 1) * 60 * 1000);
		}
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
				htmltext = (player.getStatus().getLevel() >= 74) ? "31453-01.htm" : "31453-12.htm";
				break;
			
			case STARTED:
				final int npcId = npc.getNpcId();
				final int cond = st.getCond();
				switch (npcId)
				{
					case NAMELESS_SPIRIT:
						if (cond == 1)
							htmltext = player.getInventory().hasItems(GOBLET_OF_ALECTIA, GOBLET_OF_TISHAS, GOBLET_OF_MEKARA, GOBLET_OF_MORIGUL) ? "31453-15.htm" : "31453-14.htm";
						else if (cond == 2)
							htmltext = "31453-17.htm";
						break;
					
					case GHOST_OF_WIGOTH_1:
						if (cond == 1)
						{
							if (player.getInventory().hasItems(GOBLET_OF_ALECTIA, GOBLET_OF_MEKARA, GOBLET_OF_MORIGUL, GOBLET_OF_TISHAS))
								htmltext = "31452-04.htm";
							else if (Math.min(1, player.getInventory().getItemCount(GOBLET_OF_ALECTIA)) + Math.min(1, player.getInventory().getItemCount(GOBLET_OF_MEKARA)) + Math.min(1, player.getInventory().getItemCount(GOBLET_OF_MORIGUL)) + Math.min(1, player.getInventory().getItemCount(GOBLET_OF_TISHAS)) == 3)
								htmltext = "31452-02.htm";
							else
								htmltext = "31452-01.htm";
						}
						if (cond == 2)
							htmltext = "31452-05.htm";
						break;
					
					case GHOST_OF_WIGOTH_2:
						if (st.getBool("completed", false))
						{
							int index = 5;
							if (cond == 2)
								index = 9;
							else if (player.getInventory().hasItems(GOBLET_OF_ALECTIA, GOBLET_OF_MEKARA, GOBLET_OF_MORIGUL, GOBLET_OF_TISHAS))
								index = 1;
							
							if (player.getInventory().hasItems(SEALED_BOX))
								index += 1;
							if (player.getInventory().getItemCount(BROKEN_RELIC_PART) >= 1000)
								index += 2;
							
							htmltext = String.format("31454-%02d.htm", index);
						}
						break;
					
					case GHOST_CHAMBERLAIN_OF_ELMOREDEN:
						htmltext = "31919-01.htm";
						break;
					
					default:
						if (SEPULCHER_MANAGERS.containsKey(npcId))
						{
							htmltext = npcId + "-01.htm";
						}
						else if (HALL_GATEKEEPER_DOORS.containsKey(npcId))
						{
							if (player.getInventory().hasItems(CHAPEL_KEY))
							{
								takeItems(player, CHAPEL_KEY, 1);
								
								final int doorId = HALL_GATEKEEPER_DOORS.get(npcId);
								final Door door = DoorData.getInstance().getDoor(doorId);
								door.openMe();
								startQuestTimer("3001", npc, null, 15000);
								
								final int spawnX1 = npc.getTemplate().getAiParams().getInteger("SpawnX1");
								final int spawnY1 = npc.getTemplate().getAiParams().getInteger("SpawnY1");
								final int spawnZ1 = npc.getTemplate().getAiParams().getInteger("SpawnZ1");
								
								final int spawnX2 = npc.getTemplate().getAiParams().getInteger("SpawnX2");
								final int spawnY2 = npc.getTemplate().getAiParams().getInteger("SpawnY2");
								final int spawnZ2 = npc.getTemplate().getAiParams().getInteger("SpawnZ2");
								
								final int spawnX3 = npc.getTemplate().getAiParams().getInteger("SpawnX3");
								final int spawnY3 = npc.getTemplate().getAiParams().getInteger("SpawnY3");
								final int spawnZ3 = npc.getTemplate().getAiParams().getInteger("SpawnZ3");
								
								final Creature master = npc.getMaster();
								
								if (spawnX1 != 0)
								{
									if (master != null)
									{
										final int roomIDX = npc.getTemplate().getAiParams().getInteger("RoomIDX");
										((Npc) master).sendScriptEvent(1000 + roomIDX, 0, 0);
									}
									
									switch (Rnd.get(3))
									{
										case 0:
										{
											createOnePrivateEx(npc, 18244, spawnX1, spawnY1, spawnZ1, 32768, 0, false, 1, 0, 0);
											createOnePrivateEx(npc, 18245, spawnX2, spawnY2, spawnZ2, 32768, 0, false, 2, 0, 0);
											createOnePrivateEx(npc, 18246, spawnX3, spawnY2, spawnZ3, 32768, 0, false, 3, 0, 0);
											break;
										}
										case 1:
										{
											createOnePrivateEx(npc, 18244, spawnX2, spawnY2, spawnZ2, 32768, 0, false, 1, 0, 0);
											createOnePrivateEx(npc, 18245, spawnX3, spawnY3, spawnZ3, 32768, 0, false, 2, 0, 0);
											createOnePrivateEx(npc, 18246, spawnX1, spawnY1, spawnZ1, 32768, 0, false, 3, 0, 0);
											break;
										}
										case 2:
										{
											createOnePrivateEx(npc, 18244, spawnX3, spawnY3, spawnZ3, 32768, 0, false, 1, 0, 0);
											createOnePrivateEx(npc, 18245, spawnX1, spawnY1, spawnZ1, 32768, 0, false, 2, 0, 0);
											createOnePrivateEx(npc, 18246, spawnX2, spawnY2, spawnZ2, 32768, 0, false, 3, 0, 0);
											break;
										}
									}
									npc.broadcastNpcShout(NpcStringId.ID_1000502);
								}
								else
								{
									if (master != null)
									{
										((Npc) master).sendScriptEvent(1000 + npc._param1, 0, 0);
									}
								}
								
								htmltext = null;
							}
							else
								htmltext = npcId + "-01.htm";
						}
						break;
				}
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("62001"))
		{
			final int i0 = Calendar.getInstance().get(Calendar.MINUTE);
			if (i0 >= 54)
				npc.deleteMe();
		}
		if (name.equalsIgnoreCase("3000"))
		{
			final int i0 = Calendar.getInstance().get(Calendar.MINUTE);
			final int i1 = Calendar.getInstance().get(Calendar.SECOND);
			if (i0 == 55 && i1 == 0)
			{
				npc._i_ai0 = 0;
				final Door door = DoorData.getInstance().getDoor(HALL_GATEKEEPER_DOORS.get(npc.getNpcId()));
				door.closeMe();
			}
		}
		else if (name.equalsIgnoreCase("3001"))
		{
			final Door door = DoorData.getInstance().getDoor(HALL_GATEKEEPER_DOORS.get(npc.getNpcId()));
			door.closeMe();
			npc._i_ai0 = 0;
		}
		
		return null;
	}
	
	/**
	 * Gives reward to a {@link Player}.
	 * @param player : The {@link Player} to be rewarded.
	 * @return boolean : True, when there was a reward.
	 */
	private static final boolean openSealedBox(Player player)
	{
		boolean result = false;
		int group = Rnd.get(5);
		
		if (group == 0)
		{
			result = true;
			
			giveItems(player, 57, 10000);
		}
		else if (group == 1)
		{
			if (Rnd.get(1000) < 848)
			{
				result = true;
				int i = Rnd.get(1000);
				
				if (i < 43)
					giveItems(player, 1884, 42);
				else if (i < 66)
					giveItems(player, 1895, 36);
				else if (i < 184)
					giveItems(player, 1876, 4);
				else if (i < 250)
					giveItems(player, 1881, 6);
				else if (i < 287)
					giveItems(player, 5549, 8);
				else if (i < 484)
					giveItems(player, 1874, 1);
				else if (i < 681)
					giveItems(player, 1889, 1);
				else if (i < 799)
					giveItems(player, 1877, 1);
				else if (i < 902)
					giveItems(player, 1894, 1);
				else
					giveItems(player, 4043, 1);
			}
			
			if (Rnd.get(1000) < 323)
			{
				result = true;
				int i = Rnd.get(1000);
				
				if (i < 335)
					giveItems(player, 1888, 1);
				else if (i < 556)
					giveItems(player, 4040, 1);
				else if (i < 725)
					giveItems(player, 1890, 1);
				else if (i < 872)
					giveItems(player, 5550, 1);
				else if (i < 962)
					giveItems(player, 1893, 1);
				else if (i < 986)
					giveItems(player, 4046, 1);
				else
					giveItems(player, 4048, 1);
			}
		}
		else if (group == 2)
		{
			if (Rnd.get(1000) < 847)
			{
				result = true;
				int i = Rnd.get(1000);
				
				if (i < 148)
					giveItems(player, 1878, 8);
				else if (i < 175)
					giveItems(player, 1882, 24);
				else if (i < 273)
					giveItems(player, 1879, 4);
				else if (i < 322)
					giveItems(player, 1880, 6);
				else if (i < 357)
					giveItems(player, 1885, 6);
				else if (i < 554)
					giveItems(player, 1875, 1);
				else if (i < 685)
					giveItems(player, 1883, 1);
				else if (i < 803)
					giveItems(player, 5220, 1);
				else if (i < 901)
					giveItems(player, 4039, 1);
				else
					giveItems(player, 4044, 1);
			}
			
			if (Rnd.get(1000) < 251)
			{
				result = true;
				int i = Rnd.get(1000);
				
				if (i < 350)
					giveItems(player, 1887, 1);
				else if (i < 587)
					giveItems(player, 4042, 1);
				else if (i < 798)
					giveItems(player, 1886, 1);
				else if (i < 922)
					giveItems(player, 4041, 1);
				else if (i < 966)
					giveItems(player, 1892, 1);
				else if (i < 996)
					giveItems(player, 1891, 1);
				else
					giveItems(player, 4047, 1);
			}
		}
		else if (group == 3)
		{
			if (Rnd.get(1000) < 31)
			{
				result = true;
				int i = Rnd.get(1000);
				
				if (i < 223)
					giveItems(player, 730, 1);
				else if (i < 893)
					giveItems(player, 948, 1);
				else
					giveItems(player, 960, 1);
			}
			
			if (Rnd.get(1000) < 5)
			{
				result = true;
				int i = Rnd.get(1000);
				
				if (i < 202)
					giveItems(player, 729, 1);
				else if (i < 928)
					giveItems(player, 947, 1);
				else
					giveItems(player, 959, 1);
			}
		}
		else if (group == 4)
		{
			if (Rnd.get(1000) < 329)
			{
				result = true;
				int i = Rnd.get(1000);
				
				if (i < 88)
					giveItems(player, 6698, 1);
				else if (i < 185)
					giveItems(player, 6699, 1);
				else if (i < 238)
					giveItems(player, 6700, 1);
				else if (i < 262)
					giveItems(player, 6701, 1);
				else if (i < 292)
					giveItems(player, 6702, 1);
				else if (i < 356)
					giveItems(player, 6703, 1);
				else if (i < 420)
					giveItems(player, 6704, 1);
				else if (i < 482)
					giveItems(player, 6705, 1);
				else if (i < 554)
					giveItems(player, 6706, 1);
				else if (i < 576)
					giveItems(player, 6707, 1);
				else if (i < 640)
					giveItems(player, 6708, 1);
				else if (i < 704)
					giveItems(player, 6709, 1);
				else if (i < 777)
					giveItems(player, 6710, 1);
				else if (i < 799)
					giveItems(player, 6711, 1);
				else if (i < 863)
					giveItems(player, 6712, 1);
				else if (i < 927)
					giveItems(player, 6713, 1);
				else
					giveItems(player, 6714, 1);
			}
			
			if (Rnd.get(1000) < 54)
			{
				result = true;
				int i = Rnd.get(1000);
				
				if (i < 100)
					giveItems(player, 6688, 1);
				else if (i < 198)
					giveItems(player, 6689, 1);
				else if (i < 298)
					giveItems(player, 6690, 1);
				else if (i < 398)
					giveItems(player, 6691, 1);
				else if (i < 499)
					giveItems(player, 7579, 1);
				else if (i < 601)
					giveItems(player, 6693, 1);
				else if (i < 703)
					giveItems(player, 6694, 1);
				else if (i < 801)
					giveItems(player, 6695, 1);
				else if (i < 902)
					giveItems(player, 6696, 1);
				else
					giveItems(player, 6697, 1);
			}
		}
		
		return result;
	}
}