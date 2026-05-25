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
package ext.mods.gameserver.scripting.script.ai.siegablehall;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior.WarriorAggressive.WarriorAggressive;

public class AzitWateringCrazyYeti extends WarriorAggressive
{
	public AzitWateringCrazyYeti()
	{
		super("ai/siegeablehall");
	}
	
	public AzitWateringCrazyYeti(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35592
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.broadcastNpcShout(NpcStringId.ID_1010627);
		
		super.onCreated(npc);
	}
}