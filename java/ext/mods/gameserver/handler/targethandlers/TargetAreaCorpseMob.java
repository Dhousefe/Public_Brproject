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
package ext.mods.gameserver.handler.targethandlers;

import java.util.ArrayList;
import java.util.List;

import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.handler.ITargetHandler;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.DecayTaskManager;

public class TargetAreaCorpseMob implements ITargetHandler
{
	@Override
	public SkillTargetType getTargetType()
	{
		return SkillTargetType.AREA_CORPSE_MOB;
	}
	
	@Override
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill)
	{
		final List<Creature> list = new ArrayList<>();
		list.add(target);
		
		for (Creature creature : target.getKnownTypeInRadius(Creature.class, skill.getSkillRadius()))
		{
			if (creature == caster || !ext.mods.gameserver.model.actor.move.MovementIntegration.canSeeTarget(target, creature))
				continue;
			
			if (skill.getId() == 444)
			{
				if (creature instanceof Attackable && creature.isDead())
					list.add(creature);
				
				continue;
			}
			else if (creature.isDead())
				continue;
			
			if (caster instanceof Playable playable && (creature instanceof Attackable || creature instanceof Playable))
			{
				if (creature.isAttackableWithoutForceBy(playable))
					list.add(creature);
			}
			else if (caster instanceof Attackable && creature instanceof Playable)
			{
				if (creature.isAttackableBy(caster))
					list.add(creature);
			}
		}
		
		return list.toArray(new Creature[list.size()]);
	}
	
	@Override
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill)
	{
		return target;
	}
	
	@Override
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed)
	{
		final Long time = DecayTaskManager.getInstance().get(target);
		if (time == null || target instanceof Pet)
		{
			caster.sendPacket(SystemMessageId.INVALID_TARGET);
			return false;
		}
		
		if (skill.getSkillType() == SkillType.HARVEST)
		{
			if (!(target instanceof Monster))
			{
				caster.sendPacket(SystemMessageId.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
				return false;
			}
			
			return true;
		}
		
		final NpcTemplate template = (NpcTemplate) target.getTemplate();
		
		final boolean isSeededOrSpoiled = target instanceof Monster targetMonster && (targetMonster.getSeedState().isSeeded() || targetMonster.getSpoilState().isSpoiled());
		if (!isSeededOrSpoiled && System.currentTimeMillis() >= time - (template.getCorpseTime() * 1000 / 2))
		{
			caster.sendPacket(SystemMessageId.CORPSE_TOO_OLD_SKILL_NOT_USED);
			return false;
		}
		
		if (skill.getSkillType() == SkillType.SWEEP && !(target instanceof Monster))
		{
			caster.sendPacket(SystemMessageId.SWEEPER_FAILED_TARGET_NOT_SPOILED);
			return false;
		}
		
		return true;
	}
}