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
package ext.mods.gameserver.network;

import ext.mods.Config;

public class GameCrypt
{
	private final byte[] _inKey = new byte[16];
	private final byte[] _outKey = new byte[16];
	private boolean _isEnabled;
	
	public void setKey(byte[] key)
	{
		System.arraycopy(key, 0, _inKey, 0, 16);
		System.arraycopy(key, 0, _outKey, 0, 16);
	}
	
	public void decrypt(byte[] raw, final int offset, final int size)
	{
		if (!Config.USE_BLOWFISH_CIPHER || !_isEnabled)
			return;
		
		int temp = 0;
		for (int i = 0; i < size; i++)
		{
			int temp2 = raw[offset + i] & 0xFF;
			raw[offset + i] = (byte) (temp2 ^ _inKey[i & 15] ^ temp);
			temp = temp2;
		}
		
		int old = _inKey[8] & 0xff;
		old |= _inKey[9] << 8 & 0xff00;
		old |= _inKey[10] << 0x10 & 0xff0000;
		old |= _inKey[11] << 0x18 & 0xff000000;
		
		old += size;
		
		_inKey[8] = (byte) (old & 0xff);
		_inKey[9] = (byte) (old >> 0x08 & 0xff);
		_inKey[10] = (byte) (old >> 0x10 & 0xff);
		_inKey[11] = (byte) (old >> 0x18 & 0xff);
	}
	
	public void encrypt(byte[] raw, final int offset, final int size)
	{
		if (!_isEnabled)
		{
			_isEnabled = Config.USE_BLOWFISH_CIPHER;
			return;
		}
		
		int temp = 0;
		for (int i = 0; i < size; i++)
		{
			int temp2 = raw[offset + i] & 0xFF;
			temp = temp2 ^ _outKey[i & 15] ^ temp;
			raw[offset + i] = (byte) temp;
		}
		
		int old = _outKey[8] & 0xff;
		old |= _outKey[9] << 8 & 0xff00;
		old |= _outKey[10] << 0x10 & 0xff0000;
		old |= _outKey[11] << 0x18 & 0xff000000;
		
		old += size;
		
		_outKey[8] = (byte) (old & 0xff);
		_outKey[9] = (byte) (old >> 0x08 & 0xff);
		_outKey[10] = (byte) (old >> 0x10 & 0xff);
		_outKey[11] = (byte) (old >> 0x18 & 0xff);
	}
}
