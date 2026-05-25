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
import ext.mods.protection.hwid.manager.hwidInfoList.LockType;

public class hwidPlayer
{
	protected static CLogger LOGGER = new CLogger(hwidPlayer.class.getName());
	private static hwidPlayer INSTANCE;
	private static Map<Integer, hwidInfoList> _list = new HashMap<>();
	private static Map<Integer, Integer> _sessions = new HashMap<>();
	
	public hwidPlayer()
	{
		load();
		LOGGER.info("Loaded {" + _list.size() + "} player(s) Hwid(s)");
	}
	
	public static hwidPlayer getInstance()
	{
		if (INSTANCE == null)
			INSTANCE = new hwidPlayer();
		return INSTANCE;
	}
	
	private static void load()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM hwid_info");
			ResultSet rset = statement.executeQuery())
		{
			int counterHWIDInfo = 0;
			while (rset.next())
			{
				final hwidInfoList hInfo = new hwidInfoList(counterHWIDInfo);
				hInfo.setHWID(rset.getString("HWID"));
				hInfo.setLogin(rset.getString("Account"));
				hInfo.setPlayerID(rset.getInt("PlayerID"));
				hInfo.setLockType(LockType.valueOf(rset.getString("LockType")));
				_list.put(counterHWIDInfo, hInfo);
				++counterHWIDInfo;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void reload()
	{
		INSTANCE = new hwidPlayer();
	}
	
	public static int startSession(int WindowsCount)
	{
		synchronized (_list)
		{
			if (_sessions.get(WindowsCount) == null)
				_sessions.put(WindowsCount, 0);
			_sessions.put(WindowsCount, _sessions.get(WindowsCount) + 1);
		}
		return _sessions.get(WindowsCount);
	}
	
	public static void updateHWIDInfo(GameClient client)
	{
		updateHWIDInfo(client, LockType.NONE);
	}
	
	public static void updateHWIDInfo(final GameClient client, final LockType lockType)
	{
		int counterHwidInfo = _list.size();
		boolean isFound = false;
		for (int i = 0; i < _list.size(); ++i)
		{
			if (_list.get(i).getHWID().equals(client.getHWID()))
			{
				isFound = true;
				counterHwidInfo = i;
				break;
			}
		}
		final hwidInfoList hInfo = new hwidInfoList(counterHwidInfo);
		hInfo.setHWID(client.getHWID());
		hInfo.setLogin(client.getAccountName());
		hInfo.setPlayerID(client.getPlayerId());
		hInfo.setLockType(lockType);
		_list.put(counterHwidInfo, hInfo);
		if (isFound)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement("UPDATE hwid_info SET Account=?,PlayerID=?,LockType=? WHERE HWID=?"))
			{
				statement.setString(1, client.getAccountName());
				statement.setInt(2, client.getPlayerId());
				statement.setString(3, lockType.toString());
				statement.setString(4, client.getHWID());
				statement.execute();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement statement = con.prepareStatement("INSERT INTO hwid_info (HWID, Account, PlayerID, LockType) values (?,?,?,?)"))
			{
				statement.setString(1, client.getHWID());
				statement.setString(2, client.getAccountName());
				statement.setInt(3, client.getPlayerId());
				statement.setString(4, lockType.toString());
				statement.execute();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static boolean checkLockedHWID(final GameClient client)
	{
		if (_list.size() == 0)
			return false;
		
		boolean result = false;
		for (int i = 0; i < _list.size(); ++i)
		{
			switch (_list.get(i).getLockType().ordinal())
			{
				case 2:
				{
					if (client.getPlayerId() == 0)
						break;
					
					if (_list.get(i).getPlayerID() != client.getPlayerId())
						break;
					
					if (_list.get(i).getHWID().equals(client.getHWID()))
						return false;
					
					result = true;
					break;
				}
				case 3:
				{
					if (!_list.get(i).getLogin().equals(client.getLoginName()))
						break;
					
					if (_list.get(i).getHWID().equals(client.getHWID()))
						return false;
					
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	public static int getAllowedWindowsCount(final GameClient client)
	{
		if (_list.size() == 0)
			return -1;
		
		int i = 0;
		while (i < _list.size())
		{
			if (!_list.get(i).getHWID().equals(client.getHWID()))
				++i;
			else
			{
				if (_list.get(i).getHWID().equals(""))
					return -1;
				
				return _list.get(i).getCount();
			}
		}
		return -1;
	}
	
	public static int getCountHwidInfo()
	{
		return _list.size();
	}
}
