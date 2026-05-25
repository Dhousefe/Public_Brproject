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

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.ai.type.TamedBeastAI;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.location.Location;

/**
 * A tamed beast behaves a lot like a pet and has an owner. Some points :
 * <ul>
 * <li>feeding another beast to level 4 will vanish your actual tamed beast.</li>
 * <li>running out of spices will vanish your actual tamed beast. There's a 1min food check timer.</li>
 * <li>running out of the Beast Farm perimeter will vanish your tamed beast.</li>
 * <li>no need to force attack it, it's a normal monster.</li>
 * </ul>
 * This class handles the running tasks (such as skills use and feed) of the mob.
 */
public final class TamedBeast extends FeedableBeast
{
	protected int _foodId;
	protected Player _owner;
	
	public TamedBeast(int objectId, NpcTemplate template, Player owner, int foodId, Location loc)
	{
		super(objectId, template);
		
		disableCoreAi(true);
		getStatus().setMaxHpMp();
		setTitle(owner.getName());
		
		_owner = owner;
		_owner.setTamedBeast(this);
		
		_foodId = foodId;
		
		spawnMe(loc);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (_owner != null)
			_owner.setTamedBeast(null);
		
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		if (_owner != null)
			_owner.setTamedBeast(null);
		
		super.deleteMe();
	}
	
	@Override
	public TamedBeastAI getAI()
	{
		return (TamedBeastAI) _ai;
	}
	
	@Override
	public void setAI()
	{
		_ai = new TamedBeastAI(this);
	}
	
	public int getFoodId()
	{
		return _foodId;
	}
	
	public Player getOwner()
	{
		return _owner;
	}
}