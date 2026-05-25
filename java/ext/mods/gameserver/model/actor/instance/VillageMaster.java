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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ext.mods.Config;
import ext.mods.commons.lang.StringUtil;
import ext.mods.extensions.listener.manager.PlayerListenerManager;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.data.xml.PlayerData;
import ext.mods.gameserver.data.xml.SkillTreeData;
import ext.mods.gameserver.enums.FloodProtector;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.enums.skills.AcquireSkillType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.player.SubClass;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.holder.skillnode.ClanSkillNode;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.ClanMember;
import ext.mods.gameserver.model.pledge.SubPledge;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.AcquireSkillList;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import ext.mods.gameserver.network.serverpackets.PledgeShowMemberListAll;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.UserInfo;
import ext.mods.gameserver.scripting.QuestState;

/**
 * The generic villagemaster. Some childs instances depends of it for race/classe restriction.
 */
public class VillageMaster extends Folk
{
	public VillageMaster(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public String getHtmlPath(Player player, int npcId, int val)
	{
		String filename = "";
		
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "html/villagemaster/" + filename + ".htm";
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		String[] commandStr = command.split(" ");
		String actualCommand = commandStr[0];
		
		String cmdParams = "";
		String cmdParams2 = "";
		
		if (commandStr.length >= 2)
			cmdParams = commandStr[1];
		if (commandStr.length >= 3)
			cmdParams2 = commandStr[2];
		
		if (command.startsWith("create_clan") && command.length() > 12)
		{
			final String val = command.substring(12);
			
			if (!StringUtil.isValidString(val, Config.CLAN_ALLY_NAME_TEMPLATE))
			{
				player.sendPacket(SystemMessageId.CLAN_NAME_INVALID);
				return;
			}
			
			ClanTable.getInstance().createClan(player, val);
		}
		else if (actualCommand.equalsIgnoreCase("create_academy"))
		{
			if (cmdParams.isEmpty())
				return;
			
			createSubPledge(player, cmdParams, null, Clan.SUBUNIT_ACADEMY, 5);
		}
		else if (actualCommand.equalsIgnoreCase("rename_pledge"))
		{
			if (cmdParams.isEmpty() || cmdParams2.isEmpty())
				return;
			
			if (!player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			final Clan clan = player.getClan();
			final SubPledge subPledge = player.getClan().getSubPledge(Integer.valueOf(cmdParams));
			
			if (subPledge == null)
			{
				player.sendMessage("Pledge doesn't exist.");
				return;
			}
			
			if (!StringUtil.isAlphaNumeric(cmdParams2))
			{
				player.sendPacket(SystemMessageId.CLAN_NAME_INVALID);
				return;
			}
			
			if (cmdParams2.length() < 2 || cmdParams2.length() > 16)
			{
				player.sendPacket(SystemMessageId.CLAN_NAME_LENGTH_INCORRECT);
				return;
			}
			
			subPledge.setName(cmdParams2);
			
			clan.updateSubPledgeInDB(subPledge);
			clan.broadcastToMembers(new PledgeShowMemberListAll(clan, subPledge.getId()));
		}
		else if (actualCommand.equalsIgnoreCase("create_royal"))
		{
			if (cmdParams.isEmpty())
				return;
			
			createSubPledge(player, cmdParams, cmdParams2, Clan.SUBUNIT_ROYAL1, 6);
		}
		else if (actualCommand.equalsIgnoreCase("create_knight"))
		{
			if (cmdParams.isEmpty())
				return;
			
			createSubPledge(player, cmdParams, cmdParams2, Clan.SUBUNIT_KNIGHT1, 7);
		}
		else if (actualCommand.equalsIgnoreCase("assign_subpl_leader"))
		{
			if (cmdParams.isEmpty())
				return;
			
			assignSubPledgeLeader(player, cmdParams, cmdParams2);
		}
		else if (actualCommand.equalsIgnoreCase("create_ally"))
		{
			if (cmdParams.isEmpty())
				return;
			
			if (!StringUtil.isValidString(cmdParams, Config.CLAN_ALLY_NAME_TEMPLATE))
			{
				player.sendPacket(SystemMessageId.INCORRECT_ALLIANCE_NAME);
				return;
			}
			
			if (player.getClan() == null)
				player.sendPacket(SystemMessageId.ONLY_CLAN_LEADER_CREATE_ALLIANCE);
			else if (player.getClan().createAlly(player, cmdParams))
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(player.getLocale(), "html/script/feature/Alliance/9001-05.htm");
				player.sendPacket(html);
			}
		}
		else if (actualCommand.equalsIgnoreCase("dissolve_ally"))
		{
			player.getClan().dissolveAlly(player);
		}
		else if (actualCommand.equalsIgnoreCase("dissolve_clan"))
		{
			if (!player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			final Clan clan = player.getClan();
			if (clan.getAllyId() != 0)
			{
				player.sendPacket(SystemMessageId.CANNOT_DISPERSE_THE_CLANS_IN_ALLY);
				return;
			}
			
			if (clan.isAtWar())
			{
				player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_WHILE_IN_WAR);
				return;
			}
			
			if (clan.hasCastle() || clan.hasClanHall())
			{
				player.sendPacket(SystemMessageId.CANNOT_DISSOLVE_WHILE_OWNING_CLAN_HALL_OR_CASTLE);
				return;
			}
			
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				if (castle.getSiege().checkSides(clan))
				{
					player.sendPacket((castle.getSiege().isInProgress()) ? SystemMessageId.CANNOT_DISSOLVE_WHILE_IN_SIEGE : SystemMessageId.CANNOT_DISSOLVE_CAUSE_CLAN_WILL_PARTICIPATE_IN_CASTLE_SIEGE);
					return;
				}
			}
			
			if (clan.getDissolvingExpiryTime() > System.currentTimeMillis())
			{
				player.sendPacket(SystemMessageId.DISSOLUTION_IN_PROGRESS);
				return;
			}
			
			if (Config.CLAN_DISSOLVE_DAYS > 0)
			{
				clan.setDissolvingExpiryTime(System.currentTimeMillis() + Config.CLAN_DISSOLVE_DAYS * 86400000L);
				clan.broadcastToMembers(new PledgeShowInfoUpdate(clan));
				clan.updateClanInDB();
				
				ClanTable.getInstance().scheduleRemoveClan(clan);
			}
			else
				ClanTable.getInstance().destroyClan(clan);
			
			player.applyDeathPenalty(false, false);
		}
		else if (actualCommand.equalsIgnoreCase("change_clan_leader"))
		{
			if (cmdParams.isEmpty())
				return;
			
			if (!player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			if (player.getName().equalsIgnoreCase(cmdParams))
				return;
			
			final Clan clan = player.getClan();
			final ClanMember member = clan.getClanMember(cmdParams);
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (member == null)
			{
				html.setFile(player.getLocale(), "html/script/feature/Clan/9000-07-error.htm");
				player.sendPacket(html);
				return;
			}
			
			if (!member.isOnline())
			{
				player.sendPacket(SystemMessageId.INVITED_USER_NOT_ONLINE);
				return;
			}
			
			if (member.getPledgeType() != 0)
			{
				player.sendMessage("Selected member cannot be found in main clan.");
				return;
			}
			
			if (clan.getNewLeaderId() == 0)
			{
				clan.setNewLeaderId(member.getObjectId(), true);
				html.setFile(player.getLocale(), "html/script/feature/Clan/9000-07-success.htm");
			}
			else
				html.setFile(player.getLocale(), "html/script/feature/Clan/9000-07-in-progress.htm");
			
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("cancel_clan_leader_change"))
		{
			if (!player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			final Clan clan = player.getClan();
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			if (clan.getNewLeaderId() != 0)
			{
				clan.setNewLeaderId(0, true);
				html.setFile(player.getLocale(), "html/script/feature/Clan/9000-08-success.htm");
			}
			else
				html.setFile(player.getLocale(), "html/script/feature/Clan/9000-08-no.htm");
			
			player.sendPacket(html);
		}
		else if (actualCommand.equalsIgnoreCase("recover_clan"))
		{
			if (!player.isClanLeader())
			{
				player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
				return;
			}
			
			final Clan clan = player.getClan();
			if (clan.getDissolvingExpiryTime() <= 0)
			{
				player.sendPacket(SystemMessageId.NO_REQUESTS_TO_DISPERSE);
				return;
			}
			
			clan.setDissolvingExpiryTime(0);
			clan.broadcastToMembers(new PledgeShowInfoUpdate(clan));
			clan.updateClanInDB();
		}
		else if (actualCommand.equalsIgnoreCase("increase_clan_level"))
		{
			if (player.getClan().levelUpClan(player))
				player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 0, 0));
		}
		else if (actualCommand.equalsIgnoreCase("learn_clan_skills"))
			showPledgeSkillList(player);
		else if (command.startsWith("Subclass"))
		{
			if (player.getCast().isCastingNow() || player.isAllSkillsDisabled())
			{
				player.sendPacket(SystemMessageId.SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE);
				return;
			}
			
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
				OlympiadManager.getInstance().unRegisterNoble(player);
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			int cmdChoice = 0;
			int paramOne = 0;
			int paramTwo = 0;
			
			try
			{
				cmdChoice = Integer.parseInt(command.substring(9, 10).trim());
				
				int endIndex = command.indexOf(' ', 11);
				if (endIndex == -1)
					endIndex = command.length();
				
				paramOne = Integer.parseInt(command.substring(11, endIndex).trim());
				if (command.length() > endIndex)
					paramTwo = Integer.parseInt(command.substring(endIndex).trim());
			}
			catch (Exception NumberFormatException)
			{
			}
			
			StringBuilder sb;
			Set<ClassId> subsAvailable;
			
			switch (cmdChoice)
			{
				case 0:
					html.setFile(player.getLocale(), "html/villagemaster/SubClass.htm");
					break;
				
				case 1:
					if (player.getSummon() != null)
					{
						player.sendPacket(SystemMessageId.CANT_SUBCLASS_WITH_SUMMONED_SERVITOR);
						return;
					}
					
					if (player.isOverweight())
					{
						player.sendPacket(SystemMessageId.NOT_SUBCLASS_WHILE_OVERWEIGHT);
						return;
					}
					
					if (player.getSubClasses().size() >= 3)
					{
						html.setFile(player.getLocale(), "html/villagemaster/SubClass_Fail.htm");
						break;
					}
					
					subsAvailable = getAvailableSubClasses(player);
					if (subsAvailable.isEmpty())
					{
						player.sendMessage("There are no sub classes available at this time.");
						return;
					}
					
					sb = new StringBuilder(300);
					for (ClassId subClass : subsAvailable)
						StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Subclass 4 ", subClass.getId(), "\" msg=\"1268;", subClass, "\">", subClass, "</a><br>");
					
					html.setFile(player.getLocale(), "html/villagemaster/SubClass_Add.htm");
					html.replace("%list%", sb.toString());
					break;
				
				case 2:
					if (player.getSummon() != null)
					{
						player.sendPacket(SystemMessageId.CANT_SUBCLASS_WITH_SUMMONED_SERVITOR);
						return;
					}
					
					if (player.isOverweight())
					{
						player.sendPacket(SystemMessageId.NOT_SUBCLASS_WHILE_OVERWEIGHT);
						return;
					}
					
					if (player.getSubClasses().isEmpty())
						html.setFile(player.getLocale(), "html/villagemaster/SubClass_ChangeNo.htm");
					else
					{
						sb = new StringBuilder(300);
						
						if (checkVillageMaster(player.getBaseClass()))
							StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Subclass 5 0\">", PlayerData.getInstance().getClassNameById(player.getBaseClass()), "</a><br>");
						
						for (SubClass subclass : player.getSubClasses().values())
						{
							if (checkVillageMaster(subclass.getClassDefinition()))
								StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Subclass 5 ", subclass.getClassIndex(), "\">", subclass.getClassDefinition(), "</a><br>");
						}
						
						if (sb.length() > 0)
						{
							html.setFile(player.getLocale(), "html/villagemaster/SubClass_Change.htm");
							html.replace("%list%", sb.toString());
						}
						else
							html.setFile(player.getLocale(), "html/villagemaster/SubClass_ChangeNotFound.htm");
					}
					break;
				
				case 3:
					if (player.getSubClasses().isEmpty())
					{
						html.setFile(player.getLocale(), "html/villagemaster/SubClass_ModifyEmpty.htm");
						break;
					}
					
					html.setFile(player.getLocale(), "html/villagemaster/SubClass_Modify.htm");
					if (player.getSubClasses().containsKey(1))
						html.replace("%sub1%", PlayerData.getInstance().getClassNameById(player.getSubClasses().get(1).getClassId()));
					else
						html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 1\">%sub1%</a><br>", "");
					
					if (player.getSubClasses().containsKey(2))
						html.replace("%sub2%", PlayerData.getInstance().getClassNameById(player.getSubClasses().get(2).getClassId()));
					else
						html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 2\">%sub2%</a><br>", "");
					
					if (player.getSubClasses().containsKey(3))
						html.replace("%sub3%", PlayerData.getInstance().getClassNameById(player.getSubClasses().get(3).getClassId()));
					else
						html.replace("<a action=\"bypass -h npc_%objectId%_Subclass 6 3\">%sub3%</a><br>", "");
					break;
				
				case 4:
					if (!player.getClient().performAction(FloodProtector.SUBCLASS))
						return;
					
					boolean allowAddition = true;
					
					if (player.getSubClasses().size() >= 3)
						allowAddition = false;
					
					if (player.getStatus().getLevel() < 75)
						allowAddition = false;
					
					if (allowAddition)
					{
						for (SubClass subclass : player.getSubClasses().values())
						{
							if (subclass.getLevel() < 75)
							{
								allowAddition = false;
								break;
							}
						}
					}
					
					if (allowAddition && !Config.SUBCLASS_WITHOUT_QUESTS)
						allowAddition = checkQuests(player);
					
					if (allowAddition && isValidNewSubClass(player, paramOne))
					{
						if (!player.addSubClass(paramOne, player.getSubClasses().size() + 1))
							return;
						
						player.setActiveClass(player.getSubClasses().size());
						
						if (player.getMissions().getMission(MissionType.SUBCLASS).getValue() < player.getSubClasses().size())
							player.getMissions().set(MissionType.SUBCLASS, 1, true, false);
						
						html.setFile(player.getLocale(), "html/villagemaster/SubClass_AddOk.htm");
						player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS);
						PlayerListenerManager.getInstance().notifySetClass(player, paramOne);
					}
					else
						html.setFile(player.getLocale(), "html/villagemaster/SubClass_Fail.htm");
					break;
				
				case 5:
					if (!player.getClient().performAction(FloodProtector.SUBCLASS))
						return;
					
					if (player.getClassIndex() == paramOne)
					{
						html.setFile(player.getLocale(), "html/villagemaster/SubClass_Current.htm");
						break;
					}
					
					if (paramOne == 0)
					{
						if (!checkVillageMaster(player.getBaseClass()))
							return;
					}
					else
					{
						try
						{
							if (!checkVillageMaster(player.getSubClasses().get(paramOne).getClassDefinition()))
								return;
						}
						catch (NullPointerException e)
						{
							return;
						}
					}
					
					player.stopAllEffects();
					player.setActiveClass(paramOne);
					player.sendPacket(SystemMessageId.SUBCLASS_TRANSFER_COMPLETED);
					return;
				
				case 6:
					if (paramOne < 1 || paramOne > 3)
						return;
					
					subsAvailable = getAvailableSubClasses(player);
					
					if (subsAvailable.isEmpty())
					{
						player.sendMessage("There are no sub classes available at this time.");
						return;
					}
					
					sb = new StringBuilder(300);
					for (ClassId subClass : subsAvailable)
						StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Subclass 7 ", paramOne, " ", subClass.getId(), "\" msg=\"1445;", "\">", subClass, "</a><br>");
					
					switch (paramOne)
					{
						case 1:
							html.setFile(player.getLocale(), "html/villagemaster/SubClass_ModifyChoice1.htm");
							break;
						
						case 2:
							html.setFile(player.getLocale(), "html/villagemaster/SubClass_ModifyChoice2.htm");
							break;
						
						case 3:
							html.setFile(player.getLocale(), "html/villagemaster/SubClass_ModifyChoice3.htm");
							break;
						
						default:
							html.setFile(player.getLocale(), "html/villagemaster/SubClass_ModifyChoice.htm");
					}
					html.replace("%list%", sb.toString());
					break;
				
				case 7:
					if (!player.getClient().performAction(FloodProtector.SUBCLASS))
						return;
					
					if (!isValidNewSubClass(player, paramTwo))
						return;
					
					if (player.modifySubClass(paramOne, paramTwo))
					{
						player.setActiveClass(paramOne);
						
						html.setFile(player.getLocale(), "html/villagemaster/SubClass_ModifyOk.htm");
						player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS);
					}
					else
					{
						player.setActiveClass(0);
						
						player.sendMessage("The sub class could not be added, you have been reverted to your base class.");
						return;
					}
					break;
			}
			
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else
		{
			super.onBypassFeedback(player, command);
		}
	}
	
	protected boolean checkQuests(Player player)
	{
		if (player.isNoble())
			return true;

		if (Config.SUBCLASS_REQUIRE_MIMIR)
		{
			QuestState mimir = player.getQuestList().getQuestState("Q235_MimirsElixir");
			if (mimir == null || !mimir.isCompleted())
				return false;
		}

		if (Config.SUBCLASS_REQUIRE_FATE)
		{
			QuestState fate = player.getQuestList().getQuestState("Q234_FatesWhisper");
			if (fate == null || !fate.isCompleted())
				return false;
		}

		return true;
	}
	
	private final Set<ClassId> getAvailableSubClasses(Player player)
	{
		Set<ClassId> availSubs = ClassId.getAvailableSubclasses(player);
		
		if (!availSubs.isEmpty())
		{
			for (Iterator<ClassId> availSub = availSubs.iterator(); availSub.hasNext();)
			{
				ClassId classId = availSub.next();
				
				if (!checkVillageMaster(classId))
				{
					availSub.remove();
					continue;
				}
				
				for (SubClass subclass : player.getSubClasses().values())
				{
					if (subclass.getClassDefinition().equalsOrIsChildOf(classId))
					{
						availSub.remove();
						break;
					}
				}
			}
		}
		
		return availSubs;
	}
	
	/**
	 * Check subclass classId for validity (villagemaster race/type is not contains in previous subclasses, but in allowed subclasses). Base class not added into allowed subclasses.
	 * @param player : The player to check.
	 * @param classId : The class id to check.
	 * @return true if the {@link Player} can pick this subclass id.
	 */
	private final boolean isValidNewSubClass(Player player, int classId)
	{
		if (!checkVillageMaster(classId))
			return false;
		
		final ClassId cid = ClassId.VALUES[classId];
		for (SubClass subclass : player.getSubClasses().values())
		{
			if (subclass.getClassDefinition().equalsOrIsChildOf(cid))
				return false;
		}
		
		final Set<ClassId> availSubs = ClassId.getAvailableSubclasses(player);
		if (availSubs.isEmpty())
			return false;
		
		boolean found = false;
		for (ClassId pclass : availSubs)
		{
			if (pclass.getId() == classId)
			{
				found = true;
				break;
			}
		}
		
		return found;
	}
	
	protected boolean checkVillageMasterRace(ClassId pclass)
	{
		return true;
	}
	
	protected boolean checkVillageMasterTeachType(ClassId pclass)
	{
		return true;
	}
	
	public final boolean checkVillageMaster(int classId)
	{
		return checkVillageMaster(ClassId.VALUES[classId]);
	}
	
	public final boolean checkVillageMaster(ClassId pclass)
	{
		if (Config.GAME_SUBCLASS_EVERYWHERE)
			return true;
		
		return checkVillageMasterRace(pclass) && checkVillageMasterTeachType(pclass);
	}
	
	private static final void createSubPledge(Player player, String clanName, String leaderName, int pledgeType, int minClanLvl)
	{
		final Clan clan = player.getClan();
		
		if (clan == null)
			return;
		
		if (clan.getLevel() < minClanLvl)
		{
			if (pledgeType == Clan.SUBUNIT_ACADEMY)
				player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN_ACADEMY);
			else
				player.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_CRITERIA_IN_ORDER_TO_CREATE_A_MILITARY_UNIT);
			
			return;
		}
		
		if (!StringUtil.isAlphaNumeric(clanName))
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_INVALID);
			return;
		}
		
		if (clanName.length() < 2 || clanName.length() > 16)
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_LENGTH_INCORRECT);
			return;
		}
		
		for (Clan tempClan : ClanTable.getInstance().getClans())
		{
			if (tempClan.getSubPledge(clanName) != null)
			{
				if (pledgeType == Clan.SUBUNIT_ACADEMY)
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_EXISTS).addString(clanName));
				else
					player.sendPacket(SystemMessageId.ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME);
				
				return;
			}
		}
		
		if (pledgeType != Clan.SUBUNIT_ACADEMY && (clan.getClanMember(leaderName) == null || clan.getClanMember(leaderName).getPledgeType() != 0))
		{
			if (pledgeType >= Clan.SUBUNIT_KNIGHT1)
				player.sendPacket(SystemMessageId.CAPTAIN_OF_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
			else if (pledgeType >= Clan.SUBUNIT_ROYAL1)
				player.sendPacket(SystemMessageId.CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED);
			
			return;
		}
		
		final int leaderId = pledgeType != Clan.SUBUNIT_ACADEMY ? clan.getClanMember(leaderName).getObjectId() : 0;
		if (clan.createSubPledge(player, pledgeType, leaderId, clanName) == null)
			return;
		
		SystemMessage sm;
		if (pledgeType == Clan.SUBUNIT_ACADEMY)
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_S1S_CLAN_ACADEMY_HAS_BEEN_CREATED).addString(player.getClan().getName());
		else if (pledgeType >= Clan.SUBUNIT_KNIGHT1)
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED).addString(player.getClan().getName());
		else if (pledgeType >= Clan.SUBUNIT_ROYAL1)
			sm = SystemMessage.getSystemMessage(SystemMessageId.THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED).addString(player.getClan().getName());
		else
			sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_CREATED);
		
		player.sendPacket(sm);
		
		if (pledgeType != Clan.SUBUNIT_ACADEMY)
		{
			final ClanMember leaderSubPledge = clan.getClanMember(leaderName);
			final Player leaderPlayer = leaderSubPledge.getPlayerInstance();
			if (leaderPlayer != null)
			{
				leaderPlayer.setPledgeClass(ClanMember.calculatePledgeClass(leaderPlayer));
				leaderPlayer.sendPacket(new UserInfo(leaderPlayer));
			}
		}
	}
	
	private static final void assignSubPledgeLeader(Player player, String clanName, String leaderName)
	{
		if (!player.isClanLeader())
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		
		if (leaderName.length() > 16)
		{
			player.sendPacket(SystemMessageId.NAMING_CHARNAME_UP_TO_16CHARS);
			return;
		}
		
		if (player.getName().equals(leaderName))
		{
			player.sendPacket(SystemMessageId.CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED);
			return;
		}
		
		final Clan clan = player.getClan();
		final SubPledge subPledge = player.getClan().getSubPledge(clanName);
		
		if (null == subPledge || subPledge.getId() == Clan.SUBUNIT_ACADEMY)
		{
			player.sendPacket(SystemMessageId.CLAN_NAME_INVALID);
			return;
		}
		
		final ClanMember leaderSubPledge = clan.getClanMember(leaderName);
		
		if (leaderSubPledge == null || leaderSubPledge.getPledgeType() != 0)
		{
			if (subPledge.getId() >= Clan.SUBUNIT_KNIGHT1)
				player.sendPacket(SystemMessageId.CAPTAIN_OF_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
			else if (subPledge.getId() >= Clan.SUBUNIT_ROYAL1)
				player.sendPacket(SystemMessageId.CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED);
			
			return;
		}
		
		if (clan.isSubPledgeLeader(leaderSubPledge.getObjectId()))
		{
			if (subPledge.getId() >= Clan.SUBUNIT_KNIGHT1)
				player.sendPacket(SystemMessageId.CAPTAIN_OF_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED);
			else if (subPledge.getId() >= Clan.SUBUNIT_ROYAL1)
				player.sendPacket(SystemMessageId.CAPTAIN_OF_ROYAL_GUARD_CANNOT_BE_APPOINTED);
			
			return;
		}
		
		subPledge.setLeaderId(leaderSubPledge.getObjectId());
		clan.updateSubPledgeInDB(subPledge);
		
		final Player leaderPlayer = leaderSubPledge.getPlayerInstance();
		if (leaderPlayer != null)
		{
			leaderPlayer.setPledgeClass(ClanMember.calculatePledgeClass(leaderPlayer));
			leaderPlayer.sendPacket(new UserInfo(leaderPlayer));
		}
		
		clan.broadcastToMembers(new PledgeShowMemberListAll(clan, subPledge.getId()), SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_SELECTED_AS_CAPTAIN_OF_S2).addString(leaderName).addString(clanName));
	}
	
	public static final void showPledgeSkillList(Player player)
	{
		if (!player.isClanLeader())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(player.getLocale(), "html/script/feature/Clan/9000-09-no.htm");
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final List<ClanSkillNode> skills = SkillTreeData.getInstance().getClanSkillsFor(player);
		if (skills.isEmpty())
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DO_NOT_HAVE_FURTHER_SKILLS_TO_LEARN_S1).addNumber(5));
		else
			player.sendPacket(new AcquireSkillList(AcquireSkillType.CLAN, skills));
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}