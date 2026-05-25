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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.custom.data.PcCafeData;
import ext.mods.gameserver.enums.PcCafeConsumeType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.ExPCCafePointInfo;

public class PcCafeManager
{
	public static final CLogger LOGGER = new CLogger(PcCafeManager.class.getName());
	
	private static final String CLEAR_MEMO = "DELETE FROM character_memo WHERE var='cafe_points_today'";
	
	private static final String ENABLE = "enable";
	private static final String ENABLE_TIMER_BONUS = "enable_timer_bonus";
	private static final String TIMER_POINTS = "timer_points";
	private static final String DOUBLE_POINTS_PROBABILITY = "timer_double_points_probability";
	private static final String ENABLE_DAILY_BONUS = "enable_daily_bonus";
	private static final String DAILY_BONUS_POINTS = "daily_bonus_points";
	private static final String DAILY_POINTS = "cafe_points_today";
	private static final String ENABLE_FARMING_BONUS = "enable_farming_bonus";
	private static final String FARMING_BONUS_POINTS = "farming_bonus_points";
	private static final String FARMING_BONUS_LEVEL_DIFF = "farming_bonus_level_diff";
	private static final String ENABLE_PVP_BONUS = "enable_pvp_bonus";
	private static final String PVP_BONUS_POINTS = "pvp_bonus_points";
	
	protected PcCafeManager()
	{
		if (PcCafeData.getInstance().getCafeBool(ENABLE_TIMER_BONUS, false))
			ThreadPool.scheduleAtFixedRate(() -> rewardPoint(), TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(15));
	}
	
	public int enableEvent()
	{
		return PcCafeData.getInstance().getCafeInt(ENABLE, 1);
	}
	
	private boolean isEventEnabled()
	{
		return enableEvent() == 1;
	}
	
	private void rewardPoint()
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (isEventEnabled() && player != null && player.getClient() != null && !player.getClient().isDetached())
			{
				int timerPoints = PcCafeData.getInstance().getCafeInt(TIMER_POINTS, 10);
				boolean doublePoints = Rnd.get(100) < PcCafeData.getInstance().getCafeInt(DOUBLE_POINTS_PROBABILITY, 10);
				player.increasePcCafePoints(timerPoints, doublePoints);
			}
		}
	}
	
	public void onPlayerLogin(Player player)
	{
		if (isEventEnabled() && PcCafeData.getInstance().getCafeBool(ENABLE_DAILY_BONUS, false))
		{
			if (!player.getMemos().getBool(DAILY_POINTS, false))
			{
				player.getMemos().set(DAILY_POINTS, true);
				player.increasePcCafePoints(PcCafeData.getInstance().getCafeInt(DAILY_BONUS_POINTS, 1000));
			}
		}
		
		player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), 0, PcCafeConsumeType.NORMAL));
	}
	
	public void onAttackableKill(Player player)
	{
		if (isEventEnabled() && PcCafeData.getInstance().getCafeBool(ENABLE_FARMING_BONUS, false))
		{
			if (player == null)
				return;
			
			final Creature target = (Creature) player.getTarget();
			if (target == null)
				return;
			
			final int levelDiff = target.getStatus().getLevel() - player.getStatus().getLevel();
			final int maxLevelDiff = PcCafeData.getInstance().getCafeInt(FARMING_BONUS_LEVEL_DIFF, 11);
			if ((levelDiff > -maxLevelDiff) && (levelDiff < maxLevelDiff))
				player.increasePcCafePoints(PcCafeData.getInstance().getCafeInt(FARMING_BONUS_POINTS, 50));
		}
	}
	
	public void onReset()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(CLEAR_MEMO))
		{
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to reset daily login pc points.", e);
		}
		
		World.getInstance().getPlayers().stream().forEach(player -> player.getMemos().remove(DAILY_POINTS));
	}
	
	public void onPlayerPvPKill(Player player)
	{
		if (isEventEnabled() && PcCafeData.getInstance().getCafeBool(ENABLE_PVP_BONUS, false))
			player.increasePcCafePoints(PcCafeData.getInstance().getCafeInt(PVP_BONUS_POINTS, 100));
	}
	
	public static final PcCafeManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PcCafeManager INSTANCE = new PcCafeManager();
	}
}