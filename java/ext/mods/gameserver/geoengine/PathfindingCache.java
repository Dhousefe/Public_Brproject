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
package ext.mods.gameserver.geoengine;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ext.mods.Config;
import ext.mods.gameserver.model.location.Location;

/**
 * Sistema de cache para pathfinding para reduzir carga de CPU.
 * 
 * OTIMIZAÇÕES:
 * - Cache de paths recentes (evita recalcular paths idênticos)
 * - Limite de tamanho (evita uso excessivo de RAM)
 * - Thread-safe (ConcurrentHashMap)
 * - Estatísticas de hit/miss
 * - Configurável via geoengine.properties
 * 
 * QUANDO USAR:
 * - Movimento de NPCs (patrulha, aggro)
 * - Movimento de Players (clique para mover)
 * - Follow tasks
 * 
 * QUANDO NÃO USAR:
 * - Debugging de paths
 * - Situações críticas de combate
 */
public class PathfindingCache
{
	private static final ConcurrentHashMap<String, CachedPath> _cache = new ConcurrentHashMap<>();
	
	private static final AtomicInteger _hits = new AtomicInteger(0);
	private static final AtomicInteger _misses = new AtomicInteger(0);
	
	/**
	 * Obtém um path do cache ou retorna null se não existir/expirado.
	 * @param ox Origin X
	 * @param oy Origin Y
	 * @param oz Origin Z
	 * @param tx Target X
	 * @param ty Target Y
	 * @param tz Target Z
	 * @return Path cacheado ou null se não existe
	 */
	public static List<Location> getPath(int ox, int oy, int oz, int tx, int ty, int tz)
	{
		if (!Config.ENABLE_PATHFINDING_CACHE)
			return null;
		
		final String key = createKey(
			roundCoord(ox), roundCoord(oy), roundCoord(oz),
			roundCoord(tx), roundCoord(ty), roundCoord(tz)
		);
		
		final CachedPath cached = _cache.get(key);
		
		if (cached == null)
		{
			_misses.incrementAndGet();
			return null;
		}
		
		if (System.currentTimeMillis() - cached.timestamp > Config.PATHFINDING_CACHE_EXPIRATION)
		{
			_cache.remove(key);
			_misses.incrementAndGet();
			return null;
		}
		
		_hits.incrementAndGet();
		return cached.path;
	}
	
	/**
	 * Armazena um path no cache.
	 * @param ox Origin X
	 * @param oy Origin Y
	 * @param oz Origin Z
	 * @param tx Target X
	 * @param ty Target Y
	 * @param tz Target Z
	 * @param path Path calculado
	 */
	public static void cachePath(int ox, int oy, int oz, int tx, int ty, int tz, List<Location> path)
	{
		if (!Config.ENABLE_PATHFINDING_CACHE)
			return;
		
		if (path == null || path.isEmpty())
			return;
		
		if (_cache.size() >= Config.PATHFINDING_CACHE_SIZE)
		{
			clearOldEntries();
		}
		
		final String key = createKey(
			roundCoord(ox), roundCoord(oy), roundCoord(oz),
			roundCoord(tx), roundCoord(ty), roundCoord(tz)
		);
		
		_cache.put(key, new CachedPath(path, System.currentTimeMillis()));
	}
	
	/**
	 * Limpa entradas antigas do cache (25% das mais antigas).
	 */
	private static void clearOldEntries()
	{
		final long now = System.currentTimeMillis();
		final int toRemove = Config.PATHFINDING_CACHE_SIZE / 4;
		int removed = 0;
		
		for (var entry : _cache.entrySet())
		{
			if (removed >= toRemove)
				break;
			
			if (now - entry.getValue().timestamp > Config.PATHFINDING_CACHE_EXPIRATION / 2)
			{
				_cache.remove(entry.getKey());
				removed++;
			}
		}
	}
	
	/**
	 * Limpa todo o cache.
	 */
	public static void clearCache()
	{
		_cache.clear();
		_hits.set(0);
		_misses.set(0);
	}
	
	/**
	 * @return Taxa de acerto do cache (0.0 a 1.0)
	 */
	public static double getHitRate()
	{
		int total = _hits.get() + _misses.get();
		return total == 0 ? 0.0 : (double) _hits.get() / total;
	}
	
	/**
	 * @return Estatísticas do cache
	 */
	public static String getStats()
	{
		return String.format("PathfindingCache: %d entries, %.2f%% hit rate (%d hits, %d misses)",
			_cache.size(),
			getHitRate() * 100,
			_hits.get(),
			_misses.get()
		);
	}
	
	/**
	 * Arredonda coordenada para célula geodata (~16 unidades).
	 */
	private static int roundCoord(int coord)
	{
		return (coord / 16) * 16;
	}
	
	/**
	 * Cria chave única para o cache.
	 */
	private static String createKey(int ox, int oy, int oz, int tx, int ty, int tz)
	{
		return ox + "," + oy + "," + oz + ">" + tx + "," + ty + "," + tz;
	}
	
	/**
	 * Classe interna para armazenar path com timestamp.
	 */
	private static class CachedPath
	{
		final List<Location> path;
		final long timestamp;
		
		CachedPath(List<Location> path, long timestamp)
		{
			this.path = path;
			this.timestamp = timestamp;
		}
	}
	
	private PathfindingCache()
	{
	}
}

