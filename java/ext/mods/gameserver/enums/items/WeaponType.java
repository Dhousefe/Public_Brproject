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
package ext.mods.gameserver.enums.items;

import ext.mods.gameserver.enums.skills.Stats;

public enum WeaponType implements ItemType
{
	NONE(40, null),
	SWORD(40, Stats.SWORD_WPN_VULN),
	BLUNT(40, Stats.BLUNT_WPN_VULN),
	DAGGER(40, Stats.DAGGER_WPN_VULN),
	BOW(500, Stats.BOW_WPN_VULN),
	POLE(66, Stats.POLE_WPN_VULN),
	ETC(40, null),
	FIST(40, null),
	DUAL(40, Stats.DUAL_WPN_VULN),
	DUALFIST(40, Stats.DUALFIST_WPN_VULN),
	BIGSWORD(40, Stats.BIGSWORD_WPN_VULN),
	FISHINGROD(40, null),
	BIGBLUNT(40, Stats.BIGBLUNT_WPN_VULN),
	PET(40, null);
	
	public static final WeaponType[] VALUES = values();
	
	private final int _mask;
	
	private final int _range;
	private final Stats _vulnStat;
	
	private WeaponType(int range, Stats stat)
	{
		_mask = 1 << ordinal();
		
		_range = range;
		_vulnStat = stat;
	}
	
	@Override
	public int mask()
	{
		return _mask;
	}
	
	public int getRange()
	{
		return _range;
	}
	
	public Stats getVulnStat()
	{
		return _vulnStat;
	}
}