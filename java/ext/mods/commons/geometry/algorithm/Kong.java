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
package ext.mods.commons.geometry.algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.geometry.Triangle;

import ext.mods.gameserver.model.location.Point2D;

public class Kong
{
	private Kong()
	{
		throw new IllegalStateException("Utility class");
	}
	
	private static final int TRIANGULATION_MAX_LOOPS = 100;
	
	/**
	 * Creates a triangulated polygon using Kong's algorithm.<br>
	 * The list of points must form a monotone polygon, otherwise the algorithm fails.
	 * @param points : List of {@link Point} points forming a polygon.
	 * @return List of {@link Triangle}s forming a polygon.
	 * @throws IndexOutOfBoundsException : Less than 3 {@link Point2D}s.
	 * @throws IllegalArgumentException : Given {@link Point2D}s do not form monotone polygon.
	 */
	public static final List<Triangle> doTriangulation(List<Point2D> points) throws IndexOutOfBoundsException, IllegalArgumentException
	{
		if (points.size() < 3)
			throw new IndexOutOfBoundsException("Can't triangulate polygon from less than 3 coordinates.");
		
		final boolean isCw = getPolygonOrientation(points);
		
		final List<Point2D> nonConvexPoints = calculateNonConvexPoints(points, isCw);
		
		return doTriangulationAlgorithm(points, isCw, nonConvexPoints);
	}
	
	/**
	 * Returns clockwise (cw) or counter-clockwise (ccw) orientation of the polygon.
	 * @param points : List of all points.
	 * @return {@code boolean} : True, when the polygon is clockwise orientated.
	 */
	private static final boolean getPolygonOrientation(List<Point2D> points)
	{
		
		final int size = points.size();
		int index = 0;
		Point2D point = points.get(0);
		for (int i = 1; i < size; i++)
		{
			Point2D pt = points.get(i);
			
			if ((pt.getX() < point.getX()) || pt.getX() == point.getX() && pt.getY() > point.getY())
			{
				point = pt;
				index = i;
			}
		}
		
		final Point2D pointPrev = points.get(getPrevIndex(size, index));
		
		final Point2D pointNext = points.get(getNextIndex(size, index));
		
		final int vx = point.getX() - pointPrev.getX();
		final int vy = point.getY() - pointPrev.getY();
		final int res = pointNext.getX() * vy - pointNext.getY() * vx + vx * pointPrev.getY() - vy * pointPrev.getX();
		
		return res <= 0;
	}
	
	/**
	 * Returns next index to given index of data container.
	 * @param size : Size of the data container.
	 * @param index : Index to be compared.
	 * @return {@code int} : Next index.
	 */
	private static final int getNextIndex(int size, int index)
	{
		if (++index >= size)
			return index - size;
		
		return index;
	}
	
	/**
	 * Returns previous index to given index of data container.
	 * @param size : Size of the data container.
	 * @param index : Index to be compared.
	 * @return {@code int} : Previous index.
	 */
	private static final int getPrevIndex(int size, int index)
	{
		if (--index < 0)
			return size + index;
		
		return index;
	}
	
	/**
	 * This determines all concave vertices of the polygon and separate convex ones.
	 * @param points : List of all points.
	 * @param isCw : Polygon orientation (clockwise/counterclockwise).
	 * @return {@code List<Point>} : List of non-convex points.
	 */
	private static final List<Point2D> calculateNonConvexPoints(List<Point2D> points, boolean isCw)
	{
		final List<Point2D> nonConvexPoints = new ArrayList<>();
		
		final int size = points.size();
		for (int i = 0; i < size - 1; i++)
		{
			final Point2D point = points.get(i);
			final Point2D pointNext = points.get(getNextIndex(size, i));
			final Point2D pointNextNext = points.get(getNextIndex(size, i + 1));
			
			final int vx = pointNext.getX() - point.getX();
			final int vy = pointNext.getY() - point.getY();
			
			final boolean res = (pointNextNext.getX() * vy - pointNextNext.getY() * vx + vx * point.getY() - vy * point.getX()) > 0;
			if (res == isCw)
				nonConvexPoints.add(pointNext);
		}
		
		return nonConvexPoints;
	}
	
	/**
	 * Perform Kong's triangulation algorithm.
	 * @param points : List of all points.
	 * @param isCw : Polygon orientation (clockwise/counterclockwise).
	 * @param nonConvexPoints : List of all non-convex points.
	 * @return {@code List<Triangle>} : List of {@link Triangle}.
	 * @throws IllegalArgumentException : When coordinates are not aligned to form monotone polygon.
	 */
	private static final List<Triangle> doTriangulationAlgorithm(List<Point2D> points, boolean isCw, List<Point2D> nonConvexPoints) throws IllegalArgumentException
	{
		final List<Triangle> triangles = new ArrayList<>();
		
		int size = points.size();
		int loops = 0;
		int index = 1;
		while (size > 3)
		{
			final int indexPrev = getPrevIndex(size, index);
			final int indexNext = getNextIndex(size, index);
			
			final Point2D pointPrev = points.get(indexPrev);
			final Point2D point = points.get(index);
			final Point2D pointNext = points.get(indexNext);
			
			if (isEar(isCw, nonConvexPoints, pointPrev, point, pointNext))
			{
				triangles.add(new Triangle(pointPrev, point, pointNext));
				
				points.remove(index);
				size--;
				
				index = getPrevIndex(size, index);
			}
			else
			{
				index = indexNext;
			}
			
			if (++loops == TRIANGULATION_MAX_LOOPS)
				throw new IllegalArgumentException("Coordinates are not aligned to form monotone polygon.");
		}
		
		triangles.add(new Triangle(points.get(0), points.get(1), points.get(2)));
		
		return triangles;
	}
	
	/**
	 * Returns true if the triangle formed by A, B, C points is an ear considering the polygon - thus if no other point is inside and it is convex.
	 * @param isCw : Polygon orientation (clockwise/counterclockwise).
	 * @param nonConvexPoints : List of all non-convex points.
	 * @param A : ABC triangle
	 * @param B : ABC triangle
	 * @param C : ABC triangle
	 * @return {@code boolean} : True, when ABC is ear of the polygon.
	 */
	private static final boolean isEar(boolean isCw, List<Point2D> nonConvexPoints, Point2D A, Point2D B, Point2D C)
	{
		if (!isConvex(isCw, A, B, C))
			return false;
		
		for (Point2D p : nonConvexPoints)
		{
			if (isInside(A, B, C, p))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Returns true when the {@link Point2D} B is convex considered the actual polygon. A, B and C are three consecutive {@link Point2D} of the polygon.
	 * @param isCw : Polygon orientation (clockwise/counterclockwise).
	 * @param A : {@link Point2D}, previous to B.
	 * @param B : {@link Point2D}, which convex information is being checked.
	 * @param C : {@link Point2D}, next to B.
	 * @return {@code boolean} : True, when B is convex point.
	 */
	private static final boolean isConvex(boolean isCw, Point2D A, Point2D B, Point2D C)
	{
		final int BAx = B.getX() - A.getX();
		final int BAy = B.getY() - A.getY();
		
		final boolean cw = (C.getX() * BAy - C.getY() * BAx + BAx * A.getY() - BAy * A.getX()) > 0;
		
		return cw != isCw;
	}
	
	/**
	 * Returns true, when {@link Point2D} P is inside triangle ABC.
	 * @param A : ABC triangle
	 * @param B : ABC triangle
	 * @param C : ABC triangle
	 * @param P : {@link Point2D} to be checked in ABC.
	 * @return {@code boolean} : True, when P is inside ABC.
	 */
	private static final boolean isInside(Point2D A, Point2D B, Point2D C, Point2D P)
	{
		final int BAx = B.getX() - A.getX();
		final int BAy = B.getY() - A.getY();
		final int CAx = C.getX() - A.getX();
		final int CAy = C.getY() - A.getY();
		final int PAx = P.getX() - A.getX();
		final int PAy = P.getY() - A.getY();
		
		final double detXYZ = BAx * CAy - CAx * BAy;
		
		final double ba = (BAx * PAy - PAx * BAy) / detXYZ;
		final double ca = (PAx * CAy - CAx * PAy) / detXYZ;
		
		return (ba > 0 && ca > 0 && (ba + ca) < 1);
	}
}