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

public class ExclusiveDespawnNormalMaker extends DefaultMaker
{
	public ExclusiveDespawnNormalMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		maker.getMakerMemo().set("i_ai0", 0);
		maker.getMakerMemo().set("i_ai1", 0);
		
		super.onStart(maker);
	}
	
	@Override
	public void onNpcCreated(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (npc.getTemplate().getAlias().equalsIgnoreCase(maker.getMakerMemo().get("unique_npc")))
		{
			maker.getMakerMemo().set("i_ai0", 1);
			
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(maker.getMakerMemo().get("maker_name1"));
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1001", maker0, 1, 0);
			
			final NpcMaker maker1 = SpawnManager.getInstance().getNpcMaker(maker.getMakerMemo().get("maker_name2"));
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("1001", maker1, 1, 0);
		}
		else if (maker.getMakerMemo().getInteger("i_ai0") == 0)
			npc.deleteMe();
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		if (npc.getTemplate().getAlias().equalsIgnoreCase(maker.getMakerMemo().get("unique_npc")))
		{
			maker.getMakerMemo().set("i_ai0", 0);
			
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(maker.getMakerMemo().get("maker_name1"));
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
			
			final NpcMaker maker1 = SpawnManager.getInstance().getNpcMaker(maker.getMakerMemo().get("maker_name2"));
			if (maker1 != null)
				maker1.getMaker().onMakerScriptEvent("10025", maker1, 0, 0);
			
			super.onNpcDeleted(npc, ms, maker);
		}
		else if (maker.getMakerMemo().getInteger("i_ai0") == 1)
		{
			if (maker.increaseSpawnedCount(ms, 1))
				ms.scheduleSpawn(ms.calculateRespawnDelay() * 1000);
		}
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		if (name.equalsIgnoreCase("10025"))
		{
			final int i_ai1 = maker.getMakerMemo().getInteger("i_ai1") + 1;
			
			maker.getMakerMemo().set("i_ai1", i_ai1);
			if (i_ai1 == maker.getMakerMemo().getInteger("despawn_count"))
			{
				
				final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(maker.getMakerMemo().get("maker_name2"));
				if (maker0 != null)
				{
					maker0.getMaker().onMakerScriptEvent("1000", maker0, 0, 0);
					maker.getMakerMemo().set("i_ai0", int1);
					maker.getMakerMemo().set("i_ai1", 0);
				}
			}
		}
		else
		{
			maker.getMakerMemo().set("i_ai0", int1);
		}
	}
}
