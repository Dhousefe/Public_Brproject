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
package ext.mods.BossZerg;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.entity.autofarm.ZoneBuilder;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage;
import ext.mods.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import ext.mods.gameserver.taskmanager.PvpFlagTaskManager;

/**
 * Boss Zerg Manager
 * 
 * Autor: Dhousefe - L2jBr
 * Data: 29/01/2026
 * Versão: 1.2.0
 * Descrição: Gerencia o sistema de zerg de bosses e aplica efeitos de zerg aos jogadores.
 * 
 * Responsável por:
 * - Detectar e gerenciar o zerg de bosses.
 * - Aplicar efeitos de zerg aos jogadores.
 * - Anunciar o zerg de bosses.
 * - Aplicar penalidade de heal aos jogadores em zerg.
 * - Aplicar preview de área de zerg aos jogadores.
 * - Aplicar preview de área de PvP flag aos jogadores.
 * - Aplicar preview de área de zerg aos jogadores.
*/


public final class BossZergManager
{
	private static final CLogger LOGGER = new CLogger(BossZergManager.class.getName());
	
	private final Map<Integer, BossZergState> _bossStates = new ConcurrentHashMap<>();
	
	private BossZergManager()
	{
		start();
	}
	
	private void start()
	{
		if (!Config.BOSS_ZERG_ENABLED)
			return;
		
		final long interval = Math.max(1000L, Config.BOSS_ZERG_CHECK_INTERVAL_MS);
		ThreadPool.scheduleAtFixedRate(this::tick, interval, interval);
		LOGGER.info("BossZerg: Manager iniciado (intervalo: {}ms).", interval);
	}
	
	private void tick()
	{
		if (!Config.BOSS_ZERG_ENABLED)
			return;
		
		final Set<Integer> activeBossIds = new HashSet<>();
		for (WorldObject object : World.getInstance().getObjects())
		{
			if (!(object instanceof Npc boss))
				continue;
			
			if (!boss.isRaidBoss() || boss.isDead())
				continue;
			
			final BossZergState state = _bossStates.computeIfAbsent(boss.getObjectId(), id -> new BossZergState());
			processBoss(boss, state);
			activeBossIds.add(boss.getObjectId());
		}
		
		cleanupRemovedBosses(activeBossIds);
	}
	
	private void processBoss(Npc boss, BossZergState state)
	{
		final boolean ignoreBoss = isBossIgnored(boss);
		if (!ignoreBoss)
			applyPvpFlagAndPreview(boss, state);
		else
			clearFlagPreview(state);
		
		final Map<Integer, PartyPresence> parties = collectPartiesNearBoss(boss, Config.BOSS_ZERG_RANGE);
		final Set<Integer> zergPlayers = new HashSet<>();
		final Set<Integer> activePartyLeaders = new HashSet<>();
		boolean zergActive = false;
		
		for (PartyPresence presence : parties.values())
		{
			if (presence.players.size() < Config.BOSS_ZERG_MIN_PARTY_SIZE)
				continue;
			
			zergActive = true;
			activePartyLeaders.add(presence.party.getLeaderObjectId());
			zergPlayers.addAll(presence.getPlayerIds());
			maybeAnnounceZerg(boss, state, presence);
		}
		
		state.replaceZergPlayers(zergPlayers);
		state.replaceActiveParties(activePartyLeaders);
		if (!ignoreBoss)
			updateZergAreaPreview(boss, state, zergActive);
		else
			clearPreview(state);

		final List<Player> playersInRange = boss.getKnownTypeInRadius(Player.class, Config.BOSS_ZERG_RANGE);

		state.updateEntryTimes(playersInRange);

		final Map<Integer, List<Player>> groupMap = new HashMap<>();
		for (Player player : playersInRange)
		{
			if (player == null || player.getClan() == null)
				continue;
			
			final int groupId = (player.getAllyId() > 0) ? player.getAllyId() : player.getClanId();
			groupMap.computeIfAbsent(groupId, k -> new ArrayList<>()).add(player);
		}

		announceAllyCount(groupMap, ignoreBoss);

		for (Map.Entry<Integer, List<Player>> entry : groupMap.entrySet())
		{
			final List<Player> members = entry.getValue();
			if (members.size() > Config.BOSS_ZERG_MAX_ALLY_MEMBERS)
			{
				members.sort(Comparator.comparingLong(player -> state.playerEntryTimes.getOrDefault(player.getObjectId(), 0L)));

				for (int i = Config.BOSS_ZERG_MAX_ALLY_MEMBERS; i < members.size(); i++)
				{
					final Player excessPlayer = members.get(i);
					teleportExcessPlayer(excessPlayer, boss.getName());
				}
			}
		}
	}

	private void teleportExcessPlayer(Player player, String bossName) {
		if (player == null || !player.isOnline() || player.isTeleporting()) 
			return;
		
		player.sendMessage("Sua Aliança/Clã excedeu o limite de " + Config.BOSS_ZERG_MAX_ALLY_MEMBERS + " membros para o " + bossName + ".");
		player.sendMessage("Você entrou por último e foi removido para manter o equilíbrio.");
		
		final Location restartLocation = RestartPointData.getInstance().getNearestRestartLocation(player);
		if (restartLocation != null)
			player.teleToLocation(restartLocation);
		
		final Clan clan = player.getClan();
		final String allyName = (clan != null && clan.getAllyId() > 0) ? clan.getAllyName() : "N/A";
		LOGGER.info("BossZerg: Player {} (Ally: {}) teleportado do Boss {}. Limite excedido.", 
			player.getName(), allyName, bossName);
	}
	
	private void applyPvpFlagAndPreview(Npc boss, BossZergState state)
	{
		if (!Config.BOSS_ZERG_FLAG_ENABLED)
		{
			clearFlagPreview(state);
			return;
		}
		
		final int range = Math.max(0, Config.BOSS_ZERG_FLAG_RANGE);
		if (range == 0)
		{
			clearFlagPreview(state);
			return;
		}
		
		final long flagTime = Math.max(1000L, Config.BOSS_ZERG_FLAG_RENEW_MS);
		final List<Player> nearbyPlayers = boss.getKnownTypeInRadius(Player.class, range);
		final int minPlayers = Math.max(1, Config.BOSS_ZERG_FLAG_MIN_PLAYERS);
		
		if (Config.BOSS_ZERG_FLAG_AREA_ENABLED)
		{
			final int warningRange = Math.max(0, Config.BOSS_ZERG_FLAG_WARNING_RANGE);
			final int previewRange = warningRange > 0 ? warningRange : range;
			final List<Player> warningPlayers = boss.getKnownTypeInRadius(Player.class, previewRange);
			final Set<Integer> newPreviewPlayers = new HashSet<>();
			
			if (warningPlayers.size() >= minPlayers)
			{
				final Color previewColor = new Color(Config.BOSS_ZERG_FLAG_AREA_COLOR);
				final int previewRadius = Math.max(0, Config.BOSS_ZERG_FLAG_AREA_RADIUS);
				
				for (Player player : warningPlayers)
				{
					if (player == null || !player.isOnline())
						continue;
					
					newPreviewPlayers.add(player.getObjectId());
					ZoneBuilder.getInstance().previewCylinder(player, previewRadius, previewColor, boss.getPosition());
				}
			}
			
			clearFlagPreviewForMissingPlayers(state, newPreviewPlayers);
			state.replaceFlagPreviewPlayers(newPreviewPlayers);
		}
		else
		{
			clearFlagPreview(state);
		}
		
		if (nearbyPlayers.size() >= minPlayers)
		{
			for (Player player : nearbyPlayers)
			{
				if (player == null || !player.isOnline())
					continue;
				
				PvpFlagTaskManager.getInstance().add(player, flagTime);
			}
		}
		
	}

	private void announceAllyCount(Map<Integer, List<Player>> groupMap, boolean ignoreBoss)
	{
		if (ignoreBoss || !Config.BOSS_ZERG_SHOW_ALLY_COUNT || groupMap.isEmpty())
			return;
		
		for (Map.Entry<Integer, List<Player>> entry : groupMap.entrySet())
		{
			final int groupSize = entry.getValue().size();
			final String message = "Ally/Clan no Boss: " + groupSize + "/" + Config.BOSS_ZERG_MAX_ALLY_MEMBERS;
			final ExShowScreenMessage packet = new ExShowScreenMessage(message, 1100, SMPOS.TOP_CENTER, false);
			
			for (Player player : entry.getValue())
			{
				if (player == null || !player.isOnline())
					continue;
				
				player.sendPacket(packet);
			}
		}
	}

	private boolean isBossIgnored(Npc boss)
	{
		return Config.BOSS_ZERG_IGNORED_BOSS_IDS.contains(boss.getNpcId());
	}
	
	private Map<Integer, PartyPresence> collectPartiesNearBoss(Npc boss, int range)
	{
		final Map<Integer, PartyPresence> parties = new HashMap<>();
		final List<Player> nearbyPlayers = boss.getKnownTypeInRadius(Player.class, Math.max(0, range));
		
		for (Player player : nearbyPlayers)
		{
			if (player == null || !player.isOnline())
				continue;
			
			final Party party = player.getParty();
			if (party == null)
				continue;
			
			final int partyId = party.getLeaderObjectId();
			parties.computeIfAbsent(partyId, id -> new PartyPresence(party)).addPlayer(player);
		}
		
		return parties;
	}
	
	private void maybeAnnounceZerg(Npc boss, BossZergState state, PartyPresence presence)
	{
		if (!Config.BOSS_ZERG_ANNOUNCE)
			return;
		
		final long now = System.currentTimeMillis();
		final int partyId = presence.party.getLeaderObjectId();
		final long lastAnnounce = state.partyAnnounceTimes.getOrDefault(partyId, 0L);
		
		if (now - lastAnnounce < Config.BOSS_ZERG_ANNOUNCE_COOLDOWN_MS)
			return;
		
		state.partyAnnounceTimes.put(partyId, now);
		final String clanName = presence.getDominantClanName();
		final String message = (clanName == null || clanName.isEmpty())
			? "Zerg detectada no boss " + boss.getName() + "."
			: "Zerg detectada no boss " + boss.getName() + ". Clã: " + clanName + ".";
		
		World.announceToOnlinePlayers(message, true);

		
	}

	private void updateZergAreaPreview(Npc boss, BossZergState state, boolean zergActive)
	{
		if (!Config.BOSS_ZERG_SHOW_AREA)
		{
			clearPreview(state);
			return;
		}
		
		if (!zergActive)
		{
			clearPreview(state);
			return;
		}
		
		final int radius = Math.max(0, Config.BOSS_ZERG_AREA_RADIUS);
		if (radius == 0)
			return;
		
		final List<Player> nearbyPlayers = boss.getKnownTypeInRadius(Player.class, radius);
		final Set<Integer> newPreviewPlayers = new HashSet<>();
		
		for (Player player : nearbyPlayers)
		{
			if (player == null || !player.isOnline())
				continue;
			
			if (state.isFlagPreviewPlayer(player.getObjectId()))
				continue;
			
			newPreviewPlayers.add(player.getObjectId());
			ZoneBuilder.getInstance().previewCylinder(player, radius, Color.RED, boss.getPosition());
		}
		
		clearPreviewForMissingPlayers(state, newPreviewPlayers);
		state.replacePreviewPlayers(newPreviewPlayers);
	}
	
	private void clearPreview(BossZergState state)
	{
		if (state.previewPlayers.isEmpty())
			return;
		
		for (Integer playerId : state.previewPlayers)
		{
			final Player player = World.getInstance().getPlayer(playerId);
			if (player != null)
				ZoneBuilder.getInstance().clearCylinderPreview(player);
		}
		
		state.previewPlayers.clear();
	}
	
	private void clearFlagPreview(BossZergState state)
	{
		if (state.flagPreviewPlayers.isEmpty())
			return;
		
		for (Integer playerId : state.flagPreviewPlayers)
		{
			final Player player = World.getInstance().getPlayer(playerId);
			if (player != null)
				ZoneBuilder.getInstance().clearCylinderPreview(player);
		}
		
		state.flagPreviewPlayers.clear();
	}
	
	private void clearPreviewForMissingPlayers(BossZergState state, Set<Integer> newPreviewPlayers)
	{
		if (state.previewPlayers.isEmpty())
			return;
		
		for (Integer playerId : state.previewPlayers)
		{
			if (newPreviewPlayers.contains(playerId))
				continue;
			
			final Player player = World.getInstance().getPlayer(playerId);
			if (player != null)
				ZoneBuilder.getInstance().clearCylinderPreview(player);
		}
	}
	
	private void clearFlagPreviewForMissingPlayers(BossZergState state, Set<Integer> newPreviewPlayers)
	{
		if (state.flagPreviewPlayers.isEmpty())
			return;
		
		for (Integer playerId : state.flagPreviewPlayers)
		{
			if (newPreviewPlayers.contains(playerId))
				continue;
			
			final Player player = World.getInstance().getPlayer(playerId);
			if (player != null)
				ZoneBuilder.getInstance().clearCylinderPreview(player);
		}
	}
	
	private void cleanupRemovedBosses(Set<Integer> activeBossIds)
	{
		if (_bossStates.isEmpty())
			return;
		
		final List<Integer> toRemove = new ArrayList<>();
		for (Map.Entry<Integer, BossZergState> entry : _bossStates.entrySet())
		{
			if (activeBossIds.contains(entry.getKey()))
				continue;
			
			clearPreview(entry.getValue());
			clearFlagPreview(entry.getValue());
			toRemove.add(entry.getKey());
		}
		
		for (Integer bossId : toRemove)
			_bossStates.remove(bossId);
	}
	
	public boolean isPlayerInZerg(Player player)
	{
		if (player == null)
			return false;
		
		final int playerId = player.getObjectId();
		for (BossZergState state : _bossStates.values())
		{
			if (state.isZergPlayer(playerId))
				return true;
		}
		
		return false;
	}
	
	public double applyHealPenalty(Creature effector, Creature effected, double amount)
	{
		if (!Config.BOSS_ZERG_HEAL_PENALTY_ENABLED || amount <= 0)
			return amount;
		
		if (!(effected instanceof Player targetPlayer))
			return amount;
		
		if (!isPlayerInZerg(targetPlayer))
			return amount;
		
		double result = amount * Config.BOSS_ZERG_HEAL_PENALTY_MULTIPLIER;
		
		if (hasPrayerEffect(targetPlayer))
			result *= Config.BOSS_ZERG_PRAYER_REVERSE_MULTIPLIER;
		
		if (effector instanceof Player casterPlayer && hasPrayerEffect(casterPlayer))
			result *= Config.BOSS_ZERG_PRAYER_REVERSE_MULTIPLIER;
		
		return Math.max(1.0, result);
	}
	
	private boolean hasPrayerEffect(Player player)
	{
		if (player == null || Config.BOSS_ZERG_PRAYER_SKILL_IDS.length == 0)
			return false;
		
		for (int skillId : Config.BOSS_ZERG_PRAYER_SKILL_IDS)
		{
			if (player.getFirstEffect(skillId) != null)
				return true;
		}
		
		return false;
	}
	
	public static BossZergManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class BossZergState
	{
		private final Map<Integer, Long> partyAnnounceTimes = new ConcurrentHashMap<>();
		private final Map<Integer, Long> playerEntryTimes = new ConcurrentHashMap<>();
		private final Set<Integer> zergPlayers = ConcurrentHashMap.newKeySet();
		private final Set<Integer> previewPlayers = ConcurrentHashMap.newKeySet();
		private final Set<Integer> flagPreviewPlayers = ConcurrentHashMap.newKeySet();
		private final Set<Integer> activePartyLeaders = ConcurrentHashMap.newKeySet();
		
		private BossZergState()
		{
		}
		
		private void replaceZergPlayers(Set<Integer> playerIds)
		{
			zergPlayers.clear();
			zergPlayers.addAll(playerIds);
		}
		
		private void replacePreviewPlayers(Set<Integer> playerIds)
		{
			previewPlayers.clear();
			previewPlayers.addAll(playerIds);
		}
		
		private void replaceFlagPreviewPlayers(Set<Integer> playerIds)
		{
			flagPreviewPlayers.clear();
			flagPreviewPlayers.addAll(playerIds);
		}
		
		private void replaceActiveParties(Set<Integer> partyLeaderIds)
		{
			activePartyLeaders.clear();
			activePartyLeaders.addAll(partyLeaderIds);
		}

		private void updateEntryTimes(List<Player> playersInRange)
		{
			final long now = System.currentTimeMillis();
			
			final Set<Integer> currentIds = new HashSet<>();
			for (Player player : playersInRange)
				currentIds.add(player.getObjectId());
			playerEntryTimes.keySet().retainAll(currentIds);
			
			for (Player player : playersInRange)
				playerEntryTimes.putIfAbsent(player.getObjectId(), now);
		}
		
		private boolean isZergPlayer(int playerId)
		{
			return zergPlayers.contains(playerId);
		}
		
		private boolean isFlagPreviewPlayer(int playerId)
		{
			return flagPreviewPlayers.contains(playerId);
		}
	}
	
	private static class PartyPresence
	{
		private final Party party;
		private final List<Player> players = new ArrayList<>();
		private final Map<Integer, Integer> clanCounts = new HashMap<>();
		private final Map<Integer, String> clanNames = new HashMap<>();
		private int noClanCount;
		
		private PartyPresence(Party party)
		{
			this.party = party;
		}
		
		private void addPlayer(Player player)
		{
			players.add(player);
			final Clan clan = player.getClan();
			if (clan == null)
			{
				noClanCount++;
				return;
			}
			
			final int clanId = clan.getClanId();
			clanCounts.merge(clanId, 1, Integer::sum);
			clanNames.putIfAbsent(clanId, clan.getName());
		}
		
		private String getDominantClanName()
		{
			if (clanCounts.isEmpty())
				return null;
			
			if (clanCounts.size() == 1 && noClanCount > 0)
			{
				final Integer clanId = clanCounts.keySet().iterator().next();
				return clanNames.getOrDefault(clanId, "");
			}
			
			return clanCounts.entrySet().stream()
				.filter(entry -> entry.getValue() >= 2)
				.max(Comparator.comparingInt(Map.Entry<Integer, Integer>::getValue)
					.thenComparing(entry -> clanNames.getOrDefault(entry.getKey(), "")))
				.map(entry -> clanNames.getOrDefault(entry.getKey(), ""))
				.orElse(null);
		}
		
		private Set<Integer> getPlayerIds()
		{
			final Set<Integer> ids = new HashSet<>();
			for (Player player : players)
				ids.add(player.getObjectId());
			return ids;
		}
	}
	
	private static class SingletonHolder
	{
		private static final BossZergManager INSTANCE = new BossZergManager();
	}
}
