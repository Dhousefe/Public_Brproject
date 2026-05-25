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
package ext.mods.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.Config;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.taskmanager.DecayTaskManager;

public class AdminManage implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_cancel",
		"admin_heal",
		"admin_kill",
		"admin_suicide",
		"admin_res"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		String name = null;
		int radius = 0;
		
		final int paramCount = st.countTokens();
		if (paramCount == 2)
		{
			name = st.nextToken();
			radius = Integer.parseInt(st.nextToken());
		}
		else if (paramCount == 1)
		{
			final String paramToTest = st.nextToken();
			if (StringUtil.isDigit(paramToTest))
				radius = Integer.parseInt(paramToTest);
			else
				name = paramToTest;
		}
		
		Creature targetCreature = getTargetCreature(player, true);
		
		if (!StringUtil.isEmpty(name))
		{
			final Player worldPlayer = World.getInstance().getPlayer(name);
			if (worldPlayer != null)
				targetCreature = worldPlayer;
		}
		
		if (targetCreature == null)
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			return;
		}
		
		if (command.startsWith("admin_cancel"))
		{
			targetCreature.stopAllEffects();
			
			if (radius > 0)
				targetCreature.forEachKnownTypeInRadius(Creature.class, radius, Creature::stopAllEffects);
		}
		else if (command.startsWith("admin_heal"))
		{
			heal(targetCreature);
			
			if (radius > 0)
				targetCreature.forEachKnownTypeInRadius(Creature.class, radius, c -> heal(c));
		}
		else if (command.startsWith("admin_kill"))
		{
			kill(targetCreature, player);
			
			if (radius > 0)
				targetCreature.forEachKnownTypeInRadius(Creature.class, radius, c -> kill(c, player));
		}
		else if (command.startsWith("admin_suicide"))
		{
			if (suicide(targetCreature, player))
				player.sendMessage(targetCreature.getName() + " is suicide.");
		}
		else if (command.startsWith("admin_res"))
		{
			resurrect(targetCreature);
			
			if (radius > 0)
				targetCreature.forEachKnownTypeInRadius(Creature.class, radius, c -> resurrect(c));
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private static void heal(Creature creature)
	{
		if (creature.isDead())
			return;
		
		if (creature instanceof Player player)
			player.getStatus().setMaxCpHpMp();
		else
			creature.getStatus().setMaxHpMp();
	}
	
	private static boolean suicide(Creature creature, Player player)
	{
		if (creature.isDead())
			return false;
		
		creature.stopAllEffects();
		creature.reduceCurrentHp(creature.getStatus().getMaxHp() + creature.getStatus().getMaxCp() + 1, player, null);
		return true;
	}
	
	private static boolean kill(Creature creature, Player player)
	{
		if (creature.isDead() || creature == player)
			return false;
		
		creature.stopAllEffects();
		creature.reduceCurrentHp(creature.isChampion() ? creature.getStatus().getMaxHp() * Config.CHAMPION_HP + 1 : creature.getStatus().getMaxHp() + creature.getStatus().getMaxCp() + 1, player, null);
		return true;
	}
	
	private static void resurrect(Creature creature)
	{
		if (!creature.isDead())
			return;
		
		if (creature instanceof Player player)
			player.restoreExp(100.0);
		else
			DecayTaskManager.getInstance().cancel(creature);
		
		creature.doRevive();
	}
}