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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.geometry.algorithm.Kong;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

/**
 * A polygon consisting of various {@link Triangle}s.
 */
public class Polygon extends AShape
{
	protected final Collection<Triangle> _shapes;
	
	public Polygon(StatSet set)
	{
		this(set.getList("coords"));
	}
	
	public Polygon(List<Point2D> coords)
	{
		_shapes = Kong.doTriangulation(coords);
		
		for (Triangle shape : _shapes)
			_area += shape.getArea();
	}
	
	public Polygon(Set<Triangle> shapes)
	{
		_shapes = shapes;
		
		for (Triangle shape : _shapes)
			_area += shape.getArea();
	}
	
	@Override
	public boolean isInside(int x, int y)
	{
		for (Triangle shape : _shapes)
		{
			if (shape.isInside(x, y))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean intersects(int x1, int y1, int x2, int y2)
	{
		for (Triangle shape : _shapes)
		{
			if (shape.intersects(x1, y1, x2, y2))
				return true;
		}
		return false;
	}
	
	@Override
	public Location getRandomLocation()
	{
		long size = Rnd.get(_area);
		
		for (Triangle shape : _shapes)
		{
			size -= shape.getArea();
			if (size < 0)
				return shape.getRandomLocation();
		}
		
		return null;
	}
	
	@Override
	public void visualize(String info, ExServerPrimitive debug, int z)
	{
		for (Triangle shape : _shapes)
			shape.visualize(info, debug, z);
	}
	
	@Override
	public void visualize3D(String info, ExServerPrimitive debug, int minZ, int maxZ)
	{
		for (Triangle shape : _shapes)
			shape.visualize3D(info, debug, minZ, maxZ);
	}
	
	public Collection<Triangle> getShapes()
	{
		return _shapes;
	}
}