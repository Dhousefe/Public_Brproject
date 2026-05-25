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
package ext.mods.gameserver.taskmanager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.instance.Folk;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.instance.RaidBoss;

/**
 * Handle all {@link Npc} AI tasks.
 */
public final class AiTaskManager implements Runnable
{
	private final Set<Npc> _npcs = ConcurrentHashMap.newKeySet();
	
	private static final int RETURN_HOME_RAIDBOSS_RADIUS = Config.RETURN_HOME_RAIDBOSS_RADIUS;
	private static final int RETURN_HOME_MONSTER_RADIUS = Config.RETURN_HOME_MONSTER_RADIUS;
	
	private static final Set<Integer> EXCLUDED_RAIDBOSS_IDS = Set.of(29095);
	private static final Set<Integer> EXCLUDED_MONSTER_IDS = Set.of(29016, 29008, 29004);
	
	protected AiTaskManager()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
		
		ThreadPool.scheduleAtFixedRate(this::animationTask, 10000, 10000);
	}
	
	@Override
	public final void run()
	{
		_npcs.forEach(npc -> processNpc(npc));
	}
	
	private void processNpc(Npc npc)
	{
		if (Config.ENABLE_NPC_MOVEMENT_OPTIMIZATION && !(npc instanceof GrandBoss || npc instanceof RaidBoss))
		{
			if (!hasVisiblePlayersNearNpc(npc))
			{
				return;
			}
		}
		
		npc.getAI().runAI();
		
		if (npc instanceof GrandBoss)
			return;
		else if (npc instanceof RaidBoss raidBoss)
			monsterReturn(raidBoss, Config.RETURN_HOME_RAIDBOSS, RETURN_HOME_RAIDBOSS_RADIUS, EXCLUDED_RAIDBOSS_IDS);
		else if (npc instanceof Monster monster)
			monsterReturn(monster, Config.RETURN_HOME_MONSTER, RETURN_HOME_MONSTER_RADIUS, EXCLUDED_MONSTER_IDS);
	}
	
	private void monsterReturn(Monster monster, boolean returnHome, int radius, Set<Integer> excludedNpcIds)
	{
		if (!returnHome || isNpcIdExcluded(monster.getNpcId(), excludedNpcIds))
			return;
		
		if (!monster.isIn3DRadius(monster.getSpawnLocation(), radius))
		{
			System.out.println("Returning monster: " + monster.getNpcId());
			monster.teleportTo(monster.getSpawnLocation(), 0);
			monster.removeAllAttackDesire();
			monster.getStatus().setHpMp(monster.getStatus().getMaxHp(), monster.getStatus().getMaxMp());
			teleportMinions(monster);
		}
	}
	
	private boolean isNpcIdExcluded(int npcId, Set<Integer> excludedNpcIds)
	{
	    return excludedNpcIds.contains(npcId);
	}
	
	private void teleportMinions(Monster monster)
	{
		monster.getMinions().forEach(minion ->
		{
			if (!minion.isDead())
			{
				minion.teleportToMaster();
				minion.removeAllAttackDesire();
				minion.getStatus().setHpMp(minion.getStatus().getMaxHp(), minion.getStatus().getMaxMp());
			}
		});
	}
	
	/**
	 * Verifica se há players visíveis próximos ao NPC.
	 * Economiza processamento evitando AI de NPCs em áreas sem players.
	 * @param npc O NPC a verificar
	 * @return true se há players visíveis, false caso contrário
	 */
	private boolean hasVisiblePlayersNearNpc(Npc npc)
	{
		if (Config.NPC_MOVEMENT_PLAYER_RANGE <= 0)
			return true;
		
		return npc.getKnownTypeInRadius(ext.mods.gameserver.model.actor.Player.class, Config.NPC_MOVEMENT_PLAYER_RANGE)
			.stream()
			.anyMatch(player -> player.getAppearance().isVisible());
	}
	
	protected final void animationTask()
	{
		_npcs.stream().filter(npc -> npc instanceof Folk).forEach(folk ->
		{
			int moveAroundSocial = folk.getTemplate().getAiParams().getInteger("MoveAroundSocial", 0);
			int moveAroundSocial1 = folk.getTemplate().getAiParams().getInteger("MoveAroundSocial1", 0);
			
			if (moveAroundSocial > 0 || moveAroundSocial1 > 0)
			{
				if (folk.getStatus().getHpRatio() > 0.4 && !folk.isDead() && folk.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
				{
					if (Rnd.get(100) < Config.NPC_ANIMATION)
					{
						if (moveAroundSocial > 0)
							folk.getAI().addSocialDesire(3, (moveAroundSocial * 1000) / 30, 50);
						else if (moveAroundSocial1 > 0)
							folk.getAI().addSocialDesire(3, (moveAroundSocial1 * 1000) / 30, 50);
					}
				}
			}
		});
	}
	
	/**
	 * Add the {@link Npc} set as parameter to the {@link AiTaskManager}.
	 * @param npc : The {@link Npc} to add.
	 */
	public final void add(Npc npc)
	{
		npc.setAISleeping(false);
		
		_npcs.add(npc);
	}
	
	/**
	 * Remove the {@link Npc} set as parameter from the {@link AiTaskManager}.
	 * @param npc : The {@link Npc} to remove.
	 */
	public final void remove(Npc npc)
	{
		npc.setAISleeping(true);
		
		_npcs.remove(npc);
	}
	
	public static final AiTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static final class SingletonHolder
	{
		protected static final AiTaskManager INSTANCE = new AiTaskManager();
	}
}