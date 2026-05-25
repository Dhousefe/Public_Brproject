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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.RaidBoss.RaidBossParty;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

public class IceFairySirra extends RaidBossType2
{
	private static final L2Skill DEBUFF_1 = SkillTable.getInstance().getInfo(4480, 1);
	private static final L2Skill DEBUFF_2 = SkillTable.getInstance().getInfo(4481, 1);
	private static final L2Skill DEBUFF_3 = SkillTable.getInstance().getInfo(4482, 1);
	
	public IceFairySirra()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/IceFairySirra");
	}
	
	public IceFairySirra(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29056
	};
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (npc._i_ai0 == 3 && creature instanceof Npc && ((Npc) creature).getNpcId() == 29057 && getAbnormalLevel(creature, DEBUFF_3) <= 0)
			DEBUFF_3.getEffects(npc, creature);
		
		if (!(creature instanceof Playable))
			return;
		
		if (npc.isInMyTerritory())
			npc.getAI().addAttackDesire(creature, 200);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("1005", npc, null, ((34 * 60) * 1000) + 30000);
		
		npc._c_ai0 = null;
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1005"))
		{
			final NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("schuttgart13_npc2314_1m1");
			if (maker0 != null)
				maker0.getMaker().onMakerScriptEvent("10005", maker0, 0, 0);
			
			npc.deleteMe();
		}
		else if (name.equalsIgnoreCase("2003"))
		{
			if (npc._c_ai0 != null)
			{
				final Party party0 = npc._c_ai0.getParty();
				if (party0 != null)
					party0.broadcastOnScreen(10000, NpcStringId.ID_1010643, "30");
			}
			
			startQuestTimer("2004", npc, null, 600000);
		}
		else if (name.equalsIgnoreCase("2004"))
		{
			if (npc._c_ai0 != null)
			{
				final Party party0 = npc._c_ai0.getParty();
				if (party0 != null)
					party0.broadcastOnScreen(10000, NpcStringId.ID_1010643, "20");
			}
			
			startQuestTimer("1012", npc, null, 15 * 60000);
		}
		else if (name.equalsIgnoreCase("1012"))
		{
			if (npc._c_ai0 != null)
			{
				final Party party0 = npc._c_ai0.getParty();
				if (party0 != null)
					party0.broadcastOnScreen(10000, NpcStringId.ID_1121002);
			}
		}
		return null;
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature target, int damage)
	{
		if (Rnd.get(50) < 1)
			called.getAI().addCastDesire(target, getNpcSkillByType(called, NpcSkillType.RANGE_DD_MAGIC_A), 1000000);
		
		super.onPartyAttacked(caller, called, target, damage);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (Rnd.get(50) < 1)
			npc.getAI().addCastDesire(caster, getNpcSkillByType(npc, NpcSkillType.RANGE_DD_MAGIC_A), 1000000);
		
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 11036)
		{
			if (arg1 == 1)
			{
				npc._i_ai0 = 1;
				
				npc.getAI().addCastDesire(npc, DEBUFF_1, 1000000);
			}
			else if (arg1 == 2)
			{
				npc._i_ai0 = 2;
				
				npc.getAI().addCastDesire(npc, DEBUFF_2, 1000000);
			}
			else if (arg1 == 3)
			{
				npc._i_ai0 = 3;
				
				npc.lookNeighbor(600);
			}
			else if (arg1 == 0)
				npc._i_ai0 = 0;
		}
		else if (eventId == 11040)
		{
			npc._c_ai0 = (Creature) World.getInstance().getObject(arg1);
			
			startQuestTimer("2003", npc, null, 300000);
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		NpcMaker maker0 = SpawnManager.getInstance().getNpcMaker("schuttgart13_npc2314_1m1");
		if (maker0 != null)
			maker0.getMaker().onMakerScriptEvent("10025", maker0, 0, 0);
		
		maker0 = SpawnManager.getInstance().getNpcMaker("schuttgart13_npc2314_3m1");
		if (maker0 != null)
			maker0.getMaker().onMakerScriptEvent("10025", maker0, 0, 0);
	}
}