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

import ext.mods.commons.math.MathUtil;
import ext.mods.commons.util.ArraysUtil;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.FlyType;
import ext.mods.gameserver.enums.skills.ShieldDefense;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.FlyToLocation;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.ValidateLocation;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.effects.EffectFear;


public class Pdam implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.PDAM,
		SkillType.FATAL
	};

	private static final int SONIC_RAGE = 345;
    private static final int RAGING_FORCE = 346; 
	
	@Override
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
	{
		if (creature.isAlikeDead())
			return;

		final Player playeractor = creature.getActingPlayer();
		final boolean ss = creature.isChargedShot(ShotType.SOULSHOT);
		final ItemInstance weapon = creature.getActiveWeaponInstance();

		if (playeractor != null && (skill.getId() == SONIC_RAGE || skill.getId() == RAGING_FORCE))
			{
				int maxCharges = skill.getMaxCharges();
				if (maxCharges <= 0) maxCharges = 8;

				if (playeractor.getCharges() < maxCharges)
				{
					playeractor.increaseCharges(1, maxCharges);
				}
				else
				{
					playeractor.sendPacket(SystemMessageId.FORCE_MAXLEVEL_REACHED);
				}
			}
		
		for (WorldObject target : targets)
		{
			if (!(target instanceof Creature targetCreature))
				continue;
			
			if (targetCreature.isDead())
				continue;
			
			if (target instanceof Playable && ArraysUtil.contains(EffectFear.DOESNT_AFFECT_PLAYABLE, skill.getId()))
				continue;
			
			if (weapon != null && weapon.getItemType() != WeaponType.BOW && Formulas.calcPhysicalSkillEvasion(targetCreature, skill))
			{
				if (creature instanceof Player player)
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(targetCreature));
				
				if (target instanceof Player targetPlayer)
					targetPlayer.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(creature));
				
				continue;
			}
			
			final boolean isCrit = skill.getBaseCritRate() > 0 && Formulas.calcCrit(skill.getBaseCritRate() * 10 * Formulas.getSTRBonus(creature));
			final ShieldDefense sDef = Formulas.calcShldUse(creature, targetCreature, skill, isCrit);
			final byte reflect = Formulas.calcSkillReflect(targetCreature, skill);
			
			if (skill.hasEffects() && targetCreature.getFirstEffect(EffectType.BLOCK_DEBUFF) == null)
			{
				if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0)
				{
					creature.stopSkillEffects(skill.getId());
					
					skill.getEffects(targetCreature, creature);
				}
				else
				{
					targetCreature.stopSkillEffects(skill.getId());
					
					skill.getEffects(creature, targetCreature, sDef, false);
				}
			}
			
			double damage = Formulas.calcPhysicalSkillDamage(creature, targetCreature, skill, sDef, isCrit, ss);
			
			if (damage > 0)
			{
				if ((reflect & Formulas.SKILL_COUNTER) != 0)
				{
					if (target instanceof Player targetPlayer)
						targetPlayer.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_S1_ATTACK).addCharName(creature));
					
					if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PERFORMING_COUNTERATTACK).addCharName(targetCreature));
					
					damage *= targetCreature.getStatus().calcStat(Stats.COUNTER_SKILL_PHYSICAL, 0, targetCreature, null) / 100.;
					
					creature.reduceCurrentHp(damage, targetCreature, skill);
					
					targetCreature.sendDamageMessage(creature, (int) damage, false, false, false);
				}
				else
				{
					Formulas.calcCastBreak(targetCreature, damage);
					
					targetCreature.reduceCurrentHp(damage, creature, skill);
					
					creature.sendDamageMessage(targetCreature, (int) damage, false, false, false);
				}
				
				Formulas.calcLethalHit(creature, targetCreature, skill);
			}
			else
				creature.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = creature.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(creature);
		}
		
		if (skill.getFlyType() == FlyType.CHARGE)
		{
			int heading = creature.getHeading();
			if (targets.length > 0)
				heading = MathUtil.calculateHeadingFrom(creature.getX(), creature.getY(), targets[0].getX(), targets[0].getY());
			
			final Point2D chargePoint = MathUtil.getNewLocationByDistanceAndHeading(creature.getX(), creature.getY(), heading, skill.getFlyRadius());
			
			final Location chargeLoc = GeoEngine.getInstance().getValidLocation(creature, chargePoint.getX(), chargePoint.getY(), creature.getZ());
			
			creature.broadcastPacket(new FlyToLocation(creature, chargeLoc.getX(), chargeLoc.getY(), chargeLoc.getZ(), FlyType.CHARGE));
			
			creature.setXYZ(chargeLoc.getX(), chargeLoc.getY(), chargeLoc.getZ());
			
			creature.broadcastPacket(new ValidateLocation(creature));
		}
		
		if (skill.isSuicideAttack())
			creature.doDie(creature);
		
		creature.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}