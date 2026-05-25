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
package ext.mods.gameserver.enums;

/**
 * This class contains each possible event that can happen to an actor.
 */
public enum AiEventType
{
	/** An action is required, due to a previous step being completed/aborted. The actor must think on next action. */
	THINK,
	/** The actor was attacked. It may start attack in response, or ignore this event if it already attacks someone. */
	ATTACKED,
	/** Increase/decrease aggression towards a target, or reduce global aggression if target is null. */
	AGGRESSION,
	/** The actor evaded an hit. */
	EVADED,
	/** The actor completed an action and is now ready to act. */
	FINISHED_ATTACK,
	/** The actor arrived to assigned location, or didn't need to move. */
	ARRIVED,
	/** The actor cannot move anymore due to obstacles. */
	ARRIVED_BLOCKED,
	/** Cancel the actor's current action execution, without changing the intention. */
	CANCEL,
	/** The actor died. */
	DEAD,
	/** The actor has finished a skill cast. */
	FINISHED_CASTING,
	/** The actor has finished sitting down */
	SAT_DOWN,
	/** The actor has finished standing up */
	STOOD_UP,
	/** The actor has finished to attack with a bow */
	FINISHED_ATTACK_BOW,
	/** The actor attack bow reuse tim has now ended */
	BOW_ATTACK_REUSED,
	/** The actor's owner is under attack */
	OWNER_ATTACKED,
	/** The actor has been teleported */
	TELEPORTED
}