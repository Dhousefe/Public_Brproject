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
package ext.mods.Crypta;

import com.deepl.api.DeepLClient;
import com.deepl.api.DeepLException;
import com.deepl.api.QuotaExceededException;
import com.deepl.api.TextResult;
import com.deepl.api.TextTranslationOptions;
import com.deepl.api.Translator;
import ext.mods.Config;
import ext.mods.commons.pool.ThreadPool;
import ext.mods.commons.logging.CLogger;
import ext.mods.gameserver.data.HTMLData;
import ext.mods.gameserver.model.actor.Player;
import ext.mods.gameserver.network.serverpackets.NpcHtmlMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles HTML translation using the DeepL API, refactored for Java 11+ with a language selection menu.
 * @author ColdPlay Refatored by Dhousefe
 */
public class DeeplTranslator {
	private static final CLogger LOGGER = new CLogger(DeeplTranslator.class.getName());

	public enum Language {
		ENGLISH("en_US", "English", true),
		PORTUGUESE("pt-BR", "Portuguese", false),
		SPANISH("es", "Spanish", false),
		KOREAN("ko", "Korean", false),
		RUSSIAN("ru_RU", "Russian", false),
		CHINESE("zh", "Chinese", false),
		GERMAN("de", "German", false),
		ITALIAN("it", "Italian", false),
		FRENCH("fr", "French", false),
		POLISH("pl", "Polish", false),
		GREEK("el", "Greek", false),
		JAPANESE("ja", "Japanese", false);

		private final String _code;
		private final String _displayName;
		private final boolean _isOriginal;

		Language(String code, String displayName, boolean isOriginal) {
			_code = code;
			_displayName = displayName;
			_isOriginal = isOriginal;
		}

		public String getCode() {
			return _code;
		}

		public String getDisplayName() {
			return _displayName;
		}

		public boolean isOriginal() {
			return _isOriginal;
		}

		public static Optional<Language> fromCode(String code) {
			return Arrays.stream(values())
				.filter(lang -> lang.getCode().equalsIgnoreCase(code))
				.findFirst();
		}
	}

	private static final String HTML_ROOT_PATH = "data/locale/en_US/html/";
	private static final String LOCALE_BASE_PATH = "data/locale/";
	private static final String MENU_BYPASS = "";

	private final Map<Integer, Map<String, String>> _playerReplaces = new ConcurrentHashMap<>();
	private final Map<Integer, Language> _playerPreferences = new ConcurrentHashMap<>();
	private final Map<String, Map<String, String>> _placeholderMappings = new ConcurrentHashMap<>();

	private volatile boolean _accountLimitReached = false;
	private TextTranslationOptions _translatorOptions;
	private Translator _translator;

	private DeeplTranslator() {
		asyncReload();
	}

	public void asyncReload() {
		ThreadPool.execute(this::reload);
	}
	
	/**
	 * Verifica se o tradutor está disponível (chave da API configurada)
	 * @return true se o tradutor está disponível, false caso contrário
	 */
	public boolean isTranslatorAvailable() {
		return _translator != null;
	}

	private void reload() {
		try {
			
			if (Config.DEEPL_AUTH_KEY == null || Config.DEEPL_AUTH_KEY.trim().isEmpty()) {
				_translator = null;
				return;
			}
			
			_translator = new DeepLClient(Config.DEEPL_AUTH_KEY);
			_translatorOptions = new TextTranslationOptions()
				.setPreserveFormatting(true)
				.setTagHandling("html")
				.setContext(Config.DEEP_CONTEXT_STRING);

			_accountLimitReached = _translator.getUsage().anyLimitReached();
			LOGGER.info("DeeplTranslator has been initialized. Account limit reached: {}", _accountLimitReached);
		} catch (Exception e) {
			LOGGER.error("Failed to initialize DeeplTranslator.", e);
			_translator = null;
		}
	}

	public boolean handlePacket(Player player, NpcHtmlMessage html) {
		
		final String originalFile = html.getFileName();
		
		if (originalFile == null || originalFile.trim().isEmpty() || html.isFromTranslator()) {
			return true;
		}

		final String normalizedFile = normalizeFilePath(originalFile);
		
		if (normalizedFile == null) {
			return true;
		}

		final boolean isTranslatable = isTranslatable(normalizedFile);

		if (isTranslatable && html.getReplaces() != null) {
			_playerReplaces.put(player.getObjectId(), html.getReplaces());
		}

		final Language playerLang = getPlayerLanguage(player);

		if (playerLang.isOriginal()) {
			html.setFileName(originalFile);
			if (isTranslatable) {
				html.replace("</body>", MENU_BYPASS + "</body>");
			}
			return true;
		}

		if (isTranslatable) {
			final Path translatedPath = Path.of(getTranslatedPath(normalizedFile, playerLang));

			if (Files.exists(translatedPath)) {
				try {
					String content = Files.readString(translatedPath, StandardCharsets.UTF_8);
					html.setHtml(content);
					html.applyReplaces(html.getReplaces());
				} catch (IOException e) {
					LOGGER.error("Failed to read translated file '{}'.", e, translatedPath);
					html.replace("</body>", MENU_BYPASS + "</body>");
					return true;
				}
			} else {
				
				if (!accountLimitReached() && _translator != null) {
					asyncTranslate(player, normalizedFile, playerLang);
					return false;
				} else {
					html.replace("</body>", MENU_BYPASS + "</body>");
					return true;
				}
			}
		} else {
			html.replace("</body>", MENU_BYPASS + "</body>");
		}

		html.replace("</body>", MENU_BYPASS + "</body>");
		return true;
	}

	

	public void showLanguageMenu(Player player) {
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		final int maxCols = 6;

		var sb = new StringBuilder("<html><title>Seletor de Idioma</title><body><center>");
		sb.append("<img src=\"L2UI.SquareGray\" width=280 height=1>");
		sb.append("<table width=280 height=40 bgcolor=000000>");
		sb.append("<tr><td align=center><font color=LEVEL>Selecione seu Idioma</font></td></tr>");
		sb.append("<tr><td align=center><font color=B09878>Clique na bandeira para escolher o idioma.</font></td></tr>");
		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareGray\" width=280 height=1><br>");
		sb.append("<table width=280>");

		int currentCol = 0;
		for (var lang : Language.values()) {
			if (currentCol == 0) {
				sb.append("<tr>");
			}

			sb.append("<td width=40 align=center><button title=\"").append(lang.getDisplayName()).append("\" action=\"bypass -h _trans set ").append(lang.getCode()).append("\" width=25 height=25 back=\"L2butom.").append(lang.getDisplayName()).append("\" fore=\"L2butom.").append(lang.getDisplayName()).append("\"></td>");

			currentCol++;

			if (currentCol >= maxCols) {
				sb.append("</tr>");
				sb.append("<tr><td height=10></td></tr>");
				currentCol = 0;
			}
		}

		if (currentCol != 0) {
			sb.append("</tr>");
		}

		sb.append("</table><br>");
		sb.append("<img src=\"L2UI.SquareGray\" width=280 height=1>");
		sb.append("</center></body></html>");

		html.setHtml(sb.toString());
		html.setFileName("language_menu.html");
		html.setFromTranslator(true);
		player.sendPacket(html);
	}

	public void setPlayerLanguage(Player player, String langCode) {
		Language.fromCode(langCode).ifPresent(lang -> {
			_playerPreferences.put(player.getObjectId(), lang);
			
			if (_translator == null) {
				player.sendMessage("Idioma alterado para " + lang.getDisplayName() + ".");
				player.sendMessage("AVISO: Chave da API DeepL não configurada. Configure em game/config/translator.properties");
			} else {
				player.sendMessage("Idioma alterado para " + lang.getDisplayName() + ".");
			}
			showLanguageMenu(player);
		});
	}

	private void asyncTranslate(Player player, String file, Language targetLang) {
		
		if (accountLimitReached()) {
			String originalFile = getOriginalRelativeFile(file);
			showChatWindow(player, originalFile, true);
			player.sendMessage("A tradução não está disponível no momento. Tente novamente mais tarde.");
			return;
		}

		showTranslatingWindow(player);

		translateFile(file, targetLang).thenAccept(success -> {
			if (success) {
				String translatedPath = getTranslatedPath(file, targetLang);
				showChatWindow(player, translatedPath, false);
			} else {
				String originalFile = getOriginalRelativeFile(file);
				showChatWindow(player, originalFile, true);
				player.sendMessage("Ocorreu um erro ao traduzir. Por favor, tente novamente.");
			}
		});
	}

	/**
	 * Extracts all translatable TextNode objects from a Jsoup document.
	 * This method uses a NodeVisitor to traverse the entire DOM tree, ensuring no text is missed,
	 * unlike selector-based approaches which can miss text nodes that are direct children of the body.
	 *
	 * @param doc The Jsoup document to parse.
	 * @return A list of TextNode objects that contain translatable text.
	 */
	private List<TextNode> extractTextNodesForTranslation(Document doc) {
			final List<TextNode> textNodes = new ArrayList<>();
			final DeeplTranslator self = this;
			
			doc.body().traverse(new org.jsoup.select.NodeVisitor() {
				@Override
				public void head(org.jsoup.nodes.Node node, int depth) {
					if (node instanceof org.jsoup.nodes.TextNode) {
						org.jsoup.nodes.TextNode textNode = (org.jsoup.nodes.TextNode) node;
						final String text = textNode.text().trim();
						if (!text.isBlank() && !text.replace("\u00A0", "").isBlank()) {
							if (self.containsClosingTags(text)) {
								return;
							}
							
							if (self.isOnlyPlaceholder(text)) {
								return;
							}
							
							org.jsoup.nodes.Node parent = textNode.parent();
							if (parent != null) {
								String parentTag = ((Element) parent).tagName().toLowerCase();
								if (!parentTag.equals("button") && !parentTag.equals("script") && !parentTag.equals("style")) {
									textNodes.add(textNode);
								}
							}
						}
					}
				}
	
				@Override
				public void tail(org.jsoup.nodes.Node node, int depth) {
				}
			});
			return textNodes;
		}
		
	
	
		private void showChatWindow(Player player, String file, boolean isOriginal) {
			final var html = new NpcHtmlMessage(0);
			
			String relativeFile = file;
			if (file.contains("html/")) {
				int htmlIndex = file.indexOf("html/");
				relativeFile = file.substring(htmlIndex);
			}
			
			if (isOriginal) {
				Locale defaultLocale = Locale.forLanguageTag("en-US");
				html.setFile(defaultLocale, relativeFile);
			} else {
				try {
					String content = Files.readString(Path.of(file), StandardCharsets.UTF_8);
					html.setHtml(content);
					html.setFileName(relativeFile);
				} catch (IOException e) {
					LOGGER.error("Failed to read content from file '{}', falling back to HtmCache.", e, file);
					String originalFile = file;
					if (file.contains("/locale/")) {
						int htmlIndex = file.indexOf("html/");
						if (htmlIndex != -1) {
							originalFile = file.substring(htmlIndex);
						}
					}
					html.setFile(Locale.getDefault(), originalFile);
				}
			}
	
			Optional.ofNullable(_playerReplaces.get(player.getObjectId())).ifPresent(html::applyReplaces);
	
			html.setFromTranslator(true);
			html.replace("</body>", MENU_BYPASS + "</body>");
			player.sendPacket(html);
		}
		
		private static void showTranslatingWindow(Player player) {
			final var html = new NpcHtmlMessage(0);
			html.setHtml("<html><head><title>Idiomas</title></head><body>Por favor, aguarde...<br>Traduzindo o texto...</body></html>");
			html.setFileName("translating.html");
			html.setFromTranslator(true);
			player.sendPacket(html);
		}
	
	/**
	 * Verifica se o texto contém tags de fechamento que não devem ser traduzidas
	 * @param text Texto a ser verificado
	 * @return true se contém tags de fechamento proibidas
	 */
	private boolean containsClosingTags(String text) {
		String[] forbiddenClosingTags = {
			"</button>", "</multiedit>", "</combobox>", "</edit>"
		};
		
		String trimmedText = text.trim();
		String lowerText = trimmedText.toLowerCase();
		
		for (String tag : forbiddenClosingTags) {
			if (lowerText.equals(tag.toLowerCase()) || 
				(lowerText.contains(tag.toLowerCase()) && trimmedText.length() <= tag.length() + 10)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Verifica se o texto é APENAS um placeholder (não deve ser traduzido)
	 * @param text Texto a ser verificado
	 * @return true se é apenas um placeholder
	 */
	private boolean isOnlyPlaceholder(String text) {
		String trimmedText = text.trim();
		
		if (trimmedText.matches("^%\\w+%$")) {
			return true;
		}
		
		if (trimmedText.matches("^<\\w+>%\\w+%</\\w+>$")) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Remove placeholders do texto antes da tradução usando regex simples
	 * @param text Texto original
	 * @return Texto sem placeholders para tradução
	 */
	private String removePlaceholdersForTranslation(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		
		Map<String, String> placeholderMap = new HashMap<>();
		String result = text;
		int counter = 0;
		
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("%\\w+%");
		java.util.regex.Matcher matcher = pattern.matcher(result);
		
		while (matcher.find()) {
			String placeholder = matcher.group();
			String tempMarker = "PLACEHOLDER_" + counter + "_MARKER";
			placeholderMap.put(tempMarker, placeholder);
			result = result.replace(placeholder, tempMarker);
			counter++;
		}
		
		_placeholderMappings.put(text, placeholderMap);
		
		return result;
	}
	
	/**
	 * Restaura placeholders após a tradução
	 * @param translatedText Texto traduzido
	 * @param originalText Texto original
	 * @return Texto traduzido com placeholders restaurados
	 */
	private String restorePlaceholdersAfterTranslation(String translatedText, String originalText) {
		if (translatedText == null || originalText == null) {
			return translatedText;
		}
		
		Map<String, String> placeholderMap = _placeholderMappings.get(originalText);
		if (placeholderMap == null || placeholderMap.isEmpty()) {
			return translatedText;
		}
		
		String result = translatedText;
		for (Map.Entry<String, String> entry : placeholderMap.entrySet()) {
			result = result.replace(entry.getKey(), entry.getValue());
		}
		
		_placeholderMappings.remove(originalText);
		
		return result;
	}
	
	/**
	 * Remove todas as tags de fechamento do HTML antes de salvar
	 * @param html HTML content
	 * @return HTML sem as tags de fechamento
	 */
	private String removeClosingTags(String html) {
		String[] closingTagsToRemove = {
			"</button>", "</multiedit>", "</combobox>", "</edit>"
		};
		
		String result = html;
		int totalRemoved = 0;
		
		for (String tag : closingTagsToRemove) {
			int count = 0;
			while (result.contains(tag)) {
				result = result.replace(tag, "");
				count++;
			}
			if (count > 0) {
				totalRemoved += count;
			}
		}
		
		if (totalRemoved > 0) {
		}
		
		return result;
	}
	
	/**
	 * Normaliza o caminho do arquivo para sempre usar o caminho completo
	 * @param fileName Nome do arquivo (pode ser relativo ou completo)
	 * @return Caminho completo normalizado
	 */
	private String normalizeFilePath(String fileName) {
		
		if (fileName == null) {
			return null;
		}
		
		if (fileName.contains("/locale/")) {
			return fileName;
		}
		
		if (fileName.startsWith("html/")) {
			String result = HTML_ROOT_PATH + fileName.substring(5);
			return result;
		}
		
		String result = HTML_ROOT_PATH + fileName;
		return result;
	}

	/**
	 * Extrai o arquivo original relativo a partir de um caminho completo
	 * @param fullPath Caminho completo do arquivo
	 * @return Arquivo original relativo (ex: html/gatekeeper/30256.htm)
	 */
	private String getOriginalRelativeFile(String fullPath) {
		
		if (fullPath == null) {
			return null;
		}
		
		if (fullPath.contains("/locale/")) {
			int htmlIndex = fullPath.indexOf("html/");
			if (htmlIndex != -1) {
				String result = fullPath.substring(htmlIndex);
				return result;
			}
		}
		
		if (fullPath.startsWith("html/")) {
			return fullPath;
		}
		
		if (fullPath.startsWith(HTML_ROOT_PATH)) {
			String result = "html/" + fullPath.substring(HTML_ROOT_PATH.length());
			return result;
		}
		
		if (fullPath.startsWith("data/html/")) {
			String result = fullPath.substring(5);
			return result;
		}
		
		return fullPath;
	}

	private static String getTranslatedPath(String currentFile, Language lang) {
		
		if (currentFile.contains("/locale/")) {
			String result = currentFile.replaceAll("/locale/[^/]+/", "/locale/" + lang.getCode() + "/");
			return result;
		}
		
		String relativePath = currentFile;
		if (currentFile.startsWith(HTML_ROOT_PATH)) {
			relativePath = currentFile.substring(HTML_ROOT_PATH.length());
		}
		
		String result = LOCALE_BASE_PATH + lang.getCode() + "/html/" + relativePath;
		return result;
	}
	
	/**
	 * Mapeia códigos de idioma para o formato esperado pela API DeepL
	 * @param languageCode Código do idioma
	 * @return Código mapeado para DeepL
	 */
	private static String mapLanguageCodeForDeepL(String languageCode) {
		switch (languageCode) {
			case "en_US":
				return "en";
			case "pt-BR":
				return "pt-BR";
			case "ru_RU":
				return "ru";
			default:
				return languageCode;
		}
	}
	
	private static boolean isTranslatable(String fileName) {
		
		
		boolean notInExclusionList = true;
		if (Config.DO_NOT_TRANSLATE != null && !Config.DO_NOT_TRANSLATE.isEmpty()) {
			notInExclusionList = Config.DO_NOT_TRANSLATE.stream().noneMatch(exclusionPath -> {
				boolean contains = fileName.contains(exclusionPath);
				return contains;
			});
		}
		if (!notInExclusionList) {
		}
		
		boolean originalExists = Files.exists(Path.of(fileName));
		
		boolean result = notInExclusionList && originalExists;
		
		return result;
	}
	
	
		public Language getPlayerLanguage(Player player) {
			Language lang = _playerPreferences.getOrDefault(player.getObjectId(), Language.ENGLISH);
			return lang;
		}
	
		public boolean accountLimitReached() {
			return _accountLimitReached;
		}
	
		public static DeeplTranslator getInstance() {
			return SingletonHolder.INSTANCE;
		}
	
		private static class SingletonHolder {
			protected static final DeeplTranslator INSTANCE = new DeeplTranslator();
		}

	/**
	 * Translates an HTML file using a robust, structure-aware method with Jsoup.
	 * This method extracts text nodes, translates them in a batch, and then updates the nodes
	 * directly in the Jsoup document, preserving HTML structure and attributes perfectly.
	 *
	 * @param htmlFile   The path to the original HTML file.
	 * @param targetLang The language to translate to.
	 * @return A CompletableFuture indicating if the translation was successful.
	 */
	public CompletableFuture<Boolean> translateFile(String htmlFile, Language targetLang) {
		return CompletableFuture.supplyAsync(() -> {
			
			if (_translator == null) {
				reload();
				if (_translator == null) {
					LOGGER.warn("Translation requested but translator is not available - API key not configured.");
					return false;
				}
			}
			

			try {
				final Path outputPath = Path.of(getTranslatedPath(htmlFile, targetLang));
				
				String relativeFile = htmlFile;
				if (htmlFile.contains("html/")) {
					int htmlIndex = htmlFile.indexOf("html/");
					relativeFile = htmlFile.substring(htmlIndex);
				}
				
				Locale defaultLocale = Locale.forLanguageTag("en-US");
				
				HTMLData htmlData = HTMLData.getInstance();
				
				String htmContent = null;
				if (htmlData != null) {
					htmContent = htmlData.getHtm(defaultLocale, relativeFile);
				}
				if (htmContent == null || htmContent.trim().isEmpty()) {
					HTMLData fallbackHtmlData = HTMLData.getInstance();
					if (fallbackHtmlData != null) {
						htmContent = fallbackHtmlData.getHtm(Locale.getDefault(), relativeFile);
					}
					if (htmContent == null || htmContent.trim().isEmpty()) {
						try {
							htmContent = Files.readString(Path.of(htmlFile), StandardCharsets.UTF_8);
						} catch (IOException e) {
							LOGGER.warn("Could not read HTML content for file: {}", relativeFile);
							return false;
						}
					}
				}
				
				if (htmContent == null || htmContent.trim().isEmpty()) {
					return false;
				}
				

				htmContent = htmContent.replace("<br1>", "<br>");

				final Document doc = Jsoup.parse(htmContent);
				doc.head().append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

				final List<TextNode> nodesToTranslate = extractTextNodesForTranslation(doc);
				
				if (nodesToTranslate.isEmpty()) {
					Files.createDirectories(outputPath.getParent());
					Files.writeString(outputPath, doc.outerHtml(), StandardCharsets.UTF_8);
					return true;
				}

				final List<String> originalTexts = new ArrayList<>();
				final List<String> textsForTranslation = new ArrayList<>();
				
				for (TextNode textNode : nodesToTranslate) {
					String originalText = textNode.text();
					originalTexts.add(originalText);
					
					String textForTranslation = removePlaceholdersForTranslation(originalText);
					textsForTranslation.add(textForTranslation);
				}

				
				String sourceLang = DeeplTranslator.mapLanguageCodeForDeepL(Language.ENGLISH.getCode());
				String targetLangCode = DeeplTranslator.mapLanguageCodeForDeepL(targetLang.getCode());
				
				
				final List<TextResult> translationResults = _translator.translateText(textsForTranslation, sourceLang, targetLangCode, _translatorOptions);

				if (originalTexts.size() != translationResults.size()) {
					LOGGER.error("Mismatch between original text count ({}) and translated text count ({}) for file '{}'.", originalTexts.size(), translationResults.size(), htmlFile);
					return false;
				}

				for (int i = 0; i < nodesToTranslate.size(); i++) {
					TextNode node = nodesToTranslate.get(i);
					String translatedText = translationResults.get(i).getText();
					String originalText = originalTexts.get(i);
					
					String finalTranslatedText = restorePlaceholdersAfterTranslation(translatedText, originalText);
					node.text(finalTranslatedText);
					
				}

				String translatedHtml = doc.outerHtml();
				
				translatedHtml = removeClosingTags(translatedHtml);

				Files.createDirectories(outputPath.getParent());
				
				Files.writeString(outputPath, translatedHtml, StandardCharsets.UTF_8);

				return true;
			} catch (QuotaExceededException e) {
				LOGGER.warn("DeepL usage limit has been reached.", e);
				_accountLimitReached = true;
			} catch (DeepLException e) {
				LOGGER.error("Failed to translate file '{}' to {}.", e, htmlFile, targetLang.getCode());
			} catch (IOException e) {
				LOGGER.error("Failed to write translated file for '{}'.", e, htmlFile);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("An unexpected error occurred during translation of '{}'.", e, htmlFile);
			}

			return false;
		});
	}

	/**
	 * Traduz uma string do inglês para o idioma alvo (mesma origem que {@link #translateFile}).
	 *
	 * @param targetLang idioma de destino
	 * @param text       texto em inglês
	 * @return texto traduzido, ou o original se o tradutor estiver indisponível ou ocorrer erro
	 */
	public CompletableFuture<String> translateText(Language targetLang, String text) {
		return CompletableFuture.supplyAsync(() -> {
			if (text == null || text.isEmpty()) {
				return text;
			}
			if (targetLang == null || targetLang.isOriginal()) {
				return text;
			}
			if (_translator == null) {
				reload();
				if (_translator == null) {
					return text;
				}
			}
			try {
				String sourceLang = mapLanguageCodeForDeepL(Language.ENGLISH.getCode());
				String targetLangCode = mapLanguageCodeForDeepL(targetLang.getCode());
				List<TextResult> translationResults = _translator.translateText(List.of(text), sourceLang, targetLangCode, _translatorOptions);
				if (translationResults == null || translationResults.isEmpty()) {
					return text;
				}
				return translationResults.get(0).getText();
			} catch (QuotaExceededException e) {
				LOGGER.warn("DeepL usage limit has been reached.", e);
				_accountLimitReached = true;
			} catch (DeepLException e) {
				LOGGER.error("Failed to translate text to {}.", e, targetLang.getCode());
			} catch (Exception e) {
				LOGGER.error("Unexpected error translating text.", e);
			}
			return text;
		});
	}

}