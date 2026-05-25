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
package ext.mods.summonmobitem;

/**
 * Holder para armazenar as configurações de um item de summon de monstro
 * 
 * @author Dhousefe
 */
public class SummonMobItemHolder
{
	private final int _itemId;
	private final int _monsterId;
	private final String _monsterName;
	private final boolean _enabled;
	private final int _maxUsesPerDay;
	private final int _cooldownMinutes;
	private final String _requiredClass;
	private final String _description;
	
	public SummonMobItemHolder(int itemId, int monsterId, String monsterName, boolean enabled, 
		int maxUsesPerDay, int cooldownMinutes, String requiredClass, String description)
	{
		_itemId = itemId;
		_monsterId = monsterId;
		_monsterName = monsterName;
		_enabled = enabled;
		_maxUsesPerDay = maxUsesPerDay;
		_cooldownMinutes = cooldownMinutes;
		_requiredClass = requiredClass;
		_description = description;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public int getMonsterId()
	{
		return _monsterId;
	}
	
	public String getMonsterName()
	{
		return _monsterName;
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	public int getMaxUsesPerDay()
	{
		return _maxUsesPerDay;
	}
	
	public int getCooldownMinutes()
	{
		return _cooldownMinutes;
	}
	
	public String getRequiredClass()
	{
		return _requiredClass;
	}
	
	public String getDescription()
	{
		return _description;
	}
	
	/**
	 * Verifica se uma classe específica pode usar este item
	 * 
	 * @param className Nome da classe
	 * @return true se pode usar
	 */
	public boolean canUseByClass(String className)
	{
		if (_requiredClass == null || _requiredClass.trim().isEmpty())
			return true;
		
		final String[] allowedClasses = _requiredClass.split(",");
		for (String allowedClass : allowedClasses)
		{
			if (allowedClass.trim().equalsIgnoreCase(className))
				return true;
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		return "SummonMobItemHolder{" +
			"itemId=" + _itemId +
			", monsterId=" + _monsterId +
			", monsterName='" + _monsterName + '\'' +
			", enabled=" + _enabled +
			", maxUsesPerDay=" + _maxUsesPerDay +
			", cooldownMinutes=" + _cooldownMinutes +
			", requiredClass='" + _requiredClass + '\'' +
			", description='" + _description + '\'' +
			'}';
	}
}
