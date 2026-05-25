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

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public class TakeCastle implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.TAKE_CASTLE
	};
	
	@Override
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
	{
		if (!(creature instanceof Player player))
			return;
		
		if (targets.length == 0)
			return;
		
		if (!player.isClanLeader())
			return;
		
		final Castle castle = check(player, targets[0], skill, false);
		if (castle == null)
			return;
		
		castle.engrave(player.getClan(), targets[0]);
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @param target : The {@link WorldObject} to test.
	 * @param skill : The {@link L2Skill} to test.
	 * @param announce : If True, broadcast to SiegeSide.DEFENDER than opponents started to engrave.
	 * @return The {@link Castle} affiliated to the {@link WorldObject} target, or null if operation aborted for a condition or another.
	 */
	public static Castle check(Player player, WorldObject target, L2Skill skill, boolean announce)
	{
		final Castle castle = CastleManager.getInstance().getCastle(player);
		if (castle == null || castle.getId() <= 0)
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
		else if (!castle.isGoodArtifact(target))
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.INVALID_TARGET));
		else if (!castle.getSiege().isInProgress())
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
		else if (!player.isIn3DRadius(target, 200))
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.DIST_TOO_FAR_CASTING_STOPPED));
		else if (!player.isInsideZone(ZoneId.CAST_ON_ARTIFACT))
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
		else if (!castle.getSiege().checkSide(player.getClan(), SiegeSide.ATTACKER))
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addSkillName(skill));
		else
		{
			if (announce)
				castle.getSiege().announce(SystemMessageId.OPPONENT_STARTED_ENGRAVING, SiegeSide.DEFENDER);
			
			return castle;
		}
		return null;
	}
}