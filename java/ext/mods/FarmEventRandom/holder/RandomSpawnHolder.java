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
import ext.mods.gameserver.model.holder.IntIntHolder;

public class RandomSpawnHolder
{
	private static final CLogger LOGGER = new CLogger(RandomSpawnHolder.class.getName());
	private static final boolean DEBUG = false;

	private final int _npcId;
	private final IntIntHolder _countAndRange;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _respawnDelay;
	private final List<DropHolder> _drops;
	private final String _title;

	private final double _rateXp;
	private final double _rateSp;
	private final double _rateAdena;

	public RandomSpawnHolder(StatSet set)
	{
		_npcId = set.getInteger("npcId");
		_countAndRange = set.getIntIntHolder("countAndRange");
		_x = set.getInteger("x", 0);
		_y = set.getInteger("y", 0);
		_z = set.getInteger("z", 0);
		_respawnDelay = set.getInteger("respawnDelay", -1);
		_title = set.getString("title", "");

		_rateXp = set.getDouble("rateXp", -1.0);
		_rateSp = set.getDouble("rateSp", -1.0);
		_rateAdena = set.getDouble("rateAdena", -1.0);

		_drops = new ArrayList<>();
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
						_drops.add(new DropHolder(itemId, count, chance));
					} catch (NumberFormatException e) {
						LOGGER.error("[RandomSpawnHolder] Error parsing drop: " + part, e);
					}
				}
			}
		}

		if (DEBUG)
		{
			LOGGER.info("[Debug XML Spawn] Parsed: npcId=" + _npcId +
				", count=" + getCount() + ", range=" + getRange() +
				", respawn=" + _respawnDelay + ", title=" + _title +
				", drops=" + _drops.size() +
				", rateXp=" + _rateXp + ", rateSp=" + _rateSp + ", rateAdena=" + _rateAdena);
		}
	}

	public int getNpcId() { return _npcId; }
	public int getCount() { return (_countAndRange != null) ? _countAndRange.getId() : 0; }
	public int getRange() { return (_countAndRange != null) ? _countAndRange.getValue() : 0; }
	public int getX() { return _x; }
	public int getY() { return _y; }
	public int getZ() { return _z; }
	public int getRespawnDelay() { return _respawnDelay; }
	public List<DropHolder> getDrops() { return _drops; }
	public String getTitle() { return _title; }

	/**
	 * Gets the specific XP rate defined for this spawn.
	 * @return The XP rate multiplier, or -1.0 if not defined in the XML for this specific spawn.
	 */
	public double getRateXp() { return _rateXp; }

	/**
	 * Gets the specific SP rate defined for this spawn.
	 * @return The SP rate multiplier, or -1.0 if not defined in the XML for this specific spawn.
	 */
	public double getRateSp() { return _rateSp; }

	/**
	 * Gets the specific Adena rate defined for this spawn.
	 * @return The Adena rate multiplier, or -1.0 if not defined in the XML for this specific spawn.
	 */
	public double getRateAdena() { return _rateAdena; }
}