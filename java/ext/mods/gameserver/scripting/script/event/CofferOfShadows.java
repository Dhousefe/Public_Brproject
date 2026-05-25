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
import java.util.List;

import ext.mods.Config;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.location.SpawnLocation;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.scripting.QuestState;

public class CofferOfShadows extends Events
{
	private static boolean ACTIVE = false;
	
	private List<Npc> _npclist;
	
	private static int MANAGER = 32091;
	
	private static final SpawnLocation[] _coords =
	{
		new SpawnLocation(-14823, 123567, -3143, 0),
		new SpawnLocation(-83159, 150914, -3155, 49152),
		new SpawnLocation(18600, 145971, -3095, 40960),
		new SpawnLocation(82287, 148643, -3464, 0),
		new SpawnLocation(110992, 218753, -3568, 0),
		new SpawnLocation(116339, 75424, -2738, 0),
		new SpawnLocation(81140, 55218, -1551, 32768),
		new SpawnLocation(147148, 27401, -2231, 40960),
		new SpawnLocation(43532, -46807, -823, 31471),
		new SpawnLocation(87765, -141947, -1367, 21000),
		new SpawnLocation(147154, -55527, -2807, 32000)
	};
	
	public CofferOfShadows()
	{
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
		World.announceToOnlinePlayers(10_159, getName());
		return true;
	}
	
	@Override
	public boolean eventStop()
	{
		if (!ACTIVE)
			return false;
		
		ACTIVE = false;
		
		if (!_npclist.isEmpty())
			_npclist.forEach(npc -> npc.deleteMe());
		
		_npclist.clear();
		
		eventStatusStop();
		
		World.announceToOnlinePlayers(10_160, getName());
		return true;
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		if (event.equalsIgnoreCase("COFFER1"))
		{
			if ((player.getInventory().getItemCount(Config.COFFER_PRICE_ID) >= Config.COFFER_PRICE_AMOUNT))
			{
				takeItems(player, Config.COFFER_PRICE_ID, Config.COFFER_PRICE_AMOUNT);
				giveItems(player, 8659, 1);
				return null;
			}
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
		}
		else if (event.equalsIgnoreCase("COFFER5"))
		{
			if ((player.getInventory().getItemCount(Config.COFFER_PRICE_ID) >= (Config.COFFER_PRICE_AMOUNT * 5)))
			{
				takeItems(player, Config.COFFER_PRICE_ID, Config.COFFER_PRICE_AMOUNT * 5);
				giveItems(player, 8659, 5);
				return null;
			}
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
		}
		else if (event.equalsIgnoreCase("COFFER10"))
		{
			if ((player.getInventory().getItemCount(Config.COFFER_PRICE_ID) >= (Config.COFFER_PRICE_AMOUNT * 10)))
			{
				takeItems(player, Config.COFFER_PRICE_ID, Config.COFFER_PRICE_AMOUNT * 10);
				giveItems(player, 8659, 10);
				return null;
			}
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
		}
		else if (event.equalsIgnoreCase("COFFER50"))
		{
			if ((player.getInventory().getItemCount(Config.COFFER_PRICE_ID) >= (Config.COFFER_PRICE_AMOUNT * 50)))
			{
				takeItems(player, Config.COFFER_PRICE_ID, Config.COFFER_PRICE_AMOUNT * 50);
				giveItems(player, 8659, 50);
				return null;
			}
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
		}
		return null;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		QuestState st = player.getQuestList().getQuestState(getName());
		if (st == null)
			st = newQuestState(player);
		
		return npc.getNpcId() + ".htm";
	}
	
	private void recordSpawn(int npcId, SpawnLocation loc)
	{
		Npc npc = addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), false, 0, false);
		if (npc != null)
			_npclist.add(npc);
	}
}