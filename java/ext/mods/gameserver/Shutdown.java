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
package ext.mods.gameserver;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import ext.mods.Config;
import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.network.ServerType;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.util.JvmOptimizer;
import ext.mods.extensions.listener.manager.GameListenerManager;
import ext.mods.fakeplayer.FakePlayer;
import ext.mods.fakeplayer.FakePlayerManager;
import ext.mods.gameserver.data.manager.BufferManager;
import ext.mods.gameserver.data.manager.CastleManorManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.data.manager.CoupleManager;
import ext.mods.gameserver.data.manager.FestivalOfDarknessManager;
import ext.mods.gameserver.data.manager.FishingChampionshipManager;
import ext.mods.gameserver.data.manager.HeroManager;
import ext.mods.gameserver.data.manager.PetitionManager;
import ext.mods.gameserver.data.manager.RelationManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.sql.OfflineTradersTable;
import ext.mods.gameserver.data.sql.ServerMemoTable;
import ext.mods.gameserver.data.xml.ScriptData;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.olympiad.Olympiad;
import ext.mods.gameserver.network.GameClient;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.gameserverpackets.ServerStatus;
import ext.mods.gameserver.network.serverpackets.ServerClose;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.taskmanager.ItemInstanceTaskManager;
import ext.mods.gameserver.taskmanager.ItemsOnGroundTaskManager;

/**
 * This class provides functions for shutting down and restarting the server. It closes all client connections and saves data.
 */
public class Shutdown extends Thread
{
	private static final CLogger LOGGER = new CLogger(Shutdown.class.getName());
	
	private static Shutdown _counterInstance = null;
	
	private int _secondsShut;
	private int _shutdownMode;
	
	public static final int SIGTERM = 0;
	public static final int GM_SHUTDOWN = 1;
	public static final int GM_RESTART = 2;
	public static final int ABORT = 3;
	private static final String[] MODE_TEXT =
	{
		"SIGTERM",
		"shutting down",
		"restarting",
		"aborting"
	};
	
	protected Shutdown()
	{
		_secondsShut = -1;
		_shutdownMode = SIGTERM;
	}
	
	public Shutdown(int seconds, boolean restart)
	{
		_secondsShut = Math.max(0, seconds);
		_shutdownMode = (restart) ? GM_RESTART : GM_SHUTDOWN;
	}
	
	/**
	 * This function is called, when a new thread starts if this thread is the thread of getInstance, then this is the shutdown hook and we save all data and disconnect all clients.<br>
	 * <br>
	 * After this thread ends, the server will completely exit if this is not the thread of getInstance, then this is a countdown thread.<br>
	 * <br>
	 * We start the countdown, and when we finished it, and it was not aborted, we tell the shutdown-hook why we call exit, and then call exit when the exit status of the server is 1, startServer.sh / startServer.bat will restart the server.
	 */
	@Override
	public void run()
	{
		if (this == SingletonHolder.INSTANCE)
		{
			String os = System.getProperty("os.name").toLowerCase();
			if ((os.contains("win") || os.contains("mac")))
				playServerLoadedSound();
			
			GameListenerManager.getInstance().notifyShutdown();
			StringUtil.printSection("Under " + MODE_TEXT[_shutdownMode] + " process");
			
			try
			{
				if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
					OfflineTradersTable.getInstance().store();
			}
			catch (Throwable t)
			{
			}
			
			try
			{
				disconnectAllPlayers();
				LOGGER.info("Players have been disconnected.");
			}
			catch (Exception e)
			{
			}
			
			try
			{
				LoginServerThread.getInstance().interrupt();
			}
			catch (Exception e)
			{
			}
			
			if (!SevenSignsManager.getInstance().isSealValidationPeriod())
			{
				FestivalOfDarknessManager.getInstance().saveFestivalData(false);
				LOGGER.info("FestivalOfDarknessManager has been saved.");
			}
			
			SevenSignsManager.getInstance().saveSevenSignsData();
			SevenSignsManager.getInstance().saveSevenSignsStatus();
			LOGGER.info("SevenSignsManager has been saved.");
			
			ScriptData.getInstance().stopAllScripts();
			LOGGER.info("Running scripts have been stopped.");
			
			ZoneManager.getInstance().save();
			LOGGER.info("ZoneManager has been saved.");
			
			SpawnManager.getInstance().save();
			LOGGER.info("SpawnManager has been saved.");
			
			if (Config.OLY_ENABLED)
			{
				Olympiad.getInstance().saveOlympiadStatus();
				LOGGER.info("Olympiad has been saved.");
			}
			
			HeroManager.getInstance().shutdown();
			LOGGER.info("HeroManager has been saved.");
			
			CastleManorManager.getInstance().storeMe();
			LOGGER.info("CastleManorManager has been saved.");
			
			FishingChampionshipManager.getInstance().shutdown();
			LOGGER.info("FishingChampionshipManager has been saved.");
			
			BufferManager.getInstance().saveSchemes();
			LOGGER.info("BufferManager has been saved.");
			
			PetitionManager.getInstance().store();
			LOGGER.info("PetitionManager has been saved.");
			
			ClanHallManager.getInstance().save();
			LOGGER.info("ClanHallManager has been saved.");
			
			RelationManager.getInstance().save();
			LOGGER.info("RelationManager has been saved.");
			
			CoupleManager.getInstance().save();
			LOGGER.info("CoupleManager has been saved.");
			
			ItemInstanceTaskManager.getInstance().save();
			LOGGER.info("ItemInstanceTaskManager has been saved.");
			
			ItemsOnGroundTaskManager.getInstance().save();
			LOGGER.info("ItemsOnGroundTaskManager has been saved.");
			
			ServerMemoTable.getInstance().set("server_crash", false);
			
			try
			{
				GameServer.getInstance().getSelectorThread().shutdown();
			}
			catch (Exception e)
			{
			}
			
			try
			{
				ext.mods.gameserver.geoengine.pathfinding.PathfinderCache.getInstance().shutdown();
				LOGGER.info("PathfinderCache has been shut down.");
			}
			catch (Exception e)
			{
			}
			
			ThreadPool.shutdown();
			LOGGER.info("ThreadPool has been shut down.");
			
			try
			{
				ConnectionPool.shutdown();
			}
			catch (Exception e)
			{
			}
			
			if (SingletonHolder.INSTANCE._shutdownMode == GM_SHUTDOWN)
			{
				JvmOptimizer.setNormalShutdown();
			}
			
			Runtime.getRuntime().halt((SingletonHolder.INSTANCE._shutdownMode == GM_RESTART) ? 2 : 0);
		}
		else
		{
			countdown();
			
			switch (_shutdownMode)
			{
				case GM_SHUTDOWN:
					JvmOptimizer.setNormalShutdown();
					SingletonHolder.INSTANCE.setMode(GM_SHUTDOWN);
					SingletonHolder.INSTANCE.run();
					System.exit(0);
					break;
				
				case GM_RESTART:
					SingletonHolder.INSTANCE.setMode(GM_RESTART);
					SingletonHolder.INSTANCE.run();
					System.exit(2);
					break;
			}
		}
	}
	
	/**
	 * This functions starts a shutdown countdown.
	 * @param player : The {@link Player} who issued the shutdown command.
	 * @param seconds : The number of seconds until shutdown.
	 * @param restart : If true, the server will restart after shutdown.
	 */
	public void startShutdown(Player player, int seconds, boolean restart)
	{
		_shutdownMode = (restart) ? GM_RESTART : GM_SHUTDOWN;
		
		if (player != null)
			LOGGER.info("GM: {} issued {} process in {} seconds.", player.toString(), MODE_TEXT[_shutdownMode], seconds);
		
		if (_shutdownMode > 0)
		{
			switch (seconds)
			{
				case 540, 480, 420, 360, 300, 240, 180, 120, 60, 30, 10, 5, 4, 3, 2, 1:
					break;
				
				default:
					sendServerQuit(seconds);
			}
		}
		
		if (_counterInstance != null)
			_counterInstance.setMode(ABORT);
		
		_counterInstance = new Shutdown(seconds, restart);
		_counterInstance.start();
	}
	
	/**
	 * This function aborts a running countdown.
	 * @param player : The {@link Player} who issued the abort process.
	 */
	public void abort(Player player)
	{
		if (_counterInstance != null)
		{
			LOGGER.info("GM: {} aborted {} process.", player.toString(), MODE_TEXT[_shutdownMode]);
			_counterInstance.setMode(ABORT);
			
			World.announceToOnlinePlayers("Server aborted " + MODE_TEXT[_shutdownMode] + " process and continues normal operation.");
		}
	}
	
	/**
	 * Set the shutdown mode.
	 * @param mode : what mode shall be set.
	 */
	private void setMode(int mode)
	{
		_shutdownMode = mode;
	}
	
	/**
	 * Report the current countdown to all players. Flag the server as "down" when reaching 60sec. Rehabilitate the server status if ABORT {@link ServerStatus} is seen.
	 */
	private void countdown()
	{
		try
		{
			while (_secondsShut > 0)
			{
				if (_shutdownMode == ABORT)
				{
					if (LoginServerThread.getInstance().getServerType() == ServerType.DOWN)
						LoginServerThread.getInstance().setServerType((Config.SERVER_GMONLY) ? ServerType.GM_ONLY : ServerType.AUTO);
					
					break;
				}
				
				switch (_secondsShut)
				{
					case 540, 480, 420, 360, 300, 240, 180, 120, 60, 30, 10, 5, 4, 3, 2, 1:
						sendServerQuit(_secondsShut);
						break;
				}
				
				if (_secondsShut <= 60 && LoginServerThread.getInstance().getServerType() != ServerType.DOWN)
					LoginServerThread.getInstance().setServerType(ServerType.DOWN);
				
				_secondsShut--;
				
				Thread.sleep(1000);
			}
		}
		catch (InterruptedException ie)
		{
		}
	}
	
	private static void sendServerQuit(int seconds)
	{
		World.toAllOnlinePlayers(SystemMessage.getSystemMessage(SystemMessageId.THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECONDS).addNumber(seconds));
	}
	
	/**
	 * Disconnect all {@link Player}s from the server.
	 */
	private static void disconnectAllPlayers()
	{
		
		for (FakePlayer fake : FakePlayerManager.getInstance().getFakePlayers())
		{
			fake.despawnPlayer();
		}
		for (Player player : World.getInstance().getPlayers())
		{
			final GameClient client = player.getClient();
			if (client != null && !client.isDetached())
			{
				client.close(ServerClose.STATIC_PACKET);
				client.setPlayer(null);
				
				player.setClient(null);
			}
			player.deleteMe();
		}
	}
	
	public void playServerLoadedSound()
	{
		try
		{
			File soundFile = new File("../sound/Shutdown.wav");
			if (!soundFile.exists())
			{
				System.err.println("⚠ Arquivo de som não encontrado: " + soundFile.getAbsolutePath());
				return;
			}
			
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
			
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.start();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static Shutdown getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final Shutdown INSTANCE = new Shutdown();
	}
}