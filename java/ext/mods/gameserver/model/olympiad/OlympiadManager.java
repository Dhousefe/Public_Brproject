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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import ext.mods.commons.data.StatSet;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.AntiFeedManager;
import ext.mods.gameserver.enums.OlympiadType;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.Safedisconect.SafeDisconnectManager;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class OlympiadManager
{
	private final List<Integer> _nonClassBasedParticipants = new CopyOnWriteArrayList<>();
	private final Map<Integer, List<Integer>> _classBasedParticipants = new ConcurrentHashMap<>();
	
	protected OlympiadManager()
	{
	}
	
	public final List<Integer> getNonClassBasedParticipants()
	{
		return _nonClassBasedParticipants;
	}
	
	public final Map<Integer, List<Integer>> getClassBasedParticipants()
	{
		return _classBasedParticipants;
	}
	
	protected final boolean hasEnoughNonClassBasedParticipants()
	{
		return _nonClassBasedParticipants.size() >= Config.OLY_NONCLASSED;
	}
	
	protected final List<List<Integer>> hasEnoughClassBasedParticipants()
	{
		List<List<Integer>> result = null;
		for (List<Integer> classList : _classBasedParticipants.values())
		{
			if (classList != null && classList.size() >= Config.OLY_CLASSED)
			{
				if (result == null)
					result = new ArrayList<>();
				
				result.add(classList);
			}
		}
		return result;
	}
	
	protected final void clearParticipants()
	{
		_nonClassBasedParticipants.clear();
		_classBasedParticipants.clear();
		AntiFeedManager.getInstance().clear(AntiFeedManager.OLYMPIAD_ID);
	}
	
	public final boolean isRegisteredInComp(Player noble)
	{
		return isRegistered(noble, false) || isInCompetition(noble, false);
	}
	
	public final boolean isRegistered(Player player)
	{
		return isRegistered(player, false);
	}
	
	private final boolean isRegistered(Player player, boolean showMessage)
	{
		final Integer objId = Integer.valueOf(player.getObjectId());
		
		if (_nonClassBasedParticipants.contains(objId))
		{
			if (showMessage)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_FOR_ALL_CLASSES_WAITING_TO_PARTICIPATE_IN_THE_GAME));
			
			return true;
		}
		
		final List<Integer> classed = _classBasedParticipants.get(player.getBaseClass());
		if (classed != null && classed.contains(objId))
		{
			if (showMessage)
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_ARE_ALREADY_ON_THE_WAITING_LIST_TO_PARTICIPATE_IN_THE_GAME_FOR_YOUR_CLASS));
			
			return true;
		}
		
		return false;
	}
	
	private static final boolean isInCompetition(Player player, boolean showMessage)
	{
		if (!Olympiad.getInstance().isInCompPeriod())
			return false;
		
		for (int i = OlympiadGameManager.getInstance().getNumberOfStadiums(); --i >= 0;)
		{
			AbstractOlympiadGame game = OlympiadGameManager.getInstance().getOlympiadTask(i).getGame();
			if (game == null)
				continue;
			
			if (game.containsParticipant(player.getObjectId()))
			{
				if (showMessage)
					player.sendPacket(SystemMessageId.YOU_HAVE_ALREADY_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_AN_EVENT);
				
				return true;
			}
		}
		
		return false;
	}
	
	public final boolean registerNoble(Npc npc, Player player, OlympiadType type)
	{
		if (!Olympiad.getInstance().isInCompPeriod())
		{
			player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		
		if (Olympiad.getInstance().getMillisToCompEnd() < 600000)
		{
			player.sendPacket(SystemMessageId.GAME_REQUEST_CANNOT_BE_MADE);
			return false;
		}
		
		switch (type)
		{
			case CLASSED:
				if (!checkNoble(npc, player))
					return false;
				
				final List<Integer> classed = _classBasedParticipants.computeIfAbsent(player.getBaseClass(), c -> new CopyOnWriteArrayList<>());
				classed.add(player.getObjectId());
				
				player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_CLASSIFIED_GAMES);
				break;
			
			case NON_CLASSED:
				if (!checkNoble(npc, player))
					return false;
				
				_nonClassBasedParticipants.add(player.getObjectId());
				player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_NO_CLASS_GAMES);
				break;
		}
		return true;
	}
	
	public final boolean unRegisterNoble(Player player)
	{
		if (!Olympiad.getInstance().isInCompPeriod())
		{
			player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
			return false;
		}
		
		if (!player.isNoble())
		{
			player.sendPacket(SystemMessageId.NOBLESSE_ONLY);
			return false;
		}
		
		if (!isRegistered(player, false))
		{
			player.sendPacket(SystemMessageId.YOU_HAVE_NOT_BEEN_REGISTERED_IN_A_WAITING_LIST_OF_A_GAME);
			return false;
		}
		
		if (isInCompetition(player, false))
			return false;
		
		final Integer objectId = Integer.valueOf(player.getObjectId());
		if (_nonClassBasedParticipants.remove(objectId))
		{
			if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0)
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, player);
			
			player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
			return true;
		}
		
		final List<Integer> classed = _classBasedParticipants.get(player.getBaseClass());
		if (classed != null && classed.remove(objectId))
		{
			if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0)
				AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, player);
			
			player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_DELETED_FROM_THE_WAITING_LIST_OF_A_GAME);
			return true;
		}
		
		return false;
	}
	
	public final void removeDisconnectedCompetitor(Player player)
	{
		final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
		if (SafeDisconnectManager.getInstance().isSafeDisconnectActive(player) && Config.SAFEDISCONNECT_INTEGRATION_OLYMPIAD)
		{
			if (task != null && task.isGameStarted())
				task.pauseForDisconnect(player);
			return;
		}
		
		if (task != null && task.isGameStarted())
			task.getGame().handleDisconnect(player);
		
		final Integer objId = Integer.valueOf(player.getObjectId());
		if (_nonClassBasedParticipants.remove(objId))
			return;
		
		final List<Integer> classed = _classBasedParticipants.get(player.getBaseClass());
		if (classed != null)
			classed.remove(objId);
	}
	
	/**
	 * @param npc : The {@link Npc} the {@link Player} is talking to.
	 * @param player : The {@link Player} being tested.
	 * @return True if all requirements are met, or false otherwise.
	 */
	private final boolean checkNoble(Npc npc, Player player)
	{
		if (!player.isNoble())
		{
			player.sendPacket(SystemMessageId.ONLY_NOBLESS_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
			return false;
		}
		
		if (player.isSubClassActive())
		{
			player.sendPacket(SystemMessageId.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
			return false;
		}
		
		if (player.isCursedWeaponEquipped())
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_JOIN_OLYMPIAD_POSSESSING_S1).addItemName(player.getCursedWeaponEquippedId()));
			return false;
		}
		
		if (player.isOverweight())
		{
			player.sendPacket(SystemMessageId.SINCE_80_PERCENT_OR_MORE_OF_YOUR_INVENTORY_SLOTS_ARE_FULL_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD);
			return false;
		}
		
		if (isRegistered(player, true))
			return false;
		
		if (isInCompetition(player, true))
			return false;
		
		final int charId = player.getObjectId();
		if (CTFEvent.getInstance().isPlayerParticipant(charId) || DMEvent.getInstance().isPlayerParticipant(charId) || LMEvent.getInstance().isPlayerParticipant(charId) || TvTEvent.getInstance().isPlayerParticipant(charId))
		{
			player.sendMessage(player.getSysString(10_104));
			return false;
		}
		
		StatSet set = Olympiad.getInstance().getNobleStats(player.getObjectId());
		if (set == null)
		{
			set = new StatSet();
			set.set(Olympiad.CLASS_ID, player.getBaseClass());
			set.set(Olympiad.CHAR_NAME, player.getName());
			set.set(Olympiad.POINTS, Config.OLY_START_POINTS);
			set.set(Olympiad.COMP_DONE, 0);
			set.set(Olympiad.COMP_WON, 0);
			set.set(Olympiad.COMP_LOST, 0);
			set.set(Olympiad.COMP_DRAWN, 0);
			
			Olympiad.getInstance().addNobleStats(player.getObjectId(), set);
		}
		
		final int points = Olympiad.getInstance().getNoblePoints(player.getObjectId());
		if (points <= 0)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile(player.getLocale(), "html/olympiad/noble_nopoints1.htm");
			html.replace("%objectId%", npc.getObjectId());
			player.sendPacket(html);
			return false;
		}
		
		if ((Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.OLYMPIAD_ID, player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP))
		{
			NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setFile(player.getLocale(), "html/mods/OlympiadIPRestriction.htm");
			html.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)));
			player.sendPacket(html);
			return false;
		}
		
		return true;
	}
	
	public static final OlympiadManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final OlympiadManager INSTANCE = new OlympiadManager();
	}
}