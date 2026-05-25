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
package ext.mods.gameserver.scripting.script.ai.individual.Guard;

import ext.mods.gameserver.model.actor.Npc;

public class GuardStand extends Guard
{
	public GuardStand()
	{
		super("ai/individual/Guard");
	}
	
	public GuardStand(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		30042,
		30043,
		30046,
		30074,
		30076,
		30124,
		30125,
		30126,
		30128,
		30198,
		30216,
		30219,
		30221,
		30285,
		30333,
		30337,
		30347,
		30348,
		30357,
		30382,
		30384,
		30386,
		30430,
		30431,
		30432,
		30433,
		30452,
		30466,
		30543,
		30544,
		30545,
		30548,
		30580,
		30582,
		30583,
		30584,
		30708,
		30710,
		30711,
		30712,
		30714,
		30724,
		30725,
		30726,
		30872,
		30874,
		30876,
		30882,
		30884,
		30886,
		30888,
		30918,
		30920,
		30922,
		31293,
		31294,
		31295,
		31296,
		31297,
		31298,
		31299,
		31341,
		31342,
		31343,
		31344,
		31345,
		31346,
		31347,
		31677,
		31678,
		31681,
		31682,
		31985,
		31988,
		30039,
		30071,
		30121,
		30196,
		30224,
		30284,
		30332,
		30707,
		30723,
		30870,
		30880,
		30916,
		31292,
		31340
	};
	
	@Override
	public void onNoDesire(Npc npc)
	{
		npc.getAI().addMoveToDesire(npc.getSpawnLocation(), 30);
	}
	
	@Override
	public void onMoveToFinished(Npc npc, int x, int y, int z)
	{
		if (!npc.getSpawnLocation().equals(x, y, z))
			npc.getAI().addMoveToDesire(npc.getSpawnLocation(), 30);
		else
			npc.getAI().addDoNothingDesire(40, 30);
	}
}