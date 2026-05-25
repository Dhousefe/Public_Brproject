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
package ext.mods.gameserver.model.actor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.OperateType;
import ext.mods.gameserver.enums.boats.BoatDock;
import ext.mods.gameserver.enums.boats.BoatState;
import ext.mods.gameserver.model.actor.ai.type.BoatAI;
import ext.mods.gameserver.model.actor.move.BoatMove;
import ext.mods.gameserver.model.actor.status.BoatStatus;
import ext.mods.gameserver.model.actor.template.CreatureTemplate;
import ext.mods.gameserver.model.boat.BoatEngine;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.VehicleInfo;

public class Boat extends Creature
{
	private final Set<Player> _passengers = ConcurrentHashMap.newKeySet();
	
	private BoatEngine _engine;
	private Future<?> _payTask;
	
	public Boat(int objectId, CreatureTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public BoatAI getAI()
	{
		return (BoatAI) _ai;
	}
	
	@Override
	public void setAI()
	{
		_ai = new BoatAI(this);
	}
	
	@Override
	public BoatStatus getStatus()
	{
		return (BoatStatus) _status;
	}
	
	@Override
	public void setStatus()
	{
		_status = new BoatStatus(this);
	}
	
	@Override
	public BoatMove getMove()
	{
		return (BoatMove) _move;
	}
	
	@Override
	public void setMove()
	{
		_move = new BoatMove(this);
	}
	
	@Override
	public boolean teleportTo(int x, int y, int z, int randomOffset)
	{
		if (!_isTeleporting.compareAndSet(false, true))
			return false;
		
		stopPayTask();
		
		getMove().stop();
		
		for (Player player : _passengers)
			player.teleportTo(x, y, z, randomOffset);
		
		decayMe();
		setXYZ(x, y, z);
		
		onTeleported();
		revalidateZone(true);
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		stopPayTask();
		
		_engine = null;
		
		getMove().stop();
		
		oustPlayers();
		
		decayMe();
		
		super.deleteMe();
	}
	
	@Override
	public void updateAbnormalEffect()
	{
	}
	
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getActiveWeaponItem()
	{
		return null;
	}
	
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	@Override
	public Weapon getSecondaryWeaponItem()
	{
		return null;
	}
	
	@Override
	public void sendInfo(Player player)
	{
		player.sendPacket(new VehicleInfo(this));
	}
	
	@Override
	public boolean isFlying()
	{
		return true;
	}
	
	@Override
	public void onInteract(Player actor)
	{
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
	}
	
	@Override
	public boolean isAttackableBy(Creature attacker)
	{
		return false;
	}
	
	@Override
	public String toString()
	{
		return "Boat " + _engine.toString() + " passengers=" + _passengers.size();
	}
	
	public Set<Player> getPassengers()
	{
		return _passengers;
	}
	
	/**
	 * Oust all {@Player}s set as passengers to TeleportType.TOWN.
	 */
	public void oustPlayers()
	{
		for (Player player : _passengers)
			oustPlayer(player, RestartPointData.getInstance().getLocationToTeleport(player, RestartType.TOWN));
	}
	
	/**
	 * Oust a {@Player} out of this {@link Boat}.
	 * <ul>
	 * <li>Remove him from peace zone.</li>
	 * <li>Teleport him back to a valid zone, or Location set as parameter. setBoat(null) is embedded into teleportTo, no need to care about it.</li>
	 * </ul>
	 * In case he is offline, his position is forced to be edited, and setBoat(null) applies.
	 * @param player : The Player to oust.
	 * @param loc : The Location used as oust.
	 */
	public void oustPlayer(Player player, Location loc)
	{
		if (player.isInStoreMode())
		{
			player.setOperateType(OperateType.NONE);
			player.broadcastUserInfo();
		}
		
		if (player.isOnline())
			player.teleportTo(loc.getX(), loc.getY(), loc.getZ(), 0);
		else
		{
			removePassenger(player);
			
			player.setXYZInvisible(loc);
		}
	}
	
	/**
	 * Test and add a {@link Player} passenger to this {@link Boat} if conditions matched.
	 * @param player : The {@link Player} to test.
	 */
	public void addPassenger(Player player)
	{
		final Boat boat = player.getBoatInfo().getBoat();
		
		if (boat != this)
			return;
		
		if (!_passengers.add(player))
			return;
		
		player.setInsideZone(ZoneId.PEACE, true);
		player.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		
		player.sendPacket(SystemMessageId.ENTER_PEACEFUL_ZONE);
	}
	
	/**
	 * Remove a {@link Player} passenger from this {@link Boat}.
	 * @param player : The {@link Player} to test.
	 */
	public void removePassenger(Player player)
	{
		stopPayTask();
		
		player.getBoatInfo().setBoat(null);
		
		player.setInsideZone(ZoneId.PEACE, false);
		player.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		
		player.sendPacket(SystemMessageId.EXIT_PEACEFUL_ZONE);
		
		_passengers.remove(player);
	}
	
	/**
	 * Consume passengers tickets from this {@link Boat} and teleport {@link Player}s if they don't own one.
	 * @param itemId : The itemId to check.
	 * @param loc : The {@link Location} used as oust in case a {@link Player} can't pay.
	 */
	public void payForRide(int itemId, Location loc)
	{
		stopPayTask();
		
		if (itemId <= 0)
			return;
		
		_payTask = ThreadPool.schedule(() ->
		{
			for (Player player : _passengers)
			{
				if (player.destroyItemByItemId(itemId, 1, false))
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(itemId));
				else
				{
					oustPlayer(player, loc);
					player.sendPacket(SystemMessageId.NOT_CORRECT_BOAT_TICKET);
				}
			}
		}, 5000);
	}
	
	/**
	 * Stop task if already running.
	 */
	private void stopPayTask()
	{
		if (_payTask != null)
		{
			_payTask.cancel(false);
			_payTask = null;
		}
	}
	
	public void setEngine(BoatEngine engine)
	{
		_engine = engine;
	}
	
	public BoatEngine getEngine()
	{
		return _engine;
	}
	
	public BoatDock getDock()
	{
		return (_engine == null) ? null : _engine.getDock();
	}
	
	public boolean isDocked()
	{
		return _engine != null && _engine.getState() == BoatState.DOCKED;
	}
}