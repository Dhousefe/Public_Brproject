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
package ext.mods.gameserver.model.actor.instance;

import java.util.Map.Entry;
import java.util.StringTokenizer;

import ext.mods.Config;
import ext.mods.gameserver.data.manager.SevenSignsManager;
import ext.mods.gameserver.enums.CabalType;
import ext.mods.gameserver.enums.SealType;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.actor.template.NpcTemplate;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.network.SystemMessageId;
import ext.mods.gameserver.network.serverpackets.ActionFailed;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.gameserver.network.serverpackets.SystemMessage;

public class SignsPriest extends Folk
{
	public SignsPriest(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player.getCurrentFolk() == null || player.getCurrentFolk().getObjectId() != getObjectId())
			return;
		
		if (command.startsWith("SevenSignsDesc"))
			showChatWindow(player, Integer.parseInt(command.substring(15)), null, true);
		else if (command.startsWith("SevenSigns"))
		{
			final StringTokenizer st = new StringTokenizer(command.trim());
			st.nextToken();
			
			final int value = Integer.parseInt(st.nextToken());
			switch (value)
			{
				case 2:
					if (!player.getInventory().validateCapacity(1))
					{
						player.sendPacket(SystemMessageId.SLOTS_FULL);
						return;
					}
					
					if (!player.reduceAdena(SevenSignsManager.RECORD_SEVEN_SIGNS_COST, true))
					{
						showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn_no" : "dusk_no", false);
						return;
					}
					
					player.addItem(SevenSignsManager.RECORD_SEVEN_SIGNS_ID, 1, true);
					
					showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn" : "dusk", false);
					break;
				
				case 33:
					CabalType cabal = CabalType.VALUES[Integer.parseInt(st.nextToken())];
					
					if (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()) != CabalType.NORMAL)
					{
						showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn_member" : "dusk_member", false);
						return;
					}
					
					if (!Config.SEVEN_SIGNS_BYPASS_PREREQUISITES)
					{
						final int classLevel = player.getClassId().getLevel();
						if (classLevel == 0)
						{
							showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn_firstclass" : "dusk_firstclass", false);
							return;
						}
						
						if (classLevel > 1)
						{
							final boolean hasCastle = player.getClan() != null && player.getClan().hasCastle();
							
							if (cabal == CabalType.DUSK && hasCastle)
							{
								showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "signs_33_dusk_no.htm");
								return;
							}
							
							if (cabal == CabalType.DAWN && !hasCastle)
							{
								showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "signs_33_dawn_fee.htm");
								return;
							}
						}
					}
					
					showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn" : "dusk", false);
					break;
				
				case 34:
					final boolean canPayFee = (player.getAdena() >= SevenSignsManager.ADENA_JOIN_DAWN_COST) || player.getInventory().hasItems(SevenSignsManager.CERTIFICATE_OF_APPROVAL_ID);
					showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + ((canPayFee) ? "signs_33_dawn.htm" : "signs_33_dawn_no.htm"));
					break;
				
				case 3, 8:
					cabal = CabalType.VALUES[Integer.parseInt(st.nextToken())];
					showChatWindow(player, value, cabal.getShortName(), false);
					break;
				
				case 4:
					cabal = CabalType.VALUES[Integer.parseInt(st.nextToken())];
					SealType seal = SealType.VALUES[Integer.parseInt(st.nextToken())];
					
					if (!Config.SEVEN_SIGNS_BYPASS_PREREQUISITES)
					{
						final boolean hasCastle = player.getClan() != null && player.getClan().hasCastle();
						
						if (cabal == CabalType.DUSK && hasCastle)
						{
							showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "signs_33_dusk_no.htm");
							return;
						}
						
						if (cabal == CabalType.DAWN && (!hasCastle && !player.destroyItemByItemId(SevenSignsManager.CERTIFICATE_OF_APPROVAL_ID, 1, false) && !player.reduceAdena(SevenSignsManager.ADENA_JOIN_DAWN_COST, false)))
						{
							showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "signs_33_dawn_no.htm");
							return;
						}
					}
					
					SevenSignsManager.getInstance().setPlayerInfo(player.getObjectId(), cabal, seal);
					
					if (cabal == CabalType.DAWN)
						player.sendPacket(SystemMessageId.SEVENSIGNS_PARTECIPATION_DAWN);
					else
						player.sendPacket(SystemMessageId.SEVENSIGNS_PARTECIPATION_DUSK);
						
					switch (seal)
					{
						case AVARICE:
							player.sendPacket(SystemMessageId.FIGHT_FOR_AVARICE);
							break;
						
						case GNOSIS:
							player.sendPacket(SystemMessageId.FIGHT_FOR_GNOSIS);
							break;
						
						case STRIFE:
							player.sendPacket(SystemMessageId.FIGHT_FOR_STRIFE);
							break;
					}
					
					showChatWindow(player, 4, cabal.getShortName(), false);
					break;
				
				case 5:
					if (SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()) == CabalType.NORMAL)
						showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn_no" : "dusk_no", false);
					else
						showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn" : "dusk", false);
					break;
				
				case 6:
					int stoneType = Integer.parseInt(st.nextToken());
					
					ItemInstance blueStones = player.getInventory().getItemByItemId(SevenSignsManager.SEAL_STONE_BLUE_ID);
					ItemInstance greenStones = player.getInventory().getItemByItemId(SevenSignsManager.SEAL_STONE_GREEN_ID);
					ItemInstance redStones = player.getInventory().getItemByItemId(SevenSignsManager.SEAL_STONE_RED_ID);
					
					int blueStoneCount = blueStones == null ? 0 : blueStones.getCount();
					int greenStoneCount = greenStones == null ? 0 : greenStones.getCount();
					int redStoneCount = redStones == null ? 0 : redStones.getCount();
					
					int contribScore = SevenSignsManager.getInstance().getPlayerContribScore(player.getObjectId());
					boolean stonesFound = false;
					
					if (contribScore == Config.MAXIMUM_PLAYER_CONTRIB)
						player.sendPacket(SystemMessageId.CONTRIB_SCORE_EXCEEDED);
					else
					{
						String stoneColor = "";
						int stoneCount = 0;
						int stoneId = 0;
						
						switch (stoneType)
						{
							case 1:
								stoneColor = "Blue";
								stoneId = SevenSignsManager.SEAL_STONE_BLUE_ID;
								stoneCount = blueStoneCount;
								break;
							
							case 2:
								stoneColor = "Green";
								stoneId = SevenSignsManager.SEAL_STONE_GREEN_ID;
								stoneCount = greenStoneCount;
								break;
							
							case 3:
								stoneColor = "Red";
								stoneId = SevenSignsManager.SEAL_STONE_RED_ID;
								stoneCount = redStoneCount;
								break;
							
							case 4:
								int tempContribScore = contribScore;
								int redContribCount = (Config.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSignsManager.SEAL_STONE_RED_VALUE;
								if (redContribCount > redStoneCount)
									redContribCount = redStoneCount;
								
								tempContribScore += redContribCount * SevenSignsManager.SEAL_STONE_RED_VALUE;
								int greenContribCount = (Config.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSignsManager.SEAL_STONE_GREEN_VALUE;
								if (greenContribCount > greenStoneCount)
									greenContribCount = greenStoneCount;
								
								tempContribScore += greenContribCount * SevenSignsManager.SEAL_STONE_GREEN_VALUE;
								int blueContribCount = (Config.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSignsManager.SEAL_STONE_BLUE_VALUE;
								if (blueContribCount > blueStoneCount)
									blueContribCount = blueStoneCount;
								
								if (redContribCount > 0)
									stonesFound |= player.destroyItemByItemId(SevenSignsManager.SEAL_STONE_RED_ID, redContribCount, true);
								
								if (greenContribCount > 0)
									stonesFound |= player.destroyItemByItemId(SevenSignsManager.SEAL_STONE_GREEN_ID, greenContribCount, true);
								
								if (blueContribCount > 0)
									stonesFound |= player.destroyItemByItemId(SevenSignsManager.SEAL_STONE_BLUE_ID, blueContribCount, true);
								
								if (!stonesFound)
									showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn_no_stones" : "dusk_no_stones", false);
								else
								{
									contribScore = SevenSignsManager.getInstance().addPlayerStoneContrib(player.getObjectId(), blueContribCount, greenContribCount, redContribCount);
									player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CONTRIB_SCORE_INCREASED_S1).addItemNumber(contribScore));
									
									showChatWindow(player, 6, (this instanceof DawnPriest) ? "dawn" : "dusk", false);
								}
								return;
						}
						
						final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
						html.setFile(player.getLocale(), SevenSignsManager.SEVEN_SIGNS_HTML_PATH + ((this instanceof DawnPriest) ? "signs_6_dawn_contribute.htm" : "signs_6_dusk_contribute.htm"));
						html.replace("%stoneColor%", stoneColor);
						html.replace("%stoneCount%", stoneCount);
						html.replace("%stoneItemId%", stoneId);
						html.replace("%objectId%", getObjectId());
						player.sendPacket(html);
					}
					break;
				
				case 7:
					int amount = 0;
					try
					{
						amount = Integer.parseInt(command.substring(13).trim());
					}
					catch (Exception e)
					{
						showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "blkmrkt_3.htm");
						return;
					}
					
					if (amount < 1)
					{
						showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "blkmrkt_3.htm");
						return;
					}
					
					if (player.getAncientAdena() < amount)
					{
						showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "blkmrkt_4.htm");
						return;
					}
					
					if ((Integer.MAX_VALUE - player.getInventory().getAdena() - amount) < 0)
						return;
					
					if (player.reduceAncientAdena(amount, true))
						player.addAdena(amount, true);
					
					showChatWindow(player, SevenSignsManager.SEVEN_SIGNS_HTML_PATH + "blkmrkt_5.htm");
					break;
				
				case 9:
					if (SevenSignsManager.getInstance().isSealValidationPeriod() && SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId()) == SevenSignsManager.getInstance().getWinningCabal())
					{
						if (!player.getInventory().validateCapacity(1))
						{
							player.sendPacket(SystemMessageId.SLOTS_FULL);
							return;
						}
						
						final int reward = SevenSignsManager.getInstance().getAncientAdenaReward(player.getObjectId());
						if (reward <= 0)
						{
							showChatWindow(player, 9, (this instanceof DawnPriest) ? "dawn_b" : "dusk_b", false);
							return;
						}
						
						player.addAncientAdena(reward, true);
						
						showChatWindow(player, 9, (this instanceof DawnPriest) ? "dawn_a" : "dusk_a", false);
					}
					break;
				
				case 11:
					try
					{
						final int x = Integer.parseInt(st.nextToken());
						final int y = Integer.parseInt(st.nextToken());
						final int z = Integer.parseInt(st.nextToken());
						
						final int ancientAdenaCost = Integer.parseInt(st.nextToken());
						if (ancientAdenaCost > 0 && !player.reduceAncientAdena(ancientAdenaCost, true))
							return;
						
						player.teleportTo(x, y, z, 0);
					}
					catch (Exception e)
					{
						LOGGER.error("An error occurred while teleporting a player.", e);
					}
					break;
				
				case 16:
					showChatWindow(player, value, (this instanceof DawnPriest) ? "dawn" : "dusk", false);
					break;
				
				case 17:
					stoneType = Integer.parseInt(command.substring(14));
					
					int stoneId = 0;
					int stoneCount = 0;
					int stoneValue = 0;
					
					String stoneColor = "";
					
					if (!player.getInventory().validateCapacity(1))
					{
						player.sendPacket(SystemMessageId.SLOTS_FULL);
						return;
					}
					
					switch (stoneType)
					{
						case 1:
							stoneColor = "blue";
							stoneId = SevenSignsManager.SEAL_STONE_BLUE_ID;
							stoneValue = SevenSignsManager.SEAL_STONE_BLUE_VALUE;
							break;
						
						case 2:
							stoneColor = "green";
							stoneId = SevenSignsManager.SEAL_STONE_GREEN_ID;
							stoneValue = SevenSignsManager.SEAL_STONE_GREEN_VALUE;
							break;
						
						case 3:
							stoneColor = "red";
							stoneId = SevenSignsManager.SEAL_STONE_RED_ID;
							stoneValue = SevenSignsManager.SEAL_STONE_RED_VALUE;
							break;
						
						case 4:
							ItemInstance blueStonesAll = player.getInventory().getItemByItemId(SevenSignsManager.SEAL_STONE_BLUE_ID);
							ItemInstance greenStonesAll = player.getInventory().getItemByItemId(SevenSignsManager.SEAL_STONE_GREEN_ID);
							ItemInstance redStonesAll = player.getInventory().getItemByItemId(SevenSignsManager.SEAL_STONE_RED_ID);
							
							int blueStoneCountAll = blueStonesAll == null ? 0 : blueStonesAll.getCount();
							int greenStoneCountAll = greenStonesAll == null ? 0 : greenStonesAll.getCount();
							int redStoneCountAll = redStonesAll == null ? 0 : redStonesAll.getCount();
							int ancientAdenaRewardAll = 0;
							
							ancientAdenaRewardAll = SevenSignsManager.calcScore(blueStoneCountAll, greenStoneCountAll, redStoneCountAll);
							
							if (ancientAdenaRewardAll == 0)
							{
								showChatWindow(player, 18, (this instanceof DawnPriest) ? "dawn_no_stones" : "dusk_no_stones", false);
								return;
							}
							
							if (blueStoneCountAll > 0)
								player.destroyItemByItemId(SevenSignsManager.SEAL_STONE_BLUE_ID, blueStoneCountAll, true);
							
							if (greenStoneCountAll > 0)
								player.destroyItemByItemId(SevenSignsManager.SEAL_STONE_GREEN_ID, greenStoneCountAll, true);
							
							if (redStoneCountAll > 0)
								player.destroyItemByItemId(SevenSignsManager.SEAL_STONE_RED_ID, redStoneCountAll, true);
							
							player.addAncientAdena(ancientAdenaRewardAll, true);
							
							showChatWindow(player, 18, (this instanceof DawnPriest) ? "dawn" : "dusk", false);
							return;
					}
					
					ItemInstance stoneInstance = player.getInventory().getItemByItemId(stoneId);
					if (stoneInstance != null)
						stoneCount = stoneInstance.getCount();
					
					NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
					html.setFile(player.getLocale(), SevenSignsManager.SEVEN_SIGNS_HTML_PATH + ((this instanceof DawnPriest) ? "signs_17_dawn.htm" : "signs_17_dusk.htm"));
					html.replace("%stoneColor%", stoneColor);
					html.replace("%stoneValue%", stoneValue);
					html.replace("%stoneCount%", stoneCount);
					html.replace("%stoneItemId%", stoneId);
					html.replace("%objectId%", getObjectId());
					player.sendPacket(html);
					break;
				
				case 18:
					int itemId = Integer.parseInt(st.nextToken());
					amount = 0;
					try
					{
						amount = Integer.parseInt(command.substring(19).trim());
					}
					catch (Exception e)
					{
						showChatWindow(player, 18, (this instanceof DawnPriest) ? "dawn_failed" : "dusk_failed", false);
						return;
					}
					
					ItemInstance convertItem = player.getInventory().getItemByItemId(itemId);
					if (convertItem == null)
					{
						showChatWindow(player, 18, (this instanceof DawnPriest) ? "dawn_no_stones" : "dusk_no_stones", false);
						return;
					}
					
					if (amount <= 0 || amount > convertItem.getCount())
					{
						showChatWindow(player, 18, (this instanceof DawnPriest) ? "dawn_low_stones" : "dusk_low_stones", false);
						return;
					}
					
					int reward = 0;
					switch (itemId)
					{
						case SevenSignsManager.SEAL_STONE_BLUE_ID:
							reward = SevenSignsManager.calcScore(amount, 0, 0);
							break;
						
						case SevenSignsManager.SEAL_STONE_GREEN_ID:
							reward = SevenSignsManager.calcScore(0, amount, 0);
							break;
						
						case SevenSignsManager.SEAL_STONE_RED_ID:
							reward = SevenSignsManager.calcScore(0, 0, amount);
							break;
					}
					
					if (!player.destroyItemByItemId(itemId, amount, true))
						return;
					
					player.addAncientAdena(reward, true);
					
					showChatWindow(player, 18, (this instanceof DawnPriest) ? "dawn" : "dusk", false);
					break;
				
				case 19:
					cabal = CabalType.VALUES[Integer.parseInt(st.nextToken())];
					seal = SealType.VALUES[Integer.parseInt(st.nextToken())];
					
					showChatWindow(player, value, seal.getShortName() + "_" + cabal.getShortName(), false);
					break;
				
				case 20:
					final StringBuilder sb = new StringBuilder();
					
					if (this instanceof DawnPriest)
						sb.append("<html><body>Priest of Dawn:<br><font color=\"LEVEL\">[ Seal Status ]</font><br>");
					else
						sb.append("<html><body>Dusk Priestess:<br><font color=\"LEVEL\">[ Status of the Seals ]</font><br>");
					
					for (Entry<SealType, CabalType> entry : SevenSignsManager.getInstance().getSealOwners().entrySet())
					{
						final SealType s = entry.getKey();
						final CabalType so = entry.getValue();
						
						if (so != CabalType.NORMAL)
							sb.append("[" + s.getFullName() + ": " + so.getFullName() + "]<br>");
						else
							sb.append("[" + s.getFullName() + ": Nothingness]<br>");
					}
					
					sb.append("<a action=\"bypass -h npc_" + getObjectId() + "_Chat 0\">Go back.</a></body></html>");
					
					html = new NpcHtmlMessage(getObjectId());
					html.setHtml(sb.toString());
					player.sendPacket(html);
					break;
				
				case 21:
					try
					{
						itemId = Integer.parseInt(st.nextToken());
						
						amount = Integer.parseInt(command.substring(19).trim());
					}
					catch (Exception e)
					{
						showChatWindow(player, 6, (this instanceof DawnPriest) ? "dawn_failure" : "dusk_failure", false);
						return;
					}
					
					stoneCount = Math.min(amount, player.getInventory().getItemCount(itemId));
					
					int score = SevenSignsManager.getInstance().getPlayerContribScore(player.getObjectId());
					
					int maxStoneCountToContribute = 0;
					switch (itemId)
					{
						case SevenSignsManager.SEAL_STONE_BLUE_ID:
							maxStoneCountToContribute = (Config.MAXIMUM_PLAYER_CONTRIB - score) / SevenSignsManager.SEAL_STONE_BLUE_VALUE;
							break;
						
						case SevenSignsManager.SEAL_STONE_GREEN_ID:
							maxStoneCountToContribute = (Config.MAXIMUM_PLAYER_CONTRIB - score) / SevenSignsManager.SEAL_STONE_GREEN_VALUE;
							break;
						
						case SevenSignsManager.SEAL_STONE_RED_ID:
							maxStoneCountToContribute = (Config.MAXIMUM_PLAYER_CONTRIB - score) / SevenSignsManager.SEAL_STONE_RED_VALUE;
							break;
					}
					
					stoneCount = Math.min(stoneCount, maxStoneCountToContribute);
					
					if (!player.destroyItemByItemId(itemId, stoneCount, true))
					{
						showChatWindow(player, 6, (this instanceof DawnPriest) ? "dawn_low_stones" : "dusk_low_stones", false);
						return;
					}
					
					switch (itemId)
					{
						case SevenSignsManager.SEAL_STONE_BLUE_ID:
							score = SevenSignsManager.getInstance().addPlayerStoneContrib(player.getObjectId(), stoneCount, 0, 0);
							break;
						
						case SevenSignsManager.SEAL_STONE_GREEN_ID:
							score = SevenSignsManager.getInstance().addPlayerStoneContrib(player.getObjectId(), 0, stoneCount, 0);
							break;
						
						case SevenSignsManager.SEAL_STONE_RED_ID:
							score = SevenSignsManager.getInstance().addPlayerStoneContrib(player.getObjectId(), 0, 0, stoneCount);
							break;
					}
					
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CONTRIB_SCORE_INCREASED_S1).addItemNumber(score));
					
					showChatWindow(player, 6, (this instanceof DawnPriest) ? "dawn" : "dusk", false);
					break;
				
				default:
					showChatWindow(player, value, null, false);
					break;
			}
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		final int npcId = getTemplate().getNpcId();
		String filename = SevenSignsManager.SEVEN_SIGNS_HTML_PATH;
		
		final CabalType playerCabal = SevenSignsManager.getInstance().getPlayerCabal(player.getObjectId());
		final CabalType winningCabal = SevenSignsManager.getInstance().getWinningCabal();
		
		switch (npcId)
		{
			case 31092:
				filename += "blkmrkt_1.htm";
				break;
			
			case 31113:
				if (Config.STRICT_SEVENSIGNS)
				{
					final CabalType sealAvariceOwner = SevenSignsManager.getInstance().getSealOwner(SealType.AVARICE);
					switch (winningCabal)
					{
						case DAWN:
							if (playerCabal != winningCabal || playerCabal != sealAvariceOwner)
							{
								player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
								player.sendPacket(ActionFailed.STATIC_PACKET);
								return;
							}
							break;
						
						case DUSK:
							if (playerCabal != winningCabal || playerCabal != sealAvariceOwner)
							{
								player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
								player.sendPacket(ActionFailed.STATIC_PACKET);
								return;
							}
							break;
						
						default:
							player.sendPacket(SystemMessageId.QUEST_EVENT_PERIOD);
							return;
					}
				}
				filename += "mammmerch_1.htm";
				break;
			
			case 31126:
				if (Config.STRICT_SEVENSIGNS)
				{
					final CabalType sealGnosisOwner = SevenSignsManager.getInstance().getSealOwner(SealType.GNOSIS);
					switch (winningCabal)
					{
						case DAWN:
							if (playerCabal != winningCabal || playerCabal != sealGnosisOwner)
							{
								player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DAWN);
								player.sendPacket(ActionFailed.STATIC_PACKET);
								return;
							}
							break;
						
						case DUSK:
							if (playerCabal != winningCabal || playerCabal != sealGnosisOwner)
							{
								player.sendPacket(SystemMessageId.CAN_BE_USED_BY_DUSK);
								player.sendPacket(ActionFailed.STATIC_PACKET);
								return;
							}
							break;
					}
				}
				filename += "mammblack_1.htm";
				break;
			
			default:
				filename = (getHtmlPath(player, npcId, val));
				break;
		}
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(player.getLocale(), filename);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private void showChatWindow(Player player, int val, String suffix, boolean isDescription)
	{
		String filename = SevenSignsManager.SEVEN_SIGNS_HTML_PATH;
		
		filename += (isDescription) ? "desc_" + val : "signs_" + val;
		filename += (suffix != null) ? "_" + suffix + ".htm" : ".htm";
		
		showChatWindow(player, filename);
	}
}