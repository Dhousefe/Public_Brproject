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
package ext.mods.gameserver.data;

import java.util.concurrent.ThreadLocalRandom;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.DropType;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.item.DropCategory;
import ext.mods.gameserver.model.item.DropData;

public final class DropCalc
{
	public static final int SEED = 0x7FFF;
	
	public boolean dice(Player player, Monster monster, double chance)
	{
		return calcLevelPenalty(player, monster) >= (prob() % 100) && dice(chance);
	}
	
	public boolean dice(double chance)
	{
		return rand(0.0f, 100.0f) <= chance;
	}
	
	public double calcDropChance(Player player, Npc npc, double chance, DropType dropType, boolean isRaid, boolean isGrand)
	{
		if (dropType == DropType.SPOIL)
			chance *= (int) ((player.getStatus().calcStat(Stats.SPOIL_RATE, 100, null, null)) / 100);
		
		if (dropType == DropType.DROP)
			chance *= (int) ((player.getStatus().calcStat(Stats.DROP_RATE, 100, null, null)) / 100);
		
		if (dropType == DropType.CURRENCY)
			chance *= (int) ((player.getStatus().calcStat(Stats.CURRENCY_RATE, 100, null, null)) / 100);
		
		return chance * dropType.getDropRate(player, npc, isRaid, isGrand);
	}
	
	public double calcDropChance(Player player, Npc npc, DropData drop, DropType dropType, boolean isRaid, boolean isGrand)
	{
		return calcDropChance(player, npc, drop.getChance(), dropType, isRaid, isGrand);
	}
	
	public double calcDropChance(Player player, Npc npc, DropCategory category, DropType dropType, boolean isRaid, boolean isGrand)
	{
		return calcDropChance(player, npc, category.getChance(), dropType, isRaid, isGrand);
	}
	
	public int calcItemDropCount(Player player, Monster monster, double categoryChance, DropData drop, DropType dropType, boolean isRaid, boolean isGrand)
	{
		int itemCount = (int) rand(drop.getMinDrop(), drop.getMaxDrop(), 0.5);
		if (Config.ALTERNATE_DROP_LIST)
		{
			final double chance = DropCalc.getInstance().calcDropChance(player, monster, drop, dropType, isRaid, isGrand);
			
			final double overflowFactor = Math.max(0.0, (chance - 100) / 100);
			final double inverseCategoryChance = (100 - categoryChance) / 100;
			final double reduceFactor = Math.pow(inverseCategoryChance, 10);
			
			int min = drop.getMinDrop();
			int max = drop.getMaxDrop();
			
			min = (int) (min + min * overflowFactor - min * overflowFactor * reduceFactor);
			max = (int) (max + max * overflowFactor - max * overflowFactor * reduceFactor);
			min = Math.max(min, drop.getMinDrop());
			max = Math.max(max, min);
			
			itemCount = Rnd.get(min, max);
		}
		
		return itemCount;
	}
	
	static float calcLevelPenalty(Player player, Monster monster)
	{
		final int diff = monster.getStatus().getLevel() - player.getStatus().getLevel();
		if (diff < -5)
		{
			if (diff < -10)
				return 10.0f;
			else
				return diff * 18.0f + 190.0f;
		}
		else
			return 100.0f;
	}
	
	static double rand(double min, double max)
	{
		return prob() / (double) SEED * (max - min) + min;
	}
	
	static double rand(double min, double max, double fact)
	{
		return prob() / (double) SEED * (max - min) + min + fact;
	}
	
	static int prob()
	{
		return ThreadLocalRandom.current().nextInt() & SEED;
	}
	
	public static final DropCalc getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final DropCalc INSTANCE = new DropCalc();
	}
}