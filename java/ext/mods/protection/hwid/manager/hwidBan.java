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
package ext.mods.protection.hwid.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.gameserver.network.GameClient;

public class hwidBan
{
	protected static CLogger LOGGER = new CLogger(hwidBan.class.getName());
	private static hwidBan INSTANCE;
	private static Map<Integer, hwidBanList> _lists = new HashMap<>();
	
	public hwidBan()
	{
		load();
		LOGGER.info("Loaded {" + hwidBan._lists.size() + "} banned(s) Hwid(s)");
	}
	
	public static hwidBan getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new hwidBan();
		return INSTANCE;
	}
	
	private static void load()
	{
		String HWID = "";
		int counterHWIDBan = 0;
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hwid_bans");
			ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				HWID = rset.getString("HWID");
				final hwidBanList hb = new hwidBanList(counterHWIDBan);
				hb.setHWIDBan(HWID);
				_lists.put(counterHWIDBan, hb);
				++counterHWIDBan;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void reload()
	{
		INSTANCE = new hwidBan();
	}
	
	public boolean checkFullHWIDBanned(final GameClient client)
	{
		if (_lists.size() == 0)
		{
			return false;
		}
		for (int i = 0; i < _lists.size(); ++i)
		{
			if (_lists.get(i).getHWID().equals(client.getHWID()))
				return true;
		}
		return false;
	}
	
	public static int getCountHWIDBan()
	{
		return _lists.size();
	}
	
	public static void addHWIDBan(final GameClient client)
	{
		final String HWID = client.getHWID();
		final int counterHwidBan = _lists.size();
		final hwidBanList hb = new hwidBanList(counterHwidBan);
		hb.setHWIDBan(HWID);
		_lists.put(counterHwidBan, hb);
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement("INSERT INTO hwid_bans SET HWID=?"))
		{
			statement.setString(1, HWID);
			statement.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
