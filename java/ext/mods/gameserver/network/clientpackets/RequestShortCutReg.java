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

import ext.mods.gameserver.enums.ShortcutType;
import ext.mods.gameserver.model.Shortcut;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.ShortCutRegister;

public final class RequestShortCutReg extends L2GameClientPacket
{
	private int _type;
	private int _id;
	private int _slot;
	private int _page;
	private int _characterType;
	
	@Override
	protected void readImpl()
	{
		_type = readD();
		int slot = readD();
		_id = readD();
		_characterType = readD();
		
		_slot = slot % 12;
		_page = slot / 12;
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_page < 0 || _page > 10)
			return;
		
		if (_type < 1 || _type > ShortcutType.VALUES.length)
			return;
		
		final ShortcutType type = ShortcutType.VALUES[_type];
		
		switch (type)
		{
			case ITEM:
			case ACTION:
			case MACRO:
			case RECIPE:
				Shortcut shortcut = new Shortcut(_slot, _page, type, _id, -1, _characterType);
				sendPacket(new ShortCutRegister(player, shortcut));
				player.getShortcutList().addShortcut(shortcut);
				break;
			
			case SKILL:
				final int level = player.getSkillLevel(_id);
				if (level > 0)
				{
					shortcut = new Shortcut(_slot, _page, type, _id, level, _characterType);
					sendPacket(new ShortCutRegister(player, shortcut));
					player.getShortcutList().addShortcut(shortcut);
				}
				break;
		}
	}
}