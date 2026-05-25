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
package ext.mods.gameserver.network.serverpackets;

import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.enums.SayType;
import ext.mods.gameserver.model.actor.Player;


public final class NpcHtmlMessage extends L2GameServerPacket
{
	public static boolean SHOW_FILE;
	
	private int _objectId;
	
	private String _html;
	private String _file;
	
	private int _itemId = 0;
	private boolean _validate = true;

	private String _fileName;
	private boolean _isFromTranslator;
	private Map<String, String> _replaces;
	
	public NpcHtmlMessage(int objectId)
	{
		_objectId = objectId;
	}
	
	@Override
	public void runImpl()
	{
		if (!_validate)
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (SHOW_FILE && player.isGM() && _file != null)
			player.sendPacket(new CreatureSay(SayType.ALL, "HTML", _file));
		
		player.clearBypass();
		for (int i = 0; i < _html.length(); i++)
		{
			int start = _html.indexOf("\"bypass ", i);
			int finish = _html.indexOf("\"", start + 1);
			if (start < 0 || finish < 0)
				break;
			
			if (_html.substring(start + 8, start + 10).equals("-h"))
				start += 11;
			else
				start += 8;
			
			i = finish;
			int finish2 = _html.indexOf("$", start);
			if (finish2 < finish && finish2 > 0)
				player.addBypass2(_html.substring(start, finish2).trim());
			else
				player.addBypass(_html.substring(start, finish).trim());
		}
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x0f);
		
		writeD(_objectId);
		writeS(_html);
		writeD(_itemId);
	}
	
	public void disableValidation()
	{
		_validate = false;
	}
	
	public void setItemId(int itemId)
	{
		_itemId = itemId;
	}
	
	public void setHtml(String text)
	{
		if (text == null) {
			_html = "<html><body>Erro: Conteúdo HTML não especificado.</body></html>";
			return;
		}
		
		if (text.trim().isEmpty()) {
			_html = "<html><body>Erro: Conteúdo HTML está vazio.</body></html>";
			return;
		}
		
		if (text.length() > 9192)
		{
			_html = "<html><body>Html was too long.</body></html>";
			LOGGER.warn("An html content was too long.");
			return;
		}
		
		if (!text.toLowerCase().contains("<html") && !text.toLowerCase().contains("<body")) {
		}
		
		_html = text;
	}
	
	public void setFile(Locale locale, String filename)
	{
		_fileName = filename;
		if (SHOW_FILE)
		{
			_file = filename;
			
			
			final int index = _file.indexOf("html/");
			if (index != -1)
				_file = _file.substring(index + 5, _file.length());
		}
		
		if (filename == null || filename.trim().isEmpty()) {
			setHtml("<html><body>Erro: Arquivo HTML não especificado.</body></html>");
			return;
		}
		
		String htmlContent = HTMLData.getInstance().getHtm(locale, filename);
		if (htmlContent == null || htmlContent.trim().isEmpty()) {
			htmlContent = HTMLData.getInstance().getHtm(Locale.getDefault(), filename);
			if (htmlContent == null || htmlContent.trim().isEmpty()) {
				htmlContent = "<html><body>Erro: Arquivo HTML não encontrado: " + filename + "</body></html>";
			}
		}
		
		setHtml(htmlContent);
	}
	
	public void replace(String pattern, String value)
	{
		replace(pattern, value, true);
	}
			
	public void replace(String pattern, String value, boolean record)
	{
		_html = _html.replaceAll(pattern, value.replaceAll("\\$", "\\\\\\$"));
		if (record)
			recordReplace(pattern, value);
	}
	
	public void replace(String pattern, int value)
	{
		replace(pattern, String.valueOf(value), true);
	}
	
	public void replace(String pattern, long value)
	{
		replace(pattern, String.valueOf(value), true);
	}
	
	public void replace(String pattern, double value)
	{
		replace(pattern, String.valueOf(value), true);
	}
	
	public void replace(String pattern, boolean value)
	{
		replace(pattern, String.valueOf(value), true);
 	}
	
	public String getContent()
	{
		return _html;
	}
	
	public String getFileName()
	{
		return _fileName;
	}
	
	/**
	 * Define o nome do arquivo manualmente
	 * @param fileName Nome do arquivo
	 */
	public void setFileName(String fileName)
	{
		_fileName = fileName;
	}
	
	/**
	 * Verifica se o HTML é válido para tradução
	 * @return true se o HTML pode ser traduzido
	 */
	public boolean isValidForTranslation()
	{
		if (_html == null || _html.trim().isEmpty()) {
			return false;
		}
		
		if (_fileName == null || _fileName.trim().isEmpty()) {
			return false;
		}
		
		String htmlLower = _html.toLowerCase();
		if (!htmlLower.contains("<body") || !htmlLower.contains("</body>")) {
			return false;
		}
		
		String bodyContent = _html.substring(_html.indexOf("<body") + 5, _html.lastIndexOf("</body>"));
		if (bodyContent.trim().isEmpty() || bodyContent.trim().matches("<[^>]*>\\s*")) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Obtém informações de debug sobre o HTML
	 * @return String com informações de debug
	 */
	public String getDebugInfo()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("NpcHtmlMessage Debug Info:\n");
		sb.append("- FileName: ").append(_fileName).append("\n");
		sb.append("- IsFromTranslator: ").append(_isFromTranslator).append("\n");
		sb.append("- HasReplaces: ").append(_replaces != null && !_replaces.isEmpty()).append("\n");
		sb.append("- HtmlLength: ").append(_html != null ? _html.length() : 0).append("\n");
		sb.append("- IsValidForTranslation: ").append(isValidForTranslation()).append("\n");
		sb.append("- ObjectId: ").append(_objectId).append("\n");
		
		if (_replaces != null) {
			sb.append("- ReplacesCount: ").append(_replaces.size()).append("\n");
		}
		
		return sb.toString();
	}

	/*
	 * Salva os replaces que forem feitos nesse html para serem reaplicados depois após a tradução
	 */
	private synchronized void recordReplace(String regex, String replacement)
	{
		if (_replaces == null)
			_replaces = new HashMap<>();
		
		_replaces.put(regex, replacement);
	}
	
	public boolean isFromTranslator()
	{
		return _isFromTranslator;
	}
	
	public void setFromTranslator(Boolean status)
	{
		_isFromTranslator = status;
	}
	
	public Map<String, String> getReplaces()
	{
		if (_objectId != 0)
		{
			if (_replaces == null)
				_replaces = new HashMap<>();
			
			_replaces.put("%objectId%", String.valueOf(_objectId));
		}
		
		return _replaces;
	}
	
	public void applyReplaces(Map<String, String> map)
	{
		if (map == null || map.isEmpty()) {
			return;
		}
		
		
		for (Entry<String, String> entry : map.entrySet())
		{
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}
			
			if (entry.getKey().equals("%objectId%"))
			{
				try {
					_objectId = Integer.parseInt(entry.getValue().replace("%", ""));
				} catch (NumberFormatException e) {
				}
			}
			
			replace(entry.getKey(), entry.getValue(), false);
		}
		
	}
}