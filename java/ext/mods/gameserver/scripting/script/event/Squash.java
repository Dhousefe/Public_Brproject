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
package ext.mods.gameserver.scripting.script.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.custom.data.EventsData;
import ext.mods.gameserver.data.manager.EventsDropManager;
import ext.mods.gameserver.data.manager.EventsDropManager.RuleType;
import ext.mods.gameserver.data.xml.SysString;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.kind.Weapon;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.model.records.custom.EventsInfo;
import ext.mods.gameserver.network.serverpackets.CreatureSay;
import ext.mods.gameserver.skills.L2Skill;

public class Squash extends Events
{
	private static boolean ACTIVE = false;
	
	private List<Npc> _npclist;
	
	private static final int MANAGER = 31255;
	private static final int NECTAR_SKILL = 2005;
	
	private static final List<Integer> SQUASH_LIST = Arrays.asList(12774, 12775, 12776, 12777, 12778, 12779, 13016, 13017);
	private static final List<Integer> CHRONO_SQUASH_LIST = Arrays.asList(12777, 12778, 12779, 13017);
	private static final List<Integer> CHRONO_LIST = Arrays.asList(4202, 5133, 5817, 7058, 8350);
	
	public static boolean isValidCreature(Creature creature)
	{
		if (creature instanceof Npc npc)
		{
			for (final int id : SQUASH_LIST)
			{
				if (npc.getNpcId() == id)
					return true;
			}
		}
		return false;
	}
	
	public static boolean isValidWeapon(Player player)
	{
		Weapon weapon = player.getActiveWeaponItem();
		for (int id : CHRONO_LIST)
		{
			if (weapon.getItemId() == id)
				return true;
		}
		return false;
	}
	
	private static final String[] NOCHRONO_TEXT =
	{
		"10121",
		"10122",
		"10123",
		"10124",
		"10125"
	};
	
	private static final String[] CHRONO_TEXT =
	{
		"10126",
		"10127",
		"10128",
		"10129",
		"10130"
	};
	
	private static final String[] NECTAR_TEXT =
	{
		"10131",
		"10132",
		"10133",
		"10134",
		"10135",
		"10136"
	};
	
	private static final int[][] DROPLIST =
	{
		/* 
		 * @formatter:off
		 */
		{ 12774, 100, 6391, 2 },
		{ 12776, 100, 6391, 10 },
		{ 12775, 100, 6391, 30 },
		{ 13016, 100, 6391, 50 },
		{ 12777, 100, 1540, 2, 1539, 2 },
		{ 12779, 50, 729, 4, 730, 4, 6569, 2, 6570, 2 },
		{ 12779, 30, 6622, 1 },
		{ 12779, 10, 8750, 1 },
		{ 12779, 10, 8751, 1 },
		{ 12779, 99, 1540, 4, 1539, 4 },
		{ 12779, 50, 1461, 4 },
		{ 12779, 30, 1462, 3 },
		{ 12779, 50, 2133, 4 },
		{ 12779, 30, 2134, 3 },
		{ 12778, 7, 5577, 1, 5578, 1, 5579, 1, 5580, 1, 5581, 1, 5582, 1, 5908, 1, 5911, 1, 5914, 1 },
		{ 12778, 35, 729, 4, 730, 4, 959, 3, 960, 3, 6569, 2, 6570, 2, 6577, 1, 6578, 1 },
		{ 12778, 28, 6622, 3, 6622, 2, 6622, 1},
		{ 12778, 14, 8750, 10 },
		{ 12778, 14, 8751, 8 },
		{ 12778, 14, 8752, 6 },
		{ 12778, 21, 8760, 1, 8761, 1, 8762, 1 },
		{ 12778, 21, 8623, 1, 8624, 1, 8625, 1, 8626, 1, 8627, 1, 8629, 1, 8630, 1, 8631, 1, 8632, 1, 8633, 1, 8635, 1, 8636, 1, 8637, 1, 8638, 1, 8639, 1 },
		{ 12778, 99, 1540, 9, 1539, 9},
		{ 12778, 63, 1461, 8},
		{ 12778, 49, 1462, 5},
		{ 12778, 63, 2133, 6},
		{ 12778, 49, 2134, 4},
		{ 13017, 10, 5577, 1, 5578, 1, 5579, 1, 5580, 1, 5581, 1, 5582, 1, 5908, 1, 5911, 1, 5914, 1},
		{ 13017, 50, 729, 4, 730, 4, 959, 3, 960, 3, 6569, 2, 6570, 2, 6577, 1, 6578, 1},
		{ 13017, 40, 6622, 3, 6622, 2, 6622, 1},
		{ 13017, 20, 8750, 10},
		{ 13017, 20, 8751, 8},
		{ 13017, 20, 8752, 6},
		{ 13017, 30, 8760, 1, 8761, 1, 8762, 1},
		{ 13017, 30, 8623, 1, 8624, 1, 8625, 1, 8626, 1, 8627, 1, 8629, 1, 8630, 1, 8631, 1, 8632, 1, 8633, 1, 8635, 1, 8636, 1, 8637, 1, 8638, 1, 8639, 1},
		{ 13017, 99, 1540, 12, 1539, 12},
		{ 13017, 90, 1461, 8 },
		{ 13017, 70, 1462, 5 },
		{ 13017, 90, 2133, 6 },
		{ 13017, 70, 2134, 4 },
		/* 
		 * @formatter:on
		 */
	};
	
	private static final SpawnLocation[] _coords =
	{
		new SpawnLocation(-84267, 243246, -3729, 16000),
		new SpawnLocation(-44813, -113364, -202, 16000),
		new SpawnLocation(45235, 49396, 3068, 38000),
		new SpawnLocation(114702, -178369, -820, 0),
		new SpawnLocation(10925, 15968, -4574, 0),
		new SpawnLocation(83075, 148394, -3472, 32768),
		new SpawnLocation(148059, -56554, -2781, 21000),
		new SpawnLocation(19000, 145783, -3081, 49152),
		new SpawnLocation(-14052, 123219, -3117, 32000),
		new SpawnLocation(86979, -142136, -1343, 55000),
		new SpawnLocation(116468, 75321, -2712, 16000),
		new SpawnLocation(147146, 26536, -2205, 16000),
		new SpawnLocation(43488, -48421, -797, 16500),
		new SpawnLocation(81963, 53937, -1496, 0),
		new SpawnLocation(-81634, 150195, -3132, 16500),
		new SpawnLocation(111624, 219228, -3543, 48000),
		new SpawnLocation(17559, 170318, -3506, 17000)
	};
	
	private void addDrop()
	{
		final EventsInfo event = EventsData.getInstance().getEventsData(getName());
		
		EventsDropManager.getInstance().addSquashRule(getName(), RuleType.ALL_NPC, event.items());
	}
	
	private void removeDrop()
	{
		EventsDropManager.getInstance().removeSquashRules(getName());
	}
	
	public Squash()
	{
		for (int mob : SQUASH_LIST)
		{
			addAttacked(mob);
			addMyDying(mob);
			addCreated(mob);
			addCreated(CHRONO_SQUASH_LIST);
			addSeeSpell(mob);
		}
		
		addQuestStart(MANAGER);
		addFirstTalkId(MANAGER);
		addTalkId(MANAGER);
	}
	
	@Override
	public boolean eventStart(int priority)
	{
		if (ACTIVE)
			return false;
		
		ACTIVE = true;
		
		_npclist = new ArrayList<>();
		
		for (SpawnLocation loc : _coords)
			recordSpawn(MANAGER, loc);
		
		eventStatusStart(priority);
		addDrop();
		
		World.announceToOnlinePlayers(10_159, getName());
		return true;
	}
	
	@Override
	public boolean eventStop()
	{
		if (!ACTIVE)
			return false;
		
		ACTIVE = false;
		
		eventStatusStop();
		
		if (!_npclist.isEmpty())
			_npclist.forEach(npc -> npc.deleteMe());
		
		_npclist.clear();
		
		removeDrop();
		
		World.announceToOnlinePlayers(10_160, getName());
		return true;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return npc.getNpcId() + ".htm";
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (CHRONO_SQUASH_LIST.contains(npc.getNpcId()))
		{
			if ((attacker.getActiveWeaponItem() != null) && CHRONO_LIST.contains(attacker.getActiveWeaponItem().getItemId()))
			{
				chronoText(npc, (Player) attacker);
				npc.setInvul(false);
				npc.getStatus().reduceHp(10, attacker);
			}
			else
			{
				noChronoText(npc, (Player) attacker);
				npc.setInvul(true);
			}
		}
		super.onAttacked(npc, attacker, damage, skill);
	}
	
	@Override
	public void onSeeSpell(Npc npc, Player caster, L2Skill skill, Creature[] targets, boolean isPet)
	{
		if (skill.getId() == NECTAR_SKILL && targets[0] == npc)
		{
			if (SQUASH_LIST.contains(npc.getNpcId()) && skill.getId() == NECTAR_SKILL)
			{
				switch (npc.getNpcId())
				{
					case 12774:
						randomSpawn(13016, 12775, 12776, npc, caster);
						break;
					case 12777:
						randomSpawn(13017, 12778, 12779, npc, caster);
						break;
				}
			}
		}
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		if (SQUASH_LIST.contains(npc.getNpcId()))
			dropItem(npc, (Player) killer);
		
		super.onMyDying(npc, killer);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc.setIsImmobilized(true);
		npc.disableCoreAi(true);
		
		if (CHRONO_SQUASH_LIST.contains(npc.getNpcId()))
			npc.setInvul(true);
		
		return;
	}
	
	static
	{
		Arrays.sort(DROPLIST, new Comparator<int[]>()
		{
			@Override
			public int compare(int[] a, int[] b)
			{
				return a[0] - b[0];
			}
		});
	}
	
	private static final void dropItem(Npc mob, Player player)
	{
		final int npcId = mob.getNpcId();
		final int chance = Rnd.get(100);
		
		for (int[] drop : DROPLIST)
		{
			if (npcId == drop[0])
			{
				if (chance < drop[1])
				{
					int i = 2 + (2 * Rnd.get((drop.length - 2) / 2));
					int itemId = drop[i + 0];
					int itemQty = drop[i + 1];
					if (itemQty > 1)
						itemQty = Rnd.get(1, itemQty);
					
					mob.dropItem(player, itemId, itemQty);
					continue;
				}
			}
			
			if (npcId < drop[0])
				return;
		}
	}
	
	private void randomSpawn(int low, int medium, int high, Npc npc, Player player)
	{
		final int random = Rnd.get(100);
		if (random < 5)
			spawnNext(low, npc);
		else if (random < 10)
			spawnNext(medium, npc);
		else if (random < 30)
			spawnNext(high, npc);
		else
			nectarText(npc, player);
	}
	
	private void autoChat(Npc monster, String text, Player player)
	{
		if (player == null)
			return;
		
		var msg = SysString.getInstance().get(player.getLocale(), text);
		CreatureSay mov = new CreatureSay(monster.getObjectId(), SayType.ALL, monster.getName(), msg.toString());
		monster.broadcastPacket(mov);
	}
	
	private void chronoText(Npc npc, Player player)
	{
		if (Rnd.get(100) < 20)
			autoChat(npc, CHRONO_TEXT[Rnd.get(CHRONO_TEXT.length)], player);
	}
	
	private void noChronoText(Npc npc, Player player)
	{
		if (Rnd.get(100) < 20)
			autoChat(npc, NOCHRONO_TEXT[Rnd.get(NOCHRONO_TEXT.length)], player);
	}
	
	private void nectarText(Npc npc, Player player)
	{
		if (Rnd.get(100) < 30)
			autoChat(npc, NECTAR_TEXT[Rnd.get(NECTAR_TEXT.length)], player);
	}
	
	private void spawnNext(int npcId, Npc npc)
	{
		npc.deleteMe();
		addSpawn(npcId, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), false, 60000, true);
	}
	
	private void recordSpawn(int npcId, SpawnLocation loc)
	{
		Npc npc = addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 0, false);
		if (npc != null)
			_npclist.add(npc);
	}
}