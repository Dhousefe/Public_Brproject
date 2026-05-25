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
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.group.PartyMatchRoom;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExPartyRoomMember;
import ext.mods.gameserver.network.serverpackets.PartyMatchDetail;

public class RequestManagePartyRoom extends L2GameClientPacket
{
	private int _roomId;
	private int _maxMembers;
	private int _minLvl;
	private int _maxLvl;
	private int _lootType;
	private String _roomTitle;
	
	@Override
	protected void readImpl()
	{
		_roomId = readD();
		_maxMembers = readD();
		_minLvl = readD();
		_maxLvl = readD();
		_lootType = readD();
		_roomTitle = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_roomId > 0)
		{
			final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(_roomId);
			if (room != null && room.getLeader() == player)
			{
				room.setMaxMembers(_maxMembers);
				room.setMinLvl(_minLvl);
				room.setMaxLvl(_maxLvl);
				room.setLootType(_lootType);
				room.setTitle(_roomTitle);
				room.refreshLocation();
				
				for (Player member : room.getMembers())
				{
					member.sendPacket(new PartyMatchDetail(room));
					member.sendPacket(new ExPartyRoomMember(room, 2));
					member.sendPacket(SystemMessageId.PARTY_ROOM_REVISED);
				}
			}
		}
		else if (PartyMatchRoomManager.getInstance().removeWaitingPlayer(player))
		{
			final int newId = PartyMatchRoomManager.getInstance().getNewRoomId();
			final PartyMatchRoom room = new PartyMatchRoom(newId, _roomTitle, _lootType, _minLvl, _maxLvl, _maxMembers, player);
			
			PartyMatchRoomManager.getInstance().addRoom(newId, room);
			
			final Party party = player.getParty();
			if (party != null)
			{
				for (Player member : party.getMembers())
				{
					if (member == player)
						continue;
					
					room.addMember(member, newId);
				}
			}
			
			player.sendPacket(new PartyMatchDetail(room));
			player.sendPacket(new ExPartyRoomMember(room, 1));
			player.sendPacket(SystemMessageId.PARTY_ROOM_CREATED);
			player.setPartyRoom(newId);
			player.broadcastUserInfo();
		}
	}
}