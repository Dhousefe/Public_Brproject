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
package ext.mods.loginserver.network;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.mmocore.MMOClient;
import ext.mods.commons.mmocore.MMOConnection;
import ext.mods.commons.mmocore.SendablePacket;
import ext.mods.commons.random.Rnd;

import ext.mods.loginserver.LoginController;
import ext.mods.loginserver.crypt.LoginCrypt;
import ext.mods.loginserver.crypt.ScrambledKeyPair;
import ext.mods.loginserver.enums.LoginClientState;
import ext.mods.loginserver.model.Account;
import ext.mods.loginserver.network.serverpackets.L2LoginServerPacket;
import ext.mods.loginserver.network.serverpackets.LoginFail;
import ext.mods.loginserver.network.serverpackets.PlayFail;

/**
 * Represents a client connected into the LoginServer
 */
public final class LoginClient extends MMOClient<MMOConnection<LoginClient>>
{
	private static final CLogger LOGGER = new CLogger(LoginClient.class.getName());
	
	private final LoginCrypt _loginCrypt;
	private final ScrambledKeyPair _scrambledPair;
	private final byte[] _blowfishKey;
	private final int _sessionId;
	private final long _connectionStartTime;
	
	private LoginClientState _state;
	private Account _account;
	private SessionKey _sessionKey;
	private boolean _joinedGS;
	
	public LoginClient(MMOConnection<LoginClient> con)
	{
		super(con);
		
		_state = LoginClientState.CONNECTED;
		_scrambledPair = LoginController.getInstance().getScrambledRSAKeyPair();
		_blowfishKey = LoginController.getInstance().getRandomBlowfishKey();
		_sessionId = Rnd.nextInt();
		_connectionStartTime = System.currentTimeMillis();
		_loginCrypt = new LoginCrypt();
		_loginCrypt.setKey(_blowfishKey);
	}
	
	@Override
	public String toString()
	{
		final InetAddress address = getConnection().getInetAddress();
		if (getState() == LoginClientState.AUTHED_LOGIN)
			return "[" + getAccount() + " (" + (address == null ? "disconnected" : address.getHostAddress()) + ")]";
		
		return "[" + (address == null ? "disconnected" : address.getHostAddress()) + "]";
	}
	
	@Override
	public boolean decrypt(ByteBuffer buf, int size)
	{
		try
		{
			if (!_loginCrypt.decrypt(buf.array(), buf.position(), size))
			{
				super.getConnection().close((SendablePacket<LoginClient>) null);
				return false;
			}
			return true;
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't decrypt LoginClient packet.", e);
			super.getConnection().close((SendablePacket<LoginClient>) null);
			return false;
		}
	}
	
	@Override
	public boolean encrypt(ByteBuffer buf, int size)
	{
		final int offset = buf.position();
		try
		{
			size = _loginCrypt.encrypt(buf.array(), offset, size);
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't encrypt LoginClient packet.", e);
			return false;
		}
		
		buf.position(offset + size);
		return true;
	}
	
	@Override
	public void onDisconnection()
	{
		if (_account == null)
			return;
		
		if (!hasJoinedGS() || (getConnectionStartTime() + LoginController.LOGIN_TIMEOUT) < System.currentTimeMillis())
			LoginController.getInstance().removeAuthedLoginClient(_account.getLogin());
	}
	
	@Override
	protected void onForcedDisconnection()
	{
	}
	
	public LoginClientState getState()
	{
		return _state;
	}
	
	public void setState(LoginClientState state)
	{
		_state = state;
	}
	
	public byte[] getBlowfishKey()
	{
		return _blowfishKey;
	}
	
	public byte[] getScrambledModulus()
	{
		return _scrambledPair.getScrambledModulus();
	}
	
	public RSAPrivateKey getRSAPrivateKey()
	{
		return (RSAPrivateKey) _scrambledPair.getKeyPair().getPrivate();
	}
	
	public Account getAccount()
	{
		return _account;
	}
	
	public void setAccount(Account account)
	{
		_account = account;
	}
	
	public int getSessionId()
	{
		return _sessionId;
	}
	
	public boolean hasJoinedGS()
	{
		return _joinedGS;
	}
	
	public void setJoinedGS(boolean val)
	{
		_joinedGS = val;
	}
	
	public void setSessionKey(SessionKey sessionKey)
	{
		_sessionKey = sessionKey;
	}
	
	public SessionKey getSessionKey()
	{
		return _sessionKey;
	}
	
	public long getConnectionStartTime()
	{
		return _connectionStartTime;
	}
	
	public void sendPacket(L2LoginServerPacket lsp)
	{
		getConnection().sendPacket(lsp);
	}
	
	public void close(LoginFail reason)
	{
		getConnection().close(reason);
	}
	
	public void close(PlayFail reason)
	{
		getConnection().close(reason);
	}
	
	public void close(L2LoginServerPacket lsp)
	{
		getConnection().close(lsp);
	}
}