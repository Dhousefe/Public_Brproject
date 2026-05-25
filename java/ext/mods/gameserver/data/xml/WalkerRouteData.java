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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.model.location.WalkerLocation;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

/**
 * This class loads and stores routes for walking NPCs, under a List of {@link WalkerLocation} ; the key being the npcId.
 */
public class WalkerRouteData implements IXmlReader
{
	private final Map<String, Map<String, List<WalkerLocation>>> _routes = new HashMap<>();
	
	protected WalkerRouteData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/walkerRoutes.xml");
		LOGGER.info("Loaded {} walking routes.", _routes.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "route", routeNode ->
		{
			final NamedNodeMap routeAttrs = routeNode.getAttributes();
			final String routeName = parseString(routeAttrs, "name");
			final Map<String, List<WalkerLocation>> routeList = new HashMap<>();
			
			forEach(routeNode, "npc", npcNode ->
			{
				final NamedNodeMap npcAttrs = npcNode.getAttributes();
				final String npcName = parseString(npcAttrs, "name");
				final List<WalkerLocation> nodeList = new ArrayList<>();
				
				forEach(npcNode, "node", nodeNode -> nodeList.add(new WalkerLocation(parseAttributes(nodeNode))));
				routeList.put(npcName, nodeList);
			});
			_routes.put(routeName, routeList);
		}));
	}
	
	public void reload()
	{
		_routes.clear();
		
		load();
	}
	
	public Map<String, Map<String, List<WalkerLocation>>> getWalkerRoutes()
	{
		return _routes;
	}
	
	public List<WalkerLocation> getWalkerRoute(String routeName, String npcName)
	{
		final Map<String, List<WalkerLocation>> npcRoutes = _routes.get(routeName);
		if (npcRoutes == null)
			return Collections.emptyList();
		
		return npcRoutes.get(npcName);
	}
	
	public static WalkerRouteData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final WalkerRouteData INSTANCE = new WalkerRouteData();
	}
}