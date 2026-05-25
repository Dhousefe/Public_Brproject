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
package ext.mods.gameserver.data.cache;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import ext.mods.commons.logging.CLogger;

import ext.mods.Config;
import ext.mods.gameserver.enums.CrestType;

/**
 * A cache storing clan crests under .dds format.<br>
 * <br>
 * Size integrity checks are made on crest save, deletion, get and also during server first load.
 */
public class CrestCache
{
	private static final CLogger LOGGER = new CLogger(CrestCache.class.getName());
	
	private static final String CRESTS_DIR = Config.DATA_PATH.resolve("crests").toString();
	
	private final Map<Integer, byte[]> _crests = new HashMap<>();
	
	public CrestCache()
	{
		load();
	}
	
	/**
	 * Initial method used to load crests data and store it in server memory.<br>
	 * <br>
	 * If a file doesn't meet integrity checks requirements, it is simply deleted.
	 */
	private final void load()
	{
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(CRESTS_DIR), new DdsFilter()))
		{
			for (Path filePath : stream)
			{
				final String fileName = filePath.getFileName().toString();
				
				final byte[] data = Files.readAllBytes(filePath);
				
				for (CrestType type : CrestType.values())
				{
					if (!fileName.startsWith(type.getPrefix()))
						continue;
					
					if (data.length != type.getSize())
					{
						if (Files.deleteIfExists(filePath))
							LOGGER.warn("The data for crest {} is invalid. The crest has been deleted.", fileName);
					}
					else
						_crests.put(Integer.valueOf(fileName.substring(type.getPrefix().length(), fileName.length() - 4)), data);
					
					break;
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Error loading crest files.", e);
		}
		
		LOGGER.info("Loaded {} crests.", _crests.size());
	}
	
	/**
	 * Clean the crest cache, and reload it.
	 */
	public final void reload()
	{
		_crests.clear();
		
		load();
	}
	
	/**
	 * @param type : The {@link CrestType} to refer on. Size integrity check is made based on it.
	 * @param id : The crest id data to retrieve.
	 * @return a byte array or null if id wasn't found.
	 */
	public final byte[] getCrest(CrestType type, int id)
	{
		byte[] data = _crests.get(id);
		
		if (data == null || data.length != type.getSize())
			return null;
		
		return data;
	}
	
	/**
	 * Remove the crest from both memory and file system.
	 * @param type : The {@link CrestType} to refer on. Size integrity check is made based on it.
	 * @param id : The crest id to delete.
	 */
	public final void removeCrest(CrestType type, int id)
	{
		final byte[] data = _crests.get(id);
		if (data == null || data.length != type.getSize())
			return;
		
		_crests.remove(id);
		
		final Path filePath = Paths.get(CRESTS_DIR, type.getPrefix() + id + ".dds");
		try
		{
			Files.deleteIfExists(filePath);
		}
		catch (Exception e)
		{
			LOGGER.error("Error deleting crest file: {}.", e, filePath.getFileName());
		}
	}
	
	/**
	 * Store the crest as a physical file and in cache memory.
	 * @param type : The {@link CrestType} used to register the crest. Crest name uses it.
	 * @param id : The crest id to register this new crest.
	 * @param data : The crest data to store.
	 * @return true if the crest has been successfully saved, false otherwise.
	 */
	public final boolean saveCrest(CrestType type, int id, byte[] data)
	{
		final Path filePath = Paths.get(CRESTS_DIR, type.getPrefix() + id + ".dds");
		
		if (data.length != type.getSize())
		{
			LOGGER.warn("The data for crest {} is invalid. Saving process is aborted.", filePath.getFileName());
			return false;
		}
		
		try
		{
			Files.write(filePath, data);
		}
		catch (Exception e)
		{
			LOGGER.error("Error saving crest file: {}.", e, filePath.getFileName());
			return false;
		}
		
		_crests.put(id, data);
		
		return true;
	}
	
	private static class DdsFilter implements DirectoryStream.Filter<Path>
	{
		@Override
		public boolean accept(Path file)
		{
			final String fileName = file.getFileName().toString();
			
			return (fileName.startsWith("Crest_") || fileName.startsWith("LargeCrest_") || fileName.startsWith("AllyCrest_")) && fileName.endsWith(".dds");
		}
	}
	
	public static CrestCache getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CrestCache INSTANCE = new CrestCache();
	}
}