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
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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

import ext.mods.loginserver.LoginServer;
import ext.mods.loginserver.data.manager.GameServerManager;
import ext.mods.commons.logging.formatter.NoTimestampConsoleFormatter;

public class InterfaceLS {

    private JTextArea console;
    private static final String[] shutdownOptions = {"Shutdown", "Cancel"};
    private static final String[] restartOptions = {"Restart", "Cancel"};
    private JFrame frame;
    private CustomTopPanel topPanel;
    
    private JLayeredPane layeredPanel;
    private JScrollPane scrollPanel;
    private JPanel infoPanel;

    private static final int DEFAULT_WIDTH = 850;
    private static final int DEFAULT_HEIGHT = 517;

    public InterfaceLS() {
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

        frame = new JFrame("Auth");
        frame.setUndecorated(true);

        ComponentResizer resizer = new ComponentResizer(frame);

        JMenuBar menuBar = createMenuBar();

        Runnable closeAction = () -> {
            boolean hasConnectedServers = false;
            try {
                if (GameServerManager.getInstance() != null && GameServerManager.getInstance().getGameServersCount() > 0) 
                    hasConnectedServers = true;
            } catch (Exception ex) {
                hasConnectedServers = false;
            }
            if (hasConnectedServers) {
                if (JOptionPane.showOptionDialog(frame, "Shutdown LoginServer?", "GameServers are connected!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, shutdownOptions, shutdownOptions[1]) == 0) {
                    LoginServer.getInstance().shutdown(false);
                }
            }
            else {
                LoginServer.getInstance().shutdown(false);
            }
        };

        String iconPath = "images" + File.separator + "16x16.png";
        if (!new File(iconPath).exists()) {
            iconPath = ".." + File.separator + "images" + File.separator + "16x16.png";
        }

        topPanel = new CustomTopPanel(frame, menuBar, closeAction, true, iconPath);
        frame.add(topPanel, BorderLayout.NORTH);

        scrollPanel = new JScrollPane(console);
        scrollPanel.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND);
        scrollPanel.setBackground(ThemeManager.VERY_DARK_BACKGROUND);
        scrollPanel.setBorder(null);
        
        scrollPanel.getVerticalScrollBar().setUI(new ModernUI.ModernScrollBarUI());
        scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));

        infoPanel = new InterfaceLSInfo();

        layeredPanel = new JLayeredPane();
        layeredPanel.add(scrollPanel, Integer.valueOf(0));
        layeredPanel.add(infoPanel, Integer.valueOf(1));

        frame.add(layeredPanel, BorderLayout.CENTER);

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
                int infoW = infoPanel.getPreferredSize().width; 
                int infoH = infoPanel.getPreferredSize().height;
                
                int xPos = w - infoW - margin;
                int yPos = margin;
                
                infoPanel.setBounds(xPos, yPos, infoW, infoH);
                layeredPanel.moveToFront(infoPanel);
            }
        });

        frame.setIconImages(GuiUtils.loadIcons());
        frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        frame.setLocationRelativeTo(null);

        redirectSystemStreams(); 

        frame.setVisible(true);
        SwingUtilities.invokeLater(() -> frame.dispatchEvent(new ComponentEvent(frame, ComponentEvent.COMPONENT_RESIZED)));
    }

    private JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setForeground(ThemeManager.TEXT_COLOR);
        return menu;
    }

    private JMenuItem createMenuItem(String title, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(title);
        item.setBackground(ThemeManager.VERY_DARK_BACKGROUND);
        item.setForeground(ThemeManager.TEXT_SELECTED);
        if (listener != null) {
            item.addActionListener(listener);
        }
        return item;
    }

    private JMenuItem createFontMenuItem(String size) {
        JMenuItem item = new JMenuItem(size);
        item.setBackground(ThemeManager.VERY_DARK_BACKGROUND);
        item.setForeground(ThemeManager.TEXT_SELECTED);
        item.addActionListener(e -> console.setFont(new Font("Consolas", Font.PLAIN, Integer.parseInt(size))));
        return item;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        menuBar.setBackground(ModernUI.TEXT_GRAY);
        menuBar.setBorder(BorderFactory.createEmptyBorder());
        
        JMenu loginMenu = createMenu("Login");
        loginMenu.add(createMenuItem("Shutdown", e -> {
            if (JOptionPane.showOptionDialog(frame, "Shutdown LoginServer?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, shutdownOptions, shutdownOptions[1]) == 0) {
                LoginServer.getInstance().shutdown(false);
            }
        }));
        loginMenu.add(createMenuItem("Restart", e -> {
            if (JOptionPane.showOptionDialog(frame, "Restart LoginServer?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, restartOptions, restartOptions[1]) == 0) {
                LoginServer.getInstance().shutdown(true);
            }
        }));
        
        JMenu fontMenu = createMenu("Font");
        for (String size : new String[] {"10", "13", "16", "20", "24"}) {
            fontMenu.add(createFontMenuItem(size));
        }
        
        JMenu helpMenu = createMenu("Help");
        helpMenu.add(createMenuItem("About", e -> new InterfaceAbout()));
        
        menuBar.add(loginMenu);
        menuBar.add(fontMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void updateConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            console.append(text);
            try {
                console.setCaretPosition(console.getDocument().getLength());
            } catch (Exception e) {/* Ignora */}
        });
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override public void write(int b) { updateConsole(String.valueOf((char) b)); }
            @Override public void write(byte[] b, int off, int len) { updateConsole(new String(b, off, len)); }
            @Override public void write(byte[] b) { write(b, 0, b.length); }
        };
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
        
        installJavaUtilLoggingHandler();
    }
    
    private void installJavaUtilLoggingHandler() {
        Logger root = Logger.getLogger("");
        Handler handler = new Handler() {
            private final NoTimestampConsoleFormatter formatter = new NoTimestampConsoleFormatter();
            
            @Override
            public void publish(LogRecord record) {
                if (!isLoggable(record)) return;
                String message = formatter.format(record);
                if (message != null && !message.isEmpty()) {
                    updateConsole(message);
                }
            }
            
            @Override public void flush() {}
            @Override public void close() throws SecurityException {}
        };
        handler.setLevel(Level.ALL);
        root.addHandler(handler);
    }
}