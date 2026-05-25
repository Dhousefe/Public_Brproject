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
package ext.mods.gameserver.network.serverpackets;

import java.util.Calendar;

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.clanhall.SiegableHall;

public class SiegeInfo extends L2GameServerPacket
{
	private Castle _castle;
	private SiegableHall _hall;
	
	public SiegeInfo(Castle castle)
	{
		_castle = castle;
	}
	
	public SiegeInfo(SiegableHall hall)
	{
		_hall = hall;
	}
	
	@Override
	protected final void writeImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		writeC(0xc9);
		
		if (_castle != null)
		{
			final int ownerId = _castle.getOwnerId();
			
			writeD(_castle.getId());
			writeD((ownerId == player.getClanId() && player.isClanLeader()) ? 0x01 : 0x00);
			writeD(ownerId);
			
			Clan clan = null;
			if (ownerId > 0)
				clan = ClanTable.getInstance().getClan(ownerId);
			
			if (clan != null)
			{
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getAllyId());
				writeS(clan.getAllyName());
			}
			else
			{
				writeS("NPC");
				writeS("");
				writeD(0);
				writeS("");
			}
			
			writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
			writeD((int) (_castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));
		}
		else
		{
			final int ownerId = _hall.getOwnerId();
			
			writeD(_hall.getId());
			writeD(((ownerId == player.getClanId()) && player.isClanLeader()) ? 0x01 : 0x00);
			writeD(ownerId);
			
			Clan clan = null;
			if (ownerId > 0)
				clan = ClanTable.getInstance().getClan(ownerId);
			
			if (clan != null)
			{
				writeS(clan.getName());
				writeS(clan.getLeaderName());
				writeD(clan.getAllyId());
				writeS(clan.getAllyName());
			}
			else
			{
				writeS("NPC");
				writeS("");
				writeD(0);
				writeS("");
			}
			
			writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
			writeD((int) ((_hall.getNextSiegeTime()) / 1000));
		}
		writeD(0x00);
	}
}