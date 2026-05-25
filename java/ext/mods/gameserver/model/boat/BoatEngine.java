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

import java.util.Iterator;
import java.util.List;

import ext.mods.gameserver.data.xml.BoatData;
import ext.mods.gameserver.enums.boats.BoatDock;
import ext.mods.gameserver.enums.boats.BoatSound;
import ext.mods.gameserver.enums.boats.BoatState;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.BoatLocation;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.taskmanager.BoatTaskManager;

public class BoatEngine implements Runnable
{
	private static final int BOAT_BROADCAST_RADIUS = 20000;
	
	private Iterator<ScheduledBoatMessages> _messages;
	
	private final Boat _boat;
	private final int _waitDelay;
	private final BoatRouteInfo[] _info;
	private final PlaySound[] _sounds = new PlaySound[3];
	
	private int _shoutCount = 0;
	private int _delay = 0;
	
	private BoatDock _currentDock;
	private BoatDock _destinationDock;
	
	private BoatState _state = BoatState.PREPARING;
	
	public BoatEngine(BoatItinerary itinerary)
	{
		_boat = BoatData.getInstance().getNewBoat(itinerary);
		_boat.setEngine(this);
		
		_currentDock = itinerary.getInfo()[0].getDock();
		_destinationDock = (itinerary.isOneWay()) ? _currentDock : itinerary.getInfo()[1].getDock();
		
		_info = itinerary.getInfo();
		
		_waitDelay = (_currentDock == BoatDock.RUNE && _destinationDock == BoatDock.PRIMEVAL) ? 0 : 300;
		_delay = _waitDelay;
		
		_sounds[0] = BoatSound.LEAVE_5_MIN.get(_boat);
		_sounds[1] = BoatSound.LEAVE_1_MIN.get(_boat);
		_sounds[2] = BoatSound.ARRIVAL_DEPARTURE.get(_boat);
		
		_messages = getRouteInfoByDock(_currentDock).getScheduledMessages().iterator();
		
		BoatTaskManager.getInstance().add(this);
	}
	
	@Override
	public String toString()
	{
		return "BoatEngine [currentDock=" + _currentDock + ", destinationDock=" + _destinationDock + ", state=" + _state + "]";
	}
	
	/**
	 * @return the {@link BoatDock} which represent the entrance from the dock to the Boat.
	 */
	public BoatDock getDock()
	{
		return _currentDock;
	}
	
	public BoatState getState()
	{
		return _state;
	}
	
	public void setState(BoatState state)
	{
		_state = state;
	}
	
	/**
	 * Broadcast one or several {@link L2GameServerPacket}(s) in both path points.
	 * @param packets : The {@link L2GameServerPacket}(s) to broadcast.
	 */
	public void broadcast(List<L2GameServerPacket> packets)
	{
		if (packets == null || packets.isEmpty())
			return;
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isIn2DRadius(_info[0].getDock().getDockLoc(), BOAT_BROADCAST_RADIUS) || (_info[1] != null && player.isIn2DRadius(_info[1].getDock().getDockLoc(), BOAT_BROADCAST_RADIUS)))
			{
				for (L2GameServerPacket packet : packets)
					player.sendPacket(packet);
			}
		}
	}
	
	/**
	 * Broadcast one or several {@link L2GameServerPacket}(s) in both path points.
	 * @param packets : The {@link L2GameServerPacket}(s) to broadcast.
	 */
	public void broadcast(L2GameServerPacket... packets)
	{
		if (packets == null || packets.length == 0)
			return;
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isIn2DRadius(_info[0].getDock().getDockLoc(), BOAT_BROADCAST_RADIUS) || (_info[1] != null && player.isIn2DRadius(_info[1].getDock().getDockLoc(), BOAT_BROADCAST_RADIUS)))
			{
				for (L2GameServerPacket packet : packets)
					player.sendPacket(packet);
			}
		}
	}
	
	public boolean canRun()
	{
		if (_delay > 0)
			_delay--;
		
		return _delay == 0;
	}
	
	/**
	 * @param dock : The {@link BoatDock} used as parameter.
	 * @return The {@link BoatRouteInfo} associated to the {@link BoatDock} set as parameter.
	 */
	public BoatRouteInfo getRouteInfoByDock(BoatDock dock)
	{
		for (BoatRouteInfo routeInfo : _info)
		{
			if (routeInfo.getDock() == dock)
				return routeInfo;
		}
		return null;
	}
	
	@Override
	public void run()
	{
		switch (_state)
		{
			case PREPARING:
				if (_messages.hasNext())
				{
					final ScheduledBoatMessages sm = _messages.next();
					
					broadcast(sm.getMessages());
					
					_delay = sm.getDelay();
					
					switch (_delay)
					{
						case 240 -> broadcast(_sounds[0]);
						case 40, 20 -> broadcast(_sounds[1]);
					}
				}
				else
					_state = BoatState.EXECUTE_ROUTE;
				
				break;
			
			case EXECUTE_ROUTE:
				final BoatRouteInfo info = getRouteInfoByDock(_currentDock);
				
				_boat.payForRide(info.getItemId(), info.getDock().getOustLoc());
				_boat.getMove().executePath(info.getPaths());
				
				info.getDock().setBusy(false);
				
				broadcast(_sounds[2]);
				
				_state = BoatState.SEALING;
				break;
			
			case READY_TO_MOVE_TO_DOCK:
				if (_destinationDock.isBusy())
				{
					_delay = 5;
					
					if (_shoutCount == 0)
						broadcast(getRouteInfoByDock(_destinationDock).getBusyMessage());
					
					_shoutCount++;
					if (_shoutCount > 35)
						_shoutCount = 0;
					
					_shoutCount = (_shoutCount + 1) % 36;
					return;
				}
				
				_destinationDock.setBusy(true);
				
				final BoatLocation[] path = getRouteInfoByDock(_currentDock).getPaths();
				_boat.getMove().moveToBoatLocation(path[path.length - 1]);
				
				_state = BoatState.SEALING;
				break;
			
			case DOCKED:
				_delay = _waitDelay;
				
				broadcast(_sounds[2]);
				
				final BoatDock temp = _currentDock;
				
				_currentDock = _destinationDock;
				_destinationDock = temp;
				
				_messages = getRouteInfoByDock(_currentDock).getScheduledMessages().iterator();
				
				_boat.forEachKnownType(Player.class, _boat::sendInfo);
				
				_state = BoatState.PREPARING;
				break;
		}
	}
}