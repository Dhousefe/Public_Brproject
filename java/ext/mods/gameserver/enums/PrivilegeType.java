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

public enum PrivilegeType
{
	NONE(0),
	
	SP_INVITE(2),
	SP_MANAGE_TITLES(4),
	SP_WAREHOUSE_SEARCH(8),
	SP_MANAGE_RANKS(16),
	SP_CLAN_WAR(32),
	SP_DISMISS(64),
	SP_EDIT_CREST(128),
	SP_MASTER_RIGHTS(256),
	SP_MANAGE_LEVELS(512),
	
	CHP_ENTRY_EXIT_RIGHTS(1024),
	CHP_USE_FUNCTIONS(2048),
	CHP_AUCTION(4096),
	CHP_RIGHT_TO_DISMISS(8192),
	CHP_SET_FUNCTIONS(16384),
	
	CP_ENTRY_EXIT_RIGHTS(32768),
	CP_MANOR_ADMINISTRATION(65536),
	CP_MANAGE_SIEGE_WAR(131072),
	CP_USE_FUNCTIONS(262144),
	CP_RIGHT_TO_DISMISS(524288),
	CP_MANAGE_TAXES(1048576),
	CP_MERCENARIES(2097152),
	CP_SET_FUNCTIONS(4194304),
	
	ALL(8388606);
	
	private int _mask;
	
	private PrivilegeType(int mask)
	{
		_mask = mask;
	}
	
	public int getMask()
	{
		return _mask;
	}
}