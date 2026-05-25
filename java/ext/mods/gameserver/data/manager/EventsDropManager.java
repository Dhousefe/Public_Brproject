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
package ext.mods.gameserver.data.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.records.custom.EventItem;

public class EventsDropManager
{
	private boolean _haveActiveChristmasEvent = false;
	private boolean _haveActiveMedalsEvent = false;
	private boolean _haveActiveL2DayEvent = false;
	private boolean _haveActiveSquashEvent = false;
	
	private final Map<Integer, RewardRule> _rewardChristmasRules = new HashMap<>();
	private final Map<Integer, RewardRule> _rewardMedalsRules = new HashMap<>();
	private final Map<Integer, RewardRule> _rewardL2DayRules = new HashMap<>();
	private final Map<Integer, RewardRule> _rewardSquashRules = new HashMap<>();
	
	public static enum RuleType
	{
		ALL_NPC,
		BY_NPCID,
		BY_ZONE
	}
	
	public boolean haveActiveChristmasEvent()
	{
		return _haveActiveChristmasEvent;
	}
	
	public boolean haveActiveMedalsEvent()
	{
		return _haveActiveMedalsEvent;
	}
	
	public boolean haveActiveL2DayEvent()
	{
		return _haveActiveL2DayEvent;
	}
	
	public boolean haveActiveSquashEvent()
	{
		return _haveActiveSquashEvent;
	}
	
	public void addChristmasRule(String event, RuleType type, List<EventItem> items)
	{
		addRule(_rewardChristmasRules, event, type, items, true);
		_haveActiveChristmasEvent = true;
	}
	
	public void addMedalsRule(String event, RuleType type, List<EventItem> items)
	{
		addRule(_rewardMedalsRules, event, type, items, true);
		_haveActiveMedalsEvent = true;
	}
	
	public void addL2DayRule(String event, RuleType type, List<EventItem> items)
	{
		addRule(_rewardL2DayRules, event, type, items, true);
		_haveActiveL2DayEvent = true;
	}
	
	public void addSquashRule(String event, RuleType type, List<EventItem> items)
	{
		addRule(_rewardSquashRules, event, type, items, true);
		_haveActiveSquashEvent = true;
	}
	
	private void addRule(Map<Integer, RewardRule> rules, String event, RuleType type, List<EventItem> items, boolean lvlControl)
	{
		RewardRule rule = new RewardRule(event, type, items, lvlControl);
		rules.put(rules.size() + 1, rule);
	}
	
	public void removeChristmasRules(String event)
	{
		removeRules(_rewardChristmasRules, event);
		_haveActiveChristmasEvent = !_rewardChristmasRules.isEmpty();
	}
	
	public void removeMedalsRules(String event)
	{
		removeRules(_rewardMedalsRules, event);
		_haveActiveMedalsEvent = !_rewardMedalsRules.isEmpty();
	}
	
	public void removeL2DayRules(String event)
	{
		removeRules(_rewardL2DayRules, event);
		_haveActiveL2DayEvent = !_rewardL2DayRules.isEmpty();
	}
	
	public void removeSquashRules(String event)
	{
		removeRules(_rewardSquashRules, event);
		_haveActiveSquashEvent = !_rewardSquashRules.isEmpty();
	}
	
	private void removeRules(Map<Integer, RewardRule> rules, String event)
	{
		rules.values().removeIf(tmp -> tmp.eventName().equals(event));
	}
	
	private List<Integer> calculateRewardItem(Map<Integer, RewardRule> rules, NpcTemplate npcTemplate, Creature lastAttacker)
	{
		List<Integer> res = new ArrayList<>(List.of(0, 0));
		int lvlDif = lastAttacker.getStatus().getLevel() - npcTemplate.getLevel();
		List<Rewards> rewardsList = new ArrayList<>();
		
		if (!rules.isEmpty())
		{
			for (RewardRule rule : rules.values())
			{
				if (rule.levDifferenceControl() && (lvlDif <= 7 && lvlDif >= -7))
					calculateRewards(rule, rewardsList);
			}
		}
		
		if (!rewardsList.isEmpty())
		{
			int rndRew = Rnd.get(rewardsList.size());
			res.set(0, rewardsList.get(rndRew).rewardId());
			res.set(1, rewardsList.get(rndRew).rewardCnt());
		}
		
		return res;
	}
	
	private void calculateRewards(RewardRule rule, List<Rewards> rewardsList)
	{
		if (rule.ruleType() == RuleType.ALL_NPC)
		{
			for (EventItem item : rule.items())
			{
				if (item.chance() >= Rnd.get(0, 100))
					rewardsList.add(new Rewards(item.id(), item.count()));
			}
		}
	}
	
	public record RewardRule(int rewardCnt, String eventName, RuleType ruleType, boolean levDifferenceControl, List<EventItem> items)
	{
		public RewardRule(String eventName, RuleType ruleType, List<EventItem> items, boolean levDifferenceControl)
		{
			this(items.size(), eventName, ruleType, levDifferenceControl, items);
		}
	}
	
	public record Rewards(int rewardId, int rewardCnt)
	{
	}
	
	public List<Integer> calculateChristmasRewardItem(NpcTemplate npcTemplate, Creature lastAttacker)
	{
		return calculateRewardItem(_rewardChristmasRules, npcTemplate, lastAttacker);
	}
	
	public List<Integer> calculateMedalsRewardItem(NpcTemplate npcTemplate, Creature lastAttacker)
	{
		return calculateRewardItem(_rewardMedalsRules, npcTemplate, lastAttacker);
	}
	
	public List<Integer> calculateL2DayRewardItem(NpcTemplate npcTemplate, Creature lastAttacker)
	{
		return calculateRewardItem(_rewardL2DayRules, npcTemplate, lastAttacker);
	}
	
	public List<Integer> calculateSquashRewardItem(NpcTemplate npcTemplate, Creature lastAttacker)
	{
		return calculateRewardItem(_rewardSquashRules, npcTemplate, lastAttacker);
	}
	
	public static final EventsDropManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final EventsDropManager INSTANCE = new EventsDropManager();
	}
}