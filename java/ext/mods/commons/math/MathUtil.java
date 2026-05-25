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
package ext.mods.commons.math;

import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;

public class MathUtil
{
	private MathUtil()
	{
		throw new IllegalStateException("Utility class");
	}
	
	public static final int[][] MATRICE_3X3_LINES =
	{
		{
			1,
			2,
			3
		},
		{
			4,
			5,
			6
		},
		{
			7,
			8,
			9
		},
		{
			1,
			4,
			7
		},
		{
			2,
			5,
			8
		},
		{
			3,
			6,
			9
		},
		{
			1,
			5,
			9
		},
		{
			3,
			5,
			7
		},
	};
	
	/**
	 * @param objectsSize : The overall elements size.
	 * @param pageSize : The number of elements per page.
	 * @return The number of pages, based on the number of elements and the number of elements we want per page.
	 */
	public static int countPagesNumber(int objectsSize, int pageSize)
	{
		return objectsSize / pageSize + (objectsSize % pageSize == 0 ? 0 : 1);
	}
	
	/**
	 * Calculate the angle in degrees between two {@link WorldObject} instances on a 2D plane.<br>
	 * <br>
	 * The angle is calculated based on the positions of the two objects, relative to the positive X-axis.
	 * @param obj1 : The first {@link WorldObject} from which the angle is calculated.
	 * @param obj2 : The second {@link WorldObject} to which the angle is calculated.
	 * @return The angle in degrees, ranging from 0 to 360 degrees.
	 */
	public static double calculateAngleFrom(WorldObject obj1, WorldObject obj2)
	{
		return calculateAngleFrom(obj1.getX(), obj1.getY(), obj2.getX(), obj2.getY());
	}
	
	/**
	 * Calculate the angle in degrees between two points on a 2D plane.<br>
	 * <br>
	 * The angle is calculated based on the coordinates of the two points, relative to the positive X-axis.
	 * @param obj1X : The X coordinate of the first point.
	 * @param obj1Y : The Y coordinate of the first point.
	 * @param obj2X : The X coordinate of the second point.
	 * @param obj2Y : The Y coordinate of the second point.
	 * @return The angle in degrees, ranging from 0 to 360 degrees.
	 */
	public static final double calculateAngleFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		if (angleTarget < 0)
			angleTarget += 360;
		
		return angleTarget;
	}
	
	/**
	 * Convert a game-specific heading value to degrees.<br>
	 * <br>
	 * The heading is typically represented as an integer value in a range where 0 corresponds to 0 degrees, and 65535 corresponds to just under 360 degrees. This method converts that heading to a standard degree value.
	 * @param clientHeading : The heading value to be converted, typically in the range [0, 65535].
	 * @return The corresponding angle in degrees.
	 */
	public static final double convertHeadingToDegree(int clientHeading)
	{
		return clientHeading / 182.04444444444444;
	}
	
	/**
	 * Calculate a new location based on a starting point, a heading, and a distance.<br>
	 * <br>
	 * The method converts the heading to degrees and calculates the new position after moving a specified distance in the direction of the heading from the given coordinates.
	 * @param x : The X coordinate of the starting point.
	 * @param y : The Y coordinate of the starting point.
	 * @param heading : The heading in game-specific units (not degrees).
	 * @param distance : The distance to move from the starting point in the direction of the heading.
	 * @return A {@link Point2D} representing the new location after the move.
	 */
	public static final Point2D getNewLocationByDistanceAndHeading(int x, int y, int heading, int distance)
	{
		return getNewLocationByDistanceAndDegree(x, y, MathUtil.convertHeadingToDegree(heading), distance);
	}
	
	/**
	 * Calculate a new location based on a starting point, an angle in degrees, and a distance.<br>
	 * <br>
	 * The method calculates the new position after moving a specified distance in the direction of the given angle from the provided coordinates.
	 * @param x : The X coordinate of the starting point.
	 * @param y : The Y coordinate of the starting point.
	 * @param degree : The angle in degrees from the positive X-axis.
	 * @param distance : The distance to move from the starting point in the direction of the angle.
	 * @return A {@link Point2D} representing the new location after the move.
	 */
	public static final Point2D getNewLocationByDistanceAndDegree(int x, int y, double degree, int distance)
	{
		final double radians = Math.toRadians(degree);
		final int deltaX = (int) (distance * Math.cos(radians));
		final int deltaY = (int) (distance * Math.sin(radians));
		
		return new Point2D(x + deltaX, y + deltaY);
	}
	
	/**
	 * Calculate the heading angle from one point to another in a 2D plane.<br>
	 * <br>
	 * The heading is computed as the angle in degrees between the line formed by the two points and the positive X-axis, adjusted to fall within the range [0, 360) degrees. This angle is then converted into a game-specific heading, where 360 degrees corresponds to 65536 units.
	 * @param obj1X : The X coordinate of the first point.
	 * @param obj1Y : The Y coordinate of the first point.
	 * @param obj2X : The X coordinate of the second point.
	 * @param obj2Y : The Y coordinate of the second point.
	 * @return The calculated heading as an integer, where 0 represents 0 degrees, and 65535 represents just under 360 degrees
	 */
	public static final int calculateHeadingFrom(int obj1X, int obj1Y, int obj2X, int obj2Y)
	{
		double angleTarget = Math.toDegrees(Math.atan2(obj2Y - obj1Y, obj2X - obj1X));
		
		if (angleTarget < 0)
			angleTarget += 360;
		
		return (int) Math.round(angleTarget * 182.04444444444444);
	}
	
	/**
	 * @param val : The initial value to edit.
	 * @param numPlaces : The number of decimals to keep.
	 * @return The rounded value to specified number of digits after the decimal point. Based on PHP round().
	 */
	public static float roundTo(float val, int numPlaces)
	{
		if (numPlaces <= 1)
			return Math.round(val);
		
		float exponent = (float) Math.pow(10, numPlaces);
		
		return (Math.round(val * exponent) / exponent);
	}
	
	public static double calculateDistance(int x1, int y1, int z1, int x2, int y2, int z2, boolean includeZAxis)
	{
		double dx = x1 - x2;
		double dy = y1 - y2;
		double dz = includeZAxis ? (z1 - z2) : 0;
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	public static double calculateDistance(Player fakePlayer, Creature creature, boolean includeZAxis)
	{
		if (fakePlayer == null || creature == null)
			return Double.MAX_VALUE;
		
		return calculateDistance(fakePlayer.getX(), fakePlayer.getY(), fakePlayer.getZ(), creature.getX(), creature.getY(), creature.getZ(), includeZAxis);
	}
	
	public static double calculateDistance(Player fakePlayer, Location loc, boolean includeZAxis)
	{
		if (fakePlayer == null || loc == null)
		{
			return Double.MAX_VALUE;
		}
		
		return calculateDistance(fakePlayer.getX(), fakePlayer.getY(), fakePlayer.getZ(), loc.getX(), loc.getY(), loc.getZ(), includeZAxis);
	}
}