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
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;

public class AlarmDeviceMaker1 extends DefaultMaker
{
	public AlarmDeviceMaker1(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		maker.getMakerMemo().set("i_ai0", 1);
		
		ThreadPool.schedule(() -> onTimer("2000", maker), 3000);
	}
	
	@Override
	public void onTimer(String name, NpcMaker maker)
	{
		if (name.equalsIgnoreCase("2000"))
		{
			final MultiSpawn def0 = maker.getSpawns().get(0);
			if (def0 != null)
				def0.sendScriptEvent(10025, 1, 0);
		}
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		if (name.equalsIgnoreCase("10008"))
		{
			maker.getMakerMemo().set("i_ai0", maker.getMakerMemo().getInteger("i_ai0") + 1);
			
			final MultiSpawn def0 = maker.getSpawns().get(0);
			if (def0 != null)
				def0.sendScriptEvent(10025, maker.getMakerMemo().getInteger("i_ai0"), 0);
		}
		else if (name.equalsIgnoreCase("1001"))
		{
			maker.getMakerMemo().set("i_ai0", 1);
			
			ThreadPool.schedule(() -> onTimer("2000", maker), 3000);
		}
		super.onMakerScriptEvent(name, maker, int1, int2);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (maker.getSpawnedCount() == 0)
		{
			onMakerScriptEvent("10008", maker, 0, 0);
			
			NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("godard32_2515_19m1");
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("10008", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker("godard32_2515_20m1");
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("10008", maker0, 0, 0);
			
			maker0 = SpawnManager.getInstance().getNpcMaker("godard32_2515_21m1");
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("10008", maker0, 0, 0);
			
			maker.getMakerMemo().set("i_ai0", 0);
		}
	}
}