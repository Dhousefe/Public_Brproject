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
package ext.mods.gameserver.model.buylist;

import java.util.LinkedHashMap;

/**
 * A datatype used to hold buylists. Each buylist got a Map of {@link Product}.<br>
 * For security reasons and to avoid crafted packets, we added npcId aswell.
 */
public class NpcBuyList extends LinkedHashMap<Integer, Product>
{
	private static final long serialVersionUID = 1L;
	
	private final int _listId;
	
	private int _npcId;
	
	public NpcBuyList(int listId)
	{
		_listId = listId;
	}
	
	public int getListId()
	{
		return _listId;
	}
	
	public int getNpcId()
	{
		return _npcId;
	}
	
	public void setNpcId(int id)
	{
		_npcId = id;
	}
	
	public void addProduct(Product product)
	{
		put(product.getItemId(), product);
	}
	
	public boolean isNpcAllowed(int npcId)
	{
		return _npcId == npcId;
	}
}