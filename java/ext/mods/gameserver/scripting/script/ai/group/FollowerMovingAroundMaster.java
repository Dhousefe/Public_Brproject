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
package ext.mods.gameserver.scripting.script.ai.group;

import java.util.HashMap;
import java.util.Map;

import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.scripting.Quest;

/**
 * This script is used by the followers moving around their master.
 */
public class FollowerMovingAroundMaster extends Quest
{
	private static final Map<Integer, Integer> MASTERS = HashMap.newHashMap(19);
	private static final Map<Integer, Npc> FOLLOWERS = HashMap.newHashMap(19);
	
	public FollowerMovingAroundMaster()
	{
		super(-1, "ai/group");
		
		MASTERS.put(30731, 31202);
		MASTERS.put(30827, 31203);
		MASTERS.put(30828, 31204);
		MASTERS.put(30829, 31205);
		MASTERS.put(30830, 31206);
		MASTERS.put(30831, 31207);
		MASTERS.put(30869, 31208);
		MASTERS.put(31067, 31209);
		MASTERS.put(31265, 31758);
		MASTERS.put(31309, 31266);
		MASTERS.put(31592, 31593);
		MASTERS.put(31605, 31606);
		MASTERS.put(31608, 31609);
		MASTERS.put(31614, 31629);
		MASTERS.put(31624, 31625);
		MASTERS.put(31701, 31703);
		MASTERS.put(31702, 31704);
		MASTERS.put(31954, 31955);
		MASTERS.put(32070, 32071);
		
		addEventIds(MASTERS.keySet(), EventHandler.CREATED);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("5001"))
		{
			if (!npc.isAISleeping())
			{
				final Npc master = FOLLOWERS.get(npc.getNpcId());
				
				final Location loc = master.getPosition().clone();
				loc.addRandomOffset(50);
				
				npc.setWalkOrRun(true);
				npc.getAI().addMoveToDesire(loc, 30);
			}
		}
		return null;
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		final int followerId = MASTERS.get(npc.getNpcId());
		
		final Npc follower = addSpawn(followerId, npc, false, 0, false);
		
		FOLLOWERS.put(followerId, npc);
		
		startQuestTimerAtFixedRate("5001", follower, null, 0, 5000);
		
		super.onCreated(npc);
	}
}