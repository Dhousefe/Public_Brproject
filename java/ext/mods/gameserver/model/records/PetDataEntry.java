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

public record PetDataEntry(long maxExp, int maxMeal, int expType, int mealInBattle, int mealInNormal, double pAtk, double pDef, double mAtk, double mDef, double maxHp, double maxMp, float hpRegen, float mpRegen, int ssCount, int spsCount, int mountMealInBattle, int mountMealInNormal, double mountAtkSpd, double mountPatk, double mountMatk, int mountBaseSpeed, int mountWaterSpeed, int mountFlySpeed)
{
	public PetDataEntry(StatSet set)
	{
		this(set.getLong("exp"), set.getInteger("maxMeal"), set.getInteger("expType"), set.getInteger("mealInBattle"), set.getInteger("mealInNormal"), set.getDouble("pAtk"), set.getDouble("pDef"), set.getDouble("mAtk"), set.getDouble("mDef"), set.getDouble("hp"), set.getDouble("mp"), set.getFloat("hpRegen"), set.getFloat("mpRegen"), set.getInteger("ssCount"), set.getInteger("spsCount"), set.getInteger("mealInBattleOnRide", 0), set.getInteger("mealInNormalOnRide", 0), set.getDouble("atkSpdOnRide", 0.), set.getDouble("pAtkOnRide", 0.), set.getDouble("mAtkOnRide", 0.), set.getInteger("mountBaseSpeed"), set.getInteger("mountWaterSpeed"), set.getInteger("mountFlySpeed"));
	}
}