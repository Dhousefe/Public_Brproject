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
package ext.mods.gameserver.enums.items;

public enum CrystalType
{
	NONE(0, 0, 0, 0, 0, 0),
	D(1, 1458, 11, 90, 0, 0),
	C(2, 1459, 6, 45, 2130, 20),
	B(3, 1460, 11, 67, 2130, 30),
	A(4, 1461, 19, 144, 2131, 20),
	S(5, 1462, 25, 250, 2131, 25);
	
	private final int _id;
	private final int _crystalId;
	private final int _crystalEnchantBonusArmor;
	private final int _crystalEnchantBonusWeapon;
	private final int _gemstoneId;
	private final int _gemstoneCount;
	
	private CrystalType(int id, int crystalId, int crystalEnchantBonusArmor, int crystalEnchantBonusWeapon, int gemstoneId, int gemstoneCount)
	{
		_id = id;
		_crystalId = crystalId;
		_crystalEnchantBonusArmor = crystalEnchantBonusArmor;
		_crystalEnchantBonusWeapon = crystalEnchantBonusWeapon;
		_gemstoneId = gemstoneId;
		_gemstoneCount = gemstoneCount;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getCrystalId()
	{
		return _crystalId;
	}
	
	public int getCrystalEnchantBonusArmor()
	{
		return _crystalEnchantBonusArmor;
	}
	
	public int getCrystalEnchantBonusWeapon()
	{
		return _crystalEnchantBonusWeapon;
	}
	
	public int getGemstoneId()
	{
		return _gemstoneId;
	}
	
	public int getGemstoneCount()
	{
		return _gemstoneCount;
	}
	
	public boolean isGreater(CrystalType crystalType)
	{
		return getId() > crystalType.getId();
	}
	
	public boolean isLesser(CrystalType crystalType)
	{
		return getId() < crystalType.getId();
	}
}