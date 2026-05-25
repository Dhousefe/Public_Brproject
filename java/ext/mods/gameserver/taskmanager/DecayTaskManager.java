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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Monster;

/**
 * Destroys {@link Creature} corpse after specified time.
 */
public final class DecayTaskManager implements Runnable
{
	private final Map<Creature, Long> _creatures = new ConcurrentHashMap<>();
	
	protected DecayTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public final void run()
	{
		if (_creatures.isEmpty())
			return;
		
		final long time = System.currentTimeMillis();
		
		for (Map.Entry<Creature, Long> entry : _creatures.entrySet())
		{
			final Creature creature = entry.getKey();
			
			if (creature instanceof Summon summon && summon.getOwner().getSummon() != creature)
			{
				_creatures.remove(creature);
				continue;
			}
			
			if (time < entry.getValue())
				continue;
			
			creature.onDecay();
			
			_creatures.remove(creature);
		}
	}
	
	public final Long get(Creature creature)
	{
		return _creatures.get(creature);
	}
	
	/**
	 * Adds a {@link Creature} to the {@link DecayTaskManager} with additional interval.
	 * @param creature : The {@link Creature} to be added.
	 * @param interval : Interval in seconds, after which the decay task is triggered.
	 */
	public final void add(Creature creature, int interval)
	{
		if (creature instanceof Monster monster && (monster.getSpoilState().isSpoiled() || monster.getSeedState().isSeeded()))
			interval = interval * 2 + 25;
		
		_creatures.put(creature, System.currentTimeMillis() + interval * 1000);
	}
	
	/**
	 * Removes the {@link Creature} passed as parameter from the {@link DecayTaskManager}.
	 * @param creature : The {@link Creature} to be removed.
	 * @return True if an entry was successfully removed or false otherwise.
	 */
	public final boolean cancel(Creature creature)
	{
		return _creatures.remove(creature) != null;
	}
	
	public static final DecayTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		protected static final DecayTaskManager INSTANCE = new DecayTaskManager();
	}
}