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
package ext.mods.battlerboss.holder;

import ext.mods.commons.data.StatSet;

public class TeleportHolder
{
	private final int arenaX, arenaY, arenaZ;
	private final int returnX, returnY, returnZ;
	private final int returnDelay;
	
	public TeleportHolder(StatSet set)
	{
		arenaX = set.getInteger("arenaX", 0);
		arenaY = set.getInteger("arenaY", 0);
		arenaZ = set.getInteger("arenaZ", 0);
		
		returnX = set.getInteger("returnX", 0);
		returnY = set.getInteger("returnY", 0);
		returnZ = set.getInteger("returnZ", 0);
		returnDelay = set.getInteger("delay", 0);
	}
	
	public int getArenaX()
	{
		return arenaX;
	}
	
	public int getArenaY()
	{
		return arenaY;
	}
	
	public int getArenaZ()
	{
		return arenaZ;
	}
	
	public int getReturnX()
	{
		return returnX;
	}
	
	public int getReturnY()
	{
		return returnY;
	}
	
	public int getReturnZ()
	{
		return returnZ;
	}
	
	public int getReturnDelay()
	{
		return returnDelay;
	}
}
