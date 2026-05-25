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
package ext.mods.gameserver.model.location;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.network.NpcStringId;

/**
 * A datatype extending {@link Location}, used as a unique node of a pre-programmed route for walking NPCs.<br>
 * <br>
 * Added to the x/y/z informations, you can also find delay (the time the walking NPC will stand on the point without moving), the String to broadcast (null if none) and the running behavior.
 */
public class WalkerLocation extends Location
{
	private final int _delay;
	private final NpcStringId _fstring;
	private final int _socialId;
	
	public WalkerLocation(StatSet set)
	{
		super(set);
		
		_delay = set.getInteger("delay", 0) * 1000;
		_fstring = set.getNpcStringId("fstring", null);
		_socialId = set.getInteger("socialId", 0);
	}
	
	public int getDelay()
	{
		return _delay;
	}
	
	public NpcStringId getNpcStringId()
	{
		return _fstring;
	}
	
	public int getSocialId()
	{
		return _socialId;
	}
}