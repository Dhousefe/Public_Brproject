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

import ext.mods.commons.data.MemoSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

/**
 * A global, server-size, container for variables of any type, which can be then saved/restored upon server restart. It extends {@link MemoSet}.
 */
public class ServerMemoTable extends MemoSet
{
	private static final long serialVersionUID = 1L;
	
	private static final CLogger LOGGER = new CLogger(ServerMemoTable.class.getName());
	
	private static final String SELECT_MEMOS = "SELECT * FROM server_memo";
	private static final String DELETE_MEMO = "DELETE FROM server_memo WHERE var = ?";
	private static final String INSERT_OR_UPDATE_MEMO = "INSERT INTO server_memo (var, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value = VALUES(value)";
	
	protected ServerMemoTable()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_MEMOS);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
				put(rs.getString("var"), rs.getString("value"));
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore server variables.", e);
		}
		LOGGER.info("Loaded {} server variables.", size());
	}
	
	@Override
	protected void onSet(String key, String value)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_OR_UPDATE_MEMO))
		{
			ps.setString(1, key);
			ps.setString(2, value);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't set {} server memo.", e, key);
		}
	}
	
	@Override
	protected void onUnset(String key)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_MEMO))
		{
			ps.setString(1, key);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't unset {} server memo.", e, key);
		}
	}
	
	public static final ServerMemoTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ServerMemoTable INSTANCE = new ServerMemoTable();
	}
}