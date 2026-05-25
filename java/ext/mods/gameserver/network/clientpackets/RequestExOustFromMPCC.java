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
package ext.mods.gameserver.network.clientpackets;

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.CommandChannel;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class RequestExOustFromMPCC extends L2GameClientPacket
{
	private String _targetName;
	
	@Override
	protected void readImpl()
	{
		_targetName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player requestor = getClient().getPlayer();
		if (requestor == null)
			return;
		
		final Player target = World.getInstance().getPlayer(_targetName);
		if (target == null)
		{
			requestor.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
			return;
		}
		
		if (requestor.equals(target))
		{
			requestor.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final Party requestorParty = requestor.getParty();
		final Party targetParty = target.getParty();
		
		if (requestorParty == null || targetParty == null)
		{
			requestor.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final CommandChannel requestorChannel = requestorParty.getCommandChannel();
		if (requestorChannel == null || !requestorChannel.isLeader(requestor))
		{
			requestor.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (!requestorChannel.removeParty(targetParty))
		{
			requestor.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		targetParty.broadcastMessage(SystemMessageId.DISMISSED_FROM_COMMAND_CHANNEL);
		
		if (requestorParty.isInCommandChannel())
			requestorParty.getCommandChannel().broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PARTY_DISMISSED_FROM_COMMAND_CHANNEL).addCharName(targetParty.getLeader()));
	}
}