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
package ext.mods.gameserver.model.actor.ai.type;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.ClanHallManagerNpc;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.ClanHallFunction;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.skills.L2Skill;

public class ClanHallManagerNpcAI extends NpcAI<ClanHallManagerNpc>
{
	private long _lastBuffCheckTime = 0;
	
	public ClanHallManagerNpcAI(ClanHallManagerNpc clanHallManager)
	{
		super(clanHallManager);
	}
	
	@Override
	public void thinkIdle()
	{
		if (System.currentTimeMillis() - _lastBuffCheckTime > 300000)
		{
			_lastBuffCheckTime = System.currentTimeMillis();
			L2Skill supportMagicSkill = SkillTable.getInstance().getInfo(4367, 1);
			final ClanHallFunction chfSM = _actor.getClanHall().getFunction(ClanHall.FUNC_SUPPORT);
			if (chfSM != null)
				supportMagicSkill = SkillTable.getInstance().getInfo(4366 + chfSM.getLvl(), 1);
			
			supportMagicSkill.getEffects(_actor, _actor);
		}
	}
	
	@Override
	protected void thinkCast()
	{
		if (_currentIntention.getFinalTarget().getActingPlayer() == null)
		{
			super.thinkCast();
			return;
		}
		
		final L2Skill skill = _currentIntention.getSkill();
		
		if (_actor.isSkillDisabled(skill))
			return;
		
		final Player player = (Player) _currentIntention.getFinalTarget();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(_actor.getObjectId());
		if (_actor.getStatus().getMp() < skill.getMpConsume() + skill.getMpInitialConsume())
			html.setFile(player.getLocale(), "html/clanHallManager/support-no_mana.htm");
		else
		{
			super.thinkCast();
			
			html.setFile(player.getLocale(), "html/clanHallManager/support-done.htm");
		}
		
		html.replace("%mp%", (int) _actor.getStatus().getMp());
		html.replace("%objectId%", _actor.getObjectId());
		player.sendPacket(html);
	}
	
	public void resetBuffCheckTime()
	{
		_lastBuffCheckTime = 0;
	}
}