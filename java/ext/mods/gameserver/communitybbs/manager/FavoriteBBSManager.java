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
package ext.mods.gameserver.communitybbs.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.communitybbs.model.Favorite;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.Player;

public class FavoriteBBSManager extends BaseBBSManager
{
	private static final String SELECT_FAVORITES = "SELECT * FROM bbs_favorite ORDER BY id ASC";
	private static final String INSERT_FAVORITE = "INSERT INTO bbs_favorite (id,player_id,title,bypass,date) VALUES (?,?,?,?,?)";
	private static final String DELETE_FAVORITE = "DELETE FROM bbs_favorite WHERE id=?";
	
	private final Map<Integer, Set<Favorite>> _favorites = new ConcurrentHashMap<>();
	
	private int _lastFavoriteId = 0;
	
	protected FavoriteBBSManager()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_FAVORITES);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				getFavorites(rs.getInt("player_id")).add(new Favorite(rs));
				
				final int favoriteId = rs.getInt("id");
				if (favoriteId > _lastFavoriteId)
					_lastFavoriteId = favoriteId;
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load favorites.", e);
		}
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.equals("_bbsgetfav"))
		{
			String content = HTMLData.getInstance().getHtm(player, CB_PATH + "favorite/favorite-get.htm");
			
			final Set<Favorite> favorites = _favorites.get(player.getObjectId());
			if (favorites == null)
				content = content.replace("<?FAV_LIST?>", "");
			else
			{
				final StringBuilder sb = new StringBuilder();
				for (Favorite favorite : favorites)
				{
					String template = HTMLData.getInstance().getHtm(player, CB_PATH + "favorite/template.htm");
					template = template.replace("<?sDate?>", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(favorite.getDate()));
					template = template.replace("<?fav_id?>", Integer.toString(favorite.getId()));
					template = template.replace("<?bypass?>", favorite.getBypass());
					template = template.replace("<?arg_last?>", favorite.getTitle());
					
					sb.append(template);
				}
				content = content.replace("<?FAV_LIST?>", sb.toString());
			}
			separateAndSend(content, player);
		}
		else if (command.startsWith("_bbsgetfav_add"))
		{
			/* 
			 * @formatter:off TODO
			 * 
			final String storedFav = player.getMemos().get("add_fav");
			if (storedFav == null)
				return;
			
			player.getMemos().remove("add_fav");
			
			* @formatter:on
			*/
			
			final String storedFav = "Testing favorites&_bbshome";
			
			final String[] params = storedFav.split("&");
			if (params.length > 1)
			{
				final int id = getNewFavoriteId();
				final int playerId = player.getObjectId();
				final String title = params[0];
				final String bypass = params[1];
				final Timestamp date = new Timestamp(System.currentTimeMillis());
				
				getFavorites(playerId).add(new Favorite(id, playerId, title, bypass, date));
				
				try (Connection con = ConnectionPool.getConnection();
					PreparedStatement ps = con.prepareStatement(INSERT_FAVORITE))
				{
					ps.setInt(1, id);
					ps.setInt(2, playerId);
					ps.setString(3, title);
					ps.setString(4, bypass);
					ps.setTimestamp(5, date);
					ps.execute();
				}
				catch (Exception e)
				{
					LOGGER.error("Couldn't add the favorite.", e);
				}
			}
			
			parseCmd("_bbsgetfav", player);
		}
		else if (command.startsWith("_bbsgetfav_del"))
		{
			final StringTokenizer st = new StringTokenizer(command, "_");
			st.nextToken();
			st.nextToken();
			
			final int id = Integer.parseInt(st.nextToken());
			
			if (getFavorites(player.getObjectId()).removeIf(f -> f.getId() == id))
			{
				try (Connection con = ConnectionPool.getConnection();
					PreparedStatement ps = con.prepareStatement(DELETE_FAVORITE))
				{
					ps.setInt(1, id);
					ps.execute();
				}
				catch (Exception e)
				{
					LOGGER.error("Couldn't delete favorite #{}.", e, id);
				}
			}
			
			parseCmd("_bbsgetfav", player);
		}
		else
			super.parseCmd(command, player);
	}
	
	@Override
	protected String getFolder()
	{
		return "favorite/";
	}
	
	private synchronized int getNewFavoriteId()
	{
		return ++_lastFavoriteId;
	}
	
	private Set<Favorite> getFavorites(int objectId)
	{
		return _favorites.computeIfAbsent(objectId, f -> ConcurrentHashMap.newKeySet());
	}
	
	public static FavoriteBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FavoriteBBSManager INSTANCE = new FavoriteBBSManager();
	}
}