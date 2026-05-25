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

import ext.mods.gameserver.model.actor.Playable;

public class RelationChanged extends L2GameServerPacket
{
	public static final int RELATION_PVP_FLAG = 0x00002;
	public static final int RELATION_HAS_KARMA = 0x00004;
	public static final int RELATION_LEADER = 0x00080;
	public static final int RELATION_INSIEGE = 0x00200;
	public static final int RELATION_ATTACKER = 0x00400;
	public static final int RELATION_ALLY = 0x00800;
	public static final int RELATION_ENEMY = 0x01000;
	public static final int RELATION_MUTUAL_WAR = 0x08000;
	public static final int RELATION_1SIDED_WAR = 0x10000;
	
	private final int _objectId;
	private final int _relation;
	private final int _autoAttackable;
	private final int _karma;
	private final int _pvpFlag;
	
	public RelationChanged(Playable playable, int relation, boolean isAutoAttackable)
	{
		_objectId = playable.getObjectId();
		_relation = relation;
		_autoAttackable = (isAutoAttackable) ? 1 : 0;
		_karma = playable.getKarma();
		_pvpFlag = playable.getPvpFlag();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0xce);
		writeD(_objectId);
		writeD(_relation);
		writeD(_autoAttackable);
		writeD(_karma);
		writeD(_pvpFlag);
	}
}