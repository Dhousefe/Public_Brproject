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
package ext.mods.gameserver.communitybbs.custom;

import java.util.List;

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.xml.TeleportData;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.TeleportLocation;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ShowBoard;

public class GateKeeperBBSManager extends BaseBBSManager
{
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.equals("_bbsgetfav"))
			showPage(0, player);
		else if (command.startsWith("_bbsgetfav;page"))
		{
			String[] args = command.split(" ");
			if (args.length > 1)
				showPage(Integer.parseInt(args[1]), player);
		}
		else if (command.startsWith("_bbsgetfav;go"))
		{
			String[] args = command.split(" ");
			if (args.length > 1)
				teleport(player, Integer.parseInt(args[1]));
			
			showPage(0, player);
			
			player.sendPacket(new ShowBoard());
		}
		
	}
	
	private void showPage(int page, Player player)
	{
		String content;
		if (page > 0)
			content = HTMLData.getInstance().getHtm(player.getLocale(), String.format(CB_PATH + getFolder() + "50010-%d.htm", page));
		else
			content = HTMLData.getInstance().getHtm(player.getLocale(), CB_PATH + getFolder() + "50010.htm");
		
		separateAndSend(content, player);
	}
	
	/**
	 * Teleport the {@link Player} into the {@link Npc}'s {@link TeleportLocation}s {@link List} index.<br>
	 * <br>
	 * @param player : The {@link Player} to test.
	 * @param index : The {@link TeleportLocation} index information to retrieve from this {@link Npc}'s instant teleports {@link List}.
	 */
	protected void teleport(Player player, int index)
	{
		final List<TeleportLocation> teleports = TeleportData.getInstance().getTeleports(50010);
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
		
		if (Config.FREE_TELEPORT || player.getStatus().getLevel() <= Config.LVL_FREE_TELEPORT || teleport.getPriceCount() == 0 || player.destroyItemByItemId(teleport.getPriceId(), teleport.getPriceCount(), true))
			player.teleportTo(teleport, 20);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	protected String getFolder()
	{
		return "custom/gk/";
	}
	
	public static GateKeeperBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GateKeeperBBSManager INSTANCE = new GateKeeperBBSManager();
	}
}
