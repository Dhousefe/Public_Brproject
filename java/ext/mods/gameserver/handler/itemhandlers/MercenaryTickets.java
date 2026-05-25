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
package ext.mods.gameserver.handler.itemhandlers;

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.MercenaryTicket;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

/**
 * Handler to use mercenary tickets.<br>
 * <br>
 * Check constraints:
 * <ul>
 * <li>Only specific tickets may be used in each castle (different tickets for each castle)</li>
 * <li>Only the owner of that castle may use them</li>
 * <li>tickets cannot be used during siege</li>
 * <li>Check if max number of tickets from this ticket's TYPE has been reached</li>
 * </ul>
 * If allowed, spawn the item in the world and remove it from the player's inventory.
 */
public class MercenaryTickets implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		final Player player = (Player) playable;
		if (player == null)
			return;
		
		final Castle castle = CastleManager.getInstance().getCastle(player);
		if (castle == null)
			return;
		
		if (!player.isCastleLord(castle.getId()))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_AUTHORITY_TO_POSITION_MERCENARIES);
			return;
		}
		
		final int itemId = item.getItemId();
		final MercenaryTicket ticket = castle.getTicket(itemId);
		
		if (ticket == null)
		{
			player.sendPacket(SystemMessageId.MERCENARIES_CANNOT_BE_POSITIONED_HERE);
			return;
		}
		
		if (castle.getSiege().isInProgress())
		{
			player.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			return;
		}
		
		if (!SevenSignsManager.getInstance().isSealValidationPeriod())
		{
			player.sendPacket(SystemMessageId.MERC_CAN_BE_ASSIGNED);
			return;
		}
		
		if (!ticket.isSsqType(SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE)))
		{
			player.sendPacket(SystemMessageId.MERC_CANT_BE_ASSIGNED_USING_STRIFE);
			return;
		}
		
		if (castle.getDroppedTicketsCount(itemId) >= ticket.getMaxAmount())
		{
			player.sendPacket(SystemMessageId.THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE);
			return;
		}
		
		if (castle.isTooCloseFromDroppedTicket(player.getX(), player.getY(), player.getZ()))
		{
			player.sendPacket(SystemMessageId.POSITIONING_CANNOT_BE_DONE_BECAUSE_DISTANCE_BETWEEN_MERCENARIES_TOO_SHORT);
			return;
		}
		
		final ItemInstance droppedTicket = player.dropItem(item.getObjectId(), 1, player.getX(), player.getY(), player.getZ(), false);
		if (droppedTicket == null)
			return;
		
		castle.addDroppedTicket(droppedTicket);
		
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PLACE_S1_IN_CURRENT_LOCATION_AND_DIRECTION).addItemName(itemId));
	}
}