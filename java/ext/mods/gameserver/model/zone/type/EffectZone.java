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
package ext.mods.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.zone.type.subtype.ZoneType;
import ext.mods.gameserver.network.serverpackets.EtcStatusUpdate;
import ext.mods.gameserver.skills.L2Skill;

/**
 * A zone extending {@link ZoneType}, which fires a task on the first {@link Creature} entrance.<br>
 * <br>
 * This task launches skill effects on all {@link Creature}s within this zone, and can affect specific class types. It can also be activated or desactivated. The zone is considered a danger zone.
 */
public class EffectZone extends ZoneType
{
	private final List<IntIntHolder> _skills = new ArrayList<>(5);
	
	private int _chance = 100;
	private int _initialDelay = 0;
	private int _reuseDelay = 30000;
	private Class<?> _target = Player.class;
	
	private boolean _isEnabled = true;
	
	private volatile Future<?> _task;
	
	public EffectZone(int id)
	{
		super(id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("chance"))
			_chance = Integer.parseInt(value);
		else if (name.equals("initialDelay"))
			_initialDelay = Integer.parseInt(value);
		else if (name.equals("reuseDelay"))
			_reuseDelay = Integer.parseInt(value);
		else if (name.equals("defaultStatus"))
			_isEnabled = Boolean.parseBoolean(value);
		else if (name.equals("skill"))
		{
			final String[] skills = value.split(";");
			for (String skill : skills)
			{
				final String[] skillSplit = skill.split("-");
				if (skillSplit.length != 2)
					LOGGER.warn("Invalid skill format {} for {}.", skill, toString());
				else
				{
					try
					{
						_skills.add(new IntIntHolder(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1])));
					}
					catch (NumberFormatException nfe)
					{
						LOGGER.warn("Invalid skill format {} for {}.", skill, toString());
					}
				}
			}
		}
		else if (name.equals("targetType"))
		{
			try
			{
				_target = Class.forName("ext.mods.gameserver.model.actor." + value);
			}
			catch (ClassNotFoundException e)
			{
				LOGGER.error("Invalid target type {} for {}.", value, toString());
			}
		}
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected boolean isAffected(Creature creature)
	{
		return _target.isInstance(creature);
	}
	
	@Override
	protected void onEnter(Creature creature)
	{
		Future<?> task = _task;
		if (task == null)
		{
			synchronized (this)
			{
				task = _task;
				if (task == null)
					_task = ThreadPool.scheduleAtFixedRate(this::applyEffect, _initialDelay, _reuseDelay);
			}
		}
		
		if (creature instanceof Player player)
		{
			player.setInsideZone(ZoneId.DANGER_AREA, true);
			player.sendPacket(new EtcStatusUpdate(player));
		}
	}
	
	@Override
	protected void onExit(Creature creature)
	{
		if (creature instanceof Player player)
		{
			player.setInsideZone(ZoneId.DANGER_AREA, false);
			
			if (!player.isInsideZone(ZoneId.DANGER_AREA))
				player.sendPacket(new EtcStatusUpdate(player));
		}
	}
	
	/**
	 * Edit this zone activation state.
	 * @param state : The new state to set.
	 */
	public void editStatus(boolean state)
	{
		_isEnabled = state;
	}
	
	/**
	 * Apply this {@link EffectZone} effect to all {@link Creature}s of defined target type.
	 */
	private final void applyEffect()
	{
		if (!_isEnabled)
			return;
		
		if (_creatures.isEmpty())
		{
			_task.cancel(true);
			_task = null;
			
			return;
		}
		
		for (Creature temp : _creatures)
		{
			if (temp.isDead() || Rnd.get(100) >= _chance)
				continue;
			
			for (IntIntHolder entry : _skills)
			{
				final L2Skill skill = entry.getSkill();
				
				if (skill != null && skill.checkCondition(temp, temp, false) && temp.getFirstEffect(entry.getId()) == null)
				{
					if (skill.getId() == 4698 && temp instanceof Player)
						continue;
					
					skill.getEffects(temp, temp);
				}
			}
		}
	}
}