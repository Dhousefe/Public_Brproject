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
package ext.mods.gameserver.model.entity.autofarm.zone.form;

import java.awt.Color;
import java.util.Arrays;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.zone.form.ZoneNPoly;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class ZoneNPolyZ extends ZoneNPoly
{
	private final int[] _z;
	
	public ZoneNPolyZ(int[] x, int[] y, int[] z, int z1, int z2)
	{
		super(x, y, z1, z2);
		
		_z = z;
	}
	
	@Override
	public void visualizeZone(String info, ExServerPrimitive debug)
	{
		debug.reset();
		
		for (int i = 0; i < _x.length; i++)
		{
			int nextIndex = i + 1;
			
			if (nextIndex == _x.length)
				nextIndex = 0;
			
			debug.addPoint(String.valueOf(i + 1), Color.RED, true, _x[i], _y[i], _z[i]);
			
			if (info.contains("ROTA") && nextIndex == 0)
				continue;
			
			debug.addLine(info, Color.RED, true, _x[i], _y[i], _z[i], _x[nextIndex], _y[nextIndex], _z[nextIndex]);
		}
	}
	
	/*
	 * Based on the intersectsRectangle of ZoneNPoly, but adding the Z.
	 */
	public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2, int minZ, int maxZ)
	{
		int tX, tY, tZ;
		int uX, uY, uZ;

		if (_x[0] > ax1 && _x[0] < ax2 && _y[0] > ay1 && _y[0] < ay2 && _z[0] >= minZ && _z[0] <= maxZ)
			return true;

		if (isInsideZone(ax1, ay1, minZ) || isInsideZone(ax1, ay1, maxZ))
			return true;

		for (int i = 0; i < _y.length; i++)
		{
			tX = _x[i];
			tY = _y[i];
			tZ = _z[i];

			uX = _x[(i + 1) % _x.length];
			uY = _y[(i + 1) % _x.length];
			uZ = _z[(i + 1) % _x.length];

			if (tZ >= minZ && tZ <= maxZ && uZ >= minZ && uZ <= maxZ)
			{
				if (lineSegmentsIntersect(tX, tY, uX, uY, ax1, ay1, ax1, ay2))
					return true;

				if (lineSegmentsIntersect(tX, tY, uX, uY, ax1, ay1, ax2, ay1))
					return true;

				if (lineSegmentsIntersect(tX, tY, uX, uY, ax2, ay2, ax1, ay2))
					return true;

				if (lineSegmentsIntersect(tX, tY, uX, uY, ax2, ay2, ax2, ay1))
					return true;
			}
		}

		return false;
	}
	
	public boolean intersectsRectangleOnEdge(int x1, int x2, int y1, int y2)
	{
		for (int i = 0; i < _y.length; i++)
		{
			int tX = _x[i];
			int tY = _y[i];
			int uX = _x[(i + 1) % _x.length];
			int uY = _y[(i + 1) % _x.length];

			if (lineSegmentsIntersect(tX, tY, uX, uY, x1, y1, x1, y2))
				return true;

			if (lineSegmentsIntersect(tX, tY, uX, uY, x1, y1, x2, y1))
				return true;

			if (lineSegmentsIntersect(tX, tY, uX, uY, x2, y2, x1, y2))
				return true;

			if (lineSegmentsIntersect(tX, tY, uX, uY, x2, y2, x2, y1))
				return true;
		}

		return false;
	}

	
	public Location findPointInCenter()
	{
		int centerX = 0;
		int centerY = 0;
		int centerZ = (getLowZ() + getHighZ()) / 2;

		for (int i = 0; i < _x.length; i++)
		{
			centerX += _x[i];
			centerY += _y[i];
		}
		centerX /= _x.length;
		centerY /= _y.length;

		return new Location(centerX, centerY, centerZ);
	}

	public Location getRandomPoint()
	{
		int minX = Arrays.stream(_x).min().getAsInt();
		int maxX = Arrays.stream(_x).max().getAsInt();
		int minY = Arrays.stream(_y).min().getAsInt();
		int maxY = Arrays.stream(_y).max().getAsInt();

		for (int i = 0; i < 20; i++)
		{
			int randomX = Rnd.nextInt(maxX - minX + 1) + minX;
			int randomY = Rnd.nextInt(maxY - minY + 1) + minY;
			int randomZ = Rnd.nextInt(getHighZ() - getLowZ() + 1) + getLowZ();

			if (isInsideZone(randomX, randomY, randomZ))
				return new Location(randomX, randomY, randomZ);
		}

		return null;
	}
}