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
package ext.mods.gameserver.scripting.script.ai.siegablehall.BloodyLordNurka1;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class BloodyLordNurka1 extends DefaultNpc
{
	private static final L2Skill DEBUFF = SkillTable.getInstance().getInfo(5456, 1);
	private static final L2Skill DD_MAGIC = SkillTable.getInstance().getInfo(4042, 1);
	
	public BloodyLordNurka1()
	{
		super("ai/siegeablehall/BloodyLordNurka1");
	}
	
	public BloodyLordNurka1(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35368
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.getSpawn().instantTeleportInMyTerritory(51952, 111060, -1970, 200);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker.getStatus().getLevel() > npc.getStatus().getLevel() + 8 && getAbnormalLevel(attacker, DEBUFF) == -1)
		{
			npc.getAI().addCastDesireHold(attacker, DEBUFF, 1000000);
			npc.getAI().getAggroList().stopHate(attacker);
		}
		if (attacker instanceof Playable)
		{
			if (Rnd.get(100) < 10)
				npc.getAI().addCastDesireHold(attacker, DD_MAGIC, 1000000);
			
			npc.getAI().addAttackDesireHold(attacker, (((damage * 1.0) / npc.getStatus().getMaxHp()) / 0.050000) * 10000);
		}
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
		{
			if (Rnd.get(100) < 2)
				called.getAI().addCastDesireHold(attacker, DD_MAGIC, 1000000);
			
			called.getAI().addAttackDesireHold(attacker, (((damage * 1.0) / called.getStatus().getMaxHp()) / 0.050000) * 5000);
		}
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (skill.getAggroPoints() > 0 && Rnd.get(100) < 15)
			npc.getAI().addCastDesireHold(caster, DD_MAGIC, 1000000);
	}
}