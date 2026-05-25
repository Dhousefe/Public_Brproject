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
package ext.mods.loginserver.network.gameserverpackets;

import ext.mods.commons.network.AttributeType;
import ext.mods.commons.network.ServerType;

import ext.mods.loginserver.data.manager.GameServerManager;
import ext.mods.loginserver.model.GameServerInfo;
import ext.mods.loginserver.network.clientpackets.ClientBasePacket;

public class ServerStatus extends ClientBasePacket
{
	private static final int ON = 0x01;
	
	public ServerStatus(byte[] decrypt, int serverId)
	{
		super(decrypt);
		
		GameServerInfo gsi = GameServerManager.getInstance().getRegisteredGameServers().get(serverId);
		if (gsi != null)
		{
			int size = readD();
			for (int i = 0; i < size; i++)
			{
				int type = readD();
				int value = readD();
				
				switch (AttributeType.VALUES[type])
				{
					case STATUS:
						gsi.setType(ServerType.VALUES[value]);
						break;
					
					case CLOCK:
						gsi.setShowingClock(value == ON);
						break;
					
					case BRACKETS:
						gsi.setShowingBrackets(value == ON);
						break;
					
					case AGE_LIMIT:
						gsi.setAgeLimit(value);
						break;
					
					case TEST_SERVER:
						gsi.setTestServer(value == ON);
						break;
					
					case PVP_SERVER:
						gsi.setPvp(value == ON);
						break;
					
					case MAX_PLAYERS:
						gsi.setMaxPlayers(value);
						break;
				}
			}
		}
	}
}