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

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.ShieldDefense;
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.cancelmanager.CancelReturnManager;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.SiegeSummon;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;

public class Disablers implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.STUN,
		SkillType.ROOT,
		SkillType.SLEEP,
		SkillType.CONFUSION,
		SkillType.AGGDAMAGE,
		SkillType.AGGREDUCE,
		SkillType.AGGREDUCE_CHAR,
		SkillType.AGGREMOVE,
		SkillType.MUTE,
		SkillType.FAKE_DEATH,
		SkillType.NEGATE,
		SkillType.CANCEL_DEBUFF,
		SkillType.PARALYZE,
		SkillType.ERASE,
		SkillType.BETRAY
	};
	
	@Override
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
	{
		final SkillType type = skill.getSkillType();
		
		final boolean bsps = creature.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (WorldObject obj : targets)
		{
			if (!(obj instanceof Creature targetCreature))
				continue;
			
			if (targetCreature.isDead() || (targetCreature.isInvul() && !targetCreature.isParalyzed()))
				continue;
			
			if (skill.isOffensive() && targetCreature.getFirstEffect(EffectType.BLOCK_DEBUFF) != null)
				continue;
			
			final ShieldDefense sDef = Formulas.calcShldUse(creature, targetCreature, skill, false);
			
			switch (type)
			{
				case BETRAY:
					if (Formulas.calcSkillSuccess(creature, targetCreature, skill, sDef, bsps))
						skill.getEffects(creature, targetCreature, sDef, bsps);
					else if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(skill));
					break;
				
				case FAKE_DEATH:
					skill.getEffects(creature, targetCreature, sDef, bsps);
					break;
				
				case ROOT, STUN, SLEEP, PARALYZE:
					if (Formulas.calcSkillReflect(targetCreature, skill) == Formulas.SKILL_REFLECT_SUCCEED)
						targetCreature = creature;
					
					if (Formulas.calcSkillSuccess(creature, targetCreature, skill, sDef, bsps))
						skill.getEffects(creature, targetCreature, sDef, bsps);
					else if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(skill.getId()));
					break;
				
				case MUTE:
					if (Formulas.calcSkillReflect(targetCreature, skill) == Formulas.SKILL_REFLECT_SUCCEED)
						targetCreature = creature;
					
					if (Formulas.calcSkillSuccess(creature, targetCreature, skill, sDef, bsps))
					{
						for (AbstractEffect effect : targetCreature.getAllEffects())
						{
							if (effect.getTemplate().getStackOrder() == 99)
								continue;
							
							if (effect.getSkill().getSkillType() == type)
								effect.exit();
						}
						skill.getEffects(creature, targetCreature, sDef, bsps);
					}
					else if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(skill.getId()));
					break;
				
				case CONFUSION:
					if (targetCreature instanceof Attackable targetAttackable)
					{
						if (Formulas.calcSkillSuccess(creature, targetAttackable, skill, sDef, bsps))
						{
							for (AbstractEffect effect : targetAttackable.getAllEffects())
							{
								if (effect.getTemplate().getStackOrder() == 99)
									continue;
								
								if (effect.getSkill().getSkillType() == type)
									effect.exit();
							}
							skill.getEffects(creature, targetAttackable, sDef, bsps);
						}
						else if (creature instanceof Player player)
							player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetAttackable).addSkillName(skill));
					}
					else if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INVALID_TARGET));
					break;
				
				case AGGDAMAGE:
					if ((targetCreature instanceof Player player) && (Rnd.get(100) < 75))
					{
						if (player.getPvpFlag() != 0 || player.isInOlympiadMode() || player.isInCombat() || player.isInsideZone(ZoneId.PVP))
						{
							player.setTarget(creature);
							player.getAttack().stop();
							player.getAI().tryToAttack(creature);
						}
					}
					
					if (targetCreature instanceof Attackable targetAttackable)
						targetAttackable.getAI().notifyEvent(AiEventType.AGGRESSION, creature, (int) (skill.getPower() / (targetAttackable.getStatus().getLevel() + 7) * 150));
					
					skill.getEffects(creature, targetCreature, sDef, bsps);
					break;
				
				case AGGREDUCE:
					if (targetCreature instanceof Attackable targetAttackable)
					{
						skill.getEffects(creature, targetAttackable, sDef, bsps);
						
						if (skill.getPower() > 0)
							targetAttackable.getAI().getAggroList().reduceAllHate((int) skill.getPower());
						else
						{
							final double hate = targetAttackable.getAI().getAggroList().getHate(creature);
							final double diff = hate - targetAttackable.getStatus().calcStat(Stats.AGGRESSION, hate, targetAttackable, skill);
							if (diff > 0)
								targetAttackable.getAI().getAggroList().reduceAllHate((int) diff);
						}
					}
					break;
				
				case AGGREDUCE_CHAR:
					if (Formulas.calcSkillSuccess(creature, targetCreature, skill, sDef, bsps))
					{
						if (targetCreature instanceof Attackable targetAttackable)
						{
							targetAttackable.getAI().getAggroList().stopHate(creature);
							targetAttackable.getAI().getHateList().stopHate(creature);
						}
						
						skill.getEffects(creature, targetCreature, sDef, bsps);
					}
					else if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(skill));
					break;
				
				case AGGREMOVE:
					if (targetCreature instanceof Attackable targetAttackable && !targetCreature.isRaidRelated())
					{
						if (Formulas.calcSkillSuccess(creature, targetCreature, skill, sDef, bsps))
						{
							if (skill.getTargetType() == SkillTargetType.UNDEAD)
							{
								if (targetCreature.isUndead())
								{
									targetAttackable.getAI().getAggroList().cleanAllHate();
									targetAttackable.getAI().getHateList().cleanAllHate();
								}
							}
							else
							{
								targetAttackable.getAI().getAggroList().cleanAllHate();
								targetAttackable.getAI().getHateList().cleanAllHate();
							}
						}
						else if (creature instanceof Player player)
							player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(skill));
					}
					break;
				
				case ERASE:
					if (Formulas.calcSkillSuccess(creature, targetCreature, skill, sDef, bsps))
					{
						if (targetCreature instanceof Summon targetSummon && !(targetCreature instanceof SiegeSummon))
						{
							final Player summonOwner = targetCreature.getActingPlayer();
							if (summonOwner != null)
							{
								targetSummon.unSummon(summonOwner);
								
								summonOwner.sendPacket(SystemMessageId.YOUR_SERVITOR_HAS_VANISHED);
							}
						}
					}
					else if (creature instanceof Player player)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_RESISTED_YOUR_S2).addCharName(targetCreature).addSkillName(skill));
					break;
				
				case CANCEL_DEBUFF:
					final AbstractEffect[] effects = targetCreature.getAllEffects();
					if (effects == null || effects.length == 0)
						break;
					
					int count = (skill.getMaxNegatedEffects() > 0) ? 0 : -2;
					for (AbstractEffect effect : effects)
					{
						if (!effect.getSkill().isDebuff() || !effect.getSkill().canBeDispeled() || effect.getTemplate().getStackOrder() == 99)
							continue;
						
						effect.exit();
						
						if (count > -1)
						{
							count++;
							if (count >= skill.getMaxNegatedEffects())
								break;
						}
					}
					break;
				
				case NEGATE:
					if (Formulas.calcSkillReflect(targetCreature, skill) == Formulas.SKILL_REFLECT_SUCCEED)
						targetCreature = creature;
					
					final java.util.List<AbstractEffect> removedEffects = new java.util.ArrayList<>();
					
					if (skill.getNegateId().length != 0)
					{
						for (int id : skill.getNegateId())
						{
							if (id != 0)
							{
								final AbstractEffect effect = targetCreature.getFirstEffect(id);
								if (effect != null)
									removedEffects.add(effect);
								targetCreature.stopSkillEffects(id);
							}
						}
					}
					else
					{
						for (AbstractEffect effect : targetCreature.getAllEffects())
						{
							if (effect.getTemplate().getStackOrder() == 99)
								continue;
							
							final L2Skill effectSkill = effect.getSkill();
							for (SkillType skillType : skill.getNegateStats())
							{
								if (skill.getNegateLvl() == -1)
								{
									if (effectSkill.getSkillType() == skillType || (effectSkill.getEffectType() != null && effectSkill.getEffectType() == skillType))
									{
										removedEffects.add(effect);
										effect.exit();
									}
								}
								else
								{
									if (effectSkill.getEffectType() != null && effectSkill.getEffectAbnormalLvl() >= 0)
									{
										if (effectSkill.getEffectType() == skillType && effectSkill.getEffectAbnormalLvl() <= skill.getNegateLvl())
										{
											removedEffects.add(effect);
											effect.exit();
										}
									}
									else if (effectSkill.getSkillType() == skillType && effectSkill.getAbnormalLvl() <= skill.getNegateLvl())
									{
										removedEffects.add(effect);
										effect.exit();
									}
								}
							}
						}
					}
					
					if (!removedEffects.isEmpty())
					{
						CancelReturnManager.onNegate(targetCreature, skill, removedEffects);
					}
					
					skill.getEffects(creature, targetCreature, sDef, bsps);
					break;
			}
		}
		
		if (skill.hasSelfEffects())
		{
			final AbstractEffect effect = creature.getFirstEffect(skill.getId());
			if (effect != null && effect.isSelfEffect())
				effect.exit();
			
			skill.getEffectsSelf(creature);
		}
		creature.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, skill.isStaticReuse());
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}