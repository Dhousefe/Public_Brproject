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

public enum MakerSpawnTime
{
	AGIT_BR_START("agit_battle_royal_start"),
	AGIT_FINAL_START("agit_final_start"),
	AGIT_DEF_START("agit_defend_warfare_start"),
	AGIT_ATK_START("agit_attack_warfare_start"),
	SIEGE_START("siege_warfare_start"),
	PC_SIEGE_START("pc_siege_warfare_start"),
	DOOR_OPEN("door_open");
	
	private final String _name;
	
	private MakerSpawnTime(String name)
	{
		_name = name;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public static final MakerSpawnTime[] VALUES = values();
	
	public static MakerSpawnTime getEnumByName(String name)
	{
		for (MakerSpawnTime mst : VALUES)
		{
			if (mst.getName().equalsIgnoreCase(name))
				return mst;
		}
		return null;
	}
}