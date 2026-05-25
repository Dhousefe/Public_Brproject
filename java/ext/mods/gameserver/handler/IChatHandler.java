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
package ext.mods.gameserver.handler;

import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.actor.Player;

/**
 * Interface used by chat handlers.
 */
public interface IChatHandler
{
	/**
	 * Handle a specific type of chat message.
	 * @param type : The {@link SayType} associated to the message.
	 * @param player : The {@link Player} which send the message.
	 * @param target : The {@link String} target to send the message.
	 * @param text : The {@link String} used as message.
	 */
	public void handleChat(SayType type, Player player, String target, String text);
	
	/**
	 * @return The array of {@link SayType}s registered to this handler.
	 */
	public SayType[] getChatTypeList();
}
