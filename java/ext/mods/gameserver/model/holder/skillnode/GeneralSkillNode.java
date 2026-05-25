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
package ext.mods.gameserver.model.holder.skillnode;

import ext.mods.commons.data.StatSet;

/**
 * A datatype used by general skill types. It extends {@link SkillNode}.
 */
public class GeneralSkillNode extends SkillNode
{
	private final int _cost;
	
	public GeneralSkillNode(StatSet set)
	{
		super(set);
		
		_cost = set.getInteger("cost");
	}
	
	public int getCost()
	{
		return _cost;
	}
	
	/**
	 * Method used for Divine Inspiration skill implementation, since it uses -1 as cost (easier management). We couldn't keep 0, otherwise it would be considered as an autoGet and be freely given ; and using a boolean tag would kill me.<br>
	 * <br>
	 * <b>Only used to display the correct value to client, regular uses must be -1.</b>
	 * @return 0 or the initial cost if superior to 0.
	 */
	public int getCorrectedCost()
	{
		return Math.max(0, _cost);
	}
}