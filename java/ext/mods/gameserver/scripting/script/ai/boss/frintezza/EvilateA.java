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
package ext.mods.gameserver.scripting.script.ai.boss.frintezza;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class EvilateA extends DefaultNpc
{
	public EvilateA()
	{
		super("ai/boss/frintezza");
	}
	
	public EvilateA(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		29049
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("1000", npc, null, 45000);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		final int spawnPosX = getNpcIntAIParam(npc, "SpawnPosX");
		final int spawnPosY = getNpcIntAIParam(npc, "SpawnPosY");
		final int spawnPosZ = getNpcIntAIParam(npc, "SpawnPosZ");
		final int spawnAngle = getNpcIntAIParam(npc, "SpawnAngle");
		
		if (name.equalsIgnoreCase("1000"))
		{
			createOnePrivateEx(npc, 29050, spawnPosX, spawnPosY, spawnPosZ, spawnAngle, 0, true);
			startQuestTimer("1001", npc, null, 20000);
		}
		else if (name.equalsIgnoreCase("1001"))
		{
			createOnePrivateEx(npc, 29050, spawnPosX, spawnPosY, spawnPosZ, spawnAngle, 0, true);
			startQuestTimer("1002", npc, null, 20000);
		}
		else if (name.equalsIgnoreCase("1002"))
		{
			createOnePrivateEx(npc, 29050, spawnPosX, spawnPosY, spawnPosZ, spawnAngle, 0, true);
			startQuestTimer("1003", npc, null, 20000);
		}
		else if (name.equalsIgnoreCase("1003"))
			createOnePrivateEx(npc, 29050, spawnPosX, spawnPosY, spawnPosZ, spawnAngle, 0, true);
		
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (skill != null && skill.getId() == 2276)
			npc.doDie(npc);
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (called != caller && called.isMaster() && called.isDead())
			caller.scheduleRespawn(20000);
	}
}