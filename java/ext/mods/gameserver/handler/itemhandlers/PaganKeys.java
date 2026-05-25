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

import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.enums.FloodProtector;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class PaganKeys implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player player))
			return;
		
		final WorldObject target = player.getTarget();
		
		if (!(target instanceof Door targetDoor))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!player.isIn3DRadius(targetDoor, Npc.INTERACTION_DISTANCE))
		{
			player.sendPacket(SystemMessageId.DIST_TOO_FAR_CASTING_STOPPED);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (!player.getClient().performAction(FloodProtector.ROLL_DICE))
			return;
		
		if (!playable.destroyItem(item.getObjectId(), 1, true))
			return;
		
		final int doorId = targetDoor.getDoorId();
		
		switch (item.getItemId())
		{
			case 8056:
				if (doorId == 23150004 || doorId == 23150003)
				{
					DoorData.getInstance().getDoor(23150003).openMe();
					DoorData.getInstance().getDoor(23150004).openMe();
				}
				else
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(8056));
				break;
			
			case 8273:
				switch (doorId)
				{
					case 19160002, 19160003, 19160004, 19160005, 19160006, 19160007, 19160008, 19160009:
						DoorData.getInstance().getDoor(doorId).openMe();
						break;
					
					default:
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(8273));
						break;
				}
				break;
			
			case 8274:
				switch (doorId)
				{
					case 19160010, 19160011:
						DoorData.getInstance().getDoor(doorId).openMe();
						break;
					
					default:
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(8275));
						break;
				}
				break;
			
			case 8275:
				switch (doorId)
				{
					case 19160012, 19160013:
						DoorData.getInstance().getDoor(doorId).openMe();
						break;
					
					default:
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(8275));
						break;
				}
				break;
		}
	}
}