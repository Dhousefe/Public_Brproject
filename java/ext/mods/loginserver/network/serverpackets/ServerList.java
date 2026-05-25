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
package ext.mods.loginserver.network.serverpackets;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ext.mods.commons.network.ServerType;

import ext.mods.Config;
import ext.mods.loginserver.data.manager.GameServerManager;
import ext.mods.loginserver.data.manager.ProxyManager;
import ext.mods.loginserver.model.Account;
import ext.mods.loginserver.model.GameServerInfo;
import ext.mods.loginserver.model.L2Proxy;
import ext.mods.loginserver.model.ServerData;

public final class ServerList extends L2LoginServerPacket
{
	private final List<ServerData> _servers = new ArrayList<>();
	
	private final int _lastServer;
	
	private static final Comparator<ServerData> SERVER_DATA_COMPARATOR = Comparator.comparingInt(ServerData::serverId);
	
	public ServerList(Account account)
	{
		_lastServer = account.getLastServer();
		
		for (GameServerInfo gsi : GameServerManager.getInstance().getRegisteredGameServers().values())
		{
			final ServerType type = (account.getAccessLevel() < 0 || (gsi.getType() == ServerType.GM_ONLY && account.getAccessLevel() <= 0)) ? ServerType.DOWN : gsi.getType();
			final String hostName = (isLocalIp(account.getClientIp()) && gsi.getGameServerThread() != null) ? gsi.getGameServerThread().getConnectionIp() : gsi.getHostName();
			
			if (Config.PROXY)
			{
				var proxyInfo = ProxyManager.getInstance().getProxyInfoByGameserverId(gsi.getId());
				
				if (proxyInfo == null)
				{
					_servers.add(new ServerData(type, hostName, gsi));
					continue;
				}
				
				var proxiesForGameserver = ProxyManager.getInstance().getProxies().stream().filter(x -> x.getGameserverId() == gsi.getId()).collect(Collectors.toList());
				if (!proxyInfo.hidesGameServer())
					_servers.add(new ServerData(type, hostName, gsi));
				
				if (proxyInfo.fallbackToGameServer() && proxiesForGameserver.stream().noneMatch(L2Proxy::isHealthy))
					_servers.add(new ServerData(type, hostName, gsi));
				
				proxiesForGameserver.forEach(proxy ->
				{
					var newType = proxy.isHealthy() ? ServerType.AUTO : ServerType.DOWN;
					var proxyGsi = gsi.cloneInfo();
					
					proxyGsi.setPort(proxy.getProxyPort());
					proxyGsi.setId(proxy.getProxyServerId());
					
					if (gsi.getType() == ServerType.DOWN)
					{
						proxyGsi.setDown();
						newType = ServerType.DOWN;
					}
					_servers.add(new ServerData(newType, proxy.getProxyAddress().getHostAddress(), proxyGsi));
				});
			}
			else
				_servers.add(new ServerData(type, hostName, gsi));
		}
		
		if (Config.PROXY)
			_servers.sort(SERVER_DATA_COMPARATOR);
	}
	
	@Override
	public void write()
	{
		writeC(0x04);
		writeC(_servers.size());
		writeC(_lastServer);
		
		for (ServerData server : _servers)
		{
			writeC(server.serverId());
			
			try
			{
				final byte[] raw = InetAddress.getByName(server.hostName()).getAddress();
				writeC(raw[0] & 0xff);
				writeC(raw[1] & 0xff);
				writeC(raw[2] & 0xff);
				writeC(raw[3] & 0xff);
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
				writeC(127);
				writeC(0);
				writeC(0);
				writeC(1);
			}
			
			writeD(server.port());
			writeC(server.ageLimit());
			writeC(server.isPvp() ? 0x01 : 0x00);
			writeH(server.currentPlayers());
			writeH(server.maxPlayers());
			writeC(server.type() == ServerType.DOWN ? 0x00 : 0x01);
			
			int bits = 0;
			if (server.isTestServer())
				bits |= 0x04;
			
			if (server.isShowingClock())
				bits |= 0x02;
			
			writeD(bits);
			writeC(server.isShowingBrackets() ? 0x01 : 0x00);
		}
	}
	
	public static boolean isLocalIp(InetAddress address)
	{
		return address == null || address.isLinkLocalAddress() || address.isLoopbackAddress() || address.isAnyLocalAddress() || address.isSiteLocalAddress();
	}
}