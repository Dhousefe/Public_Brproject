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
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;

public class L2SkillDrain extends L2Skill
{
	private final float _absorbPart;
	private final int _absorbAbs;
	
	public L2SkillDrain(StatSet set)
	{
		super(set);
		
		_absorbPart = set.getFloat("absorbPart", 0.f);
		_absorbAbs = set.getInteger("absorbAbs", 0);
	}
	
	@Override
	public void useSkill(Creature creature, WorldObject[] targets)
	{
		if (creature.isAlikeDead())
			return;
		
		final boolean sps = creature.isChargedShot(ShotType.SPIRITSHOT);
		final boolean bsps = creature.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		final boolean isPlayable = creature instanceof Playable;
		
		for (WorldObject target : targets)
		{
			if (!(target instanceof Creature targetCreature))
				continue;
			
			if (targetCreature.isAlikeDead() && getTargetType() != SkillTargetType.CORPSE_MOB)
				continue;
			
			if (creature != targetCreature && targetCreature.isInvul())
				continue;
			
			final boolean isCrit = Formulas.calcMCrit(creature, targetCreature, this);
			final ShieldDefense sDef = Formulas.calcShldUse(creature, targetCreature, this, false);
			final int damage = (int) Formulas.calcMagicDam(creature, targetCreature, this, sDef, sps, bsps, isCrit);
			
			if (damage > 0)
			{
				int targetCp = 0;
				if (target instanceof Player targetPlayer)
					targetCp = (int) targetPlayer.getStatus().getCp();
				
				final int targetHp = (int) targetCreature.getStatus().getHp();
				
				int drain = 0;
				if (isPlayable && targetCp > 0)
				{
					if (damage < targetCp)
						drain = 0;
					else
						drain = damage - targetCp;
				}
				else if (damage > targetHp)
					drain = targetHp;
				else
					drain = damage;
				
				creature.getStatus().addHp(_absorbAbs + _absorbPart * drain);
				
				if (!targetCreature.isDead() || getTargetType() != SkillTargetType.CORPSE_MOB)
				{
					Formulas.calcCastBreak(targetCreature, damage);
					
					creature.sendDamageMessage(targetCreature, damage, isCrit, false, false);
					
					if (hasEffects() && getTargetType() != SkillTargetType.CORPSE_MOB)
					{
						if ((Formulas.calcSkillReflect(targetCreature, this) & Formulas.SKILL_REFLECT_SUCCEED) > 0)
						{
							creature.stopSkillEffects(getId());
							getEffects(targetCreature, creature);
						}
						else
						{
							targetCreature.stopSkillEffects(getId());
							if (Formulas.calcSkillSuccess(creature, targetCreature, this, sDef, bsps))
								getEffects(creature, targetCreature);
							else
								creature.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(getId()));
						}
					}
					targetCreature.reduceCurrentHp(damage, creature, this);
				}
			}
		}
		
		if (hasSelfEffects())
		{
			final AbstractEffect effect = creature.getFirstEffect(getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			getEffectsSelf(creature);
		}
		
		creature.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, isStaticReuse());
	}
	
	public float getAbsorbPart()
	{
		return _absorbPart;
	}
	
	public int getAbsorbAbs()
	{
		return _absorbAbs;
	}
}