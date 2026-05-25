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
package ext.mods.security.services;

import java.util.LinkedHashMap;

/**
 * Interface que define o contrato para um gerenciador que lê e fornece os multiplicadores
 * de taxa (Rates) do servidor a partir de um arquivo de configuração.
 */
public interface IRatesManager {

    /**
     * @return Um LinkedHashMap contendo os nomes das propriedades de Rate
     * e seus respectivos valores Double lidos do arquivo rates.properties.
     */
    LinkedHashMap<String, Double> getAllRates();

    /**
     * Tenta recarregar os dados das Rates do arquivo de configuração.
     */
    void reloadRates();
}