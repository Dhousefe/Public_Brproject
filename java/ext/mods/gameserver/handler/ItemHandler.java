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
package ext.mods.gameserver.handler;

import ext.mods.gameserver.model.item.kind.EtcItem;
import ext.mods.gameserver.handler.itemhandlers.ItemNobles;
import ext.mods.gameserver.handler.itemhandlers.CapsuleBox_System;

public class ItemHandler extends AbstractHandler<Integer, IItemHandler>
{
	protected ItemHandler()
	{
		super(IItemHandler.class, "itemhandlers");
		registerHandler(new ItemNobles());
		registerHandler(new CapsuleBox_System());
	}
	
	@Override
	protected void registerHandler(IItemHandler handler)
	{
		_entries.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
	}
	
	@Override
	public IItemHandler getHandler(Object key)
	{
		if (!(key instanceof EtcItem etcItem) || etcItem.getHandlerName() == null)
			return null;
		
		return super.getHandler(etcItem.getHandlerName().hashCode());
	}
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler INSTANCE = new ItemHandler();
	}
}