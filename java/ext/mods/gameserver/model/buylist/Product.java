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
package ext.mods.gameserver.model.buylist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.atomic.AtomicInteger;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.taskmanager.BuyListTaskManager;

/**
 * A datatype entry for {@link NpcBuyList}. It can own a count and a restock delay, the whole system of tasks being controlled by {@link BuyListTaskManager}.
 */
public class Product
{
	private static final CLogger LOGGER = new CLogger(Product.class.getName());
	
	private static final String ADD_OR_UPDATE_BUYLIST = "INSERT INTO buylists (buylist_id,item_id,count,next_restock_time) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE count=VALUES(count), next_restock_time=VALUES(next_restock_time)";
	private static final String DELETE_BUYLIST = "DELETE FROM buylists WHERE buylist_id=? AND item_id=?";
	
	private final int _buyListId;
	private final Item _item;
	private final int _price;
	private final long _restockDelay;
	private final int _maxCount;
	
	private AtomicInteger _count = null;
	
	public Product(int buyListId, StatSet set)
	{
		_buyListId = buyListId;
		_item = ItemData.getInstance().getTemplate(set.getInteger("id"));
		_price = set.getInteger("price", 0);
		_restockDelay = set.getLong("restockDelay", -1) * 60000;
		_maxCount = set.getInteger("count", -1);
		
		if (hasLimitedStock())
			_count = new AtomicInteger(_maxCount);
	}
	
	public int getBuyListId()
	{
		return _buyListId;
	}
	
	public Item getItem()
	{
		return _item;
	}
	
	public int getItemId()
	{
		return _item.getItemId();
	}
	
	public int getPrice()
	{
		return _price;
	}
	
	public long getRestockDelay()
	{
		return _restockDelay;
	}
	
	public int getMaxCount()
	{
		return _maxCount;
	}
	
	/**
	 * Get the actual {@link Product} count.<br>
	 * If this Product doesn't own a timer (valid if _maxCount > -1), return 0.
	 * @return the actual Product count.
	 */
	public int getCount()
	{
		if (_count == null)
			return 0;
		
		final int count = _count.get();
		return (count > 0) ? count : 0;
	}
	
	/**
	 * Set arbitrarily the current amount of a {@link Product}.
	 * @param currentCount : The amount to set.
	 */
	public void setCount(int currentCount)
	{
		_count.set(currentCount);
	}
	
	/**
	 * Decrease {@link Product} count, but only if result is superior or equal to 0, and if _count exists.<br>
	 * We setup this Product in the general task if not already existing, and save result on database.
	 * @param val : The value to decrease.
	 * @return true if the Product count can be reduced ; false otherwise.
	 */
	public boolean decreaseCount(int val)
	{
		if (_count == null)
			return false;
		
		final boolean result = _count.addAndGet(-val) >= 0;
		if (result)
			BuyListTaskManager.getInstance().add(this, getRestockDelay());
		
		return result;
	}
	
	public boolean hasLimitedStock()
	{
		return _maxCount > -1;
	}
	
	/**
	 * Save the {@link Product} into database. Happens on successful decrease count.
	 * @param nextRestockTime : The new restock timer.
	 */
	public void save(long nextRestockTime)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(ADD_OR_UPDATE_BUYLIST))
		{
			ps.setInt(1, getBuyListId());
			ps.setInt(2, getItemId());
			ps.setInt(3, getCount());
			ps.setLong(4, nextRestockTime);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save product for buylist id:{} and item id: {}.", e, getBuyListId(), getItemId());
		}
	}
	
	/**
	 * Delete the {@link Product} from database. Happens on restock time reset.
	 */
	public void delete()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_BUYLIST))
		{
			ps.setInt(1, getBuyListId());
			ps.setInt(2, getItemId());
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete product for buylist id:{} and item id: {}.", e, getBuyListId(), getItemId());
		}
	}
}