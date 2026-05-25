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
package ext.mods.gameserver.model.memo;

import java.util.Map;

import ext.mods.commons.data.MemoSet;

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;

/**
 * An implementation of {@link MemoSet} used for Npc.
 */
public class NpcMemo extends MemoSet
{
	public static final NpcMemo DUMMY_SET = new NpcMemo();
	
	private static final long serialVersionUID = 1L;
	
	public NpcMemo()
	{
		super();
	}
	
	public NpcMemo(final int size)
	{
		super(size);
	}
	
	public NpcMemo(final Map<String, String> m)
	{
		super(m);
	}
	
	@Override
	protected void onSet(String key, String value)
	{
	}
	
	@Override
	protected void onUnset(String key)
	{
	}
	
	/**
	 * @param str : The {@link String} used as parameter.
	 * @return The {@link Creature} linked to the objectId passed as a {@link String} parameter, or null if not found.
	 */
	public final Creature getCreature(String str)
	{
		final int id = getInteger(str, 0);
		if (id == 0)
			return null;
		
		final WorldObject object = World.getInstance().getObject(id);
		if (object == null || (object instanceof Npc npc && npc.isDecayed()))
			return null;
		
		return (object instanceof Creature creature) ? creature : null;
	}
}