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
package ext.mods.gameserver.enums.actors;

import ext.mods.gameserver.model.actor.Player;

/**
 * This class defines all races that a player can choose.
 */
public enum ClassRace
{
	HUMAN(1),
	ELF(1.5),
	DARK_ELF(1.5),
	ORC(0.9),
	DWARF(0.8);
	
	public static final ClassRace[] VALUES = values();
	
	private final double _breathMultiplier;
	
	private ClassRace(double breathMultiplier)
	{
		_breathMultiplier = breathMultiplier;
	}
	
	/**
	 * @return the breath multiplier.
	 */
	public double getBreathMultiplier()
	{
		return _breathMultiplier;
	}
	
	public static final boolean isSameRace(Player player, String race)
	{
		if (player == null || race == null)
			return false;
		
		switch (race)
		{
			case "@race_human":
				return player.getRace() == HUMAN;
			
			case "@race_elf":
				return player.getRace() == ELF;
			
			case "@race_dark_elf":
				return player.getRace() == DARK_ELF;
			
			case "@race_orc":
				return player.getRace() == ORC;
			
			case "@race_dwarf":
				return player.getRace() == DWARF;
		}
		return false;
	}
}