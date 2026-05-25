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
package ext.mods.battlerboss.holder;

public class EventHolder
{
	private final int _id;
	private final String _name;
	private final String _type;
	private final InfoHolder _info;
	private final RegistrationHolder _registration;
	private final BattleHolder _battle;
	private final MonsterChallengeHolder _monster;
	private final TeleportHolder _teleport;
	private final RewardsHolder _rewards;
	private final BattleBossConfigHolder _config;
	
	public EventHolder(int id, String name, String type, InfoHolder info, RegistrationHolder registration, BattleHolder battle, MonsterChallengeHolder monster, TeleportHolder teleport, RewardsHolder rewards, BattleBossConfigHolder config)
	{
		_id = id;
		_name = name;
		_type = type;
		_info = info;
		_registration = registration;
		_battle = battle;
		_monster = monster;
		_teleport = teleport;
		_rewards = rewards;
		_config = config;
	}
	
	public int getId()
	{
		return _id;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public InfoHolder getInfo()
	{
		return _info;
	}
	
	public RegistrationHolder getRegistration()
	{
		return _registration;
	}
	
	public BattleHolder getBattle()
	{
		return _battle;
	}
	
	public MonsterChallengeHolder getMonster()
	{
		return _monster;
	}
	
	public TeleportHolder getTeleport()
	{
		return _teleport;
	}
	
	public RewardsHolder getRewards()
	{
		return _rewards;
	}
	
	public BattleBossConfigHolder getConfig()
	{
		return _config;
	}
}
