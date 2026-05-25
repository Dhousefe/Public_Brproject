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

import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;

/**
 * Pacote de validação de localização melhorado.
 * Valida posição antes de enviar e corrige automaticamente se necessário.
 */
public final class ValidateLocation extends L2GameServerPacket
{
	private final int _objectId;
	private final Location _position;
	
	/**
	 * Cria pacote de validação com posição específica.
	 * Valida e corrige posição antes de enviar.
	 */
	public ValidateLocation(Creature creature, Location position)
	{
		_objectId = creature.getObjectId();
		
		_position = validateAndCorrectPosition(creature, position);
	}
	
	/**
	 * Cria pacote de validação com posição atual da criatura.
	 * Valida e corrige posição antes de enviar.
	 */
	public ValidateLocation(Creature creature)
	{
		_objectId = creature.getObjectId();
		
		_position = validateAndCorrectPosition(creature, creature.getPosition());
	}
	
	/**
	 * Valida e corrige posição antes de enviar ao cliente.
	 * Garante que a posição é válida usando geodata e outras verificações.
	 * 
	 * @param creature A criatura
	 * @param position Posição a validar
	 * @return Posição validada e corrigida
	 */
	private Location validateAndCorrectPosition(Creature creature, Location position)
	{
		final Location currentPos = creature.getPosition();
		final Location validatedPos = position.clone();
		
		if (validatedPos.getX() < -327680 || validatedPos.getX() > 327680 ||
			validatedPos.getY() < -262144 || validatedPos.getY() > 262144)
		{
			return currentPos.clone();
		}
		
		final int terrainHeight = GeoEngine.getInstance().getHeight(
			validatedPos.getX(), validatedPos.getY(), validatedPos.getZ()
		);
		
		if (!creature.isFlying() && !creature.isInWater())
		{
			final int heightDiff = Math.abs(validatedPos.getZ() - terrainHeight);
			
			if (heightDiff < 100 && heightDiff > 5)
			{
				validatedPos.setZ(terrainHeight);
			}
			else if (heightDiff > 200)
			{
				return currentPos.clone();
			}
		}
		
		if (creature instanceof Npc)
		{
			final double dist2D = currentPos.distance2D(validatedPos);
			
			if (dist2D > 50)
			{
				if (!GeoEngine.getInstance().canMoveToTarget(
					currentPos.getX(), currentPos.getY(), currentPos.getZ(),
					validatedPos.getX(), validatedPos.getY(), validatedPos.getZ()))
				{
					final Location validPos = GeoEngine.getInstance().getValidLocation(
						currentPos.getX(), currentPos.getY(), currentPos.getZ(),
						validatedPos.getX(), validatedPos.getY(), validatedPos.getZ(), null
					);
					
					if (validPos != null && validPos.distance3D(currentPos) < 200)
					{
						return validPos;
					}
					else
					{
						return currentPos.clone();
					}
				}
			}
		}
		
		if (creature instanceof Player)
		{
			final double dist3D = currentPos.distance3D(validatedPos);
			
			if (dist3D > 200 && !creature.isTeleporting())
			{
				return currentPos.clone();
			}
		}
		
		return validatedPos;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x79);
		writeD(_objectId);
		writeLoc(_position);
	}
}