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
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import ext.mods.commons.gui.ConfigGS;
import ext.mods.commons.gui.ModernUI;
import ext.mods.commons.gui.ModernUI.*; 
import ext.mods.commons.gui.CustomToggleSwitch;
import ext.mods.commons.gui.services.ProcessManagerService;
import ext.mods.commons.services.IRamAllocationService;
import ext.mods.security.services.DatabaseManager;
import ext.mods.security.services.IRatesManager;

public class DashboardPanel {

    private final JFrame parentFrame;
    private final DatabaseManager databaseManager;
    private final ProcessManagerService processManagerService;
    private final MainFrame mainFrame;
    private final IRamAllocationService ramAllocationService;
    private final IRatesManager ratesManager;

    private JPanel dashboardPanel;
    
    private NeonProgressBar ramProgressBar;
    private CustomToggleSwitch tglLightMode;
    
    private JSlider gsSlider;
    private JLabel gsMaxLabel;
    private JSlider lsSlider;
    private JLabel lsMaxLabel;

    public DashboardPanel(JFrame parentFrame, DatabaseManager databaseManager, ProcessManagerService processManagerService, MainFrame mainFrame, IRamAllocationService ramAllocationService, IRatesManager ratesManager) {
        this.parentFrame = parentFrame;
        this.databaseManager = databaseManager;
        this.processManagerService = processManagerService;
        this.mainFrame = mainFrame;
        this.ramAllocationService = ramAllocationService;
        this.ratesManager = ratesManager;
        
        createPanel();
        startRamMonitoring();
    }

    public JPanel getPanel() { return dashboardPanel; }

    private void createPanel() {
        dashboardPanel = new JPanel(new GridBagLayout());
        dashboardPanel.setBackground(ModernUI.BG_DARK);
        dashboardPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    public void updateLayout(String key) {
        dashboardPanel.removeAll();
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.fill = GridBagConstraints.BOTH;
        gbcMain.weighty = 1.0;

        gbcMain.gridx = 0; 
        gbcMain.weightx = 0.6;
        gbcMain.insets = new Insets(0, 0, 0, 10);
        SectionPanel leftPanel = new SectionPanel("Server Configurations");
        leftPanel.add(createLeftContent(), BorderLayout.CENTER);
        leftPanel.add(createLeftButtons(), BorderLayout.SOUTH);
        dashboardPanel.add(leftPanel, gbcMain);

        gbcMain.gridx = 1;
        gbcMain.weightx = 0.4;
        gbcMain.insets = new Insets(0, 0, 0, 0);
        SectionPanel rightPanel = new SectionPanel("Server Status & Resources");
        
        JPanel rightCenter = new JPanel(new GridBagLayout());
        rightCenter.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 2, 2, 2);
        
        gbc.gridx = 0; gbc.gridy = 0;
        ramProgressBar = new NeonProgressBar();
        ramProgressBar.update(ramAllocationService.getTotalPhysicalMemoryGB(), ramAllocationService.getAvailablePhysicalMemoryGB());
        rightCenter.add(ramProgressBar, gbc);

        gbc.gridy++;
        rightCenter.add(Box.createVerticalStrut(8), gbc);

        gbc.gridy++;
        int maxMemAvailable = ramAllocationService.getAvailablePhysicalMemoryGB() * 1024;
        int sliderMaxGs = Math.max(2048, maxMemAvailable); 
        
        rightCenter.add(createPrecisionSliderBlock("Game Server RAM (GB)", true,
                ramAllocationService.getGsMemoryMB(), 2048, sliderMaxGs, 512,
                v -> ramAllocationService.setGsMemoryMB(v)), gbc);

        gbc.gridy++;
        rightCenter.add(Box.createVerticalStrut(5), gbc);

        gbc.gridy++;
        int sliderMaxLs = Math.min(2048, maxMemAvailable);
        
        if (sliderMaxLs < 128) sliderMaxLs = 128;

        rightCenter.add(createPrecisionSliderBlock("Login Server RAM (MB)", false,
                ramAllocationService.getLsMemoryMB(), 128, sliderMaxLs, 128,
                v -> ramAllocationService.setLsMemoryMB(v)), gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 2, 0, 2); 
        JPanel toggleContainer = new JPanel(new GridBagLayout());
        toggleContainer.setOpaque(false);
        
        tglLightMode = new CustomToggleSwitch(databaseManager.isLightModeEnabled());
        tglLightMode.addActionListener(e -> databaseManager.setLightMode(tglLightMode.isOn(), parentFrame));
        addToggleRow(toggleContainer, "Light Mode", tglLightMode, 0);
        
        CustomToggleSwitch tglDev = new CustomToggleSwitch(databaseManager.isDeveloperModeEnabled());
        tglDev.addActionListener(e -> databaseManager.setDeveloperMode(tglDev.isOn(), parentFrame));
        addToggleRow(toggleContainer, "Developer Mode", tglDev, 1);
        
        rightCenter.add(toggleContainer, gbc);
        
        gbc.gridy++;
        gbc.weighty = 1.0; 
        rightCenter.add(new JPanel() {{ setOpaque(false); }}, gbc);

        rightPanel.add(rightCenter, BorderLayout.CENTER);
        rightPanel.add(createRightButtons(key), BorderLayout.SOUTH);

        dashboardPanel.add(rightPanel, gbcMain);
        
        dashboardPanel.revalidate();
        dashboardPanel.repaint();
    }

    private void startRamMonitoring() {
        ramAllocationService.startMemoryMonitoring(() -> {
            SwingUtilities.invokeLater(() -> {
                int totalGB = ramAllocationService.getTotalPhysicalMemoryGB();
                int freeGB = ramAllocationService.getAvailablePhysicalMemoryGB();
                int freeMB = freeGB * 1024;

                if (ramProgressBar != null) {
                    ramProgressBar.update(totalGB, freeGB);
                }

                if (gsSlider != null && gsMaxLabel != null) {
                    int newMax = Math.max(2048, freeMB); 
                    
                    if (gsSlider.getMaximum() != newMax) {
                        gsSlider.setMaximum(newMax);
                        gsMaxLabel.setText(format(newMax, true));
                        
                    }
                }
                
                if (lsSlider != null && lsMaxLabel != null) {
                    int newMax = Math.min(2048, freeMB);
                    if (newMax < 128) newMax = 128; 

                    if (lsSlider.getMaximum() != newMax) {
                        lsSlider.setMaximum(newMax);
                        lsMaxLabel.setText(format(newMax, false));
                    }
                }
            });
        });
    }

    private JPanel createLeftContent() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        JPanel infoBlock = new JPanel(new GridLayout(2, 1, 0, 5));
        infoBlock.setOpaque(false);
        infoBlock.setBorder(new EmptyBorder(0, 0, 15, 0)); 

        infoBlock.add(createInfoRow(0, "Database:", DatabaseManager.getInstance().loadDatabaseConfig().getProperty("dbName", "l2jdb"), ModernUI.NEON_PURPLE));
        infoBlock.add(createInfoRow(1, "IP:", getIpDisplay(), ModernUI.NEON_BLUE));
        
        infoBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(infoBlock);

        JPanel ratesList = new JPanel();
        ratesList.setLayout(new BoxLayout(ratesList, BoxLayout.Y_AXIS));
        ratesList.setOpaque(false);
        
        Map<String, Double> rates = ratesManager.getAllRates();
        int count = 0;
        DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(new Dimension(800, 24));
            row.setBackground((count % 2 == 0) ? new Color(255, 255, 255, 10) : new Color(0,0,0,0));
            row.setOpaque(true);
            row.setBorder(new EmptyBorder(2, 5, 2, 5));

            JLabel lblName = new JLabel(entry.getKey());
            lblName.setForeground(ModernUI.TEXT_WHITE);
            lblName.setFont(new Font("Segoe UI", Font.PLAIN, 12));

            JLabel lblVal = new JLabel(df.format(entry.getValue()) + "x");
            lblVal.setForeground(ModernUI.NEON_CYAN);
            lblVal.setFont(new Font("Segoe UI", Font.BOLD, 12));

            row.add(lblName, BorderLayout.WEST);
            row.add(lblVal, BorderLayout.EAST);
            ratesList.add(row);
            count++;
        }
        
        JScrollPane scroll = new JScrollPane(ratesList);
        scroll.setOpaque(false); 
        scroll.getViewport().setOpaque(false); 
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        scroll.getVerticalScrollBar().setUI(new ModernUI.ModernScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(5, 0));
        
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        container.add(scroll);
        return container;
    }

    private JPanel createInfoRow(int iconType, String label, String val, Color valColor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
        p.setOpaque(false);
        
        JLabel iconLabel = new JLabel(new VectorIcon(iconType, 18, valColor));
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 8)); 
        p.add(iconLabel);
        
        JLabel l = new JLabel(label); 
        l.setForeground(ModernUI.TEXT_WHITE); 
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(l);
        
        JLabel v = new JLabel("  " + val);
        v.setForeground(valColor); 
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(v);
        
        return p;
    }

    private JPanel createLeftButtons() {
        JPanel p = new JPanel(new GridLayout(1, 3, 10, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));
        NeonButton b1 = new NeonButton("Database", ModernUI.NEON_BLUE, ModernUI.NEON_PURPLE, true, null);
        NeonButton b2 = new NeonButton("Edit Rates", ModernUI.NEON_BLUE, ModernUI.NEON_PURPLE, true, null);
        NeonButton b3 = new NeonButton("Config GS", ModernUI.NEON_BLUE, ModernUI.NEON_PURPLE, true, null);
        
        b1.addActionListener(e -> databaseManager.configurarBanco(parentFrame));
        b2.addActionListener(e -> openRatesFile());
        b3.addActionListener(e -> { new ConfigGS(parentFrame).showWindow(); });
        p.add(b1); p.add(b2); p.add(b3);
        return p;
    }


    private JPanel createPrecisionSliderBlock(String title, boolean isGB, int initial, int min, int max, int step, Consumer<Integer> onSlide) {
        JPanel block = new JPanel(new GridBagLayout());
        block.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 2, 0);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(ModernUI.TEXT_WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        block.add(lblTitle, gbc);

        
        if (max < min) {
            max = min;
        }

        if (initial < min) {
            initial = min;
        }
        if (initial > max) {
            initial = max;
        }
        
        if (initial != ramAllocationService.getGsMemoryMB() && isGB) {
        }

        gbc.gridy = 1;
        JSlider slider = new JSlider(min, max, initial);
        slider.setOpaque(false);
        slider.setUI(new NeonSliderUI(slider));
        slider.setPaintTicks(false); 
        slider.setMajorTickSpacing(step);
        block.add(slider, gbc);
        
        if (isGB) gsSlider = slider; else lsSlider = slider;

        gbc.gridy = 2;
        JPanel labels = new JPanel(new BorderLayout());
        labels.setOpaque(false);
        
        JLabel lblMin = new JLabel(format(min, isGB)); 
        lblMin.setForeground(ModernUI.TEXT_GRAY); lblMin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel lblMax = new JLabel(format(max, isGB)); 
        lblMax.setForeground(ModernUI.TEXT_GRAY); lblMax.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        if (isGB) gsMaxLabel = lblMax; else lsMaxLabel = lblMax;
        
        JLabel lblVal = new JLabel();
        lblVal.setForeground(ModernUI.NEON_PURPLE); 
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateLabel(lblVal, initial, isGB);

        slider.addChangeListener(e -> {
            updateLabel(lblVal, slider.getValue(), isGB);
            onSlide.accept(slider.getValue());
        });

        labels.add(lblMin, BorderLayout.WEST);
        
        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightInfo.setOpaque(false);
        rightInfo.add(lblMax);
        rightInfo.add(lblVal);
        
        labels.add(rightInfo, BorderLayout.EAST);
        block.add(labels, gbc);
        return block;
    }

    private void addToggleRow(JPanel container, String label, CustomToggleSwitch toggle, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 0, 2, 0);
        
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.WEST;
        JLabel l = new JLabel(label);
        l.setForeground(ModernUI.TEXT_WHITE);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        container.add(l, gbc);

        gbc.gridx = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.EAST;
        container.add(toggle, gbc);
    }

    private JPanel createRightButtons(String key) {
        JPanel p = new JPanel(new GridLayout(1, 2, 15, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(5, 0, 0, 0));

        NeonButton bGame = new NeonButton("  Start Game", ModernUI.NEON_BLUE, ModernUI.NEON_PURPLE, false, null);
        bGame.setText("Start Game");
        
        NeonButton bAuth = new NeonButton("  Start Auth", ModernUI.NEON_BLUE, ModernUI.NEON_PURPLE, false, null);
        bAuth.setText("Start Auth");

        bGame.addActionListener(e -> {
            LauncherApp.setKey(key);
            processManagerService.iniciarProcesso("gameserver", key, LauncherApp.getLoggedUserEmail(), databaseManager.isLightModeEnabled(), parentFrame);
        });
        bAuth.addActionListener(e -> {
            LauncherApp.setKey(key);
            processManagerService.iniciarProcesso("loginserver", key, LauncherApp.getLoggedUserEmail(), false, parentFrame);
        });
        p.add(bGame); p.add(bAuth);
        return p;
    }

    private void updateLabel(JLabel l, int val, boolean isGB) {
        if(isGB) l.setText(String.format("xmx: %.2f GB", val/1024.0));
        else l.setText("xmx: " + val + " MB");
    }
    private String format(int val, boolean isGB) { return isGB ? (val/1024)+"G" : val+"M"; }
    private String getIpDisplay() { String h = databaseManager.getServerHostname(); return "*".equals(h) ? "127.0.0.1" : h; }
    private void openRatesFile() { try { File f = new File("game/config/rates.properties"); if(f.exists() && Desktop.isDesktopSupported()) Desktop.getDesktop().open(f); } catch(IOException e){} }
}