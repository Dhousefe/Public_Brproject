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

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import ext.mods.commons.logging.CLogger;

import ext.mods.gameserver.model.actor.Creature;

public final class StringUtil
{
	private StringUtil()
	{
		throw new IllegalStateException("Utility class");
	}
	
	public static final String DIGITS = "0123456789";
	public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
	public static final String UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static final String LETTERS = LOWER_CASE_LETTERS + UPPER_CASE_LETTERS;
	public static final String LETTERS_AND_DIGITS = LETTERS + DIGITS;
	
	private static final CLogger LOGGER = new CLogger(StringUtil.class.getName());
	
	/**
	 * Checks each String passed as parameter. If at least one is empty or null, than return true.
	 * @param strings : The Strings to test.
	 * @return true if at least one String is empty or null.
	 */
	public static boolean isEmpty(String... strings)
	{
		for (String str : strings)
		{
			if (str == null || str.isEmpty())
				return true;
		}
		return false;
	}
	
	/**
	 * Appends objects to an existing StringBuilder.
	 * @param sb : the StringBuilder to edit.
	 * @param content : parameters to append.
	 */
	public static void append(StringBuilder sb, Object... content)
	{
		for (Object obj : content)
			sb.append((obj == null) ? null : obj.toString());
	}
	
	/**
	 * @param text : the String to check.
	 * @return true if the String contains only numbers, false otherwise.
	 */
	public static boolean isDigit(String text)
	{
		if (text == null)
			return false;
		
		return text.matches("[0-9]+");
	}
	
	/**
	 * @param text : the String to check.
	 * @return true if the String contains only numbers and letters, false otherwise.
	 */
	public static boolean isAlphaNumeric(String text)
	{
		if (text == null)
			return false;
		
		for (char chars : text.toCharArray())
		{
			if (!Character.isLetterOrDigit(chars))
				return false;
		}
		return true;
	}
	
	/**
	 * @param value : the number to format.
	 * @return a number formatted with "," delimiter.
	 */
	public static String formatNumber(long value)
	{
		return NumberFormat.getInstance(Locale.ENGLISH).format(value);
	}
	
	/**
	 * @param string : the initial word to scramble.
	 * @return an anagram of the given string.
	 */
	public static String scrambleString(String string)
	{
		final List<String> letters = Arrays.asList(string.split(""));
		Collections.shuffle(letters);
		
		final StringBuilder sb = new StringBuilder(string.length());
		for (String c : letters)
			sb.append(c);
		
		return sb.toString();
	}
	
	/**
	 * Verify if the given text matches with the regex pattern.
	 * @param text : the text to test.
	 * @param regex : the regex pattern to make test with.
	 * @return true if matching.
	 */
	public static boolean isValidString(String text, String regex)
	{
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(regex);
		}
		catch (PatternSyntaxException e)
		{
			pattern = Pattern.compile(".*");
		}
		
		Matcher regexp = pattern.matcher(text);
		
		return regexp.matches();
	}
	
	/**
	 * Format a given text to fit with logging "title" criterias, and send it.
	 * @param text : the String to format.
	 */
	public static void printSection(String text)
	{
		final StringBuilder sb = new StringBuilder("=[ " + text + " ]");
		while (sb.length() < 61)
		{
			sb.insert(0, "-");
		}
		
		LOGGER.info(sb.toString());
	}
	
	/**
	 * Format a time given in seconds into "h m s" String format.
	 * @param time : a time given in seconds.
	 * @return a "h m s" formated String.
	 */
	public static String getTimeStamp(int time)
	{
		final int hours = time / 3600;
		time %= 3600;
		final int minutes = time / 60;
		time %= 60;
		
		String result = "";
		if (hours > 0)
			result += hours + "h";
		if (minutes > 0)
			result += " " + minutes + "m";
		if (time > 0 || result.length() == 0)
			result += " " + time + "s";
		
		return result;
	}
	
	/**
	 * Format a {@link String} to delete its extension ("castles.xml" > "castles"), if any.
	 * @param fileName : The String to edit, which is a former file name.
	 * @return a left-side truncated String to the first "." encountered.
	 */
	public static String getNameWithoutExtension(String fileName)
	{
		final int pos = fileName.lastIndexOf(".");
		if (pos > 0)
			fileName = fileName.substring(0, pos);
		
		return fileName;
	}
	
	/**
	 * Trim the {@link String} set as parameter to the amount of characters set as second parameter.
	 * @param s : The {@link String} to trim.
	 * @param maxWidth : The maximum length.
	 * @return The {@link String} trimmed to the good format.
	 */
	public static String trim(String s, int maxWidth)
	{
		return (s.length() > maxWidth) ? s.substring(0, maxWidth) : s;
	}
	
	/**
	 * Trim the {@link String} set as parameter to the amount of characters set as second parameter, or return {@link String} defaultValue if {@link String} is null or empty.
	 * @param s : The {@link String} to trim.
	 * @param maxWidth : The maximum length.
	 * @param defaultValue : The default {@link String} to return if {@link String} is null or empty.
	 * @return The {@link String} trimmed to the good format.
	 */
	public static String trim(String s, int maxWidth, String defaultValue)
	{
		if (s == null || s.isEmpty())
			return defaultValue;
		
		return trim(s, maxWidth);
	}
	
	/**
	 * Trim the {@link String} set as parameter to the amount of characters set as second parameter. Add "..." in the end of the {@link String}.
	 * @param s : The {@link String} to trim.
	 * @param maxWidth : The maximum length.
	 * @return The trimmed {@link String} followed by "...".
	 */
	public static String trimAndDress(String s, int maxWidth)
	{
		if (s.length() > maxWidth)
		{
			s = s.substring(0, maxWidth - 3);
			s += "...";
		}
		return s;
	}
	
	/**
	 * @param timestamp : The {@link String} to format.
	 * @return a timestamp in seconds, based on a {@link String} set as parameter.
	 */
	public static final int getTimeStamp(String timestamp)
	{
		if (timestamp == null)
			return 0;
		
		if (timestamp.equalsIgnoreCase("no"))
			return -1;
		
		if (timestamp.endsWith("hour"))
			return Integer.parseInt(timestamp.split("hour")[0]) * 3600;
		
		if (timestamp.endsWith("min"))
			return Integer.parseInt(timestamp.split("min")[0]) * 60;
		
		if (timestamp.endsWith("sec"))
			return Integer.parseInt(timestamp.split("sec")[0]);
		
		return 0;
	}
	
	public static String getCreatureDescription(StringBuilder sb, Creature creature)
	{
		if (creature == null)
			return "none";
		
		final String teleLoc = creature.getPosition().toString().replace(",", "");
		
		return "<a action=\"bypass admin_teleport " + teleLoc + "\">" + trimAndDress(creature.getName(), 12) + "</a>";
	}

	/**
	 * @param name : The {@link String} on the material.
	 * @param search : The {@link String} that name must contain.
	 * @return True in case the name contains the search.
	 */
	public static boolean matches(String name, String search)
	{
		if (name == null || name.isBlank() || search == null || search.isBlank())
			return false;
		
		return Arrays.stream(search.toLowerCase().split(" ")).allMatch(r -> name.toLowerCase().contains(r));
	}

	/**
	 * Gets the HTML representation of MP Warn gauge.
	 * @param width the width
	 * @param current the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @return the HTML
	 */
	public static String getMpGauge(int width, long current, long max, boolean displayAsPercentage)
	{
		return getGauge(width, current, max, displayAsPercentage, "L2UI_CH3.br_bar1_mp", "L2UI_CH3.br_bar1_mp", 12, -13);
	}
	
	/**
	 * Gets the HTML representation of a gauge.
	 * @param width the width
	 * @param currentValue the current value
	 * @param max the max value
	 * @param displayAsPercentage if {@code true} the text in middle will be displayed as percent else it will be displayed as "current / max"
	 * @param backgroundImage the background image
	 * @param image the foreground image
	 * @param imageHeight the image height
	 * @param top the top adjustment
	 * @return the HTML
	 */
	private static String getGauge(int width, long currentValue, long max, boolean displayAsPercentage, String backgroundImage, String image, long imageHeight, long top)
	{
		final long current = Math.min(currentValue, max);
		final StringBuilder sb = new StringBuilder();
		sb.append("<table width=");
		sb.append(width);
		sb.append(" cellpadding=0 cellspacing=0>");
		sb.append("<tr>");
		sb.append("<td background=\"");
		sb.append(backgroundImage);
		sb.append("\">");
		sb.append("<img src=\"");
		sb.append(image);
		sb.append("\" width=");
		sb.append((long) (((double) current / max) * width));
		sb.append(" height=");
		sb.append(imageHeight);
		sb.append(">");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td align=center>");
		sb.append("<table cellpadding=0 cellspacing=");
		sb.append(top);
		sb.append(">");
		sb.append("<tr>");
		sb.append("<td>");
		if (displayAsPercentage)
		{
			sb.append("<table cellpadding=0 cellspacing=2>");
			sb.append("<tr><td>");
			sb.append(String.format("%.2f%%", ((double) current / max) * 100));
			sb.append("</td></tr>");
			sb.append("</table>");
		}
		else
		{
			final int tdWidth = (width - 10) / 2;
			sb.append("<table cellpadding=0 cellspacing=7>");
			sb.append("<tr>");
			sb.append("<td width=");
			sb.append(tdWidth);
			sb.append(" align=right>");
			sb.append(current);
			sb.append("</td>");
			sb.append("<td width=10 align=center>/</td>");
			sb.append("<td width=");
			sb.append(tdWidth);
			sb.append(">");
			sb.append(max);
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
		}
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</td>");
		sb.append("</tr>");
		sb.append("</table>");
		return sb.toString();
	}
}