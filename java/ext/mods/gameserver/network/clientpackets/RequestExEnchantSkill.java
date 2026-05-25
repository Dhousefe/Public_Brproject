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
import ext.mods.commons.random.Rnd;
import ext.mods.extensions.listener.manager.PlayerListenerManager;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.SkillTreeData;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Folk;
import ext.mods.gameserver.model.holder.skillnode.EnchantSkillNode;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.UserInfo;
import ext.mods.gameserver.skills.L2Skill;

public final class RequestExEnchantSkill extends L2GameClientPacket
{
	private int _skillId;
	private int _skillLevel;
	
	@Override
	protected void readImpl()
	{
		_skillId = readD();
		_skillLevel = readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_skillId <= 0 || _skillLevel <= 0)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.getClassId().getLevel() < 3 || player.getStatus().getLevel() < 76)
			return;
		
		final Folk folk = player.getCurrentFolk();
		if (folk == null || !player.getAI().canDoInteract(folk))
			return;
		
		if (player.getSkillLevel(_skillId) >= _skillLevel)
			return;
		
		final L2Skill skill = SkillTable.getInstance().getInfo(_skillId, _skillLevel);
		if (skill == null)
			return;
		
		final EnchantSkillNode esn = SkillTreeData.getInstance().getEnchantSkillFor(player, _skillId, _skillLevel);
		if (esn == null)
			return;
		
		if (player.getStatus().getSp() < esn.getSp())
		{
			player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		if (player.getStatus().getExp() - esn.getExp() < player.getStatus().getExpForLevel(76))
		{
			player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ENOUGH_EXP_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		if (Config.ES_SP_BOOK_NEEDED && esn.getItem() != null && !player.destroyItemByItemId(esn.getItem().getId(), esn.getItem().getValue(), true))
		{
			player.sendPacket(SystemMessageId.YOU_DONT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
			return;
		}
		
		player.removeExpAndSp(esn.getExp(), esn.getSp());
		
		if (Rnd.get(100) <= esn.getEnchantRate(player.getStatus().getLevel()))
		{
			player.addSkill(skill, true, true);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUCCEEDED_IN_ENCHANTING_THE_SKILL_S1).addSkillName(_skillId, _skillLevel));
			final int value = _skillLevel >= 130 ? _skillLevel - 140 : _skillLevel - 100;
			
			if (player.getMissions().getMission(MissionType.ENCHANT_SKILL).getValue() < value)
				player.getMissions().set(MissionType.ENCHANT_SKILL, value, false, false);
		}
		else
		{
			player.addSkill(SkillTable.getInstance().getInfo(_skillId, SkillTable.getInstance().getMaxLevel(_skillId)), true, true);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_ENCHANT_THE_SKILL_S1).addSkillName(_skillId, _skillLevel));
		}
		
		player.sendPacket(new SkillList(player));
		player.sendPacket(new UserInfo(player));
		
		folk.showEnchantSkillList(player);
		
		PlayerListenerManager.getInstance().notifySkillEnchantSuccess(player, _skillId, _skillLevel);
	}
}