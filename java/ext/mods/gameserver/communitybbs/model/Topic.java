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
package ext.mods.gameserver.communitybbs.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

public class Topic
{
	private static final CLogger LOGGER = new CLogger(Topic.class.getName());
	
	private static final String INSERT_TOPIC = "INSERT INTO bbs_topic (id,forum_id,name,date,owner_name,owner_id) VALUES (?,?,?,?,?,?)";
	private static final String DELETE_TOPIC = "DELETE FROM bbs_topic WHERE id=? AND forum_id=?";
	private static final String INSERT_POST = "INSERT INTO bbs_post (id,owner_name,owner_id,date,topic_id,forum_id,txt) values (?,?,?,?,?,?,?)";
	private static final String DELETE_POST = "DELETE FROM bbs_post WHERE forum_id=? AND topic_id=?";
	
	private final List<Post> _posts = new ArrayList<>();
	
	private final int _id;
	private final int _forumId;
	private final String _name;
	private final long _date;
	private final String _ownerName;
	private final int _ownerId;
	
	public Topic(Forum forum, String name, String ownerName, int ownerId, String text)
	{
		final long date = System.currentTimeMillis();
		
		_id = forum.getANewTopicId();
		_forumId = forum.getId();
		_name = name;
		_date = date;
		_ownerName = ownerName;
		_ownerId = ownerId;
		
		_posts.add(new Post(0, ownerName, ownerId, date, _id, _forumId, text));
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(INSERT_TOPIC);
			PreparedStatement ps2 = con.prepareStatement(INSERT_POST))
		{
			ps.setInt(1, _id);
			ps.setInt(2, _forumId);
			ps.setString(3, _name);
			ps.setLong(4, _date);
			ps.setString(5, _ownerName);
			ps.setInt(6, _ownerId);
			ps.execute();
			
			ps2.setInt(1, 0);
			ps2.setString(2, ownerName);
			ps2.setInt(3, ownerId);
			ps2.setLong(4, date);
			ps2.setInt(5, _id);
			ps2.setInt(6, _forumId);
			ps2.setString(7, text);
			ps2.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save new Topic.", e);
		}
	}
	
	public Topic(ResultSet rs) throws SQLException
	{
		_id = rs.getInt("id");
		_forumId = rs.getInt("forum_id");
		_name = rs.getString("name");
		_date = rs.getLong("date");
		_ownerName = rs.getString("owner_name");
		_ownerId = rs.getInt("owner_id");
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getForumId()
	{
		return _forumId;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getOwnerName()
	{
		return _ownerName;
	}
	
	public long getDate()
	{
		return _date;
	}
	
	/**
	 * Delete this {@link Topic} and associated {@link Post}s from memory and database.
	 * @param forum : The associated {@link Forum}.
	 */
	public void deleteMe(Forum forum)
	{
		forum.removeTopic(_id);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_TOPIC);
			PreparedStatement ps2 = con.prepareStatement(DELETE_POST))
		{
			ps.setInt(1, _id);
			ps.setInt(2, forum.getId());
			ps.execute();
			
			ps2.setInt(1, forum.getId());
			ps2.setInt(2, _id);
			ps2.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete topic.", e);
		}
		_posts.clear();
	}
	
	public Post getPost(int id)
	{
		return _posts.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
	}
	
	public void addPost(Post post)
	{
		_posts.add(post);
	}
}