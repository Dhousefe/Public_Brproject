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
package ext.mods.util;

import java.util.HashMap;
import java.util.Map;

public class Tokenizer
{
	private final Map<Integer, String> _tokens = new HashMap<>();
	
	public Tokenizer(String str)
	{
		this(str, " ");
	}
	
	public Tokenizer(String str, String separator)
	{
		if (str == null || separator == null || str.isEmpty() || separator.isEmpty())
		{
			return;
		}
		
		for (final String s : str.split(separator))
		{
			_tokens.put(_tokens.size(), s);
		}
	}
	
	public final String getToken(int index)
	{
		return _tokens.get(index);
	}
	
	public final String getFirstToken()
	{
		return getToken(0);
	}
	
	public final String getLastToken()
	{
		return _tokens.isEmpty() ? null : _tokens.get(_tokens.size() - 1);
	}
	
	public final byte getAsByte(int index, byte defaultValue)
	{
		final String token = getToken(index);
		if (token == null || !token.matches("-?\\d+"))
		{
			return defaultValue;
		}
		
		return Byte.parseByte(token);
	}
	
	public final int getAsInteger(int index, int defaultValue)
	{
		final String token = getToken(index);
		if (token == null || !token.matches("-?\\d+"))
		{
			return defaultValue;
		}
		
		return Integer.parseInt(token);
	}
	
	public final float getAsFloat(int index, float defaultValue)
	{
		final String token = getToken(index);
		if (token == null || !token.matches("-?\\d+(\\.\\d+)?"))
		{
			return defaultValue;
		}
		
		return Float.parseFloat(token);
	}
	
	public final double getAsDouble(int index, double defaultValue)
	{
		final String token = getToken(index);
		if (token == null || !token.matches("-?\\d+(\\.\\d+)?"))
		{
			return defaultValue;
		}
		
		return Double.parseDouble(token);
	}
	
	public final long getAsLong(int index, long defaultValue)
	{
		final String token = getToken(index);
		if (token == null || !token.matches("-?\\d+"))
		{
			return defaultValue;
		}
		
		return Long.parseLong(token);
	}
	
	public final boolean getAsBoolean(int index)
	{
		final String token = getToken(index);
		return Boolean.parseBoolean(token);
	}
	
	public <E extends Enum<E>> E getAsEnum(int index, final Class<E> enumClass, final E defaultValue)
	{
		final String val = getToken(index);
		if (val != null)
		{
			try
			{
				return Enum.valueOf(enumClass, val);
			}
			catch (IllegalArgumentException e)
			{
			}
		}
		return defaultValue;
	}
	
	public final int size()
	{
		return _tokens.size();
	}
	
	public final boolean isEmpty()
	{
		return _tokens.isEmpty();
	}
	
	public final String getAsString(int index)
	{
		return getToken(index);
	}
	
	public final String getRemainingTokensFromIndex(int startIndex)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = startIndex; i < _tokens.size(); i++)
		{
			String token = getToken(i);
			if (sb.length() > 0)
			{
				sb.append(" ");
			}
			sb.append(token);
		}
		return sb.toString();
	}
	
	public final int countTokens()
	{
		return _tokens.size();
	}
	
	public <E extends Enum<E>> E nextEnum(final Class<E> enumClass)
	{
		return getAsEnum(1, enumClass, null);
	}
	
}