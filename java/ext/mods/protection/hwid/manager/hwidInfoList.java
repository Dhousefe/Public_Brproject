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

public class hwidInfoList
{
	private final int _id;
	private String _hwid;
	private int _count;
	private int _playerID;
	private String _login;
	private LockType _lockType;
	
	public hwidInfoList(final int id)
	{
		_id = id;
	}
	
	public int get_id()
	{
		return _id;
	}
	
	public int getCount()
	{
		return _count;
	}
	
	public void setCount(final int count)
	{
		_count = count;
	}
	
	public int getPlayerID()
	{
		return _playerID;
	}
	
	public void setPlayerID(final int playerID)
	{
		_playerID = playerID;
	}
	
	public String getHWID()
	{
		return _hwid;
	}
	
	public void setHWID(final String hwid)
	{
		_hwid = hwid;
	}
	
	public String getLogin()
	{
		return _login;
	}
	
	public void setLogin(final String login)
	{
		_login = login;
	}
	
	public LockType getLockType()
	{
		return _lockType;
	}
	
	public void setLockType(final LockType lockType)
	{
		_lockType = lockType;
	}
	
	public enum LockType
	{
		PLAYER_LOCK,
		ACCOUNT_LOCK,
		NONE;
	}
}
