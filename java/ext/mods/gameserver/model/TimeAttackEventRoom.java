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
package ext.mods.gameserver.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.gameserver.model.group.Party;

public class TimeAttackEventRoom
{
	private final Map<RoomIndex, Party> eventRoomMap = new ConcurrentHashMap<>();
	
	public boolean clear(int index, int partType)
	{
		return eventRoomMap.remove(new RoomIndex(index, partType)) != null;
	}
	
	public synchronized boolean addParty(int index, int partType, Party party)
	{
		if (party == null)
			return false;
		
		if (party.getMembersCount() == 0)
			return false;
		
		final RoomIndex rindex = new RoomIndex(index, partType);
		final Party oldParty = eventRoomMap.get(rindex);
		if (oldParty != null && oldParty.getMembersCount() > 0)
			return false;
		
		eventRoomMap.put(rindex, party);
		return true;
	}
	
	public synchronized Party getParty(int index, int partType)
	{
		final RoomIndex rindex = new RoomIndex(index, partType);
		final Party party = eventRoomMap.get(rindex);
		if (party != null)
		{
			if (party.getMembersCount() > 0)
				return party;
			else
				eventRoomMap.remove(rindex);
		}
		return null;
	}
	
	record RoomIndex(int index, int partType)
	{
	}
	
	public static TimeAttackEventRoom getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private final static TimeAttackEventRoom INSTANCE = new TimeAttackEventRoom();
	}
}