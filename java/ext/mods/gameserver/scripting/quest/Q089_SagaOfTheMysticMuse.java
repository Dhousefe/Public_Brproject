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

public class Q089_SagaOfTheMysticMuse extends ThirdClassQuest
{
	public Q089_SagaOfTheMysticMuse()
	{
		super(89, "Saga of the Mystic Muse", ClassId.MYSTIC_MUSE);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7083;
		_itemOptional = 0;
		_itemReward = 7530;
		_itemAmulet1st = 7287;
		_itemAmulet2nd = 7318;
		_itemAmulet3rd = 7349;
		_itemAmulet4th = 7380;
		_itemHalishaMark = 7504;
		_itemAmulet5th = 7411;
		_itemAmulet6th = 7442;
		
		_npcMain = 30174;
		_npc1st = 31283;
		_npc2nd = 31627;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31648;
		_npcTablet3rd = 31651;
		_npc3rd = 31283;
		_npcTablet4th = 31654;
		_npc4th = 31283;
		_npcTablet5th = 31655;
		_npcTablet6th = 31658;
		_npcDefender = 31643;
		
		_mobGuardian = 27215;
		_mobCorrupted = 27251;
		_mobHalisha = 27238;
		_mobAttacker = 27255;
		
		_locCorrupted = new SpawnLocation(119518, -28658, -3811, -1);
		_locAttacker = new SpawnLocation(181227, 36703, -4816, -1);
		_locDefender = new SpawnLocation(181215, 36676, -4812, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_8950;
		_msgCorruptedDespawn = NpcStringId.ID_8951;
		_msgCorruptedKill = NpcStringId.ID_8952;
		_msgHalishaSpawn = NpcStringId.ID_8953;
		_msgHalishaDespawn = NpcStringId.ID_8956;
		_msgHalishaKill = NpcStringId.ID_8954;
		_msgHalishaKillOther = NpcStringId.ID_8955;
		_msgAttackerSpawn = NpcStringId.ID_8964;
		_msgAttackerDespawn = NpcStringId.ID_8965;
		_msgAttackerAttack1 = NpcStringId.ID_8966;
		_msgAttackerAttack16 = NpcStringId.ID_8967;
		_msgDefenderSpawn = NpcStringId.ID_8957;
		_msgDefenderDespawnWon = NpcStringId.ID_8961;
		_msgDefenderDespawnLost = NpcStringId.ID_8962;
		_msgDefenderCombat = NpcStringId.ID_8958;
		_msgDefenderCombatIdle1 = NpcStringId.ID_8959;
		_msgDefenderCombatIdle2 = NpcStringId.ID_8960;
		_msgDefenderReward = NpcStringId.ID_8963;
	}
}