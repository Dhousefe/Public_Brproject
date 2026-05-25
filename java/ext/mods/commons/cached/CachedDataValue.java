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
package ext.mods.commons.cached;

import java.sql.SQLException;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

public class CachedDataValue
{
	public static final CLogger LOGGER = new CLogger(CachedDataValue.class.getName());
	
	private static final String LOAD_QUERY = "SELECT `valueData` FROM `character_data` WHERE `valueName`='%s' AND `charId`=%d LIMIT 1";
	private static final String UPDATE_QUERY = "INSERT INTO `character_data` (`charId`, `valueName`, `valueData`) VALUES (%d, '%s', ?) ON DUPLICATE KEY UPDATE `valueData`=?";
	
	private final String valueName;
	
	private final String compiledLoadQuery;
	private final String compiledUpdateQuery;
	
	private volatile String valueData;
	
	private volatile boolean dirty = false;
	
	CachedDataValue(String valueName, String defaultValue, int charId)
	{
		this.valueName = valueName;
		this.valueData = defaultValue;
		compiledLoadQuery = String.format(LOAD_QUERY, valueName, charId);
		compiledUpdateQuery = String.format(UPDATE_QUERY, charId, valueName);
	}
	
	public String getKey()
	{
		return valueName;
	}
	
	synchronized void update()
	{
		if (dirty)
		{
			save();
			dirty = false;
		}
	}
	
	void save()
	{
		try (var conn = ConnectionPool.getConnection();
			var stmt = conn.prepareStatement(compiledUpdateQuery))
		{
			stmt.setString(1, valueData);
			stmt.setString(2, valueData);
			stmt.executeUpdate();
		}
		catch (SQLException e)
		{
			LOGGER.error("Failed save({}) character_data", e, valueName);
		}
	}
	
	void load()
	{
		try (var conn = ConnectionPool.getConnection();
			var stmt = conn.prepareStatement(compiledLoadQuery))
		{
			try (var rs = stmt.executeQuery())
			{
				while (rs.next())
					valueData = rs.getString(1);
			}
		}
		catch (SQLException e)
		{
			LOGGER.error("Failed load({}) character_data", e, valueName);
		}
	}
	
	public synchronized void setValue(String value)
	{
		valueData = value;
		dirty = true;
	}
	
	public String getValue()
	{
		return valueData;
	}
}