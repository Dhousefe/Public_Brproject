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

import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.serverpackets.GMHennaInfo;
import ext.mods.gameserver.network.serverpackets.GMViewCharacterInfo;
import ext.mods.gameserver.network.serverpackets.GMViewItemList;
import ext.mods.gameserver.network.serverpackets.GMViewPledgeInfo;
import ext.mods.gameserver.network.serverpackets.GMViewQuestList;
import ext.mods.gameserver.network.serverpackets.GMViewSkillInfo;
import ext.mods.gameserver.network.serverpackets.GMViewWarehouseWithdrawList;

public final class RequestGMCommand extends L2GameClientPacket
{
	private String _targetName;
	private int _command;
	
	@Override
	protected void readImpl()
	{
		_targetName = readS();
		_command = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (!player.isGM() || !player.getAccessLevel().allowAltG())
			return;
		
		final Player target = World.getInstance().getPlayer(_targetName);
		final Clan clan = ClanTable.getInstance().getClanByName(_targetName);
		
		if (target == null && (clan == null || _command != 6))
			return;
		
		switch (_command)
		{
			case 1:
				sendPacket(new GMViewCharacterInfo(target));
				sendPacket(new GMHennaInfo(target));
				break;
			
			case 2:
				if (target != null && target.getClan() != null)
					sendPacket(new GMViewPledgeInfo(target.getClan(), target));
				break;
			
			case 3:
				sendPacket(new GMViewSkillInfo(target));
				break;
			
			case 4:
				sendPacket(new GMViewQuestList(target));
				break;
			
			case 5:
				sendPacket(new GMViewItemList(target));
				sendPacket(new GMHennaInfo(target));
				break;
			
			case 6:
				if (target != null)
					sendPacket(new GMViewWarehouseWithdrawList(target));
				else
					sendPacket(new GMViewWarehouseWithdrawList(clan));
				break;
		}
	}
}