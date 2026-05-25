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
import ext.mods.gameserver.network.serverpackets.ExDuelAskStart;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public final class RequestDuelStart extends L2GameClientPacket
{
	private String _targetName;
	private boolean _isPartyDuel;
	
	@Override
	protected void readImpl()
	{
		_targetName = readS();
		_isPartyDuel = readD() == 1;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player target = World.getInstance().getPlayer(_targetName);
		if (target == null || player == target)
		{
			player.sendPacket(SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL);
			return;
		}
		
		if (!player.canDuel())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
			return;
		}
		
		if (!target.canDuel())
		{
			player.sendPacket(target.getNoDuelReason());
			return;
		}
		
		if (!player.isIn3DRadius(target, 2000))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_RECEIVE_A_DUEL_CHALLENGE_BECAUSE_S1_IS_TOO_FAR_AWAY).addCharName(target));
			return;
		}
		
		if (_isPartyDuel)
		{
			final Party playerParty = player.getParty();
			if (playerParty == null || !playerParty.isLeader(player) || playerParty.containsPlayer(target))
			{
				player.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
				return;
			}
			
			final Party targetParty = target.getParty();
			if (targetParty == null)
			{
				player.sendPacket(SystemMessageId.SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY);
				return;
			}
			
			for (Player member : playerParty.getMembers())
			{
				if (member != player && !member.canDuel())
				{
					player.sendPacket(SystemMessageId.YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME);
					return;
				}
			}
			
			for (Player member : targetParty.getMembers())
			{
				if (member != target && !member.canDuel())
				{
					player.sendPacket(SystemMessageId.THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL);
					return;
				}
			}
			
			final Player partyLeader = targetParty.getLeader();
			
			if (!partyLeader.isProcessingRequest())
			{
				final CommandChannel playerChannel = playerParty.getCommandChannel();
				if (playerChannel != null)
					playerChannel.removeParty(playerParty);
				
				final CommandChannel targetChannel = targetParty.getCommandChannel();
				if (targetChannel != null)
					targetChannel.removeParty(targetParty);
				
				for (Player member : playerParty.getMembers())
					member.removeMeFromPartyMatch();
				
				for (Player member : targetParty.getMembers())
					member.removeMeFromPartyMatch();
				
				player.onTransactionRequest(partyLeader);
				partyLeader.sendPacket(new ExDuelAskStart(player.getName(), _isPartyDuel));
				
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL).addCharName(partyLeader));
				target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL).addCharName(player));
			}
			else
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(partyLeader));
		}
		else
		{
			if (!target.isProcessingRequest())
			{
				player.removeMeFromPartyMatch();
				target.removeMeFromPartyMatch();
				
				player.onTransactionRequest(target);
				target.sendPacket(new ExDuelAskStart(player.getName(), _isPartyDuel));
				
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_CHALLENGED_TO_A_DUEL).addCharName(target));
				target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_CHALLENGED_YOU_TO_A_DUEL).addCharName(player));
			}
			else
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(target));
		}
	}
}