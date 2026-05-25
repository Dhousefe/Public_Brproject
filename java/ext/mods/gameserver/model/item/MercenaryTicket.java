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
package ext.mods.gameserver.model.item;

import java.util.Arrays;

import ext.mods.commons.data.StatSet;

import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.items.TicketType;

public final class MercenaryTicket
{
	private final int _itemId;
	private final TicketType _type;
	private final boolean _isStationary;
	private final int _npcId;
	private final int _maxAmount;
	private final CabalType[] _ssq;
	
	public MercenaryTicket(StatSet set)
	{
		_itemId = set.getInteger("itemId");
		_type = set.getEnum("type", TicketType.class);
		_isStationary = set.getBool("stationary");
		_npcId = set.getInteger("npcId");
		_maxAmount = set.getInteger("maxAmount");
		
		final String[] ssq = set.getStringArray("ssq");
		
		_ssq = new CabalType[ssq.length];
		for (int i = 0; i < ssq.length; i++)
			_ssq[i] = Enum.valueOf(CabalType.class, ssq[i]);
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public TicketType getType()
	{
		return _type;
	}
	
	public boolean isStationary()
	{
		return _isStationary;
	}
	
	public int getNpcId()
	{
		return _npcId;
	}
	
	public int getMaxAmount()
	{
		return _maxAmount;
	}
	
	public boolean isSsqType(CabalType type)
	{
		return Arrays.asList(_ssq).contains(type);
	}
}