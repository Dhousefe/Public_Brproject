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
package ext.mods.gameserver.model;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.SellBuffsManager;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class SellBuffHolder
{
	private final int _skillId;
	private final int _skillLvl;
	private int _price;
	
	public SellBuffHolder(int skillId, int skillLvl, int price)
	{
		_skillId = skillId;
		_skillLvl = skillLvl;
		_price = price;
	}
	
	public int getSkillId()
	{
		return _skillId;
	}
	
	public int getSkillLvl()
	{
		return _skillLvl;
	}
	
	public void setPrice(int price)
	{
		_price = price;
	}
	
	public int getPrice()
	{
		return _price;
	}
	
	public L2Skill getSkill()
	{
		return SkillTable.getInstance().getInfo(_skillId, _skillLvl);
	}
	
	public L2Skill getSkillFrom(Player seller)
	{
		return switch (_skillId)
		{
			case 4699 -> normalize(SellBuffsManager.getInstance().getBuffSkill(seller, 1331, NpcSkillType.BUFF1));
			case 4700 -> normalize(SellBuffsManager.getInstance().getBuffSkill(seller, 1331, NpcSkillType.BUFF2));
			case 4702 -> normalize(SellBuffsManager.getInstance().getBuffSkill(seller, 1332, NpcSkillType.BUFF1));
			case 4703 -> normalize(SellBuffsManager.getInstance().getBuffSkill(seller, 1332, NpcSkillType.BUFF2));
			default -> normalize(seller.getSkill(_skillId));
		};
	}
	
	private L2Skill normalize(L2Skill actual)
	{
		if (actual == null || actual.getLevel() < _skillLvl)
			return null;
		else if (actual.getLevel() == _skillLvl)
			return actual;
		else
			return getSkill();
	}
	
	public int getSkillUse()
	{
		return switch (_skillId)
		{
			case 4699 -> 4700;
			default -> _skillId;
		};
	}
}