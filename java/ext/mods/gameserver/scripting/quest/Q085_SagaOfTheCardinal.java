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

public class Q085_SagaOfTheCardinal extends ThirdClassQuest
{
	public Q085_SagaOfTheCardinal()
	{
		super(85, "Saga of the Cardinal", ClassId.CARDINAL);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7087;
		_itemOptional = 0;
		_itemReward = 7522;
		_itemAmulet1st = 7283;
		_itemAmulet2nd = 7314;
		_itemAmulet3rd = 7345;
		_itemAmulet4th = 7376;
		_itemHalishaMark = 7500;
		_itemAmulet5th = 7407;
		_itemAmulet6th = 7438;
		
		_npcMain = 30191;
		_npc1st = 31588;
		_npc2nd = 31626;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31647;
		_npcTablet3rd = 31651;
		_npc3rd = 31280;
		_npcTablet4th = 31654;
		_npc4th = 31280;
		_npcTablet5th = 31655;
		_npcTablet6th = 31658;
		_npcDefender = 31644;
		
		_mobGuardian = 27214;
		_mobCorrupted = 27267;
		_mobHalisha = 27234;
		_mobAttacker = 27274;
		
		_locCorrupted = new SpawnLocation(119518, -28658, -3811, -1);
		_locAttacker = new SpawnLocation(181215, 36676, -4812, -1);
		_locDefender = new SpawnLocation(181227, 36703, -4816, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_8550;
		_msgCorruptedDespawn = NpcStringId.ID_8551;
		_msgCorruptedKill = NpcStringId.ID_8552;
		_msgHalishaSpawn = NpcStringId.ID_8553;
		_msgHalishaDespawn = NpcStringId.ID_8556;
		_msgHalishaKill = NpcStringId.ID_8554;
		_msgHalishaKillOther = NpcStringId.ID_8555;
		_msgAttackerSpawn = NpcStringId.ID_8564;
		_msgAttackerDespawn = NpcStringId.ID_8565;
		_msgAttackerAttack1 = NpcStringId.ID_8566;
		_msgAttackerAttack16 = NpcStringId.ID_8567;
		_msgDefenderSpawn = NpcStringId.ID_8557;
		_msgDefenderDespawnWon = NpcStringId.ID_8561;
		_msgDefenderDespawnLost = NpcStringId.ID_8562;
		_msgDefenderCombat = NpcStringId.ID_8558;
		_msgDefenderCombatIdle1 = NpcStringId.ID_8559;
		_msgDefenderCombatIdle2 = NpcStringId.ID_8560;
		_msgDefenderReward = NpcStringId.ID_8563;
	}
}