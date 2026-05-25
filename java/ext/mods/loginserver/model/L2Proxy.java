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
import java.net.UnknownHostException;

public class L2Proxy
{
	private final int _gameserverId;
	private final int _proxyServerId;
	private final InetAddress _proxyAddress;
	private final int _proxyPort;
	private final String _apiKey;
	private final int _apiPort;
	private final boolean _validateHealth;
	private boolean _isHealthy = true;
	private int _maxConnections = -1;
	
	public L2Proxy(int gameserverId, int proxyServerId, String proxyHost, int proxyPort, String apiKey, int apiPort, boolean validateHealth) throws UnknownHostException
	{
		_gameserverId = gameserverId;
		_proxyServerId = proxyServerId;
		_proxyAddress = InetAddress.getByName(proxyHost);
		_proxyPort = proxyPort;
		_apiKey = apiKey;
		_apiPort = apiPort;
		_validateHealth = validateHealth;
	}
	
	public int getGameserverId()
	{
		return _gameserverId;
	}
	
	public int getProxyServerId()
	{
		return _proxyServerId;
	}
	
	public InetAddress getProxyAddress()
	{
		return _proxyAddress;
	}
	
	public int getProxyPort()
	{
		return _proxyPort;
	}
	
	public boolean isHealthy()
	{
		return _isHealthy;
	}
	
	public void setHealthy(boolean healthy)
	{
		_isHealthy = healthy;
	}
	
	public int getMaxConnections()
	{
		return _maxConnections;
	}
	
	public void setMaxConnections(int maxConnections)
	{
		_maxConnections = maxConnections;
	}
	
	public String getApiKey()
	{
		return _apiKey;
	}
	
	public int getApiPort()
	{
		return _apiPort;
	}
	
	public boolean shouldValidateHealth()
	{
		return _validateHealth;
	}
}