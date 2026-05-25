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
package ext.mods.gameserver.scripting.script.event;

import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.custom.data.EventsData;
import ext.mods.gameserver.data.manager.EventsDropManager;
import ext.mods.gameserver.data.manager.EventsDropManager.RuleType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.records.custom.EventsInfo;
import ext.mods.gameserver.scripting.QuestState;

public class L2Day extends Events
{
	private static boolean ACTIVE = false;
	
	private List<Npc> _npclist;
	
	private final static int LETTER_A = 3875;
	private final static int LETTER_C = 3876;
	private final static int LETTER_E = 3877;
	private final static int LETTER_F = 3878;
	private final static int LETTER_G = 3879;
	private final static int LETTER_I = 3881;
	private final static int LETTER_L = 3882;
	private final static int LETTER_N = 3883;
	private final static int LETTER_O = 3884;
	private final static int LETTER_S = 3886;
	private final static int LETTER_T = 3887;
	private final static int LETTER_II = 3888;
	
	protected final int EVENT_NPC = 31854;
	
	private static final SpawnLocation[] _coords =
	{
		new SpawnLocation(-84415, 244813, -3737, 57343),
		new SpawnLocation(-84021, 243049, -3734, 2902),
		new SpawnLocation(9924, 16328, -4578, 63027),
		new SpawnLocation(11548, 17597, -4589, 16000),
		new SpawnLocation(115087, -178360, -890, 0),
		new SpawnLocation(116198, -182696, -1513, 63635),
		new SpawnLocation(46907, 50860, -3000, 7497),
		new SpawnLocation(45542, 48348, -3064, 49816),
		new SpawnLocation(-45273, -112764, -244, 0),
		new SpawnLocation(-45368, -114106, -244, 15612),
		new SpawnLocation(-81028, 150037, -3048, 62180),
		new SpawnLocation(-83158, 150994, -3133, 65142),
		new SpawnLocation(-13726, 122116, -2993, 16383),
		new SpawnLocation(-14133, 123862, -3121, 39765),
		new SpawnLocation(16110, 142851, -2714, 18106),
		new SpawnLocation(17273, 144997, -3039, 22165),
		new SpawnLocation(82143, 148614, -3475, 612),
		new SpawnLocation(83077, 149328, -3473, 32199),
		new SpawnLocation(81757, 146487, -3541, 32767),
		new SpawnLocation(111003, 218924, -3547, 14661),
		new SpawnLocation(108423, 221873, -3602, 48655),
		new SpawnLocation(81986, 53725, -1500, 65051),
		new SpawnLocation(81081, 56116, -1564, 32767),
		new SpawnLocation(115883, 76384, -2717, 64055),
		new SpawnLocation(117350, 76703, -2699, 45557),
		new SpawnLocation(147198, 25615, -2017, 14613),
		new SpawnLocation(148558, 26803, -2209, 32024),
		new SpawnLocation(148210, -55784, -2785, 60699),
		new SpawnLocation(147415, -55432, -2741, 47429),
		new SpawnLocation(43962, -47707, -801, 48698),
		new SpawnLocation(43165, -48465, -801, 17559),
		new SpawnLocation(86863, -142917, -1345, 27497),
		new SpawnLocation(87791, -142236, -1348, 43140)
	};
	
	private void addDrop()
	{
		final EventsInfo event = EventsData.getInstance().getEventsData(getName());
		
		EventsDropManager.getInstance().addL2DayRule(getName(), RuleType.ALL_NPC, event.items());
	}
	
	private void removeDrop()
	{
		EventsDropManager.getInstance().removeL2DayRules(getName());
	}
	
	public L2Day()
	{
		addQuestStart(EVENT_NPC);
		addFirstTalkId(EVENT_NPC);
		addTalkId(EVENT_NPC);
	}
	
	@Override
	public boolean eventStart(int priority)
	{
		if (ACTIVE)
			return false;
		
		ACTIVE = true;
		
		_npclist = new ArrayList<>();
		
		for (SpawnLocation loc : _coords)
			recordSpawn(EVENT_NPC, loc);
		
		eventStatusStart(priority);
		addDrop();
		
		World.announceToOnlinePlayers(10_159, getName());
		return true;
	}
	
	@Override
	public boolean eventStop()
	{
		if (!ACTIVE)
			return false;
		
		ACTIVE = false;
		
		if (!_npclist.isEmpty())
			_npclist.forEach(npc -> npc.deleteMe());
		
		_npclist.clear();
		
		eventStatusStop();
		removeDrop();
		
		World.announceToOnlinePlayers(10_160, getName());
		return true;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		
		int i0 = Rnd.get(100);
		int i1 = Rnd.get(100000);
		int i2 = Rnd.get(100);
		
		if (event.equalsIgnoreCase("LINEAGEII"))
		{
			if ((player.getInventory().getItemCount(LETTER_L) >= 1) && (player.getInventory().getItemCount(LETTER_I) >= 1) && (player.getInventory().getItemCount(LETTER_N) >= 1) && (player.getInventory().getItemCount(LETTER_E) >= 2) && (player.getInventory().getItemCount(LETTER_A) >= 1) && (player.getInventory().getItemCount(LETTER_G) >= 1) && (player.getInventory().getItemCount(LETTER_II) >= 1))
			{
				takeItems(player, LETTER_L, 1);
				takeItems(player, LETTER_I, 1);
				takeItems(player, LETTER_N, 1);
				takeItems(player, LETTER_E, 2);
				takeItems(player, LETTER_A, 1);
				takeItems(player, LETTER_G, 1);
				takeItems(player, LETTER_II, 1);
				
				if (i0 < 600)
				{
					if (i1 < 8)
						giveItems(player, 6660, 1);
					else if (i1 < 16)
						giveItems(player, 6662, 1);
					else if (i1 < 1016)
						giveItems(player, 729, 1);
					else if (i1 < 3016)
						giveItems(player, 947, 2);
					else if (i1 < 10016)
						giveItems(player, 3959, 3);
					else if (i1 < 35016)
						giveItems(player, 3958, 3);
					else
					{
						if (i2 < 10)
							giveItems(player, 3926, 3);
						else if (i2 < 20)
							giveItems(player, 3927, 3);
						else if (i2 < 30)
							giveItems(player, 3928, 3);
						else if (i2 < 40)
							giveItems(player, 3929, 3);
						else if (i2 < 50)
							giveItems(player, 3935, 3);
						else if (i2 < 60)
							giveItems(player, 3931, 3);
						else if (i2 < 70)
							giveItems(player, 3932, 3);
						else if (i2 < 80)
							giveItems(player, 3933, 3);
						else if (i2 < 90)
							giveItems(player, 3930, 3);
						else if (i2 < 100)
							giveItems(player, 3934, 3);
					}
				}
				
				return "31854.htm";
			}
			htmltext = "31854-02.htm";
		}
		else if (event.equalsIgnoreCase("NCSOFT"))
		{
			if ((player.getInventory().getItemCount(LETTER_N) >= 1) && (player.getInventory().getItemCount(LETTER_C) >= 1) && (player.getInventory().getItemCount(LETTER_S) >= 1) && (player.getInventory().getItemCount(LETTER_O) >= 1) && (player.getInventory().getItemCount(LETTER_F) >= 1) && (player.getInventory().getItemCount(LETTER_T) >= 1))
			{
				takeItems(player, LETTER_N, 1);
				takeItems(player, LETTER_C, 1);
				takeItems(player, LETTER_S, 1);
				takeItems(player, LETTER_O, 1);
				takeItems(player, LETTER_F, 1);
				takeItems(player, LETTER_T, 1);
				
				if (i0 < 600)
				{
					if (i1 < 8)
						giveItems(player, 6660, 1);
					else if (i1 < 16)
						giveItems(player, 6662, 1);
					else if (i1 < 1016)
						giveItems(player, 947, 2);
					else if (i1 < 3016)
						giveItems(player, 951, 3);
					else if (i1 < 10016)
						giveItems(player, 3959, 2);
					else if (i1 < 35016)
						giveItems(player, 3958, 2);
					else
					{
						if (i2 < 11)
							giveItems(player, 3926, 2);
						else if (i2 < 22)
							giveItems(player, 3927, 2);
						else if (i2 < 33)
							giveItems(player, 3928, 2);
						else if (i2 < 44)
							giveItems(player, 3929, 2);
						else if (i2 < 55)
							giveItems(player, 3935, 2);
						else if (i2 < 66)
							giveItems(player, 3931, 2);
						else if (i2 < 77)
							giveItems(player, 3932, 2);
						else if (i2 < 88)
							giveItems(player, 3933, 2);
						else if (i2 < 100)
							giveItems(player, 3930, 2);
					}
				}
				
				return "31854.htm";
			}
			htmltext = "31854-02.htm";
		}
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestList().getQuestState(getName());
		if (st == null)
			st = newQuestState(player);
		
		return "31854.htm";
	}
	
	private void recordSpawn(int npcId, SpawnLocation loc)
	{
		Npc npc = addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 0, false);
		if (npc != null)
			_npclist.add(npc);
	}
}