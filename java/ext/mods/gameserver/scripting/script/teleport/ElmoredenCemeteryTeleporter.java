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
package ext.mods.gameserver.scripting.script.teleport;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.scripting.Quest;

public class ElmoredenCemeteryTeleporter extends Quest
{
	private static final int USED_GRAVE_PASS = 7261;
	private static final int ANTIQUE_BROOCH = 7262;
	
	private static final Location FOUR_SEPULCHERS_LOC = new Location(178127, -84435, -7215);
	private static final Location IMPERIAL_TOMB_LOC = new Location(186699, -75915, -2826);
	
	public ElmoredenCemeteryTeleporter()
	{
		super(-1, "teleport");
		
		addTalkId(31919, 31920);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = null;
		
		if (event.equalsIgnoreCase("4S"))
		{
			if (player.getInventory().hasItems(ANTIQUE_BROOCH))
			{
				player.teleportTo(FOUR_SEPULCHERS_LOC, 0);
			}
			else if (player.getInventory().hasItems(USED_GRAVE_PASS))
			{
				takeItems(player, USED_GRAVE_PASS, 1);
				player.teleportTo(FOUR_SEPULCHERS_LOC, 0);
			}
			else
				htmltext = npc.getNpcId() + "-1.htm";
		}
		else if (event.equalsIgnoreCase("IT"))
		{
			if (player.getInventory().hasItems(ANTIQUE_BROOCH))
			{
				player.teleportTo(IMPERIAL_TOMB_LOC, 0);
			}
			else if (player.getInventory().hasItems(USED_GRAVE_PASS))
			{
				takeItems(player, USED_GRAVE_PASS, 1);
				player.teleportTo(IMPERIAL_TOMB_LOC, 0);
			}
			else
				htmltext = npc.getNpcId() + "-1.htm";
		}
		
		return htmltext;
	}
}