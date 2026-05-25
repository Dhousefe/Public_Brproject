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
package ext.mods.gameserver.handler.voicedcommandhandlers;

import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import ext.mods.commons.pool.ThreadPool;
import ext.mods.dressme.DressMeData;
import ext.mods.dressme.holder.DressMeHolder;
import ext.mods.dressme.holder.DressMeVisualType;
import ext.mods.gameserver.data.xml.ItemData;
import ext.mods.gameserver.handler.IVoicedCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;

public class skins implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"skin"
	};
	private static final int ITEMS_PER_PAGE = 4;
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String target)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		st.nextToken();
		
		String type = "ARMOR";
		String subtype = null;
		int page = 1;
		
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			
			if ("type".equalsIgnoreCase(token) && st.hasMoreTokens())
			{
				type = st.nextToken().toUpperCase();
			}
			else if ("subtype".equalsIgnoreCase(token) && st.hasMoreTokens())
			{
				subtype = st.nextToken().toUpperCase();
			}
			else if ("page".equalsIgnoreCase(token) && st.hasMoreTokens())
			{
				try
				{
					page = Integer.parseInt(st.nextToken());
				}
				catch (NumberFormatException e)
				{
					page = 1;
				}
			}
			else if ("apply".equalsIgnoreCase(token) && st.hasMoreTokens())
			{
				int skillId = Integer.parseInt(st.nextToken());
				final long cooldown = 900;
				
				long currentTime = System.currentTimeMillis();
				if (currentTime - player.getLastDressMeSummonTime() < cooldown)
				{
					player.sendMessage("You need to wait before summoning another DressMe.");
					return true;
				}
				
				DressMeHolder skin = DressMeData.getInstance().getBySkillId(skillId);
				if (skin != null)
				{
					if (player.getPremiumService() <= 0)
					{
						player.sendMessage("Este comando é exclusivo para jogadores VIP.");
						return true;
					}
					
					if (skin.isVip() || player.getPremiumService() > 0)
					{
						player.applyDressMe(skin, true);
						player.sendMessage("Skin aplicada: " + skin.getName());
						player.setLastDressMeSummonTime(currentTime);
					}
				}
			}
			else if ("preview".equalsIgnoreCase(token) && st.hasMoreTokens())
			{
				int skillId = Integer.parseInt(st.nextToken());
				final long cooldown = 1000 * 60 * 1;
				
				long currentTime = System.currentTimeMillis();
				if (currentTime - player.getLastDressMeSummonTime() < cooldown)
				{
					player.sendMessage("You need to wait before summoning another DressMe.");
					return true;
				}
				
				DressMeHolder skin = DressMeData.getInstance().getBySkillId(skillId);
				if (skin != null)
				{
					player.applyDressMe(skin, false);
					player.sendMessage("Visual temporário: " + skin.getName());
					ThreadPool.schedule(() ->
					{
						player.removeDressMeArmor();
						player.removeDressMeWeapon();
					}, 1L * 60L * 1000L);
					player.setLastDressMeSummonTime(currentTime);
				}
			}
			else if ("clean".equalsIgnoreCase(token))
			{
				player.removeDressMeArmor();
				player.removeDressMeWeapon();
			}
		}
		
		showSkins(player, type, page, subtype);
		return true;
	}
	
	private void showSkins(Player player, String type, int page, String subtype)
	{
	    List<DressMeHolder> all = DressMeData.getInstance().getEntries().stream()
	        .filter(e -> e.getType().toString().equalsIgnoreCase(type))
	        .filter(e ->
	        {
	            if (type.equalsIgnoreCase("WEAPON") && subtype != null && !subtype.isEmpty())
	            {
	                return e.getWeaponTypeVisual() != null && e.getWeaponTypeVisual().equalsIgnoreCase(subtype);
	            }
	            else if (type.equalsIgnoreCase("ARMOR") && subtype != null && !subtype.isEmpty())
	            {
	                if (subtype.equalsIgnoreCase("HELMET"))
	                    return e.getHelmetId() > 0; 
	            }
	            return true;
	        }).collect(Collectors.toList());

	    int totalPages = (int) Math.ceil((double) all.size() / ITEMS_PER_PAGE);
	    page = Math.max(1, Math.min(page, totalPages));

	    int start = (page - 1) * ITEMS_PER_PAGE;
	    int end = Math.min(start + ITEMS_PER_PAGE, all.size());

	    StringBuilder sb = new StringBuilder();
	    sb.append("<html><title>Skin</title><body><center>");
	    sb.append("<br><font color=LEVEL>Visualizações - ").append(type).append("</font>");

	    sb.append("<table><tr>");
	    sb.append("<td><button value=\"ARMOR\" action=\"bypass -h voiced_skin type ARMOR\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	    sb.append("<td><button value=\"WEAPON\" action=\"bypass -h voiced_skin type WEAPON\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	    sb.append("</tr></table><br>");

	    if (type.equalsIgnoreCase("WEAPON"))
	    {
	        String[] subtypes =
	        {
	            "SWORD", "BOW", "DAGGER",
	            "POLE", "BLUNT", "DUAL",
	            "DUALFIST", "ETC"
	        };

	        sb.append("<table width=300>");
	        for (int i = 0; i < subtypes.length; i++)
	        {
	            if (i % 3 == 0)
	                sb.append("<tr>");

	            String sub = subtypes[i];
	            sb.append("<td><button value=\"" + sub + "\" action=\"bypass -h voiced_skin type WEAPON subtype " + sub + "\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");

	            if (i % 3 == 2 || i == subtypes.length - 1)
	                sb.append("</tr>");
	        }
	        sb.append("</table><br>");
	    }


	    if (type.equalsIgnoreCase("ARMOR"))
	    {
	        String[] subtypes =
	        {
	            "HELMET"
	            
	        };

	        sb.append("<table width=300>");
	        for (int i = 0; i < subtypes.length; i++)
	        {
	            if (i % 3 == 0)
	                sb.append("<tr>");

	            String sub = subtypes[i];
	            sb.append("<td><button value=\"" + sub + "\" action=\"bypass -h voiced_skin type ARMOR subtype " + sub + "\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");

	            if (i % 3 == 2 || i == subtypes.length - 1)
	                sb.append("</tr>");
	        }
	        sb.append("</table><br>");
	    }

	    for (int i = start; i < end; i++)
	    {
	        DressMeHolder skin = all.get(i);
	        
	        int iconId;
	        if (skin.getType() == DressMeVisualType.WEAPON)
	        {
	            iconId = skin.getRightHandId();
	        }
	        else
	        {
	            if ("HELMET".equalsIgnoreCase(subtype))
	                iconId = skin.getHelmetId();
	            else
	                iconId = skin.getChestId();
	        }

	        sb.append("<table width=300><tr>");
	        sb.append("<td width=32><img src=\"icon." + getIconName(iconId) + "\" width=32 height=32></td>");
	        sb.append("<td><font color=\"LEVEL\">" + skin.getName() + "</font>");
	        sb.append("<table><tr>");
	        sb.append("<td><button value=\"Aplicar\" action=\"bypass -h voiced_skin apply " + skin.getSkillId() + "\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	        sb.append("<td><button value=\"Testar\" action=\"bypass -h voiced_skin preview " + skin.getSkillId() + "\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	        sb.append("<td><button value=\"Limpa\" action=\"bypass -h voiced_skin clean\" width=60 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	        sb.append("</tr></table>");
	        sb.append("</td></tr></table>");
	    }

	    sb.append("<table><tr>");
	    if (page > 1)
	        sb.append("<td><button value=\"<<\" action=\"bypass -h voiced_skin type " + type + (subtype != null ? " subtype " + subtype : "") + " page " + (page - 1) + "\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	    else
	        sb.append("<td width=40></td>");

	    sb.append("<td width=200 align=center>Page ").append(page).append(" / ").append(totalPages).append("</td>");

	    if (page < totalPages)
	        sb.append("<td><button value=\">>\" action=\"bypass -h voiced_skin type " + type + (subtype != null ? " subtype " + subtype : "") + " page " + (page + 1) + "\" width=40 height=20 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
	    else
	        sb.append("<td width=40></td>");

	    sb.append("</tr></table>");

	    sb.append("</center></body></html>");

	    NpcHtmlMessage html = new NpcHtmlMessage(0);
	    html.setHtml(sb.toString());
	    player.sendPacket(html);
	}

	
	private String getIconName(int itemId)
	{
		return ItemData.getInstance().getTemplate(itemId).getIcon();
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
