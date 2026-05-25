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
package ext.mods.gameserver.handler.admincommandhandlers;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.PlayerData;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public class AdminSkill implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_show_skills",
		"admin_remove_skills",
		"admin_skill_list",
		"admin_skill_index",
		"admin_add_skill",
		"admin_remove_skill",
		"admin_give_all_skills",
		"admin_remove_all_skills",
		"admin_show_skills_by_class",
		"admin_learn_skill",
		"admin_clan_skill"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final Player targetPlayer = getTargetPlayer(player, true);
		
		if (command.equals("admin_show_skills"))
			showMainPage(player);
		else if (command.startsWith("admin_remove_skills"))
		{
			try
			{
				removeSkillsPage(player, Integer.parseInt(command.substring(20)));
			}
			catch (Exception e)
			{
				removeSkillsPage(player, 1);
			}
		}
		else if (command.startsWith("admin_skill_list"))
			sendFile(player, "skills.htm");
		else if (command.startsWith("admin_skill_index"))
		{
			try
			{
				String val = command.substring(18);
				sendFile(player, "skills/" + val + ".htm");
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("admin_add_skill"))
		{
			try
			{
				String val = command.substring(15);
				adminAddSkill(player, val);
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //add_skill <skill_id> <level>");
			}
		}
		else if (command.startsWith("admin_remove_skill"))
		{
			try
			{
				String id = command.substring(19);
				int idval = Integer.parseInt(id);
				adminRemoveSkill(player, idval);
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //remove_skill <skill_id>");
			}
		}
		else if (command.equals("admin_give_all_skills"))
			adminGiveAllSkills(player);
		else if (command.equals("admin_remove_all_skills"))
		{
			if (targetPlayer != null)
			{
				for (L2Skill skill : targetPlayer.getSkills().values())
					targetPlayer.removeSkill(skill.getId(), true);
				
				player.sendMessage("You removed all skills from " + targetPlayer.getName() + ".");
				if (targetPlayer != player)
					targetPlayer.sendMessage("Admin removed all skills from you.");
				
				targetPlayer.sendPacket(new SkillList(player));
				showMainPage(player);
			}
		}
		else if (command.startsWith("admin_learn_skill"))
		{
			try
			{
				String[] parts = command.split(" ");
				int skillId = Integer.parseInt(parts[1]);
				int skillLevel = Integer.parseInt(parts[2]);
				int classId = Integer.parseInt(parts[3]);
				int page = Integer.parseInt(parts[4]);
				
				adminLearnSkill(player, skillId, skillLevel, classId, page);
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //learn_skill <skill_id> <skill_level> <class_name> <page>");
			}
		}
		else if (command.startsWith("admin_show_skills_by_class"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				final int classId = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
				final int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
				
				showSkillsByClass(player, classId, page);
			}
			catch (Exception e)
			{
				player.sendMessage("Usage: //show_skills_by_class <class_name> [<page_number>]");
			}
		}
		else if (command.startsWith("admin_clan_skill"))
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			int page = 1;
			if (!targetPlayer.isClanLeader())
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_A_CLAN_LEADER).addCharName(targetPlayer));
				showMainPage(player);
				return;
			}
			
			final Clan clan = targetPlayer.getClan();
			
			if (!st.hasMoreTokens())
			{
				showClanSkillList(player, clan, page);
				return;
			}
			
			final String param = st.nextToken();
			if (StringUtil.isDigit(param))
				page = Integer.parseInt(param);
			else
			{
				switch (param)
				{
					case "set":
						try
						{
							final String param2 = st.nextToken();
							if (param2.equals("all"))
							{
								if (clan.addAllClanSkills())
									player.sendMessage("You gave all available skills to " + clan.getName() + " clan.");
							}
							else
							{
								final int id = Integer.parseInt(param2);
								final int level = Integer.parseInt(st.nextToken());
								
								if (id < 370 || id > 391 || level < 1 || level > 3)
								{
									player.sendMessage("Usage: //clan_skill set id level [page]");
									showClanSkillList(player, clan, page);
									return;
								}
								
								final L2Skill skill = SkillTable.getInstance().getInfo(id, level);
								if (skill == null)
								{
									player.sendMessage("Usage: //clan_skill set id level [page]");
									showClanSkillList(player, clan, page);
									return;
								}
								
								if (clan.addClanSkill(skill, false))
									player.sendMessage("You gave " + skill.getName() + " skill to " + clan.getName() + " clan.");
							}
						}
						catch (Exception e)
						{
							player.sendMessage("Usage: //clan_skill set id level [page]");
							
						}
						break;
					
					case "remove":
						try
						{
							final String param2 = st.nextToken();
							if (param2.equals("all"))
							{
								if (clan.removeAllClanSkills())
									player.sendMessage("You removed all skills from " + clan.getName() + " clan.");
							}
							else
							{
								final int skillId = Integer.parseInt(param2);
								
								if (clan.removeClanSkill(skillId))
									player.sendMessage("You removed " + skillId + " skillId from " + clan.getName() + " clan.");
							}
						}
						catch (Exception e)
						{
							player.sendMessage("Usage: //clan_skill remove id|all [page]");
						}
						break;
				}
				
				if (st.hasMoreTokens())
				{
					final String param3 = st.nextToken();
					if (StringUtil.isDigit(param3))
						page = Integer.parseInt(param3);
				}
			}
			
			showClanSkillList(player, clan, page);
		}
		
		return;
	}
	
	/**
	 * This function will give all the skills that the target can learn at his/her level
	 * @param player The GM char.
	 */
	private void adminGiveAllSkills(Player player)
	{
		final Player targetPlayer = getTargetPlayer(player, true);
		if (targetPlayer != null)
		{
			targetPlayer.rewardSkills();
			player.sendMessage("You gave all available skills to " + targetPlayer.getName() + ".");
		}
	}
	
	private void removeSkillsPage(Player player, int page)
	{
		final Player targetPlayer = getTargetPlayer(player, true);
		if (targetPlayer == null)
		{
			player.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			return;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		final Pagination<L2Skill> list = new Pagination<>(targetPlayer.getSkills().values().stream(), page, PAGE_LIMIT_10);
		
		list.append("<html><body><table width=270><tr>");
		list.append("<td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		list.append("<td width=180><center>Delete Skills Menu</center></td>");
		list.append("<td width=45><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		list.append("</tr></table><br><br><center>Editing <font color=\"LEVEL\">", targetPlayer.getName(), "</font>, ");
		list.append(targetPlayer.getTemplate().getClassName(), " lvl ", targetPlayer.getStatus().getLevel(), ".<br>");
		
		list.generatePages("bypass -h admin_remove_skills %page%");
		
		list.append("<table width=270><tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
		
		for (L2Skill skill : list)
		{
			list.append("<tr>");
			list.append("<td width=80><a action=\"bypass -h admin_remove_skill ", skill.getId(), "\">", skill.getName(), "</a></td>");
			list.append("<td width=60>", skill.getLevel(), "</td>");
			list.append("<td width=40>", skill.getId(), "</td>");
			list.append("</tr>");
		}
		
		list.append("</table><br><center><table width=200><tr>");
		list.append("<td width=50 align=right>Id: </td>");
		list.append("<td><edit var=\"id_to_remove\" width=55></td>");
		list.append("<td width=100><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
		list.append("</tr><tr>");
		list.append("<td></td><td></td><td><button value=\"Back to stats\" action=\"bypass -h admin_debug\" width=95 height=21 back=\"bigbutton_over\" fore=\"bigbutton\"></td>");
		list.append("</tr></table></center></body></html>");
		
		html.setHtml(list.getContent());
		player.sendPacket(html);
	}
	
	private void showMainPage(Player player)
	{
		final Player targetPlayer = getTargetPlayer(player, true);
		if (targetPlayer == null)
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(targetPlayer.getLocale(), "html/admin/charskills.htm");
		html.replace("%name%", targetPlayer.getName());
		html.replace("%level%", targetPlayer.getStatus().getLevel());
		html.replace("%class%", targetPlayer.getTemplate().getClassName());
		player.sendPacket(html);
	}
	
	private void adminAddSkill(Player player, String val)
	{
		final Player targetPlayer = getTargetPlayer(player, true);
		if (targetPlayer == null)
		{
			showMainPage(player);
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		StringTokenizer st = new StringTokenizer(val);
		if (st.countTokens() != 2)
			showMainPage(player);
		else
		{
			L2Skill skill = null;
			try
			{
				String id = st.nextToken();
				String level = st.nextToken();
				int idval = Integer.parseInt(id);
				int levelval = Integer.parseInt(level);
				skill = SkillTable.getInstance().getInfo(idval, levelval);
			}
			catch (Exception e)
			{
			}
			
			if (skill != null)
			{
				String name = skill.getName();
				
				targetPlayer.addSkill(skill, true, true);
				targetPlayer.sendMessage("Admin gave you the skill " + name + ".");
				if (targetPlayer != player)
					player.sendMessage("You gave the skill " + name + " to " + targetPlayer.getName() + ".");
				
				targetPlayer.sendPacket(new SkillList(player));
			}
			else
				player.sendMessage("Error: there is no such skill.");
			
			showMainPage(player);
		}
	}
	
	private void adminRemoveSkill(Player player, int idval)
	{
		final Player targetPlayer = getTargetPlayer(player, true);
		if (targetPlayer == null)
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		final L2Skill skill = targetPlayer.removeSkill(idval, true);
		if (skill == null)
			player.sendMessage("Error: there is no such skill.");
		else
		{
			player.sendMessage("You removed the skill " + skill.getName() + " from " + targetPlayer.getName() + ".");
			if (targetPlayer != player)
				targetPlayer.sendMessage("Admin removed the skill " + skill.getName() + " from your skills list.");
			
			targetPlayer.sendPacket(new SkillList(player));
		}
		
		removeSkillsPage(player, 1);
	}
	
	private void showSkillsByClass(Player player, int classId, int page)
	{
		PlayerData playerData = PlayerData.getInstance();
		List<L2Skill> skills = playerData.getSkillsByClassId(classId);
		
		if (skills == null || skills.isEmpty())
		{
			player.sendMessage("No skills found for class: " + classId);
			return;
		}
		
		Player targetPlayer = getTargetPlayer(player, true);
		if (targetPlayer == null)
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		List<L2Skill> filteredSkills = skills.stream().filter(skill ->
		{
			L2Skill existingSkill = targetPlayer.getSkills().get(skill.getId());
			return existingSkill == null || existingSkill.getLevel() < skill.getLevel();
		}).toList();
		
		Pagination<L2Skill> list = new Pagination<>(filteredSkills.stream(), page, PAGE_LIMIT_15);
		
		list.append("<html><body>");
		list.append("<table width=270><tr>");
		list.append("<td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		list.append("<td width=180 align=center>Skills for ", playerData.getClassNameById(classId), "</td>");
		list.append("<td width=45><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
		list.append("</tr></table><br><br>");
		
		list.generatePages("bypass -h admin_show_skills_by_class " + classId + " %page%");
		list.append("<table width=300><tr><td width=32>Icon: </td><td width=80>Name:</td><td width=30>Id:</td></tr>");
		
		for (L2Skill skill : list)
		{
			list.append("<tr>");
			list.append("<td width=32><button action=\"bypass admin_learn_skill ", skill.getId(), " ", skill.getLevel(), " ", classId, " ", page, " ", "\" width=32 height=32 back=\"", skill.getIcon(), "\" fore=\"", skill.getIcon(), "\" /></td>");
			list.append("<td width=160>", "<a action=\"bypass -h admin_learn_skill ", skill.getId(), " ", skill.getLevel(), " ", classId, " ", page, "\">", skill.getName(), " Lvl. ", skill.getLevel(), "</a></td>");
			list.append("<td width=30>", skill.getId(), "</td>");
			list.append("</tr>");
		}
		
		list.append("</table>");
		list.generatePages("bypass -h admin_show_skills_by_class " + classId + " %page%");
		list.append("</body></html>");
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setHtml(list.getContent());
		player.sendPacket(html);
	}
	
	private void adminLearnSkill(Player player, int skillId, int skillLevel, int classId, int page)
	{
		Player targetPlayer = getTargetPlayer(player, true);
		if (targetPlayer == null)
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
		if (skill == null)
		{
			player.sendMessage("Error: there is no such skill.");
			return;
		}
		
		targetPlayer.addSkill(skill, true, true);
		targetPlayer.sendPacket(new SkillList(targetPlayer));
		targetPlayer.sendMessage("Admin gave you the skill " + skill.getName() + " " + skill.getLevel() + " lvl.");
		
		showSkillsByClass(player, classId, page);
	}
	
	private static void showClanSkillList(Player player, Clan targetClan, int page)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), "html/admin/clan_skills.htm");
		html.replace("%name%", targetClan.getName());
		
		final Pagination<L2Skill> list = new Pagination<>(Arrays.stream(SkillTable.getClanSkills()), page, PAGE_LIMIT_15);
		list.append("<table width=270><tr><td width=220>Name</td><td width=20>Lvl</td><td width=30>Id</td></tr>");
		
		for (L2Skill skill : list)
		{
			final L2Skill currentSkill = targetClan.getClanSkills().get(skill.getId());
			if (currentSkill == null)
				list.append("<tr><td>", skill.getName(), "</td><td>", skill.getLevel(), "</td><td>", skill.getId(), "</td></tr>");
			else
				list.append("<tr><td><a action=\"bypass -h admin_clan_skill remove ", skill.getId(), "\">", skill.getName(), "</a>", "</td><td>", currentSkill.getLevel(), "</td><td>", skill.getId(), "</td></tr>");
		}
		list.append("</table><br>");
		
		list.generateSpace();
		list.generatePages("bypass admin_clan_skill %page%");
		
		html.replace("%content%", list.getContent());
		player.sendPacket(html);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}