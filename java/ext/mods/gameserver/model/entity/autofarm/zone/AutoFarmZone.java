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
package ext.mods.gameserver.model.entity.autofarm.zone;

import java.util.List;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.WorldRegion;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class AutoFarmZone extends AutoFarmArea
{
	private boolean _isBuilt;
	
	public AutoFarmZone(int zoneId, String name, int ownerId)
	{
		super(zoneId, name, ownerId, AutoFarmType.ZONA);
	}
	
	public AutoFarmZone(String name, int ownerId)
	{
		super(name, ownerId, AutoFarmType.ZONA);
	}

	@Override
	public void onEnter(Creature character)
	{
		if (character.getActingPlayer() != null)
		{
			if (character.getObjectId() != getOwnerId())
				return;
			
			if (!getProfile().isEnabled())
				character.sendMessage("You have entered an AutoFarm zone.");
		}
		else
			getMonsterHistory().add(character.getName());
		
		character.setInsideZone(ZoneId.AUTO_FARM, true);
	}

	@Override
	public void onExit(Creature character)
	{
		if (character.getActingPlayer() != null && !getProfile().isEnabled())
			character.sendMessage("You have exited the AutoFarm zone.");
		
		character.setInsideZone(ZoneId.AUTO_FARM, false);
	}
	
	@Override
	public void visualizeZone(ExServerPrimitive debug)
	{
		getZone().visualizeZone("ZONA " + getName(), debug);
	}
	
	@Override
	public AutoFarmZone getFarmZone()
	{
		return this;
	}
	
	public boolean isBuilt()
	{
		return _isBuilt;
	}
	
	@Override
	public List<Monster> getMonsters()
	{
		return getKnownTypeInside(Monster.class);
	}
	
	public Location findValidLocation()
	{
		Location center = getZoneZ().findPointInCenter();
		int attempt = 0;
		int radius = 100;

		while (attempt < 10)
		{
			if (getOwner().getMove().maybeMoveToLocation(center, 0, true, true))
			{
				return center;
			}

			double angle = Math.random() * 2 * Math.PI;
			int newX = (int) (center.getX() + radius * Math.cos(angle));
			int newY = (int) (center.getY() + radius * Math.sin(angle));

			int newZ = center.getZ();

			center = new Location(newX, newY, newZ);
			radius += 100;
			attempt++;
		}

		return null;
	}
	
	public boolean tryGoBackInside()
	{
		final Location way = getWayIn();
		if (way != null)
		{
			getOwner().getMove().maybePlayerMoveToLocation(way, 0, Config.SISTEMA_PATHFINDING, false);
			return true;
		}
		
		return false;
	}
	
	private Location getWayIn()
	{
		final Player owner = getOwner();
		final Location center = getZoneZ().findPointInCenter();
		
		if (GeoEngine.getInstance().canMoveToTarget(owner, center))
			return center;
		
		final List<Monster> monsters = getMonsters().stream().filter(m -> GeoEngine.getInstance().canMoveToTarget(owner, m)).toList();
		if (!monsters.isEmpty())
			return Rnd.get(monsters).getPosition().clone();
		
		for (int i = 0; i < 10; i++)
		{
			final Location loc = getZoneZ().getRandomPoint();
			if (loc != null && GeoEngine.getInstance().canMoveToTarget(owner, loc))
				return loc;
		}
		
		return null;
	}
	
	public void addToWorld()
	{
		if (_isBuilt)
			return;

		updateWorldRegions();
		_isBuilt = true;
	}
	
	public void removeFromWorld()
	{
		if (!_isBuilt)
			return;

		getCreatures().forEach(c -> removeCreature(c));
		updateWorldRegions();
		_isBuilt = false;
	}
	
	private void updateWorldRegions()
	{
		final WorldRegion[][] regions = World.getInstance().getWorldRegions();
		for (int x = 0; x < regions.length; x++)
		{
			final int xLoc = World.getRegionX(x);
			final int xLoc2 = World.getRegionX(x + 1);
			for (int y = 0; y < regions[x].length; y++)
			{
				if (getZone().intersectsRectangle(xLoc, xLoc2, World.getRegionY(y), World.getRegionY(y + 1)))
				{
					if (_isBuilt)
						regions[x][y].removeZone(this);
					else
					{
						for (WorldObject object : regions[x][y].getObjects())
						{
							if (object instanceof Creature)
								revalidateInZone(((Creature) object));
						}
						
						regions[x][y].addZone(this);
					}
				}
			}
		}
	}
}