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
package ext.mods.gameserver.model.actor.instance;

import java.util.concurrent.ScheduledFuture;

import ext.mods.commons.pool.ThreadPool;

import ext.mods.gameserver.data.SkillTable.FrequentSkill;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.skills.L2Skill;

/**
 * Christmas trees used on events.<br>
 * The special tree (npcId 13007) emits a regen aura, but only when set outside a peace zone.
 */
public class ChristmasTree extends Folk
{
	public static final int SPECIAL_TREE_ID = 13007;
	
	private ScheduledFuture<?> _aiTask;
	
	public ChristmasTree(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		if (template.getNpcId() == SPECIAL_TREE_ID && !isInsideZone(ZoneId.TOWN))
		{
			final L2Skill recoveryAura = FrequentSkill.SPECIAL_TREE_RECOVERY_BONUS.getSkill();
			if (recoveryAura == null)
				return;
			
			_aiTask = ThreadPool.scheduleAtFixedRate(() ->
			{
				forEachKnownTypeInRadius(Player.class, 200, player ->
				{
					if (player.getFirstEffect(recoveryAura) == null)
						recoveryAura.getEffects(player, player);
				});
			}, 3000, 3000);
		}
	}
	
	@Override
	public void deleteMe()
	{
		if (_aiTask != null)
		{
			_aiTask.cancel(true);
			_aiTask = null;
		}
		super.deleteMe();
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}