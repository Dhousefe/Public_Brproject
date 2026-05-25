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
package ext.mods.gameserver.handler.usercommandhandlers;

import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.handler.IUserCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.restart.RestartPoint;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class Loc implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS =
	{
		0
	};
	
	@Override
	public void useUserCommand(int id, Player player)
	{
		final RestartPoint rp = RestartPointData.getInstance().getCalculatedRestartPoint(player);
		if (rp != null)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(rp.getLocName());
			if (sm != null)
				player.sendPacket(sm.addNumber(player.getX()).addNumber(player.getY()).addNumber(player.getZ()));
		}
	}
	
	@Override
	public int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}