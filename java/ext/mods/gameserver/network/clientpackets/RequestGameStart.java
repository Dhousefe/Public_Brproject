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

import java.util.Locale;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.AntiFeedManager;
import ext.mods.gameserver.enums.FloodProtector;
import ext.mods.gameserver.model.CharSelectSlot;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.GameClient;
import ext.mods.gameserver.network.GameClient.GameClientState;
import ext.mods.gameserver.network.serverpackets.CharSelected;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SSQInfo;
import ext.mods.protection.hwid.hwid;

public class RequestGameStart extends L2GameClientPacket
{
	private int _slot;
	
	@Override
	protected void readImpl()
	{
		_slot = readD();
		readH();
		readD();
		readD();
		readD();
	}
	
	@Override
	protected void runImpl()
	{
		final GameClient client = getClient();
		if (!client.performAction(FloodProtector.CHARACTER_SELECT))
			return;
		
		if ((Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddClient(AntiFeedManager.GAME_ID, client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP))
		{
			final NpcHtmlMessage msg = new NpcHtmlMessage(0);
			msg.setFile(Locale.forLanguageTag("en-US"), "html/mods/IPRestriction.htm");
			msg.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(client, Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP)));
			client.sendPacket(msg);
			return;
		}
		
		if (client.getActiveCharLock().tryLock())
		{
			try
			{
				if (client.getPlayer() == null)
				{
					final CharSelectSlot info = client.getCharSelectSlot(_slot);
					if (info == null || info.getAccessLevel() < 0)
						return;
					
					final Player player = client.loadCharFromDisk(_slot);
					if (player == null)
						return;
					
					player.setClient(client);
					client.setPlayer(player);
					player.setOnlineStatus(true, true);
					player.getStatus().setHpMp(info.getCurrentHp(), info.getCurrentMp());
					player.getStatus().setCp(info.getMaxHp());
					
					if (hwid.isProtectionOn() && !hwid.checkPlayerWithHWID(getClient(), player.getObjectId(), player.getName()))
						return;

					
					sendPacket(SSQInfo.sendSky());
					
					client.setState(GameClientState.ENTERING);
					
					sendPacket(new CharSelected(player, client.getSessionId().playOkID1));
				}
			}
			finally
			{
				client.getActiveCharLock().unlock();
			}
		}
	}
}