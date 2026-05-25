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

import ext.mods.Config;

public enum FloodProtector
{
	ROLL_DICE(0, Config.ROLL_DICE_TIME),
	HERO_VOICE(1, Config.HERO_VOICE_TIME),
	SUBCLASS(2, Config.SUBCLASS_TIME),
	DROP_ITEM(3, Config.DROP_ITEM_TIME),
	SERVER_BYPASS(4, Config.SERVER_BYPASS_TIME),
	MULTISELL(5, Config.MULTISELL_TIME),
	MANUFACTURE(6, Config.MANUFACTURE_TIME),
	MANOR(7, Config.MANOR_TIME),
	SENDMAIL(8, Config.SENDMAIL_TIME),
	CHARACTER_SELECT(9, Config.CHARACTER_SELECT_TIME),
	GLOBAL_CHAT(10, Config.GLOBAL_CHAT_TIME),
	TRADE_CHAT(11, Config.TRADE_CHAT_TIME),
	SOCIAL(12, Config.SOCIAL_TIME),
	USE_ITEM(13, Config.ITEM_TIME),
	ACTION(14, Config.ACTION_TIME);
	
	private final int _id;
	private final int _reuseDelay;
	
	private FloodProtector(int id, int reuseDelay)
	{
		_id = id;
		_reuseDelay = reuseDelay;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public int getReuseDelay()
	{
		return _reuseDelay;
	}
	
	public static final int VALUES_LENGTH = FloodProtector.values().length;
}