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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class RoomInfo
{
	private final int _index;
	private final AtomicBoolean _isLocked = new AtomicBoolean();
	
	private int _time;
	private Party _party;
	private List<Integer> _memberIds;
	
	public RoomInfo(int index)
	{
		_index = index;
	}
	
	public int getIndex()
	{
		return _index;
	}
	
	public int getTime()
	{
		return _time;
	}
	
	public Party getParty()
	{
		return _party;
	}
	
	public void setParty(Party party)
	{
		_party = party;
		_memberIds = party.getMembers().stream().map(pm -> pm.getObjectId()).toList();
	}
	
	/**
	 * @return True if the {@link Party} evolved, being an edition of members amount or Player objectIds - or false otherwise.
	 */
	public boolean isPartyChanged()
	{
		if (_party == null)
			return true;
		
		if (_memberIds.size() != _party.getMembers().size())
			return true;
		
		return !_party.getMembers().stream().allMatch(m -> _memberIds.contains(m.getObjectId()));
	}
	
	public List<Integer> getMemberIds()
	{
		return _memberIds;
	}
	
	public boolean isLocked()
	{
		return _isLocked.get();
	}
	
	public void setLock(boolean isLocked)
	{
		_time = GameTimeTaskManager.getInstance().getCurrentTick();
		_isLocked.set(isLocked);
	}
	
	/**
	 * Reset {@link Party} and {@link List} member ids references, and unlock the room.
	 */
	public void clear()
	{
		_party = null;
		_memberIds = null;
		
		setLock(false);
	}
}