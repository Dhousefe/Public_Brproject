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

public class BypassHandler extends AbstractHandler<Integer, IBypassHandler>
{
	protected BypassHandler()
	{
		super(IBypassHandler.class, "bypasshandlers");
	}
	
	@Override
	public void registerHandler(IBypassHandler handler)
	{
		for (String id : handler.getBypassList())
			_entries.put(id.hashCode(), handler);
	}
	
	@Override
	public IBypassHandler getHandler(Object key)
	{
		if (!(key instanceof String bypassCommand))
			return null;
		
		final int index = bypassCommand.indexOf(" ");
		final String command = (index == -1) ? bypassCommand : bypassCommand.substring(0, index);
		
		return super.getHandler(command.hashCode());
	}
	
	public static BypassHandler getInstance()
	{
		return Singleton.INSTANCE;
	}
	
	private static class Singleton
	{
		private static final BypassHandler INSTANCE = new BypassHandler();
	}
}