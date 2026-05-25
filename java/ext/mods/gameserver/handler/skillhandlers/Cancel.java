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
package ext.mods.gameserver.handler.skillhandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.cancelmanager.CancelReturnManager;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;

public class Cancel implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.CANCEL,
		SkillType.MAGE_BANE,
		SkillType.WARRIOR_BANE
	};
	
	@Override
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
	{
		final int minRate = (skill.getSkillType() == SkillType.CANCEL) ? 25 : 40;
		final int maxRate = (skill.getSkillType() == SkillType.CANCEL) ? 75 : 95;
		
		final double skillPower = skill.getPower();
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature targetCreature))
				continue;
			
			if (targetCreature.isDead())
				continue;
			
			int count = skill.getMaxNegatedEffects();
			final List<AbstractEffect> removedEffects = new ArrayList<>();
			
			final int diffLevel = skill.getMagicLevel() - targetCreature.getStatus().getLevel();
			final double skillVuln = Formulas.calcSkillVulnerability(creature, targetCreature, skill, skill.getSkillType());
			
			final List<AbstractEffect> list = Arrays.asList(targetCreature.getAllEffects());
			Collections.shuffle(list);
			
			for (AbstractEffect effect : list)
			{
				if (effect.getSkill().isToggle() || effect.getSkill().isDebuff())
					continue;
				
				if (EffectType.isntCancellable(effect.getEffectType()))
					continue;
				
				switch (skill.getSkillType())
				{
					case MAGE_BANE:
						if ("casting_time_down".equalsIgnoreCase(effect.getTemplate().getStackType()))
							break;
						
						if ("ma_up".equalsIgnoreCase(effect.getTemplate().getStackType()))
							break;
						
						continue;
					
					case WARRIOR_BANE:
						if ("attack_time_down".equalsIgnoreCase(effect.getTemplate().getStackType()))
							break;
						
						if ("speed_up".equalsIgnoreCase(effect.getTemplate().getStackType()))
							break;
						
						continue;
				}
				
				if (calcCancelSuccess(effect.getPeriod(), diffLevel, skillPower, skillVuln, minRate, maxRate))
				{
					removedEffects.add(effect);
					effect.exit();
				}
				
				count--;
				
				if (count == 0)
					break;
			}
			
			if (!removedEffects.isEmpty())
			{
				CancelReturnManager.onCancel(targetCreature, skill, removedEffects);
			}
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = creature.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(creature);
		}
		creature.setChargedShot(creature.isChargedShot(ShotType.BLESSED_SPIRITSHOT) ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	private static boolean calcCancelSuccess(int effectPeriod, int diffLevel, double baseRate, double vuln, int minRate, int maxRate)
	{
		double rate = (2 * diffLevel + baseRate + effectPeriod / 120) * vuln;
		
		if (Config.DEVELOPER)
			LOGGER.info("calcCancelSuccess(): diffLevel:{}, baseRate:{}, vuln:{}, total:{}.", diffLevel, baseRate, vuln, rate);
		
		return Rnd.get(100) < Math.clamp(rate, minRate, maxRate);
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}