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

import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.records.Fish;

import org.w3c.dom.Document;

/**
 * This class loads and stores {@link Fish} infos.<br>
 * TODO Plain wrong values and system, have to be reworked entirely.
 */
public class FishData implements IXmlReader
{
	private final List<Fish> _fish = new ArrayList<>();
	
	protected FishData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("xml/fish.xml");
		LOGGER.info("Loaded {} fish.", _fish.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "fish", fishNode -> _fish.add(new Fish(parseAttributes(fishNode)))));
	}
	
	/**
	 * Get a random {@link Fish} based on level, type and group.
	 * @param lvl : the fish level to check.
	 * @param type : the fish type to check.
	 * @param group : the fish group to check.
	 * @return a Fish with good criterias.
	 */
	public Fish getFish(int lvl, int type, int group)
	{
		return Rnd.get(_fish.stream().filter(f -> f.level() == lvl && f.type() == type && f.group() == group).toList());
	}
	
	public static FishData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final FishData INSTANCE = new FishData();
	}
}