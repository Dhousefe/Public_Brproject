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

public class Q077_SagaOfTheDominator extends ThirdClassQuest
{
	public Q077_SagaOfTheDominator()
	{
		super(77, "Saga of the Dominator", ClassId.DOMINATOR);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7100;
		_itemOptional = 0;
		_itemReward = 7539;
		_itemAmulet1st = 7275;
		_itemAmulet2nd = 7306;
		_itemAmulet3rd = 7337;
		_itemAmulet4th = 7368;
		_itemHalishaMark = 7492;
		_itemAmulet5th = 7399;
		_itemAmulet6th = 7430;
		
		_npcMain = 31336;
		_npc1st = 31371;
		_npc2nd = 31624;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31648;
		_npcTablet3rd = 31653;
		_npc3rd = 31290;
		_npcTablet4th = 31654;
		_npc4th = 31290;
		_npcTablet5th = 31655;
		_npcTablet6th = 31656;
		_npcDefender = 31636;
		
		_mobGuardian = 27215;
		_mobCorrupted = 27294;
		_mobHalisha = 27226;
		_mobAttacker = 27262;
		
		_locCorrupted = new SpawnLocation(162898, -76492, -3096, -1);
		_locAttacker = new SpawnLocation(47429, -56923, -2383, -1);
		_locDefender = new SpawnLocation(47391, -56929, -2370, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_7750;
		_msgCorruptedDespawn = NpcStringId.ID_7751;
		_msgCorruptedKill = NpcStringId.ID_7752;
		_msgHalishaSpawn = NpcStringId.ID_7753;
		_msgHalishaDespawn = NpcStringId.ID_7756;
		_msgHalishaKill = NpcStringId.ID_7754;
		_msgHalishaKillOther = NpcStringId.ID_7755;
		_msgAttackerSpawn = NpcStringId.ID_7764;
		_msgAttackerDespawn = NpcStringId.ID_7765;
		_msgAttackerAttack1 = NpcStringId.ID_7766;
		_msgAttackerAttack16 = NpcStringId.ID_7767;
		_msgDefenderSpawn = NpcStringId.ID_7757;
		_msgDefenderDespawnWon = NpcStringId.ID_7761;
		_msgDefenderDespawnLost = NpcStringId.ID_7762;
		_msgDefenderCombat = NpcStringId.ID_7758;
		_msgDefenderCombatIdle1 = NpcStringId.ID_7759;
		_msgDefenderCombatIdle2 = NpcStringId.ID_7760;
		_msgDefenderReward = NpcStringId.ID_7763;
	}
}