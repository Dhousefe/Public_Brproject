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
package ext.mods.gameserver.model.actor.container.monster;

import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.holder.IntIntHolder;

/**
 * A container holding all related informations of a {@link Monster} spoil state.<br>
 * <br>
 * A spoil occurs when a {@link Player} procs a spoil skill over a Monster.
 */
public class SpoilState extends ArrayList<IntIntHolder>
{
	private static final long serialVersionUID = 1L;
	
	private int _spoilerId;
	
	public int getSpoilerId()
	{
		return _spoilerId;
	}
	
	public void setSpoilerId(int value)
	{
		_spoilerId = value;
	}
	
	/**
	 * @return true if the spoiler objectId is set.
	 */
	public boolean isSpoiled()
	{
		return _spoilerId > 0;
	}
	
	/**
	 * @param player : The Player to test.
	 * @return true if the given {@link Player} set as parameter is the actual spoiler.
	 */
	public boolean isActualSpoiler(Player player)
	{
		return player != null && player.getObjectId() == _spoilerId;
	}
	
	/**
	 * @return true if _sweepItems {@link List} is filled.
	 */
	public boolean isSweepable()
	{
		return !isEmpty();
	}
	
	/**
	 * Clear all spoil related variables.
	 */
	@Override
	public void clear()
	{
		_spoilerId = 0;
		
		super.clear();
	}
}