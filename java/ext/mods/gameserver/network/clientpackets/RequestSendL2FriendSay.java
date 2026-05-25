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

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.L2FriendSay;

public final class RequestSendL2FriendSay extends L2GameClientPacket
{
	private static final Logger CHAT_LOG = Logger.getLogger("chat");
	
	private String _message;
	private String _recipient;
	
	@Override
	protected void readImpl()
	{
		_message = readS();
		_recipient = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (_message == null || _message.isEmpty() || _message.length() > 300)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Player recipient = World.getInstance().getPlayer(_recipient);
		if (recipient == null || !RelationManager.getInstance().areFriends(player.getObjectId(), recipient.getObjectId()))
		{
			player.sendPacket(SystemMessageId.TARGET_IS_NOT_FOUND_IN_THE_GAME);
			return;
		}
		
		if (RelationManager.getInstance().isInBlockList(recipient, player))
		{
			player.sendPacket(new L2FriendSay(_recipient, player.getName(), _message, 620));
			return;
		}
		
		if (Config.LOG_CHAT)
		{
			LogRecord logRecord = new LogRecord(Level.INFO, _message);
			logRecord.setLoggerName("chat");
			logRecord.setParameters(new Object[]
			{
				"PRIV_MSG",
				"[" + player.getName() + " to " + _recipient + "]"
			});
			
			CHAT_LOG.log(logRecord);
		}
		
		recipient.sendPacket(new L2FriendSay(player.getName(), _recipient, _message, 0));
	}
}