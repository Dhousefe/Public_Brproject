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

import java.net.InetAddress;

import ext.mods.commons.network.ServerType;

import ext.mods.Config;
import ext.mods.loginserver.data.manager.GameServerManager;
import ext.mods.loginserver.data.manager.ProxyManager;
import ext.mods.loginserver.data.sql.AccountTable;

public final class Account
{
	private final String _login;
	private final String _password;
	
	private final int _accessLevel;
	private final int _lastServer;
	
	private InetAddress _clientIp;
	
	public Account(final String login, final String password, final int accessLevel, final int lastServer)
	{
		_login = login.toLowerCase();
		_password = password;
		_accessLevel = accessLevel;
		_lastServer = lastServer;
	}
	
	public String getLogin()
	{
		return _login;
	}
	
	public String getPassword()
	{
		return _password;
	}
	
	public int getAccessLevel()
	{
		return _accessLevel;
	}
	
	public int getLastServer()
	{
		return _lastServer;
	}
	
	public InetAddress getClientIp()
	{
		return _clientIp;
	}
	
	public void setClientIp(InetAddress addr)
	{
		_clientIp = addr;
	}
	
	public final boolean isLoginPossible(int serverId)
	{
		GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServers().get(serverId);
		
		if (!Config.PROXY)
		{
			L2Proxy proxy = ProxyManager.getInstance().getProxyById(serverId);
			
			if (gsi == null && proxy != null)
				gsi = GameServerManager.getInstance().getRegisteredGameServers().get(proxy.getGameserverId());
		}
		
		if (gsi == null || !gsi.isAuthed())
			return false;
		
		final ServerType type = gsi.getType();
		
		if (type == ServerType.DOWN)
			return false;
		
		final boolean canLogin = (type == ServerType.GM_ONLY || gsi.getCurrentPlayerCount() >= gsi.getMaxPlayers()) ? _accessLevel > 0 : _accessLevel >= 0;
		if (canLogin && _lastServer != serverId)
			AccountTable.getInstance().setAccountLastServer(_login, serverId);
		
		return canLogin;
	}
}