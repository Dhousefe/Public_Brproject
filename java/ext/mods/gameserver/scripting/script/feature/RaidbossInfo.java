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
package ext.mods.gameserver.scripting.script.feature;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.spawn.ASpawn;
import ext.mods.gameserver.scripting.Quest;

public class RaidbossInfo extends Quest
{
	private static final int[] NPCs =
	{
		31729,
		31730,
		31731,
		31732,
		31733,
		31734,
		31735,
		31736,
		31737,
		31738,
		31775,
		31776,
		31777,
		31778,
		31779,
		31780,
		31781,
		31782,
		31783,
		31784,
		31785,
		31786,
		31787,
		31788,
		31789,
		31790,
		31791,
		31792,
		31793,
		31794,
		31795,
		31796,
		31797,
		31798,
		31799,
		31800,
		31801,
		31802,
		31803,
		31804,
		31805,
		31806,
		31807,
		31808,
		31809,
		31810,
		31811,
		31812,
		31813,
		31814,
		31815,
		31816,
		31817,
		31818,
		31819,
		31820,
		31821,
		31822,
		31823,
		31824,
		31825,
		31826,
		31827,
		31828,
		31829,
		31830,
		31831,
		31832,
		31833,
		31834,
		31835,
		31836,
		31837,
		31838,
		31839,
		31840,
		31841,
		31991,
		31992,
		31993,
		31994,
		31995
	};
	
	public RaidbossInfo()
	{
		super(-1, "feature");
		
		addTalkId(NPCs);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (!StringUtil.isDigit(event))
			return event;
		
		final int raidId = Integer.parseInt(event);
		
		final ASpawn spawn = SpawnManager.getInstance().getSpawn(raidId);
		if (spawn != null)
			player.getRadarList().addMarker(spawn.getSpawnLocation());
		else
		{
			final Npc raid = World.getInstance().getNpc(raidId);
			if (raid != null)
				player.getRadarList().addMarker(raid.getPosition());
		}
		
		return null;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		return "info.htm";
	}
}