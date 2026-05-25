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
package ext.mods.gameserver.skills;

import java.util.Set;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.skills.TriggerType;

public final class ChanceCondition
{
	private final TriggerType _triggerType;
	private final int _chance;
	
	private ChanceCondition(TriggerType trigger, int chance)
	{
		_triggerType = trigger;
		_chance = chance;
	}
	
	@Override
	public String toString()
	{
		return "ChanceCondition[" + _chance + ";" + _triggerType + "]";
	}
	
	public static ChanceCondition parse(StatSet set)
	{
		final TriggerType trigger = set.getEnum("chanceType", TriggerType.class, null);
		if (trigger == null)
			return null;
		
		final int chance = set.getInteger("activationChance", -1);
		
		return new ChanceCondition(trigger, chance);
	}
	
	public static ChanceCondition parse(String chanceType, int chance)
	{
		if (chanceType == null)
			return null;
		
		final TriggerType trigger = Enum.valueOf(TriggerType.class, chanceType);
		if (trigger == null)
			return null;
		
		return new ChanceCondition(trigger, chance);
	}
	
	public boolean trigger(Set<TriggerType> triggers)
	{
		return triggers.contains(_triggerType) && (_chance < 0 || Rnd.get(100) < _chance);
	}
}