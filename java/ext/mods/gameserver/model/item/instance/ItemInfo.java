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
package ext.mods.gameserver.model.item.instance;

import ext.mods.gameserver.enums.items.ItemState;
import ext.mods.gameserver.model.item.kind.Item;

/**
 * Get all information from ItemInstance to generate ItemInfo.
 */
public class ItemInfo
{
	private int _objectId;
	private int _enchant;
	private int _augmentation;
	private int _count;
	private int _type1;
	private int _type2;
	private int _equipped;
	private int _manaLeft;
	
	private Item _item;
	
	private ItemState _state;
	
	public ItemInfo(ItemInstance item, ItemState state)
	{
		if (item == null)
			return;
		
		_objectId = item.getObjectId();
		_enchant = item.getEnchantLevel();
		_augmentation = (item.isAugmented()) ? item.getAugmentation().getId() : 0;
		_count = item.getCount();
		_type1 = item.getCustomType1();
		_type2 = item.getCustomType2();
		_equipped = item.isEquipped() ? 1 : 0;
		_manaLeft = item.getManaLeft();
		
		_item = item.getItem();
		
		_state = state;
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public Item getItem()
	{
		return _item;
	}
	
	public int getEnchant()
	{
		return _enchant;
	}
	
	public int getAugmentation()
	{
		return _augmentation;
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public void setCount(int count)
	{
		_count = count;
	}
	
	public int getCustomType1()
	{
		return _type1;
	}
	
	public int getCustomType2()
	{
		return _type2;
	}
	
	public int getEquipped()
	{
		return _equipped;
	}
	
	public ItemState getState()
	{
		return _state;
	}
	
	public int getManaLeft()
	{
		return _manaLeft;
	}
	
	public int getDisplayedManaLeft()
	{
		return _manaLeft / 60;
	}
}