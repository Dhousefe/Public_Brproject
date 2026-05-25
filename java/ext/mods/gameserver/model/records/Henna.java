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
package ext.mods.gameserver.model.records;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.model.actor.Player;

public record Henna(int symbolId, int dyeId, int drawPrice, int INT, int STR, int CON, int MEN, int DEX, int WIT, int[] classes)
{
	
	public static final int DRAW_AMOUNT = 10;
	public static final int REMOVE_AMOUNT = 5;
	
	public Henna(StatSet set)
	{
		this(set.getInteger("symbolId"), set.getInteger("dyeId"), set.getInteger("price", 0), set.getInteger("INT", 0), set.getInteger("STR", 0), set.getInteger("CON", 0), set.getInteger("MEN", 0), set.getInteger("DEX", 0), set.getInteger("WIT", 0), set.getIntegerArray("classes"));
	}
	
	public int getRemovePrice()
	{
		return drawPrice / REMOVE_AMOUNT;
	}
	
	public boolean canBeUsedBy(Player player)
	{
		return ArraysUtil.contains(classes, player.getClassId().getId());
	}
}