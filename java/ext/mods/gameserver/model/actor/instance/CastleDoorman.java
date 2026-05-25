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
package ext.mods.gameserver.model.actor.instance;

import ext.mods.gameserver.enums.PrivilegeType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.residence.clanhall.SiegableHall;

/**
 * An instance type extending {@link Doorman}, used by castle doorman.<br>
 * <br>
 * isUnderSiege() checks current siege state associated to the doorman castle, while isOwnerClan() checks if the user is part of clan owning the castle and got the rights to open/close doors.
 */
public class CastleDoorman extends Doorman
{
	public CastleDoorman(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	protected void openDoors(Player player, String command)
	{
		if (getResidence() == null)
			return;
		
		for (String doorId : command.substring(11).split(", "))
			getResidence().openDoor(player, Integer.parseInt(doorId));
	}
	
	@Override
	protected final void closeDoors(Player player, String command)
	{
		if (getResidence() == null)
			return;
		
		for (String doorId : command.substring(12).split(", "))
			getResidence().closeDoor(player, Integer.parseInt(doorId));
	}
	
	@Override
	protected final boolean isOwnerClan(Player player)
	{
		if (player.getClan() != null)
		{
			if (getSiegableHall() != null)
				return player.getClanId() == getSiegableHall().getOwnerId() && player.hasClanPrivileges(PrivilegeType.CHP_ENTRY_EXIT_RIGHTS);
			
			if (getCastle() != null)
				return player.getClanId() == getCastle().getOwnerId() && player.hasClanPrivileges(PrivilegeType.CP_ENTRY_EXIT_RIGHTS);
		}
		return false;
	}
	
	@Override
	protected final boolean isUnderSiege()
	{
		final SiegableHall hall = getSiegableHall();
		if (hall != null)
			return hall.isInSiege();
		
		return getCastle() != null && getCastle().getSiegeZone().isActive();
	}
}