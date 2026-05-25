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
package ext.mods.gameserver.model.actor.instance;

import java.util.List;
import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.PlayerData;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.TutorialCloseHtml;
import ext.mods.gameserver.network.serverpackets.TutorialShowHtml;
import ext.mods.gameserver.network.serverpackets.TutorialShowQuestionMark;
import ext.mods.gameserver.network.serverpackets.UserInfo;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFManager;
import ext.mods.gameserver.model.entity.events.deathmatch.DMManager;
import ext.mods.gameserver.model.entity.events.lastman.LMManager;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTManager;
import ext.mods.tour.TournamentEvent;
import ext.mods.Crypta.RandomManager;

/**
 * Custom class allowing you to choose your class.<br>
 * <br>
 * You can customize class rewards as needed items. Check npc.properties for more informations.<br>
 * This NPC type got 2 differents ways to level:
 * <ul>
 * <li>the normal one, where you have to be at least of the good level.<br>
 * NOTE : you have to take 1st class then 2nd, if you try to take 2nd directly it won't work.</li>
 * <li>the "allow_entire_tree" version, where you can take class depending of your current path.<br>
 * NOTE : you don't need to be of the good level.</li>
 * </ul>
 * Added to the "change class" function, this NPC can noblesse and give available skills (related to your current class and level).
 */
public final class ClassMaster extends Folk
{
	public ClassMaster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		return "html/mods/classmaster/" + npcId + ((val == 0) ? "" : ("-" + val)) + ".htm";
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		if (command.startsWith("1stClass"))
			showHtmlMenu(player, getObjectId(), 1);
		else if (command.startsWith("2ndClass"))
			showHtmlMenu(player, getObjectId(), 2);
		else if (command.startsWith("3rdClass"))
			showHtmlMenu(player, getObjectId(), 3);
		else if (command.startsWith("change_class"))
		{
			final int val = Integer.parseInt(command.substring(13));
			
			if (checkAndChangeClass(player, val))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 4));
				html.replace("%name%", PlayerData.getInstance().getClassNameById(val));
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("become_noble"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (player.isNoble())
			{
				html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 5));
				player.sendPacket(html);
				return;
			}
			
			if (!player.destroyItemByItemId(Config.NOBLE_ITEM_ID, Config.NOBLE_ITEM_COUNT, true))
				return;
			
			html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 6));
			player.setNoble(true, true);
			player.sendPacket(new UserInfo(player));
			player.sendPacket(html);
		}
		else if (command.startsWith("learn_skills"))
			player.rewardSkills();
		else
			super.onBypassFeedback(player, command);
	}
	
	public static final void onTutorialLink(Player player, String request)
	{
		if (!Config.ALTERNATE_CLASS_MASTER || request == null || !request.startsWith("CO"))
			return;
		
		try
		{
			int val = Integer.parseInt(request.substring(2));
			checkAndChangeClass(player, val);
		}
		catch (NumberFormatException e)
		{
		}
		player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
	}
	
	public static final void onTutorialQuestionMark(Player player, int number)
	{
		if (ext.mods.levelupmaker.LevelUpMakerManager.getInstance().isEnabled() && number == ext.mods.levelupmaker.LevelUpMakerManager.getInstance().getQuestionMarkId())
		{
			String html = ext.mods.levelupmaker.LevelUpMakerManager.getInstance().getTutorialAlertHtml(player);
			if (html != null)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage(0);
				msg.setHtml(html);
				player.sendPacket(msg);
			}
			return;
		}
		if (number == TournamentEvent.TUTORIAL_QUESTION_MARK_ID)
		{
			String html = TournamentEvent.getTutorialAlertHtml(player);
			if (html != null)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage(0);
				msg.setHtml(html);
				player.sendPacket(msg);
			}
			return;
		}
		if (number == CTFManager.TUTORIAL_QUESTION_MARK_ID)
		{
			String html = CTFManager.getInstance().getTutorialAlertHtml(player);
			if (html != null)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage(0);
				msg.setHtml(html);
				player.sendPacket(msg);
			}
			return;
		}
		if (number == DMManager.TUTORIAL_QUESTION_MARK_ID)
		{
			String html = DMManager.getInstance().getTutorialAlertHtml(player);
			if (html != null)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage(0);
				msg.setHtml(html);
				player.sendPacket(msg);
			}
			return;
		}
		if (number == LMManager.TUTORIAL_QUESTION_MARK_ID)
		{
			String html = LMManager.getInstance().getTutorialAlertHtml(player);
			if (html != null)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage(0);
				msg.setHtml(html);
				player.sendPacket(msg);
			}
			return;
		}
		if (number == TvTManager.TUTORIAL_QUESTION_MARK_ID)
		{
			String html = TvTManager.getInstance().getTutorialAlertHtml(player);
			if (html != null)
			{
				NpcHtmlMessage msg = new NpcHtmlMessage(0);
				msg.setHtml(html);
				player.sendPacket(msg);
			}
			return;
		}
		for (ext.mods.gameserver.model.zone.type.RandomZone zone : RandomManager.getInstance().getActiveZones())
		{
			ext.mods.FarmEventRandom.holder.RandomZoneData zoneData = (ext.mods.FarmEventRandom.holder.RandomZoneData) RandomManager.getInstance().getZoneDataForZone(zone);
			Integer qmId = RandomManager.TUTORIAL_QUESTION_MARK_ID;
			if (zoneData != null && zoneData.isActive() && number == qmId)
			{
				String html = RandomManager.getInstance().getTutorialAlertHtml(player);
				if (html != null)
				{
					NpcHtmlMessage msg = new NpcHtmlMessage(0);
					msg.setHtml(html);
					player.sendPacket(msg);
				}
				return;
			}
		}
		if (!Config.ALTERNATE_CLASS_MASTER || number != 1001)
			return;
		
		showTutorialHtml(player);
	}
	
	public static final void showQuestionMark(Player player)
	{
		if (!Config.ALTERNATE_CLASS_MASTER)
			return;
		
		final ClassId classId = player.getClassId();
		if (getMinLevel(classId.getLevel()) > player.getStatus().getLevel())
			return;
		
		if (!Config.CLASS_MASTER_SETTINGS.isAllowed(classId.getLevel() + 1))
			return;
		
		player.sendPacket(new TutorialShowQuestionMark(1001));
	}
	
	private final void showHtmlMenu(Player player, int objectId, int level)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(objectId);
		
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
				html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 1));
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
							StringUtil.append(menu, "<a action=\"bypass -h npc_%objectId%_change_class ", cid.getId(), "\">", PlayerData.getInstance().getClassNameById(cid.getId()), "</a><br>");
					}
					
					if (menu.length() > 0)
					{
						html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 2));
						html.replace("%name%", PlayerData.getInstance().getClassNameById(currentClassId.getId()));
						html.replace("%menu%", menu.toString());
					}
					else
					{
						html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 3));
						html.replace("%level%", getMinLevel(level - 1));
					}
				}
				else
				{
					if (minLevel < Integer.MAX_VALUE)
					{
						html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 3));
						html.replace("%level%", minLevel);
					}
					else
						html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 1));
				}
			}
		}
		
		html.replace("%objectId%", objectId);
		html.replace("%req_items%", getRequiredItems(level));
		player.sendPacket(html);
	}
	
	private static final void showTutorialHtml(Player player)
	{
		final ClassId currentClassId = player.getClassId();
		if (getMinLevel(currentClassId.getLevel()) > player.getStatus().getLevel() && !Config.ALLOW_ENTIRE_TREE)
			return;
		
		String msg = HTMLData.getInstance().getHtm(player, "html/mods/classmaster/50000-7.htm");
		msg = msg.replaceAll("%name%", PlayerData.getInstance().getClassNameById(currentClassId.getId()));
		
		final StringBuilder menu = new StringBuilder(100);
		for (ClassId cid : ClassId.values())
		{
			if (validateClassId(currentClassId, cid))
				StringUtil.append(menu, "<a action=\"link CO", String.valueOf(cid.getId()), "\">", PlayerData.getInstance().getClassNameById(cid.getId()), "</a><br>");
		}
		
		msg = msg.replaceAll("%menu%", menu.toString());
		msg = msg.replace("%req_items%", getRequiredItems(currentClassId.getLevel() + 1));
		player.sendPacket(new TutorialShowHtml(msg));
	}
	
	private static final boolean checkAndChangeClass(Player player, int val)
	{
		final ClassId currentClassId = player.getClassId();
		if (getMinLevel(currentClassId.getLevel()) > player.getStatus().getLevel() && !Config.ALLOW_ENTIRE_TREE)
			return false;
		
		if (!validateClassId(currentClassId, val))
			return false;
		
		int newJobLevel = currentClassId.getLevel() + 1;
		
		if (player.getWeightPenalty().ordinal() > 2 && !Config.CLASS_MASTER_SETTINGS.getRewardItems(newJobLevel).isEmpty())
		{
			player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
			return false;
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
		
		if (Config.CLASS_MASTER_SETTINGS.isAllowed(player.getClassId().getLevel() + 1) && Config.ALTERNATE_CLASS_MASTER && (((player.getClassId().getLevel() == 1) && (player.getStatus().getLevel() >= 40)) || ((player.getClassId().getLevel() == 2) && (player.getStatus().getLevel() >= 76))))
			showQuestionMark(player);
		
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
		
		return Config.ALLOW_ENTIRE_TREE && newCID.isChildOf(oldCID);
	}
	
	private static String getRequiredItems(int level)
	{
		final List<IntIntHolder> neededItems = Config.CLASS_MASTER_SETTINGS.getRequiredItems(level);
		if (neededItems == null || neededItems.isEmpty())
			return "<tr><td>none</td></tr>";
		
		final StringBuilder sb = new StringBuilder();
		for (IntIntHolder item : neededItems)
			StringUtil.append(sb, "<tr><td><font color=\"LEVEL\">", item.getValue(), "</font></td><td>", ItemData.getInstance().getTemplate(item.getId()).getName(), "</td></tr>");
		
		return sb.toString();
	}
}