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

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class UndeadBandmaster extends DefaultNpc
{
	
	private static final int SOUL_BREAKING_ARROW = 8192;
	
	public UndeadBandmaster()
	{
		super("ai/boss/frintezza");
	}
	
	public UndeadBandmaster(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		18334
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		if (getNpcIntAIParam(npc, "Shoutman") == 1)
			npc.broadcastNpcShout(NpcStringId.ID_1010644);
		
		npc._i_ai0 = 1;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Rnd.get(100) < 33)
			broadcastScriptEventEx(npc, 0, 10033, attacker.getObjectId(), 1500);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10032)
		{
			if (arg1 == 0 || arg1 == 2 || arg1 == 4 || arg1 == 6)
				npc._i_ai0 = arg1;
		}
		
		if (getNpcIntAIParam(npc, "Shoutman") == 1)
		{
			if (arg1 == 10042)
				npc.broadcastNpcShout(NpcStringId.ID_1010645);
			else if (arg1 == 10043)
				npc.broadcastNpcShout(NpcStringId.ID_1010646);
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (npc._i_ai0 == 0 || npc._i_ai0 == 2 || npc._i_ai0 == 4 || npc._i_ai0 == 6)
			npc.dropItem(killer, SOUL_BREAKING_ARROW, 1);
	}
}