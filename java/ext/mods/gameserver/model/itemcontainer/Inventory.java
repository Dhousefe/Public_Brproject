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
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Stream;

import ext.mods.commons.pool.ConnectionPool;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.HeroManager;
import ext.mods.gameserver.enums.Paperdoll;
import ext.mods.gameserver.enums.items.ArmorType;
import ext.mods.gameserver.enums.items.EtcItemType;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.enums.items.ItemState;
import ext.mods.gameserver.enums.items.ItemType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInfo;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.itemcontainer.listeners.ChangeRecorderListener;
import ext.mods.gameserver.model.itemcontainer.listeners.OnEquipListener;
import ext.mods.gameserver.model.itemcontainer.listeners.StatsListener;
import ext.mods.gameserver.taskmanager.InventoryUpdateTaskManager;
import ext.mods.gameserver.taskmanager.ItemInstanceTaskManager;

/**
 * This class manages a {@link Playable}'s inventory.<br>
 * <br>
 * It extends {@link ItemContainer}.
 */
public abstract class Inventory extends ItemContainer
{
	private static final Logger ITEM_LOG = Logger.getLogger("item");
	
	private static final String RESTORE_INVENTORY = "SELECT * FROM items WHERE owner_id=? AND (loc=? OR loc=?) ORDER BY loc_data";
	
	protected Playable _owner;
	
	private final ItemInstance[] _paperdoll = new ItemInstance[Paperdoll.TOTAL_SLOTS];
	
	protected final List<OnEquipListener> _paperdollListeners = new ArrayList<>();
	protected final Queue<ItemInfo> _updateList = new ConcurrentLinkedQueue<>();
	
	protected int _totalWeight;
	private int _wornMask;
	
	protected Inventory(Playable owner)
	{
		_owner = owner;
		
		addPaperdollListener(StatsListener.getInstance());
	}
	
	protected abstract ItemLocation getEquipLocation();
	
	@Override
	public Playable getOwner()
	{
		return _owner;
	}
	
	@Override
	protected void addBasicItem(ItemInstance item)
	{
		super.addBasicItem(item);
		
		addUpdate(item, ItemState.ADDED);
	}
	
	@Override
	protected boolean removeItem(ItemInstance item, boolean isDrop)
	{
		if (!super.removeItem(item, isDrop))
			return false;
		
		for (int i = 0; i < _paperdoll.length; i++)
		{
			if (_paperdoll[i] == item)
				unequipItemInSlot(i);
		}
		
		if (isDrop)
		{
			item.setOwnerId(0);
			item.setLocation(ItemLocation.VOID);
		}
		
		addUpdate(item, ItemState.REMOVED);
		return true;
	}
	
	@Override
	public void restore()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(RESTORE_INVENTORY))
		{
			ps.setInt(1, getOwnerId());
			ps.setString(2, getBaseLocation().name());
			ps.setString(3, getEquipLocation().name());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final ItemInstance item = ItemInstance.restoreFromDb(rs);
					if (item == null)
						continue;
					
					if (ItemInstanceTaskManager.getInstance().contains(item))
						continue;
					
					if (getOwner() instanceof Player && item.isHeroItem() && !HeroManager.getInstance().isActiveHero(getOwnerId()))
						item.setLocation(ItemLocation.INVENTORY);
					
					World.getInstance().addObject(item);
					
					if (item.isStackable() && getItemByItemId(item.getItemId()) != null)
						addItem(item);
					else
						super.addBasicItem(item);
					
					if (item.isEquipped())
						equipItem(item);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore inventory for {}.", e, getOwnerId());
		}
	}
	
	public int getTotalWeight()
	{
		return _totalWeight;
	}
	
	public boolean updateWeight()
	{
		int weight = 0;
		for (ItemInstance item : _items)
			weight += item.getItem().getWeight() * item.getCount();
		
		if (_totalWeight == weight)
			return false;
		
		_totalWeight = weight;
		return true;
	}
	
	/**
	 * @param type : The {@link ItemType} to check.
	 * @return True if the given {@link ItemType} is worn, false otherwise.
	 */
	public boolean isWearingType(ItemType type)
	{
		return isWearingType(type.mask());
	}
	
	/**
	 * @param mask : The mask to check.
	 * @return True if the given {@link ItemType} mask is worn, false otherwise.
	 */
	public boolean isWearingType(int mask)
	{
		return (mask & _wornMask) != 0;
	}
	
	/**
	 * Drop an item from this {@link Inventory} and update database.
	 * @param item : The {@link ItemInstance} to drop.
	 * @return The {@link ItemInstance} corresponding to the destroyed item or the updated item in {@link Inventory}.
	 */
	public ItemInstance dropItem(ItemInstance item)
	{
		return (removeItem(item, true)) ? item : null;
	}
	
	/**
	 * Drop an item using its objectIdfrom this {@link Inventory} and update database.
	 * @param objectId : The {@link ItemInstance} objectId to drop.
	 * @param count : The amount to drop.
	 * @return The {@link ItemInstance} corresponding to the destroyed item or the updated item in {@link Inventory}.
	 */
	public ItemInstance dropItem(int objectId, int count)
	{
		ItemInstance item = getItemByObjectId(objectId);
		if (item == null)
			return null;
		
		synchronized (item)
		{
			if (!_items.contains(item))
				return null;
			
			if (item.getCount() > count)
			{
				item.changeCount(-count, getOwner());
				
				item = ItemInstance.create(item.getItemId(), count);
				return item;
			}
		}
		return dropItem(item);
	}
	
	/**
	 * @param slot : The {@link Paperdoll} slot to check.
	 * @return The {@link ItemInstance} associated to the {@link Paperdoll} slot.
	 */
	public ItemInstance getItemFrom(Paperdoll slot)
	{
		return _paperdoll[slot.getId()];
	}
	
	/**
	 * @param slot : The {@link Paperdoll} slot to check.
	 * @return True if an {@link ItemInstance} is associated to the {@link Paperdoll} slot, false otherwise.
	 */
	public boolean hasItemIn(Paperdoll slot)
	{
		return _paperdoll[slot.getId()] != null;
	}
	
	/**
	 * @param slot : The {@link Paperdoll} slot to test.
	 * @return The id of the {@link ItemInstance} in the {@link Paperdoll} slot, or 0 if not found.
	 */
	public int getItemIdFrom(Paperdoll slot)
	{
		final ItemInstance item = getItemFrom(slot);
		return (item == null) ? 0 : item.getItemId();
	}
	
	/**
	 * @param slot : The {@link Paperdoll} slot to test.
	 * @return The augment id of the {@link ItemInstance} in the {@link Paperdoll} slot, or 0 if not found.
	 */
	public int getAugmentationIdFrom(Paperdoll slot)
	{
		final ItemInstance item = getItemFrom(slot);
		return (item == null || item.getAugmentation() == null) ? 0 : item.getAugmentation().getId();
	}
	
	/**
	 * @param slot : The {@link Paperdoll} slot to test.
	 * @return The object id of the {@link ItemInstance} in the {@link Paperdoll} slot, or 0 if not found.
	 */
	public int getItemObjectIdFrom(Paperdoll slot)
	{
		final ItemInstance item = getItemFrom(slot);
		return (item == null) ? 0 : item.getObjectId();
	}
	
	/**
	 * @param itemSlot : The item slot to check.
	 * @return The {@link ItemInstance} associated to the item slot.
	 */
	public ItemInstance getItemFrom(int itemSlot)
	{
		return getItemFrom(getPaperdollIndex(itemSlot));
	}
	
	/**
	 * @return The {@link List} of equipped {@link ItemInstance}s.
	 */
	public List<ItemInstance> getPaperdollItems()
	{
		return Stream.of(_paperdoll).filter(Objects::nonNull).toList();
	}
	
	/**
	 * @param slot : The item slot to test.
	 * @return The {@link Paperdoll} associated to an item slot.
	 */
	public static Paperdoll getPaperdollIndex(int slot)
	{
		switch (slot)
		{
			case Item.SLOT_UNDERWEAR:
				return Paperdoll.UNDER;
			
			case Item.SLOT_R_EAR:
				return Paperdoll.REAR;
			
			case Item.SLOT_L_EAR:
				return Paperdoll.LEAR;
			
			case Item.SLOT_NECK:
				return Paperdoll.NECK;
			
			case Item.SLOT_R_FINGER:
				return Paperdoll.RFINGER;
			
			case Item.SLOT_L_FINGER:
				return Paperdoll.LFINGER;
			
			case Item.SLOT_HEAD:
				return Paperdoll.HEAD;
			
			case Item.SLOT_R_HAND, Item.SLOT_LR_HAND:
				return Paperdoll.RHAND;
			
			case Item.SLOT_L_HAND:
				return Paperdoll.LHAND;
			
			case Item.SLOT_GLOVES:
				return Paperdoll.GLOVES;
			
			case Item.SLOT_CHEST, Item.SLOT_FULL_ARMOR, Item.SLOT_ALLDRESS:
				return Paperdoll.CHEST;
			
			case Item.SLOT_LEGS:
				return Paperdoll.LEGS;
			
			case Item.SLOT_FEET:
				return Paperdoll.FEET;
			
			case Item.SLOT_BACK:
				return Paperdoll.CLOAK;
			
			case Item.SLOT_FACE, Item.SLOT_HAIRALL:
				return Paperdoll.FACE;
			
			case Item.SLOT_HAIR:
				return Paperdoll.HAIR;
		}
		return Paperdoll.NULL;
	}
	
	/**
	 * Register a new {@link OnEquipListener} on paperdoll listeners.
	 * @param listener : The {@link OnEquipListener} to add.
	 */
	public synchronized void addPaperdollListener(OnEquipListener listener)
	{
		_paperdollListeners.add(listener);
	}
	
	/**
	 * Unregister an existing {@link OnEquipListener} from paperdoll listeners.
	 * @param listener : The {@link OnEquipListener} to remove.
	 */
	public synchronized void removePaperdollListener(OnEquipListener listener)
	{
		_paperdollListeners.remove(listener);
	}
	
	public void setPaperdollItemVisual(Paperdoll slot)
	{
		ItemInstance item = getItemFrom(slot);
		ItemInstance old = _paperdoll[slot.getId()];
		if (old != item)
		{
			if (old != null)
			{
				_paperdoll[slot.getId()] = null;
				
				old.setLocation(getBaseLocation());
				addUpdate(old, ItemState.MODIFIED);
				
				_wornMask &= ~old.getItem().getItemMask();
				
				for (OnEquipListener listener : _paperdollListeners)
					listener.onUnequip(slot, old, getOwner());
			}
			
			if (item != null)
			{
				_paperdoll[slot.getId()] = item;
				
				item.setLocation(getEquipLocation(), slot.getId());
				addUpdate(item, ItemState.MODIFIED);
				
				final Item itm = item.getItem();
				if (itm.getBodyPart() == Item.SLOT_CHEST)
				{
					final ItemInstance legs = getItemFrom(Paperdoll.LEGS);
					if (legs != null && legs.getItem().getItemMask() == itm.getItemMask())
						_wornMask |= itm.getItemMask();
				}
				else if (itm.getBodyPart() == Item.SLOT_LEGS)
				{
					final ItemInstance legs = getItemFrom(Paperdoll.CHEST);
					if (legs != null && legs.getItem().getItemMask() == itm.getItemMask())
						_wornMask |= itm.getItemMask();
				}
				else
					_wornMask |= itm.getItemMask();
				
				for (OnEquipListener listener : _paperdollListeners)
					listener.onEquip(slot, item, getOwner());
			}
		}
		
		Player owner = getOwner() instanceof Player ? (Player) getOwner() : null;
		if (owner != null)
		{
			owner.broadcastCharInfo();
		}
	}
	
	/**
	 * Equip an {@link ItemInstance} in the given {@link Paperdoll} slot.
	 * @param slot : The {@link Paperdoll} slot to edit.
	 * @param item : The {@link ItemInstance} to add.
	 * @return The previous {@link ItemInstance} set in given {@link Paperdoll}, or null if unequipped.
	 */
	public synchronized ItemInstance setPaperdollItem(Paperdoll slot, ItemInstance item)
	{
		ItemInstance old = getItemFrom(slot);
		if (old != item)
		{
			if (old != null)
			{
				_paperdoll[slot.getId()] = null;
				
				old.setLocation(getBaseLocation());
				addUpdate(old, ItemState.MODIFIED);
				
				_wornMask &= ~old.getItem().getItemMask();
				
				for (OnEquipListener listener : _paperdollListeners)
					listener.onUnequip(slot, old, getOwner());
			}
			
			if (item != null)
			{
				_paperdoll[slot.getId()] = item;
				
				item.setLocation(getEquipLocation(), slot.getId());
				addUpdate(item, ItemState.MODIFIED);
				
				final Item itm = item.getItem();
				if (itm.getBodyPart() == Item.SLOT_CHEST)
				{
					final ItemInstance legs = getItemFrom(Paperdoll.LEGS);
					if (legs != null && legs.getItem().getItemMask() == itm.getItemMask())
						_wornMask |= itm.getItemMask();
				}
				else if (itm.getBodyPart() == Item.SLOT_LEGS)
				{
					final ItemInstance legs = getItemFrom(Paperdoll.CHEST);
					if (legs != null && legs.getItem().getItemMask() == itm.getItemMask())
						_wornMask |= itm.getItemMask();
				}
				else
					_wornMask |= itm.getItemMask();
				
				for (OnEquipListener listener : _paperdollListeners)
					listener.onEquip(slot, item, getOwner());
			}
		}
		return old;
	}
	
	/**
	 * @param item : The {@link ItemInstance} to test.
	 * @return The item slot associated to a given {@link Paperdoll}.
	 */
	public int getSlotFromItem(ItemInstance item)
	{
		switch (Paperdoll.getEnumById(item.getLocationSlot()))
		{
			case UNDER:
				return Item.SLOT_UNDERWEAR;
			
			case LEAR:
				return Item.SLOT_L_EAR;
			
			case REAR:
				return Item.SLOT_R_EAR;
			
			case NECK:
				return Item.SLOT_NECK;
			
			case RFINGER:
				return Item.SLOT_R_FINGER;
			
			case LFINGER:
				return Item.SLOT_L_FINGER;
			
			case HAIR:
				return Item.SLOT_HAIR;
			
			case FACE:
				return Item.SLOT_FACE;
			
			case HEAD:
				return Item.SLOT_HEAD;
			
			case RHAND:
				return Item.SLOT_R_HAND;
			
			case LHAND:
				return Item.SLOT_L_HAND;
			
			case GLOVES:
				return Item.SLOT_GLOVES;
			
			case CHEST:
				return item.getItem().getBodyPart();
			
			case LEGS:
				return Item.SLOT_LEGS;
			
			case CLOAK:
				return Item.SLOT_BACK;
			
			case FEET:
				return Item.SLOT_FEET;
			
			default:
				return -1;
		}
	}
	
	/**
	 * Equip an {@link ItemInstance} in {@link Paperdoll} slot.
	 * @param item : The {@link ItemInstance} to set.
	 */
	public void equipItem(ItemInstance item)
	{
		switch (item.getItem().getBodyPart())
		{
			case Item.SLOT_LR_HAND:
				setPaperdollItem(Paperdoll.LHAND, null);
				setPaperdollItem(Paperdoll.RHAND, item);
				break;
			
			case Item.SLOT_L_HAND:
				ItemInstance rh = getItemFrom(Paperdoll.RHAND);
				if (rh != null && rh.getItem().getBodyPart() == Item.SLOT_LR_HAND && !((rh.getItemType() == WeaponType.BOW && item.getItemType() == EtcItemType.ARROW) || (rh.getItemType() == WeaponType.FISHINGROD && item.getItemType() == EtcItemType.LURE)))
					setPaperdollItem(Paperdoll.RHAND, null);
				
				setPaperdollItem(Paperdoll.LHAND, item);
				break;
			
			case Item.SLOT_R_HAND:
				setPaperdollItem(Paperdoll.RHAND, item);
				break;
			
			case Item.SLOT_L_EAR, Item.SLOT_R_EAR, Item.SLOT_L_EAR | Item.SLOT_R_EAR:
				if (getItemFrom(Paperdoll.LEAR) == null)
					setPaperdollItem(Paperdoll.LEAR, item);
				else if (getItemFrom(Paperdoll.REAR) == null)
					setPaperdollItem(Paperdoll.REAR, item);
				else
				{
					if (getItemIdFrom(Paperdoll.REAR) == item.getItemId())
						setPaperdollItem(Paperdoll.LEAR, item);
					else if (getItemIdFrom(Paperdoll.LEAR) == item.getItemId())
						setPaperdollItem(Paperdoll.REAR, item);
					else
						setPaperdollItem(Paperdoll.LEAR, item);
				}
				break;
			
			case Item.SLOT_L_FINGER, Item.SLOT_R_FINGER, Item.SLOT_L_FINGER | Item.SLOT_R_FINGER:
				if (getItemFrom(Paperdoll.LFINGER) == null)
					setPaperdollItem(Paperdoll.LFINGER, item);
				else if (getItemFrom(Paperdoll.RFINGER) == null)
					setPaperdollItem(Paperdoll.RFINGER, item);
				else
				{
					if (getItemIdFrom(Paperdoll.RFINGER) == item.getItemId())
						setPaperdollItem(Paperdoll.LFINGER, item);
					else if (getItemIdFrom(Paperdoll.LFINGER) == item.getItemId())
						setPaperdollItem(Paperdoll.RFINGER, item);
					else
						setPaperdollItem(Paperdoll.LFINGER, item);
				}
				break;
			
			case Item.SLOT_NECK:
				setPaperdollItem(Paperdoll.NECK, item);
				break;
			
			case Item.SLOT_FULL_ARMOR:
				setPaperdollItem(Paperdoll.LEGS, null);
				setPaperdollItem(Paperdoll.CHEST, item);
				break;
			
			case Item.SLOT_CHEST:
				setPaperdollItem(Paperdoll.CHEST, item);
				break;
			
			case Item.SLOT_LEGS:
				final ItemInstance chest = getItemFrom(Paperdoll.CHEST);
				if (chest != null && chest.getItem().getBodyPart() == Item.SLOT_FULL_ARMOR)
					setPaperdollItem(Paperdoll.CHEST, null);
				
				setPaperdollItem(Paperdoll.LEGS, item);
				break;
			
			case Item.SLOT_FEET:
				setPaperdollItem(Paperdoll.FEET, item);
				break;
			
			case Item.SLOT_GLOVES:
				setPaperdollItem(Paperdoll.GLOVES, item);
				break;
			
			case Item.SLOT_HEAD:
				setPaperdollItem(Paperdoll.HEAD, item);
				break;
			
			case Item.SLOT_FACE:
				final ItemInstance hair = getItemFrom(Paperdoll.HAIR);
				if (hair != null && hair.getItem().getBodyPart() == Item.SLOT_HAIRALL)
					setPaperdollItem(Paperdoll.HAIR, null);
				
				setPaperdollItem(Paperdoll.FACE, item);
				break;
			
			case Item.SLOT_HAIR:
				final ItemInstance face = getItemFrom(Paperdoll.FACE);
				if (face != null && face.getItem().getBodyPart() == Item.SLOT_HAIRALL)
					setPaperdollItem(Paperdoll.FACE, null);
				
				setPaperdollItem(Paperdoll.HAIR, item);
				break;
			
			case Item.SLOT_HAIRALL:
				setPaperdollItem(Paperdoll.FACE, null);
				setPaperdollItem(Paperdoll.HAIR, item);
				break;
			
			case Item.SLOT_UNDERWEAR:
				setPaperdollItem(Paperdoll.UNDER, item);
				break;
			
			case Item.SLOT_BACK:
				setPaperdollItem(Paperdoll.CLOAK, item);
				break;
			
			case Item.SLOT_ALLDRESS:
				setPaperdollItem(Paperdoll.LEGS, null);
				if (item.getItemId() != 6408)
				{
					setPaperdollItem(Paperdoll.LHAND, null);
					setPaperdollItem(Paperdoll.RHAND, null);
				}
				setPaperdollItem(Paperdoll.HEAD, null);
				setPaperdollItem(Paperdoll.FEET, null);
				setPaperdollItem(Paperdoll.GLOVES, null);
				setPaperdollItem(Paperdoll.CHEST, item);
				break;
			
			default:
				LOGGER.warn("Unknown body slot {} for itemId {}.", item.getItem().getBodyPart(), item.getItemId());
		}
	}
	
	/**
	 * Equip an {@link ItemInstance} and return alterations.<br>
	 * <br>
	 * <b>If you dont need return value use {@link Inventory#equipItem(ItemInstance)} instead.</b>
	 * @param item : The {@link ItemInstance} to equip.
	 * @return The array of altered {@link ItemInstance}s.
	 */
	public ItemInstance[] equipItemAndRecord(ItemInstance item)
	{
		final ChangeRecorderListener recorder = new ChangeRecorderListener(this);
		
		try
		{
			equipItem(item);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Equip an {@link ItemInstance}.<br>
	 * <br>
	 * Concerning pets, armors go to Paperdoll.CHEST and weapon to Paperdoll.RHAND.
	 * @param item : The {@link ItemInstance} to equip.
	 */
	public void equipPetItem(ItemInstance item)
	{
		if (item.isPetItem())
		{
			if (item.getItemType() == WeaponType.PET)
				setPaperdollItem(Paperdoll.RHAND, item);
			else if (item.getItemType() == ArmorType.PET)
				setPaperdollItem(Paperdoll.CHEST, item);
		}
	}
	
	/**
	 * Unequip an {@link ItemInstance} and return alterations.
	 * @param item : The {@link ItemInstance} used to find the slot back.
	 * @return The array of altered {@link ItemInstance}s.
	 */
	public ItemInstance[] unequipItemInBodySlotAndRecord(ItemInstance item)
	{
		final ChangeRecorderListener recorder = new ChangeRecorderListener(this);
		
		try
		{
			unequipItemInBodySlot(getSlotFromItem(item));
			
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Unequip an {@link ItemInstance} and return alterations.
	 * @param itemSlot : The item slot to test.
	 * @return The array of altered {@link ItemInstance}s.
	 */
	public ItemInstance[] unequipItemInBodySlotAndRecord(int itemSlot)
	{
		final ChangeRecorderListener recorder = new ChangeRecorderListener(this);
		
		try
		{
			unequipItemInBodySlot(itemSlot);
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Unequip an {@link ItemInstance} by its {@link Paperdoll} id.
	 * @param slot : The {@link Paperdoll} id.
	 * @return The unequipped {@link ItemInstance}, or null if already unequipped.
	 */
	public ItemInstance unequipItemInSlot(int slot)
	{
		return setPaperdollItem(Paperdoll.getEnumById(slot), null);
	}
	
	/**
	 * Unequip an {@link ItemInstance} and return alterations.
	 * @param slot : The slot to test.
	 * @return The array of altered {@link ItemInstance}s.
	 */
	public ItemInstance[] unequipItemInSlotAndRecord(int slot)
	{
		final ChangeRecorderListener recorder = new ChangeRecorderListener(this);
		
		try
		{
			unequipItemInSlot(slot);
			if (getOwner() instanceof Player player)
				player.refreshExpertisePenalty();
		}
		finally
		{
			removePaperdollListener(recorder);
		}
		return recorder.getChangedItems();
	}
	
	/**
	 * Unequip an {@link ItemInstance} using its item slot.
	 * @param itemSlot : The item slot used to find the {@link Paperdoll} slot.
	 * @return The unequipped {@link ItemInstance}, or null if already unequipped.
	 */
	public ItemInstance unequipItemInBodySlot(int itemSlot)
	{
		Paperdoll slot = Paperdoll.NULL;
		
		switch (itemSlot)
		{
			case Item.SLOT_L_EAR:
				slot = Paperdoll.LEAR;
				break;
			
			case Item.SLOT_R_EAR:
				slot = Paperdoll.REAR;
				break;
			
			case Item.SLOT_NECK:
				slot = Paperdoll.NECK;
				break;
			
			case Item.SLOT_R_FINGER:
				slot = Paperdoll.RFINGER;
				break;
			
			case Item.SLOT_L_FINGER:
				slot = Paperdoll.LFINGER;
				break;
			
			case Item.SLOT_HAIR:
				slot = Paperdoll.HAIR;
				break;
			
			case Item.SLOT_FACE:
				slot = Paperdoll.FACE;
				break;
			
			case Item.SLOT_HAIRALL:
				setPaperdollItem(Paperdoll.FACE, null);
				slot = Paperdoll.FACE;
				break;
			
			case Item.SLOT_HEAD:
				slot = Paperdoll.HEAD;
				break;
			
			case Item.SLOT_R_HAND, Item.SLOT_LR_HAND:
				slot = Paperdoll.RHAND;
				break;
			
			case Item.SLOT_L_HAND:
				slot = Paperdoll.LHAND;
				break;
			
			case Item.SLOT_GLOVES:
				slot = Paperdoll.GLOVES;
				break;
			
			case Item.SLOT_CHEST, Item.SLOT_FULL_ARMOR, Item.SLOT_ALLDRESS:
				slot = Paperdoll.CHEST;
				break;
			
			case Item.SLOT_LEGS:
				slot = Paperdoll.LEGS;
				break;
			
			case Item.SLOT_BACK:
				slot = Paperdoll.CLOAK;
				break;
			
			case Item.SLOT_FEET:
				slot = Paperdoll.FEET;
				break;
			
			case Item.SLOT_UNDERWEAR:
				slot = Paperdoll.UNDER;
				break;
			
			default:
				LOGGER.warn("Slot type {} is unhandled.", slot);
		}
		
		return (slot == Paperdoll.NULL) ? null : setPaperdollItem(slot, null);
	}
	
	public void addUpdate(ItemInstance item, ItemState state)
	{
		if (item == null)
			return;
		
		if (Config.LOG_ITEMS && !item.isStackable())
		{
			final LogRecord logRecord = new LogRecord(Level.INFO, state.toString());
			logRecord.setLoggerName("item");
			logRecord.setParameters(new Object[]
			{
				getOwner(),
				item
			});
			ITEM_LOG.log(logRecord);
		}
		
		if (!_updateList.isEmpty() && item.isStackable())
		{
			final ItemInfo info = _updateList.stream().filter(i -> i.getObjectId() == item.getObjectId() && i.getState() == state).findAny().orElse(null);
			if (info != null)
			{
				info.setCount(item.getCount());
				return;
			}
		}
		
		_updateList.add(new ItemInfo(item, state));
		
		InventoryUpdateTaskManager.getInstance().add(this);
	}
	
	public Queue<ItemInfo> getUpdateList()
	{
		return _updateList;
	}
	
	public void clearUpdateList()
	{
		_updateList.clear();
	}
	
	/**
	 * @param bow : The {@link Item} designating the bow.
	 * @return The {@link ItemInstance} pointing out arrows.
	 */
	public ItemInstance findArrowForBow(Item bow)
	{
		if (bow == null)
			return null;
		
		switch (bow.getCrystalType())
		{
			case NONE:
				return getItemByItemId(17);
				
			case D:
				return getItemByItemId(1341);
				
			case C:
				return getItemByItemId(1342);
				
			case B:
				return getItemByItemId(1343);
				
			case A:
				return getItemByItemId(1344);
				
			case S:
				return getItemByItemId(1345);
				
			default:
				return null;
		}
	}
}