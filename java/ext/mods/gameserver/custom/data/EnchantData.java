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

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.records.custom.EnchantScroll;

import org.w3c.dom.Document;

public class EnchantData implements IXmlReader
{
	private final Map<Integer, EnchantScroll> _data = new HashMap<>();
	
	public EnchantData()
	{
		load();
	}
	
	public void reload()
	{
		_data.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/enchants.xml");
		LOGGER.info("Loaded {} enchant scroll data.", _data.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "scroll", scrollNode ->
		{
			final StatSet set = parseAttributes(scrollNode);
			forEach(scrollNode, "settings", settingsNode -> set.putAll(parseAttributes(settingsNode)));
			forEach(scrollNode, "chances", chancesNode -> set.putAll(parseAttributes(chancesNode)));
			forEach(scrollNode, "announce", announceNode -> set.putAll(parseAttributes(announceNode)));
			_data.put(set.getInteger("id"), new EnchantScroll(set));
		}));
	}
	
	public EnchantScroll getEnchantScroll(ItemInstance item)
	{
		return _data.get(item.getItemId());
	}
	
	public static EnchantData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EnchantData INSTANCE = new EnchantData();
	}
}