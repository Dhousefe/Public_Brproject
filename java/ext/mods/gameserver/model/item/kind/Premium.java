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
package ext.mods.gameserver.model.item.kind;

public class Premium
{
	private int _charId;
	private int _itemId;
	private long _activationTime;
	
	public static final int MODIFIER_XP = 0;
	public static final int MODIFIER_SP = 1;
	public static final int MODIFIER_PARTY_XP = 2;
	public static final int MODIFIER_PARTY_SP = 3;
	public static final int MODIFIER_DROP_ADENA = 4;
	public static final int MODIFIER_DROP_ITEMS = 5;
	public static final int MODIFIER_SPOIL = 6;
	public static final int MODIFIER_QUEST = 7;
	public static final int MODIFIER_QUEST_ADENA = 8;
	public static final int MODIFIER_DROP_SEAL_STONES = 9;
	
	public int getCharId()
	{
		return _charId;
	}
	
	public void setCharId(int charId)
	{
		_charId = charId;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}
	
	public long getActivationTime()
	{
		return _activationTime;
	}
	
	public void setActivationTime(long activationTime)
	{
		_activationTime = activationTime;
	}
}