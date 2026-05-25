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
package ext.mods.gameserver.scripting.script.ai.siegablehall;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class Agit03Lidia extends DefaultNpc
{
	public Agit03Lidia()
	{
		super("ai/siegeablehall");
	}
	
	public Agit03Lidia(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35629
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.broadcastNpcShout(NpcStringId.ID_1010624);
		
		createOnePrivateEx(npc, 35631, 56619, -27866, 569, 54000, 0, false);
		createOnePrivateEx(npc, 35630, 59282, -26496, 569, 48000, 0, false);
		createOnePrivateEx(npc, 35647, 57905, -27648, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 57905, -27712, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27182, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27232, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27282, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27332, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27382, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27432, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27482, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27532, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27582, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27632, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27682, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27732, 608, 33540, 0, false);
		createOnePrivateEx(npc, 35647, 58233, -27782, 608, 33540, 0, false);
		
		npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_BUFF), 1000000);
		
		startQuestTimer("1001", npc, null, 30000);
		
		npc._i_ai1 = 0;
		
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (npc.isInMyTerritory() && attacker instanceof Playable)
			npc.getAI().addAttackDesire(attacker, (((damage * 1.0) / npc.getStatus().getMaxHp()) / 0.05) * 1000);
		
		if (Rnd.get((15 * 30)) < 1 || (npc.getStatus().getHpRatio() < 0.2 && Rnd.get((15 * 30)) < 1))
			npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.SELF_RANGE_DD_MAGIC), 1000000);
		
		if (npc.distance2D(attacker) > 300 && Rnd.get((25 * 30)) < 1)
			npc.getAI().addCastDesire(attacker, getNpcSkillByType(npc, NpcSkillType.DD_MAGIC), 1000000);
		
		if (!npc.isInMyTerritory())
		{
			npc.removeAllAttackDesire();
			npc.teleportTo(npc.getSpawnLocation(), 0);
		}
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (attacker instanceof Playable)
			called.getAI().addAttackDesire(attacker, (((damage * 1.0) / called.getStatus().getMaxHp()) / 0.05) * 1000);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller.getNpcId() == 35631)
			called._i_ai1++;
		
		if (caller.getNpcId() == 35630)
			called._i_ai1++;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			npc.getAI().addCastDesire(npc, getNpcSkillByType(npc, NpcSkillType.SELF_BUFF), 1000000);
			
			if (npc._i_ai1 < 2)
				startQuestTimer("1001", npc, player, 30000);
		}
		
		return null;
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		npc.broadcastNpcShout(NpcStringId.ID_1010638);
		
	}
}