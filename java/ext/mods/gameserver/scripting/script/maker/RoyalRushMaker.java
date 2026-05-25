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
package ext.mods.gameserver.scripting.script.maker;

import java.util.Calendar;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;

public class RoyalRushMaker extends DefaultMaker
{
	public RoyalRushMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		ThreadPool.scheduleAtFixedRate(() -> onTimer("3000", maker), 1000, 1000);
	}
	
	@Override
	public void onNpcCreated(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		final Calendar c = Calendar.getInstance();
		
		final int currentMinute = c.get(Calendar.MINUTE);
		if (currentMinute > 49 && currentMinute < 60)
			npc.deleteMe();
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		final Calendar c = Calendar.getInstance();
		
		final int currentMinute = c.get(Calendar.MINUTE);
		if (ms.calculateRespawnDelay() != 0 && !(currentMinute > 49 && currentMinute < 59))
			super.onNpcDeleted(npc, ms, maker);
	}
	
	@Override
	public void onTimer(String name, NpcMaker maker)
	{
		if (name.equalsIgnoreCase("3000"))
		{
			final Calendar c = Calendar.getInstance();
			
			final int currentMinute = c.get(Calendar.MINUTE);
			if (currentMinute == 54)
			{
				final int currentSecond = c.get(Calendar.SECOND);
				if (currentSecond == 0)
					maker.getMaker().onMakerScriptEvent("1000", maker, 0, 0);
				else if (currentSecond == 1)
				{
					if (maker.getSpawnedCount() > 0)
						maker.getMaker().onMakerScriptEvent("1000", maker, 0, 0);
				}
				else if (currentSecond == 2)
				{
					if (maker.getSpawnedCount() > 0)
						maker.getMaker().onMakerScriptEvent("1000", maker, 0, 0);
				}
			}
		}
	}
}