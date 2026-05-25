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
package ext.mods.commons.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Locale;

public class Locator
{
	
	private Locator()
	{
	}
	
	
	public static File getClassSource(Class<?> c)
	{
		final String classResource = c.getName().replace('.', '/') + ".class";
		return getResourceSource(c.getClassLoader(), classResource);
	}
	
	
	public static File getResourceSource(ClassLoader classLoader, String resource)
	{
		ClassLoader c = classLoader;
		if (c == null)
		{
			c = Locator.class.getClassLoader();
		}
		URL url = null;
		if (c == null)
		{
			url = ClassLoader.getSystemResource(resource);
		}
		else
		{
			url = c.getResource(resource);
		}
		if (url != null)
		{
			final String u = url.toString();
			if (u.startsWith("jar:file:"))
			{
				final int pling = u.indexOf('!');
				final String jarName = u.substring(4, pling);
				return new File(fromURI(jarName));
			}
			else if (u.startsWith("file:"))
			{
				final int tail = u.indexOf(resource);
				final String dirName = u.substring(0, tail);
				return new File(fromURI(dirName));
			}
		}
		return null;
	}
	

	public static String fromURI(String uriValue)
	{
		String uri = uriValue;
		URL url = null;
		try
		{
		
			url = URI.create(uri).toURL();
		}
		catch (MalformedURLException emYouEarlEx)
		{
			
		}
		
		if ((url == null) || !("file".equals(url.getProtocol())))
		{
			throw new IllegalArgumentException("Can only handle valid file: URIs");
		}
		
		final StringBuilder buf = new StringBuilder(url.getHost());
		if (buf.length() > 0)
		{
			buf.insert(0, File.separatorChar).insert(0, File.separatorChar);
		}
		final String file = url.getFile();
		final int queryPos = file.indexOf('?');
		buf.append((queryPos < 0) ? file : file.substring(0, queryPos));
		uri = buf.toString().replace('/', File.separatorChar);
		if ((File.pathSeparatorChar == ';') && uri.startsWith("\\") && (uri.length() > 2) && Character.isLetter(uri.charAt(1)) && (uri.lastIndexOf(':') > -1))
		{
			uri = uri.substring(1);
		}
		
		return decodeUri(uri);
	}
	


	private static String decodeUri(String uri)
	{
		if (uri.indexOf('%') == -1)
		{
			return uri;
		}
		final StringBuilder sb = new StringBuilder();
		final CharacterIterator iter = new StringCharacterIterator(uri);
		for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next())
		{
			if (c == '%')
			{
				final char c1 = iter.next();
				if (c1 != CharacterIterator.DONE)
				{
					final int i1 = Character.digit(c1, 16);
					final char c2 = iter.next();
					if (c2 != CharacterIterator.DONE)
					{
						final int i2 = Character.digit(c2, 16);
						sb.append((char) ((i1 << 4) + i2));
					}
				}
			}
			else
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}
	

	public static File getToolsJar()
	{
		boolean toolsJarAvailable = false;
		try
		{
			Class.forName("com.sun.tools.javac.Main");
			toolsJarAvailable = true;
		}
		catch (Exception e)
		{
			try
			{
				Class.forName("sun.tools.javac.Main");
				toolsJarAvailable = true;
			}
			catch (Exception e2)
			{
			}
		}
		if (toolsJarAvailable)
		{
			return null;
		}
		String javaHome = System.getProperty("java.home");
		if (javaHome.toLowerCase(Locale.US).endsWith("jre"))
		{
			javaHome = javaHome.substring(0, javaHome.length() - 4);
		}
		final File toolsJar = new File(javaHome + "/lib/tools.jar");
		if (!toolsJar.exists())
		{
			System.out.println("Unable to locate tools.jar. " + "Expected to find it in " + toolsJar.getPath());
			return null;
		}
		return toolsJar;
	}
	
	/**
	 * Get an array of URLs representing all of the jar files in the given location. If the location is a file, it is returned as the only element of the array. If the location is a directory, it is scanned for jar files.
	 * @param location the location to scan for Jars.
	 * @return an array of URLs for all jars in the given location.
	 * @exception MalformedURLException if the URLs for the jars cannot be formed.
	 */
	public static URL[] getLocationURLs(File location) throws MalformedURLException
	{
		return getLocationURLs(location, new String[]
		{
			".jar"
		});
	}
	
	/**
	 * Get an array of URLs representing all of the files of a given set of extensions in the given location. If the location is a file, it is returned as the only element of the array. If the location is a directory, it is scanned for matching files.
	 * @param location the location to scan for files.
	 * @param extensions an array of extension that are to match in the directory search.
	 * @return an array of URLs of matching files.
	 * @exception MalformedURLException if the URLs for the files cannot be formed.
	 */
	public static URL[] getLocationURLs(File location, String[] extensions) throws MalformedURLException
	{
		URL[] urls = new URL[0];
		
		if (!location.exists())
		{
			return urls;
		}
		if (!location.isDirectory())
		{
			urls = new URL[1];
			final String path = location.getPath();
			for (String extension : extensions)
			{
				if (path.toLowerCase().endsWith(extension))
				{
					urls[0] = location.toURI().toURL();
					break;
				}
			}
			return urls;
		}
		final File[] matches = location.listFiles((FilenameFilter) (dir, name) ->
		{
			for (String extension : extensions)
			{
				if (name.toLowerCase().endsWith(extension))
				{
					return true;
				}
			}
			return false;
		});
		urls = new URL[matches.length];
		for (int i = 0; i < matches.length; ++i)
		{
			urls[i] = matches[i].toURI().toURL();
		}
		return urls;
	}
}
