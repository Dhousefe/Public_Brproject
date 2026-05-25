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
package ext.mods.gameserver.model.entity.events.teamvsteam;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;

public class TvTAntiAFK
{
	private final ConcurrentHashMap<String, PlayerInfo> _player = new ConcurrentHashMap<>();
	
	private TvTAntiAFK()
	{
		ThreadPool.scheduleAtFixedRate(() -> checkPlayers(), 20000, 20000);
	}
	
	private void checkPlayers()
	{
		if (TvTEvent.getInstance().isStarted())
			Arrays.stream(TvTEvent.getInstance()._teams).flatMap(team -> team.getParticipatedPlayers().values().stream()).filter(player -> player != null && player.isOnline() && !player.isDead() && !player.isImmobilized() && !player.isParalyzed()).forEach(player -> addTvTSpawnInfo(player, player.getName(), player.getX(), player.getY(), player.getZ()));
		else
			_player.clear();
	}
	
	private void addTvTSpawnInfo(Player player, String name, int x, int y, int z)
	{
		_player.compute(name, (key, playerInfo) ->
		{
			if (playerInfo == null)
				return new PlayerInfo(x, y, z, 1);
			else
			{
				if (playerInfo.isSameLocation(x, y, z) && !player.getAttack().isAttackingNow() && !player.getCast().isCastingNow())
				{
					if (playerInfo.incrementAndGetSameLoc() >= 4)
					{
						TvTEvent.getInstance().onLogout(player);
						kickedMsg(player);
						return null;
					}
				}
				else
					return new PlayerInfo(x, y, z, 1);
				return playerInfo;
			}
		});
	}
	
	private void kickedMsg(Player player)
	{
		player.sendPacket(new ExShowScreenMessage("You're kicked out of the TvT by staying afk!", 6000));
	}
	
	private static class PlayerInfo
	{
		private int _x, _y, _z, _sameLoc;
		
		public PlayerInfo(int x, int y, int z, int sameLoc)
		{
			_x = x;
			_y = y;
			_z = z;
			_sameLoc = sameLoc;
		}
		
		public boolean isSameLocation(int x, int y, int z)
		{
			return _x == x && _y == y && _z == z;
		}
		
		public int incrementAndGetSameLoc()
		{
			return ++_sameLoc;
		}
	}
	
	public static final TvTAntiAFK getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final TvTAntiAFK INSTANCE = new TvTAntiAFK();
	}
}