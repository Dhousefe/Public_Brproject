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
package ext.mods.gameserver.scripting.script.maker;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.enums.SiegeStatus;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.model.spawn.MultiSpawn;
import ext.mods.gameserver.model.spawn.NpcMaker;
import ext.mods.gameserver.model.spawn.SpawnData;

public class BenomMaker extends DefaultMaker
{
	private ScheduledFuture<?> _controlTowerTask;
	
	public BenomMaker(String name)
	{
		super(name);
	}
	
	@Override
	public void onStart(NpcMaker maker)
	{
		final int castleId = maker.getMakerMemo().getInteger("CastleID", 8);
		
		final Castle castle = CastleManager.getInstance().getCastleById(castleId);
		if (castle != null)
			castle.getSiege().addMakerEvent(maker);
		
		if (maker.getSpawns().size() > 1)
		{
			MultiSpawn def0 = maker.getSpawns().get(0);
			if (maker.increaseSpawnedCount(def0, def0.getTotal()))
				def0.doSpawn(def0.getTotal(), false);
			
			def0 = maker.getSpawns().get(1);
			
			if (castle != null && castle.getSiege().getStatus() != SiegeStatus.REGISTRATION_OPENED)
				def0.loadDBNpcInfo();
		}
	}
	
	@Override
	public void onNpcDBInfo(MultiSpawn ms, SpawnData spawnData, NpcMaker maker)
	{
		if (maker.increaseSpawnedCount(ms, 1))
			if (ms.getSpawnData() != null && !ms.getSpawnData().checkDead())
				if (ms.getSpawnData().checkAlive(ms.getSpawnLocation(), ms.getTemplate().getBaseHpMax(0), ms.getTemplate().getBaseMpMax(0)))
					ms.doSpawn(true);
	}
	
	@Override
	public void onNpcDeleted(Npc npc, MultiSpawn ms, NpcMaker maker)
	{
	}
	
	@Override
	public void onSiegeEvent(Siege siege, NpcMaker maker)
	{
		final List<MultiSpawn> spawnDefs = maker.getSpawns();
		
		switch (siege.getStatus())
		{
			case IN_PROGRESS:
				if (_controlTowerTask != null)
				{
					_controlTowerTask.cancel(false);
					_controlTowerTask = null;
				}
				
				_controlTowerTask = ThreadPool.scheduleAtFixedRate(() ->
				{
					if (siege.getStatus() != SiegeStatus.IN_PROGRESS)
					{
						_controlTowerTask.cancel(false);
						_controlTowerTask = null;
						return;
					}
					
					if (siege.getCastle().getAliveLifeTowerCount() < 2 && maker.getSpawns().size() > 1)
					{
						MultiSpawn def0 = maker.getSpawns().get(1);
						
						Npc c0 = def0.getNpc();
						if (c0 != null)
							c0.sendScriptEvent(10100, 1, 0);
						else
						{
							def0.loadDBNpcInfo();
							return;
						}
						
						def0 = maker.getSpawns().get(0);
						
						c0 = def0.getNpc();
						if (c0 != null)
							c0.sendScriptEvent(10101, 1, 0);
						
						_controlTowerTask.cancel(false);
						_controlTowerTask = null;
					}
					
				}, 0, 30000);
				break;
			
			case REGISTRATION_OPENED:
				if (spawnDefs.size() > 1)
					spawnDefs.get(1).doDelete();
				break;
			
			case REGISTRATION_OVER:
				if (spawnDefs.size() > 1 && maker.increaseSpawnedCount(spawnDefs.get(1), spawnDefs.get(1).getTotal()))
					spawnDefs.get(1).doSpawn(spawnDefs.get(1).getTotal(), true);
				break;
		}
	}
}