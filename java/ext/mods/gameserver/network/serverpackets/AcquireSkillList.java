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

import java.util.List;

import ext.mods.gameserver.enums.skills.AcquireSkillType;
import ext.mods.gameserver.model.holder.skillnode.ClanSkillNode;
import ext.mods.gameserver.model.holder.skillnode.FishingSkillNode;
import ext.mods.gameserver.model.holder.skillnode.GeneralSkillNode;
import ext.mods.gameserver.model.holder.skillnode.SkillNode;

public final class AcquireSkillList extends L2GameServerPacket
{
	private final AcquireSkillType _type;
	private final List<? extends SkillNode> _skills;
	
	public AcquireSkillList(AcquireSkillType type, List<? extends SkillNode> skills)
	{
		_type = type;
		_skills = skills;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x8a);
		writeD(_type.ordinal());
		writeD(_skills.size());
		
		switch (_type)
		{
			case USUAL:
				_skills.stream().map(GeneralSkillNode.class::cast).forEach(gsn ->
				{
					writeD(gsn.getId());
					writeD(gsn.getValue());
					writeD(gsn.getValue());
					writeD(gsn.getCorrectedCost());
					writeD(0);
				});
				break;
			
			case FISHING:
				_skills.stream().map(FishingSkillNode.class::cast).forEach(gsn ->
				{
					writeD(gsn.getId());
					writeD(gsn.getValue());
					writeD(gsn.getValue());
					writeD(0);
					writeD(1);
				});
				break;
			
			case CLAN:
				_skills.stream().map(ClanSkillNode.class::cast).forEach(gsn ->
				{
					writeD(gsn.getId());
					writeD(gsn.getValue());
					writeD(gsn.getValue());
					writeD(gsn.getCost());
					writeD(0);
				});
				break;
		}
	}
}