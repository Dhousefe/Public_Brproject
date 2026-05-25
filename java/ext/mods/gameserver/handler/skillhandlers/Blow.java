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

import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.skills.ShieldDefense;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;

public class Blow implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.BLOW
	};
	
	@Override
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
	{
		if (creature.isAlikeDead())
			return;
		
		final boolean ss = creature.isChargedShot(ShotType.SOULSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature targetCreature))
				continue;
			
			if (targetCreature.isAlikeDead())
				continue;
			
			if (Formulas.calcBlowRate(creature, targetCreature, skill))
			{
				if (Formulas.calcPhysicalSkillEvasion(targetCreature, skill))
				{
					if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(targetCreature));
					
					if (targetCreature instanceof Player targetPlayer)
						targetPlayer.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(creature));
					
					continue;
				}
				
				final boolean isCrit = skill.getBaseCritRate() > 0 && Formulas.calcCrit(skill.getBaseCritRate() * 10 * Formulas.getSTRBonus(creature));
				final ShieldDefense sDef = Formulas.calcShldUse(creature, targetCreature, skill, isCrit);
				final byte reflect = Formulas.calcSkillReflect(targetCreature, skill);
				
				if (skill.hasEffects())
				{
					if (reflect == Formulas.SKILL_REFLECT_SUCCEED)
					{
						creature.stopSkillEffects(skill.getId());
						
						skill.getEffects(targetCreature, creature);
					}
					else
					{
						targetCreature.stopSkillEffects(skill.getId());
						
						if (Formulas.calcSkillSuccess(creature, targetCreature, skill, sDef, true))
							skill.getEffects(creature, targetCreature, sDef, false);
						else
							creature.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(skill));
					}
				}
				
				double damage = (int) Formulas.calcBlowDamage(creature, targetCreature, skill, sDef, ss);
				creature.sendPacket(new PlaySound("skillsound.critical_hit_02"));
				
				if (isCrit)
					damage *= 2;
				
				if ((reflect & Formulas.SKILL_COUNTER) != 0)
				{
					if (targetCreature instanceof Player targetPlayer)
						targetPlayer.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_S1_ATTACK).addCharName(creature));
					
					if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PERFORMING_COUNTERATTACK).addCharName(targetCreature));
					
					final double counteredPercent = targetCreature.getStatus().calcStat(Stats.COUNTER_SKILL_PHYSICAL, 0, targetCreature, null) / 100.;
					
					damage *= counteredPercent;
					
					creature.reduceCurrentHp(damage, targetCreature, skill);
					
					targetCreature.sendDamageMessage(creature, (int) damage, false, true, false);
				}
				else
				{
					Formulas.calcCastBreak(targetCreature, damage);
					
					targetCreature.reduceCurrentHp(damage, creature, skill);
					
					creature.sendDamageMessage(targetCreature, (int) damage, false, true, false);
				}
				
				creature.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
			}
			
			Formulas.calcLethalHit(creature, targetCreature, skill);
			
			if (skill.hasSelfEffects())
			{
				final AbstractEffect effect = creature.getFirstEffect(skill.getId());
				if (effect != null && effect.isSelfEffect())
					effect.exit();
				
				skill.getEffectsSelf(creature);
			}
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}