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
package ext.mods.gameserver.data;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import ext.mods.commons.logging.CLogger;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Player;

public final class HTMLData extends AbstractLocaleData
{
	private static final CLogger LOGGER = new CLogger(HTMLData.class.getName());
	
	private final Map<Locale, Map<String, String>> _data = new HashMap<>();
	
	@Override
	public void load()
	{
		for (var locale : Config.LOCALES)
		{
			_data.put(locale, new ConcurrentHashMap<>());
			doLoad(locale);
		}
	}
	
	public void reload()
	{
		LOGGER.info("HTMLData has been cleared ({} entries).", _data.size());
		
		for (var locale : Config.LOCALES)
		{
			_data.get(locale).clear();
			doLoad(locale);
		}
	}
	
	private void doLoad(Locale locale)
	{
		final Path localeBasePath = resolve(locale, "");
		try
		{
			Files.walkFileTree(localeBasePath, new SimpleFileVisitor<Path>()
			{
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				{
					final var fileKey = localeBasePath.relativize(file).toString().replace("\\", "/");
					if (Files.isDirectory(file) || !(fileKey.endsWith(".htm") || fileKey.endsWith(".html")))
						return FileVisitResult.CONTINUE;
					
					ForkJoinPool.commonPool().execute(() ->
					{
						try
						{
							var content = readString(file);
							_data.get(locale).put(fileKey, content);
						}
						catch (DataException e)
						{
							e.printStackTrace();
						}
					});
					
					return FileVisitResult.CONTINUE;
				}
			});
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String get(Locale locale, String key)
	{
		return getHtm(locale, key);
	}
	
	public String getHtm(Player player, String file)
	{
		return getHtm(player.getLocale(), file);
	}
	
	public String getHtm(Locale locale, String file)
	{
		var result = _data.get(locale).get(file);
		if (result == null)
		{
			result = _data.get(Config.DEFAULT_LOCALE).get(file);
			if (result == null)
				return "<html><body>Not found file: " + file + "</body></html>";
		}
		return result;
	}
	
	public boolean exists(Player player, String file)
	{
		return exists(player.getLocale(), file);
	}
	
	public boolean exists(Locale locale, String file)
	{
		var path = resolve(locale, file);
		
		if (Files.exists(path) && !Files.isDirectory(path))
			return true;
		
		if (!locale.equals(Config.DEFAULT_LOCALE))
			return exists(Config.DEFAULT_LOCALE, file);
		
		return false;
	}
	
	public static HTMLData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final HTMLData INSTANCE = new HTMLData();
	}
}