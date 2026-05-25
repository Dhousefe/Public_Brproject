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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class ComponentResizer extends MouseAdapter {
    private static final Dimension MIN_SIZE = new Dimension(600, 400);
    private final Window window;
    private final int dragInsets = 8;

    private int pressedCursorType = Cursor.DEFAULT_CURSOR;
    private Rectangle startBounds;
    private Point startMousePoint;

    public ComponentResizer(Window window) {
        this.window = window;
        window.addMouseListener(this);
        window.addMouseMotionListener(this);
    }

    public void registerComponent(Component... components) {
        for (Component c : components) {
            c.addMouseListener(this);
            c.addMouseMotionListener(this);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Component source = e.getComponent();
        Point p = SwingUtilities.convertPoint(source, e.getPoint(), window);
        int cursorType = getCursorType(p);

        if (cursorType != Cursor.DEFAULT_CURSOR) {
            window.setCursor(Cursor.getPredefinedCursor(cursorType));
            if (source != window) source.setCursor(Cursor.getPredefinedCursor(cursorType));
        } else {
            if (window.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
                window.setCursor(Cursor.getDefaultCursor());
            }
            if (source != window) source.setCursor(null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Component source = e.getComponent();
        Point p = SwingUtilities.convertPoint(source, e.getPoint(), window);
        
        pressedCursorType = getCursorType(p);
        startBounds = window.getBounds();
        startMousePoint = e.getLocationOnScreen();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        pressedCursorType = Cursor.DEFAULT_CURSOR;
        startBounds = null;
        startMousePoint = null;
        window.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (pressedCursorType == Cursor.DEFAULT_CURSOR || startBounds == null) return;

        Point currMousePoint = e.getLocationOnScreen();
        int deltaX = currMousePoint.x - startMousePoint.x;
        int deltaY = currMousePoint.y - startMousePoint.y;

        Rectangle newBounds = new Rectangle(startBounds);

        
        if (pressedCursorType == Cursor.E_RESIZE_CURSOR || 
            pressedCursorType == Cursor.NE_RESIZE_CURSOR || 
            pressedCursorType == Cursor.SE_RESIZE_CURSOR) {
            newBounds.width = Math.max(MIN_SIZE.width, startBounds.width + deltaX);
        }

        if (pressedCursorType == Cursor.W_RESIZE_CURSOR || 
            pressedCursorType == Cursor.NW_RESIZE_CURSOR || 
            pressedCursorType == Cursor.SW_RESIZE_CURSOR) {
            int newWidth = Math.max(MIN_SIZE.width, startBounds.width - deltaX);
            if (newWidth != MIN_SIZE.width) {
                newBounds.x = startBounds.x + deltaX;
                newBounds.width = newWidth;
            } else {
                newBounds.x = startBounds.x + (startBounds.width - MIN_SIZE.width);
                newBounds.width = MIN_SIZE.width;
            }
        }

        if (pressedCursorType == Cursor.S_RESIZE_CURSOR || 
            pressedCursorType == Cursor.SE_RESIZE_CURSOR || 
            pressedCursorType == Cursor.SW_RESIZE_CURSOR) {
            newBounds.height = Math.max(MIN_SIZE.height, startBounds.height + deltaY);
        }

        if (pressedCursorType == Cursor.N_RESIZE_CURSOR || 
            pressedCursorType == Cursor.NE_RESIZE_CURSOR || 
            pressedCursorType == Cursor.NW_RESIZE_CURSOR) {
            int newHeight = Math.max(MIN_SIZE.height, startBounds.height - deltaY);
            if (newHeight != MIN_SIZE.height) {
                newBounds.y = startBounds.y + deltaY;
                newBounds.height = newHeight;
            } else {
                newBounds.y = startBounds.y + (startBounds.height - MIN_SIZE.height);
                newBounds.height = MIN_SIZE.height;
            }
        }

        window.setBounds(newBounds);
        window.revalidate();
        window.repaint();
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        if (pressedCursorType == Cursor.DEFAULT_CURSOR) {
            window.setCursor(Cursor.getDefaultCursor());
        }
    }

    private int getCursorType(Point p) {
        int w = window.getWidth();
        int h = window.getHeight();

        boolean isRight = p.x >= w - dragInsets;
        boolean isLeft = p.x <= dragInsets;
        boolean isBottom = p.y >= h - dragInsets;
        boolean isTop = p.y <= dragInsets;

        if (isRight && isBottom) return Cursor.SE_RESIZE_CURSOR;
        if (isLeft && isBottom) return Cursor.SW_RESIZE_CURSOR;
        if (isRight && isTop) return Cursor.NE_RESIZE_CURSOR;
        if (isLeft && isTop) return Cursor.NW_RESIZE_CURSOR;
        
        if (isRight) return Cursor.E_RESIZE_CURSOR;
        if (isLeft) return Cursor.W_RESIZE_CURSOR;
        if (isBottom) return Cursor.S_RESIZE_CURSOR;
        if (isTop) return Cursor.N_RESIZE_CURSOR;
        
        return Cursor.DEFAULT_CURSOR;
    }
}