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
package ext.mods.gameserver.model.actor.ai.type;

import java.util.List;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.enums.skills.SkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.TamedBeast;
import ext.mods.gameserver.network.serverpackets.SocialAction;
import ext.mods.gameserver.skills.L2Skill;

public class TamedBeastAI extends AttackableAI<TamedBeast>
{
	private static final int MAX_DISTANCE_FROM_HOME = 13000;
	
	protected static final String[] FOOD_CHAT =
	{
		"Refills! Yeah!",
		"I am such a gluttonous beast, it is embarrassing! Ha ha.",
		"Your cooperative feeling has been getting better and better.",
		"I will help you!",
		"The weather is really good. Wanna go for a picnic?",
		"I really like you! This is tasty...",
		"If you do not have to leave this place, then I can help you.",
		"What can I help you with?",
		"I am not here only for food!",
		"Yam, yam, yam, yam, yam!"
	};
	
	private int _step;
	
	public TamedBeastAI(TamedBeast tamedBeast)
	{
		super(tamedBeast);
	}
	
	@Override
	public void runAI()
	{
		if (++_step % 5 != 0)
			return;
		
		final Player owner = getOwner();
		if (owner == null || !owner.isOnline())
		{
			_actor.deleteMe();
			return;
		}
		
		if (_step > 60)
		{
			if (!_actor.isIn2DRadius(52335, -83086, MAX_DISTANCE_FROM_HOME))
			{
				_actor.deleteMe();
				return;
			}
			
			if (!owner.destroyItemByItemId(_actor.getFoodId(), 1, true))
			{
				_actor.deleteMe();
				return;
			}
			
			_actor.broadcastPacket(new SocialAction(_actor, 2));
			_actor.broadcastNpcSay(Rnd.get(FOOD_CHAT));
			
			_step = 0;
		}
		
		if (owner.isDead())
			return;
		
		final List<L2Skill> skills = _actor.getTemplate().getSkills(NpcSkillType.BUFF1, NpcSkillType.BUFF2, NpcSkillType.BUFF3, NpcSkillType.BUFF4, NpcSkillType.BUFF5);
		skills.removeIf(s -> owner.getFirstEffect(s) != null);
		
		if (skills.size() > 2)
			addCastDesire(owner, Rnd.get(skills), 1000000);
		else
			addFollowDesire(owner, 1000000);
	}
	
	@Override
	protected void onEvtOwnerAttacked(Creature attacker)
	{
		if (getOwner() == null || !getOwner().isOnline())
		{
			_actor.deleteMe();
			return;
		}
		
		if (getOwner().isDead())
			return;
		
		if (Rnd.nextBoolean())
		{
			final L2Skill skill = _actor.getTemplate().getSkill(NpcSkillType.HEAL);
			if (skill != null)
			{
				if (skill.getSkillType() == SkillType.MANARECHARGE || skill.getSkillType() == SkillType.MANAHEAL_PERCENT)
				{
					if (getOwner().getStatus().getMpRatio() < 0.5)
						addCastDesire(getOwner(), skill, 1000000);
				}
				else if (getOwner().getStatus().getHpRatio() < 0.5)
					addCastDesire(getOwner(), skill, 1000000);
			}
		}
		else
		{
			final L2Skill skill = _actor.getTemplate().getSkill(NpcSkillType.DEBUFF);
			if (skill != null && attacker.getFirstEffect(skill) == null)
				addCastDesire(attacker, skill, 1000000);
		}
	}
	
	@Override
	protected void onEvtFinishedCasting()
	{
		if (_nextIntention.isBlank())
			doFollowIntention(getOwner(), false);
		else
			doIntention(_nextIntention);
	}
	
	private Player getOwner()
	{
		return _actor.getOwner();
	}
}