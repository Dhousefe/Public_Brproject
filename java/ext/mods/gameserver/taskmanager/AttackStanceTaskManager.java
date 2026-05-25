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
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Cubic;
import ext.mods.gameserver.network.serverpackets.AutoAttackStop;

/**
 * Turns off attack stance of {@link Creature} after ATTACK_STANCE_PERIOD (set to 15sec by default).
 */
public final class AttackStanceTaskManager implements Runnable
{
	private static final long ATTACK_STANCE_PERIOD = 15000;
	
	private final Map<Creature, Long> _creatures = new ConcurrentHashMap<>();
	
	protected AttackStanceTaskManager()
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
			if (time < entry.getValue())
				continue;
			
			final Creature creature = entry.getKey();
			
			creature.broadcastPacket(new AutoAttackStop(creature.getObjectId()));
			
			if (creature instanceof Player player)
			{
				final Summon summon = player.getSummon();
				if (summon != null)
					summon.broadcastPacket(new AutoAttackStop(summon.getObjectId()));
			}
			
			_creatures.remove(creature);
		}
	}
	
	/**
	 * Add a {@link Creature} to the {@link AttackStanceTaskManager}.
	 * @param creature : The Creature to add.
	 */
	public final void add(Creature creature)
	{
		if (creature instanceof Playable)
		{
			for (Cubic cubic : creature.getActingPlayer().getCubicList())
				if (cubic.getId() != Cubic.LIFE_CUBIC)
					cubic.doAction();
		}
		
		_creatures.put(creature, System.currentTimeMillis() + ATTACK_STANCE_PERIOD);
	}
	
	/**
	 * @param creature : The Creature to remove.
	 * @return true if the {@link Creature} was successfully dropped from the {@link AttackStanceTaskManager}.
	 */
	public final boolean remove(Creature creature)
	{
		if (creature instanceof Summon)
			creature = creature.getActingPlayer();
		
		return _creatures.remove(creature) != null;
	}
	
	/**
	 * @param creature : The Creature to test.
	 * @return true if a {@link Creature} is registered in the {@link AttackStanceTaskManager}, false otherwise.
	 */
	public final boolean isInAttackStance(Creature creature)
	{
		if (creature instanceof Summon)
			creature = creature.getActingPlayer();
		
		return _creatures.containsKey(creature);
	}
	
	public static final AttackStanceTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AttackStanceTaskManager INSTANCE = new AttackStanceTaskManager();
	}
}