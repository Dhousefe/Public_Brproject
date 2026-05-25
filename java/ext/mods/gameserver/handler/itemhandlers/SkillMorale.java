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

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.handler.IItemHandler;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.skills.L2Skill;

public class SkillMorale implements IItemHandler
{
	private final int[] clanSkills = new int[]
	{
		374
	};
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (playable instanceof Player)
		{
			Player player = (Player) playable;
			if (player.isClanLeader())
			{
				int[] var5 = this.clanSkills;
				int var6 = var5.length;
				
				for (int var7 = 0; var7 < var6; ++var7)
				{
					int s = var5[var7];
					L2Skill clanSkill = SkillTable.getInstance().getInfo(s, SkillTable.getInstance().getMaxLevel(s));
					if (clanSkill != null)
					{
						player.addSkill(clanSkill, true);
					}
					
					player.getClan().addClanSkill(clanSkill, true);
				}
				
				player.sendPacket(new SkillList(player));
				
				player.getClan().updateClanInDB();
				playable.destroyItem(item.getObjectId(), 1, false);
				player.broadcastUserInfo();
			}
			else
			{
				player.sendMessage("You are not the clan leader.");
			}
			
		}
	}
}
