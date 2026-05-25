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
package ext.mods.FarmEventRandom.holder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ext.mods.commons.data.StatSet;

public class RamdomConfig
{
	private final boolean _enabled;
	private final String _eventName;
	private final int _preparation;
	private final int _interval;
	private final int _zoneValue;
	private final Set<Integer> _activeDays;
	private final List<LocalTime> _activeTimes;

	private final int _announceEndMinutes;
	
	public RamdomConfig(StatSet set)
	{
		_enabled = set.getBool("enable", false);
		_eventName = set.getString("name", "[Unnamed Event]");
		_preparation = set.getInteger("prepareMinutes", 5);
		_interval = set.getInteger("intervalHours", 6);
		_zoneValue = set.getInteger("select", 1);
		
		_announceEndMinutes = set.getInteger("announceEnd", 5);

		String days = set.getString("days", "0,1,2,3,4,5,6");
		_activeDays = Arrays.stream(days.split(",")).map(String::trim).map(Integer::parseInt).collect(Collectors.toSet());
		
		String times = set.getString("times", "");
		DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("H:mm");
		_activeTimes = Arrays.stream(times.split(";")).map(String::trim).filter(s -> !s.isEmpty()).map(time -> LocalTime.parse(time, timeFmt)).collect(Collectors.toList());
	}
	
	public boolean isEnabled() { return _enabled; }
	public String getName() { return _eventName; }
	public int getInterval() { return _interval; }
	public int getPrepareMinutes() { return _preparation; }
	public int getZoneValue() { return _zoneValue; }
	public Set<Integer> getActiveDays() { return _activeDays; }
	public List<LocalTime> getActiveTimes() { return _activeTimes; }

	/**
	 * @return O intervalo (em minutos) para os anúncios automáticos de "tempo restante".
	 */
	public int getAnnounceEndMinutes()
	{
		return _announceEndMinutes;
	}

	public boolean isTimeToRun()
	{
		LocalDateTime now = LocalDateTime.now();
		int currentDay = now.getDayOfWeek().getValue() % 7;
		LocalTime currentTime = now.toLocalTime().withSecond(0).withNano(0);
		
		if (!_activeDays.contains(currentDay))
			return false;
		
		return _activeTimes.stream().anyMatch(time -> time.equals(currentTime));
	}
}