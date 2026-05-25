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

import ext.mods.Config;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.SiegeSummon;
import ext.mods.gameserver.model.zone.type.subtype.SpawnZoneType;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.taskmanager.PvpFlagTaskManager;

/**
 * A zone extending {@link SpawnZoneType}, used for castle on siege progress, and which handles following spawns type :
 * <ul>
 * <li>Generic spawn locs : other_restart_village_list (spawns used on siege, to respawn on second closest town.</li>
 * <li>Chaotic spawn locs : chao_restart_point_list (spawns used on siege, to respawn PKs on second closest town.</li>
 * </ul>
 */
public class SiegeZone extends ZoneType
{
	private int _siegableId = -1;
	private boolean _isActiveSiege = false;
	
	public SiegeZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("castleId") || name.equals("clanHallId"))
			_siegableId = Integer.parseInt(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		if (_isActiveSiege)
		{
			creature.setInsideZone(ZoneId.PVP, true);
			creature.setInsideZone(ZoneId.SIEGE, true);
			creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, true);
			
			if (creature instanceof Player player)
			{
				player.sendPacket(SystemMessageId.ENTERED_COMBAT_ZONE);
				player.enterOnNoLandingZone();
			}
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		creature.setInsideZone(ZoneId.PVP, false);
		creature.setInsideZone(ZoneId.SIEGE, false);
		creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
		
		if (creature instanceof Player player)
		{
			if (_isActiveSiege)
			{
				player.sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
				player.exitOnNoLandingZone();
				
				PvpFlagTaskManager.getInstance().add(player, Config.PVP_NORMAL_TIME);
				
				if (player.getPvpFlag() == 0)
					player.updatePvPFlag(1);
			}
		}
		else if (creature instanceof SiegeSummon siegeSummon)
			siegeSummon.unSummon(siegeSummon.getOwner());
	}
	
	public int getSiegableId()
	{
		return _siegableId;
	}
	
	public boolean isActive()
	{
		return _isActiveSiege;
	}
	
	public void setActive(boolean val)
	{
		_isActiveSiege = val;
		
		if (_isActiveSiege)
		{
			for (Creature creature : _creatures)
				onEnter(creature);
		}
		else
		{
			for (Creature creature : _creatures)
			{
				creature.setInsideZone(ZoneId.PVP, false);
				creature.setInsideZone(ZoneId.SIEGE, false);
				creature.setInsideZone(ZoneId.NO_SUMMON_FRIEND, false);
				
				if (creature instanceof Player player)
				{
					player.sendPacket(SystemMessageId.LEFT_COMBAT_ZONE);
					player.exitOnNoLandingZone();
				}
				else if (creature instanceof SiegeSummon siegeSummon)
					siegeSummon.unSummon(siegeSummon.getOwner());
			}
		}
	}
	
	/**
	 * Kick {@link Player}s who don't belong to the clan set as parameter from this zone. They are ported to chaotic or regular spawn locations depending of their karma.
	 * @param clanId : The castle owner id. Related players aren't teleported out.
	 */
	public void banishForeigners(int clanId)
	{
		for (Player player : getKnownTypeInside(Player.class))
		{
			if (player.getClanId() == clanId)
				continue;
			
			player.teleportTo(RestartType.TOWN);
		}
	}
}