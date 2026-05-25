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

public class Q090_SagaOfTheStormScreamer extends ThirdClassQuest
{
	public Q090_SagaOfTheStormScreamer()
	{
		super(90, "Saga of the Storm Screamer", ClassId.STORM_SCREAMER);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7084;
		_itemOptional = 0;
		_itemReward = 7531;
		_itemAmulet1st = 7288;
		_itemAmulet2nd = 7319;
		_itemAmulet3rd = 7350;
		_itemAmulet4th = 7381;
		_itemHalishaMark = 7505;
		_itemAmulet5th = 7412;
		_itemAmulet6th = 7443;
		
		_npcMain = 30175;
		_npc1st = 31287;
		_npc2nd = 31627;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31649;
		_npcTablet3rd = 31652;
		_npc3rd = 31287;
		_npcTablet4th = 31654;
		_npc4th = 31287;
		_npcTablet5th = 31655;
		_npcTablet6th = 31659;
		_npcDefender = 31598;
		
		_mobGuardian = 27216;
		_mobCorrupted = 27252;
		_mobHalisha = 27239;
		_mobAttacker = 27256;
		
		_locCorrupted = new SpawnLocation(161719, -92823, -1893, -1);
		_locAttacker = new SpawnLocation(124376, 82127, -2796, -1);
		_locDefender = new SpawnLocation(124355, 82155, -2803, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_9050;
		_msgCorruptedDespawn = NpcStringId.ID_9051;
		_msgCorruptedKill = NpcStringId.ID_9052;
		_msgHalishaSpawn = NpcStringId.ID_9053;
		_msgHalishaDespawn = NpcStringId.ID_9056;
		_msgHalishaKill = NpcStringId.ID_9054;
		_msgHalishaKillOther = NpcStringId.ID_9055;
		_msgAttackerSpawn = NpcStringId.ID_9064;
		_msgAttackerDespawn = NpcStringId.ID_9065;
		_msgAttackerAttack1 = NpcStringId.ID_9066;
		_msgAttackerAttack16 = NpcStringId.ID_9067;
		_msgDefenderSpawn = NpcStringId.ID_9057;
		_msgDefenderDespawnWon = NpcStringId.ID_9061;
		_msgDefenderDespawnLost = NpcStringId.ID_9062;
		_msgDefenderCombat = NpcStringId.ID_9058;
		_msgDefenderCombatIdle1 = NpcStringId.ID_9059;
		_msgDefenderCombatIdle2 = NpcStringId.ID_9060;
		_msgDefenderReward = NpcStringId.ID_9063;
	}
}