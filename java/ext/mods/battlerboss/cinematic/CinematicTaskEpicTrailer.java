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
package ext.mods.battlerboss.cinematic;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NormalCamera;
import ext.mods.gameserver.network.serverpackets.SpecialCamera;
import ext.mods.gameserver.network.serverpackets.StopMove;

public class CinematicTaskEpicTrailer
{
	private record CameraStep(int dist, int yaw, int pitch, int time, int duration)
	{
	}
	
	public static void playNpcCinematic(Npc boss, Player player)
	{
		int objId = boss.getObjectId();
		
		boss.setIsParalyzed(true);
		boss.broadcastPacket(new StopMove(boss));
		player.setInvul(true);
		player.setIsImmobilized(true);
		
		CameraStep[] steps = new CameraStep[]
		{
			new CameraStep(100, 180, 30, 3000, 5000),
			new CameraStep(150, 270, 25, 3000, 5000),
			new CameraStep(160, 360, 20, 3000, 5000),
			new CameraStep(160, 450, 10, 3000, 5000),
			new CameraStep(160, 560, 0, 7000, 10000),
			new CameraStep(70, 560, 0, 500, 1000)
		};
		
		runStep(player, boss, objId, steps, 0);
	}
	
	private static void runStep(Player player, Npc boss, int objId, CameraStep[] steps, int index)
	{
		if (index >= steps.length)
		{
			player.sendPacket(NormalCamera.STATIC_PACKET);
			boss.setIsParalyzed(false);
			boss.setTarget(player);
			boss.getAttack().doAttack(player);
			player.setInvul(false);
			player.setIsImmobilized(false);
			return;
		}
		
		CameraStep step = steps[index];
		boss.broadcastPacket(new SpecialCamera(objId, step.dist(), step.yaw(), step.pitch(), step.time(), step.duration(), 0, 0, 1, 0));
		ThreadPool.schedule(() -> runStep(player, boss, objId, steps, index + 1), step.duration());
	}
}
