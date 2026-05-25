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
package ext.mods.gameserver.model.restart;

import java.util.EnumMap;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.geometry.Territory;

import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.model.actor.Player;

/**
 * A zone used as restart point when dead or scrolling out.<br>
 * <br>
 * It priors and overrides behavior from {@link RestartPoint}, and enforce the restart point based on {@link ClassRace}.
 */
public class RestartArea extends Territory
{
	private final EnumMap<ClassRace, String> _classRestrictions;
	
	@SuppressWarnings("unchecked")
	public RestartArea(StatSet set)
	{
		super(set);
		
		_classRestrictions = set.getObject("classRestrictions", EnumMap.class);
	}
	
	public String getClassRestriction(Player player)
	{
		return _classRestrictions.get(player.getTemplate().getRace());
	}
}