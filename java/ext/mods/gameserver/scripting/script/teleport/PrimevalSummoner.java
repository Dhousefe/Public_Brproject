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
package ext.mods.gameserver.scripting.script.teleport;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.skills.L2Skill;

public class PrimevalSummoner extends Quest
{
	public PrimevalSummoner()
	{
		super(-1, "teleport");
		
		addTalkId(32104);
		addCreated(32104);
		addUseSkillFinished(32104);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._c_ai0 = npc;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = "";
		
		int i2 = 0;
		
		final Party party0 = player.getParty();
		if (party0 != null)
		{
			for (Player partyMember : party0.getMembers())
				if (partyMember.isDead() && npc.getSpawn().isInMyTerritory(partyMember))
					i2++;
		}
		
		if (i2 == 0)
			htmltext = "vervato002.htm";
		else if (player.reduceAdena(200000, true))
		{
			if (npc._c_ai0 == npc)
			{
				npc._c_ai0 = player;
				
				npc.getAI().addCastDesire(npc, 5121, 1, 1000000);
			}
			else
				htmltext = "vervato005.htm";
		}
		else
			htmltext = "vervato003.htm";
		
		return htmltext;
	}
	
	@Override
	public void onUseSkillFinished(Npc npc, Creature creature, L2Skill skill, boolean success)
	{
		if (skill.getId() == 5121 && npc._c_ai0 != npc)
		{
			final Party party0 = npc._c_ai0.getParty();
			if (party0 != null)
			{
				for (Player partyMember : party0.getMembers())
				{
					if (partyMember.isDead() && npc.getSpawn().isInMyTerritory(partyMember))
						partyMember.teleportTo(11320, -23504, -3640, 0);
				}
			}
			startQuestTimer("2005", npc, null, 3000);
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("2005") && npc._c_ai0 != npc)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile(player.getLocale(), "html/script/" + getDescr() + "/" + getName() + "/" + "vervato004.htm");
			
			npc._c_ai0.sendPacket(html);
			npc._c_ai0.sendPacket(ActionFailed.STATIC_PACKET);
			npc._c_ai0 = npc;
		}
		return super.onTimer(name, npc, player);
	}
}