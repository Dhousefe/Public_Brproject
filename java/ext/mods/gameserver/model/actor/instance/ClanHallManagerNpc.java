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

import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.enums.TeleportType;
import ext.mods.gameserver.enums.actors.NpcTalkCond;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.ai.type.ClanHallManagerNpcAI;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.ClanHall.Buff;
import ext.mods.gameserver.model.residence.clanhall.ClanHallFunction;
import ext.mods.gameserver.model.residence.clanhall.SiegableHall;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ClanHallDecoration;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.WarehouseDepositList;
import ext.mods.gameserver.network.serverpackets.WarehouseWithdrawList;
import ext.mods.gameserver.skills.L2Skill;

public class ClanHallManagerNpc extends Merchant
{
	private static final String HP_GRADE_1 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 2\">40%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 5\">100%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 8\">160%</a>]";
	private static final String HP_GRADE_2 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 4\">80%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 7\">140%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 10\">200%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 260\">260%</a>]";
	private static final String HP_GRADE_3 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 4\">80%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 6\">120%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 9\">180%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 12\">240%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 15\">300%</a>]";
	private static final String HP_GRADE_2_SCH = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 25\">300%</a>]";
	private static final String HP_GRADE_3_SCH = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 25\">300%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 30\">400%</a>]";
	
	private static final String EXP_GRADE_1 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 1\">5%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 3\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 6\">30%</a>]";
	private static final String EXP_GRADE_2 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 1\">5%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 3\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 5\">25%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 8\">40%</a>]";
	private static final String EXP_GRADE_3 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 3\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 5\">25%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 7\">35%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 10\">50%</a>]";
	private static final String EXP_GRADE_2_SCH = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 19\">45%</a>]";
	private static final String EXP_GRADE_3_SCH = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 19\">45%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 20\">50%</a>]";
	
	private static final String MP_GRADE_1 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 1\">5%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 3\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 5\">25%</a>]";
	private static final String MP_GRADE_2 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 1\">5%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 3\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 6\">30%</a>]";
	private static final String MP_GRADE_3 = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 1\">5%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 3\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 6\">30%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 8\">40%</a>]";
	private static final String MP_GRADE_2_SCH = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 18\">40%</a>]";
	private static final String MP_GRADE_3_SCH = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 18\">40%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 20\">50%</a>]";
	
	private static final String SUPPORT_GRADE_1 = "[<a action=\"bypass -h npc_%objectId%_manage other edit_support 1\">1st Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 2\">2nd Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 4\">4th Level</a>]";
	private static final String SUPPORT_GRADE_2 = "[<a action=\"bypass -h npc_%objectId%_manage other edit_support 3\">3st Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 4\">4th Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 5\">5th Level</a>]";
	private static final String SUPPORT_GRADE_3 = "[<a action=\"bypass -h npc_%objectId%_manage other edit_support 3\">3st Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 5\">5th Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 7\">7th Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 8\">8th Level</a>]";
	private static final String SUPPORT_GRADE_2_SCH = "[<a action=\"bypass -h npc_%objectId%_manage other edit_support 15\">Level 5</a>]";
	private static final String SUPPORT_GRADE_3_SCH = "[<a action=\"bypass -h npc_%objectId%_manage other edit_support 15\">Level 5</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 18\">Level 8</a>]";
	
	private static final String ITEM = "[<a action=\"bypass -h npc_%objectId%_manage other edit_item 1\">1st Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_item 2\">2nd Stage</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_item 3\">3st Stage</a>]";
	private static final String ITEM_SCH = "[<a action=\"bypass -h npc_%objectId%_manage other edit_item 11\">Level 1</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_item 12\">Level 2</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_item 13\">Level 3</a>]";
	
	private static final String TELE = "[<a action=\"bypass -h npc_%objectId%_manage other edit_tele 1\">1st Level</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_tele 2\">2nd Stage</a>]";
	private static final String TELE_SCH = "[<a action=\"bypass -h npc_%objectId%_manage other edit_tele 1\">Level 11</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_tele 12\">Level 2</a>]";
	
	private static final String CURTAINS = "[<a action=\"bypass -h npc_%objectId%_manage deco edit_curtains 1\">1st Stage</a>][<a action=\"bypass -h npc_%objectId%_manage deco edit_curtains 2\">2nd Stage</a>]";
	private static final String FIXTURES = "[<a action=\"bypass -h npc_%objectId%_manage deco edit_fixtures 1\">1st Stage</a>][<a action=\"bypass -h npc_%objectId%_manage deco edit_fixtures 2\">2nd Stage</a>]";
	
	public ClanHallManagerNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public ClanHallManagerNpcAI getAI()
	{
		return (ClanHallManagerNpcAI) _ai;
	}
	
	@Override
	public void setAI()
	{
		_ai = new ClanHallManagerNpcAI(this);
	}
	
	@Override
	public boolean isWarehouse()
	{
		return true;
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		final NpcTalkCond condition = getNpcTalkCond(player);
		if (condition != NpcTalkCond.OWNER)
			return;
		
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		
		String val = (st.hasMoreTokens()) ? st.nextToken() : "";
		
		if (actualCommand.equalsIgnoreCase("banish_foreigner"))
		{
			if (!validatePrivileges(player, PrivilegeType.CHP_RIGHT_TO_DISMISS))
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			if (val.equalsIgnoreCase("list"))
				html.setFile(player.getLocale(), "html/clanHallManager/banish-list.htm");
			else if (val.equalsIgnoreCase("banish"))
			{
				getClanHall().banishForeigners();
				html.setFile(player.getLocale(), "html/clanHallManager/banish.htm");
			}
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("manage_vault"))
		{
			if (!validatePrivileges(player, PrivilegeType.SP_WAREHOUSE_SEARCH))
				return;
			
			final boolean isSCH = (getClanHall() instanceof SiegableHall);
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/clanHallManager/vault" + (isSCH ? "_sch" : "") + ".htm");
			html.replace("%rent%", getClanHall().getLease());
			html.replace("%date%", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(getClanHall().getPaidUntil()));
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("door"))
		{
			if (!validatePrivileges(player, PrivilegeType.CHP_ENTRY_EXIT_RIGHTS))
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			if (val.equalsIgnoreCase("open"))
			{
				getClanHall().openDoors();
				html.setFile(player.getLocale(), "html/clanHallManager/door-open.htm");
			}
			else if (val.equalsIgnoreCase("close"))
			{
				getClanHall().closeDoors();
				html.setFile(player.getLocale(), "html/clanHallManager/door-close.htm");
			}
			else
				html.setFile(player.getLocale(), "html/clanHallManager/door.htm");
			
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("functions"))
		{
			if (!validatePrivileges(player, PrivilegeType.CHP_USE_FUNCTIONS))
				return;
			
			if (val.equalsIgnoreCase("tele"))
			{
				final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_TELEPORT);
				if (chf == null)
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/clanHallManager/chamberlain-nac.htm");
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					return;
				}
				
				showTeleportWindow(player, (chf.getLvl() == 2) ? TeleportType.CHF_LEVEL_2 : TeleportType.CHF_LEVEL_1);
			}
			else if (val.equalsIgnoreCase("item_creation"))
			{
				if (!st.hasMoreTokens())
					return;
				
				final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE);
				if (chf == null)
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/clanHallManager/chamberlain-nac.htm");
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					return;
				}
				
				showBuyWindow(player, Integer.parseInt(st.nextToken()) + (chf.getLvl() * 100000));
			}
			else if (val.equalsIgnoreCase("support"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				StringBuilder buffsHtml = new StringBuilder();
				for (Buff buff : getClanHall().getBuffs())
					buffsHtml.append("<a action=\"bypass -h npc_%objectId%_support " + buff.id() + " " + buff.lvl() + "\">" + buff.desc() + "</a><br1>");
				
				final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_SUPPORT);
				if (chf == null)
					html.setFile(player.getLocale(), "html/clanHallManager/chamberlain-nac.htm");
				else
				{
					html.setFile(player.getLocale(), "html/clanHallManager/support" + chf.getLvl() + ".htm");
					html.replace("%mp%", (int) getStatus().getMp());
					html.replace("%buffs%", buffsHtml.toString());
				}
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
			else if (val.equalsIgnoreCase("back"))
				showChatWindow(player);
			else
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/clanHallManager/functions.htm");
				
				final ClanHallFunction chfExp = getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP);
				if (chfExp != null)
					html.replace("%xp_regen%", chfExp.getLvl());
				else
					html.replace("%xp_regen%", "0");
				
				final ClanHallFunction chfHp = getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP);
				if (chfHp != null)
					html.replace("%hp_regen%", chfHp.getLvl());
				else
					html.replace("%hp_regen%", "0");
				
				final ClanHallFunction chfMp = getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP);
				if (chfMp != null)
					html.replace("%mp_regen%", chfMp.getLvl());
				else
					html.replace("%mp_regen%", "0");
				
				html.replace("%npcId%", getNpcId());
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (actualCommand.equalsIgnoreCase("manage"))
		{
			if (!validatePrivileges(player, PrivilegeType.CHP_SET_FUNCTIONS))
				return;
			
			if (val.equalsIgnoreCase("recovery"))
			{
				if (st.hasMoreTokens())
				{
					if (getClanHall().getOwnerId() == 0)
						return;
					
					val = st.nextToken();
					
					if (val.equalsIgnoreCase("hp_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel.htm");
						html.replace("%apply%", "recovery hp 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("mp_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel.htm");
						html.replace("%apply%", "recovery mp 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("exp_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel.htm");
						html.replace("%apply%", "recovery exp 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_hp"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply.htm");
						html.replace("%name%", "Fireplace (HP Recovery Device)");
						
						int level = Integer.parseInt(st.nextToken());
						final int funcLvl = level;
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_HP, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_RESTORE_HP, level);
						if (level > 20)
							level -= 10;
						final int percent = level * 20;
						
						html.replace("%cost%", cost + "</font> adenas / " + days + " day</font>)");
						html.replace("%use%", "Provides additional HP recovery for clan members in the clan hall.<font color=\"00FFFF\">" + percent + "%</font>");
						html.replace("%apply%", "recovery hp " + funcLvl);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_mp"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply.htm");
						html.replace("%name%", "Carpet (MP Recovery)");
						
						int level = Integer.parseInt(st.nextToken());
						final int funcLvl = level;
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_MP, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_RESTORE_MP, level);
						
						if (level > 10)
							level -= 10;
						
						final int percent = level * 5;
						
						html.replace("%cost%", cost + "</font> adenas / " + days + " day</font>)");
						html.replace("%use%", "Provides additional MP recovery for clan members in the clan hall.<font color=\"00FFFF\">" + percent + "%</font>");
						html.replace("%apply%", "recovery mp " + funcLvl);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_exp"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply.htm");
						html.replace("%name%", "Chandelier (EXP Recovery Device)");
						
						int level = Integer.parseInt(st.nextToken());
						final int funcLvl = level;
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_EXP, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_RESTORE_EXP, level);
						
						if (level > 10)
							level -= 10;
						
						final int percent = level * 5;
						
						html.replace("%cost%", cost + "</font> adenas / " + days + " day</font>)");
						html.replace("%use%", "Restores the Exp of any clan member who is resurrected in the clan hall.<font color=\"00FFFF\">" + percent + "%</font>");
						html.replace("%apply%", "recovery exp " + funcLvl);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("hp"))
					{
						int level = Integer.parseInt(st.nextToken());
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_HP, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_RESTORE_HP, level);
						
						if (level > 20)
							level -= 10;
						
						final int percent = level * 20;
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP);
						if (chf != null && chf.getLvl() == percent)
						{
							html.setFile(player.getLocale(), "html/clanHallManager/functions-used.htm");
							html.replace("%val%", level + "%");
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply_confirmed.htm");
						
						int fee = cost;
						if (percent == 0)
						{
							fee = 0;
							html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel_confirmed.htm");
						}
						
						if (!getClanHall().updateFunction(player, ClanHall.FUNC_RESTORE_HP, percent, fee, TimeUnit.DAYS.toMillis(days)))
							html.setFile(player.getLocale(), "html/clanHallManager/low_adena.htm");
						else
							revalidateDeco(player);
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("mp"))
					{
						int level = Integer.parseInt(st.nextToken());
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_MP, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_RESTORE_MP, level);
						
						if (level > 10)
							level -= 10;
						
						final int percent = level * 5;
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP);
						if (chf != null && chf.getLvl() == percent)
						{
							html.setFile(player.getLocale(), "html/clanHallManager/functions-used.htm");
							html.replace("%val%", level + "%");
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply_confirmed.htm");
						
						int fee = cost;
						if (percent == 0)
						{
							fee = 0;
							html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel_confirmed.htm");
						}
						
						if (!getClanHall().updateFunction(player, ClanHall.FUNC_RESTORE_MP, percent, fee, TimeUnit.DAYS.toMillis(days)))
							html.setFile(player.getLocale(), "html/clanHallManager/low_adena.htm");
						else
							revalidateDeco(player);
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("exp"))
					{
						int level = Integer.parseInt(st.nextToken());
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_EXP, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_RESTORE_EXP, level);
						
						if (level > 20)
							level -= 10;
						
						final int percent = level * 5;
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP);
						if (chf != null && chf.getLvl() == percent)
						{
							html.setFile(player.getLocale(), "html/clanHallManager/functions-used.htm");
							html.replace("%val%", level + "%");
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply_confirmed.htm");
						
						int fee = cost;
						if (percent == 0)
						{
							fee = 0;
							html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel_confirmed.htm");
						}
						
						if (!getClanHall().updateFunction(player, ClanHall.FUNC_RESTORE_EXP, percent, fee, TimeUnit.DAYS.toMillis(days)))
							html.setFile(player.getLocale(), "html/clanHallManager/low_adena.htm");
						else
							revalidateDeco(player);
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
				}
				else
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/clanHallManager/edit_recovery.htm");
					
					final int grade = getClanHall().getGrade();
					final boolean isSCH = (getClanHall() instanceof SiegableHall);
					
					final ClanHallFunction chfHp = getClanHall().getFunction(ClanHall.FUNC_RESTORE_HP);
					if (chfHp != null)
					{
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_HP, chfHp.getFuncLvl());
						html.replace("%hp_recovery%", chfHp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfHp.getLease() + "</font> adenas / " + days + " day)");
						html.replace("%hp_period%", "Next fee at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfHp.getEndTime()));
						
						switch (grade)
						{
							case 1:
								html.replace("%change_hp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery hp_cancel\">Stops using</a>]" + HP_GRADE_1);
								break;
							
							case 2:
								html.replace("%change_hp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery hp_cancel\">Remove</a>]" + (isSCH ? HP_GRADE_2_SCH : HP_GRADE_2));
								break;
							
							case 3:
								html.replace("%change_hp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery hp_cancel\">Remove</a>]" + (isSCH ? HP_GRADE_3_SCH : HP_GRADE_3));
								break;
						}
					}
					else
					{
						html.replace("%hp_recovery%", "Suspended");
						html.replace("%hp_period%", "Suspended");
						
						switch (grade)
						{
							case 1:
								html.replace("%change_hp%", HP_GRADE_1);
								break;
							
							case 2:
								html.replace("%change_hp%", (isSCH ? HP_GRADE_2_SCH : HP_GRADE_2));
								break;
							
							case 3:
								html.replace("%change_hp%", (isSCH ? HP_GRADE_3_SCH : HP_GRADE_3));
								break;
						}
					}
					
					final ClanHallFunction chfExp = getClanHall().getFunction(ClanHall.FUNC_RESTORE_EXP);
					if (chfExp != null)
					{
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_EXP, chfExp.getFuncLvl());
						html.replace("%exp_recovery%", chfExp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfExp.getLease() + "</font> adenas / " + days + " day)");
						html.replace("%exp_period%", "Next fee at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfExp.getEndTime()));
						
						switch (grade)
						{
							case 1:
								html.replace("%change_exp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery exp_cancel\">Stops using</a>]" + EXP_GRADE_1);
								break;
							
							case 2:
								html.replace("%change_exp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery exp_cancel\">Remove</a>]" + (isSCH ? EXP_GRADE_2_SCH : EXP_GRADE_2));
								break;
							
							case 3:
								html.replace("%change_exp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery exp_cancel\">Remove</a>]" + (isSCH ? EXP_GRADE_3_SCH : EXP_GRADE_3));
								break;
						}
					}
					else
					{
						html.replace("%exp_recovery%", "Suspended");
						html.replace("%exp_period%", "Suspended");
						
						switch (grade)
						{
							case 1:
								html.replace("%change_exp%", EXP_GRADE_1);
								break;
							
							case 2:
								html.replace("%change_exp%", (isSCH ? EXP_GRADE_2_SCH : EXP_GRADE_2));
								break;
							
							case 3:
								html.replace("%change_exp%", (isSCH ? EXP_GRADE_3_SCH : EXP_GRADE_3));
								break;
						}
					}
					
					final ClanHallFunction chfMp = getClanHall().getFunction(ClanHall.FUNC_RESTORE_MP);
					if (chfMp != null)
					{
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_RESTORE_MP, chfMp.getFuncLvl());
						html.replace("%mp_recovery%", chfMp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfMp.getLease() + "</font> adenas / " + days + " day)");
						html.replace("%mp_period%", "Next fee at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfMp.getEndTime()));
						
						switch (grade)
						{
							case 1:
								html.replace("%change_mp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery mp_cancel\">Stops using</a>]" + MP_GRADE_1);
								break;
							
							case 2:
								html.replace("%change_mp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery mp_cancel\">Remove</a>]" + (isSCH ? MP_GRADE_2_SCH : MP_GRADE_2));
								break;
							
							case 3:
								html.replace("%change_mp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery mp_cancel\">Remove</a>]" + (isSCH ? MP_GRADE_3_SCH : MP_GRADE_3));
								break;
						}
					}
					else
					{
						html.replace("%mp_recovery%", "Suspended");
						html.replace("%mp_period%", "Suspended");
						
						switch (grade)
						{
							case 1:
								html.replace("%change_mp%", MP_GRADE_1);
								break;
							
							case 2:
								html.replace("%change_mp%", (isSCH ? MP_GRADE_2_SCH : MP_GRADE_2));
								break;
							
							case 3:
								html.replace("%change_mp%", (isSCH ? MP_GRADE_3_SCH : MP_GRADE_3));
								break;
						}
					}
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
			}
			else if (val.equalsIgnoreCase("other"))
			{
				if (st.hasMoreTokens())
				{
					if (getClanHall().getOwnerId() == 0)
						return;
					
					val = st.nextToken();
					
					if (val.equalsIgnoreCase("item_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel.htm");
						html.replace("%apply%", "other item 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("tele_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel.htm");
						html.replace("%apply%", "other tele 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("support_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel.htm");
						html.replace("%apply%", "other support 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_item"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply.htm");
						html.replace("%name%", "Magic Equipment (Item Production Facilities)");
						
						int level = Integer.parseInt(st.nextToken());
						final int funcLvl = level;
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_ITEM_CREATE, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_ITEM_CREATE, level);
						
						if (level > 10)
							level -= 10;
						
						html.replace("%cost%", cost + "</font> adenas / " + days + " day</font>)");
						html.replace("%use%", "Allow the purchase of special items at fixed intervals.");
						html.replace("%apply%", "other item " + funcLvl);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_support"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply.htm");
						html.replace("%name%", "Insignia (Supplementary Magic)");
						
						int level = Integer.parseInt(st.nextToken());
						final int funcLvl = level;
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_SUPPORT, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_SUPPORT, level);
						
						if (level > 10)
							level -= 10;
						
						html.replace("%cost%", cost + "</font> adenas / " + days + " day</font>)");
						html.replace("%use%", "Enables the use of supplementary magic.");
						html.replace("%apply%", "other support " + funcLvl);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_tele"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply.htm");
						html.replace("%name%", "Mirror (Teleportation Device)");
						
						int level = Integer.parseInt(st.nextToken());
						final int funcLvl = level;
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_TELEPORT, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_TELEPORT, level);
						if (level > 10)
							level -= 10;
						
						html.replace("%cost%", cost + "</font> adenas / " + days + " day</font>)");
						html.replace("%use%", "Teleports clan members in a clan hall to the target <font color=\"00FFFF\">Stage " + level + "</font> staging area");
						html.replace("%apply%", "other tele " + funcLvl);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("item"))
					{
						if (getClanHall().getOwnerId() == 0)
							return;
						
						int level = Integer.parseInt(st.nextToken());
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_ITEM_CREATE, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_ITEM_CREATE, level);
						if (level > 10)
							level -= 10;
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE);
						if (chf != null && chf.getLvl() == level)
						{
							html.setFile(player.getLocale(), "html/clanHallManager/functions-used.htm");
							html.replace("%val%", "Stage " + val);
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply_confirmed.htm");
						
						int fee = cost;
						if (level == 0)
						{
							fee = 0;
							html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel_confirmed.htm");
						}
						
						if (!getClanHall().updateFunction(player, ClanHall.FUNC_ITEM_CREATE, level, fee, TimeUnit.DAYS.toMillis(days)))
							html.setFile(player.getLocale(), "html/clanHallManager/low_adena.htm");
						else
							revalidateDeco(player);
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("tele"))
					{
						int level = Integer.parseInt(st.nextToken());
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_TELEPORT, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_TELEPORT, level);
						
						if (level > 10)
							level -= 10;
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_TELEPORT);
						if (chf != null && chf.getLvl() == level)
						{
							html.setFile(player.getLocale(), "html/clanHallManager/functions-used.htm");
							html.replace("%val%", "Stage " + level);
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply_confirmed.htm");
						
						int fee = cost;
						if (level == 0)
						{
							fee = 0;
							html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel_confirmed.htm");
						}
						
						if (!getClanHall().updateFunction(player, ClanHall.FUNC_TELEPORT, level, fee, TimeUnit.DAYS.toMillis(days)))
							html.setFile(player.getLocale(), "html/clanHallManager/low_adena.htm");
						else
							revalidateDeco(player);
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("support"))
					{
						int level = Integer.parseInt(st.nextToken());
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_SUPPORT, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_SUPPORT, level);
						
						if (level > 10)
							level -= 10;
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_SUPPORT);
						if (chf != null && chf.getLvl() == level)
						{
							html.setFile(player.getLocale(), "html/clanHallManager/functions-used.htm");
							html.replace("%val%", "Stage " + val);
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply_confirmed.htm");
						
						int fee = cost;
						if (level == 0)
						{
							fee = 0;
							html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel_confirmed.htm");
						}
						
						if (!getClanHall().updateFunction(player, ClanHall.FUNC_SUPPORT, level, fee, TimeUnit.DAYS.toMillis(days)))
							html.setFile(player.getLocale(), "html/clanHallManager/low_adena.htm");
						else
						{
							getAI().resetBuffCheckTime();
							revalidateDeco(player);
						}
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
				}
				else
				{
					final boolean isSCH = (getClanHall() instanceof SiegableHall);
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/clanHallManager/edit_other.htm");
					
					final ClanHallFunction chfTel = getClanHall().getFunction(ClanHall.FUNC_TELEPORT);
					if (chfTel != null)
					{
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_TELEPORT, chfTel.getFuncLvl());
						html.replace("%tele%", "- Stage " + chfTel.getLvl() + "</font> (<font color=\"FFAABB\">" + chfTel.getLease() + "</font> adenas / " + days + " day)");
						html.replace("%tele_period%", "Next fee at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfTel.getEndTime()));
						html.replace("%change_tele%", "[<a action=\"bypass -h npc_%objectId%_manage other tele_cancel\">Remove</a>]" + (isSCH ? TELE_SCH : TELE));
					}
					else
					{
						html.replace("%tele%", "Suspended");
						html.replace("%tele_period%", "Suspended");
						html.replace("%change_tele%", (isSCH ? TELE_SCH : TELE));
					}
					
					final int grade = getClanHall().getGrade();
					final ClanHallFunction chfSup = getClanHall().getFunction(ClanHall.FUNC_SUPPORT);
					if (chfSup != null)
					{
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_SUPPORT, chfSup.getFuncLvl());
						html.replace("%support%", "- Stage " + chfSup.getLvl() + "</font> (<font color=\"FFAABB\">" + chfSup.getLease() + "</font> adenas / " + days + " day)");
						html.replace("%support_period%", "Next fee at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfSup.getEndTime()));
						
						switch (grade)
						{
							case 1:
								html.replace("%change_support%", "[<a action=\"bypass -h npc_%objectId%_manage other support_cancel\">Stops using</a>]" + SUPPORT_GRADE_1);
								break;
							
							case 2:
								html.replace("%change_support%", "[<a action=\"bypass -h npc_%objectId%_manage other support_cancel\">Remove</a>]" + (isSCH ? SUPPORT_GRADE_2_SCH : SUPPORT_GRADE_2));
								break;
							
							case 3:
								html.replace("%change_support%", "[<a action=\"bypass -h npc_%objectId%_manage other support_cancel\">Remove</a>]" + (isSCH ? SUPPORT_GRADE_3_SCH : SUPPORT_GRADE_3));
								break;
						}
					}
					else
					{
						html.replace("%support%", "Suspended");
						html.replace("%support_period%", "Suspended");
						
						switch (grade)
						{
							case 1:
								html.replace("%change_support%", SUPPORT_GRADE_1);
								break;
							
							case 2:
								html.replace("%change_support%", (isSCH ? SUPPORT_GRADE_2_SCH : SUPPORT_GRADE_2));
								break;
							
							case 3:
								html.replace("%change_support%", (isSCH ? SUPPORT_GRADE_3_SCH : SUPPORT_GRADE_3));
								break;
						}
					}
					
					final ClanHallFunction chfCreate = getClanHall().getFunction(ClanHall.FUNC_ITEM_CREATE);
					if (chfCreate != null)
					{
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_ITEM_CREATE, chfCreate.getFuncLvl());
						html.replace("%item%", "- Stage " + chfCreate.getLvl() + "</font> (<font color=\"FFAABB\">" + chfCreate.getLease() + "</font> adenas / " + days + " day)");
						html.replace("%item_period%", "Next fee at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfCreate.getEndTime()));
						html.replace("%change_item%", "[<a action=\"bypass -h npc_%objectId%_manage other item_cancel\">Remove</a>]" + (isSCH ? ITEM_SCH : ITEM));
					}
					else
					{
						html.replace("%item%", "Suspended");
						html.replace("%item_period%", "Suspended");
						html.replace("%change_item%", (isSCH ? ITEM_SCH : ITEM));
					}
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
			}
			else if (val.equalsIgnoreCase("deco"))
			{
				if (st.hasMoreTokens())
				{
					if (getClanHall().getOwnerId() == 0)
						return;
					
					val = st.nextToken();
					if (val.equalsIgnoreCase("curtains_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel.htm");
						html.replace("%apply%", "deco curtains 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("fixtures_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel.htm");
						html.replace("%apply%", "deco fixtures 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_curtains"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply.htm");
						html.replace("%name%", "Curtains (Decoration)");
						
						int level = Integer.parseInt(st.nextToken());
						final int funcLvl = level;
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_DECO_CURTAINS, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_DECO_CURTAINS, level);
						
						if (level > 10)
							level -= 10;
						
						html.replace("%cost%", cost + "</font> adenas / " + days + " day</font>)");
						html.replace("%use%", "These curtains can be used to decorate the clan hall.");
						html.replace("%apply%", "deco curtains " + funcLvl);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_fixtures"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply.htm");
						html.replace("%name%", "Front Platform (Decoration)");
						
						int level = Integer.parseInt(st.nextToken());
						final int funcLvl = level;
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_DECO_FRONTPLATEFORM, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_DECO_FRONTPLATEFORM, level);
						
						if (level > 10)
							level -= 10;
						
						html.replace("%cost%", cost + "</font> adenas / " + days + " day</font>)");
						html.replace("%use%", "Used to decorate the clan hall.");
						html.replace("%apply%", "deco fixtures " + funcLvl);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("curtains"))
					{
						int level = Integer.parseInt(st.nextToken());
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_DECO_CURTAINS, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_DECO_CURTAINS, level);
						
						if (level > 10)
							level -= 10;
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_DECO_CURTAINS);
						if (chf != null && chf.getLvl() == level)
						{
							html.setFile(player.getLocale(), "html/clanHallManager/functions-used.htm");
							html.replace("%val%", "Stage " + val);
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply_confirmed.htm");
						
						int fee = cost;
						if (level == 0)
						{
							fee = 0;
							html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel_confirmed.htm");
						}
						
						if (!getClanHall().updateFunction(player, ClanHall.FUNC_DECO_CURTAINS, level, fee, TimeUnit.DAYS.toMillis(days)))
							html.setFile(player.getLocale(), "html/clanHallManager/low_adena.htm");
						else
							revalidateDeco(player);
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("fixtures"))
					{
						int level = Integer.parseInt(st.nextToken());
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_DECO_FRONTPLATEFORM, level);
						final int cost = ClanHallManager.getInstance().getDecoFee(ClanHall.FUNC_DECO_FRONTPLATEFORM, level);
						
						if (level > 10)
							level -= 10;
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM);
						if (chf != null && chf.getLvl() == level)
						{
							html.setFile(player.getLocale(), "html/clanHallManager/functions-used.htm");
							html.replace("%val%", "Stage " + val);
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/clanHallManager/functions-apply_confirmed.htm");
						
						int fee = cost;
						if (level == 0)
						{
							fee = 0;
							html.setFile(player.getLocale(), "html/clanHallManager/functions-cancel_confirmed.htm");
						}
						
						if (!getClanHall().updateFunction(player, ClanHall.FUNC_DECO_FRONTPLATEFORM, level, fee, TimeUnit.DAYS.toMillis(days)))
							html.setFile(player.getLocale(), "html/clanHallManager/low_adena.htm");
						else
							revalidateDeco(player);
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
				}
				else
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/clanHallManager/deco.htm");
					
					final ClanHallFunction chfCurtains = getClanHall().getFunction(ClanHall.FUNC_DECO_CURTAINS);
					if (chfCurtains != null)
					{
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_DECO_CURTAINS, chfCurtains.getFuncLvl());
						html.replace("%curtain%", "- Stage " + chfCurtains.getLvl() + "</font> (<font color=\"FFAABB\">" + chfCurtains.getLease() + "</font> adenas / " + days + " day)");
						html.replace("%curtain_period%", "Next fee at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfCurtains.getEndTime()));
						html.replace("%change_curtain%", "[<a action=\"bypass -h npc_%objectId%_manage deco curtains_cancel\">Remove</a>]" + CURTAINS);
					}
					else
					{
						html.replace("%curtain%", "Suspended");
						html.replace("%curtain_period%", "Suspended");
						html.replace("%change_curtain%", CURTAINS);
					}
					
					final ClanHallFunction chfPlateform = getClanHall().getFunction(ClanHall.FUNC_DECO_FRONTPLATEFORM);
					if (chfPlateform != null)
					{
						final int days = ClanHallManager.getInstance().getDecoDays(ClanHall.FUNC_DECO_FRONTPLATEFORM, chfPlateform.getFuncLvl());
						html.replace("%fixture%", "- Stage " + chfPlateform.getLvl() + "</font> (<font color=\"FFAABB\">" + chfPlateform.getLease() + "</font> adenas / " + days + " day)");
						html.replace("%fixture_period%", "Next fee at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfPlateform.getEndTime()));
						html.replace("%change_fixture%", "[<a action=\"bypass -h npc_%objectId%_manage deco fixtures_cancel\">Remove</a>]" + FIXTURES);
					}
					else
					{
						html.replace("%fixture%", "Suspended");
						html.replace("%fixture_period%", "Suspended");
						html.replace("%change_fixture%", FIXTURES);
					}
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
			}
			else if (val.equalsIgnoreCase("back"))
				showChatWindow(player);
			else
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), (getClanHall() instanceof SiegableHall) ? "html/clanHallManager/manage_sch.htm" : "html/clanHallManager/manage.htm");
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (actualCommand.equalsIgnoreCase("support"))
		{
			if (!validatePrivileges(player, PrivilegeType.CHP_USE_FUNCTIONS))
				return;
			
			final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_SUPPORT);
			if (chf == null || chf.getLvl() == 0)
				return;
			
			if (player.isCursedWeaponEquipped())
			{
				player.sendMessage("The wielder of a cursed weapon cannot receive outside heals or buffs");
				return;
			}
			
			setTarget(player);
			
			try
			{
				final int id = Integer.parseInt(val);
				final int lvl = (st.hasMoreTokens()) ? Integer.parseInt(st.nextToken()) : 0;
				if (Config.CUSTOM_BUFFER_MANAGER_NPC)
				{
					L2Skill skill = SkillTable.getInstance().getInfo(id, lvl);
					if (skill != null)
						skill.getEffects(this, player);
					
					StringBuilder buffsHtml = new StringBuilder();
					for (Buff buff : getClanHall().getBuffs())
					{
						if (buff.id() == id)
							player.destroyItemByItemId(57, buff.price(), true);
						
						buffsHtml.append("<a action=\"bypass -h npc_%objectId%_support " + buff.id() + " " + buff.lvl() + "\">" + buff.desc() + "</a><br1>");
					}
					
					final double mpConsume = getStatus().getMpConsume(skill);
					if (mpConsume > 0 && mpConsume > getStatus().getMp())
						return;
					
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/clanHallManager/support" + chf.getLvl() + ".htm");
					html.replace("%mp%", (int) getStatus().getMp());
					html.replace("%buffs%", buffsHtml.toString());
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
				else
					getAI().addCastDesireHold(player, id, lvl, 1000000);
			}
			catch (Exception e)
			{
				player.sendMessage("Invalid skill, contact your server support.");
			}
		}
		else if (actualCommand.equalsIgnoreCase("list_back"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/clanHallManager/chamberlain.htm");
			html.replace("%npcname%", getName());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("support_back"))
		{
			if (!validatePrivileges(player, PrivilegeType.CHP_USE_FUNCTIONS))
				return;
			
			final ClanHallFunction chf = getClanHall().getFunction(ClanHall.FUNC_SUPPORT);
			if (chf == null || chf.getLvl() == 0)
				return;
			
			StringBuilder buffsHtml = new StringBuilder();
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/clanHallManager/support" + getClanHall().getFunction(ClanHall.FUNC_SUPPORT).getLvl() + ".htm");
			html.replace("%mp%", (int) getStatus().getMp());
			html.replace("%objectId%", getObjectId());
			html.replace("%buffs%", buffsHtml.toString());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("WithdrawC"))
		{
			if (!validatePrivileges(player, PrivilegeType.SP_WAREHOUSE_SEARCH))
			{
				player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
				return;
			}
			
			if (player.getClan().getLevel() == 0)
			{
				player.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
				return;
			}
			
			player.setActiveWarehouse(player.getClan().getWarehouse());
			player.sendPacket(new WarehouseWithdrawList(player, WarehouseWithdrawList.CLAN));
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else if (actualCommand.equalsIgnoreCase("DepositC"))
		{
			if (player.getClan() != null)
			{
				if (player.getClan().getLevel() == 0)
					player.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
				else
				{
					player.setActiveWarehouse(player.getClan().getWarehouse());
					player.tempInventoryDisable();
					player.sendPacket(new WarehouseDepositList(player, WarehouseDepositList.CLAN));
				}
			}
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getLocale(), "html/clanHallManager/chamberlain" + ((getNpcTalkCond(player) == NpcTalkCond.OWNER) ? ".htm" : "-no.htm"));
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	@Override
	public boolean isTeleportAllowed(Player player)
	{
		return validatePrivileges(player, PrivilegeType.CHP_USE_FUNCTIONS);
	}
	
	@Override
	protected NpcTalkCond getNpcTalkCond(Player player)
	{
		if (getClanHall() != null && player.getClan() != null && getClanHall().getOwnerId() == player.getClanId())
			return NpcTalkCond.OWNER;
		
		return NpcTalkCond.NONE;
	}
	
	private void revalidateDeco(Player player)
	{
		getClanHall().getZone().broadcastPacket(new ClanHallDecoration(getClanHall()));
	}
	
	private boolean validatePrivileges(Player player, PrivilegeType privilege)
	{
		if (!player.hasClanPrivileges(privilege))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/clanHallManager/not_authorized.htm");
			player.sendPacket(html);
			return false;
		}
		return true;
	}
}