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

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.model.spawn.SpawnData;

public class FarmMaker extends DefaultMaker
{
	public FarmMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		if (maker.getSpawns().size() > 1)
		{
			MultiSpawn def0 = maker.getSpawns().get(0);
			def0.doSpawn(false);
			
			for (int i = 1; i < maker.getSpawns().size(); i++)
			{
				maker.getSpawns().get(i).loadDBNpcInfo();
			}
		}
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
	}
	
	@Override
	public void onNpcDBInfo(MultiSpawn ms, SpawnData spawnData, NpcMaker maker)
	{
		if (maker.increaseSpawnedCount(ms, 1))
			if (ms.getSpawnData() != null && !ms.getSpawnData().checkDead())
				if (ms.getSpawnData().checkAlive(ms.getSpawnLocation(), ms.getTemplate().getBaseHpMax(0), ms.getTemplate().getBaseMpMax(0)))
					ms.doSpawn(true);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		if (name.equalsIgnoreCase("onSiegeStart"))
		{
			for (int i0 = 1; i0 < (maker.getSpawns().size() - 1); i0++)
			{
				MultiSpawn def0 = maker.getSpawns().get(i0);
				if (maker.increaseSpawnedCount(def0, def0.getTotal()))
					def0.doSpawn(def0.getTotal(), false);
			}
		}
		else if (name.equalsIgnoreCase("onFlagWarFinalEvent"))
		{
			for (int i0 = 1; i0 < maker.getSpawns().size(); i0++)
			{
				MultiSpawn def0 = maker.getSpawns().get(i0);
				if (maker.increaseSpawnedCount(def0, def0.getTotal()))
					def0.doSpawn(def0.getTotal(), false);
			}
		}
		else if (name.equalsIgnoreCase("onSiegeEnd"))
		{
			for (int i0 = 1; i0 < maker.getSpawns().size(); i0++)
			{
				MultiSpawn def0 = maker.getSpawns().get(i0);
				def0.doDelete();
			}
		}
	}
}
