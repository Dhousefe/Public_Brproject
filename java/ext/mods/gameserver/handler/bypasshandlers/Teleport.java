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

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.xml.TeleportData;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.TeleportLocation;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;

public class Teleport implements IBypassHandler
{
	private static final String[] COMMANDS = { "teleport" };
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{		
		try
		{
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (target instanceof Npc npc)
				teleport(player, npc, Integer.parseInt(st.nextToken()));
		}
		catch (Exception e)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		return true;
	}
	
	/**
	 * Teleport the {@link Player} into the {@link Npc}'s {@link TeleportLocation}s {@link List} index.<br>
	 * <br>
	 * Following checks are done : {@link Npc#isTeleportAllowed(Player)}, castle siege, price.
	 * @param player : The {@link Player} to test.
	 * @param npc : The {@link Npc} from which the teleport locations are retrieved.
	 * @param index : The {@link TeleportLocation} index information to retrieve from this {@link Npc}'s instant teleports {@link List}.
	 */
	public void teleport(Player player, Npc npc, int index)
	{
		if (!npc.isTeleportAllowed(player))
			return;
		
		final List<TeleportLocation> teleports = TeleportData.getInstance().getTeleports(npc.getNpcId());
		if (teleports == null || index > teleports.size())
			return;
		
		final TeleportLocation teleport = teleports.get(index);
		if (teleport == null)
			return;
		
		if (teleport.getCastleId() > 0)
		{
			final Castle castle = CastleManager.getInstance().getCastleById(teleport.getCastleId());
			if (castle != null && castle.getSiege().isInProgress())
			{
				player.sendPacket(SystemMessageId.CANNOT_PORT_VILLAGE_IN_SIEGE);
				return;
			}
		}
		
		if (Config.FREE_TELEPORT && player.getStatus().getLevel() <= Config.LVL_FREE_TELEPORT || teleport.getPriceCount() == 0 || player.destroyItemByItemId(teleport.getPriceId(), teleport.getCalculatedPriceCount(player), true))
			player.teleportTo(teleport, 20);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}