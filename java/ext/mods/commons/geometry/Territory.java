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
package ext.mods.commons.geometry;

import java.util.List;
import java.util.Set;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

/**
 * Define a 3D shaped area inside the world.<br>
 * <br>
 * The associated shape extends {@link AShape}, and is based on the number of accorded coords.
 */
public class Territory
{
	private static final CLogger LOGGER = new CLogger(Territory.class.getName());
	
	private static final int MAX_ITERATIONS = 10;
	
	private final String _name;
	
	private final int _minZ;
	private final int _maxZ;
	private final int _avgZ;
	
	private final AShape _shape;
	
	public Territory(StatSet set)
	{
		this(set.getString("name", null), set.getInteger("minZ"), set.getInteger("maxZ"), set.getList("coords"));
	}
	
	public Territory(String name, int minZ, int maxZ, List<Point2D> coords)
	{
		final int coordsNumber = coords.size();
		if (coordsNumber >= 4)
			_shape = new Polygon(coords);
		else if (coordsNumber == 3)
			_shape = new Triangle(coords);
		else if (coordsNumber == 2)
			_shape = new Rectangle(coords);
		else
			throw new IllegalArgumentException("Invalid number of coordinates for Territory");
		
		_name = name;
		
		_minZ = minZ;
		_maxZ = maxZ;
		_avgZ = (_minZ + _maxZ) / 2;
	}
	
	public Territory(String name, int minZ, int maxZ, Set<Triangle> shapes)
	{
		_shape = new Polygon(shapes);
		
		_name = name;
		
		_minZ = minZ;
		_maxZ = maxZ;
		_avgZ = (_minZ + _maxZ) / 2;
	}
	
	/**
	 * @return This {@link Territory} name.
	 */
	public final String getName()
	{
		return _name;
	}
	
	/**
	 * @return This {@link Territory} minimum Z coordinate.
	 */
	public final int getMinZ()
	{
		return _minZ;
	}
	
	/**
	 * @return This {@link Territory} maximum Z coordinate.
	 */
	public final int getMaxZ()
	{
		return _maxZ;
	}
	
	/**
	 * @return This {@link Territory} average Z coordinate.
	 */
	public final int getAvgZ()
	{
		return _avgZ;
	}
	
	/**
	 * @return The {@link AShape} associated to this {@link Territory}.
	 */
	public final AShape getShape()
	{
		return _shape;
	}
	
	/**
	 * @param x : The X coordinate to test.
	 * @param y : The Y coordinate to test.
	 * @return True if the tested 2D point is part of this {@link Territory}, or false otherwise.
	 */
	public boolean isInside(int x, int y)
	{
		return _shape.isInside(x, y);
	}
	
	/**
	 * @param x : The X coordinate to test.
	 * @param y : The Y coordinate to test.
	 * @param z : The Z coordinate to test.
	 * @return True if the tested 3D point is part of this {@link Territory}, or false otherwise.
	 */
	public boolean isInside(int x, int y, int z)
	{
		if (z < _minZ || z > _maxZ)
			return false;
		
		return _shape.isInside(x, y);
	}
	
	public boolean isInside(Location loc)
	{
		return loc != null && isInside(loc.getX(), loc.getY(), loc.getZ());
	}
	
	public boolean isInside(WorldObject object)
	{
		return object != null && isInside(object.getPosition());
	}
	
	/**
	 * @return A random {@link SpawnLocation} inside this {@link AShape}, validated by surrounding geodata.
	 */
	public SpawnLocation getRandomGeoLocation()
	{
		Location loc = null;
		
		int failedZ = 0;
		int failedGeo = 0;
		
		for (int i = 0; i < MAX_ITERATIONS; i++)
		{
			loc = _shape.getRandomLocation();
			loc.setZ(GeoEngine.getInstance().getHeight(loc.getX(), loc.getY(), _avgZ));
			
			if (loc.getZ() < _minZ || loc.getZ() > _maxZ)
			{
				failedZ++;
				continue;
			}
			
			if (!GeoEngine.getInstance().canMoveAround(loc.getX(), loc.getY(), loc.getZ()))
			{
				failedGeo++;
				continue;
			}
			
			return new SpawnLocation(loc.getX(), loc.getY(), loc.getZ(), Rnd.get(65536));
		}
		
		if (Config.DEVELOPER)
			LOGGER.warn("Territory name \"{}\", wrong Z {}, wrong geo {}", _name, failedZ, failedGeo);
		
		return (loc == null) ? null : new SpawnLocation(loc.getX(), loc.getY(), loc.getZ(), Rnd.get(65536));
	}
	
	/**
	 * @param bannedTerritory : The banned {@link Territory} to test.
	 * @return A random {@link SpawnLocation} inside this {@link AShape}, validated by surrounding geodata and eventual banned {@link Territory}.
	 */
	public SpawnLocation getRandomGeoLocation(Territory bannedTerritory)
	{
		Location loc = null;
		
		int failedZ = 0;
		int failedGeo = 0;
		
		for (int i = 0; i < MAX_ITERATIONS; i++)
		{
			loc = _shape.getRandomLocation();
			loc.setZ(GeoEngine.getInstance().getHeight(loc.getX(), loc.getY(), _avgZ));
			
			if (bannedTerritory != null && bannedTerritory.isInside(loc))
				continue;
			
			if (loc.getZ() < _minZ || loc.getZ() > _maxZ)
			{
				failedZ++;
				continue;
			}
			
			if (!GeoEngine.getInstance().canMoveAround(loc.getX(), loc.getY(), loc.getZ()))
			{
				failedGeo++;
				continue;
			}
			
			return new SpawnLocation(loc.getX(), loc.getY(), loc.getZ(), Rnd.get(65536));
		}
		
		if (Config.DEVELOPER)
			LOGGER.warn("Territory name \"{}\", wrong Z {}, wrong geo {}", _name, failedZ, failedGeo);
		
		return (loc == null) ? null : new SpawnLocation(loc.getX(), loc.getY(), loc.getZ(), Rnd.get(65536));
	}
	
	public void visualize(ExServerPrimitive debug)
	{
		_shape.visualize3D(_name, debug, _minZ, _maxZ);
	}
}