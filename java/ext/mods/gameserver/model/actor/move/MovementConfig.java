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
package ext.mods.gameserver.model.actor.move;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Global Movement Configuration (Tuner)
 * Version: 4.0 (Russian Patch Applied - Strong Physics)
 */
public class MovementConfig {

    public static volatile boolean DEBUG_ENABLED = false;

    public static volatile long ATTACK_FOLLOW_INTERVAL = 250;
    public static final long DEFAULT_ATTACK_FOLLOW_INTERVAL = 250;

    public static volatile int ZIGZAG_LAYER_OFFSET = 50;
    public static final int DEFAULT_ZIGZAG_LAYER_OFFSET = 50;

    public static volatile double RANDOM_SPEED_VARIATION = 0.10;
    public static final double DEFAULT_RANDOM_SPEED_VARIATION = 0.10;

    
    public static volatile double SEPARATION_WEIGHT = 8.0; 
    public static final double DEFAULT_SEPARATION_WEIGHT = 8.0;
    
    public static volatile double SEPARATION_RADIUS_MULTIPLIER = 2.0;
    public static final double DEFAULT_SEPARATION_RADIUS_MULTIPLIER = 2.0;
    
    public static volatile double COLLISION_PUSH_FACTOR = 0.7;
    public static final double DEFAULT_COLLISION_PUSH_FACTOR = 0.7;
    
    public static volatile double MIN_DISTANCE_BETWEEN_MOBS = 30.0;
    public static final double DEFAULT_MIN_DISTANCE_BETWEEN_MOBS = 30.0;
    
    public static volatile double HYSTERESIS_BUFFER = 50.0;
    public static final double DEFAULT_HYSTERESIS_BUFFER = 50.0;

    private static final ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<>();
    private static final int MAX_LOG_SIZE = 100;
    private static final AtomicLong lastPollTime = new AtomicLong(System.currentTimeMillis());

    public static void log(String msg) {
        if (!DEBUG_ENABLED) return;

        long now = System.currentTimeMillis();
        if (logQueue.size() >= MAX_LOG_SIZE || (now - lastPollTime.get() > 10000)) {
            DEBUG_ENABLED = false;
            logQueue.clear();
            System.out.println("[MovementConfig] Telemetry auto-disabled.");
            return;
        }
        logQueue.offer("[" + (now % 100000) + "] " + msg);
    }

    public static String pollLog() {
        lastPollTime.set(System.currentTimeMillis());
        return logQueue.poll();
    }
}