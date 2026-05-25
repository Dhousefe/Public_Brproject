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

import ext.mods.commons.pool.ThreadPool;

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
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;

/**
 * The Fortress of the Dead is located southeast of the Rune Township and is a contested hideout similar to the siege style of the Devastated Castle clan hall. It is of the highest grade among all contested clan halls.<br>
 * <br>
 * Only a clan level 4 or higher may participate.<br>
 * <br>
 * Siege registration is open up to two hours before a war and is scheduled through the messenger NPC outside of the clan hall.<br>
 * <br>
 * The siege war follows the same rules as Devastated Castle. The siege war goes on for one hour, and the clan that contributes the most to killing Lidia von Hellmann takes possession of the clan hall. If the followers of Lidia von Hellmann, Alfred von Hellmann, and Giselle von Hellmann are killed,
 * the clan hall war will be a lot easier.<br>
 * <br>
 * The siege war ends upon the death of Lidia von Hellmann; just as in Devastated Castle, and if there is no clan that has killed the applicable NPC, the clan hall is under the NPC's possession until the next siege war.<br>
 * <br>
 * The clan that owned the clan hall previously will be automatically registered in the next clan hall war.<br>
 * <br>
 * The possessing clan hall leader can ride on a wyvern.
 */
public final class FortressOfTheDead extends ClanHallSiege
{
	private static final int LIDIA = 35629;
	private static final int ALFRED = 35630;
	private static final int GISELLE = 35631;
	
	private static final int VAMPIRE_SOLDIER = 35633;
	private static final int VAMPIRE_CASTER = 35634;
	private static final int VAMPIRE_MAGISTER = 35635;
	private static final int VAMPIRE_WARLORD = 35636;
	private static final int VAMPIRE_LEADER_1 = 35637;
	private static final int VAMPIRE_LEADER_2 = 35647;
	
	private final Map<Integer, Integer> _damageToLidia = new ConcurrentHashMap<>();
	
	public FortressOfTheDead()
	{
		super("siegablehall", FORTRESS_OF_DEAD);
		
		addAttacked(LIDIA);
		addCreated(LIDIA, ALFRED, GISELLE);
		addMyDying(LIDIA, ALFRED, GISELLE);
		addNoDesire(LIDIA, ALFRED, GISELLE, VAMPIRE_SOLDIER, VAMPIRE_CASTER, VAMPIRE_MAGISTER, VAMPIRE_WARLORD, VAMPIRE_LEADER_1, VAMPIRE_LEADER_2);
		addPartyDied(LIDIA, ALFRED, GISELLE);
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equals("1001"))
		{
			npc.getAI().addCastDesire(npc, 4997, 1, 1000000);
			
			if (npc.getScriptValue() > 1)
				cancelQuestTimer("1001", npc, null);
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
			_damageToLidia.merge(clan.getClanId(), damage, Integer::sum);
		
		if (!npc.isInMyTerritory())
		{
			((Attackable) npc).getAI().getAggroList().cleanAllHate();
			npc.teleportTo(npc.getSpawnLocation(), 0);
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		switch (npc.getNpcId())
		{
			case LIDIA:
				npc.broadcastNpcShout(NpcStringId.ID_1010624);
				startQuestTimerAtFixedRate("1001", npc, null, 0, 30000);
				break;
			
			case ALFRED:
				npc.broadcastNpcShout(NpcStringId.ID_1010636);
				break;
			
			case GISELLE:
				npc.broadcastNpcShout(NpcStringId.ID_1010637);
				break;
		}
		super.onCreated(npc);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		switch (npc.getNpcId())
		{
			case LIDIA:
				npc.broadcastNpcShout(NpcStringId.ID_1010638);
				
				if (_hall.isInSiege())
				{
					_missionAccomplished = true;
					
					cancelSiegeTask();
					endSiege();
				}
				break;
			
			case ALFRED:
				npc.broadcastNpcShout(NpcStringId.ID_1010625);
				break;
			
			case GISELLE:
				npc.broadcastNpcShout(NpcStringId.ID_1010625);
				break;
		}
		super.onMyDying(npc, killer);
	}
	
	@Override
	public void onNoDesire(Npc npc)
	{
	}
	
	@Override
	public void onPartyDied(Npc caller, Npc called)
	{
		if (called.getNpcId() == LIDIA)
		{
			switch (caller.getNpcId())
			{
				case ALFRED, GISELLE:
					called.setScriptValue(called.getScriptValue() + 1);
					break;
			}
		}
		super.onPartyDied(caller, called);
	}
	
	@Override
	public Clan getWinner()
	{
		if (_damageToLidia.isEmpty())
			return null;
		
		final int clanId = Collections.max(_damageToLidia.entrySet(), Map.Entry.comparingByValue()).getKey();
		
		_damageToLidia.clear();
		
		return ClanTable.getInstance().getClan(clanId);
	}
	
	@Override
	public void startSiege()
	{
		final int hoursLeft = (GameTimeTaskManager.getInstance().getGameTime() / 60) % 24;
		if (hoursLeft < 0 || hoursLeft > 6)
		{
			cancelSiegeTask();
			
			long scheduleTime = (24 - hoursLeft) * 600000L;
			_siegeTask = ThreadPool.schedule(this::startSiege, scheduleTime);
		}
		else
			super.startSiege();
	}
	
	@Override
	public void spawnNpcs()
	{
		SpawnManager.getInstance().startSpawnTime("agit_defend_warfare_start", "64", null, null, true);
	}
	
	@Override
	public void unspawnNpcs()
	{
		SpawnManager.getInstance().stopSpawnTime("agit_defend_warfare_start", "64", null, null, true);
	}
}