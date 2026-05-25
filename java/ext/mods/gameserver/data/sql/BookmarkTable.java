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
package ext.mods.gameserver.data.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.Bookmark;

/**
 * This class loads and handles {@link Bookmark} into a {@link List}.<br>
 * To retrieve a {@link Bookmark}, you need its name and the {@link Player} objectId.
 */
public class BookmarkTable
{
	private static final CLogger LOGGER = new CLogger(BookmarkTable.class.getName());
	
	private final List<Bookmark> _bks = new ArrayList<>();
	
	protected BookmarkTable()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM bookmarks");
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
				_bks.add(new Bookmark(rs.getString("name"), rs.getInt("obj_Id"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z")));
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore bookmarks.", e);
		}
		LOGGER.info("Loaded {} bookmarks.", _bks.size());
	}
	
	/**
	 * Verify if a {@link Bookmark} already exists.
	 * @param name : The {@link Bookmark} name.
	 * @param objId : The {@link Player} objectId to make checks on.
	 * @return true if the {@link Bookmark} exists, false otherwise.
	 */
	public boolean isExisting(String name, int objId)
	{
		return getBookmark(name, objId) != null;
	}
	
	/**
	 * Retrieve a {@link Bookmark} by its name and its specific {@link Player} objectId.
	 * @param name : The {@link Bookmark} name.
	 * @param objId : The {@link Player} objectId to make checks on.
	 * @return the {@link Bookmark} if it exists, null otherwise.
	 */
	public Bookmark getBookmark(String name, int objId)
	{
		for (Bookmark bk : _bks)
		{
			if (bk.name().equalsIgnoreCase(name) && bk.id() == objId)
				return bk;
		}
		return null;
	}
	
	/**
	 * @param objId : The {@link Player} objectId to make checks on.
	 * @return the list of {@link Bookmark}s of a {@link Player}.
	 */
	public List<Bookmark> getBookmarks(int objId)
	{
		return _bks.stream().filter(bk -> bk.id() == objId).toList();
	}
	
	/**
	 * Creates a new {@link Bookmark} and store info to database.
	 * @param name : The name of the {@link Bookmark}.
	 * @param player : The {@link Player} who requested the creation. We use the {@link Player} location as {@link Bookmark} location.
	 */
	public void saveBookmark(String name, Player player)
	{
		final int objId = player.getObjectId();
		final int x = player.getX();
		final int y = player.getY();
		final int z = player.getZ();
		
		_bks.add(new Bookmark(name, objId, x, y, z));
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT INTO bookmarks (name, obj_Id, x, y, z) values (?,?,?,?,?)"))
		{
			ps.setString(1, name);
			ps.setInt(2, objId);
			ps.setInt(3, x);
			ps.setInt(4, y);
			ps.setInt(5, z);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save bookmark.", e);
		}
	}
	
	/**
	 * Delete a {@link Bookmark}, based on the {@link Player} objectId and its name.
	 * @param name : The name of the {@link Bookmark}.
	 * @param objId : The {@link Player} objectId to make checks on.
	 */
	public void deleteBookmark(String name, int objId)
	{
		final Bookmark bookmark = getBookmark(name, objId);
		if (bookmark != null)
		{
			_bks.remove(bookmark);
			
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement("DELETE FROM bookmarks WHERE name=? AND obj_Id=?"))
			{
				ps.setString(1, name);
				ps.setInt(2, objId);
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't delete bookmark.", e);
			}
		}
	}
	
	public static BookmarkTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BookmarkTable INSTANCE = new BookmarkTable();
	}
}