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
package ext.mods.commons.gui.tools;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.enums.items.ItemLocation;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.item.kind.Item;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ItemList;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class CreateItemDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final String _playerName;
	private final JFrame _parentFrame;

	public CreateItemDialog(JFrame mainFrame, String playerName) {
		super(mainFrame, "Create Item: " + playerName, true);
		this._playerName = playerName;
		this._parentFrame = mainFrame;

		setSize(350, 230);
		setLayout(new GridLayout(7, 2, 5, 5));

		JLabel lblItemId = new JLabel("Item Id:");
		JTextField txtItemId = new JTextField();
		JLabel lblAmount = new JLabel("Amount:");
		JTextField txtAmount = new JTextField();
		JLabel lblLocation = new JLabel("Location:");
		JComboBox<String> locationComboBox = new JComboBox<>();
		locationComboBox.addItem("INVENTORY");
		locationComboBox.addItem("WAREHOUSE");
		locationComboBox.addItem("CLANWH");
		JLabel lblEnchant = new JLabel("Apply Enchant:");
		JCheckBox enchantCheckBox = new JCheckBox();
		JLabel lblEnchantLevel = new JLabel("Enchant Level:");
		JTextField txtEnchantLevel = new JTextField();
		lblEnchantLevel.setEnabled(false);
		txtEnchantLevel.setEnabled(false);

		enchantCheckBox.addActionListener(e -> {
			boolean isChecked = enchantCheckBox.isSelected();
			lblEnchantLevel.setEnabled(isChecked);
			txtEnchantLevel.setEnabled(isChecked);
		});

		JButton btnSetVip = new JButton("Apply");
		btnSetVip.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int itemId = Integer.parseInt(txtItemId.getText().trim());
					int amount = Integer.parseInt(txtAmount.getText().trim());
					String selectedLocation = (String) locationComboBox.getSelectedItem();
					int enchantLevel = 0;
					if (enchantCheckBox.isSelected()) {
						enchantLevel = Integer.parseInt(txtEnchantLevel.getText().trim());
					}
					addCreateItem(_playerName, itemId, amount, selectedLocation, enchantLevel, _parentFrame);
					dispose();
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(CreateItemDialog.this, "Please enter valid numbers for itemId.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		add(lblItemId);
		add(txtItemId);
		add(lblAmount);
		add(txtAmount);
		add(lblLocation);
		add(locationComboBox);
		add(lblEnchant);
		add(enchantCheckBox);
		add(lblEnchantLevel);
		add(txtEnchantLevel);
		add(new JLabel());
		add(btnSetVip);

		setLocationRelativeTo(mainFrame);
	}


	private static void addCreateItem(String playerName, int itemid, int amount, String locationName, int enchantLevel, JFrame mainFrame) {
		Player player = World.getInstance().getPlayer(playerName);

		if (player != null) {

			ItemInstance itemcreate = player.addItem(itemid, amount, false);
			if (enchantLevel > 0) {
				itemcreate.setEnchantLevel(enchantLevel, player);
			}

			if (itemcreate.getCount() > 1)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S3_S2).addCharName(player).addItemName(itemcreate).addItemNumber(itemcreate.getCount()));
			else if (itemcreate.getEnchantLevel() > 0)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S2_S3).addCharName(player).addNumber(itemcreate.getEnchantLevel()).addItemName(itemcreate));
			else
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_OBTAINED_S2).addCharName(player).addItemName(itemcreate));

			player.sendPacket(new ItemList(player, true));
			JOptionPane.showMessageDialog(mainFrame, "Create Item successfully for " + playerName, "Success", JOptionPane.INFORMATION_MESSAGE);
		} else {
			ItemLocation location = getItemLocation(locationName);

			if (location != null) {
				giveItemToOfflinePlayer(playerName, itemid, amount, enchantLevel, location);
				JOptionPane.showMessageDialog(mainFrame, "Item created successfully for " + playerName, "Success", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(mainFrame, "Invalid location selected.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void giveItemToOfflinePlayer(String playername, int id, int count, int enchantLevel, ItemLocation location) {
		try (Connection con = ConnectionPool.getConnection(); PreparedStatement selectStatement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?"); PreparedStatement insertStatement = con.prepareStatement("INSERT INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,object_id,custom_type1,custom_type2,mana_left,time) VALUES (?,?,?,?,?,?,?,?,?,?,?)")) {

			Item item = ItemData.getInstance().getTemplate(id);
			int objectId = IdFactory.getInstance().getNextId();

			selectStatement.setString(1, playername);
			try (ResultSet result = selectStatement.executeQuery()) {
				int objId = 0;

				if (result.next()) {
					objId = result.getInt(1);
				}

				if (objId == 0) {
					return;
				}

				if (item == null) {
					return;
				}

				if (count > 1 && !item.isStackable()) {
					return;
				}

				insertStatement.setInt(1, objId);
				insertStatement.setInt(2, item.getItemId());
				insertStatement.setInt(3, count);
				insertStatement.setString(4, location.name());
				insertStatement.setInt(5, 0);
				insertStatement.setInt(6, enchantLevel);
				insertStatement.setInt(7, objectId);
				insertStatement.setInt(8, 0);
				insertStatement.setInt(9, 0);
				insertStatement.setInt(10, -1);
				insertStatement.setLong(11, 0);

				insertStatement.executeUpdate();
			}
		} catch (SQLException e) {
		}
	}

	private static ItemLocation getItemLocation(String name) {
		switch (name.toUpperCase()) {
			case "INVENTORY":
				return ItemLocation.INVENTORY;
			case "WAREHOUSE":
				return ItemLocation.WAREHOUSE;
			case "CLANWH":
				return ItemLocation.CLANWH;
			default:
				return null;
		}
	}
}