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
package ext.mods.protection.hwid;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.network.GameClient;
import ext.mods.protection.hwid.manager.hwidPlayer;

/**
 * @author IMPOSTORA
 *
 */
public class Manager
{
	protected static CLogger LOGGER = new CLogger(Manager.class.getName());
	protected static String _logFile = "Manager";
	protected static String _logMainFile = "hwid_logs";
	protected static Manager INSTANCE;
	protected static ScheduledFuture<?> _GGTask = null;
	protected static ConcurrentHashMap<String, InfoSet> _objects = new ConcurrentHashMap<>();

	public static Manager getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Manager();
		}
		return INSTANCE;
	}

	public void addPlayer(final GameClient client)
	{
		hwidPlayer.updateHWIDInfo(client);
		_objects.put(client.getPlayerName(), new InfoSet(client.getPlayerName(), client.getHWID()));
	}

	public static void removePlayer(final String name)
	{
		if (_objects.containsKey(name))
			_objects.remove(name);
	}

	public static int getCountByHWID(final String HWID)
	{
		int result = 0;
		for (final InfoSet object : _objects.values())
		{
			if (object._hwid.equals(HWID))
				++result;
		}
		return result;
	}

	public class InfoSet
	{
		public String _playerName;
		public long _lastGGSendTime;
		public long _lastGGRecvTime;
		public int _attempts;
		public String _hwid;
		
		public InfoSet(final String name, final String HWID)
		{
			_playerName = "";
			_hwid = "";
			_playerName = name;
			_lastGGSendTime = System.currentTimeMillis();
			_lastGGRecvTime = _lastGGSendTime;
			_attempts = 0;
			_hwid = HWID;
		}
	}

}
