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

import java.awt.Color;
import java.util.List;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class Triangle extends AShape
{
	protected final int _Ax;
	protected final int _Ay;
	
	protected final int _BAx;
	protected final int _BAy;
	
	protected final int _CAx;
	protected final int _CAy;
	
	public Triangle(List<Point2D> coords)
	{
		this(coords.get(0), coords.get(1), coords.get(2));
	}
	
	public Triangle(Point2D A, Point2D B, Point2D C)
	{
		_Ax = A.getX();
		_Ay = A.getY();
		
		_BAx = B.getX() - A.getX();
		_BAy = B.getY() - A.getY();
		
		_CAx = C.getX() - A.getX();
		_CAy = C.getY() - A.getY();
		
		_center = new Point2D((A.getX() + B.getX() + C.getX()) / 3, (A.getY() + B.getY() + C.getY()) / 3);
		_area = Math.abs((long) (_BAx * _CAy - _CAx * _BAy)) / 2;
	}
	
	@Override
	public final boolean isInside(int x, int y)
	{
		final long dx = x - _Ax;
		final long dy = y - _Ay;
		
		final boolean a = (0 - dx) * (_BAy - 0) - (_BAx - 0) * (0 - dy) >= 0;
		final boolean b = (_BAx - dx) * (_CAy - _BAy) - (_CAx - _BAx) * (_BAy - dy) >= 0;
		final boolean c = (_CAx - dx) * (0 - _CAy) - (0 - _CAx) * (_CAy - dy) >= 0;
		
		return a == b && b == c;
	}
	
	@Override
	public boolean intersects(int x1, int y1, int x2, int y2)
	{
		int minX = Math.min(_Ax, Math.min(x1, x2));
		int maxX = Math.max(_Ax, Math.max(x1, x2));
		int minY = Math.min(_Ay, Math.min(y1, y2));
		int maxY = Math.max(_Ay, Math.max(y1, y2));
		
		return minX < (_Ax + _BAx) && maxX > _Ax && minY < (_Ay + _BAy) && maxY > _Ay;
	}
	
	@Override
	public Location getRandomLocation()
	{
		double ba = Rnd.nextDouble();
		double ca = Rnd.nextDouble();
		
		if (ba + ca > 1)
		{
			ba = 1 - ba;
			ca = 1 - ca;
		}
		
		final int x = _Ax + (int) (ba * _BAx + ca * _CAx);
		final int y = _Ay + (int) (ba * _BAy + ca * _CAy);
		
		return new Location(x, y, 0);
	}
	
	@Override
	public void visualize(String info, ExServerPrimitive debug, int z)
	{
		final int x2 = _Ax + _BAx;
		final int y2 = _Ay + _BAy;
		final int x3 = _Ax + _CAx;
		final int y3 = _Ay + _CAy;
		
		debug.addLine(info, Color.YELLOW, true, _Ax, _Ay, z, x2, y2, z);
		debug.addLine(info, Color.YELLOW, true, _Ax, _Ay, z, x3, y3, z);
		debug.addLine(info, Color.YELLOW, true, x2, y2, z, x3, y3, z);
	}
	
	@Override
	public void visualize3D(String info, ExServerPrimitive debug, int minZ, int maxZ)
	{
		final int x2 = _Ax + _BAx;
		final int y2 = _Ay + _BAy;
		final int x3 = _Ax + _CAx;
		final int y3 = _Ay + _CAy;
		
		debug.addLine(info, Color.GREEN, true, _Ax, _Ay, minZ, x2, y2, minZ);
		debug.addLine(info, Color.GREEN, true, _Ax, _Ay, minZ, x3, y3, minZ);
		debug.addLine(info, Color.GREEN, true, x2, y2, minZ, x3, y3, minZ);
		
		debug.addLine(info, Color.RED, true, _Ax, _Ay, maxZ, x2, y2, maxZ);
		debug.addLine(info, Color.RED, true, _Ax, _Ay, maxZ, x3, y3, maxZ);
		debug.addLine(info, Color.RED, true, x2, y2, maxZ, x3, y3, maxZ);
		
		debug.addLine(info, Color.YELLOW, true, _Ax, _Ay, minZ, _Ax, _Ay, maxZ);
		debug.addLine(info, Color.YELLOW, true, x2, y2, minZ, x2, y2, maxZ);
		debug.addLine(info, Color.YELLOW, true, x3, y3, minZ, x3, y3, maxZ);
	}
}