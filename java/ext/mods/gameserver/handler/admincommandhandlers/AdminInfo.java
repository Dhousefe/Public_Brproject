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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.DropCalc;
import ext.mods.gameserver.data.manager.BuyListManager;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.ScriptData;
import ext.mods.gameserver.data.xml.SkipData;
import ext.mods.gameserver.enums.DropType;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.enums.skills.ElementType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.ai.Desire;
import ext.mods.gameserver.model.actor.container.attackable.AggroList;
import ext.mods.gameserver.model.actor.container.npc.AggroInfo;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.instance.StaticObject;
import ext.mods.gameserver.model.buylist.NpcBuyList;
import ext.mods.gameserver.model.item.DropCategory;
import ext.mods.gameserver.model.item.DropData;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestTimer;
import ext.mods.gameserver.skills.L2Skill;

public class AdminInfo implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_info"
	};
	
	private static final DecimalFormat PERCENT = new DecimalFormat("#.###");
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_info"))
		{
			final WorldObject targetWorldObject = getTarget(WorldObject.class, player, true);
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			if (targetWorldObject instanceof Door targetDoor)
				showDoorInfo(player, targetDoor, html);
			else if (targetWorldObject instanceof Npc targetNpc)
			{
				final StringTokenizer st = new StringTokenizer(command, " ");
				st.nextToken();
				
				if (!st.hasMoreTokens())
					sendGeneralInfos(player, targetNpc, html, 0);
				else
				{
					final String subCommand = st.nextToken();
					switch (subCommand)
					{
						case "ai":
							try
							{
								final int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 0;
								
								sendAiInfos(player, targetNpc, html, page);
							}
							catch (Exception e)
							{
								sendAiInfos(player, targetNpc, html, 0);
							}
							
							break;
						
						case "aggro":
							sendAggroInfos(player, targetNpc, html);
							break;
						
						case "desire":
							sendDesireInfos(player, targetNpc, html);
							break;
						
						case "drop", "spoil":
							try
							{
								final int page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
								final int subPage = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
								
								sendDropInfos(player, targetNpc, html, page, subPage, subCommand.equalsIgnoreCase("drop"));
							}
							catch (Exception e)
							{
								sendDropInfos(player, targetNpc, html, 1, 1, true);
							}
							break;
						
						case "script":
							sendScriptInfos(player, targetNpc, html);
							break;
						
						case "shop":
							sendShopInfos(player, targetNpc, html);
							break;
						
						case "skill":
							sendSkillInfos(player, targetNpc, html);
							break;
						
						case "spawn":
							sendSpawnInfos(player, targetNpc, html);
							break;
						
						case "stat":
							sendStatsInfos(player, targetNpc, html);
							break;
						
						default:
							sendGeneralInfos(player, targetNpc, html, StringUtil.isDigit(subCommand) ? Integer.valueOf(subCommand) : 0);
					}
				}
			}
			else if (targetWorldObject instanceof Player targetPlayer)
				AdminEditChar.gatherPlayerInfo(player, targetPlayer, html);
			else if (targetWorldObject instanceof Summon targetSummon)
			{
				final Player owner = targetWorldObject.getActingPlayer();
				
				html.setFile(player.getLocale(), "html/admin/petinfo.htm");
				html.replace("%name%", (targetSummon.getName() == null) ? "N/A" : targetSummon.getName());
				html.replace("%level%", targetSummon.getStatus().getLevel());
				html.replace("%exp%", targetSummon.getStatus().getExp());
				html.replace("%owner%", (owner == null) ? "N/A" : " <a action=\"bypass -h admin_debug " + owner.getName() + "\">" + owner.getName() + "</a>");
				html.replace("%class%", targetSummon.getClass().getSimpleName());
				html.replace("%ai%", targetSummon.getAI().getCurrentIntention().getType().name());
				html.replace("%hp%", (int) targetSummon.getStatus().getHp() + "/" + targetSummon.getStatus().getMaxHp());
				html.replace("%mp%", (int) targetSummon.getStatus().getMp() + "/" + targetSummon.getStatus().getMaxMp());
				html.replace("%karma%", targetSummon.getKarma());
				html.replace("%undead%", (targetSummon.isUndead()) ? "yes" : "no");
				
				if (targetWorldObject instanceof Pet targetPet)
				{
					html.replace("%inv%", (owner == null) ? "N/A" : " <a action=\"bypass admin_summon inventory\">view</a>");
					html.replace("%food%", targetPet.getCurrentFed() + "/" + targetPet.getPetData().maxMeal());
					html.replace("%load%", targetPet.getInventory().getTotalWeight() + "/" + targetPet.getWeightLimit());
				}
				else
				{
					html.replace("%inv%", "none");
					html.replace("%food%", "N/A");
					html.replace("%load%", "N/A");
				}
			}
			else if (targetWorldObject instanceof StaticObject targetStaticObject)
			{
				html.setFile(player.getLocale(), "html/admin/staticinfo.htm");
				html.replace("%x%", targetStaticObject.getX());
				html.replace("%y%", targetStaticObject.getY());
				html.replace("%z%", targetStaticObject.getZ());
				html.replace("%objid%", targetStaticObject.getObjectId());
				html.replace("%staticid%", targetStaticObject.getStaticObjectId());
				html.replace("%class%", targetStaticObject.getClass().getSimpleName());
			}
			player.sendPacket(html);
		}
	}
	
	public static void showPetInfo(final Summon pet, Player player, final NpcHtmlMessage html)
	{
		if (pet != null)
		{
			final Summon targetSummon = pet;
			final Player owner = pet.getActingPlayer();
			
			html.setFile(player.getLocale(), "html/admin/petinfo.htm");
			html.replace("%name%", (pet.getName() == null) ? "N/A" : pet.getName());
			html.replace("%level%", targetSummon.getStatus().getLevel());
			html.replace("%exp%", targetSummon.getStatus().getExp());
			html.replace("%owner%", (owner == null) ? "N/A" : " <a action=\"bypass -h admin_debug " + owner.getName() + "\">" + owner.getName() + "</a>");
			html.replace("%class%", targetSummon.getClass().getSimpleName());
			html.replace("%ai%", targetSummon.getAI().getCurrentIntention().getType().name());
			html.replace("%hp%", (int) targetSummon.getStatus().getHp() + "/" + targetSummon.getStatus().getMaxHp());
			html.replace("%mp%", (int) targetSummon.getStatus().getMp() + "/" + targetSummon.getStatus().getMaxMp());
			html.replace("%karma%", targetSummon.getKarma());
			html.replace("%undead%", (targetSummon.isUndead()) ? "yes" : "no");
			
			if (pet instanceof Pet targetPet)
			{
				html.replace("%inv%", (owner == null) ? "N/A" : " <a action=\"bypass admin_summon inventory\">view</a>");
				html.replace("%food%", targetPet.getCurrentFed() + "/" + targetPet.getPetData().maxMeal());
				html.replace("%load%", targetPet.getInventory().getTotalWeight() + "/" + targetPet.getWeightLimit());
			}
			else
			{
				html.replace("%inv%", "none");
				html.replace("%food%", "N/A");
				html.replace("%load%", "N/A");
			}
		}
	}
	
	public static void showDoorInfo(Player player, Door targetDoor, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/admin/doorinfo.htm");
		html.replace("%name%", targetDoor.getName());
		html.replace("%objid%", targetDoor.getObjectId());
		html.replace("%doorid%", targetDoor.getTemplate().getId());
		html.replace("%doortype%", targetDoor.getTemplate().getType().toString());
		html.replace("%doorlvl%", targetDoor.getTemplate().getLevel());
		html.replace("%residence%", (targetDoor.getResidence() != null) ? targetDoor.getResidence().getName() : "none");
		html.replace("%opentype%", targetDoor.getTemplate().getOpenType().toString());
		html.replace("%initial%", targetDoor.getTemplate().isOpened() ? "Opened" : "Closed");
		html.replace("%ot%", targetDoor.getTemplate().getOpenTime());
		html.replace("%ct%", targetDoor.getTemplate().getCloseTime());
		html.replace("%rt%", targetDoor.getTemplate().getRandomTime());
		html.replace("%controlid%", targetDoor.getTemplate().getTriggerId());
		html.replace("%hp%", (int) targetDoor.getStatus().getHp());
		html.replace("%hpmax%", targetDoor.getStatus().getMaxHp());
		html.replace("%hpratio%", targetDoor.getStatus().getUpgradeHpRatio());
		html.replace("%pdef%", targetDoor.getStatus().getPDef(null));
		html.replace("%mdef%", targetDoor.getStatus().getMDef(null, null));
		html.replace("%spawn%", targetDoor.getPosition().toString());
		html.replace("%height%", targetDoor.getTemplate().getCollisionHeight());
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 * @param index : The used index.
	 */
	private static void sendAiInfos(Player player, Npc npc, NpcHtmlMessage html, int index)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/default.htm");
		
		final StringBuilder sb = new StringBuilder(500);
		sb.append("<center><table width=240><tr><td width=70>");
		
		switch (index)
		{
			default:
			case 0:
				sb.append("[AI Path]</td><td width=70><a action=\"bypass -h admin_info ai 1\">Template</a></td><td width=70><a action=\"bypass -h admin_info ai 2\">Spawn</a></td><td width=70><a action=\"bypass -h admin_info ai 3\">Npc</a></td></tr></table></center><br>");
				
				final Quest aiScript = npc.getTemplate().getEventQuests().values().stream().flatMap(List::stream).filter(q -> !q.isRealQuest()).findFirst().orElse(null);
				if (aiScript == null)
					StringUtil.append(sb, "This NPC doesn't hold any AI related script.");
				else
				{
					Class<?> checkedClass = aiScript.getClass();
					while (checkedClass != Quest.class)
					{
						StringUtil.append(sb, checkedClass.getSimpleName(), "<br1>");
						checkedClass = checkedClass.getSuperclass();
					}
				}
				break;
			
			case 1:
				sb.append("<a action=\"bypass -h admin_info ai 0\">AI Path</a></td><td width=70>[Template]</a></td><td width=70><a action=\"bypass -h admin_info ai 2\">Spawn</a></td><td width=70><a action=\"bypass -h admin_info ai 3\">Npc</a></td></tr></table></center><br>");
				
				if (npc.getTemplate().getAiParams().isEmpty())
					StringUtil.append(sb, "This NPC's template doesn't hold any AI parameters.");
				else
				{
					for (Entry<String, String> aiParam : npc.getTemplate().getAiParams().entrySet())
						StringUtil.append(sb, "<font color=\"LEVEL\">[", aiParam.getKey(), "]</font> ", aiParam.getValue(), "<br1>");
				}
				break;
			
			case 2:
				sb.append("<a action=\"bypass -h admin_info ai 0\">AI Path</a></td><td width=70><a action=\"bypass -h admin_info ai 1\">Template</a></td><td width=70>[Spawn]</td><td width=70><a action=\"bypass -h admin_info ai 3\">Npc</a></td></tr></table></center><br>");
				
				final ASpawn spawn = npc.getSpawn();
				if (spawn == null)
					StringUtil.append(sb, "This NPC doesn't have any Spawn.");
				else if (spawn.getMemo().isEmpty())
					StringUtil.append(sb, "This NPC Spawn doesn't hold any memos.");
				else
				{
					for (Entry<String, String> aiParam : spawn.getMemo().entrySet())
						StringUtil.append(sb, "<font color=\"LEVEL\">[", aiParam.getKey(), "]</font> ", aiParam.getValue(), "<br1>");
				}
				break;
			
			case 3:
				sb.append("<a action=\"bypass -h admin_info ai 0\">AI Path</a></td><td width=70><a action=\"bypass -h admin_info ai 1\">Template</a></td><td width=70><a action=\"bypass -h admin_info ai 2\">Spawn</a></td><td width=70>[Npc]</td></tr></table></center><br>");
				
				StringUtil.append(sb, "<table width=280>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[i_ai0]</font> ", npc._i_ai0, "</td><td width=140 align=left><font color=\"LEVEL\">[i_quest0]</font> ", npc._i_quest0, "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[i_ai1]</font> ", npc._i_ai1, "</td><td width=140 align=left><font color=\"LEVEL\">[i_quest1]</font> ", npc._i_quest1, "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[i_ai2]</font> ", npc._i_ai2, "</td><td width=140 align=left><font color=\"LEVEL\">[i_quest2]</font> ", npc._i_quest2, "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[i_ai3]</font> ", npc._i_ai3, "</td><td width=140 align=left><font color=\"LEVEL\">[i_quest3]</font> ", npc._i_quest3, "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[i_ai4]</font> ", npc._i_ai4, "</td><td width=140 align=left><font color=\"LEVEL\">[i_quest4]</font> ", npc._i_quest4, "</td></tr>");
				StringUtil.append(sb, "</table><br><table width=280>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[c_ai0]</font> ", StringUtil.getCreatureDescription(sb, npc._c_ai0), "</td><td width=140 align=left><font color=\"LEVEL\">[c_quest0]</font> ", StringUtil.getCreatureDescription(sb, npc._c_quest0), "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[c_ai1]</font> ", StringUtil.getCreatureDescription(sb, npc._c_ai1), "</td><td width=140 align=left><font color=\"LEVEL\">[c_quest1]</font> ", StringUtil.getCreatureDescription(sb, npc._c_quest1), "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[c_ai2]</font> ", StringUtil.getCreatureDescription(sb, npc._c_ai2), "</td><td width=140 align=left><font color=\"LEVEL\">[c_quest2]</font> ", StringUtil.getCreatureDescription(sb, npc._c_quest2), "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[c_ai3]</font> ", StringUtil.getCreatureDescription(sb, npc._c_ai3), "</td><td width=140 align=left><font color=\"LEVEL\">[c_quest3]</font> ", StringUtil.getCreatureDescription(sb, npc._c_quest3), "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[c_ai4]</font> ", StringUtil.getCreatureDescription(sb, npc._c_ai4), "</td><td width=140 align=left><font color=\"LEVEL\">[c_quest4]</font> ", StringUtil.getCreatureDescription(sb, npc._c_quest4), "</td></tr>");
				StringUtil.append(sb, "</table><br><table width=280>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[param1]</font> ", npc._param1, "</td><td width=140 align=left><font color=\"LEVEL\">[flag]</font> ", npc._flag, "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[param2]</font> ", npc._param2, "</td><td width=140 align=left><font color=\"LEVEL\">[respawnTime]</font> ", npc._respawnTime, "</td></tr>");
				StringUtil.append(sb, "<tr><td width=140><font color=\"LEVEL\">[param3]</font> ", npc._param3, "</td><td width=140 align=left><font color=\"LEVEL\">[weightPoint]</font> ", npc._weightPoint, "</td></tr>");
				StringUtil.append(sb, "</table>");
				break;
		}
		html.replace("%content%", sb.toString());
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with {@link AggroList} informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 */
	private static void sendAggroInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/default.htm");
		if (!(npc instanceof Attackable attackable))
		{
			html.replace("%content%", "This NPC can't build aggro towards targets.<br><button value=\"Refresh\" action=\"bypass -h admin_info aggro\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">");
			return;
		}
		
		final AggroList aggroList = attackable.getAI().getAggroList();
		if (aggroList.isEmpty())
		{
			html.replace("%content%", "This NPC's AggroList is empty.<br><button value=\"Refresh\" action=\"bypass -h admin_info aggro\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">");
			return;
		}
		
		final StringBuilder sb = new StringBuilder(500);
		sb.append("<button value=\"Refresh\" action=\"bypass -h admin_info aggro\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"><br><table width=\"280\"><tr><td><font color=\"LEVEL\">Attacker</font></td><td><font color=\"LEVEL\">Damage</font></td><td><font color=\"LEVEL\">Hate</font></td></tr>");
		
		for (AggroInfo ai : aggroList.values().stream().sorted(Comparator.comparing(AggroInfo::getHate, Comparator.reverseOrder())).limit(15).toList())
			StringUtil.append(sb, "<tr><td>", ai.getAttacker().getName(), "</td><td>", ai.getDamage(), "</td><td>", ai.getHate(), "</td></tr>");
		
		sb.append("</table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		html.replace("%content%", sb.toString());
	}
	
	private static void sendDesireInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/default.htm");
		
		final Set<Desire> desires = npc.getAI().getDesires();
		if (desires.isEmpty())
		{
			html.replace("%content%", "This NPC's Desires are empty.<br><button value=\"Refresh\" action=\"bypass -h admin_info desire\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">");
			return;
		}
		
		final StringBuilder sb = new StringBuilder(500);
		sb.append("<button value=\"Refresh\" action=\"bypass -h admin_info desire\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"><br><table width=\"280\"><tr><td><font color=\"LEVEL\">Type</font></td><td><font color=\"LEVEL\">Weight</font></td></tr>");
		
		for (Desire desire : desires)
			StringUtil.append(sb, "<tr><td>", desire.getType(), "</td><td>", desire.getWeight(), "</td></tr>");
		
		sb.append("</table><img src=\"L2UI.SquareGray\" width=280 height=1>");
		
		html.replace("%content%", sb.toString());
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with <b>DROPS</b> or <b>SPOILS</b> informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 * @param page : The current page of categories we are checking.
	 * @param subPage : The current page of drops we are checking.
	 * @param isDrop : If true, we check drops only. If false, we check spoils.
	 */
	private static void sendDropInfos(Player player, Npc npc, NpcHtmlMessage html, int page, int subPage, boolean isDrop)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/default.htm");
		
		int row = 0;
		
		List<DropCategory> filteredCategories = npc.getTemplate().getDropData().stream().filter(category -> !isDrop || category.getDropType() != DropType.SPOIL).filter(category -> category.getAllDrops().stream().noneMatch(drop -> SkipData.getInstance().isSkipped(drop.getItemId()))).filter(category -> !category.getAllDrops().isEmpty()).toList();
		
		final Pagination<DropCategory> list = new Pagination<>(filteredCategories.stream(), page, PAGE_LIMIT_1);
		
		for (DropCategory category : list)
		{
			double catChance = Math.min(DropCalc.getInstance().calcDropChance(player, npc, category, category.getDropType(), npc.isRaidBoss(), npc instanceof GrandBoss), 100.0);
			double baseCatChance = category.getChance() * category.getDropType().getDropRate(player, npc, npc.isRaidBoss(), npc instanceof GrandBoss);
			double chanceMultiplier = 1;
			double countMultiplier = 1;
			
			if (baseCatChance > 100)
			{
				countMultiplier = baseCatChance / category.getCategoryCumulativeChance();
				chanceMultiplier = baseCatChance / 100d / countMultiplier;
				baseCatChance = 100;
			}
			
			if (Config.ALTERNATE_DROP_LIST)
			{
				list.append("<br></center>Category: ", category.getDropType(), " - Rate: ", PERCENT.format(catChance), "%<center>");
				
				final Pagination<DropData> droplist = new Pagination<>(category.getAllDrops().stream(), subPage, PAGE_LIMIT_10);
				
				for (DropData drop : droplist)
				{
					if (SkipData.getInstance().isSkipped(drop.getItemId()))
						continue;
					
					final double chance = DropCalc.getInstance().calcDropChance(player, npc, drop, category.getDropType(), npc.isRaidBoss(), npc instanceof GrandBoss);
					final double normChance = Math.min(100, chance);
					
					final double overflowFactor = Math.max(0.0, (chance - 100) / 100);
					final double inverseCategoryChance = (100 - category.getChance()) / 100;
					final double reduceFactor = Math.pow(inverseCategoryChance, 10);
					
					int min = drop.getMinDrop();
					int max = drop.getMaxDrop();
					
					min = (int) (min + min * overflowFactor - min * overflowFactor * reduceFactor);
					max = (int) (max + max * overflowFactor - max * overflowFactor * reduceFactor);
					min = Math.max(min, drop.getMinDrop());
					max = Math.max(max, min);
					
					final String color = (normChance > 80.) ? "90EE90" : (normChance > 5.) ? "BDB76B" : "F08080";
					final String percent = PERCENT.format(normChance);
					final String amount = (min == max) ? min + "" : min + "-" + max;
					final Item item = ItemData.getInstance().getTemplate(drop.getItemId());
					
					String name = item.getName();
					if (name.startsWith("Recipe: "))
						name = "R: " + name.substring(8);
					
					name = StringUtil.trimAndDress(name, 45);
					
					droplist.append(((row % 2) == 0 ? "<table width=280 bgcolor=000000><tr>" : "<table width=280><tr>"));
					droplist.append("<td width=44 height=41 align=center><table bgcolor=" + "FFFFFF" + " cellpadding=6 cellspacing=\"-5\"><tr><td><button width=32 height=32 back=" + item.getIcon() + " fore=" + item.getIcon() + "></td></tr></table></td>");
					droplist.append("<td width=246>&nbsp;", name, "<br1>");
					droplist.append("<table width=240><tr><td width=80><font color=B09878>Rate:</font> <font color=", color, ">", percent, "%</font></td><td width=160><font color=B09878>Amount: </font>", amount, "</td></tr></table>");
					droplist.append("</td></tr></table><img src=L2UI.SquareGray width=280 height=1>");
					
					row++;
				}
				
				if (droplist.totalEntries() > 0)
				{
					if (droplist.totalEntries() > 10)
					{
						droplist.generateSpace(41);
						droplist.generatePages("bypass admin_info " + ((isDrop) ? "drop" : "spoil") + " " + page + " %page%");
					}
					
					list.append(droplist.getContent());
				}
			}
			else
			{
				list.append("<br></center>Category: ", category.getDropType(), " - Rate: ", PERCENT.format(baseCatChance), "%<center>");
				
				final Pagination<DropData> droplist = new Pagination<>(category.getAllDrops().stream(), subPage, 6);
				
				for (DropData drop : droplist)
				{
					if (SkipData.getInstance().isSkipped(drop.getItemId()))
						continue;
					
					final double chance = drop.getChance() * chanceMultiplier;
					final String color = (chance > 80.) ? "90EE90" : (chance > 5.) ? "BDB76B" : "F08080";
					final String percent = PERCENT.format(chance);
					final String amount = (drop.getMinDrop() == drop.getMaxDrop()) ? (int) (drop.getMinDrop() * countMultiplier) + "" : (int) (drop.getMinDrop() * countMultiplier) + " - " + (int) (drop.getMaxDrop() * countMultiplier);
					final Item item = ItemData.getInstance().getTemplate(drop.getItemId());
					
					String name = item.getName();
					if (name.startsWith("Recipe: "))
						name = "R: " + name.substring(8);
					
					name = StringUtil.trimAndDress(name, 45);
					
					droplist.append(((row % 2) == 0 ? "<table width=280 bgcolor=000000><tr>" : "<table width=280><tr>"));
					droplist.append("<td width=44 height=41 align=center><table bgcolor=" + "FFFFFF" + " cellpadding=6 cellspacing=\"-5\"><tr><td><button width=32 height=32 back=" + item.getIcon() + " fore=" + item.getIcon() + "></td></tr></table></td>");
					droplist.append("<td width=246>&nbsp;", name, "<br1>");
					droplist.append("<table width=240><tr><td width=80><font color=B09878>Rate:</font> <font color=", color, ">", percent, "%</font></td><td width=160><font color=B09878>Amount: </font>", amount, "</td></tr></table>");
					droplist.append("</td></tr></table><img src=L2UI.SquareGray width=280 height=1>");
					
					row++;
				}
				
				if (droplist.totalEntries() > 0)
				{
					droplist.generateSpace(41);
					droplist.generatePages("bypass admin_info " + ((isDrop) ? "drop" : "spoil") + " " + page + " %page%");
					
					list.append(droplist.getContent());
				}
			}
		}
		
		if (list.totalEntries() > 0)
		{
			list.generateSpace(30);
			list.generatePages("bypass admin_info " + ((isDrop) ? "drop" : "spoil") + " %page% 1");
		}
		
		html.replace("%content%", list.getContent());
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with <b>GENERAL</b> informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 * @param index : The used index.
	 */
	public static void sendGeneralInfos(Player player, Npc npc, NpcHtmlMessage html, int index)
	{
		switch (index)
		{
			case 0:
			default:
				html.setFile(player.getLocale(), "html/admin/npcinfo/general-0.htm");
				html.replace("%objectId%", npc.getObjectId());
				
				html.replace("%npcId%", npc.getTemplate().getNpcId());
				html.replace("%idTemplate%", npc.getTemplate().getIdTemplate());
				
				html.replace("%name%", npc.getTemplate().getName());
				html.replace("%title%", npc.getTemplate().getTitle());
				html.replace("%alias%", npc.getTemplate().getAlias());
				
				html.replace("%usingServerSideName%", npc.getTemplate().isUsingServerSideName());
				html.replace("%usingServerSideTitle%", npc.getTemplate().isUsingServerSideTitle());
				
				html.replace("%type%", npc.getClass().getSimpleName());
				html.replace("%level%", npc.getTemplate().getLevel());
				
				html.replace("%radius%", npc.getTemplate().getCollisionRadius());
				html.replace("%height%", npc.getTemplate().getCollisionHeight());
				
				html.replace("%hitTimeFactor%", npc.getTemplate().getHitTimeFactor());
				
				html.replace("%rHand%", npc.getTemplate().getRightHand());
				html.replace("%lHand%", npc.getTemplate().getLeftHand());
				break;
			
			case 1:
				html.setFile(player.getLocale(), "html/admin/npcinfo/general-1.htm");
				html.replace("%exp%", npc.getTemplate().getRewardExp());
				html.replace("%sp%", npc.getTemplate().getRewardSp());
				
				html.replace("%baseAttackRange%", npc.getTemplate().getBaseAttackRange());
				html.replace("%baseDamageRange%", Arrays.toString(npc.getTemplate().getBaseDamageRange()));
				html.replace("%baseRandomDamage%", npc.getTemplate().getBaseRandomDamage());
				
				html.replace("%race%", npc.getTemplate().getRace().toString());
				
				html.replace("%clan%", (npc.getTemplate().getClans() == null) ? "none" : Arrays.toString(npc.getTemplate().getClans()));
				html.replace("%clanRange%", npc.getTemplate().getClanRange());
				html.replace("%ignoredIds%", (npc.getTemplate().getIgnoredIds() == null) ? "none" : Arrays.toString(npc.getTemplate().getIgnoredIds()));
				break;
			
			case 2:
				html.setFile(player.getLocale(), "html/admin/npcinfo/general-2.htm");
				html.replace("%isUndying%", npc.getTemplate().isUndying());
				html.replace("%canBeAttacked%", npc.getTemplate().canBeAttacked());
				html.replace("%isNoSleepMode%", npc.getTemplate().isNoSleepMode());
				html.replace("%aggroRange%", npc.getTemplate().getAggroRange());
				html.replace("%canMove%", npc.getTemplate().canMove());
				html.replace("%isSeedable%", npc.getTemplate().isSeedable());
				
				html.replace("%residence%", (npc.getResidence() != null) ? npc.getResidence().getName() : "none");
				break;
		}
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 */
	private static void sendScriptInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/script.htm");
		
		final StringBuilder sb = new StringBuilder(500);
		
		if (npc.getTemplate().getEventQuests().isEmpty())
			sb.append("This NPC isn't affected by scripts.");
		else
		{
			EventHandler type = null;
			
			for (Map.Entry<EventHandler, List<Quest>> entry : npc.getTemplate().getEventQuests().entrySet())
			{
				if (type != entry.getKey())
				{
					type = entry.getKey();
					StringUtil.append(sb, "<br><font color=\"LEVEL\">", type.name(), "</font><br1>");
				}
				
				for (Quest quest : entry.getValue())
					StringUtil.append(sb, quest.getName(), "<br1>");
			}
		}
		html.replace("%scripts%", sb.toString());
		
		sb.setLength(0);
		
		for (Quest quest : ScriptData.getInstance().getQuests())
		{
			final List<QuestTimer> qts = quest.getQuestTimers(npc);
			if (!qts.isEmpty())
			{
				StringUtil.append(sb, "<br><font color=\"LEVEL\">", quest.getName(), "</font><br1>");
				
				for (QuestTimer qt : qts)
					StringUtil.append(sb, qt.getName(), ((qt.getPlayer() == null) ? "" : (" affecting player ") + qt.getPlayer().getName()), "<br1>");
			}
		}
		html.replace("%tasks%", sb.toString());
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with <b>SPAWN</b> informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 */
	private static void sendSpawnInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/spawn.htm");
		
		html.replace("%loc%", npc.getX() + " " + npc.getY() + " " + npc.getZ());
		html.replace("%dist%", (int) player.distance3D(npc));
		html.replace("%corpse%", StringUtil.getTimeStamp(npc.getTemplate().getCorpseTime()));
		
		final ASpawn spawn = npc.getSpawn();
		if (spawn != null)
		{
			html.replace("%spawn%", spawn.toString());
			
			if (spawn instanceof MultiSpawn ms)
			{
				html.replace("%spawndesc%", "<a action=\"bypass -h admin_maker " + ms.getNpcMaker().getName() + "\">" + ms.getDescription() + "</a>");
				
				final int[][] coords = ms.getCoords();
				if (coords == null)
					html.replace("%spawninfo%", "loc: anywhere");
				else if (coords.length == 1)
					html.replace("%spawninfo%", "loc: fixed " + coords[0][0] + ", " + coords[0][1] + ", " + coords[0][2]);
				else
					html.replace("%spawninfo%", "loc: fixed random 1 of " + coords.length);
			}
			else
			{
				html.replace("%spawndesc%", spawn.getDescription());
				html.replace("%spawninfo%", "loc: " + spawn.getSpawnLocation());
			}
			
			html.replace("%loc2d%", (int) npc.distance2D(npc.getSpawnLocation()));
			html.replace("%loc3d%", (int) npc.distance3D(npc.getSpawnLocation()));
			html.replace("%resp%", StringUtil.getTimeStamp(spawn.getRespawnDelay()));
			html.replace("%rand_resp%", StringUtil.getTimeStamp(spawn.getRespawnRandom()));
			html.replace("%privates%", spawn.getPrivateData() != null && !spawn.getPrivateData().isEmpty());
		}
		else
		{
			html.replace("%spawn%", "<font color=FF0000>--</font>");
			html.replace("%spawndesc%", "<font color=FF0000>--</font>");
			html.replace("%spawninfo%", "<font color=FF0000>--</font>");
			html.replace("%loc2d%", "<font color=FF0000>--</font>");
			html.replace("%loc3d%", "<font color=FF0000>--</font>");
			html.replace("%resp%", "<font color=FF0000>--</font>");
			html.replace("%rand_resp%", "<font color=FF0000>--</font>");
			html.replace("%privates%", "<font color=FF0000>--</font>");
		}
		
		final StringBuilder sb = new StringBuilder(500);
		
		if (npc.isMaster())
			StringUtil.append(sb, "I'm a master, holding ", npc.getMinions().size(), " crappy minions.");
		else if (npc.hasMaster())
			StringUtil.append(sb, "I'm a crappy minion, my master <font color=LEVEL>", npc.getMaster().getName(), "</font> holds ", npc.getMinions().size(), " minions.");
		else
			StringUtil.append(sb, "I'm a regular NPC");
		
		html.replace("%minion%", sb.toString());
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with <b>STATS</b> informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 */
	private static void sendStatsInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/stat.htm");
		
		html.replace("%hp%", npc.isChampion() ? (int) npc.getStatus().getHp() * Config.CHAMPION_HP : (int) npc.getStatus().getHp());
		html.replace("%hpmax%", npc.isChampion() ? npc.getStatus().getMaxHp() * Config.CHAMPION_HP : npc.getStatus().getMaxHp());
		html.replace("%mp%", (int) npc.getStatus().getMp());
		html.replace("%mpmax%", npc.getStatus().getMaxMp());
		html.replace("%hpreg%", String.format("%.2f", npc.getStatus().getRegenHp()));
		html.replace("%mpreg%", String.format("%.2f", npc.getStatus().getRegenMp()));
		html.replace("%patk%", npc.isChampion() ? npc.getStatus().getPAtk(null) * Config.CHAMPION_ATK : npc.getStatus().getPAtk(null));
		html.replace("%matk%", npc.isChampion() ? npc.getStatus().getMAtk(null, null) * Config.CHAMPION_MATK : npc.getStatus().getMAtk(null, null));
		html.replace("%pdef%", npc.getStatus().getPDef(null));
		html.replace("%mdef%", npc.getStatus().getMDef(null, null));
		html.replace("%accu%", npc.getStatus().getAccuracy());
		html.replace("%evas%", npc.getStatus().getEvasionRate(null));
		html.replace("%crit%", npc.getStatus().getCriticalHit(null, null));
		html.replace("%rspd%", (int) npc.getStatus().getMoveSpeed());
		html.replace("%aspd%", npc.isChampion() ? npc.getStatus().getPAtkSpd() * Config.CHAMPION_SPD_ATK : npc.getStatus().getPAtkSpd());
		html.replace("%cspd%", npc.isChampion() ? npc.getStatus().getMAtkSpd() * Config.CHAMPION_SPD_MATK : npc.getStatus().getMAtkSpd());
		html.replace("%str%", npc.getStatus().getSTR());
		html.replace("%dex%", npc.getStatus().getDEX());
		html.replace("%con%", npc.getStatus().getCON());
		html.replace("%int%", npc.getStatus().getINT());
		html.replace("%wit%", npc.getStatus().getWIT());
		html.replace("%men%", npc.getStatus().getMEN());
		html.replace("%ele_fire%", npc.getStatus().getDefenseElementValue(ElementType.FIRE));
		html.replace("%ele_water%", npc.getStatus().getDefenseElementValue(ElementType.WATER));
		html.replace("%ele_wind%", npc.getStatus().getDefenseElementValue(ElementType.WIND));
		html.replace("%ele_earth%", npc.getStatus().getDefenseElementValue(ElementType.EARTH));
		html.replace("%ele_holy%", npc.getStatus().getDefenseElementValue(ElementType.HOLY));
		html.replace("%ele_dark%", npc.getStatus().getDefenseElementValue(ElementType.DARK));
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with <b>SHOPS</b> informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 */
	private static void sendShopInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/default.htm");
		
		final List<NpcBuyList> buyLists = BuyListManager.getInstance().getBuyListsByNpcId(npc.getNpcId());
		if (buyLists.isEmpty())
		{
			html.replace("%content%", "This NPC doesn't hold any buyList.");
			return;
		}
		
		final StringBuilder sb = new StringBuilder(500);
		
		if (npc.getCastle() != null)
			StringUtil.append(sb, "Tax rate: ", npc.getCastle().getCurrentTaxPercent(), "%<br>");
		
		StringUtil.append(sb, "<table width=\"100%\">");
		
		for (NpcBuyList buyList : buyLists)
			StringUtil.append(sb, "<tr><td><a action=\"bypass -h admin_buy ", buyList.getListId(), " 1\">Buylist id: ", buyList.getListId(), "</a></td></tr>");
		
		StringUtil.append(sb, "</table>");
		
		html.replace("%content%", sb.toString());
	}
	
	/**
	 * Feed a {@link NpcHtmlMessage} with <b>SKILLS</b> informations regarding a {@link Npc}.
	 * @param player : The {@link Player} used as reference.
	 * @param npc : The {@link Npc} used as reference.
	 * @param html : The {@link NpcHtmlMessage} used as reference.
	 */
	private static void sendSkillInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/admin/npcinfo/default.htm");
		
		if (npc.getTemplate().getSkills().isEmpty())
		{
			html.replace("%content%", "This NPC doesn't hold any skill.");
			return;
		}
		
		final StringBuilder sb = new StringBuilder(500);
		
		NpcSkillType type = null;
		
		for (Map.Entry<NpcSkillType, L2Skill> entry : npc.getTemplate().getSkills().entrySet())
		{
			if (type != entry.getKey())
			{
				type = entry.getKey();
				StringUtil.append(sb, "<br><font color=\"LEVEL\">", type.name(), "</font><br1>");
			}
			
			final L2Skill skill = entry.getValue();
			StringUtil.append(sb, ((skill.getSkillType() == SkillType.NOTDONE) ? ("<font color=\"777777\">" + skill.getName() + "</font>") : skill.getName()), " [", skill.getId(), "-", skill.getLevel(), "]<br1>");
		}
		
		html.replace("%content%", sb.toString());
	}
}