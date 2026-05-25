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
package ext.mods.gameserver.network.serverpackets;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.skills.L2Skill;

public class MagicSkillLaunched extends L2GameServerPacket
{
	private final int _objectId;
	private final int _skillId;
	private final int _skillLevel;
	private Creature[] _targets;
	
	public MagicSkillLaunched(Creature cha, L2Skill skill, Creature[] targets)
	{
		_objectId = cha.getObjectId();
		_skillId = skill.getId();
		_skillLevel = skill.getLevel();
		_targets = targets;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x76);
		writeD(_objectId);
		writeD(_skillId);
		writeD(_skillLevel);
		
		if (_targets.length == 0)
		{
			writeD(0);
			writeD(0);
		}
		else
		{
			writeD(_targets.length);
			for (Creature target : _targets)
				writeD(target.getObjectId());
		}
	}
}