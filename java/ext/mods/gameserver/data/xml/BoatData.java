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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.gameserver.enums.boats.BoatDock;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.actor.Boat;
import ext.mods.gameserver.model.actor.template.CreatureTemplate;
import ext.mods.gameserver.model.boat.BoatEngine;
import ext.mods.gameserver.model.boat.BoatItinerary;
import ext.mods.gameserver.model.location.BoatLocation;
import ext.mods.gameserver.taskmanager.BoatTaskManager;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class BoatData implements IXmlReader
{
	private final List<BoatItinerary> _itineraries = new ArrayList<>();
	private final Map<Integer, Boat> _boats = new HashMap<>();
	
	private final CreatureTemplate _template;
	
	protected BoatData()
	{
		final StatSet set = new StatSet();
		
		set.set("str", 0);
		set.set("con", 0);
		set.set("dex", 0);
		set.set("int", 0);
		set.set("wit", 0);
		set.set("men", 0);
		
		set.set("hp", 50000);
		
		set.set("hpRegen", 3.e-3f);
		set.set("mpRegen", 3.e-3f);
		
		set.set("radius", 0);
		set.set("height", 0);
		
		set.set("pAtk", 0);
		set.set("mAtk", 0);
		set.set("pDef", 100);
		set.set("mDef", 100);
		
		set.set("runSpd", 0);
		
		_template = new CreatureTemplate(set);
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "itinerary", itineraryNode ->
		{
			final NamedNodeMap attrs = itineraryNode.getAttributes();
			
			final BoatDock dock1 = parseEnum(attrs, BoatDock.class, "dock1");
			final BoatDock dock2 = parseEnum(attrs, BoatDock.class, "dock2", null);
			
			final int item1 = parseInteger(attrs, "item1", 0);
			final int item2 = parseInteger(attrs, "item2", 0);
			
			final int heading = parseInteger(attrs, "heading");
			
			final List<BoatLocation[]> routes = new ArrayList<>();
			
			forEach(itineraryNode, "route", routeNode ->
			{
				final List<BoatLocation> nodes = new ArrayList<>();
				
				forEach(routeNode, "node", nodeNode -> nodes.add(new BoatLocation(parseAttributes(nodeNode))));
				
				routes.add(nodes.toArray(new BoatLocation[0]));
			});
			
			_itineraries.add(new BoatItinerary(dock1, dock2, item1, item2, heading, routes.toArray(new BoatLocation[0][])));
		}));
	}
	
	public void reload()
	{
		_itineraries.clear();
		
		_boats.values().forEach(Boat::deleteMe);
		_boats.clear();
		
		for (BoatDock dock : BoatDock.VALUES)
			dock.setBusy(false);
		
		BoatTaskManager.getInstance().clear();
		
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/boatRoutes.xml");
		LOGGER.info("Loaded {} boat itineraries.", _itineraries.size());
		
		_itineraries.forEach(itinerary -> BoatTaskManager.getInstance().add(new BoatEngine(itinerary)));
	}
	
	public List<BoatItinerary> getItineraries()
	{
		return _itineraries;
	}
	
	public Boat getBoat(int boatId)
	{
		return _boats.get(boatId);
	}
	
	public Boat getNewBoat(BoatItinerary itinerary)
	{
		final Boat boat = new Boat(IdFactory.getInstance().getNextId(), _template);
		boat.spawnMe(itinerary);
		
		_boats.put(boat.getObjectId(), boat);
		
		return boat;
	}
	
	public static final BoatData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final BoatData INSTANCE = new BoatData();
	}
}