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

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.model.spawn.SpawnData;

public class SailrenMaker extends DefaultUseDBMaker
{
	public SailrenMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		if (name.equalsIgnoreCase("11042"))
		{
			final MultiSpawn def0 = maker.getSpawns().get(0);
			if (def0 != null)
				def0.sendScriptEvent(11042, 1, 0);
		}
		super.onMakerScriptEvent(name, maker, int1, int2);
	}
	
	@Override
	public void onNpcDBInfo(MultiSpawn ms, SpawnData spawnData, NpcMaker maker)
	{
		if (ms.getSpawnData() != null && !ms.getSpawnData().checkDead())
		{
			if (maker.increaseSpawnedCount(ms, 1))
			{
				if (ms.getSpawnData().getX() != -113091)
				{
					NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune16_npc2017_01m1");
					if (maker0 != null)
						maker0.getMaker().onMakerScriptEvent("11047", maker0, 0, 0);
					
					maker0 = SpawnManager.getInstance().getNpcMaker("rune16_npc2017_13m1");
					if (maker0 != null)
						maker0.getMaker().onMakerScriptEvent("11047", maker0, 0, 0);
				}
				ms.doSpawn(false);
			}
		}
		else if ((maker.increaseSpawnedCount(ms, 1)))
			ms.doSpawn(false);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("rune16_npc2017_01m1");
		if (maker0 != null)
			maker0.getMaker().onMakerScriptEvent("11045", maker0, 0, 0);
		
		if (maker.increaseSpawnedCount(ms, 1))
			ms.scheduleSpawn(ms.calculateRespawnDelay() * 1000);
	}
}