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
package ext.mods.gameserver.scripting.script.ai.siegeguards.GludioHold;

import ext.mods.gameserver.data.SkillTable.FrequentSkill;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.skills.L2Skill;

public class GludioSpearGuard extends GludioHold
{
	public GludioSpearGuard()
	{
		super("ai/siegeguards/GludioHold");
	}
	
	public GludioSpearGuard(String descr)
	{
		super(descr);
	}
	
	protected final int[] _npcIds =
	{
		35016,
		35026,
		35036,
		35046,
		35056,
		35070,
		35073,
		35076,
		35112,
		35115,
		35118,
		35154,
		35157,
		35160,
		35196,
		35199,
		35202,
		35239,
		35242,
		35245,
		35286,
		35289,
		35292,
		35330,
		35333,
		35336,
		35475,
		35478,
		35481,
		35522,
		35525,
		35528
	};
	
	@Override
	public void onSpelled(Npc npc, Player caster, L2Skill skill)
	{
		if (skill == FrequentSkill.SEAL_OF_RULER.getSkill())
			npc.getAI().addAttackDesire(caster, 50000);
	}
}