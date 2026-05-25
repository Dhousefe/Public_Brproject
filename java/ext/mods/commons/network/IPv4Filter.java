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
package ext.mods.commons.network;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.mmocore.IAcceptFilter;

public class IPv4Filter implements IAcceptFilter, Runnable
{
	private static final long SLEEP_TIME = 5000;
	
	private final Map<Integer, FloodHolder> _floods = new ConcurrentHashMap<>();
	
	public IPv4Filter()
	{
		final Thread t = new Thread(this);
		t.setName(getClass().getSimpleName());
		t.setDaemon(true);
		t.start();
	}
	
	@Override
	public boolean accept(Socket socket)
	{
		final int hash = hash(socket.getInetAddress().getAddress());
		
		final FloodHolder flood = _floods.get(hash);
		if (flood != null)
		{
			final long currentTime = System.currentTimeMillis();
			
			if (flood.tries == -1)
			{
				flood.lastAccess = currentTime;
				return false;
			}
			
			if (flood.lastAccess + 1000 > currentTime)
			{
				flood.lastAccess = currentTime;
				
				if (flood.tries >= 3)
				{
					flood.tries = -1;
					return false;
				}
				
				flood.tries++;
			}
			else
				flood.lastAccess = currentTime;
		}
		else
			_floods.put(hash, new FloodHolder());
		
		return true;
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			final long referenceTime = System.currentTimeMillis() - (1000 * 300);
			
			_floods.values().removeIf(f -> f.lastAccess < referenceTime);
			
			try
			{
				Thread.sleep(SLEEP_TIME);
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
	}
	
	protected final class FloodHolder
	{
		protected long lastAccess = System.currentTimeMillis();
		protected int tries;
	}
	
	private static final int hash(byte[] ip)
	{
		return ip[0] & 0xFF | ip[1] << 8 & 0xFF00 | ip[2] << 16 & 0xFF0000 | ip[3] << 24 & 0xFF000000;
	}
}