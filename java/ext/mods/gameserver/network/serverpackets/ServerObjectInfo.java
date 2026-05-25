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
import ext.mods.gameserver.model.actor.Npc;

public final class ServerObjectInfo extends L2GameServerPacket
{
	private final Npc _npc;
	
	private final int _idTemplate;
	private final String _name;
	
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	
	private final double _collisionHeight;
	private final double _collisionRadius;
	
	private final boolean _isAttackable;
	
	private final int _type;
	private final int _effect;
	
	public ServerObjectInfo(Npc npc, Creature actor, int type, int effect)
	{
		_npc = npc;
		
		_idTemplate = _npc.getTemplate().getIdTemplate();
		_name = _npc.getName();
		
		_x = _npc.getX();
		_y = _npc.getY();
		_z = _npc.getZ();
		_heading = _npc.getHeading();
		
		_collisionHeight = _npc.getCollisionHeight();
		_collisionRadius = _npc.getCollisionRadius();
		
		_isAttackable = _npc.isAttackableBy(actor);
		
		_type = type;
		_effect = effect;
	}
	
	public ServerObjectInfo(Npc npc, Creature actor)
	{
		_npc = npc;
		
		_idTemplate = _npc.getTemplate().getIdTemplate();
		_name = _npc.getName();
		
		_x = _npc.getX();
		_y = _npc.getY();
		_z = _npc.getZ();
		_heading = _npc.getHeading();
		
		_collisionHeight = _npc.getCollisionHeight();
		_collisionRadius = _npc.getCollisionRadius();
		
		_isAttackable = _npc.isAttackableBy(actor);
		
		_type = 1;
		_effect = 0;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x8C);
		writeD(_npc.getObjectId());
		writeD(_idTemplate + 1000000);
		writeS(_name);
		writeD(_isAttackable ? 1 : 0);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
		writeF(1.0);
		writeF(1.0);
		writeF(_collisionRadius);
		writeF(_collisionHeight);
		writeD((int) (_isAttackable ? _npc.getStatus().getHp() : 0));
		writeD(_isAttackable ? _npc.getStatus().getMaxHp() : 0);
		writeD(_type);
		writeD(_effect);
	}
}