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

public class VMPC implements ProtectionCrypt
{
	private byte _n;
	private byte[] _P;
	private byte _s;
	
	public VMPC()
	{
		_n = 0;
		_P = new byte[256];
		_s = 0;
	}
	
	@Override
	public void setup(final byte[] key, final byte[] iv)
	{
		_s = 0;
		for (int i = 0; i < 256; ++i)
		{
			_P[i] = (byte) (i & 0xFF);
		}
		for (int m = 0; m < 768; ++m)
		{
			_s = _P[_s + _P[m & 0xFF] + key[m % 64] & 0xFF];
			final byte temp = _P[m & 0xFF];
			_P[m & 0xFF] = _P[_s & 0xFF];
			_P[_s & 0xFF] = temp;
		}
		for (int m = 0; m < 768; ++m)
		{
			_s = _P[_s + _P[m & 0xFF] + iv[m % 64] & 0xFF];
			final byte temp = _P[m & 0xFF];
			_P[m & 0xFF] = _P[_s & 0xFF];
			_P[_s & 0xFF] = temp;
		}
		for (int m = 0; m < 768; ++m)
		{
			_s = _P[_s + _P[m & 0xFF] + key[m % 64] & 0xFF];
			final byte temp = _P[m & 0xFF];
			_P[m & 0xFF] = _P[_s & 0xFF];
			_P[_s & 0xFF] = temp;
		}
		_n = 0;
	}
	
	@Override
	public void crypt(final byte[] raw, final int offset, final int size)
	{
		for (int i = 0; i < size; ++i)
		{
			_s = _P[_s + _P[_n & 0xFF] & 0xFF];
			final byte z = _P[_P[_P[_s & 0xFF] & 0xFF] + 1 & 0xFF];
			final byte temp = _P[_n & 0xFF];
			_P[_n & 0xFF] = _P[_s & 0xFF];
			_P[_s & 0xFF] = temp;
			_n = (byte) (_n + 1 & 0xFF);
			raw[offset + i] ^= z;
		}
	}
}
