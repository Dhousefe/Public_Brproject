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
package ext.mods.commons.cached;

import java.util.ArrayList;
import java.util.List;

public class CachedData
{
	private final int charId;
	
	private final List<CachedDataValue> cachedDataValueList = new ArrayList<>();
	
	public CachedData(int charId)
	{
		this.charId = charId;
	}
	
	public void load()
	{
		for (var value : cachedDataValueList)
			value.load();
	}
	
	public void store()
	{
		for (var value : cachedDataValueList)
			value.update();
	}
	
	public static final boolean BOOLEAN_DEFAULT = true;
	public static final byte BYTE_DEFAULT = (byte) 0;
	public static final short SHORT_DEFAULT = (short) 0;
	public static final int INT_DEFAULT = 0;
	public static final long LONG_DEFAULT = 0;
	public static final float FLOAT_DEFAULT = 0.0f;
	public static final double DOUBLE_DEFAULT = 0.0d;
	public static final String STRING_DEFAULT = "";
	
	public CachedDataValueBoolean newBoolean(String name)
	{
		return newBoolean(name, BOOLEAN_DEFAULT);
	}
	
	public CachedDataValueBoolean newBoolean(String name, boolean defaultValue)
	{
		var value = new CachedDataValueBoolean(name, defaultValue, charId);
		cachedDataValueList.add(value);
		return value;
	}
	
	public CachedDataValueByte newByte(String name)
	{
		return newByte(name, BYTE_DEFAULT);
	}
	
	public CachedDataValueByte newByte(String name, byte defaultValue)
	{
		var value = new CachedDataValueByte(name, defaultValue, charId);
		cachedDataValueList.add(value);
		return value;
	}
	
	public CachedDataValueShort newShort(String name)
	{
		return newShort(name, SHORT_DEFAULT);
	}
	
	public CachedDataValueShort newShort(String name, short defaultValue)
	{
		var value = new CachedDataValueShort(name, defaultValue, charId);
		cachedDataValueList.add(value);
		return value;
	}
	
	public CachedDataValueInt newInt(String name)
	{
		return newInt(name, INT_DEFAULT);
	}
	
	public CachedDataValueInt newInt(String name, int defaultValue)
	{
		var value = new CachedDataValueInt(name, defaultValue, charId);
		cachedDataValueList.add(value);
		return value;
	}
	
	public CachedDataValueLong newLong(String name)
	{
		return newLong(name, LONG_DEFAULT);
	}
	
	public CachedDataValueLong newLong(String name, long defaultValue)
	{
		var value = new CachedDataValueLong(name, defaultValue, charId);
		cachedDataValueList.add(value);
		return value;
	}
	
	public CachedDataValueFloat newFloat(String name)
	{
		return newFloat(name, FLOAT_DEFAULT);
	}
	
	public CachedDataValueFloat newFloat(String name, float defaultValue)
	{
		var value = new CachedDataValueFloat(name, defaultValue, charId);
		cachedDataValueList.add(value);
		return value;
	}
	
	public CachedDataValueDouble newDouble(String name)
	{
		return newDouble(name, DOUBLE_DEFAULT);
	}
	
	public CachedDataValueDouble newDouble(String name, double defaultValue)
	{
		var value = new CachedDataValueDouble(name, defaultValue, charId);
		cachedDataValueList.add(value);
		return value;
	}
	
	public CachedDataValueString newString(String name)
	{
		return newString(name, STRING_DEFAULT);
	}
	
	public CachedDataValueString newString(String name, String defaultValue)
	{
		var value = new CachedDataValueString(name, defaultValue, charId);
		cachedDataValueList.add(value);
		return value;
	}
	
	public <T> CachedDataValueObject<T> newObject(String name, String defaultValue, CachedDataValueObject.Converter<T> converter)
	{
		var value = new CachedDataValueObject<>(name, defaultValue, charId, converter);
		cachedDataValueList.add(value);
		return value;
	}
}