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
package ext.mods.commons.logging.formatter;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class NoTimestampConsoleFormatter extends Formatter
{
    @Override
    public String format(LogRecord record)
    {
        if (record.getSourceClassName() != null && record.getSourceClassName().contains("JvmOptimizer"))
        {
            String message = record.getMessage();
            
            if (message == null)
                return System.lineSeparator();
            
            message = message.trim();
            
            message = message.replaceFirst("^INFORMA\\?\\?ES:\\s*", "");
            message = message.replaceFirst("^AVISO:\\s*", "");
            message = message.replaceFirst("^ERRO:\\s*", "");
            message = message.replaceFirst("^DEBUG:\\s*", "");
            message = message.replaceFirst("^CONFIG:\\s*", "");
            message = message.replaceFirst("^FINE:\\s*", "");
            message = message.replaceFirst("^FINER:\\s*", "");
            message = message.replaceFirst("^FINEST:\\s*", "");
            message = message.replaceFirst("^SEVERE:\\s*", "");
            message = message.replaceFirst("^WARNING:\\s*", "");
            
            String className = record.getSourceClassName();
            String methodName = record.getSourceMethodName();
            
            if (className != null)
            {
                message = message.replace(className, "").trim();
                String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
                message = message.replace(simpleClassName, "").trim();
                message = message.replace("ext.mods.commons.util", "").trim();
            }
            
            if (methodName != null)
            {
                message = message.replace(methodName, "").trim();
            }
            
            message = message.replaceAll("\\[\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\]\\s*", "");
            message = message.replaceAll("\\[\\w{3}\\.\\s+\\d{1,2},\\s+\\d{4}\\s+\\d{1,2}:\\d{2}:\\d{2}\\s+(AM|PM)\\]\\s*", "");
            message = message.replaceAll("\\w{3}\\.\\s+\\d{1,2},\\s+\\d{4}\\s+\\d{1,2}:\\d{2}:\\d{2}\\s+(AM|PM)\\s*", "");
            
            message = message.replaceAll("\\s+", " ").trim();
            
            if (message.isEmpty() || message.matches("^[\\s\\-\\|\\+]*$"))
            {
                return System.lineSeparator();
            }
            
            return message + System.lineSeparator();
        }
        
        String message = record.getMessage();
        return (message != null ? message : "") + System.lineSeparator();
    }
}