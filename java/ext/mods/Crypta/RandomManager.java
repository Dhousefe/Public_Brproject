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
package ext.mods.Crypta;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.model.spawn.Spawn;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.zone.type.RandomZone;

import ext.mods.FarmEventRandom.holder.DropHolder;
import ext.mods.FarmEventRandom.holder.MessagesHolder;
import ext.mods.FarmEventRandom.holder.RamdomConfig;
import ext.mods.FarmEventRandom.holder.RandomSpawnHolder;
import ext.mods.FarmEventRandom.holder.RandomZoneData;
import ext.mods.FarmEventRandom.RandomData;


import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.ConfirmDlg;
import ext.mods.gameserver.network.serverpackets.TutorialShowQuestionMark;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.SetupGauge;
import ext.mods.gameserver.enums.GaugeColor;
import ext.mods.gameserver.enums.SpawnType;


public class RandomManager
{
	public static final CLogger LOGGER = new CLogger(RandomManager.class.getName());

	private static final List<RandomZone> activeZones = Collections.synchronizedList(new ArrayList<>());
	private static final List<Spawn> activeSpawns = Collections.synchronizedList(new ArrayList<>());
	private static final List<Npc> oldMonsters = Collections.synchronizedList(new ArrayList<>());
	private static ScheduledFuture<?> eventChecker;
	private static ScheduledFuture<?> screenMessageTask;
	private static ScheduledFuture<?> zoneSwitchTask;
	private static ScheduledFuture<?> zoneCleanerTask;
	private static ScheduledFuture<?> _partyCheckTask;
	private static ScheduledFuture<?> _enchanterZonePvpFlagTask;
	private static ScheduledFuture<?> questionMarkRefresher;
	private static boolean isRunning;
	/** ID do ícone de tutorial (quest mark) para o Random Event. */
	public static final int TUTORIAL_QUESTION_MARK_ID = 2005;
	private static String lastEventTime;
	private static long timeRemaining;
	private static RamdomConfig config;
	private static MessagesHolder messages;
	private static final int COUNTDOWN_INTERVAL = 1000;
	private static final int PARTY_CHECK_INTERVAL = 30000;
	
	/**
	 * Define um tempo de respawn muito longo (em segundos) para desativar os spawns originais
	 * (Duração do Evento + 10 minutos)
	 */
	private static int getEventPauseDelay()
	{
		if (config == null) return 3600 + 600;
		return (config.getInterval() * 3600) + 600; 
	}

	public void reload()
	{
		LOGGER.info("RandomEvent: Iniciando reload...");

		if (isRunning)
		{
			stopZones();
			isRunning = false;
		}

		if (eventChecker != null)
		{
			eventChecker.cancel(true);
			eventChecker = null;
		}

		RandomData.getInstance().reload();

		start();
		LOGGER.info("RandomEvent: Reload completo.");
	}

	public void start()
	{
		config = RandomData.getInstance().getFirstConfig();
		messages = (config != null) ? RandomData.getInstance().getMessages(config.getName()) : null;

		if (config == null || messages == null)
		{
			LOGGER.error("RandomEvent: Nenhum evento encontrado ou configurado. Verifique random_event.xml. Abortando.");
			return;
		}

		if (eventChecker == null || eventChecker.isCancelled())
		{
			eventChecker = ThreadPool.scheduleAtFixedRate(this::checkStartEvent, 1000, 1000);
			LOGGER.info("RandomEvent: Event checker started. Evento: " + config.getName());
		}
	}

	private void checkStartEvent()
	{
		if (config == null || !config.isEnabled() || isRunning)
			return;

		if (!config.isTimeToRun())
			return;

		String currentTime = LocalTime.now().withSecond(0).withNano(0).toString();
		if (currentTime.equals(lastEventTime))
			return;

		LOGGER.info("RandomEvent: Event STARTING. Time matched: " + currentTime);

		lastEventTime = currentTime;
		isRunning = true;
		sendQuestionMarkToAllOnline();
		questionMarkRefresher = ThreadPool.scheduleAtFixedRate(RandomManager::sendQuestionMarkToAllOnline, 60_000, 60_000);

		Map<String, String> data = new HashMap<>();

		if (config.getPrepareMinutes() < 1)
			data.put("time", config.getPrepareMinutes() * 60 + " segundos");
		else
			data.put("time", config.getPrepareMinutes() + " minutos");

		for (String msg : messages.getOnPrepare())
		{
			String formatted = formatMessage(msg, data);
			World.announceToOnlinePlayers(config.getName() + ": " + formatted, true);
		}
		
		ThreadPool.schedule(this::activateZonesAndSpawn, config.getPrepareMinutes() * 60L * 1000L);
	}
	
	private void startZoneCleaner()
	{
		if (zoneCleanerTask != null && !zoneCleanerTask.isDone())
		{
			zoneCleanerTask.cancel(true);
		}
		
		final int pauseDelay = getEventPauseDelay();

		zoneCleanerTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (!isRunning) return;
			
			for (RandomZone zone : activeZones)
			{
				for (Attackable npc : zone.getKnownTypeInside(Attackable.class))
				{
					if (npc == null || npc.isDead() || npc.isDecayed())
						continue;

					boolean isEventMonster = activeSpawns.stream()
						.anyMatch(spawn -> spawn != null && npc.getSpawn() == spawn);

					if (!isEventMonster)
					{
						if (Config.DEVELOPER)
						{
							LOGGER.info("[Debug Cleaner] Removing non-event NPC: " + npc.getName() + " (ID: " + npc.getNpcId() + ")");
						}
						
						if (npc.getSpawn() != null) {
							npc.getSpawn().setRespawnDelay(pauseDelay);
						}
						npc.deleteMe();
					}
				}
			}
		}, 1000, 5000);
	}

	private void activateZonesAndSpawn()
	{
		if (Config.DEVELOPER) { LOGGER.info("[Debug Event] activateZonesAndSpawn: Activating zones..."); }

		List<RandomZone> allZones = new ArrayList<>(ZoneManager.getInstance().getAllZones(RandomZone.class));
		if (allZones.isEmpty()) { LOGGER.warn("{} Nenhuma zona do tipo RandomZone encontrada.", config.getName()); isRunning = false; return; }

		List<RandomZone> configuredZones = allZones.stream()
			.filter(zone -> RandomData.getInstance().getZoneData(config.getName(), zone.getId()) != null)
			.collect(Collectors.toList());
		if (configuredZones.isEmpty()) { LOGGER.warn("{} Nenhuma das RandomZones encontradas tem configuração no random_event.xml.", config.getName()); isRunning = false; return; }

		List<RandomZone> eligibleZones = configuredZones.stream()
			.filter(zone -> RandomData.getInstance().getZoneData(config.getName(), zone.getId()).isActive())
			.collect(Collectors.toList());
		if (eligibleZones.isEmpty()) {
			LOGGER.warn("{} Nenhuma zona configurada está marcada como 'active=true'. Nenhuma zona será ativada.", config.getName());
			isRunning = false;
			return;
		}
		
		if (Config.DEVELOPER) {
			LOGGER.info("[Debug Event] Zonas elegíveis (active=true) encontradas: " + eligibleZones.size() + ". Sorteando " + config.getZoneValue() + " delas.");
		}

		Collections.shuffle(eligibleZones);
		
		activeZones.clear();
		boolean hasPartyZone = false;
		boolean hasEnchanterZone = false;

		for (int i = 0; i < Math.min(config.getZoneValue(), eligibleZones.size()); i++)
		{
			RandomZone zone = eligibleZones.get(i); 
			zone.setActive(true);
			activeZones.add(zone); 
			
			RandomZoneData zData = RandomData.getInstance().getZoneData(config.getName(), zone.getId());
			if (zData != null && zData.isPartyZone()) {
				hasPartyZone = true;
			}
			if (zData != null && zData.isEnchanterZone()) {
				hasEnchanterZone = true;
			}
			
			LOGGER.info("RandomEvent: Zone Activated: " + zone.getName() + " (ID: " + zone.getId() + ")");
			
			if (Config.DEVELOPER) {
				if (zData != null) { LOGGER.info("[Debug Event] Using ZoneData: zoneId=" + zData.getZoneId() + ", isVip=" + zData.isVip() + ", isPartyZone=" + zData.isPartyZone() + ", dwarvenOnly=" + zData.isDwarvenOnly() + ", enchanterZone=" + zData.isEnchanterZone() + ", enchantChance=" + zData.getEnchantChance() + "%, maxEnchant=" + zData.getMaxEnchant()); }
				else { LOGGER.error("[Debug Event] CRITICAL: ZoneData is null even after filtering! Zone ID: " + zone.getId()); }
			}
			for (String msg : messages.getOnZone()) { 
				String formatted = msg.replace("%zone%", zone.getName());
				if (zData != null && zData.isDwarvenOnly()) {
					formatted += " [DWARVEN Only]";
				}
				if (zData != null && zData.isEnchanterZone()) {
					formatted += " [Enchanter Zone - PvP Ativo - Chance: " + zData.getEnchantChance() + "% - Max: +" + zData.getMaxEnchant() + "]";
				}
				World.announceToOnlinePlayers(config.getName() + ": " + formatted, true); 
			}
		}

		if (activeZones.isEmpty()) { LOGGER.warn("RandomEvent: No zones were activated after selection."); isRunning = false; return; }

		cleanPreviousMonsters(); 
		spawnEventMonsters();
		startZoneCleaner(); 
		startCountdown();
		scheduleZoneSwitch();
		
		if (hasPartyZone) {
			startPartyCheckTask();
		}
		
		if (hasEnchanterZone) {
			startEnchanterZonePvpFlagTask();
		}
		
		sendTeleportConfirmationToAllPlayers();
	}

	private void spawnEventMonsters()
	{
		if (Config.DEVELOPER) { LOGGER.info("[Debug Spawn] spawnEventMonsters: Starting..."); }

		final int pauseDelay = getEventPauseDelay();
		
		if (Config.DEVELOPER) { LOGGER.info("[Debug Spawn] Pausing " + oldMonsters.size() + " old monsters from active zones."); }
		for (Npc npc : oldMonsters) {
			if (npc != null && !npc.isDead() && !npc.isDecayed()) {
				if (npc.getSpawn() != null) { 
					npc.getSpawn().setRespawnDelay(pauseDelay); 
				}
				npc.deleteMe();
			}
		}

		activeSpawns.clear();

		for (RandomZone zone : activeZones) {
			RandomZoneData zoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId());
			if (zoneData == null) {
				LOGGER.warn("RandomEvent: No <spawns> data found for zoneId=" + zone.getId());
				continue;
			}
			if (Config.DEVELOPER) { LOGGER.info("[Debug Spawn] Spawning for Zone ID " + zone.getId() + " (useOriginals=" + zoneData.useOriginals() + ")"); }

			if (zoneData.useOriginals()) {
				for (Npc oldNpc : oldMonsters) {
					if (oldNpc != null && oldNpc.getSpawn() != null) {
						SpawnLocation loc = oldNpc.getSpawn().getSpawnLocation();
						if (loc == null) continue;
						
						if (zone.isInsideZone(loc.getX(), loc.getY(), loc.getZ())) {
							ASpawn oldSpawn = oldNpc.getSpawn();
							String title = zoneData.getDefaultTitle();
							int respawnDelay = zoneData.getDefaultRespawnDelay();
							if (respawnDelay == -1) { respawnDelay = oldSpawn.getRespawnDelay(); } 
							
							if (Config.DEVELOPER) { LOGGER.info("[Debug Spawn] MODE 1/3: Re-spawning original NPC " + oldNpc.getNpcId() + " with default title: " + title + " and respawn: " + respawnDelay); }
							try {
								Spawn newEventSpawn = new Spawn(oldSpawn.getTemplate());
								newEventSpawn.setLoc(loc.getX(), loc.getY(), loc.getZ(), loc.getHeading());
								newEventSpawn.setRespawnDelay(respawnDelay); 
								SpawnManager.getInstance().addSpawn(newEventSpawn);
								newEventSpawn.doSpawn(false);
								Npc newNpc = newEventSpawn.getNpc();
								if (newNpc != null) {
									newNpc.setTitle(title);
									newNpc.updateAbnormalEffect();
								}
								activeSpawns.add(newEventSpawn);
							} catch (Exception e) { LOGGER.warn("RandomEvent: Erro ao re-spawnar monstro original: {}", e.getMessage(), e); }
						}
					}
				}
			}

			
			for (RandomSpawnHolder holder : zoneData.getCustomSpawns()) {
				
				if (holder.getCount() <= 0) { continue; }
				if (holder.getX() == 0 && holder.getY() == 0 && holder.getZ() == 0) { LOGGER.error("RandomEvent: Coordenadas (0,0,0) não são suportadas. NPC {}.", holder.getNpcId()); continue; }
				if (!zone.isInsideZone(holder.getX(), holder.getY(), holder.getZ())) { LOGGER.warn("RandomEvent: Coordenadas ({},{},{}) NPC {} FORA da zona {}.", holder.getX(), holder.getY(), holder.getZ(), holder.getNpcId(), zone.getName()); continue; }
				Location centerPoint = new Location(holder.getX(), holder.getY(), holder.getZ());
				if (Config.DEVELOPER) { LOGGER.info("[Debug Spawn] MODE 2/3: Spawning custom NPC " + holder.getNpcId() + " Count: " + holder.getCount()); }
				for (int i = 0; i < holder.getCount(); i++) {
					try {
						NpcTemplate template = NpcData.getInstance().getTemplate(holder.getNpcId()); if (template == null) continue;
						int finalX, finalY, finalZ; int heading = Rnd.get(65535);
						finalX = centerPoint.getX(); finalY = centerPoint.getY(); finalZ = centerPoint.getZ();
						if (holder.getRange() > 0) { int radius = holder.getRange(); double angle = Rnd.nextDouble() * 2 * Math.PI; finalX += (int) (Math.cos(angle) * Rnd.get(0, radius)); finalY += (int) (Math.sin(angle) * Rnd.get(0, radius)); }
						finalZ = GeoEngine.getInstance().getHeight(finalX, finalY, finalZ);
						Spawn spawn = new Spawn(template); spawn.setLoc(finalX, finalY, finalZ, heading); int respawnDelay = (holder.getRespawnDelay() != -1) ? holder.getRespawnDelay() : 60; spawn.setRespawnDelay(respawnDelay); SpawnManager.getInstance().addSpawn(spawn); spawn.doSpawn(false);
						if (spawn.getNpc() != null && holder.getTitle() != null && !holder.getTitle().isEmpty()) { spawn.getNpc().setTitle(holder.getTitle()); spawn.getNpc().updateAbnormalEffect(); }
						activeSpawns.add(spawn);
					} catch (Exception e) { LOGGER.error("RandomEvent: Exception spawning custom NPC ID {}: {}", holder.getNpcId(), e.getMessage(), e); }
				}
			}
		} 

		
		int intervalHours = config.getInterval();
		String durationText = intervalHours + (intervalHours == 1 ? " hora" : " hora(s)");
		Map<String, String> data = new HashMap<>();
		data.put("time", durationText);
		for (String msg : messages.getOnStart()) {
			String formatted = formatMessage(msg, data);
			World.announceToOnlinePlayers(config.getName() + ": " + formatted, true);
		}
	} 

	
	private void startCountdown() {  
		timeRemaining = config.getInterval() * 60L * 60L * 1000L;
		final int announceIntervalMinutes = config.getAnnounceEndMinutes();
		final long announceIntervalMs = announceIntervalMinutes * 60L * 1000L;
		final boolean enableTimedAnnouncements = (announceIntervalMinutes > 0);
		if (screenMessageTask != null && !screenMessageTask.isCancelled()) screenMessageTask.cancel(true);
		screenMessageTask = ThreadPool.scheduleAtFixedRate(() -> {
			if (timeRemaining <= 0) { if (screenMessageTask != null) screenMessageTask.cancel(true); return; }
			if (enableTimedAnnouncements && (timeRemaining % announceIntervalMs == 0 || (timeRemaining <= 60000 && timeRemaining % 10000 == 0))) {
				long seconds = timeRemaining / 1000 % 60; long minutes = timeRemaining / (1000 * 60) % 60; long hours = timeRemaining / (1000 * 60 * 60);
				if (messages != null && !messages.getOnAuto().isEmpty()) { String template = messages.getOnAuto().get(0); String message = template.replace("%dh", hours + "h").replace("%dm", minutes + "m").replace("%ds", seconds + "s"); World.announceToOnlinePlayers(config.getName() + ": " + message, true); }
			}
			timeRemaining -= COUNTDOWN_INTERVAL;
		}, 1000, 1000);
	}
	private void scheduleZoneSwitch() {  
		if (zoneSwitchTask != null && !zoneSwitchTask.isCancelled()) zoneSwitchTask.cancel(true);
		zoneSwitchTask = ThreadPool.schedule(() -> { stopZones(); isRunning = false; }, config.getInterval() * 60L * 60L * 1000L);
	}
	
	
	private void cleanPreviousMonsters() {  
		oldMonsters.clear();
		for (RandomZone zone : activeZones) { for (Npc npc : zone.getKnownTypeInside(Npc.class)) { if (npc != null && !npc.isDead()) oldMonsters.add(npc); } }
		if (Config.DEVELOPER) { LOGGER.info("[Debug Event] cleanPreviousMonsters: Saved " + oldMonsters.size() + " old NPCs from active zones."); }
	}

	private void stopZones() {
		if (Config.DEVELOPER) { LOGGER.info("[Debug Event] Event STOPPING. Cleaning up zones."); }
		if (questionMarkRefresher != null) {
			questionMarkRefresher.cancel(false);
			questionMarkRefresher = null;
		}
		if (messages != null && config != null) {
			Map<String, String> data = new HashMap<>();
			data.put("time", String.valueOf(Math.round(config.getInterval() / 60.0)));
			data.put("next_time", getNextEventTimeFormatted());
			for (String msg : messages.getOnEnd()) {
				String formatted = formatMessage(msg, data);
				World.announceToOnlinePlayers(config.getName() + ": " + formatted, true);
			}
		}

		
		if (zoneCleanerTask != null && !zoneCleanerTask.isDone()) zoneCleanerTask.cancel(true);
		if (screenMessageTask != null && !screenMessageTask.isDone()) screenMessageTask.cancel(true);
		if (zoneSwitchTask != null && !zoneSwitchTask.isDone()) zoneSwitchTask.cancel(true);
		if (_partyCheckTask != null && !_partyCheckTask.isDone()) _partyCheckTask.cancel(true);
		if (_enchanterZonePvpFlagTask != null && !_enchanterZonePvpFlagTask.isDone()) _enchanterZonePvpFlagTask.cancel(true);
		

		for (RandomZone zone : activeZones) { zone.setActive(false); }
		activeZones.clear();

		
		if (Config.DEVELOPER) { LOGGER.info("[Debug Event] Deleting " + activeSpawns.size() + " event spawns."); }
		for (Spawn spawn : activeSpawns) { if (spawn != null) { spawn.setRespawnDelay(0); if (spawn.getNpc() != null) spawn.getNpc().deleteMe(); SpawnManager.getInstance().deleteSpawn(spawn); } }
		activeSpawns.clear();
		if (Config.DEVELOPER) { LOGGER.info("[Debug Event] Re-spawning " + oldMonsters.size() + " old monsters."); }
		for (Npc npc : oldMonsters) { if (npc != null && npc.getSpawn() != null) { try { ASpawn oldSpawn = npc.getSpawn(); int originalRespawn = oldSpawn.getRespawnDelay(); if (originalRespawn <= 0 || originalRespawn >= (getEventPauseDelay() - 10)) { originalRespawn = 60; } oldSpawn.setRespawnDelay(originalRespawn); oldSpawn.doSpawn(false); } catch (Exception e) { LOGGER.warn("RandomEvent: Erro ao re-spawnar NPC antigo: {}", e.getMessage(), e); } } }
		oldMonsters.clear();
		
		isRunning = false; 
	}

	
	public void onKill(Player player, Attackable monster) {  
		if (player == null || monster == null) return; if (Config.DEVELOPER) { LOGGER.info("[Debug Kill] " + player.getName() + " killed " + monster.getName()); }
		boolean isEventMonster = activeSpawns.stream().anyMatch(spawn -> spawn != null && monster.getSpawn() == spawn); 
		if (isEventMonster && isRunning && timeRemaining > 0) {
			if (Config.DEVELOPER) { LOGGER.info("[Debug Kill] Monster " + monster.getNpcId() + " is an event monster. Checking drops..."); }
			RandomZoneData zoneData = null;
			for (RandomZone zone : activeZones) { if (zone.isInsideZone(monster)) { if (config != null) { zoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId()); } break; } }
			if (zoneData == null) { if (Config.DEVELOPER) { LOGGER.warn("[Debug Kill] No ZoneData found for the zone monster is in!"); } return; }
			RandomSpawnHolder customSpawnData = zoneData.getCustomSpawns().stream().filter(s -> s.getCount() > 0 && activeSpawns.stream().anyMatch(as -> as == monster.getSpawn() && as.getTemplate().getNpcId() == s.getNpcId())).findFirst().orElse(null);
			List<DropHolder> dropsToUse = null;
			if (customSpawnData != null && !customSpawnData.getDrops().isEmpty()) { if (Config.DEVELOPER) { LOGGER.info("[Debug Kill] Monster is CUSTOM spawn. Using custom drops."); } dropsToUse = customSpawnData.getDrops(); }
			else if (zoneData.useOriginals() && zoneData.dropsOriginals()) { if (Config.DEVELOPER) { LOGGER.info("[Debug Kill] Monster is ORIGINAL spawn and DropsOriginals=true. Using default zone drops."); } dropsToUse = zoneData.getDefaultDrops(); }
			if (dropsToUse != null && !dropsToUse.isEmpty()) {
				for (DropHolder drop : dropsToUse) {
					if (Rnd.get(100) < drop.getChance()) {
						if (Config.DEVELOPER) { LOGGER.info("[Debug Kill] SUCCESS! Dropping item " + drop.getItemId() + " (Chance: " + drop.getChance() + ")"); }
						int totalAmount = drop.getCount();
						if (player.isInParty()) {
							List<Player> members = player.getParty().getMembers(); int size = members.size(); if (size == 0) { player.addItem(drop.getItemId(), totalAmount, true); continue; } int baseAmount = totalAmount / size; int remainder = totalAmount % size;
							for (Player member : members) { if (member != null && member.isOnline() && player.isIn3DRadius(member, Config.PARTY_RANGE)) { int amount = baseAmount; if (remainder > 0) { amount++; remainder--; } if (amount > 0) member.addItem(drop.getItemId(), amount, true); } else if (remainder > 0) { remainder--; } }
							if (remainder > 0) { player.addItem(drop.getItemId(), remainder, true); }
						} else { player.addItem(drop.getItemId(), totalAmount, true); }
					} else if (Config.DEVELOPER) { LOGGER.info("[Debug Kill] FAILED drop for item " + drop.getItemId() + " (Chance: " + drop.getChance() + ")"); }
				}
			} else if (customSpawnData == null && zoneData.useOriginals() && !zoneData.dropsOriginals()) { if (Config.DEVELOPER) { LOGGER.info("[Debug Kill] Monster is ORIGINAL spawn and DropsOriginals=false. No event drops added."); } }
			else if (Config.DEVELOPER){ LOGGER.warn("[Debug Kill] No drops configured or applicable for this monster!"); }
		}
	}
	public boolean shouldCancelOriginalDrop(Attackable monster) {  
		if (!isRunning || monster == null || config == null) return false; boolean isEventMonster = activeSpawns.stream().anyMatch(spawn -> spawn != null && monster.getSpawn() == spawn); if (!isEventMonster) return false; RandomZoneData zoneData = null; for (RandomZone zone : activeZones) { if (zone.isInsideZone(monster)) { zoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId()); break; } } if (zoneData == null) return false; RandomSpawnHolder customSpawnData = zoneData.getCustomSpawns().stream().filter(s -> s.getCount() > 0 && activeSpawns.stream().anyMatch(as -> as == monster.getSpawn() && as.getTemplate().getNpcId() == s.getNpcId())).findFirst().orElse(null); if (customSpawnData != null) return true; return zoneData.useOriginals() && !zoneData.dropsOriginals();
	}
	public double getActiveRateXp(Attackable monster) {  
		if (!isRunning || monster == null || config == null) return 1.0; boolean isEventMonster = activeSpawns.stream().anyMatch(spawn -> spawn != null && monster.getSpawn() == spawn); if (!isEventMonster) return 1.0; for (RandomZone zone : activeZones) { if (zone.isInsideZone(monster)) { RandomZoneData zoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId()); if (zoneData == null) return 1.0; RandomSpawnHolder customSpawnData = zoneData.getCustomSpawns().stream().filter(s -> s.getCount() > 0 && activeSpawns.stream().anyMatch(as -> as == monster.getSpawn() && as.getTemplate().getNpcId() == s.getNpcId())).findFirst().orElse(null); if (customSpawnData != null && customSpawnData.getRateXp() > -1.0) { if(Config.DEVELOPER) LOGGER.info("[Debug Rate] Usando rateXP específico do Spawn {} para NPC {}", customSpawnData.getRateXp(), monster.getNpcId()); return customSpawnData.getRateXp(); } if(Config.DEVELOPER) LOGGER.info("[Debug Rate] Usando rateXP padrão da Zona {} ({}) para NPC {}", zoneData.getRateXp(), zone.getName(), monster.getNpcId()); return zoneData.getRateXp(); } } return 1.0;
	}
	public double getActiveRateSp(Attackable monster) {  
		if (!isRunning || monster == null || config == null) return 1.0; boolean isEventMonster = activeSpawns.stream().anyMatch(spawn -> spawn != null && monster.getSpawn() == spawn); if (!isEventMonster) return 1.0; for (RandomZone zone : activeZones) { if (zone.isInsideZone(monster)) { RandomZoneData zoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId()); if (zoneData == null) return 1.0; RandomSpawnHolder customSpawnData = zoneData.getCustomSpawns().stream().filter(s -> s.getCount() > 0 && activeSpawns.stream().anyMatch(as -> as == monster.getSpawn() && as.getTemplate().getNpcId() == s.getNpcId())).findFirst().orElse(null); if (customSpawnData != null && customSpawnData.getRateSp() > -1.0) { if(Config.DEVELOPER) LOGGER.info("[Debug Rate] Usando rateSP específico do Spawn {} para NPC {}", customSpawnData.getRateSp(), monster.getNpcId()); return customSpawnData.getRateSp(); } if(Config.DEVELOPER) LOGGER.info("[Debug Rate] Usando rateSP padrão da Zona {} ({}) para NPC {}", zoneData.getRateSp(), zone.getName(), monster.getNpcId()); return zoneData.getRateSp(); } } return 1.0;
	}
	public double getActiveRateAdena(Attackable monster) {  
		if (!isRunning || monster == null || config == null) return 1.0; boolean isEventMonster = activeSpawns.stream().anyMatch(spawn -> spawn != null && monster.getSpawn() == spawn); if (!isEventMonster) return 1.0; for (RandomZone zone : activeZones) { if (zone.isInsideZone(monster)) { RandomZoneData zoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId()); if (zoneData == null) return 1.0; RandomSpawnHolder customSpawnData = zoneData.getCustomSpawns().stream().filter(s -> s.getCount() > 0 && activeSpawns.stream().anyMatch(as -> as == monster.getSpawn() && as.getTemplate().getNpcId() == s.getNpcId())).findFirst().orElse(null); if (customSpawnData != null && customSpawnData.getRateAdena() > -1.0) { if(Config.DEVELOPER) LOGGER.info("[Debug Rate] Usando rateAdena específico do Spawn {} para NPC {}", customSpawnData.getRateAdena(), monster.getNpcId()); return customSpawnData.getRateAdena(); } if(Config.DEVELOPER) LOGGER.info("[Debug Rate] Usando rateAdena padrão da Zona {} ({}) para NPC {}", zoneData.getRateAdena(), zone.getName(), monster.getNpcId()); return zoneData.getRateAdena(); } } return 1.0;
	}
	public boolean isEventMonster(Attackable monster) {  
		if (!isRunning || monster == null) { return false; } return activeSpawns.stream().anyMatch(spawn -> spawn != null && monster.getSpawn() == spawn);
	}
	
	/**
	 * Retorna os dados da zona para uma RandomZone específica.
	 * @param zone A zona para buscar dados
	 * @return RandomZoneData ou null se não encontrado
	 */
	public RandomZoneData getZoneDataForZone(RandomZone zone)
	{
		if (!isRunning || config == null || zone == null)
			return null;
		
		for (RandomZone activeZone : activeZones)
		{
			if (activeZone.getId() == zone.getId())
			{
				return RandomData.getInstance().getZoneData(config.getName(), zone.getId());
			}
		}
		
		return null;
	}
	
	/**
	 * Retorna uma cópia da lista de zonas ativas do evento.
	 * @return Lista de RandomZone ativas ou lista vazia se não houver evento ativo
	 */
	public List<RandomZone> getActiveZones()
	{
		if (!isRunning || activeZones.isEmpty())
			return new ArrayList<>();
		
		return new ArrayList<>(activeZones);
	}
	
	/**
	 * Verifica se há um evento ativo.
	 * @return true se há evento ativo
	 */
	public boolean isEventRunning()
	{
		return isRunning;
	}
	
	/**
	 * Retorna uma cópia da lista de spawns ativos do evento.
	 * @return Lista de Spawn ativos ou lista vazia se não houver evento ativo
	 */
	public List<Spawn> getActiveSpawns()
	{
		if (!isRunning || activeSpawns.isEmpty())
			return new ArrayList<>();
		
		return new ArrayList<>(activeSpawns);
	}
	
	/**
	 * Manipula kills PvP em Enchanter_Zone e aplica encantamento automático.
	 * @param killer O player que matou
	 * @param victim O player que foi morto
	 */
	public void onPvPKill(Player killer, Player victim)
	{
		if (!isRunning || killer == null || victim == null || config == null)
			return;
		
		RandomZoneData killerZoneData = null;
		RandomZoneData victimZoneData = null;
		RandomZone sameZone = null;
		
		for (RandomZone zone : activeZones)
		{
			boolean killerInZone = zone.isInsideZone(killer);
			boolean victimInZone = zone.isInsideZone(victim);
			
			if (killerInZone)
			{
				killerZoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId());
				if (victimInZone)
				{
					sameZone = zone;
					victimZoneData = killerZoneData;
					break;
				}
			}
			if (victimInZone)
			{
				victimZoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId());
			}
		}
		
		if (sameZone == null || killerZoneData == null || killerZoneData != victimZoneData)
			return;
		
		if (!killerZoneData.isEnchanterZone() || killerZoneData.getEnchantChance() <= 0)
			return;
		
		if (Rnd.get(100) >= killerZoneData.getEnchantChance())
		{
			if (Config.DEVELOPER)
				LOGGER.info("[Debug Enchant] Encantamento falhou para " + killer.getName() + " (chance: " + killerZoneData.getEnchantChance() + "%)");
			return;
		}
		
		List<ItemInstance> enchantableItems = new ArrayList<>();
		List<ItemInstance> paperdollItems = killer.getInventory().getPaperdollItems();
		for (ItemInstance item : paperdollItems)
		{
			if (item == null)
				continue;
			
			if (!item.isEnchantable())
				continue;
			
			if (item.getEnchantLevel() >= killerZoneData.getMaxEnchant())
				continue;
			
			if (item.isWeapon() || item.isArmor())
			{
				enchantableItems.add(item);
			}
		}
		
		if (enchantableItems.isEmpty())
		{
			if (Config.DEVELOPER)
				LOGGER.info("[Debug Enchant] Nenhum item encantável encontrado para " + killer.getName());
			return;
		}
		
		ItemInstance itemToEnchant = enchantableItems.get(Rnd.get(enchantableItems.size()));
		
		int newEnchantLevel = itemToEnchant.getEnchantLevel() + 1;
		if (newEnchantLevel > killerZoneData.getMaxEnchant())
			newEnchantLevel = killerZoneData.getMaxEnchant();
		
		itemToEnchant.setEnchantLevel(newEnchantLevel, killer);
		
		killer.broadcastUserInfo();
		
		killer.sendMessage("Você encantou " + itemToEnchant.getItemName() + " para +" + newEnchantLevel + " ao vencer " + victim.getName() + " em PvP!");
		
		if (Config.DEVELOPER)
			LOGGER.info("[Debug Enchant] " + killer.getName() + " encantou " + itemToEnchant.getItemName() + " para +" + newEnchantLevel + " (kill PvP em Enchanter_Zone)");
	}
	
	
	public static String formatMessage(String template, Map<String, String> values) {  
		if (template == null || values == null) return ""; for (Map.Entry<String, String> entry : values.entrySet()) { if (entry.getKey() != null && entry.getValue() != null) { template = template.replace("%" + entry.getKey() + "%", entry.getValue()); } } return template;
	}
	private String getNextEventTimeFormatted() {  
		if (config == null) return "unknown"; LocalDateTime now = LocalDateTime.now(); LocalTime currentTime = now.toLocalTime().withSecond(0).withNano(0); int currentDay = now.getDayOfWeek().getValue() % 7; for (int i = 0; i < 7; i++) { int dayToCheck = (currentDay + i) % 7; if (config.getActiveDays().contains(dayToCheck)) { List<LocalTime> sortedTimes = config.getActiveTimes().stream().sorted().collect(Collectors.toList()); for (LocalTime time : sortedTimes) { if (i > 0 || time.isAfter(currentTime)) { LocalDateTime nextEvent = now.plusDays(i).with(time); return nextEvent.format(DateTimeFormatter.ofPattern("EEE HH:mm")); } } } } return "unknown";
	}

	
	private void startPartyCheckTask() {
		if (_partyCheckTask != null && !_partyCheckTask.isDone()) {
			_partyCheckTask.cancel(true);
		}
		_partyCheckTask = ThreadPool.scheduleAtFixedRate(this::checkPartyIntegrity, PARTY_CHECK_INTERVAL, PARTY_CHECK_INTERVAL);
		if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Tarefa iniciada."); }
	}

	private void checkPartyIntegrity() {
		if (!isRunning || activeZones.isEmpty()) {
			if (_partyCheckTask != null && !_partyCheckTask.isDone()) {
				_partyCheckTask.cancel(true);
				if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Tarefa cancelada (evento inativo)."); }
			}
			return;
		}

		if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Verificando integridade das parties..."); }
		
		
		Set<Integer> processedPartyLeaders = new HashSet<>();

		for (RandomZone zone : activeZones) {
			RandomZoneData zoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId());
			
			
			if (zoneData == null || !zoneData.isPartyZone()) {
				continue;
			}
			
			if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Verificando Zona: " + zone.getName() + " (ID: " + zone.getId() + ")"); }

			
			List<Player> playersInside = zone.getKnownTypeInside(Player.class);
			
			for (Player player : playersInside) {
				
				if (player == null || player.isGM()) {
					continue;
				}

				Party party = player.getParty();
				
				
				if (party != null && processedPartyLeaders.contains(party.getLeaderObjectId())) {
					continue; 
				}

				boolean expelParty = false;
				String reason = "";

				
				if (party == null) {
					expelParty = true;
					reason = "Você não está mais em uma Party.";
					if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Player " + player.getName() + " não está mais em party."); }
				} 
				
				else if (party.getMembersCount() < zoneData.getMinPartySize()) {
					expelParty = true;
					reason = "Sua Party não tem mais o número mínimo de " + zoneData.getMinPartySize() + " membros.";
					if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Party de " + player.getName() + " tem menos que " + zoneData.getMinPartySize() + " membros."); }
				} 
				
				else {
					for (Player member : party.getMembers()) {
						
						if (member == null || !zone.isInsideZone(member)) {
							expelParty = true;
							reason = "Todos os membros da sua Party devem permanecer dentro da zona do evento.";
							if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Membro " + (member != null ? member.getName() : "NULO") + " da party de " + player.getName() + " não está na zona."); }
							break; 
						}
					}
				}

				if (expelParty) {
					if (party != null) {
						processedPartyLeaders.add(party.getLeaderObjectId());
						
						Location returnLoc = zone.getReturnLocation();
						SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1).addString(reason);
						
						if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Expulsando party do líder " + party.getLeader().getName() + " da zona " + zone.getName() + ". Razão: " + reason); }
						
						for (Player member : party.getMembers()) {
							if (member != null) {
								member.sendPacket(sm);
								member.teleToLocation(returnLoc);
							}
						}
					} else {
						if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Expulsando jogador " + player.getName() + " (sem party) da zona " + zone.getName() + ". Razão: " + reason); }
						
						Location returnLoc = zone.getReturnLocation();
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1).addString(reason));
						player.teleToLocation(returnLoc);
					}
				}
				else if (party != null && !processedPartyLeaders.contains(party.getLeaderObjectId())) {
				    processedPartyLeaders.add(party.getLeaderObjectId());
				    if (Config.DEVELOPER) { LOGGER.info("[Debug Party Check] Party de " + player.getName() + " OK na zona " + zone.getName()); }
				}
			}
		}
	}
	
	/**
	 * Inicia a task que atualiza o flag PvP a cada 5 segundos para players dentro das EnchanterZones.
	 */
	private void startEnchanterZonePvpFlagTask()
	{
		if (_enchanterZonePvpFlagTask != null && !_enchanterZonePvpFlagTask.isDone())
		{
			_enchanterZonePvpFlagTask.cancel(true);
		}
		
		_enchanterZonePvpFlagTask = ThreadPool.scheduleAtFixedRate(this::updateEnchanterZonePvpFlags, 1000, 1000);
		
		if (Config.DEVELOPER)
		{
			LOGGER.info("[Debug Enchanter Zone] Task de atualização de PvP flag iniciada (intervalo: 1 segundo).");
		}
	}
	
	/**
	 * Atualiza o flag PvP para todos os players dentro das EnchanterZones ativas.
	 * Garante que o flag nunca desapareça enquanto o player estiver na zona.
	 */
	private void updateEnchanterZonePvpFlags()
	{
		if (!isRunning || activeZones.isEmpty())
		{
			if (_enchanterZonePvpFlagTask != null && !_enchanterZonePvpFlagTask.isDone())
			{
				_enchanterZonePvpFlagTask.cancel(true);
				if (Config.DEVELOPER)
				{
					LOGGER.info("[Debug Enchanter Zone] Task de PvP flag cancelada (evento inativo).");
				}
			}
			return;
		}
		
		for (RandomZone zone : activeZones)
		{
			RandomZoneData zoneData = RandomData.getInstance().getZoneData(config.getName(), zone.getId());
			
			if (zoneData == null || !zoneData.isEnchanterZone())
			{
				continue;
			}
			
			List<Player> playersInside = zone.getKnownTypeInside(Player.class);
			
			for (Player player : playersInside)
			{
				if (player == null || !player.isOnline() || player.isGM())
				{
					continue;
				}
				
				if (!zone.isInsideZone(player))
				{
					continue;
				}
				
				if (player.getPvpFlag() == 0)
				{
					player.updatePvPFlag(1);
				}
				else if (player.getPvpFlag() == 2)
				{
					player.updatePvPFlag(1);
				}
				
				ext.mods.gameserver.taskmanager.PvpFlagTaskManager.getInstance().remove(player, false);
			}
		}
	}
	
	/**
	 * Envia uma caixa de confirmação de teleporte para todos os players online quando o evento iniciar.
	 */
	private void sendTeleportConfirmationToAllPlayers()
	{
		if (!isRunning || activeZones.isEmpty())
			return;
		
		final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1.getId());
		dlg.addString("Você deseja se teleportar para a Farm Zone?");
		dlg.addTime(30000);
		
		for (Player player : World.getInstance().getPlayers())
		{
			if (player != null && player.isOnline())
			{
				if (player.isDead() || player.isInOlympiadMode() || player.getDungeon() != null)
					continue;
				
				player.sendPacket(dlg);
				player.setLastCommand("farm_event_teleport");
			}
		}
	}
	
	/**
	 * Processa a confirmação de teleporte do evento usando a mesma lógica do FarmZoneTeleport.
	 * @param player O player que confirmou
	 * @param confirmed true se confirmou, false caso contrário
	 * @return true se o teleporte foi processado com sucesso
	 */
	public boolean handleEventTeleportConfirmation(Player player, boolean confirmed)
	{
		if (!isRunning || player == null)
			return false;
		
		if (!"farm_event_teleport".equals(player.getLastCommand()))
			return false;
		
		player.setLastCommand(null);
		
		if (!confirmed)
			return false;
		
		if (player.isDead() || player.isInOlympiadMode() || 
			player.getCast().isCastingNow() || player.isTeleporting())
		{
			player.sendMessage("Teleporte cancelado. Sua condição mudou.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (player.getDungeon() != null)
		{
			player.sendMessage("Você não pode usar este comando dentro de uma dungeon.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (player.isInsideZone(ext.mods.gameserver.enums.ZoneId.PVP) || 
			player.isInsideZone(ext.mods.gameserver.enums.ZoneId.SIEGE))
		{
			player.sendMessage("Você não pode usar este comando desta área.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		if (activeZones == null || activeZones.isEmpty())
		{
			player.sendMessage("Nenhuma Farm Zone ativa no momento.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		RandomZone targetZone = Rnd.get(activeZones);
		if (targetZone == null)
		{
			player.sendMessage("Erro ao selecionar zona do evento.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		Location loc = null;
		RandomZoneData zoneData = RandomData.getInstance().getZoneData(config.getName(), targetZone.getId());
		if (zoneData != null && zoneData.getSpawnLocation() != null)
		{
			loc = zoneData.getSpawnLocation();
		}
		
		if (loc == null)
		{
			loc = targetZone.getRndSpawn(SpawnType.NORMAL);
		}
		
		if (loc == null)
		{
			if (activeSpawns != null && !activeSpawns.isEmpty())
			{
				List<Spawn> zoneSpawns = new ArrayList<>();
				for (Spawn spawn : activeSpawns)
				{
					if (spawn != null && spawn.getNpc() != null)
					{
						SpawnLocation spawnLoc = spawn.getSpawnLocation();
						if (spawnLoc != null && targetZone.isInsideZone(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ()))
						{
							zoneSpawns.add(spawn);
						}
					}
				}
				
				if (!zoneSpawns.isEmpty())
				{
					Spawn selectedSpawn = Rnd.get(zoneSpawns);
					if (selectedSpawn != null && selectedSpawn.getNpc() != null)
					{
						loc = new Location(selectedSpawn.getNpc().getX(), selectedSpawn.getNpc().getY(), selectedSpawn.getNpc().getZ());
					}
				}
			}
		}
		
		if (loc == null)
		{
			Location returnLoc = targetZone.getReturnLocation();
			if (returnLoc != null && returnLoc.getX() != 0 && returnLoc.getY() != 0)
			{
				loc = returnLoc;
			}
		}
		
		if (loc == null)
		{
			player.sendMessage("Não foi possível encontrar um ponto de teleporte na Farm Zone.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return false;
		}
		
		final Location finalLoc = loc;
		final int SOE_VISUAL_SKILL_ID = 2040;
		final int SOE_VISUAL_SKILL_LEVEL = 1;
		final int CAST_TIME_MS = 17000;
		
		player.broadcastPacket(new MagicSkillUse(player, player, SOE_VISUAL_SKILL_ID, SOE_VISUAL_SKILL_LEVEL, CAST_TIME_MS, 0));
		player.sendPacket(new SetupGauge(GaugeColor.BLUE, CAST_TIME_MS));
		
		ThreadPool.schedule(() ->
		{
			if (player.isDead() || player.isInCombat() || player.isTeleporting())
				return;
			player.teleToLocation(finalLoc);
		}, CAST_TIME_MS);
		
		return true;
	}

	public static RandomManager getInstance() { return SingletonHolder._instance; }
	private static class SingletonHolder { protected static final RandomManager _instance = new RandomManager(); }

	/** Para CryptaManager/reflexão: indica se o evento está ativo. */
	public boolean isRunning() { return isRunning; }

	private static void sendQuestionMarkToAllOnline() {
		if (!isRunning) return;
		for (Player online : World.getInstance().getPlayers()) {
			if (online != null && online.isOnline())
				online.sendPacket(new TutorialShowQuestionMark(TUTORIAL_QUESTION_MARK_ID));
		}
	}

	/** Retorna o HTML de alerta do evento para o jogador (usado pelo quest mark). */
	public String getTutorialAlertHtml(Player player) {
		if (player == null || !isRunning) return null;
		String html = HTMLData.getInstance().getHtm(player.getLocale(), "html/mods/tournament/tutorial_alert_random.htm");
		if (html == null || html.isEmpty()) return null;
		String eventName = config != null ? config.getName() : "Random Event";
		int intervalHours = config != null ? config.getInterval() : 1;
		String msg = eventName + " is active! Zones have been announced. Duration: " + intervalHours + (intervalHours == 1 ? " hour." : " hours.");
		return html.replace("%message%", msg);
	}
}