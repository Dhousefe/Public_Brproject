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

import ext.mods.Config;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.move.MovementIntegration;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ValidateLocation;

/**
 * Validação melhorada de posição do cliente.
 * Inclui verificações de geodata, colisão, altura e movimento válido.
 */
public class ValidatePosition extends L2GameClientPacket
{
	private int _x;
	private int _y;
	private int _z;
	private int _heading;
	
	private static final double MAX_DISTANCE_DIFF = 64.0;
	private static final double MAX_HEIGHT_DIFF = 200.0;
	private static final double MAX_SPEED_CHECK = 2.0;
	
	@Override
	protected void readImpl()
	{
		_x = readD();
		_y = readD();
		_z = readD();
		_heading = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isDead() || player.isTeleporting() || player.isMovementDisabled())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(new ValidateLocation(player));
			return;
		}
			
		final Location clientPosition = new Location(_x, _y, _z);
		final Location serverPosition = player.getPosition();
		
		final double distance = clientPosition.distance3D(serverPosition);
		if (distance > MAX_DISTANCE_DIFF)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(new ValidateLocation(player));
			return;
		}
		
		final double maxMovePerTick = player.getStatus().getRealMoveSpeed(false) * MAX_SPEED_CHECK;
		final double actualMove = serverPosition.distance2D(clientPosition);
		if (actualMove > maxMovePerTick)
		{
			if (Config.DEVELOPER)
			{
				player.sendMessage("Movement validation failed: speed check (moved " + 
					actualMove + " units, max: " + maxMovePerTick + ")");
			}
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(new ValidateLocation(player));
			return;
		}
		
		final double heightDiff = Math.abs(clientPosition.getZ() - serverPosition.getZ());
		if (heightDiff > MAX_HEIGHT_DIFF && !player.isFlying() && !player.isInWater())
		{
			if (Config.DEVELOPER)
			{
				player.sendMessage("Movement validation failed: height check (diff: " + heightDiff + ")");
			}
			player.sendPacket(ActionFailed.STATIC_PACKET);
			player.sendPacket(new ValidateLocation(player));
			return;
		}
		
		if (Config.SISTEMA_PATHFINDING)
		{
			if (!MovementIntegration.canMoveToTarget(
				serverPosition.getX(), serverPosition.getY(), serverPosition.getZ(),
				_x, _y, _z))
			{
				final Location validPos = GeoEngine.getInstance().getValidLocation(
					serverPosition.getX(), serverPosition.getY(), serverPosition.getZ(),
					_x, _y, _z, null
				);
				
				if (validPos != null && validPos.distance3D(serverPosition) < MAX_DISTANCE_DIFF)
				{
					player.setXYZ(validPos.getX(), validPos.getY(), validPos.getZ());
					player.getPosition().setHeading(_heading);
					player.sendPacket(new ValidateLocation(player, validPos));
					return;
				}
				else
				{
					player.sendPacket(ActionFailed.STATIC_PACKET);
					player.sendPacket(new ValidateLocation(player));
					return;
				}
			}
		}
		
		
		final int terrainHeight = GeoEngine.getInstance().getHeight(_x, _y, _z);
		final int heightDiffTerrain = Math.abs(_z - terrainHeight);
		
		if (heightDiffTerrain < 50 && heightDiffTerrain > 5)
		{
			_z = terrainHeight;
		}
		
		player.setXYZ(_x, _y, _z);
		player.getPosition().setHeading(_heading);
		
		if (heightDiffTerrain >= 5 && heightDiffTerrain < 50)
		{
			player.sendPacket(new ValidateLocation(player));
		}
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}