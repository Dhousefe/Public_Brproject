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
package ext.mods.commons.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import ext.mods.commons.gui.CustomTopPanel;
import ext.mods.commons.gui.CustomToggleSwitch;
import ext.mods.commons.gui.ThemeManager;

public class ConfigDiversosFrame {

    private JFrame configFrame;
    private CustomTopPanel topPanel;
    private Frame parentFrame;

    private final Map<String, CustomToggleSwitch> toggleMap = new LinkedHashMap<>();
    private final Map<String, JTextField> inputMap = new LinkedHashMap<>();

    private record ConfigField(String label, String key, String defaultValue, boolean isBoolean, String description) {}

    private static final List<ConfigField> DIVERSOS_FIELDS = List.of(
        new ConfigField("Cancel Lesser Effect", "CancelLesserEffect", "True", true,
            "<html>If True, lesser effects in the same stack group<br>" +
            "will be canceled when stronger effects are applied.</html>"),
        new ConfigField("HP Regen Multiplier (x)", "HpRegenMultiplier", "1.0", false,
            "<html>Multiplier for HP regeneration rate - base 1.0 = 100%</html>"),
        new ConfigField("MP Regen Multiplier (x)", "MpRegenMultiplier", "1.0", false,
            "<html>Multiplier for MP regeneration rate - base 1.0 = 100%</html>"),
        new ConfigField("CP Regen Multiplier (x)", "CpRegenMultiplier", "1.0", false,
            "<html>Multiplier for CP regeneration rate - base 1.0 = 100%</html>"),
        new ConfigField("Spawn Protection (s)", "PlayerSpawnProtection", "10", false,
            "<html>Player protection time after teleport or login in seconds.<br>0 disables protection.</html>"),
        new ConfigField("Fake Death Up Protection (s)", "PlayerFakeDeathUpProtection", "5", false,
            "<html>Protection time against mobs aggression after fake death.<br>Seconds, 0 disables.</html>"),
        new ConfigField("HP Restore on Respawn (%)", "RespawnRestoreHP", "1.0", false,
            "<html>Amount of HP restored upon respawn (1.0 = 100%)</html>"),
        new ConfigField("Max Dwarf Buy Slots", "MaxPvtStoreBuySlotsDwarf", "5", false,
            "<html>Maximum private store buy slots for dwarves</html>"),
        new ConfigField("Max Other Races Buy Slots", "MaxPvtStoreBuySlotsOther", "4", false,
            "<html>Maximum private store buy slots for other races</html>"),
        new ConfigField("Max Dwarf Sell Slots", "MaxPvtStoreSellSlotsDwarf", "4", false,
            "<html>Maximum private store sell slots for dwarves</html>"),
        new ConfigField("Max Other Races Sell Slots", "MaxPvtStoreSellSlotsOther", "3", false,
            "<html>Maximum private store sell slots for other races</html>"),
        new ConfigField("Use Deep Blue Drop Rules", "UseDeepBlueDropRules", "True", true,
            "<html>If True, applies deep blue mob drop penalties:<br>" +
            "- When player level is 9x mob level drops divided by 3<br>" +
            "- Drops chance decrease by 9% per level difference after 9 levels</html>"),
        new ConfigField("Allow Delevel", "AllowDelevel", "True", true,
            "<html>Enable loss of XP and deleveling (default True)</html>"),
        new ConfigField("Death Penalty Chance (%)", "DeathPenaltyChance", "5", false,
            "<html>Chance of death penalty when killed by mob (%)</html>")
    );

    private static final List<ConfigField> INVENTORY_FIELDS = List.of(
        new ConfigField("Non-Dwarf Slots", "MaximumSlotsForNoDwarf", "80", false,
            "<html>Inventory slots for non-dwarf characters</html>"),
        new ConfigField("Dwarf Slots", "MaximumSlotsForDwarf", "100", false,
            "<html>Inventory slots for dwarf characters</html>"),
        new ConfigField("Pet Slots", "MaximumSlotsForPet", "12", false,
            "<html>Inventory slots for pets</html>"),
        new ConfigField("Weight Limit Multiplier (x)", "WeightLimit", "100.0", false,
            "<html>Weight limit multiplier for players and pets (1.0 = 100%)</html>")
    );

    public static void main(String[] args) {
        ThemeManager.applyTheme();

        SwingUtilities.invokeLater(() -> {
            ConfigDiversosFrame frame = new ConfigDiversosFrame(null);
            frame.showWindow();
            frame.configFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }

    public ConfigDiversosFrame(Frame parent) {
        this.parentFrame = parent;
        initialize();
    }

    public void showWindow() {
        if (configFrame != null) {
            SwingUtilities.invokeLater(() -> configFrame.setVisible(true));
        }
    }

    private void initialize() {
        configFrame = new JFrame("GameServer Miscellaneous Settings");
        configFrame.setUndecorated(true);
        configFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        configFrame.setSize(750, 550);
        configFrame.setResizable(true);
        configFrame.setLocationRelativeTo(parentFrame);

        Runnable closeAction = () -> configFrame.dispose();

        String iconPath = (parentFrame == null) ? "./images/16x16.png" : ".." + File.separator + "images" + File.separator + "16x16.png";

        topPanel = new CustomTopPanel(configFrame, new JMenuBar(), closeAction, true, iconPath);
        configFrame.add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());
        tabbedPane.setForeground(ThemeManager.TEXT_COLOR);

        tabbedPane.addTab("General & Regen", createGeneralRegenPanel());
        tabbedPane.addTab("Inventory & Warehouse", createInventoryPanel());
        tabbedPane.addTab("Augmentation", createAugmentationPanel());
        tabbedPane.addTab("Karma & PvP", createKarmaPvPPanel());
        tabbedPane.addTab("Party & Admins", createPartyAdminPanel());
        tabbedPane.addTab("Skills & Craft", createSkillsCraftPanel());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ThemeManager.VERY_DARK_BACKGROUND);

        JButton btnSave = new JButton("Save All Settings");
        btnSave.setBackground(ThemeManager.BASE_PURPLE);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.addActionListener(e -> saveAllConfigs());
        buttonPanel.add(btnSave);

        configFrame.add(tabbedPane, BorderLayout.CENTER);
        configFrame.add(buttonPanel, BorderLayout.SOUTH);

        loadInitialValues();
    }

    private JScrollPane createGeneralRegenPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 10, 0, 10);

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);

        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.gridx = 0;
        gbcWrapper.gridy = 0;
        gbcWrapper.weightx = 1.0;
        gbcWrapper.fill = GridBagConstraints.HORIZONTAL;

        JPanel pnlDiverse = createSectionPanel(" Miscellaneous & Regeneration ");
        populateFields(pnlDiverse, DIVERSOS_FIELDS);

        contentWrapper.add(pnlDiverse, gbcWrapper);
        gbcWrapper.gridy++;
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)), gbcWrapper);
        gbcWrapper.gridy++;

        contentWrapper.add(Box.createVerticalGlue(), gbcWrapper);
        gbcWrapper.gridy++;
        gbcWrapper.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(contentWrapper, gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(Box.createVerticalGlue(), gbc);

        return scroll;
    }

    private JScrollPane createInventoryPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);
        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.gridx = 0;
        gbcWrapper.gridy = 0;
        gbcWrapper.weightx = 1.0;
        gbcWrapper.fill = GridBagConstraints.HORIZONTAL;

        JPanel pnlInventory = createSectionPanel(" Inventory ");
        populateFields(pnlInventory, INVENTORY_FIELDS);

        contentWrapper.add(pnlInventory, gbcWrapper);
        gbcWrapper.gridy++;

        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)), gbcWrapper);
        gbcWrapper.gridy++;

        contentWrapper.add(Box.createVerticalGlue(), gbcWrapper);
        gbcWrapper.weighty = 1.0;

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.anchor = GridBagConstraints.NORTHWEST;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.weightx = 1.0;
        gbcMain.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(contentWrapper, gbcMain);

        gbcMain.gridy = 1;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(Box.createVerticalGlue(), gbcMain);

        return scroll;
    }

    private JScrollPane createAugmentationPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());
        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);
        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.gridx = 0; gbcWrapper.gridy = 0; gbcWrapper.weightx = 1.0; gbcWrapper.fill = GridBagConstraints.HORIZONTAL;

        List<ConfigField> augmentationFields = List.of(
            new ConfigField("Normal Grade", "AugmentationNGSkillChance", "15", false,
                "<html>Skill chance for normal grade augmentation (%)</html>"),
            new ConfigField("Middle Grade", "AugmentationMidSkillChance", "30", false,
                "<html>Skill chance for middle grade augmentation (%)</html>"),
            new ConfigField("High Grade", "AugmentationHighSkillChance", "45", false,
                "<html>Skill chance for high grade augmentation (%)</html>"),
            new ConfigField("Top Grade", "AugmentationTopSkillChance", "60", false,
                "<html>Skill chance for top grade augmentation (%)</html>"),
            new ConfigField("Base Stat Chance", "AugmentationBaseStatChance", "1", false,
                "<html>Chance to obtain a base stat modifier (%)</html>"),
            new ConfigField("Normal Grade Glow", "AugmentationNGGlowChance", "0", false,
                "<html>Glow chance for normal grade (%)</html>"),
            new ConfigField("Middle Grade Glow", "AugmentationMidGlowChance", "40", false,
                "<html>Glow chance for middle grade (%)</html>"),
            new ConfigField("High Grade Glow", "AugmentationHighGlowChance", "70", false,
                "<html>Glow chance for high grade (%)</html>"),
            new ConfigField("Top Grade Glow", "AugmentationTopGlowChance", "100", false,
                "<html>Glow chance for top grade (%)</html>")
        );

        JPanel pnlAugmentation = createSectionPanel(" Augmentation Chances ");
        populateFields(pnlAugmentation, augmentationFields);
        contentWrapper.add(pnlAugmentation, gbcWrapper);

        gbcWrapper.gridy++;
        contentWrapper.add(Box.createVerticalGlue(), gbcWrapper);
        gbcWrapper.weighty = 1.0;

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.anchor = GridBagConstraints.NORTHWEST;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.weightx = 1.0;
        gbcMain.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(contentWrapper, gbcMain);

        gbcMain.gridy = 1;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(Box.createVerticalGlue(), gbcMain);

        return scroll;
    }

    private JScrollPane createKarmaPvPPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());
        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);
        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.gridx = 0; gbcWrapper.gridy = 0; gbcWrapper.weightx = 1.0; gbcWrapper.fill = GridBagConstraints.HORIZONTAL;

        List<ConfigField> karmaFields = List.of(
            new ConfigField("Karma Player Can Shop", "KarmaPlayerCanShop", "False", true,
                "<html>Allow shopping for karma players</html>"),
            new ConfigField("Karma Player Can Teleport", "KarmaPlayerCanTeleport", "True", true,
                "<html>Allow teleportation for karma players</html>"),
            new ConfigField("Karma Player Can Use GK", "KarmaPlayerCanUseGK", "False", true,
                "<html>Allow use of Gatekeepers for karma players</html>"),
            new ConfigField("Karma Player Can Trade", "KarmaPlayerCanTrade", "True", true,
                "<html>Allow trading for karma players</html>"),
            new ConfigField("Karma Player Can Use WH", "KarmaPlayerCanUseWareHouse", "True", true,
                "<html>Allow warehouse access for karma players</html>")
        );

        JPanel pnlKarma = createSectionPanel(" Karma Rules ");
        populateFields(pnlKarma, karmaFields);
        contentWrapper.add(pnlKarma, gbcWrapper);

        gbcWrapper.gridy++;
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)), gbcWrapper);
        gbcWrapper.gridy++;

        List<ConfigField> pvpFields = List.of(
            new ConfigField("GM Can Drop Equipment", "CanGMDropEquipment", "False", true,
                "<html>Allow GMs to drop equipment</html>"),
            new ConfigField("Minimum PK Required To Drop", "MinimumPKRequiredToDrop", "5", false,
                "<html>Minimum PK kills to cause item drop</html>"),
            new ConfigField("Award PK Kill PVP Point", "AwardPKKillPVPPoint", "False", true,
                "<html>Award PVP point for PK kills</html>"),
            new ConfigField("PvP Vs Normal Time (ms)", "PvPVsNormalTime", "40000", false,
                "<html>Duration in ms for PvP mode vs normal</html>"),
            new ConfigField("PvP Vs PvP Time (ms)", "PvPVsPvPTime", "20000", false,
                "<html>Duration in ms for PvP mode vs PvP</html>"),
            new ConfigField("Pet Items (List of IDs)", "ListOfPetItems", "2375,3500,3501", false,
                "<html>Comma-separated list of pet item IDs</html>"),
            new ConfigField("Non-Droppable Items for PK", "ListOfNonDroppableItemsForPK", "57,1147,425", false,
                "<html>Comma-separated list of items non-droppable by PKers</html>")
        );

        JPanel pnlPvP = createSectionPanel(" PvP & Item Drop ");
        populateFields(pnlPvP, pvpFields);
        contentWrapper.add(pnlPvP, gbcWrapper);

        gbcWrapper.gridy++;
        contentWrapper.add(Box.createVerticalGlue(), gbcWrapper);
        gbcWrapper.weighty = 1.0;

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.anchor = GridBagConstraints.NORTHWEST;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.weightx = 1.0;
        gbcMain.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(contentWrapper, gbcMain);

        gbcMain.gridy = 1;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(Box.createVerticalGlue(), gbcMain);

        return scroll;
    }

    private JScrollPane createPartyAdminPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());
        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);
        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.gridx = 0; gbcWrapper.gridy = 0; gbcWrapper.weightx = 1.0; gbcWrapper.fill = GridBagConstraints.HORIZONTAL;

        List<ConfigField> partyFields = List.of(
            new ConfigField("XP Cutoff Method", "PartyXpCutoffMethod", "level", false,
                "<html>Party XP cutoff method: auto, level, percentage, none</html>"),
            new ConfigField("XP Cutoff % (if 'percentage')", "PartyXpCutoffPercent", "3.0", false,
                "<html>Cutoff percentage if method is percentage</html>"),
            new ConfigField("XP Cutoff Level (if 'level')", "PartyXpCutoffLevel", "20", false,
                "<html>Cutoff level if method is level</html>"),
            new ConfigField("Party Range", "PartyRange", "1500", false,
                "<html>Range for party checks like quests and item distribution</html>")
        );

        JPanel pnlParty = createSectionPanel(" Party XP/Item Distribution ");
        populateFields(pnlParty, partyFields);
        contentWrapper.add(pnlParty, gbcWrapper);

        gbcWrapper.gridy++;
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)), gbcWrapper);
        gbcWrapper.gridy++;

        List<ConfigField> adminFields = List.of(
            new ConfigField("Default Access Level", "DefaultAccessLevel", "0", false,
                "<html>Default access level for all users</html>"),
            new ConfigField("GM Hero Aura", "GMHeroAura", "False", true,
                "<html>Enable hero aura for GMs</html>"),
            new ConfigField("GM Startup Invulnerable", "GMStartupInvulnerable", "True", true,
                "<html>GMs start invulnerable</html>"),
            new ConfigField("GM Startup Invisible", "GMStartupInvisible", "True", true,
                "<html>GMs start invisible</html>"),
            new ConfigField("GM Startup Block All", "GMStartupBlockAll", "True", true,
                "<html>Block all private messages to GMs</html>"),
            new ConfigField("GM Startup Auto List", "GMStartupAutoList", "False", true,
                "<html>Automatically list GMs on login</html>")
        );

        JPanel pnlAdmin = createSectionPanel(" GMs / Admin ");
        populateFields(pnlAdmin, adminFields);
        contentWrapper.add(pnlAdmin, gbcWrapper);

        gbcWrapper.gridy++;
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)), gbcWrapper);
        gbcWrapper.gridy++;

        List<ConfigField> petitionFields = List.of(
            new ConfigField("Petitioning Allowed", "PetitioningAllowed", "True", true,
                "<html>Allow players to send petitions</html>"),
            new ConfigField("Max Petitions Per Player", "MaxPetitionsPerPlayer", "5", false,
                "<html>Max petitions per player per session</html>"),
            new ConfigField("Max Petitions Pending", "MaxPetitionsPending", "25", false,
                "<html>Max petitions pending; new ones rejected if exceeded</html>")
        );

        JPanel pnlPetition = createSectionPanel(" Petitions ");
        populateFields(pnlPetition, petitionFields);
        contentWrapper.add(pnlPetition, gbcWrapper);

        gbcWrapper.gridy++;

        contentWrapper.add(Box.createVerticalGlue(), gbcWrapper);
        gbcWrapper.weighty = 1.0;

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.anchor = GridBagConstraints.NORTHWEST;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.weightx = 1.0;
        gbcMain.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(contentWrapper, gbcMain);

        gbcMain.gridy = 1;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(Box.createVerticalGlue(), gbcMain);

        return scroll;
    }

    private JScrollPane createSkillsCraftPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());
        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(ThemeManager.COMPONENT_BACKGROUND.darker());

        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setOpaque(false);
        GridBagConstraints gbcWrapper = new GridBagConstraints();
        gbcWrapper.gridx = 0; gbcWrapper.gridy = 0; gbcWrapper.weightx = 1.0; gbcWrapper.fill = GridBagConstraints.HORIZONTAL;

        List<ConfigField> craftingFields = List.of(
            new ConfigField("Crafting Enabled", "CraftingEnabled", "True", true,
                "<html>Enable or disable crafting</html>"),
            new ConfigField("Dwarf Recipe Limit", "DwarfRecipeLimit", "50", false,
                "<html>Recipe limit for dwarves</html>"),
            new ConfigField("Common Recipe Limit", "CommonRecipeLimit", "50", false,
                "<html>Recipe limit for other races</html>")
        );

        JPanel pnlCraft = createSectionPanel(" Crafting ");
        populateFields(pnlCraft, craftingFields);
        contentWrapper.add(pnlCraft, gbcWrapper);

        gbcWrapper.gridy++;
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)), gbcWrapper);
        gbcWrapper.gridy++;

        List<ConfigField> skillFields = List.of(
            new ConfigField("Auto Learn Skills", "AutoLearnSkills", "True", true,
                "<html>Automatically learn skills</html>"),
            new ConfigField("Max Auto Learn Skills Level", "LvlAutoLearnSkills", "80", false,
                "<html>Max level to auto learn skills</html>"),
            new ConfigField("Magic Failures", "MagicFailures", "True", true,
                "<html>Enable magic failures</html>"),
            new ConfigField("Perfect Shield Block Rate (%)", "PerfectShieldBlockRate", "5", false,
                "<html>Chance for perfect shield block (%)</html>"),
            new ConfigField("Life Crystal Needed", "LifeCrystalNeeded", "True", true,
                "<html>Life crystal needed for clan skills</html>"),
            new ConfigField("Spellbook Needed", "SpBookNeeded", "True", true,
                "<html>Spellbook needed to learn skills</html>"),
            new ConfigField("Enchant Skill SP Book Needed", "EnchantSkillSpBookNeeded", "True", true,
                "<html>Special book needed to enchant skills</html>"),
            new ConfigField("Divine Inspiration SP Book Needed", "DivineInspirationSpBookNeeded", "True", true,
                "<html>Special book needed for Divine Inspiration</html>"),
            new ConfigField("SubClass Without Quests", "SubClassWithoutQuests", "False", true,
                "<html>Allow subclasses without quests</html>"),
            new ConfigField("Subclass Require Mimir", "SubclassRequireMimir", "True", true,
                "<html>Require 'Mimir's Elixir' quest for subclass</html>"),
            new ConfigField("Subclass Require Fate", "SubclassRequireFate", "False", true,
                "<html>Require 'Fate's Whisper' quest for subclass</html>")
        );

        JPanel pnlSkills = createSectionPanel(" Skills & Penalties ");
        populateFields(pnlSkills, skillFields);
        contentWrapper.add(pnlSkills, gbcWrapper);

        gbcWrapper.gridy++;
        contentWrapper.add(Box.createRigidArea(new Dimension(0, 20)), gbcWrapper);
        gbcWrapper.gridy++;

        List<ConfigField> buffFields = List.of(
            new ConfigField("Max Buffs Amount (Slots)", "MaxBuffsAmount", "25", false,
                "<html>Maximum buff slots available</html>"),
            new ConfigField("Store Skill Cooltime", "StoreSkillCooltime", "True", true,
                "<html>Store buffs/debuffs cooldown on logout</html>"),
            new ConfigField("Expertise Penalty", "ExpertisePenalty", "True", true,
                "<html>Enable penalty for using high grade equipment early</html>")
        );

        JPanel pnlBuffs = createSectionPanel(" Buff Settings ");
        populateFields(pnlBuffs, buffFields);
        contentWrapper.add(pnlBuffs, gbcWrapper);

        gbcWrapper.gridy++;
        contentWrapper.add(Box.createVerticalGlue(), gbcWrapper);
        gbcWrapper.weighty = 1.0;

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.anchor = GridBagConstraints.NORTHWEST;
        gbcMain.fill = GridBagConstraints.HORIZONTAL;
        gbcMain.weightx = 1.0;
        gbcMain.insets = new Insets(10, 10, 10, 10);
        mainPanel.add(contentWrapper, gbcMain);

        gbcMain.gridy = 1;
        gbcMain.weighty = 1.0;
        gbcMain.fill = GridBagConstraints.VERTICAL;
        mainPanel.add(Box.createVerticalGlue(), gbcMain);

        return scroll;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        Border lineBorder = BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR);

        Border titledBorder = BorderFactory.createTitledBorder(
            lineBorder, title, TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13), ThemeManager.TEXT_COLOR.brighter()
        );
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return panel;
    }

    private void populateFields(JPanel panel, List<ConfigField> fields) {
        GridBagConstraints gbcPanel = new GridBagConstraints();
        gbcPanel.anchor = GridBagConstraints.NORTHWEST;
        gbcPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcPanel.weightx = 1.0;
        gbcPanel.gridx = 0;
        gbcPanel.gridy = 0;

        JPanel wrapper = new JPanel(new GridLayout(0, 2, 20, 5));
        wrapper.setOpaque(false);

        for (ConfigField field : fields) {
            JPanel linePanel = new JPanel(new GridBagLayout());
            linePanel.setOpaque(false);

            GridBagConstraints gbcLine = new GridBagConstraints();
            gbcLine.anchor = GridBagConstraints.LINE_START;
            gbcLine.insets = new Insets(0, 0, 5, 10);
            gbcLine.gridx = 0;
            gbcLine.gridy = 0;
            gbcLine.weightx = 1.0;

            JLabel descLabel = new JLabel(field.description(), JLabel.LEFT);
            descLabel.setForeground(ThemeManager.TEXT_COLOR.brighter());
            descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

            linePanel.add(descLabel, gbcLine);

            JLabel label = new JLabel(field.label());
            label.setForeground(ThemeManager.TEXT_COLOR);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gbcLine.gridy = 1;
            linePanel.add(label, gbcLine);

            if (field.isBoolean()) {
                CustomToggleSwitch toggle = new CustomToggleSwitch(Boolean.parseBoolean(field.defaultValue()));
                toggleMap.put(field.key(), toggle);
                gbcLine.gridx = 1;
                gbcLine.gridy = 1;
                gbcLine.anchor = GridBagConstraints.LINE_END;
                gbcLine.weightx = 0;
                gbcLine.insets = new Insets(0, 0, 0, 0);
                linePanel.add(toggle, gbcLine);
            } else {
                JTextField textField = new JTextField(field.defaultValue(), 5);
                textField.setBackground(ThemeManager.COMPONENT_BACKGROUND.brighter());
                textField.setForeground(ThemeManager.TEXT_COLOR);
                textField.setCaretColor(ThemeManager.TEXT_COLOR.brighter());
                inputMap.put(field.key(), textField);
                gbcLine.gridx = 1;
                gbcLine.gridy = 1;
                gbcLine.anchor = GridBagConstraints.LINE_END;
                gbcLine.weightx = 0;
                gbcLine.insets = new Insets(0, 0, 0, 0);
                linePanel.add(textField, gbcLine);
            }

            wrapper.add(linePanel);
        }

        panel.add(wrapper, gbcPanel);
    }

    private void loadInitialValues() {
        System.out.println("Reading initial settings...");
    }

    private void saveAllConfigs() {
        System.out.println("Saving all settings...");

        JOptionPane.showMessageDialog(configFrame, "Settings saved (Placeholder logic).", "Saved", JOptionPane.INFORMATION_MESSAGE);
    }
}
