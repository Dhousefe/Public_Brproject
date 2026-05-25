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
package ext.mods.gameserver.scripting.script.ai.boss.core;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class Core extends DefaultNpc
{
	private static final int TELEPORTATION_CUBE = 31842;
	private static final int CORE_DOOR = 20210001;
	
	public Core()
	{
		super("ai/boss/core");
	}
	
	public Core(String descr)
	{
		super(descr);
		addDoorChange(CORE_DOOR);
	}
	
	protected final int[] _npcIds =
	{
		29006
	};
	
	@Override
	public void onDoorChange(Door door)
	{
		if (door.isOpened())
			SpawnManager.getInstance().startSpawnTime("door_open", "[kuruma_parent]", null, null, false);
		else
			SpawnManager.getInstance().stopSpawnTime("door_open", "[kuruma_parent]", null, null, false);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.broadcastPacket(new PlaySound(1, "BS01_A", npc));
		npc._i_ai0 = 0;
		
		createPrivates(npc);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (caller != called && called.isMaster() && !called.isDead())
			caller.scheduleRespawn((280 + Rnd.get(40)) * 1000L);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!Config.RAID_DISABLE_CURSE && attacker.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidCurse = SkillTable.getInstance().getInfo(4515, 1);
			npc.getAI().addCastDesire(attacker, raidCurse, 1000000);
		}
		
		if (npc._i_ai0 == 0)
		{
			npc.broadcastNpcSay(NpcStringId.ID_1000001);
			npc.broadcastNpcSay(NpcStringId.ID_1000002);
			
			npc._i_ai0 = 1;
		}
		else if (Rnd.get(100) < 1)
			npc.broadcastNpcSay(NpcStringId.ID_1000003);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		if (!called.isDead())
		{
			if (called._i_ai0 == 0)
			{
				called.broadcastNpcSay(NpcStringId.ID_1000001);
				called.broadcastNpcSay(NpcStringId.ID_1000002);
				called._i_ai0 = 1;
			}
			else if (Rnd.get(100) < 1)
				called.broadcastNpcSay(NpcStringId.ID_1000003);
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		npc.broadcastPacket(new PlaySound(1, "BS02_D", npc));
		
		npc.broadcastNpcSay(NpcStringId.ID_1000004);
		npc.broadcastNpcSay(NpcStringId.ID_1000005);
		npc.broadcastNpcSay(NpcStringId.ID_1000006);
		
		addSpawn(TELEPORTATION_CUBE, 16502, 110165, -6394, 0, false, 900000, false);
		addSpawn(TELEPORTATION_CUBE, 18948, 110166, -6397, 0, false, 900000, false);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (!Config.RAID_DISABLE_CURSE && caster.getStatus().getLevel() > (npc.getStatus().getLevel() + 8))
		{
			final L2Skill raidMute = SkillTable.getInstance().getInfo(4215, 1);
			
			npc.getAI().addCastDesire(caster, raidMute, 1000000);
			
			return;
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
}