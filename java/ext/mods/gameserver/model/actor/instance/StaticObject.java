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
package ext.mods.gameserver.model.actor.instance;

import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.ShowTownMap;
import ext.mods.gameserver.network.serverpackets.StaticObjectInfo;

/**
 * A static object with low amount of interactions and no AI - such as throne, village town maps, etc.
 */
public class StaticObject extends WorldObject
{
	private int _staticObjectId;
	private int _type = -1;
	private boolean _isBusy;
	private ShowTownMap _map;
	
	public StaticObject(int objectId)
	{
		super(objectId);
	}
	
	@Override
	public void onInteract(Player player)
	{
		if (getType() == 2)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile(player.getLocale(), "html/signboard.htm");
			player.sendPacket(html);
		}
		else if (getType() == 0)
			player.sendPacket(getMap());
	}
	
	@Override
	public void onAction(Player player, boolean isCtrlPressed, boolean isShiftPressed)
	{
		if (player.getTarget() != this)
			player.setTarget(this);
		else
			player.getAI().tryToInteract(this, isCtrlPressed, isShiftPressed);
	}
	
	@Override
	public void sendInfo(Player player)
	{
		player.sendPacket(new StaticObjectInfo(this));
	}
	
	public int getStaticObjectId()
	{
		return _staticObjectId;
	}
	
	public void setStaticObjectId(int staticObjectId)
	{
		_staticObjectId = staticObjectId;
	}
	
	public int getType()
	{
		return _type;
	}
	
	public void setType(int type)
	{
		_type = type;
	}
	
	public boolean isBusy()
	{
		return _isBusy;
	}
	
	public void setBusy(boolean isBusy)
	{
		_isBusy = isBusy;
	}
	
	public ShowTownMap getMap()
	{
		return _map;
	}
	
	public void setMap(String texture, int x, int y)
	{
		_map = new ShowTownMap("town_map." + texture, x, y);
	}
}