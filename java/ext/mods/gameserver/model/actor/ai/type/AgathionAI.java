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
package ext.mods.gameserver.model.actor.ai.type;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.model.actor.instance.Agathion;
import ext.mods.gameserver.geoengine.GeoEngine;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.network.serverpackets.MoveToLocation;
import ext.mods.gameserver.network.serverpackets.MoveToPawn;

public class AgathionAI extends NpcAI<Agathion>
{
	public AgathionAI(Agathion actor)
	{
		super(actor);
	}
	
	/**
	 * Este é o método principal da IA, chamado em cada "tick" do servidor.
	 * Ele decide qual ação o Agathion deve tomar.
	 */
	@Override
	protected synchronized void onEvtThink()
	{
		switch (_actor.getAI().getCurrentIntention().getType())
		{
			case FOLLOW:
				thinkFollow();
				break;
			case IDLE:
			default:
				_actor.getAI().thinkIdle();
				break;
		}
	}
	
	/**
	 * Contém a lógica detalhada para seguir o dono.
	 * Usa GeoEngine para validação de movimento.
	 */
	@Override
	protected void thinkFollow()
	{
		final Agathion agathion = _actor;
		final Player owner = agathion.getPlayer();
		
		if (owner == null || !owner.isOnline())
		{
			agathion.deleteMe();
			return;
		}
		
		if (_actor.distance2D(owner) > 1500 || owner.isTeleporting())
		{
			final int safeOffset = 45;
			final int angleDeg = (int) ((Math.random() * 360));
			final double rad = Math.toRadians(angleDeg);
			final int dx = (int) (Math.cos(rad) * safeOffset);
			final int dy = (int) (Math.sin(rad) * safeOffset);
			final Location ownerPos = owner.getPosition().clone();
			ownerPos.setX(ownerPos.getX() + dx);
			ownerPos.setY(ownerPos.getY() + dy);
			_actor.teleportTo(ownerPos, 0);
			return;
		}
		
		final int followDistance = 45;
		if (!_actor.isIn2DRadius(owner, followDistance))
		{
			final Location ownerLoc = owner.getPosition();
			final int dx = ownerLoc.getX() - _actor.getX();
			final int dy = ownerLoc.getY() - _actor.getY();
			final double distance = Math.sqrt(dx * dx + dy * dy);
			
			if (distance > 0)
			{
				final double ratio = (distance - followDistance) / distance;
				final int targetX = _actor.getX() + (int) (dx * ratio);
				final int targetY = _actor.getY() + (int) (dy * ratio);
				final int targetZ = ownerLoc.getZ();
				
			if (GeoEngine.getInstance().canMoveToTarget(_actor.getX(), _actor.getY(), _actor.getZ(), targetX, targetY, targetZ))
				{
					final Location targetLoc = new Location(targetX, targetY, targetZ);
					_actor.getMove().maybeMoveToLocation(targetLoc, 0, true, false);
                    
				}
				else
				{
					_actor.teleportTo(ownerLoc, followDistance);
				}
			}
		}
	}
	
	/**
	 * Garante que a intenção padrão seja sempre seguir o jogador, se aplicável.
	 */
	@Override
	public void thinkIdle()
	{
	}
}
