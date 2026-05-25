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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.util.prefs.Preferences;
import java.io.File;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import ext.mods.commons.gui.ThemeManager;
import ext.mods.commons.gui.ModernUI; 
import ext.mods.commons.gui.CustomTopPanel;
import ext.mods.commons.gui.ConfigGS;
import ext.mods.commons.gui.DBMonitorConsole;
import ext.mods.security.services.AuthService;
import ext.mods.security.services.DatabaseManager;
import ext.mods.commons.gui.services.ProcessManagerService;
import ext.mods.commons.services.IRamAllocationService;
import ext.mods.commons.services.RamAllocationService;
import ext.mods.security.services.IRatesManager;
import ext.mods.security.services.RatesManager;

public class MainFrame {

    private JFrame frame;
    private JPanel mainPanel;
    private CustomTopPanel topPanel;
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;

    private final Preferences prefs = Preferences.userRoot().node("project_dashboard");
    
    private final ProcessManagerService processManagerService = new ProcessManagerService();
    private final AuthService authService = new AuthService();
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final IRamAllocationService ramAllocationService = new RamAllocationService();
    private final IRatesManager ratesManager = new RatesManager();

    public MainFrame() {
        applyTheme();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("BR Project - GameServer Launcher");
        frame.setUndecorated(true);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        ext.mods.commons.pool.CoroutinePool.init();

        
        frame.setSize(650, 400); 
        frame.setLocationRelativeTo(null);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleCloseAction();
            }
        });
        
        JMenuBar menuBar = createMenuBar();
        Runnable closeAction = this::handleCloseAction;
        String iconPath = "./images/16x16.png";

        topPanel = new CustomTopPanel(frame, menuBar, closeAction, false, iconPath);
        frame.add(topPanel, BorderLayout.NORTH);

        mainPanel = new JPanel(new CardLayout());
        mainPanel.setBackground(ModernUI.BG_DARK); 
        
        loginPanel = new LoginPanel(frame, authService, this);
        dashboardPanel = new DashboardPanel(frame, databaseManager, processManagerService, this, ramAllocationService, ratesManager);

        mainPanel.add(loginPanel.getPanel(), "login");
        mainPanel.add(dashboardPanel.getPanel(), "dashboard");

        frame.add(mainPanel, BorderLayout.CENTER);
        
        loadIcons();
        
        String email = prefs.get("email", "brprojeto@l2jbrasil.com");
        String senha = prefs.get("senha", "12345678");
        
        boolean success = authService.authenticate(email, senha);

        if (success) {
            playServerLoadedSoundStart();
            LauncherApp.setLoggedUserEmail(email);
            LauncherApp.setKey(authService.generateRandomKey());
            authService.loadLicenses(email);
            showDashboardPanel();
            if (prefs.get("email", "").isEmpty() && prefs.get("senha", "").isEmpty()) {
                prefs.put("email", email);
                prefs.put("senha", senha);
            }
        } else {
            loginPanel.clearFields();
            JOptionPane.showMessageDialog(frame, "Credenciais inválidas! Por favor, faça login.");
            showLoginPanel();
        }

        frame.setVisible(true);
    }
    
    
    private void handleCloseAction() {
        String[] opcoes = {"Esconder", "Fechar", "Cancelar"};
        int escolha = JOptionPane.showOptionDialog(frame, "O que deseja fazer?", "Sair do sistema", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
        if (escolha == 0) { frame.setVisible(false); }
        else if (escolha == 1) { playServerLoadedSoundLogout(); System.exit(0); }
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder()); 

        JMenu menu = new JMenu("Painel");
        menu.setForeground(ThemeManager.TEXT_COLOR);
        
        JMenuItem itemSair = createMenuItem("Sair", e -> logout());
        menu.add(itemSair);
        
        JMenuItem itemConfig = createMenuItem("Config", e -> {
            ConfigGS configWindow = new ConfigGS(frame);
            configWindow.showWindow();
        });
        menu.add(itemConfig);
        
        JMenuItem itemDBMonitor = createMenuItem("DB Monitor", e -> {
            DBMonitorConsole.getInstance().showWindow();
        });
        menu.add(itemDBMonitor);
        
        menuBar.add(menu);
        
        JMenu menuSuporte = new JMenu("Suporte");
        menuSuporte.setForeground(ThemeManager.TEXT_COLOR);
        
        JMenuItem itemReport = createMenuItem("Report Bug", e -> JOptionPane.showMessageDialog(frame, "Não há bugs reportados."));
        menuSuporte.add(itemReport);
        menuBar.add(menuSuporte);
        
        return menuBar;
    }

    private JMenuItem createMenuItem(String title, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(title);
        if (listener != null) item.addActionListener(listener);
        return item;
    }
    
    public void showLoginPanel() {
        loginPanel.updateRememberMeState();
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "login");
    }
    
    public void showDashboardPanel() {
        dashboardPanel.updateLayout(LauncherApp.getKey()); 
        CardLayout cl = (CardLayout) mainPanel.getLayout();
        cl.show(mainPanel, "dashboard");
    }
    
    public void refreshLicenses() {
        authService.loadLicenses(LauncherApp.getLoggedUserEmail());
        showDashboardPanel();
    }

    public void logout() {
        if (JOptionPane.showConfirmDialog(frame, "Tem certeza que deseja logout?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            playServerLoadedSoundLogout();
            if (loginPanel.getChkRemember() == null || !loginPanel.getChkRemember().isSelected()) {
                prefs.remove("email");
                prefs.remove("senha");
            }
            LauncherApp.setLoggedUserEmail(null);
            LauncherApp.setKey(null);
            loginPanel.clearFields();
            showLoginPanel();
        }
    }

    private void applyTheme() {
        ThemeManager.applyTheme();
    }
    
    private void loadIcons() {
        java.util.List<Image> icons = new ArrayList<>();
        try {
            ImageIcon icon16 = new ImageIcon("./images/16x16.png");
            if (icon16.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) icons.add(icon16.getImage());
        } catch (Exception e) {}
        try {
            ImageIcon icon32 = new ImageIcon("./images/32x32.png");
            if (icon32.getImageLoadStatus() == java.awt.MediaTracker.COMPLETE) icons.add(icon32.getImage());
        } catch (Exception e) {}
        if (!icons.isEmpty()) frame.setIconImages(icons);
    }
    
    public void playServerLoadedSoundStart() { playSound("Start.wav"); }
    public void playServerLoadedSoundLogout() { playSound("Shutdown.wav"); }

    public void playSound(String fileName) {
        try {
            File soundFile = new File("./sound/" + fileName);
            if (!soundFile.exists()) return;
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {}
    }
}