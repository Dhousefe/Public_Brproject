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
package ext.mods.gameserver.scripting.script.ai.individual.Guard;

import ext.mods.commons.random.Rnd;

import ext.mods.Config;
import ext.mods.gameserver.enums.IntentionType;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.enums.actors.Sex;
import ext.mods.gameserver.model.actor.Creature;
import ext.mods.gameserver.model.actor.Npc;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcSay;
import ext.mods.gameserver.scripting.script.ai.individual.DefaultNpc;
import ext.mods.gameserver.skills.L2Skill;

public class Guard extends DefaultNpc
{
	private boolean _isChattyGuard;
	private long _lastAggroSay;
	private long _lastNormalSay;
	
	private static final int AGGRO_MSG_START = 11000;
	private static final int AGGRO_MSG_END = 11019;
	private static final int NORMAL_MALE_MSG_START = 11100;
	private static final int NORMAL_MALE_MSG_END = 11113;
	private static final int NORMAL_FEMALE_MSG_START = 11200;
	private static final int NORMAL_FEMALE_MSG_END = 11213;
	
	public Guard()
	{
		super("ai/individual/Guard");
	}
	
	public Guard(String descr)
	{
		super(descr);
	}
	
	@Override
	public void onCreated(Npc npc)
	{
		if (Config.ENABLE_GUARD_CHAT)
		{
			_isChattyGuard = Rnd.get(100) < Config.GUARD_CHATTY_CHANCE;
			_lastAggroSay = 0;
			_lastNormalSay = 0;
			
			if (_isChattyGuard)
				startQuestTimerAtFixedRate("guard_chat_check", npc, null, 5000, 5000);
		}
		
		if (getNpcIntAIParam(npc, "MoveAroundSocial") > 0 || getNpcIntAIParam(npc, "MoveAroundSocial1") > 0)
			startQuestTimerAtFixedRate("1671", npc, null, 10000, 10000);
		
		startQuestTimerAtFixedRate("9903", npc, null, 60000, 60000);
		
		super.onCreated(npc);
	}
	
	@Override
	public void onAttacked(Npc npc, Creature attacker, int damage, L2Skill skill)
	{
		if (Config.ENABLE_GUARD_CHAT && _isChattyGuard && attacker instanceof Player player)
		{
			if (player.getKarma() > 0 && System.currentTimeMillis() > _lastAggroSay + Config.GUARD_SAY_AGGRO_PERIOD)
			{
				sayAggressiveMessage(npc, player);
				_lastAggroSay = System.currentTimeMillis();
			}
		}
		
		npc.getAI().addAttackDesire(attacker, 2000);
	}
	
	@Override
	public void onSeeCreature(Npc npc, Creature creature)
	{
		if (creature instanceof Player player && player.getKarma() > 0)
		{
			if (Config.ENABLE_GUARD_CHAT && _isChattyGuard && System.currentTimeMillis() > _lastAggroSay + Config.GUARD_SAY_AGGRO_PERIOD)
			{
				sayAggressiveMessage(npc, player);
				_lastAggroSay = System.currentTimeMillis();
			}
			
			npc.getAI().addAttackDesire(player, 1500);
		}
	}
	
	@Override
	public String onTimer(String name, Npc npc, Player player)
	{
		if (name.equalsIgnoreCase("guard_chat_check"))
		{
			if (Config.ENABLE_GUARD_CHAT && _isChattyGuard && !npc.isDead() && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			{
				if (System.currentTimeMillis() > _lastNormalSay + Config.GUARD_SAY_NORMAL_PERIOD)
				{
					for (Player nearbyPlayer : npc.getKnownTypeInRadius(Player.class, Config.GUARD_CHAT_RANGE))
					{
						if (nearbyPlayer != null && nearbyPlayer.getKarma() == 0 && nearbyPlayer.getAppearance().isVisible())
						{
							if (Rnd.get(100) < Config.GUARD_SAY_NORMAL_CHANCE)
							{
								sayNormalMessage(npc, nearbyPlayer);
								_lastNormalSay = System.currentTimeMillis();
								break;
							}
						}
					}
				}
			}
		}
		else if (name.equalsIgnoreCase("1671"))
		{
			if (npc.getStatus().getHpRatio() > 0.4 && !npc.isDead() && npc.getAI().getCurrentIntention().getType() != IntentionType.ATTACK)
			{
				final int moveAroundSocial1 = getNpcIntAIParam(npc, "MoveAroundSocial1");
				final int moveAroundSocial = getNpcIntAIParam(npc, "MoveAroundSocial");
				
				if (moveAroundSocial > 0 || moveAroundSocial1 > 0)
				{
					if (moveAroundSocial > 0 && Rnd.get(100) < Config.NPC_ANIMATION)
						npc.getAI().addSocialDesire(3, (moveAroundSocial * 1000) / 30, 50);
					else if (moveAroundSocial1 > 0 && Rnd.get(100) < Config.NPC_ANIMATION)
						npc.getAI().addSocialDesire(3, (moveAroundSocial1 * 1000) / 30, 50);
				}
			}
		}
		else if (name.equalsIgnoreCase("9903"))
		{
			if (!npc.isInMyTerritory())
				npc.teleportTo(npc.getSpawnLocation(), 0);
		}
		
		return super.onTimer(name, npc, player);
	}
	
	/**
	 * Diz mensagem agressiva para player PK
	 * @param npc O guarda que vai falar
	 * @param player O player PK alvo
	 */
	private void sayAggressiveMessage(Npc npc, Player player)
	{
		final int messageId = Rnd.get(AGGRO_MSG_START, AGGRO_MSG_END + 1);
		final String message = player.getSysString(messageId).replace("{name}", player.getName());
		npc.broadcastPacket(new NpcSay(npc, SayType.ALL, message));
	}
	
	/**
	 * Diz mensagem normal baseada no sexo do player
	 * @param npc O guarda que vai falar
	 * @param player O player alvo
	 */
	private void sayNormalMessage(Npc npc, Player player)
	{
		final int messageId;
		
		if (player.getAppearance().getSex() == Sex.MALE)
			messageId = Rnd.get(NORMAL_MALE_MSG_START, NORMAL_MALE_MSG_END + 1);
		else
			messageId = Rnd.get(NORMAL_FEMALE_MSG_START, NORMAL_FEMALE_MSG_END + 1);
		
		final String message = player.getSysString(messageId).replace("{name}", player.getName());
		npc.broadcastPacket(new NpcSay(npc, SayType.ALL, message));
	}
}