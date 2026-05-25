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
 * The enum listing all events type.
 */
public enum EventHandler
{
	/**
	 * An event triggered when a NPC abnormal status changes.
	 */
	ABNORMAL_STATUS_CHANGED(true),
	
	/**
	 * An event triggered when a NPC is attacked by someone.
	 */
	ATTACKED(true),
	
	/**
	 * An event triggered when a NPC ends an attack over someone.
	 */
	ATTACK_FINISHED(true),
	
	/**
	 * An event triggered when a NPC calls members of its clan upon attack.
	 */
	CLAN_ATTACKED(true),
	
	/**
	 * An event triggered when a NPC calls members of its clan upon own death.
	 */
	CLAN_DIED(true),
	
	/**
	 * An event triggered when a NPC is spawned or respawned.
	 */
	CREATED(true),
	
	/**
	 * An event triggered when a NPC decays.
	 */
	DECAYED(true),
	
	/**
	 * An event controlling the first dialog shown by NPCs when they are clicked.
	 */
	FIRST_TALK(false),
	
	/**
	 * An event triggered when a finishes a movement.
	 */
	MOVE_TO_FINISHED(true),
	
	/**
	 * An event triggered when a mob gets killed.
	 */
	MY_DYING(true),
	
	/**
	 * An event triggered when a NPC got nothing to do (peace mode).
	 */
	NO_DESIRE(true),
	
	/**
	 * An event triggered when a NPC is out of his territory.
	 */
	OUT_OF_TERRITORY(true),
	
	/**
	 * An event triggered when a NPC calls members of its party upon attack.
	 */
	PARTY_ATTACKED(true),
	
	/**
	 * An event triggered when a NPC calls members of its party upon own death.
	 */
	PARTY_DIED(true),
	
	/**
	 * An event triggered when a NPC picks up an item.
	 */
	PICKED_ITEM(true),
	
	/**
	 * An event controlling onTalk action from start npcs.
	 */
	QUEST_START(true),
	
	/**
	 * An event triggered when a NPC see another creature.
	 */
	SEE_CREATURE(true),
	
	/**
	 * An event triggered when a NPC see a specific item.
	 */
	SEE_ITEM(true),
	
	/**
	 * An event triggered when a NPC see a spell being casted.
	 */
	SEE_SPELL(true),
	
	/**
	 * An event triggered when a NPC is being spelled.
	 */
	SPELLED(true),
	
	/**
	 * An event triggered when a Door/Wall calls members of its clan upon attack.
	 */
	STATIC_OBJECT_CLAN_ATTACKED(true),
	
	/**
	 * An event controlling onTalk action from npcs participating in a quest.
	 */
	TALKED(true),
	
	/**
	 * An event triggered when a spell goes to the end, once casted. Used for exotic skills.
	 */
	USE_SKILL_FINISHED(true),
	
	/**
	 * An event triggered when a Creature exits a zone.
	 */
	ZONE_ENTER(true),
	
	/**
	 * An event triggered when a Creature exits a zone.
	 */
	ZONE_EXIT(true),
	
	/**
	 * An event triggered when an event script is called.
	 */
	SCRIPT_EVENT(true),
	
	AI_TALKED(false),
	
	AI_MENU_SELECTED(false);
	
	private boolean _allowMultipleRegistration;
	
	EventHandler(boolean allowMultipleRegistration)
	{
		_allowMultipleRegistration = allowMultipleRegistration;
	}
	
	/**
	 * @return true if the {@link EventHandler} allows multiple registrations.
	 */
	public boolean isMultipleRegistrationAllowed()
	{
		return _allowMultipleRegistration;
	}
}