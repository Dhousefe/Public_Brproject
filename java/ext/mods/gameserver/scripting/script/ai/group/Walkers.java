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

import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.scripting.Quest;

public class Walkers extends Quest
{
	private static final int[] WALKING_NPCS =
	{
		31357,
		31358,
		31359,
		31360,
		31362,
		31364,
		31365,
		31525,
		32072,
		32128
	};
	
	public Walkers()
	{
		super(-1, "ai/group/Walker");
		
		addCreated(31356, 31357, 31358, 31359, 31360, 31361, 31362, 31363, 31364, 31365, 31525, 31705, 32070, 32072, 32128);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (ArraysUtil.contains(WALKING_NPCS, npc.getNpcId()))
			npc.setWalkOrRun(false);
		
		npc.getAI().addMoveRouteDesire(npc.getTemplate().getAlias(), 1000000);
	}
}