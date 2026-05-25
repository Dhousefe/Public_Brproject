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
package ext.mods.gameserver.model.actor.container.player;

import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.boat.BoatEngine;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.network.serverpackets.GetOnVehicle;
import ext.mods.gameserver.network.serverpackets.StopMoveInVehicle;

public class BoatInfo
{
	private final Player _player;
	
	private Boat _boat;
	
	private boolean _isBoatMovement = false;
	private boolean _canBoard = false;
	
	private final SpawnLocation _boatPosition = new SpawnLocation(0, 0, 0, 0);
	
	public BoatInfo(Player player)
	{
		_player = player;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public void stopMoveInVehicle(int boatId)
	{
		_player.sendPacket(new StopMoveInVehicle(_player, boatId));
	}
	
	public boolean isBoatMovement()
	{
		return _isBoatMovement;
	}
	
	public void setBoatMovement(boolean isBoatMovement)
	{
		_isBoatMovement = isBoatMovement;
	}
	
	public boolean canBoard()
	{
		return _canBoard;
	}
	
	public void setCanBoard(boolean canBoard)
	{
		_canBoard = canBoard;
	}
	
	/**
	 * @return true if the current {@link Player} is linked to a {@link Boat}.
	 */
	public boolean isInBoat()
	{
		return _boat != null;
	}
	
	/**
	 * @return the {@link Boat} linked to the current {@link Player}.
	 */
	public Boat getBoat()
	{
		return _boat;
	}
	
	/**
	 * Set the {@link Boat} for the current {@link Player}.<br>
	 * <br>
	 * If the parameter is null but Player is registered into a Boat, we delete the passenger from the Boat.
	 * @param boat : The Boat to set, or null to clean it.
	 */
	public void setBoat(Boat boat)
	{
		if (boat == null && _boat != null)
		{
			_boat.getPassengers().remove(_player);
			
			_boatPosition.clean();
		}
		_boat = boat;
	}
	
	/**
	 * @return the {@link SpawnLocation} related to Boat.
	 */
	public SpawnLocation getBoatPosition()
	{
		return _boatPosition;
	}
	
	public void sendInfo(Player player)
	{
		if (_boat != null)
			player.sendPacket(new GetOnVehicle(this));
	}
	
	/**
	 * @return the {@link Location} related to the docked {@link Boat}, if any, or {@link Location#DUMMY_LOC} if not found.
	 */
	public Location getDockLocation()
	{
		if (_boat == null)
			return Location.DUMMY_LOC;
		
		final BoatEngine engine = _boat.getEngine();
		if (engine == null)
			return Location.DUMMY_LOC;
		
		return engine.getDock().getOustLoc();
	}
}