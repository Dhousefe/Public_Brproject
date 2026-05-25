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
package ext.mods.gameserver.handler.voicedcommandhandlers;

import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.Crypta.DeeplTranslator;

/**
 * Handler para o comando de voz .translation/.trans que abre o menu de linguagens do DeeplTranslator
 */
public class LanguageCommand implements IVoicedCommandHandler
{
	private static final String[] COMMANDS = { "translation", "trans", "toggletrans", "toggletranslation" };
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		if (command.equals("translation") || command.equals("trans"))
		{
			Object deeplTranslator = DeeplTranslator.getInstance();
			if (deeplTranslator != null)
			{
				DeeplTranslator.getInstance().showLanguageMenu(player);
				return true;
			}
			else
			{
				player.sendMessage("Sistema de tradução não disponível no momento.");
				return false;
			}
		}
		else if (command.equals("toggletrans") || command.equals("toggletranslation"))
		{
			player.switchTranslatePreference();
			boolean enabled = player.isHtmlTranslationEnabled();
			player.sendMessage("Tradução HTML " + (enabled ? "habilitada" : "desabilitada") + ".");
			return true;
		}
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
