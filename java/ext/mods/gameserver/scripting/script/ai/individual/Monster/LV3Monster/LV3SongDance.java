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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.LV3Monster;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class LV3SongDance extends LV3Monster
{
	public LV3SongDance()
	{
		super("ai/individual/Monster/LV3Monster");
	}
	
	public LV3SongDance(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		27269,
		27270,
		27272,
		27288
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 0;
		npc._i_ai1 = 0;
		npc._i_ai2 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (damage == 0)
				damage = 1;
			
			npc.getAI().addAttackDesire(attacker, ((1.000000 * damage) / (npc.getStatus().getLevel() + 7)) * 100);
		}
		
		if (Rnd.get(100) < 33 && npc._i_ai0 == 0)
			npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DEBUFF1), 1000000);
		
		if (Rnd.get(100) < 33 && npc._i_ai1 == 0)
			npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DEBUFF2), 1000000);
		
		if (Rnd.get(100) < 33 && npc._i_ai2 == 0)
			npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DEBUFF3), 1000000);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill == getNpcSkillByType(npc, NpcSkillType.DEBUFF1))
			npc._i_ai0 = 1;
		
		if (skill == getNpcSkillByType(npc, NpcSkillType.DEBUFF2))
			npc._i_ai1 = 1;
		
		if (skill == getNpcSkillByType(npc, NpcSkillType.DEBUFF3))
			npc._i_ai2 = 1;
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (caster != null)
		{
			if (caster.getObjectId() == npc._param2)
			{
				if (skill.getEffectId() == getNpcSkillByType(npc, NpcSkillType.DEBUFF1).getEffectId())
					npc._i_ai0 = 0;
				
				if (skill.getEffectId() == getNpcSkillByType(npc, NpcSkillType.DEBUFF2).getEffectId())
					npc._i_ai1 = 0;
				
				if (skill.getEffectId() == getNpcSkillByType(npc, NpcSkillType.DEBUFF3).getEffectId())
					npc._i_ai2 = 0;
			}
		}
		
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}