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
package ext.mods.gameserver.model.item;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.DropType;
import ext.mods.gameserver.model.holder.IntIntHolder;

/**
 * A container used by monster drops.
 */
public class DropData
{
	public static final int MAX_CHANCE = 1000000;

	private final int _itemId;
	private final int _minDrop;
	private final int _maxDrop;
	private final double _chance;

	private DropType _categoryType;

	public DropData(int itemId, int minDrop, int maxDrop, double chance)
	{
		_itemId = itemId;
		_minDrop = minDrop;
		_maxDrop = maxDrop;
		_chance = chance;
	}

	@Override
	public String toString()
	{
		return "DropData =[ItemID: " + _itemId + " Min: " + _minDrop + " Max: " + _maxDrop + " Chance: " + _chance + "%]";
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getMinDrop()
	{
		return _minDrop;
	}

	public int getMaxDrop()
	{
		return _maxDrop;
	}

	public double getChance()
	{
		return _chance;
	}


	public void setCategoryType(DropType type)
	{
		_categoryType = type;
	}

	
	public DropType getCategoryType()
	{
		return _categoryType;
	}

	public IntIntHolder calculateDrop(double ratio)
	{
		int count;
		if (ratio <= 1)
		{
			count = Rnd.get(_minDrop, _maxDrop);
		}
		else
		{
			ratio *= 100;
			int multiplier = (int) (ratio / 100);
			int bonus = (int) (ratio % 100);

			count = Rnd.get(_minDrop * multiplier, _maxDrop * multiplier);
			if (Rnd.get(100) < bonus)
				count += Rnd.get(_minDrop, _maxDrop);
		}

		return new IntIntHolder(_itemId, count);
	}
}
