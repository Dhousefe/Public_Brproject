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
package ext.mods.commons.pool
import ext.mods.Config
import kotlinx.coroutines.*
import java.util.concurrent.*
import java.util.concurrent.atomic.LongAdder
import java.util.logging.Logger
object CoroutinePool {
    private val LOGGER: Logger = Logger.getLogger(CoroutinePool::class.java.name)
    private var scheduledExecutors: Array<ScheduledThreadPoolExecutor>? = null
    private var instantExecutors: Array<ThreadPoolExecutor>? = null
    private var pathfindingExecutor: ThreadPoolExecutor? = null
    private var virtualExecutor: ExecutorService? = null
    private var forkJoinPool: ForkJoinPool? = null
    private val BackgroundScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    val PathfindingDispatcher: CoroutineDispatcher
        get() = pathfindingExecutor?.asCoroutineDispatcher() ?: Dispatchers.Default
    private val rejectedHandler = RejectedExecutionHandlerOptimized()
    private val totalTasksSubmitted = LongAdder()
    private val totalTasksCompleted = LongAdder()
    private val totalTasksRejected = LongAdder()
    private val totalExecutionTimeMs = LongAdder()
    private val pathfindingTasksSubmitted = LongAdder()
    private val pathfindingTasksCompleted = LongAdder()
    private const val MAX_DELAY_MS: Long = 2_000_000_000L
    
    @JvmStatic 
    fun init() {
        try {
            if (scheduledExecutors != null) {
                LOGGER.warning("CoroutinePool já foi inicializado. Ignorando nova inicialização.")
                return
            }
            val availableProcessors = Runtime.getRuntime().availableProcessors()
            val totalScheduled = Config.SCHEDULED_THREAD_POOL_COUNT.let {
                if (it == -1) availableProcessors * 4 else it
            }.coerceIn(1, 256)
            val scheduledPoolCount = minOf(4, totalScheduled).coerceAtLeast(1)
            val scheduledPoolCoreSize = (totalScheduled / scheduledPoolCount).coerceAtLeast(1)
            scheduledExecutors = Array(scheduledPoolCount) { index ->
                val threadProvider = ThreadProvider("Scheduled-Pool-$index", Thread.NORM_PRIORITY, true)
                ScheduledThreadPoolExecutor(
                    scheduledPoolCoreSize,
                    threadProvider as ThreadFactory
                ).apply {
                    setRemoveOnCancelPolicy(true)
                    setExecuteExistingDelayedTasksAfterShutdownPolicy(false)
                }
            }
            val totalInstant = Config.INSTANT_THREAD_POOL_COUNT.let {
                if (it == -1) availableProcessors * 2 else it
            }.coerceIn(1, 256)
            val instantPoolCount = minOf(2, totalInstant).coerceAtLeast(1)
            val instantPoolCoreSize = (totalInstant / instantPoolCount).coerceAtLeast(1)
            val instantPoolMaxSize = (instantPoolCoreSize * 2).coerceAtMost(256)
            instantExecutors = Array(instantPoolCount) { index ->
                val threadProvider = ThreadProvider("Instant-Pool-$index", Thread.NORM_PRIORITY, true)
                ThreadPoolExecutor(
                    instantPoolCoreSize,
                    instantPoolMaxSize,
                    60L, TimeUnit.SECONDS,
                    LinkedBlockingQueue(5000),
                    threadProvider as ThreadFactory,
                    rejectedHandler
                ).apply {
                    allowCoreThreadTimeOut(true)
                }
            }
            
            virtualExecutor = Executors.newVirtualThreadPerTaskExecutor().apply {
                LOGGER.info("Virtual Thread Executor inicializado")
            }
            
            val pathfindingThreads = Config.PATHFINDING_THREADS.let {
                if (it == -1) (availableProcessors / 2).coerceAtLeast(2) else it
            }.coerceAtLeast(2)
            
            val pathfindingThreadProvider = ThreadProvider(
                "Pathfinding-Thread",
                Thread.NORM_PRIORITY - 1,
                true
            )
            
            pathfindingExecutor = ThreadPoolExecutor(
                pathfindingThreads,
                pathfindingThreads,
                60L, TimeUnit.SECONDS,
                LinkedBlockingQueue(200),
                pathfindingThreadProvider as ThreadFactory,
                ThreadPoolExecutor.CallerRunsPolicy()
            ).apply {
                allowCoreThreadTimeOut(false)
                prestartAllCoreThreads()
            }
            
            forkJoinPool = ForkJoinPool(
                availableProcessors,
                ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                null,
                true
            )
            
            LOGGER.info("=== Inicializado ===")
            LOGGER.info("  - Scheduled: $scheduledPoolCount pools x $scheduledPoolCoreSize threads (total $totalScheduled)")
            LOGGER.info("  - Instant: $instantPoolCount pools x core $instantPoolCoreSize max $instantPoolMaxSize (total até $totalInstant)")
            LOGGER.info("  - Virtual Thread Executor: Ativo (I/O-bound tasks)")
            LOGGER.info("  - Pathfinding Threads: $pathfindingThreads (CPU-bound)")
            LOGGER.info("  - JVM: ${System.getProperty("java.version")}")
            
        } catch (e: Exception) {
            LOGGER.severe("ERRO CRÍTICO ao inicializar CoroutinePool: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }
    
    @JvmStatic 
    fun execute(r: Runnable) {
        val executors = instantExecutors
        
        if (executors == null || executors.isEmpty()) {
            LOGGER.warning("CoroutinePool não inicializado. Executando tarefa de emergência.")
            try { r.run() } catch (e: Exception) { e.printStackTrace() }
            return
        }
        
        try {
            totalTasksSubmitted.increment()
            val startTime = System.nanoTime()
            
            val selectedPool = executors[ThreadLocalRandom.current().nextInt(executors.size)]
            
            selectedPool.execute {
                try {
                    r.run()
                    totalTasksCompleted.increment()
                    totalExecutionTimeMs.add((System.nanoTime() - startTime) / 1_000_000)
                } catch (e: Throwable) {
                    val t = Thread.currentThread()
                    t.uncaughtExceptionHandler?.uncaughtException(t, e)
                }
            }
        } catch (e: RejectedExecutionException) {
            totalTasksRejected.increment()
            LOGGER.warning("Tarefa rejeitada (pool cheio). Executando na thread atual.")
            val pool0 = instantExecutors?.getOrNull(0)
            if (pool0 == null || pool0.isShutdown) {
                try { r.run() } catch (ex: Exception) { ex.printStackTrace() }
            } else {
                rejectedHandler.rejectedExecution(r, pool0)
            }
        } catch (e: Exception) {
            LOGGER.severe("Erro ao executar tarefa instantânea: ${e.message}")
        }
    }
    
    @JvmStatic
    fun executeIO(task: Runnable) {
        val executor = virtualExecutor
        
        if (executor == null) {
            execute(task)
            return
        }
        
        try {
            totalTasksSubmitted.increment()
            val startTime = System.nanoTime()
            
            executor.execute {
                try {
                    task.run()
                    totalTasksCompleted.increment()
                    totalExecutionTimeMs.add((System.nanoTime() - startTime) / 1_000_000)
                } catch (e: Throwable) {
                    val t = Thread.currentThread()
                    t.uncaughtExceptionHandler?.uncaughtException(t, e)
                }
            }
            
        } catch (e: Exception) {
            totalTasksRejected.increment()
            LOGGER.severe("Erro ao executar tarefa I/O: ${e.message}")
            execute(task)
        }
    }
    
    @JvmStatic
    fun schedule(r: Runnable, delay: Long): ScheduledFuture<*> {
        val executors = scheduledExecutors
        if (executors == null || executors.isEmpty()) {
            LOGGER.fine("Schedule ignorado (pool não inicializado ou já desligado).")
            return ScheduledFutureTask.cancelled()
        }
        return try {
            totalTasksSubmitted.increment()
            val selectedPool = executors[ThreadLocalRandom.current().nextInt(executors.size)]
            selectedPool.schedule(RunnableWrapper(r), validate(delay), TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            LOGGER.severe("Erro ao agendar tarefa: ${e.message}")
            ScheduledFutureTask.cancelled()
        }
    }
    @JvmStatic
    fun scheduleAtFixedRate(r: Runnable, delay: Long, period: Long): ScheduledFuture<*> {
        val executors = scheduledExecutors
        if (executors == null || executors.isEmpty()) {
            LOGGER.fine("ScheduleAtFixedRate ignorado (pool não inicializado ou já desligado).")
            return ScheduledFutureTask.cancelled()
        }
        return try {
            totalTasksSubmitted.increment()
            val selectedPool = executors[ThreadLocalRandom.current().nextInt(executors.size)]
            selectedPool.scheduleAtFixedRate(RunnableWrapper(r), validate(delay), validate(period), TimeUnit.MILLISECONDS)
        } catch (e: Exception) {
            LOGGER.severe("Erro ao agendar tarefa periódica: ${e.message}")
            ScheduledFutureTask.cancelled()
        }
    }
    
    @JvmStatic
    fun executePathfinding(task: Runnable) {
        val executor = pathfindingExecutor
        if (executor == null) {
            task.run()
            return
        }
        try {
            pathfindingTasksSubmitted.increment()
            executor.execute(RunnableWrapper {
                try {
                    task.run()
                    pathfindingTasksCompleted.increment()
                } catch (e: Throwable) {
                    val t = Thread.currentThread()
                    t.uncaughtExceptionHandler?.uncaughtException(t, e)
                }
            })
        } catch (e: Exception) {
            LOGGER.fine("PathfindingPool ocupado/erro: ${e.message}")
            task.run()
        }
    }
    
    @JvmStatic
    fun <T> runPathfindingBlocking(block: () -> T): T {
        val executor = pathfindingExecutor
        if (executor == null) return block()
        try {
            pathfindingTasksSubmitted.increment()
            val future = CompletableFuture.supplyAsync({
                val result = block()
                pathfindingTasksCompleted.increment()
                result
            }, executor)
            return future.get()
        } catch (e: Exception) {
            return block()
        }
    }
    
    @JvmStatic
    fun executeParallel(task: Runnable): Job {
        return BackgroundScope.launch(Dispatchers.Default) {
            try { task.run() } catch (e: Exception) { LOGGER.severe("Erro executeParallel: ${e.message}") }
        }
    }
    
    @JvmStatic
    fun executeParallelBlocking(task: Runnable) {
        val pool = forkJoinPool ?: return execute(task)
        try {
            pool.submit(task).join()
        } catch (e: Exception) {
            LOGGER.severe("Erro executeParallelBlocking: ${e.message}")
        }
    }
    
    @JvmStatic fun getPathfindingQueueSize(): Int = pathfindingExecutor?.queue?.size ?: 0
    @JvmStatic fun getPathfindingActiveCount(): Int = pathfindingExecutor?.activeCount ?: 0
    @JvmStatic fun getTotalTasksSubmitted(): Long = totalTasksSubmitted.sum()
    @JvmStatic fun getTotalTasksCompleted(): Long = totalTasksCompleted.sum()
    @JvmStatic fun getTotalTasksRejected(): Long = totalTasksRejected.sum()
    
    @JvmStatic
    fun getAverageExecutionTimeMs(): Double {
        val completed = totalTasksCompleted.sum()
        return if (completed > 0) totalExecutionTimeMs.sum().toDouble() / completed else 0.0
    }
    
    @JvmStatic
    fun getMetrics(): Map<String, Any> {
        val instantPools = instantExecutors ?: emptyArray()
        val scheduledPools = scheduledExecutors ?: emptyArray()
        val pfExecutor = pathfindingExecutor
        
        val totalInstantCapacity = instantPools.sumOf { it.maximumPoolSize }
        
        return mapOf(
            "tasksSubmitted" to totalTasksSubmitted.sum(),
            "tasksCompleted" to totalTasksCompleted.sum(),
            "tasksRejected" to totalTasksRejected.sum(),
            "averageLatency" to getAverageExecutionTimeMs(),
            
            "scheduledPools" to scheduledPools.size,
            "scheduledQueueSize" to scheduledPools.sumOf { it.queue.size },
            
            "instantPools" to instantPools.size,
            "instantPoolsSize" to totalInstantCapacity,
            "instantPoolsActive" to instantPools.sumOf { it.activeCount },
            "instantPoolsQueueSize" to instantPools.sumOf { it.queue.size },
            
            "pathfindingSize" to (pfExecutor?.maximumPoolSize ?: 0),
            "pathfindingActive" to getPathfindingActiveCount(),
            "pathfindingQueueSize" to getPathfindingQueueSize(),
            "pathfindingTasksSubmitted" to pathfindingTasksSubmitted.sum(),
            "pathfindingTasksCompleted" to pathfindingTasksCompleted.sum()
        )
    }
    
    @JvmStatic
    fun printMetrics() {
        val metrics = getMetrics()
        LOGGER.info("=== CoroutinePool Metrics ===")
        metrics.forEach { (key, value) -> LOGGER.info("  $key: $value") }
    }
    private fun validate(delay: Long): Long {
        return maxOf(0, minOf(MAX_DELAY_MS, delay))
    }
    @JvmStatic
    fun shutdown() {
        if (scheduledExecutors == null) return
        try {
            LOGGER.info("Desligando CoroutinePool...")
            BackgroundScope.cancel()
            scheduledExecutors?.forEach { it.shutdown() }
            instantExecutors?.forEach { it.shutdown() }
            pathfindingExecutor?.shutdown()
            virtualExecutor?.shutdown()
            forkJoinPool?.shutdown()
            scheduledExecutors = null
            instantExecutors = null
            pathfindingExecutor = null
            virtualExecutor = null
            forkJoinPool = null
            LOGGER.info("CoroutinePool desligado.")
        } catch (e: Exception) {
            LOGGER.severe("Erro ao desligar: ${e.message}")
        }
    }
}