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
package ext.mods.battlerboss.tasks;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import ext.mods.Crypta.BattleBossData;
import ext.mods.battlerboss.holder.EventHolder;
import ext.mods.battlerboss.register.BattleBossOpenRegister;
import ext.mods.commons.pool.ThreadPool;

public class BattleBossCountDownTask
{
	public static Logger LOGGER = Logger.getLogger(BattleBossCountDownTask.class.getName());
	
	private final Map<EventHolder, ScheduledFuture<?>> scheduledEvents = new HashMap<>();
	
	public void start()
	{
		Object battleBossDataInstance = BattleBossData.getInstance();
		if (battleBossDataInstance == null)
		{
			LOGGER.severe("BattleBossData não está disponível.");
			return;
		}
		
		List<EventHolder> events = null;
		try
		{
			Object result = BattleBossData.getInstance().getEvents();
			if (result instanceof List)
			{
				@SuppressWarnings("unchecked")
				List<EventHolder> tempList = (List<EventHolder>) result;
				events = tempList;
			}
		}
		catch (Exception e)
		{
			LOGGER.severe("Erro ao acessar dados do BattleBoss: " + e.getMessage());
			e.printStackTrace();
			return;
		}
		
		if (events == null)
		{
			LOGGER.warning("Nenhum evento encontrado.");
			return;
		}
		
		for (EventHolder event : events)
		{
			scheduleNextEvent(event);
		}
	}
	
	private void scheduleNextEvent(EventHolder event)
	{
		try
		{
			if (!event.getConfig().isEnabled())
				return;
			
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime nextTime = getNextEventTime(event, now);
			if (nextTime == null)
				return;
			
			long delay = ChronoUnit.MILLIS.between(now, nextTime);
			
			ScheduledFuture<?> future = ThreadPool.schedule(() ->
			{
				try
				{
					startEvent(event);
				}
				catch (Exception e)
				{
					LOGGER.warning("Erro ao iniciar evento " + event.getId() + ": " + e.getMessage());
				}
				finally
				{
					scheduleNextEvent(event);
				}
			}, delay);
			
			scheduledEvents.put(event, future);
		}
		catch (Exception e)
		{
			LOGGER.severe("Erro ao agendar evento " + event.getId() + ": " + e.getMessage());
		}
	}
	
	private LocalDateTime getNextEventTime(EventHolder event, LocalDateTime from)
	{
		List<Integer> days = event.getConfig().getDays();
		List<String> times = event.getConfig().getTimes();
		
		LocalDateTime nextEvent = null;
		
		for (int i = 0; i <= 7; i++)
		{
			LocalDateTime day = from.plusDays(i);
			int dayOfWeek = day.getDayOfWeek().getValue();
			if (!days.contains(dayOfWeek))
				continue;
			
			for (String timeStr : times)
			{
				LocalTime eventTime = LocalTime.parse(timeStr);
				LocalDateTime candidate = day.withHour(eventTime.getHour()).withMinute(eventTime.getMinute()).withSecond(0).withNano(0);
				
				if (candidate.isAfter(from))
				{
					if (nextEvent == null || candidate.isBefore(nextEvent))
						nextEvent = candidate;
				}
			}
		}
		return nextEvent;
	}
	
	private void startEvent(EventHolder event)
	{
		BattleBossOpenRegister.getInstance().open(event);
	}
	
	public void reset()
	{
		scheduledEvents.values().forEach(f -> f.cancel(false));
		scheduledEvents.clear();
		start();
	}
	
	public static BattleBossCountDownTask getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final BattleBossCountDownTask INSTANCE = new BattleBossCountDownTask();
	}
}
