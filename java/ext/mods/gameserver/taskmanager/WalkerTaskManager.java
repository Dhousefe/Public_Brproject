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

import ext.mods.gameserver.model.actor.Npc;

/**
 * Handles {@link Npc} waiting state case, when their current WalkerLocation got a delay.
 */
public final class WalkerTaskManager implements Runnable
{
	private final Map<Npc, Long> _walkers = new ConcurrentHashMap<>();
	
	protected WalkerTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public final void run()
	{
		if (_walkers.isEmpty())
			return;
		
		final long time = System.currentTimeMillis();
		
		for (Map.Entry<Npc, Long> entry : _walkers.entrySet())
		{
			if (time < entry.getValue())
				continue;
			
			final Npc npc = entry.getKey();
			
			if (npc.isMoving())
				continue;
			
			npc.getAI().moveToNextPoint();
			
			_walkers.remove(npc);
		}
	}
	
	/**
	 * Adds {@link Npc} to the WalkerTaskManager.
	 * @param npc : The {@link Npc} to be added.
	 * @param delay : The delay to add.
	 */
	public final void add(Npc npc, int delay)
	{
		_walkers.put(npc, System.currentTimeMillis() + delay);
	}
	
	public static final WalkerTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final WalkerTaskManager INSTANCE = new WalkerTaskManager();
	}
}