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

public class ItemRequest
{
	protected int _objectId;
	protected int _itemId;
	protected int _count;
	protected int _price;
	protected int _enchant;
	
	public ItemRequest(int objectId, int count, int price)
	{
		_objectId = objectId;
		_count = count;
		_price = price;
	}
	
	public ItemRequest(int objectId, int itemId, int count, int price, int enchant)
	{
		_objectId = objectId;
		_itemId = itemId;
		_count = count;
		_price = price;
		_enchant = enchant;
	}
	
	@Override
	public String toString()
	{
		return "ItemRequest [objectId=" + _objectId + ", itemId=" + _itemId + ", count=" + _count + ", price=" + _price + ", enchant=" + _enchant + "]";
	}
	
	public int getObjectId()
	{
		return _objectId;
	}
	
	public void setObjectId(int objectId)
	{
		_objectId = objectId;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public void setCount(int count)
	{
		_count = count;
	}
	
	public int getPrice()
	{
		return _price;
	}
	
	public void setPrice(int price)
	{
		_price = price;
	}
	
	public int getEnchant()
	{
		return _enchant;
	}
	
	public void setEnchant(int enchant)
	{
		_enchant = enchant;
	}
}