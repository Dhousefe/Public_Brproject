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
package ext.mods.gameserver.model.actor.cast;

import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.GaugeColor;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.model.actor.move.MovementIntegration;
import ext.mods.gameserver.handler.SkillHandler;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.MagicSkillCanceled;
import ext.mods.gameserver.network.serverpackets.MagicSkillLaunched;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.SetupGauge;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;

/**
 * Esta classe agrupa todos os dados de lançamento (cast) relacionados a uma {@link Creature}.
 * @param <T> : A {@link Creature} usada como ator.
 */
public class CreatureCast<T extends Creature>
{
    protected final T _actor;
    
    protected long _castInterruptTime;
    
    protected Creature[] _targets;
    protected Creature _target;
    protected L2Skill _skill;
    protected ItemInstance _item;
    protected int _hitTime;
    protected int _coolTime;
    
    protected ScheduledFuture<?> _castTask;
    
    protected boolean _isCastingNow;
    
    public CreatureCast(T actor)
    {
        _actor = actor;
    }
    
    public final boolean canAbortCast()
    {
        return _castInterruptTime > System.currentTimeMillis();
    }
    
    public final boolean isCastingNow()
    {
        return _isCastingNow;
    }
    
    public final L2Skill getCurrentSkill()
    {
        return _skill;
    }
    
    /**
     * Interrompe o lançamento atual se ele ainda for cancelável.
     */
    public void interrupt()
    {
        if (canAbortCast())
        {
            stop();
            _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CASTING_INTERRUPTED));
        }
    }

    /**
     * Descreve o lançamento atual para um jogador (envia o pacote de animação).
     * @param player O jogador que receberá o pacote.
     */
    public void describeCastTo(Player player)
    {
        if (_skill != null)
        {
            player.sendPacket(new MagicSkillUse(_actor, _target, _skill.getId(), _skill.getLevel(), _hitTime, _skill.getReuseDelay(), false));
        }
    }

    public void doFusionCast(L2Skill skill, Creature target) {}
    
    public void doInstantCast(L2Skill itemSkill, ItemInstance item) {}
    
    public void doToggleCast(L2Skill skill, Creature target) {}
    
    public void doCast(L2Skill skill, Creature target, ItemInstance itemInstance)
    {
        int hitTime = skill.getHitTime();
        int coolTime = skill.getCoolTime();
        if (!skill.isStaticHitTime())
        {
            hitTime = Formulas.calcAtkSpd(_actor, skill, hitTime);
            if (coolTime > 0)
                coolTime = Formulas.calcAtkSpd(_actor, skill, coolTime);
            
            if (skill.isMagic() && (_actor.isChargedShot(ShotType.SPIRITSHOT) || _actor.isChargedShot(ShotType.BLESSED_SPIRITSHOT)))
            {
                hitTime = (int) (0.70 * hitTime);
                coolTime = (int) (0.70 * coolTime);
            }
            
            if (Config.HIT_TIME)
            {
                if (skill.getHitTime() >= 500 && hitTime < 500)
                    hitTime = 500;
            }
        }
        
        int reuseDelay = skill.getReuseDelay();
        if (!skill.isStaticReuse())
        {
            reuseDelay *= _actor.getStatus().calcStat(skill.isMagic() ? Stats.MAGIC_REUSE_RATE : Stats.P_REUSE, 1, null, null);
            reuseDelay *= 333.0 / (skill.isMagic() ? _actor.getStatus().getMAtkSpd() : _actor.getStatus().getPAtkSpd());
        }

        if (_actor instanceof Npc)
        {
            reuseDelay = Math.max(3000, reuseDelay * 2);
        }
        
        final boolean skillMastery = Formulas.calcSkillMastery(_actor, skill);
        if (reuseDelay > 30000 && !skillMastery)
            _actor.addTimeStamp(skill, reuseDelay);
        
        if (reuseDelay > 10)
        {
            if (skillMastery)
            {
                reuseDelay = 100;
                if (_actor.getActingPlayer() != null)
                    _actor.getActingPlayer().sendPacket(SystemMessageId.SKILL_READY_TO_USE_AGAIN);
            }
            _actor.disableSkill(skill, reuseDelay);
        }
        
        final int initMpConsume = _actor.getStatus().getMpInitialConsume(skill);
        if (initMpConsume > 0)
            _actor.getStatus().reduceMp(initMpConsume);
        
        _actor.broadcastPacket(new MagicSkillUse(_actor, target, skill.getId(), skill.getLevel(), hitTime, reuseDelay, false));
        
        if (itemInstance == null)
            _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.USE_S1).addSkillName(skill));
        
        final long castInterruptTime = System.currentTimeMillis() + hitTime - 200;
        
        setCastTask(skill, target, itemInstance, hitTime, coolTime, castInterruptTime);
        
        if (_hitTime > 410)
        {
            if (_actor instanceof Player player)
                player.sendPacket(new SetupGauge(GaugeColor.BLUE, _hitTime));
        }
        else
            _hitTime = 0;
        
        _castTask = ThreadPool.schedule(this::onMagicLaunch, hitTime > 410 ? hitTime - 400 : 0);
    }
    
    private final void onMagicLaunch()
    {
        if (!isCastingNow())
            return;
        
        if (_target != _actor)
        {
            int escapeRange = 0;
            if (_skill.getEffectRange() > 0)
                escapeRange = _skill.getEffectRange();
            else if (_skill.getCastRange() <= 0 && _skill.getSkillRadius() > 80)
                escapeRange = _skill.getSkillRadius();
            
            if (_actor.getAI().isTargetLost(_target, _skill))
            {
                stop();
                return;
            }
            
            if (escapeRange > 0 && !_actor.isInStrictRadius(_target, escapeRange))
            {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DIST_TOO_FAR_CASTING_STOPPED));
                stop();
                return;
            }
            
            if (_skill.isOffensive())
            {
                final int skillRange = _skill.getCastRange();
                final double distance = _actor.distance2D(_target);
                
                if (skillRange >= 300 || distance > 400)
                {
                    if (!MovementIntegration.canSeeTarget(_actor, _target))
                    {
                        _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_SEE_TARGET));
                        stop();
                        return;
                    }
                }
            }
        }
        
        _targets = _skill.getTargetList(_actor, _target);
        _actor.broadcastPacket(new MagicSkillLaunched(_actor, _skill, _targets));
        _castTask = ThreadPool.schedule(this::onMagicHitTimer, _hitTime == 0 ? 0 : 400);
    }
    
    protected void onMagicHitTimer()
    {
        if (!isCastingNow())
            return;
        
        final double mpConsume = _actor.getStatus().getMpConsume(_skill);
        if (mpConsume > 0)
        {
            if (mpConsume > _actor.getStatus().getMp())
            {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_MP));
                stop();
                return;
            }
            _actor.getStatus().reduceMp(mpConsume);
        }
        
        final double hpConsume = _skill.getHpConsume();
        if (hpConsume > 0)
        {
            if (hpConsume > _actor.getStatus().getHp())
            {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_HP));
                stop();
                return;
            }
            _actor.getStatus().reduceHp(hpConsume, _actor, true);
        }
        
        callSkill(_skill, _targets, _item);
        _castTask = ThreadPool.schedule(this::onMagicFinalizer, (_hitTime == 0 || _coolTime == 0) ? 0 : _coolTime);
    }
    
    protected final void onMagicFinalizer()
    {
        if (!isCastingNow())
            return;
        
        if (_actor instanceof Playable)
            _actor.rechargeShots(_skill.useSoulShot(), _skill.useSpiritShot());
        
        if (_skill.isOffensive() && _targets.length != 0)
            _actor.getAI().startAttackStance();
        
        _isCastingNow = false;
        notifyCastFinishToAI(false);
    }
    
    public void stop()
    {
        if (isCastingNow())
        {
            _actor.broadcastPacket(new MagicSkillCanceled(_actor.getObjectId()));
            if (_castTask != null)
            {
                _castTask.cancel(false);
                _castTask = null;
            }
            notifyCastFinishToAI(true);
            _isCastingNow = false;
        }
    }
    
    protected void notifyCastFinishToAI(boolean isInterrupted)
    {
        _actor.getAI().notifyEvent(AiEventType.FINISHED_CASTING, null, null);
    }
    
    protected void setCastTask(L2Skill skill, Creature target, ItemInstance item, int hitTime, int coolTime, long castInterruptTime)
    {
        _skill = skill;
        _target = target;
        _item = item;
        _hitTime = hitTime;
        _coolTime = coolTime;
        _castInterruptTime = castInterruptTime;
        _isCastingNow = true;
    }
    
    public void callSkill(L2Skill skill, Creature[] targets, ItemInstance itemInstance)
    {
        for (final Creature target : targets)
        {
            final Weapon activeWeaponItem = _actor.getActiveWeaponItem();
            if (activeWeaponItem != null && !target.isDead())
                activeWeaponItem.castSkillOnMagic(_actor, target, skill);
        }
        
        if (skill.isOffensive())
        {
            for (final Creature target : targets)
                target.getAI().notifyEvent(AiEventType.ATTACKED, _actor, null);
        }
        
        final ISkillHandler handler = SkillHandler.getInstance().getHandler(skill.getSkillType());
        if (handler != null)
            handler.useSkill(_actor, skill, targets, itemInstance);
        else
            skill.useSkill(_actor, targets);
    }

    public boolean canAttemptCast(Creature target, L2Skill skill)
    {
        if (_actor.isSkillDisabled(skill))
            return false;
        return true;
    }

    public boolean meetsHpMpConditions(Creature target, L2Skill skill)
    {
        if (target == null || skill == null)
            return false;
        final int initialMpConsume = _actor.getStatus().getMpInitialConsume(skill);
        final int mpConsume = _actor.getStatus().getMpConsume(skill);
        if ((initialMpConsume > 0 || mpConsume > 0) && (int) _actor.getStatus().getMp() < initialMpConsume + mpConsume)
        {
            _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_MP));
            return false;
        }
        if (skill.getHpConsume() > 0 && (int) _actor.getStatus().getHp() <= skill.getHpConsume())
        {
            _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_HP));
            return false;
        }
        return true;
    }

    public boolean meetsHpMpDisabledConditions(Creature target, L2Skill skill)
    {
        if (!meetsHpMpConditions(target, skill))
            return false;
        if (!skill.isMagic2() && (skill.isMagic() && _actor.isMuted() || !skill.isMagic() && _actor.isPhysicalMuted()))
            return false;
        return true;
    }

    public boolean canCast(Creature target, L2Skill skill, boolean isCtrlPressed, int itemObjectId)
    {
        if (!meetsHpMpDisabledConditions(target, skill))
            return false;
        if (skill.isOffensive() && target != _actor && skill.getCastRange() > 0)
        {
            final int skillRange = skill.getCastRange();
            final int totalRange = (int)(skillRange + _actor.getCollisionRadius() + target.getCollisionRadius());
            if (!_actor.isInStrictRadius(target, totalRange))
            {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.TARGET_TOO_FAR));
                return false;
            }
            final double distance = _actor.distance2D(target);
            if (skillRange >= 300 || distance > 400)
            {
                if (!MovementIntegration.canCastOnTarget(_actor, target, skillRange))
                {
                    _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_SEE_TARGET));
                    return false;
                }
            }
        }
        return skill.getWeaponDependancy(_actor);
    }
}