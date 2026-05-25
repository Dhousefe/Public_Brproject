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
package ext.mods.gameserver.model.records;

import ext.mods.commons.data.StatSet;

/**
 * This record stores Soul Crystal leveling infos related to items.
 * @param level : The current level on the hierarchy tree of items.
 * @param initialItemId : The initial itemId from where we start.
 * @param stagedItemId : The succeeded itemId rewarded if absorb was successful.
 * @param brokenItemId : The broken itemId rewarded if absorb failed.
 */
public record SoulCrystal(int level, int initialItemId, int stagedItemId, int brokenItemId)
{
	public SoulCrystal(StatSet set)
	{
		this(set.getInteger("level"), set.getInteger("initial"), set.getInteger("staged"), set.getInteger("broken"));
	}
}