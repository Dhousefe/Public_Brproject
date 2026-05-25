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
package ext.mods.gameserver.model.olympiad;

import java.util.List;

import ext.mods.commons.logging.CLogger;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.Config;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.manager.AntiFeedManager;
import ext.mods.gameserver.enums.MessageType;
import ext.mods.gameserver.enums.OlympiadType;
import ext.mods.gameserver.enums.actors.MissionType;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.Summon;
import ext.mods.gameserver.model.actor.instance.Pet;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.group.Party;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.location.Location;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.zone.type.OlympiadStadiumZone;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ExOlympiadMode;
import ext.mods.gameserver.network.serverpackets.L2GameServerPacket;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.skills.L2Skill;

/**
 * The abstract layer for an Olympiad game (individual, class and non-class based).
 */
public abstract class AbstractOlympiadGame
{
	protected static final CLogger LOGGER = new CLogger(AbstractOlympiadGame.class.getName());
	
	protected static final String POINTS = "olympiad_points";
	protected static final String COMP_DONE = "competitions_done";
	protected static final String COMP_WON = "competitions_won";
	protected static final String COMP_LOST = "competitions_lost";
	protected static final String COMP_DRAWN = "competitions_drawn";
	
	protected final int _stadiumId;
	
	protected long _startTime = 0;
	protected boolean _aborted = false;
	
	protected AbstractOlympiadGame(int id)
	{
		_stadiumId = id;
	}
	
	/**
	 * @return the {@link OlympiadType} of that game.
	 */
	public abstract OlympiadType getType();
	
	/**
	 * @return the array consisting of {@link Player} names.
	 */
	public abstract String[] getPlayerNames();
	
	/**
	 * @param objectId : The objectId to test.
	 * @return true if the current objectId is part of that game.
	 */
	public abstract boolean containsParticipant(int objectId);
	
	/**
	 * Sends olympiad info to the spectator.
	 * @param player : The Creature to send infos.
	 */
	public abstract void sendOlympiadInfo(Creature player);
	
	/**
	 * Broadcasts olympiad info to participants and spectators on battle start.
	 * @param stadium : The related stadium.
	 */
	public abstract void broadcastOlympiadInfo(OlympiadStadiumZone stadium);
	
	/**
	 * Broadcasts packet to participants only.
	 * @param packet : The packet to broadcast.
	 */
	protected abstract void broadcastPacket(L2GameServerPacket packet);
	
	/**
	 * @return true if a defection occured.
	 */
	protected abstract boolean checkDefection();
	
	/**
	 * Delete all effects related to {@link Player}s, and fully heal them. Unsummon their {@link Pet} if existing.
	 */
	protected abstract void removals();
	
	/**
	 * Buff {@link Player}s.
	 */
	protected abstract void buffPlayers();
	
	/**
	 * Heal {@link Player}s.
	 */
	protected abstract void healPlayers();
	
	/**
	 * @param spawns : The Locations used to teleport Players.
	 * @return true if both {@link Participant}s have been successfully teleported on the given {@link Location}s, false otherwise.
	 */
	protected abstract boolean portPlayersToArena(List<Location> spawns);
	
	/**
	 * Cancel all {@link Player}s animations, set their Intention to IDLE. Affects also their {@link Summon}, if existing. Heal Players, start their HP/MP regen.
	 */
	protected abstract void cleanEffects();
	
	/**
	 * Teleport {@link Player}s back to their initial {@link Location}.
	 */
	protected abstract void portPlayersBack();
	
	/**
	 * Reset {@link Player}s status back to regular behaviour : stop all charges and effects, add back clan & hero skills.
	 */
	protected abstract void playersStatusBack();
	
	/**
	 * Clear {@link Player}s references on {@link Participant}s.
	 */
	protected abstract void clearPlayers();
	
	/**
	 * Set the given {@link Player} as disconnected.
	 * @param player : The Player to affect.
	 */
	protected abstract void handleDisconnect(Player player);
	
	/**
	 * Reset done damages.
	 */
	protected abstract void resetDamage();
	
	/**
	 * Add damages for the given {@link Player}.
	 * @param player : The Player who dealt damages.
	 * @param damage : The amount of damage to add.
	 */
	protected abstract void addDamage(Player player, int damage);
	
	/**
	 * @return true if the battle is still continuing due to regular Olympiad rules (no disconnection, correct number of Participants,...)
	 */
	protected abstract boolean checkBattleStatus();
	
	/**
	 * @return true if either the battle ended abruptly, or if one of the side died.
	 */
	protected abstract boolean haveWinner();
	
	/**
	 * Compute winner and loser.
	 * @param stadium : The stadium zone used to broadcast packets.
	 */
	protected abstract void validateWinner(OlympiadStadiumZone stadium);
	
	/**
	 * @return the divider used by that type of game to calculate Olympiad points gain.
	 */
	protected abstract int getDivider();
	
	/**
	 * @return the array of {@link IntIntHolder} rewards.
	 */
	protected abstract IntIntHolder[] getReward();
	
	public final boolean isAborted()
	{
		return _aborted;
	}
	
	public final int getStadiumId()
	{
		return _stadiumId;
	}
	
	protected boolean makeCompetitionStart()
	{
		_startTime = System.currentTimeMillis();
		return !_aborted;
	}
	
	protected final void addPointsToParticipant(Participant par, int points)
	{
		if (par.getPlayer() != null)
		{
			par.getPlayer().getMissions().update(MissionType.OLYMPIAD_FIGHT);
			par.getPlayer().getMissions().update(MissionType.OLYMPIAD_FIGHT_WON);
		}
		
		par.updateStat(POINTS, points);
		
		broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_GAINED_S2_OLYMPIAD_POINTS).addString(par.getName()).addNumber(points));
	}
	
	protected final void removePointsFromParticipant(Participant par, int points)
	{
		if (par.getPlayer() != null)
		{
			par.getPlayer().getMissions().update(MissionType.OLYMPIAD_FIGHT);
			par.getPlayer().getMissions().update(MissionType.OLYMPIAD_FIGHT_LOST);
		}
		
		par.updateStat(POINTS, -points);
		
		broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_LOST_S2_OLYMPIAD_POINTS).addString(par.getName()).addNumber(points));
	}
	
	/**
	 * @param player : The Player to check.
	 * @return null if the tested {@link Player} passed all checks or broadcast the {@link SystemMessage} associated to opponent defection.
	 */
	protected static SystemMessage checkDefection(Player player)
	{
		if (player == null || !player.isOnline())
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME);
		
		if (player.getClient() == null || player.getClient().isDetached())
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_ENDS_THE_GAME);
		
		if (player.isInObserverMode())
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		
		if (player.isDead())
		{
			player.sendPacket(SystemMessageId.CANNOT_PARTICIPATE_OLYMPIAD_WHILE_DEAD);
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isSubClassActive())
		{
			player.sendPacket(SystemMessageId.SINCE_YOU_HAVE_CHANGED_YOUR_CLASS_INTO_A_SUB_JOB_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isCursedWeaponEquipped())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_JOIN_OLYMPIAD_POSSESSING_S1).addItemName(player.getCursedWeaponEquippedId()));
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (player.isOverweight())
		{
			player.sendPacket(SystemMessageId.SINCE_80_PERCENT_OR_MORE_OF_YOUR_INVENTORY_SLOTS_ARE_FULL_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		if (CTFEvent.getInstance().isPlayerParticipant(player.getObjectId()) || DMEvent.getInstance().isPlayerParticipant(player.getObjectId()) || LMEvent.getInstance().isPlayerParticipant(player.getObjectId()) || TvTEvent.getInstance().isPlayerParticipant(player.getObjectId()))
		{
			player.sendMessage(player.getSysString(10_103));
			return SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_HAS_BEEN_CANCELLED_BECAUSE_THE_OTHER_PARTY_DOES_NOT_MEET_THE_REQUIREMENTS_FOR_JOINING_THE_GAME);
		}
		
		return null;
	}
	
	/**
	 * @param par : The Participant to teleport.
	 * @param loc : The Location to teleport.
	 * @param id : The olympiad game id.
	 * @return true if the {@link Participant} has been successfully teleported on the given {@link Location}, false otherwise.
	 */
	protected static final boolean portPlayerToArena(Participant par, Location loc, int id)
	{
		final Player player = par.getPlayer();
		if (player == null || !player.isOnline())
			return false;
		
		player.getSavedLocation().set(player.getPosition());
		
		player.setTarget(null);
		
		player.setOlympiadGameId(id);
		player.setOlympiadMode(true);
		player.setOlympiadStart(false);
		player.setOlympiadSide(par.getSide());
		player.teleportTo(loc, 0);
		player.sendPacket(new ExOlympiadMode(par.getSide()));
		return true;
	}
	
	/**
	 * Delete all effects related to a {@link Player}, and fully heal him. Unsummon his {@link Pet} if existing.
	 * @param player : The Player to affect.
	 * @param removeParty : If true, expels the Player from its current {@link Party}.
	 */
	protected static final void removals(Player player, boolean removeParty)
	{
		if (player == null)
			return;
		
		player.stopAllEffectsExceptThoseThatLastThroughDeath();
		
		final Clan clan = player.getClan();
		if (clan != null)
		{
			for (L2Skill skill : clan.getClanSkills().values())
				player.removeSkill(skill.getId(), false);
		}
		
		player.abortAll(true);
		
		player.getAppearance().setVisible(true);
		
		if (player.isHero())
		{
			for (L2Skill skill : SkillTable.getHeroSkills())
				player.removeSkill(skill.getId(), false);
		}
		
		player.getStatus().setMaxCpHpMp();
		
		if (player.isMounted())
			player.dismount();
		else
		{
			final Summon summon = player.getSummon();
			if (summon != null)
			{
				if (summon instanceof Pet)
					summon.unSummon(player);
				else
				{
					summon.stopAllEffectsExceptThoseThatLastThroughDeath();
					summon.abortAll(true);
				}
			}
		}
		
		player.getCubicList().stopCubicsGivenByOthers();
		
		if (removeParty)
		{
			final Party party = player.getParty();
			if (party != null)
				party.removePartyMember(player, MessageType.EXPELLED);
		}
		
		player.checkItemRestriction();
		
		player.disableAutoShotsAll();
		
		ItemInstance item = player.getActiveWeaponInstance();
		if (item != null)
			item.unChargeAllShots();
		
		player.sendPacket(new SkillList(player));
	}
	
	/**
	 * Buff the {@link Player}. WW2 for fighter/mage + haste 1 if fighter.
	 * @param player : the happy benefactor.
	 */
	protected static final void buffPlayer(Player player)
	{
		L2Skill skill = SkillTable.getInstance().getInfo(1204, 2);
		if (skill != null)
			skill.getEffects(player, player);
		
		if (!player.isMageClass())
		{
			skill = SkillTable.getInstance().getInfo(1086, 1);
			if (skill != null)
				skill.getEffects(player, player);
		}
	}
	
	/**
	 * Cancel all {@link Player} animations, set the Intention to IDLE. Affects also the {@link Summon}, if existing. Heal the Player, start his HP/MP regen.
	 * @param player : The Player to affect.
	 */
	protected static final void cleanEffects(Player player)
	{
		player.setOlympiadStart(false);
		player.abortAll(true);
		player.getAI().tryToIdle();
		
		if (player.isDead())
			player.setIsDead(false);
		
		final Summon summon = player.getSummon();
		if (summon != null && !summon.isDead())
		{
			summon.abortAll(true);
			summon.getAI().tryToIdle();
		}
		
		player.getStatus().setMaxCpHpMp();
		player.getStatus().startHpMpRegeneration();
	}
	
	/**
	 * Reset {@link Player} status back to regular behaviour : stop all charges and effects, add back clan & hero skills.
	 * @param player : The Player to affect.
	 */
	protected static final void playerStatusBack(Player player)
	{
		player.setOlympiadMode(false);
		player.setOlympiadStart(false);
		player.setOlympiadSide(-1);
		player.setOlympiadGameId(-1);
		player.sendPacket(new ExOlympiadMode(0));
		
		player.stopAllEffectsExceptThoseThatLastThroughDeath();
		player.clearCharges();
		
		final Summon summon = player.getSummon();
		if (summon != null && !summon.isDead())
			summon.stopAllEffectsExceptThoseThatLastThroughDeath();
		
		if (player.getClan() != null)
		{
			player.getClan().checkAndAddClanSkills(player);
			
			player.getStatus().setMaxCpHpMp();
		}
		
		if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0)
			AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, player);
		
		if (player.isHero())
		{
			for (L2Skill skill : SkillTable.getHeroSkills())
				player.addSkill(skill, false);
		}
		player.sendPacket(new SkillList(player));
	}
	
	/** Coordenadas de Giran para teleporte de emergencia ( Safe Disconnect ). */
	private static final Location GIRAN_LOC = new Location(83314, 148012, -3400);
	
	/**
	 * Teleport the {@link Player} back to his initial {@link Location}.
	 * @param player : The Player to teleport back.
	 */
	protected static final void portPlayerBack(Player player)
	{
		if (player == null || player.getSavedLocation().equals(Location.DUMMY_LOC))
			return;
		
		player.teleportTo(player.getSavedLocation(), 0);
		player.getSavedLocation().clean();
	}
	
	/**
	 * Define a saved location do player para Giran (retorno ao fim da luta).
	 * Usado no restart apos Safe Disconnect para evitar que a loc de retorno fique na arena.
	 * @param player O player.
	 */
	protected static final void setSavedLocationToGiran(Player player)
	{
		if (player == null)
			return;
		player.getSavedLocation().set(GIRAN_LOC.getX(), GIRAN_LOC.getY(), GIRAN_LOC.getZ());
	}
	
	/**
	 * Teleporta o player para Giran e persiste a posicao no DB.
	 * Usado quando Safe Disconnect foi acionado para evitar que fique preso na arena.
	 * @param player O player a teleportar (pode estar em Safe Disconnect).
	 */
	protected static final void portPlayerToGiranPersistent(Player player)
	{
		if (player == null)
			return;
		player.teleportTo(GIRAN_LOC, 0);
		player.storeCharBase();
	}
	
	/**
	 * Reward a {@link Player} with items.
	 * @param player : The Player to reward.
	 * @param reward : The IntIntHolder container used as itemId / quantity holder.
	 */
	public static final void rewardParticipant(Player player, IntIntHolder[] reward)
	{
		if (player == null || !player.isOnline() || ArraysUtil.isEmpty(reward))
			return;
		
		for (IntIntHolder it : reward)
		{
			final ItemInstance item = player.getInventory().addItem(it.getId(), it.getValue());
			if (item == null)
				continue;
			
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(it.getId()).addNumber(it.getValue()));
		}
	}
}