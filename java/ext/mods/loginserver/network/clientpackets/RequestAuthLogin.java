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

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import ext.mods.loginserver.LoginController;
import ext.mods.loginserver.network.LoginClient;
import ext.mods.loginserver.network.serverpackets.LoginFail;

public class RequestAuthLogin extends L2LoginClientPacket
{
	private final byte[] _raw = new byte[128];
	
	@Override
	public boolean readImpl()
	{
		if (super._buf.remaining() >= 128)
		{
			readB(_raw);
			return true;
		}
		return false;
	}
	
	@Override
	public void run()
	{
		final LoginClient client = getClient();
		
		byte[] decrypted = null;
		try
		{
			final Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
			rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
			decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
		}
		catch (GeneralSecurityException e)
		{
			LOGGER.error("Failed to generate a cipher.", e);
			client.close(LoginFail.REASON_ACCESS_FAILED);
			return;
		}
		
		try
		{
			final String user = new String(decrypted, 0x5E, 14).trim().toLowerCase();
			final String password = new String(decrypted, 0x6C, 16).trim();
			
			LoginController.getInstance().retrieveAccountInfo(client, user, password);
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to decrypt user/password.", e);
			client.close(LoginFail.REASON_ACCESS_FAILED);
		}
	}
}