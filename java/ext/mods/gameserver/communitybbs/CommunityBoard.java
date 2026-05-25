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
package ext.mods.gameserver.communitybbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.communitybbs.manager.ClanBBSManager;
import ext.mods.gameserver.communitybbs.manager.FavoriteBBSManager;
import ext.mods.gameserver.communitybbs.manager.FriendsBBSManager;
import ext.mods.gameserver.communitybbs.manager.MailBBSManager;
import ext.mods.gameserver.communitybbs.manager.PostBBSManager;
import ext.mods.gameserver.communitybbs.manager.RegionBBSManager;
import ext.mods.gameserver.communitybbs.manager.TopBBSManager;
import ext.mods.gameserver.communitybbs.manager.TopicBBSManager;
import ext.mods.gameserver.communitybbs.model.Forum;
import ext.mods.gameserver.communitybbs.model.Post;
import ext.mods.gameserver.communitybbs.model.Topic;
import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.gameserver.enums.bbs.ForumAccess;
import ext.mods.gameserver.enums.bbs.ForumType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.GameClient;
import ext.mods.gameserver.network.SystemMessageId;

public class CommunityBoard
{
	private static final CLogger LOGGER = new CLogger(CommunityBoard.class.getName());
	
	private static final String SELECT_FORUMS = "SELECT * FROM bbs_forum";
	private static final String SELECT_TOPICS = "SELECT * FROM bbs_topic ORDER BY id DESC";
	private static final String SELECT_POSTS = "SELECT * FROM bbs_post ORDER BY id ASC";
	
	private final Set<Forum> _forums = ConcurrentHashMap.newKeySet();
	
	protected CommunityBoard()
	{
		if (!Config.ENABLE_COMMUNITY_BOARD)
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(SELECT_FORUMS);
			PreparedStatement ps2 = con.prepareStatement(SELECT_TOPICS);
			PreparedStatement ps3 = con.prepareStatement(SELECT_POSTS))
		{
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
					addForum(new Forum(rs));
			}
			
			try (ResultSet rs2 = ps2.executeQuery())
			{
				while (rs2.next())
				{
					final Forum forum = getForumByID(rs2.getInt("forum_id"));
					if (forum == null)
						return;
					
					forum.addTopic(new Topic(rs2));
				}
			}
			
			try (ResultSet rs3 = ps3.executeQuery())
			{
				while (rs3.next())
				{
					final Forum forum = getForumByID(rs3.getInt("forum_id"));
					if (forum == null)
						return;
					
					final Topic topic = forum.getTopic(rs3.getInt("topic_id"));
					if (topic == null)
						return;
					
					topic.addPost(new Post(rs3));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load forums.", e);
		}
		LOGGER.info("Loaded {} forums.", _forums.size());
	}
	
	public void handleCommands(GameClient client, String command)
	{
		final Player player = client.getPlayer();
		if (player == null)
			return;
		
		if (!Config.ENABLE_COMMUNITY_BOARD)
		{
			player.sendPacket(SystemMessageId.CB_OFFLINE);
			return;
		}
		
		if (command.startsWith("_bbshome"))
			TopBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsgetfav"))
			FavoriteBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsloc"))
			RegionBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsclan"))
			ClanBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmemo"))
			TopicBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmail") || command.equals("_maillist_0_1_0_"))
			MailBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_friend") || command.startsWith("_block"))
			FriendsBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbstopics"))
			TopicBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsposts"))
			PostBBSManager.getInstance().parseCmd(command, player);
		else if (command.startsWith("_bbsmultisell;"))
			MultisellData.getInstance().separateAndSendCb(command.substring(14).trim(), player, false);
		else
			BaseBBSManager.separateAndSend("<html><body><br><br><center>The command: " + command + " isn't implemented.</center></body></html>", player);
	}
	
	public void handleWriteCommands(GameClient client, String url, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		final Player player = client.getPlayer();
		if (player == null)
			return;
		
		if (!Config.ENABLE_COMMUNITY_BOARD)
		{
			player.sendPacket(SystemMessageId.CB_OFFLINE);
			return;
		}
		
		if (url.equals("Topic"))
			TopicBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("Post"))
			PostBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_bbsloc"))
			RegionBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_bbsclan"))
			ClanBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("Mail"))
			MailBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else if (url.equals("_friend"))
			FriendsBBSManager.getInstance().parseWrite(arg1, arg2, arg3, arg4, arg5, player);
		else
			BaseBBSManager.separateAndSend("<html><body><br><br><center>The command: " + url + " isn't implemented.</center></body></html>", player);
	}
	
	public void addForum(Forum forum)
	{
		if (forum == null)
			return;
		
		_forums.add(forum);
	}
	
	public int getANewForumId()
	{
		return _forums.stream().mapToInt(Forum::getId).max().orElse(0) + 1;
	}
	
	public Forum getForum(ForumType type, int ownerId)
	{
		return _forums.stream().filter(f -> f.getType() == type && f.getOwnerId() == ownerId).findFirst().orElse(null);
	}
	
	public Forum getForumByID(int id)
	{
		return _forums.stream().filter(f -> f.getId() == id).findFirst().orElse(null);
	}
	
	public Forum getOrCreateForum(ForumType type, ForumAccess access, int ownerId)
	{
		Forum forum = getForum(type, ownerId);
		if (forum != null)
			return forum;
		
		forum = new Forum(type, access, ownerId);
		forum.insertIntoDb();
		
		addForum(forum);
		
		return forum;
	}
	
	public static CommunityBoard getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CommunityBoard INSTANCE = new CommunityBoard();
	}
}