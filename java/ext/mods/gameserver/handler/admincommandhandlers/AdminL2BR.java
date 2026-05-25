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
package ext.mods.gameserver.handler.admincommandhandlers;

import ext.mods.gameserver.geoengine.pathfinding.integration.GeoEngineBridge;
import ext.mods.gameserver.geoengine.pathfinding.integration.L2BRAdminCommands;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;

/**
 * Handler para comandos admin L2BR Pathfinder.
 * @author Dhousefe
 */
public class AdminL2BR implements IAdminCommandHandler {

	private static final String[] ADMIN_COMMANDS = {
		"admin_l2br_generate",
		"admin_l2br_generate_all",  
		"admin_l2br_info",          
		"admin_l2br_status",
		"admin_l2br_reload",
		"admin_l2br_unload",
		"admin_l2br_benchmark",
		"admin_l2br_config",
		"admin_l2br_help"
	};

	@Override
	public void useAdminCommand(String command, Player player) {
		
		final String l2brCommand = command.replace("admin_l2br_", "//l2br_");

		final var bridge = GeoEngineBridge.getInstance();
		final var adminCommands = bridge.getAdminCommands();

		if (adminCommands == null) {
			player.sendMessage("L2BR Pathfinder não está inicializado. Verifique UseL2BRPathfinding em geoengine.properties.");
			return;
		}

		final var adminInterface = L2BRAdminCommands.createAdminInterface(player);
		final boolean handled = adminCommands.processCommand(l2brCommand, adminInterface);

		if (!handled) {
			player.sendMessage("Comando L2BR não reconhecido: " + l2brCommand);
		}
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}