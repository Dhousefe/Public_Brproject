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
package ext.mods.gameserver.network.serverpackets;

import ext.mods.gameserver.network.NpcStringId;

public class ExShowScreenMessage extends L2GameServerPacket
{
	public enum SMPOS
	{
		DUMMY,
		TOP_LEFT,
		TOP_CENTER,
		TOP_RIGHT,
		MIDDLE_LEFT,
		MIDDLE_CENTER,
		MIDDLE_RIGHT,
		BOTTOM_CENTER,
		BOTTOM_RIGHT,
	}
	
	private final int _type;
	private final int _sysMsgId;
	private final boolean _showHide;
	private final int _unk2;
	private final int _unk3;
	private final boolean _showFading;
	private final int _size;
	private final SMPOS _position;
	private final boolean _showEffect;
	private final String _text;
	private final int _time;
	
	public ExShowScreenMessage(NpcStringId message, int time)
	{
		this(1, -1, SMPOS.TOP_CENTER, false, 0, 0, 0, false, time, false, message.getMessage());
	}
	
	public ExShowScreenMessage(NpcStringId message, int time, Object... params)
	{
		this(1, -1, SMPOS.TOP_CENTER, false, 0, 0, 0, false, time, false, message.getMessage(params));
	}
	
	public ExShowScreenMessage(String text, int time)
	{
		this(1, -1, SMPOS.TOP_CENTER, false, 0, 0, 0, false, time, false, text);
	}
	
	public ExShowScreenMessage(String text, int time, SMPOS pos, boolean effect)
	{
		this(1, -1, pos, false, 0, 0, 0, effect, time, false, text);
	}
	
	public ExShowScreenMessage(int type, int sysMsgId, SMPOS position, boolean showHide, int size, int unk2, int unk3, boolean showEffect, int time, boolean showFading, String text)
	{
		_type = type;
		_sysMsgId = sysMsgId;
		_position = position;
		_showHide = showHide;
		_size = size;
		_unk2 = unk2;
		_unk3 = unk3;
		_showEffect = showEffect;
		_time = time;
		_showFading = showFading;
		_text = text;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x38);
		writeD(_type);
		writeD(_sysMsgId);
		writeD(_position.ordinal());
		writeD(_showHide ? 1 : 0);
		writeD(_size);
		writeD(_unk2);
		writeD(_unk3);
		writeD(_showEffect ? 1 : 0);
		writeD(_time);
		writeD(_showFading ? 1 : 0);
		writeS(_text);
	}
}