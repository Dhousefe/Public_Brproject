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
package ext.mods.FarmEventRandom.holder;

import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.data.StatSet;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.model.location.Location;

/**
 * Armazena as configurações para UMA zona de evento.
 * Inclui regras padrão (para monstros originais), rates customizados,
 * e uma lista de spawns customizados (para monstros extras).
 * ATUALIZADO: Inclui flags 'active', 'isVip', 'isPartyZone' e 'minPartySize'.
 */
public class RandomZoneData
{
	private static final CLogger LOGGER = new CLogger(RandomZoneData.class.getName());
	
	private final int _zoneId;
	private final boolean _useOriginals;
	private final boolean _active;

	private final boolean _dropsOriginals; 
	private final double _rateXp;
	private final double _rateSp;
	private final double _rateAdena;

	private final String _defaultTitle;
	private final int _defaultRespawnDelay;
	private final List<DropHolder> _defaultDrops;

	private final List<RandomSpawnHolder> _customSpawns = new ArrayList<>();

	private final boolean _isVip;
	private final boolean _isPartyZone;
	private final int _minPartySize;
	private final boolean _dwarvenOnly;
	private final boolean _enchanterZone;
	private final double _enchantChance;
	private final int _maxEnchant;
	private final Location _spawnLocation;

	public RandomZoneData(StatSet set)
	{
		_zoneId = set.getInteger("zoneId");
		_useOriginals = set.getBool("useOriginals", false);
		_active = set.getBool("active", true);

		_dropsOriginals = set.getBool("DropsOriginals", false);
		_rateXp = set.getDouble("rateXp", 1.0);
		_rateSp = set.getDouble("rateSp", 1.0);
		_rateAdena = set.getDouble("rateAdena", 1.0);

		_defaultTitle = set.getString("title", "[Farm Event]");
		_defaultRespawnDelay = set.getInteger("respawnDelay", -1);

		_defaultDrops = new ArrayList<>();
		String dropsStr = set.getString("drops", "");
		if (!dropsStr.isEmpty())
		{
			for (String part : dropsStr.split(";"))
			{
				String[] vals = part.split("-");
				if (vals.length >= 3)
				{
					try {
						int itemId = Integer.parseInt(vals[0]);
						int count = Integer.parseInt(vals[1]);
						int chance = Integer.parseInt(vals[2]);
						_defaultDrops.add(new DropHolder(itemId, count, chance));
					} catch (NumberFormatException e) {
						LOGGER.error("[RandomZoneData] Erro ao ler drop padrão: " + part, e);
					}
				}
			}
		}

		_isVip = set.getBool("isVip", false);
		_isPartyZone = set.getBool("isPartyZone", false);
		_minPartySize = set.getInteger("minPartySize", 2);
		_dwarvenOnly = set.getBool("DWARVEN_Only", false);
		_enchanterZone = set.getBool("Enchanter_Zone", false);
		_enchantChance = set.getDouble("enchantChance", 0.0);
		_maxEnchant = set.getInteger("maxEnchant", 15);
		
		Location tempSpawnLocation = null;
		String spawnLocStr = set.getString("spawnLocation", "");
		if (!spawnLocStr.isEmpty())
		{
			try
			{
				String[] coords = spawnLocStr.split(",");
				if (coords.length == 3)
				{
					int x = Integer.parseInt(coords[0].trim());
					int y = Integer.parseInt(coords[1].trim());
					int z = Integer.parseInt(coords[2].trim());
					tempSpawnLocation = new Location(x, y, z);
				}
				else
				{
					LOGGER.warn("[RandomZoneData] Formato inválido para spawnLocation na zona " + _zoneId + ". Esperado: 'x,y,z'. Recebido: '" + spawnLocStr + "'");
				}
			}
			catch (NumberFormatException e)
			{
				LOGGER.error("[RandomZoneData] Erro ao parsear spawnLocation na zona " + _zoneId + ": " + spawnLocStr, e);
			}
		}
		_spawnLocation = tempSpawnLocation;
	}

	public int getZoneId() { return _zoneId; }
	public boolean useOriginals() { return _useOriginals; }
	public boolean isActive() { return _active; }
	public boolean dropsOriginals() { return _dropsOriginals; }
	public double getRateXp() { return _rateXp; }
	public double getRateSp() { return _rateSp; }
	public double getRateAdena() { return _rateAdena; }
	public String getDefaultTitle() { return _defaultTitle; }
	public int getDefaultRespawnDelay() { return _defaultRespawnDelay; }
	public List<DropHolder> getDefaultDrops() { return _defaultDrops; }
	public void addCustomSpawn(RandomSpawnHolder spawn) { _customSpawns.add(spawn); }
	public List<RandomSpawnHolder> getCustomSpawns() { return _customSpawns; }

	/**
	 * @return true se esta zona requer que o jogador seja VIP.
	 */
	public boolean isVip() { return _isVip; }

	/**
	 * @return true se esta zona requer que o jogador esteja em uma Party.
	 */
	public boolean isPartyZone() { return _isPartyZone; }

	/**
	 * @return O número mínimo de membros necessários na Party para entrar/permanecer na zona (se isPartyZone for true).
	 */
	public int getMinPartySize() { return _minPartySize; }

	/**
	 * @return true se esta zona permite apenas classes DWARVEN.
	 */
	public boolean isDwarvenOnly() { return _dwarvenOnly; }

	/**
	 * @return true se esta zona é uma Enchanter Zone (PvP ativo e encantamento automático).
	 */
	public boolean isEnchanterZone() { return _enchanterZone; }

	/**
	 * @return A porcentagem de chance de encantamento automático em kills PvP (0.0 a 100.0).
	 */
	public double getEnchantChance() { return _enchantChance; }

	/**
	 * @return O nível máximo de encantamento permitido para itens nesta zona.
	 */
	public int getMaxEnchant() { return _maxEnchant; }
	
	/**
	 * @return A localização de spawn configurada para esta zona, ou null se não configurada (usa spawn random).
	 */
	public Location getSpawnLocation() { return _spawnLocation; }
}