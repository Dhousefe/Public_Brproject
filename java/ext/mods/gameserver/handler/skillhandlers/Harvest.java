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

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.handler.ISkillHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.monster.SeedState;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public class Harvest implements ISkillHandler
{
	private static final SkillType[] SKILL_IDS =
	{
		SkillType.HARVEST
	};
	
	@Override
	public void useSkill(Creature creature, L2Skill skill, WorldObject[] targets, ItemInstance item)
	{
		if (!(creature instanceof Player player))
			return;
		
		if (!(targets[0] instanceof Monster targetMonster))
		{
			player.sendPacket(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
			return;
		}
		
		final SeedState seedState = targetMonster.getSeedState();
		if (!seedState.isSeeded())
		{
			player.sendPacket(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
			return;
		}
		
		if (seedState.isHarvested())
		{
			player.sendPacket(SystemMessageId.THE_HARVEST_HAS_FAILED);
			return;
		}
		
		if (!seedState.isAllowedToHarvest(player))
		{
			player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
			return;
		}
		
		seedState.setHarvested();
		
		if (!calcSuccess(player, targetMonster))
		{
			player.sendPacket(SystemMessageId.THE_HARVEST_HAS_FAILED);
			return;
		}
		
		final IntIntHolder crop = seedState.getHarvestedCrop();
		player.addEarnedItem(crop.getId(), crop.getValue(), true);
		
		if (player.isInParty())
		{
			SystemMessage sm;
			if (crop.getValue() > 1)
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HARVESTED_S3_S2S).addCharName(player).addItemName(crop.getId()).addNumber(crop.getValue());
			else
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HARVESTED_S2S).addCharName(player).addItemName(crop.getId());
			
			player.getParty().broadcastToPartyMembers(player, sm);
		}
	}
	
	private static boolean calcSuccess(Player player, Creature target)
	{
		int rate = 100;
		
		final int diff = Math.abs(player.getStatus().getLevel() - target.getStatus().getLevel());
		if (diff > 5)
			rate -= (diff - 5) * 5;
		
		return Rnd.get(100) < Math.max(1, rate);
	}
	
	@Override
	public SkillType[] getSkillIds()
	{
		return SKILL_IDS;
	}
}