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
package ext.mods.gameserver.model.actor.attack;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.GaugeColor;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.items.WeaponType;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.ShieldDefense;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.ai.type.NpcAI;
import ext.mods.gameserver.model.actor.container.creature.ChanceSkillList;
import ext.mods.gameserver.model.actor.move.MovementIntegration;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.Attack;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.SetupGauge;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.Formulas;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

public class CreatureAttack<T extends Creature> {
    public static final CLogger LOGGER = new CLogger(CreatureAttack.class.getName());

    protected final T _actor;
    private boolean _isAttackingNow;
    private boolean _isBowCoolingDown;
    private HitHolder[] _hitHolders;
    private WeaponType _weaponType;
    private int _afterAttackDelay;
    private boolean _isHit;
    private ScheduledFuture<?> _attackTask;

    public CreatureAttack(T actor) {
        this._actor = actor;
    }

    public boolean isAttackingNow() {
        return this._isAttackingNow;
    }

    public boolean isBowCoolingDown() {
        return this._isBowCoolingDown;
    }

    /**
     * Verifica se o ator pode iniciar um ataque físico contra o alvo.
     */
    public boolean canAttack(Creature target) {
        if (_isAttackingNow || _isBowCoolingDown) {
            return false;
        }

        if (_actor.isAttackingDisabled() || target == null || target.isDead()) {
            return false;
        }
        
        final int weaponRange = _actor.getStatus().getPhysicalAttackRange();
        final double distance = _actor.distance2D(target);
        
        
        
        int totalAttackRange = (int) (weaponRange + _actor.getCollisionRadius() + target.getCollisionRadius());

        if (distance > totalAttackRange) {
            _actor.getMove().maybeStartOffensiveFollow(target, weaponRange);
           
        }

        if (_actor.isMovementDisabled() && distance > totalAttackRange) {
            return false;
        }

        if (!_actor.knows(target) || !target.isAttackableBy(_actor)) {
            return false;
        }

        if (_actor instanceof Player && weaponRange >= 200 && distance > 150) {
            if (!MovementIntegration.canSeeTarget(_actor, target)) {
                _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_SEE_TARGET));
                return false;
            }
        }

        

        return true;
    }

    private void onHitTimer() {
        if (!this.isAttackingNow()) {
            return;
        }

        Creature mainTarget = this._hitHolders[0]._target;
        if (!_actor.knows(mainTarget) || mainTarget.isDead()) {
            this.stop();
            return;
        }

        if (!isTargetInMeleeRange(mainTarget)) {
            if (_actor instanceof Npc) {
                int attackRange = _actor.getStatus().getPhysicalAttackRange();
                _actor.getMove().maybeStartOffensiveFollow(mainTarget, attackRange);
            }
            
            final double dist = _actor.distance2D(mainTarget);
            if (Config.DEBUG_MELEE_ATTACK && _actor instanceof Npc && _actor.getStatus().getPhysicalAttackRange() <= 200) {
                LOGGER.info("[MeleeDebug] onHitTimer: outOfRangeHit npc={} target={} dist={}", _actor.getObjectId(), mainTarget.getObjectId(), dist);
            }
            
            if (dist > 300) {
                this.clearAttackTask(false);
                return;
            }
        }

        if (this._isHit) {
            _actor.setChargedShot(ShotType.SOULSHOT, false);
        }

        if (_actor instanceof Playable) {
            _actor.rechargeShots(true, false);
        }

        long finalDelay = Math.max(150, (long) this._afterAttackDelay);

        switch (this._weaponType) {
            case BOW: {
                this.doHit(this._hitHolders[0]);
                
                this._isAttackingNow = false;
                this._isBowCoolingDown = true;
                
                this._attackTask = ThreadPool.schedule(() -> {
                    this._isBowCoolingDown = false;
                    this._actor.getAI().notifyEvent(AiEventType.BOW_ATTACK_REUSED, null, null);
                }, finalDelay);
                
                this.onFinishedAttackBow(mainTarget);
                break;
            }
            case DUAL:
            case DUALFIST: {
                this.doHit(this._hitHolders[0]);
                this._attackTask = ThreadPool.schedule(() -> {
                    if (isAttackingNow()) {
                        doHit(_hitHolders[1]);
                        ThreadPool.schedule(() -> onFinishedAttack(mainTarget), finalDelay / 2);
                    } else {
                        onFinishedAttack(mainTarget);
                    }
                }, finalDelay / 2);
                break;
            }
            case POLE: {
                for (HitHolder hitHolder : this._hitHolders) {
                    this.doHit(hitHolder);
                }
                this._attackTask = ThreadPool.schedule(() -> onFinishedAttack(mainTarget), finalDelay);
                break;
            }
            default: {
                this.doHit(this._hitHolders[0]);
                this._attackTask = ThreadPool.schedule(() -> onFinishedAttack(mainTarget), finalDelay);
                break;
            }
        }
    }

    private boolean isTargetInMeleeRange(Creature target) {
        if (_weaponType == WeaponType.BOW) {
            return true;
        }

        int attackRange = _actor.getStatus().getPhysicalAttackRange();
        int totalAttackRange = (int) (attackRange + _actor.getCollisionRadius() + target.getCollisionRadius());
        int tolerance = 20;
        return _actor.distance2D(target) <= (totalAttackRange + tolerance);
    }

    protected void onFinishedAttackBow(Creature mainTarget) {
        if (_actor instanceof Npc) {
            if (_actor.getStatus().getPhysicalAttackRange() > 200) {
                _actor.getMove().repositionAfterAttack(mainTarget);
            }
            
            ((NpcAI) _actor.getAI()).runAI(false);
        }
        _actor.getAI().notifyEvent(AiEventType.FINISHED_ATTACK_BOW, null, null);
    }
    
    protected void onFinishedAttack(Creature mainTarget) {
        this.clearAttackTask(false);
        
        if (_actor instanceof Npc) {
            if (_actor.getStatus().getPhysicalAttackRange() > 200) {
                _actor.getMove().repositionAfterAttack(mainTarget);
            }
    
            ((NpcAI) _actor.getAI()).runAI(false);
        }
        _actor.getAI().notifyEvent(AiEventType.FINISHED_ATTACK, null, null);
    }

    private void doHit(HitHolder hitHolder) {
        Creature target = hitHolder._target;
        if (hitHolder._miss) {
            _actor.getAI().startAttackStance();
            target.getAI().notifyEvent(AiEventType.EVADED, _actor, null);
        }

        _actor.sendDamageMessage(target, hitHolder._damage, false, hitHolder._crit, hitHolder._miss);
        _actor.getAI().startAttackStance();
        
        if (Config.DEBUG_MELEE_ATTACK && _actor instanceof Npc && _actor.getStatus().getPhysicalAttackRange() <= 200) {
            LOGGER.info("[MeleeDebug] doHit: npc={} target={} dmg={} crit={} miss={}", _actor.getObjectId(), target.getObjectId(), hitHolder._damage, hitHolder._crit, hitHolder._miss);
        }

        if (!hitHolder._miss && hitHolder._damage > 0) {
            target.getAI().notifyEvent(AiEventType.ATTACKED, _actor, null);
            target.reduceCurrentHp(hitHolder._damage, _actor, null);
            
            double reflectPercent = target.getStatus().calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0.0, null, null);
            if (_weaponType != WeaponType.BOW && !target.isInvul() && reflectPercent > 0.0) {
                int reflectedDamage = (int) (reflectPercent / 100.0 * hitHolder._damage);
                _actor.reduceCurrentHp(Math.min(reflectedDamage, target.getStatus().getMaxHp()), target, true, false, null);
            }

            if (_weaponType != WeaponType.BOW) {
                double absorbPercent = _actor.getStatus().calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0.0, null, null);
                if (absorbPercent > 0.0) {
                    _actor.getStatus().addHp(absorbPercent / 100.0 * hitHolder._damage);
                }
            }

            Formulas.calcCastBreak(target, hitHolder._damage);
            ChanceSkillList chanceSkills = _actor.getChanceSkills();
            if (chanceSkills != null) {
                chanceSkills.onTargetHit(target, hitHolder._crit);
            }
            if (target.getChanceSkills() != null) {
                target.getChanceSkills().onSelfHit(_actor);
            }
            
            Weapon activeWeapon = _actor.getActiveWeaponItem();
            if (hitHolder._crit && activeWeapon != null) {
                activeWeapon.castSkillOnCrit(_actor, target);
            }
        }
    }

    public void doAttack(Creature target) {
        
        
        if (_isAttackingNow && _attackTask == null) {
            _isAttackingNow = false;
        }

        if (_isAttackingNow || _isBowCoolingDown) {
            return;
        }
        
        if (Config.DEBUG_MELEE_ATTACK && _actor instanceof Npc && _actor.getStatus().getPhysicalAttackRange() <= 200) {
            LOGGER.info("[MeleeDebug] doAttack: npc={} target={} moving={} dist={}", _actor.getObjectId(), target.getObjectId(), _actor.isMoving(), _actor.distance2D(target));
        }

        int timeAtk = Formulas.calculateTimeBetweenAttacks(_actor);
        Weapon weaponItem = _actor.getActiveWeaponItem();
        boolean isSoulshot = _actor.isChargedShot(ShotType.SOULSHOT);
        _actor.getPosition().setHeadingTo(target);

        HitHolder[] hits = switch (_actor.getAttackType()) {
            case WeaponType.BOW -> this.doAttackHitByBow(target, weaponItem, timeAtk, isSoulshot);
            case WeaponType.DUAL, WeaponType.DUALFIST -> this.doAttackHitByDual(target, weaponItem, timeAtk, isSoulshot);
            case WeaponType.POLE -> this.doAttackHitByPole(target, weaponItem, timeAtk, isSoulshot);
            default -> this.doAttackHitSimple(target, weaponItem, timeAtk, isSoulshot);
        };

        if (hits != null) {
            
            _actor.broadcastPacket(new Attack(_actor, hits));
        }
    }

    private HitHolder[] doAttackHitByBow(Creature target, Weapon weapon, int sAtk, boolean isSoulshot) {
        final boolean infinityArrows = Config.INFINITY_ARROWS && (!Config.INFINITY_ARROWS_PREMIUM_ONLY
                || (_actor instanceof Playable p && p.getActingPlayer() != null && p.getActingPlayer().getPremiumService() > 0));
        if (!infinityArrows) {
            _actor.reduceArrowCount();
        }
        _actor.getStatus().reduceMp(_actor.getActiveWeaponItem().getMpConsume());
        
        HitHolder[] hits = new HitHolder[]{this.getHitHolder(target, isSoulshot, false)};
        int reuse = weapon.getReuseDelay();
        if (reuse != 0) {
            reuse = reuse * 345 / _actor.getStatus().getPAtkSpd();
        }

        int safeAtkTime = Math.max(200, sAtk);
        this.setAttackTask(hits, weapon, reuse, isSoulshot);
        this._attackTask = ThreadPool.schedule(this::onHitTimer, (long) safeAtkTime);

        if (_actor instanceof Player) {
            _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.GETTING_READY_TO_SHOOT_AN_ARROW));
            _actor.sendPacket(new SetupGauge(GaugeColor.RED, safeAtkTime + reuse));
        }
        return hits;
    }

    private HitHolder[] doAttackHitByDual(Creature target, Weapon weapon, int sAtk, boolean isSoulshot) {
        HitHolder[] hits = new HitHolder[]{this.getHitHolder(target, isSoulshot, true), this.getHitHolder(target, isSoulshot, true)};
        int safeAtkTime = Math.max(200, sAtk);
        this.setAttackTask(hits, weapon, safeAtkTime / 2, isSoulshot);
        this._attackTask = ThreadPool.schedule(this::onHitTimer, (long) (safeAtkTime / 2));
        return hits;
    }

    private HitHolder[] doAttackHitByPole(Creature target, Weapon weapon, int sAtk, boolean isSoulshot) {
        ArrayList<HitHolder> hitHolders = new ArrayList<>();
        hitHolders.add(this.getHitHolder(target, isSoulshot, false));
        
        int maxAttackedCount = _actor.getFirstEffect(EffectType.POLEARM_TARGET_SINGLE) != null ? 1 : (int) _actor.getStatus().calcStat(Stats.ATTACK_COUNT_MAX, 0.0, null, null);
        if (maxAttackedCount > 1) {
            int maxAngleDiff = (int) _actor.getStatus().calcStat(Stats.POWER_ATTACK_ANGLE, 120.0, null, null);
            for (Creature knownCreature : _actor.getKnownTypeInRadius(Creature.class, _actor.getStatus().getPhysicalAttackRange())) {
                if (knownCreature == target || knownCreature.isDead() || !_actor.isFacing(knownCreature, maxAngleDiff) || !knownCreature.isAttackableBy(_actor)) {
                    continue;
                }
                hitHolders.add(this.getHitHolder(knownCreature, isSoulshot, false));
                if (hitHolders.size() >= maxAttackedCount) break;
            }
        }
        
        HitHolder[] hits = hitHolders.toArray(new HitHolder[0]);
        int safeAtkTime = Math.max(200, sAtk);
        this.setAttackTask(hits, weapon, safeAtkTime, isSoulshot);
        this._attackTask = ThreadPool.schedule(this::onHitTimer, (long) safeAtkTime);
        return hits;
    }

    private HitHolder[] doAttackHitSimple(Creature target, Weapon weapon, int sAtk, boolean isSoulshot) {
        HitHolder[] hits = new HitHolder[]{this.getHitHolder(target, isSoulshot, false)};
        int safeAtkTime = Math.max(200, sAtk);
        this.setAttackTask(hits, weapon, safeAtkTime, isSoulshot);
        this._attackTask = ThreadPool.schedule(this::onHitTimer, (long) safeAtkTime);
        return hits;
    }

    private HitHolder getHitHolder(Creature target, boolean isSoulshot, boolean isSplit) {
        boolean crit = false;
        ShieldDefense shld = ShieldDefense.FAILED;
        int damage = 0;
        boolean miss = Formulas.calcHitMiss(_actor, target);
        if (!miss) {
            crit = Formulas.calcCrit(_actor, target, null);
            shld = Formulas.calcShldUse(_actor, target, null, crit);
            damage = (int) Formulas.calcPhysicalAttackDamage(_actor, target, shld, crit, isSoulshot);
            if (isSplit) {
                damage /= 2;
            }
        }
        return new HitHolder(target, damage, crit, miss, shld);
    }

    public void stop() {
        if (this._attackTask != null) {
            this._attackTask.cancel(false);
            this._attackTask = null;
        }
        this.clearAttackTask(true);
    }

    public void interrupt() {
        if (this.isAttackingNow()) {
            this.stop();
            _actor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ATTACK_FAILED));
        }
    }

    private void setAttackTask(HitHolder[] hitHolders, Weapon weapon, int afterAttackDelay, boolean isSoulshot) {
        WeaponType weaponType = weapon == null ? WeaponType.ETC : weapon.getItemType();
        this._isAttackingNow = true;
        this._isBowCoolingDown = false; 
        this._hitHolders = hitHolders;
        this._weaponType = weaponType;
        this._afterAttackDelay = afterAttackDelay;
        this._isHit = false;
        
        final int weaponGrade = weapon == null ? 0 : weapon.getCrystalType().getId();
        for (HitHolder hit : this._hitHolders) {
            if (hit._miss) {
                hit._flags = 128;
                continue;
            }
            this._isHit = true;
            if (isSoulshot) {
                hit._flags = 0x10 | weaponGrade;
            }
            if (hit._crit) {
                hit._flags |= 0x20;
            }
            if (hit._sDef != ShieldDefense.FAILED) {
                hit._flags |= 0x40;
            }
        }
    }

    private void clearAttackTask(boolean clearBowCooldown) {
        this._isAttackingNow = false;
        if (clearBowCooldown) {
            this._isBowCoolingDown = false;
        }
    }

    public static class HitHolder {
        public Creature _target;
        public int _targetId;
        public int _damage;
        public boolean _crit;
        public boolean _miss;
        public ShieldDefense _sDef;
        public int _flags;

        public HitHolder(Creature target, int damage, boolean crit, boolean miss, ShieldDefense sDef) {
            this._target = target;
            this._targetId = target.getObjectId();
            this._damage = damage;
            this._crit = crit;
            this._sDef = sDef;
            this._miss = miss;
        }
    }
}