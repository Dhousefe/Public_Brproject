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
package ext.mods.commons.gui.services;

/**
 * Enum que define os tipos de servidores que o ProcessManagerService pode iniciar.
 */
public enum ServerType {
    
    GAME_SERVER("Game Server", "ext.mods.gameserver.GameServer", "game"),
    LOGIN_SERVER("Login Server", "ext.mods.loginserver.LoginServer", "login");

    private final String displayName;
    private final String mainClass;
    private final String workingDirectory;

    ServerType(String displayName, String mainClass, String workingDirectory) {
        this.displayName = displayName;
        this.mainClass = mainClass;
        this.workingDirectory = workingDirectory;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Encontra um ServerType pelo seu displayName (ex: "Game Server").
     * @param displayName O nome de exibição.
     * @return o ServerType ou null se não encontrado.
     */
    public static ServerType fromDisplayName(String displayName) {
        for (ServerType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(displayName)) {
                return type;
            }
        }
        return null;
    }
}