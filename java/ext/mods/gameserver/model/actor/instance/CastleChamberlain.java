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
import java.util.Calendar;
import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.CastleManorManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.enums.TeleportType;
import ext.mods.gameserver.enums.actors.NpcTalkCond;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.ai.type.CastleManagerNpcAI;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.castle.Castle.CastleFunction;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.ExShowCropInfo;
import ext.mods.gameserver.network.serverpackets.ExShowCropSetting;
import ext.mods.gameserver.network.serverpackets.ExShowManorDefaultInfo;
import ext.mods.gameserver.network.serverpackets.ExShowSeedInfo;
import ext.mods.gameserver.network.serverpackets.ExShowSeedSetting;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SiegeInfo;
import ext.mods.gameserver.skills.L2Skill;

/**
 * An instance type extending {@link Merchant}, used for castle chamberlains.<br>
 * <br>
 * It handles following actions :
 * <ul>
 * <li>Tax rate control</li>
 * <li>Regional manor system control</li>
 * <li>Castle treasure control</li>
 * <li>Siege time modifier</li>
 * <li>Items production</li>
 * <li>Doors management && Doors/walls upgrades</li>
 * <li>Traps management && upgrades</li>
 * </ul>
 */
public class CastleChamberlain extends Merchant
{
	private static final int CERTIFICATES_BUNDLE = 10;
	private static final int CERTIFICATES_PRICE = 1000;
	
	private int _preHour = 6;
	
	public CastleChamberlain(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public CastleManagerNpcAI getAI()
	{
		return (CastleManagerNpcAI) _ai;
	}
	
	@Override
	public void setAI()
	{
		_ai = new CastleManagerNpcAI(this);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		final NpcTalkCond condition = getNpcTalkCond(player);
		if (condition == NpcTalkCond.NONE)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/chamberlain/noprivs.htm");
			player.sendPacket(html);
			return;
		}
		
		if (condition == NpcTalkCond.UNDER_SIEGE)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/chamberlain/busy.htm");
			player.sendPacket(html);
			return;
		}
		
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		
		String val = (st.hasMoreTokens()) ? st.nextToken() : "";
		if (actualCommand.equalsIgnoreCase("banish_foreigner"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_RIGHT_TO_DISMISS))
				return;
			
			getCastle().banishForeigners();
			sendFileMessage(player, "html/chamberlain/banishafter.htm");
		}
		else if (actualCommand.equalsIgnoreCase("banish_foreigner_show"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_RIGHT_TO_DISMISS))
				return;
			
			sendFileMessage(player, "html/chamberlain/banishfore.htm");
		}
		else if (actualCommand.equalsIgnoreCase("manage_functions"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			sendFileMessage(player, "html/chamberlain/chamberlain-manage.htm");
		}
		else if (actualCommand.equalsIgnoreCase("functions"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_USE_FUNCTIONS))
				return;
			
			if (val.equalsIgnoreCase("tele"))
			{
				final CastleFunction chf = getCastle().getFunction(Castle.FUNC_TELEPORT);
				if (chf == null)
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/chamberlain/chamberlain-nac.htm");
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					return;
				}
				
				showTeleportWindow(player, (chf.getLvl() == 2) ? TeleportType.CHF_LEVEL_2 : TeleportType.CHF_LEVEL_1);
			}
			else if (val.equalsIgnoreCase("support"))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				
				final CastleFunction chf = getCastle().getFunction(Castle.FUNC_SUPPORT);
				if (chf == null)
					html.setFile(player.getLocale(), "html/chamberlain/chamberlain-nac.htm");
				else
				{
					html.setFile(player.getLocale(), "html/chamberlain/support" + chf.getLvl() + ".htm");
					html.replace("%mp%", (int) getStatus().getMp());
				}
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
			else if (val.equalsIgnoreCase("back"))
				showChatWindow(player);
			else
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/chamberlain/chamberlain-functions.htm");
				
				final CastleFunction chfExp = getCastle().getFunction(Castle.FUNC_RESTORE_EXP);
				if (chfExp != null)
					html.replace("%xp_regen%", chfExp.getLvl());
				else
					html.replace("%xp_regen%", "0");
				
				final CastleFunction chfHp = getCastle().getFunction(Castle.FUNC_RESTORE_HP);
				if (chfHp != null)
					html.replace("%hp_regen%", chfHp.getLvl());
				else
					html.replace("%hp_regen%", "0");
				
				final CastleFunction chfMp = getCastle().getFunction(Castle.FUNC_RESTORE_MP);
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
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			if (val.equalsIgnoreCase("recovery"))
			{
				if (st.hasMoreTokens())
				{
					if (getCastle().getOwnerId() == 0)
						return;
					
					val = st.nextToken();
					
					if (val.equalsIgnoreCase("hp_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-cancel.htm");
						html.replace("%apply%", "recovery hp 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("mp_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-cancel.htm");
						html.replace("%apply%", "recovery mp 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("exp_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-cancel.htm");
						html.replace("%apply%", "recovery exp 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_hp"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply.htm");
						html.replace("%name%", "Fireplace (HP Recovery Device)");
						
						final int percent = Integer.parseInt(st.nextToken());
						
						int cost;
						switch (percent)
						{
							case 80:
								cost = Config.CS_HPREG1_FEE;
								break;
							case 120:
								cost = Config.CS_HPREG2_FEE;
								break;
							case 180:
								cost = Config.CS_HPREG3_FEE;
								break;
							case 240:
								cost = Config.CS_HPREG4_FEE;
								break;
							default:
								cost = Config.CS_HPREG5_FEE;
								break;
						}
						
						html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_HPREG_FEE_RATIO / 86400000) + " day</font>)");
						html.replace("%use%", "Provides additional HP recovery for clan members in the castle.<font color=\"00FFFF\">" + percent + "%</font>");
						html.replace("%apply%", "recovery hp " + percent);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_mp"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply.htm");
						html.replace("%name%", "Carpet (MP Recovery)");
						
						final int percent = Integer.parseInt(st.nextToken());
						
						int cost;
						switch (percent)
						{
							case 5:
								cost = Config.CS_MPREG1_FEE;
								break;
							case 15:
								cost = Config.CS_MPREG2_FEE;
								break;
							case 30:
								cost = Config.CS_MPREG3_FEE;
								break;
							default:
								cost = Config.CS_MPREG4_FEE;
								break;
						}
						html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_MPREG_FEE_RATIO / 86400000) + " day</font>)");
						html.replace("%use%", "Provides additional MP recovery for clan members in the castle.<font color=\"00FFFF\">" + percent + "%</font>");
						html.replace("%apply%", "recovery mp " + percent);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_exp"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply.htm");
						html.replace("%name%", "Chandelier (EXP Recovery Device)");
						
						final int percent = Integer.parseInt(st.nextToken());
						
						int cost;
						switch (percent)
						{
							case 15:
								cost = Config.CS_EXPREG1_FEE;
								break;
							case 25:
								cost = Config.CS_EXPREG2_FEE;
								break;
							case 35:
								cost = Config.CS_EXPREG3_FEE;
								break;
							default:
								cost = Config.CS_EXPREG4_FEE;
								break;
						}
						html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_EXPREG_FEE_RATIO / 86400000) + " day</font>)");
						html.replace("%use%", "Restores the Exp of any clan member who is resurrected in the castle.<font color=\"00FFFF\">" + percent + "%</font>");
						html.replace("%apply%", "recovery exp " + percent);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("hp"))
					{
						val = st.nextToken();
						final int percent = Integer.parseInt(val);
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final CastleFunction chf = getCastle().getFunction(Castle.FUNC_RESTORE_HP);
						if (chf != null && chf.getLvl() == percent)
						{
							html.setFile(player.getLocale(), "html/chamberlain/functions-used.htm");
							html.replace("%val%", val + "%");
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply_confirmed.htm");
						
						int fee;
						switch (percent)
						{
							case 0:
								fee = 0;
								html.setFile(player.getLocale(), "html/chamberlain/functions-cancel_confirmed.htm");
								break;
							case 80:
								fee = Config.CS_HPREG1_FEE;
								break;
							case 120:
								fee = Config.CS_HPREG2_FEE;
								break;
							case 180:
								fee = Config.CS_HPREG3_FEE;
								break;
							case 240:
								fee = Config.CS_HPREG4_FEE;
								break;
							default:
								fee = Config.CS_HPREG5_FEE;
								break;
						}
						
						if (!getCastle().updateFunctions(player, Castle.FUNC_RESTORE_HP, percent, fee, Config.CS_HPREG_FEE_RATIO, (getCastle().getFunction(Castle.FUNC_RESTORE_HP) == null)))
							html.setFile(player.getLocale(), "html/chamberlain/low_adena.htm");
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("mp"))
					{
						val = st.nextToken();
						final int percent = Integer.parseInt(val);
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final CastleFunction chf = getCastle().getFunction(Castle.FUNC_RESTORE_MP);
						if (chf != null && chf.getLvl() == percent)
						{
							html.setFile(player.getLocale(), "html/chamberlain/functions-used.htm");
							html.replace("%val%", val + "%");
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply_confirmed.htm");
						
						int fee;
						switch (percent)
						{
							case 0:
								fee = 0;
								html.setFile(player.getLocale(), "html/chamberlain/functions-cancel_confirmed.htm");
								break;
							case 5:
								fee = Config.CS_MPREG1_FEE;
								break;
							case 15:
								fee = Config.CS_MPREG2_FEE;
								break;
							case 30:
								fee = Config.CS_MPREG3_FEE;
								break;
							default:
								fee = Config.CS_MPREG4_FEE;
								break;
						}
						if (!getCastle().updateFunctions(player, Castle.FUNC_RESTORE_MP, percent, fee, Config.CS_MPREG_FEE_RATIO, (getCastle().getFunction(Castle.FUNC_RESTORE_MP) == null)))
							html.setFile(player.getLocale(), "html/chamberlain/low_adena.htm");
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("exp"))
					{
						val = st.nextToken();
						final int percent = Integer.parseInt(val);
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final CastleFunction chf = getCastle().getFunction(Castle.FUNC_RESTORE_EXP);
						if (chf != null && chf.getLvl() == percent)
						{
							html.setFile(player.getLocale(), "html/chamberlain/functions-used.htm");
							html.replace("%val%", val + "%");
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply_confirmed.htm");
						
						int fee;
						switch (percent)
						{
							case 0:
								fee = 0;
								html.setFile(player.getLocale(), "html/chamberlain/functions-cancel_confirmed.htm");
								break;
							case 15:
								fee = Config.CS_EXPREG1_FEE;
								break;
							case 25:
								fee = Config.CS_EXPREG2_FEE;
								break;
							case 35:
								fee = Config.CS_EXPREG3_FEE;
								break;
							default:
								fee = Config.CS_EXPREG4_FEE;
								break;
						}
						if (!getCastle().updateFunctions(player, Castle.FUNC_RESTORE_EXP, percent, fee, Config.CS_EXPREG_FEE_RATIO, (getCastle().getFunction(Castle.FUNC_RESTORE_EXP) == null)))
							html.setFile(player.getLocale(), "html/chamberlain/low_adena.htm");
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
				}
				else
				{
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/chamberlain/edit_recovery.htm");
					String hp = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 80\">80%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 120\">120%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 180\">180%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 240\">240%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_hp 300\">300%</a>]";
					String exp = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 15\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 25\">25%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 35\">35%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_exp 50\">50%</a>]";
					String mp = "[<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 5\">5%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 15\">15%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 30\">30%</a>][<a action=\"bypass -h npc_%objectId%_manage recovery edit_mp 40\">40%</a>]";
					
					final CastleFunction chfHp = getCastle().getFunction(Castle.FUNC_RESTORE_HP);
					if (chfHp != null)
					{
						html.replace("%hp_recovery%", chfHp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfHp.getLease() + "</font> adenas /" + Config.CS_HPREG_FEE_RATIO / 86400000 + " day");
						html.replace("%hp_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfHp.getEndTime()));
						html.replace("%change_hp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery hp_cancel\">Deactivate</a>]" + hp);
					}
					else
					{
						html.replace("%hp_recovery%", "none");
						html.replace("%hp_period%", "none");
						html.replace("%change_hp%", hp);
					}
					
					final CastleFunction chfExp = getCastle().getFunction(Castle.FUNC_RESTORE_EXP);
					if (chfExp != null)
					{
						html.replace("%exp_recovery%", chfExp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfExp.getLease() + "</font> adenas /" + Config.CS_EXPREG_FEE_RATIO / 86400000 + " day)");
						html.replace("%exp_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfExp.getEndTime()));
						html.replace("%change_exp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery exp_cancel\">Deactivate</a>]" + exp);
					}
					else
					{
						html.replace("%exp_recovery%", "none");
						html.replace("%exp_period%", "none");
						html.replace("%change_exp%", exp);
					}
					
					final CastleFunction chfMp = getCastle().getFunction(Castle.FUNC_RESTORE_MP);
					if (chfMp != null)
					{
						html.replace("%mp_recovery%", chfMp.getLvl() + "%</font> (<font color=\"FFAABB\">" + chfMp.getLease() + "</font> adenas /" + Config.CS_MPREG_FEE_RATIO / 86400000 + " day)");
						html.replace("%mp_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(getCastle().getFunction(Castle.FUNC_RESTORE_MP).getEndTime()));
						html.replace("%change_mp%", "[<a action=\"bypass -h npc_%objectId%_manage recovery mp_cancel\">Deactivate</a>]" + mp);
					}
					else
					{
						html.replace("%mp_recovery%", "none");
						html.replace("%mp_period%", "none");
						html.replace("%change_mp%", mp);
					}
					
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
			}
			else if (val.equalsIgnoreCase("other"))
			{
				if (st.hasMoreTokens())
				{
					if (getCastle().getOwnerId() == 0)
						return;
					
					val = st.nextToken();
					
					if (val.equalsIgnoreCase("tele_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-cancel.htm");
						html.replace("%apply%", "other tele 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("support_cancel"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-cancel.htm");
						html.replace("%apply%", "other support 0");
						html.replace("%apply%", "other tele 0");
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_support"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply.htm");
						html.replace("%name%", "Insignia (Supplementary Magic)");
						
						final int stage = Integer.parseInt(st.nextToken());
						
						int cost;
						switch (stage)
						{
							case 1:
								cost = Config.CS_SUPPORT1_FEE;
								break;
							case 2:
								cost = Config.CS_SUPPORT2_FEE;
								break;
							case 3:
								cost = Config.CS_SUPPORT3_FEE;
								break;
							default:
								cost = Config.CS_SUPPORT4_FEE;
								break;
						}
						html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_SUPPORT_FEE_RATIO / 86400000) + " day</font>)");
						html.replace("%use%", "Enables the use of supplementary magic.");
						html.replace("%apply%", "other support " + stage);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("edit_tele"))
					{
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply.htm");
						html.replace("%name%", "Mirror (Teleportation Device)");
						
						final int stage = Integer.parseInt(st.nextToken());
						
						int cost;
						switch (stage)
						{
							case 1:
								cost = Config.CS_TELE1_FEE;
								break;
							default:
								cost = Config.CS_TELE2_FEE;
								break;
						}
						
						html.replace("%cost%", cost + "</font> adenas / " + (Config.CS_TELE_FEE_RATIO / 86400000) + " day</font>)");
						html.replace("%use%", "Teleports clan members in a castle to the target <font color=\"00FFFF\">Stage " + stage + "</font> staging area");
						html.replace("%apply%", "other tele " + stage);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("tele"))
					{
						val = st.nextToken();
						final int lvl = Integer.parseInt(val);
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final CastleFunction chf = getCastle().getFunction(Castle.FUNC_TELEPORT);
						if (chf != null && chf.getLvl() == lvl)
						{
							html.setFile(player.getLocale(), "html/chamberlain/functions-used.htm");
							html.replace("%val%", "Stage " + val);
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply_confirmed.htm");
						
						int fee;
						switch (lvl)
						{
							case 0:
								fee = 0;
								html.setFile(player.getLocale(), "html/chamberlain/functions-cancel_confirmed.htm");
								break;
							case 1:
								fee = Config.CS_TELE1_FEE;
								break;
							default:
								fee = Config.CS_TELE2_FEE;
								break;
						}
						if (!getCastle().updateFunctions(player, Castle.FUNC_TELEPORT, lvl, fee, Config.CS_TELE_FEE_RATIO, (getCastle().getFunction(Castle.FUNC_TELEPORT) == null)))
							html.setFile(player.getLocale(), "html/chamberlain/low_adena.htm");
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					else if (val.equalsIgnoreCase("support"))
					{
						val = st.nextToken();
						final int lvl = Integer.parseInt(val);
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						
						final CastleFunction chf = getCastle().getFunction(Castle.FUNC_SUPPORT);
						if (chf != null && chf.getLvl() == lvl)
						{
							html.setFile(player.getLocale(), "html/chamberlain/functions-used.htm");
							html.replace("%val%", "Stage " + val);
							html.replace("%objectId%", getObjectId());
							player.sendPacket(html);
							return;
						}
						
						html.setFile(player.getLocale(), "html/chamberlain/functions-apply_confirmed.htm");
						
						int fee;
						switch (lvl)
						{
							case 0:
								fee = 0;
								html.setFile(player.getLocale(), "html/chamberlain/functions-cancel_confirmed.htm");
								break;
							case 1:
								fee = Config.CS_SUPPORT1_FEE;
								break;
							case 2:
								fee = Config.CS_SUPPORT2_FEE;
								break;
							case 3:
								fee = Config.CS_SUPPORT3_FEE;
								break;
							default:
								fee = Config.CS_SUPPORT4_FEE;
								break;
						}
						if (!getCastle().updateFunctions(player, Castle.FUNC_SUPPORT, lvl, fee, Config.CS_SUPPORT_FEE_RATIO, (chf == null)))
							html.setFile(player.getLocale(), "html/chamberlain/low_adena.htm");
						
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
				}
				else
				{
					NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/chamberlain/edit_other.htm");
					
					String tele = "[<a action=\"bypass -h npc_%objectId%_manage other edit_tele 1\">Level 1</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_tele 2\">Level 2</a>]";
					String support = "[<a action=\"bypass -h npc_%objectId%_manage other edit_support 1\">Level 1</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 2\">Level 2</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 3\">Level 3</a>][<a action=\"bypass -h npc_%objectId%_manage other edit_support 4\">Level 4</a>]";
					
					final CastleFunction chfTel = getCastle().getFunction(Castle.FUNC_TELEPORT);
					if (chfTel != null)
					{
						html.replace("%tele%", "Stage " + chfTel.getLvl() + "</font> (<font color=\"FFAABB\">" + chfTel.getLease() + "</font> adenas /" + Config.CS_TELE_FEE_RATIO / 86400000 + " day)");
						html.replace("%tele_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfTel.getEndTime()));
						html.replace("%change_tele%", "[<a action=\"bypass -h npc_%objectId%_manage other tele_cancel\">Deactivate</a>]" + tele);
					}
					else
					{
						html.replace("%tele%", "none");
						html.replace("%tele_period%", "none");
						html.replace("%change_tele%", tele);
					}
					
					final CastleFunction chfSup = getCastle().getFunction(Castle.FUNC_SUPPORT);
					if (chfSup != null)
					{
						html.replace("%support%", "Stage " + chfSup.getLvl() + "</font> (<font color=\"FFAABB\">" + chfSup.getLease() + "</font> adenas /" + Config.CS_SUPPORT_FEE_RATIO / 86400000 + " Day)");
						html.replace("%support_period%", "Withdraw the fee for the next time at " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chfSup.getEndTime()));
						html.replace("%change_support%", "[<a action=\"bypass -h npc_%objectId%_manage other support_cancel\">Deactivate</a>]" + support);
					}
					else
					{
						html.replace("%support%", "none");
						html.replace("%support_period%", "none");
						html.replace("%change_support%", support);
					}
					
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
			}
			else if (val.equalsIgnoreCase("back"))
				showChatWindow(player);
			else
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/chamberlain/manage.htm");
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (actualCommand.equalsIgnoreCase("support"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_USE_FUNCTIONS))
				return;
			
			final CastleFunction chf = getCastle().getFunction(Castle.FUNC_SUPPORT);
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
					
					final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), "html/chamberlain/support" + chf.getLvl() + ".htm");
					html.replace("%mp%", (int) getStatus().getMp());
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
				}
				else
					getAI().addCastDesire(player, id, lvl, 1000000);
			}
			catch (Exception e)
			{
				player.sendMessage("Invalid skill, contact your server support.");
			}
		}
		else if (actualCommand.equalsIgnoreCase("support_back"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_USE_FUNCTIONS))
				return;
			
			final CastleFunction chf = getCastle().getFunction(Castle.FUNC_SUPPORT);
			if (chf == null || chf.getLvl() == 0)
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/chamberlain/support" + chf.getLvl() + ".htm");
			html.replace("%mp%", (int) getStatus().getMp());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("products"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_USE_FUNCTIONS))
				return;
			
			sendFileMessage(player, "html/chamberlain/products.htm");
		}
		else if (actualCommand.equalsIgnoreCase("list_siege_clans"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_MANAGE_SIEGE_WAR))
				return;
			
			player.sendPacket(new SiegeInfo(getCastle()));
		}
		else if (actualCommand.equalsIgnoreCase("receive_report"))
		{
			if (condition == NpcTalkCond.CLAN_MEMBER)
				sendFileMessage(player, "html/chamberlain/noprivs.htm");
			else
			{
				final Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
				
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/chamberlain/report.htm");
				html.replace("%objectId%", getObjectId());
				html.replace("%clanname%", clan.getName());
				html.replace("%clanleadername%", clan.getLeaderName());
				html.replace("%castlename%", getCastle().getName());
				html.replace("%ss_event%", SevenSignsManager.getInstance().getCurrentPeriod().getName());
				
				switch (SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE))
				{
					case NORMAL:
						html.replace("%ss_avarice%", "Not in Possession");
						break;
					
					case DAWN:
						html.replace("%ss_avarice%", "Lords of Dawn");
						break;
					
					case DUSK:
						html.replace("%ss_avarice%", "Revolutionaries of Dusk");
						break;
				}
				
				switch (SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS))
				{
					case NORMAL:
						html.replace("%ss_gnosis%", "Not in Possession");
						break;
					
					case DAWN:
						html.replace("%ss_gnosis%", "Lords of Dawn");
						break;
					
					case DUSK:
						html.replace("%ss_gnosis%", "Revolutionaries of Dusk");
						break;
				}
				
				switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
				{
					case NORMAL:
						html.replace("%ss_strife%", "Not in Possession");
						break;
					
					case DAWN:
						html.replace("%ss_strife%", "Lords of Dawn");
						break;
					
					case DUSK:
						html.replace("%ss_strife%", "Revolutionaries of Dusk");
						break;
				}
				player.sendPacket(html);
			}
		}
		else if (actualCommand.equalsIgnoreCase("items"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_USE_FUNCTIONS))
				return;
			
			if (val.isEmpty())
				return;
			
			showBuyWindow(player, Integer.parseInt(val + "1"));
		}
		else if (actualCommand.equalsIgnoreCase("manage_vault"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_MANAGE_TAXES))
				return;
			
			String filename = "html/chamberlain/vault.htm";
			int amount = 0;
			
			if (st.hasMoreTokens())
				amount = Integer.parseInt(st.nextToken());
			
			if (amount > 0)
			{
				if (val.equalsIgnoreCase("deposit"))
				{
					if (getCastle().getTreasury() + amount < Integer.MAX_VALUE && player.reduceAdena(amount, true))
						getCastle().editTreasury(amount, true);
				}
				else if (val.equalsIgnoreCase("withdraw"))
				{
					if (getCastle().getTreasury() < amount)
						filename = "html/chamberlain/vault-no.htm";
					else if (getCastle().editTreasury(-amount, true))
						player.addAdena(amount, true);
				}
			}
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), filename);
			html.replace("%objectId%", getObjectId());
			html.replace("%tax_income%", StringUtil.formatNumber(getCastle().getTreasury()));
			html.replace("%tax_income_reserved%", StringUtil.formatNumber(getCastle().getTaxRevenue()));
			html.replace("%seed_income%", StringUtil.formatNumber(getCastle().getSeedIncome()));
			html.replace("%withdraw_amount%", StringUtil.formatNumber(amount));
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("operate_door"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_ENTRY_EXIT_RIGHTS))
				return;
			
			if (val.isEmpty())
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/chamberlain/" + getNpcId() + "-d.htm");
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
				return;
			}
			
			boolean open = (Integer.parseInt(val) == 1);
			while (st.hasMoreTokens())
			{
				if (open)
					getCastle().openDoor(player, Integer.parseInt(st.nextToken()));
				else
					getCastle().closeDoor(player, Integer.parseInt(st.nextToken()));
			}
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), (open) ? "html/chamberlain/doors-open.htm" : "chamberlain/doors-close.htm");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			
		}
		else if (actualCommand.equalsIgnoreCase("tax_set"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (!validatePrivileges(player, PrivilegeType.CP_MANAGE_TAXES))
			{
				html.setFile(player.getLocale(), "html/chamberlain/tax-member.htm");
				html.replace("%currentTaxRate%", getCastle().getCurrentTaxPercent());
				html.replace("%nextTaxRate%", getCastle().getNextTaxPercent());
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
				return;
			}
			
			html.setFile(player.getLocale(), "html/chamberlain/tax-leader.htm");
			html.replace("%currentTaxRate%", getCastle().getCurrentTaxPercent());
			html.replace("%nextTaxRate%", getCastle().getNextTaxPercent());
			html.replace("%objectId%", getObjectId());
			html.replace("%taxLimit%", getTaxLimit());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("tax_adjust"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			final int taxLimit = getTaxLimit();
			final int taxRate = (val.isEmpty()) ? 0 : Integer.parseInt(val);
			
			if (taxRate < 0 || taxRate > taxLimit)
			{
				html.setFile(player.getLocale(), "html/chamberlain/tax-toohigh.htm");
				html.replace("%objectId%", getObjectId());
				html.replace("%taxLimit%", taxLimit);
				player.sendPacket(html);
				return;
			}
			
			getCastle().setNextTaxPercent(taxRate, true);
			
			html.setFile(player.getLocale(), "html/chamberlain/tax-after.htm");
			html.replace("%nextTaxRate%", getCastle().getNextTaxPercent());
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("manor"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_MANOR_ADMINISTRATION))
				return;
			
			String filename = "";
			if (!Config.ALLOW_MANOR)
				filename = "html/npcdefault.htm";
			else
			{
				int cmd = Integer.parseInt(val);
				switch (cmd)
				{
					case 0:
						filename = "html/chamberlain/manor/manor.htm";
						break;
					
					case 4:
						filename = "html/chamberlain/manor/manor_help00" + st.nextToken() + ".htm";
						break;
					
					default:
						filename = "html/chamberlain/no.htm";
						break;
				}
			}
			
			if (filename.length() != 0)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), filename);
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("manor_menu_select"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_MANOR_ADMINISTRATION))
				return;
			
			final CastleManorManager manor = CastleManorManager.getInstance();
			if (manor.isUnderMaintenance())
			{
				player.sendPacket(ActionFailed.STATIC_PACKET);
				player.sendPacket(SystemMessageId.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
				return;
			}
			
			final String params = command.substring(command.indexOf("?") + 1);
			final StringTokenizer str = new StringTokenizer(params, "&");
			
			final int ask = Integer.parseInt(str.nextToken().split("=")[1]);
			final int state = Integer.parseInt(str.nextToken().split("=")[1]);
			final boolean time = str.nextToken().split("=")[1].equals("1");
			
			final int castleId = (state == -1) ? getCastle().getId() : state;
			
			switch (ask)
			{
				case 3:
					player.sendPacket(new ExShowSeedInfo(castleId, time, true));
					break;
				
				case 4:
					player.sendPacket(new ExShowCropInfo(castleId, time, true));
					break;
				
				case 5:
					player.sendPacket(new ExShowManorDefaultInfo(true));
					break;
				
				case 7:
					if (manor.isManorApproved())
						player.sendPacket(SystemMessageId.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM);
					else
						player.sendPacket(new ExShowSeedSetting(castleId));
					break;
				
				case 8:
					if (manor.isManorApproved())
						player.sendPacket(SystemMessageId.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_6_AM_AND_8_PM);
					else
						player.sendPacket(new ExShowCropSetting(castleId));
					break;
			}
		}
		else if (actualCommand.equalsIgnoreCase("siege_change"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_MANAGE_SIEGE_WAR))
				return;
			
			if (getCastle().getSiege().getSiegeRegistrationEndDate() < Calendar.getInstance().getTimeInMillis())
				sendFileMessage(player, "html/chamberlain/siegetime1.htm");
			else if (getCastle().getSiege().isTimeRegistrationOver())
				sendFileMessage(player, "html/chamberlain/siegetime2.htm");
			else
				sendFileMessage(player, "html/chamberlain/siegetime3.htm");
		}
		else if (actualCommand.equalsIgnoreCase("siege_time_set"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_MANAGE_SIEGE_WAR))
				return;
			
			switch (Integer.parseInt(val))
			{
				case 1:
					_preHour = Integer.parseInt(st.nextToken());
					break;
				
				default:
					break;
			}
			
			if (_preHour != 6)
			{
				getCastle().getSiegeDate().set(Calendar.HOUR_OF_DAY, _preHour + 12);
				
				getCastle().getSiege().endTimeRegistration(false);
				sendFileMessage(player, "html/chamberlain/siegetime8.htm");
				return;
			}
			
			sendFileMessage(player, "html/chamberlain/siegetime6.htm");
		}
		else if (actualCommand.equals("give_crown"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (condition == NpcTalkCond.OWNER)
			{
				if (player.getInventory().getItemByItemId(6841) == null)
				{
					player.addItem(6841, 1, true);
					
					html.setFile(player.getLocale(), "html/chamberlain/gavecrown.htm");
					html.replace("%CharName%", player.getName());
					html.replace("%FeudName%", getCastle().getName());
				}
				else
					html.setFile(player.getLocale(), "html/chamberlain/hascrown.htm");
			}
			else
				html.setFile(player.getLocale(), "html/chamberlain/noprivs.htm");
			
			player.sendPacket(html);
		}
		else if (actualCommand.equals("manor_certificate"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_USE_FUNCTIONS))
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (SevenSignsManager.getInstance().isSealValidationPeriod())
			{
				if (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()) == CabalType.DUSK)
					html.setFile(player.getLocale(), "html/chamberlain/not-dawn-or-event.htm");
				else if (getCastle().getLeftCertificates() == 0)
					html.setFile(player.getLocale(), "html/chamberlain/not-enough-ticket.htm");
				else
				{
					html.setFile(player.getLocale(), "html/chamberlain/sell-dawn-ticket.htm");
					html.replace("%left%", getCastle().getLeftCertificates());
					html.replace("%bundle%", CERTIFICATES_BUNDLE);
					html.replace("%price%", CERTIFICATES_PRICE);
				}
			}
			else
				html.setFile(player.getLocale(), "html/chamberlain/not-dawn-or-event.htm");
			
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equals("validate_certificate"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_USE_FUNCTIONS))
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (SevenSignsManager.getInstance().isSealValidationPeriod())
			{
				if (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()) == CabalType.DUSK)
					html.setFile(player.getLocale(), "html/chamberlain/not-dawn-or-event.htm");
				else if (getCastle().getLeftCertificates() == 0)
					html.setFile(player.getLocale(), "html/chamberlain/not-enough-ticket.htm");
				else if (player.reduceAdena(CERTIFICATES_BUNDLE * CERTIFICATES_PRICE, true))
				{
					player.addItem(6388, CERTIFICATES_BUNDLE, true);
					
					getCastle().setLeftCertificates(getCastle().getLeftCertificates() - 10, true);
					
					html.setFile(player.getLocale(), "html/chamberlain/sell-dawn-ticket.htm");
					html.replace("%left%", getCastle().getLeftCertificates());
					html.replace("%bundle%", CERTIFICATES_BUNDLE);
					html.replace("%price%", CERTIFICATES_PRICE);
				}
				else
					html.setFile(player.getLocale(), "html/chamberlain/not-enough-adena.htm");
			}
			else
				html.setFile(player.getLocale(), "html/chamberlain/not-dawn-or-event.htm");
			
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("castle_devices"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			sendFileMessage(player, "html/chamberlain/devices.htm");
		}
		else if (actualCommand.equalsIgnoreCase("doors_update"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			if (val.isEmpty())
				html.setFile(player.getLocale(), "html/chamberlain/" + getNpcId() + "-gu.htm");
			else
			{
				html.setFile(player.getLocale(), "html/chamberlain/doors-update.htm");
				html.replace("%id%", val);
				html.replace("%type%", st.nextToken());
			}
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("doors_choose_upgrade"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			final String id = val;
			final String type = st.nextToken();
			final String level = st.nextToken();
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/chamberlain/doors-confirm.htm");
			html.replace("%objectId%", getObjectId());
			html.replace("%id%", id);
			html.replace("%level%", level);
			html.replace("%type%", type);
			html.replace("%price%", getDoorCost(Integer.parseInt(type), Integer.parseInt(level)));
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("doors_confirm_upgrade"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			final int type = Integer.parseInt(st.nextToken());
			final int level = Integer.parseInt(st.nextToken());
			final int price = getDoorCost(type, level);
			
			if (price == 0)
				return;
			
			final int id = Integer.parseInt(val);
			final Door door = getCastle().getDoor(id);
			if (door == null)
				return;
			
			final int currentHpRatio = door.getStatus().getUpgradeHpRatio();
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (currentHpRatio >= level)
			{
				html.setFile(player.getLocale(), "html/chamberlain/doors-already-updated.htm");
				html.replace("%level%", currentHpRatio * 100);
			}
			else if (!player.reduceAdena(price, true))
				html.setFile(player.getLocale(), "html/chamberlain/not-enough-adena.htm");
			else
			{
				getCastle().upgradeDoor(id, level, true);
				
				html.setFile(player.getLocale(), "html/chamberlain/doors-success.htm");
			}
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("traps_update"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			if (val.isEmpty())
				html.setFile(player.getLocale(), "html/chamberlain/" + getNpcId() + "-tu.htm");
			else
			{
				html.setFile(player.getLocale(), "html/chamberlain/traps-update" + ((getCastle().getName().equalsIgnoreCase("aden")) ? "1" : "") + ".htm");
				html.replace("%trapIndex%", val);
			}
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("traps_choose_upgrade"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			final String trapIndex = val;
			final String level = st.nextToken();
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/chamberlain/traps-confirm.htm");
			html.replace("%objectId%", getObjectId());
			html.replace("%trapIndex%", trapIndex);
			html.replace("%level%", level);
			html.replace("%price%", getTrapCost(Integer.parseInt(level)));
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("traps_confirm_upgrade"))
		{
			if (!validatePrivileges(player, PrivilegeType.CP_SET_FUNCTIONS))
				return;
			
			final int level = Integer.parseInt(st.nextToken());
			final int price = getTrapCost(level);
			
			if (price == 0)
				return;
			
			final int trapIndex = Integer.parseInt(val);
			final int currentLevel = getCastle().getTrapUpgradeLevel(trapIndex);
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (currentLevel >= level)
			{
				html.setFile(player.getLocale(), "html/chamberlain/traps-already-updated.htm");
				html.replace("%level%", currentLevel);
			}
			else if (!player.reduceAdena(price, true))
				html.setFile(player.getLocale(), "html/chamberlain/not-enough-adena.htm");
			else
			{
				getCastle().setTrapUpgrade(trapIndex, level, true);
				
				html.setFile(player.getLocale(), "html/chamberlain/traps-success.htm");
			}
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		switch (getNpcTalkCond(player))
		{
			case NONE:
				html.setFile(player.getLocale(), "html/chamberlain/no.htm");
				break;
			
			case UNDER_SIEGE:
				html.setFile(player.getLocale(), "html/chamberlain/busy.htm");
				break;
			
			default:
				html.setFile(player.getLocale(), "html/chamberlain/chamberlain.htm");
				break;
		}
		
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	@Override
	protected NpcTalkCond getNpcTalkCond(Player player)
	{
		if (getCastle() != null && player.getClan() != null)
		{
			if (getCastle().getSiege().isInProgress())
				return NpcTalkCond.UNDER_SIEGE;
			
			if (getCastle().getOwnerId() == player.getClanId())
			{
				if (player.isClanLeader())
					return NpcTalkCond.OWNER;
				
				return NpcTalkCond.CLAN_MEMBER;
			}
		}
		return NpcTalkCond.NONE;
	}
	
	private boolean validatePrivileges(Player player, PrivilegeType privilege)
	{
		if (!player.hasClanPrivileges(privilege))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/chamberlain/noprivs.htm");
			player.sendPacket(html);
			return false;
		}
		return true;
	}
	
	private void sendFileMessage(Player player, String htmlMessage)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getLocale(), htmlMessage);
		html.replace("%objectId%", getObjectId());
		html.replace("%npcId%", getNpcId());
		html.replace("%time%", getCastle().getSiegeDate().getTime().toString());
		player.sendPacket(html);
	}
	
	/**
	 * Retrieve the price of the door, following its type, required level of upgrade and current Seven Signs state.
	 * @param type : The type of doors (1: normal gates, 2: metallic gates, 3: walls).
	 * @param level : The required level of upgrade (x2, x3 or x5 HPs).
	 * @return The price modified by Seal of Strife state (-20% if Dawn is winning, x3 if Dusk is winning).
	 */
	private static int getDoorCost(int type, int level)
	{
		int price = 0;
		
		switch (type)
		{
			case 1:
				switch (level)
				{
					case 2:
						price = 300000;
						break;
					case 3:
						price = 400000;
						break;
					case 5:
						price = 500000;
						break;
				}
				break;
			
			case 2:
				switch (level)
				{
					case 2:
						price = 750000;
						break;
					case 3:
						price = 900000;
						break;
					case 5:
						price = 1000000;
						break;
				}
				break;
			
			case 3:
				switch (level)
				{
					case 2:
						price = 1600000;
						break;
					case 3:
						price = 1800000;
						break;
					case 5:
						price = 2000000;
						break;
				}
				break;
		}
		
		switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
		{
			case DUSK:
				price *= 3;
				break;
			
			case DAWN:
				price *= 0.8;
				break;
		}
		
		return price;
	}
	
	/**
	 * Retrieve the price of traps, following its level.
	 * @param level : The required level of upgrade.
	 * @return The price modified by Seal of Strife state (-20% if Dawn is winning, x3 if Dusk is winning).
	 */
	private static int getTrapCost(int level)
	{
		int price = 0;
		
		switch (level)
		{
			case 1:
				price = 3000000;
				break;
			
			case 2:
				price = 4000000;
				break;
			
			case 3:
				price = 5000000;
				break;
			
			case 4:
				price = 6000000;
				break;
		}
		
		switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
		{
			case DUSK:
				price *= 3;
				break;
			
			case DAWN:
				price *= 0.8;
				break;
		}
		
		return price;
	}
	
	private static int getTaxLimit()
	{
		switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
		{
			case DAWN:
				return 25;
			
			case DUSK:
				return 5;
			
			default:
				return 15;
		}
	}
}