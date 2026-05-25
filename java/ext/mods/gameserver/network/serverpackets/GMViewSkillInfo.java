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

import java.util.Collection;

import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class GMViewSkillInfo extends L2GameServerPacket
{
	private final Player _player;
	private final Collection<L2Skill> _skills;
	private final boolean _isWearingFormalWear;
	private final boolean _isClanDisabled;
	
	public GMViewSkillInfo(Player player)
	{
		_player = player;
		_skills = player.getSkills().values();
		_isWearingFormalWear = player.isWearingFormalWear();
		_isClanDisabled = player.getClan() != null && player.getClan().getReputationScore() < 0;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x91);
		writeS(_player.getName());
		writeD(_skills.size());
		
		for (L2Skill skill : _skills)
		{
			writeD(skill.isPassive() ? 1 : 0);
			writeD(skill.getLevel());
			writeD(skill.getId());
			writeC(_isWearingFormalWear || (skill.isClanSkill() && _isClanDisabled) ? 1 : 0);
		}
	}
}