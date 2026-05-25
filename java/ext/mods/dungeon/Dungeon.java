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
package ext.mods.dungeon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.skills.L2Skill;

import ext.mods.InstanceMap.InstanceManager;
import ext.mods.InstanceMap.MapInstance;
import ext.mods.dungeon.holder.SpawnTemplate;
import ext.mods.dungeon.holder.StageTemplate;

public class Dungeon
{
	
	private final DungeonTemplate template;
	private final List<Player> players;
	private final Map<Monster, SpawnTemplate> mobToTemplate = new ConcurrentHashMap<>();
	private ScheduledFuture<?> dungeonCancelTask;
	private ScheduledFuture<?> nextTask;
	private ScheduledFuture<?> timerTask;
	private ScheduledFuture<?> countdownTask;
	
	private long stageBeginTime;
	private final MapInstance instance;
	private StageTemplate currentStage;
	private List<SpawnTemplate> currentSpawns;
	private int currentStageIndex = 0;
	private boolean stagePaused = false;
	private long stageRemainingMs = 0L;
	private int disconnectPauseCount = 0;
	
	private ScheduledFuture<?> monitorTask;
	private volatile boolean _isEnding = false;
	
	public Dungeon(DungeonTemplate template, List<Player> players)
	{
		this.template = template;
		this.players = players;
		this.instance = InstanceManager.getInstance().createInstance();
		beginTeleport();
		
	}
	
	private void beginTeleport()
    {
        if (!getNextStage())
        {
            broadcastScreenMessage("Failed to load dungeon stage!", 5);
            cancelDungeon();
            return;
        }
        
        Player initiator = (players != null && !players.isEmpty()) ? players.get(0) : null;

		if (initiator == null)
        {
            cancelDungeon();
            return;
        }
        final int maxDistance = 1500;
        for (Player member : players)
        {
            if (member == null || member.equals(initiator))
            {
                continue;
            }
            
            if (!member.isIn3DRadius(initiator, maxDistance))
            {
                member.teleportTo(initiator.getX(), initiator.getY(), initiator.getZ(), 0);
                member.sendMessage("You have been teleported to your party leader to start the dungeon.");
            }
        }
        if (initiator != null && initiator.isInsideZone(ZoneId.PEACE))
        {
            broadcastScreenMessage("Peace Zone detected. Teleporting directly!", 5);
            
            for (Player player : players)
            {
                player.setInstanceMap(instance, true);
                player.setDungeon(this);
            }
            teleportPlayers();
        }
        else
        {
            broadcastScreenMessage("You will be teleported in a few seconds!", 5);
            
            L2Skill skill = SkillTable.getInstance().getInfo(1050, 1);
            
            for (Player player : players)
            {
                player.setIsParalyzed(true);
                player.broadcastPacketInRadius(new MagicSkillUse(player, player, skill.getId(), skill.getLevel(), 9000, 5000), 1500);
                player.setInstanceMap(instance, true);
                player.getCast().doCast(skill, player, null);
                player.setDungeon(this);
                
                ThreadPool.schedule(() -> player.setIsParalyzed(false), 10000);
            }
            
            nextTask = ThreadPool.schedule(this::teleportPlayers, 10000);
        }
    }
	
	private void teleportPlayers()
	{
		teleToStage();
		broadcastScreenMessage("Stage " + currentStage.order + " begins in 10 seconds!", 5);
		nextTask = ThreadPool.schedule(this::finalProximityCheck, 10 * 1000);
	}

	private void finalProximityCheck()
    {
        Player initiator = (players != null && !players.isEmpty()) ? players.get(0) : null;
        if (initiator == null)
        {
            cancelDungeon();
            return;
        }
        
        boolean allClose = true;
        final int maxDistance = 1000;
        
        for (Player member : players)
        {
            if (member == null || member.equals(initiator))
                continue;
            
            if (!member.isIn3DRadius(initiator, maxDistance))
            {
                allClose = false;
                break;
            }
        }
        
        if (allClose)
        {
            beginStage();
        }
        else
        {
            broadcastScreenMessage("A party member is not present. Re-teleporting...", 5);
            teleToStage();
            nextTask = ThreadPool.schedule(this::finalProximityCheck, 5 * 1000);
        }
    }
	
	private void beginStage()
	{
		stagePaused = false;
		stageRemainingMs = 0L;
		for (SpawnTemplate spawn : currentSpawns)
		{
			for (int i = 0; i < spawn.count; i++)
			{
				NpcTemplate template = NpcData.getInstance().getTemplate(spawn.npcId);
				try
				{
					Spawn spawns = new Spawn(template);
					
					int x = spawn.x;
					int y = spawn.y;
					int z = spawn.z;
					
					if (spawn.count > 1)
					{
						final int radius = spawn.range;
						final double angle = Rnd.nextDouble() * 2 * Math.PI;
						x += (int) (Math.cos(angle) * Rnd.get(0, radius));
						y += (int) (Math.sin(angle) * Rnd.get(0, radius));
					}
					
					Location loc = new Location(x, y, z);
					
					spawns.setLoc(loc.getX(), loc.getY(), loc.getZ(), 0);
					
					spawns.doSpawn(false);
					((Monster) spawns.getNpc()).setDungeon(this);
					spawns.getNpc().setInstanceMap(instance, false);
					spawns.getNpc().setTitle(spawn.title);
					SpawnManager.getInstance().addSpawn(spawns);
					
					spawns.getNpc().broadcastPacket(new NpcInfo(spawns.getNpc(), null));
					
					mobToTemplate.put(((Monster) spawns.getNpc()), spawn);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		broadcastScreenMessage("You have " + currentStage.timeLimit + " minutes to finish stage " + currentStage.order + "!", 5);
		stageBeginTime = System.currentTimeMillis();
		updatePlayerStage(currentStage.order);
		timerTask = ThreadPool.scheduleAtFixedRate(this::broadcastTimer, 5 * 1000, 1000);
		dungeonCancelTask = ThreadPool.schedule(this::cancelDungeon, 1000 * 60 * currentStage.timeLimit);
		monitorTask = ThreadPool.scheduleAtFixedRate(this::monitorDungeon, 5000, 5000);
		
	}

	public synchronized void pauseForDisconnect(Player player)
	{
		if (currentStage == null)
			return;
		
		disconnectPauseCount++;
		if (disconnectPauseCount == 1)
			pauseStageTimer();
	}

	public synchronized void resumeForReconnect(Player player)
	{
		if (currentStage == null)
			return;
		
		if (disconnectPauseCount > 0)
			disconnectPauseCount--;
		
		if (disconnectPauseCount == 0)
			resumeStageTimer();
	}

	private void pauseStageTimer()
	{
		if (stagePaused || currentStage == null)
			return;
		
		final long totalMs = currentStage.timeLimit * 60L * 1000L;
		final long elapsed = System.currentTimeMillis() - stageBeginTime;
		stageRemainingMs = Math.max(0L, totalMs - elapsed);
		stagePaused = true;
		
		if (timerTask != null)
		{
			timerTask.cancel(true);
			timerTask = null;
		}
		if (dungeonCancelTask != null)
		{
			dungeonCancelTask.cancel(true);
			dungeonCancelTask = null;
		}
		if (monitorTask != null)
		{
			monitorTask.cancel(true);
			monitorTask = null;
		}
	}

	private void resumeStageTimer()
	{
		if (!stagePaused || currentStage == null)
			return;
		
		final long totalMs = currentStage.timeLimit * 60L * 1000L;
		final long remaining = Math.max(1000L, stageRemainingMs);
		stageBeginTime = System.currentTimeMillis() - (totalMs - remaining);
		
		timerTask = ThreadPool.scheduleAtFixedRate(this::broadcastTimer, 5 * 1000, 1000);
		dungeonCancelTask = ThreadPool.schedule(this::cancelDungeon, remaining);
		monitorTask = ThreadPool.scheduleAtFixedRate(this::monitorDungeon, 5000, 5000);
		
		stagePaused = false;
	}
	
	private void monitorDungeon()
	{
		
		boolean allDead = true;
        if (players.size() > 1)
        {
            Player initiator = players.get(0);
            if (initiator != null && initiator.getParty() == null)
            {
                broadcastScreenMessage("The party has been disbanded. The dungeon is being canceled.", 5);
                cancelDungeon();
                return;
            }
        }
		
		for (Player player : players)
		{
			if (player == null)
				continue;
			
			if (!player.isDead())
				allDead = false;

			
		}
		
		if (allDead)
		{
			cancelDungeon();
		}
	}
	
	private boolean advanceToNextStage()
	{
		if (!getNextStage())
			return false;
		
		if (currentStage != null)
		{
			ThreadPool.schedule(this::teleToStage, 5 * 1000);
			return true;
		}
		return false;
	}
	
	private void teleToStage()
	{
		if (currentStage.teleport)
		{
			for (Player player : players)
			{
				player.teleportTo(currentStage.x, currentStage.y, currentStage.z, 0);
			}
		}
	}
	
	public void cancelDungeon()
    {
		
        synchronized (this)
        {
            if (_isEnding)
                return;
            _isEnding = true;
        }
        
        stagePaused = false;
        stageRemainingMs = 0L;
        disconnectPauseCount = 0;
        
		for (Player player : players)
		{
			player.teleportTo(81664, 149056, -3472, 15);
		}

        cancelScheduledTasks();
        
        for (Attackable mob : mobToTemplate.keySet())
        {
            deleteMob(mob);
        }

		
        
        broadcastScreenMessage("You have failed to complete the dungeon. ", 5);
        ThreadPool.schedule(this::teleToTown, 5 * 1000);
    }
	
	private static void deleteMob(Attackable mob)
	{
		ASpawn spawn = mob.getSpawn();
		spawn.setRespawnDelay(0);
		spawn.getNpc().deleteMe();
		SpawnManager.getInstance().deleteSpawn((Spawn) spawn);
		
	}
	
	private void teleToTown()
	{
		
		for (Player player : players)
		{
			
			if (player.isDead())
				player.doRevive();
			
			player.teleportTo(81664, 149056, -3472, 15);
			
			player.broadcastCharInfo();
			player.broadcastUserInfo();
			player.setDungeon(null);
			player.setInstanceMap(InstanceManager.getInstance().getInstance(0), true);
			
		}
		
		cleanupDungeon();
	}
	
	private void cleanupDungeon()
	{
		InstanceManager.getInstance().deleteInstance(instance.getId());
		DungeonManager.getInstance().removeDungeon(this);
		
		cancelScheduledTasks();
	}
	
	private void completeDungeon()
    {
        broadcastScreenMessage("You have completed the dungeon!", 5);
        startReturnCountdown(50);
    }
    
    /**
     * Inicia uma contagem regressiva para teleportar os jogadores de volta à cidade.
     * @param seconds O tempo em segundos para a contagem.
     */
    private void startReturnCountdown(int seconds)
    {
        if (seconds <= 0)
        {
            teleToTown();
            return;
        }
        
        final String msg = "Returning to town in " + seconds + "...";
        ExShowScreenMessage packet = new ExShowScreenMessage(msg, 1100, SMPOS.TOP_CENTER, false);
        for (Player player : players)
        {
            player.sendPacket(packet);
        }
        
        countdownTask = ThreadPool.schedule(() -> startReturnCountdown(seconds - 1), 1000);
    }
	
	private void cancelScheduledTasks()
    {
        if (dungeonCancelTask != null)
        {
            dungeonCancelTask.cancel(true);
            dungeonCancelTask = null;
        }
        if (monitorTask != null)
        {
            monitorTask.cancel(true);
            monitorTask = null;
        }
        if (timerTask != null)
        {
            timerTask.cancel(true);
            timerTask = null;
        }
        if (nextTask != null)
        {
            nextTask.cancel(true);
            nextTask = null;
        }
        if (countdownTask != null)
        {
            countdownTask.cancel(true);
            countdownTask = null;
        }
    }
	
	private void broadcastTimer()
	{
		int secondsLeft = (int) ((stageBeginTime + (1000 * 60 * currentStage.timeLimit)) - System.currentTimeMillis()) / 1000;
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft % 60;
		
		ExShowScreenMessage packet = new ExShowScreenMessage(String.format("%02d:%02d", minutes, seconds), 1010, SMPOS.BOTTOM_RIGHT, false);
		for (Player player : players)
		{
			player.sendPacket(packet);
		}
	}
	
	private void broadcastScreenMessage(String msg, int seconds)
	{
		ExShowScreenMessage packet = new ExShowScreenMessage(msg, seconds * 1000, SMPOS.TOP_CENTER, false);
		for (Player player : players)
		{
			player.sendPacket(packet);
		}
	}
	
	private boolean getNextStage()
	{
		if (template.stages.isEmpty())
			return false;
		
		if (currentStageIndex >= template.stages.size())
		{
			currentStage = null;
			return false;
		}
		
		currentStage = template.stages.get(currentStageIndex);
		
		List<SpawnTemplate> stageSpawns = template.spawns.get(currentStage.order);
		if (stageSpawns == null || stageSpawns.isEmpty())
			return false;
		
		currentSpawns = stageSpawns;
		
		stageBeginTime = System.currentTimeMillis();
		currentStageIndex++;
		return true;
	}
	
	public synchronized void onMobKill(Attackable attackable)
	{
		
		SpawnTemplate spawnTemplate = mobToTemplate.remove(attackable);
		if (spawnTemplate == null)
			return;
		
		List<DropData> drops = parseDrops(spawnTemplate.drops);
		
		for (Player player : players)
		{
			if (drops != null && !drops.isEmpty())
			{
				for (DropData drop : drops)
				{
					if (Rnd.get(1000000) < drop.chance)
					{
						int totalAmount = Rnd.get(drop.min, drop.max);
						
						if (player.isInParty())
						{
							List<Player> members = player.getParty().getMembers();
							int size = members.size();
							
							int baseAmount = totalAmount / size;
							int remainder = totalAmount % size;
							
							for (Player member : members)
							{
								int amount = baseAmount;
								if (remainder > 0)
								{
									amount++;
									remainder--;
								}
								
								if (amount > 0)
									member.addItem(drop.itemId, amount, true);
							}
						}
						else
						{
							player.addItem(drop.itemId, totalAmount, true);
						}
					}
				}
			}
		}
		
		deleteMob(attackable);
		
		if (mobToTemplate.isEmpty())
		{
			cancelScheduledTasks();
			
			if (advanceToNextStage())
			{
				broadcastScreenMessage("You have completed stage " + (currentStage.order - 1) + "! Next stage begins in 10 seconds.", 5);
				nextTask = ThreadPool.schedule(this::beginStage, 10 * 1000);
			}
			else
			{
				completeDungeon();
			}
		}
	}
	
	private List<DropData> parseDrops(String dropString)
	{
		List<DropData> drops = new ArrayList<>();
		if (dropString == null || dropString.isEmpty())
			return drops;
		
		String[] entries = dropString.split(";");
		for (String entry : entries)
		{
			String[] parts = entry.split("-");
			if (parts.length < 3)
				continue;
			
			int itemId = Integer.parseInt(parts[0]);
			int min = Integer.parseInt(parts[1]);
			int max = Integer.parseInt(parts[2]);
			int chance = parts.length > 3 ? Integer.parseInt(parts[3]) : 1000000;
			
			drops.add(new DropData(itemId, min, max, chance));
		}
		return drops;
	}
	
	public class DropData
	{
		public final int itemId;
		public final int min;
		public final int max;
		public final int chance;
		
		public DropData(int itemId, int min, int max, int chance)
		{
			this.itemId = itemId;
			this.min = min;
			this.max = max;
			this.chance = chance;
		}
	}
	
	public void updatePlayerStage(int stage)
	{
		for (Player player : players)
		{
			DungeonManager.getInstance().updateStage(template.id, player.getObjectId(), stage);
		}
	}
	
	public List<Player> getPlayers()
	{
		return players;
	}
	
}
