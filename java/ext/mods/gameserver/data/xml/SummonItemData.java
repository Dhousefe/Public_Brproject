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
package ext.mods.gameserver.data.xml;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.holder.IntIntHolder;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * This class loads and stores summon items.<br>
 * TODO Delete it and move it back wherever it belongs.
 */
public class SummonItemData implements IXmlReader
{
	private final Map<Integer, IntIntHolder> _items = new HashMap<>();
	
	protected SummonItemData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/summonItems.xml");
		LOGGER.info("Loaded {} summon items.", _items.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "item", itemNode ->
		{
			final NamedNodeMap attrs = itemNode.getAttributes();
			final int itemId = parseInteger(attrs, "id");
			final int npcId = parseInteger(attrs, "npcId");
			final int summonType = parseInteger(attrs, "summonType");
			_items.put(itemId, new IntIntHolder(npcId, summonType));
		}));
	}
	
	public IntIntHolder getSummonItem(int itemId)
	{
		return _items.get(itemId);
	}
	
	public static SummonItemData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SummonItemData INSTANCE = new SummonItemData();
	}
}