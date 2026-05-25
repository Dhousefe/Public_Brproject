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
package ext.mods.gameserver.handler;

import ext.mods.gameserver.enums.skills.SkillTargetType;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Playable;
import ext.mods.gameserver.skills.L2Skill;

public interface ITargetHandler
{
	static final Creature[] EMPTY_TARGET_ARRAY = new Creature[0];
	
	/**
	 * @return The associated {@link SkillTargetType}.
	 */
	public SkillTargetType getTargetType();
	
	/**
	 * The worker method called by a {@link Creature} when using a {@link L2Skill}.
	 * @param caster : The {@link Creature} used as caster.
	 * @param target : The {@link Creature} used as target.
	 * @param skill : The {@link L2Skill} to cast.
	 * @return The array of valid {@link WorldObject} targets, based on the {@link Creature} caster, {@link Creature} target and {@link L2Skill} set as parameters.
	 */
	public Creature[] getTargetList(Creature caster, Creature target, L2Skill skill);
	
	/**
	 * @param caster : The {@link Creature} used as caster.
	 * @param target : The {@link Creature} used as target.
	 * @param skill : The {@link L2Skill} to cast.
	 * @return The real {@link Creature} target.
	 */
	public Creature getFinalTarget(Creature caster, Creature target, L2Skill skill);
	
	/**
	 * @param caster : The {@link Playable} used as caster.
	 * @param target : The {@link Creature} used as target.
	 * @param skill : The {@link L2Skill} to cast.
	 * @param isCtrlPressed : If True, we use specific CTRL rules.
	 * @return True if casting is possible, false otherwise.
	 */
	public boolean meetCastConditions(Playable caster, Creature target, L2Skill skill, boolean isCtrlPressed);
}