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

public class Q080_SagaOfTheWindRider extends ThirdClassQuest
{
	public Q080_SagaOfTheWindRider()
	{
		super(80, "Saga of the Wind Rider", ClassId.WIND_RIDER);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7103;
		_itemOptional = 0;
		_itemReward = 7517;
		_itemAmulet1st = 7278;
		_itemAmulet2nd = 7309;
		_itemAmulet3rd = 7340;
		_itemAmulet4th = 7371;
		_itemHalishaMark = 7495;
		_itemAmulet5th = 7402;
		_itemAmulet6th = 7433;
		
		_npcMain = 31603;
		_npc1st = 31284;
		_npc2nd = 31624;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31648;
		_npcTablet3rd = 31652;
		_npc3rd = 31615;
		_npcTablet4th = 31654;
		_npc4th = 31616;
		_npcTablet5th = 31655;
		_npcTablet6th = 31659;
		_npcDefender = 31612;
		
		_mobGuardian = 27215;
		_mobCorrupted = 27300;
		_mobHalisha = 27229;
		_mobAttacker = 27303;
		
		_locCorrupted = new SpawnLocation(161719, -92823, -1893, -1);
		_locAttacker = new SpawnLocation(124314, 82155, -2803, -1);
		_locDefender = new SpawnLocation(124355, 82155, -2803, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_8050;
		_msgCorruptedDespawn = NpcStringId.ID_8051;
		_msgCorruptedKill = NpcStringId.ID_8052;
		_msgHalishaSpawn = NpcStringId.ID_8053;
		_msgHalishaDespawn = NpcStringId.ID_8056;
		_msgHalishaKill = NpcStringId.ID_8054;
		_msgHalishaKillOther = NpcStringId.ID_8055;
		_msgAttackerSpawn = NpcStringId.ID_8064;
		_msgAttackerDespawn = NpcStringId.ID_8065;
		_msgAttackerAttack1 = NpcStringId.ID_8066;
		_msgAttackerAttack16 = NpcStringId.ID_8067;
		_msgDefenderSpawn = NpcStringId.ID_8057;
		_msgDefenderDespawnWon = NpcStringId.ID_8061;
		_msgDefenderDespawnLost = NpcStringId.ID_8062;
		_msgDefenderCombat = NpcStringId.ID_8058;
		_msgDefenderCombatIdle1 = NpcStringId.ID_8059;
		_msgDefenderCombatIdle2 = NpcStringId.ID_8060;
		_msgDefenderReward = NpcStringId.ID_8063;
	}
}