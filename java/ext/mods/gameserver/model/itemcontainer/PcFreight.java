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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import ext.mods.Config;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;

public class PcFreight extends ItemContainer
{
	private final Player _owner;
	
	private int _activeLocationId;
	private int _tempOwnerId = 0;
	
	public PcFreight(Player owner)
	{
		_owner = owner;
	}
	
	@Override
	public String getName()
	{
		return "Freight";
	}
	
	@Override
	public Player getOwner()
	{
		return _owner;
	}
	
	@Override
	public ItemLocation getBaseLocation()
	{
		return ItemLocation.FREIGHT;
	}
	
	public void setActiveLocation(int locationId)
	{
		_activeLocationId = locationId;
	}
	
	@Override
	public int getSize()
	{
		int size = 0;
		for (ItemInstance item : _items)
		{
			if (item.getLocationSlot() == 0 || _activeLocationId == 0 || item.getLocationSlot() == _activeLocationId)
				size++;
		}
		return size;
	}
	
	@Override
	public Set<ItemInstance> getItems()
	{
		if (_items.isEmpty())
			return Collections.emptySet();
		
		return _items.stream().filter(i -> i.getLocationSlot() == 0 || i.getLocationSlot() == _activeLocationId).collect(Collectors.toSet());
	}
	
	@Override
	public ItemInstance getItemByItemId(int itemId)
	{
		for (ItemInstance item : _items)
		{
			if (item.getItemId() == itemId && (item.getLocationSlot() == 0 || _activeLocationId == 0 || item.getLocationSlot() == _activeLocationId))
				return item;
		}
		return null;
	}
	
	@Override
	protected void addBasicItem(ItemInstance item)
	{
		super.addBasicItem(item);
		
		if (_activeLocationId > 0)
			item.setLocation(item.getLocation(), _activeLocationId);
	}
	
	@Override
	public void restore()
	{
		int locationId = _activeLocationId;
		_activeLocationId = 0;
		
		super.restore();
		
		_activeLocationId = locationId;
	}
	
	@Override
	public boolean validateCapacity(int slotCount)
	{
		if (slotCount == 0)
			return true;
		
		return getSize() + slotCount <= ((_owner == null) ? Config.FREIGHT_SLOTS : _owner.getStatus().getFreightLimit());
	}
	
	@Override
	public int getOwnerId()
	{
		return (_owner == null) ? _tempOwnerId : super.getOwnerId();
	}
	
	/**
	 * This provides support to load a new PcFreight without owner so that transactions can be done
	 * @param val The id of the owner.
	 */
	public void doQuickRestore(int val)
	{
		_tempOwnerId = val;
		
		restore();
	}
}