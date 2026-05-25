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
package ext.mods.gameserver.model.entity.autofarm;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmArea;

public class AutoFarmTask implements Runnable
{
	private int _runTick;
	
	public AutoFarmTask()
	{
		ThreadPool.scheduleAtFixedRate(this, 400, 400);
	}
	
	@Override
	public void run()
	{
		_runTick++;
		
		AutoFarmManager.getInstance().getPlayers().parallelStream().filter(AutoFarmProfile::isEnabled).forEach(AutoFarmProfile::startRoutine);
		
		if (_runTick >= 60)
		{
			for (AutoFarmProfile autoFarmProfile : AutoFarmManager.getInstance().getPlayers())
			{
				if (autoFarmProfile.isEnabled())
					continue;
				
				if (System.currentTimeMillis() > autoFarmProfile.getLastActiveTime() + TimeUnit.MINUTES.toMillis(10))
				{
					final List<AutoFarmArea> areas = autoFarmProfile.getAreas().values().stream().filter(a -> a.getId() != autoFarmProfile.getSelectedAreaId() && a.isFromDb() && a.getType() == AutoFarmType.ZONA && a.getFarmZone().isBuilt()).toList();
					areas.forEach(a -> a.getFarmZone().removeFromWorld());
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