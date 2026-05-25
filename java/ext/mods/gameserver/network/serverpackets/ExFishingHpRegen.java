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

import ext.mods.gameserver.model.actor.Creature;

public class ExFishingHpRegen extends L2GameServerPacket
{
	private final int _objectId;
	private final int _time;
	private final int _fishHp;
	private final int _hpMode;
	private final int _anim;
	private final int _goodUse;
	private final int _penalty;
	private final int _hpBarColor;
	
	public ExFishingHpRegen(Creature creature, int time, int fishHp, int hpMode, int goodUse, int anim, int penalty, int hpBarColor)
	{
		_objectId = creature.getObjectId();
		_time = time;
		_fishHp = fishHp;
		_hpMode = hpMode;
		_goodUse = goodUse;
		_anim = anim;
		_penalty = penalty;
		_hpBarColor = hpBarColor;
	}
	
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x16);
		
		writeD(_objectId);
		writeD(_time);
		writeD(_fishHp);
		writeC(_hpMode);
		writeC(_goodUse);
		writeC(_anim);
		writeD(_penalty);
		writeC(_hpBarColor);
	}
}