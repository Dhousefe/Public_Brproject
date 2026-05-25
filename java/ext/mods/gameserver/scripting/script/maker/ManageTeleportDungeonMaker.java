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

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;

public class ManageTeleportDungeonMaker extends DefaultMaker
{
	public ManageTeleportDungeonMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		final MultiSpawn def0 = (MultiSpawn) SpawnManager.getInstance().getSpawn(maker.getMakerMemo().get("manager_npc_name"));
		if (def0 != null)
		{
			final int spawnCount = def0.getTotal() - def0.getSpawnedCount();
			if (spawnCount > 0 && maker.increaseSpawnedCount(def0, spawnCount))
				def0.doSpawn(spawnCount, false);
		}
		maker.getMakerMemo().set("i_ai0", 0);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		final String managerNpcAlias = maker.getMakerMemo().get("manager_npc_name");
		
		switch (name)
		{
			case "0":
				maker.getMakerMemo().set("i_ai0", 0);
				maker.setSpawnedCount(0);
				for (MultiSpawn ms : maker.getSpawns())
				{
					ms.cancelScheduledSpawns();
					if (!managerNpcAlias.equalsIgnoreCase(ms.getTemplate().getAlias()))
						ms.doDelete();
				}
				break;
			
			case "1":
				final int i_ai0 = maker.getMakerMemo().getInteger("i_ai0");
				if (i_ai0 == 0)
				{
					maker.getMakerMemo().set("i_ai0", 1);
					maker.setSpawnedCount(0);
					for (MultiSpawn ms : maker.getSpawns())
					{
						ms.cancelScheduledSpawns();
						if (!managerNpcAlias.equalsIgnoreCase(ms.getTemplate().getAlias()))
						{
							if (maker.increaseSpawnedCount(ms, ms.getTotal()))
								ms.doSpawn(ms.getTotal(), false);
						}
					}
				}
				break;
			
			case "2":
				for (MultiSpawn ms : maker.getSpawns())
				{
					if (managerNpcAlias.equalsIgnoreCase(ms.getTemplate().getAlias()))
					{
						for (Npc npc : ms.getNpcs())
							npc.sendScriptEvent(Integer.parseInt(name), 0, 0);
					}
				}
				break;
			
			case "3":
				maker.getMakerMemo().set("i_ai0", 0);
				
				for (MultiSpawn ms : maker.getSpawns())
				{
					if (managerNpcAlias.equalsIgnoreCase(ms.getTemplate().getAlias()))
					{
						for (Npc npc : ms.getNpcs())
							npc.sendScriptEvent(Integer.parseInt(name), 0, 0);
					}
					else
						ms.doDelete();
				}
				break;
		}
		super.onMakerScriptEvent(name, maker, int1, int2);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (maker.getMakerMemo().getInteger("i_ai0", 0) == 1)
		{
			if (maker.increaseSpawnedCount(ms, 1))
				ms.scheduleSpawn(ms.calculateRespawnDelay() * 1000);
		}
	}
	
	@Override
	public void onNpcCreated(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (maker.getMakerMemo().getInteger("i_ai0", 0) == 0)
		{
			final String managerNpcAlias = maker.getMakerMemo().get("manager_npc_name");
			if (!managerNpcAlias.equalsIgnoreCase(npc.getTemplate().getAlias()))
				npc.deleteMe();
		}
	}
}