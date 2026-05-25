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

public class Q076_SagaOfTheGrandKhavatari extends ThirdClassQuest
{
	public Q076_SagaOfTheGrandKhavatari()
	{
		super(76, "Saga of the Grand Khavatari", ClassId.GRAND_KHAVATARI);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7099;
		_itemOptional = 0;
		_itemReward = 7539;
		_itemAmulet1st = 7274;
		_itemAmulet2nd = 7305;
		_itemAmulet3rd = 7336;
		_itemAmulet4th = 7367;
		_itemHalishaMark = 7491;
		_itemAmulet5th = 7398;
		_itemAmulet6th = 7429;
		
		_npcMain = 31339;
		_npc1st = 31589;
		_npc2nd = 31624;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31647;
		_npcTablet3rd = 31652;
		_npc3rd = 31290;
		_npcTablet4th = 31654;
		_npc4th = 31290;
		_npcTablet5th = 31655;
		_npcTablet6th = 31659;
		_npcDefender = 31637;
		
		_mobGuardian = 27214;
		_mobCorrupted = 27293;
		_mobHalisha = 27226;
		_mobAttacker = 27284;
		
		_locCorrupted = new SpawnLocation(161719, -92823, -1893, -1);
		_locAttacker = new SpawnLocation(124355, 82155, -2803, -1);
		_locDefender = new SpawnLocation(124376, 82127, -2796, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_7650;
		_msgCorruptedDespawn = NpcStringId.ID_7651;
		_msgCorruptedKill = NpcStringId.ID_7652;
		_msgHalishaSpawn = NpcStringId.ID_7653;
		_msgHalishaDespawn = NpcStringId.ID_7656;
		_msgHalishaKill = NpcStringId.ID_7654;
		_msgHalishaKillOther = NpcStringId.ID_7655;
		_msgAttackerSpawn = NpcStringId.ID_7664;
		_msgAttackerDespawn = NpcStringId.ID_7665;
		_msgAttackerAttack1 = NpcStringId.ID_7666;
		_msgAttackerAttack16 = NpcStringId.ID_7667;
		_msgDefenderSpawn = NpcStringId.ID_7657;
		_msgDefenderDespawnWon = NpcStringId.ID_7661;
		_msgDefenderDespawnLost = NpcStringId.ID_7662;
		_msgDefenderCombat = NpcStringId.ID_7658;
		_msgDefenderCombatIdle1 = NpcStringId.ID_7659;
		_msgDefenderCombatIdle2 = NpcStringId.ID_7660;
		_msgDefenderReward = NpcStringId.ID_7663;
	}
}