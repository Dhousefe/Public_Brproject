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
package ext.mods.gameserver.config;

/**
 * Configurações para o sistema de movimento e anti-colisão
 */
public class MovementConfig
{
	public static final long PEACE_ZONE_CACHE_DURATION = 1000;
	public static final long MOVE_SPEED_CACHE_DURATION = 100;
	public static final long DISTANCE_CACHE_DURATION = 50;
	
	public static final boolean ENABLE_PEACE_ZONE_COLLISION_BYPASS = true;
	public static final boolean ENABLE_MOVEMENT_CACHE = true;
	public static final boolean ENABLE_DEBUG_LOGGING = false;
	
	public static final int MAX_CACHE_ENTRIES = 1000;
	public static final long CACHE_CLEANUP_INTERVAL = 300000;
	
	/**
	 * Verifica se o bypass de colisão está habilitado
	 * @return true se habilitado
	 */
	public static boolean isPeaceZoneCollisionBypassEnabled()
	{
		return ENABLE_PEACE_ZONE_COLLISION_BYPASS;
	}
	
	/**
	 * Verifica se o cache de movimento está habilitado
	 * @return true se habilitado
	 */
	public static boolean isMovementCacheEnabled()
	{
		return ENABLE_MOVEMENT_CACHE;
	}
	
	/**
	 * Verifica se o debug está habilitado
	 * @return true se habilitado
	 */
	public static boolean isDebugLoggingEnabled()
	{
		return ENABLE_DEBUG_LOGGING;
	}
}

