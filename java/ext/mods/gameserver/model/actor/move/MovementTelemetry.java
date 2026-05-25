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

import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

/**
 * TELEMETRIA DE MOVIMENTO & COMBATE - V3.1 (FILTERED)
 * Filtra ruído: Só mostra dados se o alvo for um Player.
 */
public class MovementTelemetry {
    
    public static volatile boolean ENABLED = true; 
    
    private static final CLogger LOGGER = new CLogger("PhysicsDebug");

    public static void logAttackDecision(Creature actor, Creature target, double dist, int weaponRange, int collision, boolean willMove) {
        if (!ENABLED || !MovementConfig.DEBUG_ENABLED) return;
        
        if (!(actor instanceof Npc)) return;

        if (target == null || !(target instanceof Player)) return;

        double attackBorder = weaponRange + collision;
        String decision = willMove ? "MOVING" : "ATTACKING";
        
        String msg = String.format(
            "[ATK] %s -> %s | Dist: %.0f | Range: %.0f | Act: %s",
            actor.getName(), target.getName(), dist, attackBorder, decision
        );
        
        sendToInterface(msg);
    }

    public static void logPhysics(Creature actor, String type, double moveX, double moveY, double repulseX, double repulseY, boolean moved) {
        if (!ENABLED || !MovementConfig.DEBUG_ENABLED) return;
        if (!(actor instanceof Npc)) return;
        
        if (!isTargetingPlayer(actor)) return;

        if (moved && repulseX == 0 && repulseY == 0 && moveX == 0 && moveY == 0) return;
        
        String msg = String.format(
            "[PHY] %s | %s | Move: %.1f,%.1f | Repul: %.1f,%.1f | %s",
            actor.getName(), type, moveX, moveY, repulseX, repulseY, moved ? "OK" : "BLOCKED"
        );
        
        sendToInterface(msg);
    }

    /**
     * Verifica se o monstro está "interessado" em um jogador.
     * Checa o Target atual e a Intenção da AI.
     */
    private static boolean isTargetingPlayer(Creature actor) {
        WorldObject target = actor.getTarget();
        if (target instanceof Player) return true;

        if (actor.getAI() != null && actor.getAI().getCurrentIntention() != null) {
            Creature aiTarget = actor.getAI().getCurrentIntention().getFinalTarget();
            if (aiTarget instanceof Player) return true;
        }

        return false;
    }

    private static void sendToInterface(String msg) {
        MovementConfig.log(msg); 
    }
}