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
package ext.mods.gameserver.model.entity;

import java.util.List;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.enums.skills.AbnormalEffect;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;

public class Events
{
	protected static final CLogger LOGGER = new CLogger(Events.class.getName());
	
	protected static void closeDoors(List<Integer> doors)
	{
		for (int doorId : doors)
		{
			Door door = DoorData.getInstance().getDoor(doorId);
			
			if (door != null)
				door.closeMe();
		}
	}
	
	protected static void openDoors(List<Integer> doors)
	{
		for (int doorId : doors)
		{
			Door door = DoorData.getInstance().getDoor(doorId);
			
			if (door != null)
				door.openMe();
		}
	}
	
	public static void spawnProtection(Player player)
	{
		player.startAbnormalEffect(AbnormalEffect.HOLD_2);
		player.setIsParalyzed(true);
		player.sendMessage(player.getSysString(10_175));
		
		ThreadPool.schedule(() ->
		{
			player.setIsParalyzed(false);
			player.stopAbnormalEffect(AbnormalEffect.HOLD_2);
			player.sendPacket(new ExShowScreenMessage("FIGHT!", 3000));
			player.sendMessage(player.getSysString(10_011));
		}, 15000);
	}
}