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

import ext.mods.gameserver.model.records.custom.RatesHolder;

import org.w3c.dom.Document;

public class RatesData implements IXmlReader
{
	private final Map<Integer, RatesHolder> _ratess = new HashMap<>();
	
	public RatesData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/rates.xml");
		LOGGER.info(getClass().getSimpleName() + ": Loaded " + _ratess.size() + " rates.");
		
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> {
			forEach(listNode, "rate", greetNode -> {
				StatSet set = new StatSet(parseAttributes(greetNode));
				RatesHolder holder = new RatesHolder(set);
				_ratess.put(holder.getLevel(), holder);
			});
		});
	}
	
	public RatesHolder getRates(int lvl)
	{
		return _ratess.get(lvl);
	}
	
	public static RatesData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final RatesData _instance = new RatesData();
	}
}
