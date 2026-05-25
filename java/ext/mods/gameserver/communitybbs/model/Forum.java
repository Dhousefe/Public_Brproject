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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.communitybbs.CommunityBoard;
import ext.mods.gameserver.enums.bbs.ForumAccess;
import ext.mods.gameserver.enums.bbs.ForumType;

public class Forum
{
	private static final CLogger LOGGER = new CLogger(Forum.class.getName());
	
	private static final String ADD_FORUM = "INSERT INTO bbs_forum (id,type,access,owner_id) VALUES (?,?,?,?)";
	
	private final Map<Integer, Topic> _topics = new ConcurrentHashMap<>();
	
	private final int _id;
	private ForumType _type;
	private ForumAccess _access;
	private int _ownerId;
	
	private int _lastTopicId = 0;
	
	public Forum(ForumType type, ForumAccess access, int ownerId)
	{
		_id = CommunityBoard.getInstance().getANewForumId();
		_type = type;
		_access = access;
		_ownerId = ownerId;
	}
	
	public Forum(ResultSet rs) throws SQLException
	{
		_id = rs.getInt("id");
		_type = Enum.valueOf(ForumType.class, rs.getString("type"));
		_access = Enum.valueOf(ForumAccess.class, rs.getString("access"));
		_ownerId = rs.getInt("owner_id");
	}
	
	public int getTopicSize()
	{
		return _topics.size();
	}
	
	public Topic getTopic(int id)
	{
		return _topics.get(id);
	}
	
	public Topic getTopicById(int forumId)
	{
		return _topics.values().stream().filter(t -> t.getId() == forumId).findFirst().orElse(null);
	}
	
	public void addTopic(Topic topic)
	{
		_topics.put(topic.getId(), topic);
		
		if (topic.getId() > getCurrentTopicId())
			_lastTopicId = topic.getId();
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public ForumType getType()
	{
		return _type;
	}
	
	public ForumAccess getAccess()
	{
		return _access;
	}
	
	public void removeTopic(int id)
	{
		_topics.remove(id);
	}
	
	public void insertIntoDb()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(ADD_FORUM))
		{
			ps.setInt(1, _id);
			ps.setString(2, _type.toString());
			ps.setString(3, _access.toString());
			ps.setInt(4, _ownerId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't save new Forum.", e);
		}
	}
	
	public synchronized int getANewTopicId()
	{
		return ++_lastTopicId;
	}
	
	public synchronized int getCurrentTopicId()
	{
		return _lastTopicId;
	}
}