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
package ext.mods.gameserver.handler;

import ext.mods.commons.logging.CLogger;

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public interface IAdminCommandHandler
{
	final CLogger LOGGER = new CLogger(IAdminCommandHandler.class.getName());
	
	public static final int PAGE_LIMIT_1 = 1;
	public static final int PAGE_LIMIT_7 = 7;
	public static final int PAGE_LIMIT_8 = 8;
	public static final int PAGE_LIMIT_10 = 10;
	public static final int PAGE_LIMIT_12 = 12;
	public static final int PAGE_LIMIT_14 = 14;
	public static final int PAGE_LIMIT_15 = 15;
	public static final int PAGE_LIMIT_20 = 20;
	
	public void useAdminCommand(String command, Player player);
	
	public String[] getAdminCommandList();
	
	public default Player getTargetPlayer(Player player, String playerName, boolean defaultAdmin)
	{
		final Player toTest = World.getInstance().getPlayer(playerName);
		return (toTest == null) ? getTargetPlayer(player, defaultAdmin) : toTest;
	}
	
	public default Player getTargetPlayer(Player player, boolean defaultAdmin)
	{
		return getTarget(Player.class, player, defaultAdmin);
	}
	
	public default Creature getTargetCreature(Player player, boolean defaultAdmin)
	{
		return getTarget(Creature.class, player, defaultAdmin);
	}
	
	/**
	 * @param <A> : The {@link Class} to cast upon result.
	 * @param type : The {@link Class} type to check.
	 * @param player : The {@link Player} used to retrieve the target from.
	 * @param defaultAdmin : If true, we test the {@link Player} itself, in case target was invalid, otherwise we return null directly.
	 * @return The target of the {@link Player} set as parameter, under the given {@link Class} type. If the target isn't assignable to that {@link Class}, or if the defaultAdmin is set to true and the {@link Player} instance isn't assignable to that {@link Class} aswell, then return null.
	 */
	public default <A extends WorldObject> A getTarget(Class<A> type, Player player, boolean defaultAdmin)
	{
		final WorldObject target = player.getTarget();
		
		if (target == null || !type.isAssignableFrom(target.getClass()))
			return (defaultAdmin && type.isAssignableFrom(player.getClass())) ? type.cast(player) : null;
		
		return type.cast(target);
	}
	
	public default void sendFile(Player player, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/" + filename);
		player.sendPacket(html);
	}
}