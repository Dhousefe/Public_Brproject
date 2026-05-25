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

public class HeavyMedal extends Events
{
	private static boolean ACTIVE = false;
	
	private List<Npc> _npclist;
	
	private final static int CAT_ROY = 31228;
	private final static int CAT_WINNIE = 31229;
	private static final int LOUIE_THE_CAT = 31230;
	
	private final static int GLITTERING_MEDAL = 6393;
	
	private final static int WIN_CHANCE = 50;
	
	private final static int[] MEDALS =
	{
		5,
		10,
		20,
		40
	};
	
	private final static int[] BADGES =
	{
		6399,
		6400,
		6401,
		6402
	};
	
	private void addDrop()
	{
		final EventsInfo event = EventsData.getInstance().getEventsData(getName());
		
		EventsDropManager.getInstance().addMedalsRule(getName(), RuleType.ALL_NPC, event.items());
	}
	
	private void removeDrop()
	{
		EventsDropManager.getInstance().removeMedalsRules(getName());
	}
	
	private static final SpawnLocation[] _spawns_winnie =
	{
		new SpawnLocation(-44342, -113726, -240, 0),
		new SpawnLocation(-44671, -115437, -240, 22500),
		new SpawnLocation(-13073, 122841, -3117, 0),
		new SpawnLocation(-13972, 121893, -2988, 32768),
		new SpawnLocation(-14843, 123710, -3117, 8192),
		new SpawnLocation(11327, 15682, -4584, 25000),
		new SpawnLocation(11243, 17712, -4574, 57344),
		new SpawnLocation(18154, 145192, -3054, 7400),
		new SpawnLocation(19214, 144327, -3097, 32768),
		new SpawnLocation(19459, 145775, -3086, 48000),
		new SpawnLocation(17418, 170217, -3507, 36000),
		new SpawnLocation(47146, 49382, -3059, 32000),
		new SpawnLocation(44157, 50827, -3059, 57344),
		new SpawnLocation(79798, 55629, -1560, 0),
		new SpawnLocation(83328, 55769, -1525, 32768),
		new SpawnLocation(80986, 54452, -1525, 32768),
		new SpawnLocation(83671, 149218, -3400, 32768),
		new SpawnLocation(82277, 148564, -3467, 0),
		new SpawnLocation(81620, 148689, -3464, 32768),
		new SpawnLocation(81691, 145610, -3467, 32768),
		new SpawnLocation(114719, -178742, -821, 0),
		new SpawnLocation(115708, -182422, -1449, 0),
		new SpawnLocation(-80731, 151152, -3043, 28672),
		new SpawnLocation(-84097, 150171, -3129, 4096),
		new SpawnLocation(-82678, 151666, -3129, 49152),
		new SpawnLocation(117459, 76664, -2695, 38000),
		new SpawnLocation(115936, 76488, -2711, 59000),
		new SpawnLocation(119576, 76940, -2275, 40960),
		new SpawnLocation(-84516, 243015, -3730, 34000),
		new SpawnLocation(-86031, 243153, -3730, 60000),
		new SpawnLocation(147124, 27401, -2192, 40960),
		new SpawnLocation(147985, 25664, -2000, 16384),
		new SpawnLocation(107899, 218149, -3675, 0),
		new SpawnLocation(114920, 220080, -3632, 32768),
		new SpawnLocation(147924, -58052, -2979, 49000),
		new SpawnLocation(147285, -56461, -2776, 11500),
		new SpawnLocation(44176, -48688, -800, 33000),
		new SpawnLocation(44294, -47642, -792, 50000)
	};
	
	private static final SpawnLocation[] _spawns_roy =
	{
		new SpawnLocation(-44337, -113669, -224, 0),
		new SpawnLocation(-44628, -115409, -240, 22500),
		new SpawnLocation(-13073, 122801, -3117, 0),
		new SpawnLocation(-13949, 121934, -2988, 32768),
		new SpawnLocation(-14786, 123686, -3117, 8192),
		new SpawnLocation(11281, 15652, -4584, 25000),
		new SpawnLocation(11303, 17732, -4574, 57344),
		new SpawnLocation(18178, 145149, -3054, 7400),
		new SpawnLocation(19208, 144380, -3097, 32768),
		new SpawnLocation(19508, 145775, -3086, 48000),
		new SpawnLocation(17396, 170259, -3507, 36000),
		new SpawnLocation(47151, 49436, -3059, 32000),
		new SpawnLocation(44122, 50784, -3059, 57344),
		new SpawnLocation(79806, 55570, -1560, 0),
		new SpawnLocation(83328, 55824, -1525, 32768),
		new SpawnLocation(80986, 54504, -1525, 32768),
		new SpawnLocation(83682, 149267, -3400, 32768),
		new SpawnLocation(82277, 148598, -3467, 0),
		new SpawnLocation(81621, 148725, -3467, 32768),
		new SpawnLocation(81680, 145656, -3467, 32768),
		new SpawnLocation(114733, -178691, -821, 0),
		new SpawnLocation(115708, -182362, -1449, 0),
		new SpawnLocation(-80789, 151073, -3043, 28672),
		new SpawnLocation(-84049, 150176, -3129, 4096),
		new SpawnLocation(-82623, 151666, -3129, 49152),
		new SpawnLocation(117498, 76630, -2695, 38000),
		new SpawnLocation(115914, 76449, -2711, 59000),
		new SpawnLocation(119536, 76988, -2275, 40960),
		new SpawnLocation(-84516, 242971, -3730, 34000),
		new SpawnLocation(-86060, 243225, -3728, 65000),
		new SpawnLocation(147184, 27405, -2192, 45960),
		new SpawnLocation(147920, 25664, -2000, 16384),
		new SpawnLocation(111776, 221104, -3543, 16384),
		new SpawnLocation(107904, 218096, -3675, 0),
		new SpawnLocation(114920, 220020, -3632, 32768),
		new SpawnLocation(147888, -58048, -2979, 49000),
		new SpawnLocation(147262, -56450, -2776, 11500),
		new SpawnLocation(44176, -48732, -800, 33000),
		new SpawnLocation(44319, -47640, -792, 50000)
	};
	
	private static final SpawnLocation[] _spawns_louie =
	{
		new SpawnLocation(-46536, -117242, -224, 0)
	};
	
	public HeavyMedal()
	{
		addQuestStart(CAT_ROY, CAT_WINNIE);
		addTalkId(CAT_ROY, CAT_WINNIE);
		addFirstTalkId(CAT_ROY, CAT_WINNIE);
	}
	
	@Override
	public boolean eventStart(int priority)
	{
		if (ACTIVE)
			return false;
		
		ACTIVE = true;
		
		_npclist = new ArrayList<>();
		
		for (SpawnLocation loc : _spawns_roy)
			recordSpawn(CAT_ROY, loc);
		
		for (SpawnLocation loc : _spawns_winnie)
			recordSpawn(CAT_WINNIE, loc);
		
		for (SpawnLocation loc : _spawns_louie)
			recordSpawn(LOUIE_THE_CAT, loc);
		
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
		
		int level = checkLevel(player);
		
		if (event.equalsIgnoreCase("game"))
			htmltext = player.getInventory().getItemCount(GLITTERING_MEDAL) < MEDALS[level] ? "31229-no.htm" : "31229-game.htm";
		else if (event.equalsIgnoreCase("heads") || event.equalsIgnoreCase("tails"))
		{
			if (player.getInventory().getItemCount(GLITTERING_MEDAL) < MEDALS[level])
				htmltext = "31229-" + event.toLowerCase() + "-10.htm";
			else
			{
				takeItems(player, GLITTERING_MEDAL, MEDALS[level]);
				
				if (Rnd.get(100) > WIN_CHANCE)
					level = 0;
				else
				{
					if (level > 0)
						takeItems(player, BADGES[level - 1], -1);
					
					giveItems(player, BADGES[level], 1);
					playSound(player, SOUND_ITEMGET);
					level++;
				}
				htmltext = "31229-" + event.toLowerCase() + "-" + String.valueOf(level) + ".htm";
			}
		}
		else if (event.equalsIgnoreCase("talk"))
			htmltext = String.valueOf(npc.getNpcId()) + "-lvl-" + String.valueOf(level) + ".htm";
		
		return htmltext;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		if (player.getQuestList().getQuestState(getName()) == null)
			newQuestState(player);
		
		return npc.getNpcId() + ".htm";
	}
	
	public int checkLevel(Player player)
	{
		int level = 0;
		if (player == null)
			return 0;
		
		if (player.getInventory().hasItems(6402))
			level = 4;
		else if (player.getInventory().hasItems(6401))
			level = 3;
		else if (player.getInventory().hasItems(6400))
			level = 2;
		else if (player.getInventory().hasItems(6399))
			level = 1;
		
		return level;
	}
	
	private void recordSpawn(int npcId, SpawnLocation loc)
	{
		Npc npc = addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 0, false);
		if (npc != null)
			_npclist.add(npc);
	}
}