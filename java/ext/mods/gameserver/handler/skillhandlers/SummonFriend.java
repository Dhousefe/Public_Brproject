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
package ext.mods.gameserver.handler.skillhandlers;

import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ConfirmDlg;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public class SummonFriend implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.SUMMON_FRIEND,
		SkillType.SUMMON_PARTY,
	};
	
	@Override
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
	{
		if (!(creature instanceof Player player))
			return;
		
		if (!checkSummoner(player))
			return;
		
		if (skill.getSkillType() == SkillType.SUMMON_PARTY)
		{
			final Party party = player.getParty();
			if (party == null)
				return;
			
			for (Player member : party.getMembers())
			{
				if (member == player)
					continue;
				
				if (!checkSummoned(player, member))
					continue;
				
				teleportTo(member, player, skill);
			}
		}
		else
		{
			for (WorldObject obj : targets)
			{
				if (!(obj instanceof Player target))
					continue;
				
				if (!checkSummoned(player, target))
					continue;
				
				if (!target.teleportRequest(player, skill))
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ALREADY_SUMMONED).addCharName(target));
					continue;
				}
				
				if (skill.getId() == 1403)
				{
					final ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId());
					confirm.addCharName(player);
					confirm.addZoneName(player.getPosition());
					confirm.addTime(30000);
					confirm.addRequesterId(player.getObjectId());
					target.sendPacket(confirm);
				}
				else
				{
					teleportTo(target, player, skill);
					target.teleportRequest(null, null);
				}
			}
		}
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
	
	/**
	 * Test if the current {@link Player} can summon. Send back messages if he can't.
	 * @param player : The {@link Player} to test.
	 * @return True if the {@link Player} can summon, false otherwise.
	 */
	public static boolean checkSummoner(Player player)
	{
		if (player.isMounted())
			return false;
		
		if (player.isInOlympiadMode() || player.isInObserverMode() || player.isInsideZone(ZoneId.NO_SUMMON_FRIEND))
		{
			player.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
			return false;
		}
		
		if (!TvTEvent.getInstance().onEscapeUse(player.getObjectId()) || !DMEvent.getInstance().onEscapeUse(player.getObjectId()) || !LMEvent.getInstance().onEscapeUse(player.getObjectId()))
		{
			player.sendPacket(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Test if the {@link WorldObject} can be summoned. Send back messages if he can't.
	 * @param player : The {@link Player} to test.
	 * @param target : The {@link WorldObject} to test.
	 * @return True if the given {@link WorldObject} can be summoned, false otherwise.
	 */
	public static boolean checkSummoned(Player player, WorldObject target)
	{
		if (!(target instanceof Player))
			return false;
		
		final Player targetPlayer = (Player) target;
		
		if (targetPlayer == player)
		{
			player.sendPacket(SystemMessageId.CANNOT_USE_ON_YOURSELF);
			return false;
		}
		
		if (targetPlayer.isAlikeDead())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED).addCharName(targetPlayer));
			return false;
		}
		
		if (targetPlayer.isOperating())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CURRENTLY_TRADING_OR_OPERATING_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED).addCharName(targetPlayer));
			return false;
		}
		
		if (targetPlayer.isRooted() || targetPlayer.isInCombat())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED).addCharName(targetPlayer));
			return false;
		}
		
		if (targetPlayer.isInOlympiadMode())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_IN_OLYMPIAD);
			return false;
		}
		
		if (targetPlayer.isFestivalParticipant() || targetPlayer.isFlying())
		{
			player.sendPacket(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING);
			return false;
		}
		
		if (targetPlayer.isInObserverMode() || targetPlayer.isInsideZone(ZoneId.NO_SUMMON_FRIEND))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IN_SUMMON_BLOCKING_AREA).addCharName(targetPlayer));
			return false;
		}
		
		if (!CTFEvent.getInstance().onEscapeUse(player.getObjectId()) || !DMEvent.getInstance().onEscapeUse(player.getObjectId()) || !LMEvent.getInstance().onEscapeUse(player.getObjectId()) || !TvTEvent.getInstance().onEscapeUse(player.getObjectId()))
		{
			player.sendPacket(SystemMessageId.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING);
			return false;
		}
		
		return true;
	}
	
	/**
	 * Teleport the current {@link Player} to the destination of another player.<br>
	 * <br>
	 * Check if summoning is allowed, and consume items if {@link L2Skill} got such constraints.
	 * @param player : The {@link Player} which requests the teleport.
	 * @param target : The {@link Player} to teleport on.
	 * @param skill : The {@link L2Skill} used to find item consumption informations.
	 */
	public static void teleportTo(Player player, Player target, L2Skill skill)
	{
		if (!checkSummoner(player) || !checkSummoned(player, target))
			return;
		
		if (skill.getTargetConsumeId() > 0 && skill.getTargetConsume() > 0)
		{
			if (player.getInventory().getItemCount(skill.getTargetConsumeId()) < skill.getTargetConsume())
			{
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_REQUIRED_FOR_SUMMONING).addItemName(skill.getTargetConsumeId()));
				return;
			}
			
			player.destroyItemByItemId(skill.getTargetConsumeId(), skill.getTargetConsume(), true);
		}
		player.teleportTo(target.getPosition(), 0);
	}
}