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
package ext.mods.gameserver.network.serverpackets;

public class CharCreateFail extends L2GameServerPacket
{
	public static final CharCreateFail REASON_CREATION_FAILED = new CharCreateFail(0x00);
	public static final CharCreateFail REASON_TOO_MANY_CHARACTERS = new CharCreateFail(0x01);
	public static final CharCreateFail REASON_NAME_ALREADY_EXISTS = new CharCreateFail(0x02);
	public static final CharCreateFail REASON_16_ENG_CHARS = new CharCreateFail(0x03);
	public static final CharCreateFail REASON_INCORRECT_NAME = new CharCreateFail(0x04);
	public static final CharCreateFail REASON_CREATE_NOT_ALLOWED = new CharCreateFail(0x05);
	public static final CharCreateFail REASON_CHOOSE_ANOTHER_SVR = new CharCreateFail(0x06);
	
	private final int _error;
	
	public CharCreateFail(int errorCode)
	{
		_error = errorCode;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x1a);
		writeD(_error);
	}
}