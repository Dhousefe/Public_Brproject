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
package ext.mods.gameserver.model.actor.container.player;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.enums.PunishmentType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.network.serverpackets.EtcStatusUpdate;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.PlaySound;

public class Punishment
{
	private final Player _owner;
	
	private PunishmentType _type = PunishmentType.NONE;
	private long _timer;
	private ScheduledFuture<?> _task;
	
	public Punishment(Player owner)
	{
		_owner = owner;
	}
	
	public Player getOwner()
	{
		return _owner;
	}
	
	public PunishmentType getType()
	{
		return _type;
	}
	
	/**
	 * Set the {@link PunishmentType} for this {@link Player}. If state is out of range, do nothing.
	 * @param type : The PunishmentType to apply.
	 */
	public void setType(int type)
	{
		if (type < 0 || type > PunishmentType.VALUES.length)
			return;
		
		_type = PunishmentType.VALUES[type];
	}
	
	/**
	 * Sets the {@link PunishmentType} for this {@link Player}, based on a delay.
	 * @param state : The PunishmentType to apply.
	 * @param delayInMinutes : A time in minutes, 0 for infinite.
	 */
	public void setType(PunishmentType state, int delayInMinutes)
	{
		switch (state)
		{
			case NONE:
				switch (_type)
				{
					case CHAT:
						_type = state;
						
						stopTask(true);
						
						_owner.sendPacket(new EtcStatusUpdate(_owner));
						_owner.sendMessage(_owner.getSysString(10_108));
						_owner.sendPacket(new PlaySound("systemmsg_e.345"));
						break;
					
					case JAIL:
						_type = state;
						
						if (!CTFEvent.getInstance().isInactive() && CTFEvent.getInstance().isPlayerParticipant(_owner.getObjectId()))
							CTFEvent.getInstance().removeParticipant(_owner.getObjectId());
						
						if (!DMEvent.getInstance().isInactive() && DMEvent.getInstance().isPlayerParticipant(_owner.getObjectId()))
							DMEvent.getInstance().removeParticipant(_owner);
						
						if (!LMEvent.getInstance().isInactive() && LMEvent.getInstance().isPlayerParticipant(_owner.getObjectId()))
							LMEvent.getInstance().removeParticipant(_owner);
						
						if (!TvTEvent.getInstance().isInactive() && TvTEvent.getInstance().isPlayerParticipant(_owner.getObjectId()))
							TvTEvent.getInstance().removeParticipant(_owner.getObjectId());
						
						final NpcHtmlMessage html = new NpcHtmlMessage(0);
						html.setFile(_owner.getLocale(), "html/jail_out.htm");
						_owner.sendPacket(html);
						
						stopTask(true);
						_owner.teleportTo(17836, 170178, -3507, 20);
						break;
				}
				break;
			
			case CHAT:
				if (_type == PunishmentType.JAIL)
					break;
				
				_type = state;
				_timer = 0;
				_owner.sendPacket(new EtcStatusUpdate(_owner));
				
				stopTask(false);
				
				if (delayInMinutes > 0)
				{
					_timer = delayInMinutes * 60000L;
					
					_task = ThreadPool.schedule(() -> setType(PunishmentType.NONE, 0), _timer);
					_owner.sendMessage(_owner.getSysString(10_109, delayInMinutes));
				}
				else
					_owner.sendMessage(_owner.getSysString(10_101));
				
				_owner.sendPacket(new PlaySound("systemmsg_e.346"));
				break;
			
			case JAIL:
				_type = state;
				_timer = 0;
				
				stopTask(false);
				
				if (delayInMinutes > 0)
				{
					_timer = delayInMinutes * 60000L;
					
					_task = ThreadPool.schedule(() -> setType(PunishmentType.NONE, 0), _timer);
					
					_owner.sendMessage(_owner.getSysString(10_111, delayInMinutes));
				}
				
				if (OlympiadManager.getInstance().isRegisteredInComp(_owner))
					OlympiadManager.getInstance().removeDisconnectedCompetitor(_owner);
				
				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile(_owner.getLocale(), "html/jail_in.htm");
				_owner.sendPacket(html);
				
				_owner.setIsIn7sDungeon(false);
				_owner.teleportTo(-114356, -249645, -2984, 0);
				break;
			
			case CHAR:
				_owner.setAccessLevel(-1);
				_owner.logout(false);
				break;
			
			case ACC:
				_owner.setAccountAccesslevel(-100);
				_owner.logout(false);
				break;
			
			default:
				_type = state;
				break;
		}
		
		_owner.storeCharBase();
	}
	
	public long getTimer()
	{
		return _timer;
	}
	
	/**
	 * Set the {@link Punishment} data on {@link Player} load.
	 * @param type : The PunishmentType ordinal to set.
	 * @param timer : The time to set, under ms.
	 */
	public void load(int type, long timer)
	{
		setType(type);
		
		_timer = (_type == PunishmentType.NONE) ? 0 : timer;
	}
	
	/**
	 * Handle {@link Punishment} actions.
	 */
	public void handle()
	{
		if (_type != PunishmentType.NONE)
		{
			if (_timer > 0)
			{
				_task = ThreadPool.schedule(() -> setType(PunishmentType.NONE, 0), _timer);
				_owner.sendMessage(_owner.getSysString(10_112, _type.getDescription(), Math.round(_timer / 60000f)));
			}
			
			if (_type == PunishmentType.JAIL && !_owner.isInsideZone(ZoneId.JAIL))
				_owner.teleportTo(-114356, -249645, -2984, 20);
		}
	}
	
	/**
	 * Stop the {@link Punishment} task.
	 * @param save : If true, we save the task timer.
	 */
	public void stopTask(boolean save)
	{
		if (_task != null)
		{
			if (save)
			{
				long delay = _task.getDelay(TimeUnit.MILLISECONDS);
				if (delay < 0)
					delay = 0;
				
				_timer = delay;
			}
			
			_task.cancel(false);
			_task = null;
		}
	}
}