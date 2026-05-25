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

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;

public class RoyalReqNextMaker extends RoyalRushMaker
{
	public RoyalReqNextMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
		final int bossMaker = maker.getMakerMemo().getInteger("BossMaker", -1);
		if (bossMaker == 1)
		{
			if (npc.getNpcId() == 25339 || npc.getNpcId() == 25342 || npc.getNpcId() == 25346 || npc.getNpcId() == 25349)
			{
				final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(maker.getMakerMemo().get("next_maker_name"));
				if (maker0 != null)
					maker0.getMaker().onMakerScriptEvent("1002", maker0, 0, 0);
			}
		}
		else
		{
			final int reqCount = maker.getMakerMemo().getInteger("req_count", -1);
			if (maker.getSpawnedCount() == reqCount)
			{
				final Calendar c = Calendar.getInstance();
				
				final int currentMinute = c.get(Calendar.MINUTE);
				if (currentMinute >= 0 && currentMinute <= 50)
				{
					final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker(maker.getMakerMemo().get("next_maker_name"));
					if (maker0 != null)
						maker0.getMaker().onMakerScriptEvent("1001", maker0, 0, 0);
				}
			}
		}
	}
}