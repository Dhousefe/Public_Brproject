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

import java.nio.BufferUnderflowException;

import ext.mods.Config;
import ext.mods.commons.math.MathUtil;
import ext.mods.extensions.listener.manager.CreatureListenerManager;
import ext.mods.gameserver.enums.TeleportMode;
import ext.mods.gameserver.enums.boats.BoatDock;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.player.BoatInfo;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.MoveToLocation;
import ext.mods.gameserver.network.serverpackets.MoveToLocationInVehicle;

public class MoveBackwardToLocation extends L2GameClientPacket
{
	private static final Point2D CENTER_BOAT = new Point2D(0, -100);
	
	private int _targetX;
	private int _targetY;
	private int _targetZ;
	private int _originX;
	private int _originY;
	private int _originZ;
	private int _moveMovement;
	
	@Override
	protected void readImpl()
	{
		_targetX = readD();
		_targetY = readD();
		_targetZ = readD();
		_originX = readD();
		_originY = readD();
		_originZ = readD();
		
		try
		{
			_moveMovement = readD();
		}
		catch (BufferUnderflowException e)
		{
			if (Config.L2WALKER_PROTECTION)
			{
				final Player player = getClient().getPlayer();
				if (player != null)
					player.logout(false);
			}
		}
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final BoatInfo info = player.getBoatInfo();
		
		if (player.isOutOfControl() || player.getCast().isCastingNow())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (player.getStatus().getMoveSpeed() == 0)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(SystemMessageId.CANT_MOVE_TOO_ENCUMBERED);
			return;
		}
		
		player.cancelActiveEnchant();
		
		_targetZ += player.getCollisionHeight();
		
		switch (player.getTeleportMode())
		{
			case ONE_TIME:
				player.setTeleportMode(TeleportMode.NONE);
			case FULL_TIME:
				player.sendPacket(ActionFailed.STATIC_PACKET);
				player.teleportTo(_targetX, _targetY, _targetZ, 0);
				return;
		}
		
		final Location targetLoc = new Location(_targetX, _targetY, _targetZ);
		
		if (!targetLoc.isIn3DRadius(_originX, _originY, _originZ, 9900))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (AutoFarmManager.getInstance().isPlayerAddingLoc(player.getObjectId()))
		{
			AutoFarmManager.getInstance().addAreaNode(player, targetLoc);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (AutoFarmManager.getInstance().isRouteFarmActive(player.getObjectId()))
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final boolean isInBoat = info.isInBoat();
		
		if (!isInBoat)
		{
			if (player.tryToPassBoatEntrance(targetLoc, _moveMovement == 0))
				return;
			
			info.setCanBoard(false);
			
			if (_moveMovement == 0)
			{
				int vectorX = targetLoc.getX() - player.getX();
				int vectorY = targetLoc.getY() - player.getY();
				
				double magnitude = Math.sqrt(vectorX * vectorX + vectorY * vectorY);
				
				if (magnitude < 1.0)
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				double unitVectorX = vectorX / magnitude;
				double unitVectorY = vectorY / magnitude;
				
				int newVectorX = (int) (unitVectorX * 16);
				int newVectorY = (int) (unitVectorY * 16);
				
				int newX = player.getX() + newVectorX;
				int newY = player.getY() + newVectorY;
				
				final Location checkLoc = new Location(newX, newY, player.getZ());
				
				if (!GeoEngine.getInstance().canMove(player.getX(), player.getY(), player.getZ(), checkLoc.getX(), checkLoc.getY(), checkLoc.getZ(), null))
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (!player.getMove().maybeMoveToLocation(GeoEngine.getInstance().getValidLocation(player, targetLoc), 0, false, false))
					player.sendPacket(ActionFailed.STATIC_PACKET);
				
				return;
			}
			
			player.getAI().tryToMoveTo(targetLoc, null);
			CreatureListenerManager.getInstance().notifyMove(player, targetLoc);

		}
		else
		{
			player.getPosition().setHeading(MathUtil.calculateHeadingFrom(_originX, _originY, _targetX, _targetY));
			
			final Boat boat = info.getBoat();
			if (boat == null)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final BoatDock dock = boat.getDock();
			if (dock == null)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final boolean isMoving = boat.isMoving();
			
			final Point2D targetPoint = new Point2D(_targetX, _targetY);
			final Point2D originPoint = new Point2D(_originX, _originY);
			
			Point2D boardingPoint = dock.getBoardingPoint(originPoint, targetPoint, isInBoat);
			
			if (boardingPoint == null)
				boardingPoint = BoatDock.getBoardingPoint(dock.getBoatExit(), originPoint, targetPoint, isInBoat);
			
			if (boardingPoint == null && !isMoving)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			final Location pos = info.getBoatPosition();
			final int oX = pos.getX();
			final int oY = pos.getY();
			final int z = pos.getZ();
			
			info.setBoatMovement(true);
			
			final double distToBorder = isMoving ? 400 : originPoint.distance2D(boardingPoint);
			
			if (boardingPoint != null && distToBorder < 90)
			{
				player.broadcastPacket(new MoveToLocation(player, new Location(boardingPoint.getX(), boardingPoint.getY(), -3624)));
				info.setBoatMovement(false);
				info.setCanBoard(false);
				return;
			}
			
			final SpawnLocation boatPos = info.getBoatPosition();
			final Point2D currentPoint = new Point2D(boatPos.getX(), boatPos.getY());
			
			final double distToCenter = CENTER_BOAT.distance2D(currentPoint);
			
			if (distToCenter > 350)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (!isMoving && distToBorder > 200 && distToCenter > 250)
			{
				sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (isMoving && distToCenter < 250)
			{
				player.broadcastPacket(new MoveToLocation(player, new Location(_targetX, _targetY, _targetZ)));
				info.setBoatMovement(false);
				info.setCanBoard(false);
				return;
			}
			
			if (boardingPoint != null)
			{
				boardingPoint = dock.convertWorldToBoatCoordinates(boardingPoint.getX(), boardingPoint.getY());
				
				final int tX = boardingPoint.getX();
				final int tY = boardingPoint.getY();
				
				player.broadcastPacket(new MoveToLocationInVehicle(player, boat, tX, tY, z, oX, oY, z));
				info.setBoatMovement(false);
				info.setCanBoard(false);
			}
			
			sendPacket(ActionFailed.STATIC_PACKET);
		}
		
		sendPacket(ActionFailed.STATIC_PACKET);
	}
}