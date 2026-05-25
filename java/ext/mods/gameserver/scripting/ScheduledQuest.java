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
package ext.mods.gameserver.scripting;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

import ext.mods.gameserver.enums.ScheduleType;

public abstract class ScheduledQuest extends Quest
{
	private ScheduleType _type;
	private Calendar _start;
	private Calendar _end;
	private boolean _started;
	
	private ScheduledFuture<?> _task;
	
	protected ScheduledQuest(int questId, String descr)
	{
		super(questId, descr);
	}
	
	/**
	 * Return true, when a {@link ScheduledQuest} is started.
	 * @return boolean : True, when started.
	 */
	public final boolean isStarted()
	{
		return _started;
	}
	
	/**
	 * Set up schedule system for the script. Returns true, when successfully done.
	 * @param type : Type of the schedule.
	 * @param start : Start information.
	 * @param end : End information.
	 * @return boolean : True, when successfully loaded schedule system.
	 */
	public final boolean setSchedule(String type, String start, String end)
	{
		try
		{
			_type = Enum.valueOf(ScheduleType.class, type);
			_start = parseTimeStamp(start);
			_end = parseTimeStamp(end);
			_started = false;
			
			final long st = _start.getTimeInMillis();
			final long now = System.currentTimeMillis();
			if (_end == null || _end.getTimeInMillis() == st)
			{
				_end = null;
				
				if (st < now)
					_start.add(_type.getPeriod(), 1);
			}
			else
			{
				final long en = _end.getTimeInMillis();
				if (st < en)
				{
					if (en < now)
						_start.add(_type.getPeriod(), 1);
					else if (st < now)
						_started = true;
					else
						_end.add(_type.getPeriod(), -1);
				}
				else
				{
					if (st < now)
					{
						_end.add(_type.getPeriod(), 1);
						_started = true;
					}
					else if (now < en)
					{
						_start.add(_type.getPeriod(), -1);
						_started = true;
					}
				}
			}
			
			return init();
		}
		catch (Exception e)
		{
			LOGGER.error("Error loading schedule data for {}.", e, toString());
			
			_type = null;
			_start = null;
			_end = null;
			_started = false;
			return false;
		}
	}
	
	private final Calendar parseTimeStamp(String value)
	{
		if (value == null)
			return null;
		
		final Calendar calendar = Calendar.getInstance();
		String[] timeStamp;
		
		switch (_type)
		{
			case HOURLY:
				timeStamp = value.split(":");
				calendar.set(Calendar.MINUTE, Integer.valueOf(timeStamp[0]));
				calendar.set(Calendar.SECOND, Integer.valueOf(timeStamp[1]));
				calendar.set(Calendar.MILLISECOND, 0);
				return calendar;
			
			case DAILY:
				timeStamp = value.split(":");
				break;
			
			case WEEKLY:
				String[] params = value.split(" ");
				timeStamp = params[1].split(":");
				calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(params[0]));
				break;
			
			case MONTHLY_DAY:
				params = value.split(" ");
				timeStamp = params[1].split(":");
				calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(params[0]));
				break;
			
			case MONTHLY_WEEK:
				params = value.split(" ");
				String[] date = params[0].split("-");
				timeStamp = params[1].split(":");
				calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(date[0]));
				calendar.set(Calendar.WEEK_OF_MONTH, Integer.valueOf(date[1]));
				break;
			
			case YEARLY_DAY:
				params = value.split(" ");
				date = params[0].split("-");
				timeStamp = params[1].split(":");
				calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(date[0]));
				calendar.set(Calendar.MONTH, Integer.valueOf(date[1]) - 1);
				break;
			
			case YEARLY_WEEK:
				params = value.split(" ");
				date = params[0].split("-");
				timeStamp = params[1].split(":");
				calendar.set(Calendar.DAY_OF_WEEK, getDayOfWeek(date[0]));
				calendar.set(Calendar.WEEK_OF_YEAR, Integer.valueOf(date[1]));
				break;
			
			default:
				return null;
		}
		
		calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timeStamp[0]));
		calendar.set(Calendar.MINUTE, Integer.valueOf(timeStamp[1]));
		calendar.set(Calendar.SECOND, Integer.valueOf(timeStamp[2]));
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar;
	}
	
	/**
	 * Returns time of next action of the script.
	 * @return long : Time in milliseconds.
	 */
	public final long getTimeNext()
	{
		if (_type == null)
			return 0;
		
		return _started ? _end.getTimeInMillis() : _start.getTimeInMillis();
	}
	
	/**
	 * Returns the last/next start time, regardless the state of the script.
	 * @return long : Time in milliseconds.
	 */
	public final long getStartTime()
	{
		return _start.getTimeInMillis();
	}
	
	/**
	 * Returns the last/next end time, regardless the state of the script.
	 * @return long : Time in milliseconds.
	 */
	public final long getEndTime()
	{
		return _end.getTimeInMillis();
	}
	
	/**
	 * Notify and schedule next action of the script.
	 */
	public final void notifyAndSchedule()
	{
		if (_type == null)
			return;
		
		if (_end == null)
		{
			_start.add(_type.getPeriod(), 1);
			print(_start);
			
			try
			{
				onStart();
			}
			catch (Exception e)
			{
				LOGGER.error("Error starting {}.", e, toString());
			}
			
			return;
		}
		
		if (_started)
		{
			_start.add(_type.getPeriod(), 1);
			print(_start);
			
			try
			{
				_started = false;
				onEnd();
			}
			catch (Exception e)
			{
				LOGGER.error("Error ending {}.", e, toString());
			}
		}
		else
		{
			_end.add(_type.getPeriod(), 1);
			print(_end);
			
			try
			{
				_started = true;
				onStart();
			}
			catch (Exception e)
			{
				LOGGER.error("Error starting {}.", e, toString());
			}
		}
	}
	
	/**
	 * Initializes a script and returns information about script to be scheduled or not. Set internal values, parameters, etc...<br>
	 * <br>
	 * Note: Default behavior is to call onStart(), when the script is supposed to be started.
	 * @return boolean : True, when script was initialized and can be scheduled.
	 */
	protected boolean init()
	{
		if (_started)
			onStart();
		
		return true;
	}
	
	/**
	 * Starts a script. Handles spawns, announcements, loads variables, etc...
	 */
	protected abstract void onStart();
	
	/**
	 * Ends a script. Handles spawns, announcements, saves variables, etc...
	 */
	protected abstract void onEnd();
	
	/**
	 * Stops a script. Clears internal values, parameters, etc...<br>
	 * <br>
	 * Note: Default behavior is to call onEnd(), when the script is started.
	 */
	public void stop()
	{
		if (_started)
			onEnd();
	}
	
	/**
	 * Convert a {@link String} representation of a day into a {@link Calendar} day.
	 * @param day : The {@link String} representation of a day.
	 * @return The {@link Calendar} representation of a day.
	 */
	private final int getDayOfWeek(String day)
	{
		if (day.equals("MON"))
			return Calendar.MONDAY;
		else if (day.equals("TUE"))
			return Calendar.TUESDAY;
		else if (day.equals("WED"))
			return Calendar.WEDNESDAY;
		else if (day.equals("THU"))
			return Calendar.THURSDAY;
		else if (day.equals("FRI"))
			return Calendar.FRIDAY;
		else if (day.equals("SAT"))
			return Calendar.SATURDAY;
		else if (day.equals("SUN"))
			return Calendar.SUNDAY;
		
		LOGGER.error("Error parsing day of week {}, MONDAY will be used for {}.", day, toString());
		return Calendar.MONDAY;
	}
	
	private final void print(Calendar c)
	{
		LOGGER.debug("{}: {} = {}.", toString(), ((c == _start) ? "Next start" : "Next end"), String.format("%d.%d.%d %d:%02d:%02d", c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND)));
	}
	
	public final void setTask(ScheduledFuture<?> task)
	{
		cleanTask();
		
		_task = task;
	}
	
	public final void cleanTask()
	{
		if (_task != null)
		{
			_task.cancel(false);
			_task = null;
		}
	}
}