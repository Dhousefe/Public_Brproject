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

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.data.manager.SpawnManager;
import ext.mods.gameserver.data.sql.ClanTable;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.residence.clanhall.ClanHallSiege;
import ext.mods.gameserver.skills.L2Skill;

/**
 * Fortress of Resistance clan hall siege Script.
 */
public final class FortressOfResistance extends ClanHallSiege
{
	private static final int BRAKEL = 35382;
	
	private static final int BLOODY_LORD_NURKA_1 = 35368;
	private static final int BLOODY_LORD_NURKA_2 = 35375;
	
	private static final int PARTISAN_HEALER = 35369;
	private static final int PARTISAN_COURT_GUARD_1 = 35370;
	private static final int PARTISAN_COURT_GUARD_2 = 35371;
	private static final int PARTISAN_SOLDIER = 35372;
	private static final int PARTISAN_SORCERER = 35373;
	private static final int PARTISAN_ARCHER = 35374;
	
	private final Map<Integer, Integer> _damageToNurka = new ConcurrentHashMap<>();
	
	public FortressOfResistance()
	{
		super("siegablehall", FORTRESS_OF_RESISTANCE);
		
		addFirstTalkId(BRAKEL);
		
		addAttacked(BLOODY_LORD_NURKA_1, BLOODY_LORD_NURKA_2);
		addClanAttacked(BLOODY_LORD_NURKA_1, BLOODY_LORD_NURKA_2, PARTISAN_HEALER, PARTISAN_COURT_GUARD_1, PARTISAN_COURT_GUARD_2, PARTISAN_SOLDIER, PARTISAN_SORCERER, PARTISAN_ARCHER);
		addCreated(BLOODY_LORD_NURKA_1, BLOODY_LORD_NURKA_2);
		addMyDying(BLOODY_LORD_NURKA_1, BLOODY_LORD_NURKA_2);
		addNoDesire(PARTISAN_SOLDIER);
		addSeeSpell(BLOODY_LORD_NURKA_1, BLOODY_LORD_NURKA_2);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return getHtmlText("partisan_ordery_brakel001.htm", player).replace("%nextSiege%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_hall.getSiegeDate().getTime()));
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (!_hall.isInSiege() || !(attacker instanceof Playable))
			return;
		
		if (Rnd.get(100) < 10)
			npc.getAI().addCastDesire(attacker, 4042, 1, 1000000);
		
		final Clan clan = attacker.getActingPlayer().getClan();
		if (clan != null && !clan.hasClanHall())
			_damageToNurka.merge(clan.getClanId(), damage, Integer::sum);
		
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onClanAttacked(Npc caller, Npc called, Creature attacker, int damage, L2Skill skill)
	{
		switch (called.getNpcId())
		{
			case BLOODY_LORD_NURKA_1, BLOODY_LORD_NURKA_2:
				if (Rnd.get(100) < 2)
					called.getAI().addCastDesire(attacker, 4042, 1, 1000000);
				break;
		}
		super.onClanAttacked(caller, called, attacker, damage, skill);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.getSpawn().instantTeleportInMyTerritory(51952, 111060, -1970, 200);
		
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
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (Rnd.get(100) < 15)
			npc.getAI().addCastDesire(caster, 4042, 1, 1000000);
		
		super.onSeeSpell(npc, caster, skill, targets, isPet);
	}
	
	@Override
	public Clan getWinner()
	{
		if (_damageToNurka.isEmpty())
			return null;
		
		final int clanId = Collections.max(_damageToNurka.entrySet(), Map.Entry.comparingByValue()).getKey();
		
		_damageToNurka.clear();
		
		return ClanTable.getInstance().getClan(clanId);
	}
	
	@Override
	public void spawnNpcs()
	{
		SpawnManager.getInstance().startSpawnTime((_wasPreviouslyOwned) ? "agit_attack_warfare_start" : "agit_defend_warfare_start", "21", null, null, true);
	}
	
	@Override
	public void unspawnNpcs()
	{
		SpawnManager.getInstance().stopSpawnTime((_wasPreviouslyOwned) ? "agit_attack_warfare_start" : "agit_defend_warfare_start", "21", null, null, true);
	}
	
	@Override
	public void loadAttackers()
	{
	}
}