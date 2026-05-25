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
package ext.mods.gameserver.enums.skills;

public enum ElementType
{
	NONE(null, null),
	WIND(Stats.WIND_POWER, Stats.WIND_RES),
	FIRE(Stats.FIRE_POWER, Stats.FIRE_RES),
	WATER(Stats.WATER_POWER, Stats.WATER_RES),
	EARTH(Stats.EARTH_POWER, Stats.EARTH_RES),
	HOLY(Stats.HOLY_POWER, Stats.HOLY_RES),
	DARK(Stats.DARK_POWER, Stats.DARK_RES),
	VALAKAS(Stats.VALAKAS_POWER, Stats.VALAKAS_RES);
	
	public static final ElementType[] VALUES = values();
	
	private ElementType(Stats atkStat, Stats resStat)
	{
		_atkStat = atkStat;
		_resStat = resStat;
	}
	
	private Stats _atkStat;
	private Stats _resStat;
	
	public Stats getAtkStat()
	{
		return _atkStat;
	}
	
	public Stats getResStat()
	{
		return _resStat;
	}
}