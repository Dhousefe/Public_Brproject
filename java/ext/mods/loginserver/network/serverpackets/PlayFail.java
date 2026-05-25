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

public final class PlayFail extends L2LoginServerPacket
{
	public static final PlayFail REASON_SYSTEM_ERROR = new PlayFail(0x01);
	public static final PlayFail REASON_USER_OR_PASS_WRONG = new PlayFail(0x02);
	public static final PlayFail REASON3 = new PlayFail(0x03);
	public static final PlayFail REASON4 = new PlayFail(0x04);
	public static final PlayFail REASON_TOO_MANY_PLAYERS = new PlayFail(0x0f);
	
	private final int _reason;
	
	private PlayFail(int reason)
	{
		_reason = reason;
	}
	
	@Override
	protected void write()
	{
		writeC(0x06);
		writeC(_reason);
	}
}