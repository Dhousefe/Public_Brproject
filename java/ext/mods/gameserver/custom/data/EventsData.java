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
import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.records.custom.EventItem;
import ext.mods.gameserver.model.records.custom.EventsInfo;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class EventsData implements IXmlReader
{
	private final List<EventsInfo> _events = new ArrayList<>();
	
	protected EventsData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/events.xml");
		LOGGER.info("Loaded {} events.", _events.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "event", event ->
		{
			String eventName = parseString(event.getAttributes(), "name");
			
			final List<EventItem> items = new ArrayList<>();
			
			forEach(event, "item", itemNode ->
			{
				final NamedNodeMap attrs = itemNode.getAttributes();
				int id = parseInteger(attrs, "id");
				int count = parseInteger(attrs, "count");
				int chance = parseInteger(attrs, "chance");
				int minLvl = parseInteger(attrs, "minLvl", 1);
				
				items.add(new EventItem(id, count, chance, minLvl));
			});
			
			_events.add(new EventsInfo(eventName, items));
		}));
	}
	
	public EventsInfo getEventsData(String eventName)
	{
		return _events.stream().filter(event -> event.eventName().equals(eventName)).findFirst().orElse(null);
	}
	
	public static EventsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EventsData INSTANCE = new EventsData();
	}
}