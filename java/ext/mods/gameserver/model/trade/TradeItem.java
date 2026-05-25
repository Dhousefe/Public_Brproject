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

import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;

public class TradeItem extends ItemRequest
{
	private Item _item;
	private int _quantity;
	
	public TradeItem(ItemInstance item, int count, int price)
	{
		super(item.getObjectId(), item.getItem().getItemId(), count, price, item.getEnchantLevel());
		
		_item = item.getItem();
		_quantity = count;
	}
	
	public TradeItem(Item item, int count, int price, int enchant)
	{
		super(0, item.getItemId(), count, price, enchant);
		
		_item = item;
		_quantity = count;
	}
	
	public TradeItem(TradeItem item, int count, int price)
	{
		super(item.getObjectId(), item.getItemId(), count, price, item.getEnchant());
		
		_item = item.getItem();
		_quantity = count;
	}
	
	@Override
	public String toString()
	{
		return "TradeItem [item=" + _item + ", quantity=" + _quantity + ", objectId=" + _objectId + ", itemId=" + _itemId + ", count=" + _count + ", price=" + _price + ", enchant=" + _enchant + "]";
	}
	
	public Item getItem()
	{
		return _item;
	}
	
	public int getQuantity()
	{
		return _quantity;
	}
	
	public void setQuantity(int quantity)
	{
		_quantity = quantity;
	}
}