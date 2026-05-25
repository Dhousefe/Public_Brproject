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
package ext.mods.gameserver.scripting.script.ai.individual.Monster.WizardBase.PartyLeaderWizard;

import ext.mods.commons.random.Rnd;

import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.skills.L2Skill;

public class PartyLeaderWizardDD2Summon extends PartyLeaderWizardDD2
{
	public PartyLeaderWizardDD2Summon()
	{
		super("ai/individual/Monster/WizardBase/PartyLeaderWizard");
	}
	
	public PartyLeaderWizardDD2Summon(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		22033,
		22035,
		22041,
		22043
	};
	
	@Override
	public void onCreated(Npc npc)
	{
		npc._i_ai1 = 0;
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		super.onAttacked(npc, attacker, damage, skill);
		
		if (npc._i_ai1 == 0)
		{
			int i1 = (Rnd.get(50) - 25);
			int i2 = (Rnd.get(50) - 25);
			createOnePrivateEx(npc, getNpcIntAIParam(npc, "silhouette"), npc.getX() + i1, npc.getY() + i2, npc.getZ(), 0, 0, true, 1000, attacker.getObjectId(), 0);
			i1 = (Rnd.get(100) - 50);
			i2 = (Rnd.get(100) - 50);
			createOnePrivateEx(npc, getNpcIntAIParam(npc, "silhouette2"), npc.getX() + i1, npc.getY() + i2, npc.getZ(), 0, 0, true, 1000, attacker.getObjectId(), 0);
			i1 = (Rnd.get(100) - 50);
			i2 = (Rnd.get(100) - 50);
			createOnePrivateEx(npc, getNpcIntAIParam(npc, "silhouette3"), npc.getX() + i1, npc.getY() + i2, npc.getZ(), 0, 0, true, 1000, attacker.getObjectId(), 0);
			npc._i_ai1 = 1;
		}
	}
}