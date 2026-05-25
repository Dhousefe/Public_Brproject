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
package ext.mods.gameserver.scripting.quest;

import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.network.NpcStringId;

public class Q073_SagaOfTheDuelist extends ThirdClassQuest
{
	public Q073_SagaOfTheDuelist()
	{
		super(73, "Saga of the Duelist", ClassId.DUELIST);
	}
	
	@Override
	protected final void setItemsNpcsMobsLocs()
	{
		_itemMain = 7096;
		_itemOptional = 7546;
		_itemReward = 7537;
		_itemAmulet1st = 7271;
		_itemAmulet2nd = 7302;
		_itemAmulet3rd = 7333;
		_itemAmulet4th = 7364;
		_itemHalishaMark = 7488;
		_itemAmulet5th = 7395;
		_itemAmulet6th = 7426;
		
		_npcMain = 30849;
		_npc1st = 31226;
		_npc2nd = 31624;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31647;
		_npcTablet3rd = 31653;
		_npc3rd = 31331;
		_npcTablet4th = 31654;
		_npc4th = 31277;
		_npcTablet5th = 31655;
		_npcTablet6th = 31656;
		_npcDefender = 31639;
		
		_mobGuardian = 27214;
		_mobCorrupted = 27289;
		_mobHalisha = 27222;
		_mobAttacker = 27281;
		
		_locCorrupted = new SpawnLocation(162898, -76492, -3096, -1);
		_locAttacker = new SpawnLocation(47429, -56923, -2383, -1);
		_locDefender = new SpawnLocation(47391, -56929, -2370, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_7350;
		_msgCorruptedDespawn = NpcStringId.ID_7351;
		_msgCorruptedKill = NpcStringId.ID_7352;
		_msgHalishaSpawn = NpcStringId.ID_7353;
		_msgHalishaDespawn = NpcStringId.ID_7356;
		_msgHalishaKill = NpcStringId.ID_7354;
		_msgHalishaKillOther = NpcStringId.ID_7355;
		_msgAttackerSpawn = NpcStringId.ID_7364;
		_msgAttackerDespawn = NpcStringId.ID_7365;
		_msgAttackerAttack1 = NpcStringId.ID_7366;
		_msgAttackerAttack16 = NpcStringId.ID_7367;
		_msgDefenderSpawn = NpcStringId.ID_7357;
		_msgDefenderDespawnWon = NpcStringId.ID_7361;
		_msgDefenderDespawnLost = NpcStringId.ID_7362;
		_msgDefenderCombat = NpcStringId.ID_7358;
		_msgDefenderCombatIdle1 = NpcStringId.ID_7359;
		_msgDefenderCombatIdle2 = NpcStringId.ID_7360;
		_msgDefenderReward = NpcStringId.ID_7363;
	}
}