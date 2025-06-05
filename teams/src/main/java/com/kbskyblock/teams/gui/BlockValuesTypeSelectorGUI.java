package com.kbskyblock.teams.gui;

import com.kbskyblock.teams.KBSkyblockTeams;
import com.kbskyblock.teams.configs.inventories.BlockValuesTypeSelectorInventoryConfig;
import com.kbskyblock.teams.configs.inventories.NoItemGUI;
import com.kbskyblock.teams.database.KBSkyblockUser;
import com.kbskyblock.teams.database.Team;
import com.kbskyblock.core.gui.BackGUI;
import com.kbskyblock.core.utils.ItemStackUtils;
import com.kbskyblock.core.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class BlockValuesTypeSelectorGUI<T extends Team, U extends KBSkyblockUser<T>> extends BackGUI {

    private final KBSkyblockTeams<T, U> teams;
    private final String teamArg;

    public BlockValuesTypeSelectorGUI(String teamArg, Player player, KBSkyblockTeams<T, U> teams) {
        super(teams.getInventories().blockValuesTypeSelectorGUI.background, player, teams.getInventories().backButton);
        this.teams = teams;
        this.teamArg = teamArg;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        NoItemGUI noItemGUI = teams.getInventories().blockValuesTypeSelectorGUI;
        Inventory inventory = Bukkit.createInventory(this, noItemGUI.size, StringUtils.color(noItemGUI.title));
        addContent(inventory);
        return inventory;
    }

    @Override
    public void addContent(Inventory inventory) {
        super.addContent(inventory);

        BlockValuesTypeSelectorInventoryConfig blockValuesTypeSelectorInventoryConfig = teams.getInventories().blockValuesTypeSelectorGUI;
        if (blockValuesTypeSelectorInventoryConfig.blocks.enabled) {
            inventory.setItem(blockValuesTypeSelectorInventoryConfig.blocks.item.slot, ItemStackUtils.makeItem(blockValuesTypeSelectorInventoryConfig.blocks.item));
        }

        if (blockValuesTypeSelectorInventoryConfig.spawners.enabled) {
            inventory.setItem(blockValuesTypeSelectorInventoryConfig.spawners.item.slot, ItemStackUtils.makeItem(blockValuesTypeSelectorInventoryConfig.spawners.item));
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        super.onInventoryClick(event);
        BlockValuesTypeSelectorInventoryConfig blockValuesTypeSelectorInventoryConfig = teams.getInventories().blockValuesTypeSelectorGUI;

        if (event.getSlot() == blockValuesTypeSelectorInventoryConfig.blocks.item.slot && blockValuesTypeSelectorInventoryConfig.blocks.enabled) {
            teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().blockValueCommand, new String[]{"blocks", teamArg});
        }

        if (event.getSlot() == blockValuesTypeSelectorInventoryConfig.spawners.item.slot && blockValuesTypeSelectorInventoryConfig.spawners.enabled) {
            teams.getCommandManager().executeCommand(event.getWhoClicked(), teams.getCommands().blockValueCommand, new String[]{"spawners", teamArg});
        }
    }
}
