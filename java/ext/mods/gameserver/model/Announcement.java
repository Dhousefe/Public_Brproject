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
package ext.mods.gameserver.model;

import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;

/**
 * A datatype used to retain informations for announcements. It notably holds a {@link ScheduledFuture}.
 */
public class Announcement implements Runnable
{
	protected final String _message;
	
	protected boolean _critical;
	protected boolean _auto;
	protected boolean _unlimited;
	
	protected int _initialDelay;
	protected int _delay;
	protected int _limit;
	protected int _tempLimit;
	
	protected ScheduledFuture<?> _task;
	
	public Announcement(String message, boolean critical)
	{
		_message = message;
		_critical = critical;
	}
	
	public Announcement(String message, boolean critical, boolean auto, int initialDelay, int delay, int limit)
	{
		_message = message;
		_critical = critical;
		_auto = auto;
		_initialDelay = initialDelay;
		_delay = delay;
		_limit = limit;
		
		if (_auto)
		{
			switch (_limit)
			{
				case 0:
					_task = ThreadPool.scheduleAtFixedRate(this, _initialDelay * 1000L, _delay * 1000L);
					_unlimited = true;
					break;
				
				default:
					_task = ThreadPool.schedule(this, _initialDelay * 1000L);
					_tempLimit = _limit;
					break;
			}
		}
	}
	
	@Override
	public void run()
	{
		if (!_unlimited)
		{
			if (_tempLimit == 0)
				return;
			
			_task = ThreadPool.schedule(this, _delay * 1000L);
			_tempLimit--;
		}
		World.announceToOnlinePlayers(_message, _critical);
	}
	
	public String getMessage()
	{
		return _message;
	}
	
	public boolean isCritical()
	{
		return _critical;
	}
	
	public boolean isAuto()
	{
		return _auto;
	}
	
	public int getInitialDelay()
	{
		return _initialDelay;
	}
	
	public int getDelay()
	{
		return _delay;
	}
	
	public int getLimit()
	{
		return _limit;
	}
	
	public void stopTask()
	{
		if (_task != null)
		{
			_task.cancel(true);
			_task = null;
		}
	}
	
	public void reloadTask()
	{
		stopTask();
		
		if (_auto)
		{
			switch (_limit)
			{
				case 0:
					_task = ThreadPool.scheduleAtFixedRate(this, _initialDelay * 1000L, _delay * 1000L);
					_unlimited = true;
					break;
				
				default:
					_task = ThreadPool.schedule(this, _initialDelay * 1000L);
					_tempLimit = _limit;
					break;
			}
		}
	}
}