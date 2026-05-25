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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class ModernUI {

    public static final Color BG_DARK      = new Color(10, 10, 15); 
    public static final Color BG_CONSOLE   = new Color(10, 10, 15); 
    public static final Color BG_PANEL     = new Color(22, 20, 28, 200); 
    
    
    public static final Color NEON_BLUE    = new Color(0, 150, 200); 
    
    public static final Color NEON_CYAN    = new Color(180, 140, 255); 

    public static final Color NEON_PURPLE  = new Color(100, 50, 180);
    public static final Color NEON_MAGENTA = new Color(80, 190, 220);

    public static final Color NEON_GREEN   = new Color(0, 200, 220);
    public static final Color TEXT_WHITE   = new Color(245, 245, 250);
    public static final Color TEXT_GRAY    = new Color(160, 150, 170); 

    public static class SectionPanel extends JPanel {
        private final String title;

        public SectionPanel(String title) {
            this.title = title;
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(30, 15, 10, 15)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int arc = 20;

            g2.setColor(BG_PANEL);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            if (ThemeManager.isSafeGraphics()) {
                g2.setColor(NEON_CYAN);
            } else {
                g2.setPaint(new GradientPaint(0, 0, NEON_GREEN, 0, h, NEON_PURPLE));
            }
            g2.setStroke(new BasicStroke(1.2f)); 
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

            if (title != null) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
                if (ThemeManager.isSafeGraphics()) {
                    g2.setColor(NEON_CYAN);
                } else {
                    g2.setPaint(new GradientPaint(0, 0, NEON_GREEN, w, 0, NEON_CYAN));
                }
                g2.drawString(title, 15, 20);
                
                g2.setColor(new Color(0, 200, 220, 80));
                g2.drawLine(15, 25, w - 15, 25);
            }
            g2.dispose();
        }
    }

    public static class ModernScrollBarUI extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            this.thumbColor = NEON_BLUE;
            this.trackColor = BG_DARK;
        }
        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
        private JButton createZeroButton() {
            JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0));
            return b;
        }
        @Override protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(new Color(15, 15, 25)); 
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
        @Override protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (ThemeManager.isSafeGraphics()) {
                g2.setColor(isDragging ? NEON_CYAN : isThumbRollover() ? NEON_PURPLE : NEON_BLUE.darker());
            } else if (isDragging) {
                g2.setPaint(new GradientPaint(thumbBounds.x, 0, NEON_GREEN, thumbBounds.x + thumbBounds.width, 0, NEON_CYAN));
            } else if (isThumbRollover()) {
                g2.setPaint(new GradientPaint(thumbBounds.x, 0, NEON_BLUE, thumbBounds.x + thumbBounds.width, 0, NEON_PURPLE));
            } else {
                g2.setColor(NEON_BLUE.darker());
            }

            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 5, 5);
            g2.dispose();
        }
    }

    public static class NeonButton extends JButton {
        private final Color c1, c2;
        private boolean isHovered = false;
        private final boolean isOutline;

        public NeonButton(String text) {
            this(text, NEON_GREEN, NEON_PURPLE, false, null);
        }

        public NeonButton(String text, Color c1, Color c2, boolean isOutline, Icon icon) {
            super(text);
            this.c1 = c1; this.c2 = c2; this.isOutline = isOutline;
            if (icon != null) setIcon(icon);
            setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false);
            setForeground(TEXT_WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            int arc = 12;

            if (isOutline) {
                if (ThemeManager.isSafeGraphics()) {
                    g2d.setColor(c1);
                } else {
                    g2d.setPaint(new GradientPaint(0, 0, c1, w, 0, c2));
                }
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
                if (isHovered) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                    g2d.fillRoundRect(1, 1, w - 3, h - 3, arc, arc);
                }
            } else {
                Color start = isHovered ? c1.brighter() : c1;
                Color end = isHovered ? c2.brighter() : c2;
                if (ThemeManager.isSafeGraphics()) {
                    g2d.setColor(new Color((start.getRed() + end.getRed()) / 2, (start.getGreen() + end.getGreen()) / 2, (start.getBlue() + end.getBlue()) / 2));
                } else {
                    g2d.setPaint(new GradientPaint(0, 0, start, w, 0, end));
                }
                g2d.fillRoundRect(0, 0, w, h, arc, arc);
                
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
            }
            super.paintComponent(g);
            g2d.dispose();
        }
    }

    public static class NeonProgressBar extends JComponent {
        private int max = 1; private int current = 0; private String textInfo = "Carregando...";
        public NeonProgressBar() { setPreferredSize(new Dimension(200, 45)); }
        public void update(int maxGB, int freeGB) { 
            this.max = maxGB; 
            this.current = maxGB - freeGB; 
            this.textInfo = "RAM Total: " + maxGB + " GB | Livre: " + freeGB + " GB"; 
            repaint(); 
        }
        
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12)); 
            g2d.setColor(TEXT_GRAY); 
            g2d.drawString(textInfo, 0, 15);
            
            int yBar = 25; int hBar = 5; int w = getWidth();
            g2d.setColor(new Color(30, 25, 35));
            g2d.fillRoundRect(0, yBar, w, hBar, hBar, hBar);
            
            if (max > 0) {
                int wFill = (int) ((double) current / max * w);
                if (ThemeManager.isSafeGraphics()) {
                    g2d.setColor(NEON_PURPLE);
                } else {
                    g2d.setPaint(new GradientPaint(0, 0, NEON_GREEN, wFill, 0, NEON_PURPLE));
                }
                g2d.fillRoundRect(0, yBar, wFill, hBar, hBar, hBar);
                
                g2d.setColor(Color.WHITE); 
                g2d.fillOval(Math.max(0, wFill - 5), yBar - 1, 7, 7);
                g2d.setColor(new Color(0, 200, 220, 120));
                g2d.fillOval(Math.max(0, wFill - 7), yBar - 3, 11, 11);
            }
            g2d.dispose();
        }
    }

    public static class NeonSliderUI extends BasicSliderUI {
        public NeonSliderUI(JSlider b) { super(b); }
        @Override public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Rectangle t = trackRect;
            g2.setColor(new Color(30, 25, 35));
            g2.fillRoundRect(t.x, t.y + (t.height-4)/2, t.width, 4, 4, 4);
            int fillW = thumbRect.x - t.x;
            if (fillW > 0) {
                if (ThemeManager.isSafeGraphics()) {
                    g2.setColor(NEON_PURPLE);
                } else {
                    g2.setPaint(new GradientPaint(t.x, 0, NEON_GREEN, t.x+fillW, 0, NEON_PURPLE));
                }
                g2.fillRoundRect(t.x, t.y + (t.height-4)/2, fillW, 4, 4, 4);
            }
        }
        @Override public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int x = thumbRect.x, y = thumbRect.y + (thumbRect.height-14)/2;
            if (ThemeManager.isSafeGraphics()) {
                g2.setColor(NEON_CYAN);
            } else {
                g2.setPaint(new GradientPaint(x, y, NEON_GREEN, x+14, y+14, NEON_CYAN));
            }
            g2.fillOval(x, y, 14, 14);
            g2.setColor(Color.WHITE); g2.fillOval(x+4, y+4, 6, 6); 
            g2.setColor(new Color(0, 180, 200, 80));
            g2.drawOval(x-2, y-2, 18, 18);
        }
        @Override protected Dimension getThumbSize() { return new Dimension(14, 14); }
    }
    
    public static class VectorIcon implements Icon {
        private final int type; private final int size; private final Color color;
        public VectorIcon(int type, int size, Color color) { this.type = type; this.size = size; this.color = color; }
        public int getIconWidth() { return size; } public int getIconHeight() { return size; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create(); g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.translate(x, y); g2d.setColor(color); g2d.setStroke(new BasicStroke(2f));
            if (type == 0) { g2d.drawLine(4, 4, size-4, size-4); g2d.drawLine(size-4, 4, 4, size-4); } 
            else if (type == 1) { g2d.drawLine(4, size/2, size-4, size/2); }
            g2d.dispose();
        }
    }
}