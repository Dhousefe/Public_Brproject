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
package ext.mods.gameserver.model.actor.container.creature;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.enums.skills.TriggerType;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.handler.SkillHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.network.serverpackets.MagicSkillLaunched;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.skills.ChanceCondition;
import ext.mods.gameserver.skills.IChanceSkillTrigger;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.effects.EffectChanceSkillTrigger;

public class ChanceSkillList extends ConcurrentHashMap<IChanceSkillTrigger, ChanceCondition>
{
	private static final long serialVersionUID = 1L;
	
	private final Creature _owner;
	
	public ChanceSkillList(Creature owner)
	{
		super();
		
		_owner = owner;
	}
	
	public Creature getOwner()
	{
		return _owner;
	}
	
	public void onTargetHit(Creature target, boolean isCrit)
	{
		final EnumSet<TriggerType> triggers = EnumSet.noneOf(TriggerType.class);
		
		triggers.add(TriggerType.ON_HIT);
		
		if (isCrit)
			triggers.add(TriggerType.ON_CRIT);
		
		onChanceSkillEvent(triggers, target);
	}
	
	public void onSelfHit(Creature target)
	{
		final EnumSet<TriggerType> triggers = EnumSet.noneOf(TriggerType.class);
		
		triggers.add(TriggerType.ON_ATTACKED);
		triggers.add(TriggerType.ON_ATTACKED_HIT);
		
		onChanceSkillEvent(triggers, target);
	}
	
	public void onSkillTargetHit(Creature target, L2Skill skill)
	{
		final EnumSet<TriggerType> triggers = EnumSet.noneOf(TriggerType.class);
		
		if (skill.isDamage())
			triggers.add(TriggerType.ON_MAGIC_OFFENSIVE);
		else if (!skill.isOffensive())
			triggers.add(TriggerType.ON_MAGIC_GOOD);
		
		onChanceSkillEvent(triggers, target);
	}
	
	public void onSkillSelfHit(Creature target, L2Skill skill)
	{
		final EnumSet<TriggerType> triggers = EnumSet.noneOf(TriggerType.class);
		if (skill.isDamage())
			triggers.add(TriggerType.ON_ATTACKED);
		
		onChanceSkillEvent(triggers, target);
	}
	
	public void onChanceSkillEvent(Set<TriggerType> triggers, Creature target)
	{
		if (_owner.isDead())
			return;
		
		for (Map.Entry<IChanceSkillTrigger, ChanceCondition> entry : entrySet())
		{
			final ChanceCondition cond = entry.getValue();
			if (cond != null && cond.trigger(triggers))
			{
				final IChanceSkillTrigger trigger = entry.getKey();
				if (trigger instanceof L2Skill skill)
					makeCast(skill, target);
				else if (trigger instanceof EffectChanceSkillTrigger ecst)
					makeCast(ecst, target);
			}
		}
	}
	
	private void makeCast(L2Skill skill, Creature target)
	{
		if (skill.getWeaponDependancy(_owner) && skill.checkCondition(_owner, target, false))
		{
			if (skill.triggersChanceSkill())
			{
				skill = SkillTable.getInstance().getInfo(skill.getTriggeredChanceId(), skill.getTriggeredChanceLevel());
				if (skill == null || skill.getSkillType() == SkillType.NOTDONE)
					return;
			}
			
			if (_owner.isSkillDisabled(skill))
				return;
			
			if (skill.getReuseDelay() > 0)
				_owner.disableSkill(skill, skill.getReuseDelay());
			
			final Creature[] targets = skill.getTargetList(_owner, target);
			if (targets.length == 0)
				return;
			
			final Creature firstTarget = targets[0];
			
			_owner.broadcastPacket(new MagicSkillLaunched(_owner, skill, targets));
			_owner.broadcastPacket(new MagicSkillUse(_owner, firstTarget, skill.getId(), skill.getLevel(), 0, 0));
			
			final ISkillHandler handler = SkillHandler.getInstance().getHandler(skill.getSkillType());
			if (handler != null)
				handler.useSkill(_owner, skill, targets, null);
			else
				skill.useSkill(_owner, targets);
		}
	}
	
	private void makeCast(EffectChanceSkillTrigger effect, Creature target)
	{
		if (effect == null || !effect.triggersChanceSkill())
			return;
		
		final L2Skill triggered = SkillTable.getInstance().getInfo(effect.getTriggeredChanceId(), effect.getTriggeredChanceLevel());
		if (triggered == null)
			return;
		
		final Creature caster = triggered.getTargetType() == SkillTargetType.SELF ? _owner : effect.getEffector();
		
		if (caster == null || triggered.getSkillType() == SkillType.NOTDONE || caster.isSkillDisabled(triggered))
			return;
		
		if (triggered.getReuseDelay() > 0)
			caster.disableSkill(triggered, triggered.getReuseDelay());
		
		final Creature[] targets = triggered.getTargetList(_owner, target);
		if (targets.length == 0)
			return;
		
		final Creature firstTarget = targets[0];
		final ISkillHandler handler = SkillHandler.getInstance().getHandler(triggered.getSkillType());
		
		_owner.broadcastPacket(new MagicSkillLaunched(_owner, triggered, targets));
		_owner.broadcastPacket(new MagicSkillUse(_owner, firstTarget, triggered.getId(), triggered.getLevel(), 0, 0));
		
		if (handler != null)
			handler.useSkill(caster, triggered, targets, null);
		else
			triggered.useSkill(caster, targets);
	}
}