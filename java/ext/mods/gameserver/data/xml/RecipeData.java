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
package ext.mods.gameserver.data.xml;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.records.Recipe;

import org.w3c.dom.Document;

/**
 * This class loads and stores {@link Recipe}s. Recipes are part of craft system, which uses a Recipe associated to items (materials) to craft another item (product).
 */
public class RecipeData implements IXmlReader
{
	private final Map<Integer, Recipe> _recipes = new HashMap<>();
	
	protected RecipeData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/recipes.xml");
		LOGGER.info("Loaded {} recipes.", _recipes.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "recipe", recipeNode ->
		{
			final StatSet set = parseAttributes(recipeNode);
			_recipes.put(set.getInteger("id"), new Recipe(set));
		}));
	}
	
	public Recipe getRecipeList(int listId)
	{
		return _recipes.get(listId);
	}
	
	public Recipe getRecipeByItemId(int itemId)
	{
		return _recipes.values().stream().filter(r -> r.recipeId() == itemId).findFirst().orElse(null);
	}
	
	public static RecipeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final RecipeData INSTANCE = new RecipeData();
	}
}