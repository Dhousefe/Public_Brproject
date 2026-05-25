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
package ext.mods.gameserver.model.holder;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.skills.L2Skill;

/**
 * A generic int/int container.
 */
public class IntIntHolder
{
	private int _id;
	private int _value;
	
	public IntIntHolder(int id, int value)
	{
		_id = id;
		_value = value;
	}
	
	@Override
	public String toString()
	{
		return "IntIntHolder [id=" + _id + " value=" + _value + "]";
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getValue()
	{
		return _value;
	}
	
	public void setId(int id)
	{
		_id = id;
	}
	
	public void setValue(int value)
	{
		_value = value;
	}
	
	/**
	 * @return The {@link L2Skill} associated to the id/value stored on this {@link IntIntHolder}.
	 */
	public final L2Skill getSkill()
	{
		return SkillTable.getInstance().getInfo(_id, _value);
	}
	
	/**
	 * If this {@link IntIntHolder} is used as itemId/quantity, calculates the expected total weight size of this {@link Item}.
	 * @return The weight of the potential {@link Item} weight multiplied by count, or 0 if the {@link Item} doesn't exist.
	 */
	public final int getWeight()
	{
		final Item item = ItemData.getInstance().getTemplate(_id);
		if (item == null)
			return 0;
		
		return item.getWeight() * _value;
	}
	
	/**
	 * @param id : The int to test as id.
	 * @param value : The int to test as value.
	 * @return True if both values equal, false otherwise.
	 */
	public final boolean equals(int id, int value)
	{
		return _id == id && _value == value;
	}
}