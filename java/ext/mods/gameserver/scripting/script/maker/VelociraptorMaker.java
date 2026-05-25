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

public class VelociraptorMaker extends DefaultMaker
{
	public VelociraptorMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		maker.getMakerMemo().set("i_ai0", 0);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			for (MultiSpawn ms : maker.getSpawns())
			{
				int toSpawnCount = ms.getTotal() - ms.getSpawnedCount();
				if (maker.increaseSpawnedCount(ms, toSpawnCount))
				{
					for (int i = 0; i < toSpawnCount; i++)
						ms.scheduleSpawn(int1 * 1000);
					
					ThreadPool.schedule(() -> onTimer("1001", maker), 5000);
				}
			}
			return;
		}
		else if (name.equalsIgnoreCase("11052"))
		{
			maker.getMakerMemo().set("i_ai0", 1);
		}
		else if (name.equalsIgnoreCase("11053"))
		{
			maker.getMakerMemo().set("i_ai0", 0);
		}
		super.onMakerScriptEvent(name, maker, int1, int2);
	}
	
	@Override
	public void onTimer(String name, NpcMaker maker)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			for (MultiSpawn ms : maker.getSpawns())
				ms.sendScriptEvent(11049, 0, 0);
			
			ThreadPool.schedule(() -> onTimer("78001", maker), 18000 * 1000);
		}
		else if (name.equalsIgnoreCase("78001"))
		{
			if (maker.getMakerMemo().getInteger("i_ai0") == 0)
			{
				NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_01m1");
				if (maker0 != null)
				{
					maker0.getMaker().onMakerScriptEvent("11052", maker0, 0, 0);
					maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
				}
				
				maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_02m1");
				if (maker0 != null)
				{
					maker0.getMaker().onMakerScriptEvent("11052", maker0, 0, 0);
					maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
				}
				
				maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_03m1");
				if (maker0 != null)
				{
					maker0.getMaker().onMakerScriptEvent("11052", maker0, 0, 0);
					maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
				}
				
				maker0 = SpawnManager.getInstance().getNpcMaker("rune16_npc2017_01m1");
				if (maker0 != null)
					maker0.getMaker().onMakerScriptEvent("11050", maker0, 0, 0);
			}
		}
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune20_mb2017_02m1");
		if (maker0 != null)
		{
			if (maker.getSpawnedCount() == 0 && maker.getMakerMemo().getInteger("i_ai0") == 0)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
		}
	}
}