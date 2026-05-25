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
package ext.mods.Crypta;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import ext.mods.aghation.holder.AgathionHolder;
import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

public class AgathionData implements IXmlReader
{
	private final Map<Integer, List<AgathionHolder>> _listagation = new HashMap<>();
	public AgathionData()
	{
		load();
	}
	
	public void reload()
	{
		_listagation.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/agathionList.xml");
		LOGGER.info("Loaded {" + _listagation.size() + "} Agathion list.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "items", itemNode ->
			{
				StatSet set = parseAttributes(itemNode);
				int id = set.getInteger("id");
				
				forEach(itemNode, "agathion", holder ->
				{
					StatSet npcAttributes = parseAttributes(holder);
					AgathionHolder agathionholder = new AgathionHolder(npcAttributes);
					
					_listagation.computeIfAbsent(id, k -> new ArrayList<>()).add(agathionholder);
				});
			});
		});
	}
	
	public List<AgathionHolder> getAgathionsByItemId(int boxId)
	{
		return _listagation.getOrDefault(boxId, List.of());
	}
	
	public static AgathionData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final AgathionData _instance = new AgathionData();
	}
}