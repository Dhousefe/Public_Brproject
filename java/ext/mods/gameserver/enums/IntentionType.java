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
 * Enumeration of generic intentions of an actor.
 */
public enum IntentionType
{
	/** Move to target if too far, then attack it - may be ignored (another target, invalid zoning, etc). */
	ATTACK,
	/** Move to target if too far, then cast a spell. */
	CAST,
	/** Fake death. */
	FAKE_DEATH,
	/** Flee the target. */
	FLEE,
	/** Check target's movement and follow it. */
	FOLLOW,
	/** Stop all actions and do nothing. */
	IDLE,
	/** Move to target if too far, then interact. */
	INTERACT,
	/** Move to way point route. */
	MOVE_ROUTE,
	/** Move to another location. */
	MOVE_TO,
	/** Do nothing, used as a filler to schedule later desires. */
	NOTHING,
	/** Move to target if too far, then pick up the item. */
	PICK_UP,
	/** Rest (sit until attacked). */
	SIT,
	/** SocialAction call. */
	SOCIAL,
	/** Stand Up. */
	STAND,
	/** Use an Item. */
	USE_ITEM,
	/** Move around your actual location. */
	WANDER
}