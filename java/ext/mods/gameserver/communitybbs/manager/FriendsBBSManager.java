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

import java.util.Set;
import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.FriendList;

public class FriendsBBSManager extends BaseBBSManager
{
	private static final String FRIENDLIST_DELETE_BUTTON = "<br>\n<table><tr><td width=10></td><td>Are you sure you want to delete all friends from your Friends List?</td><td width=20></td><td><button value=\"OK\" action=\"bypass _friend;delall\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"></td></tr></table>";
	private static final String BLOCKLIST_DELETE_BUTTON = "<br>\n<table><tr><td width=10></td><td>Are you sure you want to delete all players from your Block List?</td><td width=20></td><td><button value=\"OK\" action=\"bypass _block;delall\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"></td></tr></table>";
	
	protected FriendsBBSManager()
	{
	}
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.startsWith("_friendlist"))
			showFriendsList(player, false);
		else if (command.startsWith("_blocklist"))
			showBlockList(player, false);
		else if (command.startsWith("_friend"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			String action = st.nextToken();
			
			if (action.equals("select"))
			{
				player.selectFriend((st.hasMoreTokens()) ? Integer.valueOf(st.nextToken()) : 0);
				showFriendsList(player, false);
			}
			else if (action.equals("deselect"))
			{
				player.deselectFriend((st.hasMoreTokens()) ? Integer.valueOf(st.nextToken()) : 0);
				showFriendsList(player, false);
			}
			else if (action.equals("delall"))
			{
				RelationManager.getInstance().removeAllFromFriendList(player);
				player.getSelectedFriendList().clear();
				
				showFriendsList(player, false);
				
				player.sendMessage("You have cleared your friends list.");
				player.sendPacket(new FriendList(player));
			}
			else if (action.equals("delconfirm"))
				showFriendsList(player, true);
			else if (action.equals("del"))
			{
				for (int friendId : player.getSelectedFriendList())
				{
					final Player friend = World.getInstance().getPlayer(friendId);
					if (friend != null)
					{
						RelationManager.getInstance().removeFromFriendList(friend, player.getObjectId());
						friend.sendPacket(new FriendList(friend));
					}
					
					RelationManager.getInstance().removeFromFriendList(player, friendId);
				}
				
				player.getSelectedFriendList().clear();
				showFriendsList(player, false);
				
				player.sendPacket(new FriendList(player));
			}
			else if (action.equals("mail"))
			{
				if (!player.getSelectedFriendList().isEmpty())
					showMailWrite(player);
			}
		}
		else if (command.startsWith("_block"))
		{
			StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			String action = st.nextToken();
			
			boolean delMsg = false;
			
			if (action.equals("select"))
				player.selectBlock((st.hasMoreTokens()) ? Integer.valueOf(st.nextToken()) : 0);
			else if (action.equals("deselect"))
				player.deselectBlock((st.hasMoreTokens()) ? Integer.valueOf(st.nextToken()) : 0);
			else if (action.equals("delall"))
			{
				RelationManager.getInstance().removeAllFromBlockList(player);
				player.getSelectedBlocksList().clear();
			}
			else if (action.equals("delconfirm"))
				delMsg = true;
			else if (action.equals("del"))
			{
				for (int blockId : player.getSelectedBlocksList())
					RelationManager.getInstance().removeFromBlockList(player, blockId);
				
				player.getSelectedBlocksList().clear();
			}
			showBlockList(player, delMsg);
		}
		else
			super.parseCmd(command, player);
	}
	
	@Override
	public void parseWrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player player)
	{
		if (ar1.equalsIgnoreCase("mail"))
		{
			MailBBSManager.getInstance().sendMail(ar2, ar4, ar5, player);
			showFriendsList(player, false);
		}
		else
			super.parseWrite(ar1, ar2, ar3, ar4, ar5, player);
	}
	
	@Override
	protected String getFolder()
	{
		return "friend/";
	}
	
	private static void showFriendsList(Player player, boolean delMsg)
	{
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "friend/friend-list.htm");
		if (content == null)
			return;
		
		final Set<Integer> list = RelationManager.getInstance().getFriendList(player.getObjectId());
		final Set<Integer> selectedList = player.getSelectedFriendList();
		
		final StringBuilder sb = new StringBuilder();
		
		for (Integer id : list)
		{
			if (selectedList.contains(id))
				continue;
			
			final String friendName = PlayerInfoTable.getInstance().getPlayerName(id);
			if (friendName == null)
				continue;
			
			final Player friend = World.getInstance().getPlayer(id);
			StringUtil.append(sb, "<a action=\"bypass _friend;select;", id, "\">[Select]</a>&nbsp;", friendName, " ", ((friend != null && friend.isOnline()) ? "(on)" : "(off)"), "<br1>");
		}
		content = content.replace("%friendslist%", sb.toString());
		
		sb.setLength(0);
		
		for (Integer id : selectedList)
		{
			final String friendName = PlayerInfoTable.getInstance().getPlayerName(id);
			if (friendName == null)
				continue;
			
			final Player friend = World.getInstance().getPlayer(id);
			StringUtil.append(sb, "<a action=\"bypass _friend;deselect;", id, "\">[Deselect]</a>&nbsp;", friendName, " ", ((friend != null && friend.isOnline()) ? "(on)" : "(off)"), "<br1>");
		}
		content = content.replace("%selectedFriendsList%", sb.toString());
		
		content = content.replace("%deleteMSG%", (delMsg) ? FRIENDLIST_DELETE_BUTTON : "");
		
		separateAndSend(content, player);
	}
	
	private static void showBlockList(Player player, boolean delMsg)
	{
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "friend/friend-blocklist.htm");
		if (content == null)
			return;
		
		final Set<Integer> list = RelationManager.getInstance().getBlockList(player.getObjectId());
		final Set<Integer> selectedList = player.getSelectedBlocksList();
		
		final StringBuilder sb = new StringBuilder();
		
		for (Integer id : list)
		{
			if (selectedList.contains(id))
				continue;
			
			final String blockName = PlayerInfoTable.getInstance().getPlayerName(id);
			if (blockName == null)
				continue;
			
			final Player block = World.getInstance().getPlayer(id);
			StringUtil.append(sb, "<a action=\"bypass _block;select;", id, "\">[Select]</a>&nbsp;", blockName, " ", ((block != null && block.isOnline()) ? "(on)" : "(off)"), "<br1>");
		}
		content = content.replace("%blocklist%", sb.toString());
		
		sb.setLength(0);
		
		for (Integer id : selectedList)
		{
			final String blockName = PlayerInfoTable.getInstance().getPlayerName(id);
			if (blockName == null)
				continue;
			
			final Player block = World.getInstance().getPlayer(id);
			StringUtil.append(sb, "<a action=\"bypass _block;deselect;", id, "\">[Deselect]</a>&nbsp;", blockName, " ", ((block != null && block.isOnline()) ? "(on)" : "(off)"), "<br1>");
		}
		content = content.replace("%selectedBlocksList%", sb.toString());
		
		content = content.replace("%deleteMSG%", (delMsg) ? BLOCKLIST_DELETE_BUTTON : "");
		
		separateAndSend(content, player);
	}
	
	public static final void showMailWrite(Player player)
	{
		String content = HTMLData.getInstance().getHtm(player, CB_PATH + "friend/friend-mail.htm");
		if (content == null)
			return;
		
		final StringBuilder sb = new StringBuilder();
		for (int id : player.getSelectedFriendList())
		{
			String friendName = PlayerInfoTable.getInstance().getPlayerName(id);
			if (friendName == null)
				continue;
			
			if (sb.length() > 0)
				sb.append(";");
			
			sb.append(friendName);
		}
		
		content = content.replace("%list%", sb.toString());
		
		separateAndSend(content, player);
	}
	
	public static FriendsBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FriendsBBSManager INSTANCE = new FriendsBBSManager();
	}
}