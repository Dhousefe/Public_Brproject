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
import ext.mods.commons.pool.CoroutinePool
import ext.mods.gameserver.data.manager.ZoneManager
import ext.mods.gameserver.enums.AiEventType
import ext.mods.gameserver.enums.actors.MoveType
import ext.mods.gameserver.geoengine.GeoEngine
import ext.mods.gameserver.geoengine.geodata.GeoStructure
import ext.mods.gameserver.geoengine.pathfinding.SmoothObstacleAvoidance
import ext.mods.gameserver.model.World
import ext.mods.gameserver.model.WorldObject
import ext.mods.gameserver.model.actor.Boat
import ext.mods.gameserver.model.actor.Creature
import ext.mods.gameserver.model.actor.Npc
import ext.mods.gameserver.model.actor.Player
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager
import ext.mods.gameserver.model.location.Location
import ext.mods.gameserver.model.zone.type.WaterZone
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive
import ext.mods.gameserver.network.serverpackets.MoveToLocation
import ext.mods.gameserver.network.serverpackets.MoveToPawn
import java.awt.Color
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ScheduledFuture
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sqrt
class PlayerMove(actor: Player) : CreatureMove<Player>(actor) {
    
    companion object {
    }
    
    @Volatile
    private var _instant: Instant = Instant.now()
    
    private var _moveTimeStamp: Int = 0
    private var _lastStuckCheckTime: Long = 0
    private var _lastStuckX: Int = Int.MIN_VALUE
    private var _lastStuckY: Int = Int.MIN_VALUE
    private var _lastStuckDist: Double = Double.MAX_VALUE
    
    init {
        _zAccurate = actor.getZ().toDouble()
    }
    
    override fun cancelMoveTask() {
        super.cancelMoveTask()
        _moveTimeStamp = 0
    }
    
    private fun isMeleeDebug(): Boolean {
        return Config.DEBUG_MELEE_ATTACK && _actor.getStatus().getPhysicalAttackRange() <= 80
    }
    
    private fun moveToPawn(pawn: WorldObject, offset: Int) {
        val tx = pawn.getX()
        val ty = pawn.getY()
        val tz = pawn.getZ()
        
        if (isMeleeDebug()) {
            LOGGER.info("[MeleeDebug][PlayerMove][moveToPawn] actor={} pawn={} offset={} dest=({}, {}, {})",
                _actor.getObjectId(), pawn.getObjectId(), offset, tx, ty, tz)
        }
        
        _pawn = pawn
        _offset = offset
        
        if (_task != null) {
            updatePosition()
        }
        
        _instant = Instant.now()
        
        val ox = _actor.getX()
        val oy = _actor.getY()
        val oz = _actor.getZ()
        
        _xAccurate = ox.toDouble()
        _yAccurate = oy.toDouble()
        _zAccurate = oz.toDouble()
        
        if (Config.SISTEMA_PATHFINDING) {
            val pathStart = calculatePath(ox, oy, oz, tx, ty, tz)
            if (pathStart != null) {
                _destination.set(pathStart)
                _actor.getPosition().setHeadingTo(_destination)
                registerMoveTask()
                _actor.broadcastPacket(MoveToPawn(_actor, pawn, offset))
                syncPlayerLocation(true)
                return
            }
        }
        
        _geoPath.clear()
        
        if (_isDebugMove) {
            _actor.forEachKnownGM { p ->
                val debug = p.getDebugPacket("MOVE${_actor.getObjectId()}")
                debug.reset()
                debug.addPoint(Color.RED, ox, oy, oz)
                debug.addLine("MoveToPawn ($offset): $tx $ty $tz", Color.WHITE, true, ox, oy, oz, tx, ty, tz)
                p.sendMessage("Moving from $ox $oy $oz to $tx $ty $tz")
            }
        }
        
        _destination.set(tx, ty, tz)
        _actor.getPosition().setHeadingTo(tx, ty)
        registerMoveTask()
        _actor.broadcastPacket(MoveToPawn(_actor, pawn, offset))
        syncPlayerLocation(true)
    }
    
    override fun moveToLocation(destination: Location, pathfinding: Boolean) {
        if (isMeleeDebug()) {
            LOGGER.info("[MeleeDebug][PlayerMove][moveToLocation] actor={} dest={} pathfinding={}",
                _actor.getObjectId(), destination, pathfinding)
        }
        if (AutoFarmManager.getInstance().isRouteFarmActive(_actor.getObjectId())) {
            handleAutoFarmMovement(destination)
            return
        }
        
        if (_task != null) {
            updatePosition()
        }
        
        _instant = Instant.now()
        
        val position = _actor.getPosition().clone()
        
        _xAccurate = position.getX().toDouble()
        _yAccurate = position.getY().toDouble()
        _zAccurate = position.getZ().toDouble()
        
        _geoPath.clear()
        
        if (pathfinding && Config.SISTEMA_PATHFINDING) {
            val loc = calculatePath(position.getX(), position.getY(), position.getZ(),
                destination.getX(), destination.getY(), destination.getZ())
            loc?.let { destination.set(it) }
        }
        
        if (_isDebugMove) {
            _actor.forEachKnownGM { p ->
                val debug = p.getDebugPacket("MOVE${_actor.getObjectId()}")
                debug.reset()
                debug.addLine("MoveToLocation: ${destination}", Color.WHITE, true, position, destination)
                
                val boat = _actor.getDockedBoat()
                boat?.let {
                    debug.addLine("Boat Entrance", Color.WHITE, true, it.getEngine().getDock().getBoatEntrance(), -3624)
                    debug.addLine("Boat Exit", Color.WHITE, true, it.getEngine().getDock().getBoatExit(), -3624)
                }
                
                debug.addPoint(Color.RED, position)
                
                if (_geoPath.isNotEmpty()) {
                    debug.addLine("Segment #1", Color.YELLOW, true, position, destination)
                    
                    val curPos = Location(destination)
                    var i = 2
                    
                    for (geoPos in _geoPath) {
                        debug.addLine("Segment #$i", Color.YELLOW, true, curPos, geoPos)
                        curPos.set(geoPos)
                        i++
                    }
                } else {
                    debug.addLine("No geopath", Color.YELLOW, true, position, destination)
                }
                
                p.sendMessage("Moving from ${position} to ${destination}")
            }
        }
        
        _destination.set(destination)
        _actor.getPosition().setHeadingTo(destination)
        registerMoveTask()
        _actor.broadcastPacket(MoveToLocation(_actor, destination))
    }
    
    private fun handleAutoFarmMovement(destination: Location) {
        val currentPos = _actor.getPosition().clone()
        val distance = currentPos.distance3D(destination)
        
        val stepSize = Config.initProperties(Config.GEOENGINE_FILE).getProperty("MovementStepSize", 14)
        val nextDestination = if (Config.SISTEMA_PATHFINDING) {
            val path = GeoEngine.getInstance().findPath(
                currentPos.getX(), currentPos.getY(), currentPos.getZ(),
                destination.getX(), destination.getY(), destination.getZ(),
                true, null
            )
            if (path.isNotEmpty()) path[0] else destination
        } else {
            destination
        }
        
        if (distance < stepSize * 2) {
            _actor.setXYZ(destination.getX(), destination.getY(), destination.getZ())
            _actor.revalidateZone(false)
            _actor.broadcastPacket(MoveToLocation(_actor, destination))
            return
        }
        
        startAutoFarmMove(nextDestination)
    }
    private fun startAutoFarmMove(destination: Location) {
        if (_task != null) {
            updatePosition()
        }
        
        _instant = Instant.now()
        
        val position = _actor.getPosition().clone()
        _xAccurate = position.getX().toDouble()
        _yAccurate = position.getY().toDouble()
        _zAccurate = position.getZ().toDouble()
        
        _geoPath.clear()
        _destination.set(destination)
        _actor.getPosition().setHeadingTo(destination)
        registerMoveTask()
        _actor.broadcastPacket(MoveToLocation(_actor, destination))
    }
    
    override fun updatePosition(): Boolean {
        if (_task == null || !_actor.isVisible()) {
            return true
        }
        
        if (_pawn != null && !_actor.knows(_pawn)) {
            return true
        }
        
        val instant = Instant.now()
        
        var timePassed = Duration.between(_instant, instant).toMillis()
        if (timePassed == 0L) {
            timePassed = 1L
        }
        
        _instant = instant
        
        val type = getMoveType()
        val canBypassZCheck = _actor.getBoatInfo().getBoat() != null || type == MoveType.FLY || type == MoveType.SWIM
        
        _moveTimeStamp++
        
        val curX = _actor.getX()
        val curY = _actor.getY()
        val curZ = _actor.getZ()
        
        if (_pawn != null) {
            _destination.set(_pawn!!.getPosition())
        }
        
        val isTargetInWater = ZoneManager.getInstance().getZone(_destination.getX(), _destination.getY(), _destination.getZ(), WaterZone::class.java) != null
        if (type == MoveType.GROUND && !isTargetInWater) {
            _destination.setZ(GeoEngine.getInstance().getHeight(_destination).toInt())
        }
        
        val dx = (_destination.getX() - curX).toDouble()
        val dy = (_destination.getY() - curY).toDouble()
        val dz = (_destination.getZ() - curZ).toDouble()
        
        val leftDistance = if (type == MoveType.GROUND) {
            sqrt(dx * dx + dy * dy)
        } else {
            sqrt(dx * dx + dy * dy + dz * dz)
        }
        
        val passedDistance = _actor.getStatus().getRealMoveSpeed(type != MoveType.FLY && _moveTimeStamp <= 2) / (1000.0 / timePassed)
        
        var maxZ = World.WORLD_Z_MAX
        if (canBypassZCheck) {
            val waterZone = ZoneManager.getInstance().getZone(curX, curY, curZ, WaterZone::class.java)
            if (waterZone != null && GeoEngine.getInstance().getHeight(curX, curY, curZ) - waterZone.getWaterZ() < -20) {
                maxZ = waterZone.getWaterZ()
            }
        }
        
        val nextX: Int
        val nextY: Int
        val nextZ: Int
        
        if (passedDistance < leftDistance) {
            val fraction = passedDistance / leftDistance
            
            _xAccurate += dx * fraction
            _yAccurate += dy * fraction
            _zAccurate += dz * fraction
            
            nextX = round(_xAccurate).toInt()
            nextY = round(_yAccurate).toInt()
            
            var isNearGroundUnderWater = false
            if (_actor.isInWater()) {
                val waterZone = ZoneManager.getInstance().getZone(curX, curY, curZ, WaterZone::class.java)
                waterZone?.let {
                    val waterZDiff = it.getWaterZ() - GeoEngine.getInstance().getHeight(nextX, nextY, curZ)
                    if (waterZDiff <= 2 * GeoStructure.CELL_HEIGHT && waterZDiff > -64) {
                        isNearGroundUnderWater = true
                    } else {
                        maxZ = it.getWaterZ()
                    }
                }
            }
            
            val shouldCheckForGround = type == MoveType.GROUND || (type == MoveType.SWIM && isNearGroundUnderWater)
            nextZ = min(
                if (shouldCheckForGround) {
                    GeoEngine.getInstance().getHeight(nextX, nextY, curZ + 2 * GeoStructure.CELL_HEIGHT).toInt()
                } else {
                    round(_zAccurate).toInt()
                },
                maxZ
            )
        } else {
            nextX = _destination.getX()
            nextY = _destination.getY()
            nextZ = min(_destination.getZ(), maxZ)
        }
        
        if (!validateMovement(nextX, nextY, nextZ)) {
            _blocked = true
            syncPlayerLocation(true)
            return true
        }
        
        if (_actor.isInWater() || type == MoveType.FLY) {
            val raycasted = GeoEngine.getInstance().raycast(curX, curY, curZ + 2 * GeoStructure.CELL_HEIGHT, nextX, nextY, nextZ, null)
            if (raycasted != null && raycasted.distance3D(nextX, nextY, nextZ) > 0) {
                _blocked = true
                syncPlayerLocation(true)
                return true
            }
        } else if (type == MoveType.GROUND && !GeoEngine.getInstance().canMoveToTarget(curX, curY, curZ, nextX, nextY, nextZ) && !_actor.temporaryFixPagan()) {
            _blocked = true
            syncPlayerLocation(true)
            return true
        }
        
        if (_pawn != null) {
            _actor.getPosition().setHeadingTo(nextX, nextY)
        }
        
        _actor.setXYZ(nextX, nextY, nextZ)
        
        if (_isDebugMove) {
            val heading = _actor.getHeading().toString()
            
            _actor.forEachKnownGM { p ->
                val debug = p.getDebugPacket("MOVE${_actor.getObjectId()}")
                debug.addPoint(heading, Color.RED, true, _actor.getPosition())
                debug.sendTo(p)
                
                if (type == MoveType.GROUND && abs(curZ - _actor.getPosition().getZ()) > 100) {
                    p.sendMessage("Falling/Climb bug found when moving from $curX, $curY, $curZ to ${_actor.getPosition()}")
                }
            }
        }
        
        _actor.revalidateZone(false)
        
        if (isOnLastPawnMoveGeoPath()) {
            val inRange = if (type == MoveType.GROUND) {
                _actor.isIn2DRadius(_pawn!!, _offset)
            } else {
                _actor.isIn3DRadius(_pawn!!, _offset)
            }
            if (inRange) {
                return true
            }
        }
        
        if (_geoPath.isNotEmpty() && (passedDistance - leftDistance) >= -(GeoStructure.CELL_SIZE / 2)) {
            _actor.setXYZ(_destination.getX(), _destination.getY(), _destination.getZ())
            return true
        }
        
        return passedDistance >= leftDistance
    }
    
    private fun calculatePathForAttack(ox: Int, oy: Int, oz: Int, tx: Int, ty: Int, tz: Int): Location? {
        val geoEngine = GeoEngine.getInstance()
        
        if (geoEngine.canMoveToTarget(ox, oy, oz, tx, ty, tz)) {
            return null
        }
        
        val gox = GeoEngine.getGeoX(ox)
        val goy = GeoEngine.getGeoY(oy)
        val goz = geoEngine.getHeightNearest(gox, goy, oz).toInt()
        
        val gtx = GeoEngine.getGeoX(tx)
        val gty = GeoEngine.getGeoY(ty)
        val gtz = geoEngine.getHeightNearest(gtx, gty, tz).toInt()
        
        val pathFinder = ext.mods.gameserver.geoengine.pathfinding.PathFinder()
        val dummy = if (_isDebugPath) ExServerPrimitive() else null
        val computePath: () -> List<Location> = {
            var p = pathFinder.findPath(_actor, gox, goy, goz, gtx, gty, gtz, dummy)
            if (Config.ENABLE_SMOOTH_OBSTACLE_AVOIDANCE && p.size >= 3) {
                p = SmoothObstacleAvoidance.getInstance().createSmoothPath(p, dummy)
            }
            p
        }
        var path = if (Config.USE_PATHFINDING_POOL) {
            ext.mods.commons.pool.CoroutinePool.runPathfindingBlocking(computePath)
        } else {
            computePath()
        }
        
        if (_isDebugPath && dummy != null) {
            _actor.forEachKnownGM { p ->
                val debug = p.getDebugPacket("PATH${_actor.getObjectId()}")
                debug.reset()
                debug.addAll(dummy)
                debug.sendTo(p)
            }
        }
        
        return processPathForAttack(path, geoEngine)
    }
    
    private fun processPathForAttack(path: List<Location>?, geoEngine: GeoEngine): Location? {
        if (path == null || path.isEmpty()) {
            val valid = geoEngine.getValidLocation(
                _actor.getX(), _actor.getY(), _actor.getZ(),
                _destination.getX(), _destination.getY(), _destination.getZ(), null
            )
            if (valid.getX() == _actor.getX() && valid.getY() == _actor.getY()) return null
            return valid
        }
        
        _geoPathFailCount = 0
        _geoPath.clear()
        _geoPath.addAll(path)
        return _geoPath.poll()
    }
    
    private fun validateMovement(nextX: Int, nextY: Int, nextZ: Int): Boolean {
        if (_actor.isStunned() || _actor.isRooted()) {
            return false
        }
        
        val currentPos = _actor.getPosition()
        
        val maxMovePerTick = _actor.getStatus().getRealMoveSpeed(false) * 0.2
        val actualMove = currentPos.distance2D(nextX, nextY)
        
        if (actualMove > maxMovePerTick * 2) {
            if (Config.DEVELOPER) {
                LOGGER.warn("Player {} tentou mover {} unidades em um tick (max: {})",
                    _actor.getName(), actualMove, maxMovePerTick)
            }
            return false
        }
        
        return true
    }
    
    fun maybeMoveToPawn(target: WorldObject, offset: Int, isShiftPressed: Boolean): Boolean {
        if (offset < 0 || _actor == target) {
            return false
        }
        
        val targetRadius = if (target is Creature) target.getCollisionRadius() else 0.0
        if (_actor.isIn3DRadius(target, (offset + _actor.getCollisionRadius() + targetRadius).toInt())) {
            return false
        }
        
        if (!_actor.isMovementDisabled() && !isShiftPressed) {
            _pawn = target
            _offset = offset
            moveToPawn(target, offset)
        }
        
        return true
    }
    
    override fun offensiveFollowTask(target: Creature, offset: Int) {
        if (_followTask == null) {
            return
        }
        
        if (!_actor.knows(target)) {
            _actor.getAI().tryToIdle()
            return
        }
        
        val realOffset = (offset + _actor.getCollisionRadius() + target.getCollisionRadius()).toInt()
        if ((if (getMoveType() == MoveType.GROUND) _actor.isIn2DRadius(target, realOffset) else _actor.isIn3DRadius(target, realOffset))) {
            return
        }
        
        val moveOk = GeoEngine.getInstance().getValidLocation(_actor, target)
        val isPathClear = target.isInStrictRadius(moveOk, offset)
        if (isPathClear) {
            _pawn = target
            _offset = offset
            moveToPawn(target, offset)
        } else {
            _pawn = null
            _offset = 0
            moveToLocation(moveOk, Config.SISTEMA_PATHFINDING)
            syncPlayerLocation(true)
        }
    }
    
    override fun friendlyFollowTask(target: Creature, offset: Int) {
        if (!Config.NEW_FOLLOW) {
            if (_followTask == null) {
                return
            }
            
            if (!_actor.knows(target)) {
                _actor.getAI().tryToIdle()
                return
            }
            
            if ((if (getMoveType() == MoveType.GROUND) _actor.isIn2DRadius(target, offset) else _actor.isIn3DRadius(target, offset))) {
                return
            }
            
            if (_task == null) {
                _pawn = target
                _offset = offset
                moveToPawn(target, offset)
            }
        } else {
            if (_followTask == null || _actor.isMovementDisabled()) {
                return
            }
            
            if (!_actor.knows(target)) {
                _actor.getAI().tryToIdle()
                return
            }
            
            val targetLoc = _actor.getPosition().clone()
            targetLoc.setLocationMinusOffset(target.getPosition(), offset + target.getCollisionRadius() + _actor.getCollisionRadius())
            
            if (if (getMoveType() == MoveType.GROUND) _actor.isIn2DRadius(targetLoc, offset) else _actor.isIn3DRadius(targetLoc, offset)) {
                return
            }
            
            _pawn = null
            _offset = 0
            moveToLocation(targetLoc, Config.SISTEMA_PATHFINDING)
        }
    }
    
    override fun calculatePath(ox: Int, oy: Int, oz: Int, tx: Int, ty: Int, tz: Int): Location? {
        if (!Config.SISTEMA_PATHFINDING) {
            return null
        }
        
        if (Config.ATTACK_USE_PATHFINDER && _pawn is Creature) {
            return calculatePathForAttack(ox, oy, oz, tx, ty, tz)
        }
        
        val moveType = getMoveType()
        
        val isWaterRelated = ZoneManager.getInstance().getZone(tx, ty, tz, WaterZone::class.java) != null || moveType == MoveType.SWIM
        
        if (isWaterRelated && GeoEngine.getInstance().canSee(ox, oy, oz, 0.0, tx, ty, tz, 0.0, null, null)) {
            return Location(tx, ty, tz)
        } else if (moveType == MoveType.FLY && GeoEngine.getInstance().canFlyToTarget(ox, oy, oz, 32.0, tx, ty, tz)) {
            return null
        }
        
        if (GeoEngine.getInstance().canMoveToTarget(ox, oy, oz, tx, ty, tz)) {
            return null
        }
        
        val dummy = if (_isDebugPath) ExServerPrimitive() else null
        
        if ((moveType != MoveType.GROUND && moveType != MoveType.SWIM) || _actor.temporaryFixPagan()) {
            return GeoEngine.getInstance().getValidFlyLocation(ox, oy, oz, 32.0, tx, ty, tz, dummy)
        }
        
        var path = GeoEngine.getInstance().findPath(ox, oy, oz, tx, ty, tz, true, if (isWaterRelated) 32 else 500, dummy)
        
        if (path.isEmpty() || path.size < 2) {
            return when {
                isWaterRelated -> Location(tx, ty, tz)
                else -> {
                    val valid = GeoEngine.getInstance().getValidLocation(ox, oy, oz, tx, ty, tz, null)
                    if (valid.x == ox && valid.y == oy) null else valid
                }
            }
        }
        
        if (_isDebugPath && dummy != null) {
            _actor.forEachKnownGM { p ->
                val debug = p.getDebugPacket("PATH${_actor.getObjectId()}")
                debug.reset()
                debug.addAll(dummy)
                debug.sendTo(p)
            }
        }
        
        _geoPath.addAll(path)
        return _geoPath.poll()
    }
    
    fun maybePlayerMoveToLocation(destination: Location, offset: Int, pathfinding: Boolean, isShiftPressed: Boolean): Boolean {
        if (_actor.isIn3DRadius(destination, offset)) {
            return false
        }
        
        if (_actor.isMovementDisabled() || isShiftPressed) {
            return true
        }
        
        _pawn = null
        _offset = 0
        
        val currentPos = _actor.getPosition()
        val dist2D = currentPos.distance2D(destination)
        
        val hasObstacle = when {
            Config.SISTEMA_PATHFINDING && dist2D > 20.0 -> {
                if (Config.ENABLE_REAL_TIME_OBSTACLE_AVOIDANCE) {
                    SmoothObstacleAvoidance.getInstance().shouldAvoidObstacle(currentPos, destination)
                } else {
                    !MovementIntegration.canMoveToTarget(
                        _actor.getX(), _actor.getY(), _actor.getZ(),
                        destination.getX(), destination.getY(), destination.getZ()
                    )
                }
            }
            else -> false
        }
        
        var finalDestination = destination
        if (hasObstacle && pathfinding && Config.SISTEMA_PATHFINDING) {
            val path = GeoEngine.getInstance().findPath(
                _actor.getX(), _actor.getY(), _actor.getZ(),
                destination.getX(), destination.getY(), destination.getZ(),
                true, null
            )
            
            if (path.isNotEmpty()) {
                finalDestination = if (Config.ENABLE_SMOOTH_OBSTACLE_AVOIDANCE) {
                    val smoothPath = SmoothObstacleAvoidance.getInstance().createSmoothPath(path, null)
                    smoothPath.firstOrNull() ?: path[0]
                } else {
                    path[0]
                }
            } else {
                finalDestination = GeoEngine.getInstance().getValidLocation(
                    _actor.getX(), _actor.getY(), _actor.getZ(),
                    destination.getX(), destination.getY(), destination.getZ(), null
                )
            }
        }
        
        moveToLocation(finalDestination, pathfinding)
        return true
    }
    
    fun startPlayerOffensiveFollow(target: Creature, offset: Int) {
        if (isMeleeDebug()) {
            LOGGER.info("[MeleeDebug][PlayerMove][startPlayerOffensiveFollow] actor={} target={} offset={}",
                _actor.getObjectId(), target.getObjectId(), offset)
        }
        cancelFollowTask()
        _followTask = CoroutinePool.scheduleAtFixedRate({ offensiveFollowTask(target, offset) }, 5, 500L)
    }
    
    fun maybeStartPlayerOffensiveFollow(target: Creature, weaponAttackRange: Int): Boolean {
        if (weaponAttackRange < 0) {
            return false
        }
        
        if (isMeleeDebug()) {
            LOGGER.info("[MeleeDebug][PlayerMove][maybeStartPlayerOffensiveFollow] actor={} target={} range={} dist={}",
                _actor.getObjectId(), target.getObjectId(), weaponAttackRange, _actor.distance2D(target))
        }
        
        val totalRadius = (weaponAttackRange + _actor.getCollisionRadius() + target.getCollisionRadius()).toInt()
        val shouldFollow = !_actor.isIn2DRadius(target, totalRadius)
        
        if (!shouldFollow) {
            val distance = _actor.distance2D(target)
            
            if (Config.SISTEMA_PATHFINDING) {
                val hasObstacle = when {
                    distance > 20.0 -> {
                        if (Config.ENABLE_REAL_TIME_OBSTACLE_AVOIDANCE) {
                            SmoothObstacleAvoidance.getInstance().shouldAvoidObstacle(
                                _actor.getPosition(),
                                target.getPosition()
                            )
                        } else {
                            !MovementIntegration.canSeeTarget(_actor, target)
                        }
                    }
                    else -> false
                }
                
                when {
                    hasObstacle && !_actor.isMovementDisabled() && _actor.getAI().getCurrentIntention().canMoveToTarget() -> {
                        startPlayerOffensiveFollow(target, target.getCollisionRadius().toInt())
                        return true
                    }
                    hasObstacle -> return false
                }
            }
            
            return false
        }
        val now = System.currentTimeMillis()
        if (now - _lastStuckCheckTime >= 800) {
            val curX = _actor.getX()
            val curY = _actor.getY()
            val curDist = _actor.distance2D(target)
            val movedTooLittle = _lastStuckX != Int.MIN_VALUE &&
                abs(curX - _lastStuckX) < 10 &&
                abs(curY - _lastStuckY) < 10 &&
                abs(curDist - _lastStuckDist) < 5.0
            val stuck = movedTooLittle && !_actor.isMoving
            _lastStuckCheckTime = now
            _lastStuckX = curX
            _lastStuckY = curY
            _lastStuckDist = curDist
            if (stuck) {
                val path = geoEngine.findPath(
                    _actor.getX(), _actor.getY(), _actor.getZ(),
                    target.getX(), target.getY(), target.getZ(),
                    true, null
                )
                
                if (path.isNotEmpty()) {
                    val candidatePath = if (Config.ENABLE_SMOOTH_OBSTACLE_AVOIDANCE) {
                        SmoothObstacleAvoidance.getInstance().createSmoothPath(path, null)
                    } else {
                        path
                    }
                    val next = candidatePath.firstOrNull { _actor.distance2D(it) > 40 } ?: candidatePath[0]
                    
                    if (isMeleeDebug()) {
                        LOGGER.info(
                            "[MeleeDebug][PlayerMove][maybeStartPlayerOffensiveFollow] stuckRepath actor={} target={} next={}",
                            _actor.getObjectId(), target.getObjectId(), next
                        )
                    }
                    
                    moveToLocation(next, true)
                    return true
                }
                
                val angle = Math.atan2((target.getY() - curY).toDouble(), (target.getX() - curX).toDouble())
                val primaryOffset = if ((target.getObjectId() and 1) == 0) Math.PI / 2 else -Math.PI / 2
                val offsets = doubleArrayOf(primaryOffset, -primaryOffset)
                val bypassSteps = intArrayOf(180, 260, 340, 420, 520)
                for (offset in offsets) {
                    for (step in bypassSteps) {
                        val bypassX = (curX + step * Math.cos(angle + offset)).toInt()
                        val bypassY = (curY + step * Math.sin(angle + offset)).toInt()
                        val bypassZ = geoEngine.getHeight(bypassX, bypassY, _actor.getZ()).toInt()
                        
                        if (geoEngine.canMoveToTarget(curX, curY, _actor.getZ(), bypassX, bypassY, bypassZ)) {
                            val bypassLoc = Location(bypassX, bypassY, bypassZ)
                            if (isMeleeDebug()) {
                                LOGGER.info(
                                    "[MeleeDebug][PlayerMove][maybeStartPlayerOffensiveFollow] stuckBypass actor={} target={} dest={} step={} side={}",
                                    _actor.getObjectId(), target.getObjectId(), bypassLoc, step,
                                    if (offset == primaryOffset) "primary" else "secondary"
                                )
                            }
                            moveToLocation(bypassLoc, true)
                            return true
                        }
                    }
                }
            }
        }
        
        if (Config.SISTEMA_PATHFINDING) {
            val hasObstacle = if (Config.ENABLE_SMOOTH_OBSTACLE_AVOIDANCE) {
                SmoothObstacleAvoidance.getInstance().shouldAvoidObstacle(
                    _actor.getPosition(),
                    target.getPosition()
                )
            } else {
                !MovementIntegration.canSeeTarget(_actor, target)
            }
            
            if (hasObstacle) {
                val path = geoEngine.findPath(
                    _actor.getX(), _actor.getY(), _actor.getZ(),
                    target.getX(), target.getY(), target.getZ(),
                    true, null
                )
                
                if (path.isNotEmpty()) {
                    val next = if (Config.ENABLE_SMOOTH_OBSTACLE_AVOIDANCE) {
                        val smoothPath = SmoothObstacleAvoidance.getInstance().createSmoothPath(path, null)
                        smoothPath.firstOrNull() ?: path[0]
                    } else {
                        path[0]
                    }
                    
                    if (isMeleeDebug()) {
                        LOGGER.info(
                            "[MeleeDebug][PlayerMove][maybeStartPlayerOffensiveFollow] forcedPath actor={} target={} next={}",
                            _actor.getObjectId(), target.getObjectId(), next
                        )
                    }
                    
                    moveToLocation(next, true)
                    return true
                }
            }
        }
        
        if (!_actor.isMovementDisabled() && _actor.getAI().getCurrentIntention().canMoveToTarget()) {
            startPlayerOffensiveFollow(target, weaponAttackRange)
            return true
        }
        
        return true
    }
    
}