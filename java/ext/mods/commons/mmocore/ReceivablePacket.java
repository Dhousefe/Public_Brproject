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
package ext.mods.commons.mmocore;

import java.nio.ByteBuffer;

public abstract class ReceivablePacket<T extends MMOClient<?>> extends AbstractPacket<T> implements Runnable
{
	NioNetStringBuffer _sbuf;
	
	protected ReceivablePacket()
	{
		
	}
	
	protected abstract boolean read();
	
	protected final void readB(final byte[] dst)
	{
		_buf.get(dst);
	}
	
	protected final void readB(final byte[] dst, final int offset, final int len)
	{
		_buf.get(dst, offset, len);
	}
	
	protected final int readC()
	{
		return _buf.get() & 0xFF;
	}
	
	protected final int readH()
	{
		return _buf.getShort() & 0xFFFF;
	}
	
	protected final int readD()
	{
		return _buf.getInt();
	}
	
	protected final long readQ()
	{
		return _buf.getLong();
	}
	
	protected final double readF()
	{
		return _buf.getDouble();
	}
	
	protected final String readS()
	{
		_sbuf.clear();
		
		char ch;
		while ((ch = _buf.getChar()) != 0)
		{
			_sbuf.append(ch);
		}
		
		return _sbuf.toString();
	}
	
	/**
	 * packet forge purpose
	 * @param data
	 * @param client
	 * @param sBuffer
	 */
	public void setBuffers(ByteBuffer data, T client, NioNetStringBuffer sBuffer)
	{
		_buf = data;
		_client = client;
		_sbuf = sBuffer;
	}
}