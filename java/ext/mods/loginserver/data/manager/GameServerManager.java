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
package ext.mods.loginserver.data.manager;

import java.math.BigInteger;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.logging.CLogger;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.loginserver.model.GameServerInfo;

import org.w3c.dom.Document;

public class GameServerManager implements IXmlReader
{
	private static final CLogger LOGGER = new CLogger(GameServerManager.class.getName());
	
	private static final int KEYS_SIZE = 10;
	
	private static final String LOAD_SERVERS = "SELECT * FROM gameservers";
	private static final String ADD_SERVER = "INSERT INTO gameservers (hexid,server_id,host) values (?,?,?)";
	
	private final Map<Integer, String> _serverNames = new HashMap<>();
	private final Map<Integer, GameServerInfo> _registeredServers = new ConcurrentHashMap<>();
	
	private KeyPair[] _keyPairs;
	
	protected GameServerManager()
	{
		load();
	}
	
	@Override
	public void load()
	{
		if (Config.DEV_MODE)
			parseDataFile("serverNames.xml");
		else
			parseFile("serverNames.xml");
		
		LOGGER.info("Loaded {} server names.", _serverNames.size());
		
		loadRegisteredGameServers();
		LOGGER.info("Loaded {} registered gameserver(s).", _registeredServers.size());
		
		if (_registeredServers.isEmpty()) {
			LOGGER.error("===================================================================");
			LOGGER.error("ERRO CRÍTICO: Nenhum gameserver registrado encontrado no banco de dados!");
			LOGGER.error("===================================================================");
			LOGGER.error("O LoginServer não pode iniciar sem pelo menos um gameserver registrado.");
			LOGGER.error("Isso causará falha de autenticação (wrong hexid) quando o GameServer tentar conectar.");
			LOGGER.error("");
			LOGGER.error("SOLUÇÃO:");
			LOGGER.error("1. Execute o DatabaseManager e configure o banco de dados.");
			LOGGER.error("2. Ou use o GameServerRegister para registrar um gameserver.");
			LOGGER.error("3. Ou insira manualmente um registro na tabela 'gameservers' do banco de dados.");
			LOGGER.error("===================================================================");
			throw new RuntimeException("Nenhum gameserver registrado encontrado no banco de dados. Configure o banco antes de iniciar o servidor.");
		}
		
		initRSAKeys();
		LOGGER.info("Cached {} RSA keys for gameserver communication.", _keyPairs.length);
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "server", serverNode ->
		{
			final StatSet set = parseAttributes(serverNode);
			_serverNames.put(set.getInteger("id"), set.getString("name"));
		}));
	}
	
	private void initRSAKeys()
	{
		try
		{
			final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4));
			
			_keyPairs = new KeyPair[KEYS_SIZE];
			for (int i = 0; i < KEYS_SIZE; i++)
				_keyPairs[i] = keyGen.genKeyPair();
		}
		catch (Exception e)
		{
			LOGGER.error("Error loading RSA keys for Game Server communication.", e);
		}
	}
	
	private void loadRegisteredGameServers()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_SERVERS);
			ResultSet rs = ps.executeQuery())
		{
			while (rs.next())
			{
				final int id = rs.getInt("server_id");
				_registeredServers.put(id, new GameServerInfo(id, stringToHex(rs.getString("hexid"))));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Error loading registered gameservers.", e);
		}
	}
	
	public Map<Integer, GameServerInfo> getRegisteredGameServers()
	{
		return _registeredServers;
	}
	
	public boolean registerWithFirstAvailableId(GameServerInfo gsi)
	{
		for (int id : _serverNames.keySet())
		{
			if (!_registeredServers.containsKey(id))
			{
				_registeredServers.put(id, gsi);
				gsi.setId(id);
				return true;
			}
		}
		return false;
	}
	
	public boolean register(int id, GameServerInfo gsi)
	{
		if (!_registeredServers.containsKey(id))
		{
			_registeredServers.put(id, gsi);
			gsi.setId(id);
			return true;
		}
		return false;
	}
	
	public void registerServerOnDB(GameServerInfo gsi)
	{
		registerServerOnDB(gsi.getHexId(), gsi.getId(), gsi.getHostName());
	}
	
	public void registerServerOnDB(byte[] hexId, int id, String hostName)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(ADD_SERVER))
		{
			ps.setString(1, hexToString(hexId));
			ps.setInt(2, id);
			ps.setString(3, hostName);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Error while saving gameserver data.", e);
		}
	}
	
	public Map<Integer, String> getServerNames()
	{
		return _serverNames;
	}
	
	public KeyPair getKeyPair()
	{
		return Rnd.get(_keyPairs);
	}
	
	private static byte[] stringToHex(String string)
	{
		return new BigInteger(string, 16).toByteArray();
	}
	
	private static String hexToString(byte[] hex)
	{
		return (hex == null) ? "null" : new BigInteger(hex).toString(16);
	}
	
	public static GameServerManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final GameServerManager INSTANCE = new GameServerManager();
	}

	public int getGameServersCount()
	{
		
		return 0;
	}
}