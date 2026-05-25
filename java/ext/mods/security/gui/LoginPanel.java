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
package ext.mods.security.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ext.mods.commons.gui.ThemeManager;
import ext.mods.security.services.AuthService;

public class LoginPanel {
    
    private final JFrame parentFrame;
    private final AuthService authService;
    private final MainFrame mainFrame; 

    private JPanel loginPanel;
    private JCheckBox chkRemember;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    
    private final Preferences prefs = Preferences.userRoot().node("project_dashboard");

	private static final Color COMPONENT_BACKGROUND = ThemeManager.COMPONENT_BACKGROUND;
	private static final Color TEXT_COLOR = ThemeManager.TEXT_SELECTED;
	private static final Color BASE_PURPLE = ThemeManager.BASE_PURPLE;
	private static final Color SOFT_PURPLE_SELECTION = ThemeManager.SOFT_PURPLE_SELECTION;
    
	private static final Color LINK_COLOR_NORMAL = new Color(0xB380FF);
	private static final Color LINK_COLOR_HOVER = new Color(0xE0AAFF);

    public LoginPanel(JFrame parentFrame, AuthService authService, MainFrame mainFrame) {
        this.parentFrame = parentFrame;
        this.authService = authService;
        this.mainFrame = mainFrame;
        createPanel();
    }
    
    public JPanel getPanel() {
        return loginPanel;
    }
    
    public JCheckBox getChkRemember() {
        return chkRemember;
    }
    
    public void clearFields() {
        if (txtEmail != null) txtEmail.setText("");
        if (txtSenha != null) txtSenha.setText("");
    }
    
    public void updateRememberMeState() {
        if (chkRemember == null) return;
        String savedEmail = prefs.get("email", ""); 
        String savedSenha = prefs.get("senha", "");
		if (!savedEmail.isEmpty() && !savedSenha.isEmpty()) {
            txtEmail.setText(savedEmail);
            txtSenha.setText(savedSenha);
            chkRemember.setSelected(true);
        } else {
             chkRemember.setSelected(false);
             txtEmail.setText("brprojeto@l2jbrasil.com"); 
             txtSenha.setText("12345678");
        }
    }

	private void createPanel() {
		loginPanel = new JPanel(new BorderLayout());
		loginPanel.setBackground(COMPONENT_BACKGROUND); 

		JPanel boxPanel = new JPanel(new GridBagLayout())
		{
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
				int width = getWidth(); int height = getHeight();
				if (ext.mods.commons.gui.ThemeManager.isSafeGraphics()) {
					g2d.setColor(BASE_PURPLE.darker().darker());
				} else {
					g2d.setPaint(new GradientPaint(0, 0, SOFT_PURPLE_SELECTION.darker(), 0, height, BASE_PURPLE.darker().darker()));
				}
				g2d.fillRoundRect(0, 0, width, height, 20, 20);
				g2d.dispose();
			}
		};
		boxPanel.setOpaque(false);
		boxPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 5, 8, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

		JLabel lblTitulo = new JLabel("Acesse sua Conta");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
		gbc.insets = new Insets(0, 0, 20, 0);
		boxPanel.add(lblTitulo, gbc);

		gbc.insets = new Insets(8, 0, 0, 5);
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridwidth = 1;
        gbc.weightx = 0;

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setForeground(TEXT_COLOR);
		lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		gbc.gridx = 0; gbc.gridy = 1;
		boxPanel.add(lblEmail, gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;

		txtEmail = new JTextField(15);
        txtEmail.setText("brprojeto@l2jbrasil.com");
		gbc.gridx = 1; gbc.gridy = 1;
		boxPanel.add(txtEmail, gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0;

		JLabel lblSenha = new JLabel("Senha:");
		lblSenha.setForeground(TEXT_COLOR);
		lblSenha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		gbc.gridx = 0; gbc.gridy = 2;
		boxPanel.add(lblSenha, gbc);

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.weightx = 1.0;

		txtSenha = new JPasswordField(15);
        txtSenha.setText("12345678");
		gbc.gridx = 1; gbc.gridy = 2;
		boxPanel.add(txtSenha, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(0, 0, 0, 0);
		JCheckBox chkShowSenha = new JCheckBox("Mostrar senha");
		chkShowSenha.setForeground(TEXT_COLOR.brighter());
		chkShowSenha.setOpaque(false);
        chkShowSenha.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		chkShowSenha.addActionListener(e -> txtSenha.setEchoChar(chkShowSenha.isSelected() ? (char) 0 : '*'));
		boxPanel.add(chkShowSenha, gbc);

		gbc.gridy = 4;
		chkRemember = new JCheckBox("Lembre-se de mim");
		chkRemember.setForeground(TEXT_COLOR.brighter());
		chkRemember.setOpaque(false);
        chkRemember.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        updateRememberMeState(); 
		boxPanel.add(chkRemember, gbc);

        JPanel buttonPanelLogin = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanelLogin.setOpaque(false);

		JButton btnLogin = new JButton("Login");
		btnLogin.setFocusPainted(false);
		btnLogin.setBackground(BASE_PURPLE);
		btnLogin.setForeground(Color.WHITE);
		btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanelLogin.add(btnLogin);

		JButton btnVoltarKeyFree = new JButton("Free");
		btnVoltarKeyFree.setToolTipText("Usar conta gratuita padrão");
		btnVoltarKeyFree.setFocusPainted(false);
		btnVoltarKeyFree.setBackground(BASE_PURPLE.darker());
		btnVoltarKeyFree.setForeground(TEXT_COLOR);
		btnVoltarKeyFree.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnVoltarKeyFree.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPanelLogin.add(btnVoltarKeyFree);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 0, 0);
        boxPanel.add(buttonPanelLogin, gbc);


		btnLogin.addActionListener(e -> handleLogin());

        btnVoltarKeyFree.addActionListener(e -> {
			txtEmail.setText("brprojeto@l2jbrasil.com"); txtSenha.setText("12345678"); chkRemember.setSelected(true);
            handleLogin();
		});

		txtSenha.addActionListener(e -> btnLogin.doClick());

		JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
		footerPanel.setBackground(COMPONENT_BACKGROUND);
		footerPanel.add(new JLabel("Parceiros:") {{ setForeground(TEXT_COLOR); setFont(new Font("Segoe UI", Font.PLAIN, 10)); }});
		footerPanel.add(createLinkLabel("L2JBrasil", "https://www.l2jbrasil.com"));
		footerPanel.add(new JLabel("|") {{ setForeground(TEXT_COLOR); setFont(new Font("Segoe UI", Font.BOLD, 10)); }});
		footerPanel.add(createLinkLabel("L2JCenter", "https://www.l2jcenter.com"));

		loginPanel.add(boxPanel, BorderLayout.CENTER);
		loginPanel.add(footerPanel, BorderLayout.SOUTH);
	}
    
    private void handleLogin() {
        String email = txtEmail.getText();
        String senha = new String(txtSenha.getPassword());
        if (email.isEmpty() || senha.isEmpty()) { JOptionPane.showMessageDialog(parentFrame, "Preencha todos os campos!"); return; }
        
        boolean success = authService.authenticate(email, senha);
        
        if (success) {
            mainFrame.playServerLoadedSoundStart();
            LauncherApp.setLoggedUserEmail(email);
            LauncherApp.setKey(authService.generateRandomKey()); 
            authService.loadLicenses(email);
            mainFrame.showDashboardPanel();
            
            if (chkRemember.isSelected()) { 
                prefs.put("email", email); 
                prefs.put("senha", senha); 
            } else { 
                prefs.remove("email"); 
                prefs.remove("senha"); 
            }
        } else { 
            JOptionPane.showMessageDialog(parentFrame, "Credenciais inválidas!"); 
        }
    }

	private JLabel createLinkLabel(String text, String url) {
		JLabel label = new JLabel("<html><a href='' style='color: " + String.format("#%06x", LINK_COLOR_NORMAL.getRGB() & 0xFFFFFF) + "; text-decoration: none;'>" + text + "</a></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		label.setForeground(LINK_COLOR_NORMAL);
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) { 
                try { Desktop.getDesktop().browse(new URI(url)); } 
                catch (Exception ex) { 
                    ex.printStackTrace(); 
                    JOptionPane.showMessageDialog(parentFrame, "Não foi possível abrir o link: " + url); 
                }}
			@Override public void mouseEntered(MouseEvent e) { 
                label.setText("<html><a href='' style='color: " + String.format("#%06x", LINK_COLOR_HOVER.getRGB() & 0xFFFFFF) + "; text-decoration: underline;'>" + text + "</a></html>"); 
            }
			@Override public void mouseExited(MouseEvent e) { 
                label.setText("<html><a href='' style='color: " + String.format("#%06x", LINK_COLOR_NORMAL.getRGB() & 0xFFFFFF) + "; text-decoration: none;'>" + text + "</a></html>"); 
            }
		});
		return label;
	}
}