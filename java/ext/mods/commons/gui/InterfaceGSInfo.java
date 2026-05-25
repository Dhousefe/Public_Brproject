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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ext.mods.gameserver.model.World;

public class InterfaceGSInfo extends ModernUI.SectionPanel {

    private static final long serialVersionUID = 1L;
    protected static final Logger LOGGER = Logger.getLogger(InterfaceGSInfo.class.getName());
    protected static final long START_TIME = System.currentTimeMillis();

    private final InfoRow rowVersion;
    private final InfoRow rowJava;
    private final InfoRow rowProtocol;
    private final InfoRow rowPlayers;
    private final InfoRow rowUptime;
    private final InfoRow rowOffline;
    private final MetricBar barMemory;

    public InterfaceGSInfo() {
        super("General Status");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(5));

        rowVersion  = addInfoRow("Version", "2.9.6");
        rowJava     = addInfoRow("Build JDK", System.getProperty("java.version"));
        rowProtocol = addInfoRow("Hwid Protocol", "Loading...");
        rowPlayers  = addInfoRow("Online Players", "0");
        rowUptime   = addInfoRow("Elapsed", "00:00:00");
        rowOffline  = addInfoRow("Offline Trade", "0");

        add(Box.createVerticalStrut(12));

        barMemory = new MetricBar(ModernUI.NEON_GREEN, ModernUI.NEON_CYAN);
        add(barMemory);

        add(Box.createVerticalGlue());

        startUpdateTask();
    }

    private InfoRow addInfoRow(String label, String value) {
        InfoRow row = new InfoRow(label, value);
        add(row);
        add(Box.createVerticalStrut(4));
        return row;
    }

    private void startUpdateTask() {
        new Timer("GSInfo-Protocol", true).schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> rowProtocol.setValue("730 | 746"));
            }
        }, 4500);

        new Timer("GSInfo-Updater", true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    int playerCount = World.getInstance().getPlayers().size();
                    if (World.getInstance().MAX_CONNECTED_COUNT < playerCount) {
                        World.getInstance().MAX_CONNECTED_COUNT = playerCount;
                    }

                    int maxOnline = World.getInstance().MAX_CONNECTED_COUNT;
                    int offlineCount = World.getInstance().OFFLINE_TRADE_COUNT;

                    Runtime rt = Runtime.getRuntime();
                    long totalMemory = rt.totalMemory();
                    long usedMemory = totalMemory - rt.freeMemory();

                    String uptimeStr = getDurationBreakdown(System.currentTimeMillis() - START_TIME);

                    SwingUtilities.invokeLater(() -> {
                        rowUptime.setValue(uptimeStr);
                        rowOffline.setValue(String.valueOf(offlineCount));
                        rowPlayers.setValue(playerCount + " / " + maxOnline + " (Max)");

                        barMemory.set(
                                "Memory",
                                formatMemorySize(usedMemory) + " / " + formatMemorySize(totalMemory),
                                (int) (totalMemory / 1024 / 1024),
                                (int) (usedMemory / 1024 / 1024)
                        );
                    });
                } catch (Exception ignored) {}
            }
        }, 1000, 1000);
    }

    static String formatMemorySize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        double size = bytes / Math.pow(1024, exp);
        return String.format(exp == 1 ? "%.0f %sB" : "%.2f %sB", size, pre);
    }

    static String getDurationBreakdown(long millis) {
        long remaining = millis;
        long days = TimeUnit.MILLISECONDS.toDays(remaining);
        remaining -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(remaining);
        remaining -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(remaining);
        remaining -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining);
        return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }

    /* ================= INFO ROW ================= */

    private static class InfoRow extends JComponent {

        private final String label;
        private String value;

        public InfoRow(String label, String value) {
            this.label = label;
            this.value = value;

            setPreferredSize(new Dimension(200, 20));
            setMaximumSize(new Dimension(320, 20));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        public void setValue(String val) {
            if (!this.value.equals(val)) {
                this.value = val;
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            FontMetrics fm = g2.getFontMetrics();

            g2.setColor(ModernUI.TEXT_GRAY);
            g2.drawString(label, 0, 11);

            g2.setColor(ModernUI.TEXT_WHITE);
            g2.drawString(value, w - fm.stringWidth(value), 11);

            g2.dispose();
        }
    }

    /* ================= METRIC BAR ================= */

    private static class MetricBar extends JComponent {

        private final Color c1, c2;
        private String label = "-", valueStr = "-";
        private int max = 100, current = 0;

        public MetricBar(Color c1, Color c2) {
            this.c1 = c1;
            this.c2 = c2;

            setPreferredSize(new Dimension(200, 20));
            setMaximumSize(new Dimension(200, 20));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        public void set(String label, String val, int max, int curr) {
            this.label = label;
            this.valueStr = val;
            this.max = Math.max(max, 1);
            this.current = Math.min(Math.max(curr, 0), this.max);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int barH = 4;
            int barY = 15;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(ModernUI.TEXT_GRAY);
            g2.drawString(label, 0, 11);

            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(ModernUI.TEXT_WHITE);
            g2.drawString(valueStr, w - fm.stringWidth(valueStr), 11);

            g2.setColor(new Color(60, 60, 70, 100));
            g2.fillRoundRect(0, barY, w, barH, barH, barH);

            if (current > 0) {
                int fillW = (int) (((double) current / max) * w);
                if (fillW < barH && fillW > 0) fillW = barH;

                if (ThemeManager.isSafeGraphics()) {
                    g2.setColor(new Color((c1.getRed() + c2.getRed()) / 2, (c1.getGreen() + c2.getGreen()) / 2, (c1.getBlue() + c2.getBlue()) / 2));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, c1, fillW, 0, c2));
                }
                g2.fillRoundRect(0, barY, fillW, barH, barH, barH);

                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillOval(fillW - barH + 1, barY, barH - 1, barH - 1);
            }

            g2.dispose();
        }
    }
}