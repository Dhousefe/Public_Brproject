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
package ext.mods.loginserver.network.clientpackets;

import java.nio.charset.StandardCharsets;

public abstract class ClientBasePacket
{
	private final byte[] _decrypt;
	private int _off;
	
	protected ClientBasePacket(byte[] decrypt)
	{
		_decrypt = decrypt;
		_off = 1;
	}
	
	public int readD()
	{
		return (_decrypt[_off++] & 0xff) | ((_decrypt[_off++] & 0xff) << 8) | ((_decrypt[_off++] & 0xff) << 16) | ((_decrypt[_off++] & 0xff) << 24);
	}
	
	public int readC()
	{
		return _decrypt[_off++] & 0xff;
	}
	
	public int readH()
	{
		return (_decrypt[_off++] & 0xff) | ((_decrypt[_off++] & 0xff) << 8);
	}
	
	public double readF()
	{
		final long result = (_decrypt[_off++] & 0xffL) | ((_decrypt[_off++] & 0xffL) << 8) | ((_decrypt[_off++] & 0xffL) << 16) | ((_decrypt[_off++] & 0xffL) << 24) | ((_decrypt[_off++] & 0xffL) << 32) | ((_decrypt[_off++] & 0xffL) << 40) | ((_decrypt[_off++] & 0xffL) << 48) | ((_decrypt[_off++] & 0xffL) << 56);
		return Double.longBitsToDouble(result);
	}
	
	public String readS()
	{
		final int start = _off;
		
		int end = start;
		while (end < _decrypt.length - 1 && (_decrypt[end] != 0 || _decrypt[end + 1] != 0))
			end += 2;
		
		_off = end + 2;
		
		return new String(_decrypt, start, end - start, StandardCharsets.UTF_16LE);
	}
	
	public final byte[] readB(int length)
	{
		byte[] result = new byte[length];
		
		System.arraycopy(_decrypt, _off, result, 0, length);
		
		_off += length;
		
		return result;
	}
}