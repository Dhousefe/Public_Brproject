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
package ext.mods.gameserver.handler.itemhandlers;

import java.util.List;

import ext.mods.Crypta.AgathionData;
import ext.mods.aghation.holder.AgathionHolder;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Agathion;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.skills.L2Skill;

public class AgathionItems implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player player = (Player) playable;
		final int itemId = item.getItemId();
		final long cooldown = 1000;
		
		long currentTime = System.currentTimeMillis();
		if (currentTime - player.getLastAgationSummonTime() < cooldown)
		{
			player.sendMessage("You must wait before performing this action again.");
			return;
		}
		player.setLastAgationSummonTime(currentTime);
		
		Object agathionDataInstance = AgathionData.getInstance();
		if (agathionDataInstance == null)
		{
			player.sendMessage("AgathionData is not available.");
			return;
		}
		
		List<AgathionHolder> agationList = null;
		try
		{
			@SuppressWarnings("unchecked")
			List<AgathionHolder> tempList = (List<AgathionHolder>) AgathionData.getInstance().getAgathionsByItemId(itemId);
			agationList = tempList;
		}
		catch (Exception e)
		{
			player.sendMessage("Error accessing AgathionData.");
			e.printStackTrace();
			return;
		}
		
		if (agationList == null || agationList.isEmpty())
		{
			player.sendMessage("No Agathion is associated with this item.");
			return;
		}
		
		AgathionHolder agathionInfo = agationList.get(0);
		
		if (player.getCurrentAgation() != null && player.getMemos().getInteger("agation", 0) == itemId)
		{
			player.deletedAgation(player.getCurrentAgation());
			player.setCurrentAgation(null);
			player.getMemos().unset("agation");
			player.sendMessage("Agathion unsummoned.");
		}
		else
		{
			if (player.getCurrentAgation() != null)
			{
				player.deletedAgation(player.getCurrentAgation());
				player.setCurrentAgation(null);
				player.getMemos().unset("agation");
			}
			
			NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(agathionInfo.getNpcId());
			if (npcTemplate == null)
			{
				player.sendMessage("Cannot spawn the Agathion. NPC template not found.");
				return;
			}
			
			L2Skill skillToCast = SkillTable.getInstance().getInfo(2046, 1);
			if (skillToCast == null)
			{
				player.sendMessage("Summoning skill not found.");
				return;
			}
			
			player.getAI().tryToCast(player, skillToCast, false, false, item.getObjectId());
			player.sendPacket(SystemMessageId.SUMMON_A_PET);
			
			ThreadPool.schedule(() ->
			{
				Agathion spawnedAgathion = player.getCurrentAgation();
				if (spawnedAgathion != null)
				{
					spawnedAgathion.setInstanceMap(player.getInstanceMap(), false);
					
					if (agathionInfo.getRunSpeed())
					{
						spawnedAgathion.setRunning(true);
					}
				}
			}, skillToCast.getHitTime() + 100);
		}
	}
}
