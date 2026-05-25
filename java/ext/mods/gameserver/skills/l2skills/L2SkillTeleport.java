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
package ext.mods.gameserver.skills.l2skills;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.data.xml.RestartPointData;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.skills.L2Skill;

public class L2SkillTeleport extends L2Skill
{
	private final String _recallType;
	private final Location _loc;
	
	public L2SkillTeleport(StatSet set)
	{
		super(set);
		
		_recallType = set.getString("recallType", "");
		_loc = set.getLocation("teleCoords", null);
	}
	
	@Override
	public void useSkill(Creature creature, WorldObject[] targets)
	{
		if (creature instanceof Player player && (player.isAfraid() || player.isInOlympiadMode()))
			return;
		
		boolean bsps = creature.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
		
		for (WorldObject target : targets)
		{
			if (!(target instanceof Player targetPlayer))
				continue;
			
			if (targetPlayer.isFestivalParticipant() || targetPlayer.isInJail() || targetPlayer.isInDuel() || targetPlayer.isFlying())
				continue;
			
			if (targetPlayer != creature)
			{
				if (targetPlayer.isInOlympiadMode())
					continue;
				
				if (targetPlayer.isInsideZone(ZoneId.BOSS))
					continue;
			}
			
			Location loc = _loc;
			
			if (loc == null)
			{
				if (_recallType.equalsIgnoreCase("Castle"))
					loc = RestartPointData.getInstance().getLocationToTeleport(targetPlayer, RestartType.CASTLE);
				else if (_recallType.equalsIgnoreCase("ClanHall"))
					loc = RestartPointData.getInstance().getLocationToTeleport(targetPlayer, RestartType.CLAN_HALL);
				else
					loc = RestartPointData.getInstance().getLocationToTeleport(targetPlayer, RestartType.TOWN);
			}
			
			if (loc != null)
			{
				targetPlayer.setIsIn7sDungeon(false);
				targetPlayer.teleportTo(loc, 20);
			}
		}
		
		creature.setChargedShot(bsps ? ShotType.BLESSED_SPIRITSHOT : ShotType.SPIRITSHOT, isStaticReuse());
	}
}