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
package ext.mods.gameserver.enums;

public enum StatusType
{
	LEVEL(1),
	EXP(2),
	STR(3),
	DEX(4),
	CON(5),
	INT(6),
	WIT(7),
	MEN(8),
	
	CUR_HP(9),
	MAX_HP(10),
	CUR_MP(11),
	MAX_MP(12),
	
	SP(13),
	CUR_LOAD(14),
	MAX_LOAD(15),
	
	P_ATK(17),
	ATK_SPD(18),
	P_DEF(19),
	EVASION(20),
	ACCURACY(21),
	CRITICAL(22),
	M_ATK(23),
	CAST_SPD(24),
	M_DEF(25),
	PVP_FLAG(26),
	KARMA(27),
	
	CUR_CP(33),
	MAX_CP(34);
	
	public static final StatusType[] VALUES = values();
	
	private final int _id;
	
	private StatusType(int id)
	{
		_id = id;
	}
	
	public int getId()
	{
		return _id;
	}
}