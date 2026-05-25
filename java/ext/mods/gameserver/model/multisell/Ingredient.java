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
package ext.mods.gameserver.model.multisell;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.model.item.kind.Armor;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.item.kind.Weapon;

/**
 * A datatype which is part of multisell system. It is either the "result" or the "required part" of a multisell action.
 */
public class Ingredient
{
	private int _itemId;
	private int _itemCount;
	private int _enchantmentLevel;
	
	private boolean _isTaxIngredient;
	private boolean _maintainIngredient;
	
	private Item _template = null;
	
	public Ingredient(StatSet set)
	{
		this(set.getInteger("id"), set.getInteger("count"), set.getInteger("enchant", 0), set.getBool("isTaxIngredient", false), set.getBool("maintainIngredient", false));
	}
	
	public Ingredient(int itemId, int itemCount, int enchantmentLevel, boolean isTaxIngredient, boolean maintainIngredient)
	{
		_itemId = itemId;
		_itemCount = itemCount;
		_enchantmentLevel = enchantmentLevel;
		_isTaxIngredient = isTaxIngredient;
		_maintainIngredient = maintainIngredient;
		
		if (_itemId > 0)
			_template = ItemData.getInstance().getTemplate(_itemId);
	}
	
	/**
	 * @return a new Ingredient instance with the same values as this.
	 */
	public Ingredient getCopy()
	{
		return new Ingredient(_itemId, _itemCount, _enchantmentLevel, _isTaxIngredient, _maintainIngredient);
	}
	
	public final int getItemId()
	{
		return _itemId;
	}
	
	public final void setItemId(int itemId)
	{
		_itemId = itemId;
	}
	
	public final int getItemCount()
	{
		return _itemCount;
	}
	
	public final void setItemCount(int itemCount)
	{
		_itemCount = itemCount;
	}
	
	public final int getEnchantLevel()
	{
		return _enchantmentLevel;
	}
	
	public final void setEnchantLevel(int enchantmentLevel)
	{
		_enchantmentLevel = enchantmentLevel;
	}
	
	public final boolean isTaxIngredient()
	{
		return _isTaxIngredient;
	}
	
	public final void setIsTaxIngredient(boolean isTaxIngredient)
	{
		_isTaxIngredient = isTaxIngredient;
	}
	
	public final boolean getMaintainIngredient()
	{
		return _maintainIngredient;
	}
	
	public final void setMaintainIngredient(boolean maintainIngredient)
	{
		_maintainIngredient = maintainIngredient;
	}
	
	public final Item getTemplate()
	{
		return _template;
	}
	
	public final boolean isStackable()
	{
		return _template == null || _template.isStackable();
	}
	
	public final boolean isArmorOrWeapon()
	{
		return _template instanceof Armor || _template instanceof Weapon;
	}
	
	public final int getWeight()
	{
		return (_template == null) ? 0 : _template.getWeight();
	}
}