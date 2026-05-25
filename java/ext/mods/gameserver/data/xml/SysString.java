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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.data.xml.IXmlReader;

import ext.mods.Config;
import ext.mods.gameserver.data.AbstractLocaleData;

import org.w3c.dom.Document;

public final class SysString extends AbstractLocaleData implements IXmlReader
{
	private final Map<Locale, Map<String, String>> _data = new HashMap<>();
	
	private Locale _activeLocale;
	
	public void reload()
	{
		LOGGER.info("SysString has been cleared ({} entries).", _data.size());
		
		for (var locale : Config.LOCALES)
		{
			Map<String, String> localeMap = _data.get(locale);
			if (localeMap != null)
			{
				localeMap.clear();
			}
		}
		load();
	}
	
	@Override
	public void load()
	{
		for (var locale : Config.LOCALES)
		{
			_activeLocale = locale;
			_data.put(locale, new ConcurrentHashMap<>());
			parseFile(resolve(locale, "sysstring.xml").toString());
			locale = null;
		}
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		if (_activeLocale == null)
		{
			LOGGER.warn("SysString.parseDocument: _activeLocale is null, skipping parse.");
			return;
		}
		
		Map<String, String> localeMap = _data.get(_activeLocale);
		if (localeMap == null)
		{
			LOGGER.warn("SysString.parseDocument: locale map not found for locale: " + _activeLocale);
			return;
		}
		
		forEach(doc, "list", root -> forEach(root, "string", node ->
		{
			localeMap.put(parseString(node.getAttributes(), "key"), node.getTextContent());
		}));
	}
	
	@Override
	public String get(Locale locale, String key)
	{
		if (locale == null)
		{
			locale = Config.DEFAULT_LOCALE;
		}
		
		Map<String, String> localeMap = _data.get(locale);
		if (localeMap == null)
		{
			locale = Config.DEFAULT_LOCALE;
			localeMap = _data.get(locale);
			if (localeMap == null)
			{
				return "missing sysstring: " + key + " (locale not loaded)";
			}
		}
		
		var result = localeMap.get(key);
		if (result == null)
		{
			if (locale != Config.DEFAULT_LOCALE)
			{
				Map<String, String> defaultMap = _data.get(Config.DEFAULT_LOCALE);
				if (defaultMap != null)
				{
					result = defaultMap.get(key);
				}
			}
			
			if (result == null)
				return "missing sysstring: " + key;
		}
		return result;
	}
	
	public static SysString getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SysString INSTANCE = new SysString();
	}
}