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
package ext.mods.gameserver
import java.awt.GraphicsEnvironment
import java.io.FileInputStream
import java.net.InetAddress
import java.util.concurrent.CountDownLatch
import java.util.logging.Level
import java.util.logging.Logger
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.BiFunction
import java.util.logging.LogManager
import javax.swing.JOptionPane
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis
import ext.mods.commons.gui.InterfaceGS
import ext.mods.commons.lang.StringUtil
import ext.mods.commons.logging.CLogger
import ext.mods.commons.mmocore.SelectorConfig
import ext.mods.commons.mmocore.SelectorThread
import ext.mods.commons.network.IPv4Filter
import ext.mods.commons.pool.ConnectionPool
import ext.mods.commons.pool.ThreadPool
import ext.mods.commons.util.JvmOptimizer
import ext.mods.commons.util.SysUtil
import ext.mods.Config
import ext.mods.dressme.DressMeData
import ext.mods.dungeon.data.DungeonData
import ext.mods.email.task.EmailDeliveryTask
import ext.mods.extensions.ExtensionLoader
import ext.mods.extensions.listener.manager.GameListenerManager
import ext.mods.fakeplayer.FakePlayerManager
import ext.mods.gameserver.communitybbs.CommunityBoard
import ext.mods.gameserver.communitybbs.CustomCommunityBoard
import ext.mods.gameserver.communitybbs.custom.AuctionBBSManager
import ext.mods.gameserver.custom.data.AuctionCurrencies
import ext.mods.gameserver.custom.data.BalanceData
import ext.mods.gameserver.custom.data.BossHpAnnounceData
import ext.mods.gameserver.custom.data.DonateData
import ext.mods.gameserver.custom.data.EnchantData
import ext.mods.gameserver.custom.data.EquipGradeRestrictionData
import ext.mods.gameserver.custom.data.EventsData
import ext.mods.gameserver.custom.data.MissionData
import ext.mods.gameserver.custom.data.PolymorphData
import ext.mods.gameserver.custom.data.PvPData
import ext.mods.gameserver.custom.data.RaidDropAnnounceData
import ext.mods.gameserver.custom.data.RatesData
import ext.mods.gameserver.data.HTMLData
import ext.mods.gameserver.data.SkillTable
import ext.mods.gameserver.data.cache.CrestCache
import ext.mods.gameserver.data.manager.AntiFeedManager
import ext.mods.gameserver.data.manager.BufferManager
import ext.mods.gameserver.data.manager.BuyListManager
import ext.mods.gameserver.data.manager.CastleManager
import ext.mods.gameserver.data.manager.CastleManorManager
import ext.mods.gameserver.data.manager.ClanHallManager
import ext.mods.gameserver.data.manager.CoupleManager
import ext.mods.gameserver.data.manager.CursedWeaponManager
import ext.mods.gameserver.data.manager.DerbyTrackManager
import ext.mods.gameserver.data.manager.EventsDropManager
import ext.mods.gameserver.data.manager.FestivalOfDarknessManager
import ext.mods.gameserver.data.manager.FishingChampionshipManager
import ext.mods.gameserver.data.manager.HeroManager
import ext.mods.gameserver.data.manager.LotteryManager
import ext.mods.gameserver.data.manager.PartyMatchRoomManager
import ext.mods.gameserver.data.manager.PcCafeManager
import ext.mods.gameserver.data.manager.PetitionManager
import ext.mods.gameserver.data.manager.RaidPointManager
import ext.mods.gameserver.data.manager.SellBuffsManager
import ext.mods.gameserver.data.manager.SevenSignsManager
import ext.mods.gameserver.data.manager.SpawnManager
import ext.mods.gameserver.data.manager.ZoneManager
import ext.mods.gameserver.data.sql.BookmarkTable
import ext.mods.gameserver.data.sql.ClanTable
import ext.mods.gameserver.data.sql.OfflineTradersTable
import ext.mods.gameserver.data.sql.PlayerInfoTable
import ext.mods.gameserver.data.sql.ServerMemoTable
import ext.mods.gameserver.data.xml.AdminData
import ext.mods.gameserver.data.xml.AnnouncementData
import ext.mods.gameserver.data.xml.ArmorSetData
import ext.mods.gameserver.data.xml.AugmentationData
import ext.mods.gameserver.data.xml.BoatData
import ext.mods.gameserver.data.xml.DoorData
import ext.mods.gameserver.data.xml.FishData
import ext.mods.gameserver.data.xml.HealSpsData
import ext.mods.gameserver.data.xml.HennaData
import ext.mods.gameserver.data.xml.InstantTeleportData
import ext.mods.gameserver.data.xml.ItemData
import ext.mods.gameserver.data.xml.ManorAreaData
import ext.mods.gameserver.data.xml.MultisellData
import ext.mods.gameserver.data.xml.NewbieBuffData
import ext.mods.gameserver.data.xml.NpcData
import ext.mods.gameserver.data.xml.ObserverGroupData
import ext.mods.gameserver.data.xml.PlayerData
import ext.mods.gameserver.data.xml.PlayerLevelData
import ext.mods.gameserver.data.xml.RecipeData
import ext.mods.gameserver.data.xml.RestartPointData
import ext.mods.gameserver.data.xml.ScriptData
import ext.mods.gameserver.data.xml.SkillTreeData
import ext.mods.gameserver.data.xml.SkipData
import ext.mods.gameserver.data.xml.SoulCrystalData
import ext.mods.gameserver.data.xml.SpellbookData
import ext.mods.gameserver.data.xml.StaticObjectData
import ext.mods.gameserver.data.xml.StaticSpawnData
import ext.mods.gameserver.data.xml.SummonItemData
import ext.mods.gameserver.data.xml.SysString
import ext.mods.gameserver.data.xml.TeleportData
import ext.mods.gameserver.data.xml.WalkerRouteData
import ext.mods.gameserver.geoengine.GeoEngine
import ext.mods.gameserver.handler.AdminCommandHandler
import ext.mods.gameserver.handler.BypassHandler
import ext.mods.gameserver.handler.ChatHandler
import ext.mods.gameserver.handler.ItemHandler
import ext.mods.gameserver.handler.SkillHandler
import ext.mods.gameserver.handler.TargetHandler
import ext.mods.gameserver.handler.UserCommandHandler
import ext.mods.gameserver.handler.VoicedCommandHandler
import ext.mods.gameserver.handler.voicedcommandhandlers.Epic
import ext.mods.gameserver.handler.voicedcommandhandlers.Raid
import ext.mods.gameserver.idfactory.IdFactory
import ext.mods.gameserver.listener.AgathionTeleportListener
import ext.mods.gameserver.model.World
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager
import ext.mods.gameserver.model.entity.autofarm.AutoFarmTask
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFManager
import ext.mods.gameserver.model.entity.events.deathmatch.DMManager
import ext.mods.gameserver.model.entity.events.lastman.LMManager
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTManager
import ext.mods.gameserver.model.memo.GlobalMemo
import ext.mods.gameserver.model.olympiad.Olympiad
import ext.mods.gameserver.model.olympiad.OlympiadGameManager
import ext.mods.gameserver.network.GameClient
import ext.mods.gameserver.network.GamePacketHandler
import ext.mods.gameserver.taskmanager.AiTaskManager
import ext.mods.gameserver.taskmanager.AttackStanceTaskManager
import ext.mods.gameserver.taskmanager.BoatTaskManager
import ext.mods.gameserver.taskmanager.DecayTaskManager
import ext.mods.gameserver.taskmanager.DelayedItemsManager
import ext.mods.gameserver.taskmanager.GameTimeTaskManager
import ext.mods.gameserver.taskmanager.InventoryUpdateTaskManager
import ext.mods.gameserver.taskmanager.ItemInstanceTaskManager
import ext.mods.gameserver.taskmanager.ItemsOnGroundTaskManager
import ext.mods.gameserver.taskmanager.MakerSpawnScheduleTaskManager
import ext.mods.gameserver.taskmanager.PvpFlagTaskManager
import ext.mods.gameserver.taskmanager.ShadowItemTaskManager
import ext.mods.gameserver.taskmanager.WaterTaskManager
import ext.mods.playergod.data.PlayerGodData
import ext.mods.protection.hwid.hwid
import ext.mods.quests.QuestData
import ext.mods.roulette.RouletteData
import ext.mods.security.gui.LauncherApp
import ext.mods.security.LicenseValidator
import ext.mods.sellBuffEngine.BuffShopConfigs
import ext.mods.sellBuffEngine.BuffShopManager
import ext.mods.summonmobitem.SummonMobItemData
import ext.mods.tour.TourData
//import ext.mods.util.CryptaManager
import ext.mods.Crypta.BattleBossData
import ext.mods.Crypta.AgathionData
import ext.mods.Crypta.RandomManager
import ext.mods.FarmEventRandom.RandomData
import ext.mods.battlerboss.tasks.BattleBossCountDownTask
import ext.mods.CapsuleBox.CapsuleBoxData
import ext.mods.extensions.listener.manager.PlayerListenerManager
private object LoadMetricsService {
    private val metrics = java.util.Collections.synchronizedMap(mutableMapOf<String, Long>())
    fun record(moduleName: String, durationMs: Long) {
        metrics[moduleName] = durationMs
    }
    fun getReportSortedBySlowest(): String {
        return metrics.entries
            .sortedByDescending { it.value }
            .joinToString("\n") { (name, ms) ->
                "  %.2fs - $name".format(ms / 1000.0)
            }
    }
    fun clear() = metrics.clear()
}
class GameServer : Runnable {
    var selectorThread: SelectorThread<GameClient>? = null
        private set
    private var isServerCrash = false
    
    private val doorsCastlesTasksLatch = CountDownLatch(1)
    private val doorsCastlesTasksReady = AtomicBoolean(false)
    
    private val npcsSpawnsLatch = CountDownLatch(1)
    private val npcsAndSpawnsReady = AtomicBoolean(false)
    var serverLoadStart = System.currentTimeMillis()
    var serverStartTimeMillis: Long = 0
        private set
    fun getServerStartTime(): Long = serverStartTimeMillis
    companion object {
        private val LOGGER = CLogger(GameServer::class.java.name)
        @JvmStatic
        var instance: GameServer? = null
            private set
        @JvmStatic
        fun main(args: Array<String>) {
            if ("true".equals(System.getProperty("brproject.safe.graphics"), ignoreCase = true)) {
                System.setProperty("sun.java2d.opengl", "false")
                System.setProperty("sun.java2d.d3d", "false")
                System.setProperty("sun.java2d.pmoffscreen", "false")
            }
            JvmOptimizer.initialize()
            GameServer(args)
        }
        private fun createDirectories() {
            try {
                createDirectory("log")
                createDirectory("log/drop")
                createDirectory("log/chat")
                createDirectory("log/console")
                createDirectory("log/error")
                createDirectory("log/gmaudit")
                createDirectory("log/item")
                createDataDirectory("crests")
            } catch (e: Exception) {
                LOGGER.error("Failed to create directories.", e)
            }
        }
        private fun createDirectory(path: String) {
            var p = Config.BASE_PATH
            if (Config.DEV_MODE) p = p.resolve("dev").resolve("game")
            p.resolve(path).toFile().mkdirs()
        }
        private fun createDataDirectory(path: String) {
            Config.DATA_PATH.resolve(path).toFile().mkdirs()
        }
        private fun getTimeUntilMidnight(): Long {
            val now = System.currentTimeMillis()
            val tomorrowMidnight = ((now / (24 * 60 * 60 * 1000)) + 1) * (24 * 60 * 60 * 1000)
            return tomorrowMidnight - now
        }
        @JvmStatic
        fun customMods() {
            StringUtil.printSection("Custom Mods: initialization...")
            BalanceData.getInstance().init()
            RatesData.getInstance()
            if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS) {
                OfflineTradersTable.getInstance().restore()
            }
            CoupleManager.getInstance()
            EventsData.getInstance()
            EventsDropManager.getInstance()
            CTFManager.getInstance()
            DMManager.getInstance()
            LMManager.getInstance()
            TvTManager.getInstance()
            EnchantData.getInstance()
            AntiFeedManager.getInstance().registerEvent(AntiFeedManager.GAME_ID)
            PcCafeManager.getInstance()
            StringUtil.printSection("CapsuleBox - Terius")
            CapsuleBoxData.getInstance()
            StaticSpawnData.getInstance()
            Raid.load()
            Epic.load()
            AuctionBBSManager.getInstance().load()
            AuctionCurrencies.getInstance()
            AutoFarmManager.getInstance()
            AutoFarmTask.getInstance()
            if (Config.SELLBUFF_ENABLED) SellBuffsManager.getInstance()
            DonateData.getInstance()
            MissionData.getInstance()
            PvPData.getInstance()
            //CryptaManager.executeMethod("AgathionData", "getInstance")
            AgathionData.getInstance()
            PolymorphData.getInstance()
            RaidDropAnnounceData.getInstance()
            EquipGradeRestrictionData.getInstance()
            BossHpAnnounceData.getInstance()
            ext.mods.BossZerg.BossZergManager.getInstance()
            RandomData.getInstance()
            RandomManager.getInstance().start()
            
            DungeonData.getInstance()
            RouletteData.getInstance()
            PlayerGodData.getInstance()
            DressMeData.getInstance()
            TourData.getInstance()
            ext.mods.levelupmaker.LevelUpMakerManager.getInstance().init()
            EmailDeliveryTask.getInstance().loadAllPending()
            FakePlayerManager.getInstance().initialise()
            if (Config.ALLOW_GUARD_SYSTEM) {
                hwid.Init()
            } else {
                LOGGER.info("Hwid Manager is disabled.")
            }
            try {
                BuffShopConfigs.getInstance().loadConfigs()
                BuffShopManager.getInstance().restoreOfflineTraders()
            } catch (e: Exception) {
                LOGGER.error("Failed to initialize Buff Shop System: " + e.message)
            }
            QuestData.getInstance()
            //CryptaManager.executeMethod("BattleBossData", "getInstance")
            BattleBossData.getInstance()
            BattleBossCountDownTask.getInstance().start()
            StringUtil.printSection("[Brproject Ext Mods]")
            ExtensionLoader.loadExtensions()
            GameListenerManager.getInstance().notifyStart()
            try {
                val agathionListener = AgathionTeleportListener()
                PlayerListenerManager.getInstance().registerTeleportListener(agathionListener)
            } catch (e: Exception) {
                LOGGER.error("Failed to initialize AgathionTeleportListener: " + e.message)
            }
        }
    }
    constructor() : this(emptyArray())
    constructor(args: Array<String>) {
        instance = this
        setupEnvironment(args)
        runBlocking {
            loadParallel()
        }
        startNetwork()
        scheduleDeferredDoorsCastlesTasks()
        scheduleDeferredNpcsSpawns()
        val os = System.getProperty("os.name").lowercase()
        if (os.contains("win") || os.contains("mac")) {
            playServerLoadedSound()
        }
    }
    private fun setupEnvironment(args: Array<String>) {
        if (Config.DEV_MODE) {
            Config.BASE_PATH.resolve("dev").resolve("game").toFile().mkdirs()
        }
        createDirectories()
        configureLogging()
        initGUI()
        if (args.isNotEmpty()) LauncherApp.setKey(args[0])
        else System.getenv("L2_KEY")?.let { LauncherApp.setKey(it) }
        if (args.size > 1) LauncherApp.setLoggedUserEmail(args[1])
        val expiryDate = LicenseValidator.checkLicenseAndGetExpiry(
            LicenseValidator.getPublicIPAddress(),
            LauncherApp.getKey(),
            LauncherApp.getLoggedUserEmail()
        )
        if (expiryDate == null) {
            JOptionPane.showMessageDialog(null, "Licença inválida.")
            Thread.sleep(10000)
            System.exit(0)
        }
    }
    private fun configureLogging() {
        try {
            FileInputStream(Config.CONFIG_PATH.resolve("logging.properties").toFile()).use { `is` ->
                LogManager.getLogManager().updateConfiguration(`is`) { key ->
                    BiFunction { _: String?, newValue: String? ->
                        if (key.endsWith(".pattern")) {
                            val v = newValue ?: ""
                            if (Config.DEV_MODE) Config.BASE_PATH.resolve("dev").resolve("game").resolve(v).toString()
                            else Config.BASE_PATH.resolve(v).toString()
                        } else newValue
                    }
                }
            }
        } catch (_: Exception) { }
    }
    private fun initGUI() {
        val os = System.getProperty("os.name").lowercase()
        if ((os.contains("win") || os.contains("mac")) && !GraphicsEnvironment.isHeadless()) {
            try {
                InterfaceGS()
                println("Game: Running in Interface GUI.")
            } catch (t: Throwable) {
                println("Game: Fallback to console mode.")
            }
        }
    }
    private suspend fun loadParallel() = coroutineScope {
        LoadMetricsService.clear()
        val totalTime = measureTimeMillis {
            LoadMetricsService.record("Config & Pools", measureTimeMillis { loadConfigAndPools() })
            listOf(
                async(Dispatchers.IO) { "World & Cache" to measureTimeMillis { loadWorldAndCache() } },
                async(Dispatchers.IO) { "IdFactory" to measureTimeMillis { loadIdFactory() } }
            ).awaitAll().forEach { (name, ms) -> LoadMetricsService.record(name, ms) }
            listOf(
                async(Dispatchers.IO) { "Skills" to measureTimeMillis { loadSkills() } },
                async(Dispatchers.IO) { "Items (massive)" to measureTimeMillis { loadItemsMassive() } },
                async(Dispatchers.IO) { "Admins & Chars" to measureTimeMillis { loadAdminsCharacters() } }
            ).awaitAll().forEach { (name, ms) -> LoadMetricsService.record(name, ms) }
            listOf(
                async(Dispatchers.Default) { "Geo & Zones" to measureTimeMillis { loadGeoAndZones() } }
            ).awaitAll().forEach { (name, ms) -> LoadMetricsService.record(name, ms) }
            listOf(
                async(Dispatchers.IO) { "Olympiad & Scripts" to measureTimeMillis { loadOlympiadScripts() } }
            ).awaitAll().forEach { (name, ms) -> LoadMetricsService.record(name, ms) }
            listOf(
                async(Dispatchers.IO) { "Events" to measureTimeMillis { loadEvents() } },
                async(Dispatchers.IO) { "Custom Mods" to measureTimeMillis { loadCustomMods() } }
            ).awaitAll().forEach { (name, ms) -> LoadMetricsService.record(name, ms) }
            LoadMetricsService.record("Handlers", measureTimeMillis { loadHandlers() })
            Runtime.getRuntime().addShutdownHook(Shutdown.getInstance())
            
        }
        LOGGER.info("Server loaded in %.2f seconds (Otimized Mode)".format(totalTime / 1000.0))
        LOGGER.info("{}", ConnectionPool.getStats())
    }
    private fun loadConfigAndPools() {
        StringUtil.printSection("Config")
        Config.loadGameServer()
        StringUtil.printSection("Poolers")
        ConnectionPool.init()
        ThreadPool.init()
    }
    private fun loadIdFactory() {
        StringUtil.printSection("IdFactory")
        IdFactory.getInstance()
    }
    private fun loadWorldAndCache() {
        StringUtil.printSection("Cache & World")
        HTMLData.getInstance().load()
        SysString.getInstance().load()
        CrestCache.getInstance()
        World.getInstance()
        AnnouncementData.getInstance()
        ServerMemoTable.getInstance()
        GlobalMemo.getInstance()
        isServerCrash = ServerMemoTable.getInstance().getBool("server_crash", false)
    }
    private fun loadSkills() {
        StringUtil.printSection("Skills")
        SkillTable.getInstance()
        SkillTreeData.getInstance()
    }
    private fun loadItemsMassive() {
        StringUtil.printSection("Items")
        ItemData.getInstance()
        SummonItemData.getInstance()
        SummonMobItemData.getInstance()
        HennaData.getInstance()
        BuyListManager.getInstance()
        MultisellData.getInstance()
        RecipeData.getInstance()
        ArmorSetData.getInstance()
        FishData.getInstance()
        SpellbookData.getInstance()
        SoulCrystalData.getInstance()
        AugmentationData.getInstance()
        CursedWeaponManager.getInstance()
        SkipData.getInstance()
    }
    private fun loadAdminsCharacters() {
        StringUtil.printSection("Admins & Chars")
        AdminData.getInstance()
        BookmarkTable.getInstance()
        PetitionManager.getInstance()
        PlayerData.getInstance()
        PlayerInfoTable.getInstance()
        PlayerLevelData.getInstance()
        PartyMatchRoomManager.getInstance()
        RaidPointManager.getInstance()
        HealSpsData.getInstance()
        RestartPointData.getInstance()
        StringUtil.printSection("Clans")
        ClanTable.getInstance()
    }
    private fun loadGeoAndZones() {
        StringUtil.printSection("Geodata & Zones")
        GeoEngine.getInstance()
        ext.mods.gameserver.geoengine.pathfinding.PathfinderCache.getInstance()
        ext.mods.gameserver.geoengine.pathfinding.PathfinderGenerator.getInstance().startAutoGeneration()
        ZoneManager.getInstance()
    }
    private fun loadDoorsCastlesTasks(quiet: Boolean = false) {
        if (!quiet) StringUtil.printSection("Doors & Castles")
        if (quiet) suppressDoorsCastlesLogs(true)
        try {
            DoorData.getInstance().spawn()
            CastleManager.getInstance()
            ClanHallManager.getInstance()
            if (!quiet) StringUtil.printSection("Task Managers")
            AiTaskManager.getInstance()
            AttackStanceTaskManager.getInstance()
            BoatTaskManager.getInstance()
            DecayTaskManager.getInstance()
            DelayedItemsManager.getInstance()
            GameTimeTaskManager.getInstance()
            ItemsOnGroundTaskManager.getInstance()
            MakerSpawnScheduleTaskManager.getInstance()
            PvpFlagTaskManager.getInstance()
            ShadowItemTaskManager.getInstance()
            WaterTaskManager.getInstance()
            InventoryUpdateTaskManager.getInstance()
            ItemInstanceTaskManager.getInstance()
            ThreadPool.scheduleAtFixedRate({
                try {
                    SummonMobItemData.getInstance().resetDailyCounters()
                } catch (e: Exception) {
                    LOGGER.error("Failed to reset daily summon item counters: " + e.message)
                }
            }, getTimeUntilMidnight(), 24 * 60 * 60 * 1000)
            SevenSignsManager.getInstance()
            FestivalOfDarknessManager.getInstance()
            ManorAreaData.getInstance()
            CastleManorManager.getInstance()
        } finally {
            if (quiet) suppressDoorsCastlesLogs(false)
        }
    }
    private val DOORS_CASTLES_LOGGER_NAMES = listOf(
        "ext.mods.commons.data.xml.IXmlReader",
        "ext.mods.commons.lang.StringUtil",
        "ext.mods.gameserver.data.manager.SevenSignsManager",
        "ext.mods.gameserver.data.manager.FestivalOfDarknessManager"
    )
    private val _suppressedDoorsLoggerLevels = mutableMapOf<String, Level?>()
    private fun suppressDoorsCastlesLogs(enable: Boolean) {
        for (name in DOORS_CASTLES_LOGGER_NAMES) {
            val log = Logger.getLogger(name)
            if (enable) {
                _suppressedDoorsLoggerLevels[name] = log.level
                log.level = Level.WARNING
            } else {
                log.level = _suppressedDoorsLoggerLevels[name] ?: Level.INFO
                _suppressedDoorsLoggerLevels.remove(name)
            }
        }
    }
    
    private fun scheduleDeferredDoorsCastlesTasks() {
        ThreadPool.execute {
            try {
                val ms = measureTimeMillis { loadDoorsCastlesTasks(quiet = true) }
                doorsCastlesTasksReady.set(true)
                doorsCastlesTasksLatch.countDown()
                LoadMetricsService.record("Doors & Castles & Tasks (deferred)", ms)
            } catch (e: Exception) {
                LOGGER.error("Erro ao carregar Doors & Castles & Tasks em background", e)
                doorsCastlesTasksReady.set(false)
                doorsCastlesTasksLatch.countDown()
            }
        }
    }
    private fun loadNpcsSpawns(quiet: Boolean = false) {
        if (!quiet) {
            StringUtil.printSection("NPCs")
        }
        if (quiet) suppressNpcSpawnLogs(true)
        try {
            BufferManager.getInstance()
            NpcData.getInstance()
            WalkerRouteData.getInstance()
            StaticObjectData.getInstance()
            SpawnManager.getInstance()
            NewbieBuffData.getInstance()
            InstantTeleportData.getInstance()
            TeleportData.getInstance()
            ObserverGroupData.getInstance()
            CastleManager.getInstance().spawnEntities()
            if (!quiet) StringUtil.printSection("Spawns")
            if (Config.PTS_EMULATION_SPAWN) {
                ThreadPool.schedule(NpcSpawn(), (Config.PTS_EMULATION_SPAWN_DURATION * 1000).toLong())
            } else {
                SpawnManager.getInstance().spawn()
            }
        } finally {
            if (quiet) suppressNpcSpawnLogs(false)
        }
    }
    private val NPC_SPAWN_LOGGER_NAMES = listOf(
        "ext.mods.commons.data.xml.IXmlReader",
        "ext.mods.commons.lang.StringUtil"
    )
    private val _suppressedLoggerLevels = mutableMapOf<String, Level?>()
    private fun suppressNpcSpawnLogs(enable: Boolean) {
        for (name in NPC_SPAWN_LOGGER_NAMES) {
            val log = Logger.getLogger(name)
            if (enable) {
                _suppressedLoggerLevels[name] = log.level
                log.level = Level.WARNING
            } else {
                log.level = _suppressedLoggerLevels[name] ?: Level.INFO
                _suppressedLoggerLevels.remove(name)
            }
        }
    }
    private fun loadOlympiadScripts() {
        StringUtil.printSection("Olympiad & Scripts")
        OlympiadGameManager.getInstance()
        Olympiad.getInstance()
        HeroManager.getInstance()
        ScriptData.getInstance()
        if (Config.ALLOW_BOAT) BoatData.getInstance().load()
        StringUtil.printSection("Community")
        if (Config.ENABLE_CUSTOM_BBS) CustomCommunityBoard.getInstance()
        if (Config.ENABLE_COMMUNITY_BOARD) CommunityBoard.getInstance()
    }
    private fun loadEvents() {
        DerbyTrackManager.getInstance()
        LotteryManager.getInstance()
        if (Config.ALLOW_FISH_CHAMPIONSHIP) FishingChampionshipManager.getInstance()
    }
    private fun loadCustomMods() {
        customMods()
        //CryptaManager.initialize()
    }
    private fun loadHandlers() {
        LOGGER.info("Loaded ${AdminCommandHandler.getInstance().size()} admin handlers.")
        LOGGER.info("Loaded ${BypassHandler.getInstance().size()} bypass handlers.")
        LOGGER.info("Loaded ${ChatHandler.getInstance().size()} chat handlers.")
        LOGGER.info("Loaded ${ItemHandler.getInstance().size()} item handlers.")
        LOGGER.info("Loaded ${SkillHandler.getInstance().size()} skill handlers.")
        LOGGER.info("Loaded ${TargetHandler.getInstance().size()} target handlers.")
        LOGGER.info("Loaded ${UserCommandHandler.getInstance().size()} user handlers.")
        LOGGER.info("Loaded ${VoicedCommandHandler.getInstance().size()} voiced handlers.")
    }
    
    private fun scheduleDeferredNpcsSpawns() {
        ThreadPool.execute {
            try {
                doorsCastlesTasksLatch.await(120, TimeUnit.SECONDS)
                val ms = measureTimeMillis { loadNpcsSpawns(quiet = true) }
                npcsAndSpawnsReady.set(true)
                npcsSpawnsLatch.countDown()
                LoadMetricsService.record("NPCs & Spawns (deferred)", ms)
            } catch (e: Exception) {
                LOGGER.error("Erro ao carregar NPCs/Spawns em background", e)
                npcsAndSpawnsReady.set(false)
                npcsSpawnsLatch.countDown()
            }
        }
    }
    
    fun awaitNpcsSpawnsReady() {
        if (!npcsAndSpawnsReady.get()) {
            npcsSpawnsLatch.await(120, TimeUnit.SECONDS)
        }
    }
    private fun startNetwork() {
        serverStartTimeMillis = System.currentTimeMillis()
        if (isServerCrash) {
            LOGGER.info("Server crashed on last session!")
        } else {
            ServerMemoTable.getInstance().set("server_crash", true)
        }
        LOGGER.info("Gameserver used memory: {} / {} Mo.", SysUtil.getUsedMemory(), SysUtil.getMaxMemory())
        LOGGER.info("Maximum allowed players: {}.", Config.MAXIMUM_ONLINE_USERS)
        StringUtil.printSection("Login")
        LoginServerThread.getInstance().start()
        val sc = SelectorConfig()
        sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS
        sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS
        sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME
        sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT
        val handler = GamePacketHandler()
        selectorThread = SelectorThread(sc, handler, handler, handler, IPv4Filter())
        var bindAddress: InetAddress? = null
        if (Config.GAMESERVER_HOSTNAME != "*") {
            try {
                bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME)
            } catch (e: Exception) {
                LOGGER.error("The GameServer bind address is invalid, using all available IPs.", e)
            }
        }
        try {
            selectorThread?.openServerSocket(bindAddress, Config.GAMESERVER_PORT)
        } catch (e: Exception) {
            LOGGER.error("Failed to open server socket.", e)
            System.exit(1)
        }
        selectorThread?.start()
    }
    override fun run() {}
    inner class NpcSpawn : Runnable {
        override fun run() {
            LOGGER.info("Emulation npc spawn: Task initialization...")
            SpawnManager.getInstance().spawn()
        }
    }
    fun playServerLoadedSound() {
    }
}