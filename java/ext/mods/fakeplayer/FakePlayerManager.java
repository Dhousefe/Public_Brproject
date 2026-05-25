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
package ext.mods.fakeplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ext.mods.Config;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;

import ext.mods.fakeplayer.data.EquipesData;
import ext.mods.fakeplayer.helper.FakePlayerHelpers;
import ext.mods.fakeplayer.move.GiranMove;

public class FakePlayerManager
{
	public void initialise()
	{
		FakePlayerTaskManager.getInstance().initialise();
		EquipesData.getInstance();
		GiranMove.getInstance();
	}
	
	public FakePlayer spawnPlayer(int x, int y, int z)
	{
		FakePlayer activeChar = FakePlayerHelpers.createRandomFakePlayer();
		
		if (activeChar == null)
		{
			System.out.println("FakePlayerManager: Todos os nomes foram usados. Não é possível criar mais fake players.");
			return null;
		}
		
		World.getInstance().addPlayer(activeChar);
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setSpawnProtection(true);
		
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!activeChar.isGM() && (activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleportTo(RestartType.TOWN);
		
		if (activeChar.getAi() == null)
		{
			activeChar.assignDefaultAI();
		}
		activeChar.setRunning(true);
		return activeChar;
	}
	
	public List<FakePlayer> spawnPlayerGroup(int x, int y, int z, int count)
	{
		List<FakePlayer> group = new ArrayList<>();
		int attempts = 0;
		int i = 0;
		
		while (group.size() < count && attempts < count * 2)
		{
			FakePlayer bot = spawnPlayer(x + (i * 45), y + (i * 45), z);
			attempts++;
			i++;
			if (bot != null)
			{
				group.add(bot);
			}
		}
		
		return group;
	}
	
	public void despawnFakePlayer(int objectId)
	{
		Player player = World.getInstance().getPlayer(objectId);
		if (player instanceof FakePlayer)
		{
			FakePlayer fakePlayer = (FakePlayer) player;
			fakePlayer.despawnPlayer();
		}
	}
	
	public int getFakePlayersCount()
	{
		return getFakePlayers().size();
	}
	
	public List<FakePlayer> getFakePlayers()
	{
		return World.getInstance().getPlayers().stream().filter(x -> x instanceof FakePlayer).map(x -> (FakePlayer) x).collect(Collectors.toList());
	}
	
	public static FakePlayerManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		private static final FakePlayerManager _instance = new FakePlayerManager();
	}
	
}
