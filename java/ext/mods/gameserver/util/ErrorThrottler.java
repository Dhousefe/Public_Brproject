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
package ext.mods.gameserver.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import ext.mods.commons.logging.CLogger;

/**
 * Utilitário para controlar spam de logs de erro
 */
public class ErrorThrottler
{
	private static final CLogger LOGGER = new CLogger(ErrorThrottler.class.getName());
	
	private static final ConcurrentHashMap<String, ErrorInfo> ERROR_COUNTS = new ConcurrentHashMap<>();
	
	private static final int MAX_ERRORS_PER_MINUTE = 5;
	private static final long THROTTLE_WINDOW_MS = 60000;
	
	private static class ErrorInfo
	{
		private final AtomicInteger count = new AtomicInteger(0);
		private final AtomicLong lastReset = new AtomicLong(System.currentTimeMillis());
		
		public boolean shouldLog()
		{
			long now = System.currentTimeMillis();
			long lastResetTime = lastReset.get();
			
			if (now - lastResetTime > THROTTLE_WINDOW_MS)
			{
				if (lastReset.compareAndSet(lastResetTime, now))
				{
					count.set(0);
				}
			}
			
			int currentCount = count.incrementAndGet();
			return currentCount <= MAX_ERRORS_PER_MINUTE;
		}
		
		public int getCount()
		{
			return count.get();
		}
	}
	
	/**
	 * Verifica se um erro deve ser logado (com throttling)
	 * @param errorKey Chave única para o tipo de erro
	 * @return true se deve logar, false se deve ser throttled
	 */
	public static boolean shouldLogError(String errorKey)
	{
		ErrorInfo info = ERROR_COUNTS.computeIfAbsent(errorKey, k -> new ErrorInfo());
		return info.shouldLog();
	}
	
	/**
	 * Obtém o número de vezes que um erro foi throttled
	 * @param errorKey Chave única para o tipo de erro
	 * @return Número de ocorrências throttled
	 */
	public static int getThrottledCount(String errorKey)
	{
		ErrorInfo info = ERROR_COUNTS.get(errorKey);
		return info != null ? info.getCount() : 0;
	}
	
	/**
	 * Loga um erro com throttling
	 * @param errorKey Chave única para o tipo de erro
	 * @param message Mensagem de erro
	 * @param args Argumentos para a mensagem
	 */
	public static void logThrottledError(String errorKey, String message, Object... args)
	{
		if (shouldLogError(errorKey))
		{
			LOGGER.warn(message, args);
		}
		else
		{
			int throttledCount = getThrottledCount(errorKey);
			if (throttledCount == MAX_ERRORS_PER_MINUTE + 1)
			{
				LOGGER.warn("Error '{}' is being throttled - too many occurrences", errorKey);
			}
		}
	}
	
	/**
	 * Limpa o cache de throttling
	 */
	public static void clearThrottling()
	{
		ERROR_COUNTS.clear();
		LOGGER.info("Error throttling cache cleared");
	}
	
	/**
	 * Obtém estatísticas de throttling
	 * @return String com estatísticas
	 */
	public static String getStats()
	{
		int totalErrors = ERROR_COUNTS.values().stream()
			.mapToInt(ErrorInfo::getCount)
			.sum();
		
		return String.format("Throttled errors: %d types, %d total occurrences", 
			ERROR_COUNTS.size(), totalErrors);
	}
}
