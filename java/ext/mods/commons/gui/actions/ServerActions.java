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
package ext.mods.commons.gui.actions;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ext.mods.commons.gui.GuiUtils;
import ext.mods.gameserver.Shutdown;
import ext.mods.gameserver.model.World;

public class ServerActions {

	private static final String[] shutdownOptions = {"Shutdown", "Cancel"};
	private static final String[] restartOptions = {"Restart", "Cancel"};
	private static final String[] abortOptions = {"Abort", "Cancel"};

	/**
	 * Lógica do menu Game -> Shutdown.
	 * @param parent 
	 */
	public static void shutdown(JFrame parent) {
		if (JOptionPane.showOptionDialog(parent, "Shutdown GameServer?", "Select an option", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, shutdownOptions, shutdownOptions[1]) == 0) {
			final Object answer = JOptionPane.showInputDialog(parent, "Shutdown delay in seconds", "Input", JOptionPane.INFORMATION_MESSAGE, null, null, "600");
			if (answer != null) {
				final String input = ((String) answer).trim();
				if (GuiUtils.isDigit(input)) {
					final int delay = Integer.parseInt(input);
					if (delay > 0) {
						Shutdown.getInstance().startShutdown(null, delay, false);
					}
				}
			}
		}
	}

	/**
	 * Lógica do menu Game -> Restart.
	 * @param parent 
	 */
	public static void restart(JFrame parent) {
		if (JOptionPane.showOptionDialog(parent, "Restart GameServer?", "Select an option", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, restartOptions, restartOptions[1]) == 0) {
			final Object answer = JOptionPane.showInputDialog(parent, "Restart delay in seconds", "Input", JOptionPane.INFORMATION_MESSAGE, null, null, "600");
			if (answer != null) {
				final String input = ((String) answer).trim();
				if (GuiUtils.isDigit(input)) {
					final int delay = Integer.parseInt(input);
					if (delay > 0) {
						Shutdown.getInstance().startShutdown(null, delay, true);
					}
				}
			}
		}
	}

	/**
	 * Lógica do menu Game -> Abort.
	 * @param parent 
	 */
	public static void abort(JFrame parent) {
		if (JOptionPane.showOptionDialog(parent, "Abort server shutdown?", "Select an option", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, abortOptions, abortOptions[1]) == 0) {
			Shutdown.getInstance().abort(null);
		}
	}

	/**
	 * Lógica do menu Announce -> Normal.
	 * @param parent 
	 */
	public static void normalAnnounce(JFrame parent) {
		final Object input = JOptionPane.showInputDialog(parent, "Announce message", "Input", JOptionPane.INFORMATION_MESSAGE, null, null, "");
		if (input != null) {
			final String message = ((String) input).trim();
			if (!message.isEmpty()) {
				World.announceToOnlinePlayers(message, false);
			}
		}
	}

	/**
	 * Lógica do menu Announce -> Critical.
	 * @param parent 
	 */
	public static void criticalAnnounce(JFrame parent) {
		final Object input = JOptionPane.showInputDialog(parent, "Critical announce message", "Input", JOptionPane.INFORMATION_MESSAGE, null, null, "");
		if (input != null) {
			final String message = ((String) input).trim();
			if (!message.isEmpty()) {
				World.announceToOnlinePlayers(message, true);
			}
		}
	}
}