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
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class InterfaceAbout {

	private JFrame frmCredits;

	private static final Color BG_COLOR = new Color(0x121212);
	private static final Color TEXT_COLOR = new Color(0xCCCCCC);
	private static final Color ACCENT_COLOR = new Color(0, 122, 204);
	private static final Color HEADER_COLOR = Color.WHITE;
	private static final Color LINK_COLOR_NORMAL = new Color(0, 150, 255);
	private static final Color LINK_COLOR_HOVER = new Color(80, 180, 255);
	private static final Color SECONDARY_TEXT_COLOR = new Color(0x888888);
    private static final Color SEPARATOR_COLOR = new Color(0x2c2c2c);

	private static final String L2JBRASIL = "https://l2jbrasil.com";
	private static final String L2JCENTER = "https://l2jcenter.com";

	public InterfaceAbout() {
		initialize();
		frmCredits.setVisible(true);
	}

	private void initialize() {
		frmCredits = new JFrame();
		frmCredits.setResizable(false);
		frmCredits.setTitle("Créditos e Parceiros");
		frmCredits.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frmCredits.setType(JFrame.Type.UTILITY);

		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBackground(BG_COLOR);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		frmCredits.getContentPane().add(mainPanel, BorderLayout.CENTER);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 0, 5, 0);

		try {
			ImageIcon logoIcon = new ImageIcon("./images/logo.png");
			if (logoIcon.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) {
				Image logoImage = logoIcon.getImage().getScaledInstance(150, -1, Image.SCALE_SMOOTH);
				JLabel lblLogo = new JLabel(new ImageIcon(logoImage));
				lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
				mainPanel.add(lblLogo, gbc);
			} else {
				System.err.println("Warning: Logo image not found or failed to load at ./images/logo.png");
			}
		} catch (Exception e) {
			System.err.println("Error loading logo image: " + e.getMessage());
		}

		JLabel lblTitle = new JLabel("BR PROJECT - Créditos");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
		lblTitle.setForeground(HEADER_COLOR);
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.insets = new Insets(10, 0, 5, 0);
		mainPanel.add(lblTitle, gbc);

		JLabel lblHeadProject = new JLabel("Head Project: agaze33");
		lblHeadProject.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		lblHeadProject.setForeground(ACCENT_COLOR);
		lblHeadProject.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.insets = new Insets(0, 0, 15, 0);
		mainPanel.add(lblHeadProject, gbc);

		JSeparator separator1 = new JSeparator(SwingConstants.HORIZONTAL);
		separator1.setForeground(SEPARATOR_COLOR);
		separator1.setBackground(BG_COLOR);
		mainPanel.add(separator1, gbc);

		gbc.insets = new Insets(10, 0, 2, 0);
		JLabel lblDevs = new JLabel("Desenvolvedores: Dhouseff, Ban, SrEli");
		setupLabel(lblDevs);
		mainPanel.add(lblDevs, gbc);

		gbc.insets = new Insets(2, 0, 15, 0);
		JLabel lblSupporters = new JLabel("Devs Apoiadores: ColdPlay, Warmen");
		setupLabel(lblSupporters);
		mainPanel.add(lblSupporters, gbc);

		JSeparator separator2 = new JSeparator(SwingConstants.HORIZONTAL);
		separator2.setForeground(SEPARATOR_COLOR);
		separator2.setBackground(BG_COLOR);
		mainPanel.add(separator2, gbc);

		gbc.insets = new Insets(15, 0, 5, 0);
		JLabel lblPartners = new JLabel("Parceiros");
		lblPartners.setHorizontalAlignment(SwingConstants.CENTER);
		lblPartners.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblPartners.setForeground(HEADER_COLOR);
		mainPanel.add(lblPartners, gbc);

		JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		linksPanel.setOpaque(false);
		linksPanel.add(createLinkLabel("L2JBrasil.com", L2JBRASIL));
		JLabel pipeLabel = new JLabel("|");
		pipeLabel.setForeground(SECONDARY_TEXT_COLOR);
		linksPanel.add(pipeLabel);
		linksPanel.add(createLinkLabel("L2JCenter.com", L2JCENTER));
		gbc.insets = new Insets(5, 0, 15, 0);
		mainPanel.add(linksPanel, gbc);

		JLabel lblFooter = new JLabel("Obrigado por apoiar o projeto!");
		lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
		lblFooter.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		lblFooter.setForeground(SECONDARY_TEXT_COLOR);
		mainPanel.add(lblFooter, gbc);

		frmCredits.pack();
		frmCredits.setMinimumSize(new Dimension(350, (int)frmCredits.getPreferredSize().getHeight()));
		frmCredits.setLocationRelativeTo(null);
	}

	private void setupLabel(JLabel label) {
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		label.setForeground(TEXT_COLOR);
	}

	private static JLabel createLinkLabel(String text, String url) {
		String normalColorHex = String.format("#%02x%02x%02x", LINK_COLOR_NORMAL.getRed(), LINK_COLOR_NORMAL.getGreen(), LINK_COLOR_NORMAL.getBlue());
		String hoverColorHex = String.format("#%02x%02x%02x", LINK_COLOR_HOVER.getRed(), LINK_COLOR_HOVER.getGreen(), LINK_COLOR_HOVER.getBlue());

		JLabel label = new JLabel("<html><font color='" + normalColorHex + "'><u>" + text + "</u></font></html>");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					try {
						Desktop.getDesktop().browse(new URI(url));
					} catch (IOException | URISyntaxException ex) {
						System.err.println("Failed to open link: " + url + " - " + ex.getMessage());
					}
				} else {
					System.err.println("Desktop browsing not supported.");
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				label.setText("<html><font color='" + hoverColorHex + "'><u>" + text + "</u></font></html>");
			}

			@Override
			public void mouseExited(MouseEvent e) {
				label.setText("<html><font color='" + normalColorHex + "'><u>" + text + "</u></font></html>");
			}
		});
		return label;
	}

	public static void main(String[] args) {
		ThemeManager.applyTheme();
		java.awt.EventQueue.invokeLater(() -> new InterfaceAbout());
	}
}