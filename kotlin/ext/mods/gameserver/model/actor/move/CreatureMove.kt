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
import ext.mods.commons.config.ExProperties
import ext.mods.commons.logging.CLogger
import ext.mods.commons.pool.CoroutinePool
import ext.mods.gameserver.enums.AiEventType
import ext.mods.gameserver.enums.IntentionType
import ext.mods.gameserver.enums.actors.MoveType
import ext.mods.gameserver.geoengine.GeoEngine
import ext.mods.gameserver.geoengine.pathfinding.SmoothObstacleAvoidance
import ext.mods.gameserver.model.WorldObject
import ext.mods.gameserver.model.actor.Creature
import ext.mods.gameserver.model.actor.Npc
import ext.mods.gameserver.model.actor.Playable
import ext.mods.gameserver.model.actor.Player
import ext.mods.gameserver.model.location.Location
import ext.mods.gameserver.network.serverpackets.MoveToLocation
import ext.mods.gameserver.network.serverpackets.StopMove
import ext.mods.gameserver.network.serverpackets.ValidateLocation
import java.util.ArrayDeque
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ThreadLocalRandom
import kotlin.jvm.JvmField
import kotlin.math.abs
import kotlin.math.sqrt
private const val ENABLE_NPC_AVOIDANCE = true 
open class CreatureMove<T : Creature>(
    @JvmField protected val _actor: T
) {
    companion object {
        @JvmField protected val LOGGER = CLogger(CreatureMove::class.java.name)
        private const val MOVE_UPDATE_INTERVAL = 100L
        private const val FOLLOW_INTERVAL = 1000L
        private const val ATTACK_FOLLOW_INTERVAL = 100L 
        @JvmField val geoEngine: GeoEngine = GeoEngine.getInstance()
        private val GEOENGINE_PROPS: ExProperties = Config.initProperties(Config.GEOENGINE_FILE)
        private val MOVEMENT_UPDATE_INTERVAL = GEOENGINE_PROPS.getProperty("MovementUpdateInterval", 80L)
    }
    @JvmField protected var _pawn: WorldObject? = null
    @JvmField protected var _offset: Int = 0
    @JvmField protected var _blocked: Boolean = false
    @JvmField protected var _moveTypes: Byte = 0
    @JvmField protected val _destination: Location = Location(0, 0, 0)
    @JvmField protected var _xAccurate: Double = 0.0
    @JvmField protected var _yAccurate: Double = 0.0
    @JvmField protected var _zAccurate: Double = _actor.z.toDouble()
    @JvmField protected var _separationForceX: Double = 0.0
    @JvmField protected var _separationForceY: Double = 0.0
    @JvmField protected val _geoPath: ArrayDeque<Location> = ArrayDeque()
    
    private val _finalPathDestination: Location = Location(0, 0, 0)
    @JvmField protected var _task: ScheduledFuture<*>? = null
    @JvmField protected var _followTask: ScheduledFuture<*>? = null
    @JvmField protected var _geoPathFailCount: Int = 0
    @JvmField protected var _isDebugMove: Boolean = false
    @JvmField protected var _isDebugPath: Boolean = false
    private var _cachedDestinationZ: Int = Int.MIN_VALUE
    private var _cachedDestinationX: Int = Int.MIN_VALUE
    private var _cachedDestinationY: Int = Int.MIN_VALUE
    private var _currentSpeedRandomFactor: Double = 1.0
    private var _lastMoveRequestTime: Long = 0
    private var _lastArrivedEventTime: Long = 0
    private var _lastValidateLocationTime: Long = 0
    private var _pauseCheckTask: ScheduledFuture<*>? = null
    private var _pausedByNoPlayers: Boolean = false
    private var _pausedDestination: Location? = null
    fun getTask(): ScheduledFuture<*>? = _task
    fun getDestination(): Location = _destination
    
    fun describeMovementTo(player: Player) {
        player.sendPacket(MoveToLocation(_actor))
    }
    fun isDebugMove(): Boolean = _isDebugMove
    fun setDebugMove(debug: Boolean) { _isDebugMove = debug }
    fun isDebugPath(): Boolean = _isDebugPath
    fun setDebugPath(debug: Boolean) { _isDebugPath = debug }
    fun getGeoPathFailCount(): Int = _geoPathFailCount
    fun resetGeoPathFailCount() { _geoPathFailCount = 0 }
    fun addGeoPathFailCount() {
        if (++_geoPathFailCount > Config.MAX_GEOPATH_FAIL_COUNT) _geoPathFailCount = 0
    }
    private fun isMeleeDebug(): Boolean {
        return Config.DEBUG_MELEE_ATTACK &&
            _actor is Player &&
            _actor.getStatus().getPhysicalAttackRange() <= 80
    }
    protected fun syncPlayerLocation(force: Boolean = false) {
        val player = _actor as? Player ?: return
        val now = System.currentTimeMillis()
        if (force || now - _lastValidateLocationTime >= 500) {
            player.sendPacket(ValidateLocation(player))
            _lastValidateLocationTime = now
        }
    }
    fun getMoveType(): MoveType {
        return when {
            (_moveTypes.toInt() and MoveType.SWIM.mask) != 0 -> MoveType.SWIM
            (_moveTypes.toInt() and MoveType.FLY.mask) != 0 -> MoveType.FLY
            else -> MoveType.GROUND
        }
    }
    fun addMoveType(type: MoveType) {
        _moveTypes = (_moveTypes.toInt() or type.mask).toByte()
    }
    fun removeMoveType(type: MoveType) {
        _moveTypes = (_moveTypes.toInt() and type.mask.inv()).toByte()
    }
    fun findAlternativeAttackPosition(target: Creature, baseRange: Int): Location? {
        val centerX = target.x
        val centerY = target.y
        val centerZ = target.z
        
        val layer = (_actor.objectId % 3) 
        val layeredRange = (baseRange + (layer * 20)).coerceAtLeast(100)
        
        val totalAngles = 12
        val angleStep = 360 / totalAngles
        
        val startAngle = Math.toDegrees(Math.atan2((_actor.y - centerY).toDouble(), (_actor.x - centerX).toDouble())).toInt()
        
        var bestLoc: Location? = null
        var bestScore = Double.MAX_VALUE
    
        for (i in 0 until totalAngles) {
            val currentAngle = Math.toRadians((startAngle + (i * angleStep)).toDouble())
            
            val jitter = (_actor.objectId % 5) * 4
            val testX = (centerX + (layeredRange + jitter) * Math.cos(currentAngle)).toInt()
            val testY = (centerY + (layeredRange + jitter) * Math.sin(currentAngle)).toInt()
            val testZ = geoEngine.getHeight(testX, testY, centerZ).toInt()
            
            val testLoc = Location(testX, testY, testZ)
    
            if (!geoEngine.canMoveToTarget(_actor.x, _actor.y, _actor.z, testX, testY, testZ)) continue
    
            val collisionGap = 40.0
            val isTooCloseToOthers = _actor.getKnownTypeInRadius(Creature::class.java, layeredRange + 100).any { other ->
                other != _actor && other != target && !other.isAlikeDead &&
                other.distance2D(testLoc) < (other.collisionRadius + _actor.collisionRadius + collisionGap)
            }
            
            if (isTooCloseToOthers) continue
    
            val score = _actor.distance2D(testLoc)
            if (score < bestScore) {
                bestScore = score
                bestLoc = testLoc
            }
        }
        
        return bestLoc
    }
    protected open fun moveToLocation(destination: Location, pathfinding: Boolean) {
        if (isMeleeDebug()) {
            LOGGER.info(
                "[MeleeDebug][CreatureMove][moveToLocation] actor={} dest={} pathfinding={}",
                _actor.objectId, destination, pathfinding
            )
        }
        val now = System.currentTimeMillis()
    
        if (_pawn != null && _actor is Npc) {
            if (wouldCollideInPath(destination)) { 
                val altPos = findAlternativeAttackPosition(_pawn as Creature, _offset)
                if (altPos != null) {
                    _destination.set(altPos)
                } else {
                    _destination.set(destination)
                }
            } else {
                _destination.set(destination)
            }
        } else {
            _destination.set(destination)
        }
        _finalPathDestination.set(_destination)
    
        if (now - _lastMoveRequestTime < 100 && _destination.distance2D(destination) < 10) {
            return
        }
        
        _lastMoveRequestTime = now
        _xAccurate = _actor.x.toDouble()
        _yAccurate = _actor.y.toDouble()
        _zAccurate = _actor.z.toDouble()
        
        _currentSpeedRandomFactor = if (_actor is Npc) {
            1.0 + ThreadLocalRandom.current().nextDouble(-0.05, 0.05)
        } else 1.0
    
        _geoPath.clear()
        _blocked = false
    
        val dist3D = _actor.distance3D(_destination)
        if ((pathfinding || dist3D > 300) && Config.SISTEMA_PATHFINDING) {
            _finalPathDestination.set(_destination)
            val nextLoc = calculatePath(_actor.x, _actor.y, _actor.z, _destination.x, _destination.y, _destination.z)
            if (nextLoc != null) {
                _destination.set(nextLoc)
            }
        }
    
        _actor.position.setHeadingTo(_destination)
        registerMoveTask()
        _actor.broadcastPacket(MoveToLocation(_actor, _destination))
        if (_actor is Player) {
            syncPlayerLocation(_followTask != null)
        }
    }
    fun forceMoveToLocation(destination: Location, pathfinding: Boolean) {
        moveToLocation(destination, pathfinding)
    }
    
    private fun wouldCollideInPath(destination: Location): Boolean {
        val checkDist = _actor.collisionRadius * 2.2
        return _actor.getKnownTypeInRadius(Creature::class.java, checkDist.toInt()).any { neighbor ->
            neighbor != _actor && neighbor != _pawn && !neighbor.isAlikeDead && neighbor.distance2D(destination) < checkDist
        }
    }
    fun registerMoveTask() {
        if (_task != null) return
        _blocked = false
        val interval = if (_actor is Npc) MOVEMENT_UPDATE_INTERVAL else MOVE_UPDATE_INTERVAL
        _task = CoroutinePool.scheduleAtFixedRate({
            if (shouldStopMovementTask()) {
                finishMovement()
                return@scheduleAtFixedRate
            }
            if (updatePosition() && !moveToNextRoutePoint()) {
                finishMovement()
            }
        }, 0, interval)
    }
    open fun shouldStopMovementTask(): Boolean {
        if (_actor is Npc && !hasVisiblePlayers()) {
            if (!_pausedByNoPlayers) {
                _pausedByNoPlayers = true
                _pausedDestination = Location(_destination)
                startResumeCheckTask()
            }
            return true
        }
        return false
    }
    private fun finishMovement() {
        val wasMoving = _task != null
        cancelMoveTask()
        _actor.revalidateZone(true)
        _actor.broadcastPacket(StopMove(_actor))
        if (_blocked && _actor is Player) {
            syncPlayerLocation(true)
        }
        if (wasMoving) {
            notifyArrived(if (_blocked) AiEventType.ARRIVED_BLOCKED else AiEventType.ARRIVED)
            
            if (_actor is Npc && _actor.ai.currentIntention.type == IntentionType.ATTACK) {
                _actor.ai.notifyEvent(AiEventType.THINK, null, null)
            }
        }
    }
    private fun notifyArrived(event: AiEventType) {
        val now = System.currentTimeMillis()
        if (now - _lastArrivedEventTime < 200) return
        _lastArrivedEventTime = now
        
        if (_actor is Npc && event == AiEventType.ARRIVED) {
            val intention = _actor.ai.currentIntention
            if (intention.type == IntentionType.ATTACK) {
                val target = intention.finalTarget
                if (target != null && !target.isAlikeDead && _actor.knows(target)) {
                    CoroutinePool.schedule({
                        val currentIntention = _actor.ai.currentIntention
                        if (currentIntention.type == IntentionType.ATTACK) {
                            val currentTarget = currentIntention.finalTarget
                            if (currentTarget != null && !currentTarget.isAlikeDead && _actor.knows(currentTarget)) {
                                _actor.ai.notifyEvent(AiEventType.THINK, null, null)
                            }
                        }
                    }, 10)
                }
            }
        }
        
        _actor.ai.notifyEvent(event, null, null)
    }
    
    fun repositionAfterAttack(target: Creature) {
        if (_actor.isMovementDisabled || _actor.isParalyzed) return
        val dist = _actor.distance2D(target)
        
        val currentSkill = _actor.cast.currentSkill
        val baseAttackRange = if (currentSkill != null && currentSkill.castRange > 0) {
            currentSkill.castRange
        } else {
            _actor.status.physicalAttackRange
        }
        
        val collisionBuffer = _actor.collisionRadius + target.collisionRadius
        val currentLoc = _actor.position
        val tooCloseThreshold = 200.0
        if (dist < tooCloseThreshold) {
            val dx = _actor.x - target.x
            val dy = _actor.y - target.y
            val angle = Math.atan2(dy.toDouble(), dx.toDouble())
            val nextX = (target.x + 400 * Math.cos(angle)).toInt()
            val nextY = (target.y + 400 * Math.sin(angle)).toInt()
            val nextZ = geoEngine.getHeight(nextX, nextY, currentLoc.z).toInt()
            
            val escapeDest = Location(nextX, nextY, nextZ)
            if (geoEngine.canMoveToTarget(currentLoc.x, currentLoc.y, currentLoc.z, nextX, nextY, nextZ)) {
                if (!wouldCollideWithCreature(nextX, nextY)) {
                    moveToLocation(escapeDest, false)
                    return 
                }
            }
        }
        if (dist > (baseAttackRange + collisionBuffer + 20)) {
            return
        }
        val angleOffset = if (ThreadLocalRandom.current().nextBoolean()) 45.0 else -45.0
        val currentAngle = Math.toRadians(_actor.position.heading.toDouble() * (360.0 / 65535.0))
        val newAngle = currentAngle + Math.toRadians(angleOffset)
        val latX = (currentLoc.x + 150 * Math.sin(newAngle)).toInt()
        val latY = (currentLoc.y + 150 * Math.cos(newAngle)).toInt()
        val latZ = geoEngine.getHeight(latX, latY, currentLoc.z).toInt()
        val lateralDest = Location(latX, latY, latZ)
        if (geoEngine.canMoveToTarget(currentLoc.x, currentLoc.y, currentLoc.z, latX, latY, latZ)) {
            if (!wouldCollideWithCreature(latX, latY)) {
                moveToLocation(lateralDest, false)
            }
        }
    }
    private fun moveToNextRoutePoint(): Boolean {
        if (_geoPath.isEmpty()) return false
        var next: Location? = _geoPath.poll() ?: return false
        while (Config.SISTEMA_PATHFINDING && _actor is Playable && next != null &&
            !geoEngine.canMoveToTarget(_actor.x, _actor.y, _actor.z, next.x, next.y, next.z)) {
            next = _geoPath.poll()
        }
        if (next == null) return false
        _destination.set(next)
        _xAccurate = _actor.x.toDouble()
        _yAccurate = _actor.y.toDouble()
        _actor.position.setHeadingTo(next)
        _actor.broadcastPacket(MoveToLocation(_actor, next))
        return true
    }
    open fun updatePosition(): Boolean {
        if (_task == null || !_actor.isVisible) return true
        
        val type = getMoveType()
        val curZ = _actor.z
        
        if (type == MoveType.GROUND && (_cachedDestinationX != _destination.x || _cachedDestinationY != _destination.y)) {
            _cachedDestinationX = _destination.x
            _cachedDestinationY = _destination.y
            _cachedDestinationZ = geoEngine.getHeight(_destination.x, _destination.y, _destination.z).toInt()
            _destination.setZ(_cachedDestinationZ)
        }
    
        val moveSpeed = (_actor.status.moveSpeed / 10.0) * _currentSpeedRandomFactor
        val dx = _destination.x - _xAccurate
        val dy = _destination.y - _yAccurate
        val distSq = dx * dx + dy * dy
    
        if (_actor is Npc && type == MoveType.GROUND) {
            calculateRepulsion()
        }
    
        if (moveSpeed * moveSpeed >= distSq) {
            val finalZ = if (type == MoveType.GROUND) _cachedDestinationZ else _destination.z
            if (handleNextPosition(_destination.x, _destination.y, finalZ, type)) {
                return checkArrival(type)
            }
        }
    
        val dist = sqrt(distSq)
        val fraction = moveSpeed / dist
        val moveX = dx * fraction
        val moveY = dy * fraction
        
        val nextXAccurate = _xAccurate + moveX + _separationForceX
        val nextYAccurate = _yAccurate + moveY + _separationForceY
        val nextX = nextXAccurate.toInt()
        val nextY = nextYAccurate.toInt()
        val nextZ = if (type == MoveType.GROUND) geoEngine.getHeight(nextX, nextY, curZ).toInt() else _actor.z
    
        if (ENABLE_NPC_AVOIDANCE && _actor is Npc && type == MoveType.GROUND) {
            val hasRepulsion = abs(_separationForceX) > 0.1 || abs(_separationForceY) > 0.1
            if (!hasRepulsion && wouldCollideWithCreature(nextX, nextY) && dist > 100) {
                _blocked = true
                return true 
            }
        }
        
        if (handleNextPosition(nextX, nextY, nextZ, type)) {
            _xAccurate = nextXAccurate
            _yAccurate = nextYAccurate
            
            val now = System.currentTimeMillis()
            val timeSinceLastSync = now - _lastMoveRequestTime
            
            val driftSq: Double = (_separationForceX * _separationForceX) + (_separationForceY * _separationForceY)
            val shouldSync = (driftSq > 225.0 && timeSinceLastSync > 200L) || (timeSinceLastSync > 500L)
            
            if (shouldSync) {
                _lastMoveRequestTime = now
                _actor.position.setHeadingTo(_destination) 
                _actor.broadcastPacket(MoveToLocation(_actor, _destination))
            }
    
            return false
        }
    
        if (tryRecalculatePathWithoutRetreat()) {
            return false
        }
        _blocked = true
        return true
    }
    private fun calculateRepulsion() {
        _separationForceX = 0.0
        _separationForceY = 0.0
        
        val checkRadius = (_actor.collisionRadius * 3.0).toInt().coerceAtLeast(80)
        val minSeparationDistance = _actor.collisionRadius * 2.2
        
        _actor.forEachKnownTypeInRadius(Creature::class.java, checkRadius) { neighbor ->
            if (neighbor == _actor || neighbor.isAlikeDead) return@forEachKnownTypeInRadius
            
            val isTarget = neighbor == _pawn
            val maxForce = if (isTarget) 0.8 else 5.0
            
            val dx = _actor.x - neighbor.x.toDouble()
            val dy = _actor.y - neighbor.y.toDouble()
            val distSq = dx * dx + dy * dy
            
            if (distSq < checkRadius * checkRadius && distSq > 0.1) {
                val dist = sqrt(distSq)
                val combinedRadius = _actor.collisionRadius + neighbor.collisionRadius
                
                if (dist < combinedRadius + 10) {
                    val overlap = (combinedRadius + 10) - dist
                    val force = (overlap / (combinedRadius + 10)) * maxForce
                    _separationForceX += (dx / dist) * force
                    _separationForceY += (dy / dist) * force
                } 
                else if (dist < minSeparationDistance) {
                    val normalizedDist = (minSeparationDistance - dist) / minSeparationDistance
                    val force = normalizedDist * normalizedDist * maxForce * 0.4
                    _separationForceX += (dx / dist) * force
                    _separationForceY += (dy / dist) * force
                }
            }
        }
        
        val totalForce = sqrt(_separationForceX * _separationForceX + _separationForceY * _separationForceY)
        val maxTotalForce = 8.0
        if (totalForce > maxTotalForce) {
            val scale = maxTotalForce / totalForce
            _separationForceX *= scale
            _separationForceY *= scale
        }
    }
    protected open fun handleNextPosition(nextX: Int, nextY: Int, nextZ: Int, type: MoveType): Boolean {
        if (!geoEngine.canMoveToTarget(_actor.x, _actor.y, _actor.z, nextX, nextY, nextZ)) {
            if (_actor is Player) {
                syncPlayerLocation(true)
            }
            return false
        }
        _actor.setXYZ(nextX, nextY, nextZ)
        _actor.revalidateZone(false)
        return true
    }
    open fun calculatePath(ox: Int, oy: Int, oz: Int, tx: Int, ty: Int, tz: Int): Location? {
        if (!Config.SISTEMA_PATHFINDING) return null
        if (geoEngine.canMoveToTarget(ox, oy, oz, tx, ty, tz)) return null
        
        if (isMeleeDebug()) {
            LOGGER.info(
                "[MeleeDebug][CreatureMove][calculatePath] actor={} from=({}, {}, {}) to=({}, {}, {})",
                _actor.objectId, ox, oy, oz, tx, ty, tz
            )
        }
        val path = geoEngine.findPath(ox, oy, oz, tx, ty, tz, _actor is Playable, null)
        if (path.size < 2) {
            addGeoPathFailCount()
            return null
        }
        resetGeoPathFailCount()
        _geoPath.addAll(path)
        return _geoPath.poll()
    }
    fun maybeStartOffensiveFollow(target: Creature, range: Int): Boolean {
        if (range < 0) return false
        
        if (isMeleeDebug()) {
            LOGGER.info(
                "[MeleeDebug][CreatureMove][maybeStartOffensiveFollow] actor={} target={} range={} dist={}",
                _actor.objectId, target.objectId, range, _actor.distance2D(target)
            )
        }
        
        val collision = _actor.collisionRadius + target.collisionRadius
        val totalRange = range + collision
        val dist = _actor.distance2D(target)
        
        if (dist <= totalRange) {
            if (_actor.isMoving || _followTask != null) {
                stop()
            }
            return false
        }
        if (_pawn == target && _followTask != null) {
             return true
        }
        
        if (!_actor.isMovementDisabled && _actor.ai.currentIntention.canMoveToTarget()) {
            val safetyMargin = if (range > 200) 0.8 else 0.9
            val targetOffset = (range * safetyMargin).toInt().coerceAtLeast(20)
            startOffensiveFollow(target, targetOffset)
            return true
        }
        return false
    }
    fun startOffensiveFollow(pawn: Creature, offset: Int) {
        if (_pawn == pawn && _followTask != null && !(_followTask!!.isDone || _followTask!!.isCancelled)) {
            _offset = offset
            return
        }
        
        if (isMeleeDebug()) {
            LOGGER.info(
                "[MeleeDebug][CreatureMove][startOffensiveFollow] actor={} target={} offset={}",
                _actor.objectId, pawn.objectId, offset
            )
        }
        
        cancelFollowTask()
        _pawn = pawn
        _offset = offset
        
        _followTask = CoroutinePool.scheduleAtFixedRate({ 
            offensiveFollowTask(pawn, offset) 
        }, 0, ATTACK_FOLLOW_INTERVAL)
    }
    protected open fun offensiveFollowTask(target: Creature, offset: Int) {
        val currentTask = _followTask
        if (currentTask == null || currentTask.isCancelled || target.isAlikeDead || !_actor.knows(target)) {
            cancelFollowTask()
            stop()
            return
        }
        val dist = _actor.distance2D(target)
        val range = offset + _actor.collisionRadius + target.collisionRadius
        
        if (isMeleeDebug()) {
            LOGGER.info(
                "[MeleeDebug][CreatureMove][offensiveFollowTask] actor={} target={} dist={} range={}",
                _actor.objectId, target.objectId, dist, range
            )
        }
        
        if (dist <= range) {
            stop()
            return
        }
        val targetPos = target.position
        if (!_actor.isMoving || _destination.distance2D(targetPos) > 32) {
            val movePos = if (_actor is Player && Config.SISTEMA_PATHFINDING && !geoEngine.canMoveToTarget(_actor.position, targetPos)) {
                geoEngine.getValidLocation(_actor.position, targetPos)
            } else {
                targetPos
            }
            moveToLocation(movePos, dist > 300)
            if (_actor is Player && movePos != targetPos) {
                syncPlayerLocation(true)
            }
        }
    }
    fun startFriendlyFollow(pawn: Creature, offset: Int) {
        if (_pawn == pawn && _followTask != null && !(_followTask!!.isDone || _followTask!!.isCancelled)) {
            return
        }
        cancelFollowTask()
        _pawn = pawn
        _offset = offset
        _followTask = CoroutinePool.scheduleAtFixedRate({ friendlyFollowTask(pawn, offset) }, 0, FOLLOW_INTERVAL)
    }
    protected open fun friendlyFollowTask(target: Creature, offset: Int) {
        if (_followTask == null || !_actor.knows(target)) {
            cancelFollowTask()
            return
        }
        val dist = _actor.distance2D(target)
        val range = offset + _actor.collisionRadius + target.collisionRadius
        if (dist <= range) return
        
        if (!_actor.isMoving || _destination.distance2D(target.position) > 64) {
            moveToLocation(target.position, true)
        }
    }
    open fun stop() {
        val wasFollowing = _followTask != null
        val wasMoving = _task != null
        
        cancelFollowTask()
        if (wasMoving) {
            cancelMoveTask()
            _actor.broadcastPacket(StopMove(_actor))
            _actor.revalidateZone(true)
            notifyArrived(AiEventType.ARRIVED)
        } else if (wasFollowing) {
            notifyArrived(AiEventType.ARRIVED)
        }
    }
    open fun cancelMoveTask() {
        _task?.cancel(true)
        _task = null
    }
    fun cancelFollowTask() {
        _followTask?.cancel(false)
        _followTask = null
        _pawn = null
    }
    fun isOnLastPawnMoveGeoPath(): Boolean = _geoPath.isEmpty() && _pawn is Creature
    open fun avoidAttack(attacker: Creature) {}
    fun maybeStartFriendlyFollow(target: Creature, range: Int): Boolean {
        if (_actor.isMovementDisabled) return false
        startFriendlyFollow(target, range)
        return true
    }
    fun maybeMoveToLocation(destination: Location, offset: Int, pathfinding: Boolean, isShiftPressed: Boolean): Boolean {
        if (_actor.isIn3DRadius(destination, offset)) return false
        if (_actor.isMovementDisabled || isShiftPressed) return true
        _pawn = null
        _offset = 0
        moveToLocation(destination, pathfinding)
        
        return true
    }
    private fun checkArrival(type: MoveType): Boolean {
        val pawn = _pawn
        return if (pawn is Creature) {
            val range = _offset + _actor.collisionRadius + pawn.collisionRadius
            _actor.isIn2DRadius(pawn, range.toInt() + 10)
        } else {
            _geoPath.isEmpty() && abs(_destination.x - _actor.x) <= 10 && abs(_destination.y - _actor.y) <= 10
        }
    }
    
    private fun tryRecalculatePathWithoutRetreat(): Boolean {
        if (!Config.SISTEMA_PATHFINDING) return false
        val finalTx = _geoPath.lastOrNull()?.let { it.x } ?: _finalPathDestination.x
        val finalTy = _geoPath.lastOrNull()?.let { it.y } ?: _finalPathDestination.y
        val finalTz = _geoPath.lastOrNull()?.let { it.z } ?: _finalPathDestination.z
        val curX = _actor.x
        val curY = _actor.y
        val curZ = _actor.z
        if (curX == finalTx && curY == finalTy) return false
        val path = geoEngine.findPath(curX, curY, curZ, finalTx, finalTy, finalTz, _actor is Playable, null)
        if (path.size < 2) return false
        val dxToGoal = (finalTx - curX).toDouble()
        val dyToGoal = (finalTy - curY).toDouble()
        val nextForward = path.firstOrNull { loc ->
            val dxToPoint = (loc.x - curX).toDouble()
            val dyToPoint = (loc.y - curY).toDouble()
            val dotProduct = dxToPoint * dxToGoal + dyToPoint * dyToGoal
            dotProduct > 0
        } ?: path.first()
        _geoPath.clear()
        val idx = path.indexOf(nextForward)
        if (idx >= 0) {
            for (i in (idx + 1) until path.size) _geoPath.add(path[i])
        }
        _destination.set(nextForward)
        _xAccurate = curX.toDouble()
        _yAccurate = curY.toDouble()
        _actor.position.setHeadingTo(nextForward)
        _actor.broadcastPacket(MoveToLocation(_actor, nextForward))
        return true
    }
    private fun wouldCollideWithCreature(targetX: Int, targetY: Int): Boolean {
        if (_actor !is Npc || !ENABLE_NPC_AVOIDANCE) return false
        val radius = _actor.collisionRadius.toInt()
        return _actor.getKnownTypeInRadius(Creature::class.java, radius + 15).any { neighbor ->
            if (neighbor == _actor || neighbor.isAlikeDead || neighbor == _pawn) return@any false
            val dx = targetX - neighbor.x
            val dy = targetY - neighbor.y
            val minDist = radius + neighbor.collisionRadius + 2
            (dx * dx + dy * dy) < (minDist * minDist)
        }
    }
    private fun hasVisiblePlayers(): Boolean {
        if (Config.NPC_MOVEMENT_PLAYER_RANGE <= 0) return true
        return _actor.getKnownTypeInRadius(Player::class.java, Config.NPC_MOVEMENT_PLAYER_RANGE)
            .any { it.appearance.isVisible }
    }
    private fun startResumeCheckTask() {
        if (_pauseCheckTask != null) return
        _pauseCheckTask = CoroutinePool.scheduleAtFixedRate({
            if (hasVisiblePlayers()) {
                _pauseCheckTask?.cancel(false)
                _pauseCheckTask = null
                _pausedByNoPlayers = false
                val resumeDest = _pausedDestination
                _pausedDestination = null
                if (resumeDest != null) {
                    _destination.set(resumeDest)
                    registerMoveTask()
                    _actor.broadcastPacket(MoveToLocation(_actor, _destination))
                }
            }
        }, 1000, 1000)
    }
}