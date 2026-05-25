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
import ext.mods.gameserver.scripting.SpawnMaker;

public class DefaultMaker extends SpawnMaker
{
	public DefaultMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		if (maker.isOnStart() || !checkHasSpawnCondition(maker))
		{
			for (MultiSpawn ms : maker.getSpawns())
			{
				int toSpawn = ms.getTotal();
				if (maker.getMaximumNpc() < (maker.getSpawnedCount() + toSpawn))
					toSpawn = maker.getMaximumNpc() - maker.getSpawnedCount();
				
				if (toSpawn > 0)
				{
					if (ms.getSpawnData() != null)
						ms.loadDBNpcInfo();
					else
					{
						if (maker.increaseSpawnedCount(ms, toSpawn))
							ms.doSpawn(toSpawn, false);
					}
				}
			}
		}
	}
	
	@Override
	public void onNpcDBInfo(MultiSpawn ms, SpawnData spawnData, NpcMaker maker)
	{
		if (maker.increaseSpawnedCount(ms, 1))
			ms.doSpawn(false);
	}
	
	@Override
	public void onNpcCreated(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (checkHasSpawnCondition(maker))
			npc.deleteMe();
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (checkHasSpawnCondition(maker))
			return;
		
		if (ms.getRespawnDelay() != 0 && maker.getMaximumNpc() >= maker.getSpawnedCount() + 1 && maker.increaseSpawnedCount(ms, 1))
		{
			SpawnData spawnData = npc.getSpawn().getSpawnData();
			if (spawnData != null)
			{
				if (spawnData.getRespawnTime() > System.currentTimeMillis())
					ms.scheduleSpawn(spawnData.getRespawnTime() - System.currentTimeMillis());
				else
				{
					long respawnDelay = npc.getSpawn().calculateRespawnDelay();
					spawnData.setRespawn(respawnDelay * 1000);
					ms.scheduleSpawn(respawnDelay * 1000);
				}
			}
			else
			{
				long respawnDelay = npc.getSpawn().calculateRespawnDelay();
				ms.scheduleSpawn(respawnDelay * 1000);
			}
		}
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		if (name.equalsIgnoreCase("1000"))
			maker.deleteAll();
		else if (name.equalsIgnoreCase("1001"))
		{
			if (checkHasSpawnCondition(maker))
				return;
			
			for (MultiSpawn ms : maker.getSpawns())
			{
				final int unspawnedCount = ms.getTotal() - ms.getSpawnedCount();
				if (unspawnedCount > 0 && maker.getMaximumNpc() >= (maker.getSpawnedCount() + unspawnedCount) && maker.increaseSpawnedCount(ms, unspawnedCount))
					for (int i = 0; i < unspawnedCount; i++)
						ms.scheduleSpawn(int1 * 1000);
			}
		}
	}
}