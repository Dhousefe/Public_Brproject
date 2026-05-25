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
package ext.mods.gameserver.enums;

/**
 * This enum contains L2OFF teleport types from AI.obj. See individual comment on each type.
 */
public enum TeleportType
{
	/**
	 * AI.obj equivalent: "" (empty string)<br>
	 * This is the main "standard" teleport used by common GK.
	 */
	STANDARD,
	
	/**
	 * AI.obj equivalent: "NewbieTokenTeleports"<br>
	 * Reference item: 8542
	 */
	NEWBIE_TOKEN,
	
	/**
	 * AI.obj equivalent: "NoblessNeedItemField"<br>
	 * Reference item: 6651
	 */
	NOBLE_HUNTING_ZONE_PASS,
	
	/**
	 * AI.obj equivalent: "NoblessNoItemField"<br>
	 * Reference item: 57
	 */
	NOBLE_HUNTING_ZONE_ADENA,
	
	/**
	 * AI.obj equivalent: "ForFriend"<br>
	 * VARKA/KETRA Alliance related content.
	 */
	ALLY,
	
	/**
	 * AI.obj equivalent: "1"<br>
	 * Used by Clan Hall Functions.
	 */
	CHF_LEVEL_1,
	
	/**
	 * AI.obj equivalent: "2"<br>
	 * Used by Clan Hall Functions.
	 */
	CHF_LEVEL_2;
}