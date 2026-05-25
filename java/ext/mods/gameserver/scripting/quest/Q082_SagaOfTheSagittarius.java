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

public class Q082_SagaOfTheSagittarius extends ThirdClassQuest
{
	public Q082_SagaOfTheSagittarius()
	{
		super(82, "Saga of the Sagittarius", ClassId.SAGGITARIUS);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7105;
		_itemOptional = 0;
		_itemReward = 7519;
		_itemAmulet1st = 7280;
		_itemAmulet2nd = 7311;
		_itemAmulet3rd = 7342;
		_itemAmulet4th = 7373;
		_itemHalishaMark = 7497;
		_itemAmulet5th = 7404;
		_itemAmulet6th = 7435;
		
		_npcMain = 30702;
		_npc1st = 31604;
		_npc2nd = 31627;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31647;
		_npcTablet3rd = 31650;
		_npc3rd = 31640;
		_npcTablet4th = 31654;
		_npc4th = 31641;
		_npcTablet5th = 31655;
		_npcTablet6th = 31657;
		_npcDefender = 31633;
		
		_mobGuardian = 27214;
		_mobCorrupted = 27296;
		_mobHalisha = 27231;
		_mobAttacker = 27305;
		
		_locCorrupted = new SpawnLocation(191046, -40640, -3042, -1);
		_locAttacker = new SpawnLocation(46066, -36396, -1685, -1);
		_locDefender = new SpawnLocation(46066, -36396, -1685, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_8250;
		_msgCorruptedDespawn = NpcStringId.ID_8251;
		_msgCorruptedKill = NpcStringId.ID_8252;
		_msgHalishaSpawn = NpcStringId.ID_8253;
		_msgHalishaDespawn = NpcStringId.ID_8256;
		_msgHalishaKill = NpcStringId.ID_8254;
		_msgHalishaKillOther = NpcStringId.ID_8255;
		_msgAttackerSpawn = NpcStringId.ID_8264;
		_msgAttackerDespawn = NpcStringId.ID_8265;
		_msgAttackerAttack1 = NpcStringId.ID_8266;
		_msgAttackerAttack16 = NpcStringId.ID_8267;
		_msgDefenderSpawn = NpcStringId.ID_8257;
		_msgDefenderDespawnWon = NpcStringId.ID_8261;
		_msgDefenderDespawnLost = NpcStringId.ID_8262;
		_msgDefenderCombat = NpcStringId.ID_8258;
		_msgDefenderCombatIdle1 = NpcStringId.ID_8259;
		_msgDefenderCombatIdle2 = NpcStringId.ID_8260;
		_msgDefenderReward = NpcStringId.ID_8263;
	}
}