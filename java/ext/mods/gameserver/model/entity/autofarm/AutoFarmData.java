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
package ext.mods.gameserver.model.entity.autofarm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmArea;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmRoute;
import ext.mods.gameserver.model.entity.autofarm.zone.AutoFarmZone;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;

public class AutoFarmData
{
	private static final CLogger LOGGER = new CLogger(AutoFarmManager.class.getName());
	
	private static final String LOAD_AREAS = "SELECT * FROM autofarm_areas WHERE player_id = ?";
	private static final String LOAD_NODES = "SELECT * FROM autofarm_nodes WHERE area_id IN (%s) ORDER BY node_id";
	private static final String INSERT_AREA = "INSERT INTO autofarm_areas (player_id, area_id, name,type) VALUES (?,?,?,?)";
	private static final String INSERT_NODES = "INSERT INTO autofarm_nodes (node_id, area_id, loc_x, loc_y, loc_z) VALUES (?,?,?,?,?)";
	private static final String DELETE_AREA = "DELETE FROM autofarm_areas WHERE player_id = ? AND area_id = ?";
	private static final String DELETE_NODES = "DELETE FROM autofarm_nodes WHERE area_id = ?";
	
	private static final String LOAD_SKILLS = "SELECT * FROM autofarm_skills WHERE player_id = ?";
	private static final String DELETE_SKILLS = "DELETE FROM autofarm_skills WHERE player_id = ?";
	private static final String INSERT_SKILL = "INSERT INTO autofarm_skills (player_id, skill_id, slot) VALUES (?,?,?)";
	
	private static final String LOAD_TIME_USAGE = "SELECT time_used FROM autofarm_player_data WHERE player_id = ?";
	private static final String UPDATE_TIME_USAGE = "INSERT INTO autofarm_player_data (player_id, time_used) VALUES (?, ?) ON DUPLICATE KEY UPDATE time_used = ?";
	
	public void restorePlayer(Player player)
	{
		restoreAreas(player);
		
		restoreSkills(player);
	}
	
	private void restoreSkills(Player player)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_SKILLS))
		{
			ps.setInt(1, player.getObjectId());
			
			try (ResultSet rset = ps.executeQuery())
			{
				final AutoFarmProfile profile = AutoFarmManager.getInstance().getProfile(player);
				profile.getSkills().clear();
				
				while (rset.next())
				{
					int skillId = rset.getInt("skill_id");
					int slot = rset.getInt("slot");
					profile.getSkills().put(slot, skillId);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("AutoFarm: Erro ao restaurar skills do player {}.", e, player.getName());
		}
	}
	
	public void storeSkills(Player player)
	{
		final AutoFarmProfile profile = AutoFarmManager.getInstance().getProfile(player);
		if (profile == null) return;

		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement psDel = con.prepareStatement(DELETE_SKILLS))
			{
				psDel.setInt(1, player.getObjectId());
				psDel.execute();
			}
			
			if (!profile.getSkills().isEmpty())
			{
				try (PreparedStatement psIns = con.prepareStatement(INSERT_SKILL))
				{
					for (Map.Entry<Integer, Integer> entry : profile.getSkills().entrySet())
					{
						psIns.setInt(1, player.getObjectId());
						psIns.setInt(2, entry.getValue());
						psIns.setInt(3, entry.getKey());
						psIns.addBatch();
					}
					psIns.executeBatch();
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("AutoFarm: Erro ao salvar skills do player {}.", e, player.getName());
		}
	}

	private void restoreAreas(Player player)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps1 = con.prepareStatement(LOAD_AREAS))
		{
			ps1.setInt(1, player.getObjectId());
			
			try (ResultSet rset = ps1.executeQuery())
			{
				while (rset.next())
				{
					final AutoFarmProfile profile = AutoFarmManager.getInstance().getProfile(player);
					final AutoFarmType type = AutoFarmType.valueOf(rset.getString("type"));
					final int areaId = rset.getInt("area_id");
					
					if (type == AutoFarmType.ZONA)
					{
						profile.getAreas().put(areaId, new AutoFarmZone(areaId, rset.getString("name"), rset.getInt("player_id")));
					}
					else if (type == AutoFarmType.ROTA)
					{
						profile.getAreas().put(areaId, new AutoFarmRoute(areaId, rset.getString("name"), rset.getInt("player_id")));
					}
				}
			}

			if (AutoFarmManager.getInstance().getPlayer(player.getObjectId()) == null)
				return;
			
			final List<Integer> areaIds = AutoFarmManager.getInstance().getProfile(player).getAreas().values().stream().map(AutoFarmArea::getId).toList();
			if (areaIds.isEmpty())
				return;
			
			final AutoFarmProfile profile = AutoFarmManager.getInstance().getProfile(player);
			final String placeholders = areaIds.stream().map(id -> "?").collect(Collectors.joining(","));
			
			try (PreparedStatement ps2 = con.prepareStatement(String.format(LOAD_NODES, placeholders)))
			{
				for (int i = 0; i < areaIds.size(); i++)
					ps2.setInt(i + 1, areaIds.get(i));
				
				try (ResultSet rset = ps2.executeQuery())
				{
					while (rset.next())
					{
						profile.getAreaById(rset.getInt("area_id")).getNodes().add(new Location(rset.getInt("loc_x"), rset.getInt("loc_y"), rset.getInt("loc_z")));
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Não foi possível restaurar as AutoFarmArea do player {}.", e, player.getName());
		}
	}
	
	public void deleteArea(int playerId, int areaId)
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps1 = con.prepareStatement(DELETE_AREA))
			{
				ps1.setInt(1, playerId);
				ps1.setInt(2, areaId);
				ps1.execute();
			}
			
			try (PreparedStatement ps2 = con.prepareStatement(DELETE_NODES))
			{
				ps2.setInt(1, areaId);
				ps2.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Não foi possível deletar a AutoFarmArea id #{}.", e, areaId);
		}	
	}
	
	public void insertNodes(AutoFarmArea area)
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps1 = con.prepareStatement(DELETE_NODES))
			{
				ps1.setInt(1, area.getId());
				ps1.execute();
			}
			
			try (PreparedStatement ps2 = con.prepareStatement(INSERT_NODES))
			{
				int indice = 0;
				for (Location loc : area.getNodes())
				{
					ps2.setInt(1, indice);
					ps2.setInt(2, area.getId());
					ps2.setInt(3, loc.getX());
					ps2.setInt(4, loc.getY());
					ps2.setInt(5, loc.getZ());
					ps2.addBatch();
					indice++;
				}
				ps2.executeBatch();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Não foi possível salvar os nodes da AutoFarmArea id #{}.", e, area.getId());
		}
	}
	
	public void insertArea(int playerId, AutoFarmArea area)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_AREA))
		{
			ps.setInt(1, playerId);
			ps.setInt(2, area.getId());
			ps.setString(3, area.getName());
			ps.setString(4, area.getType().name());
			ps.execute();
			
			area.setIsFromDb();
		}
		catch (Exception e)
		{
			LOGGER.error("Não foi possível salvar a AutoFarmArea id #{}.", e, area.getId());
		}
	}
	
	public void addExtraTime(int objectId, long extraTime) {
    }

    public long getExtraTime(int objectId) {
        return 0; 
    }

    public void updatePlayerTimeUsage(int objectId, long timeUsed) {
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(UPDATE_TIME_USAGE)) {
            ps.setInt(1, objectId);
            ps.setLong(2, timeUsed);
            ps.setLong(3, timeUsed);
            ps.execute();
        } catch (Exception e) {
            LOGGER.error("Erro ao salvar tempo de autofarm: " + e.getMessage(), e);
        }
    }

    public long loadPlayerTimeUsage(int objectId) {
        try (Connection con = ConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(LOAD_TIME_USAGE)) {
            ps.setInt(1, objectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("time_used");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Erro ao carregar tempo de autofarm: " + e.getMessage(), e);
        }
        return 0;
    }
	
	public static final AutoFarmData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final AutoFarmData INSTANCE = new AutoFarmData();
	}
}