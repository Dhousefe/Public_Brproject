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
package ext.mods.gameserver.model.entity.autofarm.zone;

import java.util.List;
import java.util.Set;

import ext.mods.gameserver.model.entity.autofarm.AutoFarmManager.AutoFarmType;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.network.serverpackets.ExServerPrimitive;

public class AutoFarmOpen extends AutoFarmArea
{
	public AutoFarmOpen(int ownerId)
	{
		super(1, "Open", ownerId, AutoFarmType.OPEN);
	}
	
	@Override
	public List<Monster> getMonsters()
	{
		return getOwner().getKnownTypeInRadius(Monster.class, getProfile().getFinalRadius());
	}
	
	@Override
	public Set<String> getMonsterHistory()
	{
		_monsterHistory.addAll(getMonsters().stream().map(Monster::getName).toList());
		return _monsterHistory;
	}
	
	@Override
	public void visualizeZone(ExServerPrimitive debug)
	{
	}
}