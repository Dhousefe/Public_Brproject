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

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;

public class UndeadBandmasterMaker extends OpenDoorMaker
{
	public UndeadBandmasterMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		ThreadPool.schedule(() -> onTimer("1000", maker), 3000);
		
		super.onStart(maker);
	}
	
	@Override
	public void onDoorEvent(Door door, NpcMaker maker)
	{
		if (door.isOpened())
		{
			if (maker.getMakerMemo().getBool("enabled"))
				return;
			
			maker.getMakerMemo().set("enabled", true);
			
			for (MultiSpawn ms : maker.getSpawns())
			{
				if (maker.increaseSpawnedCount(ms, ms.getTotal()))
				{
					ms.doSpawn(ms.getTotal(), false);
					ThreadPool.schedule(() -> onTimer("1000", maker), 3000);
				}
			}
		}
		else
		{
			if (!maker.getMakerMemo().getBool("enabled"))
				return;
			
			maker.getMakerMemo().set("enabled", false);
			maker.deleteAll();
		}
	}
	
	@Override
	public void onTimer(String name, NpcMaker maker)
	{
		if (name.equalsIgnoreCase("1000"))
		{
			for (int i = 0; i <= 6; i += 2)
			{
				final MultiSpawn def0 = maker.getSpawns().get(i);
				if (def0 != null)
					def0.sendScriptEvent(10032, i, 0);
			}
		}
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		final int npcsAlive = maker.getSpawnedCount();
		
		if (npcsAlive == 6)
		{
		}
		else if (npcsAlive == 4)
		{
		}
		else if (npcsAlive == 2)
		{
		}
		else if (npcsAlive == 0)
		{
			final String tDoorName3 = maker.getMakerMemo().getOrDefault("TDoorName3", "TDoorName3_default");
			if (!tDoorName3.equalsIgnoreCase("TDoorName3_default"))
				DoorData.getInstance().getDoor(tDoorName3).openMe();
			
			final String tDoorName4 = maker.getMakerMemo().getOrDefault("TDoorName4", "TDoorName4_default");
			if (!tDoorName4.equalsIgnoreCase("TDoorName4_default"))
				DoorData.getInstance().getDoor(tDoorName4).openMe();
			
			NpcMaker maker1 = SpawnManager.getInstance().getNpcMaker("godard32_2515_14m1");
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("1000", maker1, 0, 1);
			
			maker1 = SpawnManager.getInstance().getNpcMaker("godard32_2515_11m1");
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("1000", maker1, 0, 1);
			
			maker1 = SpawnManager.getInstance().getNpcMaker("godard32_2515_10m1");
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("1000", maker1, 0, 1);
			
			maker1 = SpawnManager.getInstance().getNpcMaker("godard32_2515_12m1");
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("1000", maker1, 0, 1);
			
			maker1 = SpawnManager.getInstance().getNpcMaker("godard32_2515_13m1");
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("1000", maker1, 0, 1);
			
			maker1 = SpawnManager.getInstance().getNpcMaker("godard32_2515_15m1");
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("1000", maker1, 0, 1);
			
			maker1 = SpawnManager.getInstance().getNpcMaker("godard32_2515_18m1");
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("1000", maker1, 0, 1);
		}
	}

}