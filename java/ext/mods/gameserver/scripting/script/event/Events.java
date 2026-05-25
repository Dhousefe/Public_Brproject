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
package ext.mods.gameserver.scripting.script.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.scripting.Quest;

public abstract class Events extends Quest
{
	private static final String UPDATE_STATUS = "SELECT status FROM events_custom_data WHERE event_name = ?";
	private static final String EVENT_INSERT = "REPLACE INTO events_custom_data (event_name, status) VALUES (?,?)";
	private static final String EVENT_DELETE = "UPDATE events_custom_data SET status = ? WHERE event_name = ?";
	
	public Events()
	{
		super(-1, "events");
		
		restoreStatus(0);
	}
	
	public abstract boolean eventStart(int priority);
	
	public abstract boolean eventStop();
	
	public void eventStatusStart(int priority)
	{
		updateStatus(true);
	}
	
	public void eventStatusStop()
	{
		updateStatus(false);
	}
	
	private void restoreStatus(int priority)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE_STATUS))
		{
			statement.setString(1, getName());
			try (ResultSet rset = statement.executeQuery())
			{
				int status = 0;
				while (rset.next())
				{
					status = rset.getInt("status");
				}
				
				if (status > 0)
					eventStart(priority);
				else
					eventStop();
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Error: Could not restore custom event data info: " + e);
		}
	}
	
	private void updateStatus(boolean newEvent)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement stmt = con.prepareStatement(newEvent ? EVENT_INSERT : EVENT_DELETE))
		{
			if (newEvent)
			{
				stmt.setString(1, getName());
				stmt.setInt(2, 1);
			}
			else
			{
				stmt.setInt(1, 0);
				stmt.setString(2, getName());
			}
			
			stmt.execute();
		}
		catch (Exception e)
		{
			LOGGER.warn("Error: could not update custom event database!");
		}
	}
}