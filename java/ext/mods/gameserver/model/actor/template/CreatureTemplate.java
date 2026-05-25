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
package ext.mods.gameserver.model.actor.template;

import ext.mods.commons.data.StatSet;

/**
 * The generic datatype used by any character template. It holds basic informations, such as base stats (STR, CON, DEX,...) and extended stats (power attack, magic attack, hp/mp regen, collision values).
 */
public class CreatureTemplate
{
	private final int _baseSTR;
	private final int _baseCON;
	private final int _baseDEX;
	private final int _baseINT;
	private final int _baseWIT;
	private final int _baseMEN;
	
	public double _baseHpMax;
	public double _baseMpMax;
	
	private final double _baseHpRegen;
	private final double _baseMpRegen;
	
	public double _basePAtk;
	public double _baseMAtk;
	public double _basePDef;
	public double _baseMDef;
	
	private final double _basePAtkSpd;
	
	private final double _baseCritRate;
	
	private final double _baseWalkSpd;
	private final double _baseRunSpd;
	
	protected final double _collisionRadius;
	protected final double _collisionHeight;
	
	public CreatureTemplate(StatSet set)
	{
		_baseSTR = set.getInteger("str", 40);
		_baseCON = set.getInteger("con", 21);
		_baseDEX = set.getInteger("dex", 30);
		_baseINT = set.getInteger("int", 20);
		_baseWIT = set.getInteger("wit", 43);
		_baseMEN = set.getInteger("men", 20);
		
		_baseHpMax = set.getDouble("hp", 0.);
		_baseMpMax = set.getDouble("mp", 0.);
		
		_baseHpRegen = set.getDouble("hpRegen", 1.5);
		_baseMpRegen = set.getDouble("mpRegen", 0.9);
		
		_basePAtk = set.getDouble("pAtk");
		_baseMAtk = set.getDouble("mAtk");
		_basePDef = set.getDouble("pDef");
		_baseMDef = set.getDouble("mDef");
		
		_basePAtkSpd = set.getDouble("atkSpd", 300.);
		
		_baseCritRate = set.getDouble("crit", 4.);
		
		_baseWalkSpd = set.getDouble("walkSpd", 0.);
		_baseRunSpd = set.getDouble("runSpd", 1.);
		
		_collisionRadius = set.getDouble("radius");
		_collisionHeight = set.getDouble("height");
	}
	
	public final int getBaseSTR()
	{
		return _baseSTR;
	}
	
	public final int getBaseCON()
	{
		return _baseCON;
	}
	
	public final int getBaseDEX()
	{
		return _baseDEX;
	}
	
	public final int getBaseINT()
	{
		return _baseINT;
	}
	
	public final int getBaseWIT()
	{
		return _baseWIT;
	}
	
	public final int getBaseMEN()
	{
		return _baseMEN;
	}
	
	public double getBaseHpMax(int level)
	{
		return _baseHpMax;
	}
	
	public double getBaseMpMax(int level)
	{
		return _baseMpMax;
	}
	
	public double getBaseHpRegen(int level)
	{
		return _baseHpRegen;
	}
	
	public double getBaseMpRegen(int level)
	{
		return _baseMpRegen;
	}
	
	public final double getBasePAtk()
	{
		return _basePAtk;
	}
	
	public final double getBaseMAtk()
	{
		return _baseMAtk;
	}
	
	public final double getBasePDef()
	{
		return _basePDef;
	}
	
	public final double getBaseMDef()
	{
		return _baseMDef;
	}
	
	public final double getBasePAtkSpd()
	{
		return _basePAtkSpd;
	}
	
	public final double getBaseCritRate()
	{
		return _baseCritRate;
	}
	
	public final double getBaseWalkSpeed()
	{
		return _baseWalkSpd;
	}
	
	public final double getBaseRunSpeed()
	{
		return _baseRunSpd;
	}
	
	public final double getCollisionRadius()
	{
		return _collisionRadius;
	}
	
	public final double getCollisionHeight()
	{
		return _collisionHeight;
	}
}