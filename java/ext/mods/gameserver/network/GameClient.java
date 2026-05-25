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
package ext.mods.gameserver.network;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReentrantLock;

import ext.mods.Config;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.mmocore.MMOClient;
import ext.mods.commons.mmocore.MMOConnection;
import ext.mods.commons.mmocore.ReceivablePacket;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.Safedisconect.SafeDisconnectManager;
import ext.mods.extensions.listener.manager.GameListenerManager;
import ext.mods.gameserver.LoginServerThread;
import ext.mods.gameserver.data.manager.AntiFeedManager;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.data.sql.OfflineTradersTable;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.enums.FloodProtector;
import ext.mods.gameserver.enums.MessageType;
import ext.mods.gameserver.model.CharSelectSlot;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.ServerClose;
import ext.mods.protection.hwid.hwid;

/**
 * Represents a client connected on Game Server.<br>
 * <br>
 * It is linked to a {@link Player} and hold account informations (flood protectors, connection time, etc).
 */
public final class GameClient extends MMOClient<MMOConnection<GameClient>> implements Runnable
{
	private static final CLogger LOGGER = new CLogger(GameClient.class.getName());
	
	private static final String SELECT_CLAN = "SELECT clanId FROM characters WHERE obj_id=?";
	private static final String UPDATE_DELETE_TIME = "UPDATE characters SET deletetime=? WHERE obj_id=?";
	
	private static final String DELETE_CHAR_HENNAS = "DELETE FROM character_hennas WHERE char_obj_id=?";
	private static final String DELETE_CHAR_MACROS = "DELETE FROM character_macroses WHERE char_obj_id=?";
	private static final String DELETE_CHAR_MEMOS = "DELETE FROM character_memo WHERE charId=?";
	private static final String DELETE_CHAR_QUESTS = "DELETE FROM character_quests WHERE charId=?";
	private static final String DELETE_CHAR_RECIPES = "DELETE FROM character_recipebook WHERE charId=?";
	private static final String DELETE_CHAR_RELATIONS = "DELETE FROM character_relations WHERE char_id=? OR friend_id=?";
	private static final String DELETE_CHAR_SHORTCUTS = "DELETE FROM character_shortcuts WHERE char_obj_id=?";
	private static final String DELETE_CHAR_SKILLS = "DELETE FROM character_skills WHERE char_obj_id=?";
	private static final String DELETE_CHAR_SKILLS_SAVE = "DELETE FROM character_skills_save WHERE char_obj_id=?";
	private static final String DELETE_CHAR_SUBCLASSES = "DELETE FROM character_subclasses WHERE char_obj_id=?";
	private static final String DELETE_CHAR_HERO = "DELETE FROM heroes WHERE char_id=?";
	private static final String DELETE_CHAR_NOBLE = "DELETE FROM olympiad_nobles WHERE char_id=?";
	private static final String DELETE_CHAR_SEVEN_SIGNS = "DELETE FROM seven_signs WHERE char_obj_id=?";
	private static final String DELETE_CHAR_PETS = "DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=?)";
	private static final String DELETE_CHAR_AUGMENTS = "DELETE FROM augmentations WHERE item_oid IN (SELECT object_id FROM items WHERE items.owner_id=?)";
	private static final String DELETE_CHAR_ITEMS = "DELETE FROM items WHERE owner_id=?";
	private static final String DELETE_CHAR_RBP = "DELETE FROM character_raid_points WHERE char_id=?";
	private static final String DELETE_CHAR = "DELETE FROM characters WHERE obj_Id=?";
	private static final String DELETE_CHAR_CACHE = "DELETE FROM character_data WHERE charId=?";
	
	public enum GameClientState
	{
		CONNECTED,
		AUTHED,
		ENTERING,
		IN_GAME
	}
	
	private final long[] _floodProtectors = new long[FloodProtector.VALUES_LENGTH];
	private final ArrayBlockingQueue<ReceivablePacket<GameClient>> _packetQueue;
	private final ReentrantLock _queueLock = new ReentrantLock();
	private final ReentrantLock _activeCharLock = new ReentrantLock();
	
	private final GameCrypt _crypt;
	private final ClientStats _stats;
	private final long _connectionStartTime;
	
	private GameClientState _state;
	private String _accountName;
	private SessionKey _sessionId;
	private Player _player;
	private boolean _isDetached;
	@SuppressWarnings("unused")
	private boolean _isAuthedGG;
	
	private CharSelectSlot[] _slots;
	
	protected final ScheduledFuture<?> _autoSaveInDB;
	protected ScheduledFuture<?> _cleanupTask;
	
	private String _realIpAddress;
	
	public GameClient(MMOConnection<GameClient> con)
	{
		super(con);
		
		_state = GameClientState.CONNECTED;
		_connectionStartTime = System.currentTimeMillis();
		_crypt = new GameCrypt();
		_stats = new ClientStats();
		_packetQueue = new ArrayBlockingQueue<>(Config.CLIENT_PACKET_QUEUE_SIZE);
		
		_autoSaveInDB = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (getPlayer() != null && getPlayer().isOnline())
			{
				getPlayer().store();
				
				if (getPlayer().getSummon() != null)
					getPlayer().getSummon().store();
			}
		}, 300000L, 900000L);
	}
	
	@Override
	public void run()
	{
		if (!_queueLock.tryLock())
			return;
		
		try
		{
			int count = 0;
			ReceivablePacket<GameClient> packet;
			while (true)
			{
				packet = _packetQueue.poll();
				if (packet == null)
					return;
				
				if (_isDetached)
				{
					_packetQueue.clear();
					return;
				}
				
				try
				{
					packet.run();
				}
				catch (Exception e)
				{
					LOGGER.error("Execution failed on {} for {}.", e, packet.getClass().getSimpleName(), toString());
				}
				
				count++;
				if (getStats().countBurst(count))
					return;
			}
		}
		finally
		{
			_queueLock.unlock();
		}
	}
	
	@Override
	public String toString()
	{
		try
		{
			final InetAddress address = getConnection().getInetAddress();
			switch (getState())
			{
				case CONNECTED:
					return "[IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				
				case AUTHED:
					return "[Account: " + getAccountName() + " - IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				
				case ENTERING, IN_GAME:
					return "[Character: " + (getPlayer() == null ? "disconnected" : getPlayer().getName()) + " - Account: " + getAccountName() + " - IP: " + (address == null ? "disconnected" : address.getHostAddress()) + "]";
				
				default:
					throw new IllegalStateException("Missing state on switch");
			}
		}
		catch (NullPointerException e)
		{
			return "[Character read failed due to disconnect]";
		}
	}
	
	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		_crypt.decrypt(buf.array(), buf.position(), size);
		return true;
	}
	
	@Override
	public boolean encrypt(final ByteBuffer buf, final int size)
	{
		_crypt.encrypt(buf.array(), buf.position(), size);
		buf.position(buf.position() + size);
		return true;
	}
	
	@Override
	protected void onDisconnection()
	{
		try
		{
			ThreadPool.execute(() ->
			{
				boolean fast = true;
				final Player player = getPlayer();
				if (player != null && !isDetached())
				{
					setDetached(true);
					
					if (CTFEvent.getInstance().isPlayerParticipant(player.getObjectId()) && CTFEvent.getInstance().isStarted())
						CTFEvent.getInstance().onLogout(player);
					
					if (DMEvent.getInstance().isPlayerParticipant(player.getObjectId()) && DMEvent.getInstance().isStarted())
						DMEvent.getInstance().onLogout(player);
					
					if (LMEvent.getInstance().isPlayerParticipant(player.getObjectId()) && LMEvent.getInstance().isStarted())
						LMEvent.getInstance().onLogout(player);
					
					if (TvTEvent.getInstance().isPlayerParticipant(player.getObjectId()) && TvTEvent.getInstance().isStarted())
						TvTEvent.getInstance().onLogout(player);
					
					if (OfflineTradersTable.offlineMode(player))
					{
						if (player.getParty() != null)
							player.getParty().removePartyMember(player, MessageType.EXPELLED);
						
						OlympiadManager.getInstance().unRegisterNoble(player);
						AntiFeedManager.getInstance().onDisconnect(GameClient.this);
						
						final Summon summon = player.getSummon();
						if (summon != null)
						{
							summon.doRevive();
							summon.unSummon(getPlayer());
						}
						
						if (Config.OFFLINE_SLEEP_EFFECT)
						{
							player.startAbnormalEffect(Integer.decode("0x80"));
							player.broadcastUserInfo();
						}
						
						if (player.getClan() != null)
							player.getClan().broadcastClanStatus();
						
						if (player.getOfflineStartTime() == 0)
							player.setOfflineStartTime(System.currentTimeMillis());
						
						return;
					}
					
					if (SafeDisconnectManager.getInstance().handleDisconnect(GameClient.this))
						return;
					
					fast = !player.isInCombat() && !player.isLocked();
				}
				
				cleanMe(fast);
			});
		}
		catch (RejectedExecutionException e)
		{
		}
	}
	
	@Override
	protected void onForcedDisconnection()
	{
		LOGGER.debug("{} disconnected abnormally.", toString());
	}
	
	public byte[] enableCrypt()
	{
		byte[] key = BlowFishKeygen.getRandomKey();
		_crypt.setKey(key);
		if (hwid.isProtectionOn())
		{
			key = hwid.getKey(key);
		}

		return key;
	}
	
	public GameClientState getState()
	{
		return _state;
	}
	
	public void setState(GameClientState pState)
	{
		if (_state != pState)
		{
			_state = pState;
			_packetQueue.clear();
		}
	}
	
	public ClientStats getStats()
	{
		return _stats;
	}
	
	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}
	
	public Player getPlayer()
	{
		return _player;
	}
	
	public void setPlayer(Player player)
	{
		_player = player;
	}
	
	public ReentrantLock getActiveCharLock()
	{
		return _activeCharLock;
	}
	
	public long[] getFloodProtectors()
	{
		return _floodProtectors;
	}
	
	public void setGameGuardOk(boolean val)
	{
		_isAuthedGG = val;
	}
	
	public void setAccountName(String pAccountName)
	{
		_accountName = pAccountName;
	}
	
	public String getAccountName()
	{
		return _accountName;
	}
	
	public void setSessionId(SessionKey sk)
	{
		_sessionId = sk;
	}
	
	public SessionKey getSessionId()
	{
		return _sessionId;
	}
	
	public String getRealIpAddress()
	{
		return _realIpAddress;
	}
	
	public void setRealIpAddress(String realIpAddress)
	{
		_realIpAddress = realIpAddress;
	}
	
	public void sendPacket(L2GameServerPacket gsp)
	{
		if (_isDetached)
			return;
		
		getConnection().sendPacket(gsp);
		gsp.runImpl();
	}
	
	public boolean isDetached()
	{
		return _isDetached;
	}
	
	public void setDetached(boolean b)
	{
		_isDetached = b;
	}
	
	/**
	 * Method to handle character deletion
	 * @param slot The slot to check.
	 * @return a byte:
	 *         <li>-1: Error: No char was found for such charslot, caught exception, etc...
	 *         <li>0: character is not member of any clan, proceed with deletion
	 *         <li>1: character is member of a clan, but not clan leader
	 *         <li>2: character is clan leader
	 */
	public byte markToDeleteChar(int slot)
	{
		final int objectId = getObjectIdForSlot(slot);
		if (objectId < 0)
			return -1;
		
		byte answer = 0;
		
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(SELECT_CLAN))
			{
				ps.setInt(1, objectId);
				
				try (ResultSet rs = ps.executeQuery())
				{
					rs.next();
					
					final int clanId = rs.getInt(1);
					if (clanId != 0)
					{
						final Clan clan = ClanTable.getInstance().getClan(clanId);
						if (clan == null)
							answer = 0;
						else if (clan.getLeaderId() == objectId)
							answer = 2;
						else
							answer = 1;
					}
				}
			}
			
			if (answer == 0)
			{
				if (Config.DELETE_DAYS == 0)
					deleteCharByObjId(objectId);
				else
				{
					try (PreparedStatement ps = con.prepareStatement(UPDATE_DELETE_TIME))
					{
						ps.setLong(1, System.currentTimeMillis() + Config.DELETE_DAYS * 86400000L);
						ps.setInt(2, objectId);
						ps.execute();
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't mark as delete a player.", e);
			return -1;
		}
		return answer;
	}
	
	public void markRestoredChar(int slot)
	{
		final int objectId = getObjectIdForSlot(slot);
		if (objectId < 0)
			return;
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_DELETE_TIME))
		{
			ps.setLong(1, 0);
			ps.setInt(2, objectId);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't restore player.", e);
		}
	}
	
	public static void deleteCharByObjId(int objectId)
	{
		if (objectId < 0)
			return;
		
		PlayerInfoTable.getInstance().removePlayer(objectId);
		GameListenerManager.getInstance().notifyCharacterDelete(objectId);
		
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_HENNAS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_MACROS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_MEMOS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_QUESTS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_RECIPES))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_RELATIONS))
			{
				ps.setInt(1, objectId);
				ps.setInt(2, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_SHORTCUTS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_SKILLS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_SKILLS_SAVE))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_SUBCLASSES))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_HERO))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_NOBLE))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_SEVEN_SIGNS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_PETS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_AUGMENTS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_ITEMS))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_RBP))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
			
			try (PreparedStatement ps = con.prepareStatement(DELETE_CHAR_CACHE))
			{
				ps.setInt(1, objectId);
				ps.execute();
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete player.", e);
		}
	}
	
	public Player loadCharFromDisk(int slot)
	{
		final int objectId = getObjectIdForSlot(slot);
		if (objectId < 0)
			return null;
		
		final Player player = World.getInstance().getPlayer(objectId);
		if (player == null)
			return Player.restore(objectId);
		
		if (SafeDisconnectManager.getInstance().prepareReconnect(player, this))
			return player;
		
		if (SafeDisconnectManager.getInstance().isSafeDisconnectActive(player))
		{
			LOGGER.info("SafeDisconnect active for {}, skipping detach/delete during reconnect.", player.getName());
			return player;
		}
		
		if (player.getClient() != null)
			player.getClient().closeNow();
		else
			player.deleteMe();
		
		return null;
	}
	
	/**
	 * Get a {@link CharSelectSlot} based on its id. Integrity checks are included.
	 * @param id : The slot id to call.
	 * @return the associated slot informations based on slot id.
	 */
	public CharSelectSlot getCharSelectSlot(int id)
	{
		if (_slots == null || id < 0 || id >= _slots.length)
			return null;
		
		return _slots[id];
	}
	
	/**
	 * Set the character selection slots.
	 * @param list : Use the List as character slots.
	 */
	public void setCharSelectSlot(CharSelectSlot[] list)
	{
		_slots = list;
	}
	
	public void close(L2GameServerPacket gsp)
	{
		if (getConnection() == null)
			return;
		
		getConnection().close(gsp);
		hwid.removePlayer(null);
	}
	
	/**
	 * @param slot : The slot to test.
	 * @return the objectId of the character associated to that slot, or -1 if not found.
	 */
	private int getObjectIdForSlot(int slot)
	{
		final CharSelectSlot info = getCharSelectSlot(slot);
		return (info == null) ? -1 : info.getObjectId();
	}
	
	/**
	 * Close client connection with {@link ServerClose} packet
	 */
	public synchronized void closeNow()
	{
		_isDetached = true;
		
		close(ServerClose.STATIC_PACKET);
		
		if (_cleanupTask != null)
		{
			_cleanupTask.cancel(true);
			_cleanupTask = null;
		}
		
		ThreadPool.schedule(new CleanupTask(), 0);
	}
	
	public synchronized void cleanMe(boolean fast)
	{
		if (_cleanupTask == null)
			_cleanupTask = ThreadPool.schedule(new CleanupTask(), fast ? 100 : 15000);
	}
	
	protected class CleanupTask implements Runnable
	{
		@Override
		public void run()
		{
			if (_autoSaveInDB != null)
				_autoSaveInDB.cancel(true);
			
			if (getPlayer() != null)
			{
				getPlayer().setClient(null);
				
				if (getPlayer().isOnline())
					getPlayer().deleteMe();
				
				AntiFeedManager.getInstance().onDisconnect(GameClient.this);
			}
			setPlayer(null);
			
			LoginServerThread.getInstance().sendLogout(getAccountName());
		}
	}
	
	/**
	 * @return false if client can receive packets. True if detached, or flood detected, or queue overflow detected and queue still not empty.
	 */
	public boolean dropPacket()
	{
		if (_isDetached)
			return true;
		
		if (getStats().countPacket(_packetQueue.size()))
		{
			sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		return getStats().dropPacket();
	}
	
	/**
	 * Counts buffer underflow exceptions.
	 */
	public void onBufferUnderflow()
	{
		if (_state == GameClientState.CONNECTED)
		{
			if (Config.PACKET_HANDLER_DEBUG)
				LOGGER.warn("{} has been disconnected: too many buffer underflows in non-authed state.", toString());
			
			closeNow();
		}
		else if (getStats().countUnderflowException())
		{
			LOGGER.warn("{} has been disconnected: too many buffer underflows.", toString());
			closeNow();
		}
	}
	
	/**
	 * Counts unknown packets
	 */
	public void onUnknownPacket()
	{
		if (_state == GameClientState.CONNECTED)
		{
			if (Config.PACKET_HANDLER_DEBUG)
				LOGGER.warn("{} has been disconnected: too many unknown packets in non-authed state.", toString());
			
			closeNow();
		}
		else if (getStats().countUnknownPacket())
		{
			LOGGER.warn("{} has been disconnected: too many unknown packets.", toString());
			closeNow();
		}
	}
	
	/**
	 * Add packet to the queue and start worker thread if needed
	 * @param packet The packet to execute.
	 */
	public void execute(ReceivablePacket<GameClient> packet)
	{
		if (getStats().countFloods())
		{
			LOGGER.warn("{} has been disconnected: too many floods ({} long and {} short).", toString(), getStats().longFloods, getStats().shortFloods);
			closeNow();
			return;
		}
		
		if (!_packetQueue.offer(packet))
		{
			if (getStats().countQueueOverflow())
			{
				LOGGER.warn("{} has been disconnected: too many queue overflows.", toString());
				closeNow();
			}
			else
				sendPacket(ActionFailed.STATIC_PACKET);
			
			return;
		}
		
		if (_queueLock.isLocked())
			return;
		
		try
		{
			if (_state == GameClientState.CONNECTED && getStats().processedPackets > 3)
			{
				if (Config.PACKET_HANDLER_DEBUG)
					LOGGER.warn("{} has been disconnected: too many packets in non-authed state.", toString());
				
				closeNow();
				return;
			}
			
			ThreadPool.execute(this);
		}
		catch (RejectedExecutionException e)
		{
		}
	}
	
	/**
	 * Try to perform an action according to client FPs value. A 0 reuse delay means the action is always possible.
	 * @param fp : The {@link FloodProtector} to track.
	 * @return True if the action is possible, False otherwise.
	 */
	public boolean performAction(FloodProtector fp)
	{
		final int reuseDelay = fp.getReuseDelay();
		if (reuseDelay == 0)
			return true;
		
		final long currentTime = System.nanoTime();
		final long[] value = _floodProtectors;
		
		synchronized (value)
		{
			if (value[fp.getId()] > currentTime)
				return false;
			
			value[fp.getId()] = currentTime + reuseDelay * 1000000L;
			return true;
		}
	}
	
	public void spawnOffline(Player player)
	{
		player.isRunning();
		player.sitDown();
		player.setOnlineStatus(true, false);
		
		World.getInstance().addPlayer(player);
		
		setDetached(true);
		player.setClient(this);
		setPlayer(player);
		setAccountName(player.getAccountName());
		player.setOnlineStatus(true, true);
		setState(GameClientState.IN_GAME);
		player.spawnMe();
		
		LoginServerThread.getInstance().addClient(player.getAccountName(), this);
	}
	
	private String _playerName;
	private String _loginName;
	private int _playerId;
	private int _count;
	private String _hwid;
	private int _revision;

	public final String getPlayerName()
	{
		return _playerName;
	}

	public void setPlayerName(String name)
	{
		_playerName = name;
	}

	public void setPlayerId(int plId)
	{
		_playerId = plId;
	}

	public int getPlayerId()
	{
		return _playerId;
	}

	 public int getCount()
	 {
	     return _count;
	 }
	 
	 public void setCount(int count)
	 {
	     _count = count;
	 }

	public final String getHWID()
	{
		return _hwid;
	}

	public void setHWID(String hwid)
	{
		_hwid = hwid;
	}

	public void setRevision(int revision)
	{
		_revision = revision;
	}

	public int getRevision()
	{
		return _revision;
	}

	public final String getLoginName()
	{
		return _loginName;
	}

	public void setLoginName(String name)
	{
		_loginName = name;
	}

}