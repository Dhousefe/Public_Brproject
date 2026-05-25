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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.RaidBoss.RaidBossAlone.RaidBossType1.RaidBossType1Aggressive;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.network.serverpackets.SpecialCamera;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class DrChaosGiganticGolem extends RaidBossType1Aggressive
{
	public DrChaosGiganticGolem()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/RaidBossType1/RaidBossType1Aggressive");
	}
	
	public DrChaosGiganticGolem(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		25512
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("1001", npc, null, 1000);
		
		npc._i_ai1 = GameTimeTaskManager.getInstance().getCurrentTick();
		npc._i_ai2 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1001"))
		{
			npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 30, -200, 20, 6000, 8000, 0, 0, 0, 0));
			
			startQuestTimer("1002", npc, null, 10000);
		}
		else if (name.equalsIgnoreCase("1002"))
			npc._i_ai2 = 1;
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		npc._i_ai1 = GameTimeTaskManager.getInstance().getCurrentTick();
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		npc._i_ai1 = GameTimeTaskManager.getInstance().getCurrentTick();
		
		if (!(creature instanceof Playable))
			return;
		
		if (npc._i_ai2 == 1)
			npc.getAI().addAttackDesire(creature, 200);
		
		super.onSeeCreature(npc, creature);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
		if (getElapsedTicks(npc._i_ai1) > 1200)
		{
			npc.broadcastNpcSay(NpcStringId.ID_1010582);
			
			if (npc.hasMaster())
				npc.getMaster().sendScriptEvent(10029, 0, 0);
			
			npc.deleteMe();
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		npc.broadcastNpcSay(NpcStringId.ID_1010583);
		
		if (npc.hasMaster())
			npc.getMaster().sendScriptEvent(10029, 0, 0);
	}
}