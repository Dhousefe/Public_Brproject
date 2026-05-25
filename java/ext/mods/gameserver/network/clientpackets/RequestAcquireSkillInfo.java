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
package ext.mods.gameserver.network.clientpackets;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.SkillTreeData;
import ext.mods.gameserver.data.xml.SpellbookData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Folk;
import ext.mods.gameserver.model.holder.skillnode.ClanSkillNode;
import ext.mods.gameserver.model.holder.skillnode.FishingSkillNode;
import ext.mods.gameserver.model.holder.skillnode.GeneralSkillNode;
import ext.mods.gameserver.network.serverpackets.AcquireSkillInfo;
import ext.mods.gameserver.skills.L2Skill;

public class RequestAcquireSkillInfo extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLevel;
	private int _skillType;
	
	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLevel = readD();
		_skillType = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_skillId <= 0 || _skillLevel <= 0)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		final Folk folk = player.getCurrentFolk();
		if (folk == null || !player.getAI().canDoInteract(folk))
			return;
		
		final L2Skill skill = SkillTable.getInstance().getInfo(_skillId, _skillLevel);
		if (skill == null)
			return;
		
		final AcquireSkillInfo asi;
		
		switch (_skillType)
		{
			case 0:
				int skillLvl = player.getSkillLevel(_skillId);
				if (skillLvl >= _skillLevel)
					return;
				
				if (skillLvl != _skillLevel - 1)
					return;
				
				if (!folk.getTemplate().canTeach(player.getClassId()))
					return;
				
				final GeneralSkillNode gsn = player.getTemplate().findSkill(_skillId, _skillLevel);
				if (gsn != null)
				{
					asi = new AcquireSkillInfo(_skillId, _skillLevel, gsn.getCorrectedCost(), 0);
					final int bookId = SpellbookData.getInstance().getBookForSkill(_skillId, _skillLevel);
					if (bookId != 0)
						asi.addRequirement(99, bookId, 1, 50);
					sendPacket(asi);
				}
				break;
			
			case 1:
				skillLvl = player.getSkillLevel(_skillId);
				if (skillLvl >= _skillLevel)
					return;
				
				if (skillLvl != _skillLevel - 1)
					return;
				
				final FishingSkillNode fsn = SkillTreeData.getInstance().getFishingSkillFor(player, _skillId, _skillLevel);
				if (fsn != null)
				{
					asi = new AcquireSkillInfo(_skillId, _skillLevel, 0, 1);
					asi.addRequirement(4, fsn.getItemId(), fsn.getItemCount(), 0);
					sendPacket(asi);
				}
				break;
			
			case 2:
				if (!player.isClanLeader())
					return;
				
				final ClanSkillNode csn = SkillTreeData.getInstance().getClanSkillFor(player, _skillId, _skillLevel);
				if (csn != null)
				{
					asi = new AcquireSkillInfo(skill.getId(), skill.getLevel(), csn.getCost(), 2);
					if (Config.LIFE_CRYSTAL_NEEDED && csn.getItemId() != 0)
						asi.addRequirement(1, csn.getItemId(), 1, 0);
					sendPacket(asi);
				}
				break;
		}
	}
}