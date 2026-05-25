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
package ext.mods.gameserver.model.craft;

import java.util.ArrayList;
import java.util.Arrays;

import ext.mods.gameserver.model.records.ManufactureItem;

/**
 * A datacontainer used by private workshop system. It retains a List of {@link ManufactureItem}s, the store name and the shop state.
 */
public class ManufactureList extends ArrayList<ManufactureItem>
{
	private static final long serialVersionUID = 1L;
	
	private boolean _confirmed;
	private boolean _isDwarven;
	
	private String _storeName;
	
	public void set(ManufactureItem[] items)
	{
		addAll(Arrays.asList(items));
	}
	
	public boolean hasConfirmed()
	{
		return _confirmed;
	}
	
	public void setConfirmedTrade(boolean confirmed)
	{
		_confirmed = confirmed;
	}
	
	public boolean isDwarven()
	{
		return _isDwarven;
	}
	
	public void setState(boolean isDwarven)
	{
		_isDwarven = isDwarven;
	}
	
	public String getStoreName()
	{
		return _storeName;
	}
	
	public void setStoreName(String storeName)
	{
		_storeName = storeName;
	}
}