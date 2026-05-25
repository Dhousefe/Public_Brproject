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
package ext.mods.gameserver.handler;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import ext.mods.commons.logging.CLogger;

public abstract class AbstractHandler<K, H>
{
	private static final CLogger LOGGER = new CLogger(AbstractHandler.class.getName());
	
	protected final Map<K, H> _entries = new HashMap<>();
	
	protected abstract void registerHandler(H handler);
	
	protected AbstractHandler(Class<H> handlerInterface, String className)
	{
		final String packagePath = "ext/mods/gameserver/handler/" + className;
		
		try
		{
			final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);
			while (resources.hasMoreElements())
			{
				final URL resource = resources.nextElement();
				
				if (resource.getProtocol().equals("file"))
				{
					final File directory = new File(resource.getFile());
					if (!directory.exists())
						continue;
					
					final String packageName = packagePath.replace("/", ".");
					
					for (String file : directory.list())
					{
						if (!file.endsWith(".class"))
							continue;
						
						final Class<?> clazz = Class.forName(packageName + "." + file.substring(0, file.length() - 6));
						if (!handlerInterface.isAssignableFrom(clazz) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
							continue;
						
						registerHandler(handlerInterface.cast(clazz.getDeclaredConstructor().newInstance()));
					}
				}
				else if (resource.getProtocol().equals("jar"))
				{
					final JarURLConnection conn = (JarURLConnection) resource.openConnection();
					try (JarFile jarFile = conn.getJarFile())
					{
						final Enumeration<JarEntry> entries = jarFile.entries();
						while (entries.hasMoreElements())
						{
							final JarEntry entry = entries.nextElement();
							final String entryName = entry.getName();
							
							if (!entryName.startsWith(packagePath) || !entryName.endsWith(".class"))
								continue;
							
							final Class<?> clazz = Class.forName(entryName.replace('/', '.').replace(".class", ""));
							if (!handlerInterface.isAssignableFrom(clazz) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers()))
								continue;
							
							registerHandler(handlerInterface.cast(clazz.getDeclaredConstructor().newInstance()));
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to load classes from package {}", e, packagePath);
		}
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public H getHandler(Object key)
	{
		return _entries.getOrDefault(key, null);
	}
}