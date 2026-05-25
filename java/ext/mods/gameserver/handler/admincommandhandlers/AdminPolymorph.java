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

import java.util.StringTokenizer;

import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;

public class AdminPolymorph implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_polymorph"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final Creature targetCreature = getTargetCreature(player, true);
		
		final Player targetPlayer = targetCreature.getActingPlayer();
		if (targetPlayer != null && targetPlayer.isMounted())
			targetPlayer.dismount();
		
		if (targetCreature.getPolymorphTemplate() != null)
			targetCreature.unpolymorph();
		else
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				
				if (!targetCreature.polymorph(Integer.parseInt(st.nextToken())))
					player.sendPacket(SystemMessageId.APPLICANT_INFORMATION_INCORRECT);
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //polymorph npcId");
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}