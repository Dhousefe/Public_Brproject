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
package ext.mods.gameserver.model.memo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ext.mods.commons.data.MemoSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

/**
 * An implementation of {@link MemoSet} used for Player. There is a restore/save system.
 */
public class PlayerMemo extends MemoSet
{
	private static final long serialVersionUID = 1L;
	
	private static final CLogger LOGGER = new CLogger(PlayerMemo.class.getName());
	
	private static final String SELECT_MEMOS = "SELECT * FROM character_memo WHERE charId = ?";
	private static final String DELETE_MEMO = "DELETE FROM character_memo WHERE charId = ? AND var = ?";
	private static final String INSERT_OR_UPDATE_MEMO = "INSERT INTO character_memo (charId, var, val) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE val = VALUES(val)";
	
	private final int _objectId;
	
	public PlayerMemo(int objectId)
	{
		_objectId = objectId;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_MEMOS))
		{
			ps.setInt(1, _objectId);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
					put(rs.getString("var"), rs.getString("val"));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore memos for player id {}.", e, _objectId);
		}
	}
	
	@Override
	protected void onSet(String key, String value)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_OR_UPDATE_MEMO))
		{
			ps.setInt(1, _objectId);
			ps.setString(2, key);
			ps.setString(3, value);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't set {} memo for player id {}.", e, key, _objectId);
		}
	}
	
	@Override
	protected void onUnset(String key)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_MEMO))
		{
			ps.setInt(1, _objectId);
			ps.setString(2, key);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't unset {} memo for player id {}.", e, key, _objectId);
		}
	}
}