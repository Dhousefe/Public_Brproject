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
package ext.mods.gameserver.network.clientpackets;

import ext.mods.gameserver.data.xml.BoatData;
import ext.mods.gameserver.enums.boats.BoatDock;
import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.player.BoatInfo;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.MoveToLocationInVehicle;

public final class RequestMoveToLocationInVehicle extends L2GameClientPacket
{
	private int _boatId;
	private int _tX;
	private int _tY;
	private int _tZ;
	private int _oX;
	private int _oY;
	private int _oZ;
	
	@Override
	protected void readImpl()
	{
		_boatId = readD();
		_tX = readD();
		_tY = readD();
		_tZ = readD();
		_oX = readD();
		_oY = readD();
		_oZ = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isSittingNow() || player.isSitting() || player.isStandingNow())
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final BoatInfo info = player.getBoatInfo();
		
		info.setBoatMovement(true);
		info.setCanBoard(true);
		
		if (_tX == _oX && _tY == _oY && _tZ == _oZ)
		{
			info.stopMoveInVehicle(_boatId);
			return;
		}
		
		final boolean isInBoat = info.isInBoat();
		
		Boat boat = info.getBoat();
		if (boat == null)
		{
			boat = BoatData.getInstance().getBoat(_boatId);
			if (boat == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (boat.isMoving())
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final BoatDock dock = boat.getDock();
			if (dock == null)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Point2D point = dock.getAdjustedBoardingPoint(player.getPosition(), dock.convertBoatToWorldCoordinates(_tX, _tY), isInBoat);
			
			if (player.getPosition().distance2D(point) < 50)
				moveToLocationInVehicle(player, boat);
			else
				player.moveToBoatEntrance(point, boat);
		}
		else
		{
			if (boat.getObjectId() != _boatId)
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (_tZ > -48)
				moveToLocationInVehicle(player, boat);
		}
		info.setBoatMovement(false);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private void moveToLocationInVehicle(final Player player, Boat boat)
	{
		if (_tY > 470)
			return;
		
		player.getBoatInfo().getBoatPosition().set(_tX, _tY, _tZ);
		player.broadcastPacket(new MoveToLocationInVehicle(player, boat, _tX, _tY, _tZ, _oX, _oY, _oZ));
	}
}