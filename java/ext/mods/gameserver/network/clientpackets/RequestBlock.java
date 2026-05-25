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

import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;

public final class RequestBlock extends L2GameClientPacket
{
	private static final int BLOCK = 0;
	private static final int UNBLOCK = 1;
	private static final int BLOCKLIST = 2;
	private static final int ALLBLOCK = 3;
	private static final int ALLUNBLOCK = 4;
	
	private String _targetName;
	private int _type;
	
	@Override
	protected void readImpl()
	{
		_type = readD();
		
		if (_type == BLOCK || _type == UNBLOCK)
			_targetName = readS();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		switch (_type)
		{
			case BLOCK, UNBLOCK:
				final int targetId = PlayerInfoTable.getInstance().getPlayerObjectId(_targetName);
				if (targetId <= 0 || player.getObjectId() == targetId)
				{
					player.sendPacket(SystemMessageId.FAILED_TO_REGISTER_TO_IGNORE_LIST);
					return;
				}
				
				if (PlayerInfoTable.getInstance().getPlayerAccessLevel(targetId) > 0)
				{
					player.sendPacket(SystemMessageId.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_GM);
					return;
				}
				
				if (_type == BLOCK)
					RelationManager.getInstance().addToBlockList(player, targetId);
				else
					RelationManager.getInstance().removeFromBlockList(player, targetId);
				break;
			
			case BLOCKLIST:
				RelationManager.getInstance().sendBlockList(player);
				break;
			
			case ALLBLOCK:
				player.sendPacket(SystemMessageId.BLOCKING_ALL);
				player.setInBlockingAll(true);
				break;
			
			case ALLUNBLOCK:
				player.sendPacket(SystemMessageId.NOT_BLOCKING_ALL);
				player.setInBlockingAll(false);
				break;
			
			default:
				LOGGER.warn("Unknown block type detected: {}.", _type);
		}
	}
}