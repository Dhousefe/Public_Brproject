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

import java.util.Collection;

import ext.mods.gameserver.model.Macro;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.records.MacroCmd;
import ext.mods.gameserver.network.SystemMessageId;

public final class RequestMakeMacro extends L2GameClientPacket
{
	private static final int MAX_MACRO_LENGTH = 12;
	
	private Macro _macro;
	private int _commandsLenght;
	
	@Override
	protected void readImpl()
	{
		int id = readD();
		String name = readS();
		String desc = readS();
		String acronym = readS();
		int icon = readC();
		int count = readC();
		
		if (count > MAX_MACRO_LENGTH)
			count = MAX_MACRO_LENGTH;
		
		MacroCmd[] commands = new MacroCmd[count];
		
		for (int i = 0; i < count; i++)
		{
			int entry = readC();
			int type = readC();
			int d1 = readD();
			int d2 = readC();
			String command = readS();
			
			_commandsLenght += command.length();
			commands[i] = new MacroCmd(entry, type, d1, d2, command);
		}
		_macro = new Macro(id, icon, name, desc, acronym, commands);
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_commandsLenght > 255)
		{
			player.sendPacket(SystemMessageId.INVALID_MACRO);
			return;
		}
		
		final Collection<Macro> macros = player.getMacroList().values();
		
		if (macros.size() > 24)
		{
			player.sendPacket(SystemMessageId.YOU_MAY_CREATE_UP_TO_24_MACROS);
			return;
		}
		
		if (_macro.name.isEmpty())
		{
			player.sendPacket(SystemMessageId.ENTER_THE_MACRO_NAME);
			return;
		}
		
		if (macros.stream().anyMatch(m -> m.name.equalsIgnoreCase(_macro.name) && m.id != _macro.id))
		{
			player.sendPacket(SystemMessageId.MACRO_NAME_ALREADY_USED);
			return;
		}
		
		if (_macro.descr.length() > 32)
		{
			player.sendPacket(SystemMessageId.MACRO_DESCRIPTION_MAX_32_CHARS);
			return;
		}
		
		player.getMacroList().registerMacro(_macro);
	}
}