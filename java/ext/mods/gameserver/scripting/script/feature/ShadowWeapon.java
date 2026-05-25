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
package ext.mods.gameserver.scripting.script.feature;

import ext.mods.Config;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.scripting.Quest;

public class ShadowWeapon extends Quest
{
	private static final int D_GRADE_COUPON = 8869;
	private static final int C_GRADE_COUPON = 8870;
	
	public ShadowWeapon()
	{
		super(-1, "feature");
		
		addTalkId(FirstClassChange.FIRST_CLASS_NPCS);
		addTalkId(SecondClassChange.SECOND_CLASS_NPCS);
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		if (!Config.ALLOW_SHADOW_WEAPONS)
			return "message.htm";
		
		boolean hasD = player.getInventory().hasItems(D_GRADE_COUPON);
		boolean hasC = player.getInventory().hasItems(C_GRADE_COUPON);
		
		if (!hasD && !hasC)
			return "exchange-no.htm";
		
		String multisell = "306893003";
		if (!hasD)
			multisell = "306893002";
		else if (!hasC)
			multisell = "306893001";
		
		return getHtmlText("exchange.htm", player).replace("%msid%", multisell);
	}
}