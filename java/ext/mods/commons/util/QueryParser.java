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
package ext.mods.commons.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntPredicate;

public class QueryParser
{
	private String _input;
	
	private int _pos, _start, _end;
	
	private String image()
	{
		return _input.substring(_start, _end);
	}
	
	private int peek()
	{
		return _input.charAt(_pos);
	}
	
	private boolean eof()
	{
		return _input.length() <= _pos;
	}
	
	private void consume()
	{
		_pos++;
	}
	
	public Map<String, String> scan(String input)
	{
		_pos = _start = _end = 0;
		_input = input;
		
		final Map<String, String> props = new HashMap<>();
		props.put("$name", scanID().trim());
		if (match(QST))
		{
			while (!eof())
			{
				final String key = scanID().trim();
				if (match(EQS))
				{
					props.put(key, scanID().trim());
					if (match(AMP))
						continue;
					
					if (eof())
						break;
				}
				
				if (match(AMP) || eof())
				{
					props.put(key, "");
					continue;
				}
				break;
			}
		}
		return props;
	}
	
	private String scanID()
	{
		_start = _pos;
		while (match(CH))
		{
		}
		_end = _pos;
		return image();
	}
	
	public boolean is(IntPredicate predicate)
	{
		return !eof() && predicate.test(peek());
	}
	
	public boolean match(IntPredicate predicate)
	{
		if (is(predicate))
		{
			consume();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static final IntPredicate QST = ch -> ch == '?';
	public static final IntPredicate EQS = ch -> ch == '=';
	public static final IntPredicate AMP = ch -> ch == '&';
	public static final IntPredicate CH = QST.or(EQS).or(AMP).negate();
	
	public static Map<String, String> parse(String input)
	{
		return new QueryParser().scan(input);
	}
}