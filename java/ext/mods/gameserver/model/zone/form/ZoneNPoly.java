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
package ext.mods.gameserver.model.zone.form;

import java.awt.Color;

import ext.mods.gameserver.model.zone.ZoneForm;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class ZoneNPoly extends ZoneForm
{
	protected final int[] _x;
	protected final int[] _y;
	private final int _z1;
	private final int _z2;
	
	public ZoneNPoly(int[] x, int[] y, int z1, int z2)
	{
		_x = x;
		_y = y;
		_z1 = z1;
		_z2 = z2;
	}
	
	@Override
	public boolean isInsideZone(int x, int y, int z)
	{
		if (z < _z1 || z > _z2)
			return false;
		
		boolean inside = false;
		for (int i = 0, j = _x.length - 1; i < _x.length; j = i++)
		{
			if ((((_y[i] <= y) && (y < _y[j])) || ((_y[j] <= y) && (y < _y[i]))) && (x < (_x[j] - _x[i]) * (y - _y[i]) / (_y[j] - _y[i]) + _x[i]))
				inside = !inside;
		}
		return inside;
	}
	
	@Override
	public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2)
	{
		int tX;
		int tY;
		int uX;
		int uY;
		
		if (_x[0] > ax1 && _x[0] < ax2 && _y[0] > ay1 && _y[0] < ay2)
			return true;
		
		if (isInsideZone(ax1, ay1, (_z2 - 1)))
			return true;
		
		for (int i = 0; i < _y.length; i++)
		{
			tX = _x[i];
			tY = _y[i];
			uX = _x[(i + 1) % _x.length];
			uY = _y[(i + 1) % _x.length];
			
			if (lineSegmentsIntersect(tX, tY, uX, uY, ax1, ay1, ax1, ay2))
				return true;
			
			if (lineSegmentsIntersect(tX, tY, uX, uY, ax1, ay1, ax2, ay1))
				return true;
			
			if (lineSegmentsIntersect(tX, tY, uX, uY, ax2, ay2, ax1, ay2))
				return true;
			
			if (lineSegmentsIntersect(tX, tY, uX, uY, ax2, ay2, ax2, ay1))
				return true;
		}
		
		return false;
	}
	
	@Override
	public int getLowZ()
	{
		return _z1;
	}
	
	@Override
	public int getHighZ()
	{
		return _z2;
	}
	
	@Override
	public void visualizeZone(String info, ExServerPrimitive debug)
	{
		final int z1 = _z1 - 32;
		final int z2 = _z2 - 32;
		
		for (int i = 0; i < _x.length; i++)
		{
			int nextIndex = i + 1;
			
			if (nextIndex == _x.length)
				nextIndex = 0;
			
			debug.addLine(info, Color.GREEN, true, _x[i], _y[i], z1, _x[nextIndex], _y[nextIndex], z1);
			debug.addLine(info, Color.RED, true, _x[i], _y[i], z2, _x[nextIndex], _y[nextIndex], z2);
			
			debug.addLine("", Color.YELLOW, true, _x[i], _y[i], z1, _x[i], _y[i], z2);
		}
	}
}