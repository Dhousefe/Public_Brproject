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

public class Q096_SagaOfTheSpectralDancer extends ThirdClassQuest
{
	public Q096_SagaOfTheSpectralDancer()
	{
		super(96, "Saga of the Spectral Dancer", ClassId.SPECTRAL_DANCER);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7092;
		_itemOptional = 0;
		_itemReward = 7527;
		_itemAmulet1st = 7294;
		_itemAmulet2nd = 7325;
		_itemAmulet3rd = 7356;
		_itemAmulet4th = 7387;
		_itemHalishaMark = 7511;
		_itemAmulet5th = 7418;
		_itemAmulet6th = 7449;
		
		_npcMain = 31582;
		_npc1st = 31284;
		_npc2nd = 31623;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31649;
		_npcTablet3rd = 31653;
		_npc3rd = 31284;
		_npcTablet4th = 31654;
		_npc4th = 31284;
		_npcTablet5th = 31655;
		_npcTablet6th = 31656;
		_npcDefender = 31611;
		
		_mobGuardian = 27216;
		_mobCorrupted = 27272;
		_mobHalisha = 27245;
		_mobAttacker = 27264;
		
		_locCorrupted = new SpawnLocation(162898, -76492, -3096, -1);
		_locAttacker = new SpawnLocation(47429, -56923, -2383, -1);
		_locDefender = new SpawnLocation(47391, -56929, -2370, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_9650;
		_msgCorruptedDespawn = NpcStringId.ID_9651;
		_msgCorruptedKill = NpcStringId.ID_9652;
		_msgHalishaSpawn = NpcStringId.ID_9653;
		_msgHalishaDespawn = NpcStringId.ID_9656;
		_msgHalishaKill = NpcStringId.ID_9654;
		_msgHalishaKillOther = NpcStringId.ID_9655;
		_msgAttackerSpawn = NpcStringId.ID_9664;
		_msgAttackerDespawn = NpcStringId.ID_9665;
		_msgAttackerAttack1 = NpcStringId.ID_9666;
		_msgAttackerAttack16 = NpcStringId.ID_9667;
		_msgDefenderSpawn = NpcStringId.ID_9657;
		_msgDefenderDespawnWon = NpcStringId.ID_9661;
		_msgDefenderDespawnLost = NpcStringId.ID_9662;
		_msgDefenderCombat = NpcStringId.ID_9658;
		_msgDefenderCombatIdle1 = NpcStringId.ID_9659;
		_msgDefenderCombatIdle2 = NpcStringId.ID_9660;
		_msgDefenderReward = NpcStringId.ID_9663;
	}
}