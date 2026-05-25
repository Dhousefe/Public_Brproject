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

import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.summonmobitem.SummonMobItemData;
import ext.mods.summonmobitem.SummonMobItemHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.SetupGauge;
import ext.mods.gameserver.enums.GaugeColor;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.InstanceMap.InstanceManager;

/**
 * Item Handler para summon de monstros customizados
 * Permite que players façam spawn de monstros através de itens específicos
 * 
 * @author Dhousefe
 */
public class ItemMonsterSummon implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player player))
			return;
		
		if (CTFEvent.getInstance().isStarted() && CTFEvent.getInstance().onItemSummon(playable.getObjectId()) || 
			DMEvent.getInstance().isStarted() && DMEvent.getInstance().onItemSummon(playable.getObjectId()) || 
			LMEvent.getInstance().isStarted() && LMEvent.getInstance().onItemSummon(playable.getObjectId()) || 
			TvTEvent.getInstance().isStarted() && TvTEvent.getInstance().onItemSummon(playable.getObjectId()))
		{
			player.sendMessage("You cannot use this item during PvP events.");
			return;
		}
		
		if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
			return;
		}
		
		if (player.isInObserverMode())
		{
			player.sendMessage("You cannot use this item in observer mode.");
			return;
		}
		
		if (player.isInOlympiadMode())
		{
			player.sendMessage("This item cannot be used in Olympiad.");
			return;
		}
		
		if (player.getAttack().isAttackingNow() || player.isInCombat())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT);
			return;
		}
		
		if (player.isInBoat())
		{
			player.sendMessage("You cannot use this item while in a boat.");
			return;
		}
		
		if (InstanceManager.getInstance().getInstance(player.getObjectId()) != null)
		{
		     player.sendMessage("You cannot use this item inside instances.");
		     return;
		}
		
				
		
		final SummonMobItemHolder itemConfig = SummonMobItemData.getInstance().getSummonItem(item.getItemId());
		
		if (itemConfig == null)
		{
			player.sendMessage("This item is not configured for monster summoning.");
			return;
		}
		
		if (!itemConfig.isEnabled())
		{
			player.sendMessage("This summon item is currently disabled.");
			return;
		}
		
		
		if (!SummonMobItemData.getInstance().canPlayerUseItem(player.getObjectId(), item.getItemId()))
		{
			final int remainingCooldown = SummonMobItemData.getInstance().getRemainingCooldown(player.getObjectId(), item.getItemId());
			if (remainingCooldown > 0)
			{
				player.sendMessage("You must wait " + remainingCooldown + " more minutes before using this item again.");
				return;
			}
			
			final int remainingUses = SummonMobItemData.getInstance().getRemainingDailyUses(player.getObjectId(), item.getItemId());
			if (remainingUses == 0)
			{
				player.sendMessage("You have reached the daily limit for this summon item.");
				return;
			}
			
			player.sendMessage("You cannot use this item at the moment.");
			return;
		}
		
		if (!itemConfig.canUseByClass(String.valueOf(player.getBaseClass())))
		{
			player.sendMessage("Your class cannot use this summon item.");
			return;
		}
		
		final int monsterId = itemConfig.getMonsterId();
		
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(monsterId);
		if (npcTemplate == null)
		{
			player.sendMessage("Monster template not found.");
			return;
		}
		
		
		if (!npcTemplate.isType("Monster") && !npcTemplate.isType("RaidBoss") && !npcTemplate.isType("GrandBoss"))
		{
			player.sendMessage("This item can only summon monsters.");
			return;
		}
		
		if (!npcTemplate.isNoRespawn())
		{
			player.sendMessage("This monster template is not configured for single spawn.");
			return;
		}
		
		player.getMove().stop();
		
		executeSummonAnimation(player, itemConfig);
		
		ThreadPool.schedule(() -> {
			try
			{
				performMonsterSpawn(player, item, itemConfig, npcTemplate);
			}
			catch (Exception e)
			{
				player.sendMessage("An error occurred while summoning the monster.");
				System.err.println("Error in ItemMonsterSummon delayed spawn: " + e.getMessage());
				e.printStackTrace();
			}
		}, 15000);
	}
	
	/**
	 * Executa a animação de summon baseada no tipo de monstro
	 */
	private void executeSummonAnimation(Player player, SummonMobItemHolder itemConfig)
	{
		final int monsterId = itemConfig.getMonsterId();
		final int castTime = 15000;
		
		int skillId;
		int skillLevel;
		
		if (monsterId >= 20000)
		{
			skillId = 1324;
			skillLevel = 1;
		}
		else if (monsterId >= 10000)
		{
			skillId = 1324; 
			skillLevel = 1;
		}
		else
		{
			skillId = 1324; 
			skillLevel = 1;
		}
		
		player.sendPacket(new SetupGauge(GaugeColor.BLUE, castTime));
		
		player.broadcastPacket(new MagicSkillUse(player, skillId, skillLevel, castTime, 0));
		
		player.sendMessage("You begin to summon " + itemConfig.getMonsterName() + "...");
	}
	
	/**
	 * Executa o spawn do monstro após a animação
	 */
	private void performMonsterSpawn(Player player, ItemInstance item, SummonMobItemHolder itemConfig, NpcTemplate npcTemplate)
	{
		try
		{
			final int distance = 400;
			final double angle = Math.toRadians(player.getHeading() / 182.044444444);
			
			final int spawnX = player.getX() + (int) (distance * Math.cos(angle));
			final int spawnY = player.getY() + (int) (distance * Math.sin(angle));
			final int spawnZ = player.getZ();
			
			final Spawn spawn = new Spawn(npcTemplate, true);
			spawn.setLoc(spawnX, spawnY, spawnZ, player.getHeading());
			
			final Npc monster = spawn.doSpawn(true);
			
			if (monster != null)
			{
				monster.getAI().getAggroList().addDamageHate(player, 0, 999999);
				
				monster.setTarget(player);
				
				player.sendMessage("You have successfully summoned " + monster.getName() + "!");
				
				if (!player.destroyItem(item, 1, false))
				{
					player.sendMessage("Failed to consume the summon item.");
					monster.deleteMe();
					return;
				}
				
				SummonMobItemData.getInstance().registerItemUse(player.getObjectId(), item.getItemId());
				
				final int remainingUses = SummonMobItemData.getInstance().getRemainingDailyUses(player.getObjectId(), item.getItemId());
				if (remainingUses > 0 && itemConfig.getMaxUsesPerDay() > 0)
				{
					player.sendMessage("You have " + remainingUses + " uses remaining today.");
				}
				else if (itemConfig.getCooldownMinutes() > 0)
				{
					player.sendMessage("This item has a " + itemConfig.getCooldownMinutes() + " minute cooldown.");
				}
			}
			else
			{
				player.sendMessage("Failed to summon the monster.");
			}
		}
		catch (Exception e)
		{
			player.sendMessage("An error occurred while summoning the monster.");
			System.err.println("Error in ItemMonsterSummon: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
