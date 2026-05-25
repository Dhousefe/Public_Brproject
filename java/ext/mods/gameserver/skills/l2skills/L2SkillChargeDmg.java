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
package ext.mods.gameserver.skills.l2skills;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.skills.ShieldDefense;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;

public class L2SkillChargeDmg extends L2Skill
{
	public L2SkillChargeDmg(StatSet set)
	{
		super(set);
	}
	
	@Override
	public void useSkill(Creature creature, WorldObject[] targets)
	{
		if (creature.isAlikeDead())
			return;
		
		double modifier = 0;
		
		if (creature instanceof Player player)
			modifier = 0.8 + 0.2 * (player.getCharges() + getNumCharges());
		
		final boolean ss = creature.isChargedShot(ShotType.SOULSHOT);
		
		for (WorldObject target : targets)
		{
			if (!(target instanceof Creature targetCreature))
				continue;
			
			if (targetCreature.isAlikeDead())
				continue;
			
			boolean skillIsEvaded = Formulas.calcPhysicalSkillEvasion(targetCreature, this);
			if (skillIsEvaded)
			{
				if (creature instanceof Player player)
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(targetCreature));
				
				if (target instanceof Player targetPlayer)
					targetPlayer.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AVOIDED_S1_ATTACK).addCharName(creature));
				
				continue;
			}
			
			final boolean isCrit = getBaseCritRate() > 0 && Formulas.calcCrit(getBaseCritRate() * 10 * Formulas.getSTRBonus(creature));
			final ShieldDefense sDef = Formulas.calcShldUse(creature, targetCreature, this, isCrit);
			final byte reflect = Formulas.calcSkillReflect(targetCreature, this);
			
			if (hasEffects())
			{
				if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0)
				{
					creature.stopSkillEffects(getId());
					
					getEffects(targetCreature, creature);
				}
				else
				{
					targetCreature.stopSkillEffects(getId());
					
					if (Formulas.calcSkillSuccess(creature, targetCreature, this, sDef, true))
						getEffects(creature, targetCreature, sDef, false);
					else
						creature.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(this));
				}
			}
			
			double damage = Formulas.calcPhysicalSkillDamage(creature, targetCreature, this, sDef, isCrit, ss);
			damage *= modifier;
			
			if ((reflect & Formulas.SKILL_COUNTER) != 0)
			{
				if (target instanceof Player targetPlayer)
					targetPlayer.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.COUNTERED_S1_ATTACK).addCharName(creature));
				
				if (creature instanceof Player player)
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PERFORMING_COUNTERATTACK).addCharName(targetCreature));
				
				final double counteredPercent = targetCreature.getStatus().calcStat(Stats.COUNTER_SKILL_PHYSICAL, 0, targetCreature, null) / 100.;
				
				damage *= counteredPercent;
				
				creature.reduceCurrentHp(damage, targetCreature, this);
				
				targetCreature.sendDamageMessage(creature, (int) damage, false, false, false);
			}
			else
			{
				Formulas.calcCastBreak(targetCreature, damage);
				
				targetCreature.reduceCurrentHp(damage, creature, this);
				
				creature.sendDamageMessage(targetCreature, (int) damage, false, false, false);
			}
		}
		
		if (hasSelfEffects())
		{
			final AbstractEffect effect = creature.getFirstEffect(getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			getEffectsSelf(creature);
		}
		
		creature.setChargedShot(ShotType.SOULSHOT, isStaticReuse());
	}
}