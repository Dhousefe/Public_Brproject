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
import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;

public class SkipData implements IXmlReader
{
	private static final List<Integer> _skip = new ArrayList<>();
	private boolean _enabled;
	
	public SkipData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/skippingItems.xml");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			final StatSet set = parseAttributes(listNode);
			_enabled = set.getBool("enabled", false);
			
			if (_enabled)
			{
				forEach(listNode, "item", itemNode ->
				{
					final StatSet itemSet = parseAttributes(itemNode);
					int itemId = itemSet.getInteger("id");
					_skip.add(itemId);
				});
				LOGGER.info("Loaded {} skip list templates.", _skip.size());
			}
			else
				LOGGER.warn("Skipping items list is disabled. No items will be loaded from this list.");
		});
	}
	
	public boolean isSkipped(int itemId)
	{
		return _skip.contains(itemId);
	}
	
	public boolean isEnabled()
	{
		return _enabled;
	}
	
	public static SkipData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SkipData INSTANCE = new SkipData();
	}
}