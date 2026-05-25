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
package ext.mods.gameserver.model.holder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceName
{
	private static final Map<Integer, String> _classNames = new HashMap<>();
	
	static
	{
		_classNames.put(88, "Duelist");
		_classNames.put(89, "Dreadnought");
		_classNames.put(90, "Phoenix Knight");
		_classNames.put(91, "Hell Knight");
		_classNames.put(92, "Sagittarius");
		_classNames.put(93, "Adventurer");
		_classNames.put(94, "Archmage");
		_classNames.put(95, "Soultaker");
		_classNames.put(96, "Arcana Lord");
		_classNames.put(97, "Cardinal");
		_classNames.put(98, "Hierophant");
		_classNames.put(99, "Eva Templar");
		_classNames.put(100, "Sword Muse");
		_classNames.put(101, "Wind Rider");
		_classNames.put(102, "Moonlight Sentinel");
		_classNames.put(103, "Mystic Muse");
		_classNames.put(104, "Elemental Master");
		_classNames.put(105, "Eva Saint");
		_classNames.put(106, "Shillien Templar");
		_classNames.put(107, "Spectral Dancer");
		_classNames.put(108, "Ghost Hunter");
		_classNames.put(109, "Ghost Sentinel");
		_classNames.put(110, "Storm Screamer");
		_classNames.put(111, "Spectral Master");
		_classNames.put(112, "Shillien Saint");
		_classNames.put(113, "Titan");
		_classNames.put(114, "Grand Khauatari");
		_classNames.put(115, "Dominator");
		_classNames.put(116, "Doomcryer");
		_classNames.put(117, "Fortune Seeker");
		_classNames.put(118, "Maestro");
	}
	
	public static String getName(int classId)
	{
		return _classNames.getOrDefault(classId, "Unknown (" + classId + ")");
	}
	
	public static List<Integer> getClassIdList()
	{
		return new ArrayList<>(_classNames.keySet());
	}
	
	public static Map<Integer, String> getClassMap()
	{
		return _classNames;
	}
}