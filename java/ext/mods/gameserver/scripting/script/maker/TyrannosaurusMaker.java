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
package ext.mods.gameserver.scripting.script.maker;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;

public class TyrannosaurusMaker extends VelociraptorMaker
{
	public TyrannosaurusMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		maker.getMakerMemo().set("i_ai0", 0);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (maker.getSpawnedCount() == 0)
			ThreadPool.schedule(() -> onTimer("1002", maker), 180000);
	}
	
	@Override
	public void onTimer(String name, NpcMaker maker)
	{
		if (name.equalsIgnoreCase("1002"))
		{
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_04m1");
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("11042", maker0, 0, 0);
		}
		super.onTimer(name, maker);
	}
}