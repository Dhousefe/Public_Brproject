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
package ext.mods.gameserver.model.group;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ext.mods.gameserver.data.manager.PartyMatchRoomManager;
import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.restart.RestartPoint;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.network.serverpackets.ExClosePartyRoom;
import ext.mods.gameserver.network.serverpackets.ExManagePartyRoomMember;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class PartyMatchRoom extends AbstractGroup
{
	private final List<Player> _members = new CopyOnWriteArrayList<>();
	
	private final int _id;
	
	private String _title;
	private int _lootType;
	private int _location;
	private int _minLvl;
	private int _maxLvl;
	private int _maxMembersCount;
	
	public PartyMatchRoom(int id, String title, int loot, int minLvl, int maxLvl, int maxMembersCount, Player leader)
	{
		super(leader);
		
		_id = id;
		_title = title;
		_lootType = loot;
		
		refreshLocation();
		
		_minLvl = minLvl;
		_maxLvl = maxLvl;
		_maxMembersCount = maxMembersCount;
		
		_members.add(leader);
	}
	
	@Override
	public List<Player> getMembers()
	{
		return _members;
	}
	
	@Override
	public int getMembersCount()
	{
		return _members.size();
	}
	
	@Override
	public boolean containsPlayer(WorldObject player)
	{
		return _members.contains(player);
	}
	
	@Override
	public void broadcastPacket(L2GameServerPacket packet)
	{
		for (Player member : _members)
			member.sendPacket(packet);
	}
	
	@Override
	public void broadcastCreatureSay(CreatureSay msg, Player broadcaster)
	{
		for (Player member : _members)
		{
			if (!RelationManager.getInstance().isInBlockList(member, broadcaster))
				member.sendPacket(msg);
		}
	}
	
	@Override
	public void broadcastOnScreen(int time, NpcStringId npcStringId)
	{
		broadcastPacket(new ExShowScreenMessage(npcStringId.getMessage(), time));
	}
	
	@Override
	public void broadcastOnScreen(int time, NpcStringId npcStringId, Object... params)
	{
		broadcastPacket(new ExShowScreenMessage(npcStringId.getMessage(params), time));
	}
	
	@Override
	public void recalculateLevel()
	{
	}
	
	@Override
	public void disband()
	{
		for (Player member : _members)
		{
			member.sendPacket(ExClosePartyRoom.STATIC_PACKET);
			member.sendPacket(SystemMessageId.PARTY_ROOM_DISBANDED);
			
			member.setPartyRoom(0);
			member.broadcastUserInfo();
		}
		_members.clear();
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getLootType()
	{
		return _lootType;
	}
	
	public void setLootType(int lootType)
	{
		_lootType = lootType;
	}
	
	public int getMinLvl()
	{
		return _minLvl;
	}
	
	public void setMinLvl(int minLvl)
	{
		_minLvl = minLvl;
	}
	
	public int getMaxLvl()
	{
		return _maxLvl;
	}
	
	public void setMaxLvl(int maxLvl)
	{
		_maxLvl = maxLvl;
	}
	
	public int getLocation()
	{
		return _location;
	}
	
	public void refreshLocation()
	{
		final RestartPoint rp = RestartPointData.getInstance().getRestartPoint(getLeader());
		_location = (rp == null) ? 100 : rp.getBbs();
	}
	
	public int getMaxMembers()
	{
		return _maxMembersCount;
	}
	
	public void setMaxMembers(int maxmem)
	{
		_maxMembersCount = maxmem;
	}
	
	public String getTitle()
	{
		return _title;
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	
	/**
	 * Add a {@link Player} on this {@link PartyMatchRoom}.
	 * @param player : The {@link Player} to add.
	 * @param roomId : The room id to link this {@link Player} with.
	 */
	public void addMember(Player player, int roomId)
	{
		_members.add(player);
		
		player.setPartyRoom(roomId);
		
		player.broadcastUserInfo();
	}
	
	/**
	 * Remove the {@link Player} set as parameter from this {@link PartyMatchRoom}.
	 * <ul>
	 * <li>If this {@link Player} isn't the leader, remove it properly.</li>
	 * <li>If it was the last {@link Player}, delete this {@link PartyMatchRoom}.</li>
	 * <li>If this {@link Player} is the leader, promote first a new leader and then remove that {@link Player} as a regular member.</li>
	 * </ul>
	 * @param player : The {@link Player} to remove.
	 */
	public void removeMember(Player player)
	{
		if (player == null || !_members.contains(player))
			return;
		
		if (!isLeader(player))
		{
			_members.remove(player);
			
			for (Player member : _members)
			{
				member.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_LEFT_PARTY_ROOM).addCharName(player));
				member.sendPacket(new ExManagePartyRoomMember(player, this, 2));
			}
			
			player.sendPacket(ExClosePartyRoom.STATIC_PACKET);
			
			player.setPartyRoom(0);
			
			player.broadcastUserInfo();
		}
		else if (_members.size() == 1)
			PartyMatchRoomManager.getInstance().deleteRoom(_id);
		else
		{
			final Player newLeader = _members.stream().filter(m -> m != getLeader()).findFirst().orElse(null);
			
			changeLeader(newLeader);
			
			removeMember(player);
		}
	}
	
	/**
	 * Change this {@link PartyMatchRoom} leader.
	 * @param newLeader : The {@link Player} to promote as leader.
	 */
	public void changeLeader(Player newLeader)
	{
		if (newLeader == null || !_members.contains(newLeader) || isLeader(newLeader) || _members.size() == 1)
			return;
		
		final int newLeaderIndex = _members.indexOf(newLeader);
		if (newLeaderIndex == -1)
			return;
		
		Player oldLeader = getLeader();
		
		Collections.swap(_members, 0, newLeaderIndex);
		
		setLeader(newLeader);
		
		for (Player member : _members)
		{
			member.sendPacket(new ExManagePartyRoomMember(newLeader, this, 1));
			member.sendPacket(new ExManagePartyRoomMember(oldLeader, this, 1));
			member.sendPacket(SystemMessageId.PARTY_ROOM_LEADER_CHANGED);
		}
	}
	
	/**
	 * @return True if this {@link PartyMatchRoom} is actually full.
	 */
	public boolean isFull()
	{
		return _members.size() >= _maxMembersCount;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return True if the {@link Player} set as parameter can enter in this {@link PartyMatchRoom}.
	 */
	public boolean checkEntrance(Player player)
	{
		return player.getStatus().getLevel() >= _minLvl && player.getStatus().getLevel() <= _maxLvl && _members.size() < _maxMembersCount;
	}
}