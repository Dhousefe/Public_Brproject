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

public class Q091_SagaOfTheArcanaLord extends ThirdClassQuest
{
	public Q091_SagaOfTheArcanaLord()
	{
		super(91, "Saga of the Arcana Lord", ClassId.ARCANA_LORD);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7110;
		_itemOptional = 0;
		_itemReward = 7604;
		_itemAmulet1st = 7289;
		_itemAmulet2nd = 7320;
		_itemAmulet3rd = 7351;
		_itemAmulet4th = 7382;
		_itemHalishaMark = 7506;
		_itemAmulet5th = 7413;
		_itemAmulet6th = 7444;
		
		_npcMain = 31605;
		_npc1st = 31585;
		_npc2nd = 31622;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31647;
		_npcTablet3rd = 31651;
		_npc3rd = 31608;
		_npcTablet4th = 31654;
		_npc4th = 31608;
		_npcTablet5th = 31655;
		_npcTablet6th = 31658;
		_npcDefender = 31586;
		
		_mobGuardian = 27214;
		_mobCorrupted = 27313;
		_mobHalisha = 27240;
		_mobAttacker = 27310;
		
		_locCorrupted = new SpawnLocation(119518, -28658, -3811, -1);
		_locAttacker = new SpawnLocation(181215, 36676, -4812, -1);
		_locDefender = new SpawnLocation(181227, 36703, -4816, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_9150;
		_msgCorruptedDespawn = NpcStringId.ID_9151;
		_msgCorruptedKill = NpcStringId.ID_9152;
		_msgHalishaSpawn = NpcStringId.ID_9153;
		_msgHalishaDespawn = NpcStringId.ID_9156;
		_msgHalishaKill = NpcStringId.ID_9154;
		_msgHalishaKillOther = NpcStringId.ID_9155;
		_msgAttackerSpawn = NpcStringId.ID_9164;
		_msgAttackerDespawn = NpcStringId.ID_9165;
		_msgAttackerAttack1 = NpcStringId.ID_9166;
		_msgAttackerAttack16 = NpcStringId.ID_9167;
		_msgDefenderSpawn = NpcStringId.ID_9157;
		_msgDefenderDespawnWon = NpcStringId.ID_9161;
		_msgDefenderDespawnLost = NpcStringId.ID_9162;
		_msgDefenderCombat = NpcStringId.ID_9158;
		_msgDefenderCombatIdle1 = NpcStringId.ID_9159;
		_msgDefenderCombatIdle2 = NpcStringId.ID_9160;
		_msgDefenderReward = NpcStringId.ID_9163;
	}
}