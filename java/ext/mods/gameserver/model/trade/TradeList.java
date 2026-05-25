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
package ext.mods.gameserver.model.trade;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.itemcontainer.PcInventory;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class TradeList extends CopyOnWriteArrayList<TradeItem>
{
	private static final long serialVersionUID = 1L;
	
	private final Player _owner;
	
	private Player _partner;
	private String _title;
	
	private AtomicBoolean _isPackaged = new AtomicBoolean();
	private AtomicBoolean _isConfirmed = new AtomicBoolean();
	private AtomicBoolean _isLocked = new AtomicBoolean();
	
	public TradeList(Player owner)
	{
		_owner = owner;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		
		_partner = null;
		
		_isPackaged.set(false);
		_isConfirmed.set(false);
		_isLocked.set(false);
	}
	
	@Override
	public String toString()
	{
		return "TradeList [owner=" + _owner + ", partner=" + _partner + ", title=" + _title + ", isPackaged=" + _isPackaged.get() + ", isConfirmed=" + _isConfirmed.get() + ", isLocked=" + _isLocked.get() + "]";
	}
	
	public Player getOwner()
	{
		return _owner;
	}
	
	public Player getPartner()
	{
		return _partner;
	}
	
	public void setPartner(Player partner)
	{
		_partner = partner;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	public boolean isPackaged()
	{
		return _isPackaged.get();
	}
	
	public void setPackaged(boolean value)
	{
		_isPackaged.set(value);
	}
	
	public boolean isConfirmed()
	{
		return _isConfirmed.get();
	}
	
	public AtomicBoolean getLock()
	{
		return _isLocked;
	}
	
	public boolean isLocked()
	{
		return _isLocked.get();
	}
	
	/**
	 * Lock this {@link TradeList}, meaning than no further changes are allowed.
	 */
	public void lock()
	{
		_isLocked.set(true);
	}
	
	/**
	 * @param inventory : The {@link PcInventory} to test.
	 * @return A cloned {@link List} of this {@link TradeList} adjusted to {@link PcInventory} available items.
	 */
	public List<TradeItem> getAvailableItems(PcInventory inventory)
	{
		return stream().peek(inventory::adjustAvailableItem).toList();
	}
	
	/**
	 * Create a {@link TradeItem} based on an existing {@link ItemInstance}, and add it to this {@link TradeList}.
	 * @param objectId : The {@link WorldObject} objectId to test.
	 * @param count : The amount of newly formed {@link TradeItem}.
	 * @param price : The price of newly formed {@link TradeItem}.
	 * @return A {@link TradeItem} based on {@link ItemInstance}, which is itself retrieved from its objectId from {@link World#getObject(int)}.
	 */
	public TradeItem addItem(int objectId, int count, int price)
	{
		if (isConfirmed() || isLocked())
			return null;
		
		final WorldObject object = World.getInstance().getObject(objectId);
		if (!(object instanceof ItemInstance item))
			return null;
		
		if (!item.isTradable() || item.isQuestItem())
			return null;
		
		if (count <= 0 || count > item.getCount())
			return null;
		
		if (!item.isStackable() && count > 1)
			return null;
		
		if ((Integer.MAX_VALUE / count) < price)
			return null;
		
		for (TradeItem checkItem : this)
		{
			if (checkItem.getObjectId() == objectId)
			{
				final int newCount = checkItem.getCount() + count;
				if (item.getCount() < newCount)
					return null;
				
				checkItem.setCount(newCount);
				return checkItem;
			}
		}
		
		final TradeItem tradeItem = new TradeItem(item, count, price);
		add(tradeItem);
		
		return tradeItem;
	}
	
	/**
	 * Create a {@link TradeItem} based on itemId, and add it to this {@link TradeList}.
	 * @param itemId : The itemId of newly formed {@link TradeItem}.
	 * @param count : The amount of newly formed {@link TradeItem}.
	 * @param price : The price of newly formed {@link TradeItem}.
	 * @param enchant : The enchant value of newly formed {@link TradeItem}.
	 * @return A {@link TradeItem} based on itemId.
	 */
	public TradeItem addItemByItemId(int itemId, int count, int price, int enchant)
	{
		if (isConfirmed() || isLocked())
			return null;
		
		final Item item = ItemData.getInstance().getTemplate(itemId);
		if (item == null)
			return null;
		
		if (!item.isTradable() || item.isQuestItem())
			return null;
		
		if (!item.isStackable() && count > 1)
			return null;
		
		if ((Integer.MAX_VALUE / count) < price)
			return null;
		
		final TradeItem tradeItem = new TradeItem(item, count, price, enchant);
		add(tradeItem);
		
		return tradeItem;
	}
	
	/**
	 * Remove or decrease amount of a {@link TradeItem} from this {@link TradeList}, by either its objectId or itemId.
	 * @param objectId : The objectId to test.
	 * @param itemId : The itemId ot test.
	 * @param count : The amount to remove.
	 */
	private void removeItem(int objectId, int itemId, int count)
	{
		if (isConfirmed() || isLocked())
			return;
		
		for (TradeItem tradeItem : this)
		{
			if (tradeItem.getObjectId() == objectId || tradeItem.getItem().getItemId() == itemId)
			{
				if (count == -1)
					remove(tradeItem);
				else
				{
					tradeItem.setCount(tradeItem.getCount() - count);
					tradeItem.setQuantity(tradeItem.getQuantity() - count);
					
					if (tradeItem.getQuantity() <= 0)
						remove(tradeItem);
				}
				break;
			}
		}
	}
	
	/**
	 * Update {@link TradeItem}s from this {@link TradeList} according to their quantity in owner inventory.
	 * @param isBuyList : If True, we don't check {@link TradeItem}s item count integrity. We still check if item exists in inventory, though.
	 */
	public void updateItems(boolean isBuyList)
	{
		if (isConfirmed() || isLocked())
			return;
		
		for (TradeItem tradeItem : this)
		{
			if (isBuyList)
			{
				final ItemInstance item = _owner.getInventory().getItemByItemId(tradeItem.getItemId());
				if (item == null)
					remove(tradeItem);
			}
			else
			{
				final ItemInstance item = _owner.getInventory().getItemByObjectId(tradeItem.getObjectId());
				if (item == null || tradeItem.getCount() < 1 || item.isEquipped())
					remove(tradeItem);
				else if (item.getCount() < tradeItem.getCount())
					tradeItem.setCount(item.getCount());
			}
		}
	}
	
	/**
	 * Confirm this {@link TradeList}, cancelling the trade if checks aren't properly passed (distance, items manipulation, etc).<br>
	 * <br>
	 * In case partner already confirmed its {@link TradeList}, then proceed to the exchange. Otherwise confirm this {@link TradeList}.
	 */
	public void confirm()
	{
		if (isConfirmed() || isLocked())
			return;
		
		if (_partner == null)
		{
			_owner.cancelActiveTrade();
			return;
		}
		
		final TradeList partnerList = _partner.getActiveTradeList();
		if (partnerList == null)
		{
			_owner.cancelActiveTrade();
			return;
		}
		
		if (!_isConfirmed.compareAndSet(false, true))
			return;
		
		if (partnerList.isConfirmed())
		{
			if (_isLocked.compareAndSet(false, true) && partnerList.getLock().compareAndSet(false, true))
			{
				if (!validate(_partner, true) || !partnerList.validate(_owner, true))
				{
					_owner.cancelActiveTrade();
					return;
				}
				
				doExchange(partnerList);
			}
		}
		else
		{
			if (!validate(_partner, false) || !partnerList.validate(_owner, false))
			{
				_owner.cancelActiveTrade();
				return;
			}
			
			_partner.onTradeConfirm(_owner);
		}
	}
	
	/**
	 * Test the validity of this {@link TradeList}.
	 * @param partner : The {@link Player} partner to test.
	 * @param isCheckingItems : If True, we also check item manipulation.
	 * @return True if all tests passed, false otherwise.
	 */
	private boolean validate(Player partner, boolean isCheckingItems)
	{
		if (_owner == null || !_owner.isOnline())
			return false;
		
		if (partner == null || !partner.isOnline() || !_owner.isIn3DRadius(partner, Npc.INTERACTION_DISTANCE))
			return false;
		
		if (isCheckingItems)
			return stream().allMatch(tradeItem -> _owner.checkItemManipulation(tradeItem.getObjectId(), tradeItem.getCount()) != null);
		
		return true;
	}
	
	/**
	 * Transfer all {@link TradeItem}s of this {@link TradeList} from {@link Player} owner inventory to {@link Player} partner.
	 * @param partner : The {@link Player} used as partner.
	 * @param owner : The {@link Player} used as owner.
	 */
	private void transferItems(Player partner, Player owner)
	{
		forEach(tradeItem -> _owner.getInventory().transferItem(tradeItem.getObjectId(), tradeItem.getCount(), partner));
	}
	
	/**
	 * Proceed to the transfer of items, if all tests successfully passed.
	 * @param partnerTradeList : The {@link TradeList} of the {@link Player} partner.
	 */
	private void doExchange(TradeList partnerTradeList)
	{
		boolean isSuccessful = true;
		
		if (!_owner.getInventory().validateTradeListWeight(partnerTradeList) || !partnerTradeList.getOwner().getInventory().validateTradeListWeight(this))
		{
			isSuccessful = false;
			
			_owner.sendPacket(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
			partnerTradeList.getOwner().sendPacket(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
		}
		else if (!_owner.getInventory().validateTradeListCapacity(partnerTradeList) || !partnerTradeList.getOwner().getInventory().validateTradeListCapacity(this))
		{
			isSuccessful = false;
			
			_owner.sendPacket(SystemMessageId.SLOTS_FULL);
			partnerTradeList.getOwner().sendPacket(SystemMessageId.SLOTS_FULL);
		}
		else if (!_owner.getInventory().validateTradeListCount(partnerTradeList) || !partnerTradeList.getOwner().getInventory().validateTradeListCount(this))
			isSuccessful = false;
		else if (isEmpty() && partnerTradeList.isEmpty())
			isSuccessful = false;
		else
		{
			partnerTradeList.transferItems(_owner, partnerTradeList.getOwner());
			transferItems(partnerTradeList.getOwner(), _owner);
		}
		
		_owner.onTradeFinish(isSuccessful);
		partnerTradeList.getOwner().onTradeFinish(isSuccessful);
	}
	
	/**
	 * Buy items from this {@link TradeList}.
	 * @param player : The {@link Player} who tries to buy.
	 * @param items : The array of {@link ItemRequest}s to test.
	 * @return True if all checks passed and the buy was successful, or false otherwise.
	 */
	public boolean privateStoreBuy(Player player, ItemRequest[] items)
	{
		if (isConfirmed() || isLocked())
			return false;
		
		if (!validate(player, false))
			return false;
		
		int totalSlots = 0;
		int totalWeight = 0;
		long totalPrice = 0;
		
		final PcInventory ownerInventory = _owner.getInventory();
		final PcInventory playerInventory = player.getInventory();
		
		for (ItemRequest item : items)
		{
			if (item.getCount() < 1)
				return false;
			
			final TradeItem tradeItem = stream().filter(ti -> ti.getObjectId() == item.getObjectId() && ti.getPrice() == item.getPrice()).findFirst().orElse(null);
			if (tradeItem == null)
				return false;
			
			final Item template = ItemData.getInstance().getTemplate(tradeItem.getItemId());
			if (template == null)
				return false;
			
			final ItemInstance oldItem = _owner.checkItemManipulation(item.getObjectId(), item.getCount());
			if (oldItem == null || !oldItem.isTradable())
				return false;
			
			if ((Integer.MAX_VALUE / item.getCount()) < item.getPrice())
				return false;
			
			totalPrice += item.getCount() * item.getPrice();
			
			if (Integer.MAX_VALUE < totalPrice || totalPrice < 0)
				return false;
			
			totalWeight += item.getCount() * template.getWeight();
			
			if (!template.isStackable())
				totalSlots += item.getCount();
			else if (playerInventory.getItemByItemId(item.getItemId()) == null)
				totalSlots++;
		}
		
		if (totalPrice > playerInventory.getAdena())
		{
			player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
			return false;
		}
		
		if (!playerInventory.validateWeight(totalWeight))
		{
			player.sendPacket(SystemMessageId.WEIGHT_LIMIT_EXCEEDED);
			return false;
		}
		
		if (!playerInventory.validateCapacity(totalSlots))
		{
			player.sendPacket(SystemMessageId.SLOTS_FULL);
			return false;
		}
		
		for (ItemRequest item : items)
		{
			final ItemInstance newItem = ownerInventory.transferItem(item.getObjectId(), item.getCount(), player);
			if (newItem == null)
			{
				totalPrice -= item.getCount() * item.getPrice();
				continue;
			}
			
			removeItem(item.getObjectId(), -1, item.getCount());
			
			if (newItem.isStackable())
			{
				_owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PURCHASED_S3_S2_S).addString(player.getName()).addItemName(newItem.getItemId()).addNumber(item.getCount()));
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S3_S2_S_FROM_S1).addString(_owner.getName()).addItemName(newItem.getItemId()).addNumber(item.getCount()));
			}
			else if (newItem.getEnchantLevel() > 0)
			{
				_owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PURCHASED_S2_S3).addString(player.getName()).addNumber(newItem.getEnchantLevel()).addItemName(newItem.getItemId()));
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S2_S3_FROM_S1).addString(_owner.getName()).addNumber(newItem.getEnchantLevel()).addItemName(newItem.getItemId()));
			}
			else
			{
				_owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PURCHASED_S2).addString(player.getName()).addItemName(newItem.getItemId()));
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S2_FROM_S1).addString(_owner.getName()).addItemName(newItem.getItemId()));
			}
		}
		
		if (totalPrice > 0)
		{
			playerInventory.reduceAdena((int) totalPrice);
			ownerInventory.addAdena((int) totalPrice);
		}
		
		return true;
	}
	
	/**
	 * Sell items from this {@link TradeList}.
	 * @param player : The {@link Player} who tries to sell.
	 * @param items : The array of {@link ItemRequest} to test.
	 * @return True if all checks passed and the buy was successful, or false otherwise.
	 */
	public boolean privateStoreSell(Player player, ItemRequest[] items)
	{
		if (isConfirmed() || isLocked())
			return false;
		
		if (!validate(player, false))
			return false;
		
		final PcInventory ownerInventory = _owner.getInventory();
		final PcInventory playerInventory = player.getInventory();
		
		long totalPrice = 0;
		
		for (ItemRequest item : items)
		{
			if (item.getCount() < 1)
				return false;
			
			final Item template = ItemData.getInstance().getTemplate(item.getItemId());
			if (template == null)
				return false;
			
			if (stream().noneMatch(ti -> ti.getItemId() == item.getItemId() && ti.getPrice() == item.getPrice()))
				return false;
			
			final ItemInstance oldItem = player.checkItemManipulation(item.getObjectId(), item.getCount());
			if (oldItem == null || !oldItem.isTradable() || oldItem.getItemId() != item.getItemId() || oldItem.getEnchantLevel() != item.getEnchant())
				return false;
			
			if ((Integer.MAX_VALUE / item.getCount()) < item.getPrice())
				return false;
			
			totalPrice += item.getCount() * item.getPrice();
			
			if (Integer.MAX_VALUE < totalPrice || totalPrice < 0)
				return false;
		}
		
		if (totalPrice > ownerInventory.getAdena())
		{
			player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
			return false;
		}
		
		for (ItemRequest item : items)
		{
			final ItemInstance newItem = playerInventory.transferItem(item.getObjectId(), item.getCount(), _owner);
			if (newItem == null)
			{
				totalPrice -= item.getCount() * item.getPrice();
				continue;
			}
			
			removeItem(-1, item.getItemId(), item.getCount());
			
			if (newItem.isStackable())
			{
				_owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S3_S2_S_FROM_S1).addString(player.getName()).addItemName(newItem.getItemId()).addNumber(item.getCount()));
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PURCHASED_S3_S2_S).addString(_owner.getName()).addItemName(newItem.getItemId()).addNumber(item.getCount()));
			}
			else if (newItem.getEnchantLevel() > 0)
			{
				_owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S2_S3_FROM_S1).addString(player.getName()).addNumber(newItem.getEnchantLevel()).addItemName(newItem.getItemId()));
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PURCHASED_S2_S3).addString(_owner.getName()).addNumber(newItem.getEnchantLevel()).addItemName(newItem.getItemId()));
			}
			else
			{
				_owner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PURCHASED_S2_FROM_S1).addString(player.getName()).addItemName(newItem.getItemId()));
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PURCHASED_S2).addString(_owner.getName()).addItemName(newItem.getItemId()));
			}
		}
		
		if (totalPrice > 0)
		{
			ownerInventory.reduceAdena((int) totalPrice);
			playerInventory.addAdena((int) totalPrice);
		}
		
		return true;
	}
}