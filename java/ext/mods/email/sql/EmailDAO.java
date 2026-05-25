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
package ext.mods.email.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import ext.mods.email.task.EmailDeliveryTask;
import ext.mods.commons.pool.ConnectionPool;
import ext.mods.gameserver.data.SkillTable;
import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.Augmentation;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.gameserver.skills.L2Skill;

public class EmailDAO
{
	public static boolean isPending(int emailId)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT 1 FROM player_emails WHERE email_id=? AND status='PENDING'"))
		{
			ps.setInt(1, emailId);
			try (ResultSet rs = ps.executeQuery())
			{
				return rs.next();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static void expireAndReturnToSender(int emailId)
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("UPDATE player_emails SET status='EXPIRED' WHERE email_id=? AND status='PENDING'"))
			{
				ps.setInt(1, emailId);
				ps.executeUpdate();
			}
			
			try (PreparedStatement ps = con.prepareStatement("SELECT sender_id, item_object_id, item_id, count, enchant_level, is_augmented, augment_id FROM player_emails WHERE email_id=?"))
			{
				ps.setInt(1, emailId);
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						int senderId = rs.getInt("sender_id");
						int oldObjectId = rs.getInt("item_object_id");
						int itemId = rs.getInt("item_id");
						int count = rs.getInt("count");
						int enchant = rs.getInt("enchant_level");
						boolean isAug = rs.getBoolean("is_augmented");
						int augmentId = rs.getInt("augment_id");
						
						int newobjectId = IdFactory.getInstance().getNextId();
						
						Player player = World.getInstance().getPlayer(senderId);
						if (player != null)
						{
							
							ItemInstance item = player.addItem(itemId, count, true);
							if (enchant > 0)
								item.setEnchantLevel(enchant, player);
							
							
							
							if (isAug)
							{
								try (PreparedStatement updateAug = con.prepareStatement("UPDATE augmentations SET item_oid=? WHERE item_oid=?"))
								{
									updateAug.setInt(1, newobjectId);
									updateAug.setInt(2, oldObjectId);
									updateAug.executeUpdate();

									int attributes = 0;
									int skillId = 0;
									int skillLevel = 1;

									try (PreparedStatement sel = con.prepareStatement("SELECT attributes, skill_id, skill_level FROM augmentations WHERE item_oid=?"))
									{
										sel.setInt(1, newobjectId);
										try (ResultSet rs2 = sel.executeQuery())
										{
											if (rs2.next())
											{
												attributes = rs2.getInt("attributes");
												skillId = rs2.getInt("skill_id");
												skillLevel = rs2.getInt("skill_level");
											}
										}
									}

									int fullAugmentId = attributes | (skillId << 16);
									L2Skill skill = (skillId > 0) ? SkillTable.getInstance().getInfo(skillId, skillLevel) : null;
									item.setAugmentation(new Augmentation(fullAugmentId, skill), player);
								}
							}

							
							player.sendMessage("Um item expirado do correio foi devolvido para seu inventário.");
						}
						
						else
						{
							
							try (PreparedStatement check = con.prepareStatement("SELECT count FROM items WHERE object_id = ? AND owner_id = ? AND item_id = ? AND loc = 'INVENTORY'"))
							{
								check.setInt(1, oldObjectId);
								check.setInt(2, senderId);
								check.setInt(3, itemId);
								
								try (ResultSet rsc = check.executeQuery())
								{
									if (rsc.next())
									{
										
										int currentCount = rsc.getInt("count");
										int newCount = currentCount + count;
										
										try (PreparedStatement updateItem = con.prepareStatement("UPDATE items SET count = ? WHERE object_id = ?"))
										{
											updateItem.setInt(1, newCount);
											updateItem.setInt(2, oldObjectId);
											updateItem.executeUpdate();
										}
										
										if (isAug)
										{
											try (PreparedStatement checkAug = con.prepareStatement("SELECT item_oid FROM augmentations WHERE item_oid = ?"))
											{
												checkAug.setInt(1, oldObjectId);
												try (ResultSet rsAug = checkAug.executeQuery())
												{
													if (!rsAug.next())
													{
														int attributes = (augmentId & 0xFFFF);
														int skillId = ((augmentId >> 16) & 0xFFFF);
														int skillLevel = 1;
														
														try (PreparedStatement insertAug = con.prepareStatement("INSERT INTO augmentations (item_oid, attributes, skill_id, skill_level) VALUES (?, ?, ?, ?)"))
														{
															insertAug.setInt(1, oldObjectId);
															insertAug.setInt(2, attributes);
															insertAug.setInt(3, skillId);
															insertAug.setInt(4, skillLevel);
															insertAug.executeUpdate();
														}
													}
												}
											}
										}
									}
									else
									{
										
										try (PreparedStatement insertItem = con.prepareStatement("INSERT INTO items (owner_id, object_id, item_id, count, enchant_level, loc, loc_data, custom_type1, custom_type2, mana_left, time) VALUES (?, ?, ?, ?, ?, 'INVENTORY', 0, 0, 0, -1, 0)"))
										{
											insertItem.setInt(1, senderId);
											insertItem.setInt(2, newobjectId);
											insertItem.setInt(3, itemId);
											insertItem.setInt(4, count);
											insertItem.setInt(5, enchant);
											insertItem.executeUpdate();
										}
										
										if (isAug)
										{
											try (PreparedStatement updateAug = con.prepareStatement("UPDATE augmentations SET item_oid=? WHERE item_oid=?"))
											{
												updateAug.setInt(1, newobjectId);
												updateAug.setInt(2, oldObjectId);
												int updated = updateAug.executeUpdate();
												
												if (updated == 0)
												{
													int attributes = (augmentId & 0xFFFF);
													int skillId = ((augmentId >> 16) & 0xFFFF);
													int skillLevel = 1;
													
													try (PreparedStatement insertAug = con.prepareStatement("INSERT INTO augmentations (item_oid, attributes, skill_id, skill_level) VALUES (?, ?, ?, ?)"))
													{
														insertAug.setInt(1, newobjectId);
														insertAug.setInt(2, attributes);
														insertAug.setInt(3, skillId);
														insertAug.setInt(4, skillLevel);
														insertAug.executeUpdate();
													}
												}
											}
										}
									}
								}
							}
							
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void claimEmail(int emailId, int recipientId) {
	    try (Connection con = ConnectionPool.getConnection()) {
	        long now = System.currentTimeMillis();
	        try (PreparedStatement check = con.prepareStatement(
	                "SELECT * FROM player_emails WHERE email_id=? AND status='PENDING' AND expiration_time > ? AND target_id=? LIMIT 1")) {
	            check.setInt(1, emailId);
	            check.setLong(2, now);
	            check.setInt(3, recipientId);
	            try (ResultSet rs = check.executeQuery()) {
	                if (!rs.next()) {
	                    Player recipient = World.getInstance().getPlayer(recipientId);
	                    if (recipient != null && recipient.isOnline()) {
	                        recipient.sendMessage("Email inválido, expirado ou não destinado a você.");
	                    }
	                    return;
	                }
	            }
	        }

	        try (PreparedStatement ps = con.prepareStatement(
	                "UPDATE player_emails SET status=? WHERE email_id=? AND status=?")) {
	            
	            ps.setString(1, "CLAIMED");
	            ps.setInt(2, emailId);
	            ps.setString(3, "PENDING");
	            
	            System.out.println("[EmailDAO] Tentando atualizar status para CLAIMED, emailId: " + emailId);
	            
	            int updated = ps.executeUpdate();
	            System.out.println("[EmailDAO] Linhas atualizadas: " + updated);
	            
	            if (updated == 0) {
	                System.out.println("[EmailDAO] Nenhuma linha foi atualizada - email já processado ou inválido");
	                return;
	            }
	        } catch (Exception e) {
	            System.err.println("[EmailDAO] Erro ao atualizar status para CLAIMED: " + e.getMessage());
	            e.printStackTrace();
	            
	            try {
	                System.out.println("[EmailDAO] Tentando abordagem alternativa...");
	                try (PreparedStatement ps2 = con.prepareStatement(
	                        "UPDATE player_emails SET status='CLAIMED' WHERE email_id=? AND status='PENDING'")) {
	                    ps2.setInt(1, emailId);
	                    int updated2 = ps2.executeUpdate();
	                    System.out.println("[EmailDAO] Abordagem alternativa - Linhas atualizadas: " + updated2);
	                    if (updated2 == 0) return;
	                }
	            } catch (Exception e2) {
	                System.err.println("[EmailDAO] Abordagem alternativa também falhou: " + e2.getMessage());
	                return;
	            }
	        }

	        boolean isPaid = false;
	        int paymentItemId = -1;
	        int paymentAmount = 0;
	        int senderId = 0;

	        try (PreparedStatement ps = con.prepareStatement(
	                "SELECT sender_id, item_object_id, item_id, count, enchant_level, is_augmented, augment_id, is_paid, payment_item_id, payment_item_count FROM player_emails WHERE email_id=?")) {
	            ps.setInt(1, emailId);
	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    senderId = rs.getInt("sender_id");
	                    int oldObjectId = rs.getInt("item_object_id");
	                    int itemId = rs.getInt("item_id");
	                    int count = rs.getInt("count");
	                    int enchant = rs.getInt("enchant_level");
	                    boolean isAug = rs.getBoolean("is_augmented");
	                    int augmentId = rs.getInt("augment_id");
	                    isPaid = rs.getBoolean("is_paid");
	                    paymentItemId = rs.getInt("payment_item_id");
	                    paymentAmount = rs.getInt("payment_item_count");

	                    int newObjectId = IdFactory.getInstance().getNextId();
	                    Player recipient = World.getInstance().getPlayer(recipientId);

	                    if (recipient != null && recipient.isOnline()) {
	                        ItemInstance item = recipient.addItem(itemId, count, true);
	                        if (enchant > 0) item.setEnchantLevel(enchant, recipient);
	                        if (isAug) {
	                            try (PreparedStatement updateAug = con.prepareStatement("UPDATE augmentations SET item_oid=? WHERE item_oid=?")) {
	                                updateAug.setInt(1, newObjectId);
	                                updateAug.setInt(2, oldObjectId);
	                                int updated = updateAug.executeUpdate();
	                                if (updated > 0) {
	                                    int attributes = (augmentId & 0xFFFF);
	                                    int skillId = ((augmentId >> 16) & 0xFFFF);
	                                    int skillLevel = 1;
	                                    try (PreparedStatement sel = con.prepareStatement("SELECT attributes, skill_id, skill_level FROM augmentations WHERE item_oid=?")) {
	                                        sel.setInt(1, newObjectId);
	                                        try (ResultSet rs2 = sel.executeQuery()) {
	                                            if (rs2.next()) {
	                                                attributes = rs2.getInt("attributes");
	                                                skillId = rs2.getInt("skill_id");
	                                                skillLevel = rs2.getInt("skill_level");
	                                            }
	                                        }
	                                    }
	                                    int fullAugmentId = attributes | (skillId << 16);
	                                    L2Skill skill = (skillId > 0) ? SkillTable.getInstance().getInfo(skillId, skillLevel) : null;
	                                    item.setAugmentation(new Augmentation(fullAugmentId, skill), recipient);
	                                } else {
	                                    int attributes = (augmentId & 0xFFFF);
	                                    int skillId = ((augmentId >> 16) & 0xFFFF);
	                                    int skillLevel = 1;
	                                    try (PreparedStatement insertAug = con.prepareStatement(
	                                            "INSERT INTO augmentations (item_oid, attributes, skill_id, skill_level) VALUES (?, ?, ?, ?)")) {
	                                        insertAug.setInt(1, newObjectId);
	                                        insertAug.setInt(2, attributes);
	                                        insertAug.setInt(3, skillId);
	                                        insertAug.setInt(4, skillLevel);
	                                        insertAug.executeUpdate();
	                                    }
	                                    int fullAugmentId = attributes | (skillId << 16);
	                                    L2Skill skill = (skillId > 0) ? SkillTable.getInstance().getInfo(skillId, skillLevel) : null;
	                                    item.setAugmentation(new Augmentation(fullAugmentId, skill), recipient);
	                                }
	                            }
	                        }
	                        recipient.sendMessage("Você recebeu: " + item.getName() + " x" + count);
	                    } else {
	                        try (PreparedStatement insertItem = con.prepareStatement(
	                                "INSERT INTO items (owner_id, object_id, item_id, count, enchant_level, loc, loc_data, custom_type1, custom_type2, mana_left, time) VALUES (?, ?, ?, ?, ?, 'INVENTORY', 0, 0, 0, -1, 0)")) {
	                            insertItem.setInt(1, recipientId);
	                            insertItem.setInt(2, newObjectId);
	                            insertItem.setInt(3, itemId);
	                            insertItem.setInt(4, count);
	                            insertItem.setInt(5, enchant);
	                            insertItem.executeUpdate();
	                        }
	                        if (isAug) {
	                            try (PreparedStatement updateAug = con.prepareStatement("UPDATE augmentations SET item_oid=? WHERE item_oid=?")) {
	                                updateAug.setInt(1, newObjectId);
	                                updateAug.setInt(2, oldObjectId);
	                                int updated = updateAug.executeUpdate();
	                                if (updated == 0) {
	                                    int attributes = (augmentId & 0xFFFF);
	                                    int skillId = ((augmentId >> 16) & 0xFFFF);
	                                    int skillLevel = 1;
	                                    try (PreparedStatement insertAug = con.prepareStatement(
	                                            "INSERT INTO augmentations (item_oid, attributes, skill_id, skill_level) VALUES (?, ?, ?, ?)")) {
	                                        insertAug.setInt(1, newObjectId);
	                                        insertAug.setInt(2, attributes);
	                                        insertAug.setInt(3, skillId);
	                                        insertAug.setInt(4, skillLevel);
	                                        insertAug.executeUpdate();
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }

	        if (isPaid && paymentItemId > 0 && paymentAmount > 0) {
	            Player recipient = World.getInstance().getPlayer(recipientId);
	            if (recipient != null && recipient.isOnline()) {
	                ItemInstance paymentItem = recipient.getInventory().getItemByItemId(paymentItemId);
	                if (paymentItem == null || paymentItem.getCount() < paymentAmount) {
	                    recipient.sendMessage("Pagamento insuficiente. Email não reclamado.");
	                    try (PreparedStatement revert = con.prepareStatement("UPDATE player_emails SET status='PENDING' WHERE email_id=?")) {
	                        revert.setInt(1, emailId);
	                        revert.executeUpdate();
	                    }
	                    return;
	                }
	                recipient.destroyItemByItemId(paymentItemId, paymentAmount, true);

	                Player sender = World.getInstance().getPlayer(senderId);
	                if (sender != null && sender.isOnline()) {
	                    sender.addItem(paymentItemId, paymentAmount, true);
	                    sender.sendMessage("Você recebeu pagamento de um email reclamado.");
	                } else {
	                    try (PreparedStatement insertPayment = con.prepareStatement(
	                            "INSERT INTO items (owner_id, object_id, item_id, count, enchant_level, loc, loc_data) VALUES (?, ?, ?, ?, 0, 'INVENTORY', 0)")) {
	                        insertPayment.setInt(1, senderId);
	                        insertPayment.setInt(2, IdFactory.getInstance().getNextId());
	                        insertPayment.setInt(3, paymentItemId);
	                        insertPayment.setInt(4, paymentAmount);
	                        insertPayment.executeUpdate();
	                    }
	                }
	            } else {
	                try (PreparedStatement revert = con.prepareStatement("UPDATE player_emails SET status='PENDING' WHERE email_id=?")) {
	                    revert.setInt(1, emailId);
	                    revert.executeUpdate();
	                }
	                return;
	            }
	        }

	        EmailDeliveryTask.getInstance().cancel(emailId);
	    } catch (Exception e) {
	        e.printStackTrace();
	        Player recipient = World.getInstance().getPlayer(recipientId);
	        if (recipient != null && recipient.isOnline()) {
	            recipient.sendMessage("Erro ao reclamar email. Tente novamente.");
	        }
	    }
	}
}