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
package ext.mods.gameserver.model.entity.autofarm.zone;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmProfile;
import ext.mods.gameserver.model.entity.autofarm.zone.form.ZoneNPolyZ;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.instance.SchemeBuffer;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.TeleportLocation;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;
import ext.mods.gameserver.data.xml.TeleportData;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.enums.RestartType;

public class AutoFarmArea extends ZoneType
{
	private static final CLogger LOGGER = new CLogger(AutoFarmArea.class.getName());
	private static final String DEATH_RETURN_TAG = "[AutoFarmArea][DeathReturn]";
	
	private static void deathReturnLog(String format, Object... args) {
		if (Config.AUTOFARM_DEBUG_RETURN) {
			Object[] safeArgs = args != null ? new Object[args.length] : new Object[0];
			for (int i = 0; i < safeArgs.length; i++) {
				safeArgs[i] = args[i] != null ? args[i] : "null";
			}
			LOGGER.info(format, safeArgs);
		}
	}
	
	/**
	 * Retorno do AutoFarm não deve ser usado em dungeon (entradas limitadas), Olympiad ou eventos (TvT/DM/LM/CTF/Tournament),
	 * pois esses contextos têm rotina própria de respawn / to village.
	 */
	private static boolean isInContextThatDisallowsDeathReturn(Player player)
	{
		if (player == null)
			return false;
		if (player.getDungeon() != null)
			return true;
		if (player.isInOlympiadMode())
			return true;
		if (player.isInTournament())
			return true;
		if (CTFEvent.getInstance().isStarted() && CTFEvent.getInstance().isPlayerParticipant(player.getObjectId()))
			return true;
		if (DMEvent.getInstance().isStarted() && DMEvent.getInstance().isPlayerParticipant(player.getObjectId()))
			return true;
		if (LMEvent.getInstance().isStarted() && LMEvent.getInstance().isPlayerParticipant(player.getObjectId()))
			return true;
		if (TvTEvent.getInstance().isStarted() && TvTEvent.getInstance().isPlayerParticipant(player.getObjectId()))
			return true;
		return false;
	}
	
	protected final Set<String> _monsterHistory = new HashSet<>();
	private final String _name;
	private final int _ownerId;
	private final AutoFarmType _type;
	private final List<Location> _nodes = new ArrayList<>();
	private boolean _isFromDb;
	private boolean _isChanged;
	private ScheduledFuture<?> _deathMonitorTask;
	private volatile boolean _handlingDeath;
	private Location _lastKilledMonsterLocation;
	private Location _deathLocation;
	private static final int NPC_BUFFER_ID = 50002;
	private static final int NOBLESSE_SKILL_ID = 1323;
	private static final int SCROLL_SKILL_ID = 2040;
	private static final int SCROLL_CAST_MS = 5000;
	private static final int DEATH_WAIT_MS = 3000;
	private static final int BUFFER_INTERACTION_RANGE_MIN = 50;
	private static final int BUFFER_INTERACTION_RANGE_MAX = 200;
	private static final int BUFFER_ARRIVAL_CHECK_MS = 500;
	private static final int BUFFER_ARRIVAL_TIMEOUT_MS = 120000;
	private static final int TOWN_ARRIVAL_DELAY_MS = 6000;
	private static final int BUFFER_NPC_RETRY_DELAY_MS = 1000;
	private static final int BUFFER_NPC_MAX_RETRIES = 4;
	
	public AutoFarmArea(String name, int ownerId, AutoFarmType type)
	{
		super(IdFactory.getInstance().getNextId());
		
		_name = name;
		_ownerId = ownerId;
		_type = type;
	}
	
	public AutoFarmArea(int id, String name, int ownerId, AutoFarmType type)
	{
		super(id);
		
		_name = name;
		_ownerId = ownerId;
		_type = type;
		_isFromDb = true;
	}
	
	@Override
	public void onEnter(Creature character)
	{
	}

	@Override
	public void onExit(Creature character)
	{
	}
	
	public ZoneNPolyZ getZoneZ()
	{
		return (ZoneNPolyZ) getZone();
	}
	
	public List<Monster> getMonsters()
	{
		return null;
	}
	
	public Set<String> getMonsterHistory()
	{
		return _monsterHistory;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public AutoFarmType getType()
	{
		return _type;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public List<Location> getNodes()
	{
		return _nodes;
	}
	
	public AutoFarmZone getFarmZone()
	{
		return null;
	}
	
	public AutoFarmRoute getRouteZone()
	{
		return null;
	}
	
	public boolean isFromDb()
	{
		return _isFromDb;
	}
	
	public void setIsFromDb()
	{
		_isFromDb = true;
	}
	
	/*
	 * Check if any node has been added or removed from this area.
	 */
	public boolean isChanged()
	{
		return _isChanged;
	}
	
	public void setIsChanged(boolean status)
	{
		_isChanged = status;
	}
	
	public boolean isMovementAllowed()
	{
		switch (_type)
		{
			case OPEN:
				return getProfile().getFinalRadius() > getProfile().getAttackRange();
				
			case ROTA:
				return false;
				
			default:
				return true;
		}
	}
	
	public AutoFarmProfile getProfile()
	{
		return AutoFarmManager.getInstance().getPlayer(_ownerId);
	}
	
	public Player getOwner()
	{
		return getProfile().getPlayer();
	}

	public void startDeathMonitor()
	{
		if (_deathMonitorTask != null)
			return;
		
		_deathMonitorTask = ThreadPool.scheduleAtFixedRate(this::checkDeathAndReturnToArea, 3000, 3000);
	}

	public void stopDeathMonitor()
	{
		if (_deathMonitorTask == null)
			return;
		
		_deathMonitorTask.cancel(false);
		_deathMonitorTask = null;
		_handlingDeath = false;
	}
	
	public boolean isHandlingDeath()
	{
		return _handlingDeath;
	}

	public void updateLastKilledMonsterLocation(Location loc)
	{
		if (loc == null)
			return;
		
		_lastKilledMonsterLocation = new Location(loc);
	}

	private void checkDeathAndReturnToArea()
	{
		final Player player = getOwner();
		deathReturnLog("{} checkDeathAndReturnToArea() entry player={} objId={} area={}",
			DEATH_RETURN_TAG, player != null ? player.getName() : null, player != null ? player.getObjectId() : 0, _name);
		
		if (player == null || getProfile() == null || !getProfile().isEnabled())
		{
			deathReturnLog("{} checkDeathAndReturnToArea() skip: player/profile null or disabled", DEATH_RETURN_TAG);
			return;
		}
		
		if (!getProfile().isDeathReturnEnabled())
		{
			deathReturnLog("{} checkDeathAndReturnToArea() skip: death return disabled", DEATH_RETURN_TAG);
			return;
		}
		
		if (isInContextThatDisallowsDeathReturn(player))
		{
			deathReturnLog("{} checkDeathAndReturnToArea() skip: player in dungeon/olympiad/event (return not used)", DEATH_RETURN_TAG);
			return;
		}
		
		if (!player.isDead() || _handlingDeath)
		{
			deathReturnLog("{} checkDeathAndReturnToArea() skip: isDead={} _handlingDeath={}", DEATH_RETURN_TAG, player.isDead(), _handlingDeath);
			return;
		}
		
		_handlingDeath = true;
		_deathLocation = new Location(player.getPosition());
		player.getAI().getCurrentIntention().updateAsIdle();
		
		if (player.isOfflineFarm())
		{
			deathReturnLog("{} checkDeathAndReturnToArea() offline farm: scheduling revive + instantTeleportTo(farm) in {}ms", DEATH_RETURN_TAG, DEATH_WAIT_MS);
			ThreadPool.schedule(() ->
			{
				if (player == null || getProfile() == null || !getProfile().isEnabled())
				{
					_handlingDeath = false;
					return;
				}
				if (!player.isDead())
				{
					deathReturnLog("{} checkDeathAndReturnToArea() offline: player no longer dead, abort", DEATH_RETURN_TAG);
					_handlingDeath = false;
					return;
				}
				player.doRevive();
				player.getMove().stop();
				if (_deathLocation != null)
				{
					player.instantTeleportTo(_deathLocation, 20);
					deathReturnLog("{} checkDeathAndReturnToArea() offline farm: revived and instantTeleportTo(farm area) done", DEATH_RETURN_TAG);
				}
				_handlingDeath = false;
			}, DEATH_WAIT_MS);
			return;
		}
		
		deathReturnLog("{} checkDeathAndReturnToArea() death detected loc={} scheduling revive+town in {}ms",
			DEATH_RETURN_TAG, _deathLocation, DEATH_WAIT_MS);
		
		ThreadPool.schedule(() ->
		{
			if (!player.isDead())
			{
				deathReturnLog("{} checkDeathAndReturnToArea() revive task: player no longer dead, abort", DEATH_RETURN_TAG);
				_handlingDeath = false;
				return;
			}
			
			player.doRevive();
			player.teleportTo(RestartType.TOWN);
			deathReturnLog("{} checkDeathAndReturnToArea() revived and teleported to town, scheduling processBufferAndReturnWithRetry in {}ms",
				DEATH_RETURN_TAG, TOWN_ARRIVAL_DELAY_MS);
			
			ThreadPool.schedule(() ->
			{
				if (player.isDead())
				{
					deathReturnLog("{} checkDeathAndReturnToArea() town task: player dead again, abort", DEATH_RETURN_TAG);
					_handlingDeath = false;
					return;
				}
				player.getAI().getCurrentIntention().updateAsIdle();
				processBufferAndReturnWithRetry(player, 0);
			}, TOWN_ARRIVAL_DELAY_MS);
		}, DEATH_WAIT_MS);
	}
	
	private void processBufferAndReturnWithRetry(Player player, int retryCount)
	{
		try
		{
			deathReturnLog("{} processBufferAndReturnWithRetry() entry player={} retryCount={}/{}",
				DEATH_RETURN_TAG, player.getName(), retryCount, BUFFER_NPC_MAX_RETRIES);
			
			if (getProfile() == null || !getProfile().isEnabled())
			{
				deathReturnLog("{} processBufferAndReturnWithRetry() abort: profile null or disabled", DEATH_RETURN_TAG);
				_handlingDeath = false;
				return;
			}
			
			final boolean hasBuffs = player.getBuffCount() > 0;
			deathReturnLog("{} processBufferAndReturnWithRetry() hasBuffs={} isNoble={}", DEATH_RETURN_TAG, hasBuffs, player.isNoble());
			
			if (hasBuffs && player.isNoble())
			{
				final L2Skill noblessSkill = SkillTable.getInstance().getInfo(NOBLESSE_SKILL_ID, 1);
				if (noblessSkill != null && player.getSkill(NOBLESSE_SKILL_ID) != null)
				{
					deathReturnLog("{} processBufferAndReturnWithRetry() using Nobless skill {} (SkillTable.getInfo, Player.getSkill), scheduling doScrollAndTeleport in 2000ms", DEATH_RETURN_TAG, NOBLESSE_SKILL_ID);
					player.getAI().tryToCast(player, noblessSkill, false, false, 0);
					ThreadPool.schedule(() -> doScrollAndTeleport(player, true, false), 2000);
					return;
				}
			}
			
			if (hasBuffs)
			{
				deathReturnLog("{} processBufferAndReturnWithRetry() already buffed, calling doScrollAndTeleport(player, true, false)", DEATH_RETURN_TAG);
				doScrollAndTeleport(player, true, false);
				return;
			}
			
			final Npc bufferNpc = findClosestBufferNpc(player);
			if (bufferNpc == null)
			{
				if (retryCount < BUFFER_NPC_MAX_RETRIES)
				{
					deathReturnLog("{} processBufferAndReturnWithRetry() findClosestBufferNpc returned null, retry in {}ms retryCount={}",
						DEATH_RETURN_TAG, BUFFER_NPC_RETRY_DELAY_MS, retryCount + 1);
					ThreadPool.schedule(() -> processBufferAndReturnWithRetry(player, retryCount + 1), BUFFER_NPC_RETRY_DELAY_MS);
					return;
				}
				deathReturnLog("{} processBufferAndReturnWithRetry() findClosestBufferNpc still null after {} retries, abort", DEATH_RETURN_TAG, BUFFER_NPC_MAX_RETRIES);
				_handlingDeath = false;
				return;
			}
			
			deathReturnLog("{} processBufferAndReturnWithRetry() bufferNpc found objId={} loc={} calling moveToBufferAndReturn",
				DEATH_RETURN_TAG, bufferNpc.getObjectId(), bufferNpc.getPosition());
			moveToBufferAndReturn(player, bufferNpc);
		}
		catch (Exception e)
		{
			LOGGER.error("Exception in processBufferAndReturnWithRetry, resetting _handlingDeath", e);
			_handlingDeath = false;
		}
	}
	
	private void processBufferAndReturn(Player player)
	{
		processBufferAndReturnWithRetry(player, 0);
	}
	
	private void moveToBufferAndReturn(Player player, Npc bufferNpc)
	{
		final String scheme = isMageRole(player) ? "Mage" : "Fighter";
		deathReturnLog("{} moveToBufferAndReturn() entry player={} bufferNpc={} scheme={} (isMageRole -> Mage/Fighter)",
			DEATH_RETURN_TAG, player.getName(), bufferNpc.getObjectId(), scheme);
		
		if (player.isOfflineFarm())
		{
			final Location npcLoc = bufferNpc.getPosition();
			final int offset = (BUFFER_INTERACTION_RANGE_MIN + BUFFER_INTERACTION_RANGE_MAX) / 2;
			final Location nearBuffer = new Location(npcLoc.getX() + offset, npcLoc.getY(), npcLoc.getZ());
			player.getMove().stop();
			player.instantTeleportTo(nearBuffer, 0);
			deathReturnLog("{} moveToBufferAndReturn() offline farm: teleported to buffer, scheduling bypass and return", DEATH_RETURN_TAG);
			ThreadPool.schedule(() ->
			{
				if (player == null || player.isDead() || getProfile() == null || !getProfile().isEnabled())
				{
					_handlingDeath = false;
					return;
				}
				player.setTarget(bufferNpc);
				if (bufferNpc instanceof SchemeBuffer)
					bufferNpc.onBypassFeedback(player, "getscheme " + scheme);
				ThreadPool.schedule(() -> doScrollAndTeleport(player, true, false), 1500);
			}, 500);
			return;
		}
		
		player.setTarget(bufferNpc);
		final Location npcLoc = bufferNpc.getPosition();
		final boolean moveStarted = player.getMove().maybePlayerMoveToLocation(npcLoc, BUFFER_INTERACTION_RANGE_MAX, Config.SISTEMA_PATHFINDING, false);
		deathReturnLog("{} moveToBufferAndReturn() Player.getMove().maybePlayerMoveToLocation(loc={} offset={} pathfinding={}) returned {}",
			DEATH_RETURN_TAG, npcLoc, BUFFER_INTERACTION_RANGE_MAX, Config.SISTEMA_PATHFINDING, moveStarted);
		
		final long startTime = System.currentTimeMillis();
		final ScheduledFuture<?>[] taskHolder = new ScheduledFuture<?>[1];
		final long[] lastMoveRetry = new long[] { 0 };
		
		taskHolder[0] = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (player == null || player.isDead() || getProfile() == null || !getProfile().isEnabled())
			{
				deathReturnLog("{} moveToBufferAndReturn() polling: abort condition playerDead={} profileEnabled={}",
					DEATH_RETURN_TAG, player != null && player.isDead(), getProfile() != null && getProfile().isEnabled());
				if (taskHolder[0] != null)
					taskHolder[0].cancel(false);
				_handlingDeath = false;
				return;
			}
			
			final double distToNpc = bufferNpc != null ? player.distance3D(bufferNpc) : Double.MAX_VALUE;
			final boolean inRange = bufferNpc != null && distToNpc >= BUFFER_INTERACTION_RANGE_MIN && distToNpc <= BUFFER_INTERACTION_RANGE_MAX;
			final boolean timeout = System.currentTimeMillis() - startTime >= BUFFER_ARRIVAL_TIMEOUT_MS;
			
			if (!inRange && !timeout && bufferNpc != null)
			{
				final long now = System.currentTimeMillis();
				if (now - lastMoveRetry[0] >= 3000)
				{
					lastMoveRetry[0] = now;
					player.setTarget(bufferNpc);
					player.getMove().maybePlayerMoveToLocation(bufferNpc.getPosition(), BUFFER_INTERACTION_RANGE_MAX, Config.SISTEMA_PATHFINDING, false);
					deathReturnLog("{} moveToBufferAndReturn() polling: re-sent move to buffer distToNpc={}", DEATH_RETURN_TAG, distToNpc);
				}
			}
			
			if (inRange || timeout)
			{
				taskHolder[0].cancel(false);
				final boolean forceReturn = timeout && player.getBuffCount() == 0;
				final boolean wasBuffed;
				if (inRange && bufferNpc instanceof SchemeBuffer)
				{
					deathReturnLog("{} moveToBufferAndReturn() polling: inRange distToNpc={} calling SchemeBuffer.onBypassFeedback(getscheme {})",
						DEATH_RETURN_TAG, distToNpc, scheme);
					player.getMove().stop();
					player.setTarget(bufferNpc);
					bufferNpc.onBypassFeedback(player, "getscheme " + scheme);
					wasBuffed = true;
				}
				else
				{
					wasBuffed = false;
					deathReturnLog("{} moveToBufferAndReturn() polling: timeout={} inRange={} forceReturn={} (no buff), scheduling doScrollAndTeleport",
						DEATH_RETURN_TAG, timeout, inRange, forceReturn);
				}
				ThreadPool.schedule(() -> doScrollAndTeleport(player, wasBuffed, forceReturn), 1500);
			}
		}, BUFFER_ARRIVAL_CHECK_MS, BUFFER_ARRIVAL_CHECK_MS);
	}
	
	private Npc findClosestBufferNpc(Player player)
	{
		deathReturnLog("{} findClosestBufferNpc() entry player={} NPC_BUFFER_ID={}", DEATH_RETURN_TAG, player.getName(), NPC_BUFFER_ID);
		final Location bufferSpawnLoc = SpawnManager.getInstance().getClosestSpawnLocation(player, NPC_BUFFER_ID);
		if (bufferSpawnLoc == null)
		{
			deathReturnLog("{} findClosestBufferNpc() SpawnManager.getClosestSpawnLocation returned null", DEATH_RETURN_TAG);
			return null;
		}
		deathReturnLog("{} findClosestBufferNpc() SpawnManager.getClosestSpawnLocation loc={}", DEATH_RETURN_TAG, bufferSpawnLoc);
		
		final java.util.List<Npc> inRadius = World.getInstance().getNpcs(NPC_BUFFER_ID).stream()
			.filter(n -> n instanceof SchemeBuffer && n.isIn3DRadius(bufferSpawnLoc, 3000))
			.sorted(Comparator.comparingDouble(n -> n.distance3D(player)))
			.toList();
		Npc chosen = inRadius.isEmpty() ? null : inRadius.get(0);
		if (chosen == null)
		{
			final java.util.List<Npc> any = World.getInstance().getNpcs(NPC_BUFFER_ID).stream()
				.filter(n -> n instanceof SchemeBuffer)
				.sorted(Comparator.comparingDouble(n -> n.distance3D(player)))
				.toList();
			chosen = any.isEmpty() ? null : any.get(0);
			deathReturnLog("{} findClosestBufferNpc() World.getNpcs(50002) inRadius=0 fallback any count={} chosen={}",
				DEATH_RETURN_TAG, any.size(), chosen != null ? chosen.getObjectId() : null);
		}
		else
			deathReturnLog("{} findClosestBufferNpc() World.getNpcs(50002) inRadius count={} chosen objId={} dist={}",
				DEATH_RETURN_TAG, inRadius.size(), chosen.getObjectId(), player.distance3D(chosen));
		return chosen;
	}
	
	private void doScrollAndTeleport(Player player, boolean wasBuffed, boolean forceReturnWhenTimeout)
	{
		deathReturnLog("{} doScrollAndTeleport() entry player={} wasBuffed={} forceReturnWhenTimeout={}",
			DEATH_RETURN_TAG, player != null ? player.getName() : null, wasBuffed, forceReturnWhenTimeout);
		
		if (player == null || player.isDead())
		{
			deathReturnLog("{} doScrollAndTeleport() abort: player null or dead", DEATH_RETURN_TAG);
			_handlingDeath = false;
			return;
		}
		
		if (!wasBuffed && !forceReturnWhenTimeout)
		{
			deathReturnLog("{} doScrollAndTeleport() skip scroll: not buffed and not force return", DEATH_RETURN_TAG);
			_handlingDeath = false;
			return;
		}
		
		final Location reference = (_lastKilledMonsterLocation != null) ? _lastKilledMonsterLocation : _deathLocation;
		
		if (player.isOfflineFarm() && reference != null)
		{
			deathReturnLog("{} doScrollAndTeleport() offline farm: instantTeleportTo(farm area) reference={}", DEATH_RETURN_TAG, reference);
			player.getMove().stop();
			if (!player.isDead())
				player.instantTeleportTo(reference, 20);
			_handlingDeath = false;
			return;
		}
		
		final TeleportLocation closest = findClosestTeleport(reference);
		deathReturnLog("{} doScrollAndTeleport() findClosestTeleport(reference={}) -> closest={}",
			DEATH_RETURN_TAG, reference, closest != null ? closest.toString() : null);
		
		if (closest == null)
		{
			deathReturnLog("{} doScrollAndTeleport() abort: no TeleportLocation (TeleportData.getAllTeleports)", DEATH_RETURN_TAG);
			_handlingDeath = false;
			return;
		}
		
		deathReturnLog("{} doScrollAndTeleport() MagicSkillUse skillId={} castMs={} then Player.teleportTo in {}ms",
			DEATH_RETURN_TAG, SCROLL_SKILL_ID, SCROLL_CAST_MS, SCROLL_CAST_MS);
		player.broadcastPacket(new MagicSkillUse(player, player, SCROLL_SKILL_ID, 1, SCROLL_CAST_MS, 0));
		
		ThreadPool.schedule(() ->
		{
			if (player != null && !player.isDead())
			{
				deathReturnLog("{} doScrollAndTeleport() executing Player.teleportTo(closest, 20)", DEATH_RETURN_TAG);
				player.teleportTo(closest, 20);
			}
			_handlingDeath = false;
		}, SCROLL_CAST_MS);
	}
	
	private boolean isMageRole(Player player)
	{
		final int pAtk = player.getStatus().getPAtk(null);
		final int mAtk = player.getStatus().getMAtk(null, null);
		return mAtk > pAtk;
	}

	private TeleportLocation findClosestTeleport(Location reference)
	{
		deathReturnLog("{} findClosestTeleport() entry reference={}", DEATH_RETURN_TAG, reference);
		if (reference == null)
		{
			deathReturnLog("{} findClosestTeleport() reference null, return null", DEATH_RETURN_TAG);
			return null;
		}
		final List<TeleportLocation> teleports = TeleportData.getInstance().getAllTeleports();
		if (teleports == null || teleports.isEmpty())
		{
			deathReturnLog("{} findClosestTeleport() TeleportData.getInstance().getAllTeleports() empty or null", DEATH_RETURN_TAG);
			return null;
		}
		final TeleportLocation closest = teleports.stream()
			.min(Comparator.comparingDouble(loc -> loc.distance3D(reference)))
			.orElse(null);
		deathReturnLog("{} findClosestTeleport() closest={} totalTeleports={}", DEATH_RETURN_TAG, closest != null ? closest.toString() : null, teleports.size());
		return closest;
	}
	
	public boolean isOwnerNearOrInside(int proximityRadius)
	{
		final Player player = getOwner();
		final int ax1 = player.getX() - proximityRadius;
		final int ax2 = player.getX() + proximityRadius;
		final int ay1 = player.getY() - proximityRadius;
		final int ay2 = player.getY() + proximityRadius;
		final int az1 = player.getZ() - proximityRadius;
		final int az2 = player.getZ() + proximityRadius;
	    return getZoneZ().intersectsRectangle(ax1, ax2, ay1, ay2, az1, az2);
	}
	
	public boolean isOwnerNearEdge(int proximityRadius)
	{
		final Player player = getOwner();
		final int ax1 = player.getX() - proximityRadius;
		final int ax2 = player.getX() + proximityRadius;
		final int ay1 = player.getY() - proximityRadius;
		final int ay2 = player.getY() + proximityRadius;
	    return getZoneZ().intersectsRectangleOnEdge(ax1, ax2, ay1, ay2);
	}
}