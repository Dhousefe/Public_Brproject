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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WarriorBase.Warrior;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderSplit extends Warrior
{
	public PartyLeaderSplit()
	{
		super("ai/individual/Monster/WarriorBase/Warrior");
	}
	
	public PartyLeaderSplit(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22088
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (skill != null && !skill.isMagic() && npc._i_ai1 == 0)
		{
			final int silhouette = getNpcIntAIParam(npc, "silhouette");
			final int x = npc.getX();
			final int y = npc.getY();
			final int z = npc.getZ();
			
			createOnePrivateEx(npc, silhouette, x, y, z, 0, 0, false, 1000, attacker.getObjectId(), 0);
			createOnePrivateEx(npc, silhouette, x + 20, y, z, 0, 0, false, 1000, attacker.getObjectId(), 0);
			createOnePrivateEx(npc, silhouette, x + 40, y, z, 0, 0, false, 1000, attacker.getObjectId(), 0);
			createOnePrivateEx(npc, silhouette, x, y + 20, z, 0, 0, false, 1000, attacker.getObjectId(), 0);
			createOnePrivateEx(npc, silhouette, x, y + 40, z, 0, 0, false, 1000, attacker.getObjectId(), 0);
			createOnePrivateEx(npc, silhouette, x, y + 60, z, 0, 0, false, 1000, attacker.getObjectId(), 0);
			
			npc._i_ai1 = 1;
			
			npc.deleteMe();
		}
		
		super.onAttacked(npc, attacker, damage, skill);
	}
}