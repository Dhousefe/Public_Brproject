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
package ext.mods.gameserver.communitybbs.custom;

import java.util.List;
import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.PlayerData;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.UserInfo;

public class ClassMasterBBSManager extends BaseBBSManager
{
	@Override
	public void parseCmd(String command, Player player)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (command.equals("_bbsmemo"))
			showPage("index", player);
		if (command.startsWith("_bbsmemo;page"))
		{
			String[] args = command.split(" ");
			if (args.length > 1)
				showPage(args[1], player);
		}
		if (command.startsWith("_bbsmemo;1stClass"))
			showHtmlMenu(player, player.getObjectId(), 1);
		else if (command.startsWith("_bbsmemo;2ndClass"))
			showHtmlMenu(player, player.getObjectId(), 2);
		else if (command.startsWith("_bbsmemo;3rdClass"))
			showHtmlMenu(player, player.getObjectId(), 3);
		else if (command.startsWith("_bbsmemo;change_class"))
		{
			showPage("index", player);
			int val = Integer.parseInt(st.nextToken());
			
			if (checkAndChangeClass(player, val))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
				html.setFile(player.getLocale(), CB_PATH + getFolder() + "ok.htm");
				html.replace("%name%", PlayerData.getInstance().getClassNameById(val));
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("_bbsmemo;become_noble"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(player.getObjectId());
			
			showPage("index", player);
			
			if (player.isNoble())
			{
				html.setFile(player.getLocale(), CB_PATH + getFolder() + "alreadynoble.htm");
				player.sendPacket(html);
				return;
			}
			
			if (!player.destroyItemByItemId(Config.NOBLE_ITEM_ID, Config.NOBLE_ITEM_COUNT, true))
				return;
			
			html.setFile(player.getLocale(), CB_PATH + getFolder() + "nobleok.htm");
			player.setNoble(true, true);
			player.sendPacket(new UserInfo(player));
			player.sendPacket(html);
		}
		else if (command.startsWith("_bbsmemo;learn_skills"))
		{
			showPage("index", player);
			player.rewardSkills();
		}
	}
	
	private final void showHtmlMenu(Player player, int objectId, int level)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(objectId);
		
		showPage("index", player);
		
		if (!Config.CLASS_MASTER_SETTINGS.isAllowed(level))
		{
			final StringBuilder sb = new StringBuilder(100);
			sb.append("<html><body>");
			
			switch (player.getClassId().getLevel())
			{
				case 0:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(1))
						sb.append("Come back here when you reached level 20 to change your class.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(2))
						sb.append("Come back after your first occupation change.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back after your second occupation change.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 1:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(2))
						sb.append("Come back here when you reached level 40 to change your class.<br>");
					else if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back after your second occupation change.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 2:
					if (Config.CLASS_MASTER_SETTINGS.isAllowed(3))
						sb.append("Come back here when you reached level 76 to change your class.<br>");
					else
						sb.append("I can't change your occupation.<br>");
					break;
				
				case 3:
					sb.append("There is no class change available for you anymore.<br>");
					break;
			}
			sb.append("</body></html>");
			html.setHtml(sb.toString());
		}
		else
		{
			final ClassId currentClassId = player.getClassId();
			if (currentClassId.getLevel() >= level)
				html.setFile(player.getLocale(), CB_PATH + getFolder() + "nomore.htm");
			else
			{
				final int minLevel = getMinLevel(currentClassId.getLevel());
				if (player.getStatus().getLevel() >= minLevel || Config.ALLOW_ENTIRE_TREE)
				{
					final StringBuilder menu = new StringBuilder(100);
					for (ClassId cid : ClassId.VALUES)
					{
						if (cid.getLevel() != level)
							continue;
						
						if (validateClassId(currentClassId, cid))
							StringUtil.append(menu, "<a action=\"bypass -h _bbsmemo;change_class ", cid.getId(), "\">", PlayerData.getInstance().getClassNameById(cid.getId()), "</a><br>");
					}
					
					if (menu.length() > 0)
					{
						html.setFile(player.getLocale(), CB_PATH + getFolder() + "template.htm");
						html.replace("%name%", PlayerData.getInstance().getClassNameById(currentClassId.getId()));
						html.replace("%menu%", menu.toString());
					}
					else
					{
						html.setFile(player.getLocale(), CB_PATH + getFolder() + "comebacklater.htm");
						html.replace("%level%", getMinLevel(level - 1));
					}
				}
				else
				{
					if (minLevel < Integer.MAX_VALUE)
					{
						html.setFile(player.getLocale(), CB_PATH + getFolder() + "comebacklater.htm");
						html.replace("%level%", minLevel);
					}
					else
						html.setFile(player.getLocale(), CB_PATH + getFolder() + "nomore.htm");
				}
			}
		}
		
		html.replace("%objectId%", objectId);
		html.replace("%req_items%", getRequiredItems(level));
		player.sendPacket(html);
	}
	
	private static final boolean checkAndChangeClass(Player player, int val)
	{
		final ClassId currentClassId = player.getClassId();
		if (getMinLevel(currentClassId.getLevel()) > player.getStatus().getLevel() && !Config.ALLOW_ENTIRE_TREE)
			return false;
		
		if (!validateClassId(currentClassId, val))
			return false;
		
		int newJobLevel = currentClassId.getLevel() + 1;
		
		if (!Config.CLASS_MASTER_SETTINGS.getRewardItems(newJobLevel).isEmpty())
		{
			if (player.getWeightPenalty().ordinal() > 2)
			{
				player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
				return false;
			}
		}
		
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(newJobLevel);
		
		for (IntIntHolder item : neededItems)
		{
			if (player.getInventory().getItemCount(item.getId()) < item.getValue())
			{
				player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
				return false;
			}
		}
		
		for (IntIntHolder item : neededItems)
		{
			if (!player.destroyItemByItemId(item.getId(), item.getValue(), true))
				return false;
		}
		
		for (IntIntHolder item : Config.CLASS_MASTER_SETTINGS.getRewardItems(newJobLevel))
			player.addItem(item.getId(), item.getValue(), true);
		
		player.setClassId(val);
		
		if (player.isSubClassActive())
			player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
		else
			player.setBaseClass(player.getActiveClass());
		
		player.refreshHennaList();
		player.broadcastUserInfo();
		return true;
	}
	
	/**
	 * @param level - current skillId level (0 - start, 1 - first, etc)
	 * @return minimum player level required for next class transfer
	 */
	private static final int getMinLevel(int level)
	{
		switch (level)
		{
			case 0:
				return 20;
			case 1:
				return 40;
			case 2:
				return 76;
			default:
				return Integer.MAX_VALUE;
		}
	}
	
	/**
	 * Returns true if class change is possible
	 * @param oldCID current player ClassId
	 * @param val new class index
	 * @return
	 */
	private static final boolean validateClassId(ClassId oldCID, int val)
	{
		try
		{
			return validateClassId(oldCID, ClassId.VALUES[val]);
		}
		catch (Exception e)
		{
		}
		return false;
	}
	
	/**
	 * Returns true if class change is possible
	 * @param oldCID current player ClassId
	 * @param newCID new ClassId
	 * @return true if class change is possible
	 */
	private static final boolean validateClassId(ClassId oldCID, ClassId newCID)
	{
		if (newCID == null)
			return false;
		
		if (oldCID == newCID.getParent())
			return true;
		
		if (Config.ALLOW_ENTIRE_TREE && newCID.isChildOf(oldCID))
			return true;
		
		return false;
	}
	
	private static String getRequiredItems(int level)
	{
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(level);
		if (neededItems == null || neededItems.isEmpty())
			return "<tr><td>none</td></r>";
		
		final StringBuilder sb = new StringBuilder();
		for (IntIntHolder item : neededItems)
			StringUtil.append(sb, "<tr><td><font color=\"LEVEL\">", item.getValue(), "</font></td><td>", ItemData.getInstance().getTemplate(item.getId()).getName(), "</td></tr>");
		
		return sb.toString();
	}
	
	@Override
	public void parseWrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player player)
	{
		if (player == null)
			return;
	}
	
	private void showPage(String page, Player player)
	{
		String content = HTMLData.getInstance().getHtm(player.getLocale(), String.format(CB_PATH + getFolder() + "%s.htm", page));
		content = content.replace("%name%", player.getName());
		separateAndSend(content, player);
	}
	
	@Override
	protected String getFolder()
	{
		return "custom/classmaster/";
	}
	
	public static ClassMasterBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClassMasterBBSManager INSTANCE = new ClassMasterBBSManager();
	}
}