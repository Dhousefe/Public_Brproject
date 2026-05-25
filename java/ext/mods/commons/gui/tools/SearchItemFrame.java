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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import ext.mods.commons.gui.GuiUtils;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.model.item.kind.Item;

public class SearchItemFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public SearchItemFrame() {
		super("Search Item Manager");
		setIconImages(GuiUtils.loadIcons());
		setSize(600, 300);
		setLayout(new BorderLayout());

		JPanel itemSearchPanel = new JPanel();
		itemSearchPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		JLabel lblItemName = new JLabel("Item Name:");
		JTextField txtItemName = new JTextField(20);
		JButton btnItemSearch = new JButton("Search");

		itemSearchPanel.add(lblItemName);
		itemSearchPanel.add(txtItemName);
		itemSearchPanel.add(btnItemSearch);

		add(itemSearchPanel, BorderLayout.NORTH);

		JTable itemTable = new JTable();
		DefaultTableModel itemModel = new DefaultTableModel(new String[] {
			"Item ID", "Item Name"
		}, 0);
		itemTable.setModel(itemModel);

		JScrollPane itemScrollPane = new JScrollPane(itemTable);
		itemScrollPane.setPreferredSize(new Dimension(580, 200));

		add(itemScrollPane, BorderLayout.CENTER);

		final JFrame thisFrame = this;
		btnItemSearch.addActionListener(e -> {
			String itemName = txtItemName.getText().trim();
			if (itemName.isEmpty()) {
				JOptionPane.showMessageDialog(thisFrame, "Please enter an Item Name.", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			ItemData itemTableInstance = ItemData.getInstance();
			List<Item> items = itemTableInstance.searchItemsByName(itemName);

			itemModel.setRowCount(0);
			for (Item item : items) {
				itemModel.addRow(new Object[] {
					item.getItemId(), item.getName()
				});
			}
		});

		setLocationRelativeTo(null);
	}
}