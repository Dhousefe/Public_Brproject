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
package ext.mods.gameserver.skills.basefuncs;

import java.lang.reflect.Constructor;

import ext.mods.commons.logging.CLogger;

import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.conditions.Condition;

public final class FuncTemplate
{
	private static final CLogger LOGGER = new CLogger(FuncTemplate.class.getName());
	
	private final Condition _attachCond;
	private final Condition _applyCond;
	private final Constructor<?> _constructor;
	private final Stats _stat;
	private final double _value;
	
	public FuncTemplate(Condition attachCond, Condition applyCond, String function, Stats stat, double value)
	{
		_attachCond = attachCond;
		_applyCond = applyCond;
		_stat = stat;
		_value = value;
		
		try
		{
			final Class<?> functionClass = Class.forName("ext.mods.gameserver.skills.basefuncs.Func" + function);
			_constructor = functionClass.getConstructor(Object.class, Stats.class, Double.TYPE, Condition.class);
		}
		catch (ClassNotFoundException | NoSuchMethodException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets the functions for skills.
	 * @param caster the caster
	 * @param target the target
	 * @param skill the skill
	 * @param owner the owner
	 * @return the function if conditions are met, {@code null} otherwise
	 */
	public Func getFunc(Creature caster, Creature target, L2Skill skill, Object owner)
	{
		return getFunc(caster, target, skill, null, owner);
	}
	
	/**
	 * Gets the functions for items.
	 * @param caster the caster
	 * @param target the target
	 * @param item the item
	 * @param owner the owner
	 * @return the function if conditions are met, {@code null} otherwise
	 */
	public Func getFunc(Creature caster, Creature target, ItemInstance item, Object owner)
	{
		return getFunc(caster, target, null, item, owner);
	}
	
	/**
	 * Gets the functions for skills and items.
	 * @param caster the caster
	 * @param target the target
	 * @param skill the skill
	 * @param item the item
	 * @param owner the owner
	 * @return the function if conditions are met, {@code null} otherwise
	 */
	private Func getFunc(Creature caster, Creature target, L2Skill skill, ItemInstance item, Object owner)
	{
		if (_attachCond != null && !_attachCond.test(caster, target, skill))
			return null;
		
		try
		{
			return (Func) _constructor.newInstance(owner, _stat, _value, _applyCond);
		}
		catch (Exception e)
		{
			LOGGER.error("An error occured during getFunc.", e);
		}
		return null;
	}
}