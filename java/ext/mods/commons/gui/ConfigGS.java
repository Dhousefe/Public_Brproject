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
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, 
* SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo, Tiagorosendo, Schuster, LucasStark, damedd
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ext.mods.commons.gui.CustomTopPanel;
import ext.mods.commons.gui.CustomToggleSwitch;
import ext.mods.commons.gui.ThemeManager;
import ext.mods.commons.gui.GuiUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfigGS {


    private record ModFileConfig(String label, Path path, String key) {}

    private static final List<ModFileConfig> MODS_CONFIG_MAP = List.of(
        new ModFileConfig("Announce Boss HP %", Paths.get("game", "data", "custom", "mods", "bossHpAnnounce.xml"), "enabled"),
        new ModFileConfig("Announce Raid Drop", Paths.get("game", "data", "custom", "mods", "raid_drop_announce.xml"), "enabled"),
        new ModFileConfig("Battle Boss Event", Paths.get("game", "data", "custom", "mods", "battleboss.xml"), "enabled"),
        new ModFileConfig("Capsule Box", Paths.get("game", "data", "xml", "CapsuleBox.xml"), "enabled"),
        new ModFileConfig("Equips Grade Restrictions", Paths.get("game", "data", "custom", "mods", "equip_grade_restrictions.xml"), "enabled"),
        new ModFileConfig("Global Drop", Paths.get("game", "data", "custom", "mods", "global_drop.xml"), "enable"),
        new ModFileConfig("Olly Restrictions Mode", Paths.get("game", "data", "custom", "mods", "olympiad_enchant_config.xml"), "enabled"),
        new ModFileConfig("Player God Mode", Paths.get("game", "data", "custom", "mods", "PlayerGOD.xml"), "enabled"),
        new ModFileConfig("PVP Colors Sistem", Paths.get("game", "data", "custom", "mods", "pvpSystem.xml"), "enabled"),
        new ModFileConfig("Random Farm Zone", Paths.get("game", "data", "custom", "mods", "random_event.xml"), "enable"),
        new ModFileConfig("Sell Buff Mode", Paths.get("game", "config", "mods.properties"), "BuffShopEnabled"),
        new ModFileConfig("Summon Mob Item", Paths.get("game", "data", "custom", "mods", "SummonMobItem.xml"), "enabled"),
        new ModFileConfig("Tournament Event Mode", Paths.get("game", "data", "custom", "mods", "tourBattle.xml"), "enabled")
    );

    private static final Path SERVER_PROPERTIES_PATH = Paths.get("game", "config", "server.properties");
    private static final String NO_SPAWNS_KEY = "NoSpawns";
    private static final Path NPCS_PROPERTIES_PATH = Paths.get("game", "config", "npcs.properties");
    private static final String SPAWN_EVENTS_KEY = "SpawnEvents";

    private static final String GM_SHOP_EVENT = "gm_shop";
    private static final String NPC_BUFFER_EVENT = "npc_buffer";
    private static final String GLOBAL_GK_EVENT = "global_gatekeeper";
    private static final String GLOBAL_SERVICES_EVENT = "global_services";
    private static final String CLASS_MANAGER_EVENT = "class_manager";

    private static final List<String> FIXED_EVENTS_ORDERED = List.of(
        "18age",
        "extra_mob",
        "start_weapon"
    );

    private static class SpawnEventConfig {
        public final String label;
        public final String eventName;
        public SpawnEventConfig(String label, String eventName) {
            this.label = label;
            this.eventName = eventName;
        }
    }

    private static final List<SpawnEventConfig> SPAWN_EVENTS_CONFIG = Arrays.asList(
        new SpawnEventConfig("Spawn GM Shop", GM_SHOP_EVENT),
        new SpawnEventConfig("Spawn NPC Buffer", NPC_BUFFER_EVENT),
        new SpawnEventConfig("Spawn Global GK", GLOBAL_GK_EVENT),
        new SpawnEventConfig("Spawn Global Services", GLOBAL_SERVICES_EVENT),
        new SpawnEventConfig("Spawn Class Manager", CLASS_MANAGER_EVENT)
    );

    private JFrame configFrame;
    private CustomTopPanel topPanel;
    private Frame parentFrame;

    private CustomToggleSwitch tglNoLoadSpawns;

    private final Map<SpawnEventConfig, CustomToggleSwitch> spawnEventTogglesMap = new LinkedHashMap<>();
    private final Map<ModFileConfig, CustomToggleSwitch> modTogglesMap = new LinkedHashMap<>();

    public ConfigGS(Frame parent) {
        this.parentFrame = parent;
        initialize();
    }

    public static void main(String[] args) {
        ThemeManager.applyTheme();
        SwingUtilities.invokeLater(() -> {
            ConfigGS configWindow = new ConfigGS(null);
            configWindow.showWindow();
            configWindow.configFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    System.out.println("Janela ConfigGS fechada. Encerrando aplicação.");
                    System.exit(0);
                }
            });
            configWindow.configFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        });
    }

    private void initialize() {
        configFrame = new JFrame("Configurações do GameServer");
        configFrame.setUndecorated(true);
        configFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        configFrame.setSize(650, 400);
        configFrame.setResizable(true);

        Runnable closeAction = () -> configFrame.dispose();
        JMenuBar menuBar = null;
        String iconPath = (parentFrame == null) ? "./images/16x16.png" : ".." + File.separator + "images" + File.separator + "16x16.png";

        topPanel = new CustomTopPanel(configFrame, menuBar, closeAction, true, iconPath);
        configFrame.add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(ThemeManager.COMPONENT_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        Border lineBorder = BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 12);

        JPanel leftPanel = createModsPanel(lineBorder, titleFont);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0; gbcLeft.gridy = 0; gbcLeft.weightx = 1.0; gbcLeft.weighty = 1.0;
        gbcLeft.fill = GridBagConstraints.BOTH; gbcLeft.insets = new Insets(0, 0, 0, 5);
        contentPanel.add(leftPanel, gbcLeft);

        JPanel rightPanel = createSpawnsPanel(lineBorder, titleFont);

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 1; gbcRight.gridy = 0; gbcRight.weightx = 0.0; gbcRight.weighty = 1.0;
        gbcRight.fill = GridBagConstraints.VERTICAL; gbcRight.anchor = GridBagConstraints.NORTHWEST;
        gbcRight.insets = new Insets(0, 5, 0, 0);
        contentPanel.add(rightPanel, gbcRight);

        configFrame.add(contentPanel, BorderLayout.CENTER);

        loadCurrentConfigStates();
        addToggleSwitchListeners();
        loadIcons();

        configFrame.setLocationRelativeTo(parentFrame);
    }

    private JPanel createModsPanel(Border lineBorder, Font titleFont) {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setOpaque(false);

        Border titledBorderMods = BorderFactory.createTitledBorder(
            lineBorder, " Custom Mods ", TitledBorder.LEFT, TitledBorder.TOP,
            titleFont, ThemeManager.TEXT_COLOR.brighter()
        );
        leftPanel.setBorder(BorderFactory.createCompoundBorder(titledBorderMods, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel modsCol1 = new JPanel(new GridLayout(0, 1, 0, 5));
        JPanel modsCol2 = new JPanel(new GridLayout(0, 1, 0, 5));
        modsCol1.setOpaque(false);
        modsCol2.setOpaque(false);

        int half = (int) Math.ceil(MODS_CONFIG_MAP.size() / 2.0);

        for (int i = 0; i < MODS_CONFIG_MAP.size(); i++) {
            ModFileConfig modConfig = MODS_CONFIG_MAP.get(i);

            JLabel label = createLabel(modConfig.label());
            CustomToggleSwitch toggle = new CustomToggleSwitch(false);
            modTogglesMap.put(modConfig, toggle);

            JPanel container = new JPanel(new BorderLayout());
            container.setOpaque(false);
            container.add(label, BorderLayout.WEST);
            container.add(toggle, BorderLayout.EAST);

            if (i < half) {
                modsCol1.add(container);
            } else {
                modsCol2.add(container);
            }
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(modsCol1, gbc);

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setForeground(ThemeManager.BORDER_COLOR.darker());
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.VERTICAL;
        leftPanel.add(separator, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 5, 0, 0);
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        leftPanel.add(modsCol2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        leftPanel.add(Box.createVerticalGlue(), gbc);

        return leftPanel;
    }

    private JPanel createSpawnsPanel(Border lineBorder, Font titleFont) {

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);

        Border titledBorderSpawns = BorderFactory.createTitledBorder(
            lineBorder, " Custom Spawns ", TitledBorder.LEFT, TitledBorder.TOP,
            titleFont, ThemeManager.TEXT_COLOR.brighter()
        );
        rightPanel.setBorder(BorderFactory.createCompoundBorder(titledBorderSpawns, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel optionsWrapperPanel = new JPanel(new GridBagLayout());
        optionsWrapperPanel.setOpaque(false);
        GridBagConstraints gbcOptions = new GridBagConstraints();
        gbcOptions.anchor = GridBagConstraints.LINE_START;
        int bottomSpacing = 8;
        int rightLabelSpacing = 15;
        gbcOptions.insets = new Insets(0, 0, bottomSpacing, rightLabelSpacing);

        int row = 0;

        tglNoLoadSpawns = new CustomToggleSwitch(false);

        gbcOptions.gridy = row++;
        gbcOptions.gridx = 0;
        gbcOptions.weightx = 1.0;
        gbcOptions.fill = GridBagConstraints.HORIZONTAL;
        optionsWrapperPanel.add(createLabel("No Load Spawns"), gbcOptions);

        gbcOptions.gridx = 1;
        gbcOptions.anchor = GridBagConstraints.LINE_END;
        gbcOptions.weightx = 0;
        gbcOptions.fill = GridBagConstraints.NONE;
        optionsWrapperPanel.add(tglNoLoadSpawns, gbcOptions);

        gbcOptions.insets = new Insets(0, 0, bottomSpacing, rightLabelSpacing);

        for (SpawnEventConfig config : SPAWN_EVENTS_CONFIG) {
            CustomToggleSwitch toggle = new CustomToggleSwitch(false);
            spawnEventTogglesMap.put(config, toggle);

            gbcOptions.gridy = row++;
            gbcOptions.gridx = 0;
            gbcOptions.anchor = GridBagConstraints.LINE_START;
            gbcOptions.weightx = 1.0;
            optionsWrapperPanel.add(createLabel(config.label), gbcOptions);

            gbcOptions.gridx = 1;
            gbcOptions.anchor = GridBagConstraints.LINE_END;
            gbcOptions.weightx = 0;
            gbcOptions.insets = new Insets(0, 0, bottomSpacing, 0);
            optionsWrapperPanel.add(toggle, gbcOptions);

            gbcOptions.insets = new Insets(0, 0, bottomSpacing, rightLabelSpacing);
        }

        GridBagConstraints gbcRightWrapper = new GridBagConstraints();
        gbcRightWrapper.gridx = 0;
        gbcRightWrapper.gridy = 0;
        gbcRightWrapper.weightx = 1.0;
        gbcRightWrapper.weighty = 0;
        gbcRightWrapper.anchor = GridBagConstraints.NORTHWEST;
        gbcRightWrapper.fill = GridBagConstraints.HORIZONTAL;
        rightPanel.add(optionsWrapperPanel, gbcRightWrapper);

        gbcRightWrapper.gridy = 1;
        gbcRightWrapper.weighty = 1.0;
        gbcRightWrapper.fill = GridBagConstraints.VERTICAL;
        rightPanel.add(Box.createVerticalGlue(), gbcRightWrapper);

        return rightPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(ThemeManager.TEXT_COLOR);
        return label;
    }

    public void showWindow() {
        if (configFrame != null) {
            SwingUtilities.invokeLater(() -> configFrame.setVisible(true));
        }
    }

    private void loadCurrentConfigStates() {
        boolean noLoadSpawnsEnabled = readNoSpawnsProperty();
        tglNoLoadSpawns.setOn(noLoadSpawnsEnabled, false);
        Set<String> activeEvents = readSpawnEvents();
        for (Map.Entry<SpawnEventConfig, CustomToggleSwitch> entry : spawnEventTogglesMap.entrySet()) {
            boolean isEnabled = activeEvents.contains(entry.getKey().eventName);
            entry.getValue().setOn(isEnabled, false);
        }
        updateDependentTogglesState(noLoadSpawnsEnabled);

        for (Map.Entry<ModFileConfig, CustomToggleSwitch> entry : modTogglesMap.entrySet()) {
            ModFileConfig config = entry.getKey();
            boolean isXml = !config.path.toString().endsWith(".properties");

            String value = readConfigValueFromText(config.path, config.key, isXml);
            boolean isEnabled = Objects.toString(value, "false").equalsIgnoreCase("true");

            entry.getValue().setOn(isEnabled, false);
        }
    }

    private void addToggleSwitchListeners() {
        tglNoLoadSpawns.addActionListener(e -> {
            boolean isNoLoadOn = tglNoLoadSpawns.isOn();
            saveConfigState(NO_SPAWNS_KEY, isNoLoadOn);
            updateDependentTogglesState(isNoLoadOn);
        });
        for (Map.Entry<SpawnEventConfig, CustomToggleSwitch> entry : spawnEventTogglesMap.entrySet()) {
            entry.getValue().addActionListener(e -> saveConfigState(entry.getKey().eventName, entry.getValue().isOn()));
        }

        for (Map.Entry<ModFileConfig, CustomToggleSwitch> entry : modTogglesMap.entrySet()) {
            ModFileConfig config = entry.getKey();
            CustomToggleSwitch toggle = entry.getValue();

            toggle.addActionListener(e -> {
                boolean isOn = toggle.isOn();
                System.out.println("[Custom Mods] " + config.label() + " mudou para: " + (isOn ? "ON" : "OFF"));

                boolean success = false;
                if ("Capsule Box".equals(config.label())) {
                    success = updateCapsuleBoxEnabledAttribute(config.path.toString(), isOn ? "true" : "false");
                } else if (config.path.toString().endsWith(".properties")) {
                    success = writeProperty(config.path, config.key, isOn ? "True" : "False");
                } else {
                    success = writeConfigValueByText(config.path, config.key, isOn ? "true" : "false");
                }

                if (!success) {
                    JOptionPane.showMessageDialog(configFrame, "Erro ao salvar o estado do mod '" + config.label() + "'. Verifique o console.", "Erro de Gravação", JOptionPane.ERROR_MESSAGE);
                    toggle.setOn(!isOn, false);
                }
            });
        }
    }

    private String readConfigValueFromText(Path configFile, String key, boolean isXml) {
        if (!Files.exists(configFile)) {
            System.err.println("Arquivo de configuração não encontrado para leitura: " + configFile);
            return "false";
        }

        try {
            String regex = isXml
                ? "<[^>]*?\\s*" + Pattern.quote(key) + "\\s*=\\s*\"(.*?)\""
                : "^\\s*" + Pattern.quote(key) + "\\s*[=:]\\s*(.*?)\\s*$";

            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

            String content = Files.readString(configFile, StandardCharsets.UTF_8);

            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
            
            if (isXml) {
                final Pattern elementPattern = Pattern.compile(
                    "<\\s*" + Pattern.quote(key) + "\\s*>\\s*([^<]+?)\\s*<\\s*/\\s*" + Pattern.quote(key) + "\\s*>",
                    Pattern.CASE_INSENSITIVE);
                final Matcher elementMatcher = elementPattern.matcher(content);
                if (elementMatcher.find()) {
                    return elementMatcher.group(1).trim();
                }
            }

            return "false";

        } catch (Exception e) {
            System.err.println("Erro ao ler arquivo " + configFile.getFileName() + ": " + e.getMessage());
            return "false";
        }
    }

    private boolean writeConfigValueByText(Path configFile, String key, String newValue) {
        if (!Files.exists(configFile)) {
            System.err.println("Arquivo de configuração não encontrado para escrita: " + configFile);
            return false;
        }

        List<String> newLines = new ArrayList<>();
        boolean valueReplaced = false;
        boolean isXml = !configFile.toString().endsWith(".properties");

        String regex = isXml
            ? "(" + Pattern.quote(key) + "\\s*=\\s*\")([^\"]*)(\")"
            : "^\\s*(" + Pattern.quote(key) + "\\s*[=:])\\s*(.*?)\\s*$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        try {
            List<String> lines = Files.readAllLines(configFile, StandardCharsets.UTF_8);

            for (String line : lines) {
                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {
                    if (isXml) {
                        line = line.replace(matcher.group(0), matcher.group(1) + newValue + matcher.group(3));
                    } else {
                        line = matcher.group(1) + " " + newValue;
                    }
                    valueReplaced = true;
                }
                newLines.add(line);
            }

            if (!isXml && !valueReplaced) {
                if (!newLines.isEmpty() && !newLines.get(newLines.size() - 1).trim().isEmpty()) {
                    newLines.add("");
                }
                newLines.add(key + " = " + newValue);
                valueReplaced = true;
            }
            
            if (!valueReplaced && isXml) {
                final String content = Files.readString(configFile, StandardCharsets.UTF_8);
                final Pattern elementPattern = Pattern.compile(
                    "(<\\s*" + Pattern.quote(key) + "\\s*>\\s*)([^<]*?)(<\\s*/\\s*" + Pattern.quote(key) + "\\s*>)",
                    Pattern.CASE_INSENSITIVE);
                final Matcher elementMatcher = elementPattern.matcher(content);
                if (elementMatcher.find()) {
                    final String updated = elementMatcher.replaceAll("$1" + Matcher.quoteReplacement(newValue) + "$3");
                    Files.writeString(configFile, updated, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    System.out.println("Arquivo " + configFile.getFileName() + " salvo com sucesso (" + key + " elemento XML).");
                    return true;
                }
            }

            if (valueReplaced) {
                Files.write(configFile, newLines, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                System.out.println("Arquivo " + configFile.getFileName() + " salvo com sucesso.");
                return true;
            } else {
                System.err.println("Chave/Atributo '" + key + "' não encontrado para escrita no arquivo " + configFile.getFileName() + ". Nenhuma alteração feita.");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Erro de IO ao escrever em " + configFile.getFileName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean readNoSpawnsProperty() {
        return readProperty(SERVER_PROPERTIES_PATH, NO_SPAWNS_KEY).equalsIgnoreCase("true");
    }

    private Set<String> readSpawnEvents() {
        String spawnEventsString = readProperty(NPCS_PROPERTIES_PATH, SPAWN_EVENTS_KEY);
        if (spawnEventsString == null || spawnEventsString.trim().isEmpty()) {
            return new HashSet<>();
        }
        String[] events = spawnEventsString.trim().split("\\s*;\\s*");
        return Arrays.stream(events)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(String::toLowerCase)
            .collect(Collectors.toSet());
    }

    private String readProperty(Path propertyFile, String key) {
        Properties props = new Properties();
        if (!Files.exists(propertyFile)) {
            System.err.println("Arquivo de configuração não encontrado para leitura: " + propertyFile);
            return "";
        }

        try (var is = Files.newInputStream(propertyFile)) {
            props.load(is);
            return props.getProperty(key, "");
        } catch (Exception e) {
            System.err.println("Erro ao ler a propriedade '" + key + "' de " + propertyFile + ": " + e.getMessage());
            return "";
        }
    }

    private void updateDependentTogglesState(boolean noLoadSpawnsIsOn) {
        boolean enableOthers = !noLoadSpawnsIsOn;
        if (spawnEventTogglesMap.isEmpty()) return;

        for (CustomToggleSwitch toggle : spawnEventTogglesMap.values()) {
            if (toggle.isEnabled() != enableOthers) {
                toggle.setEnabled(enableOthers);
            }
        }
    }

    private void saveConfigState(String configKey, boolean isEnabled) {
        if (NO_SPAWNS_KEY.equals(configKey)) {
            saveNoSpawnsState(isEnabled);
        } else {
            saveSpawnEventsState();
        }
    }

    private void saveNoSpawnsState(boolean isEnabled) {
        System.out.println("Salvando Configuração '" + NO_SPAWNS_KEY + "' como: " + (isEnabled ? "True" : "False"));
        if (!writeProperty(SERVER_PROPERTIES_PATH, NO_SPAWNS_KEY, isEnabled ? "True" : "False")) {
            JOptionPane.showMessageDialog(configFrame, "Erro ao salvar configuração '" + NO_SPAWNS_KEY + "' no arquivo:\n" + SERVER_PROPERTIES_PATH.toString(), "Erro de Gravação", JOptionPane.ERROR_MESSAGE);
            tglNoLoadSpawns.setOn(!isEnabled, false);
            updateDependentTogglesState(!isEnabled);
        }
    }

    private boolean writeProperty(Path propertyFile, String key, String value) {
        List<String> lines;
        boolean keyFound = false;
        try {
            Files.createDirectories(propertyFile.getParent());
            if (Files.exists(propertyFile)) {
                lines = Files.readAllLines(propertyFile, StandardCharsets.UTF_8);
            } else {
                lines = new ArrayList<>();
                System.out.println("Arquivo " + propertyFile + " não encontrado, será criado.");
            }

            List<String> newLines = new ArrayList<>();
            Pattern keyPattern = Pattern.compile("^\\s*" + Pattern.quote(key) + "\\s*([=:]).*", Pattern.CASE_INSENSITIVE);
            boolean alreadyReplaced = false;

            for (String line : lines) {
                Matcher matcher = keyPattern.matcher(line);
                if (!alreadyReplaced && matcher.matches()) {
                    String separator = matcher.group(1) != null ? matcher.group(1) : "=";
                    newLines.add(key + " " + separator + " " + value);
                    keyFound = true;
                    alreadyReplaced = true;
                } else {
                    newLines.add(line);
                }
            }

            if (!keyFound) {
                System.out.println("Chave '" + key + "' não encontrada em " + propertyFile + ", adicionando ao final.");
                if (!newLines.isEmpty() && !newLines.get(newLines.size() - 1).trim().isEmpty()) {
                    newLines.add("");
                }
                newLines.add(key + " = " + value);
            }

            Files.write(propertyFile, newLines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Arquivo " + propertyFile + " salvo com sucesso.");
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao escrever no arquivo " + propertyFile + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void saveSpawnEventsState() {
        Set<String> allExistingEvents = readSpawnEvents();
        Set<String> uiControlledEvents = SPAWN_EVENTS_CONFIG.stream()
            .map(c -> c.eventName.toLowerCase())
            .collect(Collectors.toSet());

        List<String> finalEvents = new ArrayList<>();

        for (String fixedEvent : FIXED_EVENTS_ORDERED) {
            if (allExistingEvents.contains(fixedEvent)) {
                finalEvents.add(fixedEvent);
            }
        }

        List<String> activeUiEvents = spawnEventTogglesMap.entrySet().stream()
            .filter(entry -> entry.getValue().isOn())
            .map(entry -> entry.getKey().eventName)
            .sorted(Comparator.naturalOrder())
            .collect(Collectors.toList());

        Set<String> otherEvents = allExistingEvents.stream()
            .filter(event -> !FIXED_EVENTS_ORDERED.contains(event))
            .filter(event -> !uiControlledEvents.contains(event))
            .collect(Collectors.toSet());

        finalEvents.addAll(activeUiEvents);
        otherEvents.stream().sorted(Comparator.naturalOrder()).forEach(finalEvents::add);

        String newSpawnEventsValue = finalEvents.stream()
            .filter(s -> s != null && !s.trim().isEmpty())
            .collect(Collectors.joining(";"));

        System.out.println("Salvando Configuração '" + SPAWN_EVENTS_KEY + "' como: " + newSpawnEventsValue);
        if (!writeProperty(NPCS_PROPERTIES_PATH, SPAWN_EVENTS_KEY, newSpawnEventsValue)) {
            JOptionPane.showMessageDialog(configFrame, "Erro ao salvar a configuração '" + SPAWN_EVENTS_KEY + "' no arquivo:\n" + NPCS_PROPERTIES_PATH.toString(), "Erro de Gravação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadIcons() {
        if (parentFrame != null) {
            try {
                configFrame.setIconImages(GuiUtils.loadIcons());
            } catch (Exception e) {
                System.err.println("Erro ao carregar ícones via GuiUtils em ConfigGS (chamado por pai), tentando fallback: " + e.getMessage());
                List<Image> icons = new ArrayList<>();
                try {
                    icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "16x16.png").getImage());
                    icons.add(new ImageIcon(".." + File.separator + "images" + File.separator + "32x32.png").getImage());
                } catch (Exception ex) {
                    System.err.println("Erro no fallback de carregar ícones em ConfigGS: " + ex.getMessage());
                }
                if (!icons.isEmpty())
                    configFrame.setIconImages(icons);
            }
        } else {
            List<Image> icons = new ArrayList<>();
            try {
                icons.add(new ImageIcon("./images/16x16.png").getImage());
                icons.add(new ImageIcon("./images/32x32.png").getImage());
            } catch (Exception e) {
                System.err.println("Erro ao carregar ícones diretamente em ConfigGS (main): " + e.getMessage());
            }
            if (!icons.isEmpty())
                configFrame.setIconImages(icons);
        }
    }

    /**
     * Método específico para alterar o atributo 'enabled' no XML CapsuleBox.xml usando parser DOM
     */
    private boolean updateCapsuleBoxEnabledAttribute(String filePath, String newValue) {
        try {
            File xmlFile = new File(filePath);
            if (!xmlFile.exists()) {
                System.err.println("Arquivo não encontrado: " + filePath);
                return false;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(xmlFile);

            Element root = doc.getDocumentElement();
            if (root == null) {
                System.err.println("Arquivo XML sem nó raiz válido.");
                return false;
            }

            root.setAttribute("enabled", newValue);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);

            transformer.transform(source, result);

            System.out.println("Atributo 'enabled' atualizado para '" + newValue + "' em " + filePath);
            return true;

        } catch (Exception e) {
            System.err.println("Erro ao atualizar atributo 'enabled': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
