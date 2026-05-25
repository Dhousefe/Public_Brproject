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

public class Q084_SagaOfTheGhostSentinel extends ThirdClassQuest
{
	public Q084_SagaOfTheGhostSentinel()
	{
		super(84, "Saga of the Ghost Sentinel", ClassId.GHOST_SENTINEL);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7107;
		_itemOptional = 0;
		_itemReward = 7521;
		_itemAmulet1st = 7282;
		_itemAmulet2nd = 7313;
		_itemAmulet3rd = 7344;
		_itemAmulet4th = 7375;
		_itemHalishaMark = 7499;
		_itemAmulet5th = 7406;
		_itemAmulet6th = 7437;
		
		_npcMain = 30702;
		_npc1st = 31604;
		_npc2nd = 31587;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31649;
		_npcTablet3rd = 31652;
		_npc3rd = 31640;
		_npcTablet4th = 31654;
		_npc4th = 31641;
		_npcTablet5th = 31655;
		_npcTablet6th = 31659;
		_npcDefender = 31635;
		
		_mobGuardian = 27216;
		_mobCorrupted = 27298;
		_mobHalisha = 27233;
		_mobAttacker = 27307;
		
		_locCorrupted = new SpawnLocation(161719, -92823, -1893, -1);
		_locAttacker = new SpawnLocation(124376, 82127, -2796, -1);
		_locDefender = new SpawnLocation(124376, 82127, -2796, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_8450;
		_msgCorruptedDespawn = NpcStringId.ID_8451;
		_msgCorruptedKill = NpcStringId.ID_8452;
		_msgHalishaSpawn = NpcStringId.ID_8453;
		_msgHalishaDespawn = NpcStringId.ID_8456;
		_msgHalishaKill = NpcStringId.ID_8454;
		_msgHalishaKillOther = NpcStringId.ID_8455;
		_msgAttackerSpawn = NpcStringId.ID_8464;
		_msgAttackerDespawn = NpcStringId.ID_8465;
		_msgAttackerAttack1 = NpcStringId.ID_8466;
		_msgAttackerAttack16 = NpcStringId.ID_8467;
		_msgDefenderSpawn = NpcStringId.ID_8457;
		_msgDefenderDespawnWon = NpcStringId.ID_8461;
		_msgDefenderDespawnLost = NpcStringId.ID_8462;
		_msgDefenderCombat = NpcStringId.ID_8458;
		_msgDefenderCombatIdle1 = NpcStringId.ID_8459;
		_msgDefenderCombatIdle2 = NpcStringId.ID_8460;
		_msgDefenderReward = NpcStringId.ID_8463;
	}
}