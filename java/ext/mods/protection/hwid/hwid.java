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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.GameClient;
import ext.mods.gameserver.network.serverpackets.ServerClose;
import ext.mods.protection.hwid.crypt.BlowfishEngine;
import ext.mods.protection.hwid.crypt.FirstKey;
import ext.mods.protection.hwid.manager.hwidBan;
import ext.mods.protection.hwid.manager.hwidPlayer;
import ext.mods.protection.hwid.utils.Util;

public class hwid
{
	protected static CLogger LOGGER = new CLogger(hwid.class.getName());
	private static byte[] _key = new byte[16];
	static byte version = 11;
	protected static ConcurrentHashMap<String, Manager.InfoSet> _objects = new ConcurrentHashMap<>();
	
	public static void Init()
	{
		if (isProtectionOn())
		{
			hwidBan.getInstance();
			hwidPlayer.getInstance();
			Manager.getInstance();
			hwidManager.getInstance();
		}
	}
	
	public static boolean isProtectionOn()
	{
		return Config.ALLOW_GUARD_SYSTEM;
	}
	
	public static byte[] getKey(final byte[] key)
	{
		final byte[] bfkey = FirstKey.SKBOX;
		try
		{
			final BlowfishEngine bf = new BlowfishEngine();
			bf.init(true, bfkey);
			bf.processBlock(key, 0, _key, 0);
			bf.processBlock(key, 8, _key, 8);
		}
		catch (IOException e)
		{
			LOGGER.warn("HWID: Bad key!!!");
		}
		return _key;
	}
	
	public static void addPlayer(final GameClient client)
	{
		if (isProtectionOn() && client != null)
			Manager.getInstance().addPlayer(client);
	}
	
	public static void removePlayer(final GameClient client)
	{
		if (isProtectionOn() && client != null)
			Manager.removePlayer(client.getPlayerName());
	}
	
	public static boolean checkVerfiFlag(final GameClient client, final int flag)
	{
		boolean result = true;
		final int fl = Integer.reverseBytes(flag);
		if (fl == -1)
		{
			return false;
		}
		if (fl == 1342177280)
		{
			return false;
		}
		if ((fl & 0x1) != 0x0)
		{
			result = false;
		}
		if ((fl & 0x10) != 0x0)
		{
			result = false;
		}
		if ((fl & 0x10000000) != 0x0)
		{
			result = false;
		}
		return result;
	}
	
	public static int dumpData(final int _id, int position, final GameClient pi)
	{
		int value = 0;
		position = ((position > 4) ? 5 : position);
		boolean isIdZero = false;
		if (_id == 0)
		{
			isIdZero = true;
		}
		switch (position)
		{
			case 0:
			{
				if (_id != 1435233386)
				{
					if (!isIdZero)
					{
					}
					value = 1;
					break;
				}
				break;
			}
			case 1:
			{
				if (_id != 16)
				{
					if (!isIdZero)
					{
					}
					value = 2;
					break;
				}
				break;
			}
			case 2:
			case 3:
			case 4:
			{
				final int code = _id & 0xFF000000;
				if (code == 204)
				{
				}
				if (code == 233)
				{
					value = 3;
					break;
				}
				break;
			}
			default:
			{
				value = 0;
				break;
			}
		}
		return value;
	}
	
	public static int calcPenalty(final byte[] data, final GameClient pi)
	{
		int sum = -1;
		if (Util.verifyChecksum(data, 0, data.length))
		{
			final ByteBuffer buf = ByteBuffer.wrap(data, 0, data.length - 4);
			sum = 0;
			for (int lenPenalty = (data.length - 4) / 4, i = 0; i < lenPenalty; ++i)
			{
				sum += dumpData(buf.getInt(), i, pi);
			}
		}
		return sum;
	}
	
	public static boolean CheckHWIDs(final GameClient client, final int LastError1, final int LastError2)
	{
		boolean resultHWID = false;
		boolean resultLastError = false;
		final String HWID = client.getHWID();
		if (HWID.equalsIgnoreCase("fab888b1cc9de973c8046519fa841e6") && Config.PROTECT_KICK_WITH_EMPTY_HWID)
		{
			resultHWID = true;
		}
		if (LastError1 != 0 && Config.PROTECT_KICK_WITH_LASTERROR_HWID)
		{
			resultLastError = true;
		}
		return resultHWID || resultLastError;
	}
	
	public static String fillHex(final int data, final int digits)
	{
		String number = Integer.toHexString(data);
		for (int i = number.length(); i < digits; ++i)
		{
			number = "0" + number;
		}
		return number;
	}
	
	public static String ExtractHWID(final byte[] _data)
	{
		if (!Util.verifyChecksum(_data, 0, _data.length))
		{
			return null;
		}
		final StringBuilder resultHWID = new StringBuilder();
		for (int i = 0; i < _data.length - 8; ++i)
		{
			resultHWID.append(fillHex(_data[i] & 0xFF, 2));
		}
		return resultHWID.toString();
	}
	
	public static boolean doAuthLogin(final GameClient client, final byte[] data, final String loginName)
	{
		if (!isProtectionOn())
			return true;
		
		client.setLoginName(loginName);
		final String fullHWID = ExtractHWID(data);
		if (fullHWID == null)
		{
			LOGGER.warn("AuthLogin CRC Check Fail! May be BOT or unprotected client! Client IP: " + client.toString());
			client.close(ServerClose.STATIC_PACKET);
			return false;
		}
		final int LastError1 = ByteBuffer.wrap(data, 16, 4).getInt();
		if (CheckHWIDs(client, LastError1, 0))
		{
			LOGGER.warn("HWID error, look protection_logs.txt file, from IP: " + client.toString());
			client.close(ServerClose.STATIC_PACKET);
			return false;
		}
		if (hwidBan.getInstance().checkFullHWIDBanned(client))
		{
			LOGGER.warn("Client " + client + " is banned. Kicked! |HWID: " + client.getHWID() + " IP: " + client.toString());
			client.close(ServerClose.STATIC_PACKET);
		}
		final int VerfiFlag = ByteBuffer.wrap(data, 40, 4).getInt();
		return checkVerfiFlag(client, VerfiFlag);
	}
	
	public static void doDisconection(final GameClient client)
	{
		removePlayer(client);
	}
	
	public static boolean checkPlayerWithHWID(final GameClient client, final int playerID, final String playerName)
	{
		if (!isProtectionOn())
			return true;
		
		client.setPlayerName(playerName);
		client.setPlayerId(playerID);
		addPlayer(client);
		return true;
	}
	
	public static void enterlog(final Player player, final GameClient client)
	{
		hwidManager.getInstance().validBox(player, Config.PROTECT_WINDOWS_COUNT + 1, World.getInstance().getPlayers(), true);
		if (Config.ENABLE_CONSOLE_LOG)
			LOGGER.info("HWID: [" + client.getHWID() + "], character: [" + player.getName() + "]");
	}
}
