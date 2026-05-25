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
package ext.mods.security.gui.items.amors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ArmorsXMLGenerator extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private JTextField idField, nameField, iconField, crystalCountField, weightField, priceField;
	private JTextField basePDefField, enchantPDefField;
	
	private JComboBox<String> bodyPartCombo, crystalTypeCombo, armorTypeCombo, materialCombo, typeField, defaultActionField;
	
	private JTextArea previewArea;
	private List<L2ItemCreate> items = new ArrayList<>();
	
	public ArmorsXMLGenerator()
	{
		setBackground(new Color(40, 40, 50));
		setSize(1000, 650);
		setLayout(new BorderLayout());
		
		JPanel formPanel = new JPanel(new GridLayout(15, 2, 5, 5));
		formPanel.setBorder(new TitledBorder("Armor Item"));
		
		applyGrafiteTheme(formPanel);
		
		idField = addField(formPanel, "ItemId:");
		nameField = addField(formPanel, "Nome:");
		iconField = addField(formPanel, "Ícone:");
		
		JLabel crystalType = new JLabel("Type:");
		typeField = new JComboBox<>(new String[]
		{
			"Armor",
			"Weapon",
			"EtcItem"
		});
		formPanel.add(crystalType);
		formPanel.add(typeField);
		
		JLabel defaultTypeLabel = new JLabel("Default Action:");
		defaultActionField = new JComboBox<>(new String[]
		{
			"EQUIP",
		});
		formPanel.add(defaultTypeLabel);
		formPanel.add(defaultActionField);
		
		JLabel armorTypeLabel = new JLabel("Armor Type:");
		armorTypeCombo = new JComboBox<>(new String[]
		{
			"NONE",
			"HEAVY",
			"LIGHT",
			"MAGIC"
		});
		formPanel.add(armorTypeLabel);
		formPanel.add(armorTypeCombo);
		
		JLabel bodyPartLabel = new JLabel("Body Part:");
		bodyPartCombo = new JComboBox<>(new String[]
		{
			"FULLARMOR",
			"HEAD",
			"CHEST",
			"GLOVES",
			"LEGS",
			"FEET",
			"UNDERWEAR",
			"REAR,LEAR",
			"RFINGER,LFINGER",
			"NECK",
			"DHAIR",
			"FACE",
			"HAIR"
		});
		formPanel.add(bodyPartLabel);
		formPanel.add(bodyPartCombo);
		
		JLabel crystalTypeLabel = new JLabel("Crystal Type:");
		crystalTypeCombo = new JComboBox<>(new String[]
		{
			"NONE",
			"D",
			"C",
			"B",
			"A",
			"S"
		});
		formPanel.add(crystalTypeLabel);
		formPanel.add(crystalTypeCombo);
		
		crystalCountField = addField(formPanel, "Crystal Count:");
		JLabel materialLabel = new JLabel("Material:");
		materialCombo = new JComboBox<>(new String[]
		{
			"CLOTH",
			"LEATHER",
			"WOOD",
			"COTTON",
			"FINE_STELL",
			"CRYSTAL",
		});
		formPanel.add(materialLabel);
		formPanel.add(materialCombo);
		
		weightField = addField(formPanel, "Peso:");
		priceField = addField(formPanel, "Preço:");
		basePDefField = addField(formPanel, "Base P.Def:");
		enchantPDefField = addField(formPanel, "Enchant P.Def:");
		
		JButton addButton = new JButton("➕ Adicionar Item");
		addButton.addActionListener(new AddItemAction());
		formPanel.add(addButton);
		
		JButton clearButton = new JButton("🗑 Limpar Campos");
		clearButton.addActionListener(e -> clearFields());
		formPanel.add(clearButton);
		
		add(formPanel, BorderLayout.WEST);
		
		previewArea = new JTextArea();
		previewArea.setEditable(false);
		previewArea.setFont(new Font("Consolas", Font.PLAIN, 14));
		previewArea.setBackground(new Color(30, 30, 30));
		previewArea.setForeground(new Color(200, 200, 200));
		previewArea.setBorder(new TitledBorder("Preview XML"));
		
		JScrollPane previewScroll = new JScrollPane(previewArea);
		previewScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(previewScroll, BorderLayout.CENTER);
		JPanel footerPanel = new JPanel();
		footerPanel.setBackground(new Color(40, 40, 50));
		JButton saveButton = new JButton("💾 Salvar XML");
		saveButton.addActionListener(new SaveXMLArmor());
		footerPanel.add(saveButton);
		add(footerPanel, BorderLayout.SOUTH);
		
	}
	
	private JTextField addField(JPanel panel, String label)
	{
		JLabel jLabel = new JLabel(label);
		JTextField field = new JTextField();
		panel.add(jLabel);
		panel.add(field);
		return field;
	}
	
	private void updatePreview()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<list>\n");
		for (L2ItemCreate item : items)
		{
			sb.append(item.toXML()).append("\n\n");
		}
		sb.append("</list>");
		previewArea.setText(sb.toString());
	}
	
	private void clearFields()
	{
		idField.setText("");
		nameField.setText("");
		iconField.setText("");
		typeField.setSelectedIndex(0);
		defaultActionField.setSelectedIndex(0);
		armorTypeCombo.setSelectedIndex(0);
		bodyPartCombo.setSelectedIndex(0);
		crystalTypeCombo.setSelectedIndex(0);
		crystalCountField.setText("");
		materialCombo.setSelectedIndex(0);
		weightField.setText("");
		priceField.setText("");
		basePDefField.setText("");
		enchantPDefField.setText("");
	}
	
	private class AddItemAction implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				
				int id = Integer.parseInt(idField.getText());
				int crystalCount = Integer.parseInt(crystalCountField.getText());
				int weight = Integer.parseInt(weightField.getText());
				int price = Integer.parseInt(priceField.getText());
				int basePDef = Integer.parseInt(basePDefField.getText());
				int enchantPDef = Integer.parseInt(enchantPDefField.getText());
				
				L2ItemCreate item = new L2ItemCreate(id, (String) typeField.getSelectedItem(), nameField.getText(), iconField.getText(), (String) defaultActionField.getSelectedItem(), (String) armorTypeCombo.getSelectedItem(), (String) bodyPartCombo.getSelectedItem(), (String) crystalTypeCombo.getSelectedItem(), crystalCount, (String) materialCombo.getSelectedItem(), weight, price, basePDef, enchantPDef);
				
				items.add(item);
				updatePreview();
				
				clearFields();
			}
			catch (NumberFormatException ex)
			{
				JOptionPane.showMessageDialog(null, "❌ Erro: Verifique os campos numéricos (ID, peso, preço, etc).");
			}
		}
	}
	
	private void applyGrafiteTheme(JPanel panel)
	{
		panel.setBackground(new Color(45, 45, 45));
		panel.setForeground(Color.WHITE);
		
		for (Component comp : panel.getComponents())
		{
			if (comp instanceof JLabel)
			{
				comp.setForeground(new Color(220, 220, 220));
			}
			else if (comp instanceof JTextField)
			{
				JTextField field = (JTextField) comp;
				field.setBackground(new Color(60, 60, 60));
				field.setForeground(Color.WHITE);
				field.setCaretColor(Color.WHITE);
				field.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
			}
			else if (comp instanceof JComboBox)
			{
				JComboBox<?> combo = (JComboBox<?>) comp;
				combo.setBackground(new Color(60, 60, 60));
				combo.setForeground(Color.WHITE);
				combo.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
			}
			else if (comp instanceof JButton)
			{
				JButton button = (JButton) comp;
				button.setBackground(new Color(70, 70, 70));
				button.setForeground(Color.WHITE);
				button.setFocusPainted(false);
				button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
			}
			else if (comp instanceof JPanel)
			{
				applyGrafiteTheme((JPanel) comp);
			}
		}
	}
	
	private class SaveXMLArmor implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			String defaultFileName = UUID.randomUUID().toString().substring(0, 8) + ".xml";
			
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Salvar XML");
			fileChooser.setSelectedFile(new File(defaultFileName));
			fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos XML", "xml"));
			
			int userSelection = fileChooser.showSaveDialog((Component) e.getSource());
			
			if (userSelection == JFileChooser.APPROVE_OPTION)
			{
				File fileToSave = fileChooser.getSelectedFile();
				if (!fileToSave.getName().toLowerCase().endsWith(".xml"))
				{
					fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".xml");
				}
				
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave)))
				{
					writer.write(previewArea.getText());
					JOptionPane.showMessageDialog(null, "Arquivo salvo com sucesso:\n" + fileToSave.getAbsolutePath());
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(null, "Erro ao salvar o arquivo:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
}
