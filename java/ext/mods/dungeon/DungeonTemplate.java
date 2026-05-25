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
package ext.mods.dungeon;

import java.util.List;
import java.util.Map;

import ext.mods.dungeon.enums.DungeonType;
import ext.mods.dungeon.holder.SpawnTemplate;
import ext.mods.dungeon.holder.StageTemplate;

public class DungeonTemplate
{
	public final int id;
	public final String name;

	public final DungeonType type;

	public final boolean sharedInstance;
	public final long cooldown;
	public final List<StageTemplate> stages;
	public final Map<Integer, List<SpawnTemplate>> spawns;
	
	public DungeonTemplate(int id, String name, DungeonType type, boolean sharedInstance, long cooldown, List<StageTemplate> stages, Map<Integer, List<SpawnTemplate>> spawns)
	{
		this.id = id;
		this.name = name;
		this.type = type;
		this.sharedInstance = sharedInstance;
		this.cooldown = cooldown;
		this.stages = stages;
		this.spawns = spawns;
	}
}
