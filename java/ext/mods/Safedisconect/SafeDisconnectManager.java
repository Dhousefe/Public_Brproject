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
package ext.mods.Safedisconect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import ext.mods.Config;
import ext.mods.commons.pool.CoroutinePool;
import ext.mods.gameserver.enums.PunishmentType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.InstanceMap.InstanceManager;
import ext.mods.InstanceMap.MapInstance;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager;
import ext.mods.gameserver.network.GameClient;
import ext.mods.gameserver.network.serverpackets.ValidateLocation;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.gameserver.model.olympiad.OlympiadGameManager;
import ext.mods.gameserver.model.olympiad.OlympiadGameTask;


/**
 * Safe Disconnect Manager
 * 
 * Autor: Dhousefe - L2jBr
 * Data: 29/01/2026
 * Versão: 1.0.0
 * Descrição: Gerencia o safe disconnect e aplica efeitos de safe disconnect aos jogadores.
 * 
*/
public final class SafeDisconnectManager
{
	private static final long EXPECTED_LOGOUT_GRACE_MS = 15000L;
	private static final String MEMO_TITLE = "safe_disconnect_title";
	private static final String MEMO_NAME_COLOR = "safe_disconnect_name_color";
	private static final String MEMO_TITLE_COLOR = "safe_disconnect_title_color";
	private static final String MEMO_ACTIVE = "safe_disconnect_active";
	private static final String MEMO_AUTOFARM = "safe_disconnect_autofarm";
	private static final String MEMO_NEED_SYNC = "safe_disconnect_need_sync";
	
	private final Map<Integer, SafeDisconnectSession> _sessions = new ConcurrentHashMap<>();
	private final Map<Integer, Long> _expectedDisconnects = new ConcurrentHashMap<>();
	
	private SafeDisconnectManager()
	{
	}
	
	public void markExpectedLogout(Player player)
	{
		if (player == null)
			return;
		
		_expectedDisconnects.put(player.getObjectId(), System.currentTimeMillis());
	}
	
	public boolean handleDisconnect(GameClient client)
	{
		if (!Config.SAFEDISCONNECT_ENABLED || client == null)
			return false;
		
		final Player player = client.getPlayer();
		if (player == null)
			return false;
		
		if (isExpectedDisconnect(player) || !canApplySafeDisconnect(player))
		{
			_expectedDisconnects.remove(player.getObjectId());
			return false;
		}
		
		if (_sessions.containsKey(player.getObjectId()))
			return true;
		
		startSession(player, client);
		return true;
	}
	
	public void onEnterWorld(Player player)
	{
		if (player == null)
			return;
		
		final SafeDisconnectSession session = _sessions.remove(player.getObjectId());
		if (session != null)
		{
			session.cancelTimeout();
			restorePlayerState(player, session);
			syncWorldAfterReconnect(player);
			if (Config.SAFEDISCONNECT_INTEGRATION_DUNGEON && player.getDungeon() != null)
				player.getDungeon().resumeForReconnect(player);
			if (Config.SAFEDISCONNECT_INTEGRATION_TOURNAMENT && player.getTournamentBattle() != null)
				player.getTournamentBattle().resumeForReconnect(player);
			resumeOlympiadIfNeeded(player);
			ensureInstanceConsistency(player);
			return;
		}
		
		restoreFromMemos(player);
		if (player.getMemos().containsKey(MEMO_NEED_SYNC))
		{
			player.getMemos().unset(MEMO_NEED_SYNC);
			syncWorldAfterReconnect(player);
		}
		if (Config.SAFEDISCONNECT_INTEGRATION_DUNGEON && player.getDungeon() != null)
			player.getDungeon().resumeForReconnect(player);
		if (Config.SAFEDISCONNECT_INTEGRATION_TOURNAMENT && player.getTournamentBattle() != null)
			player.getTournamentBattle().resumeForReconnect(player);
		resumeOlympiadIfNeeded(player);
		ensureInstanceConsistency(player);
	}
	
	public boolean prepareReconnect(Player player, GameClient newClient)
	{
		if (player == null)
			return false;
		
		pauseAutoFarmForReconnect(player);
		
		if (player.getClient() != null && player.getClient().isDetached())
		{
			detachExistingClient(player, newClient);
			restoreFromMemos(player);
			player.getMemos().set(MEMO_NEED_SYNC, true);
			if (Config.SAFEDISCONNECT_INTEGRATION_DUNGEON && player.getDungeon() != null)
				player.getDungeon().resumeForReconnect(player);
			if (Config.SAFEDISCONNECT_INTEGRATION_TOURNAMENT && player.getTournamentBattle() != null)
				player.getTournamentBattle().resumeForReconnect(player);
			resumeOlympiadIfNeeded(player);
			ensureInstanceConsistency(player);
			return true;
		}
		
		final SafeDisconnectSession session = _sessions.remove(player.getObjectId());
		if (session == null)
		{
			final boolean restored = restoreFromMemos(player);
			if (restored || hasSafeDisconnectMemos(player))
			{
				detachExistingClient(player, newClient);
				player.getMemos().set(MEMO_NEED_SYNC, true);
				if (Config.SAFEDISCONNECT_INTEGRATION_DUNGEON && player.getDungeon() != null)
					player.getDungeon().resumeForReconnect(player);
				if (Config.SAFEDISCONNECT_INTEGRATION_TOURNAMENT && player.getTournamentBattle() != null)
					player.getTournamentBattle().resumeForReconnect(player);
				resumeOlympiadIfNeeded(player);
				ensureInstanceConsistency(player);
				return true;
			}
			return false;
		}
		
		session.cancelTimeout();
		detachOldClient(session, newClient);
		restorePlayerState(player, session);
		player.getMemos().set(MEMO_NEED_SYNC, true);
		if (Config.SAFEDISCONNECT_INTEGRATION_DUNGEON && player.getDungeon() != null)
			player.getDungeon().resumeForReconnect(player);
		if (Config.SAFEDISCONNECT_INTEGRATION_TOURNAMENT && player.getTournamentBattle() != null)
			player.getTournamentBattle().resumeForReconnect(player);
		resumeOlympiadIfNeeded(player);
		ensureInstanceConsistency(player);
		return true;
	}

	/**
	 * Sincroniza o estado do mundo após reconexão (critical error): força refresh de visibilidade
	 * (NPCs) e corrige a posição no cliente para evitar dessincronia de movimento.
	 */
	private void syncWorldAfterReconnect(Player player)
	{
		if (player == null)
			return;
		
		player.decayMe();
		player.spawnMe();
		
		player.sendPacket(new ValidateLocation(player));
		player.broadcastPacket(new ValidateLocation(player));
	}
	
	private void ensureInstanceConsistency(Player player)
	{
		if (player == null)
			return;
		
		final MapInstance instance = player.getInstanceMap();
		if (instance == null || instance.getId() == 0)
			return;
		
		final MapInstance registered = InstanceManager.getInstance().getInstance(instance.getId());
		final boolean dungeonMissing = player.getDungeon() == null;
		final boolean battleMissing = player.getTournamentBattle() == null;
		
		if (registered == null || (dungeonMissing && battleMissing))
		{
			player.setDungeon(null);
			player.setInstanceMap(InstanceManager.getInstance().getInstance(0), true);
			player.teleportTo(81664, 149056, -3472, 15);
			player.revalidateZone(true);
			player.broadcastCharInfo();
			player.broadcastUserInfo();
		}
	}

	private void pauseOlympiadIfNeeded(Player player)
	{
		if (player == null)
			return;
		if (!Config.SAFEDISCONNECT_INTEGRATION_OLYMPIAD)
			return;
		
		final int gameId = player.getOlympiadGameId();
		if (gameId < 0)
			return;
		
		final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(gameId);
		if (task != null && task.isGameStarted())
			task.pauseForDisconnect(player);
	}

	private void resumeOlympiadIfNeeded(Player player)
	{
		if (player == null)
			return;
		if (!Config.SAFEDISCONNECT_INTEGRATION_OLYMPIAD)
			return;
		
		final int gameId = player.getOlympiadGameId();
		if (gameId < 0)
			return;
		
		final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(gameId);
		if (task != null && task.isGameStarted())
			task.resumeForReconnect(player);
	}

	private void handleOlympiadTimeout(Player player)
	{
		if (player == null)
			return;
		if (!Config.SAFEDISCONNECT_INTEGRATION_OLYMPIAD)
			return;
		
		final int gameId = player.getOlympiadGameId();
		if (gameId < 0)
			return;
		
		final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(gameId);
		if (task != null && task.isGameStarted())
			task.handleTimeoutDisconnect(player);
	}
	
	private void startSession(Player player, GameClient client)
	{
		final int playerId = player.getObjectId();
		final SafeDisconnectSession session = new SafeDisconnectSession(player, client);
		if (_sessions.putIfAbsent(playerId, session) != null)
			return;
		
		applySafeState(player, session);
		setCharacterOnlineToZero(playerId);
		session.scheduleTimeout();
	}
	
	/**
	 * Atualiza a coluna online para 0 na tabela characters, deixando a conta acessivel para reconexao.
	 */
	private void setCharacterOnlineToZero(int objectId)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("UPDATE characters SET online=0, lastAccess=? WHERE obj_id=?"))
		{
			ps.setLong(1, System.currentTimeMillis());
			ps.setInt(2, objectId);
			ps.execute();
		}
		catch (Exception e)
		{
		}
	}
	
	private void applySafeState(Player player, SafeDisconnectSession session)
	{
		saveOriginalAppearance(player);
		saveAutoFarmState(player);
		if (Config.SAFEDISCONNECT_INTEGRATION_DUNGEON && player.getDungeon() != null)
			player.getDungeon().pauseForDisconnect(player);
		if (Config.SAFEDISCONNECT_INTEGRATION_TOURNAMENT && player.getTournamentBattle() != null)
			player.getTournamentBattle().pauseForDisconnect(player);
		pauseOlympiadIfNeeded(player);
		
		player.setOnlineStatus(false, false);
		player.abortAll(true);
		player.setTarget(null);
		player.getMove().stop();
		
		if (session.client != null)
			session.client.setDetached(true);
		
		if (Config.SAFEDISCONNECT_IMMOBILIZE)
			player.setIsImmobilized(true);
		
		if (Config.SAFEDISCONNECT_INVULNERABLE)
			player.setInvul(true);
		
		if (Config.SAFEDISCONNECT_CHANGE_NAME_COLOR)
			player.getAppearance().setNameColor(Config.SAFEDISCONNECT_NAME_COLOR);
		
		player.setTitle(Config.SAFEDISCONNECT_TITLE);
		player.broadcastUserInfo();
	}
	
	private void restorePlayerState(Player player, SafeDisconnectSession session)
	{
		if (Config.SAFEDISCONNECT_IMMOBILIZE)
			player.setIsImmobilized(session.wasImmobilized);
		
		if (Config.SAFEDISCONNECT_INVULNERABLE)
			player.setInvul(session.wasInvul);
		
		restoreOriginalAppearance(player, session);
		restoreAutoFarmState(player);
		
		player.broadcastUserInfo();
	}
	
	private void saveOriginalAppearance(Player player)
	{
		if (!player.getMemos().containsKey(MEMO_ACTIVE))
			player.getMemos().set(MEMO_ACTIVE, true);
		
		if (!player.getMemos().containsKey(MEMO_TITLE))
			player.getMemos().set(MEMO_TITLE, player.getTitle() == null ? "" : player.getTitle());
		
		if (!player.getMemos().containsKey(MEMO_NAME_COLOR))
			player.getMemos().set(MEMO_NAME_COLOR, player.getAppearance().getNameColor());
		
		if (!player.getMemos().containsKey(MEMO_TITLE_COLOR))
			player.getMemos().set(MEMO_TITLE_COLOR, player.getAppearance().getTitleColor());
	}
	
	private void restoreOriginalAppearance(Player player, SafeDisconnectSession session)
	{
		if (player.getMemos().containsKey(MEMO_TITLE))
		{
			player.setTitle(player.getMemos().get(MEMO_TITLE));
			player.getMemos().unset(MEMO_TITLE);
		}
		else
		{
			player.setTitle(session.originalTitle);
		}
		
		if (player.getMemos().containsKey(MEMO_NAME_COLOR))
		{
			player.getAppearance().setNameColor(player.getMemos().getInteger(MEMO_NAME_COLOR, session.originalNameColor));
			player.getMemos().unset(MEMO_NAME_COLOR);
		}
		else if (Config.SAFEDISCONNECT_CHANGE_NAME_COLOR)
		{
			player.getAppearance().setNameColor(session.originalNameColor);
		}
		
		if (player.getMemos().containsKey(MEMO_TITLE_COLOR))
		{
			player.getAppearance().setTitleColor(player.getMemos().getInteger(MEMO_TITLE_COLOR, session.originalTitleColor));
			player.getMemos().unset(MEMO_TITLE_COLOR);
		}
		else
		{
			player.getAppearance().setTitleColor(session.originalTitleColor);
		}
		
		if (player.getMemos().containsKey(MEMO_ACTIVE))
			player.getMemos().unset(MEMO_ACTIVE);
	}
	
	private boolean restoreFromMemos(Player player)
	{
		boolean changed = false;
		
		if (player.getMemos().containsKey(MEMO_TITLE))
		{
			player.setTitle(player.getMemos().get(MEMO_TITLE));
			player.getMemos().unset(MEMO_TITLE);
			changed = true;
		}
		
		if (player.getMemos().containsKey(MEMO_NAME_COLOR))
		{
			player.getAppearance().setNameColor(player.getMemos().getInteger(MEMO_NAME_COLOR, player.getAppearance().getNameColor()));
			player.getMemos().unset(MEMO_NAME_COLOR);
			changed = true;
		}
		
		if (player.getMemos().containsKey(MEMO_TITLE_COLOR))
		{
			player.getAppearance().setTitleColor(player.getMemos().getInteger(MEMO_TITLE_COLOR, player.getAppearance().getTitleColor()));
			player.getMemos().unset(MEMO_TITLE_COLOR);
			changed = true;
		}
		
		if (player.getMemos().containsKey(MEMO_ACTIVE))
			player.getMemos().unset(MEMO_ACTIVE);
		
		restoreAutoFarmState(player);
		
		if (changed)
			player.broadcastUserInfo();
		
		return changed;
	}

	private void saveAutoFarmState(Player player)
	{
		final AutoFarmManager autoFarmManager = AutoFarmManager.getInstance();
		if (autoFarmManager.isPlayerActive(player.getObjectId()))
		{
			player.getMemos().set(MEMO_AUTOFARM, true);
			autoFarmManager.stopPlayer(player, null, false);
		}
	}
	
	private void pauseAutoFarmForReconnect(Player player)
	{
		final AutoFarmManager autoFarmManager = AutoFarmManager.getInstance();
		if (!autoFarmManager.isPlayerActive(player.getObjectId()))
			return;
		
		player.getMemos().set(MEMO_AUTOFARM, true);
		autoFarmManager.stopPlayer(player, null, false);
	}
	
	private void restoreAutoFarmState(Player player)
	{
		if (!player.getMemos().containsKey(MEMO_AUTOFARM))
			return;
		
		player.getMemos().unset(MEMO_AUTOFARM);
		final AutoFarmManager autoFarmManager = AutoFarmManager.getInstance();
		final long delay = Math.max(0L, Config.SAFEDISCONNECT_AUTOFARM_RESTORE_DELAY_MS);
		if (delay == 0L)
		{
			if (player.isOnline() && !autoFarmManager.isPlayerActive(player.getObjectId()))
				autoFarmManager.startPlayer(player, false);
			return;
		}
		
		CoroutinePool.schedule(() ->
		{
			if (player.isOnline() && !autoFarmManager.isPlayerActive(player.getObjectId()))
				autoFarmManager.startPlayer(player, false);
		}, delay);
	}
	
	private boolean hasSafeDisconnectMemos(Player player)
	{
		return player.getMemos().containsKey(MEMO_TITLE)
			|| player.getMemos().containsKey(MEMO_NAME_COLOR)
			|| player.getMemos().containsKey(MEMO_TITLE_COLOR)
			|| player.getMemos().containsKey(MEMO_ACTIVE);
	}

	public boolean isSafeDisconnectActive(Player player)
	{
		if (player == null)
			return false;
		
		return player.getMemos().containsKey(MEMO_ACTIVE);
	}
	
	private void detachExistingClient(Player player, GameClient newClient)
	{
		final GameClient oldClient = player.getClient();
		if (oldClient == null || oldClient == newClient)
			return;
		
		oldClient.setPlayer(null);
		oldClient.setDetached(true);
		oldClient.cleanMe(true);
	}
	
	private void detachOldClient(SafeDisconnectSession session, GameClient newClient)
	{
		final GameClient oldClient = session.client;
		if (oldClient == null || oldClient == newClient)
			return;
		
		oldClient.setPlayer(null);
		oldClient.setDetached(true);
		oldClient.cleanMe(true);
	}
	
	private boolean isExpectedDisconnect(Player player)
	{
		final Long timestamp = _expectedDisconnects.get(player.getObjectId());
		if (timestamp == null)
			return false;
		
		if (System.currentTimeMillis() - timestamp > EXPECTED_LOGOUT_GRACE_MS)
		{
			_expectedDisconnects.remove(player.getObjectId());
			return false;
		}
		
		return true;
	}
	
	private boolean canApplySafeDisconnect(Player player)
	{
		if (player.getPunishment().getType() != PunishmentType.NONE)
			return false;
		
		if (player.getAccessLevel().getLevel() < 0)
			return false;
		
		if (player.isInOlympiadMode() && !Config.SAFEDISCONNECT_ALLOW_OLYMPIAD)
			return false;
		
		if (player.isInsideZone(ZoneId.BOSS) && !Config.SAFEDISCONNECT_ALLOW_BOSS)
			return false;
		
		if (!Config.SAFEDISCONNECT_ALLOW_QUEST && !player.getQuestList().getAllQuests(false).isEmpty())
			return false;
		
		if (!Config.SAFEDISCONNECT_ALLOW_FARM && player.isInCombat())
			return false;
		
		return true;
	}
	
	private void onTimeout(SafeDisconnectSession session)
	{
		if (!_sessions.remove(session.playerId, session))
			return;
		
		Player player = session.client != null ? session.client.getPlayer() : null;
		if (player == null)
			player = ext.mods.gameserver.model.World.getInstance().getPlayer(session.playerId);
		
		if (player != null)
		{
			if (Config.SAFEDISCONNECT_INTEGRATION_DUNGEON && player.getDungeon() != null)
				player.getDungeon().resumeForReconnect(player);
			if (Config.SAFEDISCONNECT_INTEGRATION_TOURNAMENT && player.getTournamentBattle() != null)
				player.getTournamentBattle().resumeForReconnect(player);
			handleOlympiadTimeout(player);
			restorePlayerState(player, session);
			player.setOnlineStatus(false, true);
			player.deleteMe();
		}
		
		if (session.client != null)
		{
			session.client.setPlayer(null);
			session.client.setDetached(true);
			session.client.cleanMe(true);
		}
	}
	
	private class SafeDisconnectSession
	{
		private final int playerId;
		private final GameClient client;
		private final String originalTitle;
		private final int originalNameColor;
		private final int originalTitleColor;
		private final boolean wasInvul;
		private final boolean wasImmobilized;
		private ScheduledFuture<?> timeoutTask;
		
		private SafeDisconnectSession(Player player, GameClient client)
		{
			this.playerId = player.getObjectId();
			this.client = client;
			this.originalTitle = player.getTitle();
			this.originalNameColor = player.getAppearance().getNameColor();
			this.originalTitleColor = player.getAppearance().getTitleColor();
			this.wasImmobilized = player.isImmobilized();
			this.wasInvul = player.isInvul() && !player.isSpawnProtected();
		}
		
		private void scheduleTimeout()
		{
			if (Config.SAFEDISCONNECT_TIMEOUT_MS <= 0)
				return;
			
			timeoutTask = CoroutinePool.schedule(() -> CoroutinePool.executeParallel(() -> onTimeout(this)), Config.SAFEDISCONNECT_TIMEOUT_MS);
		}
		
		private void cancelTimeout()
		{
			if (timeoutTask == null)
				return;
			
			timeoutTask.cancel(false);
			timeoutTask = null;
		}
	}
	
	public static SafeDisconnectManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final SafeDisconnectManager INSTANCE = new SafeDisconnectManager();
	}
}
