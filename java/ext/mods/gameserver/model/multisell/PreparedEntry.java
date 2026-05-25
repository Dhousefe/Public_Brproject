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

import java.util.ArrayList;

import ext.mods.gameserver.model.item.instance.ItemInstance;

/**
 * A dynamic layer of {@link Entry}, which holds the tax amount and can retain previous {@link ItemInstance} enchantment.
 */
public class PreparedEntry extends Entry
{
	private int _taxAmount = 0;
	
	public PreparedEntry(Entry template, ItemInstance item, boolean applyTaxes, boolean maintainEnchantment, double taxRate)
	{
		int adenaAmount = 0;
		
		_ingredients = new ArrayList<>(template.getIngredients().size());
		for (Ingredient ing : template.getIngredients())
		{
			if (ing.getItemId() == 57)
			{
				if (ing.isTaxIngredient())
				{
					if (applyTaxes)
						_taxAmount += Math.round(ing.getItemCount() * taxRate);
				}
				else
					adenaAmount += ing.getItemCount();
				
				continue;
			}
			
			final Ingredient newIngredient = ing.getCopy();
			if (maintainEnchantment && item != null && ing.isArmorOrWeapon())
				newIngredient.setEnchantLevel(item.getEnchantLevel());
			
			_ingredients.add(newIngredient);
		}
		
		adenaAmount += _taxAmount;
		if (adenaAmount > 0)
			_ingredients.add(new Ingredient(57, adenaAmount, 0, false, false));
		
		_products = new ArrayList<>(template.getProducts().size());
		for (Ingredient ing : template.getProducts())
		{
			if (!ing.isStackable())
				_stackable = false;
			
			final Ingredient newProduct = ing.getCopy();
			if (maintainEnchantment && item != null && ing.isArmorOrWeapon())
				newProduct.setEnchantLevel(item.getEnchantLevel());
			
			_products.add(newProduct);
		}
	}
	
	@Override
	public final int getTaxAmount()
	{
		return _taxAmount;
	}
}