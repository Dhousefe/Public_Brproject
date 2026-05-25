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

public class Q079_SagaOfTheAdventurer extends ThirdClassQuest
{
	public Q079_SagaOfTheAdventurer()
	{
		super(79, "Saga of the Adventurer", ClassId.ADVENTURER);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7102;
		_itemOptional = 0;
		_itemReward = 7516;
		_itemAmulet1st = 7277;
		_itemAmulet2nd = 7308;
		_itemAmulet3rd = 7339;
		_itemAmulet4th = 7370;
		_itemHalishaMark = 7494;
		_itemAmulet5th = 7401;
		_itemAmulet6th = 7432;
		
		_npcMain = 31603;
		_npc1st = 31579;
		_npc2nd = 31584;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31647;
		_npcTablet3rd = 31651;
		_npc3rd = 31615;
		_npcTablet4th = 31654;
		_npc4th = 31616;
		_npcTablet5th = 31655;
		_npcTablet6th = 31658;
		_npcDefender = 31619;
		
		_mobGuardian = 27214;
		_mobCorrupted = 27299;
		_mobHalisha = 27228;
		_mobAttacker = 27302;
		
		_locCorrupted = new SpawnLocation(119518, -28658, -3811, -1);
		_locAttacker = new SpawnLocation(181205, 36676, -4816, -1);
		_locDefender = new SpawnLocation(181215, 36676, -4812, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_7950;
		_msgCorruptedDespawn = NpcStringId.ID_7951;
		_msgCorruptedKill = NpcStringId.ID_7952;
		_msgHalishaSpawn = NpcStringId.ID_7953;
		_msgHalishaDespawn = NpcStringId.ID_7956;
		_msgHalishaKill = NpcStringId.ID_7954;
		_msgHalishaKillOther = NpcStringId.ID_7955;
		_msgAttackerSpawn = NpcStringId.ID_7964;
		_msgAttackerDespawn = NpcStringId.ID_7965;
		_msgAttackerAttack1 = NpcStringId.ID_7966;
		_msgAttackerAttack16 = NpcStringId.ID_7967;
		_msgDefenderSpawn = NpcStringId.ID_7957;
		_msgDefenderDespawnWon = NpcStringId.ID_7961;
		_msgDefenderDespawnLost = NpcStringId.ID_7962;
		_msgDefenderCombat = NpcStringId.ID_7958;
		_msgDefenderCombatIdle1 = NpcStringId.ID_7959;
		_msgDefenderCombatIdle2 = NpcStringId.ID_7960;
		_msgDefenderReward = NpcStringId.ID_7963;
	}
}