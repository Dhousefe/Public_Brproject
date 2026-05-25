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
package ext.mods.loginserver.data.manager;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import ext.mods.commons.logging.CLogger;

import ext.mods.Config;

public class IpBanManager
{
	private static final CLogger LOGGER = new CLogger(GameServerManager.class.getName());
	
	private final Map<InetAddress, Long> _bannedIps = new ConcurrentHashMap<>();
	
	protected IpBanManager()
	{
		final Path file = Config.CONFIG_PATH.resolve("banned_ips.properties");
		if (file == null)
		{
			LOGGER.warn("banned_ips.properties is missing. Ban listing is skipped.");
			return;
		}
		
		try (Stream<String> stream = Files.lines(file))
		{
			stream.filter(l -> !l.contains("#")).forEach(l ->
			{
				try
				{
					_bannedIps.putIfAbsent(InetAddress.getByName(l), 0L);
				}
				catch (Exception e)
				{
					LOGGER.error("Invalid ban address ({}).", l);
				}
			});
		}
		catch (Exception e)
		{
			LOGGER.error("Error while reading banned_ips.properties.", e);
		}
		LOGGER.info("Loaded {} banned IP(s).", _bannedIps.size());
	}
	
	public Map<InetAddress, Long> getBannedIps()
	{
		return _bannedIps;
	}
	
	/**
	 * Add the {@link InetAddress} set as parameter to the ban list, with the given duration.
	 * @param address : The {@link InetAddress} to ban.
	 * @param duration : The timer in milliseconds. 0 means it is permanently banned.
	 */
	public void addBanForAddress(InetAddress address, long duration)
	{
		if (duration > 0)
			duration += System.currentTimeMillis();
		
		_bannedIps.putIfAbsent(address, duration);
	}
	
	/**
	 * @param address : The {@link InetAddress} to test.
	 * @return True if the {@link InetAddress} set as parameter is actually banned, otherwise false.<br>
	 *         <br>
	 *         If the timer exists, compare with actual time. If too old, remove the ban. 0 timers never expire.
	 */
	public boolean isBannedAddress(InetAddress address)
	{
		if (address == null)
			return true;
		
		final Long time = _bannedIps.get(address);
		if (time != null)
		{
			if (time > 0 && time < System.currentTimeMillis())
			{
				_bannedIps.remove(address);
				
				LOGGER.info("Removed expired ip address ban {}.", address.getHostAddress());
				return false;
			}
			return true;
		}
		return false;
	}
	
	public static IpBanManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final IpBanManager INSTANCE = new IpBanManager();
	}
}