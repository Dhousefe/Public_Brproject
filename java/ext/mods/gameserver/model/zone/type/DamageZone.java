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
package ext.mods.gameserver.model.zone.type;

import java.util.concurrent.Future;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.subtype.CastleZoneType;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.EtcStatusUpdate;

/**
 * A zone extending {@link CastleZoneType}, which fires a task on the first {@link Creature} entrance, notably used by castle damage traps.<br>
 * <br>
 * This task decreases HPs using a reuse delay and can affect specific class types. The zone is considered a danger zone.
 */
public class DamageZone extends CastleZoneType
{
	private volatile Future<?> _task;
	
	private int _hpDamage = 200;
	private int _initialDelay = 1000;
	private int _reuseDelay = 5000;
	
	public DamageZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("hpDamage"))
			_hpDamage = Integer.parseInt(value);
		else if (name.equalsIgnoreCase("initialDelay"))
			_initialDelay = Integer.parseInt(value);
		else if (name.equalsIgnoreCase("reuseDelay"))
			_reuseDelay = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected boolean isAffected(Creature creature)
	{
		return creature instanceof Playable;
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (_hpDamage > 0)
		{
			if (getCastle() != null && (!isEnabled() || !getCastle().getSiege().isInProgress()))
				return;
			
			Future<?> task = _task;
			if (task == null)
			{
				synchronized (this)
				{
					task = _task;
					if (task == null)
						_task = task = ThreadPool.scheduleAtFixedRate(() ->
						{
							if (_creatures.isEmpty() || _hpDamage <= 0 || (getCastle() != null && (!isEnabled() || !getCastle().getSiege().isInProgress())))
							{
								stopTask();
								return;
							}
							
							for (Creature temp : _creatures)
							{
								if (!temp.isDead())
									temp.reduceCurrentHp(_hpDamage * (1 + (temp.getStatus().calcStat(Stats.DAMAGE_ZONE_VULN, 0, null, null) / 100)), null, null);
							}
						}, _initialDelay, _reuseDelay);
					
					if (getCastle() != null)
						getCastle().getSiege().announce(SystemMessageId.A_TRAP_DEVICE_HAS_BEEN_TRIPPED, SiegeSide.DEFENDER);
				}
			}
		}
		
		if (creature instanceof Player player)
		{
			player.setInsideZone(ZoneId.DANGER_AREA, true);
			player.sendPacket(new EtcStatusUpdate(player));
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (creature instanceof Player player)
		{
			player.setInsideZone(ZoneId.DANGER_AREA, false);
			
			if (!player.isInsideZone(ZoneId.DANGER_AREA))
				player.sendPacket(new EtcStatusUpdate(player));
		}
	}
	
	private void stopTask()
	{
		if (_task != null)
		{
			_task.cancel(false);
			_task = null;
		}
	}
}