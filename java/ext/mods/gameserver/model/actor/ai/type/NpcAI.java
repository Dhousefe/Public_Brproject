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
package ext.mods.gameserver.model.actor.ai.type;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.CursedWeaponManager;
import ext.mods.gameserver.data.xml.WalkerRouteData;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.ClassType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.ai.Desire;
import ext.mods.gameserver.model.actor.ai.DesireQueue;
import ext.mods.gameserver.model.actor.container.attackable.AggroList;
import ext.mods.gameserver.model.actor.container.attackable.HateList;
import ext.mods.gameserver.model.actor.move.NpcMove;
import ext.mods.gameserver.model.actor.container.npc.AggroInfo;
import ext.mods.gameserver.model.actor.instance.Guard;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.entity.CursedWeapon;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.WalkerLocation;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.network.serverpackets.StopMove;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.WalkerTaskManager;

public class NpcAI<T extends Npc> extends CreatureAI<T>
{
    private static final CLogger LOGGER = new CLogger(NpcAI.class.getName());
    private static final int SOCIAL_INTERVAL = 12000;
    
    protected final DesireQueue _desireQueue;
    protected final Set<Creature> _seenCreatures = ConcurrentHashMap.newKeySet();
    protected final AggroList _aggroList;
    protected final HateList _hateList;
    
    private ScheduledFuture<?> _clearAggroTask;
    
    private int _routeIndex;
    private boolean _isOnARoute;
    private boolean _isReversePath;
    
    private int _runAiStep;
    private int _lifeTime;
    
    private boolean _isOOT;
    
    private long _lastSocialBroadcast;
    
    private Creature _topDesireTarget;
    
    protected Desire _lastDesire;
    protected Desire _nextDesire;
    
    public NpcAI(T npc)
    {
        super(npc);
        
        _desireQueue = new DesireQueue();
        _seenCreatures.clear();
        _aggroList = new AggroList(npc);
        _hateList = new HateList(npc);
    }
    
    
    @Override
    public void thinkAttack() {
        if (_actor.denyAiAction()) {
            return;
        }

        final Creature target = _currentIntention.getFinalTarget();
        if (target == null || target.isAlikeDead()) {
            doIdleIntention();
            return;
        }
        
        int weaponRange = (int) _actor.getStatus().getPhysicalAttackRange();
        
        if (!_actor.knows(target)) {
            if (_actor instanceof Npc npc) {
                final int aggroRange = npc.getTemplate().getAggroRange() > 0 ? npc.getTemplate().getAggroRange() : npc.getSeeRange();
                if (_actor.isIn3DRadius(target, aggroRange + 100)) {
                    _actor.refreshKnownlist();
                    _actor.getMove().maybeStartOffensiveFollow(target, weaponRange);
                    if (Config.DEBUG_MELEE_ATTACK && weaponRange <= 200) {
                        LOGGER.info("[MeleeDebug] thinkAttack: refreshKnownlist npc={} target={} dist={}", _actor.getObjectId(), target.getObjectId(), _actor.distance2D(target));
                    }
                    ThreadPool.schedule(() -> notifyEvent(AiEventType.THINK, null, null), 100);
                    return;
                }
            }
            if (Config.DEBUG_MELEE_ATTACK && weaponRange <= 200) {
                LOGGER.info("[MeleeDebug] thinkAttack: lostKnownlist npc={} target={}", _actor.getObjectId(), target.getObjectId());
            }
            doIdleIntention();
            return;
        }
        int actorCol = (int) _actor.getCollisionRadius();
        int targetCol = (int) target.getCollisionRadius();
        
        int totalRange = weaponRange + actorCol + targetCol;
        double dist = _actor.distance2D(target);

        if (Config.DEBUG_MELEE_ATTACK && weaponRange <= 200) {
            LOGGER.info("[MeleeDebug] thinkAttack: npc={} target={} dist={} range={} moving={} intention={}", _actor.getObjectId(), target.getObjectId(), dist, totalRange, _actor.isMoving(), _currentIntention.getType());
        }
        
        if (dist > totalRange) {
            _actor.getMove().maybeStartOffensiveFollow(target, weaponRange);
            if (Config.DEBUG_MELEE_ATTACK && weaponRange <= 200) {
                LOGGER.info("[MeleeDebug] thinkAttack: startFollow npc={} target={} dist={}", _actor.getObjectId(), target.getObjectId(), dist);
            }
            return;
        } 
        
        if (_actor.getAttack().isAttackingNow() || _actor.getAttack().isBowCoolingDown()) {
            if (Config.DEBUG_MELEE_ATTACK && weaponRange <= 200) {
                LOGGER.info("[MeleeDebug] thinkAttack: cooldown npc={} target={} attackingNow={} bowCooldown={}", _actor.getObjectId(), target.getObjectId(), _actor.getAttack().isAttackingNow(), _actor.getAttack().isBowCoolingDown());
            }
            return;
        }
        
        if (weaponRange <= 200 && _actor.getMove() instanceof NpcMove npcMove) {
            //if (npcMove.maintainMeleeSpacing(target, weaponRange)) {
                //if (Config.DEBUG_MELEE_ATTACK) {
                   // LOGGER.info("[MeleeDebug] thinkAttack: spacingMove npc={} target={}", _actor.getObjectId(), target.getObjectId());
                //}
                //return;
            //}
        }
        
        
        if (weaponRange <= 200 && !_actor.getAllSkillsDisabled() && !_actor.getCast().isCastingNow() && Rnd.get(100) < 30) {
            final L2Skill skill = selectMeleeSkill();
            if (skill != null) {
                final boolean offensive = skill.isOffensive() || skill.isDebuff();
                final Creature castTarget = offensive ? target : _actor;
                doCastIntention(castTarget, skill, false, false, 0, offensive);
                if (Config.DEBUG_MELEE_ATTACK) {
                    LOGGER.info("[MeleeDebug] thinkAttack: castSkill npc={} skillId={} offensive={}", _actor.getObjectId(), skill.getId(), offensive);
                }
                return;
            }
        }
        
        if (!_actor.getAttack().canAttack(target)) {
            if (dist <= totalRange + 100) {
                _actor.getMove().startOffensiveFollow(target, weaponRange);
            } else {
                _actor.getMove().maybeStartOffensiveFollow(target, weaponRange);
            }
            if (Config.DEBUG_MELEE_ATTACK && weaponRange <= 200) {
                LOGGER.info("[MeleeDebug] thinkAttack: canAttack=false npc={} target={} dist={} range={}", _actor.getObjectId(), target.getObjectId(), dist, totalRange);
            }
            return;
        }
        
        if (_actor.isMoving()) {
            _actor.getMove().stop();
        }
        if (!_actor.isFacing(target, 35)) {
            _actor.getPosition().setHeadingTo(target);
        }
        _actor.rechargeShots(true, false);
        _actor.getAttack().doAttack(target);
        if (Config.DEBUG_MELEE_ATTACK && weaponRange <= 200) {
            LOGGER.info("[MeleeDebug] thinkAttack: doAttack npc={} target={}", _actor.getObjectId(), target.getObjectId());
        }
        
        setNextIntention(_currentIntention);

    }

    private L2Skill selectMeleeSkill() {
        final List<L2Skill> offensiveSkills = _actor.getTemplate().getSkills(
            NpcSkillType.PHYSICAL_SPECIAL,
            NpcSkillType.PHYSICAL_SPECIAL1,
            NpcSkillType.PHYSICAL_SPECIAL2,
            NpcSkillType.PHYSICAL_SPECIAL3,
            NpcSkillType.PHYSICAL_SPECIAL_A,
            NpcSkillType.PHYSICAL_SPECIAL_B,
            NpcSkillType.PHYSICAL_SPECIAL_RANGE,
            NpcSkillType.DD_PHYSIC1,
            NpcSkillType.DD_PHYSIC2,
            NpcSkillType.DD_PHYSIC3,
            NpcSkillType.SPECIAL_ATTACK,
            NpcSkillType.SPECIAL_SKILL,
            NpcSkillType.RANGE_PHYSICAL_SPECIAL,
            NpcSkillType.SELF_RANGE_PHYSICAL_SPECIAL,
            NpcSkillType.SELF_RANGE_PHYSICAL_SPECIAL_A,
            NpcSkillType.MOB_HATE
        ).stream().filter(s -> s != null && (s.isOffensive() || s.isDebuff())).toList();
        
        final List<L2Skill> buffSkills = _actor.getTemplate().getSkills(
            NpcSkillType.BUFF,
            NpcSkillType.BUFF1,
            NpcSkillType.BUFF2,
            NpcSkillType.BUFF3,
            NpcSkillType.BUFF4,
            NpcSkillType.BUFF5,
            NpcSkillType.BUFF6,
            NpcSkillType.SELF_BUFF,
            NpcSkillType.SELF_BUFF1,
            NpcSkillType.SELF_BUFF2,
            NpcSkillType.SELF_BUFF3,
            NpcSkillType.SELF_BUFF4,
            NpcSkillType.SELF_BUFF_A,
            NpcSkillType.RANGE_BUFF,
            NpcSkillType.SELF_RANGE_BUFF,
            NpcSkillType.SELF_RANGE_BUFF1,
            NpcSkillType.SELF_RANGE_BUFF_A,
            NpcSkillType.CLAN_BUFF1,
            NpcSkillType.W_CLAN_BUFF,
            NpcSkillType.FURY,
            NpcSkillType.EFFECT_SKILL,
            NpcSkillType.EFFECT_SKILL2
        ).stream().filter(s -> s != null && !s.isOffensive() && !s.isDebuff()).toList();
        
        final boolean preferOffensive = Rnd.get(100) < 50;
        if (preferOffensive && !offensiveSkills.isEmpty()) {
            return offensiveSkills.get(Rnd.get(offensiveSkills.size()));
        }
        if (!buffSkills.isEmpty()) {
            return buffSkills.get(Rnd.get(buffSkills.size()));
        }
        if (!offensiveSkills.isEmpty()) {
            return offensiveSkills.get(Rnd.get(offensiveSkills.size()));
        }
        return null;
    }

    /**
     * Sistema Zig-Zag (Deterministic Surround).
     */
    private Location getZigZagPosition(Npc actor, Creature target, int range, boolean isRanged) {
        int targetX = target.getX();
        int targetY = target.getY();
        int targetZ = target.getZ();

        int layerOffset = (actor.getObjectId() % 2) * 50; 
        int radius;
        if (isRanged) {
            radius = range + layerOffset;
        } else {
            radius = (int) (target.getCollisionRadius() + actor.getCollisionRadius() + 40 + layerOffset);
        }

        int angleDegree = (actor.getObjectId() * 137) % 360;
        double angleRadian = Math.toRadians(angleDegree);

        int destX = (int) (targetX + (Math.cos(angleRadian) * radius));
        int destY = (int) (targetY + (Math.sin(angleRadian) * radius));
        
        if (GeoEngine.getInstance().canMoveToTarget(targetX, targetY, targetZ, destX, destY, targetZ)) {
            int destZ = GeoEngine.getInstance().getHeight(destX, destY, targetZ);
            return new Location(destX, destY, destZ);
        } else {
            return new Location(targetX, targetY, targetZ);
        }
    }

    public void stopFollow() {
        if (_currentIntention.getType() == IntentionType.ATTACK) {
            final Creature target = _currentIntention.getFinalTarget();
            if (target != null && !target.isAlikeDead() && _actor instanceof Npc npc) {
                final int aggroRange = npc.getTemplate().getAggroRange() > 0 ? npc.getTemplate().getAggroRange() : npc.getSeeRange();
                if (_actor.isIn3DRadius(target, aggroRange + 100)) {
                    ThreadPool.schedule(() -> notifyEvent(AiEventType.THINK, null, null), 100);
                    return;
                }
            }
        }
        doIdleIntention();
    }


    @Override
    public void stopAITask()
    {
        _isOnARoute = false;
        super.stopAITask();
    }
    
    @Override
    public void thinkIdle()
    {
        _actor.abortAll(true);
        _actor.forceWalkStance();
        
        for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.NO_DESIRE))
            quest.onNoDesire(_actor);
    }
    
    @Override
    protected void thinkSocial()
    {
        clearCurrentDesire();
        
        if (_actor.denyAiAction())
            return;
        
        _lastSocialBroadcast = System.currentTimeMillis() + _currentIntention.getTimer();
        
        _actor.getMove().stop();
        _actor.broadcastPacket(new SocialAction(_actor, _currentIntention.getItemObjectId()));
    }

    @Override
    protected void thinkFollow()
    {
        if (_runAiStep % 2 == 0) return;
        _actor.setWalkOrRun(true);
        if (_actor.denyAiAction() || _actor.isMovementDisabled()) return;
        
        final Creature target = _currentIntention.getFinalTarget();
        if (target == null || _actor == target || target.isDead()) {
            clearCurrentDesire();
            return;
        }
        
        if (_actor.getMove().getGeoPathFailCount() >= 10) {
            _actor.teleportTo(target.getPosition(), 10);
            return;
        }
        
        if (_actor.hasMaster() && target == _actor.getMaster()) {
            if (_actor.isIn2DRadius(target, 150)) return;
            _actor.getMove().maybeMoveToLocation(target.getPosition(), 100, true, false);
        } else if (!_actor.isIn2DRadius(target, 150) && Rnd.get(100) > 50 && !_actor.isMoving()) {
            _actor.getMove().maybeMoveToLocation(target.getPosition(), 100, true, false);
        }
    }
    
    @Override
    protected void thinkNothing() {}
    
    @Override
    protected ItemInstance thinkPickUp()
    {
        if (_actor.denyAiAction()) return null;
        final WorldObject target = World.getInstance().getObject(_currentIntention.getItemObjectId());
        if (!(target instanceof ItemInstance item) || isTargetLost(target)) return null;
        if (item.getLocation() != ItemLocation.VOID) return null;
        if (_actor.getMove().maybeMoveToLocation(target.getPosition(), 36, false, false)) return null;
        
        for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.PICKED_ITEM))
            quest.onPickedItem(_actor, item);
        
        final CursedWeapon cw = CursedWeaponManager.getInstance().getCursedWeapon(item.getItemId());
        if (cw != null) cw.endOfLife(); else item.decayMe();
        
        clearCurrentDesire();
        return item;
    }
    
    @Override
    protected void thinkMoveRoute()
    {
        if (!_isOnARoute || _actor.getNpcId() == 32108) moveToNextPoint();
    }
    
    @Override
    protected void thinkMoveTo()
    {
        if (_actor.getPosition().equals(_currentIntention.getLoc())) {
            for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.MOVE_TO_FINISHED))
                quest.onMoveToFinished(_actor, _actor.getX(), _actor.getY(), _actor.getZ());
            clearCurrentDesire();
            return;
        }
        super.thinkMoveTo();
    }
    
    @Override
    protected void onEvtArrived()
    {
        if (_currentIntention.getType() == IntentionType.ATTACK)
        {
            notifyEvent(AiEventType.THINK, null, null);
            return;
        }
        
        switch (_currentIntention.getType()) {
            case MOVE_TO, FLEE:
                for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.MOVE_TO_FINISHED))
                    quest.onMoveToFinished(_actor, _actor.getX(), _actor.getY(), _actor.getZ());
            case WANDER:
                clearCurrentDesire();
                break;
            case FOLLOW:
                return;
        }
        
        if (_actor.getPosition().equals(_actor.getSpawnLocation()))
            _actor.getPosition().setHeading(_actor.getSpawnLocation().getHeading());
        
        if (!_actor.isInMyTerritory()) {
            if (!_isOOT) {
                for (Quest quest : _actor.getTemplate().getEventQuests(EventHandler.OUT_OF_TERRITORY))
                    quest.onOutOfTerritory(_actor);
                
                if (_clearAggroTask != null) {
                    _clearAggroTask.cancel(true);
                    _clearAggroTask = null;
                }
                
                _clearAggroTask = ThreadPool.scheduleAtFixedRate(() -> {
                    if (_actor.isInMyTerritory()) return;
                    final long currentTime = System.currentTimeMillis();
                    for (AggroInfo ai : _aggroList.values()) {
                        if (currentTime - ai.getTimestamp() >= 90000) ai.stopHate();
                    }
                }, 100, 10000);
                _isOOT = true;
            }
        } else {
            _isOOT = false;
            if (_clearAggroTask != null) {
                _clearAggroTask.cancel(true);
                _clearAggroTask = null;
            }
        }
        
        if (_currentIntention.getType() != IntentionType.MOVE_ROUTE) {
            _isOnARoute = false;
            return;
        }
        
        final List<WalkerLocation> route = WalkerRouteData.getInstance().getWalkerRoute(_currentIntention.getRouteName(), _actor.getTemplate().getAlias());
        if (route.isEmpty()) {
            _isOnARoute = false;
            return;
        }
        
        _isOnARoute = true;
        final WalkerLocation node = route.get(_routeIndex);
        if (node.getNpcStringId() != null) _actor.broadcastNpcSay(node.getNpcStringId());
        if (node.getDelay() > 0) {
            if (node.getSocialId() > 0) _actor.broadcastPacket(new SocialAction(_actor, node.getSocialId()));
            WalkerTaskManager.getInstance().add(_actor, node.getDelay());
        } else moveToNextPoint();
    }
    
    @Override
    protected void onEvtArrivedBlocked()
    {
        switch (_currentIntention.getType()) {
            case MOVE_TO, FLEE, WANDER:
                clearCurrentDesire();
                break;
        }
        super.onEvtArrivedBlocked();
    }
    
    @Override
    protected void onEvtFinishedCasting()
    {
        runAI(false);
    }
    
    @Override
    protected void onEvtTeleported()
    {
    }
    

    public void setBackToPeace()
    {
        _aggroList.clear();
        _hateList.clear();
    }
    
    public final AggroList getAggroList()
    {
        return _aggroList;
    }
    
    public final HateList getHateList()
    {
        return _hateList;
    }
    
    public int getRouteIndex()
    {
        return _routeIndex;
    }
    
    public boolean isReversePath()
    {
        return _isReversePath;
    }
    
    public int getLifeTime()
    {
        return _lifeTime;
    }
    
    public void resetLifeTime()
    {
        _lifeTime = 0;
    }
    
    public Creature getTopDesireTarget()
    {
        return _topDesireTarget;
    }
    
    public void setTopDesireTarget(Creature target)
    {
        _topDesireTarget = target;
    }
    
    public void runAI()
    {
        runAI(true);
    }
    
    public void runAI(boolean updateTick)
    {
        if (_lastDesire != null && _lastDesire.getType() == IntentionType.IDLE)
            getDesires().remove(_lastDesire);
        
        if (updateTick)
        {
            final List<Quest> scripts = _actor.getTemplate().getEventQuests(EventHandler.SEE_CREATURE);
            if (!scripts.isEmpty()) {
                _actor.forEachKnownType(Playable.class, pl -> {
                    if (Config.MOB_AGGRO_IN_PEACEZONE && _actor instanceof Monster monster && monster.getTemplate().getAggro() && pl.isInsideZone(ZoneId.PEACE)) {
                        monster.abortAll(true);
                        monster.removeAllAttackDesire();
                        monster.teleportTo(monster.getSpawnLocation(), 0);
                        return;
                    }
                    final Player player = pl.getActingPlayer();
                    if (player.isSpawnProtected() || player.isFlying() || !player.getAppearance().isVisible()) return;
                    
                    final boolean isInRange = _actor.isIn3DRadius(pl, _actor.getSeeRange());
                    if (!_actor.isRaidBoss() && _seenCreatures.contains(pl)) {
                        if (_actor instanceof Guard) _seenCreatures.remove(pl);
                        else if (!isInRange) _seenCreatures.remove(pl);
                    } else if (_actor.isRaidBoss() && isInRange && Math.abs(pl.getZ() - _actor.getZ()) <= 500) {
                        if (pl.isMoving() || _actor.isMoving()) {
                            for (Quest quest : scripts) quest.onSeeCreature(_actor, pl);
                        }
                    } else if (isInRange) {
                        if (pl.isSilentMoving() && !_actor.getTemplate().canSeeThrough()) return;
                        _seenCreatures.add(pl);
                        for (Quest quest : scripts) quest.onSeeCreature(_actor, pl);
                    }
                });
            }
        }
        
        _desireQueue.getDesires().removeIf(d -> d.getType() == IntentionType.CAST && (d.getWeight() <= 0 || !_actor.getCast().meetsHpMpDisabledConditions(d.getFinalTarget(), d.getSkill())));
        _desireQueue.getDesires().removeIf(d -> {
            if (d.getFinalTarget() == null) return false;
            if (d.getFinalTarget().isAlikeDead()) return true;
            if (d.getType() == IntentionType.ATTACK && _actor instanceof Npc npc) {
                final int aggroRange = npc.getTemplate().getAggroRange() > 0 ? npc.getTemplate().getAggroRange() : npc.getSeeRange();
                if (_actor.isIn3DRadius(d.getFinalTarget(), aggroRange + 100)) {
                    if (!_actor.knows(d.getFinalTarget())) _actor.refreshKnownlist();
                    return false;
                }
            }
            return !_actor.knows(d.getFinalTarget());
        });
        _desireQueue.getDesires().removeIf(d -> d.getTarget() != null && (!_actor.knows(d.getTarget())));
        
        if (!_actor.isOutOfControl()) {
            final boolean instantRun = _lifeTime == 0 && getDesires().stream().anyMatch(d -> d.getType() == IntentionType.ATTACK);
            if (!_actor.getCast().isCastingNow() && (_lifeTime > 0 || instantRun) && _lastSocialBroadcast <= System.currentTimeMillis()) {
                if (!(_currentIntention.getType() == IntentionType.FLEE && getDesires().contains(_currentIntention))) {
                    if (getDesires().isEmpty() && _nextDesire == null) {
                        if (_currentIntention.getType() == IntentionType.ATTACK) {
                            final Creature target = _currentIntention.getFinalTarget();
                            if (target != null && !target.isAlikeDead() && _actor instanceof Npc npc) {
                                final int aggroRange = npc.getTemplate().getAggroRange() > 0 ? npc.getTemplate().getAggroRange() : npc.getSeeRange();
                                if (_actor.isIn3DRadius(target, aggroRange + 100)) {
                                    return;
                                }
                            }
                        }
                        if (_currentIntention.getType() != IntentionType.IDLE) doIdleIntention();
                    } else if (!_actor.getAttack().isAttackingNow()) {
                        final Desire toDoDesire = (_nextDesire != null) ? _nextDesire : _desireQueue.getLast();
                        if (toDoDesire != null && !(_currentIntention.getType() == IntentionType.WANDER && toDoDesire.getType() == IntentionType.WANDER)) {
                            if (toDoDesire.getType() == IntentionType.ATTACK && _actor.getMove() instanceof NpcMove npcMove) {
                                final Creature target = toDoDesire.getFinalTarget();
                                if (target != null) {
                                    final int weaponRange = (int) _actor.getStatus().getPhysicalAttackRange();
                                    //if (npcMove.maintainMeleeSpacing(target, weaponRange)) {
                                        //if (Config.DEBUG_MELEE_ATTACK) {
                                        //    LOGGER.info("[MeleeDebug] runAI: spacingMove npc={} target={}", _actor.getObjectId(), target.getObjectId());
                                        //}
                                        //return;
                                    //}
                                }
                            }
                            if (toDoDesire.getType() == IntentionType.ATTACK && (_lastDesire == null || (_lastDesire.getType() == IntentionType.WANDER || _lastDesire.getType() == IntentionType.IDLE)))
                                _nextDesire = toDoDesire;
                            else _nextDesire = null;
                            if (toDoDesire.getType() != IntentionType.MOVE_ROUTE) _isOnARoute = false;
                            doIntention(toDoDesire);
                            _topDesireTarget = toDoDesire.getFinalTarget();
                            _lastDesire = toDoDesire;
                        }
                    }
                }
            }
        }
        
        if (updateTick) {
            if (getDesires().isEmpty() && _lifeTime > 0 && !_actor.getCast().isCastingNow()) {
                thinkIdle();
                _topDesireTarget = null;
            }
            _runAiStep++;
            _lifeTime++;
            if (_runAiStep % 3 == 0) {
                _aggroList.refresh();
                _hateList.refresh();
                _aggroList.reduceAllHate(6.6);
                _desireQueue.autoDecreaseWeight();
                if (_currentIntention.getType() == IntentionType.ATTACK || _currentIntention.getType() == IntentionType.CAST)
                    _actor.setWalkOrRun(true);
                _runAiStep = 0;
            }
        }
    }
    
    public void moveToNextPoint()
    {
        if (_actor.getNpcId() == 32108) {
            final List<WalkerLocation> route = WalkerRouteData.getInstance().getWalkerRoute(_currentIntention.getRouteName(), _actor.getTemplate().getAlias());
            if (route.isEmpty()) { _isOnARoute = false; return; }
            WalkerLocation nextNode;
            if (!_isOnARoute) {
                nextNode = route.stream().min(Comparator.comparingDouble(_actor::distance3D)).orElse(null);
                if (nextNode != null) { _isOnARoute = true; _routeIndex = route.indexOf(nextNode); }
            } else {
                if (_isReversePath && _routeIndex > 0) {
                    _routeIndex--;
                    if (_routeIndex == 0) _isReversePath = false;
                } else if (_routeIndex < route.size() - 1) _routeIndex++;
                else {
                    _routeIndex = 0;
                    if (!GeoEngine.getInstance().canMoveToTarget(_actor.getPosition(), route.get(_routeIndex))) {
                        _isReversePath = true;
                        _routeIndex = route.size() - 2;
                    }
                }
                nextNode = route.get(_routeIndex);
            }
            _actor.getMove().maybeMoveToLocation(nextNode, 500, false, false);
        } else {
            if (_currentIntention.getType() != IntentionType.MOVE_ROUTE) { _isOnARoute = false; return; }
            final List<WalkerLocation> route = WalkerRouteData.getInstance().getWalkerRoute(_currentIntention.getRouteName(), _actor.getTemplate().getAlias());
            if (route.isEmpty()) { _isOnARoute = false; return; }
            WalkerLocation nextNode;
            if (!_isOnARoute) {
                nextNode = route.stream().min(Comparator.comparingDouble(_actor::distance3D)).orElse(null);
                if (nextNode != null) { _isOnARoute = true; _routeIndex = route.indexOf(nextNode); }
            } else {
                if (_actor.getMove().getGeoPathFailCount() >= 10) {
                    _routeIndex = 0; _isOnARoute = true; _isReversePath = false;
                    _actor.teleportTo(route.get(_routeIndex), 0);
                }
                if (_isReversePath && _routeIndex > 0) {
                    _routeIndex--;
                    if (_routeIndex == 0) _isReversePath = false;
                } else if (_routeIndex < route.size() - 1) _routeIndex++;
                else {
                    _routeIndex = 0;
                    if (!GeoEngine.getInstance().canMoveToTarget(_actor.getPosition(), route.get(_routeIndex))) {
                        _isReversePath = true;
                        _routeIndex = route.size() - 2;
                    }
                }
                nextNode = route.get(_routeIndex);
            }
            _actor.getMove().maybeMoveToLocation(nextNode, 0, true, false);
        }
    }
    
    
    public Set<Desire> getDesires()
    {
        return _desireQueue.getDesires();
    }
    
    public void clearCurrentDesire()
    {
        getDesires().remove(_currentIntention);
    }
    
    public void addAttackDesireHold(Creature target, double weight) { addAttackDesire(target, 0, weight, true, false); }
    public void addAttackDesireHold(Creature target, int damage, double weight) { addAttackDesire(target, damage, weight, true, false); }
    public void addAttackDesireHold(Creature target, int damage, double weight, boolean updateAggro) { addAttackDesire(target, damage, weight, updateAggro, false); }
    public void addAttackDesire(Creature target, double weight) { addAttackDesire(target, 2, weight, true); }
    public void addAttackDesire(Creature target, int damage, double weight) { addAttackDesire(target, damage, weight, true); }
    public void addAttackDesire(Creature target, int damage, double weight, boolean updateAggro) { addAttackDesire(target, damage, weight, updateAggro, true); }
    public void addAttackDesire(Creature target, int damage, double weight, boolean updateAggro, boolean canMoveToTarget) {
        if (target == null) return;
        final Desire desire = new Desire(weight);
        desire.updateAsAttack(target, false, false, canMoveToTarget);
        _desireQueue.addOrUpdate(desire);
        if (_aggroList.getMostHatedCreature() == null) runAI(false);
        if (target instanceof Player player && player.getClassId().getType() == ClassType.PRIEST && damage > 0) updateAggro = true;
        if (updateAggro) _aggroList.addDamageHate(target, damage, weight);
    }
    
    public void addCastDesireHold(Creature target, int skillId, int skillLevel, double weight) {
        final L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill == null) return;
        addCastDesire(target, skill, weight, true, false);
    }
    public void addCastDesireHold(Creature target, L2Skill skill, double weight) { addCastDesire(target, skill, weight, true, false); }
    public void addCastDesireHold(Creature target, L2Skill skill, double weight, boolean checkConditions) { addCastDesire(target, skill, weight, checkConditions, false); }
    public void addCastDesire(Creature target, int skillId, int skillLevel, double weight) {
        final L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill == null) return;
        addCastDesire(target, skill, weight);
    }
    public void addCastDesire(Creature target, int skillId, int skillLevel, double weight, boolean checkConditions) {
        final L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
        if (skill == null) return;
        addCastDesire(target, skill, weight, checkConditions);
    }
    public void addCastDesire(Creature target, L2Skill skill, double weight) { addCastDesire(target, skill, weight, true); }
    public void addCastDesire(Creature target, L2Skill skill, double weight, boolean checkConditions) { addCastDesire(target, skill, weight, checkConditions, true); }
    public void addCastDesire(Creature target, L2Skill skill, double weight, boolean checkConditions, boolean canMoveToTarget) {
        if (target == null || skill == null) return;
        if (checkConditions) {
            if (!_actor.getCast().canAttemptCast(target, skill)) return;
            final double mpConsume = _actor.getStatus().getMpConsume(skill);
            if (mpConsume > 0 && mpConsume > _actor.getStatus().getMp()) return;
            final double hpConsume = skill.getHpConsume();
            if (hpConsume > 0 && hpConsume > _actor.getStatus().getHp()) return;
        }
        if (!canMoveToTarget && !_actor.isIn2DRadius(target, (int) (skill.getCastRange() + _actor.getCollisionRadius() + target.getCollisionRadius()))) return;
        if (!GeoEngine.getInstance().canSeeTarget(_actor, target)) return;
        Desire desire = new Desire(weight);
        desire.updateAsCast(_actor, target, skill, false, false, 0, canMoveToTarget);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addFleeDesire(Creature target, int distance, double weight) {
        if (target == null || _actor.isMovementDisabled()) return;
        final Desire desire = new Desire(weight);
        desire.updateAsFlee(target, _actor.getPosition(), distance);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addFollowDesire(Creature target, double weight) {
        if (target == null) return;
        final Desire desire = new Desire(weight);
        desire.updateAsFollow(target, false);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addInteractDesire(WorldObject target, double weight) {
        if (target == null) return;
        final Desire desire = new Desire(weight);
        desire.updateAsInteract(target, false, false);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addMoveRouteDesire(String routeName, double weight) {
        final Desire desire = new Desire(weight);
        desire.updateAsMoveRoute(routeName);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addMoveToDesire(Location loc, double weight) {
        if (loc == null || _actor.isMovementDisabled()) return;
        final Desire desire = new Desire(weight);
        desire.updateAsMoveTo(loc, null);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addSocialDesire(int id, int timer, double weight) {
        if (_actor.isAISleeping()) return;
        final Desire desire = new Desire(weight);
        desire.updateAsSocial(id, timer);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addWanderDesire(int timer, double weight) {
        final Desire desire = new Desire(weight);
        desire.updateAsWander(timer);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addDoNothingDesire(int timer, double weight) {
        final Desire desire = new Desire(weight);
        desire.updateAsNothing(timer);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void addPickUpDesire(int itemObjectId, double weight) {
        if (itemObjectId == 0) return;
        final Desire desire = new Desire(weight);
        desire.updateAsPickUp(itemObjectId, false);
        _desireQueue.addOrUpdate(desire);
    }
    
    public void onRandomAnimation(int id) {
        if (_actor.denyAiAction()) return;
        final long now = System.currentTimeMillis();
        if (now - _lastSocialBroadcast > SOCIAL_INTERVAL) {
            _lastSocialBroadcast = now;
            _actor.broadcastPacket(new SocialAction(_actor, id));
        }
    }
    
    public void stopRoute() { _isOnARoute = false; }
    
    public void cleanupForNextSpawn() {
        getDesires().clear();
        _seenCreatures.clear();
        _nextDesire = null;
        _isOnARoute = false;
        _topDesireTarget = null;
        _lastSocialBroadcast = 0l;
    }
}