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
package ext.mods.gameserver.handler.bypasshandlers;

import java.util.List;
import java.util.StringTokenizer;

import ext.mods.commons.lang.StringUtil;

import ext.mods.gameserver.data.xml.ObserverGroupData;
import ext.mods.gameserver.handler.IBypassHandler;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.ObserverLocation;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class ObserveGroup implements IBypassHandler
{
	private static final String[] COMMANDS = { "observe_group" };
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		final StringTokenizer st = new StringTokenizer(command);
		st.nextToken();
		
		final List<ObserverLocation> locs = ObserverGroupData.getInstance().getObserverLocations(Integer.parseInt(st.nextToken()));
		if (locs == null)
			return false;
		
		final StringBuilder sb = new StringBuilder();
		sb.append("<html><body>&$650;<br><br>");
		
		for (ObserverLocation loc : locs)
		{
			StringUtil.append(sb, "<a action=\"bypass -h npc_", target.getObjectId(), "_observe ", loc.getLocId(), "\">&$", loc.getLocId(), ";");
			
			if (loc.getCost() > 0)
				StringUtil.append(sb, " - ", loc.getCost(), " &#57;");
			
			StringUtil.append(sb, "</a><br1>");
		}
		sb.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(target.getObjectId());
		html.setHtml(sb.toString());
		
		player.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
