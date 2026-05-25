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
package ext.mods.email.items;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import ext.mods.Crypta.PlayerEmailManager;
import ext.mods.email.task.EmailDeliveryTask;
import ext.mods.commons.pool.ConnectionPool;

import ext.mods.gameserver.idfactory.IdFactory;
import ext.mods.gameserver.model.World;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.item.instance.ItemInstance;
import ext.mods.util.Tokenizer;

public class EmailItemSender
{
	
	public static void processSendCommand(Player sender, Tokenizer tokenizer)
	{
		String targetName = sender.getSelectedEmailTarget();
		String paymentOption = tokenizer.getAsString(2);
		
		String paymentItemName = tokenizer.getAsString(3);
		int paymentAmount = tokenizer.getAsInteger(4, 0);
		String durationOption = sender.getSelectedEmailDuration();
		
		
		/*System.out.println("[EmailItemSender] Debug - Todos os tokens:");
		for (int i = 0; i < tokenizer.countTokens(); i++) {
			System.out.println("  - Token " + i + ": '" + tokenizer.getAsString(i) + "'");
		}*/
		
		if (targetName == null)
		{
			sender.sendMessage("Você deve selecionar um jogador antes de enviar.");
			PlayerEmailManager.getInstance().navi(sender, "email_main.htm");
			return;
		}
		
		Integer targetObjectId = getTargetObjectId(targetName);
		if (targetObjectId == null)
		{
			sender.sendMessage("Jogador '" + targetName + "' não encontrado.");
			PlayerEmailManager.getInstance().navi(sender, "email_main.htm");
			return;
		}
		
		boolean isPaid = paymentOption.equalsIgnoreCase("sim");
		int paymentItemId = parsePaymentItemId(paymentItemName);
		
		if (isPaid)
		{
			if (paymentItemId == -1)
			{
				sender.sendMessage("Você marcou como pago, mas não selecionou um item válido de pagamento.");
				PlayerEmailManager.getInstance().navi(sender, "email_main.htm");
				return;
			}
			if (paymentAmount <= 0)
			{
				sender.sendMessage("Você marcou como pago, mas não especificou uma quantidade válida.");
				PlayerEmailManager.getInstance().navi(sender, "email_main.htm");
				return;
			}
		}
		
		long expirationTime = calculateExpiration(durationOption);
		
		Map<Integer, Long> selectedItems = sender.getTempSelectedItems();
		if (selectedItems.isEmpty())
		{
			sender.sendMessage("Nenhum item selecionado para envio.");
			PlayerEmailManager.getInstance().navi(sender, "email_main.htm");
			return;
		}
		
		int emailId = IdFactory.getInstance().getNextId();
		
		int sentCount = 0;
		for (Map.Entry<Integer, Long> entry : selectedItems.entrySet())
		{
			int objectId = entry.getKey();
			long quantity = entry.getValue();
			
			ItemInstance item = sender.getInventory().getItemByObjectId(objectId);
			if (item == null || quantity < 1 || quantity > item.getCount())
			{
				continue;
			}
			
			ItemInstance removedItem = sender.getInventory().destroyItem(objectId, (int) quantity);
			if (removedItem != null)
			{
				removedItem.setCount((int) quantity);
				
				EmailStorage.saveEmail(emailId, sender.getObjectId(), targetObjectId, removedItem, isPaid, paymentItemId, paymentAmount, expirationTime);
				sentCount++;
			}
		}
		
		sender.clearTempSelectedItems();
		sender.setSelectedEmailTarget(null);
		
		if (sentCount > 0) {
		    sender.sendMessage("Email enviado com sucesso para " + targetName + ".");
		    EmailDeliveryTask.getInstance().scheduleExpiration(sender, emailId, expirationTime);
		    
		    Player recipient = World.getInstance().getPlayer(targetName);
		    if (recipient != null && recipient.isOnline()) {
		        recipient.sendMessage("Você recebeu um novo email de " + sender.getName() + ". Use .email inbox para ver.");
		    }
		}
		else
		{
			sender.sendMessage("Nenhum Email pôde ser enviado.");
		}
	}
	
	private static Integer getTargetObjectId(String targetName)
	{
		Player target = World.getInstance().getPlayer(targetName);
		if (target != null)
		{
			return target.getObjectId();
		}
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name = ?"))
		{
			
			ps.setString(1, targetName);
			try (ResultSet rs = ps.executeQuery())
			{
				if (rs.next())
				{
					return rs.getInt("obj_Id");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private static int parsePaymentItemId(String itemName)
	{
		if (itemName == null)
		{
			return -1;
		}
		
		switch (itemName.trim().toLowerCase())
		{
			case "adena":
				return 57;
			case "goldbar":
				return 3470;
			case "ticketdonate":
				return 9999;
			case "nenhum":
				return -1;
			default:
				return -1;
		}
	}
	
	private static long calculateExpiration(String durationStr)
	{
		long currentTime = System.currentTimeMillis();
		long durationMs;
		
		if (durationStr == null || durationStr.trim().isEmpty())
		{
			durationMs = 5 * 60 * 1000;
		}
		else
		{
			switch (durationStr.toLowerCase().trim())
			{
				case "5minutos":
					durationMs = 5 * 60 * 1000;
					break;
				case "30minutos":
					durationMs = 30 * 60 * 1000;
					break;
				case "2horas":
					durationMs = 2 * 60 * 60 * 1000;
					break;
				default:
					durationMs = 5 * 60 * 1000;
					break;
			}
		}
		
		long expirationTime = currentTime + durationMs;
		
		return expirationTime;
	}
}
