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

import java.util.List;

/**
 * A datatype which is part of multisell system. A multisell list can hold multiple Products.<br>
 * Each Product owns a List of "required part(s)" and "result(s)" known both as {@link Ingredient}s.
 */
public class Entry
{
	protected List<Ingredient> _ingredients;
	protected List<Ingredient> _products;
	protected boolean _stackable = true;
	
	public Entry(final List<Ingredient> ingredients, final List<Ingredient> products)
	{
		_ingredients = ingredients;
		_products = products;
		_stackable = products.stream().allMatch(Ingredient::isStackable);
	}
	
	/**
	 * This constructor used in PreparedEntry only, ArrayLists not created.
	 */
	protected Entry()
	{
	}
	
	public List<Ingredient> getProducts()
	{
		return _products;
	}
	
	public List<Ingredient> getIngredients()
	{
		return _ingredients;
	}
	
	public boolean isStackable()
	{
		return _stackable;
	}
	
	public int getTaxAmount()
	{
		return 0;
	}
}