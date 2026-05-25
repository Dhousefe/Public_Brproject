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
 * Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, 
 * SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo, Tiagorosendo, Schuster, LucasStark, damedd
 * as a contribution for the forum L2JBrasil.com
 */
package ext.mods.gameserver.geoengine
import ext.mods.Config
import ext.mods.commons.pool.CoroutinePool
import ext.mods.commons.config.ExProperties
import ext.mods.commons.logging.CLogger
import ext.mods.gameserver.enums.GeoType
import ext.mods.gameserver.enums.MoveDirectionType
import ext.mods.gameserver.geoengine.geodata.*
import ext.mods.gameserver.model.World
import ext.mods.gameserver.model.WorldObject
import ext.mods.gameserver.model.actor.Creature
import ext.mods.gameserver.model.actor.Playable
import ext.mods.gameserver.model.location.Location
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.io.RandomAccessFile
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.locks.StampedLock
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
class GeoEngine private constructor() {
    private val _blocks: Array<Array<ABlock>>
    private val _geoBugReports: PrintWriter?
    
    private val multilayerMutex = Mutex()
    private val REGION_LOCKS = 256
    private val regionLocks = Array(REGION_LOCKS) { StampedLock() }
    private val PADDING_SHIFT = 3
    private val CACHE_CAPACITY = 65536
    private val CACHE_MASK = CACHE_CAPACITY - 1
    private val heightCacheKeys = LongArray(CACHE_CAPACITY shl PADDING_SHIFT) { -1L }
    private val heightCacheVals = ShortArray(CACHE_CAPACITY shl PADDING_SHIFT)
    private val nsweCacheKeys = LongArray(CACHE_CAPACITY shl PADDING_SHIFT) { -1L }
    private val nsweCacheVals = ByteArray(CACHE_CAPACITY shl PADDING_SHIFT)
    init {
        _blocks = Array(GeoStructure.GEO_BLOCKS_X) {
            Array(GeoStructure.GEO_BLOCKS_Y) { BlockNull }
        }
        BlockMultilayer.initialize()
        val props: ExProperties = Config.initProperties(Config.GEOENGINE_FILE.toString())
        val cpuCores = Runtime.getRuntime().availableProcessors()
        val parallelism = (cpuCores / 2).coerceIn(2, 6)
        
        GeoEngine.LOGGER.info("----------------------------------------------------------------")
        GeoEngine.LOGGER.info(" GeoEngine: Iniciando V2 (Zero-GC / Lock Striping / Retail Physics)")
        GeoEngine.LOGGER.info(" Threads detectadas: $cpuCores | Usando: $parallelism threads para Geodata")
        GeoEngine.LOGGER.info("----------------------------------------------------------------")
        
        val (loaded, failed) = runBlocking {
            val allRegions = (World.TILE_X_MIN..World.TILE_X_MAX).flatMap { rx ->
                (World.TILE_Y_MIN..World.TILE_Y_MAX).map { ry -> rx to ry }
            }
            
            val batchSize = allRegions.size / parallelism + 1
            val batches = allRegions.chunked(batchSize)
            
            GeoEngine.LOGGER.info(" Processando ${allRegions.size} regioes em ${batches.size} lotes simultaneos...")
            val jobs = batches.mapIndexed { index, batch ->
                async(Dispatchers.IO) {
                    var l = 0
                    var f = 0
                    val start = System.currentTimeMillis()
                    for ((rx, ry) in batch) {
                        try {
                            if (props.containsKey("${rx}_$ry")) {
                                if (loadGeoBlocks(rx, ry)) l++ else f++
                            } else {
                                loadNullBlocks(rx, ry)
                            }
                        } catch (e: Exception) {
                            GeoEngine.LOGGER.error("Erro na regiao ${rx}_${ry}", e)
                            loadNullBlocks(rx, ry)
                            f++
                        }
                    }
                    val time = System.currentTimeMillis() - start
                    GeoEngine.LOGGER.info(" -> Lote #${index + 1} finalizado: $l carregados em ${time}ms")
                    Pair(l, f)
                }
            }
            
            jobs.awaitAll().fold(Pair(0, 0)) { acc, pair ->
                Pair(acc.first + pair.first, acc.second + pair.second)
            }
        }
        
        GeoEngine.LOGGER.info("----------------------------------------------------------------")
        GeoEngine.LOGGER.info(" Geodata Carregada: $loaded Sucessos / $failed Falhas")
        GeoEngine.LOGGER.info("----------------------------------------------------------------")
        BlockMultilayer.release()
        
        var writer: PrintWriter? = null
        try {
            val bugFile = File(Config.GEODATA_PATH + "geo_bugs.txt")
            writer = PrintWriter(FileOutputStream(bugFile, true), true)
        } catch (e: Exception) {
            GeoEngine.LOGGER.error("Couldn't load \"geo_bugs.txt\" file.", e)
        }
        _geoBugReports = writer
        
        if (Config.USE_L2BR_PATHFINDING) {
            try {
                val bridge = ext.mods.gameserver.geoengine.pathfinding.integration.GeoEngineBridge.getInstance()
                val adapter = ext.mods.gameserver.geoengine.pathfinding.integration.GeoEngineLegacyAdapter(this)
                bridge.initialize(adapter)
                bridge.loadL2BRRegions()
            } catch (e: Exception) {
                GeoEngine.LOGGER.error("Falha ao inicializar GeoEngineBridge (L2BR)", e)
            }
        }
    }
    
    @Suppress("NOTHING_TO_INLINE")
    private inline fun getLockFor(bx: Int, by: Int): StampedLock {
        val index = ((bx * 31) + by) and (REGION_LOCKS - 1)
        return regionLocks[index]
    }
    
    @Suppress("NOTHING_TO_INLINE")
    private inline fun getGeoKey(geoX: Int, geoY: Int, z: Int): Long {
        return (geoX.toLong() shl 36) or 
               (geoY.toLong() shl 18) or 
               ((z shr 4).toLong() and 0x3FFFFL)
    }
    
    @Suppress("NOTHING_TO_INLINE")
    private inline fun hashKeyPadded(key: Long): Int {
        var h = (key xor (key ushr 32)).toInt()
        h = h xor (h ushr 16)
        h = h * -2048144789
        h = h xor (h ushr 13)
        h = h * -1028477387
        val baseIndex = (h xor (h ushr 16)) and CACHE_MASK
        return baseIndex shl PADDING_SHIFT 
    }
    private suspend fun loadGeoBlocks(regionX: Int, regionY: Int): Boolean {
        val filename = String.format(Config.GEODATA_TYPE.filename, regionX, regionY)
        val filepath = Config.GEODATA_PATH + filename
        try {
            RandomAccessFile(filepath, "r").use { raf ->
                raf.channel.use { fc ->
                    val buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).load()
                    buffer.order(ByteOrder.LITTLE_ENDIAN)
                    when (Config.GEODATA_TYPE) {
                        GeoType.L2OFF -> {
                            buffer.position(buffer.position() + 18)
                        }
                        else -> { }
                    }
                    val blockX = (regionX - World.TILE_X_MIN) * GeoStructure.REGION_BLOCKS_X
                    val blockY = (regionY - World.TILE_Y_MIN) * GeoStructure.REGION_BLOCKS_Y
                    for (ix in 0 until GeoStructure.REGION_BLOCKS_X) {
                        for (iy in 0 until GeoStructure.REGION_BLOCKS_Y) {
                            val block: ABlock = when (Config.GEODATA_TYPE) {
                                GeoType.L2J -> {
                                    val type = buffer.get()
                                    when (type) {
                                        GeoStructure.TYPE_FLAT_L2J_L2OFF -> BlockFlat(buffer, Config.GEODATA_TYPE)
                                        GeoStructure.TYPE_COMPLEX_L2J -> BlockComplex(buffer)
                                        GeoStructure.TYPE_MULTILAYER_L2J -> multilayerMutex.withLock { BlockMultilayer(buffer, Config.GEODATA_TYPE) }
                                        else -> throw IllegalArgumentException("Unknown block type: $type")
                                    }
                                }
                                else -> {
                                    val type = buffer.short
                                    when (type) {
                                        GeoStructure.TYPE_FLAT_L2J_L2OFF.toShort() -> BlockFlat(buffer, Config.GEODATA_TYPE)
                                        GeoStructure.TYPE_COMPLEX_L2OFF.toShort() -> BlockComplex(buffer)
                                        else -> multilayerMutex.withLock { BlockMultilayer(buffer, Config.GEODATA_TYPE) }
                                    }
                                }
                            }
                            _blocks[blockX + ix][blockY + iy] = block
                        }
                    }
                    if (buffer.remaining() > 0) {
                        GeoEngine.LOGGER.warn("Region file {} can be corrupted.", filename)
                    }
                    return true
                }
            }
        } catch (e: Exception) {
            GeoEngine.LOGGER.error("Error loading {} region file.", e, filename)
            loadNullBlocks(regionX, regionY)
            return false
        }
    }
    private fun loadNullBlocks(regionX: Int, regionY: Int) {
        val blockX = (regionX - World.TILE_X_MIN) * GeoStructure.REGION_BLOCKS_X
        val blockY = (regionY - World.TILE_Y_MIN) * GeoStructure.REGION_BLOCKS_Y
        for (ix in 0 until GeoStructure.REGION_BLOCKS_X) {
            for (iy in 0 until GeoStructure.REGION_BLOCKS_Y) {
                _blocks[blockX + ix][blockY + iy] = BlockNull
            }
        }
    }
    private fun getBlockRaw(geoX: Int, geoY: Int): ABlock {
        val bx = geoX / GeoStructure.BLOCK_CELLS_X
        val by = geoY / GeoStructure.BLOCK_CELLS_Y
        
        val lock = getLockFor(bx, by)
        val stamp = lock.tryOptimisticRead()
        var block = _blocks[bx][by]
        
        if (!lock.validate(stamp)) {
            val readStamp = lock.readLock()
            try {
                block = _blocks[bx][by]
            } finally {
                lock.unlockRead(readStamp)
            }
        }
        return block
    }
    fun getBlock(geoX: Int, geoY: Int): ABlock = getBlockRaw(geoX, geoY)
    fun hasGeoPos(geoX: Int, geoY: Int): Boolean = getBlockRaw(geoX, geoY).hasGeoPos()
    fun getHeightNearest(geoX: Int, geoY: Int, worldZ: Int): Short {
        val key = getGeoKey(geoX, geoY, worldZ)
        val index = hashKeyPadded(key)
        
        if (heightCacheKeys[index] == key) return heightCacheVals[index]
        
        val height = getBlockRaw(geoX, geoY).getHeightNearest(geoX, geoY, worldZ, null)
        heightCacheKeys[index] = key
        heightCacheVals[index] = height
        return height
    }
    fun getHeightNearest(geoX: Int, geoY: Int, worldZ: Int, ignore: IGeoObject?): Short {
        if (ignore != null) return getBlockRaw(geoX, geoY).getHeightNearest(geoX, geoY, worldZ, ignore)
        return getHeightNearest(geoX, geoY, worldZ)
    }
    fun getNsweNearest(geoX: Int, geoY: Int, worldZ: Int): Byte {
        val key = getGeoKey(geoX, geoY, worldZ)
        val index = hashKeyPadded(key)
        
        if (nsweCacheKeys[index] == key) return nsweCacheVals[index]
        
        val nswe = getBlockRaw(geoX, geoY).getNsweNearest(geoX, geoY, worldZ, null)
        nsweCacheKeys[index] = key
        nsweCacheVals[index] = nswe
        return nswe
    }
    fun getNsweNearest(geoX: Int, geoY: Int, worldZ: Int, ignore: IGeoObject?): Byte {
        if (ignore != null) return getBlockRaw(geoX, geoY).getNsweNearest(geoX, geoY, worldZ, ignore)
        return getNsweNearest(geoX, geoY, worldZ)
    }
    fun hasGeo(worldX: Int, worldY: Int): Boolean = hasGeoPos(getGeoX(worldX), getGeoY(worldY))
    fun getHeight(loc: Location): Short = getHeightNearest(getGeoX(loc.x), getGeoY(loc.y), loc.z)
    fun getHeight(worldX: Int, worldY: Int, worldZ: Int): Short = getHeightNearest(getGeoX(worldX), getGeoY(worldY), worldZ)
    fun getNswe(worldX: Int, worldY: Int, worldZ: Int): Byte = getNsweNearest(getGeoX(worldX), getGeoY(worldY), worldZ)
    private fun getBoundaryRadiusCells(): Int =
        if (Config.BOUNDARY_BUFFER <= 0) 1 else ((Config.BOUNDARY_BUFFER + GeoStructure.CELL_SIZE - 1) / GeoStructure.CELL_SIZE).coerceAtLeast(1)
    fun hasBlockedNeighborAtSameLevel(geoX: Int, geoY: Int, z: Int): Boolean {
        val checkZ = z + GeoStructure.CELL_IGNORE_HEIGHT
        val radius = getBoundaryRadiusCells()
        for (dx in -radius..radius) {
            for (dy in -radius..radius) {
                if (dx == 0 && dy == 0) continue
                val nx = geoX + dx
                val ny = geoY + dy
                if (nx < 0 || nx >= GeoStructure.GEO_CELLS_X || ny < 0 || ny >= GeoStructure.GEO_CELLS_Y) continue
                val block = getBlock(nx, ny)
                val index = block.getIndexBelow(nx, ny, checkZ, null)
                if (index >= 0) {
                    val nz = block.getHeight(index, null).toInt()
                    if (abs(nz - z) <= GeoStructure.CELL_IGNORE_HEIGHT) {
                        val nswe = block.getNswe(index, null)
                        if (nswe == GeoStructure.CELL_FLAG_NONE) return true
                    }
                }
            }
        }
        return false
    }
    private fun invalidateCacheForObject(obj: IGeoObject) {
        val minGX = obj.geoX
        val minGY = obj.geoY
        val geoData = obj.objectGeoData
        val maxGX = minGX + geoData.size - 1
        val maxGY = minGY + geoData[0].size - 1
        
        for (gx in minGX..maxGX) {
            for (gy in minGY..maxGY) {
                for (z in -15000..15000 step 16) {
                    val key = getGeoKey(gx, gy, z)
                    val index = hashKeyPadded(key)
                    if (heightCacheKeys[index] == key) heightCacheKeys[index] = -1L
                    if (nsweCacheKeys[index] == key) nsweCacheKeys[index] = -1L
                }
            }
        }
    }
    fun addGeoObject(obj: IGeoObject) {
        toggleGeoObject(obj, true)
    }
    fun removeGeoObject(obj: IGeoObject) {
        toggleGeoObject(obj, false)
    }
    private fun toggleGeoObject(obj: IGeoObject, add: Boolean) {
        val minGX = obj.geoX
        val minGY = obj.geoY
        val geoData = obj.objectGeoData
        val minBX = minGX / GeoStructure.BLOCK_CELLS_X
        val maxBX = (minGX + geoData.size - 1) / GeoStructure.BLOCK_CELLS_X
        val minBY = minGY / GeoStructure.BLOCK_CELLS_Y
        val maxBY = (minGY + geoData[0].size - 1) / GeoStructure.BLOCK_CELLS_Y
        
        for (bx in minBX..maxBX) {
            for (by in minBY..maxBY) {
                val lock = getLockFor(bx, by)
                val stamp = lock.writeLock()
                try {
                    var block = _blocks[bx][by]
                    if (block !is IBlockDynamic) {
                        if (block is BlockNull) continue
                        block = when (block) {
                            is BlockFlat -> BlockComplexDynamic(bx, by, block)
                            is BlockComplex -> BlockComplexDynamic(bx, by, block)
                            is BlockMultilayer -> BlockMultilayerDynamic(bx, by, block)
                            else -> block
                        }
                        _blocks[bx][by] = block
                    }
                    if (add) {
                        (block as IBlockDynamic).addGeoObject(obj)
                    } else {
                        (block as IBlockDynamic).removeGeoObject(obj)
                    }
                } finally {
                    lock.unlockWrite(stamp)
                }
            }
        }
        invalidateCacheForObject(obj)
    }
    fun canMoveAround(worldX: Int, worldY: Int, worldZ: Int): Boolean {
        val geoX = getGeoX(worldX)
        val geoY = getGeoY(worldY)
        for (ix in -1..1) {
            for (iy in -1..1) {
                val gx = geoX + ix
                val gy = geoY + iy
                if (getNsweNearest(gx, gy, worldZ) != GeoStructure.CELL_FLAG_ALL) return false
            }
        }
        return true
    }
    fun canMoveWithCollisionBox(
        ox: Int, oy: Int, oz: Int,
        tx: Int, ty: Int, tz: Int,
        collisionRadius: Double,
        debug: ExServerPrimitive?
    ): Boolean {
        if (collisionRadius <= 0.0) {
            return canMove(ox, oy, oz, tx, ty, tz, debug)
        }
        val steps = max(1, (collisionRadius / 8.0).toInt())
        
        for (i in 0..steps) {
            val factor = i.toDouble() / steps
            val checkX = (ox + (tx - ox) * factor).toInt()
            val checkY = (oy + (ty - oy) * factor).toInt()
            val checkZ = (oz + (tz - oz) * factor).toInt()
            for (offsetX in -1..1) {
                for (offsetY in -1..1) {
                    if (offsetX == 0 && offsetY == 0) continue 
                    val testX = checkX + (offsetX * collisionRadius).toInt()
                    val testY = checkY + (offsetY * collisionRadius).toInt()
                    if (!canMove(testX, testY, checkZ, testX, testY, checkZ, debug)) {
                        return false
                    }
                }
            }
        }
        return canMove(ox, oy, oz, tx, ty, tz, debug)
    }
    fun isInWater(x: Int, y: Int, z: Int): Boolean {
        val groundZ = getHeight(x, y, z).toInt()
        return z < groundZ - 10
    }
    fun getWaterLevel(x: Int, y: Int): Int {
        val groundZ = getHeight(x, y, 0).toInt()
        return groundZ - 10
    }
    fun getValidSwimLocationAdvanced(
        ox: Int, oy: Int, oz: Int, 
        tx: Int, ty: Int, tz: Int, 
        creature: Creature?
    ): Location {
        val advancedPhysics = true
        when {
            advancedPhysics -> {
                val wasInWater = isInWater(ox, oy, oz)
                val willBeOnLand = !isInWater(tx, ty, tz)
                when {
                    wasInWater && willBeOnLand -> {
                        val waterLevel = getWaterLevel(ox, oy)
                        val landLevel = getHeight(tx, ty, tz).toInt()
                        val heightDifference = landLevel - waterLevel
                        when {
                            heightDifference > 50 -> return Location(ox, oy, oz)
                        }
                    }
                }
            }
        }
        
        val collisionPathfinding = true
        when {
            collisionPathfinding && creature != null -> {
                val collisionRadius = creature.collisionRadius
                when {
                    collisionRadius > 0.0 -> {
                        val steps = max(1, (collisionRadius / 8.0).toInt())
                        (0..steps).forEach { i ->
                            val factor = i.toDouble() / steps
                            val checkX = (ox + (tx - ox) * factor).toInt()
                            val checkY = (oy + (ty - oy) * factor).toInt()
                            val checkZ = (oz + (tz - oz) * factor).toInt()
                            val hasCollision = (-1..1).flatMap { offsetX ->
                                (-1..1).mapNotNull { offsetY ->
                                    when {
                                        offsetX == 0 && offsetY == 0 -> null
                                        else -> {
                                            val testX = checkX + (offsetX * collisionRadius).toInt()
                                            val testY = checkY + (offsetY * collisionRadius).toInt()
                                            !canMove(testX, testY, checkZ, testX, testY, checkZ, null)
                                        }
                                    }
                                }
                            }.any { it }
                            if (hasCollision) return Location(ox, oy, oz)
                        }
                    }
                }
            }
        }
        return getValidSwimLocation(ox, oy, oz, tx, ty, tz, null)
    }
    fun canSeeTarget(obj: WorldObject, target: WorldObject): Boolean {
        val ox = obj.x
        val oy = obj.y
        val oz = obj.z
        val tx = target.x
        val ty = target.y
        val tz = target.z
        var oheight = 0.0
        if (obj is Creature) {
            oheight += (obj.collisionHeight * 2 * Config.PART_OF_CHARACTER_HEIGHT / 100).toDouble()
        }
        var theight = 0.0
        if (target is Creature) {
            theight += (target.collisionHeight * 2 * Config.PART_OF_CHARACTER_HEIGHT / 100).toDouble()
        }
        val ignore = if (target is IGeoObject) target else null
        return (canSee(ox, oy, oz, oheight, tx, ty, tz, theight, ignore, null)
                && canSee(tx, ty, tz, theight, ox, oy, oz, oheight, ignore, null))
    }
    fun canSeeLocation(obj: WorldObject, position: Location): Boolean {
        val ox = obj.x
        val oy = obj.y
        val oz = obj.z
        val tx = position.x
        val ty = position.y
        val tz = position.z
        var oheight = 0.0
        if (obj is Creature) {
            oheight += (obj.collisionHeight * 2 * Config.PART_OF_CHARACTER_HEIGHT / 100).toDouble()
        }
        return (canSee(ox, oy, oz, oheight, tx, ty, tz, 0.0, null, null)
                && canSee(tx, ty, tz, 0.0, ox, oy, oz, oheight, null, null))
    }
    fun canSee(
        ox: Int, oy: Int, oz: Int, oheight: Double,
        tx: Int, ty: Int, tz: Int, theight: Double,
        ignore: IGeoObject?, debug: ExServerPrimitive?
    ): Boolean {
        return when {
            World.isOutOfWorld(ox, oy) -> false
            World.isOutOfWorld(tx, ty) -> false
            else -> {
                var gox = getGeoX(ox)
                var goy = getGeoY(oy)
                val gtx = getGeoX(tx)
                val gty = getGeoY(ty)
                
                var block = getBlock(gox, goy)
                var index = block.getIndexBelow(gox, goy, oz + GeoStructure.CELL_HEIGHT, ignore)
                when {
                    index < 0 -> false
                    gox == gtx && goy == gty -> {
                        index == block.getIndexBelow(gtx, gty, tz + GeoStructure.CELL_HEIGHT, ignore)
                    }
                    else -> {
                        var groundZ = block.getHeight(index, ignore).toInt()
                        var nswe = block.getNswe(index, ignore)
                        val dx = tx - ox
                        val dy = ty - oy
                        val dz = (tz + theight) - (oz + oheight)
        
                        val mz = dz / sqrt((dx * dx + dy * dy).toDouble())
                        val mdt = MoveDirectionType.getDirection(gtx - gox, gty - goy)
        
                        val m = if (dx == 0) 0.0 else dy.toDouble() / dx
                        var gridX = ox and 0xFFFFFFF0.toInt()
                        var gridY = oy and 0xFFFFFFF0.toInt()
                        if (debug != null) {
                            debug.addSquare(Color.BLUE, gridX, gridY, groundZ + 1, 15)
                            debug.addSquare(Color.BLUE, tx and 0xFFFFFFF0.toInt(), ty and 0xFFFFFFF0.toInt(), tz, 15)
                        }
                        var dir: Byte
                        while (gox != gtx || goy != gty) {
                            var checkX = gridX + mdt.offsetX
                            var checkY = (oy + m * (checkX - ox)).toInt()
                            when {
                                mdt.stepX != 0 && getGeoY(checkY) == goy -> {
                                    debug?.let {
                                        it.addPoint(mdt.symbolX, Color.CYAN, true, checkX, checkY, groundZ)
                                        it.addSquare(Color.GREEN, gridX, gridY, groundZ, 15)
                                    }
                                    gridX += mdt.stepX
                                    gox += mdt.signumX
                                    dir = mdt.directionX
                                }
                                else -> {
                                    checkY = gridY + mdt.offsetY
                                    checkX = (ox + (checkY - oy) / m).toInt()
                                    checkX = max(gridX, min(gridX + 15, checkX))
                                    debug?.let {
                                        it.addPoint(mdt.symbolY, Color.YELLOW, true, checkX, checkY, groundZ)
                                        it.addSquare(Color.GREEN, gridX, gridY, groundZ, 15)
                                    }
                                    gridY += mdt.stepY
                                    goy += mdt.signumY
                                    dir = mdt.directionY
                                }
                            }
                            block = getBlock(gox, goy)
                            var losz = oz + oheight + Config.MAX_OBSTACLE_HEIGHT
                            losz += mz * sqrt(((checkX - ox) * (checkX - ox) + (checkY - oy) * (checkY - oy)).toDouble())
                            val canMove = (nswe.toInt() and dir.toInt()) != 0
                            index = when {
                                canMove -> block.getIndexBelow(gox, goy, groundZ + GeoStructure.CELL_IGNORE_HEIGHT, ignore)
                                else -> block.getIndexAbove(gox, goy, groundZ - 2 * GeoStructure.CELL_HEIGHT, ignore)
                            }
                            when {
                                index < 0 -> {
                                    debug?.addSquare(Color.RED, gridX, gridY, groundZ, 15)
                                    return false
                                }
                            }
                            val z = block.getHeight(index, ignore).toInt()
                            when {
                                z > losz -> {
                                    debug?.let {
                                        it.addPoint(Color.RED, checkX, checkY, losz.toInt())
                                        it.addSquare(Color.RED, gridX, gridY, z, 15)
                                    }
                                    return false
                                }
                            }
                            groundZ = z
                            nswe = block.getNswe(index, ignore)
                        }
                        true
                    }
                }
            }
        }
    }
    fun raycast(ox: Int, oy: Int, oz: Int, tx: Int, ty: Int, tz: Int, ignore: IGeoObject?): Location? {
        return when {
            World.isOutOfWorld(ox, oy) -> null
            World.isOutOfWorld(tx, ty) -> null
            else -> {
                var gox = getGeoX(ox)
                var goy = getGeoY(oy)
                val gtx = getGeoX(tx)
                val gty = getGeoY(ty)
                
                var block = getBlock(gox, goy)
                var index = block.getIndexBelow(gox, goy, oz + GeoStructure.CELL_HEIGHT, ignore)
                when {
                    index < 0 -> null
                    gox == gtx && goy == gty -> {
                        when {
                            index == block.getIndexBelow(gtx, gty, tz + GeoStructure.CELL_HEIGHT, ignore) -> null
                            else -> Location(tx, ty, tz)
                        }
                    }
                    else -> {
                        var groundZ = block.getHeight(index, ignore).toInt()
                        var nswe = block.getNswe(index, ignore)
                        val mz = (tz - oz) / sqrt(((tx - ox) * (tx - ox) + (ty - oy) * (ty - oy)).toDouble())
                        val mdt = MoveDirectionType.getDirection(gtx - gox, gty - goy)
                        
                        val m = when {
                            tx == ox -> 0.0
                            else -> (ty - oy).toDouble() / (tx - ox)
                        }
                        var gridX = ox and 0xFFFFFFF0.toInt()
                        var gridY = oy and 0xFFFFFFF0.toInt()
                        var dir: Byte
                        while (gox != gtx || goy != gty) {
                            var checkX = gridX + mdt.offsetX
                            var checkY = (oy + m * (checkX - ox)).toInt()
                            when {
                                mdt.stepX != 0 && getGeoY(checkY) == goy -> {
                                    gridX += mdt.stepX
                                    gox += mdt.signumX
                                    dir = mdt.directionX
                                }
                                else -> {
                                    checkY = gridY + mdt.offsetY
                                    checkX = (ox + (checkY - oy) / m).toInt()
                                    checkX = max(gridX, min(gridX + 15, checkX))
                                    gridY += mdt.stepY
                                    goy += mdt.signumY
                                    dir = mdt.directionY
                                }
                            }
                            block = getBlock(gox, goy)
                            
                            var losz = oz + Config.MAX_OBSTACLE_HEIGHT.toDouble()
                            val distance = sqrt(((checkX - ox) * (checkX - ox) + (checkY - oy) * (checkY - oy)).toDouble())
                            losz += mz * distance
                            val canMove = (nswe.toInt() and dir.toInt()) != 0
                            index = when {
                                canMove -> block.getIndexBelow(gox, goy, groundZ + GeoStructure.CELL_IGNORE_HEIGHT, ignore)
                                else -> block.getIndexAbove(gox, goy, groundZ - 2 * GeoStructure.CELL_HEIGHT, ignore)
                            }
                            when {
                                index < 0 -> {
                                    val collisionZ = (oz + mz * distance).toInt()
                                    return Location(checkX, checkY, collisionZ)
                                }
                            }
                            val z = block.getHeight(index, ignore).toInt()
                            when {
                                z > losz -> {
                                    val collisionZ = (oz + mz * distance).toInt()
                                    return Location(checkX, checkY, collisionZ)
                                }
                            }
                            groundZ = z
                            nswe = block.getNswe(index, ignore)
                        }
                        Location(tx, ty, tz)
                    }
                }
            }
        }
    }
    fun canFindPathToTarget(obj: WorldObject, target: WorldObject): Boolean {
        if (canMoveToTarget(obj.position, target.position)) return true
        return findPath(
            obj.x, obj.y, obj.z, target.x, target.y, target.z,
            obj is Playable, null
        ).size >= 2
    }
    fun canMoveToTarget(obj: WorldObject, target: WorldObject): Boolean =
        canMoveToTarget(obj.position, target.position)
    fun canMoveToTarget(obj: WorldObject, position: Location): Boolean =
        canMoveToTarget(obj.position, position)
    fun canMoveToTarget(origin: Location, target: Location): Boolean =
        canMove(origin.x, origin.y, origin.z, target.x, target.y, target.z, null)
    fun canMoveToTarget(ox: Int, oy: Int, oz: Int, tx: Int, ty: Int, tz: Int): Boolean =
        canMove(ox, oy, oz, tx, ty, tz, null)
    fun canMove(ox: Int, oy: Int, oz: Int, tx: Int, ty: Int, tz: Int, debug: ExServerPrimitive?): Boolean {
        if (World.isOutOfWorld(tx, ty)) return false
        var gox = getGeoX(ox)
        var goy = getGeoY(oy)
        var goz = getHeightNearest(gox, goy, oz).toInt()
        val gtx = getGeoX(tx)
        val gty = getGeoY(ty)
        if (gox == gtx && goy == gty) return goz == getHeight(tx, ty, tz).toInt()
        var nswe = getNsweNearest(gox, goy, goz, null)
        val m = if (tx == ox) 0.0 else (ty - oy).toDouble() / (tx - ox)
        val mdt = MoveDirectionType.getDirection(gtx - gox, gty - goy)
        var gridX = ox and 0xFFFFFFF0.toInt()
        var gridY = oy and 0xFFFFFFF0.toInt()
        if (debug != null) {
            debug.addSquare(Color.BLUE, gridX, gridY, goz + 1, 15)
            debug.addSquare(Color.BLUE, tx and 0xFFFFFFF0.toInt(), ty and 0xFFFFFFF0.toInt(), tz, 15)
        }
        var dir: Byte
        var nx = gox
        var ny = goy
        while (gox != gtx || goy != gty) {
            var checkX = gridX + mdt.offsetX
            var checkY = (oy + m * (checkX - ox)).toInt()
            if (mdt.stepX != 0 && getGeoY(checkY) == goy) {
                if (debug != null) {
                    debug.addPoint(mdt.symbolX, Color.CYAN, true, checkX, checkY, goz)
                    debug.addSquare(Color.GREEN, gridX, gridY, goz, 15)
                }
                gridX += mdt.stepX
                nx += mdt.signumX
                dir = mdt.directionX
            } else {
                checkY = gridY + mdt.offsetY
                checkX = (ox + (checkY - oy) / m).toInt()
                checkX = max(gridX, min(gridX + 15, checkX))
                if (debug != null) {
                    debug.addPoint(mdt.symbolY, Color.YELLOW, true, checkX, checkY, goz)
                    debug.addSquare(Color.GREEN, gridX, gridY, goz, 15)
                }
                gridY += mdt.stepY
                ny += mdt.signumY
                dir = mdt.directionY
            }
            if ((nswe.toInt() and dir.toInt()) == 0) {
                if (debug != null) debug.addSquare(Color.RED, gridX, gridY, goz, 15)
                return false
            }
            val block = getBlock(nx, ny)
            val i = block.getIndexBelow(nx, ny, goz + GeoStructure.CELL_IGNORE_HEIGHT, null)
            if (i < 0) {
                if (debug != null) debug.addSquare(Color.RED, gridX, gridY, goz, 15)
                return false
            }
            gox = nx
            goy = ny
            goz = block.getHeight(i, null).toInt()
            nswe = block.getNswe(i, null)
        }
        return goz == getHeight(tx, ty, tz).toInt()
    }
    fun canMoveClear(ox: Int, oy: Int, oz: Int, tx: Int, ty: Int, tz: Int, debug: ExServerPrimitive?): Boolean {
        return when {
            World.isOutOfWorld(tx, ty) -> false
            else -> {
                var gox = getGeoX(ox)
                var goy = getGeoY(oy)
                var goz = getHeightNearest(gox, goy, oz).toInt()
                val gtx = getGeoX(tx)
                val gty = getGeoY(ty)
                when {
                    gox == gtx && goy == gty -> goz == getHeight(tx, ty, tz).toInt()
                    else -> {
                        var nswe = getNsweNearest(gox, goy, goz, null)
                        val m = when {
                            tx == ox -> 0.0
                            else -> (ty - oy).toDouble() / (tx - ox)
                        }
                        val mdt = MoveDirectionType.getDirection(gtx - gox, gty - goy)
                        var gridX = ox and 0xFFFFFFF0.toInt()
                        var gridY = oy and 0xFFFFFFF0.toInt()
                        debug?.let {
                            it.addSquare(Color.BLUE, gridX, gridY, goz + 1, 15)
                            it.addSquare(Color.BLUE, tx and 0xFFFFFFF0.toInt(), ty and 0xFFFFFFF0.toInt(), tz, 15)
                        }
                        var nx = gox
                        var ny = goy
                        while (gox != gtx || goy != gty) {
                            var checkX = gridX + mdt.offsetX
                            var checkY = (oy + m * (checkX - ox)).toInt()
                            when {
                                mdt.stepX != 0 && getGeoY(checkY) == goy -> {
                                    debug?.let {
                                        it.addPoint(mdt.symbolX, Color.CYAN, true, checkX, checkY, goz)
                                        it.addSquare(Color.GREEN, gridX, gridY, goz, 15)
                                    }
                                    gridX += mdt.stepX
                                    nx += mdt.signumX
                                }
                                else -> {
                                    checkY = gridY + mdt.offsetY
                                    checkX = (ox + (checkY - oy) / m).toInt()
                                    checkX = max(gridX, min(gridX + 15, checkX))
                                    debug?.let {
                                        it.addPoint(mdt.symbolY, Color.YELLOW, true, checkX, checkY, goz)
                                        it.addSquare(Color.GREEN, gridX, gridY, goz, 15)
                                    }
                                    gridY += mdt.stepY
                                    ny += mdt.signumY
                                }
                            }
                            when {
                                nswe != GeoStructure.CELL_FLAG_ALL -> {
                                    debug?.addSquare(Color.RED, gridX, gridY, goz, 15)
                                    return false
                                }
                            }
                            val block = getBlock(nx, ny)
                            val i = block.getIndexBelow(nx, ny, goz + GeoStructure.CELL_IGNORE_HEIGHT, null)
                            when {
                                i < 0 -> {
                                    debug?.addSquare(Color.RED, gridX, gridY, goz, 15)
                                    return false
                                }
                            }
                            gox = nx
                            goy = ny
                            goz = block.getHeight(i, null).toInt()
                            nswe = block.getNswe(i, null)
                        }
                        goz == getHeight(tx, ty, tz).toInt()
                    }
                }
            }
        }
    }
    fun getValidLocation(obj: WorldObject, tx: Int, ty: Int, tz: Int): Location =
        getValidLocation(obj.x, obj.y, obj.z, tx, ty, tz, null)
    fun getValidLocation(follower: WorldObject, pawn: WorldObject): Location =
        getValidLocation(follower.position, pawn.position)
    fun getValidLocation(obj: WorldObject, position: Location): Location =
        getValidLocation(obj.position, position)
    fun getValidLocation(origin: Location, target: Location): Location =
        getValidLocation(origin.x, origin.y, origin.z, target.x, target.y, target.z, null)
    
    fun getValidLocation(
        ox: Int, oy: Int, oz: Int,
        tx: Int, ty: Int, tz: Int,
        debug: ExServerPrimitive?
    ): Location {
        var gox = getGeoX(ox)
        var goy = getGeoY(oy)
        var goz = getHeightNearest(gox, goy, oz).toInt()
        var nswe = getNsweNearest(gox, goy, goz, null)
        val gtx = getGeoX(tx)
        val gty = getGeoY(ty)
        val gtz = getHeightNearest(gtx, gty, tz).toInt()
        val m = when {
            tx == ox -> 0.0
            else -> (ty - oy).toDouble() / (tx - ox)
        }
        val mdt = MoveDirectionType.getDirection(gtx - gox, gty - goy)
        var gridX = ox and 0xFFFFFFF0.toInt()
        var gridY = oy and 0xFFFFFFF0.toInt()
        debug?.let {
            it.addSquare(Color.BLUE, gridX, gridY, goz + 1, 15)
            it.addSquare(Color.BLUE, tx and 0xFFFFFFF0.toInt(), ty and 0xFFFFFFF0.toInt(), gtz + 1, 15)
        }
        var dir: Byte
        var nx = gox
        var ny = goy
        while (gox != gtx || goy != gty) {
            var checkX = gridX + mdt.offsetX
            var checkY = (oy + m * (checkX - ox)).toInt()
            when {
                mdt.stepX != 0 && getGeoY(checkY) == goy -> {
                    debug?.let {
                        it.addPoint(mdt.symbolX, Color.CYAN, true, checkX, checkY, goz)
                        it.addSquare(Color.GREEN, gridX, gridY, goz, 15)
                    }
                    gridX += mdt.stepX
                    nx += mdt.signumX
                    dir = mdt.directionX
                }
                else -> {
                    checkY = gridY + mdt.offsetY
                    checkX = (ox + (checkY - oy) / m).toInt()
                    checkX = max(gridX, min(gridX + 15, checkX))
                    debug?.let {
                        it.addPoint(mdt.symbolY, Color.YELLOW, true, checkX, checkY, goz)
                        it.addSquare(Color.GREEN, gridX, gridY, goz, 15)
                    }
                    gridY += mdt.stepY
                    ny += mdt.signumY
                    dir = mdt.directionY
                }
            }
            when {
                nx < 0 || nx >= GeoStructure.GEO_CELLS_X || ny < 0 || ny >= GeoStructure.GEO_CELLS_Y -> {
                    debug?.addSquare(Color.RED, gridX, gridY, goz, 15)
                    return Location(checkX, checkY, goz)
                }
                (nswe.toInt() and dir.toInt()) == 0 -> {
                    debug?.addSquare(Color.RED, gridX, gridY, goz, 15)
                    return Location(checkX, checkY, goz)
                }
            }
            val block = getBlock(nx, ny)
            val i = block.getIndexBelow(nx, ny, goz + GeoStructure.CELL_IGNORE_HEIGHT, null)
            when {
                i < 0 -> {
                    debug?.addSquare(Color.RED, gridX, gridY, goz, 15)
                    return Location(checkX, checkY, goz)
                }
            }
            gox = nx
            goy = ny
            goz = block.getHeight(i, null).toInt()
            nswe = block.getNswe(i, null)
        }
        return if (goz == gtz) Location(tx, ty, gtz) else Location(ox, oy, oz)
    }
    
    fun computeLegacyPathRaw(
        gox: Int, goy: Int, goz: Int,
        gtx: Int, gty: Int, gtz: Int,
        debug: ExServerPrimitive?
    ): MutableList<Location> {
        val pf = ext.mods.gameserver.geoengine.pathfinding.PathFinder()
        return pf.findPath(gox, goy, goz, gtx, gty, gtz, debug).toMutableList()
    }
    fun findPath(
        ox: Int, oy: Int, oz: Int,
        tx: Int, ty: Int, tz: Int,
        playable: Boolean, debug: ExServerPrimitive?
    ): List<Location> {
        return findPath(ox, oy, oz, tx, ty, tz, playable, 500, debug)
    }
    fun findPath(
        ox: Int, oy: Int, oz: Int,
        tx: Int, ty: Int, tz: Int,
        @Suppress("UNUSED_PARAMETER") playable: Boolean, nearestZLimit: Int, debug: ExServerPrimitive?
    ): List<Location> {
        return when {
            World.isOutOfWorld(tx, ty) -> emptyList()
            else -> {
                val gox = getGeoX(ox)
                val goy = getGeoY(oy)
                when {
                    !hasGeoPos(gox, goy) -> emptyList()
                    else -> {
                        val goz = getHeightNearest(gox, goy, oz).toInt()
                        val gtx = getGeoX(tx)
                        val gty = getGeoY(ty)
                        when {
                            !hasGeoPos(gtx, gty) -> emptyList()
                            else -> {
                                val gtz = getHeightNearest(gtx, gty, tz).toInt()
                                when {
                                    abs(gtz - tz) > nearestZLimit -> emptyList()
                                    else -> {
                                        val computePath: () -> List<Location> = {
                                            val rawPath = if (Config.USE_L2BR_PATHFINDING) {
                                                val bridge = ext.mods.gameserver.geoengine.pathfinding.integration.GeoEngineBridge.getInstance()
                                                if (bridge.isInitialized()) {
                                                    val l2brPath = bridge.findPath(ox, oy, oz, tx, ty, tz, playable, debug)
                                                    if (l2brPath.isNotEmpty()) l2brPath.map { Location(it.x, it.y, it.z) }.toMutableList()
                                                    else computeLegacyPathRaw(gox, goy, goz, gtx, gty, gtz, debug)
                                                } else computeLegacyPathRaw(gox, goy, goz, gtx, gty, gtz, debug)
                                            } else computeLegacyPathRaw(gox, goy, goz, gtx, gty, gtz, debug)
                                            
                                            when {
                                                rawPath.isEmpty() -> emptyList()
                                                rawPath.size < 3 -> rawPath
                                                else -> {
                                                    val pointIterator = rawPath.listIterator()
                                                    var nodeAx = ox
                                                    var nodeAy = oy
                                                    var nodeAz = goz
                                                    when {
                                                        pointIterator.hasNext() -> {
                                                            var nodeB = pointIterator.next()
                                                            while (pointIterator.hasNext()) {
                                                                val nextIndex = pointIterator.nextIndex()
                                                                val nodeC = rawPath[nextIndex]
                                                                when {
                                                                    canMoveClear(nodeAx, nodeAy, nodeAz, nodeC.x, nodeC.y, nodeC.z, null) -> {
                                                                        pointIterator.remove()
                                                                        debug?.addPoint(Color.RED, nodeB.x, nodeB.y, nodeB.z)
                                                                    }
                                                                    else -> {
                                                                        nodeAx = nodeB.x
                                                                        nodeAy = nodeB.y
                                                                        nodeAz = nodeB.z
                                                                        debug?.addPoint(Color.GREEN, nodeB.x, nodeB.y, nodeB.z)
                                                                    }
                                                                }
                                                                nodeB = pointIterator.next()
                                                            }
                                                        }
                                                    }
                                                    debug?.let { dbg ->
                                                        var prev = Location(ox, oy, oz)
                                                        rawPath.forEachIndexed { index, next ->
                                                            dbg.addLine("Segment #${index + 1}", Color.GREEN, true, prev, next)
                                                            prev = next
                                                        }
                                                    }
                                                    rawPath
                                                }
                                            }
                                        }
                                        val path = if (Config.USE_PATHFINDING_POOL) {
                                            CoroutinePool.runPathfindingBlocking(computePath)
                                        } else {
                                            computePath()
                                        }
                                        path
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    fun getValidSwimLocation(ox: Int, oy: Int, oz: Int, tx: Int, ty: Int, tz: Int, debug: ExServerPrimitive?): Location {
        return when {
            World.isOutOfWorld(tx, ty) -> Location(ox, oy, oz)
            else -> {
                var gox = getGeoX(ox)
                var goy = getGeoY(oy)
                var block = getBlock(gox, goy)
                var index = block.getIndexBelow(gox, goy, oz + 8, null)
                
                when {
                    gox == getGeoX(tx) && goy == getGeoY(ty) -> {
                        when {
                            index == block.getIndexBelow(gox, goy, tz + 8, null) -> Location(tx, ty, tz)
                            else -> Location(ox, oy, oz)
                        }
                    }
                    else -> {
                        var groundZ = block.getHeight(index, null).toInt()
                        var nswe = block.getNswe(index, null)
                        val dx = tx - ox
                        val dy = ty - oy
                        val dz = tz - oz
                        val m = when {
                            tx == ox -> 0.0
                            else -> dy.toDouble() / dx
                        }
                        val mz = dz.toDouble() / sqrt((dx * dx + dy * dy).toDouble())
                        val gtx = getGeoX(tx)
                        val gty = getGeoY(ty)
                        val mdt = MoveDirectionType.getDirection(gtx - gox, gty - goy)
                        
                        var gridX = ox and 0xFFFFFFF0.toInt()
                        var gridY = oy and 0xFFFFFFF0.toInt()
                        
                        debug?.let {
                            it.addSquare(Color.BLUE, gridX, gridY, groundZ - 32, 15)
                            it.addSquare(Color.BLUE, tx and 0xFFFFFFF0.toInt(), ty and 0xFFFFFFF0.toInt(), tz - 32, 15)
                        }
                        
                        while (gox != gtx || goy != gty) {
                            var dir: Byte
                            var checkX = gridX + mdt.offsetX
                            var checkY = (oy + m * (checkX - ox)).toInt()
                            when {
                                mdt.stepX != 0 && getGeoY(checkY) == goy -> {
                                    debug?.let {
                                        it.addPoint(mdt.symbolX, Color.CYAN, true, checkX, checkY, groundZ)
                                        it.addSquare(Color.GREEN, gridX, gridY, groundZ, 15)
                                    }
                                    gridX += mdt.stepX
                                    gox += mdt.signumX
                                    dir = mdt.directionX
                                }
                                else -> {
                                    checkY = gridY + mdt.offsetY
                                    checkX = (ox + (checkY - oy) / m).toInt()
                                    checkX = max(gridX, min(gridX + 15, checkX))
                                    debug?.let {
                                        it.addPoint(mdt.symbolY, Color.YELLOW, true, checkX, checkY, groundZ)
                                        it.addSquare(Color.GREEN, gridX, gridY, groundZ, 15)
                                    }
                                    gridY += mdt.stepY
                                    goy += mdt.signumY
                                    dir = mdt.directionY
                                }
                            }
                            
                            block = getBlock(gox, goy)
                            val swimZ = oz + mz * sqrt(((checkX - ox) * (checkX - ox) + (checkY - oy) * (checkY - oy)).toDouble())
                            val canMove = (nswe.toInt() and dir.toInt()) != 0
                            
                            index = when {
                                canMove -> block.getIndexBelow(gox, goy, groundZ + GeoStructure.CELL_IGNORE_HEIGHT, null)
                                else -> block.getIndexAbove(gox, goy, groundZ - 2 * GeoStructure.CELL_HEIGHT, null)
                            }
                            
                            when {
                                index < 0 -> {
                                    debug?.addSquare(Color.RED, gridX, gridY, swimZ.toInt(), 15)
                                    return Location(gridX, gridY, swimZ.toInt())
                                }
                            }
                            
                            val z = block.getHeight(index, null).toInt()
                            
                            when {
                                canMove && z >= swimZ -> {
                                    groundZ = z
                                    nswe = block.getNswe(index, null)
                                }
                                !canMove && z > swimZ -> {
                                    debug?.let {
                                        it.addPoint(Color.RED, checkX, checkY, swimZ.toInt())
                                        it.addSquare(Color.RED, gridX, gridY, z, 15)
                                    }
                                    return Location(checkX, checkY, swimZ.toInt())
                                }
                            }
                            
                            index = block.getIndexBelow(gox, goy, swimZ.toInt(), null)
                            groundZ = block.getHeight(index, null).toInt()
                            nswe = block.getNswe(index, null)
                        }
                        
                        Location(tx, ty, tz)
                    }
                }
            }
        }
    }
    fun canFlyToTarget(obj: WorldObject, oheight: Double, target: WorldObject): Boolean =
        canFlyToTarget(obj.position, oheight, target.position)
    fun canFlyToTarget(obj: WorldObject, oheight: Double, position: Location): Boolean =
        canFlyToTarget(obj.position, oheight, position)
    fun canFlyToTarget(origin: Location, oheight: Double, target: Location): Boolean =
        canFlyToTarget(origin.x, origin.y, origin.z, oheight, target.x, target.y, target.z)
    fun canFlyToTarget(ox: Int, oy: Int, oz: Int, oheight: Double, tx: Int, ty: Int, tz: Int): Boolean =
        canFly(ox, oy, oz, oheight, tx, ty, tz, null)
    fun canFly(ox: Int, oy: Int, oz: Int, oheight: Double, tx: Int, ty: Int, tz: Int, debug: ExServerPrimitive?): Boolean {
        if (World.isOutOfWorld(tx, ty)) return false
        var gox = getGeoX(ox)
        var goy = getGeoY(oy)
        val gtx = getGeoX(tx)
        val gty = getGeoY(ty)
        var goz = getHeightNearest(gox, goy, oz).toInt()
        val m = if (tx == ox) 0.0 else (ty - oy).toDouble() / (tx - ox)
        val mz = (tz - oz).toDouble() / sqrt(((tx - ox) * (tx - ox) + (ty - oy) * (ty - oy)).toDouble())
        val mdt = MoveDirectionType.getDirection(gtx - gox, gty - goy)
        var gridX = ox and 0xFFFFFFF0.toInt()
        var gridY = oy and 0xFFFFFFF0.toInt()
        if (debug != null) {
            debug.addSquare(Color.BLUE, gridX, gridY, goz - 32, 15)
            debug.addSquare(Color.BLUE, tx and 0xFFFFFFF0.toInt(), ty and 0xFFFFFFF0.toInt(), tz - 32, 15)
        }
        while (gox != gtx || goy != gty) {
            var checkX = gridX + mdt.offsetX
            var checkY = (oy + m * (checkX - ox)).toInt()
            if (mdt.stepX != 0 && getGeoY(checkY) == goy) {
                gridX += mdt.stepX
                gox += mdt.signumX
            } else {
                checkY = gridY + mdt.offsetY
                checkX = (ox + (checkY - oy) / m).toInt()
                checkX = max(gridX, min(gridX + 15, checkX))
                gridY += mdt.stepY
                goy += mdt.signumY
            }
            val block = getBlock(gox, goy)
            var nextZ = oz + (mz * sqrt(((checkX - ox) * (checkX - ox) + (checkY - oy) * (checkY - oy)).toDouble())).toInt()
            var index = block.getIndexBelow(gox, goy, nextZ + oheight.toInt(), null)
            if (index < 0) {
                if (debug != null) debug.addSquare(Color.RED, gridX, gridY, nextZ - 32, 15)
                return false
            }
            goz = block.getHeight(index, null).toInt()
            if (goz > nextZ) {
                if (debug != null) debug.addSquare(Color.RED, gridX, gridY, nextZ - 32, 15)
                return false
            }
            index = block.getIndexAbove(gox, goy, nextZ, null)
            nextZ += oheight.toInt()
            if (index >= 0) {
                goz = block.getHeight(index, null).toInt()
                if (goz < nextZ) {
                    if (debug != null) debug.addSquare(Color.RED, gridX, gridY, nextZ - 32, 15)
                    return false
                }
            }
            if (debug != null) debug.addSquare(Color.GREEN, gridX, gridY, nextZ - 32, 15)
        }
        return true
    }
    fun getValidFlyLocation(
        ox: Int, oy: Int, oz: Int, oheight: Double,
        tx: Int, ty: Int, tz: Int,
        debug: ExServerPrimitive?
    ): Location {
        var gox = getGeoX(ox)
        var goy = getGeoY(oy)
        var goz = getHeightNearest(gox, goy, oz).toInt()
        val gtx = getGeoX(tx)
        val gty = getGeoY(ty)
        val m = if (tx == ox) 0.0 else (ty - oy).toDouble() / (tx - ox)
        val mz = (tz - oz).toDouble() / sqrt(((tx - ox) * (tx - ox) + (ty - oy) * (ty - oy)).toDouble())
        val mdt = MoveDirectionType.getDirection(gtx - gox, gty - goy)
        var gridX = ox and 0xFFFFFFF0.toInt()
        var gridY = oy and 0xFFFFFFF0.toInt()
        if (debug != null) {
            debug.addSquare(Color.BLUE, gridX, gridY, goz - 32, 15)
            debug.addSquare(Color.BLUE, tx and 0xFFFFFFF0.toInt(), ty and 0xFFFFFFF0.toInt(), tz - 32, 15)
        }
        var checkZ = oz
        while (gox != gtx || goy != gty) {
            var checkX = gridX + mdt.offsetX
            var checkY = (oy + m * (checkX - ox)).toInt()
            if (mdt.stepX != 0 && getGeoY(checkY) == goy) {
                gridX += mdt.stepX
                gox += mdt.signumX
            } else {
                checkY = gridY + mdt.offsetY
                checkX = (ox + (checkY - oy) / m).toInt()
                checkX = max(gridX, min(gridX + 15, checkX))
                gridY += mdt.stepY
                goy += mdt.signumY
            }
            if (gox < 0 || gox >= GeoStructure.GEO_CELLS_X || goy < 0 || goy >= GeoStructure.GEO_CELLS_Y) {
                if (debug != null) debug.addSquare(Color.RED, gridX, gridY, goz, 15)
                return Location(checkX, checkY, goz)
            }
            val block = getBlock(gox, goy)
            val bottomZ = oz + (mz * sqrt(((checkX - ox) * (checkX - ox) + (checkY - oy) * (checkY - oy)).toDouble())).toInt()
            var index = block.getIndexBelow(gox, goy, bottomZ + oheight.toInt(), null)
            if (index < 0) {
                if (debug != null) debug.addSquare(Color.RED, gridX, gridY, bottomZ - 32, 15)
                return Location(checkX, checkY, checkZ)
            }
            goz = block.getHeight(index, null).toInt()
            if (goz > bottomZ) {
                if (debug != null) debug.addSquare(Color.RED, gridX, gridY, bottomZ - 32, 15)
                return Location(checkX, checkY, checkZ)
            }
            index = block.getIndexAbove(gox, goy, bottomZ, null)
            val topZ = bottomZ + oheight.toInt()
            if (index >= 0) {
                goz = block.getHeight(index, null).toInt()
                if (goz < topZ) {
                    if (debug != null) debug.addSquare(Color.RED, gridX, gridY, topZ - 32, 15)
                    return Location(checkX, checkY, checkZ)
                }
            }
            if (debug != null) debug.addSquare(Color.GREEN, gridX, gridY, topZ - 32, 15)
            checkZ = bottomZ
        }
        return Location(tx, ty, tz)
    }
    fun addGeoBug(loc: Location, comment: String): Boolean {
        val gox = getGeoX(loc.x)
        val goy = getGeoY(loc.y)
        val goz = loc.z
        val rx = gox / GeoStructure.REGION_CELLS_X + World.TILE_X_MIN
        val ry = goy / GeoStructure.REGION_CELLS_Y + World.TILE_Y_MIN
        val bx = (gox / GeoStructure.BLOCK_CELLS_X) % GeoStructure.REGION_BLOCKS_X
        val by = (goy / GeoStructure.BLOCK_CELLS_Y) % GeoStructure.REGION_BLOCKS_Y
        val cx = gox % GeoStructure.BLOCK_CELLS_X
        val cy = goy % GeoStructure.BLOCK_CELLS_Y
        return try {
            _geoBugReports?.printf("%d;%d;%d;%d;%d;%d;%d;%s\r\n", rx, ry, bx, by, cx, cy, goz, comment.replace(";", ":"))
            true
        } catch (e: Exception) {
            GeoEngine.LOGGER.error("Couldn't save new entry to \"geo_bugs.txt\" file.", e)
            false
        }
    }
    companion object {
        private val LOGGER = CLogger(GeoEngine::class.java.name)
        
        private val _instance: GeoEngine by lazy { GeoEngine() }
        
        @JvmStatic
        fun getInstance(): GeoEngine = _instance
        
        @JvmStatic
        fun getGeoX(worldX: Int): Int = (worldX - World.WORLD_X_MIN) shr 4
        @JvmStatic
        fun getGeoY(worldY: Int): Int = (worldY - World.WORLD_Y_MIN) shr 4
        @JvmStatic
        fun getWorldX(geoX: Int): Int = (geoX shl 4) + World.WORLD_X_MIN + 8
        @JvmStatic
        fun getWorldY(geoY: Int): Int = (geoY shl 4) + World.WORLD_Y_MIN + 8
        @JvmStatic
        fun calculateGeoObject(inside: Array<BooleanArray>): Array<ByteArray> {
            val width = inside.size
            val height = inside[0].size
            val result = Array(width) { ByteArray(height) }
            for (ix in 0 until width) {
                for (iy in 0 until height) {
                    if (inside[ix][iy]) {
                        result[ix][iy] = GeoStructure.CELL_FLAG_NONE
                    } else {
                        var nswe = GeoStructure.CELL_FLAG_ALL
                        if (iy < height - 1 && inside[ix][iy + 1]) nswe = (nswe.toInt() and GeoStructure.CELL_FLAG_S.toInt().inv()).toByte()
                        if (iy > 0 && inside[ix][iy - 1]) nswe = (nswe.toInt() and GeoStructure.CELL_FLAG_N.toInt().inv()).toByte()
                        if (ix < width - 1 && inside[ix + 1][iy]) nswe = (nswe.toInt() and GeoStructure.CELL_FLAG_E.toInt().inv()).toByte()
                        if (ix > 0 && inside[ix - 1][iy]) nswe = (nswe.toInt() and GeoStructure.CELL_FLAG_W.toInt().inv()).toByte()
                        result[ix][iy] = nswe
                    }
                }
            }
            return result
        }
    }
}