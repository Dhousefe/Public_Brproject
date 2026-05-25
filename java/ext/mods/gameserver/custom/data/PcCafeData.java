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

import org.w3c.dom.Document;

/**
 * @author SweeTs
 */
public final class PcCafeData implements IXmlReader
{
	private final Map<String, String> _cafeData = new HashMap<>();
	
	protected PcCafeData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/pcCafe.xml");
		LOGGER.info("Loaded {} pcCafe variables.", _cafeData.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode ->
		{
			forEach(listNode, "variable", accessNode ->
			{
				final StatSet set = parseAttributes(accessNode);
				_cafeData.put(set.getString("name"), set.getString("value"));
			});
		});
	}
	
	public void reload()
	{
		_cafeData.clear();
		load();
	}
	
	public boolean getCafeBool(final String key, final boolean defaultValue)
	{
		final Object val = _cafeData.get(key);
		
		if (val instanceof Boolean bool)
			return bool;
		
		if (val instanceof String str)
			return Boolean.parseBoolean(str);
		
		if (val instanceof Number num)
			return num.intValue() != 0;
		
		return defaultValue;
	}
	
	public int getCafeInt(final String key, final int defaultValue)
	{
		final Object val = _cafeData.get(key);
		
		if (val instanceof Number num)
			return num.intValue();
		
		if (val instanceof String str)
			return Integer.parseInt(str);
		
		if (val instanceof Boolean bool)
			return bool ? 1 : 0;
		
		return defaultValue;
	}
	
	public static PcCafeData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final PcCafeData INSTANCE = new PcCafeData();
	}
}