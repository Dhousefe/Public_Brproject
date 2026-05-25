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
package ext.mods.gameserver.model.actor.container.creature;

import java.util.concurrent.Future;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.effects.EffectFusion;

public final class FusionSkill
{
	protected Creature _caster;
	protected Creature _target;
	
	protected Future<?> _geoCheckTask;
	
	protected int _skillCastRange;
	protected int _fusionId;
	protected int _fusionLevel;
	
	public FusionSkill(Creature caster, Creature target, L2Skill skill)
	{
		_skillCastRange = skill.getCastRange();
		_caster = caster;
		_target = target;
		_fusionId = skill.getTriggeredId();
		_fusionLevel = skill.getTriggeredLevel();
		
		final AbstractEffect effect = _target.getFirstEffect(_fusionId);
		if (effect != null)
			((EffectFusion) effect).increaseEffect();
		else
		{
			final L2Skill force = SkillTable.getInstance().getInfo(_fusionId, _fusionLevel);
			if (force != null)
				force.getEffects(_caster, _target);
		}
		
		_geoCheckTask = ThreadPool.scheduleAtFixedRate(() ->
		{
			if (!_caster.isInStrictRadius(target, _skillCastRange) || !GeoEngine.getInstance().canSeeTarget(_caster, _target))
				_caster.getCast().stop();
		}, 1000, 1000);
	}
	
	public Creature getCaster()
	{
		return _caster;
	}
	
	public Creature getTarget()
	{
		return _target;
	}
	
	public void onCastAbort()
	{
		_caster.setFusionSkill(null);
		
		final AbstractEffect effect = _target.getFirstEffect(_fusionId);
		if (effect != null)
			((EffectFusion) effect).decreaseForce();
		
		_geoCheckTask.cancel(true);
		
		_caster.getAI().notifyEvent(AiEventType.FINISHED_CASTING, null, null);
	}
}