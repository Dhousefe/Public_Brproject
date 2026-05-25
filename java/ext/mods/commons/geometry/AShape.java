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

import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

/**
 * An abstract class involving a shape of any form.
 */
public abstract class AShape
{
	/**
	 * @param x : Tested x coordinate.
	 * @param y : Tested y coordinate.
	 * @return True if given coordinates are laying inside this {@link AShape}, or false otherwise.
	 */
	public abstract boolean isInside(int x, int y);
	
	/**
	 * @param x1 : The x coordinate of the first point of the specified rectangle.
	 * @param y1 : The y coordinate of the first point of the specified rectangle.
	 * @param x2 : The x coordinate of the second point of the specified rectangle.
	 * @param y2 : The y coordinate of the second point of the specified rectangle.
	 * @return True if this {@link AShape} intersects with the specified rectangle defined by the given coordinates, or false otherwise.
	 */
	public abstract boolean intersects(int x1, int y1, int x2, int y2);
	
	/**
	 * Z is always 0 and has to be fed elsewhere (notably {@link Territory} holding this {@link AShape}).
	 * @return A random {@link Location} inside this {@link AShape}.
	 */
	public abstract Location getRandomLocation();
	
	/**
	 * Add the visualization of itself as a 2D shape to given {@link ExServerPrimitive} packet.
	 * @param info : The name to be displayed.
	 * @param debug : The given {@link ExServerPrimitive} packet to be added into.
	 * @param z : The Z coordinate as a view reference.
	 */
	public abstract void visualize(String info, ExServerPrimitive debug, int z);
	
	/**
	 * Add the visualization of itself as a 3D shape to given {@link ExServerPrimitive} packet.
	 * @param info : The name to be displayed.
	 * @param debug : The given {@link ExServerPrimitive} packet to be added into.
	 * @param minZ : The min Z coordinate as a view reference.
	 * @param maxZ : The max Z coordinate as a view reference.
	 */
	public abstract void visualize3D(String info, ExServerPrimitive debug, int minZ, int maxZ);
	
	protected Point2D _center;
	
	protected long _area;
	
	/**
	 * @return The center of this {@link AShape}.
	 */
	public Point2D getCenter()
	{
		return _center;
	}
	
	/**
	 * @return The surface area of this {@link AShape}.
	 */
	public long getArea()
	{
		return _area;
	}
}