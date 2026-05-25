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

import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.enums.StatusType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.holder.IntIntHolder;

public class StatusUpdate extends L2GameServerPacket
{
	private final int _objectId;
	private final List<IntIntHolder> _attributes;
	
	public StatusUpdate(WorldObject object)
	{
		_attributes = new ArrayList<>();
		_objectId = object.getObjectId();
	}
	
	public void addAttribute(StatusType type, int level)
	{
		_attributes.add(new IntIntHolder(type.getId(), level));
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x0e);
		writeD(_objectId);
		writeD(_attributes.size());
		
		for (IntIntHolder temp : _attributes)
		{
			writeD(temp.getId());
			writeD(temp.getValue());
		}
	}
}