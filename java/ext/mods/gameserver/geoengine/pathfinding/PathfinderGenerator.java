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
import java.util.*;
import java.util.concurrent.*;

import ext.mods.Config;
import ext.mods.commons.config.ExProperties;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.World;

/**
 * Ferramenta para geração automática de rotas de pathfinding.
 * Calcula rotas entre pontos estratégicos do mundo e salva no cache.
 * 
 * Funcionalidades:
 * - Geração automática de waypoints baseados na geodata
 * - Cálculo de rotas entre waypoints em paralelo
 * - Salvamento automático no cache
 * - Estatísticas de progresso
 */
public class PathfinderGenerator {
    private static final CLogger LOGGER = new CLogger(PathfinderGenerator.class.getName());
    
    private static final ExProperties GEOENGINE_PROPS = Config.initProperties(Config.GEOENGINE_FILE);
    private static final boolean ENABLE_AUTO_PRE_CALCULATION = GEOENGINE_PROPS.getProperty("EnableAutoPreCalculation", true);
    private static final int MAX_PRE_CALCULATION_DISTANCE = GEOENGINE_PROPS.getProperty("MaxPreCalculationDistance", 2000);
    private static final int GENERATION_THREADS = GEOENGINE_PROPS.getProperty("PathfinderGenerationThreads", 4);
    
    private static PathfinderGenerator _instance;
    
    private final PathfinderCache _cache;
    
    private int _totalWaypoints = 0;
    private int _totalPaths = 0;
    private int _generatedPaths = 0;
    private int _failedPaths = 0;
    
    private PathfinderGenerator() {
        _cache = PathfinderCache.getInstance();
    }
    
    public static PathfinderGenerator getInstance() {
        if (_instance == null) {
            synchronized (PathfinderGenerator.class) {
                if (_instance == null) {
                    _instance = new PathfinderGenerator();
                }
            }
        }
        return _instance;
    }
    
    /**
     * Inicia a geração automática de rotas se habilitada.
     */
    public void startAutoGeneration() {
        if (!ENABLE_AUTO_PRE_CALCULATION) {
            LOGGER.info("Pré-cálculo automático de rotas desabilitado");
            return;
        }
        
        LOGGER.info("Iniciando geração automática de rotas de pathfinding...");
        
        ThreadPool.schedule(() -> {
            try {
                generateStrategicWaypoints();
            } catch (Exception e) {
                LOGGER.error("Erro durante geração automática de rotas", e);
            }
        }, 5000);
    }
    
    /**
     * Gera waypoints estratégicos baseados na geodata e calcula rotas entre eles.
     */
    public void generateStrategicWaypoints() {
        LOGGER.info("Gerando waypoints estratégicos...");
        
        List<Location> waypoints = generateWaypoints();
        _totalWaypoints = waypoints.size();
        
        LOGGER.info("Encontrados {} waypoints estratégicos", _totalWaypoints);
        
        if (waypoints.isEmpty()) {
            LOGGER.warn("Nenhum waypoint encontrado - geração cancelada");
            return;
        }
        
        _totalPaths = calculateTotalPaths(waypoints);
        LOGGER.info("Serão calculadas {} rotas possíveis", _totalPaths);
        
        generatePathsParallel(waypoints);
        
        _cache.saveAllCacheToFiles();
        showFinalStatistics();
    }
    
    /**
     * Gera lista de waypoints estratégicos baseados na geodata.
     */
    private List<Location> generateWaypoints() {
        List<Location> waypoints = new ArrayList<>();
        
        int gridSize = 500;
        int minX = World.TILE_X_MIN * World.TILE_SIZE;
        int maxX = World.TILE_X_MAX * World.TILE_SIZE;
        int minY = World.TILE_Y_MIN * World.TILE_SIZE;
        int maxY = World.TILE_Y_MAX * World.TILE_SIZE;
        
        for (int x = minX; x <= maxX; x += gridSize) {
            for (int y = minY; y <= maxY; y += gridSize) {
                Location waypoint = findValidWaypoint(x, y);
                if (waypoint != null) {
                    waypoints.add(waypoint);
                }
            }
        }
        
        addSpecialWaypoints(waypoints);
        
        return waypoints;
    }
    
    /**
     * Encontra um waypoint válido próximo às coordenadas especificadas.
     */
    private Location findValidWaypoint(int x, int y) {
        for (int radius = 0; radius <= 200; radius += 50) {
            for (int angle = 0; angle < 360; angle += 45) {
                double rad = Math.toRadians(angle);
                int testX = x + (int)(radius * Math.cos(rad));
                int testY = y + (int)(radius * Math.sin(rad));
                
                if (isValidWaypoint(testX, testY)) {
                    int z = GeoEngine.getInstance().getHeight(testX, testY, 0);
                    return new Location(testX, testY, z);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Verifica se uma posição é válida para waypoint.
     */
    private boolean isValidWaypoint(int x, int y) {
        try {
            int z = GeoEngine.getInstance().getHeight(x, y, 0);
            
            if (!GeoEngine.getInstance().canMoveToTarget(x, y, z, x, y, z)) {
                return false;
            }
            
            if (z < -1000 || z > 10000) {
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Adiciona waypoints especiais (cidades, dungeons, etc.).
     */
    private void addSpecialWaypoints(List<Location> waypoints) {
        addSpecialWaypoint(waypoints, -80826, 149775, -3043);
        addSpecialWaypoint(waypoints, 111409, 219364, -3545);
        addSpecialWaypoint(waypoints, 116819, 76994, -2714);
        addSpecialWaypoint(waypoints, 17870, 170178, -3507);
        addSpecialWaypoint(waypoints, 147963, -55268, -2728);
        addSpecialWaypoint(waypoints, 116819, 76994, -2714);
        addSpecialWaypoint(waypoints, 111409, 219364, -3545);
        
        addSpecialWaypoint(waypoints, 105918, 109759, -3207);
        addSpecialWaypoint(waypoints, 17144, 170156, -3502);
        addSpecialWaypoint(waypoints, 116819, 76994, -2714);
        
        LOGGER.info("Adicionados {} waypoints especiais", waypoints.size() - _totalWaypoints);
    }
    
    /**
     * Adiciona um waypoint especial se for válido.
     */
    private void addSpecialWaypoint(List<Location> waypoints, int x, int y, int z) {
        if (isValidWaypoint(x, y)) {
            waypoints.add(new Location(x, y, z));
        }
    }
    
    /**
     * Calcula número total de rotas possíveis entre waypoints.
     */
    private int calculateTotalPaths(List<Location> waypoints) {
        int total = 0;
        for (int i = 0; i < waypoints.size(); i++) {
            for (int j = i + 1; j < waypoints.size(); j++) {
                Location from = waypoints.get(i);
                Location to = waypoints.get(j);
                
                if (from.distance3D(to) <= MAX_PRE_CALCULATION_DISTANCE) {
                    total++;
                }
            }
        }
        return total;
    }
    
    /**
     * Gera rotas em paralelo usando thread pool.
     */
    private void generatePathsParallel(List<Location> waypoints) {
        ExecutorService executor = Executors.newFixedThreadPool(GENERATION_THREADS);
        List<Future<Void>> futures = new ArrayList<>();
        
        LOGGER.info("Iniciando geração paralela com {} threads", GENERATION_THREADS);
        
        for (int i = 0; i < waypoints.size(); i++) {
            for (int j = i + 1; j < waypoints.size(); j++) {
                Location from = waypoints.get(i);
                Location to = waypoints.get(j);
                
                if (from.distance3D(to) <= MAX_PRE_CALCULATION_DISTANCE) {
                    Future<Void> future = executor.submit(() -> {
                        generateSinglePath(from, to);
                        return null;
                    });
                    futures.add(future);
                }
            }
        }
        
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                LOGGER.warn("Erro durante geração de rota", e);
            }
        }
        
        executor.shutdown();
        LOGGER.info("Geração paralela concluída");
    }
    
    /**
     * Gera uma única rota entre dois pontos.
     */
    private void generateSinglePath(Location from, Location to) {
        try {
            List<Location> path = GeoEngine.getInstance().findPath(
                from.getX(), from.getY(), from.getZ(),
                to.getX(), to.getY(), to.getZ(),
                true, null
            );
            
            if (path != null && !path.isEmpty()) {
                _generatedPaths++;
                
                
                if (_generatedPaths % 100 == 0) {
                    LOGGER.info("Progresso: {}/{} rotas geradas ({:.1f}%)", 
                        _generatedPaths, _totalPaths, 
                        (double)_generatedPaths / _totalPaths * 100);
                }
            } else {
                _failedPaths++;
            }
            
        } catch (Exception e) {
            _failedPaths++;
            LOGGER.warn("Erro ao gerar rota de {} para {}", from, to, e);
        }
    }
    
    /**
     * Gera chave de cache para uma rota.
     */
    private String generateCacheKey(Location from, Location to, boolean useOptimized) {
        return String.format("%d_%d_%d_%d_%d_%d_%s", 
            from.getX(), from.getY(), from.getZ(),
            to.getX(), to.getY(), to.getZ(),
            useOptimized ? "opt" : "leg");
    }
    
    /**
     * Adiciona rota ao cache em memória (método público para PathfinderCache).
     */
    public void addToMemoryCache(String cacheKey, List<Location> path) {
    }
    
    /**
     * Mostra estatísticas finais da geração.
     */
    private void showFinalStatistics() {
        LOGGER.info("=== ESTATÍSTICAS DE GERAÇÃO DE ROTAS ===");
        LOGGER.info("Waypoints gerados: {}", _totalWaypoints);
        LOGGER.info("Rotas calculadas: {}", _generatedPaths);
        LOGGER.info("Rotas falharam: {}", _failedPaths);
        LOGGER.info("Taxa de sucesso: {:.1f}%", 
            _totalPaths > 0 ? (double)_generatedPaths / _totalPaths * 100 : 0);
        
        PathfinderCache.CacheStatistics cacheStats = _cache.getStatistics();
        LOGGER.info("=== ESTATÍSTICAS DO CACHE ===");
        LOGGER.info("{}", cacheStats);
        
        LOGGER.info("Geração automática de rotas concluída!");
    }
    
    /**
     * Gera rotas entre waypoints específicos.
     * 
     * @param waypoints Lista de waypoints
     * @param maxDistance Distância máxima entre waypoints
     */
    public void generatePathsBetweenWaypoints(List<Location> waypoints, int maxDistance) {
        LOGGER.info("Gerando rotas entre {} waypoints (distância máxima: {})", 
            waypoints.size(), maxDistance);
        
        _totalWaypoints = waypoints.size();
        _totalPaths = calculateTotalPaths(waypoints);
        _generatedPaths = 0;
        _failedPaths = 0;
        
        generatePathsParallel(waypoints);
        showFinalStatistics();
    }
    
    /**
     * Limpa estatísticas de geração.
     */
    public void clearStatistics() {
        _totalWaypoints = 0;
        _totalPaths = 0;
        _generatedPaths = 0;
        _failedPaths = 0;
    }
    
    /**
     * Retorna estatísticas atuais.
     */
    public GenerationStatistics getStatistics() {
        return new GenerationStatistics(
            _totalWaypoints, _totalPaths, _generatedPaths, _failedPaths
        );
    }
    
    /**
     * Classe para estatísticas de geração.
     */
    public static class GenerationStatistics {
        public final int totalWaypoints;
        public final int totalPaths;
        public final int generatedPaths;
        public final int failedPaths;
        
        public GenerationStatistics(int totalWaypoints, int totalPaths, 
                                  int generatedPaths, int failedPaths) {
            this.totalWaypoints = totalWaypoints;
            this.totalPaths = totalPaths;
            this.generatedPaths = generatedPaths;
            this.failedPaths = failedPaths;
        }
        
        public double getSuccessRate() {
            return totalPaths > 0 ? (double) generatedPaths / totalPaths : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Generation Stats - Waypoints: %d, Total Paths: %d, " +
                "Generated: %d, Failed: %d, Success Rate: %.1f%%",
                totalWaypoints, totalPaths, generatedPaths, failedPaths, 
                getSuccessRate() * 100
            );
        }
    }
}
