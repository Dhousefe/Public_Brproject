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

import ext.mods.gameserver.model.buylist.Product;

/**
 * Handles individual {@link Product} restock timers.<br>
 * A timer is set, then on activation it restocks and releases it from the map. Additionally, some SQL action is done.
 */
public final class BuyListTaskManager implements Runnable
{
	private final Map<Product, Long> _products = new ConcurrentHashMap<>();
	
	protected BuyListTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public final void run()
	{
		if (_products.isEmpty())
			return;
		
		final long time = System.currentTimeMillis();
		
		for (Map.Entry<Product, Long> entry : _products.entrySet())
		{
			if (time < entry.getValue())
				continue;
			
			final Product product = entry.getKey();
			product.setCount(product.getMaxCount());
			product.delete();
			
			_products.remove(product);
		}
	}
	
	/**
	 * Adds a {@link Product} to the task. A product can't be added twice.
	 * @param product : {@link Product} to be added.
	 * @param interval : Interval in minutes, after which the task is triggered.
	 */
	public final void add(Product product, long interval)
	{
		final long newRestockTime = System.currentTimeMillis() + interval;
		if (_products.putIfAbsent(product, newRestockTime) == null)
			product.save(newRestockTime);
	}
	
	/**
	 * Test the timer : if already gone, reset the count without adding the {@link Product} to the task. A product can't be added twice.
	 * @param product : {@link Product} to be added.
	 * @param currentCount : the amount to set, if remaining time succeeds.
	 * @param nextRestockTime : time in milliseconds.
	 */
	public final void test(Product product, int currentCount, long nextRestockTime)
	{
		if (nextRestockTime - System.currentTimeMillis() > 0)
		{
			product.setCount(currentCount);
			_products.putIfAbsent(product, nextRestockTime);
		}
		else
		{
			product.setCount(product.getMaxCount());
			product.delete();
		}
	}
	
	public static final BuyListTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		protected static final BuyListTaskManager INSTANCE = new BuyListTaskManager();
	}
}