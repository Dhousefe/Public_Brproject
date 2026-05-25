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

import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;

import ext.mods.commons.crypt.BCrypt;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.loginserver.crypt.ScrambledKeyPair;
import ext.mods.loginserver.data.manager.GameServerManager;
import ext.mods.loginserver.data.manager.IpBanManager;
import ext.mods.loginserver.data.sql.AccountTable;
import ext.mods.loginserver.enums.AccountKickedReason;
import ext.mods.loginserver.enums.LoginClientState;
import ext.mods.loginserver.model.Account;
import ext.mods.loginserver.model.GameServerInfo;
import ext.mods.loginserver.network.LoginClient;
import ext.mods.loginserver.network.SessionKey;
import ext.mods.loginserver.network.serverpackets.AccountKicked;
import ext.mods.loginserver.network.serverpackets.LoginFail;
import ext.mods.loginserver.network.serverpackets.LoginOk;
import ext.mods.loginserver.network.serverpackets.ServerList;

public class LoginController
{
	private static final CLogger LOGGER = new CLogger(LoginController.class.getName());
	
	public static final int LOGIN_TIMEOUT = 60 * 1000;
	
	private final Map<String, LoginClient> _clients = new ConcurrentHashMap<>();
	private final Map<InetAddress, Integer> _failedAttempts = new ConcurrentHashMap<>();
	
	protected ScrambledKeyPair[] _keyPairs;
	
	protected byte[][] _blowfishKeys;
	private static final int BLOWFISH_KEYS = 20;
	
	protected LoginController()
	{
		_keyPairs = new ScrambledKeyPair[10];
		
		try
		{
			final KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			final RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
			
			keygen.initialize(spec);
			
			for (int i = 0; i < 10; i++)
				_keyPairs[i] = new ScrambledKeyPair(keygen.generateKeyPair());
			
			LOGGER.info("Cached 10 KeyPairs for RSA communication.");
			
			final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, _keyPairs[0].getKeyPair().getPrivate());
			
			_blowfishKeys = new byte[BLOWFISH_KEYS][16];
			
			for (int i = 0; i < BLOWFISH_KEYS; i++)
			{
				for (int j = 0; j < _blowfishKeys[i].length; j++)
					_blowfishKeys[i][j] = (byte) (Rnd.get(255) + 1);
			}
			LOGGER.info("Stored {} keys for Blowfish communication.", _blowfishKeys.length);
		}
		catch (GeneralSecurityException gse)
		{
			LOGGER.error("Failed generating keys.", gse);
		}
		
		final Thread purge = new PurgeThread();
		purge.setDaemon(true);
		purge.start();
	}
	
	public byte[] getRandomBlowfishKey()
	{
		return Rnd.get(_blowfishKeys);
	}
	
	public void removeAuthedLoginClient(String account)
	{
		if (account == null)
			return;
		
		_clients.remove(account);
	}
	
	public LoginClient getAuthedClient(String account)
	{
		return _clients.get(account);
	}
	
	/**
	 * Update attempts counter. If the maximum amount is reached, it will end with a client ban.
	 * @param address : The {@link InetAddress} to test.
	 */
	private void recordFailedAttempt(InetAddress address)
	{
		final int attempts = _failedAttempts.merge(address, 1, (k, v) -> k + v);
		if (attempts >= Config.LOGIN_TRY_BEFORE_BAN)
		{
			IpBanManager.getInstance().addBanForAddress(address, Config.LOGIN_BLOCK_AFTER_BAN * 1000L);
			
			_failedAttempts.remove(address);
			
			LOGGER.info("IP address: {} has been banned due to too many login attempts.", address.getHostAddress());
		}
	}
	
	/**
	 * If passwords don't match, register the failed attempt and eventually ban the {@link InetAddress} if AUTO_CREATE_ACCOUNTS is off.
	 * @param client : The {@link LoginClient} to eventually ban after multiple failed attempts.
	 * @param login : The {@link String} login to test.
	 * @param password : The {@link String} password to test.
	 */
	public void retrieveAccountInfo(LoginClient client, String login, String password)
	{
		final InetAddress addr = client.getConnection().getInetAddress();
		final long currentTime = System.currentTimeMillis();
		
		Account account = AccountTable.getInstance().getAccount(login);
		
		if (account == null)
		{
			if (!Config.AUTO_CREATE_ACCOUNTS)
			{
				recordFailedAttempt(addr);
				client.close(LoginFail.REASON_USER_OR_PASS_WRONG);
				return;
			}
			
			account = AccountTable.getInstance().createAccount(login, BCrypt.hashPw(password), currentTime);
			if (account == null)
			{
				client.close(LoginFail.REASON_ACCESS_FAILED);
				return;
			}
			
			LOGGER.info("Auto created account '{}'.", login);
		}
		else
		{
			if (!BCrypt.checkPw(password, account.getPassword()))
			{
				recordFailedAttempt(addr);
				client.close(LoginFail.REASON_PASS_WRONG);
				return;
			}
			
			_failedAttempts.remove(addr);
			
			if (!AccountTable.getInstance().setAccountLastTime(login, currentTime))
			{
				client.close(LoginFail.REASON_ACCESS_FAILED);
				return;
			}
		}
		
		if (account.getAccessLevel() < 0)
		{
			client.close(new AccountKicked(AccountKickedReason.PERMANENTLY_BANNED));
			return;
		}
		
		final GameServerInfo gsi = getAccountOnGameServer(login);
		if (gsi != null)
		{
			client.close(LoginFail.REASON_ACCOUNT_IN_USE);
			
			if (gsi.isAuthed())
				gsi.getGameServerThread().kickPlayer(login);
			
			return;
		}
		
		if (_clients.putIfAbsent(login, client) != null)
		{
			final LoginClient oldClient = getAuthedClient(login);
			if (oldClient != null)
			{
				oldClient.close(LoginFail.REASON_ACCOUNT_IN_USE);
				removeAuthedLoginClient(login);
			}
			client.close(LoginFail.REASON_ACCOUNT_IN_USE);
			return;
		}
		
		account.setClientIp(addr);
		
		client.setAccount(account);
		client.setState(LoginClientState.AUTHED_LOGIN);
		client.setSessionKey(new SessionKey(Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt(), Rnd.nextInt()));
		client.sendPacket((Config.SHOW_LICENCE) ? new LoginOk(client.getSessionKey()) : new ServerList(account));
		
		if (Config.SHOW_CONNECT)
			LOGGER.info("Connected [ Account: " + account.getLogin() + " Ip: " + addr.getHostAddress() + " ]");
	}
	
	public SessionKey getKeyForAccount(String account)
	{
		final LoginClient client = _clients.get(account);
		return (client == null) ? null : client.getSessionKey();
	}
	
	public GameServerInfo getAccountOnGameServer(String account)
	{
		for (GameServerInfo gsi : GameServerManager.getInstance().getRegisteredGameServers().values())
		{
			final GameServerThread gst = gsi.getGameServerThread();
			if (gst != null && gst.hasAccountOnGameServer(account))
				return gsi;
		}
		return null;
	}
	
	/**
	 * @return One of the cached {@link ScrambledKeyPair}s to communicate with Login Clients.
	 */
	public ScrambledKeyPair getScrambledRSAKeyPair()
	{
		return Rnd.get(_keyPairs);
	}
	
	private class PurgeThread extends Thread
	{
		public PurgeThread()
		{
			setName("PurgeThread");
		}
		
		@Override
		public void run()
		{
			while (!isInterrupted())
			{
				for (LoginClient client : _clients.values())
				{
					if ((client.getConnectionStartTime() + LOGIN_TIMEOUT) < System.currentTimeMillis())
						client.close(LoginFail.REASON_ACCESS_FAILED);
				}
				
				try
				{
					Thread.sleep(LOGIN_TIMEOUT / 2);
				}
				catch (InterruptedException e)
				{
					return;
				}
			}
		}
	}
	
	
	public int getOnlinePlayerCount()
	{
		return _clients.size();
	}
	
	
	public static LoginController getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final LoginController INSTANCE = new LoginController();
	}
}