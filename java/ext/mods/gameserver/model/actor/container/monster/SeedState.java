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
package ext.mods.gameserver.model.actor.container.monster;

import ext.mods.Config;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.manor.Seed;
import ext.mods.gameserver.skills.L2Skill;

/**
 * A container holding all related informations of a {@link Monster} seed state.<br>
 * <br>
 * A seed occurs when a {@link Player} uses a seed over a Monster.
 */
public class SeedState
{
	private final Monster _owner;
	
	private int _seederId;
	private Seed _seed;
	private boolean _isHarvested;
	
	public SeedState(Monster owner)
	{
		_owner = owner;
	}
	
	public boolean isSeeded()
	{
		return _seederId != 0;
	}
	
	public Seed getSeed()
	{
		return _seed;
	}
	
	/**
	 * Set the seed parameters.
	 * @param player : The {@link Player} seeding the monster.
	 * @param seed : The {@link Seed} used to seed the monster.
	 */
	public void setSeeded(Player player, Seed seed)
	{
		_seederId = player.getObjectId();
		_seed = seed;
	}
	
	public boolean isHarvested()
	{
		return _isHarvested;
	}
	
	public void setHarvested()
	{
		_isHarvested = true;
	}
	
	/**
	 * @param player : The {@link Player} to test.
	 * @return True if the {@link Player} set as parameter can harvest, or false otherwise.
	 */
	public boolean isAllowedToHarvest(Player player)
	{
		if (player == null)
			return false;
		
		if (player.getObjectId() == _seederId)
			return true;
		
		final Player seeder = World.getInstance().getPlayer(_seederId);
		if (seeder == null || seeder.getParty() == null)
			return false;
		
		return seeder.getParty().containsPlayer(player);
	}
	
	/**
	 * @return The {@link IntIntHolder} containing seed crop info.
	 */
	public IntIntHolder getHarvestedCrop()
	{
		int count = 1;
		for (L2Skill skill : _owner.getTemplate().getPassives())
		{
			if (skill.getId() == 4408)
			{
				switch (skill.getLevel())
				{
					case 10:
						count *= 2;
						break;
					case 11:
						count *= 3;
						break;
					case 12:
						count *= 4;
						break;
					case 13:
						count *= 5;
						break;
					case 14:
						count *= 6;
						break;
					case 15:
						count *= 7;
						break;
					case 16:
						count *= 8;
						break;
					case 17:
						count *= 9;
						break;
				}
			}
		}
		
		final int diff = _owner.getStatus().getLevel() - _seed.getLevel() - 5;
		if (diff > 0)
			count += diff;
		
		return new IntIntHolder(_seed.getCropId(), count * Config.RATE_DROP_MANOR);
	}
	
	/**
	 * Clear all seed related variables.
	 */
	public void clear()
	{
		_seederId = 0;
		_seed = null;
		_isHarvested = false;
	}
}