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
package ext.mods.gameserver.scripting.script.ai.individual;

import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.xml.DoorData;
import ext.mods.gameserver.enums.EventHandler;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.actors.ClassId;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.enums.actors.NpcSkillType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.Door;
import ext.mods.gameserver.model.records.PrivateData;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.AbstractEffect;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.skills.effects.EffectTemplate;

public class DefaultNpc extends Quest
{
	public DefaultNpc()
	{
		super(-1, "ai");
	}
	
	public DefaultNpc(String descr)
	{
		super(-1, descr);
	}
	
	protected static final int getAbnormalLevel(Creature creature, int skillId, int skillLevel)
	{
		return getAbnormalLevel(creature, SkillTable.getInstance().getInfo(skillId, skillLevel));
	}
	
	protected static final int getAbnormalLevel(Creature creature, L2Skill skill)
	{
		if (creature == null || skill == null)
			return -1;
		
		int abnormalLvl = -1;
		if (skill.getEffectTemplates() != null)
		{
			for (AbstractEffect effect : creature.getAllEffects())
			{
				for (EffectTemplate effectTemplate : skill.getEffectTemplates())
				{
					if (effect.getTemplate().getStackType() == effectTemplate.getStackType())
						abnormalLvl = Math.max(effect.getSkill().getAbnormalLvl(), (int) effect.getTemplate().getStackOrder());
				}
			}
		}
		
		return abnormalLvl;
	}
	
	protected static final L2Skill getNpcSkillByType(Npc npc, NpcSkillType npcSkillType)
	{
		return npc.getTemplate().getSkill(npcSkillType);
	}
	
	protected static final L2Skill getNpcSkillByTypeOrDefault(Npc npc, NpcSkillType npcSkillType, L2Skill defaultSkill)
	{
		final L2Skill skill = getNpcSkillByType(npc, npcSkillType);
		return (skill == null) ? defaultSkill : skill;
	}
	
	protected static final double getHateRatio(Npc npc, Creature attacker)
	{
		if (attacker instanceof Player player)
		{
			double ratio = 0.0;
			
			final String hateGroup = getNpcStringAIParam(npc, "SetHateGroup");
			if (hateGroup != null && ClassId.isInGroup(player, hateGroup))
				ratio += getNpcIntAIParam(npc, "SetHateGroupRatio");
			
			final String hateOccupation = getNpcStringAIParam(npc, "SetHateOccupation");
			if (hateOccupation != null && ClassId.isSameOccupation(player, hateOccupation))
				ratio += getNpcIntAIParam(npc, "SetHateOccupationRatio");
			
			final String hateRace = getNpcStringAIParam(npc, "SetHateRace");
			if (hateRace != null && ClassRace.isSameRace(player, hateRace))
				ratio += getNpcIntAIParam(npc, "SetHateRaceRatio");
			
			return ratio;
		}
		return 0.;
	}
	
	public static final int getNpcIntAIParam(Npc npc, String name)
	{
		return getNpcIntAIParamOrDefault(npc, name, 0);
	}
	
	protected static final int getNpcIntAIParamOrDefault(Npc npc, String name, int defaultValue)
	{
		try
		{
			return npc.getSpawn().getMemo().getInteger(name, npc.getTemplate().getAiParams().getInteger(name, defaultValue));
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}
	}
	
	public static final String getNpcStringAIParam(Npc npc, String name)
	{
		return getNpcStringAIParamOrDefault(npc, name, null);
	}
	
	protected static final String getNpcStringAIParamOrDefault(Npc npc, String name, String defaultValue)
	{
		return npc.getSpawn().getMemo().getOrDefault(name, npc.getTemplate().getAiParams().getOrDefault(name, defaultValue));
	}
	
	public static final L2Skill getNpcSkillAIParam(Npc npc, String name)
	{
		return getNpcSkillAIParamOrDefault(npc, name, null);
	}
	
	protected static final L2Skill getNpcSkillAIParamOrDefault(Npc npc, String name, String defaultValue)
	{
		final String skillStr = npc.getSpawn().getMemo().getOrDefault(name, npc.getTemplate().getAiParams().getOrDefault(name, defaultValue));
		
		if (skillStr != null)
		{
			final String[] splitString = skillStr.split("-");
			
			final int skillId = Integer.parseInt(splitString[0]);
			final int level = Integer.parseInt(splitString[1]);
			
			return SkillTable.getInstance().getInfo(skillId, level);
		}
		
		return null;
	}
	
	protected static boolean maybeCastPetrify(Npc npc, Creature attacker)
	{
		final Player player = attacker.getActingPlayer();
		
		if (player == null)
			return false;
		
		boolean castPertify = false;
		
		if (player.isAlliedWithVarka() && ArraysUtil.contains(npc.getTemplate().getClans(), "varka_silenos_clan"))
			castPertify = true;
		
		if (player.isAlliedWithKetra() && ArraysUtil.contains(npc.getTemplate().getClans(), "ketra_orc_clan"))
			castPertify = true;
		
		return castPertify;
	}
	
	protected static final void tryToAttack(Npc npc, Creature creature)
	{
		if (!npc.isInMyTerritory())
			return;
		
		final int aggressiveTime = getNpcIntAIParam(npc, "SetAggressiveTime");
		if (aggressiveTime == -1)
		{
			if (npc.getAI().getLifeTime() >= (Rnd.get(5) + 3))
				npc.getAI().addAttackDesire(creature, 200);
		}
		else if (aggressiveTime == 0)
			npc.getAI().addAttackDesire(creature, 200);
		else if (npc.getAI().getLifeTime() > (aggressiveTime + Rnd.get(4)))
			npc.getAI().addAttackDesire(creature, 200);
		else
			npc.getAI().addAttackDesire(creature, 200);
	}
	
	protected final void createPrivates(Npc npc)
	{
		if (!npc.getPrivateData().isEmpty())
		{
			npc.getMinions().clear();
			
			for (PrivateData pd : npc.getPrivateData())
			{
				Npc pvt = createOnePrivate(npc, pd.id(), 0, false);
				pvt.getSpawn().setRespawnDelay(pd.respawnTime());
				pvt._weightPoint = pd.weight();
			}
		}
	}
	
	protected final void makeAttackEvent(Npc npc, Creature attacker, int damage, boolean generatePartyAttacked)
	{
		onAttacked(npc, attacker, damage, null);
		
		if (generatePartyAttacked)
		{
			if (npc.isMaster() || npc.hasMaster())
			{
				final Npc master = npc.getMaster();
				if (master != null && !master.isDead() && npc != master)
				{
					for (Quest quest : master.getTemplate().getEventQuests(EventHandler.PARTY_ATTACKED))
						quest.onPartyAttacked(npc, master, attacker, damage);
				}
				
				for (Npc minion : npc.getMinions())
				{
					if (minion == npc || minion.isDead())
						continue;
					
					for (Quest quest : minion.getTemplate().getEventQuests(EventHandler.PARTY_ATTACKED))
						quest.onPartyAttacked(npc, minion, attacker, damage);
				}
			}
		}
	}
	
	protected static void openCloseDoor(String doorName, int state)
	{
		final Door door = DoorData.getInstance().getDoor(doorName);
		if (door == null)
			return;
		
		if (state == 0)
			door.openMe();
		else
			door.closeMe();
	}
	
	protected static int getPledgeCastleState(Npc npc, Creature creature)
	{
		if (!(creature instanceof Playable))
			return 0;
		
		final Player player = creature.getActingPlayer();
		if (player.getClan() == null || npc.getCastle() == null)
			return 0;
		
		if (npc.getCastle().getSiege().isInProgress())
		{
			if (npc.getCastle().getSiege().checkSides(player.getClan(), SiegeSide.DEFENDER, SiegeSide.OWNER))
				return 2;
			
			if (npc.getCastle().getSiege().checkSides(player.getClan(), SiegeSide.ATTACKER))
				return 1;
		}
		
		if (npc.getCastle().getId() == player.getClan().getCastleId())
			return 2;
		
		return 0;
	}
}