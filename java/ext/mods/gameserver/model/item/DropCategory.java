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
package ext.mods.gameserver.model.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.DropCalc;
import ext.mods.gameserver.enums.DropType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.GrandBoss;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.holder.IntIntHolder;

public class DropCategory
{
	private final DropType _dropType;
	private final double _chance;
	private final List<DropData> _drops;
	
	private double _cumulativeChance;
	
	public DropCategory(DropType dropType, double chance)
	{
		_dropType = dropType;
		_chance = chance;
		_drops = new ArrayList<>(0);
		
		_cumulativeChance = 0;
	}
	
	/**
	 * Adds {@link DropData} to this {@link DropCategory}.
	 * @param drop
	 */
	public void addDropData(DropData drop)
	{
		_drops.add(drop);
		
		_cumulativeChance += drop.getChance();
	}
	
	/**
	 * @return The {@link DropType} of this {@link DropCategory}.
	 */
	public DropType getDropType()
	{
		return _dropType;
	}
	
	/**
	 * @return The {@link DropCategory} chance.
	 */
	public double getChance()
	{
		return _chance;
	}
	
	/**
	 * @return The list of all {@link DropData}, which belongs to this {@link DropCategory}.
	 */
	public List<DropData> getAllDrops()
	{
		return _drops;
	}
	
	public double getCategoryCumulativeChance()
	{
		return (_dropType == DropType.SPOIL) ? 100. : _cumulativeChance;
	}
	
	static final ThreadLocal<DropData[]> threadLocalDropArray = ThreadLocal.withInitial(() -> new DropData[100]);
	
	public DropData[] shuffleDropList()
	{
		DropData[] arr = threadLocalDropArray.get();
		Arrays.fill(arr, null);
		for (int i = 0; i < _drops.size(); i++)
			arr[i] = _drops.get(i);
		
		Collections.shuffle(Arrays.asList(arr));
		trim(arr);
		return arr;
	}
	
	public static void trim(Object[] arr)
	{
		for (int i = 0; i < arr.length; i++)
		{
			if (arr[i] == null)
			{
				for (int n = i + 1; n < arr.length; n++)
				{
					if (arr[n] != null)
					{
						arr[i] = arr[n];
						arr[n] = null;
						break;
					}
				}
			}
		}
	}
	
	public List<IntIntHolder> calcDropList(Player player, Monster monster, List<IntIntHolder> out, boolean raid)
	{
		final var list = shuffleDropList();
		if (DropCalc.getInstance().dice(player, monster, DropCalc.getInstance().calcDropChance(player, monster, this, getDropType(), raid, monster instanceof GrandBoss)))
		{
			if (Config.ALTERNATE_DROP_LIST)
			{
				for (var i = 0; i < _drops.size(); i++)
				{
					final DropData item = list[i];
					if (item != null)
					{
						if (calcDropItem(item, player, monster, out, raid))
							continue;
					}
				}
			}
			else
			{
				for (final DropData item : _drops)
				{
					if (calcDropItem(item, player, monster, out, raid))
						break;
				}
			}
		}
		
		return out;
	}
	
	public boolean calcDropItem(DropData item, Player player, Monster monster, List<IntIntHolder> out, boolean raid)
	{
		double itemChance = DropCalc.getInstance().calcDropChance(player, monster, item, getDropType(), raid, monster instanceof GrandBoss);
		int[] itemCount =
		{
			0
		};
		
		if (DropCalc.getInstance().dice(itemChance))
			itemCount[0] = DropCalc.getInstance().calcItemDropCount(player, monster, getChance(), item, getDropType(), raid, monster instanceof GrandBoss);
		
		if (itemCount[0] > 0)
		{
			Optional<IntIntHolder> holder = out.stream().filter(h -> h.getId() == item.getItemId()).findAny();
			if (holder.isEmpty())
				out.add(new IntIntHolder(item.getItemId(), itemCount[0]));
			else
				holder.ifPresent(h -> h.setValue(h.getValue() + itemCount[0]));
			return true;
		}
		
		return false;
	}
	
	/**
	 * Calculates drops of this {@link DropCategory}.
	 * @param player
	 * @param monster
	 * @param levelMultiplier : The input level modifier of the last attacker.
	 * @param raid : The NPC is raid boss.
	 * @return The list of {@link IntIntHolder} holding item ID and item count.
	 */
	public List<IntIntHolder> calculateDrop(Player player, Monster monster, double levelMultiplier, boolean raid)
	{
		double chance = getChance() * levelMultiplier * getDropType().getDropRate(player, monster, raid, monster instanceof GrandBoss) * DropData.MAX_CHANCE / 100;
		
		if (_dropType == DropType.SPOIL)
			chance = (int) ((chance * player.getStatus().calcStat(Stats.SPOIL_RATE, 100, null, null)) / 100);
		
		if (_dropType == DropType.DROP)
			chance = (int) ((chance * player.getStatus().calcStat(Stats.DROP_RATE, 100, null, null)) / 100);
		
		if (_dropType == DropType.CURRENCY)
			chance = (int) ((chance * player.getStatus().calcStat(Stats.CURRENCY_RATE, 100, null, null)) / 100);
		
		double multiplier;
		
		if (chance <= DropData.MAX_CHANCE)
		{
			if (Rnd.get(DropData.MAX_CHANCE) < chance)
				multiplier = 1;
			else
				return Collections.emptyList();
		}
		else
			multiplier = chance / DropData.MAX_CHANCE;
		
		final List<IntIntHolder> result = new ArrayList<>(1);
		
		if (_dropType == DropType.SPOIL)
		{
			for (DropData dd : getAllDrops())
			{
				chance = dd.getChance() * multiplier * DropData.MAX_CHANCE / 100;
				chance = (int) ((chance * player.getStatus().calcStat(Stats.SPOIL_RATE, 100, null, null)) / 100);
				
				if (chance <= DropData.MAX_CHANCE)
				{
					if (Rnd.get(DropData.MAX_CHANCE) < chance)
						result.add(dd.calculateDrop(1));
				}
				else
					result.add(dd.calculateDrop(chance / DropData.MAX_CHANCE));
			}
		}
		else
		{
			chance = getCategoryCumulativeChance() * multiplier * DropData.MAX_CHANCE / 100;
			
			if (chance <= DropData.MAX_CHANCE)
			{
				chance = Rnd.get(DropData.MAX_CHANCE);
				for (DropData dd : getAllDrops())
				{
					chance -= dd.getChance() * multiplier * DropData.MAX_CHANCE / 100;
					
					if (chance < 0)
					{
						result.add(dd.calculateDrop(1));
						break;
					}
				}
			}
			else
			{
				double amount = multiplier;
				multiplier = 100 / getCategoryCumulativeChance();
				amount /= multiplier;
				
				chance = Rnd.get(DropData.MAX_CHANCE);
				
				for (DropData dd : getAllDrops())
				{
					chance -= dd.getChance() * multiplier * DropData.MAX_CHANCE / 100;
					if (chance < 0)
					{
						result.add(dd.calculateDrop(amount));
						break;
					}
				}
			}
		}
		
		return result;
	}
}