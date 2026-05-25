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
package ext.mods.battlerboss.matches;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import ext.mods.InstanceMap.InstanceManager;
import ext.mods.InstanceMap.MapInstance;
import ext.mods.battlerboss.cinematic.CinematicTaskEpicTrailer;
import ext.mods.battlerboss.holder.EventHolder;
import ext.mods.battlerboss.holder.RewardHolder;
import ext.mods.battlerboss.holder.RewardsHolder;
import ext.mods.battlerboss.register.BattleBossOpenRegister;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.TeamType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;

public class BattleBossRumble implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(BattleBossRumble.class.getName());
	
	private enum Phase
	{
		PREP,
		BATTLE,
		BOSS
	}
	
	private static final class Team
	{
		final List<Player> members;
		
		Team(List<Player> members)
		{
			this.members = members;
		}
		
		@Override
		public int hashCode()
		{
			return System.identityHashCode(members);
		}
		
		@Override
		public boolean equals(Object o)
		{
			return (o instanceof Team) && ((Team) o).members == this.members;
		}
		
		@Override
		public String toString()
		{
			return "Team@" + System.identityHashCode(members) + " size=" + members.size();
		}
	}
	
	private final int rumbleId;
	private final EventHolder event;
	private final MapInstance instance;
	
	private final Set<Player> participants = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final List<Team> teams = new CopyOnWriteArrayList<>();
	private final Map<Player, Team> playerTeam = new ConcurrentHashMap<>();
	private final Set<Team> aliveTeams = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private final Map<Team, Set<Player>> aliveMembersByTeam = new ConcurrentHashMap<>();
	
	private final List<Spawn> monsterSpawns = new CopyOnWriteArrayList<>();
	
	private volatile ScheduledFuture<?> taskStartBattle;
	private volatile ScheduledFuture<?> taskPhaseTimeout;
	private volatile ScheduledFuture<?> taskBroadcast;
	private volatile ScheduledFuture<?> taskDisconnect;
	
	private final AtomicBoolean finished = new AtomicBoolean(false);
	private final AtomicBoolean battleStarted = new AtomicBoolean(false);
	private final AtomicBoolean paused = new AtomicBoolean(false);
	private volatile Phase phase = Phase.PREP;
	
	private volatile long stageBeginTime;
	private volatile long stageDurationMillis;
	
	private final Map<Team, Boolean> teamOfflineFlag = new ConcurrentHashMap<>();
	private final Set<String> rewardedHwids = Collections.newSetFromMap(new ConcurrentHashMap<>());
	
	public BattleBossRumble(int rumbleId, EventHolder event, List<Player> players)
	{
		this.rumbleId = rumbleId;
		this.event = event;
		this.instance = InstanceManager.getInstance().createInstance();
		if (players != null)
		{
			this.participants.addAll(players);
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			startPreparation();
			taskStartBattle = ThreadPool.schedule(this::startBattleSafe, 7000L);
		}
		catch (Throwable t)
		{
			LOGGER.log(Level.SEVERE, "[BattleBoss] Falha no run inicial", t);
			forceFinish();
		}
	}
	
	private void startPreparation()
	{
		phase = Phase.PREP;
		makeTeams();
		teleportTeamsAndSetup();
		startDisconnectionWatcher();
	}
	
	private void makeTeams()
	{
		final int groupSize = Math.max(1, event.getRegistration().getGroupSize());
		final List<Player> pool = new ArrayList<>(participants);
		
		rewardedHwids.clear();
		teams.clear();
		aliveTeams.clear();
		playerTeam.clear();
		aliveMembersByTeam.clear();
		
		Map<Party, List<Player>> byParty = new HashMap<>();
		for (Player p : pool)
			if (p != null && p.isInParty())
				byParty.computeIfAbsent(p.getParty(), k -> new ArrayList<>()).add(p);
		for (List<Player> partyPlayers : byParty.values())
			extractFullGroups(partyPlayers, pool, groupSize);
		
		Map<Clan, List<Player>> byClan = new HashMap<>();
		for (Player p : new ArrayList<>(pool))
			if (p != null && p.hasClan())
				byClan.computeIfAbsent(p.getClan(), k -> new ArrayList<>()).add(p);
		for (List<Player> clanPlayers : byClan.values())
			extractFullGroups(clanPlayers, pool, groupSize);
		
		Collections.shuffle(pool);
		List<Player> buffer = new ArrayList<>(groupSize);
		for (Player p : pool)
		{
			if (p == null)
				continue;
			buffer.add(p);
			if (buffer.size() == groupSize)
			{
				addTeam(new ArrayList<>(buffer));
				buffer.clear();
			}
		}
		if (!buffer.isEmpty())
			addTeam(new ArrayList<>(buffer));
		
		if (teams.size() == 1 && pool.size() >= 2)
		{
			List<Player> oneTeam = new ArrayList<>(teams.get(0).members);
			resetTeamStructures();
			int mid = Math.max(1, oneTeam.size() / 2);
			addTeam(new ArrayList<>(oneTeam.subList(0, mid)));
			addTeam(new ArrayList<>(oneTeam.subList(mid, oneTeam.size())));
		}
	}
	
	private void extractFullGroups(List<Player> src, List<Player> pool, int groupSize)
	{
		if (src == null)
			return;
		while (src.size() >= groupSize)
		{
			List<Player> squad = new ArrayList<>(groupSize);
			for (int i = 0; i < groupSize; i++)
			{
				Player take = src.remove(0);
				if (take != null)
				{
					squad.add(take);
					pool.remove(take);
				}
			}
			if (!squad.isEmpty())
				addTeam(squad);
		}
	}
	
	private void addTeam(List<Player> list)
	{
		list.removeIf(p -> p == null);
		if (list.isEmpty())
			return;
		Team team = new Team(Collections.unmodifiableList(list));
		teams.add(team);
		aliveTeams.add(team);
		Set<Player> aliveSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
		aliveSet.addAll(list);
		aliveMembersByTeam.put(team, aliveSet);
		for (Player p : list)
			playerTeam.put(p, team);
	}
	
	private void resetTeamStructures()
	{
		teams.clear();
		aliveTeams.clear();
		playerTeam.clear();
		aliveMembersByTeam.clear();
	}
	
	private void teleportTeamsAndSetup()
	{
		final int arenaX = event.getTeleport().getArenaX();
		final int arenaY = event.getTeleport().getArenaY();
		final int arenaZ = event.getTeleport().getArenaZ();
		
		int colorToggle = 0;
		for (Team team : new ArrayList<>(teams))
		{
			TeamType color = (colorToggle++ % 2 == 0) ? TeamType.BLUE : TeamType.RED;
			int spacing = 120;
			int baseX = (color == TeamType.BLUE) ? arenaX - 500 : arenaX + 500;
			int baseY = arenaY;
			
			int rowSize = (int) Math.ceil(Math.sqrt(Math.max(1, team.members.size())));
			int index = 0;
			for (Player p : team.members)
			{
				if (p == null)
					continue;
				try
				{
					setInvulParalyze(p, true);
					if (p.isFakeDeath())
						p.stopFakeDeath(true);
					screenMsg(p, "You have been teleported to the arena! Get ready.", 10);
					applyHealAndRevive(p);
					p.setInstanceMap(instance, true);
					p.setTeam(color);
					int row = index / rowSize;
					int col = index % rowSize;
					int offsetX = baseX + ((col - rowSize / 2) * spacing);
					int offsetY = baseY + ((row - rowSize / 2) * spacing);
					p.teleportTo(offsetX, offsetY, arenaZ, (color == TeamType.BLUE) ? 90 : 270);
					index++;
					p.getStatus().setHp(p.getStatus().getMaxHp());
					p.getStatus().setCp(p.getStatus().getMaxCp());
					p.getStatus().setMp(p.getStatus().getMaxMp());
					p.stopAllEffectsDebuff();
					p.broadcastCharInfo();
				}
				catch (Exception e)
				{
					LOGGER.log(Level.FINE, "Falha ao teleportar jogador", e);
				}
			}
		}
	}
	
	private void startBattleSafe()
	{
		try
		{
			startBattle();
		}
		catch (Throwable t)
		{
			LOGGER.log(Level.SEVERE, "startBattle falhou", t);
			forceFinish();
		}
	}
	
	private void startBattle()
	{
		if (finished.get())
			return;
		phase = Phase.BATTLE;
		battleStarted.set(true);
		paused.set(false);
		
		stageBeginTime = System.currentTimeMillis();
		stageDurationMillis = Math.max(1000L, 1000L * event.getBattle().getDuration());
		
		for (Player p : snapshotParticipants())
		{
			applyHealAndRevive(p);
			screenMsg(p, "A batalha começou!", 5);
			setInvulParalyze(p, false);
			if (p.isFakeDeath())
				p.stopFakeDeath(true);
			p.setBattleBossRumbleId(rumbleId);
			p.setBattleBossEventId(event.getId());
		}
		
		reschedulePhaseTimeout(this::onBattleTimeOver, stageDurationMillis);
		rescheduleBroadcast();
	}
	
	private void onBattleTimeOver()
	{
		if (finished.get())
			return;
		if (!battleStarted.get())
			startBattleSafe();
		List<Player> winners = decideWinnersOnTimeout();
		endBattleAndProceed(winners);
	}
	
	private List<Player> decideWinnersOnTimeout()
	{
		boolean winnerByHpCp = event.getBattle().isWinnerByHpCp();
		List<Team> contenders = new ArrayList<>();
		for (Team t : new ArrayList<>(aliveTeams))
			if (!aliveMembersByTeam.getOrDefault(t, Collections.emptySet()).isEmpty())
				contenders.add(t);
			
		if (contenders.isEmpty())
			return Collections.emptyList();
		if (contenders.size() == 1)
			return new ArrayList<>(contenders.get(0).members);
		if (!winnerByHpCp)
			return Collections.emptyList();
		
		Team bestTeam = null;
		double bestScore = -1;
		int bestAlive = -1;
		for (Team t : contenders)
		{
			double score = 0;
			int alive = 0;
			for (Player p : aliveMembersByTeam.getOrDefault(t, Collections.emptySet()))
			{
				alive++;
				score += safeHp(p) + safeCp(p);
			}
			if (score > bestScore || (score == bestScore && alive > bestAlive))
			{
				bestScore = score;
				bestAlive = alive;
				bestTeam = t;
			}
		}
		return (bestTeam == null) ? Collections.emptyList() : new ArrayList<>(bestTeam.members);
	}
	
	private double safeHp(Player p)
	{
		try
		{
			return Math.max(0, p.getStatus().getHp());
		}
		catch (Throwable t)
		{
			return 0;
		}
	}
	
	private double safeCp(Player p)
	{
		try
		{
			return Math.max(0, p.getStatus().getCp());
		}
		catch (Throwable t)
		{
			return 0;
		}
	}
	
	private void endBattleAndProceed(List<Player> winners)
	{
		if (!finished.compareAndSet(false, true))
			return;
		
		paused.set(false);
		cancel(taskBroadcast);
		
		if (winners != null && !winners.isEmpty())
		{
			long delay = 11500L;
			ThreadPool.schedule(() ->
			{
				try
				{
					spawnMonsterChallenge(winners);
				}
				catch (Throwable t)
				{
					LOGGER.log(Level.SEVERE, "spawnMonsterChallenge falhou", t);
					fallbackRewardAndExit(winners);
				}
			}, delay);
		}
		else
		{
			cleanup(false, null);
		}
	}
	
	public void onPlayerDeath(Player deadPlayer)
	{
		if (!battleStarted.get() || finished.get())
			return;
		if (deadPlayer == null || !participants.contains(deadPlayer))
			return;
		
		Team team = playerTeam.get(deadPlayer);
		if (team == null)
			return;
		
		boolean stayDownOnDeath = event.getBattle().isStayDownOnDeath();
		Set<Player> aliveSet = aliveMembersByTeam.get(team);
		if (aliveSet != null)
			aliveSet.remove(deadPlayer);
		if (!stayDownOnDeath)
			deadPlayer.sendMessage("You can try to get back into the fight if you can..");
		
		if (aliveSet == null || aliveSet.isEmpty())
		{
			aliveTeams.remove(team);
			cancel(taskBroadcast);
			for (Player member : new ArrayList<>(team.members))
			{
				if (member == null)
					continue;
				setInvulParalyze(member, true);
				screenMsg(member, "Your team has been eliminated!", 10);
				giveRewardsChecked(member, false);
				applyHealAndRevive(member);
				ThreadPool.schedule(() -> safeReturnAndDetach(member), 10000L);
			}
		}
		
		if (aliveTeams.size() == 1)
		{
			List<Player> winners = new ArrayList<>(aliveTeams.iterator().next().members);
			for (Player w : winners)
			{
				screenMsg(w, "Congratulations! Your team won!", 10);
				setInvulParalyze(w, true);
			}
			endBattleAndProceed(winners);
		}
		else if (aliveTeams.isEmpty())
		{
			endBattleAndProceed(Collections.emptyList());
		}
	}
	
	public void onPlayerDeathMonster(Player player, Creature monster)
	{
		cancel(taskBroadcast);
		if (!battleStarted.get() || finished.get())
			return;
		
		List<Player> winners = new ArrayList<>();
		for (Team team : new ArrayList<>(aliveTeams))
			winners.addAll(team.members);
		for (Player w : winners)
		{
			screenMsg(w, "Congratulations! You defeated the boss!", 10);
			w.sendMessage("Teleport em " + event.getTeleport().getReturnDelay() + " segundos.");
		}
		ThreadPool.schedule(() ->
		{
			teleportBack(winners);
			cleanup(false, winners);
		}, Math.max(1000L, 1000L * event.getTeleport().getReturnDelay()));
		finished.set(true);
	}
	
	public void removePlayer(Player player)
	{
		if (player == null)
			return;
		participants.remove(player);
		Team team = playerTeam.remove(player);
		if (team != null)
		{
			Set<Player> alive = aliveMembersByTeam.get(team);
			if (alive != null)
				alive.remove(player);
			if (alive == null || alive.isEmpty())
				aliveTeams.remove(team);
		}
	}
	
	public void forceFinish()
	{
		if (finished.get())
			return;
		endBattleAndProceed(Collections.emptyList());
	}
	
	private void spawnMonsterChallenge(List<Player> winners)
	{
		if (event.getMonster() == null || event.getMonster().getNpcId() <= 0)
		{
			for (Player p : winners)
				giveRewardsChecked(p, true);
			teleportBack(winners);
			cleanup(false, null);
			return;
		}
		
		phase = Phase.BOSS;
		finished.set(false);
		paused.set(false);
		cancel(taskBroadcast);
		taskBroadcast = null;
		
		if (event.getMonster().isReviveTeam())
		{
			for (Player p : winners)
			{
				try
				{
					if (p.isFakeDeath())
						p.stopFakeDeath(true);
				}
				catch (Exception ignored)
				{
				}
				p.getStatus().setHp(p.getStatus().getMaxHp());
				p.getStatus().setCp(p.getStatus().getMaxCp());
				p.getStatus().setMp(p.getStatus().getMaxMp());
			}
		}
		
		final int npcId = event.getMonster().getNpcId();
		try
		{
			NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
			if (template == null)
			{
				LOGGER.warning("[Battle Boss] NPC Template não encontrado para ID: " + npcId);
				fallbackRewardAndExit(winners);
				return;
			}
			
			for (Player p : winners)
				giveRewardsChecked(p, true);
			
			Spawn spawn = new Spawn(template);
			spawn.setLoc(new SpawnLocation(event.getMonster().getX(), event.getMonster().getY(), event.getMonster().getZ(), Rnd.get(65535)));
			spawn.doSpawn(false);
			
			Npc npc = spawn.getNpc();
			if (npc != null)
			{
				npc.setInstanceMap(instance, false);
				SpawnManager.getInstance().addSpawn(spawn, false);
				monsterSpawns.add(spawn);
				npc.setIsParalyzed(true);
				for (Player p : winners)
				{
					setInvulParalyze(p, true);
					ThreadPool.schedule(() -> CinematicTaskEpicTrailer.playNpcCinematic(npc, p), 1500L);
				}
				
				ThreadPool.schedule(() ->
				{
					stageBeginTime = System.currentTimeMillis();
					stageDurationMillis = Math.max(1000L, 1000L * event.getMonster().getTimeLimit());
					reschedulePhaseTimeout(() ->
					{
						for (Player p : winners)
							setInvulParalyze(p, true);
						ThreadPool.schedule(() ->
						{
							teleportBack(winners);
							cleanup(false, null);
							for (Player p : winners)
								setInvulParalyze(p, false);
						}, 10000L);
					}, stageDurationMillis);
					rescheduleBroadcast();
				}, 15500L);
			}
		}
		catch (Exception e)
		{
			LOGGER.log(Level.WARNING, "[Battle Boss] Falha ao spawnar monstro do desafio", e);
			fallbackRewardAndExit(winners);
		}
	}
	
	private void fallbackRewardAndExit(List<Player> winners)
	{
		for (Player p : winners)
			giveRewardsChecked(p, true);
		teleportBack(winners);
		cleanup(false, null);
	}
	
	private void giveRewards(Player player, RewardsHolder rewards, boolean winner)
	{
		if (player == null || rewards == null)
			return;
		List<RewardHolder> list = winner ? rewards.getWinners() : rewards.getLosers();
		if (list == null || list.isEmpty())
			return;
		for (RewardHolder reward : list)
		{
			int itemId = reward.getItemId();
			int itemCount = (int) reward.getCount();
			player.addItem(itemId, itemCount, true);
		}
	}
	
	private void giveRewardsChecked(Player player, boolean winner)
	{
		if (player == null)
			return;
		String hwid = null;
		try
		{
			hwid = player.getClient() != null ? player.getClient().getHWID() : null;
		}
		catch (Throwable ignored)
		{
		}
		if (hwid == null || hwid.isEmpty())
			return;
		if (!rewardedHwids.add(hwid))
		{
			player.sendMessage("You cannot receive reward: There is already another character on the same HWID.");
			return;
		}
		giveRewards(player, event.getRewards(), winner);
	}
	
	private void teleportBack(List<Player> players)
	{
		for (Player p : safeList(players))
		{
			setInvulParalyze(p, true);
			p.setBattleBossRumbleId(0);
			p.leaveBattleBoss();
			p.setTeam(TeamType.NONE);
			p.setInstanceMap(InstanceManager.getInstance().getInstance(0), true);
			p.teleportTo(event.getTeleport().getReturnX(), event.getTeleport().getReturnY(), event.getTeleport().getReturnZ(), 150);
			applyHealAndRevive(p);
			setInvulParalyze(p, false);
			p.broadcastCharInfo();
		}
	}
	
	private void cleanup(boolean teleportSingle, List<Player> winners)
	{
		cancel(taskStartBattle);
		cancel(taskPhaseTimeout);
		cancel(taskBroadcast);
		cancel(taskDisconnect);
		
		for (Spawn s : new ArrayList<>(monsterSpawns))
		{
			try
			{
				Npc n = (s != null) ? s.getNpc() : null;
				if (n != null)
					n.deleteMe();
			}
			catch (Exception ignored)
			{
			}
		}
		monsterSpawns.clear();
		
		if (teleportSingle && winners != null)
		{
			try
			{
				teleportBack(winners);
			}
			catch (Exception ignored)
			{
			}
		}
		
		rewardedHwids.clear();
		participants.clear();
		resetTeamStructures();
		
		try
		{
			InstanceManager.getInstance().deleteInstance(instance.getId());
		}
		catch (Exception e)
		{
			LOGGER.log(Level.FINE, "Falha ao deletar instance", e);
		}
		BattleBossOpenRegister.getInstance().removeRumble(rumbleId);
	}
	
	private void safeReturnAndDetach(Player member)
	{
		if (member == null)
			return;
		try
		{
			member.setTeam(TeamType.NONE);
			member.teleportTo(event.getTeleport().getReturnX(), event.getTeleport().getReturnY(), event.getTeleport().getReturnZ(), 75);
			member.setInstanceMap(InstanceManager.getInstance().getInstance(0), true);
			setInvulParalyze(member, false);
			member.broadcastCharInfo();
			member.setBattleBossRumbleId(0);
			member.leaveBattleBoss();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.FINE, "safeReturnAndDetach falhou", e);
		}
		finally
		{
			removePlayer(member);
		}
	}
	
	public void surrenderTeam(Player player)
	{
		Team team = playerTeam.get(player);
		if (team == null)
			return;
		if (Boolean.TRUE.equals(teamOfflineFlag.get(team)))
		{
			World.announceToOnlinePlayers("[BattleBoss] O time de " + player.getName() + " desistiu da batalha.");
			endBattleAndProceed(Collections.emptyList());
		}
	}
	
	private void broadcastTimer()
	{
		if (finished.get() || paused.get())
			return;
		long elapsed = System.currentTimeMillis() - stageBeginTime;
		long remaining = Math.max(0, stageDurationMillis - elapsed);
		int totalSeconds = (int) (remaining / 1000);
		int minutes = totalSeconds / 60;
		int seconds = totalSeconds % 60;
		String label = (phase == Phase.BOSS) ? "Boss" : "Battle";
		String text = label + " " + String.format("%02d:%02d", minutes, seconds);
		ExShowScreenMessage packet = new ExShowScreenMessage(text, 1000, SMPOS.BOTTOM_RIGHT, false);
		for (Player player : snapshotParticipants())
			player.sendPacket(packet);
	}
	
	private void screenMsg(Player player, String msg, int seconds)
	{
		if (player == null)
			return;
		player.sendPacket(new ExShowScreenMessage(msg, seconds * 1000, SMPOS.TOP_CENTER, false));
	}
	
	private void cancel(ScheduledFuture<?> task)
	{
		if (task != null)
		{
			try
			{
				task.cancel(false);
			}
			catch (Exception ignored)
			{
			}
		}
	}
	
	private void reschedulePhaseTimeout(Runnable r, long delayMs)
	{
		cancel(taskPhaseTimeout);
		taskPhaseTimeout = ThreadPool.schedule(r, Math.max(1L, delayMs));
	}
	
	private void rescheduleBroadcast()
	{
		cancel(taskBroadcast);
		taskBroadcast = ThreadPool.scheduleAtFixedRate(this::broadcastTimer, 1000, 1000);
	}
	
	private void applyHealAndRevive(Player p)
	{
		if (p == null)
			return;
		try
		{
			if (p.isFakeDeath())
				p.stopFakeDeath(true);
			p.getStatus().setHp(p.getStatus().getMaxHp());
			p.getStatus().setCp(p.getStatus().getMaxCp());
			p.getStatus().setMp(p.getStatus().getMaxMp());
			p.broadcastCharInfo();
		}
		catch (Exception ignored)
		{
		}
	}
	
	private void setInvulParalyze(Player p, boolean on)
	{
		if (p == null)
			return;
		try
		{
			p.setInvul(on);
			p.setIsImmobilized(on);
		}
		catch (Throwable t)
		{
			LOGGER.log(Level.FINE, "Falha ao setar Invul/Immob", t);
		}
	}
	
	private void startDisconnectionWatcher()
	{
		cancel(taskDisconnect);
		taskDisconnect = ThreadPool.scheduleAtFixedRate(() ->
		{
			for (Player player : snapshotParticipants())
			{
				if (player == null)
					continue;
				boolean invalid = player.isOnlineInt() == 0 || player.getOfflineStartTime() > 0 || player.isTeleporting();
				if (!invalid)
				{
					try
					{
						invalid = player.getInstanceMap().getId() != instance.getId();
					}
					catch (Throwable t)
					{
						invalid = true;
					}
				}
				if (invalid)
					handleDisconnect(player);
			}
		}, 5000L, 5000L);
	}
	
	private void handleDisconnect(Player player)
	{
		if (player == null || !participants.contains(player))
			return;
		Team team = playerTeam.get(player);
		if (team == null)
		{
			removePlayer(player);
			return;
		}
		
		Set<Player> aliveSet = aliveMembersByTeam.get(team);
		if (aliveSet != null)
			aliveSet.remove(player);
		
		if (aliveSet == null || aliveSet.isEmpty())
		{
			aliveTeams.remove(team);
			for (Player member : new ArrayList<>(team.members))
			{
				if (member != null && member.isOnline())
				{
					screenMsg(member, "Seu time foi eliminado (desconexão)!", 10);
					applyHealAndRevive(member);
					teleportBack(Arrays.asList(member));
				}
				removePlayer(member);
			}
		}
		
		if (aliveTeams.size() == 1)
		{
			Team winnerTeam = aliveTeams.iterator().next();
			for (Player member : new ArrayList<>(winnerTeam.members))
				screenMsg(member, "O time inimigo foi eliminado (desconexão)!", 10);
			endBattleAndProceed(new ArrayList<>(winnerTeam.members));
		}
		else if (aliveTeams.isEmpty())
		{
			endBattleAndProceed(Collections.emptyList());
		}
	}
	
	private List<Player> snapshotParticipants()
	{
		return new ArrayList<>(participants);
	}
	
	private List<Player> safeList(List<Player> src)
	{
		return (src == null) ? Collections.emptyList() : new ArrayList<>(src);
	}
}
