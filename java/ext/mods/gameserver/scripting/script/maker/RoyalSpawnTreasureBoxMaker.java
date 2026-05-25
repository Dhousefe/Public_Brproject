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

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;

public class RoyalSpawnTreasureBoxMaker extends RoyalRushMaker
{
	public RoyalSpawnTreasureBoxMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
		if (name.equalsIgnoreCase("1002"))
		{
			final MultiSpawn def0 = maker.getSpawns().get(0);
			if (def0 == null)
				return;
			
			final Calendar c = Calendar.getInstance();
			
			final int i1 = c.get(Calendar.MINUTE);
			
			int i2 = Rnd.get(10);
			if (i1 >= 48)
				i2 += 10;
			else if (i1 >= 46)
				i2 += 15;
			else if (i1 >= 44)
				i2 += 20;
			else if (i1 >= 42)
				i2 += 26;
			else if (i1 >= 40)
				i2 += 32;
			else if (i1 >= 38)
				i2 += 39;
			else if (i1 >= 36)
				i2 += 45;
			else if (i1 >= 34)
				i2 += 52;
			else if (i1 >= 32)
				i2 += 60;
			else if (i1 >= 30)
				i2 += 68;
			else if (i1 >= 28)
				i2 += 76;
			else if (i1 >= 26)
				i2 += 85;
			else if (i1 >= 24)
				i2 += 94;
			else if (i1 >= 22)
				i2 += 103;
			else if (i1 >= 20)
				i2 += 113;
			else if (i1 >= 16)
				i2 += 123;
			else if (i1 >= 14)
				i2 += 134;
			else if (i1 >= 12)
				i2 += 145;
			else
				i2 += 157;
			
			if (maker.increaseSpawnedCount(def0, i2))
				def0.doSpawn(false);
		}
	}
}