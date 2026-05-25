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
package ext.mods.summonmobitem;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ext.mods.commons.data.xml.IXmlReader;
import ext.mods.summonmobitem.SummonMobItemHolder;

/**
 * Classe responsável por carregar e gerenciar os dados de configuração
 * dos itens de summon de monstros do arquivo SummonMobItem.xml
 * 
 * @author Dhousefe
 */
public class SummonMobItemData implements IXmlReader
{
	private static final Map<Integer, SummonMobItemHolder> _summonItems = new ConcurrentHashMap<>();
	private static final Map<String, Long> _playerItemLastUse = new ConcurrentHashMap<>(); 
	private static final Map<String, Integer> _playerItemDailyUses = new ConcurrentHashMap<>(); 
	
	private static final SummonMobItemData _instance = new SummonMobItemData();
	
	public static SummonMobItemData getInstance()
	{
		return _instance;
	}
	
	protected SummonMobItemData()
	{
		load();
	}
	
	@Override
	public void load()
	{
		parseDataFile("custom/mods/SummonMobItem.xml");
		System.out.println("Loaded " + _summonItems.size() + " summon item configurations.");
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		
		_summonItems.clear();
		
		final NodeList list = doc.getElementsByTagName("summon_item");
		for (int i = 0; i < list.getLength(); i++)
		{
			final Node node = list.item(i);
			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			final Element element = (Element) node;
			
			try
			{
				final int itemId = Integer.parseInt(element.getElementsByTagName("itemId").item(0).getTextContent());
				final int monsterId = Integer.parseInt(element.getElementsByTagName("monsterId").item(0).getTextContent());
				final String monsterName = element.getElementsByTagName("monsterName").item(0).getTextContent();
				final boolean enabled = Boolean.parseBoolean(element.getElementsByTagName("enabled").item(0).getTextContent());
				final int maxUsesPerDay = Integer.parseInt(element.getElementsByTagName("maxUsesPerDay").item(0).getTextContent());
				final int cooldownMinutes = Integer.parseInt(element.getElementsByTagName("cooldownMinutes").item(0).getTextContent());
				final String requiredClass = element.getElementsByTagName("requiredClass").item(0).getTextContent();
				final String description = element.getElementsByTagName("description").item(0).getTextContent();
				
				final SummonMobItemHolder holder = new SummonMobItemHolder(
					itemId, monsterId, monsterName, enabled, maxUsesPerDay, 
					cooldownMinutes, requiredClass, description
				);
				
				_summonItems.put(itemId, holder);
				
				System.out.println("Loaded summon item: " + itemId + " -> " + monsterId + " (" + monsterName + ")");
			}
			catch (Exception e)
			{
				System.err.println("Error loading summon item from XML: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		System.out.println("Loaded " + _summonItems.size() + " summon items from " + path.getFileName());
	}
	
	/**
	 * Obtém a configuração de um item de summon
	 * 
	 * @param itemId ID do item
	 * @return Holder com as configurações ou null se não encontrado
	 */
	public SummonMobItemHolder getSummonItem(int itemId)
	{
		return _summonItems.get(itemId);
	}
	
	/**
	 * Verifica se um item pode ser usado para summon
	 * 
	 * @param itemId ID do item
	 * @return true se o item está configurado e habilitado
	 */
	public boolean isSummonItem(int itemId)
	{
		final SummonMobItemHolder holder = _summonItems.get(itemId);
		return holder != null && holder.isEnabled();
	}
	
	/**
	 * Verifica se um player pode usar um item (cooldown e limites diários)
	 * 
	 * @param playerId ID do player
	 * @param itemId ID do item
	 * @return true se pode usar
	 */
	public boolean canPlayerUseItem(int playerId, int itemId)
	{
		final SummonMobItemHolder holder = _summonItems.get(itemId);
		if (holder == null || !holder.isEnabled())
			return false;
		
		final String playerItemKey = playerId + ":" + itemId;
		
		if (holder.getCooldownMinutes() > 0)
		{
			final Long lastUse = _playerItemLastUse.get(playerItemKey);
			if (lastUse != null)
			{
				final long cooldownMs = holder.getCooldownMinutes() * 60 * 1000L;
				if (System.currentTimeMillis() - lastUse < cooldownMs)
					return false;
			}
		}
		
		if (holder.getMaxUsesPerDay() > 0)
		{
			final Integer dailyUses = _playerItemDailyUses.get(playerItemKey);
			if (dailyUses != null && dailyUses >= holder.getMaxUsesPerDay())
				return false;
		}
		
		return true;
	}
	
	/**
	 * Registra o uso de um item por um player
	 * 
	 * @param playerId ID do player
	 * @param itemId ID do item
	 */
	public void registerItemUse(int playerId, int itemId)
	{
		final SummonMobItemHolder holder = _summonItems.get(itemId);
		if (holder == null)
			return;
		
		final String playerItemKey = playerId + ":" + itemId;
		
		_playerItemLastUse.put(playerItemKey, System.currentTimeMillis());
		
		if (holder.getMaxUsesPerDay() > 0)
		{
			_playerItemDailyUses.merge(playerItemKey, 1, Integer::sum);
		}
	}
	
	/**
	 * Limpa os contadores diários (deve ser chamado diariamente)
	 */
	public void resetDailyCounters()
	{
		_playerItemDailyUses.clear();
		System.out.println("Reset daily summon item counters");
	}
	
	/**
	 * Obtém a quantidade de configurações carregadas
	 * 
	 * @return Número de itens configurados
	 */
	public int getLoadedCount()
	{
		return _summonItems.size();
	}
	
	/**
	 * Obtém informações de cooldown restante para um player
	 * 
	 * @param playerId ID do player
	 * @param itemId ID do item
	 * @return Tempo restante em minutos, ou 0 se não há cooldown
	 */
	public int getRemainingCooldown(int playerId, int itemId)
	{
		final SummonMobItemHolder holder = _summonItems.get(itemId);
		if (holder == null || holder.getCooldownMinutes() <= 0)
			return 0;
		
		final String playerItemKey = playerId + ":" + itemId;
		final Long lastUse = _playerItemLastUse.get(playerItemKey);
		if (lastUse == null)
			return 0;
		
		final long cooldownMs = holder.getCooldownMinutes() * 60 * 1000L;
		final long remainingMs = cooldownMs - (System.currentTimeMillis() - lastUse);
		
		return remainingMs > 0 ? (int) (remainingMs / (60 * 1000)) : 0;
	}
	
	/**
	 * Obtém quantos usos restam para um player hoje
	 * 
	 * @param playerId ID do player
	 * @param itemId ID do item
	 * @return Usos restantes, ou -1 se ilimitado
	 */
	public int getRemainingDailyUses(int playerId, int itemId)
	{
		final SummonMobItemHolder holder = _summonItems.get(itemId);
		if (holder == null || holder.getMaxUsesPerDay() <= 0)
			return -1;
		
		final String playerItemKey = playerId + ":" + itemId;
		final Integer dailyUses = _playerItemDailyUses.get(playerItemKey);
		final int used = dailyUses != null ? dailyUses : 0;
		
		return Math.max(0, holder.getMaxUsesPerDay() - used);
	}
}
