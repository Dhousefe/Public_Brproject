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
package ext.mods.gameserver.handler.bypasshandlers;

import java.util.StringTokenizer;

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.xml.ObserverGroupData;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.ObserverLocation;
import ext.mods.gameserver.model.olympiad.OlympiadManager;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.SystemMessageId;

public class Observe implements IBypassHandler
{
	private static final String[] COMMANDS = { "observe" };
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		final ObserverLocation loc = ObserverGroupData.getInstance().getObserverLocation(Integer.parseInt(st.nextToken()));
		if (loc == null)
			return false;
		
		final boolean hasSummon = player.getSummon() != null;
		
		if (loc.getCastleId() > 0)
		{
			if (hasSummon)
			{
				player.sendPacket(SystemMessageId.NO_OBSERVE_WITH_PET);
				return false;
			}
			
			final Castle castle = CastleManager.getInstance().getCastleById(loc.getCastleId());
			if (castle == null || !castle.getSiege().isInProgress())
			{
				player.sendPacket(SystemMessageId.ONLY_VIEW_SIEGE);
				return false;
			}
		}
		else if (hasSummon)
			return false;
		
		if (player.isInCombat())
		{
			player.sendPacket(SystemMessageId.CANNOT_OBSERVE_IN_COMBAT);
			return false;
		}
		
		if (OlympiadManager.getInstance().isRegisteredInComp(player))
			return false;
		
		player.enterObserverMode(loc);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}