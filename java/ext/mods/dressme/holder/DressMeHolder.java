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
package ext.mods.dressme.holder;

import ext.mods.commons.data.StatSet;

public class DressMeHolder
{
	private final int _skillId;
	private final String _name;
	private final boolean _isVip;
	private final DressMeVisualType _type;
	
	private int _chestId, _legsId, _glovesId, _feetId, _helmetId;
	private int _rHandId, _lHandId, _lrHandId;
	private String _weaponTypeVisual;
	
	private DressMeEffectHolder _effect;
	
	public DressMeHolder(StatSet set)
	{
		_skillId = set.getInteger("skillId");
		_name = set.getString("name", "");
		_type = DressMeVisualType.valueOf(set.getString("type", "ARMOR"));
		_isVip = set.getBool("isVip", false);
	}
	
	public void setVisualSet(StatSet set)
	{
		_chestId = set.getInteger("chest", 0);
		_legsId = set.getInteger("legs", 0);
		_glovesId = set.getInteger("gloves", 0);
		_feetId = set.getInteger("feet", 0);
		_helmetId = set.getInteger("helmet", 0);
	}
	
	public void setWeaponSet(StatSet set)
	{
		_rHandId = set.getInteger("rhand", 0);
		_lHandId = set.getInteger("lhand", 0);
		_lrHandId = set.getInteger("lrhand", 0);
		_weaponTypeVisual = set.getString("type", "");
	}
	
	public void setEffect(DressMeEffectHolder effect)
	{
		_effect = effect;
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public boolean isVip()
	{
		return _isVip;
	}
	
	public DressMeVisualType getType()
	{
		return _type;
	}
	
	public int getChestId()
	{
		return _chestId;
	}
	
	public int getLegsId()
	{
		return _legsId;
	}
	
	public int getGlovesId()
	{
		return _glovesId;
	}
	
	public int getFeetId()
	{
		return _feetId;
	}
	
	public int getHelmetId()
	{
		return _helmetId;
	}
	
	public String getWeaponTypeVisual()
	{
		return _weaponTypeVisual;
	}
	
	public int getRightHandId()
	{
		return _rHandId;
	}
	
	public int getLeftHandId()
	{
		return _lHandId;
	}
	
	public int getTwoHandId()
	{
		return _lrHandId;
	}
	
	public DressMeEffectHolder getEffect()
	{
		return _effect;
	}
}
