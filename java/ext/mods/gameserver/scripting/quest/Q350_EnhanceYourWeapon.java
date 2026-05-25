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
package ext.mods.gameserver.scripting.quest;

import ext.mods.commons.random.Rnd;
import ext.mods.commons.util.ArraysUtil;

import ext.mods.gameserver.data.xml.SoulCrystalData;
import ext.mods.gameserver.enums.QuestStatus;
import ext.mods.gameserver.model.WorldObject;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.container.npc.AbsorbInfo;
import ext.mods.gameserver.model.actor.instance.Monster;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.model.records.LevelingInfo;
import ext.mods.gameserver.model.records.SoulCrystal;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.scripting.Quest;
import ext.mods.gameserver.scripting.QuestState;

public class Q350_EnhanceYourWeapon extends Quest
{
	private static final String QUEST_NAME = "Q350_EnhanceYourWeapon";
	
	public Q350_EnhanceYourWeapon()
	{
		super(350, "Enhance Your Weapon");
		
		addQuestStart(30115, 30194, 30856);
		addTalkId(30115, 30194, 30856);
		
		for (int npcId : SoulCrystalData.getInstance().getLevelingInfos().keySet())
			addMyDying(npcId);
		
		for (int crystalId : SoulCrystalData.getInstance().getSoulCrystals().keySet())
			addItemUse(crystalId);
	}
	
	@Override
	public String onAdvEvent(String event, Npc npc, Player player)
	{
		String htmltext = event;
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		if (event.endsWith("-04.htm"))
		{
			st.setState(QuestStatus.STARTED);
			st.setCond(1);
			playSound(player, SOUND_ACCEPT);
		}
		else if (event.endsWith("-09.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, 4629, 1);
		}
		else if (event.endsWith("-10.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, 4640, 1);
		}
		else if (event.endsWith("-11.htm"))
		{
			playSound(player, SOUND_MIDDLE);
			giveItems(player, 4651, 1);
		}
		else if (event.endsWith("-exit.htm"))
			st.exitQuest(true);
		
		return htmltext;
	}
	
	@Override
	public String onTalk(Npc npc, Player player)
	{
		String htmltext = getNoQuestMsg();
		QuestState st = player.getQuestList().getQuestState(QUEST_NAME);
		if (st == null)
			return htmltext;
		
		switch (st.getState())
		{
			case CREATED:
				if (player.getStatus().getLevel() < 40)
					htmltext = npc.getNpcId() + "-lvl.htm";
				else
					htmltext = npc.getNpcId() + "-01.htm";
				break;
			
			case STARTED:
				for (ItemInstance item : player.getInventory().getItems())
				{
					if (SoulCrystalData.getInstance().getSoulCrystals().get(item.getItemId()) != null)
						return npc.getNpcId() + "-03.htm";
				}
				htmltext = npc.getNpcId() + "-21.htm";
				break;
		}
		
		return htmltext;
	}
	
	@Override
	public void onItemUse(ItemInstance item, Player user, WorldObject target)
	{
		if (user.isDead())
			return;
		
		if (!(target instanceof Monster monster))
			return;
		
		if (monster.isDead() || !SoulCrystalData.getInstance().getLevelingInfos().containsKey(monster.getNpcId()))
			return;
		
		monster.addAbsorber(user, item);
	}
	
	@Override
	public void onMyDying(Npc npc, Creature killer)
	{
		final Player player = killer.getActingPlayer();
		if (player == null)
			return;
		
		final LevelingInfo npcInfo = SoulCrystalData.getInstance().getLevelingInfos().get(npc.getNpcId());
		if (npcInfo == null)
			return;
		
		final int chance = Rnd.get(100);
		final Monster monster = (Monster) npc;
		
		switch (npcInfo.absorbCrystalType())
		{
			case FULL_PARTY:
				for (QuestState st : getPartyMembersState(player, npc, QuestStatus.STARTED))
					tryToStageCrystal(st.getPlayer(), monster, npcInfo, chance);
				break;
			
			case PARTY_ONE_RANDOM:
				final QuestState st = getRandomPartyMemberState(player, npc, QuestStatus.STARTED);
				if (st != null)
					tryToStageCrystal(st.getPlayer(), monster, npcInfo, chance);
				break;
			
			case LAST_HIT:
				if (checkPlayerState(player, npc, QuestStatus.STARTED) != null)
					tryToStageCrystal(player, monster, npcInfo, chance);
				break;
		}
	}
	
	/**
	 * Define the Soul Crystal and try to stage it.<br>
	 * <br>
	 * Check for quest enabled, crystal(s) in inventory, required usage of crystal, mob's ability to level crystal and {@link Monster} vs {@link Player} level gap.
	 * @param player : The {@link Player} to make checks on.
	 * @param monster : The {@link Monster} to make checks on.
	 * @param npcInfo : The {@link LevelingInfo} informations.
	 * @param chance : Input variable used to determine keep/stage/break of the crystal.
	 */
	private static void tryToStageCrystal(Player player, Monster monster, LevelingInfo npcInfo, int chance)
	{
		SoulCrystal crystalData = null;
		ItemInstance crystalItem = null;
		
		for (ItemInstance item : player.getInventory().getItems())
		{
			SoulCrystal data = SoulCrystalData.getInstance().getSoulCrystals().get(item.getItemId());
			if (data == null)
				continue;
			
			if (crystalData != null)
			{
				if (npcInfo.isSkillRequired())
				{
					final AbsorbInfo ai = monster.getAbsorbInfo(player.getObjectId());
					if (ai != null && ai.isRegistered())
						player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION);
				}
				else
					player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED_RESONATION);
				
				return;
			}
			
			crystalData = data;
			crystalItem = item;
		}
		
		if (crystalData == null || crystalItem == null)
			return;
		
		if (npcInfo.isSkillRequired())
		{
			final AbsorbInfo ai = monster.getAbsorbInfo(player.getObjectId());
			if (ai == null || !ai.isRegistered())
				return;
			
			if (!ai.isValid(crystalItem.getObjectId()))
			{
				player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_REFUSED);
				return;
			}
		}
		
		if (!ArraysUtil.contains(npcInfo.levelList(), crystalData.level()))
		{
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_REFUSED);
			return;
		}
		
		if (chance < npcInfo.chanceStage())
			exchangeCrystal(player, crystalData, true);
		else if (chance < (npcInfo.chanceStage() + npcInfo.chanceBreak()))
			exchangeCrystal(player, crystalData, false);
		else
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_FAILED);
	}
	
	/**
	 * Remove the old crystal and add new one if stage, broken crystal if break. Send messages in both cases.
	 * @param player : The {@link Player} to check on (inventory and send messages).
	 * @param sc : The {@link SoulCrystal} to take information from.
	 * @param stage : Switch to determine success or fail.
	 */
	private static void exchangeCrystal(Player player, SoulCrystal sc, boolean stage)
	{
		takeItems(player, sc.initialItemId(), 1);
		if (stage)
		{
			player.sendPacket(SystemMessageId.SOUL_CRYSTAL_ABSORBING_SUCCEEDED);
			giveItems(player, sc.stagedItemId(), 1);
			playSound(player, SOUND_ITEMGET);
		}
		else
		{
			int broken = sc.brokenItemId();
			if (broken != 0)
			{
				player.sendPacket(SystemMessageId.SOUL_CRYSTAL_BROKE);
				giveItems(player, broken, 1);
			}
		}
	}
}