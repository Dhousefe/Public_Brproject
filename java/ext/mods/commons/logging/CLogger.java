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
package ext.mods.commons.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ext.mods.commons.lang.StringReplacer;

/**
 * Wraps the regular {@link Logger} to handle slf4j features, notably {} replacement.<br>
 * <br>
 * The current values should be used :
 * <ul>
 * <li>debug (Level.FINE): debug purposes (end replacement for Config.DEBUG).</li>
 * <li>info (Level.INFO) : send regular informations to the console.</li>
 * <li>warn (Level.WARNING): report failed integrity checks.</li>
 * <li>error (Level.SEVERE): report an issue involving data loss / leading to unexpected server behavior.</li>
 * </ul>
 */
public final class CLogger
{
	private final Logger _logger;
	
	public CLogger(String name)
	{
		_logger = Logger.getLogger(name);
	}
	
	private void log0(Level level, StackTraceElement caller, Object message, Throwable exception)
	{
		if (!_logger.isLoggable(level))
			return;
		
		if (caller == null)
			caller = new Throwable().getStackTrace()[2];
		
		_logger.logp(level, caller.getClassName(), caller.getMethodName(), String.valueOf(message), exception);
	}
	
	private void log0(Level level, StackTraceElement caller, Object message, Throwable exception, Object... args)
	{
		if (!_logger.isLoggable(level))
			return;
		
		if (caller == null)
			caller = new Throwable().getStackTrace()[2];
		
		_logger.logp(level, caller.getClassName(), caller.getMethodName(), format(String.valueOf(message), args), exception);
	}
	
	public void log(LogRecord logRecord)
	{
		_logger.log(logRecord);
	}
	
	/**
	 * Logs a message with Level.FINE.
	 * @param message : The object to log.
	 */
	public void debug(Object message)
	{
		log0(Level.FINE, null, message, null);
	}
	
	/**
	 * Logs a message with Level.FINE.
	 * @param message : The object to log.
	 * @param args : The passed arguments, used to format the message.
	 */
	public void debug(Object message, Object... args)
	{
		log0(Level.FINE, null, message, null, args);
	}
	
	/**
	 * Logs a message with Level.FINE.
	 * @param message : The object to log.
	 * @param exception : Log the caught exception.
	 */
	public void debug(Object message, Throwable exception)
	{
		log0(Level.FINE, null, message, exception);
	}
	
	/**
	 * Logs a message with Level.FINE.
	 * @param message : The object to log.
	 * @param exception : Log the caught exception.
	 * @param args : The passed arguments, used to format the message.
	 */
	public void debug(Object message, Throwable exception, Object... args)
	{
		log0(Level.FINE, null, message, exception, args);
	}
	
	/**
	 * Logs a message with Level.INFO.
	 * @param message : The object to log.
	 */
	public void info(Object message)
	{
		log0(Level.INFO, null, message, null);
	}
	
	/**
	 * Logs a message with Level.INFO.
	 * @param message : The object to log.
	 * @param args : The passed arguments, used to format the message.
	 */
	public void info(Object message, Object... args)
	{
		log0(Level.INFO, null, message, null, args);
	}
	
	/**
	 * Logs a message with Level.INFO.
	 * @param message : The object to log.
	 * @param exception : Log the caught exception.
	 */
	public void info(Object message, Throwable exception)
	{
		log0(Level.INFO, null, message, exception);
	}
	
	/**
	 * Logs a message with Level.INFO.
	 * @param message : The object to log.
	 * @param exception : Log the caught exception.
	 * @param args : The passed arguments, used to format the message.
	 */
	public void info(Object message, Throwable exception, Object... args)
	{
		log0(Level.INFO, null, message, exception, args);
	}
	
	/**
	 * Logs a message with Level.WARNING.
	 * @param message : The object to log.
	 */
	public void warn(Object message)
	{
		log0(Level.WARNING, null, message, null);
	}
	
	/**
	 * Logs a message with Level.WARNING.
	 * @param message : The object to log.
	 * @param args : The passed arguments, used to format the message.
	 */
	public void warn(Object message, Object... args)
	{
		log0(Level.WARNING, null, message, null, args);
	}
	
	/**
	 * Logs a message with Level.WARNING.
	 * @param message : The object to log.
	 * @param exception : Log the caught exception.
	 */
	public void warn(Object message, Throwable exception)
	{
		log0(Level.WARNING, null, message, exception);
	}
	
	/**
	 * Logs a message with Level.WARNING.
	 * @param message : The object to log.
	 * @param exception : Log the caught exception.
	 * @param args : The passed arguments, used to format the message.
	 */
	public void warn(Object message, Throwable exception, Object... args)
	{
		log0(Level.WARNING, null, message, exception, args);
	}
	
	/**
	 * Logs a message with Level.SEVERE.
	 * @param message : The object to log.
	 */
	public void error(Object message)
	{
		log0(Level.SEVERE, null, message, null);
	}
	
	/**
	 * Logs a message with Level.SEVERE.
	 * @param message : The object to log.
	 * @param args : The passed arguments, used to format the message.
	 */
	public void error(Object message, Object... args)
	{
		log0(Level.SEVERE, null, message, null, args);
	}
	
	/**
	 * Logs a message with Level.SEVERE.
	 * @param message : The object to log.
	 * @param exception : Log the caught exception.
	 */
	public void error(Object message, Throwable exception)
	{
		log0(Level.SEVERE, null, message, exception);
	}
	
	/**
	 * Logs a message with Level.SEVERE.
	 * @param message : The object to log.
	 * @param exception : Log the caught exception.
	 * @param args : The passed arguments, used to format the message.
	 */
	public void error(Object message, Throwable exception, Object... args)
	{
		log0(Level.SEVERE, null, message, exception, args);
	}
	
	/**
	 * Format the message, allowing to use {} as parameter. Avoid to generate String concatenation.
	 * @param message : the Object (String) message to format.
	 * @param args : the arguments to pass.
	 * @return a formatted String.
	 */
	private static final String format(String message, Object... args)
	{
		if (args == null || args.length == 0)
			return message;
		
		final StringReplacer sr = new StringReplacer(message);
		sr.replaceAll(args);
		return sr.toString();
	}
}