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
package ext.mods.gameserver.custom.data;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ext.mods.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class AuctionCurrencies implements IXmlReader
{
	private final Map<String, Integer> _currencyMap = new HashMap<>();
	
	public AuctionCurrencies()
	{
		load();
	}
	
	public void reload()
	{
		_currencyMap.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/auctionCurrencies.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "currency", itemsNode ->
		{
			final NamedNodeMap attrs = itemsNode.getAttributes();
			
			String name = parseString(attrs, "name");
			int id = parseInteger(attrs, "id");
			
			_currencyMap.put(name, id);
		}));
	}
	
	public int getCurrencyId(String name)
	{
		return _currencyMap.getOrDefault(name, -1);
	}
	
	public String getCurrencyName(int currencyId)
	{
		return _currencyMap.entrySet().stream().filter(entry -> entry.getValue().equals(currencyId)).map(Map.Entry::getKey).findFirst().orElse("Unknown Currency");
	}
	
	public Set<String> getCurrencyNames()
	{
		return _currencyMap.keySet();
	}
	
	public static AuctionCurrencies getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final AuctionCurrencies INSTANCE = new AuctionCurrencies();
	}
}