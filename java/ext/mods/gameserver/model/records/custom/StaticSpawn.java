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
package ext.mods.gameserver.model.records.custom;

import java.util.Calendar;
import java.util.List;

public record StaticSpawn(boolean isEnabled, int id, List<String> days, List<String> time, int randomTime, boolean earthQuake, boolean announce)
{
	private int timeStringToMinutes(String timeString)
	{
		String[] parts = timeString.split(":");
		int hours = Integer.parseInt(parts[0]);
		int minutes = Integer.parseInt(parts[1]);
		return hours * 60 + minutes;
	}
	
	public boolean canSpawnOnSameDay(int dayWeek)
	{
		return days.contains(String.valueOf(dayWeek));
	}
	
	public int getSpawnDayTime(int dayTime)
	{
		for (String t : time)
		{
			int minutes = timeStringToMinutes(t);
			if (minutes > dayTime)
				return minutes;
		}
		return -1;
	}
	
	public int getFirstSpawnDayTime()
	{
		return time.isEmpty() ? -1 : timeStringToMinutes(time.get(0));
	}
	
	public int getSpawnDayWeek(int dayWeek)
	{
		int index = dayWeek % 7;
		for (int i = 0; i < 7; i++)
		{
			if (days.contains(String.valueOf(index + 1)))
				return index + 1;
			
			index = (index + 1) % 7;
		}
		return -1;
	}
	
	public long calcNextDate()
	{
		Calendar cal = Calendar.getInstance();
		final int cDayTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		final int curDayWeek = cal.get(Calendar.DAY_OF_WEEK);
		int spawnDayTime = getSpawnDayTime(cDayTime);
		if (spawnDayTime != -1 && canSpawnOnSameDay(curDayWeek))
		{
			cal.set(Calendar.HOUR_OF_DAY, spawnDayTime / 60);
			cal.set(Calendar.MINUTE, spawnDayTime % 60);
			cal.set(Calendar.SECOND, 0);
		}
		else
		{
			final int spawnDayWeek = getSpawnDayWeek(curDayWeek);
			int deltaDay = 0;
			if (curDayWeek > spawnDayWeek)
				deltaDay = (7 - curDayWeek) + spawnDayWeek;
			else if (curDayWeek < spawnDayWeek)
				deltaDay = spawnDayWeek - curDayWeek;
			else
				deltaDay = 7;
			
			spawnDayTime = getFirstSpawnDayTime();
			cal.add(Calendar.DAY_OF_MONTH, deltaDay);
			cal.set(Calendar.HOUR_OF_DAY, spawnDayTime / 60);
			cal.set(Calendar.MINUTE, spawnDayTime % 60);
			cal.set(Calendar.SECOND, 0);
		}
		return cal.getTimeInMillis();
	}
}