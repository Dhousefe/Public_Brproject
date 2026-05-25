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

import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.location.Location;

public class PlaySound extends L2GameServerPacket
{
	private final int _soundType;
	private final String _soundFile;
	private final boolean _bindToObject;
	private final int _objectId;
	private final Location _location;
	private final int _delay;
	
	/**
	 * Used for static sound.
	 * @param soundFile : The name of the sound file.
	 */
	public PlaySound(String soundFile)
	{
		_soundType = 0;
		_soundFile = soundFile;
		_bindToObject = false;
		_objectId = 0;
		_location = Location.DUMMY_LOC;
		_delay = 0;
	}
	
	/**
	 * Used for static sound.
	 * @param soundType : The type of sound file. 0 - Sound, 1 - Music, 2 - Voice
	 * @param soundFile : The name of the sound file.
	 */
	public PlaySound(int soundType, String soundFile)
	{
		_soundType = soundType;
		_soundFile = soundFile;
		_bindToObject = false;
		_objectId = 0;
		_location = Location.DUMMY_LOC;
		_delay = 0;
	}
	
	/**
	 * Play the sound file in the client. We use a {@link WorldObject} as parameter, notably to find the position of the sound.
	 * @param soundType : The type of sound file. 0 - Sound, 1 - Music, 2 - Voice
	 * @param soundFile : The name of the sound file.
	 * @param object : The object to use.
	 */
	public PlaySound(int soundType, String soundFile, WorldObject object)
	{
		_soundType = soundType;
		_soundFile = soundFile;
		_bindToObject = true;
		_objectId = object.getObjectId();
		_location = object.getPosition().clone();
		_delay = 0;
	}
	
	/**
	 * Play the sound file in the client. All parameters can be set.
	 * @param soundType : The type of sound file. 0 - Sound, 1 - Music, 2 - Voice
	 * @param soundFile : The name of the sound file.
	 * @param bindToObject - true, if sound file binded for some object.
	 * @param objectId - object ID of caller. 0 - for quest, tutorial, etc.
	 * @param location - Location of binded object.
	 * @param delay - playing time
	 */
	public PlaySound(int soundType, String soundFile, boolean bindToObject, int objectId, Location location, int delay)
	{
		_soundType = soundType;
		_soundFile = soundFile;
		_bindToObject = bindToObject;
		_objectId = objectId;
		_location = location;
		_delay = delay;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x98);
		writeD(_soundType);
		writeS(_soundFile);
		writeD(_bindToObject ? 1 : 0);
		writeD(_objectId);
		writeLoc(_location);
		writeD(_delay);
	}
}