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
package ext.mods.gameserver.model.boat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.enums.boats.BoatDock;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.location.BoatLocation;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.BoatSay;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;

public class BoatRouteInfo
{
	private final BoatLocation[] _paths;
	private final BoatDock _dock;
	private final int _itemId;
	private final BoatSay _isBusy;
	
	private final List<ScheduledBoatMessages> _scheduledBoatMessages = new ArrayList<>();
	
	public BoatRouteInfo(BoatLocation[] paths, BoatDock dock, int itemId)
	{
		_paths = paths;
		_dock = dock;
		_itemId = itemId;
		
		_isBusy = _paths[_paths.length - 1].getBusyMessage();
		
		if (dock.isBusyOnStart())
			dock.setBusy(true);
		
		for (BoatLocation path : _paths)
		{
			final IntIntHolder[] messages = path.getScheduledMessages();
			if (messages == null)
				continue;
			
			for (IntIntHolder holder : messages)
			{
				final BoatSay bs = new BoatSay(SystemMessageId.getSystemMessageId(holder.getId()));
				
				for (ScheduledBoatMessages sbm : _scheduledBoatMessages)
				{
					if (sbm.getDelay() == holder.getValue())
					{
						sbm.getMessages().add(bs);
						return;
					}
				}
				
				final List<L2GameServerPacket> list = new ArrayList<>();
				list.add(bs);
				
				_scheduledBoatMessages.add(new ScheduledBoatMessages(holder.getValue(), list));
			}
		}
	}
	
	public List<ScheduledBoatMessages> getScheduledMessages()
	{
		return _scheduledBoatMessages;
	}
	
	public BoatDock getDock()
	{
		return _dock;
	}
	
	public BoatSay getBusyMessage()
	{
		return _isBusy;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public BoatLocation[] getPaths()
	{
		return _paths;
	}
	
	public void visualize(ExServerPrimitive debug)
	{
		for (int i = 0; i < _paths.length; i++)
		{
			int nextIndex = i + 1;
			
			if (nextIndex == _paths.length)
				continue;
			
			final BoatLocation curPoint = _paths[i];
			final BoatLocation nextPoint = _paths[nextIndex];
			
			debug.addLine("Segment #" + i, Color.YELLOW, true, curPoint, nextPoint);
		}
	}
}