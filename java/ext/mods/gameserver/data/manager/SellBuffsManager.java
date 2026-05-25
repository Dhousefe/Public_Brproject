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
package ext.mods.gameserver.data.manager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ext.mods.commons.data.Pagination;
import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.logging.CLogger;

import ext.mods.Config;
import ext.mods.gameserver.communitybbs.manager.BaseBBSManager;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.enums.actors.OperateType;
import ext.mods.gameserver.model.SellBuffHolder;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.model.records.SellBuffData;
import ext.mods.gameserver.network.serverpackets.PrivateStoreMsgSell;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.l2skills.L2SkillSummon;

import org.w3c.dom.Document;

public class SellBuffsManager implements IXmlReader
{
	private static final CLogger LOGGER = new CLogger(SellBuffsManager.class.getName());
	private static final Set<SellBuffData> ALLOWED_BUFFS = new HashSet<>();
	private static final String HTML_FOLDER = "html/mods/sellbuffs/";
	
	protected SellBuffsManager()
	{
		load();
	}
	
	@Override
	public void load()
	{
		if (Config.SELLBUFF_ENABLED)
		{
			ALLOWED_BUFFS.clear();
			parseDataFile("custom/mods/sellBuffData.xml");
			LOGGER.info(getClass().getSimpleName() + ": Loaded " + ALLOWED_BUFFS.size() + " allowed buffs.");
		}
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "skill", skillNode ->
		{
			final StatSet set = parseAttributes(skillNode);
			final int skillId = set.getInteger("id");
			final int skillTime = set.getInteger("time", 0);
			final boolean applyOnPets = set.getBool("applyOnPets", false);
			
			ALLOWED_BUFFS.add(new SellBuffData(skillId, skillTime, applyOnPets));
		}));
	}
	
	public void sendSellMenu(Player player)
	{
		final String html = HTMLData.getInstance().getHtm(player, HTML_FOLDER + (player.isSellingBuffs() ? "buffmenu_already.htm" : "buffmenu.htm"));
		BaseBBSManager.separateAndSend(html, player);
	}
	
	public void sendBuffChoiceMenu(Player player, int index)
	{
		String html = HTMLData.getInstance().getHtm(player, HTML_FOLDER + "buffchoice.htm");
		html = html.replace("%list%", buildSkillMenu(player, index));
		BaseBBSManager.separateAndSend(html, player);
	}
	
	public void sendBuffEditMenu(Player player, int index)
	{
		String html = HTMLData.getInstance().getHtm(player, HTML_FOLDER + "buffchoice.htm");
		html = html.replace("%list%", buildEditMenu(player, index));
		BaseBBSManager.separateAndSend(html, player);
	}
	
	public void sendBuffMenu(Player player, Player seller, int index)
	{
		if (!seller.isSellingBuffs() || seller.getSellingBuffs().isEmpty())
			return;
		
		String html = HTMLData.getInstance().getHtm(player, HTML_FOLDER + "buffbuymenu.htm");
		html = html.replace("%list%", buildBuffMenu(seller, index));
		BaseBBSManager.separateAndSend(html, player);
	}
	
	public void startSellBuffs(Player player, String title)
	{
		player.sitDown();
		player.setSellingBuffs(true);
		player.setOperateType(OperateType.PACKAGE_SELL);
		player.getSellList().setTitle(title);
		player.getSellList().setPackaged(true);
		player.broadcastUserInfo();
		
		player.broadcastPacket(new PrivateStoreMsgSell(player));
		
		sendSellMenu(player);
	}
	
	public void stopSellBuffs(Player player)
	{
		player.setSellingBuffs(false);
		player.setOperateType(OperateType.NONE);
		player.standUp();
		player.broadcastUserInfo();
		sendSellMenu(player);
	}
	
	private String buildBuffMenu(Player seller, int page)
	{
		final Pagination<SellBuffHolder> list = new Pagination<>(seller.getSellingBuffs().stream(), page, 9);
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<br>");
		sb.append(StringUtil.getMpGauge(250, (long) seller.getStatus().getMp(), seller.getStatus().getMaxMp(), false));
		sb.append("<br>");
		
		for (SellBuffHolder holder : list)
		{
			final L2Skill skill = switch (holder.getSkillId())
			{
				case 4699 -> getBuffSkill(seller, 1331, NpcSkillType.BUFF1);
				case 4700 -> getBuffSkill(seller, 1331, NpcSkillType.BUFF2);
				case 4702 -> getBuffSkill(seller, 1332, NpcSkillType.BUFF1);
				case 4703 -> getBuffSkill(seller, 1332, NpcSkillType.BUFF2);
				default -> seller.getSkill(holder.getSkillId());
			};
			
			if (skill == null)
				continue;
			
			final var item = ItemData.getInstance().getTemplate(Config.SELLBUFF_PAYMENT_ID);
			
			sb.append("<table border=0 cellpadding=0 cellspacing=0 bgcolor=000000 height=32>");
			sb.append("<tr>");
			sb.append("<td width=32 height=32 align=center valign=top><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>");
			sb.append("<td align=left valign=center width=150>&nbsp;" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>"));
			sb.append("<td align=center width=75><img height=6><combobox width=65 var=\"level_" + skill.getId() + "\" list=\"" + levelList(skill.getLevel()) + "\">" + "</td>");
			sb.append("<td align=right width=50> " + (Math.max(skill.getMpConsume(), 50) * Config.SELLBUFF_MP_MULTIPLER) + " <font color=\"1E90FF\">MP</font></td>");
			sb.append("<td align=right width=120> " + StringUtil.formatNumber(holder.getPrice()) + " <font color=\"LEVEL\">" + (item != null ? item.getName() : "") + "</font> </td>");
			
			sb.append("<td width=6></td><td align=center width=14><img height=8><button action=\"bypass sellbuffbuyskill " + seller.getObjectId() + " " + skill.getId() + " " + " $level_" + skill.getId() + " " + holder.getPrice() + " " + page + "\" width=14 height=14 back=L2UI_ch3.QuestWndPlusBtn_over fore=L2UI_ch3.QuestWndPlusBtn></td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<img height=4>");
		}
		
		list.generateSpace(22);
		list.generatePagesMedium("bypass sellbuffbuymenu " + seller.getObjectId() + " %page%", 480);
		
		sb.append(list.getContent());
		
		return sb.toString();
	}
	
	private String buildEditMenu(Player player, int page)
	{
		final Pagination<SellBuffHolder> list = new Pagination<>(player.getSellingBuffs().stream(), page, 9);
		final StringBuilder sb = new StringBuilder();
		
		sb.append("<table border=0 cellpadding=0 cellspacing=0");
		sb.append("<br><br><br>");
		sb.append("<tr>");
		sb.append("<td width=35></td>");
		sb.append("<td width=32 align=center></td>");
		sb.append("<td width=150>Name</td>");
		sb.append("<td width=80>Level</td>");
		sb.append("<td width=80>Old Price</td>");
		sb.append("<td width=60>New Price</td>");
		sb.append("<td width=100></td>");
		sb.append("<td width=20></td>");
		sb.append("</tr>");
		sb.append("</table>");
		
		if (player.getSellingBuffs().isEmpty())
		{
			sb.append("<br><br><br>");
			sb.append("You don't have added any buffs yet!");
		}
		else
		{
			for (SellBuffHolder holder : list)
			{
				final L2Skill skill = switch (holder.getSkillId())
				{
					case 4699 -> getBuffSkill(player, 1331, NpcSkillType.BUFF1);
					case 4700 -> getBuffSkill(player, 1331, NpcSkillType.BUFF2);
					case 4702 -> getBuffSkill(player, 1332, NpcSkillType.BUFF1);
					case 4703 -> getBuffSkill(player, 1332, NpcSkillType.BUFF2);
					default -> player.getSkill(holder.getSkillId());
				};
				
				if (skill == null)
					continue;
				
				sb.append("<table cellpadding=0 cellspacing=0 height=32 bgcolor=000000>");
				sb.append("<tr>");
				sb.append("<td align=center width=32 valign=top><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>");
				sb.append("<td width=6></td>");
				sb.append("<td align=left valign=center width=150>&nbsp;" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>"));
				sb.append("<td width=40>" + holder.getSkillLvl() + "</td>");
				sb.append("<td align=right width=100> " + StringUtil.formatNumber(holder.getPrice()) + " <font color=\"LEVEL\">Adena  </font></td>");
				sb.append("<td width=80><img height=6><edit var=\"price_" + skill.getId() + "\" width=75 height=14 type=\"number\"></td>");
				sb.append("<td width=60 align=center><img height=6><button value=\"Edit\" action=\"bypass sellbuffchangeprice " + skill.getId() + " $price_" + skill.getId() + "\" width=\"65\" height=\"21\" back=\"smallbutton2_over\" fore=\"smallbutton2\"></td>");
				sb.append("<td width=6></td><td width=20 valign=center><img height=8><button action=\"bypass sellbuffremove " + skill.getId() + "\" width=16 height=16 back=L2UI_ch3.FrameCloseOnBtn fore=L2UI_ch3.FrameCloseBtn></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<img height=4>");
			}
		}
		
		list.generateSpace(22);
		list.generatePagesMedium("bypass sellbuffedit %page%", 529);
		
		sb.append(list.getContent());
		
		return sb.toString();
	}
	
	private String buildSkillMenu(Player player, int page)
	{
		List<L2Skill> skillList = new ArrayList<>();
		
		addBuffSkills(player, 1331, skillList);
		addBuffSkills(player, 1332, skillList);
		
		for (L2Skill skill : player.getSkills().values())
		{
			if (ALLOWED_BUFFS.stream().anyMatch(buff -> buff.getSkillId() == skill.getId()) && !isInSellList(player, skill))
				skillList.add(skill);
		}
		
		final Pagination<L2Skill> list = new Pagination<>(skillList.stream(), page, 9);
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<br><br><br>");
		sb.append("<table cellpadding=0 cellspacing=0>");
		sb.append("<tr>");
		sb.append("<td width=32 align=center></td>");
		sb.append("<td width=6></td>");
		sb.append("<td width=150>Name</td>");
		sb.append("<td align=center width=75>Level</td>");
		sb.append("<td align=center width=100>Price</td>");
		sb.append("<td width=6></td><td align=center width=14></td>");
		sb.append("</tr>");
		sb.append("</table>");
		
		if (list.isEmpty())
		{
			sb.append("<br><br><br>");
			sb.append("At this moment you cannot add any buffs!");
		}
		else
		{
			for (L2Skill skill : list)
			{
				sb.append("<table cellpadding=0 cellspacing=0 height=32 bgcolor=000000>");
				sb.append("<tr>");
				sb.append("<td align=center width=32 valign=top><img src=\"" + skill.getIcon() + "\" width=\"32\" height=\"32\"></td>");
				sb.append("<td width=6></td>");
				sb.append("<td align=left valign=center width=150>" + skill.getName() + (skill.getLevel() > 100 ? "<font color=\"LEVEL\"> + " + (skill.getLevel() % 100) + "</font></td>" : "</td>"));
				sb.append("<td align=center width=75><img height=6>" + skill.getLevel() + " <font color=LEVEL>Lv.</font></td>");
				sb.append("<td align=center><img height=6><edit var=\"price_" + skill.getId() + "\" width=100 height=11 type=\"number\"></td>");
				sb.append("<td width=6></td><td align=center width=14><img height=8><button action=\"bypass sellbuffaddskill " + skill.getId() + " " + " $price_" + skill.getId() + " " + page + "\" width=14 height=14 back=L2UI_ch3.QuestWndPlusBtn_over fore=L2UI_ch3.QuestWndPlusBtn></td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("<img height=4>");
			}
		}
		
		list.generateSpace(22);
		list.generatePagesMedium("bypass sellbuffadd %page%", 403);
		
		sb.append(list.getContent());
		return sb.toString();
	}
	
	public static String levelList(int maxLevel)
	{
		StringBuilder str = new StringBuilder();
		str.append(maxLevel);
		if (maxLevel > 1)
			str.append(";");
		
		for (int i = maxLevel - 1; i > 0; i--)
		{
			str.append(i);
			if (i != 1)
				str.append(";");
		}
		
		return str.toString();
	}
	
	public boolean isInSellList(Player player, L2Skill skill)
	{
		for (SellBuffHolder holder : player.getSellingBuffs())
		{
			if (holder.getSkillId() == skill.getId())
				return true;
		}
		return false;
	}
	
	public boolean canStartSellBuffs(Player player)
	{
		if (player.isAlikeDead())
		{
			player.sendMessage("You can't sell buffs in fake death!");
			return false;
		}
		else if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage("You can't sell buffs with Olympiad status!");
			return false;
		}
		else if (CTFEvent.getInstance().isPlayerParticipant(player.getObjectId()) || TvTEvent.getInstance().isPlayerParticipant(player.getObjectId()) || LMEvent.getInstance().isPlayerParticipant(player.getObjectId()) || DMEvent.getInstance().isPlayerParticipant(player.getObjectId()))
		{
			player.sendMessage("You can't sell buffs while registered in an event!");
			return false;
		}
		else if (player.isCursedWeaponEquipped() || player.getKarma() > 0)
		{
			player.sendMessage("You can't sell buffs in Chaotic state!");
			return false;
		}
		else if (player.isInDuel())
		{
			player.sendMessage("You can't sell buffs in Duel state!");
			return false;
		}
		else if (player.isFishing())
		{
			player.sendMessage("You can't sell buffs while fishing.");
			return false;
		}
		else if (player.isMounted() || player.isFlying())
		{
			player.sendMessage("You can't sell buffs in Mount state!");
			return false;
		}
		else if (player.isInsideZone(ZoneId.NO_STORE) || !player.isInsideZone(ZoneId.PEACE) || player.isInJail())
		{
			player.sendMessage("You can't sell buffs here!");
			return false;
		}
		return true;
	}
	
	private void addBuffSkills(Player player, int skillId, List<L2Skill> skillList)
	{
		if (player.getSkill(skillId) instanceof L2SkillSummon skillSummon)
		{
			final NpcTemplate summonTemplate = NpcData.getInstance().getTemplate(skillSummon.getNpcId());
			for (NpcSkillType skillType : NpcSkillType.values())
			{
				L2Skill skill = summonTemplate.getSkill(skillType);
				if (skill != null && ALLOWED_BUFFS.stream().anyMatch(buff -> buff.getSkillId() == skill.getId()) && !isInSellList(player, skill))
					skillList.add(skill);
			}
		}
	}
	
	public L2Skill getBuffSkill(Player player, int summonSkillId, NpcSkillType buffType)
	{
		if (player.getSkill(summonSkillId) instanceof L2SkillSummon skillSummon)
		{
			final NpcTemplate summonTemplate = NpcData.getInstance().getTemplate(skillSummon.getNpcId());
			return summonTemplate.getSkill(buffType);
		}
		return null;
	}
	
	public SellBuffData getBuff(L2Skill skill)
	{
		return ALLOWED_BUFFS.stream().filter(buff -> buff.getSkillId() == skill.getId()).findFirst().orElse(null);
	}
	
	public static SellBuffsManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SellBuffsManager INSTANCE = new SellBuffsManager();
	}
}