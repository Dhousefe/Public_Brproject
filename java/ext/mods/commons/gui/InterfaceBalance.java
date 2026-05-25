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
package ext.mods.commons.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;

public class InterfaceBalance
{
	JTextArea txtrConsole;
	
	static final String[] shutdownOptions =
	{
		"Shutdown",
		"Cancel"
	};
	
	public InterfaceBalance()
	{
		try
		{
			UIManager.put("control", new Color(40, 40, 40));
			UIManager.put("info", new Color(60, 63, 65));
			UIManager.put("nimbusBase", new Color(30, 30, 30));
			UIManager.put("nimbusBlueGrey", new Color(70, 73, 75));
			UIManager.put("nimbusLightBackground", new Color(30, 30, 30));
			UIManager.put("text", new Color(220, 220, 220));
			
			UIManager.put("nimbusSelectionBackground", new Color(60, 120, 200));
			UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
			UIManager.put("nimbusFocus", new Color(100, 150, 255));
			
			UIManager.put("nimbusDisabledText", new Color(100, 100, 100));
			
			UIManager.put("nimbusRed", new Color(150, 60, 60));
			UIManager.put("nimbusOrange", new Color(200, 120, 60));
			UIManager.put("nimbusGreen", new Color(100, 160, 100));
			UIManager.put("nimbusAlertYellow", new Color(255, 210, 60));
			UIManager.put("nimbusInfoBlue", new Color(80, 140, 255));
			
			UIManager.setLookAndFeel(new NimbusLookAndFeel());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		txtrConsole = new JTextArea();
		txtrConsole.setEditable(false);
		txtrConsole.setLineWrap(true);
		txtrConsole.setWrapStyleWord(true);
		txtrConsole.setDropMode(DropMode.INSERT);
		txtrConsole.setFont(new Font("Monospaced", Font.PLAIN, 12));
		txtrConsole.getDocument().addDocumentListener(new InterfaceLimit(500));
		
		final JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		final JMenu mnBalance = new JMenu("Option");
		mnBalance.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnBalance);
		
		final JMenuItem viewModifiers = new JMenuItem("Damage");
		viewModifiers.addActionListener(e ->
		{
			JFrame balanceFrame = new JFrame("Information Balance");
			damage model = new damage();
			JTable table = new JTable(model);
			JScrollPane scroll = new JScrollPane(table);
			
			DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer()
			{
				private static final long serialVersionUID = 1L;
				
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
				{
					Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					
					if (!isSelected)
					{
						c.setBackground(UIManager.getColor("Table.background"));
						c.setForeground(UIManager.getColor("Table.foreground"));
					}
					else
					{
						c.setBackground(table.getSelectionBackground());
						c.setForeground(table.getSelectionForeground());
					}
					
					if (column >= 2 && column <= 5 && value instanceof Number)
					{
						double val = ((Number) value).doubleValue();
						if (val != 1.0)
						{
							if (!isSelected)
							{
								c.setForeground(val > 1.0 ? new Color(100, 220, 100) : new Color(220, 100, 100));
							}
						}
					}
					
					return c;
				}
			};
			
			for (int i = 2; i <= 3; i++)
			{
				table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
			}
			
			JPanel topPanel = new JPanel(new BorderLayout());
			JLabel label = new JLabel("Filtrar classe: ");
			JTextField filterField = new JTextField();
			topPanel.add(label, BorderLayout.WEST);
			topPanel.add(filterField, BorderLayout.CENTER);
			
			filterField.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void insertUpdate(javax.swing.event.DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
				
				@Override
				public void removeUpdate(javax.swing.event.DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
				
				@Override
				public void changedUpdate(javax.swing.event.DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
			});
			
			JButton helpButton = new JButton("❓");
			helpButton.setToolTipText("Clique para entender o sistema de balanceamento");
			
			helpButton.addActionListener(e2 ->
			{
				String message = "➤ PAINEL DE BALANCEAMENTO DE DANO ENTRE CLASSES\n\n" + "▸ Este painel permite controlar o dano físico (P.Atk) e mágico (M.Atk) entre classes PvP.\n" + "▸ Cada linha representa o modificador de dano de uma classe (Attacker) contra outra (Target).\n\n" + "✦ P.Atk → Controla o dano físico causado.\n" + "✦ M.Atk → Controla o dano mágico causado.\n\n" + "✔ Valor 1.0: Dano normal, sem alteração.\n" + "✔ Valor acima de 1.0: Aumenta o dano (ex: 1.2 = 20% a mais).\n" + "✔ Valor abaixo de 1.0: Reduz o dano (ex: 0.8 = 20% a menos).\n\n" + "▸ Os valores modificam diretamente o cálculo do dano final entre as classes.\n" + "▸ Os valores coloridos indicam modificações:\n" + "   - Verde: aumento de dano.\n" + "   - Vermelho: redução de dano.\n\n" + "▸ Use o filtro acima para buscar uma classe específica e editar seus modificadores.";
				
				JOptionPane.showMessageDialog(balanceFrame, message, "Ajuda - Sistema de Balanceamento", JOptionPane.INFORMATION_MESSAGE);
			});
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(topPanel, BorderLayout.NORTH);
			mainPanel.add(scroll, BorderLayout.CENTER);
			topPanel.add(helpButton, BorderLayout.EAST);
			
			final List<Image> icons = new ArrayList<>();
			icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "16x16.png").getImage());
			icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "32x32.png").getImage());
			
			balanceFrame.setIconImages(icons);
			balanceFrame.setContentPane(mainPanel);
			balanceFrame.setSize(600, 400);
			balanceFrame.setLocationRelativeTo(null);
			balanceFrame.setVisible(true);
		});
		mnBalance.add(viewModifiers);
		
		
		final JMenuItem viewDefence = new JMenuItem("Defence");
		viewDefence.addActionListener(e ->
		{
			JFrame balanceFrame = new JFrame("Information Balance");
			defence model = new defence();
			JTable table = new JTable(model);
			JScrollPane scroll = new JScrollPane(table);
			
			DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer()
			{
				private static final long serialVersionUID = 1L;
				
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
				{
					Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					
					if (!isSelected)
					{
						c.setBackground(UIManager.getColor("Table.background"));
						c.setForeground(UIManager.getColor("Table.foreground"));
					}
					else
					{
						c.setBackground(table.getSelectionBackground());
						c.setForeground(table.getSelectionForeground());
					}
					if (column >= 2 && column <= 5 && value instanceof Number)
					{
						double val = ((Number) value).doubleValue();
						if (val != 1.0)
						{
							if (!isSelected)
							{
								
								c.setForeground(val > 1.0 ? new Color(100, 220, 100) : new Color(220, 100, 100));
							}
						}
					}
					
					return c;
				}
			};
			
			for (int i = 2; i <= 3; i++)
			{
				table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
			}
			
			JPanel topPanel = new JPanel(new BorderLayout());
			JLabel label = new JLabel("Filtrar classe: ");
			JTextField filterField = new JTextField();
			topPanel.add(label, BorderLayout.WEST);
			topPanel.add(filterField, BorderLayout.CENTER);
			
			filterField.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void insertUpdate(javax.swing.event.DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
				
				@Override
				public void removeUpdate(javax.swing.event.DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
				
				@Override
				public void changedUpdate(javax.swing.event.DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
			});
			
			JButton helpButton = new JButton("❓");
			helpButton.setToolTipText("Clique para entender o sistema de defesa entre classes");
			
			helpButton.addActionListener(e2 ->
			{
				String message = "➤ PAINEL DE BALANCEAMENTO DE DEFESA ENTRE CLASSES\n\n" + "▸ Este painel permite controlar a defesa física (P.Def) e mágica (M.Def) que uma classe recebe contra outra.\n" + "▸ Cada linha representa o modificador de defesa que a classe Target (alvo) terá ao enfrentar a classe Attacker (atacante).\n\n" + "✦ P.Def → Controla a defesa física que será aplicada contra ataques físicos.\n" + "✦ M.Def → Controla a defesa mágica que será aplicada contra ataques mágicos.\n\n" + "✔ Valor 1.0: Defesa normal, sem alteração.\n" + "✔ Valor acima de 1.0: Aumenta a defesa (ex: 1.2 = 20% a mais de resistência).\n" + "✔ Valor abaixo de 1.0: Reduz a defesa (ex: 0.8 = 20% mais vulnerável).\n\n" + "▸ Os valores modificam diretamente a resistência que o alvo terá ao ser atacado por aquela classe.\n" + "▸ Os valores coloridos indicam modificações:\n" + "   - Verde: aumento de defesa.\n" + "   - Vermelho: redução de defesa.\n\n" + "▸ Use o campo de filtro para buscar uma classe específica e ajustar suas vulnerabilidades defensivas.";
				
				JOptionPane.showMessageDialog(balanceFrame, message, "Ajuda - Sistema de Defesa", JOptionPane.INFORMATION_MESSAGE);
			});
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(topPanel, BorderLayout.NORTH);
			mainPanel.add(scroll, BorderLayout.CENTER);
			topPanel.add(helpButton, BorderLayout.EAST);
			final List<Image> icons = new ArrayList<>();
			icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "16x16.png").getImage());
			icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "32x32.png").getImage());
			
			balanceFrame.setIconImages(icons);
			balanceFrame.setContentPane(mainPanel);
			balanceFrame.setSize(600, 400);
			balanceFrame.setLocationRelativeTo(null);
			balanceFrame.setVisible(true);
		});
		mnBalance.add(viewDefence);
		
		
		final JMenuItem viewvulnerability = new JMenuItem("Vulnerability");
		viewvulnerability.addActionListener(e ->
		{
			JFrame balanceFrame = new JFrame("Information Balance");
			vulnerabilityefence model = new vulnerabilityefence();
			JTable table = new JTable(model);
			JScrollPane scroll = new JScrollPane(table);
			
			DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer()
			{
				private static final long serialVersionUID = 1L;
				
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
				{
					Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					
					if (!isSelected)
					{
						c.setBackground(UIManager.getColor("Table.background"));
						c.setForeground(UIManager.getColor("Table.foreground"));
					}
					else
					{
						c.setBackground(table.getSelectionBackground());
						c.setForeground(table.getSelectionForeground());
					}
					if (column >= 2 && column <= 5 && value instanceof Number)
					{
						double val = ((Number) value).doubleValue();
						if (val != 1.0)
						{
							if (!isSelected)
							{
								
								c.setForeground(val > 1.0 ? new Color(100, 220, 100) : new Color(220, 100, 100));
							}
						}
					}
					
					return c;
				}
			};
			
			for (int i = 1; i <= 1; i++)
			{
				table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
			}
			
			JPanel topPanel = new JPanel(new BorderLayout());
			JLabel label = new JLabel("Filtrar Type: ");
			JTextField filterField = new JTextField();
			topPanel.add(label, BorderLayout.WEST);
			topPanel.add(filterField, BorderLayout.CENTER);
			
			filterField.getDocument().addDocumentListener(new DocumentListener()
			{
				@Override
				public void insertUpdate(DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
				
				@Override
				public void removeUpdate(DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
				
				@Override
				public void changedUpdate(DocumentEvent e)
				{
					model.filter(filterField.getText());
				}
			});
			
			JButton helpButton = new JButton("❓");
			helpButton.setToolTipText("Clique para entender o sistema de vulnerabilidade por tipo de skill");
			
			helpButton.addActionListener(e2 ->
			{
				String message = "➤ PAINEL DE VULNERABILIDADE POR TIPO DE SKILL\n\n" + "▸ Esta aba permite controlar o quanto as classes são vulneráveis a efeitos de habilidades (skills) específicas.\n\n" + "✦ Skill Type → Define o tipo de efeito da skill (ex: PARALYZE, SLEEP, STUN etc).\n" + "✦ Multiplier → Define o modificador de chance que o tipo de efeito terá ao ser aplicado na vítima.\n\n" + "✔ Valor 1.0: Chance normal da skill.\n" + "✔ Valor acima de 1.0: Aumenta a chance de aplicação (ex: 1.2 = 20% mais fácil de aplicar).\n" + "✔ Valor abaixo de 1.0: Reduz a chance de aplicação (ex: 0.8 = 20% mais difícil de aplicar).\n\n" + "▸ Esses valores atuam sobre a **resistência global** de todas as classes a cada tipo de efeito.\n" + "▸ Por exemplo, se STUN estiver com 0.8, todas as classes do jogo terão 20% mais resistência contra stun.\n\n" + "▸ Use esse painel para balancear o impacto de efeitos de controle no PvP/PvE, reduzindo abusos ou fortalecendo tipos pouco usados.";
				
				JOptionPane.showMessageDialog(balanceFrame, message, "Ajuda - Vulnerabilidades de Skills", JOptionPane.INFORMATION_MESSAGE);
			});
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(topPanel, BorderLayout.NORTH);
			mainPanel.add(scroll, BorderLayout.CENTER);
			
			topPanel.add(helpButton, BorderLayout.EAST);
			
			final List<Image> icons = new ArrayList<>();
			icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "16x16.png").getImage());
			icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "32x32.png").getImage());
			
			balanceFrame.setIconImages(icons);
			balanceFrame.setContentPane(mainPanel);
			balanceFrame.setSize(600, 400);
			balanceFrame.setLocationRelativeTo(null);
			balanceFrame.setVisible(true);
		});
		mnBalance.add(viewvulnerability);
		
		final JMenu mnFont = new JMenu("Font");
		mnFont.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		menuBar.add(mnFont);
		
		final String[] fonts =
		{
			"10",
			"13",
			"16",
			"21",
			"27",
			"33"
		};
		for (String font : fonts)
		{
			final JMenuItem mntmFont = new JMenuItem(font);
			mntmFont.setFont(new Font("Segoe UI", Font.PLAIN, 12));
			mntmFont.addActionListener(arg0 -> txtrConsole.setFont(new Font("Monospaced", Font.PLAIN, Integer.parseInt(font))));
			mnFont.add(mntmFont);
		}
		
		final List<Image> icons = new ArrayList<>();
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "16x16.png").getImage());
		icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "32x32.png").getImage());
		
		
		final JScrollPane scrollPanel = new JScrollPane(txtrConsole);
		
		final JFrame frame = new JFrame("Balance");
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent ev)
			{
				if (JOptionPane.showOptionDialog(null, "Shutdown balancer immediately?", "Select an option", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, shutdownOptions, shutdownOptions[1]) == 0)
				{
					frame.setVisible(false);
				}
			}
		});
		
		frame.addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent ev)
			{
				scrollPanel.setSize(frame.getContentPane().getSize());
				
			}
		});
		frame.setJMenuBar(menuBar);
		frame.setIconImages(icons);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.add(scrollPanel, BorderLayout.CENTER);
		frame.getContentPane().setPreferredSize(new Dimension(400, 360));
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		redirectSystemStreams();
		
		frame.setVisible(true);
		frame.toFront();
		frame.setState(Frame.ICONIFIED);
		frame.setState(Frame.NORMAL);
		
	}
	
	private void redirectSystemStreams()
	{
		final OutputStream out = new OutputStream()
		{
			@Override
			public void write(int b)
			{
				updateTextArea(String.valueOf((char) b));
			}
			
			@Override
			public void write(byte[] b, int off, int len)
			{
				updateTextArea(new String(b, off, len));
			}
			
			@Override
			public void write(byte[] b)
			{
				write(b, 0, b.length);
			}
		};
		
		System.setOut(new PrintStream(out, true));
		System.setErr(new PrintStream(out, true));
	}
	
	void updateTextArea(String text)
	{
		SwingUtilities.invokeLater(() ->
		{
			txtrConsole.append(text);
			txtrConsole.setCaretPosition(txtrConsole.getText().length());
		});
	}
}
