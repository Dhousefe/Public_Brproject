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
package ext.mods.gameserver.taskmanager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.enums.GaugeColor;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SetupGauge;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.Formulas;

/**
 * Updates {@link Player} drown timer and reduces {@link Player} HP, when drowning.
 */
public final class WaterTaskManager implements Runnable
{
	private final Map<Player, Long> _players = new ConcurrentHashMap<>();
	
	protected WaterTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	@Override
	public final void run()
	{
		if (_players.isEmpty())
			return;
		
		final long time = System.currentTimeMillis();
		
		for (Map.Entry<Player, Long> entry : _players.entrySet())
		{
			if (time < entry.getValue())
				continue;
			
			final Player player = entry.getKey();
			
			final double hp = Formulas.calcDrownDamage(player);
			
			player.reduceCurrentHp(hp, player, false, false, null);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DROWN_DAMAGE_S1).addNumber((int) hp));
		}
	}
	
	/**
	 * Adds {@link Player} to the WaterTask.
	 * @param player : {@link Player} to be added and checked.
	 */
	public final void add(Player player)
	{
		if (!player.isDead() && !_players.containsKey(player))
		{
			final int time = (int) player.getStatus().calcStat(Stats.BREATH, 60000 * player.getRace().getBreathMultiplier(), player, null);
			
			_players.put(player, System.currentTimeMillis() + time);
			
			player.sendPacket(new SetupGauge(GaugeColor.CYAN, time));
		}
	}
	
	/**
	 * Removes {@link Player} from the WaterTask.
	 * @param player : Player to be removed.
	 */
	public final void remove(Player player)
	{
		if (_players.remove(player) != null)
			player.sendPacket(new SetupGauge(GaugeColor.CYAN, 0));
	}
	
	public static final WaterTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final WaterTaskManager INSTANCE = new WaterTaskManager();
	}
}