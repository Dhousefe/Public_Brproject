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
package ext.mods.gameserver.handler.itemhandlers;

import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

public class ScrollsOfResurrection implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		final WorldObject obj = playable.getTarget();
		if (!(obj instanceof Creature targetCreature))
		{
			playable.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		if (targetCreature.isDead())
		{
			if (targetCreature instanceof Player targetPlayer)
			{
				if (targetPlayer.isInsideZone(ZoneId.SIEGE) && targetPlayer.getSiegeState() == 0)
				{
					playable.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_BE_RESURRECTED_DURING_SIEGE));
					return;
				}
				
				if (!CTFEvent.getInstance().onScrollUse(targetPlayer.getObjectId()) || !DMEvent.getInstance().onScrollUse(targetPlayer.getObjectId()) || !LMEvent.getInstance().onScrollUse(targetPlayer.getObjectId()) || !TvTEvent.getInstance().onScrollUse(targetPlayer.getObjectId()))
				{
					playable.sendPacket(ActionFailed.STATIC_PACKET);
					return;
				}
				
				if (targetPlayer.isFestivalParticipant())
				{
					playable.sendMessage("You may not resurrect participants in a festival.");
					return;
				}
				
				if (targetPlayer.isReviveRequested())
				{
					final Player player = (Player) playable;
					
					if (targetPlayer.isRevivingPet())
						player.sendPacket(SystemMessageId.CANNOT_RES_MASTER);
					else
						player.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED);
					
					return;
				}
			}
			else if (targetCreature instanceof Pet targetPet)
			{
				final Player player = (Player) playable;
				
				if (targetPet.getOwner() != player && targetPet.getOwner().isReviveRequested())
				{
					if (targetPet.getOwner().isRevivingPet())
						player.sendPacket(SystemMessageId.RES_HAS_ALREADY_BEEN_PROPOSED);
					else
						player.sendPacket(SystemMessageId.CANNOT_RES_PET2);
					
					return;
				}
			}
		}
		
		final IntIntHolder[] skills = item.getEtcItem().getSkills();
		if (skills == null)
		{
			LOGGER.warn("{} doesn't have any registered skill for handler.", item.getName());
			return;
		}
		
		for (IntIntHolder skillInfo : skills)
		{
			if (skillInfo == null)
				continue;
			
			final L2Skill itemSkill = skillInfo.getSkill();
			if (itemSkill == null)
				continue;
			
			playable.getAI().tryToCast(targetCreature, itemSkill);
		}
	}
}