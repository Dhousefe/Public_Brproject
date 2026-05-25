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
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import ext.mods.commons.gui.GuiUtils;
import ext.mods.commons.gui.defence;

public class DefenceBalanceFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public DefenceBalanceFrame() {
		super("Information Balance");

		defence model = new defence();
		JTable table = new JTable(model);
		JScrollPane scroll = new JScrollPane(table);

		DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (!isSelected) {
					c.setBackground(UIManager.getColor("Table.background"));
					c.setForeground(UIManager.getColor("Table.foreground"));
				} else {
					c.setBackground(table.getSelectionBackground());
					c.setForeground(table.getSelectionForeground());
				}
				if (column >= 2 && column <= 5 && value instanceof Number) {
					double val = ((Number) value).doubleValue();
					if (val != 1.0) {
						if (!isSelected) {
							c.setForeground(val > 1.0 ? new Color(100, 220, 100) : new Color(220, 100, 100));
						}
					}
				}
				return c;
			}
		};

		for (int i = 2; i <= 3; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
		}

		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Filter class: ");
		JTextField filterField = new JTextField();
		topPanel.add(label, BorderLayout.WEST);
		topPanel.add(filterField, BorderLayout.CENTER);

		filterField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				model.filter(filterField.getText());
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				model.filter(filterField.getText());
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				model.filter(filterField.getText());
			}
		});

		JButton helpButton = new JButton("❓");
		helpButton.setToolTipText("Click to understand the defense system");

		final JFrame thisFrame = this;
		helpButton.addActionListener(e2 -> {
			String message = "➤ DEFENSE BALANCE PANEL\n\n" + "▸ This panel controls Physical (P.Def) and Magical (M.Def) defense a class receives against another.\n" + "▸ Each row represents the defense modifier the Target class will have when facing the Attacker class.\n\n" + "✦ P.Def → Controls physical defense applied against physical attacks.\n" + "✦ M.Def → Controls magical defense applied against magical attacks.\n\n" + "✔ Value 1.0: Normal defense, no change.\n" + "✔ Value > 1.0: Increases defense (e.g., 1.2 = 20% more resistance).\n" + "✔ Value < 1.0: Reduces defense (e.g., 0.8 = 20% more vulnerable).\n\n" + "▸ Values directly modify the resistance the target has when attacked.\n" + "▸ Colored values indicate modifications:\n" + " - Green: defense increase.\n" + " - Red: defense reduction.\n\n" + "▸ Use the filter to find a specific class and adjust its defensive vulnerabilities.";
			JOptionPane.showMessageDialog(thisFrame, message, "Help - Defense System", JOptionPane.INFORMATION_MESSAGE);
		});

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(scroll, BorderLayout.CENTER);
		topPanel.add(helpButton, BorderLayout.EAST);

		setIconImages(GuiUtils.loadIcons());
		setContentPane(mainPanel);
		setSize(600, 400);
		setLocationRelativeTo(null);
	}
}