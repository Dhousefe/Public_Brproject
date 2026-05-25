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

import ext.mods.gameserver.enums.items.AbsorbCrystalType;

/**
 * This class stores Soul Crystal leveling infos related to NPCs.
 * @param absorbCrystalType : The AbsorbCrystalType which can be LAST_HIT, FULL_PARTY or PARTY_ONE_RANDOM.
 * @param isSkillRequired : If the item cast on monster is required or not.
 * @param chanceStage : The chance of success (base 1000).
 * @param chanceBreak : The chance of break (base 1000).
 * @param levelList : The list of allowed crystals levels.
 */
public record LevelingInfo(AbsorbCrystalType absorbCrystalType, boolean isSkillRequired, int chanceStage, int chanceBreak, int[] levelList)
{
	public LevelingInfo(StatSet set)
	{
		this(set.getEnum("absorbType", AbsorbCrystalType.class), set.getBool("skill"), set.getInteger("chanceStage"), set.getInteger("chanceBreak"), set.getIntegerArray("levelList"));
	}
}