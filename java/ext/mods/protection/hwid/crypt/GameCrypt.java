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
package ext.mods.protection.hwid.crypt;

import ext.mods.Config;
import ext.mods.protection.hwid.crypt.impl.L2Client;
import ext.mods.protection.hwid.crypt.impl.L2Server;
import ext.mods.protection.hwid.crypt.impl.VMPC;

public class GameCrypt
{
	private ProtectionCrypt _client;
	private ProtectionCrypt _server;
	private boolean _isEnabled;
	private boolean _isProtected;
	
	public GameCrypt()
	{
		_isEnabled = false;
		_isProtected = false;
	}
	
	public void setProtected(final boolean state)
	{
		_isProtected = state;
	}
	
	public void setKey(final byte[] key)
	{
		if (_isProtected)
		{
			(_client = new VMPC()).setup(key, Config.GUARD_CLIENT_CRYPT);
			(_server = new L2Server()).setup(key, null);
			(_server = new VMPC()).setup(key, Config.GUARD_SERVER_CRYPT);
		}
		else
		{
			(_client = new L2Client()).setup(key, null);
			(_server = new L2Server()).setup(key, null);
		}
	}
	
	public void decrypt(final byte[] raw, final int offset, final int size)
	{
		if (_isEnabled)
			_client.crypt(raw, offset, size);
	}
	
	public void encrypt(final byte[] raw, final int offset, final int size)
	{
		if (_isEnabled)
			_server.crypt(raw, offset, size);
		else
			_isEnabled = true;
	}
}
