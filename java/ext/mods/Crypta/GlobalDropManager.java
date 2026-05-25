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
package ext.mods.Crypta;

import java.util.List;

import ext.mods.Config;
import ext.mods.FarmEventRandom.holder.DropHolder;
import ext.mods.Crypta.RandomManager; 
import ext.mods.commons.random.Rnd;
import ext.mods.gameserver.custom.data.RatesData;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.custom.RatesHolder;
import ext.mods.global.GlobalDropData;
import ext.mods.commons.logging.CLogger;

public class GlobalDropManager
{
	public static final CLogger LOGGER = new CLogger(GlobalDropManager.class.getName());
	private final GlobalDropData _data = GlobalDropData.getInstance();
	
	public void reload()
	{
		_data.reload();
	}
	
	public boolean isEnabled()
	{
		return _data.isEnabled();
	}
	
	public boolean isIgnored(Attackable monster)
	{
		if (monster == null) return true;
		return _data.isIgnored(monster.getNpcId());
	}
	
	public boolean isDropsOriginals()
	{
		return _data.isDropsOriginals();
	}
	
	/**
	 * Verifica se este mod deve cancelar o drop original do monstro.
	 */
	public boolean shouldCancelOriginalDrop(Attackable monster)
	{
		Object randomManagerInstance = RandomManager.getInstance();
		if (randomManagerInstance != null) {
			Object result = RandomManager.getInstance().isEventMonster(monster);
			if (result instanceof Boolean && (Boolean) result) {
				return false;
			}
		}
		
		if (!isEnabled() || isIgnored(monster))
		{
			return false;
		}
		
		return !isDropsOriginals();
	}

	/**
	 * Chamado pelo Attackable.doDie() para ADICIONAR drops.
	 */
	public void onKill(Player player, Attackable monster)
	{
		Object randomManagerInstance = RandomManager.getInstance();
		if (randomManagerInstance != null) {
			Object result = RandomManager.getInstance().isEventMonster(monster);
			if (result instanceof Boolean && (Boolean) result) {
				return;
			}
		}
		
		if (player == null || !isEnabled() || isIgnored(monster))
		{
			return;
		}
		
		final int minLevel = _data.getMinLevel();
		final int maxLevel = _data.getMaxLevel();
		final int monsterLevel = monster.getStatus().getLevel();
		if (monsterLevel < minLevel)
			return;
		
		if (maxLevel > 0 && monsterLevel > maxLevel)
			return;
		
		List<DropHolder> drops = _data.getDrops();
		if (drops == null || drops.isEmpty())
		{
			return;
		}
		
		double chanceMultiplier = Math.max(0.0, _data.getChanceMultiplier());
		if (_data.isUseServerRates())
		{
			final RatesHolder rates = RatesData.getInstance().getRates(player.getStatus().getLevel());
			if (rates != null)
				chanceMultiplier *= Math.max(0.0, rates.getDropRate());
		}
		
		for (DropHolder drop : drops)
		{
			final int baseChance = Math.max(0, drop.getChance());
			final int effectiveChance = (int) Math.min(100.0, Math.round(baseChance * chanceMultiplier));
			if (Rnd.get(100) < effectiveChance)
			{
				if (Config.DEVELOPER) { LOGGER.info("[Debug GlobalDrop] SUCCESS! Dropping item " + drop.getItemId() + " (Chance: " + effectiveChance + ")"); }
				
				int totalAmount = drop.getCount();
				if (player.isInParty())
				{
					List<Player> members = player.getParty().getMembers();
					int size = members.size();
					if (size == 0) { player.addItem(drop.getItemId(), totalAmount, true); continue; }
					
					int baseAmount = totalAmount / size;
					int remainder = totalAmount % size;
					
					for (Player member : members)
					{
					    if (member != null && member.isOnline() && player.isIn3DRadius(member, Config.PARTY_RANGE))
					    {
						    int amount = baseAmount;
							if (remainder > 0) { amount++; remainder--; }
							if (amount > 0) member.addItem(drop.getItemId(), amount, true);
						} else if (remainder > 0) {
							remainder--;
						}
					}
					if (remainder > 0) { player.addItem(drop.getItemId(), remainder, true); }
				}
				else
				{
					player.addItem(drop.getItemId(), totalAmount, true);
				}
			}
			else if (Config.DEVELOPER)
			{
				LOGGER.info("[Debug GlobalDrop] FAILED drop for item " + drop.getItemId() + " (Chance: " + effectiveChance + ")");
			}
		}
	}
	
	
	public double getActiveRateXp(Attackable monster) { return 1.0; }
	public double getActiveRateSp(Attackable monster) { return 1.0; }
	public double getActiveRateAdena(Attackable monster) { return 1.0; }
	
	
	public static GlobalDropManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final GlobalDropManager _instance = new GlobalDropManager();
	}
}