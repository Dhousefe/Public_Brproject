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
package ext.mods.loginserver.network.clientpackets;

import ext.mods.Config;
import ext.mods.loginserver.model.Account;
import ext.mods.loginserver.network.SessionKey;
import ext.mods.loginserver.network.serverpackets.LoginFail;
import ext.mods.loginserver.network.serverpackets.PlayFail;
import ext.mods.loginserver.network.serverpackets.PlayOk;

public class RequestServerLogin extends L2LoginClientPacket
{
	private int _skey1;
	private int _skey2;
	private int _serverId;
	
	public int getSessionKey1()
	{
		return _skey1;
	}
	
	public int getSessionKey2()
	{
		return _skey2;
	}
	
	public int getServerID()
	{
		return _serverId;
	}
	
	@Override
	public boolean readImpl()
	{
		if (super._buf.remaining() >= 9)
		{
			_skey1 = readD();
			_skey2 = readD();
			_serverId = readC();
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		final SessionKey sk = getClient().getSessionKey();
		
		if (Config.SHOW_LICENCE && !sk.checkLoginPair(_skey1, _skey2))
		{
			getClient().close(LoginFail.REASON_ACCESS_FAILED);
			return;
		}
		
		final Account account = getClient().getAccount();
		if (account == null)
		{
			getClient().close(LoginFail.REASON_ACCESS_FAILED);
			return;
		}
		
		if (!account.isLoginPossible(_serverId))
		{
			getClient().close(PlayFail.REASON_TOO_MANY_PLAYERS);
			return;
		}
		
		getClient().setJoinedGS(true);
		getClient().sendPacket(new PlayOk(sk));
	}
}
