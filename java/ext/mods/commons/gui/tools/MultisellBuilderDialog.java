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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import ext.mods.commons.gui.GuiUtils;

public class MultisellBuilderDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public MultisellBuilderDialog(JFrame parent) {
		super(parent, "Multisell Creator", true);
		setIconImages(GuiUtils.loadIcons());
		setSize(800, 600);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout(10, 10));

		Color fundoDialogo = new Color(25, 25, 25);
		Color fundoLista = new Color(1, 1, 1);
		Color corTexto = new Color(204, 204, 204);
		Color corAcento = new Color(0, 122, 204);
		Color corBotaoVerde = new Color(70, 150, 70);
		Color corBotaoVermelho = new Color(180, 70, 70);
		Color corBotaoCinza = new Color(120, 120, 120);

		getContentPane().setBackground(fundoDialogo);

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
		topPanel.setBackground(fundoDialogo);

		JLabel lblNpcId = new JLabel("NPC ID:");
		lblNpcId.setForeground(corTexto);
		JTextField txtNpcId = new JTextField(6);

		JLabel lblIdInicio = new JLabel("Start ID:");
		lblIdInicio.setForeground(corTexto);
		JTextField txtIdInicio = new JTextField(8);

		JLabel lblIdFinal = new JLabel("End ID:");
		lblIdFinal.setForeground(corTexto);
		JTextField txtIdFinal = new JTextField(8);

		JCheckBox cbMesmoId = new JCheckBox("Same ID");
		cbMesmoId.setForeground(corTexto);
		cbMesmoId.setBackground(fundoDialogo);

		topPanel.add(lblNpcId);
		topPanel.add(txtNpcId);
		topPanel.add(lblIdInicio);
		topPanel.add(txtIdInicio);
		topPanel.add(lblIdFinal);
		topPanel.add(txtIdFinal);
		topPanel.add(cbMesmoId);

		JPanel ingredientPanel = new JPanel(new BorderLayout(5, 5));
		ingredientPanel.setBackground(fundoDialogo);
		ingredientPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Ingredients", 0, 0, new Font("Arial", Font.BOLD, 14), corTexto));

		JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		addPanel.setBackground(fundoDialogo);

		JTextField txtItemId = new JTextField(6);
		JTextField txtQuantidade = new JTextField(4);

		JButton btnAdd = new JButton("+");
		btnAdd.setBackground(corBotaoVerde);
		btnAdd.setForeground(Color.WHITE);

		JButton btnRemove = new JButton("-");
		btnRemove.setBackground(corBotaoVermelho);
		btnRemove.setForeground(Color.WHITE);

		JLabel lblItemId = new JLabel("Item ID:");
		lblItemId.setForeground(corTexto);
		addPanel.add(lblItemId);
		addPanel.add(txtItemId);
		JLabel lblItemQtd = new JLabel("Qty:");
		lblItemQtd.setForeground(corTexto);
		addPanel.add(lblItemQtd);
		addPanel.add(txtQuantidade);
		addPanel.add(btnAdd);
		addPanel.add(btnRemove);

		DefaultListModel<String> listModel = new DefaultListModel<>();
		JList<String> ingredientList = new JList<>(listModel);
		ingredientList.setBackground(fundoLista);
		ingredientList.setForeground(corTexto);
		ingredientList.setFont(new Font("Monospaced", Font.PLAIN, 13));

		JScrollPane scrollIngredients = new JScrollPane(ingredientList);

		btnAdd.addActionListener(e -> {
			String id = txtItemId.getText().trim();
			String qtd = txtQuantidade.getText().trim();
			if (!id.isEmpty() && !qtd.isEmpty()) {
				listModel.addElement("ItemID: " + id + " | Qty: " + qtd);
				txtItemId.setText("");
				txtQuantidade.setText("");
			}
		});

		btnRemove.addActionListener(e -> {
			int selectedIndex = ingredientList.getSelectedIndex();
			if (selectedIndex != -1) {
				listModel.remove(selectedIndex);
			}
		});

		ingredientPanel.add(addPanel, BorderLayout.NORTH);
		ingredientPanel.add(scrollIngredients, BorderLayout.CENTER);

		JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		optionsPanel.setBackground(fundoDialogo);

		JPanel enchantPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		enchantPanel.setBackground(fundoDialogo);

		JCheckBox cbEnchant = new JCheckBox("Multisell with Enchant");
		cbEnchant.setForeground(corTexto);
		cbEnchant.setBackground(fundoDialogo);

		JLabel lblValorEnchant = new JLabel("Value:");
		lblValorEnchant.setForeground(corTexto);

		JTextField txtEnchant = new JTextField(4);
		txtEnchant.setEnabled(false);

		cbEnchant.addActionListener(e -> txtEnchant.setEnabled(cbEnchant.isSelected()));

		enchantPanel.add(cbEnchant);
		enchantPanel.add(lblValorEnchant);
		enchantPanel.add(txtEnchant);

		optionsPanel.add(enchantPanel);

		JTextArea xmlPreview = new JTextArea(12, 50);
		xmlPreview.setEditable(false);
		xmlPreview.setBackground(fundoLista);
		xmlPreview.setForeground(new Color(0, 255, 100));
		xmlPreview.setFont(new Font("Monospaced", Font.PLAIN, 12));

		JScrollPane xmlScroll = new JScrollPane(xmlPreview);
		xmlScroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "XML Preview", 0, 0, new Font("Arial", Font.BOLD, 14), corTexto));

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
		bottomPanel.setBackground(fundoDialogo);

		JButton btnGerarLinhas = new JButton("Generate XML");
		btnGerarLinhas.setBackground(corAcento);
		btnGerarLinhas.setForeground(Color.WHITE);

		JButton btnSalvarXML = new JButton("Save XML");
		btnSalvarXML.setBackground(corAcento);
		btnSalvarXML.setForeground(Color.WHITE);

		final JDialog thisDialog = this;

		btnGerarLinhas.addActionListener(e -> {
			try {
				StringBuilder xml = new StringBuilder();
				int npcId = Integer.parseInt(txtNpcId.getText().trim());
				int idInicial = Integer.parseInt(txtIdInicio.getText().trim());
				int idFinal = Integer.parseInt(txtIdFinal.getText().trim());
				boolean mesmoId = cbMesmoId.isSelected();

				int enchantValue = 0;
				if (cbEnchant.isSelected() && !txtEnchant.getText().trim().isEmpty()) {
					enchantValue = Integer.parseInt(txtEnchant.getText().trim());
				}

				xml.append("<?xml version='1.0' encoding='utf-8'?>\n");
				xml.append("<list");
				if (cbEnchant.isSelected()) {
					xml.append(" maintainEnchantment=\"true\"");
				}
				xml.append(">\n");

				xml.append("    <npcs>\n");
				xml.append("        <npc>").append(npcId).append("</npc>\n");
				xml.append("    </npcs>\n\n");

				List<String> ingredientesXml = new ArrayList<>();
				for (int i = 0; i < listModel.size(); i++) {
					String line = listModel.get(i);
					String[] parts = line.replace("ItemID: ", "").split("\\| Qty: ");
					int itemId = Integer.parseInt(parts[0].trim());
					int qtd = Integer.parseInt(parts[1].trim());

					ingredientesXml.add("      <ingredient id=\"" + itemId + "\" count=\"" + qtd + "\"/>");
				}

				if (mesmoId) {
					xml.append("    <item>\n");
					for (String ing : ingredientesXml) {
						xml.append(ing).append("\n");
					}
					xml.append("        <production id=\"").append(idInicial).append("\" count=\"1\"");
					if (cbEnchant.isSelected()) {
						xml.append(" enchant=\"").append(enchantValue).append("\"");
					}
					xml.append("/>\n");
					xml.append("    </item>\n");
				} else {
					for (int id = idInicial; id <= idFinal; id++) {
						xml.append("    <item>\n");
						for (String ing : ingredientesXml) {
							xml.append(ing).append("\n");
						}
						xml.append("        <production id=\"").append(id).append("\" count=\"1\"");
						if (cbEnchant.isSelected()) {
							xml.append(" enchant=\"").append(enchantValue).append("\"");
						}
						xml.append("/>\n");
						xml.append("    </item>\n");
					}
				}

				xml.append("</list>");
				xmlPreview.setText(xml.toString());

			} catch (Exception ex) {
				JOptionPane.showMessageDialog(thisDialog, "Error generating XML: " + ex.getMessage());
			}
		});

		JButton btnLimpar = new JButton("Clear");
		btnLimpar.setBackground(corBotaoCinza);
		btnLimpar.setForeground(Color.WHITE);

		btnLimpar.addActionListener(e -> {
			txtNpcId.setText("");
			txtIdInicio.setText("");
			txtIdFinal.setText("");
			cbMesmoId.setSelected(false);
			cbEnchant.setSelected(false);
			txtEnchant.setText("");
			txtEnchant.setEnabled(false);
			listModel.clear();
			xmlPreview.setText("");
		});

		btnSalvarXML.addActionListener(e -> {

			String defaultFileName = UUID.randomUUID().toString().substring(0, 8) + ".xml";

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Save Multisell XML");
			fileChooser.setSelectedFile(new File(defaultFileName));

			int userSelection = fileChooser.showSaveDialog(thisDialog);
			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File fileToSave = fileChooser.getSelectedFile();

				if (!fileToSave.getName().toLowerCase().endsWith(".xml")) {
					fileToSave = new File(fileToSave.getAbsolutePath() + ".xml");
				}

				try (FileWriter fw = new FileWriter(fileToSave)) {
					fw.write(xmlPreview.getText());
					JOptionPane.showMessageDialog(thisDialog, "XML saved successfully!");
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(thisDialog, "Error saving XML!");
				}
			}
		});

		bottomPanel.add(btnGerarLinhas);
		bottomPanel.add(btnSalvarXML);
		bottomPanel.add(btnLimpar);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(10, 10));
		centerPanel.setBackground(fundoDialogo);

		centerPanel.add(ingredientPanel, BorderLayout.NORTH);

		JPanel enchantAndPreviewPanel = new JPanel(new BorderLayout(10, 10));
		enchantAndPreviewPanel.setBackground(fundoDialogo);

		enchantAndPreviewPanel.add(enchantPanel, BorderLayout.NORTH);
		enchantAndPreviewPanel.add(xmlScroll, BorderLayout.CENTER);

		centerPanel.add(enchantAndPreviewPanel, BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
}