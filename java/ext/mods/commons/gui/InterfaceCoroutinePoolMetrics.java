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
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import ext.mods.commons.pool.ConnectionPool;
import ext.mods.commons.pool.CoroutinePool;

public class InterfaceCoroutinePoolMetrics extends ModernUI.SectionPanel {

    private static final long serialVersionUID = 1L;
    private static final DecimalFormat DEC = new DecimalFormat("#,##0");
    
    private final MetricBar barTask, barLatency, barSched, barInst, barPath, barPathTasks, barHikari;
    private final Timer updateTimer;
    private long lastTotalTasks = -1;

    public InterfaceCoroutinePoolMetrics() {
        super("Thread Pool Metrics");
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(5));

        barTask    = addMetric(ModernUI.NEON_PURPLE, new Color(255, 100, 200));
        barLatency = addMetric(ModernUI.NEON_PURPLE, new Color(255, 100, 200));
        
        barSched   = addMetric(ModernUI.NEON_BLUE, ModernUI.NEON_CYAN);
        barInst    = addMetric(ModernUI.NEON_BLUE, ModernUI.NEON_CYAN);
        barPath    = addMetric(new Color(80, 80, 255), new Color(120, 120, 255));
        barPathTasks = addMetric(new Color(60, 120, 180), new Color(100, 160, 220));
        barHikari = addMetric(new Color(0, 150, 100), new Color(50, 200, 150));

        updateTimer = new Timer("ThreadPoolMetrics-Updater", true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() { update(); }
        }, 1000, 1000);
    }

    private MetricBar addMetric(Color c1, Color c2) {
        MetricBar bar = new MetricBar(c1, c2);
        add(bar);
        add(Box.createVerticalStrut(4));
        return bar;
    }

    private void update() {
        SwingUtilities.invokeLater(() -> {
            try {
                Map<String, Object> m = CoroutinePool.getMetrics();
                if (m == null || m.isEmpty()) return;

                long currentTotalTasks = getLong(m, "tasksSubmitted");
                long tps = 0;
                if (lastTotalTasks != -1) {
                    tps = currentTotalTasks - lastTotalTasks;
                    if (tps < 0) tps = 0;
                }
                lastTotalTasks = currentTotalTasks;
                
                barTask.set("Tasks:", DEC.format(currentTotalTasks) + " (TPS: " + DEC.format(tps) + ")", 1000, (int)tps);

                double lat = getDouble(m, "averageLatency");
                barLatency.set("Latency:", String.format("%.2fms", lat), 50, (int)lat);

                int schedPools = getInt(m, "scheduledPools");
                int schedTasks = getInt(m, "scheduledQueueSize"); 
                barSched.set("Scheduled:", schedTasks + " waiting (" + schedPools + " pools)", Math.max(schedTasks + 10, 50), schedTasks);

                int instP = getInt(m, "instantPoolsSize");   
                int instA = getInt(m, "instantPoolsActive"); 
                barInst.set("Instant:", instA + " / " + instP + " active", Math.max(instP, 1), instA);

                int pathA = getInt(m, "pathfindingActive");
                int pathS = getInt(m, "pathfindingSize");
                barPath.set("Pathfinding:", pathA + " / " + pathS + " active", Math.max(pathS, 1), pathA);

                long pfSubmitted = getLong(m, "pathfindingTasksSubmitted");
                long pfCompleted = getLong(m, "pathfindingTasksCompleted");
                barPathTasks.set("Pathfinder:", pfCompleted + " / " + pfSubmitted + " completed", (int) Math.max(pfSubmitted, 1), (int) Math.min(pfCompleted, Integer.MAX_VALUE));

                long hikariQueries = ConnectionPool.getTotalQueries();
                int barMax = 50_000;
                barHikari.set("HikariCP:", DEC.format(hikariQueries) + " processadas", barMax, (int) Math.min(hikariQueries, barMax));

            } catch (Exception e) {}
        });
    }

    private int getInt(Map<String,Object> m, String k) { Object o = m.get(k); return (o instanceof Number) ? ((Number)o).intValue() : 0; }
    private long getLong(Map<String,Object> m, String k) { Object o = m.get(k); return (o instanceof Number) ? ((Number)o).longValue() : 0L; }
    private double getDouble(Map<String,Object> m, String k) { Object o = m.get(k); return (o instanceof Number) ? ((Number)o).doubleValue() : 0.0; }

    private static class MetricBar extends JComponent {
        private final Color c1, c2;
        private String label = "-", valueStr = "-";
        private int max = 100, current = 0;

        public MetricBar(Color c1, Color c2) {
            this.c1 = c1; this.c2 = c2;
            setPreferredSize(new Dimension(200, 20)); 
            setMaximumSize(new Dimension(320, 20));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        public void set(String label, String val, int max, int curr) {
            this.label = label; this.valueStr = val;
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
                int fillW = (int) (((double)current / max) * w);
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