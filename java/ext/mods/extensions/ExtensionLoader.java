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
package ext.mods.extensions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import ext.mods.extensions.interfaces.L2JExtension;

public class ExtensionLoader
{
	private static final String LIBS_PATH = "../libs/";
	private static final Map<String, L2JExtension> loadedExtensions = new HashMap<>();
	
	public static void loadExtensions()
	{
		File folder = new File(LIBS_PATH);
		if (!folder.exists() || !folder.isDirectory())
		{
			System.out.println("[ExtensionLoader] Pasta libs não encontrada.");
			return;
		}
		
		File[] jars = folder.listFiles((dir, name) -> name.endsWith(".ext.jar"));
		if (jars == null || jars.length == 0)
		{
			return;
		}
		
		for (File jar : jars)
		{
			loadJar(jar);
		}
	}
	
	private static void loadJar(File jar)
	{
		try (JarFile jarFile = new JarFile(jar);
			URLClassLoader childLoader = new URLClassLoader(new URL[]
			{
				jar.toURI().toURL()
			}, ExtensionLoader.class.getClassLoader()))
		{
			
			Enumeration<JarEntry> entries = jarFile.entries();
			while (entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				
				if (entry.isDirectory() || !entry.getName().endsWith(".class"))
				{
					continue;
				}
				
				String className = entry.getName().replace("/", ".").replace(".class", "");
				try
				{
					Class<?> clazz = Class.forName(className, true, childLoader);
					
					if (!L2JExtension.class.isAssignableFrom(clazz))
					{
						continue;
					}
					
					L2JExtension extension = (L2JExtension) clazz.getDeclaredConstructor().newInstance();
					
					if (loadedExtensions.containsKey(extension.getName()))
					{
						continue;
					}
					
					extension.onLoad();
					loadedExtensions.put(extension.getName(), extension);
					
				}
				catch (ClassNotFoundException e)
				{
					System.out.println("[ExtensionLoader] Classe não encontrada: " + className);
				}
				catch (Throwable t)
				{
					System.out.println("[ExtensionLoader] Erro ao carregar classe: " + className);
					t.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			System.out.println("[ExtensionLoader] Erro ao abrir o JAR: " + jar.getName());
			e.printStackTrace();
		}
	}
	
	public static void disableExtension(String name)
	{
		L2JExtension ext = loadedExtensions.get(name);
		if (ext != null)
		{
			ext.onDisable();
		}
	}
	
	public static Set<String> listExtensions()
	{
		return loadedExtensions.keySet();
	}
}
