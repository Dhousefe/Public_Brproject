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
package ext.mods.gameserver.handler.bypasshandlers;

import java.util.List;
import java.util.StringTokenizer;

import ext.mods.gameserver.data.xml.InstantTeleportData;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ActionFailed;

public class InstantTeleport implements IBypassHandler
{
	private static final String[] COMMANDS = { "instant_teleport" };
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{		
		try
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (target instanceof Npc npc)
				instantTeleport(player, npc, Integer.parseInt(st.nextToken()));
		}
		catch (Exception e)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		return true;
	}
	
	/**
	 * Teleport the {@link Player} into the {@link Npc}'s instant teleports {@link List} index.<br>
	 * <br>
	 * The only check is {@link Npc#isTeleportAllowed(Player)}.
	 * @param player : The {@link Player} to test.
	 * @param npc : The {@link Npc} from which the teleport locations are retrieved.
	 * @param index : The {@link Location} index information to retrieve from this {@link Npc}'s instant teleports {@link List}.
	 */
	public void instantTeleport(Player player, Npc npc, int index)
	{
		if (!npc.isTeleportAllowed(player))
			return;
		
		final List<Location> teleports = InstantTeleportData.getInstance().getTeleports(npc.getNpcId());
		if (teleports == null || index > teleports.size())
			return;
		
		final Location teleport = teleports.get(index);
		if (teleport == null)
			return;
		
		player.teleportTo(teleport, 20);
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}