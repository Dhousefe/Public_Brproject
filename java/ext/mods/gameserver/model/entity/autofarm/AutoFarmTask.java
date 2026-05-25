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
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, 
* SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo, Tiagorosendo, Schuster, LucasStark, damedd
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.gameserver.model.entity.autofarm;

import java.util.Collection;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmArea;

public class AutoFarmTask implements Runnable
{
	private int _runTick;
	
	private static final long INACTIVE_TIMEOUT = 600000L; 
	
	public AutoFarmTask()
	{
		ThreadPool.scheduleAtFixedRate(() -> ThreadPool.executeIO(this), 400, 400);
	}
	
	@Override
	public void run()
	{
		_runTick++;
		
		final Collection<AutoFarmProfile> players = AutoFarmManager.getInstance().getPlayers();
		for (AutoFarmProfile profile : players)
		{
			if (profile.isEnabled())
			{
				profile.startRoutine();
			}
		}
		
		if (_runTick >= 60)
		{
			final long currentTime = System.currentTimeMillis();
			
			for (AutoFarmProfile autoFarmProfile : players)
			{
				if (autoFarmProfile.isEnabled())
					continue;
				
				if (currentTime > autoFarmProfile.getLastActiveTime() + INACTIVE_TIMEOUT)
				{
					for (AutoFarmArea area : autoFarmProfile.getAreas().values())
					{
						if (area.getId() != autoFarmProfile.getSelectedAreaId() 
							&& area.isFromDb() 
							&& area.getType() == AutoFarmType.ZONA 
							&& area.getFarmZone().isBuilt())
						{
							area.getFarmZone().removeFromWorld();
						}
					}
				}
			}
			
			_runTick = 0;
		}
	}
	
	public static final AutoFarmTask getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final AutoFarmTask INSTANCE = new AutoFarmTask();
	}
}