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
import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.BuyListManager;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.data.xml.AdminData;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.data.xml.MultisellData;
import ext.mods.Crypta.RandomManager;

public class ReloadActions {

	private static final String[] confirmOptions = {"Reload", "Cancel"};

	private static boolean confirm(JFrame parent, String message) {
		return JOptionPane.showOptionDialog(parent, message, "Select an option", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, confirmOptions, confirmOptions[1]) == 0;
	}

	public static void reloadAccess(JFrame parent) {
		if (confirm(parent, "Reload admin access levels?")) {
			AdminData.getInstance().reload();
			System.out.println("Admin access levels have been reloaded.");
		}
	}

	public static void reloadBuylists(JFrame parent) {
		if (confirm(parent, "Reload buylists?")) {
			BuyListManager.getInstance().reload();
			System.out.println("Buylists have been reloaded.");
		}
	}

	public static void reloadConfigs(JFrame parent) {
		if (confirm(parent, "Reload configs?")) {
			Config.loadGameServer();
			System.out.println("Configs files have been reloaded.");
		}
	}

	public static void reloadFarmEvent(JFrame parent) {
		if (confirm(parent, "Reload Random Farm Event?")) {

			Object RandomManagerIntance = RandomManager.getInstance();
					if (RandomManagerIntance == null)
					{
						System.out.println("AdminGui: RandomManager is not available.");
						return;
					}

					try
					{
						RandomManager.getInstance().reload();
						System.out.println("Random Farm Mod has been reloaded.");
					}
					catch (Exception e)
					{
						System.out.println("AdminGui: Error accessing RandomManager.");
						e.printStackTrace();
						return;
					}
			
		}
	}

	public static void reloadItems(JFrame parent) {
		if (confirm(parent, "Reload Items data files?")) {
			ItemData.getInstance().reload();
			System.out.println("Items' templates have been reloaded.");
		}
	}

	public static void reloadMultisells(JFrame parent) {
		if (confirm(parent, "Reload multisells?")) {
			MultisellData.getInstance().reload();
			System.out.println("The multisell instance has been reloaded.");
		}
	}

	public static void reloadSkills(JFrame parent) {
		if (confirm(parent, "Reload Skills data files?")) {
			SkillTable.getInstance().reload();
			System.out.println("Skills' XMLs have been reloaded.");
		}
	}

	public static void reloadZones(JFrame parent) {
		if (confirm(parent, "Reload Zone Data files?")) {
			ZoneManager.getInstance().reload();
			System.out.println("Zones have been reloaded.");
		}
	}
}