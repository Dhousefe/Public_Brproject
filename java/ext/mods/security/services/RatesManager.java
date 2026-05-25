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

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementação que carrega as Rates do arquivo 'rates.properties'
 * localizado em \game\config.
 */
public class RatesManager implements IRatesManager {

    private static final Path RATES_FILE_PATH = Paths.get("game", "config", "rates.properties");

    private final LinkedHashMap<String, Double> ratesCache = new LinkedHashMap<>();

    private static final LinkedHashMap<String, String> DISPLAY_RATE_KEYS = new LinkedHashMap<>() {{
        put("RateXp", "Rate Xp");
        put("RateSp", "Rate Sp");
        put("RatePartyXp", "Rate Party Xp");
        put("RatePartySp", "Rate Party Sp");
        put("RateDropCurrency", "Rate Drop Currency"); 
        put("RateDropSealStone", "Rate Drop Seal Stone");
        put("RateDropItems", "Rate Drop Items");
        put("RateQuestDrop", "Rate Quest Drop");
        put("RateQuestReward", "Rate Quest Reward");
        
        put("GrandBossRateXp", "Grand Boss Rate Xp");
        put("GrandBossRateSp", "Grand Boss Rate Sp");
        put("RateGrandDropItems", "Grand Boss Drop Items");

        put("RaidBossRateXp", "Raid Boss Rate Xp");
        put("RaidBossRateSp", "Raid Boss Rate Sp");
        put("RateRaidDropItems", "Raid Boss Drop Items");
        
        put("RateDropSpoil", "Rate Drop Spoil");
        put("RateDropHerbs", "Rate Drop Herbs");
    }};

    public RatesManager() {
        reloadRates();
    }

    @Override
    public LinkedHashMap<String, Double> getAllRates() {
        return new LinkedHashMap<>(ratesCache);
    }

    @Override
    public void reloadRates() {
        Properties properties = new Properties();
        ratesCache.clear();

        try (FileReader reader = new FileReader(RATES_FILE_PATH.toFile())) {
            properties.load(reader);
            System.out.println("Lendo Rates de: " + RATES_FILE_PATH.toAbsolutePath());

            for (Map.Entry<String, String> entry : DISPLAY_RATE_KEYS.entrySet()) {
                String fileKey = entry.getKey();
                String displayKey = entry.getValue();
                
                String valueStr = properties.getProperty(fileKey);
                
                if (valueStr != null) {
                    try {
                        String cleanedValueStr = valueStr.trim().replaceAll("[,\\.]", "");
                        
                        
                        String finalValue = valueStr.trim().replaceAll("\\.$", "");
                        
                        double rateValue = Double.parseDouble(finalValue);
                        
                        ratesCache.put(displayKey, rateValue);
                    } catch (NumberFormatException e) {
                        System.err.println("Erro FATAL ao converter Rate '" + fileKey + "' com valor '" + valueStr + "'. Verifique o formato no arquivo.");
                        ratesCache.put(displayKey, 1.0);
                    }
                } else {
                     System.out.println("Aviso: Chave de Rate '" + fileKey + "' não encontrada no arquivo. Usando 1.0.");
                     ratesCache.put(displayKey, 1.0);
                }
            }
        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: Não foi possível ler o arquivo rates.properties em " + RATES_FILE_PATH.toAbsolutePath());
            DISPLAY_RATE_KEYS.values().forEach(displayKey -> ratesCache.put(displayKey, 1.0));
        }
    }
}