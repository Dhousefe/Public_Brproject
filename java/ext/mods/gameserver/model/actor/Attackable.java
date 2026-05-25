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
package ext.mods.gameserver.model.actor;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.Config;
import ext.mods.battlerboss.register.BattleBossOpenRegister;
import ext.mods.commons.math.MathUtil;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.extensions.listener.manager.NpcListenerManager;
import ext.mods.gameserver.data.manager.PcCafeManager;
import ext.mods.Crypta.RandomManager;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.ai.type.AttackableAI;
import ext.mods.gameserver.model.actor.container.attackable.AggroList;
import ext.mods.gameserver.model.actor.status.AttackableStatus;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.quests.QuestData;
import ext.mods.quests.QuestManager;
import ext.mods.quests.holder.QuestHolder;
import ext.mods.quests.holder.QuestObjective;
import ext.mods.quests.holder.QuestReward;

/**
 * This class manages all {@link Npc}s which can hold an {@link AggroList}.
 */
public class Attackable extends Npc
{
	private final Set<Creature> _attackedBy = ConcurrentHashMap.newKeySet();
	
	private boolean _isNoRndWalk;
	
	public Attackable(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public AttackableAI<? extends Attackable> getAI()
	{
		return (AttackableAI<?>) _ai;
	}
	
	@Override
	public void setAI()
	{
		_ai = new AttackableAI<>(this);
	}
	
	@Override
	public AttackableStatus getStatus()
	{
		return (AttackableStatus) _status;
	}
	
	@Override
	public void setStatus()
	{
		_status = new AttackableStatus(this);
	}
	
	@Override
	public void removeKnownObject(WorldObject object)
	{
		super.removeKnownObject(object);
		
		if (object instanceof Creature creature)
			getAI().getAggroList().remove(creature);
	}
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, L2Skill skill)
	{
		reduceCurrentHp(damage, attacker, true, false, skill);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		try
		{
			Player player = (killer != null) ? killer.getActingPlayer() : null;
			
			if (player != null)
			{
				NpcListenerManager.getInstance().notifyNpcKill(this, player);
				PcCafeManager.getInstance().onAttackableKill(player);
				
				Object randomManagerInstance = RandomManager.getInstance();
				if (randomManagerInstance != null) {
					RandomManager.getInstance().onKill(player, this);
				}
				
				setQuestKiller(player);
				if (player.isInBattleBoss())
				{
					BattleBossOpenRegister.getInstance().onPlayerDeathMonster(player, this);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Erro em doDie Quest System: ", e);
		}
		
		_attackedBy.clear();
		return true;
	}
	
	public void setQuestKiller(Player player)
	{
		int playerClassId = player.getClassId().getId();
		
		for (int questId : player.getActiveQuestIds())
		{
			if (player.isQuestCompleted(questId))
				continue;
			
			QuestHolder quest = QuestData.getInstance().getQuest(questId);
			if (quest == null)
				continue;
			
			List<QuestObjective> objectives = quest.getObjectivesForClass(playerClassId);
			if (objectives.isEmpty())
				continue;
			
			boolean isQuestNpc = false;
			for (QuestObjective obj : objectives)
			{
				if (obj.getNpcId() == getNpcId())
				{
					isQuestNpc = true;
					break;
				}
			}
			
			if (!isQuestNpc)
				continue;
			
			player.increaseQuestKillCount(questId, getNpcId());
			
			boolean allRequirementsMet = true;
			for (QuestObjective obj : objectives)
			{
				long currentKills = player.getQuestKillCount(questId, obj.getNpcId());
				if (currentKills < obj.getCount())
				{
					allRequirementsMet = false;
					break;
				}
			}
			
			if (allRequirementsMet)
			{
				player.sendMessage("Objective complete: You have killed all required monsters.");
				
				for (QuestReward reward : quest.getRewardsForClass(playerClassId))
				{
					Item template = ItemData.getInstance().getTemplate(reward.getItemId());
					if (template != null)
					{
						player.addItem(reward.getItemId(), reward.getCount(), true);
					}
				}
				
				for (QuestObjective obj : objectives)
				{
					player.resetQuestKillCount(quest.getId(), obj.getNpcId());
				}
				
				player.setQuestCompleted(questId, true);
				
				if (!quest.getSound().isEmpty())
				{
					player.sendPacket(new PlaySound(quest.getSound()));
				}
				
				player.sendPacket(new PlaySound("ItemSound.quest_tutorial"));
				
				QuestManager.getInstance().showCompleteQuest(player, quest, 1);
			}
		}
	}
	
	@Override
	public void onSpawn()
	{
		getAI().getAggroList().clear();
		getAI().getHateList().clear();
		
		super.onSpawn();
		
		if (!isInActiveRegion())
			getAI().stopAITask();
	}
	
	@Override
	public void onInteract(Player player)
	{
	}
	
	@Override
	public void onInactiveRegion()
	{
		getAttackByList().clear();
		
		super.onInactiveRegion();
	}
	
	@Override
	public boolean isLethalable()
	{
		switch (getNpcId())
		{
			case 22215:
			case 22216:
			case 22217:
			case 35062:
			case 35410:
			case 35368:
			case 35375:
			case 35629:
				return false;
		}
		return true;
	}
	
	/**
	 * Add a {@link Creature} attacker on _attackedBy {@link List}.
	 * @param attacker : The {@link Creature} to add.
	 */
	public void addAttacker(Creature attacker)
	{
		if (attacker == null || attacker == this)
			return;
		
		_attackedBy.add(attacker);
	}
	
	/**
	 * @return True if the {@link Attackable} successfully returned to spawn point. In case of minions, they are simply deleted.
	 */
	public boolean returnHome()
	{
		if (isInMyTerritory())
			return false;
		
		if (!getAI().getHateList().isEmpty())
			return false;
		
		if (getSpawnLocation() == null || isIn2DRadius(getSpawnLocation(), getDriftRange()))
			return false;
		
		getAI().getAggroList().cleanAllHate();
		
		forceWalkStance();
		
		if (getMove().getGeoPathFailCount() >= 10)
			teleportTo(getSpawnLocation(), 0);
		else
		{
			getMove().maybeMoveToLocation(getSpawnLocation(), 0, true, false);
			ThreadPool.schedule(() ->
			{
				if (getAI().getCurrentIntention().getType() == IntentionType.WANDER)
				{
					Point2D backwardsPoint = MathUtil.getNewLocationByDistanceAndDegree(getPosition().getX(), getPosition().getY(), MathUtil.convertHeadingToDegree(getHeading()) - 180, Math.min((int) getTemplate().getCollisionRadius() * 2, 50));
					getMove().maybeMoveToLocation(new Location(backwardsPoint.getX(), backwardsPoint.getY(), getPosition().getZ()), 0, true, false);
				}
			}, (int) (Rnd.get(1500, 2500) * (100 / getStatus().getMoveSpeed())));
		}
		
		return true;
	}
	
	public int getDriftRange()
	{
		return Config.MAX_DRIFT_RANGE;
	}
	
	public final Set<Creature> getAttackByList()
	{
		return _attackedBy;
	}
	
	public final boolean isNoRndWalk()
	{
		return _isNoRndWalk;
	}
	
	public final void setNoRndWalk(boolean value)
	{
		_isNoRndWalk = value;
	}
	
	/**
	 * @return The {@link ItemInstance} used as weapon of this {@link Attackable} (null by default).
	 */
	public ItemInstance getActiveWeapon()
	{
		return null;
	}
	
	public boolean isGuard()
	{
		return false;
	}
}