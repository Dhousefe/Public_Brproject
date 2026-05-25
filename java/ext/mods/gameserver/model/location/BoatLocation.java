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
package ext.mods.gameserver.model.location;

import java.util.stream.Stream;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.BoatSay;

/**
 * A datatype extending {@link Location} used for boats. It notably holds move speed and rotation speed.
 */
public class BoatLocation extends Location
{
	private int _moveSpeed;
	private int _rotationSpeed;
	
	private BoatSay _busyMessage;
	private BoatSay[] _arrivalMessages;
	private BoatSay[] _departureMessages;
	
	private IntIntHolder[] _scheduledMessages;
	
	public BoatLocation(int x, int y, int z, int moveSpeed, int rotationSpeed)
	{
		super(x, y, z);
		
		_moveSpeed = moveSpeed;
		_rotationSpeed = rotationSpeed;
	}
	
	public BoatLocation(StatSet set)
	{
		super(set);
		
		_moveSpeed = set.getInteger("speed", 350);
		_rotationSpeed = set.getInteger("rotation", 4000);
		
		final int busy = set.getInteger("busy", 0);
		if (busy != 0)
			_busyMessage = new BoatSay(SystemMessageId.getSystemMessageId(busy));
		
		_arrivalMessages = getBoatSayArray(set.getStringArray("arrival", null));
		_departureMessages = getBoatSayArray(set.getStringArray("departure", null));
		
		_scheduledMessages = set.getIntIntHolderArray("scheduled", null);
	}
	
	private static BoatSay[] getBoatSayArray(String[] messages)
	{
		if (messages == null)
			return null;
		
		return Stream.of(messages).map(message -> new BoatSay(SystemMessageId.getSystemMessageId(Integer.parseInt(message)))).toArray(BoatSay[]::new);
	}
	
	public int getMoveSpeed()
	{
		return _moveSpeed;
	}
	
	public int getRotationSpeed()
	{
		return _rotationSpeed;
	}
	
	public BoatSay getBusyMessage()
	{
		return _busyMessage;
	}
	
	public BoatSay[] getArrivalMessages()
	{
		return _arrivalMessages;
	}
	
	public BoatSay[] getDepartureMessages()
	{
		return _departureMessages;
	}
	
	public IntIntHolder[] getScheduledMessages()
	{
		return _scheduledMessages;
	}
}