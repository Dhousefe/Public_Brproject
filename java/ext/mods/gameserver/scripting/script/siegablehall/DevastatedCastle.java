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
package ext.mods.gameserver.scripting.script.siegablehall;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.actor.Attackable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.clanhall.ClanHallSiege;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.skills.L2Skill;

/**
 * The Devastated Castle is an occupied clan hall for which ownership can be acquired by siege battle with NPCs. The siege is set up to take place with the NPCs every week, which is different from other sieges. The clan hall battle for the Devastated Castle is always defended by an NPC clan and the
 * ownership to the castle is maintained for one week by the PC clan that is able to defeat the defending NPC clan and take the castle.<br>
 * <br>
 * <b>Time of the Occupying Battle</b><br>
 * <br>
 * The time when the Devastated Castle occupying battle takes place is at the same time each week after the very first occupation battle.<br>
 * <br>
 * <b>Registration Participation</b><br>
 * <br>
 * Clan that wants to participate in the battle to occupy and acquire the clan hall must register to participate in advance. Participation in the battle to occupy takes place in clan units and only clan leaders having clans of level 4 or above can apply to register. Clans that already own another
 * clan hall cannot register. The clan that had owned the Devastated Castle previously is automatically registered for the siege and the registration for the battle to occupy must be done at least two hours before the start time. Clans that participate in the battle to occupy the Devastated Castle
 * cannot participate in another battle to occupy another clan hall or siege at the same time.<br>
 * <br>
 * <b>Start of the Siege Battle</b><br>
 * <br>
 * When the time set for the siege starts, the area inside and around the Devastated Castle is designated as the battleground and the players that are already inside the castle are all kicked to the outside. Leader of the clans that are registered in the battle to occupy the castle can build a
 * headquarters.The rules of battle during the battle to occupy the castle are the same as ordinary sieges except for the fact that everyone is on the attacking side and not on the defending side. However, even if the headquarters are destroyed or an unregistered character enters the battlefield and
 * is killed, players are not restarted at the second closest village like in a siege.<br>
 * <br>
 * <b>Conditions of Victory</b><br>
 * <br>
 * A battle to occupy lasts for one hour and the clan recorded as having contributed the most to killing the head NPC of the Devastated Castle becomes the owner of the clan hall. If the leader is killed, the battle to occupy ends immediately. If no clan is able to kill the leader within the time
 * period, the Devastated Castle continues to be occupied by the NPCs until the next battle to occupy.
 */
public final class DevastatedCastle extends ClanHallSiege
{
	private static final int DIETRICH = 35408;
	private static final int MIKHAIL = 35409;
	private static final int GUSTAV = 35410;
	
	private static final int DOOM_SERVANT = 35411;
	private static final int DOOM_GUARD = 35412;
	private static final int DOOM_ARCHER = 35413;
	private static final int DOOM_TROOPER = 35414;
	private static final int DOOM_WARRIOR = 35415;
	private static final int DOOM_KNIGHT = 35416;
	
	private final Map<Integer, Integer> _damageToGustav = new ConcurrentHashMap<>();
	
	private Player _speller;
	
	public DevastatedCastle()
	{
		super("siegablehall", DEVASTATED_CASTLE);
		
		addAttacked(GUSTAV);
		addCreated(DIETRICH, MIKHAIL, GUSTAV);
		addMyDying(GUSTAV);
		addNoDesire(DIETRICH, MIKHAIL, GUSTAV, DOOM_SERVANT, DOOM_GUARD, DOOM_ARCHER, DOOM_TROOPER, DOOM_WARRIOR, DOOM_KNIGHT);
		addPartyAttacked(DIETRICH, MIKHAIL, GUSTAV, DOOM_ARCHER, DOOM_KNIGHT);
		addUseSkillFinished(DIETRICH, MIKHAIL);
		addSeeSpell(DIETRICH);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equals("1001"))
		{
			if (!npc.isInMyTerritory() && Rnd.get(3) < 1)
			{
				((Attackable) npc).getAI().getAggroList().cleanAllHate();
				npc.teleportTo(npc.getSpawnLocation(), 0);
			}
			
			if (Rnd.get(5) < 1)
				((Attackable) npc).getAI().getAggroList().randomizeAttack();
		}
		else if (name.equals("1002"))
		{
			npc.doDie(npc);
		}
		return super.onTimer(name, npc, player);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!_hall.isInSiege() || !(attacker instanceof Playable))
			return;
		
		final Clan clan = attacker.getActingPlayer().getClan();
		if (clan != null && getAttackerClans().contains(clan))
			_damageToGustav.merge(clan.getClanId(), damage, Integer::sum);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		switch (npc.getNpcId())
		{
			case DIETRICH:
				npc.broadcastNpcShout(NpcStringId.ID_1000277);
				break;
			
			case MIKHAIL:
				npc.broadcastNpcShout(NpcStringId.ID_1000276);
				break;
			
			case GUSTAV:
				npc.broadcastNpcShout(NpcStringId.ID_1000275);
				break;
		}
		
		startQuestTimer("1001", npc, null, 60000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (_hall.isInSiege())
		{
			_missionAccomplished = true;
			
			cancelSiegeTask();
			endSiege();
		}
		super.onMyDying(npc, killer);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
	}
	
	@Override
	public void onPartyAttacked(Npc caller, Npc called, Creature attacker, int damage)
	{
		switch (called.getNpcId())
		{
			case DIETRICH:
				if (_speller != null && Rnd.get(3) < 1 && called.getSpawn().isInMyTerritory(_speller))
					called.getAI().addCastDesire(_speller, 4238, 1, 1000000);
				
				if (called.isScriptValue(0) && called.getMaster().getStatus().getHpRatio() < 0.05)
				{
					called.setScriptValue(1);
					called.getAI().addCastDesire(called, 4235, 1, 1000000);
					called.broadcastNpcSay(NpcStringId.ID_1000280);
				}
				break;
			
			case MIKHAIL:
				if (Rnd.get(3) < 1)
					called.getAI().addCastDesire(attacker, 4237, 1, 1000000);
				
				if (called.isScriptValue(0) && called.getMaster().getStatus().getHpRatio() < 0.05)
				{
					called.setScriptValue(1);
					called.getAI().addCastDesire(called, 4235, 1, 1000000);
					called.broadcastNpcSay(NpcStringId.ID_1000279);
				}
				break;
			
			case GUSTAV:
				if (Rnd.get(3) < 1)
					called.getAI().addCastDesire(attacker, 4236, 1, 1000000);
				
				if (called.isScriptValue(0) && called.getStatus().getHpRatio() < 0.05)
				{
					called.setScriptValue(1);
					called.getAI().addCastDesire(called, 4235, 1, 1000000);
					called.broadcastNpcSay(NpcStringId.ID_1000278);
				}
				break;
		}
		super.onPartyAttacked(caller, called, attacker, damage);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		switch (caster.getClassId())
		{
			case BISHOP, PROPHET, ELVEN_ELDER, SHILLIEN_ELDER, OVERLORD, WARCRYER:
				_speller = caster;
				break;
		}
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill.getId() == 4235)
		{
			((Attackable) npc).getAI().getAggroList().cleanAllHate();
			npc.teleportTo(177134, -18807, -2263, 0);
			
			if (npc.getNpcId() == GUSTAV)
				startQuestTimer("1002", npc, null, 3000);
		}
		super.onUseSkillFinished(npc, creature, skill, success);
	}
	
	@Override
	public Clan getWinner()
	{
		if (_damageToGustav.isEmpty())
			return null;
		
		final int clanId = Collections.max(_damageToGustav.entrySet(), Map.Entry.comparingByValue()).getKey();
		
		_damageToGustav.clear();
		
		return ClanTable.getInstance().getClan(clanId);
	}
	
	@Override
	public void spawnNpcs()
	{
		SpawnManager.getInstance().startSpawnTime("agit_defend_warfare_start", "34", null, null, true);
	}
	
	@Override
	public void unspawnNpcs()
	{
		SpawnManager.getInstance().stopSpawnTime("agit_defend_warfare_start", "34", null, null, true);
	}
}