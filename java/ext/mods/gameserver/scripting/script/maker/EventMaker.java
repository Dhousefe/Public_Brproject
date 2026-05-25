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

import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.model.spawn.SpawnData;

public class EventMaker extends DefaultMaker
{
	public EventMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		if (shouldSpawn(maker))
		{
			for (MultiSpawn ms : maker.getSpawns())
			{
				if (ms.getSpawnData() != null)
					ms.loadDBNpcInfo();
				else
				{
					if (maker.increaseSpawnedCount(ms, ms.getTotal()))
						ms.doSpawn(ms.getTotal(), false);
				}
			}
		}
	}
	
	@Override
	public void onNpcDBInfo(MultiSpawn ms, SpawnData spawnData, NpcMaker maker)
	{
	}
	
	@Override
	public void onNpcCreated(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (!shouldSpawn(maker))
			npc.deleteMe();
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (!shouldSpawn(maker))
			return;
		
		if (ms.getRespawnDelay() > 0 && maker.increaseSpawnedCount(ms, 1))
			ms.scheduleSpawn(ms.calculateRespawnDelay() * 1000);
	}
	
	private static boolean shouldSpawn(NpcMaker maker)
	{
		return ArraysUtil.contains(Config.SPAWN_EVENTS, maker.getMakerMemo().get("EventName"));
	}
}
