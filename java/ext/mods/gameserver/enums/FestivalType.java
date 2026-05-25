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

public enum FestivalType
{
	MAX_31(60, "Level 31 or lower", 31),
	MAX_42(70, "Level 42 or lower", 42),
	MAX_53(100, "Level 53 or lower", 53),
	MAX_64(120, "Level 64 or lower", 64),
	MAX_NONE(150, "No Level Limit", 80);
	
	private final int _maxScore;
	private final String _name;
	private final int _maxLevel;
	
	private FestivalType(int maxScore, String name, int maxLevel)
	{
		_maxScore = maxScore;
		_name = name;
		_maxLevel = maxLevel;
	}
	
	public int getMaxScore()
	{
		return _maxScore;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getMaxLevel()
	{
		return _maxLevel;
	}
	
	public static final FestivalType[] VALUES = values();
}