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
package ext.mods.gameserver.model.entity.autofarm.zone;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import ext.mods.Config;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.WorldRegion;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class AutoFarmRoute extends AutoFarmArea
{
	private int _index;
	private int _radius;
	private boolean _reversePath;
	private boolean _isOnARoute;
	private boolean _reachedFirstNode;
	
	public AutoFarmRoute(int id, String name, int ownerId)
	{
		super(id, name, ownerId, AutoFarmType.ROTA);
	}
	
	public AutoFarmRoute(String name, int ownerId)
	{
		super(name, ownerId, AutoFarmType.ROTA);
	}
	
	@Override
	public void visualizeZone(ExServerPrimitive debug)
	{
		getZone().visualizeZone("ROTA " + getName(), debug);
	}
	
	@Override
	public AutoFarmRoute getRouteZone()
	{
		return this;
	}
	
	@Override
	public List<Location> getNodes()
	{
		return super.getNodes();
	}
	
	@Override
	public List<Monster> getMonsters()
	{
		if (getProfile().getFinalRadius() < 100)
		{
			final List<Monster> monsters = new ArrayList<>();
			for (Monster m : getOwner().getKnownTypeInRadius(Monster.class, getProfile().getFinalRadius() * 2))
			{
				if (m.isInStrictRadius(m, getProfile().getFinalRadius()))
				{
					monsters.add(m);
					continue;
				}
				
				if (m.getAI().getAggroList().getHate(getOwner()) > 0)
				{
					monsters.add(m);
					continue;
				}
			}
			
			return monsters;
		}
		
		return getOwner().getKnownTypeInRadius(Monster.class, getProfile().getFinalRadius());
	}
	
	@Override
	public Set<String> getMonsterHistory()
	{
		_monsterHistory.addAll(getKnownTypeInRadius(Monster.class, AutoFarmManager.MAX_ROUTE_LINE_LENGTH).stream().map(Monster::getName).toList());
		return _monsterHistory;
	}
	
	public void reset()
	{
		_isOnARoute = false;
		_reachedFirstNode = false;
	}

	
	/**
	 * @return The route has not been started yet.
	 */
	public boolean isOwnerOnARoute()
	{
		return _isOnARoute;
	}
	
	/**
	 * @return The player is just starting the route (has not yet reached the closest index).
	 */
	public boolean reachedFirstNode()
	{
		return _reachedFirstNode;
	}
	
	public int getRadius()
	{
		return _radius;
	}
	
	public void setRadius(int value)
	{
		_radius = value;
	}
	
	/**
	 * Tenta mover para a próxima node da rota.
	 * Similar ao tryGoBackInside do AutoFarmZone.
	 * @return true se conseguiu iniciar o movimento
	 */
	public boolean tryMoveToNextNode()
	{
		final Player player = getOwner();
		final List<Location> nodes = getNodes();
		
		if (nodes.isEmpty())
			return false;
		
		if (!_isOnARoute)
		{
			final Location nearestNode = nodes.stream().min(Comparator.comparingDouble(wl -> player.distance3D(wl))).get();
			_index = nodes.indexOf(nearestNode);
			
			if (player.isIn3DRadius(nearestNode, 50))
			{
				if (_index < nodes.size() - 1)
					_index++;
				else if (nodes.size() > 1)
					_index = 0;
			}
		}
		else if (player.isIn3DRadius(nodes.get(_index), 50))
		{
			if (player.getMove().getGeoPathFailCount() >= 10)
			{
				reset();
				AutoFarmManager.getInstance().stopPlayer(player, "Character fora da rota");
				return false;
			}
			
			if (_isOnARoute && !_reachedFirstNode)
				_reachedFirstNode = true;
			
			if (_reversePath && _index > 0)
			{
				_index--;
				
				if (_index == 0)
					_reversePath = false;
			}
			else if (_index < nodes.size() - 1)
				_index++;
			else
			{
				_index = nodes.size() - 2;
				_reversePath = true;
			}
		}
		
		Location node = nodes.get(_index);
		
		if (!GeoEngine.getInstance().canMoveToTarget(player.getPosition(), node))
		{
			final List<Location> path = GeoEngine.getInstance().findPath(player.getX(), player.getY(), player.getZ(), node.getX(), node.getY(), node.getZ(), true, null);
			if (path.isEmpty())
			{
				player.getMove().addGeoPathFailCount();
				
				if (_index == 0)
				{
					_index = nodes.size() - 2;
					_reversePath = true;
				}
				else
					_index--;
				
				node = nodes.get(_index);
			}
		}
		
		player.getMove().maybePlayerMoveToLocation(node, 0, Config.SISTEMA_PATHFINDING, false);
		_isOnARoute = true;
		return true;
	}
	
	/*
	 * from NpcAI
	 * @deprecated Use tryMoveToNextNode() instead
	 */
	@Deprecated
	public void moveToNextPoint()
	{
		final Player player = getOwner();
		final List<Location> nodes = getNodes();
		
		if (nodes.isEmpty())
			return;
		
		if (!_isOnARoute)
		{
			final Location nearestNode = nodes.stream().min(Comparator.comparingDouble(wl -> player.distance3D(wl))).get();
			_index = nodes.indexOf(nearestNode);
			
			if (player.isIn3DRadius(nearestNode, 50))
			{
				if (_index < nodes.size() - 1)
					_index++;
				else if (nodes.size() > 1)
					_index = 0;
			}
		}
		else if (player.isIn3DRadius(nodes.get(_index), 50))
		{
			if (player.getMove().getGeoPathFailCount() >= 10)
			{
				reset();
				AutoFarmManager.getInstance().stopPlayer(player, "Character fora da rota");
				return;
			}
			
			if (_isOnARoute && !_reachedFirstNode)
				_reachedFirstNode = true;
			
			if (_reversePath && _index > 0)
			{
				_index--;
				
				if (_index == 0)
					_reversePath = false;
			}
			else if (_index < nodes.size() - 1)
				_index++;
			else
			{
				_index = nodes.size() - 2;
				_reversePath = true;
			}
		}
		
		Location node = nodes.get(_index);
		
		if (!GeoEngine.getInstance().canMoveToTarget(player.getPosition(), node))
		{
			final List<Location> path = GeoEngine.getInstance().findPath(player.getX(), player.getY(), player.getZ(), node.getX(), node.getY(), node.getZ(), true, null);
			if (path.isEmpty())
			{
				player.getMove().addGeoPathFailCount();
				
				if (_index == 0)
				{
					_index = nodes.size() - 2;
					_reversePath = true;
				}
				else
					_index--;
				
				node = nodes.get(_index);
			}
		}
		
		player.getMove().maybePlayerMoveToLocation(node, 0, Config.SISTEMA_PATHFINDING, false);
		_isOnARoute = true;
	}
	
	/*
	 * Adapted from WorldObject.
	 */
	private final <A extends WorldObject> List<A> getKnownTypeInRadius(Class<A> type, int radius)
	{
		final List<A> result = new ArrayList<>();
		final int depth = (radius <= 2048) ? 1 : (int) ((radius / 2048) + 1);
		
		for (Location loc : getNodes())
		{
			final WorldRegion wr = World.getInstance().getRegion(loc);
			wr.forEachRegion(depth, r -> r.forEachType(type, getOwner(), o -> o.isInStrictRadius(o, radius), result::add));
			
		}
		return result;
	}
}