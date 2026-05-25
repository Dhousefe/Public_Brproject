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
package ext.mods.playergod.holder;

import ext.mods.commons.data.StatSet;

public class PlayerGodHolder
{
	private final boolean enabled;
	
	private final int killsRequired;
	private final int timeWindow;
	private final String killAnnouncement;
	
	private final int heroAuraDuration;
	private final boolean auraOnly;
	
	private final boolean loginAnnouncementEnabled;
	private final String loginMessage;
	
	public PlayerGodHolder(StatSet set)
	{
		enabled = set.getBool("enabled", true);
		
		killsRequired = set.getInteger("killsRequired", 10);
		timeWindow = set.getInteger("timeWindow", 300);
		killAnnouncement = set.getString("killAnnouncement", "");
		
		heroAuraDuration = set.getInteger("heroAuraDuration", 600);
		auraOnly = set.getBool("auraOnly", true);
		
		loginAnnouncementEnabled = set.getBool("loginAnnouncementEnabled", true);
		loginMessage = set.getString("loginMessage", "%player_name% retornou como um verdadeiro DEUS da guerra!");
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
	public int getKillsRequired()
	{
		return killsRequired;
	}
	
	public int getTimeWindow()
	{
		return timeWindow;
	}
	
	public String getKillAnnouncement()
	{
		return killAnnouncement;
	}
	
	public int getHeroAuraDuration()
	{
		return heroAuraDuration;
	}
	
	public boolean isAuraOnly()
	{
		return auraOnly;
	}
	
	public boolean isLoginAnnouncementEnabled()
	{
		return loginAnnouncementEnabled;
	}
	
	public String getLoginMessage()
	{
		return loginMessage;
	}
}
