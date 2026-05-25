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

public class hwidInfoClient
{
	private String _playerName;
	private String _loginName;
	private int _playerId;
	private String _hwid;
	private int _revision;
	
	public hwidInfoClient()
	{
		_playerName = "";
		_loginName = "";
		_playerId = 0;
		_hwid = "";
		_revision = 0;
	}
	
	public final String getPlayerName()
	{
		return _playerName;
	}
	
	public void setPlayerName(final String name)
	{
		_playerName = name;
	}
	
	public void setPlayerId(final int plId)
	{
		_playerId = plId;
	}
	
	public int getPlayerId()
	{
		return _playerId;
	}
	
	public final String getHWID()
	{
		return _hwid;
	}
	
	public void setHWID(final String hwid)
	{
		_hwid = hwid;
	}
	
	public void setRevision(final int revision)
	{
		_revision = revision;
	}
	
	public int getRevision()
	{
		return _revision;
	}
	
	public final String getLoginName()
	{
		return _loginName;
	}
	
	public void setLoginName(final String name)
	{
		_loginName = name;
	}
}
