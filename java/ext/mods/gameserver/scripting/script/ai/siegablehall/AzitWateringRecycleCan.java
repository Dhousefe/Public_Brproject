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
package ext.mods.gameserver.scripting.script.ai.siegablehall;

import java.util.List;

import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;

public class AzitWateringRecycleCan extends DefaultNpc
{
	public AzitWateringRecycleCan()
	{
		super("ai/siegeablehall");
	}
	
	public AzitWateringRecycleCan(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35600
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		startQuestTimer("1011", npc, null, ((5 * 60) * 1000));
		startQuestTimerAtFixedRate("1002", npc, null, 30000, 30000);
		npc.lookItem(450, 20);
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		npc.broadcastNpcSay(NpcStringId.ID_1010639);
		
		return null;
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("1002"))
			npc.lookItem(450, 20);
		else if (name.equalsIgnoreCase("1011"))
			npc.deleteMe();
		
		return null;
	}
	
	@Override
	public void onSeeItem(Npc npc, int quantity, List<ItemInstance> items)
	{
		if (items.isEmpty())
			return;
		
		for (int i = 0; i < items.size(); i++)
		{
			final ItemInstance item = items.get(i);
			npc.getAI().addPickUpDesire(item.getObjectId(), item.getItemId() >= 8035 && item.getItemId() <= 8055 ? (10000 - i) * 2 : (10000 - i));
		}
	}
	
	@Override
	public void onPickedItem(Npc npc, ItemInstance item)
	{
		if (item.getItemId() == 8190 || item.getItemId() == 8689)
			npc.broadcastNpcSay(NpcStringId.ID_1800023);
	}
}