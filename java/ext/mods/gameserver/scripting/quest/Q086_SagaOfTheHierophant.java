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

public class Q086_SagaOfTheHierophant extends ThirdClassQuest
{
	public Q086_SagaOfTheHierophant()
	{
		super(86, "Saga of the Hierophant", ClassId.HIEROPHANT);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7089;
		_itemOptional = 0;
		_itemReward = 7523;
		_itemAmulet1st = 7284;
		_itemAmulet2nd = 7315;
		_itemAmulet3rd = 7346;
		_itemAmulet4th = 7377;
		_itemHalishaMark = 7501;
		_itemAmulet5th = 7408;
		_itemAmulet6th = 7439;
		
		_npcMain = 30191;
		_npc1st = 31588;
		_npc2nd = 31626;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31648;
		_npcTablet3rd = 31652;
		_npc3rd = 31280;
		_npcTablet4th = 31654;
		_npc4th = 31280;
		_npcTablet5th = 31655;
		_npcTablet6th = 31659;
		_npcDefender = 31591;
		
		_mobGuardian = 27215;
		_mobCorrupted = 27269;
		_mobHalisha = 27235;
		_mobAttacker = 27275;
		
		_locCorrupted = new SpawnLocation(161719, -92823, -1893, -1);
		_locAttacker = new SpawnLocation(124355, 82155, -2803, -1);
		_locDefender = new SpawnLocation(124376, 82127, -2796, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_8650;
		_msgCorruptedDespawn = NpcStringId.ID_8651;
		_msgCorruptedKill = NpcStringId.ID_8652;
		_msgHalishaSpawn = NpcStringId.ID_8653;
		_msgHalishaDespawn = NpcStringId.ID_8656;
		_msgHalishaKill = NpcStringId.ID_8654;
		_msgHalishaKillOther = NpcStringId.ID_8655;
		_msgAttackerSpawn = NpcStringId.ID_8664;
		_msgAttackerDespawn = NpcStringId.ID_8665;
		_msgAttackerAttack1 = NpcStringId.ID_8666;
		_msgAttackerAttack16 = NpcStringId.ID_8667;
		_msgDefenderSpawn = NpcStringId.ID_8657;
		_msgDefenderDespawnWon = NpcStringId.ID_8661;
		_msgDefenderDespawnLost = NpcStringId.ID_8662;
		_msgDefenderCombat = NpcStringId.ID_8658;
		_msgDefenderCombatIdle1 = NpcStringId.ID_8659;
		_msgDefenderCombatIdle2 = NpcStringId.ID_8660;
		_msgDefenderReward = NpcStringId.ID_8663;
	}
}