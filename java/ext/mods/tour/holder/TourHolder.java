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

import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.model.holder.IntIntHolder;

public class TourHolder
{
	private final int count;
	private final int x;
	private final int y;
	private final int z;
	private final int _duration;
	private final List<IntIntHolder> _rewards;
	
	public TourHolder(StatSet set)
	{
		count = set.getInteger("count", 1);
		x = set.getInteger("x");
		y = set.getInteger("y");
		z = set.getInteger("z");
		_duration = set.getInteger("duration", 10);
		_rewards = parseRewards(set.getString("reward", "57-1;"));
		
	}
	
	private List<IntIntHolder> parseRewards(String rewardString)
	{
		List<IntIntHolder> list = new ArrayList<>();
		
		if (rewardString.isEmpty())
			return list;
		
		String[] rewardEntries = rewardString.split(";");
		for (String entry : rewardEntries)
		{
			String[] parts = entry.split("-");
			if (parts.length == 2)
			{
				int itemId = Integer.parseInt(parts[0]);
				int amount = Integer.parseInt(parts[1]);
				list.add(new IntIntHolder(itemId, amount));
			}
		}
		return list;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getZ()
	{
		return z;
	}
	
	public int getDuration()
	{
		return _duration;
	}
	
	public List<IntIntHolder> getRewards()
	{
		return _rewards;
	}
}
