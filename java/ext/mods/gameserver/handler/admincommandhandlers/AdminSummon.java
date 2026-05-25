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

import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.records.PetDataEntry;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.GMViewItemList;

public class AdminSummon implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_ride",
		"admin_unride",
		"admin_unsummon",
		"admin_summon"
	};
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_ride"))
		{
			if (player.isCursedWeaponEquipped())
			{
				player.sendMessage("You can't use this command while cursed weapon is equipped.");
				return;
			}
			
			final StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			
			if (!st.hasMoreTokens())
			{
				player.sendMessage("You must enter a parameter for that command.");
				return;
			}
			
			final String mount = st.nextToken();
			
			int npcId;
			if (mount.equals("wyvern") || mount.equals("2"))
				npcId = 12621;
			else if (mount.equals("strider") || mount.equals("1"))
				npcId = 12526;
			else
			{
				player.sendMessage("Parameter '" + mount + "' isn't recognized for that command.");
				return;
			}
			
			if (player.isMounted())
				player.dismount();
			else if (player.getSummon() != null)
				player.getSummon().unSummon(player);
			
			player.mount(npcId, 0);
		}
		else if (command.equals("admin_unride"))
		{
			player.dismount();
		}
		else
		{
			final Player targetPlayer = getTarget(Playable.class, player, true).getActingPlayer();
			if (targetPlayer == null)
			{
				player.sendPacket(SystemMessageId.INVALID_TARGET);
				return;
			}
			
			final Summon summon = targetPlayer.getSummon();
			
			if (command.startsWith("admin_unsummon"))
			{
				if (summon == null)
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				
				summon.unSummon(targetPlayer);
			}
			else if (command.startsWith("admin_summon"))
			{
				if (!(summon instanceof Pet pet))
				{
					player.sendPacket(SystemMessageId.INVALID_TARGET);
					return;
				}
				
				final StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				
				try
				{
					switch (st.nextToken())
					{
						case "food":
							pet.setCurrentFed(pet.getPetData().maxMeal());
							break;
						
						case "inventory":
							player.sendPacket(new GMViewItemList(pet));
							break;
						
						case "level":
							final int level = Integer.parseInt(st.nextToken());
							
							final PetDataEntry pde = pet.getTemplate().getPetDataEntry(level);
							if (pde == null)
							{
								player.sendMessage("Invalid level for //summon level.");
								return;
							}
							
							final long oldExp = pet.getStatus().getExp();
							final long newExp = pde.maxExp();
							
							if (oldExp > newExp)
								pet.getStatus().removeExp(oldExp - newExp);
							else if (oldExp < newExp)
								pet.getStatus().addExp(newExp - oldExp);
							break;
						
						default:
							player.sendMessage("Usage: //summon food|inventory|level>");
							break;
					}
				}
				catch (Exception e)
				{
					player.sendMessage("Usage: //summon food|inventory|level>");
				}
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}