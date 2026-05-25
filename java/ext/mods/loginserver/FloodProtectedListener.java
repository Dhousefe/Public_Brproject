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
package ext.mods.loginserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.logging.CLogger;

import ext.mods.Config;

public abstract class FloodProtectedListener extends Thread
{
	private static final CLogger LOGGER = new CLogger(FloodProtectedListener.class.getName());
	
	private final Map<String, ForeignConnection> _flooders = new ConcurrentHashMap<>();
	
	private final ServerSocket _serverSocket;
	
	public FloodProtectedListener(String listenIp, int port) throws IOException
	{
		if (listenIp.equals("*"))
			_serverSocket = new ServerSocket(port);
		else
			_serverSocket = new ServerSocket(port, 50, InetAddress.getByName(listenIp));
	}
	
	public abstract void addClient(Socket s);
	
	@Override
	public void run()
	{
		Socket connection = null;
		while (true)
		{
			try
			{
				connection = _serverSocket.accept();
				if (Config.FLOOD_PROTECTION)
				{
					final String address = connection.getInetAddress().getHostAddress();
					final long currentTime = System.currentTimeMillis();
					
					final ForeignConnection fc = _flooders.get(address);
					if (fc != null)
					{
						fc.attempts += 1;
						if ((fc.attempts > Config.FAST_CONNECTION_LIMIT && (currentTime - fc.lastConnection) < Config.NORMAL_CONNECTION_TIME) || (currentTime - fc.lastConnection) < Config.FAST_CONNECTION_TIME || fc.attempts > Config.MAX_CONNECTION_PER_IP)
						{
							fc.lastConnection = currentTime;
							fc.attempts -= 1;
							
							connection.close();
							
							if (!fc.isFlooding)
								LOGGER.info("Flood detected from {}.", address);
							
							fc.isFlooding = true;
							continue;
						}
						
						if (fc.isFlooding)
						{
							fc.isFlooding = false;
							LOGGER.info("{} isn't considered as flooding anymore.", address);
						}
						
						fc.lastConnection = currentTime;
					}
					else
						_flooders.put(address, new ForeignConnection(currentTime));
				}
				addClient(connection);
			}
			catch (Exception e)
			{
				try
				{
					if (connection != null)
						connection.close();
				}
				catch (Exception e2)
				{
				}
				
				if (isInterrupted())
				{
					try
					{
						_serverSocket.close();
					}
					catch (IOException io)
					{
						LOGGER.error(io);
					}
					break;
				}
			}
		}
	}
	
	public void removeFloodProtection(String ip)
	{
		if (!Config.FLOOD_PROTECTION)
			return;
		
		final ForeignConnection fc = _flooders.get(ip);
		if (fc != null)
		{
			fc.attempts -= 1;
			
			if (fc.attempts == 0)
				_flooders.remove(ip);
		}
	}
	
	protected static class ForeignConnection
	{
		public int attempts;
		public long lastConnection;
		public boolean isFlooding = false;
		
		public ForeignConnection(long time)
		{
			lastConnection = time;
			attempts = 1;
		}
	}
}