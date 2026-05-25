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
package ext.mods.loginserver.model;

import ext.mods.commons.network.ServerType;

import ext.mods.loginserver.GameServerThread;

public class GameServerInfo
{
	private int _id;
	private final byte[] _hexId;
	private boolean _isAuthed;
	
	private GameServerThread _gst;
	private ServerType _type;
	
	private String _hostName;
	private int _port;
	
	private boolean _isPvp;
	private boolean _isTestServer;
	private boolean _isShowingClock;
	private boolean _isShowingBrackets;
	
	private int _ageLimit;
	private int _maxPlayers;
	
	public GameServerInfo(int id, byte[] hexId, GameServerThread gst)
	{
		_id = id;
		_hexId = hexId;
		_gst = gst;
		_type = ServerType.DOWN;
	}
	
	public GameServerInfo(int id, byte[] hexId)
	{
		this(id, hexId, null);
	}
	
	public int getId()
	{
		return _id;
	}
	
	public void setId(int id)
	{
		_id = id;
	}
	
	public byte[] getHexId()
	{
		return _hexId;
	}
	
	public boolean isAuthed()
	{
		return _isAuthed;
	}
	
	public void setAuthed(boolean isAuthed)
	{
		_isAuthed = isAuthed;
	}
	
	public GameServerThread getGameServerThread()
	{
		return _gst;
	}
	
	public void setGameServerThread(GameServerThread gst)
	{
		_gst = gst;
	}
	
	public ServerType getType()
	{
		return _type;
	}
	
	public void setType(ServerType type)
	{
		_type = type;
	}
	
	public String getHostName()
	{
		return _hostName;
	}
	
	public void setHostName(String hostName)
	{
		_hostName = hostName;
	}
	
	public int getPort()
	{
		return _port;
	}
	
	public void setPort(int port)
	{
		_port = port;
	}
	
	public int getMaxPlayers()
	{
		return _maxPlayers;
	}
	
	public void setMaxPlayers(int maxPlayers)
	{
		_maxPlayers = maxPlayers;
	}
	
	public boolean isPvp()
	{
		return _isPvp;
	}
	
	public void setPvp(boolean isPvp)
	{
		_isPvp = isPvp;
	}
	
	public boolean isTestServer()
	{
		return _isTestServer;
	}
	
	public void setTestServer(boolean isTestServer)
	{
		_isTestServer = isTestServer;
	}
	
	public boolean isShowingClock()
	{
		return _isShowingClock;
	}
	
	public void setShowingClock(boolean isShowingClock)
	{
		_isShowingClock = isShowingClock;
	}
	
	public boolean isShowingBrackets()
	{
		return _isShowingBrackets;
	}
	
	public void setShowingBrackets(boolean isShowingBrackets)
	{
		_isShowingBrackets = isShowingBrackets;
	}
	
	public int getAgeLimit()
	{
		return _ageLimit;
	}
	
	public void setAgeLimit(int ageLimit)
	{
		_ageLimit = ageLimit;
	}
	
	public void setDown()
	{
		setAuthed(false);
		setPort(0);
		setGameServerThread(null);
		setType(ServerType.DOWN);
	}
	
	public int getCurrentPlayerCount()
	{
		return (_gst == null) ? 0 : _gst.getPlayerCount();
	}
	
	public GameServerInfo cloneInfo()
	{
        return new GameServerInfo(_id, _hexId, _gst);
    }
}