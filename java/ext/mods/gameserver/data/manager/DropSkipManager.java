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
package ext.mods.gameserver.data.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ext.mods.commons.pool.ConnectionPool;

public class DropSkipManager
{
	private final Map<Integer, Set<Integer>> _playerSkips = new HashMap<>();
	
	public boolean isSkipped(int playerId, int itemId)
	{
		return _playerSkips.getOrDefault(playerId, Collections.emptySet()).contains(itemId);
	}
	
	public void toggleSkip(int playerId, int itemId)
	{
		Set<Integer> skipped = _playerSkips.computeIfAbsent(playerId, k -> new HashSet<>());
		if (skipped.contains(itemId))
		{
			skipped.remove(itemId);
			removeFromDb(playerId, itemId);
		}
		else
		{
			skipped.add(itemId);
			insertToDb(playerId, itemId);
		}
		
	}
	
	private void insertToDb(int playerId, int itemId)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("INSERT IGNORE INTO player_droplist_config (player_id, item_id) VALUES (?, ?)"))
		{
			ps.setInt(1, playerId);
			ps.setInt(2, itemId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void removeFromDb(int playerId, int itemId)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("DELETE FROM player_droplist_config WHERE player_id = ? AND item_id = ?"))
		{
			ps.setInt(1, playerId);
			ps.setInt(2, itemId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadPlayer(int playerId)
	{
		Set<Integer> items = new HashSet<>();
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT item_id FROM player_droplist_config WHERE player_id = ?"))
		{
			ps.setInt(1, playerId);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				items.add(rs.getInt("item_id"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		_playerSkips.put(playerId, items);
	}
	
	public static DropSkipManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DropSkipManager INSTANCE = new DropSkipManager();
	}
}
