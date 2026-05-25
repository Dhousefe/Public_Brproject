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
package ext.mods.gameserver.model.actor.container.npc;

/**
 * This class contains all infos of the Attackable against the absorber Creature.
 * <ul>
 * <li>_absorbedHP : The amount of HP at the moment attacker used the item.</li>
 * <li>_itemObjectId : The item id of the Soul Crystal used.</li>
 * </ul>
 */
public final class AbsorbInfo
{
	private boolean _registered;
	private int _itemId;
	private int _absorbedHpPercent;
	
	public AbsorbInfo(int itemId)
	{
		_itemId = itemId;
	}
	
	public boolean isRegistered()
	{
		return _registered;
	}
	
	public void setRegistered(boolean state)
	{
		_registered = state;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}
	
	public void setAbsorbedHpPercent(int percent)
	{
		_absorbedHpPercent = percent;
	}
	
	public boolean isValid(int itemId)
	{
		return _itemId == itemId && _absorbedHpPercent < 50;
	}
}