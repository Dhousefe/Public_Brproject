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

import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.olympiad.OlympiadGameTask;
import ext.mods.gameserver.model.zone.type.subtype.SpawnZoneType;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExOlympiadMatchEnd;
import ext.mods.gameserver.network.serverpackets.ExOlympiadUserInfo;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * A zone extending {@link SpawnZoneType}, used for olympiad event.<br>
 * <br>
 * Restart and the use of "summoning friend" skill aren't allowed. The zone is considered a pvp zone.
 */
public class OlympiadStadiumZone extends SpawnZoneType
{
	OlympiadGameTask _task = null;
	
	public OlympiadStadiumZone(int id)
	{
		super(id);
	}
	
	@Override
	protected final void onEnter(Creature creature)
	{
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
		creature.setInsideZone(ZoneId.NO_RESTART, true);
		
		if (_task != null && _task.isBattleStarted())
		{
			creature.setInsideZone(ZoneId.PVP, true);
			
			if (creature instanceof Player player)
			{
				player.sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
				
				_task.getGame().sendOlympiadInfo(player);
			}
		}
		
		final Player player = creature.getActingPlayer();
		if (player != null && !player.isGM() && !player.isInOlympiadMode() && !player.isInObserverMode())
		{
			final Summon summon = player.getSummon();
			if (summon != null)
				summon.unSummon(player);
			
			player.teleportTo(RestartType.TOWN);
		}
	}
	
	@Override
	protected final void onExit(Creature creature)
	{
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		creature.setInsideZone(ZoneId.NO_RESTART, false);
		
		if (_task != null && _task.isBattleStarted())
		{
			creature.setInsideZone(ZoneId.PVP, false);
			
			if (creature instanceof Player player)
			{
				player.sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
				player.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
			}
		}
	}
	
	public final void updateZoneStatus()
	{
		if (_task == null)
			return;
		
		for (Creature creature : _creatures)
		{
			if (_task.isBattleStarted())
			{
				creature.setInsideZone(ZoneId.PVP, true);
				if (creature instanceof Player player)
					player.sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
			}
			else
			{
				creature.setInsideZone(ZoneId.PVP, false);
				if (creature instanceof Player player)
				{
					player.sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
					player.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
				}
			}
		}
	}
	
	public final void registerTask(OlympiadGameTask task)
	{
		_task = task;
	}
	
	public final void broadcastStatusUpdate(Player player)
	{
		final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
		for (Player plyr : getKnownTypeInside(Player.class))
		{
			if (plyr.isInObserverMode() || plyr.getOlympiadSide() != player.getOlympiadSide())
				plyr.sendPacket(packet);
		}
	}
	
	public final void broadcastPacketToObservers(L2GameServerPacket packet)
	{
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (player.isInObserverMode())
				player.sendPacket(packet);
		}
	}
}