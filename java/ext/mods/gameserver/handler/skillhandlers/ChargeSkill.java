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

import ext.mods.commons.util.ArraysUtil;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.ShieldDefense;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.effects.EffectFear;

/**
 * @author Dhousefe
 * Handler unificado para Gladiadores (SONIC) e Tyrants (FORCE).
 * Gerencia tanto buffs de carga quanto ataques físicos que geram cargas.
 */
public class ChargeSkill implements ISkillHandler
{
    private static final SkillType[] SKILL_IDS =
    {
        SkillType.SONIC,
        SkillType.FORCE
    };
    
    private static final int SONIC_FOCUS = 8;
    private static final int FOCUSED_FORCE = 50;
    
    @Override
    public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
    {
        if (creature == null || creature.isAlikeDead() || skill == null)
            return;

        final Player player = creature.getActingPlayer();
        
        if (player != null && isChargeAccumulatingSkill(skill.getId()))
        {
            handleChargeIncrease(player, skill);
        }

        if (skill.getPower() > 0 || skill.isOffensive())
        {
            handlePhysicalDamage(creature, skill, targets);
        }
        else
        {
            handleBuffEffects(creature, skill, targets);
        }

        if (skill.hasSelfEffects())
        {
            final AbstractEffect effect = creature.getFirstEffect(skill.getId());
            if (effect != null && effect.isSelfEffect())
                effect.exit();
            
            skill.getEffectsSelf(creature);
        }
    }

    /**
     * Lógica de Acúmulo de Carga
     */
    private void handleChargeIncrease(Player player, L2Skill skill)
    {
        int maxCharges = skill.getMaxCharges();
        if (maxCharges <= 0) maxCharges = 8;

        if (player.getCharges() >= maxCharges)
        {
            player.sendPacket(SystemMessageId.FORCE_MAXLEVEL_REACHED);
        }
        else
        {
            int addCharges = Math.max(1, skill.getNumCharges());
            player.increaseCharges(addCharges, maxCharges);
        }
    }

    /**
     * Lógica de Dano Físico (Extraída do PDAM)
     * Resolve o problema de animação, shift+click e dano.
     */
    private void handlePhysicalDamage(Creature creature, L2Skill skill, WorldObject[] targets)
    {
        final boolean ss = creature.isChargedShot(ShotType.SOULSHOT);
        final ItemInstance weapon = creature.getActiveWeaponInstance();

        for (WorldObject obj : targets)
        {
            if (!(obj instanceof Creature target) || target.isDead())
                continue;

            if (target instanceof Playable && ArraysUtil.contains(EffectFear.DOESNT_AFFECT_PLAYABLE, skill.getId()))
                continue;

            if (weapon != null && weapon.getItemType() != WeaponType.BOW && Formulas.calcPhysicalSkillEvasion(target, skill))
            {
                if (creature instanceof Player player)
                    player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DODGES_ATTACK).addCharName(target));
                continue;
            }

            final boolean isCrit = skill.getBaseCritRate() > 0 && Formulas.calcCrit(skill.getBaseCritRate() * 10 * Formulas.getSTRBonus(creature));
            final ShieldDefense sDef = Formulas.calcShldUse(creature, target, skill, isCrit);
            final byte reflect = Formulas.calcSkillReflect(target, skill);

            if (skill.hasEffects() && target.getFirstEffect(EffectType.BLOCK_DEBUFF) == null)
            {
                if ((reflect & Formulas.SKILL_REFLECT_SUCCEED) != 0)
                {
                    creature.stopSkillEffects(skill.getId());
                    skill.getEffects(target, creature);
                }
                else
                {
                    target.stopSkillEffects(skill.getId());
                    skill.getEffects(creature, target, sDef, false);
                }
            }

            double damage = Formulas.calcPhysicalSkillDamage(creature, target, skill, sDef, isCrit, ss);

            if (damage > 0)
            {
                Formulas.calcCastBreak(target, damage);
                target.reduceCurrentHp(damage, creature, skill);
                creature.sendDamageMessage(target, (int) damage, false, isCrit, false);
                Formulas.calcLethalHit(creature, target, skill);
            }
            else
            {
                creature.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
            }
        }
        creature.setChargedShot(ShotType.SOULSHOT, skill.isStaticReuse());
    }

    /**
     * Lógica de Buffs (Sonic Focus)
     */
    private void handleBuffEffects(Creature creature, L2Skill skill, WorldObject[] targets)
    {
        if (targets == null || targets.length == 0)
        {
            if (skill.getTargetType().toString().equals("SELF"))
                skill.getEffects(creature, creature);
            return;
        }

        for (WorldObject obj : targets)
        {
            if (obj instanceof Creature target && !target.isDead())
                skill.getEffects(creature, target);
        }
    }

    private boolean isChargeAccumulatingSkill(int skillId)
    {
        return skillId == SONIC_FOCUS || skillId == FOCUSED_FORCE;
    }

    @Override
    public SkillType[] getSkillIds()
    {
        return SKILL_IDS;
    }
}