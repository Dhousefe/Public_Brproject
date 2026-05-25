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

import ext.mods.gameserver.data.manager.PartyMatchRoomManager;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.PartyMatchRoom;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ExPartyRoomMember;
import ext.mods.gameserver.network.serverpackets.PartyMatchDetail;
import ext.mods.gameserver.network.serverpackets.PartyMatchList;

public final class RequestListPartyWaiting extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _auto;
	private int _bbs;
	private int _lvl;
	
	@Override
	protected void readImpl()
	{
		_auto = readD();
		_bbs = readD();
		_lvl = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isCursedWeaponEquipped())
			return;
		
		if (player.isInPartyMatchRoom())
		{
			final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
			if (room == null)
				return;
			
			player.sendPacket(new PartyMatchDetail(room));
			player.sendPacket(new ExPartyRoomMember(room, 2));
			player.broadcastUserInfo();
		}
		else
		{
			if (player.getParty() != null && !player.getParty().isLeader(player))
			{
				player.sendPacket(SystemMessageId.CANT_VIEW_PARTY_ROOMS);
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			PartyMatchRoomManager.getInstance().addWaitingPlayer(player);
			
			player.sendPacket(new PartyMatchList(player, _bbs, _lvl));
		}
	}
}