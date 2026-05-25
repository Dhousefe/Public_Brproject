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
package ext.mods.gameserver.model.item;

public class LifeStone
{
	private static final int[] LEVELS =
	{
		46,
		49,
		52,
		55,
		58,
		61,
		64,
		67,
		70,
		76
	};
	
	private final int _grade;
	private final int _level;
	
	public LifeStone(int grade, int level)
	{
		_grade = grade;
		_level = level;
	}
	
	public final int getLevel()
	{
		return _level;
	}
	
	public final int getGrade()
	{
		return _grade;
	}
	
	public final int getPlayerLevel()
	{
		return LEVELS[_level];
	}
}