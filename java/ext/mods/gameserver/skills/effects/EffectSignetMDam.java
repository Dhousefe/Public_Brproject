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
package ext.mods.gameserver.skills.effects;

import java.util.ArrayList;

import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.enums.AiEventType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.items.ShotType;
import ext.mods.gameserver.enums.skills.EffectType;
import ext.mods.gameserver.enums.skills.ShieldDefense;
import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.actor.instance.EffectPoint;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.MagicSkillLaunched;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.Formulas;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.l2skills.L2SkillSignetCasttime;

public class EffectSignetMDam extends AbstractEffect
{
	private boolean _srcInArena;
	private int _state = 0;
	private EffectPoint _actor;
	
	public EffectSignetMDam(EffectTemplate template, L2Skill skill, Creature effected, Creature effector)
	{
		super(template, skill, effected, effector);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SIGNET_GROUND;
	}
	
	@Override
	public boolean onStart()
	{
		if (!(_skill instanceof L2SkillSignetCasttime ssc))
			return false;
		
		final NpcTemplate template = NpcData.getInstance().getTemplate(ssc.effectNpcId);
		if (template == null)
			return false;
		
		final EffectPoint effectPoint = new EffectPoint(IdFactory.getInstance().getNextId(), template, getEffector());
		effectPoint.getStatus().setMaxHpMp();
		
		Location worldPosition = null;
		if (getEffector() instanceof Player player && getSkill().getTargetType() == SkillTargetType.GROUND)
			worldPosition = player.getCast().getSignetLocation();
		
		effectPoint.setInvul(true);
		effectPoint.spawnMe((worldPosition != null) ? worldPosition : getEffector().getPosition());
		
		_actor = effectPoint;
		return true;
		
	}
	
	@Override
	public boolean onActionTime()
	{
		switch (_state)
		{
			case 0:
			case 2:
				_state++;
				return true;
			case 1:
				getEffected().broadcastPacket(new MagicSkillLaunched(_actor, getSkill(), new Creature[]
				{
					getEffected()
				}));
				_state++;
				return true;
		}
		
		int mpConsume = getSkill().getMpConsume();
		
		Player caster = (Player) getEffected();
		
		boolean ss = false;
		boolean bss = false;
		
		if (!bss && !ss)
			caster.rechargeShots(false, true);
		
		ArrayList<Creature> targets = new ArrayList<>();
		
		for (Creature creature : _actor.getKnownTypeInRadius(Creature.class, _skill.getSkillRadius(), creature -> !creature.isDead() && !(creature instanceof Door) && !creature.isInsideZone(ZoneId.PEACE)))
		{
			if ((creature == null) || creature == getEffected())
				continue;
			
			if (creature instanceof Attackable || creature instanceof Playable)
			{
				if (creature.isAlikeDead())
					continue;
				
				if (_skill.isOffensive() && !_skill.checkForAreaOffensiveSkill(_actor, creature, true, _srcInArena))
					continue;
				
				if (mpConsume > caster.getStatus().getMp())
				{
					caster.sendPacket(SystemMessageId.SKILL_REMOVED_DUE_LACK_MP);
					return false;
				}
				
				caster.getStatus().reduceMp(mpConsume);
				
				targets.add(creature);
			}
		}
		
		if (targets.size() > 0)
		{
			caster.broadcastPacket(new MagicSkillLaunched(caster, getSkill(), targets.toArray(new Creature[targets.size()])));
			for (Creature target : targets)
			{
				final boolean isCrit = Formulas.calcMCrit(caster, target, getSkill());
				final ShieldDefense sDef = Formulas.calcShldUse(caster, target, getSkill(), false);
				final boolean sps = caster.isChargedShot(ShotType.SPIRITSHOT);
				final boolean bsps = caster.isChargedShot(ShotType.BLESSED_SPIRITSHOT);
				final int damage = (int) Formulas.calcMagicDam(caster, target, getSkill(), sDef, sps, bsps, isCrit);
				
				if (target instanceof Summon)
					target.getStatus().broadcastStatusUpdate();
				
				if (damage > 0)
				{
					Formulas.calcCastBreak(target, damage);
					
					caster.sendDamageMessage(target, damage, isCrit, false, false);
					target.reduceCurrentHp(damage, caster, getSkill());
				}
				target.getAI().notifyEvent(AiEventType.ATTACKED, caster, target);
			}
		}
		return true;
	}
	
	@Override
	public void onExit()
	{
		if (_actor != null)
			_actor.deleteMe();
	}
}