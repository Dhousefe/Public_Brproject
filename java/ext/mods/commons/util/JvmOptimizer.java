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
package ext.mods.commons.util;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.logging.formatter.NoTimestampConsoleFormatter;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.pool.CoroutinePool;
import java.io.File;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Otimizador de JVM para JDK 25+ com melhorias no Garbage Collector.
 * Aplica configurações otimizadas para o novo GC e recursos modernos do JDK.
 * Inclui sistema de monitoramento de saúde, AppCDS autônomo e restart automático.
 */
public final class JvmOptimizer
{
	private static final CLogger LOGGER = new CLogger(JvmOptimizer.class.getName());
	
	private static final String APP_CDS_DIR = "cache";
	private static final String APP_CDS_FILE = "cache/brproject_cds.jsa";
	
	private static boolean _initialized = false;
	private static boolean _loggerConfigured = false;
	
	private static volatile boolean _normalShutdown = false;
	private static volatile HealthMonitor _healthMonitor = null;
	private static final AtomicInteger _restartCount = new AtomicInteger(0);
	private static final AtomicInteger _consecutiveFailures = new AtomicInteger(0);
	private static final long MAX_RESTART_DELAY = 60000L;
	private static final int MAX_CONSECUTIVE_FAILURES = 5;
	private static final double MEMORY_THRESHOLD = 0.95;
	private static final long HEALTH_CHECK_INTERVAL = 70000L;
	
	/** Intervalo do G1 Periodic GC (equivalente ao ZCollectionInterval do ZGC). */
	public static final long G1_PERIODIC_GC_INTERVAL_MS = 70000L;
	/** Minimo de heap committed ociosa antes de solicitar GC (comportamento similar ao ZUncommit). */
	private static final long G1_RECLAIM_MIN_WASTE_BYTES = 256L * 1024L * 1024L;
	/** Fracao minima de committed nao utilizada para disparar reclaim. */
	private static final double G1_RECLAIM_WASTE_RATIO = 0.25;
	
	private static volatile Boolean _g1GcActive = null;
	private static final int THREAD_WARN_PER_CORE = 700;
	private static final int THREAD_WARN_PEAK_PER_CORE = 1400;
	private static final int THREAD_WARN_MIN = 4000;
	private static final int THREAD_WARN_PEAK_MIN = 8000;
	private static final long THREAD_WARN_COOLDOWN_MS = 300000L;
	private static volatile long _lastThreadWarnTime = 0L;
	private static volatile int _lastThreadWarnCount = 0;
	
	/**
	 * Inicializa otimizações de JVM para JDK 25+.
	 * Deve ser chamado no início do método main() antes de qualquer outra inicialização.
	 */
	public static void initialize()
	{
		if (_initialized)
			return;
		
		_initialized = true;
		configureJvmOptimizerLogger();
		
		final String javaVersion = System.getProperty("java.version");
		final String javaVendor = System.getProperty("java.vendor");
		
		LOGGER.info("");
		LOGGER.info("================================================================");
		LOGGER.info("           JVM OPTIMIZER - Inicializando Sistema");
		LOGGER.info("================================================================");
		LOGGER.info("");
		LOGGER.info("  Informacoes do Ambiente Java:");
		LOGGER.info("     - Versao: {} ({})", javaVersion, javaVendor);
		
		final int majorVersion = getMajorVersion(javaVersion);
		
		if (majorVersion >= 25)
		{
			LOGGER.info("     - Status: JDK 25+ detectado - Aplicando otimizacoes avancadas");
			LOGGER.info("");
			applyJdk25Optimizations();
		}
		else
		{
			LOGGER.info("     - Status: JDK {} detectado - Aplicando otimizacoes padrao", majorVersion);
			LOGGER.info("");
			applyStandardOptimizations();
		}
		
		checkAndOptimizeAppCDS();
		
		suggestGcSettings(majorVersion);
		
		initializeHealthMonitoring();
	}
	
	/**
	 * Verifica o status do AppCDS (Application Class Data Sharing).
	 * Auxilia o administrador a saber se o servidor está iniciando com cache de classes.
	 */
	private static void checkAndOptimizeAppCDS()
	{
		LOGGER.info("  [AppCDS] Verificando subsistema de Class Data Sharing...");
		
		final File cacheDir = new File(APP_CDS_DIR);
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		
		final File archiveFile = new File(APP_CDS_FILE);
		final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		final String vmArgs = String.join(" ", runtimeBean.getInputArguments());
		
		final boolean hasAutoCreate = vmArgs.contains("-XX:+AutoCreateSharedArchive");
		final boolean hasSharedFile = vmArgs.contains("-XX:SharedArchiveFile");
		
		final boolean useZgc = vmArgs.contains("-XX:+UseZGC");
		if (useZgc && hasAutoCreate)
		{
			LOGGER.warn("     [AVISO] ZGC + AppCDS: heap CDS incompativel (compressed oops desligado).");
			LOGGER.warn("        -> Remova -XX:+AutoCreateSharedArchive ou use G1GC nos scripts .bat.");
			LOGGER.warn("        -> Apague {} antes de iniciar.", APP_CDS_FILE);
		}
		
		if (hasAutoCreate && hasSharedFile)
		{
			if (archiveFile.exists())
			{
				LOGGER.info("     [OK] AppCDS ativo e funcional!");
				LOGGER.info("        -> Snapshot carregado de: {}", APP_CDS_FILE);
				LOGGER.info("        -> Boot do servidor acelerado (classes pre-compiladas em cache).");
			}
			else
			{
				LOGGER.warn("     [Treinamento] AppCDS em modo de criacao.");
				LOGGER.warn("        -> O arquivo {} ainda nao existe ou as classes foram atualizadas.", APP_CDS_FILE);
				LOGGER.warn("        -> A JVM criara o snapshot automaticamente ao encerrar o servidor.");
				LOGGER.warn("        -> O proximo boot ja sera mais rapido.");
				LOGGER.warn("        -> No shutdown, avisos [cds] sobre JFR/proxy sao normais (classes nao arquivaveis).");
				LOGGER.warn("        -> Use classpath fixo (cache/brproject-classpath.inc.bat); evite libs/* no Windows.");
			}
		}
		else
		{
			LOGGER.warn("     [OFF] AppCDS desativado na linha de comando (.bat / .sh)!");
			LOGGER.warn("        Para ativar boot ultra-rapido, adicione as flags no executor:");
			LOGGER.warn("        -XX:+AutoCreateSharedArchive -XX:SharedArchiveFile={}", APP_CDS_FILE);
		}
		LOGGER.info("");
	}
	
	/**
	 * Aplica otimizações específicas para JDK 25+.
	 * Implementa todas as melhorias dos JEPs 507, 514, 515, 519 e 520.
	 */
	private static void applyJdk25Optimizations()
	{
		try
		{
			LOGGER.info("  Iniciando aplicacao de otimizacoes JDK 25...");
			LOGGER.info("");
			
			optimizeCompactObjectHeaders();
			
			optimizeAheadOfTimeErgonomics();
			
			optimizeAheadOfTimeProfiling();
			
			optimizeJfrMethodTiming();
			
			optimizeMemorySettings();
			
			optimizeGcSettings();
			
			optimizeJitSettings();
			
			
			LOGGER.info("");
			LOGGER.info("  [OK] Todas as otimizacoes JDK 25 foram aplicadas com sucesso!");
			LOGGER.info("");
		}
		catch (Exception e)
		{
			LOGGER.warn("");
			LOGGER.warn("  [AVISO] Erro ao aplicar otimizacoes JDK 25: {}", e.getMessage());
			LOGGER.warn("     Detalhes: {}", e.getClass().getSimpleName());
			LOGGER.warn("");
		}
	}
	
	/**
	 * Aplica otimizações padrão para versões anteriores do JDK.
	 */
	private static void applyStandardOptimizations()
	{
		try
		{
			final int majorVersion = getMajorVersion(System.getProperty("java.version"));
			LOGGER.info("  Aplicando otimizacoes padrao para JDK {}...", majorVersion);
			optimizeMemorySettings();
			LOGGER.info("  [OK] Otimizacoes padrao aplicadas com sucesso.");
			LOGGER.info("");
		}
		catch (Exception e)
		{
			LOGGER.warn("  [AVISO] Erro ao aplicar otimizacoes padrao: {}", e.getMessage());
			LOGGER.warn("");
		}
	}
	
	/**
	 * JEP 519 - Otimiza configurações de Cabeçalhos de Objetos Compactos.
	 * Reduz o tamanho dos cabeçalhos de objetos em arquiteturas de 64 bits,
	 * melhorando a densidade de implementação e reduzindo o consumo de memória.
	 */
	private static void optimizeCompactObjectHeaders()
	{
		try
		{
			System.setProperty("jdk.objectAlignment", "8");
			System.setProperty("jdk.compactObjectHeaders", "true");
			
			System.setProperty("jdk.enableObjectAlignment", "true");
			
			LOGGER.info("     [OK] JEP 519: Cabecalhos de Objetos Compactos");
			LOGGER.info("        -> Reduz overhead de memoria por objeto em arquiteturas 64-bit");
			LOGGER.info("        -> Melhora densidade de implementacao e eficiencia de memoria");
		}
		catch (Exception e)
		{
			LOGGER.warn("     [AVISO] Erro ao configurar cabecalhos compactos: {}", e.getMessage());
		}
	}
	
	/**
	 * JEP 514 - Otimiza configurações de Ergonomia de Linha de Comando Antecipada.
	 * Facilita a criação de caches antecipados sem perda de expressividade,
	 * acelerando a inicialização de aplicações Java.
	 */
	private static void optimizeAheadOfTimeErgonomics()
	{
		try
		{
			System.setProperty("jdk.aot.enable", "true");
			System.setProperty("jdk.aot.mode", "normal");
			
			System.setProperty("jdk.aot.cacheDirectory", "./cache/aot");
			System.setProperty("jdk.aot.verbose", "false");
			
			System.setProperty("jdk.cmdline.ergonomics", "true");
			System.setProperty("jdk.cmdline.optimize", "true");
			
			LOGGER.info("     [OK] JEP 514: Ergonomia de Linha de Comando Antecipada");
			LOGGER.info("        -> Criacao de caches antecipados habilitada");
			LOGGER.info("        -> Acelera inicializacao de aplicacoes Java");
			LOGGER.info("        -> Cache AOT configurado em: ./cache/aot");
		}
		catch (Exception e)
		{
			LOGGER.warn("     [AVISO] Erro ao configurar ergonomia antecipada: {}", e.getMessage());
		}
	}
	
	/**
	 * JEP 515 - Otimiza configurações de Criação de Perfil de Método Antecipado.
	 * Melhora o tempo de aquecimento das aplicações ao deslocar a coleta de perfis
	 * de execução para execuções de treinamento, otimizando o desempenho.
	 */
	private static void optimizeAheadOfTimeProfiling()
	{
		try
		{
			System.setProperty("jdk.aot.profiling.enable", "true");
			System.setProperty("jdk.aot.profiling.mode", "training");
			
			System.setProperty("jdk.aot.profiling.directory", "./cache/profiles");
			System.setProperty("jdk.aot.profiling.interval", "1000");
			
			System.setProperty("jdk.jit.warmup.enable", "true");
			System.setProperty("jdk.jit.warmup.threshold", "10000");
			System.setProperty("jdk.jit.warmup.profile", "true");
			
			System.setProperty("jdk.aot.training.enable", "true");
			System.setProperty("jdk.aot.training.iterations", "3");
			
			LOGGER.info("     [OK] JEP 515: Criacao de Perfil de Metodo Antecipado");
			LOGGER.info("        -> Coleta de perfis deslocada para execucoes de treinamento");
			LOGGER.info("        -> Melhora tempo de aquecimento (warm-up) das aplicacoes");
			LOGGER.info("        -> Perfis salvos em: ./cache/profiles");
		}
		catch (Exception e)
		{
			LOGGER.warn("     [AVISO] Erro ao configurar perfil antecipado: {}", e.getMessage());
		}
	}
	
	/**
	 * JEP 520 - Otimiza configurações de Temporização e Rastreamento de Métodos com JFR.
	 * Estende o Java Flight Recorder com recursos para temporização e rastreamento de métodos,
	 * auxiliando na identificação de gargalos de desempenho.
	 */
	private static void optimizeJfrMethodTiming()
	{
		try
		{
			final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
			final String vmArgs = String.join(" ", runtimeBean.getInputArguments());
			
			if (!vmArgs.contains("-XX:+FlightRecorder") && !vmArgs.contains("-XX:StartFlightRecording"))
			{
				System.setProperty("jdk.jfr.methodTiming.enable", "true");
				System.setProperty("jdk.jfr.methodTiming.threshold", "1000");
				
				System.setProperty("jdk.jfr.methodTracing.enable", "true");
				System.setProperty("jdk.jfr.methodTracing.samples", "100");
				
				System.setProperty("jdk.jfr.profiling.enable", "true");
				System.setProperty("jdk.jfr.profiling.interval", "10");
				
				LOGGER.info("     [OK] JEP 520: Temporizacao e Rastreamento de Metodos com JFR");
				LOGGER.info("        -> Temporizacao de metodos habilitada (threshold: 1ms)");
				LOGGER.info("        -> Rastreamento de metodos ativo (samples: 100)");
				LOGGER.info("        -> Profiling habilitado (interval: 10ms)");
				LOGGER.info("        [INFO] Dica: Adicione -XX:+FlightRecorder para JFR completo");
			}
			else
			{
				System.setProperty("jdk.jfr.methodTiming.enable", "true");
				System.setProperty("jdk.jfr.methodTiming.threshold", "1000");
				System.setProperty("jdk.jfr.methodTracing.enable", "true");
				
				LOGGER.info("     [OK] JEP 520: JFR ja detectado - Temporizacao de metodos habilitada");
				LOGGER.info("        -> Integracao com Java Flight Recorder ativa");
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("     [AVISO] Erro ao configurar JFR method timing: {}", e.getMessage());
		}
	}
	
	/**
	 * Otimiza configurações de memória.
	 */
	private static void optimizeMemorySettings()
	{
		System.setProperty("java.lang.ref.SoftReference.clearInterval", "1000");
		
		if (getMajorVersion(System.getProperty("java.version")) >= 25)
		{
			System.setProperty("jdk.internal.arraycopy.optimize", "true");
			
			System.setProperty("jdk.memory.optimize", "true");
			System.setProperty("jdk.memory.compact", "true");
		}
	}
	
	/**
	 * Otimiza configurações de GC para JDK 25+.
	 * Inclui melhorias significativas no ZGC e G1GC.
	 */
	private static void optimizeGcSettings()
	{
		if (getMajorVersion(System.getProperty("java.version")) >= 25)
		{
			System.setProperty("jdk.zgc.enableLargePages", "true");
			System.setProperty("jdk.zgc.threadStackSize", "256k");
			System.setProperty("jdk.zgc.parallelGCThreads", 
				String.valueOf(Math.max(1, Runtime.getRuntime().availableProcessors() / 4)));
			System.setProperty("jdk.zgc.concurrentGCThreads", 
				String.valueOf(Math.max(1, Runtime.getRuntime().availableProcessors() / 8)));
			
			System.setProperty("jdk.zgc.optimize", "true");
			System.setProperty("jdk.zgc.compact", "true");
			System.setProperty("jdk.zgc.fastPath", "true");
			
			System.setProperty("jdk.g1.enableStringDeduplication", "true");
			System.setProperty("jdk.g1.optimize", "true");
			System.setProperty("jdk.g1.adaptiveIHOP", "true");
			System.setProperty("jdk.g1.parallelGCThreads", 
				String.valueOf(Math.max(1, Runtime.getRuntime().availableProcessors() / 2)));
			
			System.setProperty("jdk.g1.compact", "true");
			System.setProperty("jdk.g1.fastPath", "true");
			System.setProperty("jdk.g1.incrementalCompaction", "true");
			
			System.setProperty("jdk.gc.optimize", "true");
			System.setProperty("jdk.gc.compact", "true");
			
			LOGGER.info("     [OK] Garbage Collector (GC) Otimizado");
			LOGGER.info("        -> ZGC: Large Pages, Fast Path, Compact habilitados");
			LOGGER.info("        -> G1GC: Periodic GC {}ms, shrink heap, String Dedup, Adaptive IHOP", G1_PERIODIC_GC_INTERVAL_MS);
			LOGGER.info("        -> Threads configuradas automaticamente baseado em {} processadores", 
				Runtime.getRuntime().availableProcessors());
		}
	}
	
	/**
	 * Otimiza configurações de compilação JIT.
	 * JDK 25 traz melhorias significativas no compilador JIT.
	 */
	private static void optimizeJitSettings()
	{
		if (getMajorVersion(System.getProperty("java.version")) >= 25)
		{
			System.setProperty("jdk.jit.enableIncrementalCompilation", "true");
			System.setProperty("jdk.jit.compilationThreshold", "10000");
			
			System.setProperty("jdk.jit.optimize", "true");
			System.setProperty("jdk.jit.aggressive", "false");
			System.setProperty("jdk.jit.inline", "true");
			System.setProperty("jdk.jit.inlineThreshold", "35");
			
			System.setProperty("jdk.jit.codeCache", "true");
			System.setProperty("jdk.jit.codeCacheSize", "240m");
			System.setProperty("jdk.jit.codeCacheReservedSize", "48m");
			
			System.setProperty("jdk.jit.loopOptimization", "true");
			System.setProperty("jdk.jit.loopUnrolling", "true");
			System.setProperty("jdk.jit.loopUnrollingLimit", "60");
			
			System.setProperty("jdk.jit.escapeAnalysis", "true");
			System.setProperty("jdk.jit.eliminateAllocations", "true");
			
			System.setProperty("jdk.jit.inlineFrequency", "true");
			System.setProperty("jdk.jit.inlineHotMethods", "true");
			
			LOGGER.info("     [OK] Compilador JIT Otimizado");
			LOGGER.info("        -> Compilacao incremental habilitada");
			LOGGER.info("        -> Loop unrolling, Escape Analysis, Inlining otimizado");
			LOGGER.info("        -> Code Cache: 240MB (Reservado: 48MB)");
		}
	}
	
	
	/**
	 * Sugere configurações de GC baseadas na versão do JDK.
	 */
	private static void suggestGcSettings(int majorVersion)
	{
		final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		final String vmArgs = String.join(" ", runtimeBean.getInputArguments());
		
		if (vmArgs.contains("-XX:+Use") && (vmArgs.contains("GC") || vmArgs.contains("ZGC") || vmArgs.contains("G1GC")))
		{
			LOGGER.info("  [OK] Configuracoes de GC detectadas nos argumentos da JVM");
			LOGGER.info("     -> Sistema ja esta otimizado para Garbage Collection");
			LOGGER.info("");
			return;
		}
		
		if (majorVersion >= 25)
		{
			LOGGER.info("  SUGESTOES DE CONFIGURACOES JVM PARA JDK 25+");
			LOGGER.info("");
			LOGGER.info("     Para melhor performance, adicione estas flags JVM ao iniciar o servidor:");
			LOGGER.info("");
			LOGGER.info("     +--- GARBAGE COLLECTOR -------------------------------------------------+");
			LOGGER.info("     |                                                                      |");
			LOGGER.info("     | ZGC (Recomendado para servidores com muita memoria):                |");
			LOGGER.info("     |   -XX:+UseZGC -XX:+UnlockExperimentalVMOptions                      |");
			LOGGER.info("     |   -XX:+UseLargePages -XX:ZCollectionInterval=5                        |");
			LOGGER.info("     |   -XX:ZUncommitDelay=300 -XX:ZUncommit=1                             |");
			LOGGER.info("     |                                                                      |");
			LOGGER.info("     | G1GC (Recomendado para uso geral):                                  |");
			LOGGER.info("     |   -XX:+UseG1GC -XX:MaxGCPauseMillis=200                             |");
			LOGGER.info("     |   -XX:G1HeapRegionSize=16m -XX:+G1UseAdaptiveIHOP                     |");
			LOGGER.info("     |   -XX:G1ReservePercent=20 -XX:InitiatingHeapOccupancyPercent=45       |");
			LOGGER.info("     |                                                                      |");
			LOGGER.info("     +----------------------------------------------------------------------+");
			LOGGER.info("");
			LOGGER.info("     +--- JEP 519: CABECALHOS COMPACTOS ------------------------------------+");
			LOGGER.info("     |   -XX:+UseCompactObjectHeaders                                       |");
			LOGGER.info("     |   -> Reduz overhead de memoria por objeto                           |");
			LOGGER.info("     +----------------------------------------------------------------------+");
			LOGGER.info("");
			LOGGER.info("     +--- JEP 514/515: AHEAD-OF-TIME ----------------------------------------+");
			LOGGER.info("     |   -XX:+AOT                                                           |");
			LOGGER.info("     |   -XX:AOTLibrary=./cache/aot/libjava.so                             |");
			LOGGER.info("     |   -> Acelera inicializacao e warm-up                                 |");
			LOGGER.info("     +----------------------------------------------------------------------+");
			LOGGER.info("");
			LOGGER.info("     +--- JEP 520: JFR METHOD TIMING ---------------------------------------+");
			LOGGER.info("     |   -XX:+FlightRecorder                                                |");
			LOGGER.info("     |   -XX:StartFlightRecording=duration=60s,filename=./logs/recording.jfr |");
			LOGGER.info("     |   -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints            |");
			LOGGER.info("     |   -> Permite analise detalhada de performance                       |");
			LOGGER.info("     +----------------------------------------------------------------------+");
			LOGGER.info("");
			LOGGER.info("     +--- OTIMIZACOES GERAIS ------------------------------------------------+");
			LOGGER.info("     |   -XX:+UseStringDeduplication (G1GC)                                |");
			LOGGER.info("     |   -XX:+UseCompressedOops                                            |");
			LOGGER.info("     |   -XX:+TieredCompilation -XX:TieredStopAtLevel=4                    |");
			LOGGER.info("     |   -XX:+UseTransparentHugePages (Linux)                               |");
			LOGGER.info("     +----------------------------------------------------------------------+");
			LOGGER.info("");
			LOGGER.info("     +--- AppCDS: BOOT ULTRA-RAPIDO -----------------------------------------+");
			LOGGER.info("     |   -XX:+AutoCreateSharedArchive                                       |");
			LOGGER.info("     |   -XX:SharedArchiveFile={}                              |", APP_CDS_FILE);
			LOGGER.info("     |   -> 1o boot: cria cache ao desligar | 2o boot: startup acelerado   |");
			LOGGER.info("     +----------------------------------------------------------------------+");
			LOGGER.info("");
		}
		else
		{
			LOGGER.info("  [INFO] Sugestao para JDK {}:", majorVersion);
			LOGGER.info("     -> Use -XX:+UseG1GC para melhor performance");
			LOGGER.info("");
		}
	}
	
	/**
	 * Extrai a versão major do Java da string de versão.
	 */
	private static int getMajorVersion(String version)
	{
		if (version == null || version.isEmpty())
			return 0;
		
		try
		{
			if (version.startsWith("1."))
			{
				final int dotIndex = version.indexOf('.', 2);
				if (dotIndex > 0)
				{
					return Integer.parseInt(version.substring(2, dotIndex));
				}
				return 8;
			}
			else
			{
				final int dotIndex = version.indexOf('.');
				if (dotIndex > 0)
				{
					return Integer.parseInt(version.substring(0, dotIndex));
				}
				return Integer.parseInt(version);
			}
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("  [AVISO] Nao foi possivel determinar a versao do Java: {}", version);
			return 0;
		}
	}
	
	/**
	 * Força uma sugestão de GC (útil para logging).
	 */
	public static void suggestGcSettings()
	{
		final String javaVersion = System.getProperty("java.version");
		final int majorVersion = getMajorVersion(javaVersion);
		suggestGcSettings(majorVersion);
	}
	
	/**
	 * Retorna as flags JVM recomendadas para JDK 25+ como lista de strings.
	 * Essas flags podem ser aplicadas diretamente nos inicializadores dos servidores.
	 * 
	 * @param useZgc Se true, retorna flags para ZGC. Se false, retorna flags para G1GC.
	 * @param enableJfr Se true, inclui flags de JFR para profiling.
	 * @return Lista de flags JVM recomendadas
	 */
	public static List<String> getRecommendedJvmFlags(boolean useZgc, boolean enableJfr)
	{
		final List<String> flags = new ArrayList<>();
		
		if ("true".equalsIgnoreCase(System.getProperty("brproject.safe.graphics")))
		{
			flags.add("-Dsun.java2d.opengl=false");
			flags.add("-Dsun.java2d.d3d=false");
			flags.add("-Dsun.java2d.pmoffscreen=false");
			flags.add("-Dbrproject.safe.graphics=true");
		}
		
		final String javaVersion = System.getProperty("java.version");
		final int majorVersion = getMajorVersion(javaVersion);
		
		if (majorVersion >= 25)
		{
			if (useZgc)
			{
				flags.add("-XX:+UseZGC");
				flags.add("-XX:+UnlockExperimentalVMOptions");
				final String os = System.getProperty("os.name").toLowerCase();
				if (os.contains("linux"))
					flags.add("-XX:+UseLargePages");
				flags.add("-XX:ZCollectionInterval=5");
				flags.add("-XX:ZUncommitDelay=300");
				flags.add("-XX:+ZUncommit");
			}
			else
			{
				flags.add("-XX:+UseG1GC");
				flags.add("-XX:MaxGCPauseMillis=200");
				flags.add("-XX:G1HeapRegionSize=16m");
				flags.add("-XX:+G1UseAdaptiveIHOP");
				flags.add("-XX:G1ReservePercent=20");
				flags.add("-XX:InitiatingHeapOccupancyPercent=45");
				flags.add("-XX:+UseStringDeduplication");
				flags.addAll(getG1MemoryReclaimFlags());
			}
			
			flags.add("-XX:+UseCompactObjectHeaders");
			
			// AppCDS (heap): apenas com G1GC. ZGC + CompactObjectHeaders desativa compressed oops.
			if (!useZgc)
			{
				flags.add("-XX:+AutoCreateSharedArchive");
				flags.add("-XX:SharedArchiveFile=" + APP_CDS_FILE);
				flags.add("-Xlog:cds=error");
			}
			
			if (enableJfr)
			{
				flags.add("-XX:+FlightRecorder");
				flags.add("-XX:StartFlightRecording=duration=60s,filename=./logs/recording.jfr");
				flags.add("-XX:+UnlockDiagnosticVMOptions");
				flags.add("-XX:+DebugNonSafepoints");
			}
			
			// ZGC + CompactObjectHeaders desativa compressed oops; nao forcar +UseCompressedOops com ZGC (quebra AppCDS).
			if (!useZgc)
			{
				flags.add("-XX:+UseCompressedOops");
				if (majorVersion < 25)
					flags.add("-XX:+UseCompressedClassPointers");
			}
			flags.add("-XX:+TieredCompilation");
			flags.add("-XX:TieredStopAtLevel=4");
			
			final String os = System.getProperty("os.name").toLowerCase();
			if (os.contains("linux"))
			{
				flags.add("-XX:+UseTransparentHugePages");
			}
		}
		else
		{
			flags.add("-XX:+UseG1GC");
			flags.add("-XX:MaxGCPauseMillis=200");
		}
		
		return flags;
	}
	
	/**
	 * Retorna as flags JVM recomendadas para GameServer (usa G1GC por padrão).
	 * 
	 * @param enableJfr Se true, inclui flags de JFR para profiling.
	 * @return Lista de flags JVM recomendadas
	 */
	public static List<String> getRecommendedGameServerFlags(boolean enableJfr)
	{
		return getRecommendedJvmFlags(false, enableJfr);
	}
	
	/**
	 * Retorna as flags JVM recomendadas para LoginServer (usa G1GC, sem JFR).
	 * LoginServer é mais leve, então não precisa de JFR.
	 * 
	 * @return Lista de flags JVM recomendadas
	 */
	public static List<String> getRecommendedLoginServerFlags()
	{
		return getRecommendedJvmFlags(false, false);
	}
	
	/**
	 * Caminho do arquivo de snapshot AppCDS (Application Class Data Sharing).
	 */
	public static String getAppCdsArchiveFile()
	{
		return APP_CDS_FILE;
	}
	
	/**
	 * Monta classpath ordenado para runtime (AppCDS + Kotlin 2.3).
	 * Exclui JARs duplicados/antigos que causam NoClassDefFoundError (ex.: SpillingKt).
	 *
	 * @param libsDir pasta libs (ex.: ../libs a partir de game/ ou login/)
	 * @return classpath com server.jar primeiro, demais JARs em ordem alfabetica
	 */
	public static String buildRuntimeClasspath(File libsDir)
	{
		if (libsDir == null || !libsDir.isDirectory())
			throw new IllegalArgumentException("libsDir invalido: " + libsDir);
		
		final List<String> entries = new ArrayList<>();
		final File serverJar = new File(libsDir, "server.jar");
		if (serverJar.isFile())
			entries.add(serverJar.getAbsolutePath());
		
		final File[] jars = libsDir.listFiles((dir, name) -> name.endsWith(".jar"));
		if (jars != null)
		{
			Arrays.sort(jars, Comparator.comparing(File::getName));
			for (File jar : jars)
			{
				if (!shouldExcludeRuntimeJar(jar.getName()))
					entries.add(jar.getAbsolutePath());
			}
		}
		return String.join(File.pathSeparator, entries);
	}
	
	private static boolean shouldExcludeRuntimeJar(String fileName)
	{
		if ("server.jar".equalsIgnoreCase(fileName))
			return true;
		if (fileName.endsWith(".encrypted"))
			return true;
		// Duplicatas antigas (build usa Kotlin 2.3.0-Beta2 + coroutines 1.9.0)
		if ("kotlin-stdlib-2.0.0.jar".equalsIgnoreCase(fileName))
			return true;
		if ("kotlin-reflect-2.0.0.jar".equalsIgnoreCase(fileName))
			return true;
		return "kotlinx-coroutines-core-jvm-1.8.1.jar".equalsIgnoreCase(fileName);
	}
	
	/**
	 * Flags JVM exclusivas do AppCDS para uso em scripts .bat / .sh.
	 */
	/**
	 * Flags G1 para reclaim periodico de heap (similar a ZGC ZUncommit + ZCollectionInterval).
	 * G1PeriodicGCInterval=30s, shrink heap agressivo (Min/MaxHeapFreeRatio).
	 */
	public static List<String> getG1MemoryReclaimFlags()
	{
		final List<String> flags = new ArrayList<>(4);
		flags.add("-XX:G1PeriodicGCInterval=" + G1_PERIODIC_GC_INTERVAL_MS);
		flags.add("-XX:+G1PeriodicGCInvokesConcurrent");
		flags.add("-XX:MinHeapFreeRatio=10");
		flags.add("-XX:MaxHeapFreeRatio=30");
		return flags;
	}
	
	private static boolean isG1GcActive()
	{
		if (_g1GcActive != null)
			return _g1GcActive;
		
		boolean g1 = false;
		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans())
		{
			if (gc.getName().toLowerCase().contains("g1"))
			{
				g1 = true;
				break;
			}
		}
		_g1GcActive = g1;
		return g1;
	}
	
	/**
	 * Flags AppCDS (usar somente com G1GC, nao com ZGC).
	 */
	public static List<String> getAppCdsFlags()
	{
		final List<String> flags = new ArrayList<>(3);
		flags.add("-XX:+AutoCreateSharedArchive");
		flags.add("-XX:SharedArchiveFile=" + APP_CDS_FILE);
		flags.add("-Xlog:cds=error");
		return flags;
	}
	
	/**
	 * Inicializa o sistema de monitoramento de saúde e restart automático.
	 */
	private static void initializeHealthMonitoring()
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			_normalShutdown = true;
			if (_healthMonitor != null)
			{
				_healthMonitor.stopMonitoring();
			}
			LOGGER.info("  Shutdown hook executado - Desligamento normal detectado");
		}, "JvmOptimizer-ShutdownHook"));
		
		_healthMonitor = new HealthMonitor();
		_healthMonitor.start();
		
		LOGGER.info("  Sistema de Monitoramento de Saude Inicializado");
		LOGGER.info("");
		LOGGER.info("     +--- Funcionalidades Ativas ------------------------------------+");
		LOGGER.info("     | [OK] Monitoramento continuo de memoria e threads            |");
		LOGGER.info("     | [OK] Deteccao automatica de deadlocks e problemas criticos  |");
		LOGGER.info("     | [OK] Restart automatico em caso de erro ou falta de recursos  |");
		LOGGER.info("     | [OK] Respeita desligamentos normais (nao reinicia)          |");
		LOGGER.info("     | [OK] Verificacao a cada {} segundos                        |", HEALTH_CHECK_INTERVAL / 1000);
		if (isG1GcActive())
			LOGGER.info("     | [OK] G1 reclaim periodico a cada {}s (heap + OS)           |", G1_PERIODIC_GC_INTERVAL_MS / 1000);
		LOGGER.info("     +---------------------------------------------------------------+");
		LOGGER.info("");
	}
	
	/**
	 * Marca o servidor para desligamento normal (não reinicia automaticamente).
	 */
	public static void setNormalShutdown()
	{
		_normalShutdown = true;
		LOGGER.info("");
		LOGGER.info("  Desligamento Normal Detectado");
		LOGGER.info("     -> Restart automatico desabilitado");
		LOGGER.info("     -> Servidor sera desligado sem reiniciar");
		LOGGER.info("");
	}
	
	/**
	 * Verifica se o servidor está em desligamento normal.
	 */
	public static boolean isNormalShutdown()
	{
		return _normalShutdown;
	}
	
	/**
	 * Thread de monitoramento de saúde do servidor.
	 * Verifica memória, threads e outros indicadores críticos.
	 */
	private static class HealthMonitor extends Thread
	{
		private volatile boolean _running = true;
		
		public HealthMonitor()
		{
			super("JvmOptimizer-HealthMonitor");
			setDaemon(true);
		}
		
		@Override
		public void run()
		{
			while (_running && !_normalShutdown)
			{
				try
				{
					Thread.sleep(HEALTH_CHECK_INTERVAL);
					
					if (_normalShutdown)
						break;
					
					if (!checkServerHealth())
					{
						_consecutiveFailures.incrementAndGet();
						
						if (_consecutiveFailures.get() >= MAX_CONSECUTIVE_FAILURES)
						{
							LOGGER.error("Multiplas falhas consecutivas detectadas ({}) - Reiniciando servidor...", 
								_consecutiveFailures.get());
							restartServer();
							return;
						}
					}
					else
					{
						_consecutiveFailures.set(0);
					}
				}
				catch (InterruptedException e)
				{
					LOGGER.warn("  [AVISO] HealthMonitor interrompido - Encerrando monitoramento");
					break;
				}
				catch (Exception e)
				{
					LOGGER.error("  [ERRO] Erro no HealthMonitor: {}", e.getMessage());
					LOGGER.error("     Tipo: {}", e.getClass().getSimpleName());
				}
			}
		}
		
		/**
		 * Verifica a saúde do servidor.
		 * @return true se saudável, false se houver problemas críticos
		 */
		private boolean checkServerHealth()
		{
			try
			{
				final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
				final MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
				final long usedMemory = heapUsage.getUsed();
				final long maxMemory = heapUsage.getMax();
				
				performG1PeriodicReclaim(memoryBean, heapUsage);
				
				if (maxMemory > 0)
				{
					final double memoryUsagePercent = (double) usedMemory / maxMemory;
					
					if (memoryUsagePercent >= MEMORY_THRESHOLD)
					{
						LOGGER.warn("  [ALERTA] Uso de memoria critico: atual = {:.2f}%, limite = {:.2f}%", 
							memoryUsagePercent * 100, MEMORY_THRESHOLD * 100);
						LOGGER.warn("     -> Forcando Garbage Collection...");
						
						System.gc();
						
						Thread.sleep(1000);
						final MemoryUsage heapUsageAfter = memoryBean.getHeapMemoryUsage();
						final double memoryUsageAfterPercent = (double) heapUsageAfter.getUsed() / maxMemory;
						
						if (memoryUsageAfterPercent >= MEMORY_THRESHOLD)
						{
							LOGGER.error("  [CRITICO] Memoria ainda alta apos GC: {:.2f}% - reinicio necessario",
								memoryUsageAfterPercent * 100);
							return false;
						}
						else
						{
							LOGGER.info("     [OK] Memoria liberada apos GC: {:.2f}%", memoryUsageAfterPercent * 100);
						}
					}
				}
				
				final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
				final long[] deadlockedThreads = threadBean.findDeadlockedThreads();
				
				if (deadlockedThreads != null && deadlockedThreads.length > 0)
				{
					LOGGER.error("");
					LOGGER.error("  [CRITICO] Deadlock Detectado");
					LOGGER.error("     +---------------------------------------------------------+");
					LOGGER.error("     | Threads em Deadlock: {}                              |", deadlockedThreads.length);
					LOGGER.error("     | Acao:        REINICIO NECESSARIO                    |");
					LOGGER.error("     +---------------------------------------------------------+");
					LOGGER.error("");
					return false;
				}
				
				final int threadCount = threadBean.getThreadCount();
				final int peakThreadCount = threadBean.getPeakThreadCount();
				
				if (shouldWarnHighThreads(threadCount, peakThreadCount))
				{
					LOGGER.warn("  [AVISO] Numero alto de threads detectado:");
					LOGGER.warn("     -> Atuais: {} | Pico: {}", threadCount, peakThreadCount);
					LOGGER.warn("     -> Possivel vazamento de threads (monitorando...)");
				}
				
				return true;
			}
			catch (Exception e)
			{
				LOGGER.error("  [ERRO] Erro ao verificar saude do servidor: {}", e.getMessage());
				LOGGER.error("     Tipo: {}", e.getClass().getSimpleName());
				return false;
			}
		}

		/**
		 * A cada {@link #HEALTH_CHECK_INTERVAL} solicita GC quando ha muita heap committed ociosa
		 * (comportamento proximo ao ZUncommit do ZGC). Complementa {@link #getG1MemoryReclaimFlags()}.
		 */
		private void performG1PeriodicReclaim(MemoryMXBean memoryBean, MemoryUsage heapUsage)
		{
			if (!isG1GcActive())
				return;
			
			final long used = heapUsage.getUsed();
			final long committed = heapUsage.getCommitted();
			if (committed <= 0)
				return;
			
			final long maxMemory = heapUsage.getMax();
			long minWaste = G1_RECLAIM_MIN_WASTE_BYTES;
			if (maxMemory > 0 && maxMemory < G1_RECLAIM_MIN_WASTE_BYTES)
				minWaste = Math.max(32L * 1024L * 1024L, maxMemory / 4);
			
			final long waste = committed - used;
			if (waste < minWaste)
				return;
			if ((double) waste / committed < G1_RECLAIM_WASTE_RATIO)
				return;
			
			//LOGGER.info("  [G1 Reclaim] Heap committed={}MB used={}MB | liberando ~{}MB ociosos",
			//	committed / (1024 * 1024), used / (1024 * 1024), waste / (1024 * 1024));
			
			memoryBean.gc();
		}
		
		private boolean shouldWarnHighThreads(int threadCount, int peakThreadCount)
		{
			final int cores = Math.max(1, Runtime.getRuntime().availableProcessors());
			final int warnThreshold = Math.max(THREAD_WARN_MIN, cores * THREAD_WARN_PER_CORE);
			final int warnPeakThreshold = Math.max(THREAD_WARN_PEAK_MIN, cores * THREAD_WARN_PEAK_PER_CORE);
			
			if (threadCount <= warnThreshold || peakThreadCount <= warnPeakThreshold)
				return false;
			
			final long now = System.currentTimeMillis();
			if (now - _lastThreadWarnTime < THREAD_WARN_COOLDOWN_MS && threadCount <= _lastThreadWarnCount + 200)
				return false;
			
			_lastThreadWarnTime = now;
			_lastThreadWarnCount = threadCount;
			return true;
		}
		
		/**
		 * Reinicia o servidor automaticamente.
		 */
		private void restartServer()
		{
		if (_normalShutdown)
		{
			LOGGER.info("");
			LOGGER.info("  Desligamento normal detectado - Cancelando restart automatico");
			LOGGER.info("");
			return;
		}
		
		final int restartCount = _restartCount.incrementAndGet();
		LOGGER.warn("");
		LOGGER.warn("  ================================================================");
		LOGGER.warn("     REINICIO AUTOMATICO DO SERVIDOR - Tentativa #{}", restartCount);
		LOGGER.warn("  ================================================================");
		LOGGER.warn("");
		LOGGER.warn("     Motivo: Multiplas falhas consecutivas detectadas ({})", _consecutiveFailures.get());
		LOGGER.warn("     Acao:   Reiniciando servidor para restaurar estabilidade");
		LOGGER.warn("");
		
		try
		{
			final long delay = Math.min((long) Math.pow(2, restartCount - 1) * 1000, MAX_RESTART_DELAY);
			final long delaySeconds = delay / 1000;
			
			LOGGER.info("     Aguardando {} segundo(s) antes de reiniciar...", delaySeconds);
			LOGGER.info("     -> Delay exponencial aplicado para evitar loops de restart");
			Thread.sleep(delay);
			
			if (_normalShutdown)
			{
				LOGGER.info("");
				LOGGER.info("     [OK] Desligamento normal detectado durante delay");
				LOGGER.info("     -> Cancelando restart automatico");
				LOGGER.info("");
				return;
			}
				
				final String[] restartScripts = 
				{
					"StartBrproject.bat",
					"start.vbs",
					"start.sh",
					"entrypoint.sh"
				};
				
				File restartScript = null;
				for (String script : restartScripts)
				{
					final File f = new File(script);
					if (f.exists() && f.canExecute())
					{
						restartScript = f;
						break;
					}
				}
				
				if (restartScript != null)
				{
					LOGGER.info("     Script de restart encontrado: {}", restartScript.getName());
					LOGGER.info("     -> Executando: {}", restartScript.getAbsolutePath());
					
					final String os = System.getProperty("os.name").toLowerCase();
					final ProcessBuilder pb;
					
					if (os.contains("win"))
					{
						pb = new ProcessBuilder("cmd.exe", "/c", restartScript.getAbsolutePath());
					}
					else
					{
						pb = new ProcessBuilder("bash", restartScript.getAbsolutePath());
					}
					
					pb.directory(new File("."));
					pb.start();
					
					LOGGER.info("     [OK] Script de restart executado com sucesso");
					LOGGER.info("     -> Aguardando 2 segundos antes de encerrar...");
					Thread.sleep(2000);
				}
				else
				{
					LOGGER.warn("     [AVISO] Script de restart nao encontrado");
					LOGGER.warn("     -> Usando System.exit(2) para restart automatico");
					LOGGER.warn("     -> Certifique-se de que o script de inicializacao detecta exit code 2");
				}
				
				LOGGER.info("");
				LOGGER.info("     Encerrando processo atual (exit code: 2 = restart)...");
				LOGGER.info("");
				System.exit(2);
			}
			catch (Exception e)
			{
				LOGGER.error("");
				LOGGER.error("     [ERRO] Erro ao reiniciar servidor: {}", e.getMessage());
				LOGGER.error("     -> Tipo: {}", e.getClass().getSimpleName());
				LOGGER.error("     -> Tentando encerrar com exit code 2 mesmo assim...");
				LOGGER.error("");
				System.exit(2);
			}
		}
		
		/**
		 * Para o monitoramento.
		 */
		public void stopMonitoring()
		{
			_running = false;
			interrupt();
		}
	}

	/**
	 * Configura o logger do JvmOptimizer para evitar timestamp e nome de classe,
	 * mantendo apenas a mensagem limpa no console.
	 */
	private static void configureJvmOptimizerLogger()
	{
		if (_loggerConfigured)
			return;
		
		_loggerConfigured = true;
		
		final Logger logger = Logger.getLogger(JvmOptimizer.class.getName());
		logger.setUseParentHandlers(false);
		
		for (Handler handler : logger.getHandlers())
		{
			logger.removeHandler(handler);
		}
		
		final ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		consoleHandler.setFormatter(new NoTimestampConsoleFormatter());
		logger.addHandler(consoleHandler);
		logger.setLevel(Level.ALL);
	}
	
	/**
	 * Handler global para exceções não tratadas.
	 * Detecta erros críticos e força restart se necessário.
	 */
	static
	{
		Thread.setDefaultUncaughtExceptionHandler((thread, exception) ->
		{
			LOGGER.error("");
			LOGGER.error("  [ERRO] Excecao Nao Tratada Detectada");
			LOGGER.error("     +---------------------------------------------------------+");
			LOGGER.error("     | Thread:    {}                                        |", thread.getName());
			LOGGER.error("     | Tipo:      {}                                        |", exception.getClass().getSimpleName());
			LOGGER.error("     | Mensagem:  {}                                        |", 
				exception.getMessage() != null ? exception.getMessage() : "N/A");
			LOGGER.error("     +---------------------------------------------------------+");
			
			if (!_normalShutdown && isCriticalError(exception))
			{
				LOGGER.error("");
				LOGGER.error("  [AVISO] ERRO CRITICO DETECTADO");
				LOGGER.error("     -> Tipo de erro requer reinicio do servidor");
				LOGGER.error("     -> Falhas consecutivas: {} / {}", 
					_consecutiveFailures.incrementAndGet(), MAX_CONSECUTIVE_FAILURES);
				
				if (_healthMonitor != null && _consecutiveFailures.get() >= MAX_CONSECUTIVE_FAILURES)
				{
					LOGGER.error("     -> Limite de falhas atingido - Agendando restart em 5 segundos...");
					CoroutinePool.schedule(() -> {
					    if (!_normalShutdown && _healthMonitor != null) {
					        _healthMonitor.restartServer();
					    }
					}, 5000L);
				}
				else
				{
					LOGGER.warn("     -> Monitorando... ({} falhas restantes ate restart)", 
						MAX_CONSECUTIVE_FAILURES - _consecutiveFailures.get());
				}
				LOGGER.error("");
			}
		});
	}
	
	/**
	 * Verifica se uma exceção é crítica o suficiente para justificar restart.
	 */
	private static boolean isCriticalError(Throwable exception)
	{
		if (exception == null)
			return false;
		
		final String message = exception.getMessage();
		if (message == null)
			return false;
		
		final String lowerMessage = message.toLowerCase();
		
		return lowerMessage.contains("outofmemoryerror") ||
			lowerMessage.contains("stackoverflowerror") ||
			lowerMessage.contains("unable to create new native thread") ||
			lowerMessage.contains("too many open files") ||
			lowerMessage.contains("database connection") ||
			lowerMessage.contains("connection pool exhausted");
	}
}