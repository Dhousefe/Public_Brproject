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
package ext.mods.gameserver.model.spawn;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.random.Rnd;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.memo.SpawnMemo;
import ext.mods.gameserver.model.records.PrivateData;

/**
 * This class is a mother-class for all spawns.
 */
public abstract class ASpawn
{
	protected static final CLogger LOGGER = new CLogger(ASpawn.class.getName());
	
	private static final String INSTANCE_PACKAGE = "ext.mods.gameserver.model.actor.instance.";
	
	protected final NpcTemplate _template;
	protected final Constructor<?> _constructor;
	
	protected int _respawnDelay;
	protected int _respawnRandom;
	
	protected boolean _dbLoaded;
	
	protected List<PrivateData> _privateData = Collections.emptyList();
	
	protected SpawnMemo _aiParams = SpawnMemo.DUMMY_SET;
	
	protected SpawnData _spawnData;
	
	protected ASpawn(int id) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		_template = NpcData.getInstance().getTemplate(id);
		if (_template == null)
		{
			_constructor = null;
			return;
		}
		
		Class<?> clazz = Class.forName(INSTANCE_PACKAGE + _template.getType());
		if (!Npc.class.isAssignableFrom(clazz))
		{
			throw new InvalidClassException("Template type " + _template.getType() + " is not child of Npc.");
		}
		
		_constructor = clazz.getConstructor(int.class, NpcTemplate.class);
	}
	
	protected ASpawn(NpcTemplate template) throws SecurityException, ClassNotFoundException, NoSuchMethodException, InvalidClassException
	{
		_template = template;
		if (_template == null)
		{
			_constructor = null;
			return;
		}
		
		Class<?> clazz = Class.forName(INSTANCE_PACKAGE + _template.getType());
		if (!Npc.class.isAssignableFrom(clazz))
		{
			throw new InvalidClassException("Template type " + _template.getType() + " is not child of Npc.");
		}
		
		_constructor = clazz.getConstructor(int.class, NpcTemplate.class);
	}
	
	/**
	 * @return the {@link NpcTemplate} associated to this {@link ASpawn}.
	 */
	public final NpcTemplate getTemplate()
	{
		return _template;
	}
	
	/**
	 * @return the npc id of the {@link NpcTemplate}.
	 */
	public final int getNpcId()
	{
		return _template.getNpcId();
	}
	
	/**
	 * Set the respawn delay, representing the respawn time of this {@link ASpawn}. It can't be less than 0.
	 * @param delay : Respawn delay in seconds.
	 */
	public final void setRespawnDelay(int delay)
	{
		_respawnDelay = Math.max(0, delay);
	}
	
	/**
	 * @return the respawn delay of this {@link ASpawn} in seconds.
	 */
	public final int getRespawnDelay()
	{
		return _respawnDelay;
	}
	
	/**
	 * Set the respawn random delay of this {@link ASpawn}. It can't be less than 0 and no more than respawn delay.
	 * @param random : Random respawn delay in seconds.
	 */
	public final void setRespawnRandom(int random)
	{
		_respawnRandom = Math.clamp(random, 0, _respawnDelay);
	}
	
	/**
	 * @return the respawn delay of this {@link ASpawn} in seconds.
	 */
	public final int getRespawnRandom()
	{
		return _respawnRandom;
	}
	
	/**
	 * @return Calculated respawn delay in seconds, using respawn delay and respawn random values.
	 */
	public long calculateRespawnDelay()
	{
		int respawnTime = _respawnDelay;
		
		if (_respawnRandom > 0)
			respawnTime += Rnd.get(-_respawnRandom, _respawnRandom);
		
		return respawnTime;
	}
	
	protected final long calculateBossRespawnDelayMs(Npc npc)
	{
		if (npc == null || !npc.isRaidBoss())
			return 0L;
		if (!Config.USE_CONFIG_RAID_GRAND_BOSS_RESPAWN)
			return 0L;
		
		final boolean isGrandBoss = npc instanceof GrandBoss;
		final Map<Integer, long[]> overrides = isGrandBoss ? Config.GRAND_BOSS_RESPAWN_OVERRIDES : Config.RAID_BOSS_RESPAWN_OVERRIDES;
		final long[] override = (overrides == null) ? null : overrides.get(npc.getNpcId());
		final long baseMs = (override != null) ? override[0] : (isGrandBoss ? Config.GRAND_BOSS_RESPAWN_BASE_MS : Config.RAID_BOSS_RESPAWN_BASE_MS);
		final long randomMs = (override != null) ? override[1] : (isGrandBoss ? Config.GRAND_BOSS_RESPAWN_RANDOM_MS : Config.RAID_BOSS_RESPAWN_RANDOM_MS);
		if (baseMs <= 0)
			return 0L;
		
		final long offset = (randomMs > 0) ? Rnd.get(-randomMs, randomMs) : 0L;
		return Math.max(baseMs, baseMs + offset);
	}
	
	/**
	 * @return The {@link SpawnData} of this {@link ASpawn}.
	 */
	public final SpawnData getSpawnData()
	{
		return _spawnData;
	}
	
	/**
	 * @return The {@link List} of {@link PrivateData}s of this {@link ASpawn}.
	 */
	public final List<PrivateData> getPrivateData()
	{
		return _privateData;
	}
	
	/**
	 * Retrieve flee_x, flee_y and flee_z spawn variables and build a flee {@link Location}.
	 * @return The flee {@link Location} or null if the variables match 0.
	 */
	public final Location getFleeLocation()
	{
		final int fleeX = _aiParams.getInteger("flee_x", 0);
		final int fleeY = _aiParams.getInteger("flee_y", 0);
		final int fleeZ = _aiParams.getInteger("flee_z", 0);
		
		if (fleeX == 0 && fleeY == 0 && fleeZ == 0)
			return null;
		
		return new Location(fleeX, fleeY, fleeZ);
	}
	
	public final SpawnMemo getMemo()
	{
		return _aiParams;
	}
	
	public abstract Npc getNpc();
	
	/**
	 * @return The {@link SpawnLocation} of this {@link ASpawn}.
	 */
	public abstract SpawnLocation getSpawnLocation();
	
	/**
	 * @param npc : The {@link Npc} of this {@link ASpawn}.
	 * @param offset : The random walk offset.
	 * @return The random walk {@link Location} of this {@link Npc}.
	 */
	public abstract Location getRandomWalkLocation(Npc npc, int offset);
	
	/**
	 * @param worldObject : The {@link WorldObject} to test.
	 * @return True when the {@link WorldObject} is in this {@link ASpawn} area, false otherwise.
	 */
	public abstract boolean isInMyTerritory(WorldObject worldObject);
	
	/**
	 * @return The closer description of this {@link ASpawn}. Use together with {@code toString()} to get complete information.
	 */
	public abstract String getDescription();
	
	/**
	 * Updates {@link SpawnData} of this {@link ASpawn}.
	 */
	public abstract void updateSpawnData();
	
	/**
	 * Trigger {@link EventHandler#SCRIPT_EVENT} for this {@link ASpawn}.
	 * @param eventId : The id of the event.
	 * @param arg1 : 1st argument of the event.
	 * @param arg2 : 2nd argument of the event.
	 */
	public abstract void sendScriptEvent(int eventId, int arg1, int arg2);
	
	/**
	 * Create the {@link Npc} and spawns it into the world.
	 * <ul>
	 * <li>Create {@link Npc} from defined {@link NpcTemplate} and <u>type</u>, using new object id.</li>
	 * <li>Link the {@link Npc} to this {@link ASpawn}</li>
	 * <li>Initialize parameters and create {@link Npc} in the world (see <code>initializeAndSpawn(Npc npc)</code>).</li>
	 * </ul>
	 * @param isSummonSpawn : When true, summon magic circle will appear when spawning into the world.
	 * @param summoner : If set, we allocate summoner variable, otherwise do nothing.
	 * @param clanId : If set, npc will spawn with the specified clan ID.
	 * @return The spawned {@link Npc}.
	 */
	public Npc doSpawn(boolean isSummonSpawn, Creature summoner, Integer clanId)
	{
		try
		{
			final Npc npc = (Npc) _constructor.newInstance(IdFactory.getInstance().getNextId(), _template);
			
			if (summoner != null)
				npc._summoner = summoner;
			
			if (clanId != null)
				npc.setClanId(clanId);
			
			npc.setSpawn(this);
			npc.setShowSummonAnimation(isSummonSpawn);
			
			return initializeAndSpawn(npc);
		}
		catch (Exception e)
		{
			LOGGER.warn("Error during spawn, NPC id={}", e, _template.getNpcId());
			return null;
		}
	}
	
	/**
	 * Create the {@link Npc} and spawns it into the world.
	 * <ul>
	 * <li>Create {@link Npc} from defined {@link NpcTemplate} and <u>type</u>, using new object id.</li>
	 * <li>Link the {@link Npc} to this {@link ASpawn}</li>
	 * <li>Initialize parameters and create {@link Npc} in the world (see <code>initializeAndSpawn(Npc npc)</code>).</li>
	 * </ul>
	 * @param isSummonSpawn : When true, summon magic circle will appear when spawning into the world.
	 * @param summoner : If set, we allocate summoner variable, otherwise do nothing.
	 * @return The spawned {@link Npc}.
	 */
	public Npc doSpawn(boolean isSummonSpawn, Creature summoner)
	{
		try
		{
			final Npc npc = (Npc) _constructor.newInstance(IdFactory.getInstance().getNextId(), _template);
			
			if (summoner != null)
				npc._summoner = summoner;
			
			npc.setSpawn(this);
			npc.setShowSummonAnimation(isSummonSpawn);
			
			return initializeAndSpawn(npc);
		}
		catch (Exception e)
		{
			LOGGER.warn("Error during spawn, NPC id={}", e, _template.getNpcId());
			return null;
		}
	}
	
	public Npc doSpawn(boolean isSummonSpawn)
	{
		return doSpawn(isSummonSpawn, null);
	}
	
	/**
	 * {@link Npc} has spawned.
	 * @param npc : The {@link Npc}.
	 */
	public void onSpawn(Npc npc)
	{
	}
	
	/**
	 * Delete the {@link Npc}(s) of this {@link ASpawn}.
	 */
	public abstract void doDelete();
	
	/**
	 * {@link Npc} has decayed. Create a respawn task to be launched after the fixed + random delay. Respawn is only possible when respawn enabled.
	 * @param npc : The {@link Npc}.
	 */
	public void onDecay(Npc npc)
	{
	}
	
	/**
	 * Refreshes object id of {@link Npc} and spawns it.
	 * @param npc : The {@link Npc} to be respawned.
	 */
	public void doRespawn(Npc npc)
	{
		npc.cancelRespawn();
		
		if (_spawnData != null)
			_spawnData.cancelRespawn();
		
		if (npc.isDecayed())
		{
			npc.refreshID();
			
			initializeAndSpawn(npc);
		}
	}
	
	/**
	 * Teleport all {@link Player}s located in this {@link ASpawn} to specific coords x/y/z.
	 * @param x : The X parameter used as teleport location.
	 * @param y : The Y parameter used as teleport location.
	 * @param z : The Z parameter used as teleport location.
	 * @param offset : An offset used to randomize final teleport location.
	 */
	public void instantTeleportInMyTerritory(int x, int y, int z, int offset)
	{
		World.getInstance().getPlayers().stream().filter(p -> p.isOnline() && isInMyTerritory(p)).forEach(p -> p.teleportTo(x, y, z, offset));
	}
	
	/**
	 * Teleport all {@link Player}s located in this {@link ASpawn} to specific coords x/y/z.
	 * @param x : The X parameter used as teleport location.
	 * @param y : The Y parameter used as teleport location.
	 * @param z : The Z parameter used as teleport location.
	 */
	public void instantTeleportInMyTerritory(int x, int y, int z)
	{
		instantTeleportInMyTerritory(x, y, z, 0);
	}
	
	/**
	 * Teleport all {@link Player}s located in this {@link ASpawn} to a specific {@link Location}.
	 * @see #instantTeleportInMyTerritory(int, int, int)
	 * @param loc : The {@link Location} used as coords.
	 * @param offset : An offset used to randomize final teleport location.
	 */
	public void instantTeleportInMyTerritory(Location loc, int offset)
	{
		instantTeleportInMyTerritory(loc.getX(), loc.getY(), loc.getZ(), offset);
	}
	
	/**
	 * Teleport all {@link Player}s located in this {@link ASpawn} to a specific {@link Location}.
	 * @see #instantTeleportInMyTerritory(int, int, int)
	 * @param loc : The {@link Location} used as coords.
	 */
	public void instantTeleportInMyTerritory(Location loc)
	{
		instantTeleportInMyTerritory(loc.getX(), loc.getY(), loc.getZ(), 0);
	}
	
	/**
	 * Initializes the {@link Npc} based on data of this {@link ASpawn} and spawn it into the world.
	 * <ul>
	 * <li>Check {@link SpawnLocation} of this spawn. Spawn canceled, if no location defined.</li>
	 * <li>Validate {@link SpawnLocation} and get correct Z coordinate from geodata.</li>
	 * <li>Reset {@link Npc} parameters (for re-spawning of existing {@link Npc}s)</li>
	 * <li>Check and set champion status, if allowed.</li>
	 * <li>Make {@link Npc} alive.</li>
	 * <li>Spawn {@link Npc} to the world at given {@link SpawnLocation}.</li>
	 * </ul>
	 * @param npc : {@link Npc} to be initialized and spawned.
	 * @return Spawned {@link Npc}. Null, if no {@link SpawnLocation} is found.
	 */
	public Npc initializeAndSpawn(Npc npc)
	{
		if (_spawnData != null && _spawnData.checkDead())
		{
			npc.getStatus().setHp(0);
			
			npc.setIsDead(true);
			npc.setDecayed(true);
			
			npc.scheduleDBRespawn(_spawnData.getRespawnTime() - System.currentTimeMillis());
			
			_dbLoaded = false;
		}
		else
		{
			npc.stopAllEffects();
			npc.setScriptValue(0);
			
			npc.setIsDead(false);
			npc.setDecayed(false);
			
			npc.getStatus().initializeValues();
			
			SpawnLocation loc = getSpawnLocation();
			if (loc == null)
			{
				LOGGER.warn("{} misses location informations.", this);
				return null;
			}
			
			double maxHp = npc.getStatus().getMaxHp();
			double maxMp = npc.getStatus().getMaxMp();
			
			if (_spawnData != null && _spawnData.checkAlive(loc, maxHp, maxMp))
			{
				loc = _spawnData;
				
				maxHp = _spawnData.getCurrentHp();
				maxMp = _spawnData.getCurrentMp();
				
				_dbLoaded = true;
			}
			
			npc.getStatus().setHpMp(maxHp, maxMp);
			
			if (Config.CHAMPION_FREQUENCY > 0)
			{
				if (npc instanceof Monster && !getTemplate().cantBeChampion() && npc.getStatus().getLevel() >= Config.CHAMP_MIN_LVL && npc.getStatus().getLevel() <= Config.CHAMP_MAX_LVL && !npc.isRaidRelated() && !npc.hasMaster())
					((Monster) npc).setChampion(Rnd.get(100) < Config.CHAMPION_FREQUENCY);
			}
			
			npc.setSpawnLocation(loc);
			npc.spawnMe(loc);
		}
		
		return npc;
	}
	
	public void onDie(Npc npc)
	{
	}
	
	public void doSave()
	{
		if (_spawnData != null)
			SpawnManager.getInstance().save(_spawnData);
	}
	
	public boolean getDBLoaded()
	{
		return _dbLoaded;
	}
	
	public void setDBLoaded(boolean value)
	{
		_dbLoaded = value;
	}
}