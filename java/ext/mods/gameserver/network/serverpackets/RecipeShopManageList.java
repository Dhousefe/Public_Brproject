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
package ext.mods.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.craft.ManufactureList;
import ext.mods.gameserver.model.records.ManufactureItem;
import ext.mods.gameserver.model.records.Recipe;

public class RecipeShopManageList extends L2GameServerPacket
{
	private final Player _player;
	private final Collection<Recipe> _recipes;
	private final List<ManufactureItem> _items = new ArrayList<>();
	
	public RecipeShopManageList(Player player, boolean isDwarven)
	{
		_player = player;
		_recipes = player.getRecipeBook().get(isDwarven && player.hasDwarvenCraft());
		
		final ManufactureList manufactureList = player.getManufactureList();
		manufactureList.setState(isDwarven);
		
		_items.addAll(manufactureList);
		_items.removeIf(i -> i.isDwarven() != isDwarven || !player.getRecipeBook().hasRecipe(i.recipeId()));
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xd8);
		writeD(_player.getObjectId());
		writeD(_player.getAdena());
		writeD(_player.getManufactureList().isDwarven() ? 0x00 : 0x01);
		
		if (_recipes == null)
			writeD(0);
		else
		{
			writeD(_recipes.size());
			
			int i = 0;
			for (Recipe recipe : _recipes)
			{
				writeD(recipe.id());
				writeD(++i);
			}
		}
		
		writeD(_items.size());
		
		for (ManufactureItem item : _items)
		{
			writeD(item.recipeId());
			writeD(0x00);
			writeD(item.cost());
		}
	}
}