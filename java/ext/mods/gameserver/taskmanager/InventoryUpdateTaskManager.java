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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.itemcontainer.Inventory;

/**
 * Send client packet to related {@link Inventory}'s owners, if an update is asked.
 */
public class InventoryUpdateTaskManager implements Runnable
{
	private final Set<Inventory> _list = ConcurrentHashMap.newKeySet();
	
	protected InventoryUpdateTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 333L, 333L);
	}
	
	@Override
	public final void run()
	{
		if (_list.isEmpty())
			return;
		
		for (Inventory inv : _list)
		{
			if (inv.getUpdateList().isEmpty())
			{
				_list.remove(inv);
				continue;
			}
			
			final Playable owner = inv.getOwner();
			if (!owner.isVisible() && !owner.isTeleporting())
			{
				_list.remove(inv);
				continue;
			}
			
			owner.sendIU();
			inv.updateWeight();
		}
	}
	
	public void add(Inventory inv)
	{
		if (!_list.contains(inv))
			_list.add(inv);
	}
	
	public static final InventoryUpdateTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final InventoryUpdateTaskManager INSTANCE = new InventoryUpdateTaskManager();
	}
}