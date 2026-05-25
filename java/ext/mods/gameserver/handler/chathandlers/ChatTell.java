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
package ext.mods.gameserver.handler.chathandlers;

import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.handler.IChatHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class ChatTell implements IChatHandler
{
	private static final SayType[] COMMAND_IDS =
	{
		SayType.TELL
	};
	
	@Override
	public void handleChat(SayType type, Player player, String target, String text)
	{
		if (target == null)
			return;
		
		final Player targetPlayer = World.getInstance().getPlayer(target);
		if (targetPlayer == null || targetPlayer.getClient().isDetached())
		{
			player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			return;
		}
		
		if (targetPlayer.isInJail() || targetPlayer.isChatBanned())
		{
			player.sendPacket(SystemMessageId.TARGET_IS_CHAT_BANNED);
			return;
		}
		
		if (!player.isGM())
		{
			if (targetPlayer.isBlockingAll())
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_BLOCKED_EVERYTHING).addCharName(targetPlayer));
				return;
			}
			
			if (RelationManager.getInstance().isInBlockList(targetPlayer, player))
			{
				player.sendPacket(SystemMessageId.THE_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
				return;
			}
		}
		
		targetPlayer.sendPacket(new CreatureSay(player, type, text));
		player.sendPacket(new CreatureSay(player.getObjectId(), type, "->" + targetPlayer.getName(), text));
	}
	
	@Override
	public SayType[] getChatTypeList()
	{
		return COMMAND_IDS;
	}
}