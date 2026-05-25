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
package ext.mods.gameserver.scripting.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.UserInfo;
import ext.mods.gameserver.scripting.ScheduledQuest;

public final class RecommendationUpdate extends ScheduledQuest
{
	private static final String TRUNCATE_RECOMMENDS = "TRUNCATE character_recommends";
	private static final String SELECT_RECOMMENDS = "SELECT obj_Id, level, rec_have FROM characters";
	private static final String UPDATE_RECOMMENDS = "UPDATE characters SET rec_left=?, rec_have=? WHERE obj_Id=?";
	
	public RecommendationUpdate()
	{
		super(-1, "task");
	}
	
	@Override
	public final void onStart()
	{
		for (Player player : World.getInstance().getPlayers())
		{
			player.getRecomChars().clear();
			
			final int level = player.getStatus().getLevel();
			if (level < 20)
			{
				player.setRecomLeft(3);
				player.editRecomHave(-1);
			}
			else if (level < 40)
			{
				player.setRecomLeft(6);
				player.editRecomHave(-2);
			}
			else
			{
				player.setRecomLeft(9);
				player.editRecomHave(-3);
			}
			
			player.sendPacket(new UserInfo(player));
		}
		
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(TRUNCATE_RECOMMENDS))
			{
				ps.execute();
			}
			
			try (PreparedStatement ps2 = con.prepareStatement(UPDATE_RECOMMENDS))
			{
				try (PreparedStatement ps = con.prepareStatement(SELECT_RECOMMENDS))
				{
					try (ResultSet rs = ps.executeQuery())
					{
						while (rs.next())
						{
							final int level = rs.getInt("level");
							if (level < 20)
							{
								ps2.setInt(1, 3);
								ps2.setInt(2, Math.max(0, rs.getInt("rec_have") - 1));
							}
							else if (level < 40)
							{
								ps2.setInt(1, 6);
								ps2.setInt(2, Math.max(0, rs.getInt("rec_have") - 2));
							}
							else
							{
								ps2.setInt(1, 9);
								ps2.setInt(2, Math.max(0, rs.getInt("rec_have") - 3));
							}
							ps2.setInt(3, rs.getInt("obj_Id"));
							ps2.addBatch();
						}
					}
				}
				ps2.executeBatch();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't clear players recommendations.", e);
		}
	}
	
	@Override
	public final void onEnd()
	{
	}
}