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
package ext.mods.gameserver.scripting;

import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.MakerSpawnTime;
import ext.mods.gameserver.enums.PeriodType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.enums.SiegeStatus;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.model.residence.clanhall.SiegableHall;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.model.spawn.SpawnData;

public abstract class SpawnMaker
{
	private final String _makerName;
	
	protected SpawnMaker(String name)
	{
		_makerName = name;
	}
	
	public String getMakerName()
	{
		return _makerName;
	}
	
	public void onStart(NpcMaker maker)
	{
	}
	
	public void onNpcCreated(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
	}
	
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
	}
	
	public void onNpcDBInfo(MultiSpawn ms, SpawnData spawnData, NpcMaker maker)
	{
	}
	
	public void onMakerScriptEvent(String name, NpcMaker maker, int int1, int int2)
	{
	}
	
	public void onSiegeEvent(Siege siege, NpcMaker maker)
	{
	}
	
	public void onDoorEvent(Door door, NpcMaker maker)
	{
	}
	
	public void onTimer(String name, NpcMaker maker)
	{
	}
	
	public boolean checkHasSpawnCondition(NpcMaker maker)
	{
		final String eventName = maker.getEvent();
		if (eventName != null)
		{
			final PeriodType ssqPeriod = SevenSignsManager.getInstance().getCurrentPeriod();
			final CabalType cabalWon = SevenSignsManager.getInstance().getWinningCabal();
			final CabalType sealOwner1 = SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE);
			final CabalType sealOwner2 = SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS);
			
			switch (eventName)
			{
				case "ssq_seal1_none":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner1 == CabalType.NORMAL || sealOwner1 != cabalWon);
				
				case "ssq_seal1_dawn":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner1 == CabalType.DAWN && sealOwner1 == cabalWon);
				
				case "ssq_seal1_twilight":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner1 == CabalType.DUSK && sealOwner1 == cabalWon);
				
				case "ssq_seal2_none":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner2 == CabalType.NORMAL || sealOwner2 != cabalWon);
				
				case "ssq_seal2_dawn":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner2 == CabalType.DAWN && sealOwner2 == cabalWon);
				
				case "ssq_seal2_twilight":
					if (ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION)
						return true;
					
					return !(sealOwner2 == CabalType.DUSK && sealOwner2 == cabalWon);
				
				case "ssq_event":
					return !(ssqPeriod == PeriodType.RECRUITING || ssqPeriod == PeriodType.COMPETITION);
				
				default:
					return !ArraysUtil.contains(Config.SPAWN_EVENTS, eventName);
			}
		}
		
		final MakerSpawnTime spawnTime = maker.getMakerSpawnTime();
		if (spawnTime == null)
			return false;
		
		final String[] spawnTimeParams = maker.getMakerSpawnTimeParams();
		if (spawnTimeParams == null)
			return false;
		
		SiegableHall sh = null;
		Castle cst = null;
		
		switch (spawnTime)
		{
			case AGIT_BR_START, AGIT_FINAL_START:
				return true;
			
			case AGIT_DEF_START, AGIT_ATK_START:
				sh = (SiegableHall) ClanHallManager.getInstance().getClanHall(Integer.parseInt(spawnTimeParams[0]));
				return sh.getSiegeStatus() != SiegeStatus.IN_PROGRESS;
			
			case SIEGE_START:
				cst = CastleManager.getInstance().getCastleById(Integer.parseInt(spawnTimeParams[0]));
				return !(cst.getSiegeZone().isActive() && cst.getOwnerId() == 0);
			
			case PC_SIEGE_START:
				cst = CastleManager.getInstance().getCastleById(Integer.parseInt(spawnTimeParams[0]));
				return !(cst.getSiegeZone().isActive() && cst.getOwnerId() > 0);
			
			case DOOR_OPEN:
				if (spawnTimeParams[0].equalsIgnoreCase("[kuruma_parent]"))
					return !DoorData.getInstance().getDoor(20210001).isOpened();
				
			default:
				return true;
		}
	}
}