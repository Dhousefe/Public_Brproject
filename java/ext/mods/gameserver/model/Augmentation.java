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
package ext.mods.gameserver.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.AugmentationData;
import ext.mods.gameserver.data.xml.AugmentationData.AugStat;
import ext.mods.gameserver.enums.skills.Stats;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.Timestamp;
import ext.mods.gameserver.network.serverpackets.SkillCoolTime;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.basefuncs.FuncAdd;

/**
 * A container used to store an Augmentation.<br>
 * <br>
 * Weapon Augmentation, alongside with enchanting and special abilities, adds special, random, features.<br>
 * <br>
 * Augmented items cannot be sold or transferred, they cannot be lost if your character dies, they cannot be dropped, however you can store them in your warehouse.
 */
public final class Augmentation
{
	private int _id;
	private AugmentationStatBoni _boni;
	private L2Skill _skill;
	
	public Augmentation(int id, L2Skill skill)
	{
		_id = id;
		_boni = new AugmentationStatBoni(_id);
		_skill = skill;
	}
	
	public Augmentation(int id, int skill, int skillLevel)
	{
		this(id, skill != 0 ? SkillTable.getInstance().getInfo(skill, skillLevel) : null);
	}
	
	public int getId()
	{
		return _id;
	}
	
	public L2Skill getSkill()
	{
		return _skill;
	}
	
	/**
	 * Applies this {@link Augmentation} boni to the {@link Player} set as parameter.
	 * @param player : The Augmentation boni benefactor.
	 */
	public void applyBonus(Player player)
	{
		_boni.applyBonus(player);
		
		if (_skill != null)
		{
			boolean updateTimeStamp = false;
			
			player.addSkill(_skill, false);
			
			if (_skill.isActive())
			{
				final Timestamp ts = player.getReuseTimeStamp().get(_skill.getReuseHashCode());
				if (ts != null)
				{
					final long delay = ts.getRemaining();
					if (delay > 0)
					{
						player.disableSkill(_skill, delay);
						updateTimeStamp = true;
					}
				}
			}
			
			player.sendPacket(new SkillList(player));
			
			if (updateTimeStamp)
				player.sendPacket(new SkillCoolTime(player));
		}
	}
	
	/**
	 * Removes this {@link Augmentation} boni to the {@link Player} set as parameter.
	 * @param player : The Augmentation boni benefactor.
	 */
	public void removeBonus(Player player)
	{
		_boni.removeBonus(player);
		
		if (_skill != null)
		{
			player.removeSkill(_skill.getId(), false, _skill.isPassive() || _skill.isToggle());
			
			player.sendPacket(new SkillList(player));
		}
	}
	
	public static class AugmentationStatBoni
	{
		private final Stats[] _stats;
		private final float[] _values;
		
		private boolean _active;
		
		public AugmentationStatBoni(int augmentationId)
		{
			_active = false;
			List<AugStat> as = AugmentationData.getInstance().getAugStatsById(augmentationId);
			
			_stats = new Stats[as.size()];
			_values = new float[as.size()];
			
			int i = 0;
			for (AugStat aStat : as)
			{
				_stats[i] = aStat.getStat();
				_values[i] = aStat.getValue();
				i++;
			}
		}
		
		public void applyBonus(Player player)
		{
			if (_active)
				return;
			
			for (int i = 0; i < _stats.length; i++)
				player.addStatFunc(new FuncAdd(this, _stats[i], _values[i], null));
			
			_active = true;
		}
		
		public void removeBonus(Player player)
		{
			if (!_active)
				return;
			
			player.removeStatsByOwner(this);
			
			_active = false;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			
			if (obj == null || getClass() != obj.getClass())
				return false;
			
			final AugmentationStatBoni other = (AugmentationStatBoni) obj;
			return Arrays.equals(_stats, other._stats) && Arrays.equals(_values, other._values);
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(Arrays.hashCode(_stats), Arrays.hashCode(_values));
		}
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		final Augmentation other = (Augmentation) obj;
		return _id == other._id && Objects.equals(_boni, other._boni) && Objects.equals(_skill, other._skill);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(_id, _boni, _skill);
	}
}