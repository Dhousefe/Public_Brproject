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
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, 
* SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo, Tiagorosendo, Schuster, LucasStark, damedd
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ComponentAdapter; 
import java.awt.event.ComponentEvent; 
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent;  
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JLayeredPane; 
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import ext.mods.commons.gui.actions.*;
import ext.mods.commons.gui.tools.*;
import ext.mods.gameserver.LoginServerThread;
import ext.mods.gameserver.Shutdown;
import ext.mods.security.gui.items.amors.ArmorsXMLGenerator;

public class InterfaceGS {

    private JTextArea console;
    private static final String[] shutdownOptions = {"Shutdown", "Cancel"};
    private JFrame frame;
    private CustomTopPanel topPanel; 
    
    private JPanel systemPanel; 
    private JPanel metricsPanel; 
    private JLayeredPane layeredPanel; 
    private JScrollPane scrollPanel; 

    private static final int DEFAULT_WIDTH = 850;
    private static final int DEFAULT_HEIGHT = 517;

    static { Thread.setDefaultUncaughtExceptionHandler(null); }

    public InterfaceGS() {
        ThemeManager.applyTheme();
        initialize();
    }

    private void initialize() {
        console = new JTextArea();
        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        console.setDropMode(DropMode.INSERT);
        console.setFont(new Font("Monospaced", Font.PLAIN, 13));
        console.getDocument().addDocumentListener(new InterfaceLimit(500));
        console.setBackground(ThemeManager.COMPONENT_BACKGROUND); 
        console.setForeground(ThemeManager.TEXT_COLOR); 

        Properties geoProps = GuiUtils.loadProperties("./config/geoengine.properties");
        Properties serverProps = GuiUtils.loadProperties("./config/server.properties");
        boolean isLightMode = Boolean.parseBoolean(geoProps.getProperty("UseMinimalGeoOnly", "false"));
        boolean isDevMode = Boolean.parseBoolean(serverProps.getProperty("Developer", "false"));
        String worldTitle = "World";
        if (isLightMode && isDevMode) worldTitle = "World - Light/Developer Mode";
        else if (isLightMode) worldTitle = "World - Light Mode";
        else if (isDevMode) worldTitle = "World - Developer Mode";

        frame = new JFrame(worldTitle);
        frame.setUndecorated(true); 
        
        ComponentResizer resizer = new ComponentResizer(frame);

        JMenuBar menuBar = createMenuBar();

        Runnable closeAction = () -> {
            boolean isFullyConnected = false; 
            try { 
                LoginServerThread lsThread = LoginServerThread.getInstance(); 
                String serverName = (lsThread != null) ? lsThread.getServerName() : null; 
                if (serverName != null && !serverName.isEmpty()) isFullyConnected = true; 
            } catch (Exception ex) { 
                isFullyConnected = false; 
            } 
            if (isFullyConnected) { 
                if (JOptionPane.showOptionDialog(frame, "Shutdown server immediately?", "Select an option", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, shutdownOptions, shutdownOptions[1]) == 0) { 
                    Shutdown.getInstance().startShutdown(null, 1, false); 
                } 
            } else { 
                System.exit(0); 
            }
        };

        String iconPath = ".." + File.separator + "images" + File.separator + "16x16.png";
        topPanel = new CustomTopPanel(frame, menuBar, closeAction, true, iconPath);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);

        systemPanel = new InterfaceGSInfo(); 
        metricsPanel = new InterfaceCoroutinePoolMetrics();

        scrollPanel = new JScrollPane(console); 
        scrollPanel.setBorder(null);
        scrollPanel.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND); 
        scrollPanel.getVerticalScrollBar().setUI(new ModernUI.ModernScrollBarUI());
        scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
        
        layeredPanel = new JLayeredPane(); 
        layeredPanel.add(scrollPanel, Integer.valueOf(0));
        layeredPanel.add(systemPanel, Integer.valueOf(1)); 
        layeredPanel.add(metricsPanel, Integer.valueOf(2)); 
        
        mainPanel.add(layeredPanel, BorderLayout.CENTER);
        frame.getContentPane().add(mainPanel);

        resizer.registerComponent(mainPanel);
        resizer.registerComponent(layeredPanel);
        resizer.registerComponent(console);
        resizer.registerComponent(scrollPanel.getViewport());
        resizer.registerComponent(scrollPanel.getVerticalScrollBar());

        frame.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                 topPanel.onWindowStateChanged(); 
                boolean wasMaximized = (e.getOldState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
                boolean isNowNormal = e.getNewState() == Frame.NORMAL;
                
                if (wasMaximized && isNowNormal) {
                      if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == 0) {
                        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT); 
                        frame.setLocationRelativeTo(null); 
                      }
                }
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ev) {
                int w = layeredPanel.getWidth();
                int h = layeredPanel.getHeight();
                
                scrollPanel.setBounds(0, 0, w, h); 
                
                int margin = 15; 
                int sysW = systemPanel.getPreferredSize().width;
                int sysH = systemPanel.getPreferredSize().height;
                int xPos = w - sysW - margin; 
                int yPos = margin; 
                systemPanel.setBounds(xPos, yPos, sysW, sysH);
                
                int metW = metricsPanel.getPreferredSize().width;
                int metH = metricsPanel.getPreferredSize().height;
                int metY = yPos + sysH + margin; 
                metricsPanel.setBounds(xPos, metY, metW, metH);
            }
        });

        frame.setIconImages(GuiUtils.loadIcons()); 
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setLocationRelativeTo(null);

        redirectSystemStreams();
        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> frame.dispatchEvent(new ComponentEvent(frame, ComponentEvent.COMPONENT_RESIZED)));
    }
    
    private JMenuBar createMenuBar() { 
        final JMenuBar menuBar = new JMenuBar(); 
        
        menuBar.setBackground(ModernUI.BG_DARK);
        menuBar.setBorder(BorderFactory.createEmptyBorder());
        
        final JMenu mnActions = createMenu("Game", 13); 
        mnActions.add(createMenuItem("Shutdown", 13, e -> ServerActions.shutdown(frame))); 
        mnActions.add(createMenuItem("Restart", 13, e -> ServerActions.restart(frame))); 
        mnActions.add(createMenuItem("Abort", 13, e -> ServerActions.abort(frame))); 
        
        final JMenu mnAdmin = createMenu("Administrator", 13); 
        mnAdmin.add(createMenuItem("Change Access", 13, e -> PlayerActions.changeAccess(frame))); 
        mnAdmin.add(createMenuItem("Search Item", 13, e -> new SearchItemFrame().setVisible(true))); 
        
        final JMenu mnCharacters = createMenu("Players", 13); 
        mnCharacters.add(createMenuItem("Repair Player", 13, e -> PlayerActions.repairPlayer(frame))); 
        mnCharacters.add(createMenuItem("Create Item", 13, e -> { String playerName = JOptionPane.showInputDialog(frame, "Enter player name:", "Create Item", JOptionPane.QUESTION_MESSAGE); if (playerName != null && !playerName.isEmpty()) { new CreateItemDialog(frame, playerName).setVisible(true); } })); 
        
        final JMenu mnBalance = createMenu("Balance", 13); 
        mnBalance.add(createMenuItem("Damage", 13, e -> new DamageBalanceFrame().setVisible(true))); 
        mnBalance.add(createMenuItem("Defence", 13, e -> new DefenceBalanceFrame().setVisible(true))); 
        mnBalance.add(createMenuItem("Vulnerability", 13, e -> new VulnerabilityBalanceFrame().setVisible(true))); 
        
        final JMenu mnAnnounce = createMenu("Announce", 13); 
        mnAnnounce.add(createMenuItem("Normal", 13, e -> ServerActions.normalAnnounce(frame))); 
        mnAnnounce.add(createMenuItem("Critical", 13, e -> ServerActions.criticalAnnounce(frame))); 
        
        final JMenu mnTools = createMenu("Tools", 13); 
        mnTools.add(createMenuItem("Multisell Creator", 13, e -> new MultisellBuilderDialog(frame).setVisible(true))); 
        mnTools.add(createMenuItem("Item Creator", 13, e -> abrirItemsBuilder())); 
        
        final JMenu mnReload = createMenu("Reload", 13); 
        mnReload.add(createMenuItem("Access", 13, e -> ReloadActions.reloadAccess(frame))); 
        mnReload.add(createMenuItem("Buylists", 13, e -> ReloadActions.reloadBuylists(frame))); 
        mnReload.add(createMenuItem("Configs", 13, e -> ReloadActions.reloadConfigs(frame))); 
        mnReload.add(createMenuItem("Farm Event", 13, e -> ReloadActions.reloadFarmEvent(frame))); 
        mnReload.add(createMenuItem("Items", 13, e -> ReloadActions.reloadItems(frame))); 
        mnReload.add(createMenuItem("Multisells", 13, e -> ReloadActions.reloadMultisells(frame))); 
        mnReload.add(createMenuItem("Skills", 13, e -> ReloadActions.reloadSkills(frame))); 
        mnReload.add(createMenuItem("Zones", 13, e -> ReloadActions.reloadZones(frame))); 
        
        final JMenu mnFont = createMenu("Font", 13); 
        final String[] fonts = {"10", "13", "16", "21", "27", "33"}; 
        for (String font : fonts) { 
            mnFont.add(createMenuItem(font, 13, e -> console.setFont(new Font("Monospaced", Font.PLAIN, Integer.parseInt(font))))); 
        } 
        
        final JMenu mnHelp = createMenu("Help", 13); 
        mnHelp.add(createMenuItem("About", 13, e -> new InterfaceAbout())); 
        
        menuBar.add(mnActions); 
        menuBar.add(mnAdmin); 
        menuBar.add(mnCharacters); 
        menuBar.add(mnBalance); 
        menuBar.add(mnAnnounce); 
        menuBar.add(mnTools); 
        menuBar.add(mnReload); 
        menuBar.add(mnFont); 
        menuBar.add(mnHelp); 
        return menuBar; 
    }
    
    private JMenu createMenu(String title, int fontSize) { 
        JMenu menu = new JMenu(title); 
        menu.setFont(new Font("Segoe UI", Font.PLAIN, fontSize)); 
        menu.setForeground(ThemeManager.TEXT_COLOR);
        return menu; 
    }
    
    private JMenuItem createMenuItem(String title, int fontSize, java.awt.event.ActionListener listener) { 
        JMenuItem item = new JMenuItem(title); 
        item.setFont(new Font("Segoe UI", Font.PLAIN, fontSize)); 
        
        item.setBackground(ThemeManager.VERY_DARK_BACKGROUND);
        item.setForeground(ThemeManager.TEXT_SELECTED);
        
        item.addActionListener(listener); 
        return item; 
    }
    
    private void redirectSystemStreams() { 
        OutputStream out = new OutputStream() { 
            @Override public void write(int b) { updateConsole(String.valueOf((char) b)); } 
            @Override public void write(byte[] b, int off, int len) { updateConsole(new String(b, off, len)); } 
            @Override public void write(byte[] b) { write(b, 0, b.length); } 
        }; 
        System.setOut(new PrintStream(out, true)); 
        System.setErr(new PrintStream(out, true)); 
    }
    
    private void updateConsole(String text) { 
        SwingUtilities.invokeLater(() -> { 
            console.append(text); 
            try { console.setCaretPosition(console.getDocument().getLength()); } catch (Exception e) {}
        }); 
    }

    private void abrirItemsBuilder() { 
        JFrame itemFrame = new JFrame("Item Generator"); 
        itemFrame.setIconImages(GuiUtils.loadIcons()); 
        itemFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); 
        itemFrame.setSize(1000, 650); 
        itemFrame.setLocationRelativeTo(frame); 
        javax.swing.JTabbedPane tabbedPane = new javax.swing.JTabbedPane(); 
        tabbedPane.addTab("Armor", new ArmorsXMLGenerator()); 
        tabbedPane.addTab("Weapon", new ArmorsXMLGenerator()); 
        tabbedPane.addTab("EtcItem", new ArmorsXMLGenerator()); 
        itemFrame.add(tabbedPane); 
        itemFrame.setVisible(true); 
    }
}