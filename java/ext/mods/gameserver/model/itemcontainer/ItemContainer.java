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
package ext.mods.gameserver.model.itemcontainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.Config;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.taskmanager.ItemInstanceTaskManager;

public abstract class ItemContainer
{
	protected static final CLogger LOGGER = new CLogger(ItemContainer.class.getName());
	
	private static final String RESTORE_ITEMS = "SELECT * FROM items WHERE owner_id=? AND (loc=?)";
	
	protected final Set<ItemInstance> _items = new ConcurrentSkipListSet<>();
	
	protected ItemContainer()
	{
	}
	
	protected abstract Playable getOwner();
	
	protected abstract ItemLocation getBaseLocation();
	
	public String getName()
	{
		return "ItemContainer";
	}
	
	/**
	 * @return The owner objectId of this {@link ItemContainer}.
	 */
	public int getOwnerId()
	{
		return (getOwner() == null) ? 0 : getOwner().getObjectId();
	}
	
	/**
	 * @return The quantity of {@link ItemInstance}s of this {@link ItemContainer}.
	 */
	public int getSize()
	{
		return _items.size();
	}
	
	/**
	 * @return The {@link Set} of {@link ItemInstance}s of this {@link ItemContainer}.
	 */
	public Set<ItemInstance> getItems()
	{
		return _items;
	}
	
	/**
	 * Run a {@link Consumer} upon filtered {@link ItemInstance}s of this {@link ItemContainer}.
	 * @param predicate : The {@link Predicate} to use as filter.
	 * @param action : The {@link Consumer} to use.
	 */
	public void forEachItem(Predicate<ItemInstance> predicate, Consumer<ItemInstance> action)
	{
		if (_items.isEmpty())
			return;
		
		for (ItemInstance item : _items)
		{
			if (predicate.test(item))
				action.accept(item);
		}
	}
	
	/**
	 * Run a {@link Consumer} upon {@link ItemInstance}s of this {@link ItemContainer}.
	 * @param action : The {@link Consumer} to use.
	 */
	public void forEachItem(Consumer<ItemInstance> action)
	{
		if (_items.isEmpty())
			return;
		
		_items.forEach(action);
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @return True if the item id exists in this {@link ItemContainer}, false otherwise.
	 */
	public boolean hasItems(int itemId)
	{
		if (_items.isEmpty())
			return false;
		
		for (ItemInstance i : _items)
		{
			if (i.getItemId() == itemId)
				return true;
		}
		return false;
	}
	
	/**
	 * @param itemIds : A list of item IDs to check.
	 * @return True if all item ids exist in this {@link ItemContainer}, false otherwise.
	 */
	public boolean hasItems(int... itemIds)
	{
		if (_items.isEmpty())
			return false;
		
		for (int itemId : itemIds)
		{
			if (!hasItems(itemId))
				return false;
		}
		return true;
	}
	
	/**
	 * @param itemIds : A list of item IDs to check.
	 * @return True if at least one item id exists in this {@link ItemContainer}, false otherwise.
	 */
	public boolean hasAtLeastOneItem(int... itemIds)
	{
		if (_items.isEmpty())
			return false;
		
		for (int itemId : itemIds)
		{
			if (hasItems(itemId))
				return true;
		}
		return false;
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @return A {@link List} of {@link ItemInstance}s by given item ID, or an empty {@link List} if none are found.
	 */
	public List<ItemInstance> getItemsByItemId(int itemId)
	{
		if (_items.isEmpty())
			return Collections.emptyList();
		
		final List<ItemInstance> result = new ArrayList<>();
		for (ItemInstance i : _items)
		{
			if (i.getItemId() == itemId)
				result.add(i);
		}
		return result;
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @return An {@link ItemInstance} using its item ID, or null if not found in this {@link ItemContainer}.
	 */
	public ItemInstance getItemByItemId(int itemId)
	{
		if (_items.isEmpty())
			return null;
		
		for (ItemInstance i : _items)
		{
			if (i.getItemId() == itemId)
				return i;
		}
		return null;
	}
	
	/**
	 * @param objectId : The object ID to check.
	 * @return An {@link ItemInstance} using its object ID, or null if not found in this {@link ItemContainer}.
	 */
	public ItemInstance getItemByObjectId(int objectId)
	{
		if (_items.isEmpty())
			return null;
		
		for (ItemInstance i : _items)
		{
			if (i.getObjectId() == objectId)
				return i;
		}
		return null;
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @return The quantity of items hold by this {@link ItemContainer} (item enchant level does not matter, including equipped items).
	 */
	public int getItemCount(int itemId)
	{
		if (_items.isEmpty())
			return 0;
		
		return getItemCount(itemId, -1, true);
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @param enchantLevel : The enchant level to match on (-1 for ANY enchant level).
	 * @return The quantity of items hold by this {@link ItemContainer} (including equipped items).
	 */
	public int getItemCount(int itemId, int enchantLevel)
	{
		return getItemCount(itemId, enchantLevel, true);
	}
	
	/**
	 * @param itemId : The item ID to check.
	 * @param enchantLevel : The enchant level to match on (-1 for ANY enchant level).
	 * @param includeEquipped : Include equipped items.
	 * @return The quantity of items hold by this {@link ItemContainer}.
	 */
	public int getItemCount(int itemId, int enchantLevel, boolean includeEquipped)
	{
		int count = 0;
		
		for (ItemInstance item : _items)
		{
			if (item.getItemId() == itemId && (item.getEnchantLevel() == enchantLevel || enchantLevel < 0) && (includeEquipped || !item.isEquipped()))
			{
				if (item.isStackable())
					return item.getCount();
				
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Add an {@link ItemInstance} to this {@link ItemContainer}.
	 * @param item : The {@link ItemInstance} to add.
	 * @return The {@link ItemInstance} corresponding to the new or updated item.
	 */
	public ItemInstance addItem(ItemInstance item)
	{
		final ItemInstance oldItem = getItemByItemId(item.getItemId());
		if (oldItem != null && oldItem.isStackable())
		{
			oldItem.changeCount(item.getCount(), getOwner());
			
			item.destroyMe();
			
			return oldItem;
		}
		
		item.setOwnerId(getOwnerId());
		item.setLocation(getBaseLocation());
		
		addBasicItem(item);
		
		return item;
	}
	
	/**
	 * Add an item to this {@link ItemContainer}.
	 * @param itemId : The itemId of the {@link ItemInstance} to add.
	 * @param count : The quantity of items to add.
	 * @return The {@link ItemInstance} corresponding to the new or updated item.
	 */
	public ItemInstance addItem(int itemId, int count)
	{
		ItemInstance item = getItemByItemId(itemId);
		
		if (item != null && item.isStackable())
			item.changeCount(count, getOwner());
		else
		{
			final Item template = ItemData.getInstance().getTemplate(itemId);
			if (template == null)
				return null;
			
			for (int i = 0; i < count; i++)
			{
				item = ItemInstance.create(itemId, template.isStackable() ? count : 1);
				item.setOwnerId(getOwnerId());
				item.setLocation(getBaseLocation());
				
				addBasicItem(item);
				
				if (template.isStackable() || !Config.MULTIPLE_ITEM_DROP)
					break;
			}
		}
		return item;
	}
	
	public ItemInstance transferItem(int objectId, int count, ItemContainer target)
	{
		if (target == null)
			return null;
		
		ItemInstance sourceItem = getItemByObjectId(objectId);
		if (sourceItem == null)
			return null;
		
		ItemInstance targetItem = sourceItem.isStackable() ? target.getItemByItemId(sourceItem.getItemId()) : null;
		
		synchronized (sourceItem)
		{
			if (getItemByObjectId(objectId) != sourceItem)
				return null;
			
			if (count > sourceItem.getCount())
				count = sourceItem.getCount();
			
			if (sourceItem.getCount() == count && targetItem == null)
			{
				removeItem(sourceItem, false);
				
				target.addItem(sourceItem);
				targetItem = sourceItem;
			}
			else
			{
				if (sourceItem.getCount() > count)
					sourceItem.changeCount(-count, getOwner());
				else
				{
					removeItem(sourceItem, false);
					
					sourceItem.destroyMe();
				}
				
				if (targetItem != null)
					targetItem.changeCount(count, getOwner());
				else
					targetItem = target.addItem(sourceItem.getItemId(), count);
			}
			
			if (sourceItem.isAugmented() && getOwner() instanceof Player player)
				sourceItem.getAugmentation().removeBonus(player);
		}
		return targetItem;
	}
	
	public ItemInstance transferItem(int objectId, int amount, Playable target)
	{
		if (target == null)
			return null;
		
		ItemInstance sourceitem = getItemByObjectId(objectId);
		if (sourceitem == null)
			return null;
		
		Inventory inventory = target.getInventory();
		ItemInstance targetitem = sourceitem.isStackable() ? inventory.getItemByItemId(sourceitem.getItemId()) : null;
		
		synchronized (sourceitem)
		{
			if (getItemByObjectId(objectId) != sourceitem)
				return null;
			
			if (amount > sourceitem.getCount())
				amount = sourceitem.getCount();
			
			if (sourceitem.getCount() == amount && targetitem == null)
			{
				removeItem(sourceitem, false);
				
				inventory.addItem(sourceitem);
				targetitem = sourceitem;
			}
			else
			{
				if (sourceitem.getCount() > amount)
					sourceitem.changeCount(-amount, getOwner());
				else
				{
					removeItem(sourceitem, false);
					
					sourceitem.destroyMe();
				}
				
				if (targetitem != null)
					targetitem.changeCount(amount, target);
				else
					targetitem = inventory.addItem(sourceitem.getItemId(), amount);
			}
		}
		return targetitem;
	}
	
	/**
	 * Destroy entirely the {@link ItemInstance} set as parameter from this {@link ItemContainer}.
	 * @param item : The {@link ItemInstance} to destroy.
	 * @return The {@link ItemInstance} corresponding to the destroyed item in this {@link ItemContainer}.
	 */
	public ItemInstance destroyItem(ItemInstance item)
	{
		return destroyItem(item, item.getCount());
	}
	
	/**
	 * Destroy or reduce amount of the {@link ItemInstance} set as parameter from this {@link ItemContainer}.
	 * @param item : The {@link ItemInstance} to destroy.
	 * @param count : The quantity of items to remove.
	 * @return The {@link ItemInstance} corresponding to the destroyed/updated item in this {@link ItemContainer}.
	 */
	public ItemInstance destroyItem(ItemInstance item, int count)
	{
		synchronized (item)
		{
			if (item.getCount() > count)
			{
				item.changeCount(-count, getOwner());
				
				return item;
			}
			
			if (item.getCount() < count)
				return null;
			
			if (!removeItem(item, false))
				return null;
			
			item.destroyMe();
		}
		return item;
	}
	
	/**
	 * Destroy or reduce the amount of the {@link ItemInstance} from this {@link ItemContainer} by using its objectId.
	 * @param objectId : The objectId of the {@link ItemInstance} to destroy.
	 * @param count : The quantity of items to remove.
	 * @return The {@link ItemInstance} corresponding to the destroyed/updated item in this {@link ItemContainer}.
	 */
	public ItemInstance destroyItem(int objectId, int count)
	{
		ItemInstance item = getItemByObjectId(objectId);
		if (item == null)
			return null;
		
		return destroyItem(item, count);
	}
	
	/**
	 * Destroy or reduce the amount of the {@link ItemInstance} from this {@link ItemContainer} by using its itemId.
	 * @param itemId : The itemId of the {@link ItemInstance} to destroy.
	 * @param count : The quantity of items to remove.
	 * @return The {@link ItemInstance} corresponding to the destroyed/updated item in this {@link ItemContainer}.
	 */
	public ItemInstance destroyItemByItemId(int itemId, int count)
	{
		ItemInstance item = getItemByItemId(itemId);
		if (item == null)
			return null;
		
		return destroyItem(item, count);
	}
	
	/**
	 * Destroy all {@link ItemInstance}s from this {@link ItemContainer}.
	 */
	public void destroyAllItems()
	{
		for (ItemInstance item : _items)
			destroyItem(item);
	}
	
	/**
	 * @return The amount of hold Adena, or 0 if no held {@link ItemInstance}.
	 */
	public int getAdena()
	{
		for (ItemInstance item : _items)
		{
			if (item.getItemId() == 57)
				return item.getCount();
		}
		return 0;
	}
	
	/**
	 * Add the {@link ItemInstance} set as parameter to this {@link ItemContainer}.
	 * @param item : The {@link ItemInstance} to add.
	 */
	protected void addBasicItem(ItemInstance item)
	{
		item.actualizeTime();
		
		_items.add(item);
	}
	
	/**
	 * @param item : The {@link ItemInstance} to remove.
	 * @param isDrop : If true, we also reset {@link ItemInstance}'s ownership and location.
	 * @return True if the {@link ItemInstance} set as parameter was successfully removed, or false otherwise.
	 */
	protected boolean removeItem(ItemInstance item, boolean isDrop)
	{
		return _items.remove(item);
	}
	
	/**
	 * Delete this {@link ItemContainer}, aswell as contained {@link ItemInstance}s, from {@link World}.<br>
	 * <br>
	 * Before deletion, {@link ItemInstance}s are saved in database.
	 */
	public void deleteMe()
	{
		if (getOwner() != null)
		{
			World.getInstance().removeObjects(_items);
			
			ItemInstanceTaskManager.getInstance().removeItems(_items);
			
			ItemInstanceTaskManager.getInstance().updateItems(_items);
		}
		else
			_items.clear();
	}
	
	/**
	 * Generate {@link ItemInstance} objects based on database content, and feed this {@link ItemContainer}.
	 */
	public void restore()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_ITEMS))
		{
			ps.setInt(1, getOwnerId());
			ps.setString(2, getBaseLocation().name());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final ItemInstance item = ItemInstance.restoreFromDb(rs);
					if (item == null)
						continue;
					
					if (ItemInstanceTaskManager.getInstance().contains(item))
						continue;
					
					World.getInstance().addObject(item);
					
					if (item.isStackable() && getItemByItemId(item.getItemId()) != null)
						addItem(item);
					else
						addBasicItem(item);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore container for {}.", e, getOwnerId());
		}
	}
	
	public boolean validateCapacity(int slotCount)
	{
		return true;
	}
	
	public boolean validateWeight(int weight)
	{
		return true;
	}
}