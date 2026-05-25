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
package ext.mods.gameserver.handler.admincommandhandlers;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; 
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import ext.mods.Config;
import ext.mods.FarmEventRandom.RandomData;
import ext.mods.FarmEventRandom.holder.DropHolder;
import ext.mods.FarmEventRandom.holder.RamdomConfig;
import ext.mods.FarmEventRandom.holder.RandomZoneData;
import ext.mods.gameserver.data.manager.ZoneManager;
import ext.mods.gameserver.handler.IAdminCommandHandler;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.model.zone.type.RandomZone;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import ext.mods.Crypta.RandomManager;
import ext.mods.commons.logging.CLogger;

public class AdminFarmEvent implements IAdminCommandHandler
{
	public static final CLogger LOGGER = new CLogger(AdminFarmEvent.class.getName());
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_farm_event"
	};
	
	private static final String EVENT_XML_FILE = "data/custom/mods/random_event.xml";
	private static final String ZONE_XML_FILE = "data/xml/zones/RandomZone.xml"; 
	
	
	private static final Map<Integer, String> _zoneNamesCache = new HashMap<>();
	
	
	private static final int ZONES_PER_PAGE = 5; 
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (!player.isGM())
			return;
			
		StringTokenizer st = new StringTokenizer(command);
		st.nextToken(); 
		String action = st.hasMoreTokens() ? st.nextToken() : "main";
		String bypassParams = "";
		if (st.hasMoreTokens())
		{
			bypassParams = st.nextToken("").trim(); 
		}
		
		try
		{
			
			int currentPage = 1;
			
			switch (action)
			{
				case "main":
					
					if (!bypassParams.isEmpty()) {
						try {
							currentPage = Integer.parseInt(bypassParams);
						} catch (NumberFormatException e) {  }
					}
					showMainMenu(player, currentPage); 
					break;
				
				
				case "toggle_global":
					handleToggleGlobal(player, bypassParams);
					showMainMenu(player, 1); 
					break;
				
				
				case "update_time":
					handleUpdateTime(player, bypassParams);
					showMainMenu(player, 1); 
					break;
					
				
				case "toggle_active":
					st = new StringTokenizer(bypassParams);
					int zoneId = Integer.parseInt(st.nextToken());
					String status = st.nextToken();
					
					if (st.hasMoreTokens()) { 
						try {
							currentPage = Integer.parseInt(st.nextToken());
						} catch (NumberFormatException e) {  }
					}
					handleToggleActive(player, zoneId, status.equals("activate"));
					showMainMenu(player, currentPage);
					break;
				
				default:
					player.sendMessage("Função '" + action + "' desconhecida ou desabilitada.");
					showMainMenu(player, 1);
					break;
			}
		}
		catch (Exception e)
		{
			player.sendMessage("Erro ao processar comando: " + e.getMessage());
			LOGGER.error("[AdminFarmEvent] Erro no useAdminCommand (Action: " + action + "): ", e);
			showMainMenu(player, 1); 
		}
	}
	
	
	private void showMainMenu(Player player, int page) 
	{
		RandomData.getInstance().reload();
		loadZoneNamesCache(player);
		
		RamdomConfig config = RandomData.getInstance().getFirstConfig();
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		StringBuilder sb = new StringBuilder();
		
		sb.append("<html><title>Farm Event Dashboard</title><body><center>");
		sb.append("<table width=270><tr>");
		sb.append("<td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td>");
		sb.append("<td width=180 align=center>Farm Event Manager</td>");
		sb.append("<td width=45><button value=\"Back\" action=\"bypass -h admin_admin 2\" width=40 height=21 back=\"L2UI_ch3.Btn1_normalOn\" fore=\"L2UI_ch3.Btn1_normal\"></td>");
		sb.append("</tr></table>");
		
		if (config == null)
		{
			sb.append("<font color=FF0000>Erro: random_event.xml não foi carregado ou está vazio.</font>");
			sb.append("<br><font color=FFFF00>O arquivo pode estar corrompido. Conserte-o manualmente e use //reload farm_event</font>");
			sb.append("</center></body></html>");
			html.setHtml(sb.toString());
			player.sendPacket(html);
			return;
		}
		
		
		sb.append("<table width=280 bgcolor=333333><tr>");
		sb.append("<td width=280 align=center>Configuracoes Globais</td>");
		sb.append("</tr></table>");
		sb.append("<table width=280>");
		String status = config.isEnabled() ? "<font color=00FF00>Habilitado</font>" : "<font color=FF0000>Desabilitado</font>";
		String actionLabel = config.isEnabled() ? "OFF" : "ON";
		String actionBypass = config.isEnabled() ? "disable" : "enable";
		sb.append("<tr><td width=100>Evento:</td>");
		sb.append("<td width=110>" + status + "</td>");
		sb.append("<td width=70 align=right><button value=\"" + actionLabel + "\" action=\"bypass -h admin_farm_event toggle_global " + actionBypass + "\" width=60 height=21 back=L2UI_ch3.Btn1_normalOn fore=L2UI_ch3.Btn1_normal></td>");
		sb.append("</tr>");
		sb.append("<tr><td>Nome:</td><td colspan=2 width=180>" + config.getName() + "</td></tr>");
		sb.append("<tr><td>Sorteio:</td><td colspan=2>Selecionando <font color=LEVEL>" + config.getZoneValue() + "</font> zona(s).</td></tr>");
		
		String timesList = config.getActiveTimes().isEmpty() ? "" : config.getActiveTimes().stream().map(t -> t.format(DateTimeFormatter.ofPattern("HH:mm"))).collect(Collectors.joining(";"));
		sb.append("<tr>");
		sb.append("<td width=130>Horario(s): " + (timesList.isEmpty() ? "Nenhum" : timesList) + "</td>"); 
		sb.append("<td width=80><edit var=\"times\" width=70 length=50></td>"); 
		sb.append("<td width=70 align=right><button value=\"Salvar\" action=\"bypass -h admin_farm_event update_time $times\" width=60 height=21 back=L2UI_ch3.Btn1_normalOn fore=L2UI_ch3.Btn1_normal></td>"); 
		sb.append("</tr>");
		
		sb.append("<tr><td>Aviso Prévio:</td><td colspan=2>" + config.getPrepareMinutes() + " min(s)</td></tr>");
		sb.append("<tr><td>Duracao:</td><td colspan=2>" + config.getInterval() + " hora(s)</td></tr>");
		sb.append("<tr><td>Anuncio Auto:</td><td colspan=2>A cada " + config.getAnnounceEndMinutes() + " min(s)</td></tr>");
		sb.append("</table>");
		sb.append("<font color=AAAAAA>Para editar (Nome, Rates, Drops, etc), edite o XML manualmente.</font>");
		
		
		Collection<RandomZoneData> allZoneDataCollection = RandomData.getInstance().getAllZoneData(config.getName());
		
		
		List<RandomZoneData> allZoneDataList = new ArrayList<>(allZoneDataCollection); 
		int totalZones = allZoneDataList.size();
		int totalPages = (int) Math.ceil((double) totalZones / ZONES_PER_PAGE);
		page = Math.max(1, Math.min(page, totalPages)); 

		int startIndex = (page - 1) * ZONES_PER_PAGE;
		int endIndex = Math.min(startIndex + ZONES_PER_PAGE, totalZones);
		

		if (allZoneDataList.isEmpty()) { 
			sb.append("<br>Nenhuma zona (<spawns>) configurada em random_event.xml."); 
		} else {
			sb.append("<br><table width=280><tr><td align=center>Zonas Configuradas (Pagina " + page + " de " + totalPages + ")</td></tr></table>");
			
			
			for (int i = startIndex; i < endIndex; i++)
			{
				RandomZoneData zData = allZoneDataList.get(i);
				

				String zoneName = getZoneName(zData.getZoneId()); 
				String zoneStatusColor = zData.isActive() ? "00FF00" : "AAAAAA"; 
				
				sb.append("<table width=280 border=0 bgcolor=333333><tr>");
				sb.append("<td width=200><font color=" + zoneStatusColor + ">ID " + zData.getZoneId() + " (" + zoneName + ")</font></td>");
				
				String actionLabelZone = zData.isActive() ? "Desativar" : "Ativar";
				String actionBypassZone = zData.isActive() ? "deactivate" : "activate";
				
				String toggleBypass = "bypass -h admin_farm_event toggle_active " + zData.getZoneId() + " " + actionBypassZone + " " + page;
				sb.append("<td width=80 align=right><button value=\""+actionLabelZone+"\" action=\"" + toggleBypass + "\" width=70 height=21 back=L2UI_ch3.Btn1_normalOn fore=L2UI_ch3.Btn1_normal></td>");
				sb.append("</tr></table>");
				
				
				sb.append("<table width=280 border=0 bgcolor=222222>");
				sb.append("<tr><td width=100>Titulo:</td><td width=180>" + zData.getDefaultTitle() + "</td></tr>");
				String modo = zData.useOriginals() ? (zData.getCustomSpawns().isEmpty() ? "Originais (Modo 1)" : "Hibrido (Modo 3)") : "Custom (Modo 2)";
				sb.append("<tr><td>Modo:</td><td>" + modo + "</td></tr>");
				String dropsOrig = zData.dropsOriginals() ? "<font color=00FF00>Adicionar</font>" : "<font color=FFFF00>Substituir</font>";
				sb.append("<tr><td>Drops Originais:</td><td>" + (zData.useOriginals() ? dropsOrig : "N/A") + "</td></tr>");
				sb.append("<tr><td>Rates (X/S/A):</td><td>" + zData.getRateXp() + "x / " + zData.getRateSp() + "x / " + zData.getRateAdena() + "x</td></tr>");
				sb.append("<tr><td>Respawn (Orig.):</td><td>" + (zData.getDefaultRespawnDelay() == -1 ? "Padrao" : zData.getDefaultRespawnDelay() + "s") + "</td></tr>");
				sb.append("<tr><td valign=top>Drops (Evento):</td><td>" + formatDrops(zData.getDefaultDrops()) + "</td></tr>");
				sb.append("<tr><td>Spawns Custom:</td><td>" + zData.getCustomSpawns().size() + " definidos</td></tr>");
				
				
				String vipStatus = zData.isVip() ? "<font color=00FF00>Sim</font>" : "<font color=AAAAAA>Não</font>";
				sb.append("<tr><td>Requer VIP:</td><td>" + vipStatus + "</td></tr>");
				String partyStatus = zData.isPartyZone() ? "<font color=00FF00>Sim</font>" : "<font color=AAAAAA>Não</font>";
				sb.append("<tr><td>Requer Party:</td><td>" + partyStatus + "</td></tr>");
				String minParty = zData.isPartyZone() ? String.valueOf(zData.getMinPartySize()) : "N/A";
				sb.append("<tr><td>Min. Party:</td><td>" + minParty + "</td></tr>");
				
				
				sb.append("</table>");
				sb.append("<br>"); 
			}
			
			
			if (totalPages > 1) {
				sb.append("<br><table width=280><tr>");
				
				if (page > 1) {
					sb.append("<td width=100 align=left><button value=\"<< Anterior\" action=\"bypass -h admin_farm_event main " + (page - 1) + "\" width=80 height=21 back=L2UI_ch3.Btn1_normalOn fore=L2UI_ch3.Btn1_normal></td>");
				} else {
					sb.append("<td width=100></td>"); 
				}
				
				sb.append("<td width=80 align=center>Pag. " + page + "/" + totalPages + "</td>");
				
				if (page < totalPages) {
					sb.append("<td width=100 align=right><button value=\"Próxima >>\" action=\"bypass -h admin_farm_event main " + (page + 1) + "\" width=80 height=21 back=L2UI_ch3.Btn1_normalOn fore=L2UI_ch3.Btn1_normal></td>");
				} else {
					sb.append("<td width=100></td>"); 
				}
				sb.append("</tr></table>");
			}
			
		}
		
		sb.append("</center></body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}

	
	private void handleUpdateTime(Player player, String times) {  
		String cleanedTimes = times.replace("\n", "").replace("\r", "").trim(); if (cleanedTimes.isEmpty()){ player.sendMessage("Campo de horário estava vazio. Nenhum dado alterado."); return; } if (updateGlobalConfig(player, null, null, null, null, null, null, null, cleanedTimes)) { Object RandomManagerIntance = RandomManager.getInstance(); RandomManager.getInstance().reload(); player.sendMessage("Horários do evento atualizados para '" + cleanedTimes + "' e mod recarregado."); }
	}
	private void handleToggleGlobal(Player player, String status) {  
		boolean newStatus = status.equals("enable"); if (updateGlobalConfig(player, String.valueOf(newStatus), null, null, null, null, null, null, null)) 
		{ 
			Object RandomManagerIntance = RandomManager.getInstance(); RandomManager.getInstance().reload(); 
			player.sendMessage("Evento " + (newStatus ? "HABILITADO" : "DESABILITADO") + ". Mod recarregado."); 
		}
	}
	private boolean updateGlobalConfig(Player player, String enable, String name, String prepare, String interval, String select, String announce, String days, String times) {  
		File xmlFile = new File(EVENT_XML_FILE); if (!makeBackup(xmlFile, player)) { return false; } try { Document doc = loadXML(xmlFile); NodeList configNodes = doc.getElementsByTagName("config"); if (configNodes.getLength() == 0) { player.sendMessage("ERRO: Nenhuma tag <config> encontrada no " + EVENT_XML_FILE); restoreBackup(xmlFile, player); return false; } Element configNode = (Element) configNodes.item(0); if (enable != null && !enable.trim().isEmpty()) configNode.setAttribute("enable", enable.trim()); if (name != null && !name.trim().isEmpty()) configNode.setAttribute("name", name.trim()); if (prepare != null && !prepare.trim().isEmpty()) configNode.setAttribute("prepareMinutes", prepare.trim()); if (interval != null && !interval.trim().isEmpty()) configNode.setAttribute("intervalHours", interval.trim()); if (select != null && !select.trim().isEmpty()) configNode.setAttribute("select", select.trim()); if (announce != null && !announce.trim().isEmpty()) configNode.setAttribute("announceEnd", announce.trim()); if (days != null && !days.trim().isEmpty()) configNode.setAttribute("days", days.trim()); if (times != null) { configNode.setAttribute("times", times.trim()); } return saveXML(xmlFile, doc, true); } catch (Exception e) { player.sendMessage("ERRO CRÍTICO ao salvar o XML. Restaurando backup. Verifique o console."); LOGGER.error("[AdminFarmEvent] Falha ao parsear ou salvar o " + EVENT_XML_FILE, e); restoreBackup(xmlFile, player); return false; }
	}

	
	private boolean handleToggleActive(Player player, int zoneId, boolean newStatus) {  
		File xmlFile = new File(EVENT_XML_FILE); if (!makeBackup(xmlFile, player)) { return false; } try { Document doc = loadXML(xmlFile); Element spawnNode = findNode(doc, "spawns", "zoneId", String.valueOf(zoneId)); if (spawnNode == null) { player.sendMessage("ERRO: Não foi possível encontrar <spawns zoneId=\"" + zoneId + "\"> no XML."); restoreBackup(xmlFile, player); return false; } spawnNode.setAttribute("active", String.valueOf(newStatus)); if (saveXML(xmlFile, doc, true)) { Object RandomManagerIntance = RandomManager.getInstance(); RandomManager.getInstance().reload(); return true; } restoreBackup(xmlFile, player); return false; } catch (Exception e) { player.sendMessage("ERRO CRÍTICO ao salvar o XML. Restaurando backup."); LOGGER.error("[AdminFarmEvent] Falha ao parsear ou salvar o " + EVENT_XML_FILE, e); restoreBackup(xmlFile, player); return false; }
	}
	
	
	
	private synchronized void loadZoneNamesCache(Player player) {  
		if (!_zoneNamesCache.isEmpty()) { return; } File zoneFile = new File(ZONE_XML_FILE); if (!zoneFile.exists()){ player.sendMessage("Aviso: " + ZONE_XML_FILE + " nao encontrado. Nomes das zonas nao serao exibidos."); LOGGER.warn("[AdminFarmEvent] " + ZONE_XML_FILE + " nao encontrado. Cache de nomes vazio."); return; } try { Document doc = loadXML(zoneFile); NodeList zoneNodes = doc.getElementsByTagName("zone"); for (int i = 0; i < zoneNodes.getLength(); i++){ Node zoneNode = zoneNodes.item(i); if (zoneNode.getNodeType() == Node.ELEMENT_NODE){ NodeList statNodes = zoneNode.getChildNodes(); int zoneId = -1; String zoneName = null; for (int j = 0; j < statNodes.getLength(); j++){ Node statNode = statNodes.item(j); if (statNode.getNodeType() == Node.ELEMENT_NODE && statNode.getNodeName().equals("stat")){ Element statElement = (Element) statNode; String nameAttr = statElement.getAttribute("name"); if (nameAttr.equals("id")) { zoneId = Integer.parseInt(statElement.getAttribute("val")); } else if (nameAttr.equals("name")) { zoneName = statElement.getAttribute("val"); } } } if (zoneId != -1 && zoneName != null) { _zoneNamesCache.put(zoneId, zoneName); } } } LOGGER.info("[AdminFarmEvent] Carregado " + _zoneNamesCache.size() + " nomes de zonas para o cache."); } catch (Exception e) { player.sendMessage("Erro ao carregar " + ZONE_XML_FILE + ". Nomes podem nao aparecer."); LOGGER.error("[AdminFarmEvent] Falha ao carregar " + ZONE_XML_FILE, e); }
	}
	private String getZoneName(int zoneId)	{   return _zoneNamesCache.getOrDefault(zoneId, "Zona Invalida"); }
	private boolean makeBackup(File xmlFile, Player player)	{   if (!xmlFile.exists()) { player.sendMessage("ERRO: Arquivo " + xmlFile.getName() + " não encontrado para backup."); return false; } String backupPath = xmlFile.getAbsolutePath().replace(".xml", ".xml.bak"); File backupFile = new File(backupPath); try { Files.copy(xmlFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING); return true; } catch (IOException e) { LOGGER.error("[AdminFarmEvent] Falha ao criar backup: " + xmlFile.getName(), e); player.sendMessage("ERRO: Falha ao criar backup de " + xmlFile.getName() + ". Abortando."); return false; } }
	private void restoreBackup(File xmlFile, Player player) {   String backupPath = xmlFile.getAbsolutePath().replace(".xml", ".xml.bak"); File backupFile = new File(backupPath); if (!backupFile.exists()) { LOGGER.error("[AdminFarmEvent] Backup não encontrado, não foi possível restaurar: " + backupFile.getName()); player.sendMessage("ERRO CRÍTICO: Backup " + backupFile.getName() + " não foi encontrado!"); return; } try { Files.copy(backupFile.toPath(), xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING); LOGGER.info("[AdminFarmEvent] Backup restaurado: " + xmlFile.getName()); player.sendMessage("Um backup do " + xmlFile.getName() + " foi restaurado."); } catch (IOException e) { LOGGER.error("[AdminFarmEvent] FALHA CRÍTICA ao restaurar backup: " + xmlFile.getName(), e); player.sendMessage("ERRO CRÍTICO: Falha ao restaurar backup. Verifique o console!"); } }
	private Document loadXML(File file) throws Exception	{   DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance(); dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false); dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false); dbFactory.setExpandEntityReferences(false); dbFactory.setIgnoringComments(false); dbFactory.setCoalescing(false); dbFactory.setIgnoringElementContentWhitespace(false); String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8); content = content.replaceAll("\u00A0", " "); if (content.startsWith("\uFEFF")) { content = content.substring(1); } DocumentBuilder dBuilder = dbFactory.newDocumentBuilder(); Document doc = dBuilder.parse(new org.xml.sax.InputSource(new java.io.StringReader(content))); doc.getDocumentElement().normalize(); return doc; }
	private boolean saveXML(File file, Document doc, boolean isFragileParser) throws Exception { /* ... código sem alterações (o save cirúrgico) ... */ TransformerFactory transformerFactory = TransformerFactory.newInstance(); Transformer transformer = transformerFactory.newTransformer(); if (isFragileParser) { transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); StringWriter writer = new StringWriter(); transformer.transform(new DOMSource(doc), new StreamResult(writer)); String xmlContent = writer.toString(); String finalXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + xmlContent; try (Writer fileWriter = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) { fileWriter.write(finalXml); } return true; } else { transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); DOMSource source = new DOMSource(doc); StreamResult result = new StreamResult(file); transformer.transform(source, result); return true; } }
	private Element findNode(Document doc, String tagName, String attributeName, String attributeValue) throws Exception {   XPath xPath = XPathFactory.newInstance().newXPath(); XPathExpression expr = xPath.compile("//" + tagName + "[@" + attributeName + "='" + attributeValue + "']"); Node node = (Node) expr.evaluate(doc, XPathConstants.NODE); if (node != null && node.getNodeType() == Node.ELEMENT_NODE) { return (Element) node; } return null; }
	private String formatDrops(List<DropHolder> drops) {   if (drops == null || drops.isEmpty()) { return "Nenhum"; } String dropList = drops.stream().limit(2).map(d -> d.getItemId() + "(" + d.getChance() + "%)").collect(Collectors.joining(", ")); if (drops.size() > 2) { dropList += ", ..."; } return dropList; }
	
	@Override
	public String[] getAdminCommandList()	{
		return ADMIN_COMMANDS;
	}
}