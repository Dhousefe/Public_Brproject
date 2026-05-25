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
package ext.mods.gameserver.model.actor.container.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.data.xml.RecipeData;
import ext.mods.gameserver.enums.ShortcutType;
import ext.mods.gameserver.model.Shortcut;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.ManufactureItem;
import ext.mods.gameserver.model.records.Recipe;

public class RecipeBook
{
	private static final CLogger LOGGER = new CLogger(RecipeBook.class.getName());
	
	private static final String INSERT_RECIPE = "INSERT INTO character_recipebook (charId, recipeId) VALUES (?,?)";
	private static final String DELETE_RECIPE = "DELETE FROM character_recipebook WHERE charId=? AND recipeId=?";
	private static final String LOAD_RECIPE_BOOK = "SELECT recipeId FROM character_recipebook WHERE charId=?";
	
	private final Player _owner;
	
	private final Map<Integer, Recipe> _dwarvenRecipes = new HashMap<>();
	private final Map<Integer, Recipe> _commonRecipes = new HashMap<>();
	
	public RecipeBook(Player owner)
	{
		_owner = owner;
	}
	
	/**
	 * @param isDwarven : If True, we pick Dwarven recipe book instead of Common recipe book.
	 * @return The {@link Collection} of all {@link Recipe}s of the {@link Player} owner.
	 */
	public Collection<Recipe> get(boolean isDwarven)
	{
		return (isDwarven) ? _dwarvenRecipes.values() : _commonRecipes.values();
	}
	
	/**
	 * Clear both Common or Dwarven books {@link Recipe} entries for the {@link Player} owner.
	 */
	public void clear()
	{
		_commonRecipes.clear();
		_dwarvenRecipes.clear();
	}
	
	/**
	 * @param recipeId : The id of the {@link Recipe} to check for the {@link Player} owner.
	 * @return True if the {@link Player} has the recipe on either Common or Dwarven books, otherwise false.
	 */
	public boolean hasRecipe(int recipeId)
	{
		return _dwarvenRecipes.containsKey(recipeId) || _commonRecipes.containsKey(recipeId);
	}
	
	/**
	 * @param recipeId : The id of the {@link Recipe} to check for the {@link Player} owner.
	 * @param isDwarven : If True we check Dwarven book, otherwise we check Common book.
	 * @return True if the {@link Player} has the recipe on Common or Dwarven books, otherwise false.
	 */
	public boolean hasRecipeOnSpecificBook(int recipeId, boolean isDwarven)
	{
		return (isDwarven) ? _dwarvenRecipes.containsKey(recipeId) : _commonRecipes.containsKey(recipeId);
	}
	
	/**
	 * @param itemsToCheck : The {@link ManufactureItem} array to test.
	 * @return True if the {@link ManufactureItem} array set as parameter successfully pass checks, false otherwise.
	 */
	public boolean canPassManufactureProcess(ManufactureItem[] itemsToCheck)
	{
		for (ManufactureItem itemToCheck : itemsToCheck)
		{
			if (!hasRecipeOnSpecificBook(itemToCheck.recipeId(), itemToCheck.isDwarven()))
				return false;
		}
		return true;
	}
	
	/**
	 * Put a new {@link Recipe} to either Common or Dwarven book of the {@link Player} owner.
	 * @param recipe : The {@link Recipe} to add.
	 * @param isDwarven : If True, set it to Dwarven book, otherwise set it to Common book.
	 * @param saveOnDb : If True, we save it on database.
	 */
	public void putRecipe(Recipe recipe, boolean isDwarven, boolean saveOnDb)
	{
		if (_owner.isSubClassActive())
			return;
		
		if (isDwarven)
			_dwarvenRecipes.put(recipe.id(), recipe);
		else
			_commonRecipes.put(recipe.id(), recipe);
		
		if (saveOnDb)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(INSERT_RECIPE))
			{
				ps.setInt(1, _owner.getObjectId());
				ps.setInt(2, recipe.id());
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't store recipe.", e);
			}
		}
	}
	
	/**
	 * Remove a {@link Recipe} from this {@link Player}. Delete the associated {@link Shortcut}, if existing.
	 * @param recipeId : The id of the {@link Recipe} to remove.
	 */
	public void removeRecipe(int recipeId)
	{
		if (_owner.isSubClassActive())
			return;
		
		if (_dwarvenRecipes.containsKey(recipeId))
			_dwarvenRecipes.remove(recipeId);
		else if (_commonRecipes.containsKey(recipeId))
			_commonRecipes.remove(recipeId);
		
		_owner.getShortcutList().deleteShortcuts(recipeId, ShortcutType.RECIPE);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_RECIPE))
		{
			ps.setInt(1, _owner.getObjectId());
			ps.setInt(2, recipeId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't remove recipe.", e);
		}
	}
	
	/**
	 * Restore {@link Recipe}s for this {@link Player}.
	 */
	public void restore()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_RECIPE_BOOK))
		{
			ps.setInt(1, _owner.getObjectId());
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
				{
					final Recipe recipe = RecipeData.getInstance().getRecipeList(rs.getInt("recipeId"));
					putRecipe(recipe, recipe.isDwarven(), false);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore recipe book data.", e);
		}
	}
}