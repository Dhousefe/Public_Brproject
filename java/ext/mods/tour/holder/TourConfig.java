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
package ext.mods.tour.holder;

import java.util.List;

public class TourConfig
{
	private final boolean _enabled;
	private final int _duration;
	private final int _preparation;
	private final List<Integer> _days;
	private final List<String> _times;
	
	public TourConfig(boolean enabled, int duration, int preparation, List<Integer> days, List<String> times)
	{
		_enabled = enabled;
		_duration = duration;
		_preparation = preparation;
		_days = days;
		_times = times;
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	public int getDuration()
	{
		return _duration;
	}
	
	public int getPreparation()
	{
		return _preparation;
	}
	
	public List<Integer> getDays()
	{
		return _days;
	}
	
	public List<String> getTimes()
	{
		return _times;
	}
}