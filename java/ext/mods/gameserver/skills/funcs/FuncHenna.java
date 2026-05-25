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
package ext.mods.gameserver.skills.funcs;

import ext.mods.gameserver.enums.actors.HennaType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.basefuncs.Func;

/**
 * @see Func
 */
public class FuncHenna extends Func
{
	private static final FuncHenna STR = new FuncHenna(HennaType.STR);
	private static final FuncHenna CON = new FuncHenna(HennaType.CON);
	private static final FuncHenna DEX = new FuncHenna(HennaType.DEX);
	private static final FuncHenna INT = new FuncHenna(HennaType.INT);
	private static final FuncHenna MEN = new FuncHenna(HennaType.MEN);
	private static final FuncHenna WIT = new FuncHenna(HennaType.WIT);
	
	private final HennaType _type;
	
	private FuncHenna(HennaType type)
	{
		super(null, type.getStats(), 10, 0, null);
		
		_type = type;
	}
	
	@Override
	public double calc(Creature effector, Creature effected, L2Skill skill, double base, double value)
	{
		if (effector instanceof Player player)
			return value + player.getHennaList().getStat(_type);
		
		return value;
	}
	
	public static final FuncHenna getSTR()
	{
		return STR;
	}
	
	public static final FuncHenna getCON()
	{
		return CON;
	}
	
	public static final FuncHenna getDEX()
	{
		return DEX;
	}
	
	public static final FuncHenna getINT()
	{
		return INT;
	}
	
	public static final FuncHenna getMEN()
	{
		return MEN;
	}
	
	public static final FuncHenna getWIT()
	{
		return WIT;
	}
}