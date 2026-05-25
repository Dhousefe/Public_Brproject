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
package ext.mods.gameserver.scripting.task;

import java.util.HashMap;
import java.util.Map;

import ext.mods.gameserver.data.manager.RaidPointManager;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.scripting.ScheduledQuest;

/**
 * Each month players hunt Raid Bosses and get raid points. At the end of the month top 100 players from the list get clan reputation points.<br>
 * <br>
 * The points are added after the first weekly game maintenance of the month.<br>
 * <br>
 * Only the players belonging to clans level 5 or higher can get such points.
 */
public final class RaidPointReset extends ScheduledQuest
{
	public RaidPointReset()
	{
		super(-1, "task");
	}
	
	@Override
	public final void onStart()
	{
		final Map<Integer, Integer> ranks = RaidPointManager.getInstance().getWinners();
		
		final Map<Clan, Integer> rewards = new HashMap<>();
		
		for (Clan clan : ClanTable.getInstance().getClans())
		{
			if (clan.getLevel() < 5)
				continue;
			
			for (Map.Entry<Integer, Integer> entry : ranks.entrySet())
			{
				if (!clan.isMember(entry.getKey()))
					continue;
				
				int points = 0;
				switch (entry.getValue())
				{
					case 1:
						points = 1250;
						break;
					
					case 2:
						points = 900;
						break;
					
					case 3:
						points = 700;
						break;
					
					case 4:
						points = 600;
						break;
					
					case 5:
						points = 450;
						break;
					
					case 6:
						points = 350;
						break;
					
					case 7:
						points = 300;
						break;
					
					case 8:
						points = 200;
						break;
					
					case 9:
						points = 150;
						break;
					
					case 10:
						points = 100;
						break;
					
					default:
						points = (entry.getValue() <= 50) ? 25 : 12;
						break;
				}
				rewards.merge(clan, points, Integer::sum);
			}
		}
		
		for (Map.Entry<Clan, Integer> entry : rewards.entrySet())
			entry.getKey().addReputationScore(entry.getValue());
		
		RaidPointManager.getInstance().cleanUp();
	}
	
	@Override
	public final void onEnd()
	{
	}
}