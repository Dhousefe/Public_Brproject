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
package ext.mods.tour.ranking;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.holder.IntIntHolder;

import ext.mods.tour.TourData;
import ext.mods.tour.holder.TourPrizeHolder;
import ext.mods.tour.ranking.holder.PlayerRankingData;

public class TournamentRankingManager
{
	private final Map<Integer, PlayerRankingData> _playerRankings = new ConcurrentHashMap<>();
	
	public void addWin(Player player)
	{
		PlayerRankingData data = _playerRankings.computeIfAbsent(player.getObjectId(), k -> new PlayerRankingData(player.getName()));
		data.addWin();
	}
	
	public void addLoss(Player player)
	{
		PlayerRankingData data = _playerRankings.computeIfAbsent(player.getObjectId(), k -> new PlayerRankingData(player.getName()));
		data.addLoss();
	}
	
	public void addDraw(Player player)
	{
		PlayerRankingData data = _playerRankings.computeIfAbsent(player.getObjectId(), k -> new PlayerRankingData(player.getName()));
		data.addDraw();
	}
	
	public List<PlayerRankingData> getTopRankings()
	{
		return _playerRankings.values().stream().sorted(Comparator.comparingInt(PlayerRankingData::getPoints).reversed()).collect(Collectors.toList());
	}
	
	public void rewardTopPlayers()
	{
		List<PlayerRankingData> topRankings = getTopRankings();
		
		Map<Integer, List<IntIntHolder>> positionRewards = new HashMap<>();
		
		for (TourPrizeHolder prize : TourData.getInstance().getPrizes())
		{
			List<IntIntHolder> rewards = new ArrayList<>();
			String[] rewardEntries = prize.getReward().split(";");
			for (String entry : rewardEntries)
			{
				String[] parts = entry.split("-");
				if (parts.length == 2)
				{
					int itemId = Integer.parseInt(parts[0]);
					int amount = Integer.parseInt(parts[1]);
					rewards.add(new IntIntHolder(itemId, amount));
				}
			}
			positionRewards.put(prize.getPosition(), rewards);
		}
		
		for (int i = 0; i < topRankings.size(); i++)
		{
			PlayerRankingData data = topRankings.get(i);
			int position = i + 1;
			
			List<IntIntHolder> rewards = positionRewards.get(position);
			if (rewards != null)
			{
				Player player = World.getInstance().getPlayer(data.getPlayerName());
				if (player == null)
					continue;
					
				for (IntIntHolder reward : rewards)
				{
					player.addItem(reward.getId(), reward.getValue(), true);
				}
				
				player.sendMessage("Congratulations! You have won " + position + " place in the Tournament and received your reward!");
				
				World.announceToOnlinePlayers("[TOURNAMENT] " + data.getPlayerName() + " won the " + getOrdinal(position) + " place with " + data.getPoints() + " points!", true);
			}
		}
		
	}
	
	public void clearRankings()
	{
		_playerRankings.clear();
	}
	
	private static String getOrdinal(int number)
	{
		if (number >= 11 && number <= 13)
		{
			return number + "th";
		}
		
		switch (number % 10)
		{
			case 1:
				return number + "st";
			case 2:
				return number + "nd";
			case 3:
				return number + "rd";
			default:
				return number + "th";
		}
	}
	
	public static TournamentRankingManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		private static final TournamentRankingManager _instance = new TournamentRankingManager();
	}
}