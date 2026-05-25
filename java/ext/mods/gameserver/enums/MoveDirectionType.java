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
package ext.mods.gameserver.enums;

import ext.mods.gameserver.geoengine.geodata.GeoStructure;

/**
 * Container of movement constants used for various geodata and movement checks.
 */
public enum MoveDirectionType
{
	N(0, -1),
	S(0, 1),
	W(-1, 0),
	E(1, 0),
	NW(-1, -1),
	SW(-1, 1),
	NE(1, -1),
	SE(1, 1);
	
	final int _stepX;
	final int _stepY;
	final int _signumX;
	final int _signumY;
	
	final int _offsetX;
	final int _offsetY;
	
	final byte _directionX;
	final byte _directionY;
	final String _symbolX;
	final String _symbolY;
	
	private MoveDirectionType(int signumX, int signumY)
	{
		_stepX = signumX * GeoStructure.CELL_SIZE;
		_stepY = signumY * GeoStructure.CELL_SIZE;
		_signumX = signumX;
		_signumY = signumY;
		
		_offsetX = signumX >= 0 ? GeoStructure.CELL_SIZE - 1 : 0;
		_offsetY = signumY >= 0 ? GeoStructure.CELL_SIZE - 1 : 0;
		
		_directionX = signumX < 0 ? GeoStructure.CELL_FLAG_W : signumX == 0 ? 0 : GeoStructure.CELL_FLAG_E;
		_directionY = signumY < 0 ? GeoStructure.CELL_FLAG_N : signumY == 0 ? 0 : GeoStructure.CELL_FLAG_S;
		_symbolX = signumX < 0 ? "W" : signumX == 0 ? "-" : "E";
		_symbolY = signumY < 0 ? "N" : signumY == 0 ? "-" : "S";
	}
	
	public final int getStepX()
	{
		return _stepX;
	}
	
	public final int getStepY()
	{
		return _stepY;
	}
	
	public final int getSignumX()
	{
		return _signumX;
	}
	
	public final int getSignumY()
	{
		return _signumY;
	}
	
	public final int getOffsetX()
	{
		return _offsetX;
	}
	
	public final int getOffsetY()
	{
		return _offsetY;
	}
	
	public final byte getDirectionX()
	{
		return _directionX;
	}
	
	public final byte getDirectionY()
	{
		return _directionY;
	}
	
	public final String getSymbolX()
	{
		return _symbolX;
	}
	
	public final String getSymbolY()
	{
		return _symbolY;
	}
	
	/**
	 * @param gdx : Geodata X delta coordinate.
	 * @param gdy : Geodata Y delta coordinate.
	 * @return {@link MoveDirectionType} based on given geodata dx and dy delta coordinates.
	 */
	public static final MoveDirectionType getDirection(int gdx, int gdy)
	{
		if (gdx == 0)
			return (gdy < 0) ? MoveDirectionType.N : MoveDirectionType.S;
		
		if (gdy == 0)
			return (gdx < 0) ? MoveDirectionType.W : MoveDirectionType.E;
		
		if (gdx > 0)
			return (gdy < 0) ? MoveDirectionType.NE : MoveDirectionType.SE;
		
		return (gdy < 0) ? MoveDirectionType.NW : MoveDirectionType.SW;
	}
}