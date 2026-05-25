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
package ext.mods.gameserver.handler.bypasshandlers;

import java.util.ArrayList;
import java.util.List;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.random.Rnd;
import ext.mods.gameserver.enums.GaugeColor;
import ext.mods.gameserver.enums.SpawnType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.zone.type.RandomZone;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.MagicSkillUse;
import ext.mods.gameserver.network.serverpackets.SetupGauge;
import ext.mods.Crypta.RandomManager;

public class FarmZoneTeleportBypass implements IBypassHandler
{
	private static final String[] COMMANDS = { "farm_teleport", "farmzone" };
	private static final int SOE_VISUAL_SKILL_ID = 2036;
	private static final int SOE_VISUAL_SKILL_LEVEL = 1;
	private static final int CAST_TIME_MS = 5000;
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		if (!(target instanceof Npc))
			return false;
		
		if (player.isDead())
		{
			player.sendMessage("Você não pode usar este comando enquanto está morto.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		if (player.isInOlympiadMode())
		{
			player.sendMessage("Você não pode usar este comando durante as Olimpíadas.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		if (player.getDungeon() != null)
		{
			player.sendMessage("Você não pode usar este comando dentro de uma dungeon.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		if (player.isInCombat())
		{
			player.sendMessage("Você não pode usar este comando em combate.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		if (player.getCast().isCastingNow() || player.isTeleporting())
		{
			player.sendMessage("Aguarde sua ação atual terminar.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		if (player.isInsideZone(ZoneId.PVP) || player.isInsideZone(ZoneId.SIEGE))
		{
			player.sendMessage("Você não pode usar este comando desta área.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		Object randomManager = RandomManager.getInstance();
		if (randomManager == null)
		{
			player.sendMessage("Sistema de eventos não disponível.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		Boolean isRunning = (Boolean) RandomManager.getInstance().isEventRunning();
		if (isRunning == null || !isRunning)
		{
			player.sendMessage("Nenhuma Farm Zone ativa no momento.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		@SuppressWarnings("unchecked")
		List<RandomZone> activeZones = (List<RandomZone>) RandomManager.getInstance().getActiveZones();
		
		if (activeZones == null || activeZones.isEmpty())
		{
			player.sendMessage("Nenhuma Farm Zone ativa no momento.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		RandomZone targetZone = Rnd.get(activeZones);
		if (targetZone == null)
		{
			player.sendMessage("Erro ao selecionar zona do evento.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		Location loc = null;
		ext.mods.FarmEventRandom.holder.RandomZoneData zoneData = (ext.mods.FarmEventRandom.holder.RandomZoneData) RandomManager.getInstance().getZoneDataForZone(targetZone);
		if (zoneData != null && zoneData.getSpawnLocation() != null)
		{
			loc = zoneData.getSpawnLocation();
		}
		
		if (loc == null)
		{
			loc = targetZone.getRndSpawn(SpawnType.NORMAL);
		}
		
		if (loc == null)
		{
			@SuppressWarnings("unchecked")
			List<ext.mods.gameserver.model.spawn.Spawn> activeSpawns = (List<ext.mods.gameserver.model.spawn.Spawn>) RandomManager.getInstance().getActiveSpawns();
			
			if (activeSpawns != null && !activeSpawns.isEmpty())
			{
				List<ext.mods.gameserver.model.spawn.Spawn> zoneSpawns = new ArrayList<>();
				for (ext.mods.gameserver.model.spawn.Spawn spawn : activeSpawns)
				{
					if (spawn != null && spawn.getNpc() != null)
					{
						ext.mods.gameserver.model.location.SpawnLocation spawnLoc = spawn.getSpawnLocation();
						if (spawnLoc != null && targetZone.isInsideZone(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ()))
						{
							zoneSpawns.add(spawn);
						}
					}
				}
				
				if (!zoneSpawns.isEmpty())
				{
					ext.mods.gameserver.model.spawn.Spawn selectedSpawn = Rnd.get(zoneSpawns);
					if (selectedSpawn != null && selectedSpawn.getNpc() != null)
					{
						loc = new Location(selectedSpawn.getNpc().getX(), selectedSpawn.getNpc().getY(), selectedSpawn.getNpc().getZ());
					}
				}
			}
		}
		
		if (loc == null)
		{
			Location returnLoc = targetZone.getReturnLocation();
			if (returnLoc != null && returnLoc.getX() != 0 && returnLoc.getY() != 0)
			{
				loc = returnLoc;
			}
		}
		
		if (loc == null)
		{
			player.sendMessage("Não foi possível encontrar um ponto de teleporte na Farm Zone.");
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		final Location finalLoc = loc;
		
		player.broadcastPacket(new MagicSkillUse(player, player, SOE_VISUAL_SKILL_ID, SOE_VISUAL_SKILL_LEVEL, CAST_TIME_MS, 0));
		player.sendPacket(new SetupGauge(GaugeColor.BLUE, CAST_TIME_MS));
		
		ThreadPool.schedule(() ->
		{
			if (player.isDead() || player.isInCombat() || player.isTeleporting())
				return;
			player.teleToLocation(finalLoc);
		}, CAST_TIME_MS);
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}

