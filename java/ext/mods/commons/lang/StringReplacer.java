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
package ext.mods.commons.lang;

/**
 * This class is used as a counter to replaceAll. It avoids to generate Strings and is roughly 50% faster than string method format().
 * @author xblx
 */
public final class StringReplacer
{
	private static final String DELIM_STR = "{}";
	
	private final StringBuilder _sb;
	
	public StringReplacer(String source)
	{
		_sb = new StringBuilder(source);
	}
	
	/**
	 * Replace all occurences of a String for another String.
	 * @param pattern : The String to replace.
	 * @param replacement : The new String.
	 */
	public final void replaceAll(String pattern, String replacement)
	{
		int point;
		while ((point = _sb.indexOf(pattern)) != -1)
			_sb.replace(point, point + pattern.length(), replacement);
	}
	
	/**
	 * Replace all delimiters '{}' by the String representation of any objects. Important things to note:
	 * <ul>
	 * <li>If there isn't enough parameters, then the leftover isn't processed.</li>
	 * <li>If there is too much parameters, the loop breaks when it doesn't find anything to replace.</li>
	 * <li>If the object is null, then it sends "null".
	 * </ul>
	 * @param args : The objects to pass.
	 */
	public final void replaceAll(Object... args)
	{
		int index;
		int newIndex = 0;
		
		for (Object obj : args)
		{
			index = _sb.indexOf(DELIM_STR, newIndex);
			if (index == -1)
				break;
			
			newIndex = index + 2;
			_sb.replace(index, newIndex, (obj == null) ? null : obj.toString());
		}
	}
	
	/**
	 * Replace the first occurence of a String for another String.
	 * @param pattern : The String to replace.
	 * @param replacement : The new String.
	 */
	public final void replaceFirst(String pattern, String replacement)
	{
		final int point = _sb.indexOf(pattern);
		_sb.replace(point, point + pattern.length(), replacement);
	}
	
	/**
	 * Replace the last occurence of a String for another String.
	 * @param pattern : The String to replace.
	 * @param replacement : The new String.
	 */
	public final void replaceLast(String pattern, String replacement)
	{
		final int point = _sb.lastIndexOf(pattern);
		_sb.replace(point, point + pattern.length(), replacement);
	}
	
	@Override
	public String toString()
	{
		return _sb.toString();
	}
}