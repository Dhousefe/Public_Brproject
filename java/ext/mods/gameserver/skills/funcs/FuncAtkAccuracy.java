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
package ext.mods.gameserver.skills.funcs;

import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncAtkAccuracy extends Func
{
	private static final FuncAtkAccuracy INSTANCE = new FuncAtkAccuracy();
	
	private FuncAtkAccuracy()
	{
		super(null, Stats.ACCURACY_COMBAT, 10, 0, null);
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		final int level = effector.getStatus().getLevel();
		
		value += Formulas.BASE_EVASION_ACCURACY[effector.getStatus().getDEX()] + level;
		if (effector instanceof Summon)
			value += (level < 60) ? 4 : 5;
		
		return value;
	}
	
	public static Func getInstance()
	{
		return INSTANCE;
	}
}