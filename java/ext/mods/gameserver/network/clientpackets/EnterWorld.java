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
package ext.mods.gameserver.network.clientpackets;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import ext.mods.Config;
import ext.mods.Safedisconect.SafeDisconnectManager;
import ext.mods.gameserver.GameServer;
import ext.mods.gameserver.communitybbs.manager.MailBBSManager;
import ext.mods.gameserver.custom.data.EquipGradeRestrictionData;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.data.SkillTable.FrequentSkill;
import ext.mods.gameserver.data.manager.CastleManager;
import ext.mods.gameserver.data.manager.ClanHallManager;
import ext.mods.gameserver.data.manager.CoupleManager;
import ext.mods.gameserver.data.manager.DropSkipManager;
import ext.mods.gameserver.data.manager.PcCafeManager;
import ext.mods.gameserver.data.manager.PetitionManager;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.data.xml.AdminData;
import ext.mods.gameserver.data.xml.AnnouncementData;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.RestartType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.enums.SiegeSide;
import ext.mods.gameserver.enums.ZoneId;
import ext.mods.gameserver.enums.actors.ClassRace;
import ext.mods.gameserver.enums.items.CrystalType;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.instance.ClassMaster;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFEvent;
import ext.mods.gameserver.model.entity.events.capturetheflag.CTFManager;
import ext.mods.gameserver.model.entity.events.deathmatch.DMEvent;
import ext.mods.gameserver.model.entity.events.deathmatch.DMManager;
import ext.mods.gameserver.model.entity.events.lastman.LMEvent;
import ext.mods.gameserver.model.entity.events.lastman.LMManager;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTEvent;
import ext.mods.gameserver.model.entity.events.teamvsteam.TvTManager;
import ext.mods.gameserver.model.holder.IntIntHolder;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.olympiad.Olympiad;
import ext.mods.gameserver.model.pledge.Clan;
import ext.mods.gameserver.model.pledge.SubPledge;
import ext.mods.gameserver.model.residence.castle.Castle;
import ext.mods.gameserver.model.residence.castle.Siege;
import ext.mods.gameserver.model.residence.clanhall.ClanHall;
import ext.mods.gameserver.model.residence.clanhall.SiegableHall;
import ext.mods.gameserver.network.GameClient.GameClientState;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.Die;
import ext.mods.gameserver.network.serverpackets.EtcStatusUpdate;
import ext.mods.gameserver.network.serverpackets.ExMailArrived;
import ext.mods.gameserver.network.serverpackets.ExStorageMaxCount;
import ext.mods.gameserver.network.serverpackets.FriendList;
import ext.mods.gameserver.network.serverpackets.HennaInfo;
import ext.mods.gameserver.network.serverpackets.ItemList;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.PlaySound;
import ext.mods.gameserver.network.serverpackets.PledgeShowMemberListAll;
import ext.mods.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import ext.mods.gameserver.network.serverpackets.PledgeSkillList;
import ext.mods.gameserver.network.serverpackets.QuestList;
import ext.mods.gameserver.network.serverpackets.ShortCutInit;
import ext.mods.gameserver.network.serverpackets.SkillCoolTime;
import ext.mods.gameserver.network.serverpackets.SkillList;
import ext.mods.gameserver.network.serverpackets.SystemMessage;
import ext.mods.gameserver.network.serverpackets.TutorialShowQuestionMark;
import ext.mods.gameserver.network.serverpackets.UserInfo;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;
import ext.mods.gameserver.skills.L2Skill;
import ext.mods.gameserver.taskmanager.GameTimeTaskManager;
import ext.mods.playergod.PlayerGodManager;
import ext.mods.protection.hwid.hwid;
import ext.mods.tour.TourData;
import ext.mods.tour.TournamentEvent;
import ext.mods.Crypta.RandomManager;

public class EnterWorld extends L2GameClientPacket
{
	
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final GameServer gs = GameServer.getInstance();
		if (gs != null) {
			gs.awaitNpcsSpawnsReady();
		}

		final Player player = getClient().getPlayer();
		if (player == null)
		{
			getClient().closeNow();
			return;
		}
		
		getClient().setState(GameClientState.IN_GAME);
		
		for (ItemInstance item : player.getInventory().getPaperdollItems())
		{
			if (item == null || !item.isEquipable())
				continue;
			
			final CrystalType grade = item.getItem().getCrystalType();
			if (!EquipGradeRestrictionData.getInstance().isEquipAllowed(grade))
			{
				
				player.getInventory().unequipItemInSlot(item.getLocationSlot());
				
				String message = EquipGradeRestrictionData.getInstance().getBlockMessage(grade);
				player.sendMessage("Item " + item.getName() + " was removed: " + message);
			}
		}
		
		final int objectId = player.getObjectId();
		DropSkipManager.getInstance().loadPlayer(objectId);
		if (player.isGM())
		{
			
			if (Config.SUPER_HASTE)
				SkillTable.getInstance().getInfo(7029, 4).getEffects(player, player);
			
			if (Config.GM_STARTUP_INVULNERABLE && AdminData.getInstance().hasAccess("admin_invul", player.getAccessLevel()))
				player.setInvul(true);
			
			if (Config.GM_STARTUP_INVISIBLE && AdminData.getInstance().hasAccess("admin_hide", player.getAccessLevel()))
				player.getAppearance().setVisible(false);
			
			if (Config.GM_STARTUP_BLOCK_ALL)
				player.setInBlockingAll(true);
			
			if (Config.GM_STARTUP_AUTO_LIST && AdminData.getInstance().hasAccess("admin_gmlist", player.getAccessLevel()))
				AdminData.getInstance().addGm(player, false);
			else
				AdminData.getInstance().addGm(player, true);
		}
		
		if (player.getStatus().getHp() < 0.5 && player.isMortal())
			player.setIsDead(true);
		
		if (player.getNameColor() != 0)
			player.getAppearance().setNameColor(player.getNameColor());
		
		if (player.getTitleColor() != 0)
			player.getAppearance().setTitleColor(player.getTitleColor());
		
		player.getMacroList().sendUpdate();
		player.sendPacket(new ExStorageMaxCount(player));
		player.sendPacket(new HennaInfo(player));
		player.updateEffectIcons();
		player.sendPacket(new EtcStatusUpdate(player));
		
		final Clan clan = player.getClan();
		if (clan != null)
		{
			player.sendPacket(new PledgeSkillList(clan));
			
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addCharName(player);
			final PledgeShowMemberListUpdate psmlu = new PledgeShowMemberListUpdate(player);
			
			for (Player member : clan.getOnlineMembers())
			{
				if (member == player)
					continue;
				
				member.sendPacket(sm);
				member.sendPacket(psmlu);
			}
			
			if (player.getSponsor() != 0)
			{
				final Player sponsor = World.getInstance().getPlayer(player.getSponsor());
				if (sponsor != null)
					sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addCharName(player));
			}
			else if (player.getApprentice() != 0)
			{
				final Player apprentice = World.getInstance().getPlayer(player.getApprentice());
				if (apprentice != null)
					apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addCharName(player));
			}
			
			final ClanHall ch = ClanHallManager.getInstance().getClanHallByOwner(clan);
			if (ch != null && !ch.getPaid())
				player.sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
			
			for (Castle castle : CastleManager.getInstance().getCastles())
			{
				final Map<Integer, Integer> skills = player.isClanLeader() ? castle.getSkillsLeader() : castle.getSkillsMember();
				skills.forEach((skillId, skillLvl) ->
				{
					if (castle.getId() == player.getClan().getCastleId())
						player.addSkill(SkillTable.getInstance().getInfo(skillId, skillLvl), true);
					else
						player.removeSkill(skillId, true);
				});
				
				final Siege siege = castle.getSiege();
				if (!siege.isInProgress())
					continue;
				
				final SiegeSide type = siege.getSide(clan);
				if (type == SiegeSide.ATTACKER)
					player.setSiegeState((byte) 1);
				else if (type == SiegeSide.DEFENDER || type == SiegeSide.OWNER)
					player.setSiegeState((byte) 2);
			}
			
			for (SiegableHall hall : ClanHallManager.getInstance().getSiegableHalls())
			{
				if (hall.isInSiege() && hall.isRegistered(clan))
					player.setSiegeState((byte) 1);
			}
			
			player.sendPacket(new PledgeShowMemberListUpdate(player));
			player.sendPacket(new PledgeShowMemberListAll(clan, 0));
			
			for (SubPledge sp : clan.getAllSubPledges())
				player.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
		}
		
		if (SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE) != CabalType.NORMAL)
		{
			CabalType cabal = SevenSignsManager.getInstance().getPlayerCabal(objectId);
			if (cabal != CabalType.NORMAL)
			{
				if (cabal == SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
					player.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill(), false);
				else
					player.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill(), false);
			}
		}
		else
		{
			player.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill().getId(), false);
			player.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill().getId(), false);
		}
		
		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			player.setSpawnProtection(true);
		
		player.spawnMe();
		
		player.setEnterWorldLoc(player.getX(), player.getY(), -16000);
		
		for (Entry<Integer, IntIntHolder> coupleEntry : CoupleManager.getInstance().getCouples().entrySet())
		{
			final IntIntHolder couple = coupleEntry.getValue();
			if (couple.getId() == objectId || couple.getValue() == objectId)
			{
				player.setCoupleId(coupleEntry.getKey());
				break;
			}
		}
		
		if (!player.isCursedWeaponEquipped())
		{
			int[] itemIds =
			{
				8190,
				8689
			};
			
			for (int itemId : itemIds)
			{
				if (player.getInventory().getItemByItemId(itemId) != null)
					player.destroyItem(player.getInventory().getItemByItemId(itemId), true);
			}
			
			player.removeSkill(3630, true);
			player.removeSkill(3631, true);
		}
		
		player.sendPacket(SystemMessageId.WELCOME_TO_LINEAGE);
		player.sendPacket(SevenSignsManager.getInstance().getCurrentPeriod().getMessageId());
		AnnouncementData.getInstance().showAnnouncements(player, false);
		
		PcCafeManager.getInstance().onPlayerLogin(player);
		
		CTFEvent.getInstance().onLogin(player);
		DMEvent.getInstance().onLogin(player);
		LMEvent.getInstance().onLogin(player);
		TvTEvent.getInstance().onLogin(player);
		
		if (player.getRace() == ClassRace.DARK_ELF && player.hasSkill(L2Skill.SKILL_SHADOW_SENSE))
			player.sendPacket(SystemMessage.getSystemMessage((GameTimeTaskManager.getInstance().isNight()) ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(L2Skill.SKILL_SHADOW_SENSE));
		
		player.getQuestList().getQuests(Quest::isTriggeredOnEnterWorld).forEach(q -> q.onEnterWorld(player));
		
		player.getInventory().updateWeight();
		
		player.sendPacket(new QuestList(player));
		player.sendPacket(new SkillList(player));
		player.sendPacket(new FriendList(player));
		player.sendPacket(new UserInfo(player));
		player.sendPacket(new ItemList(player, false));
		player.sendPacket(new ShortCutInit(player));
		
		player.checkCondition(player.getStatus().getMaxHp(), player.getStatus().getHp());
		
		if (player.isAlikeDead())
			player.sendPacket(new Die(player));
		
		if (Config.ENABLE_COMMUNITY_BOARD && MailBBSManager.getInstance().checkIfUnreadMail(player))
		{
			player.sendPacket(SystemMessageId.NEW_MAIL);
			player.sendPacket(new PlaySound("systemmsg_e.1233"));
			player.sendPacket(ExMailArrived.STATIC_PACKET);
		}
		
		if (Config.ENABLE_COMMUNITY_BOARD && clan != null && clan.isNoticeEnabled())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(player.getLocale(), "html/clan_notice.htm");
			html.replace("%clan_name%", clan.getName());
			html.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replace("action", "").replace("bypass", ""));
			sendPacket(html);
		}
		else if (Config.SERVER_NEWS)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile(player.getLocale(), "html/servnews.htm");
			sendPacket(html);
		}
		
		if (player.getPremiumService() == 1)
			onEnterPremium(player);
		
		PetitionManager.getInstance().checkActivePetition(player);
		
		player.onPlayerEnter();
		
		SafeDisconnectManager.getInstance().onEnterWorld(player);
		
		player.broadcastUserInfo();
		
		sendPacket(new SkillCoolTime(player));
		
		if (Olympiad.getInstance().playerInStadia(player))
			player.teleportTo(RestartType.TOWN);
		
		if (player.getClanJoinExpiryTime() > System.currentTimeMillis())
			player.sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);
		
		if (player.isInsideZone(ZoneId.SIEGE) && player.getSiegeState() < 2)
			player.teleportTo(RestartType.TOWN);
		
		if (player.isInsideZone(ZoneId.BOSS) && (System.currentTimeMillis() - player.getLastAccess()) > 300000)
			player.teleportTo(RestartType.TOWN);
		
		ClassMaster.showQuestionMark(player);
		
		final QuestState qs = player.getQuestList().getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("UC", null, player);
		
		PlayerGodManager.getInstance().onEnterWorld(player);
		player.restoreDressMe();
		if (TournamentEvent.isRunning())
		{
			player.sendPacket(new TutorialShowQuestionMark(TournamentEvent.TUTORIAL_QUESTION_MARK_ID));
			String lastStart = TournamentEvent.lastEvent();
			if (lastStart != null && !lastStart.isEmpty())
			{
				LocalTime startTime = LocalTime.parse(lastStart, DateTimeFormatter.ofPattern("HH:mm"));
				LocalTime now = LocalTime.now();
				int durationMinutes = TourData.getInstance().getConfig().getDuration();
				Duration remaining = Duration.between(now, startTime.plusMinutes(durationMinutes));
				long minutes = remaining.isNegative() ? 0 : remaining.toMinutes();
				if (minutes > 0)
					player.sendMessage("Tournament is live! Started at " + lastStart + "h. Remaining time: " + minutes + " minute(s).");
			}
		}
		if (Config.CTF_EVENT_ENABLED && (CTFEvent.getInstance().isParticipating() || CTFEvent.getInstance().isStarted()))
			player.sendPacket(new TutorialShowQuestionMark(CTFManager.TUTORIAL_QUESTION_MARK_ID));
		if (Config.DM_EVENT_ENABLED && (DMEvent.getInstance().isParticipating() || DMEvent.getInstance().isStarted()))
			player.sendPacket(new TutorialShowQuestionMark(DMManager.TUTORIAL_QUESTION_MARK_ID));
		if (Config.LM_EVENT_ENABLED && (LMEvent.getInstance().isParticipating() || LMEvent.getInstance().isStarted()))
			player.sendPacket(new TutorialShowQuestionMark(LMManager.TUTORIAL_QUESTION_MARK_ID));
		if (Config.TVT_EVENT_ENABLED && (TvTEvent.getInstance().isParticipating() || TvTEvent.getInstance().isStarted()))
			player.sendPacket(new TutorialShowQuestionMark(TvTManager.TUTORIAL_QUESTION_MARK_ID));
		for (ext.mods.gameserver.model.zone.type.RandomZone zone : RandomManager.getInstance().getActiveZones())
		{
			if (zone.isActive())
			{
				Integer qmId = RandomManager.TUTORIAL_QUESTION_MARK_ID;
				if (qmId != null)
					player.sendPacket(new TutorialShowQuestionMark(qmId));
			}
		}
		ext.mods.levelupmaker.LevelUpMakerManager.getInstance().sendQuestionMark(player);
		if (hwid.isProtectionOn())
			hwid.enterlog(player, getClient());

		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private void onEnterPremium(Player player)
	{
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date dt = new Date(player.getPremServiceData());
		player.sendMessage(player.getSysString(10_046, df.format(dt)));
		
		String color = player.getMemos().get("PlayerColorName");
		String title = player.getMemos().get("PlayerColorTitle");
		
		if (color != null)
		{
			player.setNameColor(Integer.decode("0x" + color));
			player.getAppearance().setTitleColor(Integer.decode("0x" + color));
		}
		if (title != null)
		{
			player.setTitleColor(Integer.decode("0x" + title));
			player.getAppearance().setTitleColor(Integer.decode("0x" + title));
		}
		player.broadcastUserInfo();
	}
	
	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}