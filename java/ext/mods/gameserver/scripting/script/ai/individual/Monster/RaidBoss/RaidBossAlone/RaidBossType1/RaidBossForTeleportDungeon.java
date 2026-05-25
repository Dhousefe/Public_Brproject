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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.RaidBoss.RaidBossAlone.RaidBossType1;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

public class RaidBossForTeleportDungeon extends RaidBossType1
{
	public RaidBossForTeleportDungeon()
	{
		super("ai/individual/Monster/RaidBoss/RaidBossAlone/RaidBossType1/RaidBossForTeleportDungeon");
	}
	
	public RaidBossForTeleportDungeon(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		25333,
		25334,
		25335,
		25336,
		25337,
		25338
	};
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!npc.isInMyTerritory())
		{
			return;
		}
		
		npc._i_ai0 = GameTimeTaskManager.getInstance().getCurrentTick();
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimerAtFixedRate("2003", npc, null, 180000, 60000);
		
		final NpcMaker maker0 = ((MultiSpawn) npc.getSpawn()).getNpcMaker();
		maker0.getMaker().onMakerScriptEvent("2", maker0, 0, 0);
		
		npc._i_ai0 = GameTimeTaskManager.getInstance().getCurrentTick();
		
		super.onCreated(npc);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final NpcMaker maker0 = ((MultiSpawn) npc.getSpawn()).getNpcMaker();
		maker0.getMaker().onMakerScriptEvent("3", maker0, 0, 0);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2003"))
		{
			final NpcMaker maker0 = ((MultiSpawn) npc.getSpawn()).getNpcMaker();
			
			if (getElapsedTicks(npc._i_ai0) > (60 * 3))
				maker0.getMaker().onMakerScriptEvent("3", maker0, 0, 0);
			else
				maker0.getMaker().onMakerScriptEvent("2", maker0, 0, 0);
		}
		return super.onTimer(name, npc, player);
	}
}