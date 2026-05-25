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
package ext.mods.dressme.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.skills.L2Skill;

import ext.mods.dressme.holder.DressMeHolder;

public class DressMeEffectManager
{
	
	private final Map<Integer, ActiveEffect> _activeEffects = new ConcurrentHashMap<>();
	
	private static class ActiveEffect
	{
		private final int _skillId;
		private final ScheduledFuture<?> _task;
		
		public ActiveEffect(int skillId, ScheduledFuture<?> task)
		{
			_skillId = skillId;
			_task = task;
		}
	}
	
	private DressMeEffectManager()
	{
	}
	
	public void startEffect(Player player, DressMeHolder skin)
	{
		if (player == null || skin.getEffect() == null)
			return;
		
		final int playerId = player.getObjectId();
		final int skillId = skin.getEffect().getSkillId();
		final int skillLevel = skin.getEffect().getLevel();
		final int interval = skin.getEffect().getInterval();
		
		stopEffect(player);
		
		applySkill(player, skillId, skillLevel);
		
		ScheduledFuture<?> task = ThreadPool.scheduleAtFixedRate(() ->
		{
			
			Player checkOnplayer = World.getInstance().getPlayer(player.getObjectId());
			
			if (checkOnplayer != null)
			{
				applySkill(player, skillId, skillLevel);
			}
			else
			{
				stopEffect(player);
			}
			
		}, interval * 1000L, interval * 1000L);
		
		_activeEffects.put(playerId, new ActiveEffect(skillId, task));
	}
	
	public void stopEffect(Player player)
	{
		if (player == null)
			return;
		
		ActiveEffect effect = _activeEffects.remove(player.getObjectId());
		if (effect != null)
		{
			effect._task.cancel(false);
			player.stopSkillEffects(effect._skillId);
		}
	}
	
	private static void applySkill(Player player, int skillId, int skillLevel)
	{
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
		if (skill != null)
		{
			player.broadcastPacketInRadius(new MagicSkillUse(player, player, skill.getId(), 1, 0, 0, false), skill.getSkillRadius());
			
		}
	}
	
	public static DressMeEffectManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final DressMeEffectManager INSTANCE = new DressMeEffectManager();
	}
}