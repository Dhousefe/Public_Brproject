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
package ext.mods.gameserver.handler.bypasshandlers;

import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

public class Multisell implements IBypassHandler {

	private static final String[] COMMANDS = {
		"multisell",
		"exc_multisell"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target) {
		try {
			if (target instanceof Npc npc)
			{
				if (command.toLowerCase().startsWith(COMMANDS[0]))
				{
					MultisellData.getInstance().separateAndSend(command.substring(9).trim(), player, npc, false);
					return true;
				}
				else if (command.toLowerCase().startsWith(COMMANDS[1]))
				{
					MultisellData.getInstance().separateAndSend(command.substring(13).trim(), player, npc, true);
					return true;
				}
			}
			return false;
		}
		catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return false;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
