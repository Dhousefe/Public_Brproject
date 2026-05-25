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
package ext.mods.gameserver.handler.bypasshandlers;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.DropCalc;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.SkipData;
import ext.mods.gameserver.enums.DropType;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.enums.skills.ElementType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.attackable.AggroList;
import ext.mods.gameserver.model.actor.container.npc.AggroInfo;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.item.DropCategory;
import ext.mods.gameserver.model.item.DropData;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;

public class UserNpcInfo implements IBypassHandler {

	private static final String[] COMMANDS = { "user_npc_info" };
	
	private static final DecimalFormat PERCENT = new DecimalFormat("#.###");
	public static final int PAGE_LIMIT_1 = 1;
	public static final int PAGE_LIMIT_10 = 10;
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		try
		{
			final var objId = Integer.parseInt(st.nextToken());
			final var wo = World.getInstance().getObject(objId);
			if (wo instanceof Npc)
			{
				var html = new NpcHtmlMessage(0);
				
				if (!st.hasMoreTokens())
					showNpcStatsInfos(player, (Npc) wo, html);
				else
				{
					var type = st.nextToken();
					switch (type)
					{
						case "aggr":
							showAggrInfo(player, (Npc) wo, html);
							break;
						
						case "drop", "spoil":
							try
							{
								var page = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
								var subPage = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 1;
								
								showNpcInfoDrop(player, (Npc) wo, html, page, subPage, type.equalsIgnoreCase("drop"));
							}
							catch (Exception e)
							{
								showNpcInfoDrop(player, (Npc) wo, html, 1, 1, true);
							}
							break;
						
						case "skill":
							showSkillInfos(player, (Npc) wo, html);
							break;
						
						case "effects":
							try
							{
								var page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
								showNpcInfoEffects(player, (Npc) wo, html, page);
							}
							catch (Exception e)
							{
								showNpcInfoEffects(player, (Npc) wo, html, 1);
							}
							break;
					}
				}
				player.sendPacket(html);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("bypass user_npc_info error", e);
		}
		return true;
	}
	
	public static void showNpcStatsInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/mods/npcinfo/stat.htm");
		
		html.replace("%objid%", npc.getObjectId());
		html.replace("%hp%", npc.isChampion() ? (int) npc.getStatus().getHp() * Config.CHAMPION_HP : (int) npc.getStatus().getHp());
		html.replace("%hpmax%", npc.isChampion() ? npc.getStatus().getMaxHp() * Config.CHAMPION_HP : npc.getStatus().getMaxHp());
		html.replace("%mp%", (int) npc.getStatus().getMp());
		html.replace("%mpmax%", npc.getStatus().getMaxMp());
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
	
	private static void showAggrInfo(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/mods/npcinfo/aggr.htm");
		html.replace("%objid%", npc.getObjectId());
		
		if (!(npc instanceof Attackable))
		{
			html.replace("%content%", "This NPC can't build aggro towards targets.<br><button value=\"Refresh\" action=\"bypass -h user_npc_info " + npc.getObjectId() + " aggr\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">");
			return;
		}
		
		final AggroList aggroList = ((Attackable) npc).getAI().getAggroList();
		if (aggroList.isEmpty())
		{
			html.replace("%content%", "This NPC's AggroList is empty.<br><button value=\"Refresh\" action=\"bypass -h user_npc_info " + npc.getObjectId() + " aggr\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">");
			return;
		}
		
		final StringBuilder sb = new StringBuilder(500);
		sb.append("<button value=\"Refresh\" action=\"bypass -h user_npc_info " + npc.getObjectId() + " aggr\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"><br><table width=\"280\"><tr><td><font color=\"LEVEL\">Attacker</font></td><td><font color=\"LEVEL\">Damage</font></td><td><font color=\"LEVEL\">Hate</font></td></tr>");
		
		for (AggroInfo ai : aggroList.values().stream().sorted(Comparator.comparing(AggroInfo::getHate, Comparator.reverseOrder())).limit(15).toList())
			StringUtil.append(sb, "<tr><td>", ai.getAttacker().getName(), "</td><td>", ai.getDamage(), "</td><td>", ai.getHate(), "</td></tr>");
		
		sb.append("</table><img src=\"L2UI.SquareGray\" width=277 height=1>");
		
		html.replace("%content%", sb.toString());
	}
	
	private static void showNpcInfoDrop(Player player, Npc npc, NpcHtmlMessage html, int page, int subPage, boolean isDrop)
	{
		html.setFile(player.getLocale(), "html/mods/npcinfo/droplist.htm");
		html.replace("%objid%", npc.getObjectId());
		
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
					
					final double normChance = Math.min(99.99, chance);
					
					final double overflowFactor = Math.max(0.0, (chance - 100) / 100);
					final double inverseCategoryChance = (100 - category.getChance()) / 100;
					final double reduceFactor = Math.pow(inverseCategoryChance, 10);
					final double levelFactor = (80.0 - npc.getStatus().getLevel()) / 90;
					int min = drop.getMinDrop();
					int max = drop.getMaxDrop();
					
					min = (int) (min + min * overflowFactor - min * overflowFactor * reduceFactor);
					max = (int) (max + max * overflowFactor - max * overflowFactor * reduceFactor);
					if (category.getDropType() != DropType.CURRENCY)
						min = (int) (min - min * levelFactor);
					min = Math.max(min, drop.getMinDrop());
					if (category.getDropType() != DropType.CURRENCY)
						max = (int) (max - max * levelFactor);
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
						droplist.generatePages("bypass user_npc_info " + ((isDrop) ? "drop" : "spoil") + " " + page + " %page%");
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
					if (droplist.totalEntries() > 10)
					{
						droplist.generateSpace(41);
						droplist.generatePages("bypass user_npc_info " + npc.getObjectId() + " " + ((isDrop) ? "drop" : "spoil") + " " + page + " %page%");
					}
					
					list.append(droplist.getContent());
				}
			}
		}
		
		if (list.totalEntries() > 0)
		{
			list.generateSpace(30);
			list.generatePages("bypass user_npc_info " + npc.getObjectId() + " " + ((isDrop) ? "drop" : "spoil") + " %page% 1");
		}
		
		html.replace("%content%", list.getContent());
	}
	
	private static void showSkillInfos(Player player, Npc npc, NpcHtmlMessage html)
	{
		html.setFile(player.getLocale(), "html/mods/npcinfo/skills.htm");
		html.replace("%objid%", npc.getObjectId());
		
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
	
	private static void showNpcInfoEffects(Player player, Npc npc, NpcHtmlMessage html, int page)
	{
		final int EFFECTS_PER_LIST = 12;
		final Pagination<AbstractEffect> list = new Pagination<>(Arrays.stream(npc.getAllEffects()), page, EFFECTS_PER_LIST);
		
		html.setFile(player.getLocale(), "html/mods/npcinfo/effects.htm");
		html.replace("%objid%", npc.getObjectId());
		
		final StringBuilder sb = new StringBuilder(500);
		
		sb.append("<button value=\"Refresh\" action=\"bypass -h user_npc_info " + npc.getObjectId() + " effects\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">");
		
		if (list.isEmpty())
			sb.append("This NPC's Effects is empty.");
		else
		{
			sb.append("<table width=270><tr><td width=220>Effect</td><td width=50>Time Left</td></tr>");
			
			for (AbstractEffect effect : list)
				StringUtil.append(sb, "<tr><td>", effect.getSkill().getName(), "</td><td>", (effect.getSkill().isToggle()) ? "toggle" : effect.getPeriod() - effect.getTime() + "s", "</td></tr>");
			
			sb.append("</table><br>");
			
			sb.append("<br><img src=\"L2UI.SquareGray\" width=277 height=1><table width=\"100%\" bgcolor=000000><tr>");
		}
		
		html.replace("%content%", sb.toString());
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}