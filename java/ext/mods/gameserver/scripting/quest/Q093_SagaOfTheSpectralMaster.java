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

public class Q093_SagaOfTheSpectralMaster extends ThirdClassQuest
{
	public Q093_SagaOfTheSpectralMaster()
	{
		super(93, "Saga of the Spectral Master", ClassId.SPECTRAL_MASTER);
	}
	
	@Override
	protected void setItemsNpcsMobsLocs()
	{
		_itemMain = 7112;
		_itemOptional = 0;
		_itemReward = 7606;
		_itemAmulet1st = 7291;
		_itemAmulet2nd = 7322;
		_itemAmulet3rd = 7353;
		_itemAmulet4th = 7384;
		_itemHalishaMark = 7508;
		_itemAmulet5th = 7415;
		_itemAmulet6th = 7446;
		
		_npcMain = 30175;
		_npc1st = 31613;
		_npc2nd = 31287;
		_npcTablet1st = 31646;
		_npcTablet2nd = 31649;
		_npcTablet3rd = 31653;
		_npc3rd = 30175;
		_npcTablet4th = 31654;
		_npc4th = 31613;
		_npcTablet5th = 31655;
		_npcTablet6th = 31656;
		_npcDefender = 31632;
		
		_mobGuardian = 27216;
		_mobCorrupted = 27315;
		_mobHalisha = 27242;
		_mobAttacker = 27312;
		
		_locCorrupted = new SpawnLocation(162898, -76492, -3096, -1);
		_locAttacker = new SpawnLocation(47429, -56923, -2383, -1);
		_locDefender = new SpawnLocation(47391, -56929, -2370, -1);
		
		_msgCorruptedSpawn = NpcStringId.ID_9350;
		_msgCorruptedDespawn = NpcStringId.ID_9351;
		_msgCorruptedKill = NpcStringId.ID_9352;
		_msgHalishaSpawn = NpcStringId.ID_9353;
		_msgHalishaDespawn = NpcStringId.ID_9356;
		_msgHalishaKill = NpcStringId.ID_9354;
		_msgHalishaKillOther = NpcStringId.ID_9355;
		_msgAttackerSpawn = NpcStringId.ID_9364;
		_msgAttackerDespawn = NpcStringId.ID_9365;
		_msgAttackerAttack1 = NpcStringId.ID_9366;
		_msgAttackerAttack16 = NpcStringId.ID_9367;
		_msgDefenderSpawn = NpcStringId.ID_9357;
		_msgDefenderDespawnWon = NpcStringId.ID_9361;
		_msgDefenderDespawnLost = NpcStringId.ID_9362;
		_msgDefenderCombat = NpcStringId.ID_9358;
		_msgDefenderCombatIdle1 = NpcStringId.ID_9359;
		_msgDefenderCombatIdle2 = NpcStringId.ID_9360;
		_msgDefenderReward = NpcStringId.ID_9363;
	}
}