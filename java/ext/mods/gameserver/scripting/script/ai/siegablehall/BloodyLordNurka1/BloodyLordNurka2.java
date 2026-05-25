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
package ext.mods.gameserver.scripting.script.ai.siegablehall.BloodyLordNurka1;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;

public class BloodyLordNurka2 extends BloodyLordNurka1
{
	public BloodyLordNurka2()
	{
		super("ai/siegeablehall/BloodyLordNurka1");
	}
	
	public BloodyLordNurka2(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35375
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.getSpawn().instantTeleportInMyTerritory(51952, 111060, -1970, 200);
		startQuestTimerAtFixedRate("2001", npc, null, 10000, 10000);
		npc._flag = 0;
		createPrivates(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			final long timeAfterSiegeStart = (npc.getSiegableHall().getSiegeDate().getTimeInMillis() - System.currentTimeMillis()) / 1000;
			if (timeAfterSiegeStart >= 1800)
			{
				if (timeAfterSiegeStart < 3600)
				{
					if (npc._flag < 1)
					{
						npc.getAI().addMoveToDesire(new Location(45802, 109981, -1970), 100000000);
						npc._flag = 1;
					}
				}
				else if (npc._flag < 2)
				{
					npc.getAI().addMoveToDesire(new Location(44525, 108867, -2020), 100000000);
					npc._flag = 2;
				}
			}
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller != called)
			caller.scheduleRespawn((caller.getSpawn().getRespawnDelay() * 1000));
	}
}