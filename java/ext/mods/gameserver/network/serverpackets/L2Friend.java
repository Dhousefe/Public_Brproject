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

import ext.mods.gameserver.model.actor.Player;

public class L2Friend extends L2GameServerPacket
{
	private int _action;
	private String _name;
	private int _objectId;
	private int _isOnline;
	
	public L2Friend(Player player, int action)
	{
		_action = action;
		_name = player.getName();
		_objectId = player.getObjectId();
		_isOnline = player.isOnline() ? 1 : 0;
	}
	
	public L2Friend(String name, int action)
	{
		_action = action;
		_name = name;
		_objectId = 0;
		_isOnline = 0;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xfb);
		writeD(_action);
		writeD(0x00);
		writeS(_name);
		writeD(_isOnline);
		writeD(_objectId);
	}
}