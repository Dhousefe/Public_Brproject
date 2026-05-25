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

import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.location.Point2D;
import ext.mods.gameserver.model.manor.ManorArea;
import ext.mods.gameserver.model.manor.Seed;
import ext.mods.gameserver.model.residence.castle.Castle;

import org.w3c.dom.Document;

/**
 * This class loads and stores {@link ManorArea}s.<br>
 * <br>
 * {@link ManorArea} is a polygon/territory linked to a specific {@link Castle}. This allow {@link Seed} checks while sowing.
 */
public class ManorAreaData implements IXmlReader
{
	private final List<ManorArea> _manorAreas = new ArrayList<>();
	
	protected ManorAreaData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/manorAreas.xml");
		LOGGER.info("Loaded {} manor areas.", _manorAreas.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		final List<Point2D> coords = new ArrayList<>();
		
		forEach(doc, "list", listNode -> forEach(listNode, "area", areaNode ->
		{
			final StatSet set = parseAttributes(areaNode);
			
			forEach(areaNode, "node", nodeNode -> coords.add(parsePoint2D(nodeNode)));
			set.set("coords", coords);
			
			_manorAreas.add(new ManorArea(set));
			
			coords.clear();
		}));
	}
	
	/**
	 * @return The {@List} of available {@link ManorArea}s.
	 */
	public final List<ManorArea> getManorAreas()
	{
		return _manorAreas;
	}
	
	/**
	 * @param monster : The {@link Monster} to evaluate.
	 * @return The {@link ManorArea} of the given {@link Monster}.
	 */
	public final ManorArea getManorArea(Monster monster)
	{
		final Location loc = monster.getSpawnLocation();
		
		return _manorAreas.stream().filter(ma -> ma.isInside(loc.getX(), loc.getY())).findFirst().orElse(null);
	}
	
	public static ManorAreaData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ManorAreaData INSTANCE = new ManorAreaData();
	}
}