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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;

public class EffectCancel extends AbstractEffect
{
	public EffectCancel(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.CANCEL;
	}
	
	@Override
	public boolean onStart()
	{
		if (getEffected().isDead())
			return false;
		
		final int cancelLvl = getSkill().getMagicLevel();
		int count = getSkill().getMaxNegatedEffects();
		
		double rate = getTemplate().getEffectPower();
		
		final double res = Formulas.calcSkillVulnerability(getEffector(), getEffected(), getSkill(), getTemplate().getEffectType());
		rate *= res;
		
		final List<AbstractEffect> list = Arrays.asList(getEffected().getAllEffects());
		Collections.shuffle(list);
		
		for (AbstractEffect effect : list)
		{
			if (effect.getSkill().isToggle() || effect.getSkill().isDebuff())
				continue;
			
			if (EffectType.isntCancellable(getEffectType()))
				continue;
			
			if (calcCancelSuccess(effect, cancelLvl, (int) rate))
				effect.exit();
			
			count--;
			
			if (count == 0)
				break;
		}
		return true;
	}
	
	@Override
	public boolean onActionTime()
	{
		return false;
	}
	
	private static boolean calcCancelSuccess(AbstractEffect effect, int cancelLvl, int baseRate)
	{
		int rate = 2 * (cancelLvl - effect.getSkill().getMagicLevel());
		rate += effect.getPeriod() / 120;
		rate += baseRate;
		
		return Rnd.get(100) < Math.clamp(rate, 25, 75);
	}
}