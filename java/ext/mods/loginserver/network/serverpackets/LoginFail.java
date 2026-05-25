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

public final class LoginFail extends L2LoginServerPacket
{
	public static final LoginFail REASON_SYSTEM_ERROR = new LoginFail(0x01);
	public static final LoginFail REASON_PASS_WRONG = new LoginFail(0x02);
	public static final LoginFail REASON_USER_OR_PASS_WRONG = new LoginFail(0x03);
	public static final LoginFail REASON_ACCESS_FAILED = new LoginFail(0x04);
	public static final LoginFail REASON_ACCOUNT_IN_USE = new LoginFail(0x07);
	public static final LoginFail REASON_SERVER_OVERLOADED = new LoginFail(0x0f);
	public static final LoginFail REASON_SERVER_MAINTENANCE = new LoginFail(0x10);
	public static final LoginFail REASON_TEMP_PASS_EXPIRED = new LoginFail(0x11);
	public static final LoginFail REASON_DUAL_BOX = new LoginFail(0x23);
	
	private final int _reason;
	
	private LoginFail(int reason)
	{
		_reason = reason;
	}
	
	@Override
	protected void write()
	{
		writeC(0x01);
		writeD(_reason);
	}
}