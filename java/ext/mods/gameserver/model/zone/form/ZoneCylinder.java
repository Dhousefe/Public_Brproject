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

public class ZoneCylinder extends ZoneForm
{
	protected static final int STEP = 50;
	
	protected final int _x;
	protected final int _y;
	protected final int _z1;
	protected final int _z2;
	protected final int _rad;
	protected final int _radS;
	
	public ZoneCylinder(int x, int y, int z1, int z2, int rad)
	{
		_x = x;
		_y = y;
		_z1 = z1;
		_z2 = z2;
		_rad = rad;
		_radS = rad * rad;
	}
	
	@Override
	public boolean isInsideZone(int x, int y, int z)
	{
		if ((Math.pow(_x - x, 2) + Math.pow(_y - y, 2)) > _radS || z < _z1 || z > _z2)
			return false;
		
		return true;
	}
	
	@Override
	public boolean intersectsRectangle(int ax1, int ax2, int ay1, int ay2)
	{
		if (_x > ax1 && _x < ax2 && _y > ay1 && _y < ay2)
			return true;
		
		if ((Math.pow(ax1 - _x, 2) + Math.pow(ay1 - _y, 2)) < _radS)
			return true;
		
		if ((Math.pow(ax1 - _x, 2) + Math.pow(ay2 - _y, 2)) < _radS)
			return true;
		
		if ((Math.pow(ax2 - _x, 2) + Math.pow(ay1 - _y, 2)) < _radS)
			return true;
		
		if ((Math.pow(ax2 - _x, 2) + Math.pow(ay2 - _y, 2)) < _radS)
			return true;
		
		if (_x > ax1 && _x < ax2)
		{
			if (Math.abs(_y - ay2) < _rad)
				return true;
			
			if (Math.abs(_y - ay1) < _rad)
				return true;
		}
		
		if (_y > ay1 && _y < ay2)
		{
			if (Math.abs(_x - ax2) < _rad)
				return true;
			
			if (Math.abs(_x - ax1) < _rad)
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
		
		final int count = (int) (2 * Math.PI * _rad / STEP);
		final double angle = 2 * Math.PI / count;
		
		for (int i = 0; i < count; i++)
		{
			final int x = (int) (Math.cos(angle * i) * _rad) + _x;
			final int y = (int) (Math.sin(angle * i) * _rad) + _y;
			
			debug.addPoint(info, Color.GREEN, true, x, y, z1);
			debug.addPoint(info, Color.RED, true, x, y, z2);
			
			debug.addLine("", Color.YELLOW, true, x, y, z1, x, y, z2);
		}
	}
}