/*
 * MIT License
 * * Copyright (c) 2024-2026 L2Brproject
 * * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * * Our main Developers: Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
 * Our special thanks: Nattan Felipe, Diego Fonseca, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
 * as a contribution for the forum L2JBrasil.com
 */
package ext.mods.gameserver.model.actor.move
import ext.mods.Config
import ext.mods.commons.logging.CLogger
import ext.mods.commons.random.Rnd
import ext.mods.gameserver.enums.AiEventType
import ext.mods.gameserver.enums.actors.MoveType
import ext.mods.gameserver.enums.actors.NpcSkillType
import ext.mods.gameserver.model.actor.Creature
import ext.mods.gameserver.model.actor.Npc
import ext.mods.gameserver.model.actor.ai.type.NpcAI
import ext.mods.gameserver.enums.skills.Stats
import ext.mods.gameserver.model.location.Location
import ext.mods.gameserver.network.serverpackets.StopMove
import ext.mods.gameserver.skills.L2Skill
import ext.mods.gameserver.skills.basefuncs.FuncMul
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.sqrt
class NpcMove(actor: Npc) : CreatureMove<Npc>(actor) {
    companion object {
        private val LOGGER = CLogger(NpcMove::class.java.name)
    }
    
    private val frontSlowOwner = Any()
    private var frontSlowApplied = false
    override fun offensiveFollowTask(target: Creature, offset: Int) {
        val currentTask = _followTask
        if (currentTask == null || currentTask.isCancelled || target.isAlikeDead) {
            clearFrontSlow()
            cancelFollowTask()
            return
        }
        
        if (!_actor.knows(target)) {
            val ai = _actor.ai
            if (ai is NpcAI<*>) {
                ai.stopFollow()
            } else {
                _actor.broadcastPacket(StopMove(_actor))
                ai.notifyEvent(AiEventType.THINK, null, null)
            }
            if (Config.DEBUG_MELEE_ATTACK && offset <= 200) {
                LOGGER.info("[MeleeDebug] follow: lostKnownlist npc={} target={}", _actor.objectId, target.objectId)
            }
            clearFrontSlow()
            cancelFollowTask()
            return
        }
        val targetLoc = target.position
        val dist = _actor.distance2D(targetLoc)
        
        if (applySoftRepulsion(target)) {
            return
        }
        
        if (offset <= 200 && dist <= 300 && !_actor.getAllSkillsDisabled() && !_actor.cast.isCastingNow) {
            val magicSkill = selectMagicSkill()
            if (magicSkill != null) {
                val mpConsume = _actor.status.getMpConsume(magicSkill)
                val hasMp = mpConsume <= 0 || mpConsume <= _actor.status.mp
                val chance = if (hasMp) 60 else 30
                if (Rnd.get(100) < chance) {
                    val ai = _actor.ai
                    if (ai is NpcAI<*>) {
                        ai.addCastDesire(target, magicSkill, 100000.0, true, true)
                        return
                    }
                }
            }
        }
        
        val layer = (_actor.objectId % 3)
        val layeredOffset = if (offset > 200) {
            (offset + (layer * 20)).coerceAtLeast(100)
        } else {
            offset
        }
        val totalRange = layeredOffset + _actor.collisionRadius + target.collisionRadius
        if (offset <= 100 && tryRouteDeviationForFrontBlocker(target, offset)) {
            return
        }
        
        updateFrontSlow(target, offset)
        
        if (dist <= totalRange) {
            _actor.position.setHeadingTo(targetLoc)
            
            if (offset <= 200) {
                if (Config.DEBUG_MELEE_ATTACK) {
                    LOGGER.info("[MeleeDebug] follow: meleeArrived npc={} target={} dist={} range={}", _actor.objectId, target.objectId, dist, totalRange)
                }
                clearFrontSlow()
                stop()
                return
            }
            
            if (abs(_separationForceX) < 0.1 && abs(_separationForceY) < 0.1) {
                stop()
                return
            }
        }
        if (dist > totalRange || wouldCollideInPath(targetLoc)) {
            val bestSlot = findBestAttackSlot(target, offset)
            
            if (bestSlot != null) {
                if (_destination.distance2D(bestSlot) > 25) {
                    moveToLocation(bestSlot, dist > 300)
                    return
                }
            }
        }
        if (dist > totalRange + 300) {
            moveToLocation(targetLoc, true)
            return
        } 
        
        super.offensiveFollowTask(target, offset)
    }
    
    fun maintainMeleeSpacing(target: Creature, offset: Int): Boolean {
        if (offset > 200 || _actor.isMovementDisabled) {
            return false
        }
        
        val targetLoc = target.position
        val dist = _actor.distance2D(targetLoc)
        val totalRange = offset + _actor.collisionRadius + target.collisionRadius
        if (dist > totalRange) {
            return false
        }
        
        val collisionGap = 60.0
        val neighbors = _actor.getKnownTypeInRadius(Creature::class.java, 150)
        val crowded = neighbors.any { other ->
            other != _actor && other != target && !other.isAlikeDead &&
                other.distance2D(_actor) < (other.collisionRadius + _actor.collisionRadius + collisionGap)
        }
        
        val nearest = neighbors
            .filter { other -> other != _actor && other != target && !other.isAlikeDead }
            .minByOrNull { other -> other.distance2D(_actor) }
        if (nearest != null) {
            val minDist = _actor.collisionRadius + nearest.collisionRadius + 20.0
            val curDist = nearest.distance2D(_actor)
            if (curDist > 0 && curDist < minDist) {
                val dx = (_actor.x - nearest.x).toDouble()
                val dy = (_actor.y - nearest.y).toDouble()
                val step = 20.0
                val nx = _actor.x + ((dx / curDist) * step)
                val ny = _actor.y + ((dy / curDist) * step)
                val nz = _actor.z
                moveToLocation(Location(nx.toInt(), ny.toInt(), nz), false)
                return true
            }
        }
        
        if (!crowded && !wouldCollideInPath(targetLoc)) {
            return false
        }
        
        val bestSlot = findBestAttackSlot(target, offset) ?: return false
        if (_destination.distance2D(bestSlot) > 25) {
            moveToLocation(bestSlot, false)
            return true
        }
        return false
    }
    private fun applySoftRepulsion(target: Creature): Boolean {
        val neighbors = _actor.getKnownTypeInRadius(Creature::class.java, 120)
        val nearest = neighbors
            .filter { other -> other != _actor && other != target && !other.isAlikeDead }
            .minByOrNull { other -> other.distance2D(_actor) } ?: return false
        
        val minDist = _actor.collisionRadius + nearest.collisionRadius + 15.0
        val curDist = nearest.distance2D(_actor)
        if (curDist > 0 && curDist < minDist) {
            val dx = (_actor.x - nearest.x).toDouble()
            val dy = (_actor.y - nearest.y).toDouble()
            val step = 15.0
            val nx = _actor.x + ((dx / curDist) * step)
            val ny = _actor.y + ((dy / curDist) * step)
            val nz = _actor.z
            moveToLocation(Location(nx.toInt(), ny.toInt(), nz), false)
            return true
        }
        return false
    }
    
    private fun updateFrontSlow(target: Creature, offset: Int) {
        if (offset > 100) {
            clearFrontSlow()
            return
        }
        
        val hasBlocker = hasFrontBlocker(target)
        if (hasBlocker && !frontSlowApplied) {
            _actor.addStatFunc(FuncMul(frontSlowOwner, Stats.RUN_SPEED, 0.9, null))
            frontSlowApplied = true
        } else if (!hasBlocker && frontSlowApplied) {
            clearFrontSlow()
        }
    }
    
    private fun clearFrontSlow() {
        if (frontSlowApplied) {
            _actor.removeStatsByOwner(frontSlowOwner)
            frontSlowApplied = false
        }
    }
    
    private fun tryRouteDeviationForFrontBlocker(target: Creature, offset: Int): Boolean {
        if (!hasFrontBlocker(target)) {
            return false
        }
        val bestSlot = findBestAttackSlot(target, offset) ?: return false
        if (_destination.distance2D(bestSlot) > 25) {
            moveToLocation(bestSlot, false)
            return true
        }
        return false
    }
    
    private fun hasFrontBlocker(target: Creature): Boolean {
        val tx = target.x - _actor.x
        val ty = target.y - _actor.y
        val tLen = sqrt((tx * tx + ty * ty).toDouble())
        if (tLen <= 0.1) return false
        
        val dirX = tx / tLen
        val dirY = ty / tLen
        
        return _actor.getKnownTypeInRadius(Creature::class.java, 120).any { other ->
            if (other == _actor || other == target || other.isAlikeDead) return@any false
            val ox = other.x - _actor.x
            val oy = other.y - _actor.y
            val oLen = sqrt((ox * ox + oy * oy).toDouble())
            if (oLen <= 1.0) return@any false
            val dot = (dirX * (ox / oLen)) + (dirY * (oy / oLen))
            val closerToTarget = other.distance2D(target) < _actor.distance2D(target)
            dot > 0.7 && closerToTarget
        }
    }
    
    private fun selectMagicSkill(): L2Skill? {
        val skills = _actor.template.getSkills(
            NpcSkillType.DD_MAGIC,
            NpcSkillType.DD_MAGIC1,
            NpcSkillType.DD_MAGIC2,
            NpcSkillType.DD_MAGIC3,
            NpcSkillType.DD_MAGIC_SLOW,
            NpcSkillType.LONG_RANGE_DD_MAGIC1,
            NpcSkillType.RANGE_DD,
            NpcSkillType.RANGE_DD_MAGIC1,
            NpcSkillType.RANGE_DD_MAGIC_A,
            NpcSkillType.W_LONG_RANGE_DD_MAGIC,
            NpcSkillType.W_LONG_RANGE_DD_MAGIC1,
            NpcSkillType.W_LONG_RANGE_DD_MAGIC2,
            NpcSkillType.W_MIDDLE_RANGE_DD_MAGIC,
            NpcSkillType.W_SHORT_RANGE_DD_MAGIC,
            NpcSkillType.SELF_RANGE_DD_MAGIC,
            NpcSkillType.SELF_RANGE_DD_MAGIC1,
            NpcSkillType.SELF_RANGE_DD_MAGIC2,
            NpcSkillType.SELF_RANGE_DD_MAGIC3
        )
        if (skills.isEmpty()) {
            return null
        }
        val filtered = skills.filter { skill -> skill != null && skill.isMagic && (skill.isOffensive || skill.isDebuff) }
        if (filtered.isEmpty()) {
            return null
        }
        return filtered[Rnd.get(filtered.size)]
    }
    
    private fun findBestAttackSlot(target: Creature, baseRange: Int): Location? {
        val centerX = target.x
        val centerY = target.y
        val centerZ = target.z
        
        val layer = (_actor.objectId % 3)
        val layeredRange = if (baseRange > 200) {
            (baseRange + (layer * 20)).coerceAtLeast(100)
        } else {
            baseRange
        }
        
        val angles = 12
        val angleStep = 360 / angles
        val startAngle = Math.toDegrees(Math.atan2((_actor.y - centerY).toDouble(), (_actor.x - centerX).toDouble())).toInt()
        
        var bestLoc: Location? = null
        var minScore = Double.MAX_VALUE
        for (i in 0 until angles) {
            val angle = Math.toRadians((startAngle + (i * angleStep)).toDouble())
            
            val jitter = (_actor.objectId % 4) * 5 
            val testX = (centerX + (layeredRange + jitter) * Math.cos(angle)).toInt()
            val testY = (centerY + (layeredRange + jitter) * Math.sin(angle)).toInt()
            val testZ = geoEngine.getHeight(testX, testY, centerZ).toInt()
            val testLoc = Location(testX, testY, testZ)
            if (!geoEngine.canMoveToTarget(_actor.x, _actor.y, _actor.z, testX, testY, testZ)) continue
            val collisionGap = 60.0
            val isOccupied = _actor.getKnownTypeInRadius(Creature::class.java, 150).any { other ->
                other != _actor && other != target && !other.isAlikeDead &&
                other.distance2D(testLoc) < (other.collisionRadius + _actor.collisionRadius + collisionGap)
            }
            
            if (isOccupied) continue
            val score = _actor.distance2D(testLoc)
            if (score < minScore) {
                minScore = score
                bestLoc = testLoc
            }
        }
        return bestLoc
    }
    
    private fun wouldCollideInPath(dest: Location): Boolean {
        val checkDist = _actor.collisionRadius * 2.2
        return _actor.getKnownTypeInRadius(Creature::class.java, checkDist.toInt()).any { neighbor ->
            if (neighbor == _actor || neighbor == _pawn || neighbor.isAlikeDead) return@any false
            
            _actor.distance2D(neighbor) < checkDist
        }
    }
    override fun handleNextPosition(nextX: Int, nextY: Int, nextZ: Int, type: MoveType): Boolean {
        if (super.handleNextPosition(nextX, nextY, nextZ, type)) return true
        
        val curX = _actor.x
        val curY = _actor.y
        val curZ = _actor.z
        
        if (geoEngine.canMoveToTarget(curX, curY, curZ, nextX, curY, nextZ)) {
            _actor.setXYZ(nextX, curY, nextZ)
            _actor.revalidateZone(false)
            return true
        }
        
        if (geoEngine.canMoveToTarget(curX, curY, curZ, curX, nextY, nextZ)) {
            _actor.setXYZ(curX, nextY, nextZ)
            _actor.revalidateZone(false)
            return true
        }
        
        _blocked = true
        return false
    }
}