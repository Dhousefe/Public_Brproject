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

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import ext.mods.gameserver.model.actor.move.MovementConfig;

public class InterfaceMovementTuner extends JFrame {

    private final CustomTopPanel topPanel;
    private final JTextArea logArea;
    private Timer logTimer;

    public static void open() {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("[MovementTuner] Cannot open GUI in Headless environment (No Graphics).");
            return;
        }
        SwingUtilities.invokeLater(() -> new InterfaceMovementTuner().setVisible(true));
    }

    public InterfaceMovementTuner() {
        setUndecorated(true);
        setTitle("Monsters Movement Tuner v2.1 (Zero GC Optimized)");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setBackground(ModernUI.BG_DARK);

        Runnable closeAction = () -> {
            MovementConfig.DEBUG_ENABLED = false;
            if (logTimer != null) logTimer.cancel();
            dispose();
        };
        topPanel = new CustomTopPanel(this, null, closeAction, false, "images/16x16.png");
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBackground(ModernUI.BG_DARK);
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        controlsPanel.setOpaque(false);

        ModernUI.SectionPanel pnlAI = new ModernUI.SectionPanel("AI & Reaction Time");
        pnlAI.setLayout(new BoxLayout(pnlAI, BoxLayout.Y_AXIS));
        
        pnlAI.add(createSlider("Reaction Delay (ms)", 100, 1000, (int)MovementConfig.ATTACK_FOLLOW_INTERVAL, (double)MovementConfig.DEFAULT_ATTACK_FOLLOW_INTERVAL, val -> MovementConfig.ATTACK_FOLLOW_INTERVAL = val));
        pnlAI.add(Box.createVerticalStrut(5));
        pnlAI.add(createSlider("Speed Variation (%)", 0, 50, (int)(MovementConfig.RANDOM_SPEED_VARIATION * 100), MovementConfig.DEFAULT_RANDOM_SPEED_VARIATION, val -> MovementConfig.RANDOM_SPEED_VARIATION = val / 100.0));
        
        controlsPanel.add(pnlAI);
        controlsPanel.add(Box.createVerticalStrut(10));

        ModernUI.SectionPanel pnlZigZag = new ModernUI.SectionPanel("Zig-Zag Geometry");
        pnlZigZag.setLayout(new BoxLayout(pnlZigZag, BoxLayout.Y_AXIS));
        
        pnlZigZag.add(createSlider("Layer Offset (Dist)", 0, 150, MovementConfig.ZIGZAG_LAYER_OFFSET, (double)MovementConfig.DEFAULT_ZIGZAG_LAYER_OFFSET, val -> MovementConfig.ZIGZAG_LAYER_OFFSET = val));
        
        controlsPanel.add(pnlZigZag);
        controlsPanel.add(Box.createVerticalStrut(10));

        ModernUI.SectionPanel pnlPhysics = new ModernUI.SectionPanel("Physics & Collision");
        pnlPhysics.setLayout(new BoxLayout(pnlPhysics, BoxLayout.Y_AXIS));
        
        pnlPhysics.add(createSlider("Separation Weight", 0, 500, (int)(MovementConfig.SEPARATION_WEIGHT * 100), MovementConfig.DEFAULT_SEPARATION_WEIGHT, val -> MovementConfig.SEPARATION_WEIGHT = val / 100.0));
        
        controlsPanel.add(pnlPhysics);

        JCheckBox chkDebug = new JCheckBox("Enable Telemetry Logs");
        chkDebug.setForeground(ModernUI.TEXT_WHITE);
        chkDebug.setOpaque(false);
        chkDebug.setSelected(MovementConfig.DEBUG_ENABLED);
        chkDebug.addActionListener(e -> MovementConfig.DEBUG_ENABLED = chkDebug.isSelected());
        
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(chkDebug);

        centerPanel.add(controlsPanel);

        try {
            centerPanel.add(new RadarPanel());
        } catch (NoClassDefFoundError | Exception e) {
            JPanel placeholder = new JPanel();
            placeholder.setOpaque(false);
            placeholder.setBorder(BorderFactory.createTitledBorder("Radar (Not Found)"));
            centerPanel.add(placeholder);
        }

        add(centerPanel, BorderLayout.CENTER);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        logArea.setBackground(new Color(10, 10, 15));
        logArea.setForeground(ModernUI.NEON_PURPLE); 
        
        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setPreferredSize(new Dimension(0, 120));
        scrollLog.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ModernUI.NEON_PURPLE));
        
        DefaultCaret caret = (DefaultCaret)logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        add(scrollLog, BorderLayout.SOUTH);

        logTimer = new Timer("Telemetry-Reader", true);
        logTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!MovementConfig.DEBUG_ENABLED && chkDebug.isSelected()) {
                    SwingUtilities.invokeLater(() -> chkDebug.setSelected(false));
                }

                String msg = MovementConfig.pollLog();
                if (msg != null) {
                    SwingUtilities.invokeLater(() -> {
                        logArea.append(msg + "\n");
                        if (logArea.getText().length() > 20000) logArea.setText("");
                    });
                }
            }
        }, 0, 100);
    }

    private JPanel createSlider(String title, int min, int max, int initial, double defaultValue, java.util.function.Consumer<Integer> onChange) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String defStr = (title.contains("Weight") || title.contains("Variation") || title.contains("Factor")) 
                        ? String.format("%.2f", defaultValue) 
                        : String.valueOf((int)defaultValue);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(ModernUI.TEXT_GRAY);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel lblValue = new JLabel();
        lblValue.setForeground(ModernUI.NEON_CYAN);
        lblValue.setFont(new Font("Consolas", Font.BOLD, 11));
        lblValue.setPreferredSize(new Dimension(120, 15));
        lblValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblValue, BorderLayout.EAST);

        JSlider slider = new JSlider(min, max, initial);
        slider.setOpaque(false);
        slider.setUI(new ModernUI.NeonSliderUI(slider));
        
        slider.addChangeListener(e -> {
            int val = slider.getValue();
            String currentStr;
            if (title.contains("Weight") || title.contains("Variation") || title.contains("Factor")) {
                currentStr = String.format("%.2f", val / 100.0);
            } else {
                currentStr = String.valueOf(val);
            }
            lblValue.setText(currentStr + " (Def: " + defStr + ")");
            onChange.accept(val);
        });

        if (slider.getChangeListeners().length > 0) {
             slider.getChangeListeners()[0].stateChanged(null);
        }

        panel.add(top, BorderLayout.NORTH);
        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }
}