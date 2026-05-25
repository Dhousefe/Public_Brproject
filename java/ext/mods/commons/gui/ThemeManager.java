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

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class ThemeManager {

    /** Modo seguro: usa cores solidas em vez de gradientes (VPS sem GPU). Ative com -Dbrproject.safe.graphics=true */
    public static boolean isSafeGraphics() {
        return "true".equalsIgnoreCase(System.getProperty("brproject.safe.graphics"));
    }

    public static final Color VERY_DARK_BACKGROUND = new Color(10, 10, 15); 
    public static final Color COMPONENT_BACKGROUND = new Color(10, 10, 15); 
    public static final Color MENU_POPUP_BACKGROUND = Color.BLACK;
    public static final Color BORDER_COLOR = new Color(80, 0, 120); 

public static final Color BASE_PURPLE = new Color(90, 30, 180); 
    public static final Color SOFT_PURPLE_SELECTION = new Color(120, 45, 200);

    public static final Color TEXT_COLOR = new Color(220, 220, 230);
    public static final Color TEXT_SELECTED = Color.BLACK;

    public static void applyTheme() {
        try {
            UIManager.put("control", COMPONENT_BACKGROUND);
            UIManager.put("info", BORDER_COLOR);
            UIManager.put("nimbusBase", BASE_PURPLE);
            UIManager.put("nimbusLightBackground", VERY_DARK_BACKGROUND);
            UIManager.put("text", TEXT_COLOR);

            UIManager.put("Panel.background", COMPONENT_BACKGROUND);
            UIManager.put("TextArea.background", COMPONENT_BACKGROUND);
            UIManager.put("TextField.background", COMPONENT_BACKGROUND);
            UIManager.put("List.background", COMPONENT_BACKGROUND);
            UIManager.put("Table.background", COMPONENT_BACKGROUND);
            UIManager.put("ScrollPane.background", VERY_DARK_BACKGROUND);
            UIManager.put("Viewport.background", COMPONENT_BACKGROUND);
            UIManager.put("nimbusBlueGrey", COMPONENT_BACKGROUND);

            UIManager.put("nimbusSelectedText", TEXT_SELECTED);
            UIManager.put("nimbusSelectionBackground", BASE_PURPLE);
            UIManager.put("nimbusFocus", BASE_PURPLE);

            UIManager.put("MenuBar.background", VERY_DARK_BACKGROUND);
            UIManager.put("MenuBar.border", BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
            UIManager.put("Menu.foreground", TEXT_COLOR);
            UIManager.put("Menu.background", VERY_DARK_BACKGROUND);
            UIManager.put("Menu.selectionBackground", BASE_PURPLE);
            UIManager.put("Menu.selectionForeground", TEXT_SELECTED);
            
            UIManager.put("MenuItem.background", VERY_DARK_BACKGROUND);
            UIManager.put("MenuItem.foreground", TEXT_COLOR);
            UIManager.put("MenuItem.selectionBackground", BASE_PURPLE);
            UIManager.put("MenuItem.selectionForeground", TEXT_SELECTED);

            UIManager.put("PopupMenu.background", MENU_POPUP_BACKGROUND);
            UIManager.put("PopupMenu.border", BorderFactory.createLineBorder(BORDER_COLOR));

            UIManager.setLookAndFeel(new NimbusLookAndFeel());

            UIManager.put("Panel.background", COMPONENT_BACKGROUND);
            UIManager.put("Viewport.background", COMPONENT_BACKGROUND);

        } catch (Exception e) {
            System.err.println("Erro ao aplicar o tema:");
            e.printStackTrace();
        }
    }
}