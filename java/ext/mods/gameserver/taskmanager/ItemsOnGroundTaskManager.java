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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.CursedWeaponManager;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.residence.castle.Castle;

/**
 * Destroy items on ground after specified time. When server is about to shutdown/restart, saves all dropped items in to SQL. Load them during server start.
 */
public final class ItemsOnGroundTaskManager implements Runnable
{
	private static final CLogger LOGGER = new CLogger(ItemsOnGroundTaskManager.class.getName());
	
	private static final String LOAD_ITEMS = "SELECT object_id,item_id,count,enchant_level,x,y,z,time FROM items_on_ground";
	private static final String TRUNCATE_ITEMS = "TRUNCATE items_on_ground";
	private static final String SAVE_ITEMS = "INSERT INTO items_on_ground(object_id,item_id,count,enchant_level,x,y,z,time) VALUES(?,?,?,?,?,?,?,?)";
	
	private final Map<ItemInstance, Long> _items = new ConcurrentHashMap<>();
	
	public ItemsOnGroundTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 15000, 15000);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement st = con.prepareStatement(LOAD_ITEMS);
			PreparedStatement st2 = con.prepareStatement(TRUNCATE_ITEMS);
			ResultSet rs = st.executeQuery())
		{
			final long time = System.currentTimeMillis();
			
			while (rs.next())
			{
				final ItemInstance item = new ItemInstance(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getInt(4));
				item.spawnMe(rs.getInt(5), rs.getInt(6), rs.getInt(7));
				
				final Castle castle = CastleManager.getInstance().getCastle(item);
				if (castle != null && castle.getTicket(item.getItemId()) != null)
					castle.addDroppedTicket(item);
				
				final long interval = rs.getLong(8);
				_items.put(item, (interval == 0) ? 0L : time + interval);
			}
			
			st2.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while loading items on ground data.", e);
		}
	}
	
	@Override
	public final void run()
	{
		if (_items.isEmpty())
			return;
		
		final long time = System.currentTimeMillis();
		
		for (Map.Entry<ItemInstance, Long> entry : _items.entrySet())
		{
			final long destroyTime = entry.getValue();
			
			if (destroyTime == 0 || time < destroyTime)
				continue;
			
			final ItemInstance item = entry.getKey();
			item.decayMe();
		}
	}
	
	/**
	 * Add the {@link ItemInstance} set as parameter. Calculate its destroy time based on the multiple associated {@link Config}s.
	 * @param item : The {@link ItemInstance} to add.
	 * @param creature : The {@link Creature} who dropped the item.
	 */
	public final void add(ItemInstance item, Creature creature)
	{
		if (creature == null || item.isDestroyProtected())
			return;
		
		long dropTime = 0;
		
		if (Config.ITEMS_GC_CLEANUP_ENABLED && Config.ITEMS_GC_CLEANUP_TIME_MS > 0)
		{
			dropTime = Config.ITEMS_GC_CLEANUP_TIME_MS;
		}
		else
		{
			Integer special = Config.SPECIAL_ITEM_DESTROY_TIME.get(item.getItemId());
			if (special != null)
				dropTime = special;
			else if (item.isHerb())
				dropTime = Config.HERB_AUTO_DESTROY_TIME;
			else if (item.isEquipable())
				dropTime = Config.EQUIPABLE_ITEM_AUTO_DESTROY_TIME;
			else
			{
				final Castle castle = CastleManager.getInstance().getCastle(item);
				dropTime = (castle != null && castle.getTicket(item.getItemId()) != null) ? 0 : Config.ITEM_AUTO_DESTROY_TIME;
			}
			
			if (creature instanceof Playable)
				dropTime *= Config.PLAYER_DROPPED_ITEM_MULTIPLIER;
		}
		
		if (dropTime != 0)
			dropTime += System.currentTimeMillis();
		
		_items.put(item, dropTime);
	}
	
	/**
	 * Remove the {@link ItemInstance} set as parameter.
	 * @param item : The {@link ItemInstance} to remove.
	 */
	public final void remove(ItemInstance item)
	{
		_items.remove(item);
	}
	
	public final void save()
	{
		if (_items.isEmpty())
		{
			LOGGER.info("No items on ground to save.");
			return;
		}
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement st = con.prepareStatement(SAVE_ITEMS))
		{
			final long time = System.currentTimeMillis();
			
			for (Entry<ItemInstance, Long> entry : _items.entrySet())
			{
				final ItemInstance item = entry.getKey();
				
				if (CursedWeaponManager.getInstance().isCursed(item.getItemId()))
					continue;
				
				st.setInt(1, item.getObjectId());
				st.setInt(2, item.getItemId());
				st.setInt(3, item.getCount());
				st.setInt(4, item.getEnchantLevel());
				st.setInt(5, item.getX());
				st.setInt(6, item.getY());
				st.setInt(7, item.getZ());
				
				final long left = entry.getValue();
				st.setLong(8, (left == 0) ? 0 : left - time);
				
				st.addBatch();
			}
			st.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save items on ground.", e);
		}
		LOGGER.info("Saved {} items on ground.", _items.size());
	}
	
	public static final ItemsOnGroundTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemsOnGroundTaskManager INSTANCE = new ItemsOnGroundTaskManager();
	}
}