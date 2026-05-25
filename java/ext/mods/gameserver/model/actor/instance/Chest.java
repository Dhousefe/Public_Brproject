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

import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.model.actor.template.NpcTemplate;

public final class Chest extends Monster
{
	private static int[] BOXES = new int[]
	{
		18265,
		18266,
		18267,
		18268,
		18269,
		18270,
		18271,
		18272,
		18273,
		18274,
		18275,
		18276,
		18277,
		18278,
		18279,
		18280,
		18281,
		18282,
		18283,
		18284,
		18285,
		18286,
		18287,
		18288,
		18289,
		18290,
		18291,
		18292,
		18293,
		18294,
		18295,
		18296,
		18297,
		18298
	};
	
	private final boolean _isBox;
	
	private volatile boolean _isInteracted;
	
	public Chest(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		_isBox = ArraysUtil.contains(BOXES, template.getNpcId());
		
		_isInteracted = false;
		
		setNoRndWalk(true);
		if (_isBox)
			disableCoreAi(true);
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		_isInteracted = false;
	}
	
	public boolean isBox()
	{
		return _isBox;
	}
	
	public boolean isInteracted()
	{
		return _isInteracted;
	}
	
	public void setInteracted()
	{
		_isInteracted = true;
	}
}