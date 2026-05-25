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

import ext.mods.gameserver.model.zone.form.ZoneCylinder;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class ZoneCylinderZ extends ZoneCylinder
{
	public ZoneCylinderZ(int x, int y, int z1, int z2, int rad)
	{
		super(x, y, z1, z2, rad);
	}
	
	@Override
	public void visualizeZone(String info, ExServerPrimitive debug)
	{
		final int count = (int) (2 * Math.PI * _rad / STEP);
	    final double angle = 2 * Math.PI / count;

	    int prevX = (int) (Math.cos(0) * _rad) + _x;
	    int prevY = (int) (Math.sin(0) * _rad) + _y;

	    for (int i = 1; i <= count; i++)
	    {
	        final int x = (int) (Math.cos(angle * i) * _rad) + _x;
	        final int y = (int) (Math.sin(angle * i) * _rad) + _y;

	        debug.addLine("", Color.YELLOW, true, prevX, prevY, _z1, x, y, _z1);

	        prevX = x;
	        prevY = y;
	    }
	}
}