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
package ext.mods.gameserver.geoengine.pathfinding;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import ext.mods.Config;
import ext.mods.commons.config.ExProperties;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.location.Location;

/**
 * Sistema de cache de pathfinding para pré-cálculo e armazenamento de rotas.
 * Economiza CPU calculando rotas comuns uma única vez e reutilizando-as.
 * 
 * Funcionalidades:
 * - Pré-cálculo de rotas entre pontos importantes
 * - Cache em memória com limite configurável
 * - Persistência em arquivos comprimidos
 * - Carregamento automático na inicialização
 * - Geração de rotas em background
 */
public class PathfinderCache {
    private static final CLogger LOGGER = new CLogger(PathfinderCache.class.getName());
    
    private static final ExProperties GEOENGINE_PROPS = Config.initProperties(Config.GEOENGINE_FILE);
    private static final boolean ENABLE_PATHFINDER_CACHE = GEOENGINE_PROPS.getProperty("EnablePathfinderCache", true);
    private static final int CACHE_SIZE_LIMIT = GEOENGINE_PROPS.getProperty("PathfinderCacheSize", 10000);
    private static final String CACHE_DIRECTORY = GEOENGINE_PROPS.getProperty("PathfinderCacheDirectory", "./data/pathfinder_cache/");
    private static final boolean COMPRESS_CACHE_FILES = GEOENGINE_PROPS.getProperty("CompressCacheFiles", true);
    private static final int BACKGROUND_GENERATION_THREADS = GEOENGINE_PROPS.getProperty("PathfinderGenerationThreads", 4);
    
    private static PathfinderCache _instance;
    
    private final Map<String, List<Location>> _pathCache = new ConcurrentHashMap<>();
    private final Map<String, Long> _cacheTimestamps = new ConcurrentHashMap<>();
    private final Queue<String> _cacheAccessOrder = new ConcurrentLinkedQueue<>();
    
    private long _cacheHits = 0;
    private long _cacheMisses = 0;
    private long _pathsGenerated = 0;
    private long _pathsLoaded = 0;
    
    private ExecutorService _generationExecutor;
    
    private PathfinderCache() {
        if (ENABLE_PATHFINDER_CACHE) {
            initializeCache();
        }
    }
    
    public static PathfinderCache getInstance() {
        if (_instance == null) {
            synchronized (PathfinderCache.class) {
                if (_instance == null) {
                    _instance = new PathfinderCache();
                }
            }
        }
        return _instance;
    }
    
    /**
     * Inicializa o sistema de cache.
     */
    private void initializeCache() {
        try {
            Path cacheDir = Paths.get(CACHE_DIRECTORY);
            if (!Files.exists(cacheDir)) {
                Files.createDirectories(cacheDir);
                LOGGER.info("Diretório de cache criado: {}", CACHE_DIRECTORY);
            }
            
            _generationExecutor = Executors.newFixedThreadPool(BACKGROUND_GENERATION_THREADS);
            
            loadCacheFromFiles();
            
            LOGGER.info("PathfinderCache inicializado - Cache: {}, Threads: {}", 
                CACHE_SIZE_LIMIT, BACKGROUND_GENERATION_THREADS);
                
        } catch (Exception e) {
            LOGGER.error("Erro ao inicializar PathfinderCache", e);
        }
    }
    
    /**
     * Obtém uma rota do cache ou calcula se não existir.
     * 
     * @param from Ponto de origem
     * @param to Ponto de destino
     * @param useOptimized Parâmetro mantido para compatibilidade (não usado mais)
     * @return Lista de pontos da rota, ou null se não encontrar
     */
    public List<Location> getPath(Location from, Location to, boolean useOptimized) {
        if (!ENABLE_PATHFINDER_CACHE) {
            return calculatePathDirectly(from, to);
        }
        
        final String cacheKey = generateCacheKey(from, to, false);
        
        List<Location> cachedPath = _pathCache.get(cacheKey);
        if (cachedPath != null) {
            _cacheHits++;
            updateAccessOrder(cacheKey);
            return new ArrayList<>(cachedPath);
        }
        
        _cacheMisses++;
        
        List<Location> filePath = loadPathFromFile(cacheKey);
        if (filePath != null) {
            _pathsLoaded++;
            addToMemoryCache(cacheKey, filePath);
            return new ArrayList<>(filePath);
        }
        
        List<Location> newPath = calculatePathDirectly(from, to);
        if (newPath != null && !newPath.isEmpty()) {
            _pathsGenerated++;
            
            addToMemoryCache(cacheKey, newPath);
            savePathToFile(cacheKey, newPath);
            
            return new ArrayList<>(newPath);
        }
        
        return null;
    }
    
    /**
     * Pré-calculates rotas entre pontos importantes em background.
     * 
     * @param waypoints Lista de pontos importantes
     * @param maxDistance Distância máxima entre pontos para calcular rotas
     */
    public void preCalculatePaths(List<Location> waypoints, int maxDistance) {
        if (!ENABLE_PATHFINDER_CACHE || _generationExecutor == null) {
            return;
        }
        
        LOGGER.info("Iniciando pré-cálculo de rotas para {} waypoints", waypoints.size());
        
        int totalPaths = 0;
        for (int i = 0; i < waypoints.size(); i++) {
            for (int j = i + 1; j < waypoints.size(); j++) {
                Location from = waypoints.get(i);
                Location to = waypoints.get(j);
                
                if (from.distance3D(to) <= maxDistance) {
                    totalPaths++;
                    
                    _generationExecutor.submit(() -> {
                        try {
                            getPath(from, to, false);
                        } catch (Exception e) {
                            LOGGER.warn("Erro ao calcular rota de {} para {}", from, to, e);
                        }
                    });
                }
            }
        }
        
        LOGGER.info("Submetidas {} rotas para pré-cálculo em background", totalPaths);
    }
    
    /**
     * Gera chave única para o cache baseada nas coordenadas e tipo de pathfinding.
     */
    private String generateCacheKey(Location from, Location to, boolean useOptimized) {
        return String.format("%d_%d_%d_%d_%d_%d_%s", 
            from.getX(), from.getY(), from.getZ(),
            to.getX(), to.getY(), to.getZ(),
            useOptimized ? "opt" : "leg");
    }
    
    /**
     * Calcula rota diretamente usando o GeoEngine legado.
     */
    private List<Location> calculatePathDirectly(Location from, Location to) {
        try {
            return GeoEngine.getInstance().findPath(
                from.getX(), from.getY(), from.getZ(),
                to.getX(), to.getY(), to.getZ(),
                true, null
            );
        } catch (Exception e) {
            LOGGER.warn("Erro ao calcular rota de {} para {}", from, to, e);
            return null;
        }
    }
    
    /**
     * Adiciona rota ao cache em memória, respeitando limite de tamanho.
     */
    private void addToMemoryCache(String cacheKey, List<Location> path) {
        synchronized (_pathCache) {
            while (_pathCache.size() >= CACHE_SIZE_LIMIT && !_cacheAccessOrder.isEmpty()) {
                String oldestKey = _cacheAccessOrder.poll();
                _pathCache.remove(oldestKey);
                _cacheTimestamps.remove(oldestKey);
            }
            
            _pathCache.put(cacheKey, new ArrayList<>(path));
            _cacheTimestamps.put(cacheKey, System.currentTimeMillis());
            updateAccessOrder(cacheKey);
        }
    }
    
    /**
     * Atualiza ordem de acesso para LRU (Least Recently Used).
     */
    private void updateAccessOrder(String cacheKey) {
        _cacheAccessOrder.remove(cacheKey);
        _cacheAccessOrder.offer(cacheKey);
    }
    
    /**
     * Salva rota em arquivo comprimido.
     */
    private void savePathToFile(String cacheKey, List<Location> path) {
        try {
            Path filePath = Paths.get(CACHE_DIRECTORY, cacheKey + ".path");
            
            if (COMPRESS_CACHE_FILES) {
                try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
                     GZIPOutputStream gzos = new GZIPOutputStream(fos);
                     ObjectOutputStream oos = new ObjectOutputStream(gzos)) {
                    
                    oos.writeObject(path);
                }
            } else {
                try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
                     ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    
                    oos.writeObject(path);
                }
            }
            
        } catch (Exception e) {
            LOGGER.warn("Erro ao salvar rota no arquivo: {}", cacheKey, e);
        }
    }
    
    /**
     * Carrega rota de arquivo.
     */
    @SuppressWarnings("unchecked")
    private List<Location> loadPathFromFile(String cacheKey) {
        try {
            Path filePath = Paths.get(CACHE_DIRECTORY, cacheKey + ".path");
            
            if (!Files.exists(filePath)) {
                return null;
            }
            
            if (COMPRESS_CACHE_FILES) {
                try (FileInputStream fis = new FileInputStream(filePath.toFile());
                     GZIPInputStream gzis = new GZIPInputStream(fis);
                     ObjectInputStream ois = new ObjectInputStream(gzis)) {
                    
                    return (List<Location>) ois.readObject();
                }
            } else {
                try (FileInputStream fis = new FileInputStream(filePath.toFile());
                     ObjectInputStream ois = new ObjectInputStream(fis)) {
                    
                    return (List<Location>) ois.readObject();
                }
            }
            
        } catch (Exception e) {
            LOGGER.warn("Erro ao carregar rota do arquivo: {}", cacheKey, e);
            return null;
        }
    }
    
    /**
     * Carrega cache existente dos arquivos.
     */
    private void loadCacheFromFiles() {
        try {
            Path cacheDir = Paths.get(CACHE_DIRECTORY);
            if (!Files.exists(cacheDir)) {
                return;
            }
            
            int loadedCount = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(cacheDir, "*.path")) {
                for (Path filePath : stream) {
                    String fileName = filePath.getFileName().toString();
                    String cacheKey = fileName.substring(0, fileName.lastIndexOf('.'));
                    
                    List<Location> path = loadPathFromFile(cacheKey);
                    if (path != null) {
                        addToMemoryCache(cacheKey, path);
                        loadedCount++;
                    }
                }
            }
            
            LOGGER.info("Carregadas {} rotas do cache de arquivos", loadedCount);
            
        } catch (Exception e) {
            LOGGER.error("Erro ao carregar cache de arquivos", e);
        }
    }
    
    /**
     * Salva todo o cache em memória para arquivos.
     */
    public void saveAllCacheToFiles() {
        if (!ENABLE_PATHFINDER_CACHE) {
            return;
        }
        
        LOGGER.info("Salvando cache completo para arquivos...");
        
        int savedCount = 0;
        for (Map.Entry<String, List<Location>> entry : _pathCache.entrySet()) {
            savePathToFile(entry.getKey(), entry.getValue());
            savedCount++;
        }
        
        LOGGER.info("Salvas {} rotas no cache de arquivos", savedCount);
    }
    
    /**
     * Limpa cache em memória.
     */
    public void clearMemoryCache() {
        synchronized (_pathCache) {
            _pathCache.clear();
            _cacheTimestamps.clear();
            _cacheAccessOrder.clear();
        }
        
        LOGGER.info("Cache em memória limpo");
    }
    
    /**
     * Remove arquivos de cache antigos.
     * 
     * @param maxAgeDays Idade máxima em dias
     */
    public void cleanupOldCacheFiles(int maxAgeDays) {
        try {
            Path cacheDir = Paths.get(CACHE_DIRECTORY);
            if (!Files.exists(cacheDir)) {
                return;
            }
            
            long maxAgeMillis = maxAgeDays * 24L * 60L * 60L * 1000L;
            long currentTime = System.currentTimeMillis();
            
            int removedCount = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(cacheDir, "*.path")) {
                for (Path filePath : stream) {
                    long fileAge = currentTime - Files.getLastModifiedTime(filePath).toMillis();
                    if (fileAge > maxAgeMillis) {
                        Files.delete(filePath);
                        removedCount++;
                    }
                }
            }
            
            LOGGER.info("Removidos {} arquivos de cache antigos", removedCount);
            
        } catch (Exception e) {
            LOGGER.error("Erro ao limpar arquivos de cache antigos", e);
        }
    }
    
    /**
     * Retorna estatísticas do cache.
     */
    public CacheStatistics getStatistics() {
        return new CacheStatistics(
            _cacheHits,
            _cacheMisses,
            _pathsGenerated,
            _pathsLoaded,
            _pathCache.size(),
            CACHE_SIZE_LIMIT
        );
    }
    
    /**
     * Classe para estatísticas do cache.
     */
    public static class CacheStatistics {
        public final long cacheHits;
        public final long cacheMisses;
        public final long pathsGenerated;
        public final long pathsLoaded;
        public final int currentCacheSize;
        public final int maxCacheSize;
        
        public CacheStatistics(long cacheHits, long cacheMisses, long pathsGenerated, 
                             long pathsLoaded, int currentCacheSize, int maxCacheSize) {
            this.cacheHits = cacheHits;
            this.cacheMisses = cacheMisses;
            this.pathsGenerated = pathsGenerated;
            this.pathsLoaded = pathsLoaded;
            this.currentCacheSize = currentCacheSize;
            this.maxCacheSize = maxCacheSize;
        }
        
        public double getHitRatio() {
            long total = cacheHits + cacheMisses;
            return total > 0 ? (double) cacheHits / total : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Cache Stats - Hits: %d, Misses: %d, Hit Ratio: %.2f%%, " +
                "Generated: %d, Loaded: %d, Size: %d/%d",
                cacheHits, cacheMisses, getHitRatio() * 100,
                pathsGenerated, pathsLoaded, currentCacheSize, maxCacheSize
            );
        }
    }
    
    /**
     * Shutdown do cache.
     */
    public void shutdown() {
        if (_generationExecutor != null) {
            _generationExecutor.shutdown();
            try {
                if (!_generationExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    _generationExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                _generationExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        saveAllCacheToFiles();
        LOGGER.info("PathfinderCache finalizado");
    }
}
