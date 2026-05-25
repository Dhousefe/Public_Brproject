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
package ext.mods.gameserver.handler.admincommandhandlers;

import java.util.List;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.geoengine.pathfinding.PathfinderCache;
import ext.mods.gameserver.geoengine.pathfinding.PathfinderGenerator;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;

/**
 * Comando de administrador para gerenciar o sistema de cache de pathfinding.
 * 
 * Comandos disponíveis:
 *
 *
 *
 *
 *
 *
 */
public class AdminPathfinderCache implements IAdminCommandHandler {
    
    private static final String[] ADMIN_COMMANDS = {
        "admin_pathcache"
    };
    
    @Override
    public void useAdminCommand(String command, Player activeChar) {
        if (command.startsWith("admin_pathcache")) {
            handlePathfinderCacheCommand(command, activeChar);
        }
    }
    
    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
    
    /**
     * Processa comandos do sistema de cache de pathfinding.
     */
    private void handlePathfinderCacheCommand(String command, Player activeChar) {
        String[] parts = command.split(" ");
        
        if (parts.length < 2) {
            showHelp(activeChar);
            return;
        }
        
        String subCommand = parts[1].toLowerCase();
        
        switch (subCommand) {
            case "stats":
                showCacheStatistics(activeChar);
                break;
                
            case "clear":
                clearMemoryCache(activeChar);
                break;
                
            case "save":
                saveCacheToFiles(activeChar);
                break;
                
            case "generate":
                generatePaths(activeChar, parts);
                break;
                
            case "cleanup":
                cleanupOldFiles(activeChar, parts);
                break;
                
            case "reload":
                reloadCache(activeChar);
                break;
                
            case "test":
                testPathfinding(activeChar);
                break;
                
            default:
                showHelp(activeChar);
                break;
        }
    }
    
    /**
     * Mostra ajuda dos comandos disponíveis.
     */
    private void showHelp(Player activeChar) {
        activeChar.sendMessage("=== COMANDOS DE CACHE DE PATHFINDING ===");
        activeChar.sendMessage("//pathcache stats - Mostra estatísticas do cache");
        activeChar.sendMessage("//pathcache clear - Limpa cache em memória");
        activeChar.sendMessage("//pathcache save - Salva cache para arquivos");
        activeChar.sendMessage("//pathcache generate [distancia] - Gera rotas automaticamente");
        activeChar.sendMessage("//pathcache cleanup [dias] - Remove arquivos antigos");
        activeChar.sendMessage("//pathcache reload - Recarrega cache dos arquivos");
        activeChar.sendMessage("//pathcache test - Testa pathfinding entre duas posições");
    }
    
    /**
     * Mostra estatísticas do cache.
     */
    private void showCacheStatistics(Player activeChar) {
        PathfinderCache cache = PathfinderCache.getInstance();
        PathfinderCache.CacheStatistics stats = cache.getStatistics();
        
        activeChar.sendMessage("=== ESTATÍSTICAS DO CACHE DE PATHFINDING ===");
        activeChar.sendMessage("Cache Hits: " + stats.cacheHits);
        activeChar.sendMessage("Cache Misses: " + stats.cacheMisses);
        activeChar.sendMessage("Taxa de Acerto: " + String.format("%.2f%%", stats.getHitRatio() * 100));
        activeChar.sendMessage("Rotas Geradas: " + stats.pathsGenerated);
        activeChar.sendMessage("Rotas Carregadas: " + stats.pathsLoaded);
        activeChar.sendMessage("Tamanho Atual: " + stats.currentCacheSize + "/" + stats.maxCacheSize);
        activeChar.sendMessage("Uso da Memória: " + String.format("%.1f%%", 
            (double)stats.currentCacheSize / stats.maxCacheSize * 100));
    }
    
    /**
     * Limpa cache em memória.
     */
    private void clearMemoryCache(Player activeChar) {
        PathfinderCache cache = PathfinderCache.getInstance();
        cache.clearMemoryCache();
        activeChar.sendMessage("Cache em memória limpo com sucesso!");
    }
    
    /**
     * Salva cache para arquivos.
     */
    private void saveCacheToFiles(Player activeChar) {
        PathfinderCache cache = PathfinderCache.getInstance();
        cache.saveAllCacheToFiles();
        activeChar.sendMessage("Cache salvo para arquivos com sucesso!");
    }
    
    /**
     * Gera rotas automaticamente.
     */
    private void generatePaths(Player activeChar, String[] parts) {
        int maxDistance = 2000;
        
        if (parts.length > 2) {
            try {
                maxDistance = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                activeChar.sendMessage("Distância inválida. Usando padrão: " + maxDistance);
            }
        }
        
        activeChar.sendMessage("Iniciando geração de rotas (distância máxima: " + maxDistance + ")...");
        
        ThreadPool.schedule(() -> {
            try {
                PathfinderGenerator generator = PathfinderGenerator.getInstance();
                generator.generateStrategicWaypoints();
                activeChar.sendMessage("Geração de rotas concluída!");
            } catch (Exception e) {
                activeChar.sendMessage("Erro durante geração de rotas: " + e.getMessage());
            }
        }, 0);
    }
    
    /**
     * Remove arquivos de cache antigos.
     */
    private void cleanupOldFiles(Player activeChar, String[] parts) {
        int maxAgeDays = 30;
        
        if (parts.length > 2) {
            try {
                maxAgeDays = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                activeChar.sendMessage("Idade inválida. Usando padrão: " + maxAgeDays + " dias");
            }
        }
        
        PathfinderCache cache = PathfinderCache.getInstance();
        cache.cleanupOldCacheFiles(maxAgeDays);
        activeChar.sendMessage("Limpeza de arquivos antigos concluída (mais de " + maxAgeDays + " dias)!");
    }
    
    /**
     * Recarrega cache dos arquivos.
     */
    private void reloadCache(Player activeChar) {
        PathfinderCache cache = PathfinderCache.getInstance();
        cache.clearMemoryCache();
        activeChar.sendMessage("Cache será recarregado automaticamente na próxima busca!");
    }
    
    /**
     * Testa pathfinding entre duas posições.
     */
    private void testPathfinding(Player activeChar) {
        Location currentPos = activeChar.getPosition();
        
        Location testPos = new Location(
            currentPos.getX() + 1000,
            currentPos.getY() + 1000,
            currentPos.getZ()
        );
        
        activeChar.sendMessage("Testando pathfinding de " + currentPos + " para " + testPos);
        
        PathfinderCache cache = PathfinderCache.getInstance();
        List<Location> path = cache.getPath(currentPos, testPos, true);
        
        if (path != null && !path.isEmpty()) {
            activeChar.sendMessage("Rota encontrada! Pontos: " + path.size());
            activeChar.sendMessage("Primeiro ponto: " + path.get(0));
            if (path.size() > 1) {
                activeChar.sendMessage("Último ponto: " + path.get(path.size() - 1));
            }
        } else {
            activeChar.sendMessage("Nenhuma rota encontrada!");
        }
    }
}
