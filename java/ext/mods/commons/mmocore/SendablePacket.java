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

import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.records.EffectHolder;

public abstract class SendablePacket<T extends MMOClient<?>> extends AbstractPacket<T>
{
	protected abstract void write();
	
	protected final void writeC(final int data)
	{
		_buf.put((byte) data);
	}
	
	protected final void writeF(final double value)
	{
		_buf.putDouble(value);
	}
	
	protected final void writeH(final int value)
	{
		_buf.putShort((short) value);
	}
	
	protected final void writeD(final int value)
	{
		_buf.putInt(value);
	}
	
	protected final void writeQ(final long value)
	{
		_buf.putLong(value);
	}
	
	protected final void writeB(final byte[] data)
	{
		if (data != null && data.length > 0)
			_buf.put(data);
	}
	
	protected final void writeS(final String text)
	{
		if (text != null && !text.isEmpty())
		{
			for (int i = 0; i < text.length(); i++)
				_buf.putChar(text.charAt(i));
		}
		
		_buf.putChar('\000');
	}
	
	protected final void writeLoc(final Location loc)
	{
		writeD(loc.getX());
		writeD(loc.getY());
		writeD(loc.getZ());
	}
	
	protected void writeEffect(EffectHolder effect, boolean toggle)
	{
		writeD(effect.id());
		writeH(effect.level());
		writeD((toggle) ? -1 : effect.duration() / 1000);
	}
}