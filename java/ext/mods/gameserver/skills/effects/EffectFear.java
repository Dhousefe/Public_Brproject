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
package ext.mods.gameserver.skills.effects;

import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.enums.skills.EffectFlag;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.instance.Folk;
import ext.mods.gameserver.model.actor.instance.SiegeFlag;
import ext.mods.gameserver.model.actor.instance.SiegeSummon;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

public class EffectFear extends AbstractEffect
{
	private static final int[] REDUCED_DURATION_ON_PLAYABLE =
	{
		65,
		1092,
		1169
	};
	
	public static final int[] DOESNT_AFFECT_PLAYABLE =
	{
		98,
		1272,
		1381
	};
	
	public EffectFear(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
		
		if (getEffected() instanceof Playable && ArraysUtil.contains(REDUCED_DURATION_ON_PLAYABLE, skill.getId()))
			setCount(getCount() / 2);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.FEAR;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected() instanceof Folk || getEffected() instanceof SiegeFlag || getEffected() instanceof SiegeSummon)
			return false;
		
		if (getEffected() instanceof Playable && ArraysUtil.contains(DOESNT_AFFECT_PLAYABLE, getSkill().getId()))
			return false;
		
		if (getEffected().isAfraid())
			return false;
		
		getEffected().abortAll(false);
		
		getEffected().updateAbnormalEffect();
		
		onActionTime();
		
		return true;
	}
	
	@Override
	public void onExit()
	{
		getEffected().stopEffects(EffectType.FEAR);
		
		getEffected().updateAbnormalEffect();
	}
	
	@Override
	public boolean onActionTime()
	{
		getEffected().fleeFrom(getEffector(), 2000);
		return true;
	}
	
	@Override
	public boolean onSameEffect(AbstractEffect effect)
	{
		return false;
	}
	
	@Override
	public int getEffectFlags()
	{
		return EffectFlag.FEAR.getMask();
	}
}