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
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;

public class IceFairySirrMaker extends CloseDoorMaker
{
	public IceFairySirrMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		maker.getMakerMemo().set("i_ai0", 0);
		
		super.onStart(maker);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		final MultiSpawn def0 = maker.getSpawns().get(0);
		
		if (name.equalsIgnoreCase("10005"))
		{
			maker.getMakerMemo().set("i_ai0", maker.getMakerMemo().getInteger("i_ai0") + 1);
			
			def0.sendScriptEvent(10001, maker.getMakerMemo().getInteger("i_ai0"), 0);
		}
		else if (name.equalsIgnoreCase("11040"))
			def0.sendScriptEvent(11040, int1, 0);
	}
	
	@Override
	public void onDoorEvent(Door door, NpcMaker maker)
	{
		if (!door.isOpened())
		{
			if (maker.getMakerMemo().getBool("enabled"))
				return;
			
			maker.getMakerMemo().set("enabled", true);
			
			for (MultiSpawn ms : maker.getSpawns())
			{
				if (maker.increaseSpawnedCount(ms, ms.getTotal()))
					ms.doSpawn(ms.getTotal(), false);
			}
			
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("schuttgart13_npc2314_1m1");
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("11037", maker0, 0, 0);
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
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		maker.getMakerMemo().set("i_ai0", 0);
	}
}