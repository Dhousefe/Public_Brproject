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
package ext.mods.gameserver.scripting.script.teleport;

import java.util.HashMap;
import java.util.Map;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.scripting.Quest;

public class MonsterRaceTrack extends Quest
{
	private static final int RACE_MANAGER = 30995;
	
	private static final Map<Integer, Location> RETURN_LOCATIONS = HashMap.newHashMap(12);
	
	public MonsterRaceTrack()
	{
		super(-1, "teleport");
		
		RETURN_LOCATIONS.put(30059, new Location(15670, 142983, -2705));
		RETURN_LOCATIONS.put(30080, new Location(83400, 147943, -3404));
		RETURN_LOCATIONS.put(30177, new Location(82956, 53162, -1495));
		RETURN_LOCATIONS.put(30233, new Location(116819, 76994, -2714));
		RETURN_LOCATIONS.put(30256, new Location(-12672, 122776, -3116));
		RETURN_LOCATIONS.put(30320, new Location(-80826, 149775, -3043));
		RETURN_LOCATIONS.put(30848, new Location(146331, 25762, -2018));
		RETURN_LOCATIONS.put(30899, new Location(111409, 219364, -3545));
		RETURN_LOCATIONS.put(31210, new Location(12882, 181053, -3560));
		RETURN_LOCATIONS.put(31275, new Location(147930, -55281, -2728));
		RETURN_LOCATIONS.put(31320, new Location(43835, -47749, -792));
		RETURN_LOCATIONS.put(31964, new Location(87386, -143246, -1293));
		
		addTalkId(RACE_MANAGER, 30059, 30080, 30177, 30233, 30256, 30320, 30848, 30899, 31210, 31275, 31320, 31964);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (npc.getNpcId() == RACE_MANAGER)
		{
			int npcId = player.getMemos().getInteger("MonsterRaceTrack_Npc", -1);
			if (npcId >= 0)
			{
				player.teleportTo(RETURN_LOCATIONS.get(npcId), 0);
				player.getMemos().unset("MonsterRaceTrack_Npc");
			}
		}
		else if (RETURN_LOCATIONS.containsKey(npc.getNpcId()))
		{
			player.teleportTo(12661, 181687, -3560, 0);
			player.getMemos().set("MonsterRaceTrack_Npc", npc.getNpcId());
		}
		
		return null;
	}
}