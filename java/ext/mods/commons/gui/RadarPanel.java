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
import javax.swing.SwingUtilities;

import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;

public class RadarPanel extends ModernUI.SectionPanel {

    private static final int SCALE = 5;
    private static final int REFRESH_RATE = 200;

    public RadarPanel() {
        super("Real-time Radar");
        setPreferredSize(new Dimension(300, 300));
        setBackground(ModernUI.BG_CONSOLE);

        new Timer("Radar-Updater", true).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> repaint());
            }
        }, 0, REFRESH_RATE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;

        Player gm = findActiveGM();

        if (gm == null) {
            g2.setColor(ModernUI.TEXT_GRAY);
            g2.drawString("No GM Online", cx - 40, cy);
            return;
        }

        g2.setColor(new Color(40, 40, 50));
        int scanRange = 600;
        int step = 16;

        for (int x = -scanRange; x <= scanRange; x += step) {
            for (int y = -scanRange; y <= scanRange; y += step) {
                int worldX = gm.getX() + x;
                int worldY = gm.getY() + y;
                
                if (!GeoEngine.getInstance().canMoveToTarget(worldX, worldY, gm.getZ(), worldX + 1, worldY + 1, gm.getZ())) {
                    int drawX = cx + (x / SCALE);
                    int drawY = cy + (y / SCALE);
                    g2.fillRect(drawX, drawY, 2, 2);
                }
            }
        }

        g2.setColor(new Color(60, 60, 70));
        g2.drawLine(cx, 0, cx, h);
        g2.drawLine(0, cy, w, cy);
        g2.drawOval(cx - 50, cy - 50, 100, 100); 

        g2.setColor(Color.GREEN);
        g2.fillOval(cx - 3, cy - 3, 6, 6);
        g2.drawString("You", cx + 5, cy - 5);

        for (Creature obj : gm.getKnownTypeInRadius(Npc.class, 1500)) {
            if (obj instanceof Npc) {
                Npc npc = (Npc) obj;

                int relX = (npc.getX() - gm.getX()) / SCALE;
                int relY = (npc.getY() - gm.getY()) / SCALE; 
                int drawX = cx + relX;
                int drawY = cy + relY;

                if (npc.getTarget() != null) {
                    g2.setColor(ModernUI.NEON_PURPLE); 
                } else if (npc.isMoving()) {
                    g2.setColor(Color.ORANGE); 
                } else {
                    g2.setColor(Color.RED); 
                }

                g2.fillOval(drawX - 2, drawY - 2, 4, 4);

                int visRad = (int) (npc.getCollisionRadius() / SCALE);
                g2.setColor(new Color(255, 50, 50, 40)); 
                g2.fillOval(drawX - visRad, drawY - visRad, visRad * 2, visRad * 2);
            }
        }
    }

    private Player findActiveGM() {
        for (Player player : World.getInstance().getPlayers()) {
            if (player.isOnline() && player.getAccessLevel().isGm()) {
                return player;
            }
        }
        return null;
    }
}