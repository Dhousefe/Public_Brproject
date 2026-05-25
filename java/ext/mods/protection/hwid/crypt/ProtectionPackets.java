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

import ext.mods.commons.random.Rnd;

public class ProtectionPackets
{
	public static int readB(final byte[] raw, int offset, final byte[] data, final int size)
	{
		for (int i = 0; i < size; ++i)
		{
			data[i] = (byte) (raw[offset] ^ raw[0]);
			offset += (raw[offset + 1] & 0xFF);
		}
		return offset;
	}

	public static int readS(final byte[] raw, int offset, final byte[] data, final int size)
	{
		for (int i = 0; i < size; ++i)
		{
			data[i] = (byte) (raw[offset] ^ raw[0]);
			offset += (raw[offset + 1] & 0xFF);
			if (data[i] == 0)
				break;
		}
		return offset;
	}

	public static int writeB(final byte[] raw, int offset, final byte[] data, final int size)
	{
		for (int i = 0; i < size; ++i)
		{
			raw[offset] = (byte) (data[i] ^ raw[0]);
			raw[offset + 1] = (byte) (2 + Rnd.nextInt(10));
			offset += (raw[offset + 1] & 0xFF);
		}
		return offset;
	}

	public static byte ck(final byte[] raw, final int offset, final int size)
	{
		byte c = -1;
		for (int i = 0; i < size; ++i)
		{
			c ^= raw[offset + i];
		}
		return c;
	}

}
