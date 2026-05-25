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
package ext.mods.gameserver.data.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.Duel;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * Loads and stores {@link Duel}s for easier management.
 */
public final class DuelManager
{
	private final Map<Integer, Duel> _duels = new ConcurrentHashMap<>();
	
	protected DuelManager()
	{
	}
	
	public Duel getDuel(int duelId)
	{
		return _duels.get(duelId);
	}
	
	/**
	 * Add a Duel on the _duels Map. Both {@link Player}s must exist.
	 * @param playerA : The first {@link Player} to use.
	 * @param playerB : The second {@link Player} to use.
	 * @param isPartyDuel : True if the duel is a party duel.
	 */
	public void addDuel(Player playerA, Player playerB, boolean isPartyDuel)
	{
		if (playerA == null || playerB == null)
			return;
		
		final int duelId = IdFactory.getInstance().getNextId();
		
		_duels.put(duelId, new Duel(playerA, playerB, isPartyDuel, duelId));
	}
	
	/**
	 * Remove the duel from the Map, and release the id.
	 * @param duelId : The id to remove.
	 */
	public void removeDuel(int duelId)
	{
		IdFactory.getInstance().releaseId(duelId);
		
		_duels.remove(duelId);
	}
	
	/**
	 * End the duel by a surrender action.
	 * @param player : The {@link Player} used to retrieve the duelId. The {@link Player} is then used as surrendered opponent.
	 */
	public void doSurrender(Player player)
	{
		if (player == null || !player.isInDuel())
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel != null)
			duel.doSurrender(player);
	}
	
	/**
	 * End the duel by a defeat action.
	 * @param player : The {@link Player} used to retrieve the duelId. The {@link Player} is then used as defeated opponent.
	 */
	public void onPlayerDefeat(Player player)
	{
		if (player == null || !player.isInDuel())
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel != null)
			duel.onPlayerDefeat(player);
	}
	
	/**
	 * Remove the {@link Player} set as parameter from duel, enforcing duel cancellation.
	 * @param player : The {@link Player} to check.
	 */
	public void onPartyEdit(Player player)
	{
		if (player == null || !player.isInDuel())
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel != null)
			duel.onPartyEdit();
	}
	
	/**
	 * Broadcast a packet to the team (or the {@link Player}) opposing the given {@link Player}.
	 * @param player : The {@link Player} used to find the opponent.
	 * @param packet : The {@link L2GameServerPacket} to send.
	 */
	public void broadcastToOppositeTeam(Player player, L2GameServerPacket packet)
	{
		if (player == null || !player.isInDuel())
			return;
		
		final Duel duel = getDuel(player.getDuelId());
		if (duel == null)
			return;
		
		final Player playerA = duel.getPlayerA();
		final Player playerB = duel.getPlayerB();
		
		if (playerA == player || (duel.isPartyDuel() && playerA.isInSameParty(player)))
			duel.broadcastTo(playerB, packet);
		else if (playerB == player || (duel.isPartyDuel() && playerB.isInSameParty(player)))
			duel.broadcastTo(playerA, packet);
	}
	
	public static final DuelManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DuelManager INSTANCE = new DuelManager();
	}
}