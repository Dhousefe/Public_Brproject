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

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.geoengine.geodata.IGeoObject;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmProfile;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;
import ext.mods.gameserver.network.serverpackets.ValidateLocation;

/**
 * Sistema Integrado de Movimento (MovementIntegration)
 * Versão: 4.0 (Russian Patch Logic Applied - Strict Geo)
 */
public class MovementIntegration
{
    private static final CLogger LOGGER = new CLogger(MovementIntegration.class.getName());
    
    private static final int VALIDATION_INTERVAL = 500;
    private static final double RECONCILIATION_THRESHOLD = 64.0;
    
    private static final ConcurrentMap<Integer, MovementPrediction> _predictions = new ConcurrentHashMap<>();
    private ScheduledFuture<?> _globalValidationTask;
    
    private static class SingletonHolder
    {
        protected static final MovementIntegration INSTANCE = new MovementIntegration();
    }
    
    private MovementIntegration() 
    {
        startGlobalValidationTask();
    }
    
    public static MovementIntegration getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private void startGlobalValidationTask()
    {
        if (_globalValidationTask != null) return;
        _globalValidationTask = ThreadPool.scheduleAtFixedRate(this::validateAllMovements, VALIDATION_INTERVAL, VALIDATION_INTERVAL);
    }

    private void validateAllMovements()
    {
        if (_predictions.isEmpty()) return;

        for (Map.Entry<Integer, MovementPrediction> entry : _predictions.entrySet())
        {
            int objectId = entry.getKey();
            MovementPrediction prediction = entry.getValue();
            
            WorldObject obj = ext.mods.gameserver.model.World.getInstance().getObject(objectId);
            
            if (obj == null || !(obj instanceof Player))
            {
                _predictions.remove(objectId);
                continue;
            }

            Player player = (Player) obj;
            
            if (!player.isMoving() || player.isDead()) {
                _predictions.remove(objectId);
                continue;
            }

            validateSinglePlayer(player, prediction);
        }
    }

    private void validateSinglePlayer(Player player, MovementPrediction prediction)
    {
        final Location currentPos = player.getPosition().clone();
        final Location predictedPos = calculatePredictedPosition(prediction);
        
        double distance = currentPos.distance3D(predictedPos);
        
        if (distance > RECONCILIATION_THRESHOLD)
        {
            reconcilePosition(player, predictedPos);
        }
        
        if (hasReachedDestination(prediction, currentPos))
        {
            completeMovement(player);
        }
    }

    public static boolean canMoveInAutoFarm(Player player, Location dest)
    {
        AutoFarmProfile profile = AutoFarmManager.getInstance().getProfile(player);
        
        if (!profile.isEnabled()) return true;

        if (profile.getSelectedArea() != null && profile.getSelectedArea().getType() == AutoFarmManager.AutoFarmType.ZONA)
        {
            Location anchor = profile.getAnchorLocation();
            if (anchor == null) return true;

            int radius = profile.getFinalRadius();
            if (radius <= 0) radius = 1000;

            double dist = anchor.distance2D(dest);

            if (dist > radius)
            {
                player.sendMessage("Modo Fechado: Você não pode sair da área delimitada.");
                visualizeFarmLimit(player, anchor, radius);
                return false;
            }
        }
        return true;
    }

    private static void visualizeFarmLimit(Player player, Location center, int radius)
    {
        if (!MovementConfig.DEBUG_ENABLED) return;

        ExServerPrimitive packet = new ExServerPrimitive("FarmLimit", center);
        int pointCount = 40;
        double angleSlice = 2 * Math.PI / pointCount;
        
        int prevX = (int) (center.getX() + radius * Math.cos(0));
        int prevY = (int) (center.getY() + radius * Math.sin(0));
        int z = center.getZ() + 10;

        for (int i = 1; i <= pointCount; i++)
        {
            double angle = i * angleSlice;
            int curX = (int) (center.getX() + radius * Math.cos(angle));
            int curY = (int) (center.getY() + radius * Math.sin(angle));
            packet.addLine(Color.RED, prevX, prevY, z, curX, curY, z);
            prevX = curX;
            prevY = curY;
        }
        player.sendPacket(packet);
    }

    public static void initializeMovementSystem(Creature creature) { 
        if (!Config.SISTEMA_PATHFINDING) return; 
        if (Config.USE_OPTIMIZED_MOVEMENT && creature instanceof Player) creature.setMove(); 
    }
    
    public static boolean canMoveToTarget(int ox, int oy, int oz, int tx, int ty, int tz) { 
        return GeoEngine.getInstance().canMoveToTarget(ox, oy, oz, tx, ty, tz); 
    }
    
    public static Location getValidLocation(int ox, int oy, int oz, int tx, int ty, int tz) { 
        return GeoEngine.getInstance().getValidLocation(ox, oy, oz, tx, ty, tz, null); 
    }
    
    public static boolean canSee(int ox, int oy, int oz, double oheight, int tx, int ty, int tz, double theight) {
        return GeoEngine.getInstance().canSee(ox, oy, oz, oheight, tx, ty, tz, theight, null, null); 
    }

    public static boolean canSeeTarget(WorldObject object, WorldObject target) { 
        if (!Config.SISTEMA_PATHFINDING) return true; 
        
        
        final int ox = object.getX(); final int oy = object.getY(); final int oz = object.getZ(); 
        final int tx = target.getX(); final int ty = target.getY(); final int tz = target.getZ(); 
        double oheight = (object instanceof Creature c) ? c.getCollisionHeight() * 2 * Config.PART_OF_CHARACTER_HEIGHT / 100 : 0; 
        double theight = (target instanceof Creature c) ? c.getCollisionHeight() * 2 * Config.PART_OF_CHARACTER_HEIGHT / 100 : 0; 
        final IGeoObject ignore = (target instanceof IGeoObject igo) ? igo : null; 
        return GeoEngine.getInstance().canSee(ox, oy, oz, oheight, tx, ty, tz, theight, ignore, null); 
    }

    public static boolean canMoveToTargetForAutoFarm(int ox, int oy, int oz, int tx, int ty, int tz) {
        return canMoveToTarget(ox, oy, oz, tx, ty, tz);
    }

    public static boolean canSeeTargetForAutoFarm(WorldObject object, WorldObject target) {
        return canSeeTarget(object, target); 
    }
    
    public static boolean canCastOnTarget(WorldObject caster, WorldObject target, int skillRange) { 
        if (!Config.SISTEMA_PATHFINDING) return true; 
        
        final int ox = caster.getX(); final int oy = caster.getY();
        final int tx = target.getX(); final int ty = target.getY();
        
        if (Math.abs(ox - tx) > skillRange + 200 || Math.abs(oy - ty) > skillRange + 200) return false;

        return canSeeTarget(caster, target);
    }

    public static void startMovement(Creature creature, Location destination)
    {
        if (creature instanceof Player player)
            startPlayerMovement(player, destination);
        else
            startCreatureMovement(creature, destination);
    }
    
    private static void startPlayerMovement(Player player, Location destination)
    {
        if (!canMoveInAutoFarm(player, destination))
        {
            player.sendPacket(new ValidateLocation(player));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return; 
        }

        final int objectId = player.getObjectId();
        final Location startPos = player.getPosition().clone();
        
        final MovementPrediction prediction = new MovementPrediction(
            startPos, 
            destination, 
            System.currentTimeMillis(), 
            calculateMovementSpeed(player)
        );
        
        _predictions.put(objectId, prediction);
    }
    
    private static void startCreatureMovement(Creature creature, Location destination) {}
    
    private static Location calculatePredictedPosition(MovementPrediction prediction) { 
        final long now = System.currentTimeMillis();
        final long timePassed = now - prediction.getStartTime();
        
        if (timePassed <= 0) return prediction.getStartPosition(); 
        
        final double totalDistance = prediction.getStartPosition().distance3D(prediction.getDestination()); 
        if (totalDistance <= 0.1) return prediction.getDestination();

        final double speed = prediction.getSpeed(); 
        final double distanceTraveled = (speed * timePassed) / 1000.0; 
        
        if (distanceTraveled >= totalDistance) return prediction.getDestination(); 
        
        final double ratio = distanceTraveled / totalDistance; 
        final double dirX = prediction.getDestination().getX() - prediction.getStartPosition().getX(); 
        final double dirY = prediction.getDestination().getY() - prediction.getStartPosition().getY(); 
        final double dirZ = prediction.getDestination().getZ() - prediction.getStartPosition().getZ(); 
        
        return new Location(
            prediction.getStartPosition().getX() + (int)(dirX * ratio), 
            prediction.getStartPosition().getY() + (int)(dirY * ratio), 
            prediction.getStartPosition().getZ() + (int)(dirZ * ratio)
        ); 
    }
    
    private static void reconcilePosition(Player player, Location correctPosition) { 
        player.sendPacket(new ValidateLocation(player)); 
    }
    
    private static boolean hasReachedDestination(MovementPrediction prediction, Location currentPos) { 
        return currentPos.distance3D(prediction.getDestination()) < 32.0; 
    }
    
    private static void completeMovement(Creature creature) { 
        _predictions.remove(creature.getObjectId()); 
    }
    
    private static double calculateMovementSpeed(Creature creature) { 
        return creature.getStatus().getMoveSpeed(); 
    }
    
    public static void stopMovement(Creature creature) { 
        _predictions.remove(creature.getObjectId()); 
    }
    
    public static boolean isMoving(Creature creature) { 
        return _predictions.containsKey(creature.getObjectId()); 
    }
    
    public static Location getPredictedPosition(Creature creature) { 
        final MovementPrediction prediction = _predictions.get(creature.getObjectId()); 
        if (prediction == null) return creature.getPosition(); 
        return calculatePredictedPosition(prediction); 
    }
    
    private static class MovementPrediction
    {
        private final Location _startPosition;
        private final Location _destination;
        private final long _startTime;
        private final double _speed;
        
        public MovementPrediction(Location startPosition, Location destination, long startTime, double speed) { 
            _startPosition = startPosition; 
            _destination = destination; 
            _startTime = startTime; 
            _speed = speed; 
        }
        
        public Location getStartPosition() { return _startPosition; }
        public Location getDestination() { return _destination; }
        public long getStartTime() { return _startTime; }
        public double getSpeed() { return _speed; }
    }
}