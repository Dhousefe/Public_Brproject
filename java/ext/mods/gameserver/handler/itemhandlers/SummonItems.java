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

import java.util.List;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.data.xml.SummonItemData;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.ChristmasTree;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class SummonItems implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player player))
			return;
		
		if (CTFEvent.getInstance().isStarted() && CTFEvent.getInstance().onItemSummon(playable.getObjectId()) || DMEvent.getInstance().isStarted() && DMEvent.getInstance().onItemSummon(playable.getObjectId()) || LMEvent.getInstance().isStarted() && LMEvent.getInstance().onItemSummon(playable.getObjectId()) || TvTEvent.getInstance().isStarted() && TvTEvent.getInstance().onItemSummon(playable.getObjectId()))
			return;
		
		if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
			return;
		}
		
		if (player.isInObserverMode())
			return;
		
		if (player.isAllSkillsDisabled() || player.getCast().isCastingNow())
			return;
		
		final IntIntHolder sitem = SummonItemData.getInstance().getSummonItem(item.getItemId());
		
		if ((player.getSummon() != null || player.isMounted()) && sitem.getValue() > 0)
		{
			player.sendPacket(SystemMessageId.SUMMON_ONLY_ONE);
			return;
		}
		
		if (player.getAttack().isAttackingNow() || player.isInCombat())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT);
			return;
		}
		
		if (player.isInBoat())
		{
			player.sendPacket(SystemMessageId.NOT_CALL_PET_FROM_THIS_LOCATION);
			return;
		}
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(sitem.getId());
		if (npcTemplate == null)
			return;
		
		switch (sitem.getValue())
		{
			case 0:
				final List<ChristmasTree> trees = player.getKnownTypeInRadius(ChristmasTree.class, 1200);
				if (!trees.isEmpty())
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_SUMMON_S1_AGAIN).addCharName(trees.get(0)));
					return;
				}
				
				if (!player.destroyItem(item, 1, false))
					return;
				
				player.getMove().stop();
				
				try
				{
					final Spawn spawn = new Spawn(npcTemplate);
					spawn.setLoc(player.getPosition());
					
					final Npc npc = spawn.doSpawn(true);
					npc.setTitle(player.getName());
					npc.setWalkOrRun(false);
					
					if (npcTemplate.getNpcId() == ChristmasTree.SPECIAL_TREE_ID)
						player.getMissions().update(MissionType.SPAWN_CHRISTMAS_TREE);
				}
				catch (Exception e)
				{
					player.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
				}
				break;
			
			case 1:
				player.getAI().tryToCast(player, SkillTable.getInstance().getInfo(2046, 1), false, false, item.getObjectId());
				player.sendPacket(SystemMessageId.SUMMON_A_PET);
				break;
			
			case 2:
				player.getMove().stop();
				player.mount(sitem.getId(), item.getObjectId());
				break;
		}
	}
}