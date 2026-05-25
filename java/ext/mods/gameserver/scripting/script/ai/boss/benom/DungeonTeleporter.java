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
package ext.mods.gameserver.scripting.script.ai.boss.benom;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.network.NpcStringId;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;

public class DungeonTeleporter extends DefaultNpc
{
	public DungeonTeleporter()
	{
		super("ai/boss/benom");
		addFirstTalkId(35506);
		addTalkId(35506);
	}
	
	public DungeonTeleporter(String descr)
	{
		super(descr);
		addFirstTalkId(35506);
		addTalkId(35506);
	}
	
	protected final int[] _npcIds =
	{
		35506
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai0 = 1;
	}
	
	@Override
	public String onFirstTalk(Npc npc, Player player)
	{
		return HTMLData.getInstance().getHtm(player, "html/doormen/35506.htm");
	}
	
	@Override
	public String onTalk(Npc npc, Player talker)
	{
		final Castle castle = npc.getCastle();
		
		if (castle.getSiege().isInProgress())
			return HTMLData.getInstance().getHtm(talker, "html/doormen/35506-2.htm");
		
		talker.teleportTo(12589, -49044, -3008, 0);
		
		return super.onTalk(npc, talker);
	}
	
	@Override
	public void onScriptEvent(Npc npc, int eventId, int arg1, int arg2)
	{
		if (eventId == 10101)
			npc.broadcastNpcShout(NpcStringId.ID_1010632);
	}
}