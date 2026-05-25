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

import ext.mods.gameserver.model.records.SkillRequirement;

public class ExEnchantSkillInfo extends L2GameServerPacket
{
	private final ArrayList<SkillRequirement> _reqs;
	private final int _id;
	private final int _level;
	private final int _spCost;
	private final int _xpCost;
	private final int _rate;
	
	public ExEnchantSkillInfo(int id, int level, int spCost, int xpCost, int rate)
	{
		_reqs = new ArrayList<>();
		_id = id;
		_level = level;
		_spCost = spCost;
		_xpCost = xpCost;
		_rate = rate;
	}
	
	public void addRequirement(int type, int id, int count, int unk)
	{
		_reqs.add(new SkillRequirement(type, id, count, unk));
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x18);
		
		writeD(_id);
		writeD(_level);
		writeD(_spCost);
		writeQ(_xpCost);
		writeD(_rate);
		
		writeD(_reqs.size());
		
		for (SkillRequirement temp : _reqs)
		{
			writeD(temp.type());
			writeD(temp.itemId());
			writeD(temp.count());
			writeD(temp.unk());
		}
	}
}