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

import ext.mods.gameserver.model.holder.IntIntHolder;

/**
 * A datatype used by enchant skill types. It extends {@link IntIntHolder}.
 */
public class EnchantSkillNode extends IntIntHolder
{
	private final int _exp;
	private final int _sp;
	
	private final int[] _enchantRates = new int[5];
	
	private IntIntHolder _item;
	
	private final String _name;
	private final String _desc;
	
	public EnchantSkillNode(StatSet set)
	{
		super(set.getInteger("id"), set.getInteger("lvl"));
		
		_exp = set.getInteger("exp");
		_sp = set.getInteger("sp");
		
		_enchantRates[0] = set.getInteger("rate76");
		_enchantRates[1] = set.getInteger("rate77");
		_enchantRates[2] = set.getInteger("rate78");
		_enchantRates[3] = set.getInteger("rate79");
		_enchantRates[4] = set.getInteger("rate80");
		
		if (set.containsKey("itemNeeded"))
			_item = set.getIntIntHolder("itemNeeded");
		
		_name = set.getString("name", "");
		_desc = set.getString("desc", "");
	}
	
	public int getExp()
	{
		return _exp;
	}
	
	public int getSp()
	{
		return _sp;
	}
	
	public int getEnchantRate(int level)
	{
		return _enchantRates[level - 76];
	}
	
	public IntIntHolder getItem()
	{
		return _item;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getDesc()
	{
		return _desc;
	}
}