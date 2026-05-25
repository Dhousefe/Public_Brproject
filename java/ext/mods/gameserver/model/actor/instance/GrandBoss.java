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

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.custom.data.BossHpAnnounceData;
import ext.mods.gameserver.data.manager.HeroManager;
import ext.mods.gameserver.data.manager.RaidPointManager;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

/**
 * This class manages all {@link GrandBoss}es.<br>
 * <br>
 * Those npcs inherit from {@link Monster}. Since a script is generally associated to it, {@link GrandBoss#returnHome} returns false to avoid misbehavior. No random walking is allowed.
 */
public final class GrandBoss extends Monster
{
	public GrandBoss(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		setRaidRelated();
	}
	
	@Override
	public int getSeeRange()
	{
		return getTemplate().getAggroRange();
	}
	
	@Override
	public boolean isRaidBoss()
	{
		return true;
	}
	
	@Override
	public void onSpawn()
	{
		setNoRndWalk(true);
		super.onSpawn();
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		final Player player = killer.getActingPlayer();
		if (player != null)
		{
			broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
			broadcastPacket(new PlaySound("systemmsg_e.1209"));
			
			if (Config.ANNOUNCE_DIE_GRANDBOSS)
			{
				if (player.getClan() != null)
					World.announceToOnlinePlayers(player.getSysString(10_235, getName(), getStatus().getLevel(), player.getName(), player.getClan().getName()));
				else
					World.announceToOnlinePlayers(player.getSysString(10_236, getName(), getStatus().getLevel(), player.getName()));
			}
			
			final Party party = player.getParty();
			if (party != null)
			{
				for (Player member : party.getMembers())
				{
					RaidPointManager.getInstance().addPoints(member, getNpcId(), (getStatus().getLevel() / 2) + Rnd.get(-5, 5));
					if (member.isNoble())
						HeroManager.getInstance().setRBkilled(member.getObjectId(), getNpcId());
				}
			}
			else
			{
				RaidPointManager.getInstance().addPoints(player, getNpcId(), (getStatus().getLevel() / 2) + Rnd.get(-5, 5));
				if (player.isNoble())
					HeroManager.getInstance().setRBkilled(player.getObjectId(), getNpcId());
			}
		}
		
		return true;
	}
	
	private int _lastAnnouncedHpPercent = 100;
	
	@Override
	public void reduceCurrentHp(double damage, Creature attacker, boolean awake, boolean isDOT, L2Skill skill)
	{
		super.reduceCurrentHp(damage, attacker, awake, isDOT, skill);
		
		if (!BossHpAnnounceData.getInstance().isAnnounceEnabledFor(getNpcId()) || getStatus().getMaxHp() <= 0)
			return;
		
		int currentPercent = (int) ((getStatus().getHp() / getStatus().getMaxHp()) * 100);
		
		if (currentPercent > _lastAnnouncedHpPercent)
			_lastAnnouncedHpPercent = 100;
		
		for (BossHpAnnounceData.HpThreshold threshold : BossHpAnnounceData.getInstance().getThresholds(getNpcId()))
		{
			if (currentPercent <= threshold.percent && _lastAnnouncedHpPercent > threshold.percent)
			{
				_lastAnnouncedHpPercent = threshold.percent;
				String msg = threshold.message.replace("%boss%", getName()).replace("%hp%", String.valueOf(threshold.percent));
				World.announceToOnlinePlayers(msg, true);
				break;
			}
		}
	}
	
	@Override
	public boolean canBeHealed() {
	    boolean block = false;
	     if (this instanceof GrandBoss && Config.BLOCK_HEAL_ON_GRANDBOSS) {
	        block = true;
	    }
	    return !block && super.canBeHealed();
	}
	
	@Override
	public boolean returnHome()
	{
		return false;
	}
}