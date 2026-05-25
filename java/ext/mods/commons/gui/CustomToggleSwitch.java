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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

/**
 * Um componente de botão de alternância ON/OFF personalizado com animação.
 */
public class CustomToggleSwitch extends JComponent {

    private boolean isOn;
    private float knobPosition;
    private Timer animator;
    private final List<ActionListener> listeners = new ArrayList<>();

    private final Color trackColorOff = ThemeManager.BORDER_COLOR.darker(); 
    private final Color trackColorOn = ThemeManager.BASE_PURPLE;
    private final Color trackBorderColorOff = ThemeManager.BORDER_COLOR;
    private final Color trackBorderColorOn = ThemeManager.BASE_PURPLE.brighter();
    private final Color knobColor = new Color(220, 220, 220);

    private final Dimension preferredSize = new Dimension(32, 18); 
    private final int trackArc = 8;
    private final int knobArc = 6; 
    private final int padding = 2;

    public CustomToggleSwitch(boolean initialState) {
        setPreferredSize(preferredSize);
        setMinimumSize(preferredSize);
        setMaximumSize(preferredSize);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        this.isOn = initialState;
        int knobSize = preferredSize.height - (padding * 2);
        this.knobPosition = initialState ? preferredSize.width - knobSize - padding : padding;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isEnabled()) {
                    toggleState();
                }
            }
        });
    }

    public void toggleState() {
        setOn(!isOn, true);
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        setOn(on, false);
    }

    public void setOn(boolean on, boolean fireEvent) {
        if (this.isOn == on) return;
        boolean oldState = this.isOn;
        this.isOn = on;

        if (animator != null && animator.isRunning()) {
            animator.stop();
        }

        int knobSize = getHeight() - (padding * 2);
        if (knobSize <= 0) knobSize = preferredSize.height - (padding * 2);

        final float targetPosition = isOn ? getWidth() - knobSize - padding : padding;
        if (getWidth() <= 0) {
            knobPosition = isOn ? preferredSize.width - knobSize - padding : padding;
            repaint();
        } else {
            animator = new Timer(8, e -> {
                float diff = targetPosition - knobPosition;
                if (Math.abs(diff) < 1.0f) {
                    knobPosition = targetPosition;
                    ((Timer) e.getSource()).stop();
                } else {
                    knobPosition += diff * 0.3f;
                }
                repaint();
            });
            animator.start();
        }


        if (fireEvent) {
            fireActionEvent();
        }
        firePropertyChange("state", oldState, this.isOn);
    }

    public void addActionListener(ActionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeActionListener(ActionListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void fireActionEvent() {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, isOn ? "ON" : "OFF", System.currentTimeMillis(), 0);
        for (ActionListener listener : listeners) {
            listener.actionPerformed(event);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setCursor(enabled ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
        repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        if (height < preferredSize.height) height = preferredSize.height;
        int knobSize = height - (padding * 2);

        if (!isEnabled()) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        RoundRectangle2D track = new RoundRectangle2D.Double(0, 0, width, height, trackArc, trackArc);
        g2d.setColor(isOn ? trackColorOn : trackColorOff);
        g2d.fill(track);

        g2d.setStroke(new BasicStroke(1f));
        g2d.setColor(isOn ? trackBorderColorOn : trackBorderColorOff);
        g2d.draw(track);

        float currentKnobX = Math.max(padding, Math.min(knobPosition, width - knobSize - padding));

        RoundRectangle2D knob = new RoundRectangle2D.Double(currentKnobX, padding, knobSize, knobSize, knobArc, knobArc);
        g2d.setColor(knobColor);
        g2d.fill(knob);

        g2d.dispose();
    }
}