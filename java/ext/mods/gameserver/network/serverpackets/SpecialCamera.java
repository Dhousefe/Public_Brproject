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

import ext.mods.gameserver.model.records.Sequence;

public class SpecialCamera extends L2GameServerPacket
{
	private final int _objectId;
	private final int _dist;
	private final int _yaw;
	private final int _pitch;
	private final int _time;
	private final int _duration;
	private final int _turn;
	private final int _rise;
	private final int _widescreen;
	private final int _relAngle;
	private final int _skipPath;
	
	public SpecialCamera(Sequence sequence)
	{
		this(sequence.objectId(), sequence.dist(), sequence.yaw(), sequence.pitch(), sequence.time(), sequence.duration(), sequence.turn(), sequence.rise(), sequence.widescreen(), 0);
	}
	
	public SpecialCamera(int objectId, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int skipPath)
	{
		_objectId = objectId;
		_dist = dist;
		_yaw = yaw;
		_pitch = pitch;
		_time = time;
		_duration = duration;
		_turn = turn;
		_rise = rise;
		_widescreen = widescreen;
		_relAngle = skipPath;
		_skipPath = skipPath;
	}
	
	public SpecialCamera(int objectId, int force, int angle1, int angle2, int time, int duration, int relYaw, int relPitch, int isWide, int relAngle, int skipPath)
	{
		_objectId = objectId;
		_dist = force;
		_yaw = angle1;
		_pitch = angle2;
		_time = time;
		_duration = duration;
		_turn = relYaw;
		_rise = relPitch;
		_widescreen = isWide;
		_relAngle = relAngle;
		_skipPath = skipPath;
	}
	
	@Override
	public void writeImpl()
	{
		writeC(0xc7);
		writeD(_objectId);
		writeD(_dist);
		writeD(_yaw);
		writeD(_pitch);
		writeD(_time);
		writeD(_duration);
		writeD(_turn);
		writeD(_rise);
		writeD(_widescreen);
		writeD(_relAngle);
		writeD(_skipPath);
	}
}