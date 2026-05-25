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
package ext.mods.Crypta;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ext.mods.battlerboss.holder.BattleBossConfigHolder;
import ext.mods.battlerboss.holder.BattleHolder;
import ext.mods.battlerboss.holder.EventHolder;
import ext.mods.battlerboss.register.BattleBossOpenRegister;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.battlerboss.holder.InfoHolder;
import ext.mods.battlerboss.holder.MonsterChallengeHolder;
import ext.mods.battlerboss.holder.RegistrationHolder;
import ext.mods.battlerboss.holder.RewardHolder;
import ext.mods.battlerboss.holder.RewardsHolder;
import ext.mods.battlerboss.holder.TeleportHolder;
import ext.mods.commons.data.StatSet;
import ext.mods.commons.data.xml.IXmlReader;

public class BattleBossData implements IXmlReader
{
	/** ID do ícone de tutorial (quest mark) para Battle Boss. */
	public static final int TUTORIAL_QUESTION_MARK_ID = 2006;
	private final Map<Integer, EventHolder> _events = new ConcurrentHashMap<>();

	public BattleBossData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		_events.clear();
		parseDataFile("custom/mods/battleboss.xml");
		LOGGER.info("Loaded {} battle boss events.", _events.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "events", eventsNode -> forEach(eventsNode, "event", this::parseEvent));
	}
	
	private String getChildText(Node node, String tag)
	{
		Node child = getChild(node, tag);
		return (child != null) ? child.getTextContent().trim() : "";
	}
	
	private void parseEvent(Node node)
	{
		StatSet set = new StatSet(parseAttributes(node));
		int id = set.getInteger("id");
		String name = set.getString("name");
		String type = set.getString("type");
		
		boolean enabled = Boolean.parseBoolean(getChildText(node, "enabled"));
		int duration = Integer.parseInt(getChildText(node, "duration"));
		String[] dayTokens = getChildText(node, "days").split(",");
		List<Integer> days = new ArrayList<>();
		for (String token : dayTokens)
		{
			days.add(Integer.parseInt(token.trim()));
		}
		
		List<String> times = new ArrayList<>();
		forEach(node, "times", timesNode ->
		{
			forEach(timesNode, "time", timeNode -> times.add(timeNode.getTextContent()));
		});
		
		BattleBossConfigHolder config = new BattleBossConfigHolder(enabled, days, times, duration);
		
		InfoHolder info = null;
		Node infoNode = getChild(node, "info");
		if (infoNode != null)
		{
			StatSet infoSet = new StatSet(parseAttributes(infoNode));

			String infoName = getChildText(infoNode, "name");
			String infoIcon = getChildText(infoNode, "icon");
			infoSet.set("name", infoName);
			infoSet.set("icon", infoIcon);

			List<String> descLines = new ArrayList<>();
			forEach(infoNode, "desc", d -> forEach(d, "line", l -> descLines.add(l.getTextContent().trim())));
			infoSet.set("desc", descLines);
			
			List<String> announcerLines = new ArrayList<>();
			forEach(infoNode, "announce", d -> forEach(d, "line", l -> announcerLines.add(l.getTextContent().trim())));
			infoSet.set("announce", announcerLines);
			

			info = new InfoHolder(infoSet);

		}
		
		RegistrationHolder registration = null;
		Node regNode = getChild(node, "registration");
		if (regNode != null)
		{
			StatSet regSet = new StatSet(parseAttributes(regNode));
			registration = new RegistrationHolder(regSet);
		}
		
		BattleHolder battle = null;
		Node battleNode = getChild(node, "battle");
		if (battleNode != null)
		{
			StatSet battleSet = new StatSet(parseAttributes(battleNode));
			Node rulesNode = getChild(battleNode, "rules");
			if (rulesNode != null)
			{
				battleSet.merge(parseAttributes(rulesNode));
			}
			battle = new BattleHolder(battleSet);
		}
		
		MonsterChallengeHolder monster = null;
		Node monNode = getChild(node, "monsterChallenge");
		if (monNode != null)
		{
			StatSet monSet = new StatSet(parseAttributes(monNode));
			Node spawnNode = getChild(monNode, "spawn");
			if (spawnNode != null)
			{
				monSet.merge(parseAttributes(spawnNode));
			}
			monster = new MonsterChallengeHolder(monSet);
		}
		
		TeleportHolder teleport = null;
		Node tpNode = getChild(node, "teleport");
		if (tpNode != null)
		{
			StatSet tpSet = new StatSet();
			Node arenaNode = getChild(tpNode, "arena");
			if (arenaNode != null)
			{
				var a = parseAttributes(arenaNode);
				tpSet.set("arenaX", a.get("x"));
				tpSet.set("arenaY", a.get("y"));
				tpSet.set("arenaZ", a.get("z"));
			}
			Node returnNode = getChild(tpNode, "returnSpot");
			if (returnNode != null)
			{
				var r = parseAttributes(returnNode);
				tpSet.set("returnX", r.get("x"));
				tpSet.set("returnY", r.get("y"));
				tpSet.set("returnZ", r.get("z"));
				tpSet.set("delay", r.get("delay"));
			}
			teleport = new TeleportHolder(tpSet);
		}
		
		RewardsHolder rewards = null;
		Node rewardsNode = getChild(node, "rewards");
		if (rewardsNode != null)
		{
			List<RewardHolder> winRewards = new ArrayList<>();
			List<RewardHolder> loseRewards = new ArrayList<>();
			
			Node winNode = getChild(rewardsNode, "winner");
			if (winNode != null)
			{
				forEach(winNode, "reward", r ->
				{
					RewardHolder reward = new RewardHolder(new StatSet(parseAttributes(r)));
					winRewards.add(reward);
				});
			}
			
			Node loseNode = getChild(rewardsNode, "loser");
			if (loseNode != null)
			{
				forEach(loseNode, "reward", r ->
				{
					RewardHolder reward = new RewardHolder(new StatSet(parseAttributes(r)));
					loseRewards.add(reward);
				});
			}
			
			rewards = new RewardsHolder(winRewards, loseRewards);
		}
		
		EventHolder holder = new EventHolder(id, name, type, info, registration, battle, monster, teleport, rewards, config);
		_events.put(id, holder);
		
	}
	
	public EventHolder getEvent(int id)
	{
		return _events.get(id);
	}
	
	public Collection<EventHolder> getEvents()
	{
		return _events.values();
	}
	
	public int size()
	{
		return _events.size();
	}
	
	/** Para CryptaManager/reflexão: indica se algum evento está com registro aberto. */
	public boolean isRunning()
	{
		return BattleBossOpenRegister.getInstance().hasOpenRegistration();
	}
	
	/** Retorna o HTML de alerta do Battle Boss para o jogador (usado pelo quest mark). */
	public String getTutorialAlertHtml(Player player)
	{
		if (player == null || !isRunning()) return null;
		String html = HTMLData.getInstance().getHtm(player.getLocale(), "html/mods/tournament/tutorial_alert_battleboss.htm");
		if (html == null || html.isEmpty()) return null;
		String msg = "Battle Boss event is open! Use .battleboss to participate.";
		return html.replace("%message%", msg);
	}
	
	private Node getChild(Node parent, String name)
	{
		if (parent == null || !parent.hasChildNodes())
		{
			return null;
		}
		
		for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
		{
			if (child.getNodeType() == Node.ELEMENT_NODE && name.equalsIgnoreCase(child.getNodeName()))
			{
				return child;
			}
		}
		return null;
	}

	public static BattleBossData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		private static final BattleBossData INSTANCE = new BattleBossData();
	}
}
