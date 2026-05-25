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

public class CachedDataValueObject<T> extends CachedDataValue
{
	private final Converter<T> converter;
	private T value;
	
	CachedDataValueObject(String valueName, String defaultValue, int charId, Converter<T> converter)
	{
		super(valueName, defaultValue, charId);
		this.converter = converter;
	}
	
	@Override
	void load()
	{
		super.load();
		value = converter.fromString(getValue());
	}
	
	public T get()
	{
		return value;
	}
	
	public void set(T value)
	{
		this.value = value;
		setValue(converter.toString(value));
	}
	
	public interface Converter<T>
	{
		T fromString(String value);
		
		String toString(T value);
	}
}