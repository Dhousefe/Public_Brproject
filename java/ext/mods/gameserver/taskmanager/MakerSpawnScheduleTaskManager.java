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
package ext.mods.gameserver.taskmanager;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.spawn.MultiSpawn;

public final class MakerSpawnScheduleTaskManager implements Runnable
{
	private final Map<MultiSpawn, ConcurrentLinkedQueue<Long>> _spawns = new ConcurrentHashMap<>();
	
	protected MakerSpawnScheduleTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public final void run()
	{
		if (_spawns.isEmpty())
			return;
		
		final long time = System.currentTimeMillis();
		
		Iterator<Map.Entry<MultiSpawn, ConcurrentLinkedQueue<Long>>> iterator = _spawns.entrySet().iterator();
		while (iterator.hasNext())
		{
			Map.Entry<MultiSpawn, ConcurrentLinkedQueue<Long>> entry = iterator.next();
			MultiSpawn spawn = entry.getKey();
			ConcurrentLinkedQueue<Long> values = entry.getValue();
			
			if (values == null || values.isEmpty())
				iterator.remove();
			else
			{
				Iterator<Long> innerIterator = values.iterator();
				while (innerIterator.hasNext())
				{
					Long value = innerIterator.next();
					if (time >= value)
					{
						spawn.doSpawn(false, null);
						innerIterator.remove();
					}
				}
			}
		}
	}
	
	public void addSpawn(MultiSpawn spawn, Long value)
	{
		_spawns.computeIfAbsent(spawn, k -> new ConcurrentLinkedQueue<>()).add(System.currentTimeMillis() + value);
	}
	
	public void cancelMakerRespawns(MultiSpawn multiSpawn)
	{
		_spawns.remove(multiSpawn);
	}
	
	public int getRespawningNpcCount(MultiSpawn multiSpawn)
	{
		final ConcurrentLinkedQueue<Long> spawnList = _spawns.get(multiSpawn);
		if (spawnList == null || spawnList.isEmpty())
			return 0;
		
		return spawnList.size();
	}
	
	public static final MakerSpawnScheduleTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final MakerSpawnScheduleTaskManager INSTANCE = new MakerSpawnScheduleTaskManager();
	}
}