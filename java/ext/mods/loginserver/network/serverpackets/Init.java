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
package ext.mods.loginserver.network.serverpackets;

import ext.mods.loginserver.network.LoginClient;

public final class Init extends L2LoginServerPacket
{
	private final int _sessionId;
	
	private final byte[] _publicKey;
	private final byte[] _blowfishKey;
	
	private static final int PROTOCOL_VERSION = 0x0000c621;
	
	private static final byte[] UNKNOWN_GG = new byte[16];
	
	public Init(LoginClient client)
	{
		_sessionId = client.getSessionId();
		
		_publicKey = client.getScrambledModulus();
		_blowfishKey = client.getBlowfishKey();
	}
	
	@Override
	protected void write()
	{
		writeC(0x00);
		writeD(_sessionId);
		writeD(PROTOCOL_VERSION);
		writeB(_publicKey);
		writeB(UNKNOWN_GG);
		writeB(_blowfishKey);
		writeC(0x00);
	}
}