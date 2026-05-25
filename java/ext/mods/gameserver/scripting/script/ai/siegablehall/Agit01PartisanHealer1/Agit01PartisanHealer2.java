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
package ext.mods.gameserver.scripting.script.ai.siegablehall.Agit01PartisanHealer1;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;

/**
 * TODO We must add AI application over spawn to handle it properly. For now, we use _customPlaceholder to simulate such behavior.
 */
public class Agit01PartisanHealer2 extends Agit01PartisanHealer1
{
	private static int _customPlaceholder;
	
	public Agit01PartisanHealer2()
	{
		super("ai/siegeablehall/Agit01PartisanHealer1");
	}
	
	public Agit01PartisanHealer2(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35376
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		if (_customPlaceholder == 0)
			_customPlaceholder = npc.getObjectId();
		
		npc._i_ai0 = 0;
		
		startQuestTimerAtFixedRate("2001", npc, null, 10000, 10000);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (_customPlaceholder == npc.getObjectId())
			_customPlaceholder = 0;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2001"))
		{
			if (npc.hasMaster())
			{
				if (npc.getMaster()._flag == 0)
				{
					if (npc._i_ai0 < 1)
					{
						npc.getAI().addMoveToDesire(new Location(npc.getMaster().getX() + ((_customPlaceholder == npc.getObjectId()) ? 20 : -20), npc.getMaster().getY(), npc.getMaster().getZ()), 100000000);
						npc._i_ai0 = 1;
					}
				}
				else if (npc.getMaster()._flag == 1)
				{
					if (npc._i_ai0 < 2)
					{
						npc.getAI().addMoveToDesire(new Location(((_customPlaceholder == npc.getObjectId()) ? 45822 : 45782), 109981, -1970), 100000000);
						npc._i_ai0 = 2;
					}
				}
				else if (npc._i_ai0 < 3)
				{
					npc.getAI().addMoveToDesire(new Location(((_customPlaceholder == npc.getObjectId()) ? 44545 : 44505), 108867, -2020), 100000000);
					npc._i_ai0 = 3;
				}
			}
		}
		
		return super.onTimer(name, npc, player);
	}
}