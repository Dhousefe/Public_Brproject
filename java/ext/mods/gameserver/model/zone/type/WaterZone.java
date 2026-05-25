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
package ext.mods.gameserver.model.zone.type;

import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.MoveType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;
import ext.mods.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import ext.mods.gameserver.network.serverpackets.ServerObjectInfo;

/**
 * A zone extending {@link ZoneType}, used for the water behavior. {@link Player}s can drown if they stay too long below water line.
 */
public class WaterZone extends ZoneType
{
	public WaterZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		final boolean wasInsideWater = creature.isInsideZone(ZoneId.WATER);
		creature.setInsideZone(ZoneId.WATER, true);
		
		if (wasInsideWater)
			return;
		
		creature.getMove().addMoveType(MoveType.SWIM);
		
		if (creature instanceof Player player)
			player.broadcastUserInfo();
		else if (creature instanceof Npc npc)
		{
			npc.forEachKnownType(Player.class, player ->
			{
				if (npc.getStatus().getMoveSpeed() == 0)
					player.sendPacket(new ServerObjectInfo(npc, player));
				else
					player.sendPacket(new NpcInfo(npc, player));
			});
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		creature.setInsideZone(ZoneId.WATER, false);
		
		if (creature.isInsideZone(ZoneId.WATER))
			return;
		
		creature.getMove().removeMoveType(MoveType.SWIM);
		
		if (creature instanceof Player player)
			player.broadcastUserInfo();
		else if (creature instanceof Npc npc)
		{
			npc.forEachKnownType(Player.class, player ->
			{
				if (npc.getStatus().getMoveSpeed() == 0)
					player.sendPacket(new ServerObjectInfo(npc, player));
				else
					player.sendPacket(new NpcInfo(npc, player));
			});
		}
	}
	
	public int getWaterZ()
	{
		return getZone().getHighZ();
	}
}