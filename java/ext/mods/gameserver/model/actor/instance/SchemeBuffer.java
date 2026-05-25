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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.math.MathUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.BufferManager;
import ext.mods.gameserver.data.manager.BufferManager.BufferSchemeType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.records.BuffSkill;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.skills.L2Skill;

public class SchemeBuffer extends Folk
{
	private static final int PAGE_LIMIT = 6;
	
	private final Map<Integer, Boolean> _isPetTarget = new HashMap<>();
	
	public SchemeBuffer(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		Creature target = player;
		if (_isPetTarget.getOrDefault(player.getObjectId(), false) && player.getSummon() != null)
			target = player.getSummon();
		
		switch (command)
		{
			case "premium":
				if (player.getPremiumService() == 1)
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(0);
					html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 4));
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
				else
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(0);
					html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 8));
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
				break;
			
			case "heal":
				if (target == player)
					player.getStatus().setMaxCpHpMp();
				else
					target.getStatus().setMaxHpMp();
				
				showPage(player);
				break;
			case "cleanse":
				target.stopAllEffectsDebuff();
				showPage(player);
				break;
			case "support":
				showGiveBuffsWindow(player);
				break;
			case "cleanup":
				target.stopAllEffectsExceptThoseThatLastThroughDeath();
				showPage(player);
				break;
		}
		
		if (command.startsWith("menu"))
		{
			Boolean isPet = _isPetTarget.getOrDefault(player.getObjectId(), false);
			if (isPet && player.getSummon() == null)
				_isPetTarget.put(player.getObjectId(), false);
			else if (player.getSummon() != null)
			{
				_isPetTarget.put(player.getObjectId(), !isPet);
				target = _isPetTarget.get(player.getObjectId()) ? player.getSummon() : player;
			}
			else
			{
				player.sendMessage(player.getSysString(10_201));
				_isPetTarget.put(player.getObjectId(), false);
				target = player;
			}
			
			showPage(player);
		}
		else if (command.startsWith("getscheme"))
		{
			final List<L2Skill> schemes = BufferManager.getInstance().getSchemeSkills(BufferSchemeType.valueOf(st.nextToken().toUpperCase()));
			for (L2Skill scheme : schemes)
			{
				List<L2Skill> list = new ArrayList<>();
				list.add(SkillTable.getInstance().getInfo(scheme.getId(), scheme.getLevel()));
				int cost = getFee(list);
				if (cost == 0 || player.reduceAdena(cost, true))
					list.forEach(buffId -> getEffect(player, buffId));
			}
			showPage(player);
		}
		else if (command.startsWith("getbuff"))
		{
			final int skillId = Integer.parseInt(st.nextToken());
			final int skillLevel = Integer.parseInt(st.nextToken());
			List<L2Skill> list = new ArrayList<>();
			list.add(SkillTable.getInstance().getInfo(skillId, skillLevel));
			int cost = getFee(list);
			if (cost == 0 || player.reduceAdena(cost, true))
				list.forEach(buffId -> getEffect(player, buffId));
		}
		else if (command.startsWith("singlebuff"))
			showSingleBuffSelectionWindow(player, st.nextToken(), Integer.parseInt(st.nextToken()));
		else if (command.startsWith("givebuffs"))
		{
			final String schemeName = st.nextToken();
			final int cost = Integer.parseInt(st.nextToken());
			
			Creature targets = null;
			if (st.hasMoreTokens())
			{
				final String targetType = st.nextToken();
				if (targetType != null && targetType.equalsIgnoreCase("pet"))
					targets = player.getSummon();
			}
			else
				targets = player;
			
			if (targets == null)
				player.sendMessage("You don't have a pet.");
			else if (cost == 0 || player.reduceAdena(cost, true))
				BufferManager.getInstance().applySchemeEffects(this, targets, player.getObjectId(), schemeName);
		}
		else if (command.startsWith("editschemes"))
			showEditSchemeWindow(player, st.nextToken(), st.nextToken(), Integer.parseInt(st.nextToken()));
		else if (command.startsWith("buff"))
		{
			int buffIds = 0, buffLevel = 1, nextWindow = 0;
			if (st.countTokens() == 3)
			{
				buffIds = Integer.valueOf(st.nextToken());
				buffLevel = Integer.valueOf(st.nextToken());
				nextWindow = Integer.valueOf(st.nextToken());
			}
			else if (st.countTokens() == 2)
			{
				buffIds = Integer.valueOf(st.nextToken());
				nextWindow = Integer.valueOf(st.nextToken());
			}
			else if (st.countTokens() == 1)
				nextWindow = Integer.valueOf(st.nextToken());
			
			List<L2Skill> list = new ArrayList<>();

			final L2Skill skill = SkillTable.getInstance().getInfo(buffIds, buffLevel);
			final BuffSkill buff = BufferManager.getInstance().getAvailableBuff(skill);
			if (buff == null)
				LOGGER.info("bypass hack detected player npc [{}], skill id [{}]", player.getName(), skill.getId());
			else
			{
				list.add(SkillTable.getInstance().getInfo(buffIds, buffLevel));
				final int cost = getFee(list);
				if (buffIds != 0)
				{
					if ("premium".equals(buff.type()))
					{
						if (player.getPremiumService() == 0)
						{
							LOGGER.info("bypass hack detected player npc not premium [{}]", player.getName());
							showChatWindow(player, nextWindow);
							return;
						}
					}
					
					if (cost == 0 || player.reduceAdena(cost, true))
					{
						if (skill != null)
							skill.getEffectsNpc(target, target);
						
						showChatWindow(player, nextWindow);
						return;
					}
				}
			}
		}
		else if (command.startsWith("skill"))
		{
			final String groupType = st.nextToken();
			final String schemeName = st.nextToken();
			
			final int skillId = Integer.parseInt(st.nextToken());
			final int skillLevel = Integer.parseInt(st.nextToken());
			final int page = Integer.parseInt(st.nextToken());
			
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
			final List<L2Skill> skills = BufferManager.getInstance().getScheme(player.getObjectId(), schemeName);
			premiumSkills(player, skills);
			if (command.startsWith("skillselect") && !schemeName.equalsIgnoreCase("none"))
			{
				if (skills.size() < player.getMaxBuffCount())
					skills.add(skill);
				else
					player.sendMessage(player.getSysString(10_180));
			}
			else if (command.startsWith("skillunselect"))
				skills.remove(skill);
			
			showEditSchemeWindow(player, groupType, schemeName, page);
		}
		else if (command.startsWith("createscheme"))
		{
			try
			{
				final String schemeName = st.nextToken();
				if (schemeName.length() > 14)
				{
					player.sendMessage(player.getSysString(10_181));
					return;
				}
				
				final Map<String, ArrayList<L2Skill>> schemes = BufferManager.getInstance().getPlayerSchemes(player.getObjectId());
				if (schemes != null)
				{
					if (schemes.size() == Config.BUFFER_MAX_SCHEMES)
					{
						player.sendMessage(player.getSysString(10_182));
						return;
					}
					
					if (schemes.containsKey(schemeName))
					{
						player.sendMessage(player.getSysString(10_183));
						return;
					}
				}
				
				BufferManager.getInstance().setScheme(player.getObjectId(), schemeName.trim(), new ArrayList<>());
				showGiveBuffsWindow(player);
			}
			catch (Exception e)
			{
				player.sendMessage(player.getSysString(10_181));
			}
		}
		else if (command.startsWith("deletescheme"))
		{
			try
			{
				final String schemeName = st.nextToken();
				final Map<String, ArrayList<L2Skill>> schemes = BufferManager.getInstance().getPlayerSchemes(player.getObjectId());
				
				if (schemes != null && schemes.containsKey(schemeName))
					schemes.remove(schemeName);
			}
			catch (Exception e)
			{
				player.sendMessage(player.getSysString(10_184));
			}
			showGiveBuffsWindow(player);
		}
		
		super.onBypassFeedback(player, command);
	}
	
	private void getEffect(Player player, L2Skill buff)
	{
		Creature target = player;
		if (_isPetTarget.getOrDefault(player.getObjectId(), false) && player.getSummon() != null)
			target = player.getSummon();
		
		buff.getEffectsNpc(this, target);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return player.getKarma() > 0 ? "html/mods/buffer/" + filename + "-pk.htm" : "html/mods/buffer/" + filename + ".htm";
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), val));
		
		if (player.getSummon() == null)
			_isPetTarget.put(player.getObjectId(), false);
		
		Boolean isPet = _isPetTarget.getOrDefault(player.getObjectId(), false);
		html.replace("%target%", isPet ? "Summon" : "Player");
		html.replace("%objectId%", getObjectId());
		
		player.sendPacket(html);
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void showPage(Player player)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 0));
		Boolean isPet = _isPetTarget.getOrDefault(player.getObjectId(), false);
		html.replace("%target%", isPet ? "Summon" : "Player");
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * Send an html packet to the {@link Player} set a parameter with Give Buffs menu info for player and pet, depending on targetType parameter {player, pet}.
	 * @param player : The {@link Player} to make checks on.
	 */
	private void showGiveBuffsWindow(Player player)
	{
		final StringBuilder sb = new StringBuilder(200);
		
		final Map<String, ArrayList<L2Skill>> schemes = BufferManager.getInstance().getPlayerSchemes(player.getObjectId());
		if (schemes == null || schemes.isEmpty())
			sb.append("<font color=\"LEVEL\">" + player.getSysString(10_179) + "</font>");
		else
		{
			for (Map.Entry<String, ArrayList<L2Skill>> scheme : schemes.entrySet())
			{
				final int cost = getFee(scheme.getValue());
				StringUtil.append(sb, "<table><tr><td width=\"200\"><font color=\"LEVEL\">", scheme.getKey(), " [", scheme.getValue().size(), " / ", player.getMaxBuffCount(), "]", ((cost > 0) ? " - cost: " + StringUtil.formatNumber(cost) : ""), "</font></td></tr></table><br1>");
				StringUtil.append(sb, "<table><tr><td><button value=" + player.getSysString(10_099) + " action=\"bypass npc_%objectId%_givebuffs ", scheme.getKey(), " ", cost, "\" width=69 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td>");
				StringUtil.append(sb, "<td><button value=" + player.getSysString(10_100) + " action=\"bypass npc_%objectId%_givebuffs ", scheme.getKey(), " ", cost, " pet\" width=69 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td>");
				StringUtil.append(sb, "<td><button value=" + player.getSysString(10_101) + " action=\"bypass npc_%objectId%_editschemes Buffs ", scheme.getKey(), " 1\" width=69 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td>");
				StringUtil.append(sb, "<td><button value=" + player.getSysString(10_102) + " action=\"bypass npc_%objectId%_deletescheme ", scheme.getKey(), "\" width=69 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td></tr></table><br>");
			}
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 1));
		html.replace("%schemes%", sb.toString());
		html.replace("%max_schemes%", Config.BUFFER_MAX_SCHEMES);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * Send an html packet to the {@link Player} set as parameter with Edit Scheme Menu info. This allows the {@link Player} to edit each created scheme (add/delete skills)
	 * @param player : The {@link Player} to make checks on.
	 * @param groupType : The group of skills to select.
	 * @param schemeName : The scheme to make check.
	 * @param page : The current checked page.
	 */
	private void showEditSchemeWindow(Player player, String groupType, String schemeName, int page)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		final List<L2Skill> schemeSkills = BufferManager.getInstance().getScheme(player.getObjectId(), schemeName);
		premiumSkills(player, schemeSkills);
		html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 2));
		html.replace("%schemename%", schemeName);
		html.replace("%count%", schemeSkills.size() + " / " + player.getMaxBuffCount());
		html.replace("%typesframe%", getTypesFrame(player, groupType, schemeName, false));
		html.replace("%skilllistframe%", getGroupSkillList(player, groupType, schemeName, false, page));
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	private void showSingleBuffSelectionWindow(Player player, String groupType, int page)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(player.getLocale(), getHtmlPath(player, getNpcId(), 3));
		html.replace("%typesframe%", getTypesFrame(player, groupType, "", true));
		html.replace("%skilllistframe%", getGroupSkillList(player, groupType, "", true, page));
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	/**
	 * @param player : The {@link Player} to make checks on.
	 * @param groupType : The group of skills to select.
	 * @param schemeName : The scheme to make check.
	 * @param singleSelection : Indicate if it's single buff selection or not.
	 * @param page : The current checked page.
	 * @return A {@link String} representing skills available for selection for a given groupType.
	 */
	private String getGroupSkillList(Player player, String groupType, String schemeName, boolean singleSelection, int page)
	{
		List<L2Skill> skills = BufferManager.getInstance().getSkillsIdsByType(groupType);
		
		premiumSkills(player, skills);
		
		if (skills.isEmpty())
			return player.getSysString(10_185);
		
		final int max = MathUtil.countPagesNumber(skills.size(), PAGE_LIMIT);
		if (page > max)
			page = max;
		
		skills = skills.subList((page - 1) * PAGE_LIMIT, Math.min(page * PAGE_LIMIT, skills.size()));
		
		final List<L2Skill> schemeSkills = BufferManager.getInstance().getScheme(player.getObjectId(), schemeName);
		final StringBuilder sb = new StringBuilder(skills.size() * 150);
		
		int row = 0;
		for (L2Skill skill : skills)
		{
			int skillId = skill.getId();
			int skillLevel = skill.getLevel();
			
			sb.append(((row % 2) == 0 ? "<table width=\"280\" bgcolor=\"000000\"><tr>" : "<table width=\"280\"><tr>"));
			
			if (singleSelection)
				StringUtil.append(sb, "<td height=40 width=40><button action=\"bypass npc_" + getObjectId() + "_getbuff ", skillId, " ", skillLevel, " ", "\" width=32 height=32 back=\"", skill.getIcon(), "\" fore=\"", skill.getIcon(), "\" /></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferManager.getInstance().getAvailableBuff(skill).description(), "</font></td>");
			else
			{
				if (schemeSkills.contains(skill))
					StringUtil.append(sb, "<td height=40 width=40><img src=\"", skill.getIcon(), "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferManager.getInstance().getAvailableBuff(skill).description(), "</font></td><td><button action=\"bypass npc_%objectId%_skillunselect ", groupType, " ", schemeName, " ", skillId, " ", skillLevel, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomout2\" fore=\"L2UI_CH3.mapbutton_zoomout1\"></td>");
				else
					StringUtil.append(sb, "<td height=40 width=40><img src=\"", skill.getIcon(), "\" width=32 height=32></td><td width=190>", SkillTable.getInstance().getInfo(skillId, 1).getName(), "<br1><font color=\"B09878\">", BufferManager.getInstance().getAvailableBuff(skill).description(), "</font></td><td><button action=\"bypass npc_%objectId%_skillselect ", groupType, " ", schemeName, " ", skillId, " ", skillLevel, " ", page, "\" width=32 height=32 back=\"L2UI_CH3.mapbutton_zoomin2\" fore=\"L2UI_CH3.mapbutton_zoomin1\"></td>");
			}
			
			sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
			row++;
		}
		
		for (int i = PAGE_LIMIT; i > row; i--)
			StringUtil.append(sb, "<img height=41>");
		
		sb.append("<br><img src=\"L2UI.SquareGray\" width=280 height=1><table width=\"100%\" bgcolor=000000><tr>");
		
		if (page > 1)
		{
			if (singleSelection)
				StringUtil.append(sb, "<td align=left width=70><a action=\"bypass npc_" + getObjectId() + "_singlebuff ", groupType, " ", page - 1, "\">" + player.getSysString(10_176) + "</a></td>");
			else
				StringUtil.append(sb, "<td align=left width=70><a action=\"bypass npc_" + getObjectId() + "_editschemes ", groupType, " ", schemeName, " ", page - 1, "\">" + player.getSysString(10_176) + "</a></td>");
		}
		else
			StringUtil.append(sb, "<td align=left width=70>" + player.getSysString(10_176) + "</td>");
		
		StringUtil.append(sb, "<td align=center width=100>" + player.getSysString(10_178) + " ", page, "</td>");
		
		if (page < max)
		{
			if (singleSelection)
				StringUtil.append(sb, "<td align=right width=70><a action=\"bypass npc_" + getObjectId() + "_singlebuff ", groupType, " ", page + 1, "\">" + player.getSysString(10_177) + "</a></td>");
			else
				StringUtil.append(sb, "<td align=right width=70><a action=\"bypass npc_" + getObjectId() + "_editschemes ", groupType, " ", schemeName, " ", page + 1, "\">" + player.getSysString(10_177) + "</a></td>");
		}
		else
			StringUtil.append(sb, "<td align=right width=70>" + player.getSysString(10_177) + "</td>");
		
		sb.append("</tr></table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		return sb.toString();
	}
	
	/**
	 * @param player
	 * @param groupType : The group of skills to select.
	 * @param schemeName : The scheme to make check.
	 * @param singleSelection : Indicate if it's single buff selection or not.
	 * @return A {@link String} representing all groupTypes available. The group currently on selection isn't linkable.
	 */
	private static String getTypesFrame(Player player, String groupType, String schemeName, boolean singleSelection)
	{
		final StringBuilder sb = new StringBuilder(500);
		sb.append("<table>");
		
		int count = 0;
		for (String type : BufferManager.getInstance().getSkillTypes())
		{
			if (player.getPremiumService() == 0)
			{
				if (Config.PREMIUM_BUFFS_CATEGORY.isEmpty())
					continue;
			}
			
			if (count == 0)
				sb.append("<tr>");
			
			if (groupType.equalsIgnoreCase(type))
				StringUtil.append(sb, "<td width=65>", type, "</td>");
			else
			{
				if (singleSelection)
					StringUtil.append(sb, "<td width=65><a action=\"bypass npc_%objectId%_singlebuff ", type, " 1\">", type, "</a></td>");
				else
					StringUtil.append(sb, "<td width=65><a action=\"bypass npc_%objectId%_editschemes ", type, " ", schemeName, " 1\">", type, "</a></td>");
			}
			
			count++;
			if (count == 4)
			{
				sb.append("</tr>");
				count = 0;
			}
		}
		
		if (!sb.toString().endsWith("</tr>"))
			sb.append("</tr>");
		
		sb.append("</table>");
		
		return sb.toString();
	}
	
	/**
	 * @param list : A {@link List} of skill ids.
	 * @return a global fee for all skills contained in the {@link List}.
	 */
	private static int getFee(List<L2Skill> list)
	{
		if (Config.BUFFER_STATIC_BUFF_COST > 0)
			return list.size() * Config.BUFFER_STATIC_BUFF_COST;
		
		int fee = 0;
		for (L2Skill sk : list)
		{
			if (sk != null)
			{
				BuffSkill buffSkill = BufferManager.getInstance().getAvailableBuff(sk);
				if (buffSkill != null)
					fee += buffSkill.price();
			}
		}
		
		return fee;
	}
	
	private void premiumSkills(Player player, List<L2Skill> skills)
	{
		if (player.getPremiumService() == 0)
			skills.removeIf(skill -> Config.BUFFS_CATEGORY.contains(BufferManager.getInstance().getAvailableBuff(skill).type()));
	}
}