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
package ext.mods.protection.hwid.crypt.impl;

import ext.mods.protection.hwid.crypt.ProtectionCrypt;

public class L2Client implements ProtectionCrypt
{
	private ProtectionCrypt _client;
	private byte[] _key;
	private byte[] _iv;
	
	public L2Client()
	{
		_key = new byte[16];
		_iv = null;
	}
	
	@Override
	public void setup(final byte[] key, final byte[] iv)
	{
		System.arraycopy(key, 0, _key, 0, 16);
		_iv = iv;
	}
	
	@Override
	public void crypt(final byte[] raw, final int offset, final int size)
	{
		if (_iv != null)
		{
			(_client = new VMPC()).setup(_key, _iv);
			_client.crypt(raw, offset, size);
		}
		int temp = 0;
		int temp2 = 0;
		for (int i = 0; i < size; ++i)
		{
			temp2 = (raw[offset + i] & 0xFF);
			raw[offset + i] = (byte) (temp2 ^ _key[i & 0xF] ^ temp);
			temp = temp2;
		}
		int old = _key[8] & 0xFF;
		old |= (_key[9] << 8 & 0xFF00);
		old |= (_key[10] << 16 & 0xFF0000);
		old |= (_key[11] << 24 & 0xFF000000);
		old += size;
		_key[8] = (byte) (old & 0xFF);
		_key[9] = (byte) (old >> 8 & 0xFF);
		_key[10] = (byte) (old >> 16 & 0xFF);
		_key[11] = (byte) (old >> 24 & 0xFF);
	}
}
