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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ext.mods.Config;
import ext.mods.commons.lang.StringUtil;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.sql.PlayerInfoTable;
import ext.mods.gameserver.data.xml.NpcData;
import ext.mods.gameserver.data.xml.PlayerData;
import ext.mods.gameserver.data.xml.PlayerLevelData;
import ext.mods.gameserver.data.xml.ScriptData;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.enums.ShortcutType;
import ext.mods.gameserver.enums.actors.Sex;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.Macro;
import ext.mods.gameserver.model.Shortcut;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.PlayerTemplate;
import ext.mods.gameserver.model.holder.skillnode.GeneralSkillNode;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.records.MacroCmd;
import ext.mods.gameserver.model.records.NewbieItem;
import ext.mods.gameserver.model.records.custom.Macros;
import ext.mods.gameserver.network.serverpackets.CharCreateFail;
import ext.mods.gameserver.network.serverpackets.CharCreateOk;
import ext.mods.gameserver.network.serverpackets.CharSelectInfo;
import ext.mods.gameserver.network.serverpackets.ShortCutRegister;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.quests.QuestData;
import ext.mods.quests.holder.QuestHolder;

public final class RequestCharacterCreate extends L2GameClientPacket
{
	private String _name;
	private int _race;
	private byte _sex;
	private int _classId;
	private byte _hairStyle;
	private byte _hairColor;
	private byte _face;
	
	@Override
	protected void readImpl()
	{
		_name = readS();
		_race = readD();
		_sex = (byte) readD();
		_classId = readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		_hairStyle = (byte) readD();
		_hairColor = (byte) readD();
		_face = (byte) readD();
	}
	
	@Override
	protected void runImpl()
	{
		if (_race > 4 || _race < 0)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if (_face > 2 || _face < 0)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if (_hairStyle < 0 || (_sex == 0 && _hairStyle > 4) || (_sex != 0 && _hairStyle > 6))
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if (_hairColor > 3 || _hairColor < 0)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		if (Config.LIST_RESTRICTED_CHAR_NAMES.contains(_name.toLowerCase()))
		{
			sendPacket(CharCreateFail.REASON_INCORRECT_NAME);
			return;
		}
		
		if (!StringUtil.isValidString(_name, Config.CNAME_TEMPLATE))
		{
			sendPacket(CharCreateFail.REASON_INCORRECT_NAME);
			return;
		}
		
		if (NpcData.getInstance().getTemplateByName(_name) != null)
		{
			sendPacket(CharCreateFail.REASON_INCORRECT_NAME);
			return;
		}
		
		if (PlayerInfoTable.getInstance().getCharactersInAcc(getClient().getAccountName()) >= 7)
		{
			sendPacket(CharCreateFail.REASON_TOO_MANY_CHARACTERS);
			return;
		}
		
		if (PlayerInfoTable.getInstance().getPlayerObjectId(_name) > 0)
		{
			sendPacket(CharCreateFail.REASON_NAME_ALREADY_EXISTS);
			return;
		}
		
		final PlayerTemplate template = PlayerData.getInstance().getTemplate(_classId);
		if (template == null || template.getClassBaseLevel() > 1)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		final Player player = Player.create(IdFactory.getInstance().getNextId(), template, getClient().getAccountName(), _name, _hairStyle, _hairColor, _face, Sex.VALUES[_sex]);
		if (player == null)
		{
			sendPacket(CharCreateFail.REASON_CREATION_FAILED);
			return;
		}
		
		for (String buff : player.getTemplate().getBuffIds())
		{
			String[] parts = buff.split("-");
			int skillId = Integer.parseInt(parts[0]);
			int skillLevel = Integer.parseInt(parts[1]);
			
			L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLevel);
			if (skill != null)
				skill.getEffects(player, player);
		}
		
		player.getStatus().setMaxHpMp();
		
		sendPacket(CharCreateOk.STATIC_PACKET);
		
		World.getInstance().addObject(player);
		
		player.getPosition().set(template.getRandomSpawn());
		player.setTitle(template.getTitle());
		
		player.addExpAndSp(PlayerLevelData.getInstance().getPlayerLevel(template.getStartLevel()).requiredExpToLevelUp(), 0);
		
		int i = 0;
		for (Macros macro : template.getMacros())
		{
			List<MacroCmd> commands = new ArrayList<>();
			List<Macro> macros = new ArrayList<>();
			List<Shortcut> shortcuts = new ArrayList<>();
			
			MacroCmd cmd = new MacroCmd(1, 3, 0, 0, macro.command());
			commands.add(cmd);
			
			Macro m = new Macro(100 + i, 0, macro.name(), macro.action(), macro.acronim(), commands.toArray(new MacroCmd[commands.size()]));
			macros.add(m);
			
			Shortcut shortcut = new Shortcut(macro.slot(), macro.panel(), ShortcutType.MACRO, 100 + i, 0, player.getClassId().getId());
			shortcuts.add(shortcut);
			
			player.getMacroList().registerMacro(m);
			player.sendPacket(new ShortCutRegister(player, shortcut));
			player.getShortcutList().addShortcut(shortcut);
			i++;
		}
		
		player.getShortcutList().addShortcut(new Shortcut(0, 0, ShortcutType.ACTION, 2, -1, 1));
		player.getShortcutList().addShortcut(new Shortcut(3, 0, ShortcutType.ACTION, 5, -1, 1));
		player.getShortcutList().addShortcut(new Shortcut(10, 0, ShortcutType.ACTION, 0, -1, 1));
		
		for (NewbieItem holder : template.getItems())
		{
			final ItemInstance item = player.getInventory().addItem(holder.id(), holder.count());
			
			if (holder.id() == 5588)
				player.getShortcutList().addShortcut(new Shortcut(11, 0, ShortcutType.ITEM, item.getObjectId(), -1, 1));
			
			item.setEnchantLevel(holder.enchant(), null);
			
			if (item.isEquipable() && holder.isEquipped())
				player.getInventory().equipItemAndRecord(item);
		}
		
		for (GeneralSkillNode skill : player.getAvailableAutoGetSkills())
		{
			if (skill.getId() == 1001 || skill.getId() == 1177)
				player.getShortcutList().addShortcut(new Shortcut(1, 0, ShortcutType.SKILL, skill.getId(), 1, 1), false, true);
			
			if (skill.getId() == 1216)
				player.getShortcutList().addShortcut(new Shortcut(9, 0, ShortcutType.SKILL, skill.getId(), 1, 1), false, true);
		}
		
		final Quest quest = ScriptData.getInstance().getQuest("Tutorial");
		if (quest != null)
			quest.newQuestState(player).setState(QuestStatus.STARTED);
		
		Collection<QuestHolder> tutorial = QuestData.getInstance().getQuests();
		if (!tutorial.isEmpty())
		{
			player.setQuestCompleted(1, false);
		}
		player.setQuestNotifyHtml(false);
		player.setQuestNotifyChat(false);
		player.setOnlineStatus(true, false);
		player.deleteMe();
		
		final CharSelectInfo csi = new CharSelectInfo(getClient().getAccountName(), getClient().getSessionId().playOkID1);
		sendPacket(csi);
		getClient().setCharSelectSlot(csi.getCharacterSlots());
		
	}
}