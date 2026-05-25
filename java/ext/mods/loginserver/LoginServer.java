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
package ext.mods.loginserver;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.LogManager;

import ext.mods.Config;
import ext.mods.commons.gui.InterfaceLS;
import ext.mods.commons.lang.StringUtil;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.mmocore.SelectorConfig;
import ext.mods.commons.mmocore.SelectorThread;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.util.JvmOptimizer;
import ext.mods.loginserver.data.manager.GameServerManager;
import ext.mods.loginserver.data.manager.IpBanManager;
import ext.mods.loginserver.data.sql.AccountTable;
import ext.mods.loginserver.data.xml.ProxyDataLoader;
import ext.mods.loginserver.network.LoginClient;
import ext.mods.loginserver.network.LoginPacketHandler;

public class LoginServer
{
	private static final CLogger LOGGER = new CLogger(LoginServer.class.getName());
	
	public static final int PROTOCOL_REV = 0x0102;
	
	private static LoginServer _loginServer;
	
	private GameServerListener _gameServerListener;
	private SelectorThread<LoginClient> _selectorThread;
	
	public static void main(String[] args) throws Exception
	{
		JvmOptimizer.initialize();
		
		_loginServer = new LoginServer();
	}
	
	/*
	 * Create directories for logs
	 */
	static void createDirectories()
	{
		try
		{
			createDirectory("log");
			createDirectory("log/console");
			createDirectory("log/error");
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to create directories.", e);
		}
	}
	
	/**
	 * Create a directory relative to the {@link ext.mods.Config#BASE_PATH base path}. Create last directory in the {@code path}.
	 * @param path the path to create
	 */
	static void createDirectory(String path)
	{
		var p = Config.BASE_PATH;
		
		if (Config.DEV_MODE)
			p = p.resolve("dev").resolve("login");
		
		p.resolve(path).toFile().mkdir();
	}
	
	public LoginServer() throws Exception
	{
		if (Config.DEV_MODE)
			Config.BASE_PATH.resolve("dev").resolve("login").toFile().mkdirs();
		
		createDirectories();
		
		File logFile = Config.CONFIG_PATH.resolve("logging.properties").toFile();
		if (logFile.exists())
		{
			try (InputStream is = new FileInputStream(logFile))
			{
				LogManager.getLogManager().updateConfiguration(is, (key) ->
				{
					return (oldValue, newValue) ->
					{
						if (key.endsWith(".pattern"))
						{
							if (Config.DEV_MODE)
								return Config.BASE_PATH.resolve("dev").resolve("login").resolve(newValue).toString();
							else
								return Config.BASE_PATH.resolve(newValue).toString();
						}
						return newValue;
					};
				});
			}
			catch (Exception e)
			{
				System.err.println("Failed to load logging.properties. Using default logging.");
			}
		}
		else
		{
			System.out.println("Logging config not found at: " + logFile.getAbsolutePath());
		}

		
		
		StringUtil.printSection("Config");
		Config.loadLoginServer();
		
		StringUtil.printSection("Poolers");
		ConnectionPool.init();
		
		AccountTable.getInstance();

		String os = System.getProperty("os.name").toLowerCase();
		if ((os.contains("win") || os.contains("mac")) && !GraphicsEnvironment.isHeadless())
		{
			try 
			{
				System.out.println("Login: Running in Interface GUI (Windows).");
				new InterfaceLS();
			}
			catch (Throwable t)
			{
				System.err.println("Failed to initialize InterfaceLS GUI: " + t.getMessage());
				System.out.println("Login: Fallback to console mode.");
			}
		}
		else
		{
			System.out.println("Login: Running in console mode (" + os + ").");
		}
		
		StringUtil.printSection("LoginController");
		LoginController.getInstance();
		
		StringUtil.printSection("GameServerManager");
		GameServerManager.getInstance();
		
		
		
		if (Config.PROXY)
		{
			StringUtil.printSection("Proxy");
			ProxyDataLoader.getInstance().load();
		}
		
		StringUtil.printSection("Ban List");
		IpBanManager.getInstance();
		
		StringUtil.printSection("IP, Ports & Socket infos");
		InetAddress bindAddress = null;
		if (!Config.LOGINSERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.LOGINSERVER_HOSTNAME);
			}
			catch (UnknownHostException uhe)
			{
				LOGGER.error("The LoginServer bind address is invalid, using all available IPs.", uhe);
			}
		}
		
		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		final LoginPacketHandler lph = new LoginPacketHandler();
		final SelectorHelper sh = new SelectorHelper();
		try
		{
			_selectorThread = new SelectorThread<>(sc, sh, lph, sh, sh);
		}
		catch (IOException ioe)
		{
			LOGGER.error("Failed to open selector.", ioe);
			
			System.exit(1);
		}
		
		try
		{
			_gameServerListener = new GameServerListener();
			_gameServerListener.start();
			
			LOGGER.info("Listening for gameservers on {}:{}.", Config.GAMESERVER_LOGIN_HOSTNAME, Config.GAMESERVER_LOGIN_PORT);
		}
		catch (IOException ioe)
		{
			LOGGER.error("Failed to start the gameserver listener.", ioe);
			
			System.exit(1);
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.LOGINSERVER_PORT);
		}
		catch (IOException ioe)
		{
			LOGGER.error("Failed to open server socket.", ioe);
			
			System.exit(1);
		}
		_selectorThread.start();
		LOGGER.info("Loginserver ready on {}:{}.", (bindAddress == null) ? "*" : bindAddress.getHostAddress(), Config.LOGINSERVER_PORT);
		
		StringUtil.printSection("Waiting for gameserver answer");
	}
	
	public static LoginServer getInstance()
	{
		return _loginServer;
	}
	
	public GameServerListener getGameServerListener()
	{
		return _gameServerListener;
	}
	
	public void shutdown(boolean restart)
	{
		Runtime.getRuntime().exit(restart ? 2 : 0);
	}
}