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

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;

public class PlayerActions {

	/**
	 * Lógica do menu Administrator -> Change Access.
	 * @param parent 
	 */
	public static void changeAccess(JFrame parent) {
		String playerName = JOptionPane.showInputDialog(parent, "Enter player name:", "Admin Player", JOptionPane.QUESTION_MESSAGE);

		if (playerName == null || playerName.trim().isEmpty()) {
			return;
		}

		String[] accessOptions = {
			" -1 | Banned | No access", " 0 | User | Normal player", " 1 | Chat Moderator | Moderates chat only", " 2 | Test GM | Test GM", " 3 | General GM | General GM", " 4 | Support GM | Support GM", " 5 | Event GM | Event GM", " 6 | Head GM | Head GM", " 7 | Admin | Administrator", " 8 | Master | Owner/Master"
		};

		String selected = (String) JOptionPane.showInputDialog(parent, "Select the new access level:", "Change Access Level", JOptionPane.QUESTION_MESSAGE, null, accessOptions, accessOptions[1]);

		if (selected != null) {
			try {
				int level = Integer.parseInt(selected.trim().split("\\|")[0].trim());
				adminPlayer(playerName, level);
				JOptionPane.showMessageDialog(parent, "Access level of " + playerName + " changed to: " + selected, "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(parent, "Error parsing level!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Lógica do menu Players -> Repair Player.
	 * @param parent 
	 */
	public static void repairPlayer(JFrame parent) {
		String playerName = JOptionPane.showInputDialog(parent, "Enter player name:", "Repair Player", JOptionPane.QUESTION_MESSAGE);
		if (playerName != null && !playerName.isEmpty()) {
			try {
				handlerRepair(playerName);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(parent, "Invalid player name! Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}


	private static void handlerRepair(String playerName) {
		Player player = World.getInstance().getPlayer(playerName);

		if (player != null) {
			onLineRepair(player);
		} else {
			try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE characters SET x=?, y=?, z=? WHERE char_name=?")) {
				ps.setInt(1, 83345);
				ps.setInt(2, 148144);
				ps.setInt(3, -3413);
				ps.setString(4, playerName);
				ps.executeUpdate();

				System.out.println("Repair player name: [" + playerName + "] Teleport to fixed location");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void onLineRepair(Player player) {
		if (player.isInCombat()) {
			player.abortAll(true);
		}

		player.teleportTo(RestartType.TOWN);
		player.sendMessage("Admin repair you characters, you move town");
		System.out.println("Repair player name: [" + player.getName() + "] Teleport to town");
	}

	private static void adminPlayer(String name, int adminLevel) {
		Player player = World.getInstance().getPlayer(name);

		if (player != null) {
			onLineChange(player, adminLevel);
		} else {
			try (Connection con = ConnectionPool.getConnection(); PreparedStatement ps = con.prepareStatement("UPDATE characters SET accesslevel=? WHERE char_name=?")) {
				ps.setInt(1, adminLevel);
				ps.setString(2, name);
				ps.execute();

				ps.getUpdateCount();
				System.out.println("Change Status player name: [" + name + "] accesslevel: " + adminLevel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void onLineChange(Player player, int level) {
		if (level == 0) {
			player.setTitle("");
			player.setAccessLevel(0);
			player.logout(false);
			return;
		}

		player.setAccessLevel(level);
		player.sendMessage("Your access level has been changed to " + level + ".");
		System.out.println("Change Status player name: [" + player.getName() + "] accesslevel: " + level);
	}
}